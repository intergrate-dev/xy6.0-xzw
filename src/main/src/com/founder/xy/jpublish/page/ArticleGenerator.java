package com.founder.xy.jpublish.page;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsoupHelper;
import com.founder.xy.jpublish.PubArticleJsonHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.context.ArticleContext;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PageDir;
import com.founder.xy.jpublish.data.PageUrl;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.data.VoteOption;
import com.founder.xy.jpublish.data.Widgets;
import com.founder.xy.jpublish.template.ComponentFactory;
import com.founder.xy.jpublish.template.component.Component;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.Template;

/**
 * 稿件发布生成器
 * @author Gong Lijie
 */
public class ArticleGenerator extends AbstractGenerator{
	//分页符
	private static final String splitor = "_ueditor_page_break_tag_";
	
	private ArticleContext context;
	private boolean preview; //是否预览
	private String[] previewPages = new String[2]; //为预览生成的页面
	
	/** 
	 * 稿件页生成
	 * 没有发布规则，则认为无法正确发布（即使是App稿件，也需要有发布目录来发布图片附件等）
	 * 没有发布模板，则
	 * 		1）对于Web稿，至少需要一个渠道有模板。配了模板后任一个渠道发送失败，则失败。
	 * 		2）对于App稿，只需要发布触屏版。
	 */
	public int generator(ArticleMsg data){
		if (log.isDebugEnabled()) log.debug("---------稿件页发布 " + data.getDocLibID() + "," + data.getId());
		
		//发布服务
		int result = generatorFiles(data);
		if (result != PubArticle.SUCCESS) return result;
		
		/*
		 * 发布后的处理：
		 * 1）改变稿件发布状态
		 * 2）若是Web库的专题稿件，则加入专题稿相关栏目Hash。
		 * 3）把<主栏目ID，当前时间>写入Redis中小红点的栏目最新发布时间Hash
		 */
		afterPublish();
		
		return PubArticle.SUCCESS;
	}
	
	/**
	 * 稿件页预览，生成的内容
	 * @param data 发布预览的参数
	 * @return 网页html内容，两个元素的数组，第一个元素是网站版html，第二个元素是触屏版html
	 * @throws E5Exception 
	 */
	public String[] preview(ArticleMsg data) throws E5Exception{
		preview = true;
		
		int previewResult = generatorFiles(data);
		if (previewResult == PubArticle.SUCCESS)
			return previewPages;
		else {
			throw new E5Exception(previewResult, PubArticle.ERROR_MSG[previewResult]);
		}
	}

	private int generatorFiles(ArticleMsg data) {
		//根据消息参数，组织出ColParam，可用于当前位置等组件
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), data.getDocLibID());
		param = new ColParam(colLibID, data.getColID(), 0);
		
		context = new ArticleContext();
		int initResult = context.init(data, log);
		if (initResult != PubArticle.SUCCESS)
			return initResult;
		
		if (param.getColID() == 0) {
			param.setColID(context.getColumn().getId());
		}
		
		//若没抽图完毕，则改成等待同步
		if (!preview && !extractDone()) {
			log.debug("抽图未完成，不能发布");
			return PubArticle.ERROR_EXTRACTING;
		}
		
		//设置当前时间为实际发布时间，可能模板中会引用
		context.getArticle().setPubTimeReal(DateUtils.getTimestamp());
		
		int success = PubArticle.SUCCESS;
		Template[] templates = context.getTemplate();
		
		//原始稿件内容
		String content = context.getArticle().getContent();
		
		//任一个渠道发送失败，则失败
		for (int i = 0; i < templates.length; i++) {
			
			//对于文章稿，发布时会改变正文图片的url。发布到别的渠道前得改回来
			if (i > 0) context.getArticle().setContent(content);
			
			success = pubChannel(templates[i], i);
			if (success != PubArticle.SUCCESS)
				return success;
		}
		
		//若是App稿件，则在redis中放入json，供稿件列表api使用
		if (!preview && context.getMessage().getChannel() == 2) {
			PubArticleJsonHelper.putRedisListOne(context.getArticle());
			PubArticleJsonHelper.putRedisArticle(context.getArticle());
		}
		
		return PubArticle.SUCCESS;
	}
	
	//一个发布渠道的发布
	private int pubChannel(Template template, int index) {
		PageDir pageDirs = context.getPageDir(index);
		PageUrl pageUrls = context.getPageUrl(index);;
		
		//若没有配置发布目录，则该渠道不继续发布
		if (noPubDir(pageDirs)) {
			if (log.isDebugEnabled()) log.debug(getChannelName(index) + ":没有发布目录");
			return PubArticle.SUCCESS;
		}
		
		int success = PubArticle.SUCCESS;
		
		//先发布正文附件（图片、标题图片）
		pubAttachments(pageDirs, pageUrls, index);
		
		//再发布扩展字段的附件
		pubAttExts(pageUrls.getAttachment(), pageDirs, index);
		
		//在发布之前，把稿件的标题图片改为发布url
		changePicTitleUrl(pageUrls.getPic());
		
		// 只有文章/图片/视频/活动/文档/网站专题才需要发布稿件。其它如链接稿和多标题稿件不需要发布稿件，只改状态即可
		if (context.getMessage().getType() <= Article.TYPE_VIDEO
				|| context.getMessage().getType() == Article.TYPE_ACTIVITY
				|| context.getMessage().getType() == Article.TYPE_FILE
				|| context.getMessage().getType() == Article.TYPE_PANORAMA
				|| context.getMessage().getType() == Article.TYPE_SPECIAL
				|| (context.getMessage().getType() == Article.TYPE_LIVE
					&& context.getArticle().getChannel() == 2)) {
			
			BareArticle article = context.getArticle();
			//先修改稿件链接，因为模板中可能会引用
			if (index == 0){
				article.setUrl(pageUrls.getArticle());
				article.setUrlPad(pageUrls.getAnotherUrl());
			} else {
				article.setUrlPad(pageUrls.getArticle());
				article.setUrl(pageUrls.getAnotherUrl());
			}
			// 再发布挂件附件
			success = pubWidgets(pageDirs, pageUrls, index);
			if (success != PubArticle.SUCCESS) return success;
			
			if (template != null) {
				// 最后发布稿件
				success = pubPage(pageDirs, template, index);
				if (success == PubArticle.SUCCESS && !preview) {
					//修改数据库中稿件url
					changeUrl(pageUrls.getArticle(), index);
					
					if (log.isDebugEnabled())
						log.debug(getChannelName(index) + "：已发布 " + pageDirs.getArticle());
					else
						log.info(pageDirs.getArticle());
				}
			} else {
				if (log.isDebugEnabled()) {
					String ch = getChannelName(index);
					if (context.getMessage().getType() == Article.TYPE_SPECIAL && context.getArticle().getChannel() == 1)
						log.debug( ch + " : 无模板（或者专题模板过期），不发布. —— " + context.getArticle().getColumn());
					else
						log.debug( ch + " : 无模板，不发布. —— " + context.getArticle().getColumn());
				}
			}
		} else {
			if (log.isDebugEnabled()) log.debug("非文/图/视频发布");
		}
		return success;
	}
	//把正文中的图片复制到外网，并记录图片的url
	private void pubAttachments(PageDir pageDirs, PageUrl pageUrl, int index) {
		if (preview) {
			previewAttachments(index);
			
			return; //预览时不做
		}
		
		String suffix = (index == 0) ? "" : "Pad";

		List<Attachment> atts = context.getArticle().getAttachments();
		if (atts == null || atts.size() == 0) return;
		
		try {
			String tCode = LibHelper.getTenantCodeByLib(context.getMessage().getDocLibID());
			
			String table = LibHelper.getLibTable(DocTypes.ATTACHMENT.typeID(), tCode);
			String sql = "update " + table + " set att_url" + suffix + "=? where SYS_DOCUMENTID=?";
			String sql2 = "update " + table + " set att_picUrl" + suffix + "=? where SYS_DOCUMENTID=?";
			
			for (Attachment att : atts) {
				if (att.getType() == Article.ATTACH_FILE) {
					pubAttFiles(att, pageDirs, pageUrl.getAttachment(), sql, index);
				}else if (att.getType() != Article.ATTACH_VIDEO) {
					pubAttPic(att, pageDirs, pageUrl.getPic(), sql, index);
				} else {
					pubAttVideoPic(att, pageDirs, pageUrl.getPic(), sql2, index);
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	private void pubAttFiles(Attachment att, PageDir pageDirs,	String pageUrl, String sql, int index) throws E5Exception {
		//若附件是在线附件，不需要发布
		if (att.getPath().toLowerCase().startsWith("http"))
			return;
		
		if (index == 0 || !context.isSameAttDir()) {
			//复制附件信息，生成分发信息文件
			if(att.getType() == Article.ATTACH_FILE)
				copyAndTransFiles(att.getPath(), pageDirs, att.getFileName());
		}
		
		//附件的外网地址保存到附件表中
		String url = pageUrl + "/" + att.getFileName();
		InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, new Object[]{url, att.getId()});
		
		//修改对象
		if (index == 0)
			att.setUrl(url);
		else
			att.setUrlPad(url);
		
		//稿件正文中的地址变化，换成外网的附件地址
		changeAttFileUrl(att, index, url);
	}

	private void copyAndTransFiles(String path, PageDir pageDirs, String fileName) {
		copyAndTrans(path, pageDirs.getAttachment(), fileName, pageDirs.getRoot());
	}

	private void changeAttFileUrl(Attachment att, int index, String url) {
		//稿件正文中的地址变化，换成外网的大图
		if (att.getType() == Article.ATTACH_FILE) {
			BareArticle article = context.getArticle();
			String content = article.getContent();
			content = content.replace("../../xy/file.do?path=" + att.getPath(), url);
			article.setContent(content);
		}
	}

	//发布附件的图片
	private void pubAttPic(Attachment att, PageDir pageDirs, String pageUrl, String sql, int index) throws E5Exception {
		//若附件是外网图片，不需要发布
		if (att.getPath().toLowerCase().startsWith("http"))
			return;
		
		if (index == 0 || !context.isSamePicDir()) {
			//2016.3.4：web版、触屏版都发布大中小图，以便同发布目录时只需要发布一次
			if (/*(isAppArticle() || index == 1) && */(att.getType() == Article.ATTACH_PIC
					|| (att.getType() == Article.ATTACH_PICTITLE_BIG))) {
				 //正文图片和标题大图：复制图片、大中小图，生成分发信息文件
				copyPicFile(att.getPath(), pageDirs, att.getFileName());
			} else {
				//其它标题图片，则只复制图片，生成分发信息文件
				copyAndTrans(att.getPath(), pageDirs, att.getFileName());
			}
		}
		
		//图片的外网地址保存到附件表中
		String url = pageUrl + "/" + att.getFileName();
		InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, new Object[]{url, att.getId()});
		
		//修改对象
		if (index == 0)
			att.setUrl(url);
		else
			att.setUrlPad(url);
		
		//稿件正文中的地址变化，换成外网的大图
		changeAttPicUrl(att, index, url);
	}
	
	//发布附件的视频关键帧图片
	private void pubAttVideoPic(Attachment att, PageDir pageDirs, String pageUrl, String sql, int index) throws E5Exception {
		if (StringUtils.isBlank(att.getPicPath()) 
				|| att.getPicPath().toLowerCase().startsWith("http")) return;
		
		//取出文件名
		int pos = att.getPicPath().lastIndexOf("/");
		if (pos < 0) return;
		String picFileName = att.getPicPath().substring(pos + 1);
		
		//复制图片、大中小图，生成分发信息文件。若触屏版和主版的发布目录相同就不必再发
		if (index == 0 || !context.isSamePicDir()) {
			copyPicFile(att.getPicPath(), pageDirs, picFileName);
			//copyAndTrans(att.getPicPath(), pageDirs, picFileName); //原逻辑：web下不复制大中小图
		}
		
		String url = pageUrl + "/" + picFileName;
		if (index == 1) {
			att.setPicUrlPad(url);
		} else {
			att.setPicUrl(url);
		}
		//图片的外网地址保存到附件表中
		InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, new Object[]{url, att.getId()});
	}
	
	//发布扩展字段中的附件
	private void pubAttExts(String pageUrl, PageDir pageDirs, int index) {
		List<String> extFileFields = context.getExtFileFields();
		if (extFileFields == null || extFileFields.isEmpty()) return;
		
		for (String extField : extFileFields) {
			String path = context.getArticle().getExtFields().get(extField);
			if (StringUtils.isBlank(path)) continue;
			
			//取出文件名
			int pos = path.lastIndexOf("/");
			if (pos < 0) continue;
			
			String fileName = path.substring(pos + 1);
			
			if (index == 0 || !context.isSameAttDir()) {
				copyAndTrans(path, pageDirs.getAttachment(), fileName, pageDirs.getRoot());
			}
			
			//改成外网url
			String url = pageUrl + "/" + fileName;
			context.getArticle().getExtFields().put(extField, url);
		}
	}
	
	private void copyAndTrans(String picPath, PageDir pageDirs, String fileName) {
		copyAndTrans(picPath, pageDirs.getPic(), fileName, pageDirs.getRoot());
	}
	
	private void copyAndTrans(String attPath, String destPath, String destFileName, String rootPath) {
		if (!StringUtils.isBlank(destFileName)) {
			//复制文件到外网
			InfoHelper.copyFile(attPath, destPath, destFileName);
		
			//trans：生成分发信息文件
			String path = destPath + "/" + destFileName;
			PublishHelper.writeTransPath(path, rootPath);
		}
	}

	//把挂件中的附件和投票图片复制到外网
	private int pubWidgets(PageDir pageDirs, PageUrl pageUrls, int index) {
		if (preview) {
			previewWidgets(index);
			return PubArticle.SUCCESS; //预览时不做
		}
		
		Widgets widgets = context.getArticle().getWidgets();
		if (widgets == null) return PubArticle.SUCCESS;
		
		String suffix = (index == 0) ? "" : "Pad";
		try {
			String tCode = LibHelper.getTenantCodeByLib(context.getMessage().getDocLibID());
			//发布附件到附件目录
			List<Attachment> atts = widgets.getAttachments();
			if (atts != null && atts.size() > 0) {
				String table = LibHelper.getLibTable(DocTypes.WIDGET.typeID(), tCode);
				String sql = "update " + table + " set w_url" + suffix + "=? where SYS_DOCUMENTID=?";
				
				for (Attachment att : atts) {
					String swfSuffix = "";
					if (index == 0 || !context.isSameAttDir()) {
						//若是文档，则发布转码后的swf文件
						if (isFile(att.getFileName())) swfSuffix = ".swf";
						
						copyAndTrans(att.getPath() + swfSuffix, pageDirs.getAttachment(), 
								att.getFileName() + swfSuffix, pageDirs.getRoot());
					}
					//挂件文件的外网地址
					String url = pageUrls.getAttachment() + "/" + att.getFileName() + swfSuffix;
					//修改对象url
					if (index == 0)
						att.setUrl(url);
					else
						att.setUrlPad(url);
					//保存到挂件表中
					InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, new Object[]{url, att.getId()});
				}
			}
			//发布投票选项中的图片到图片目录
			if (widgets.getVote() != null) {
				for (VoteOption option : widgets.getVote().getOptions()) {
					if (!StringUtils.isBlank(option.getPicUrl())) {
						if (index == 0 || !context.isSameAttDir()) {
							copyAndTrans(option.getPicUrl(), pageDirs, option.getPicFile());
						}
						String url = pageUrls.getPic() + "/" + option.getPicFile();
						option.setPicUrl(url);
					}
				}
			}
			
			return PubArticle.SUCCESS;
		} catch (E5Exception e) {
			log.error("发布挂件时出错：" + e.getLocalizedMessage(), e);
			return PubArticle.ERROR_PUBLISH_WIDGETS;
		}
	}
	//判断是否文档
	private boolean isFile(String suffix) {
		//不是“文档”稿件，则不会做转码
		if (context.getMessage().getType() != Article.TYPE_FILE)
			return false;
		
		suffix = suffix.toLowerCase();
		return suffix.endsWith(".docx")
				|| suffix.endsWith(".doc")
				|| suffix.endsWith(".pptx")
				|| suffix.endsWith(".ppt")
				|| suffix.endsWith(".xlsx")
				|| suffix.endsWith(".xls")
				|| suffix.endsWith(".pdf")
				|| suffix.endsWith(".txt")
				|| suffix.endsWith(".odt");
	}
	//稿件文件发布过程
	private int pubPage(PageDir pageDirs, Template template, int index) {
		try {
			List<String> pageContents = applyTemplate(template, index);
			if (preview) {
				if (pageContents.size() > 0)
					previewPages[index] = pageContents.get(0); //若是预览，则把生成网页的第一页保存下来
				return PubArticle.SUCCESS;
			} else {
				return pushArticleFile(pageContents, pageDirs);
			}
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}
	
	//预览时对附件的处理：图片附件，预览时设置url，否则只有path属性
	private void previewAttachments(int index) {
		List<Attachment> atts = context.getArticle().getAttachments();
		if (atts == null || atts.size() == 0) return;
		
		for (Attachment att : atts) {
			if (att.getType() != Article.ATTACH_VIDEO) {
				if (att.getPath().toLowerCase().startsWith("http"))
					continue;
				
				String url = "../../xy/image.do?path=" + att.getPath();
				//图片附件，预览时设置url，否则只有path属性
				if (index == 0)
					att.setUrl(url);
				else
					att.setUrlPad(url);
			}
		}
	}
	
	//预览时对挂件的处理：设置挂件中的附件和投票图片的地址
	private int previewWidgets(int index) {
		Widgets widgets = context.getArticle().getWidgets();
		if (widgets == null) return PubArticle.SUCCESS;
		
			//发布附件到附件目录
			List<Attachment> atts = widgets.getAttachments();
			if (atts != null && atts.size() > 0) {
				
				for (Attachment att : atts) {
					//挂件文件的外网地址
					String url = "../../xy/image.do?path=" + att.getPath();
					//修改对象
					if (index == 0)
						att.setUrl(url);
					else
						att.setUrlPad(url);
				}
			}
			//发布投票选项中的图片到图片目录
			if (widgets.getVote() != null) {
				for (VoteOption option : widgets.getVote().getOptions()) {
					if (!StringUtils.isBlank(option.getPicUrl())) {
						String url = "../../xy/image.do?path=" + option.getPicFile();
						option.setPicUrl(url);
					}
				}
			}
			
			return PubArticle.SUCCESS;
	}

	//稿件正文中的地址变化，换成外网的大图
	private void changeAttPicUrl(Attachment att, int index, String url) {
		//稿件正文中的地址变化，换成外网的大图
		if (att.getType() == Article.ATTACH_PIC) {
			BareArticle article = context.getArticle();
			String content = article.getContent();
			
			if ((isAppArticle() || index == 1)) {
				//触屏版：正文中的图片替换为抽图后的大图
				content = content.replace("../../xy/image.do?path=" + att.getPath(), url + ".2");
			} else {
				content = content.replace("../../xy/image.do?path=" + att.getPath(), url);
			}
			article.setContent(content);
		}
	}

	private boolean isAppArticle() {
		return context.getMessage().getChannel() == 2;
		
	}

	/**
	 * 发布时复制图片到外网，同时包括3个额外的文件（.0/.1/.2）
	 */
	private void copyPicFile(String srcPathFile, PageDir pageDirs, String destFileName) {
		int pos = srcPathFile.indexOf(";");
		if (pos < 0) return;
		
		String deviceName = srcPathFile.substring(0, pos);
		String savePath = srcPathFile.substring(pos + 1);
	
		String destPath = pageDirs.getPic();
		if (!destPath.endsWith("/")) destPath += "/";
		
		destPath += (destFileName == null) ? savePath : destFileName;
		
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//存储设备可能是编码过的，先解码
			deviceName = URLDecoder.decode(deviceName, "UTF-8");
			StorageDevice device = sdManager.getByName(deviceName);
			String devicePath = InfoHelper.getDevicePath(device);
			
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs.getRoot(), null);
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs.getRoot(), ".0");
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs.getRoot(), ".1");
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs.getRoot(), ".2");
			
		} catch (E5Exception | IOException e1) {
			e1.printStackTrace();
			System.out.println("articleID:" + context.getMessage().getId() + ",deviceName:" + deviceName);
		}
	}
	private void onePicCopyTrans(String devicePath, String savePath, String destPath, String transDir, String suffix) {
		if (suffix != null) destPath = destPath + suffix;
		File destFile = new File(destPath);
		
		try {
			File file = getSaveFile(devicePath, savePath, suffix);
			if (file.exists()) {
				FileUtils.copyFile(file, destFile);
				//trans：生成分发信息文件
				PublishHelper.writeTransPath(destPath, transDir);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			System.out.println("-------params :----------");
			System.out.println("articleID:" + context.getMessage().getId());
			System.out.println("devicePath:" + devicePath);
			System.out.println("srcPath:" + savePath);
			System.out.println("destPath:" + destPath);
			System.out.println("transDir:" + transDir);
		}
	}
	//取源图片文件供复制。尽量用.2.jpg发布，无时再用.2发布
	private File getSaveFile(String devicePath, String savePath, String suffix) {
		File file = null;
		if (suffix != null) {
			file = new File(devicePath, savePath + suffix + ".jpg");
			if (!file.exists())
				file = new File(devicePath, savePath + suffix);
		} else {
			file = new File(devicePath, savePath);
		}
		return file;
	}
	
	//检查图片是否已经抽图
	private boolean extractDone() {
		//不需要发布触屏版时，不检查抽图情况
		if (context.getColumn().getPubRulePad() < 1)
			return true;
		
		List<Attachment> atts = context.getArticle().getAttachments();
		if (atts == null || atts.size() == 0) return true;

		//检查每个附件的抽图情况
		boolean result = true;
		for (Attachment att : atts) {
			//需要抽图的是正文图片、标题图片的大图
			if ((att.getType() == Article.ATTACH_PIC
						|| (att.getType() == Article.ATTACH_PICTITLE_BIG)))
			{
				result = checkIfExtracted(att.getPath());
				if (!result) break;
			}
		}
		//若没有抽图完毕，则改成等待抽图状态
		if (!result) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			try {
				Document doc = docManager.get(context.getMessage().getDocLibID(), context.getMessage().getId());
				doc.set("a_status", Article.STATUS_EXTRACTING);
				docManager.save(doc);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private boolean checkIfExtracted(String srcPathFile) {
		//若是外网图片，不需要检查
		if (srcPathFile.toLowerCase().startsWith("http")) return true;
		
		int pos = srcPathFile.indexOf(";");
		if (pos < 0) return true;
		
		String deviceName = srcPathFile.substring(0, pos);
		String savePath = srcPathFile.substring(pos + 1);
	
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//存储设备可能是编码过的，先解码
			deviceName = URLDecoder.decode(deviceName, "UTF-8");
			StorageDevice device = sdManager.getByName(deviceName);
			
			String devicePath = InfoHelper.getDevicePath(device);
			
			//是否抽图
			return extracted(devicePath, savePath);
		} catch (E5Exception | IOException e1) {
			e1.printStackTrace();
		}
		return true;
	}
	
	//是否已抽图：只判断大图是否存在即可
	private boolean extracted(String devicePath, String savePath) {
		boolean extracted = new File(devicePath, savePath + ".2.jpg").exists();
		if (!extracted) extracted = new File(devicePath, savePath + ".2").exists();
		
		return extracted;
	}

	private String getComponentResult(String comID) throws Exception{
		String result = null;
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, comID);
		if (componentObj == null) {
			result = "";
		} else {
			Component component = null;
			component = ComponentFactory.newComponent(param, componentObj ,preview);
			component.setArticle(context.getArticle());
			result = component.getComponentResult();
		}
		return result;
	}
	
	private boolean noPubDir(PageDir pageDir) {
		boolean result = pageDir == null
				|| pageDir.getRoot() == null
				|| pageDir.getPicRoot() == null
				|| pageDir.getArticle() == null
				|| pageDir.getAttachment() == null
				|| pageDir.getPic() == null;
		return result;
	}

	//套用模板，得到结果，可能是分页阅读，多个网页
	private List<String> applyTemplate(Template template, int index) throws Exception{
		List<String> result = new ArrayList<>();
		
		//稿件的全部正文内容
		String content = context.getArticle().getContent();
		if (isAppArticle() || index == 1) {
			//对于触屏文章，替换内容中的embed为video
			if (context.getArticle().getType() == Article.TYPE_ARTICLE
					|| context.getArticle().getType() == Article.TYPE_ACTIVITY)
			content = JsoupHelper.replaceVideo(content);
		}


        String videoID = "";
        try {
            if(context.getArticle().getType()==Article.TYPE_VIDEO){
                videoID = context.getArticle().getAttVideos().get(0).getUrl();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        context.getArticle().setVideoID(videoID);

		content = content.replaceAll("\r|\n|\t", ""); //倒数据等情况下可能有\r\n字符，模板套用后会有rn字符
		
		String url = (index == 0) 
				? context.getArticle().getUrl()
				: context.getArticle().getUrlPad();
		if(index ==1){
			content = JsoupHelper.replaceRel(content);
		}
		//按分页符分成多页内容
		String[] pageContents = splitPages(content,url);
		
		//模板内容
		String templateContent = getTemplateContent(template);
		
		for (int i = 0; i < pageContents.length; i++) {
			context.getArticle().setContent(pageContents[i]);//改变稿件的内容为其中一页
			//改变稿件url
			if(index == 0){
				context.getArticle().setUrl(pathOfSplit(url, i));
			}else{
				context.getArticle().setUrlPad(pathOfSplit(url, i));
			}
			context.getArticle().setLastPage(i == pageContents.length - 1);//设置是否最后一页的标识
			
			//APP稿只处理触屏版
			if((isAppArticle() && index == 1) || !isAppArticle()){
				//只有页数大于1且不是最后一页时，判断是否点击翻页并添加链接
				if(pageContents.length > 1 && i < pageContents.length - 1){
					String nextPageUrl = pathOfSplit(url, i+1);
					String contentPage = JsoupHelper.replaceNextPage(pageContents[i], nextPageUrl);
					context.getArticle().setContent(contentPage);//改变稿件的内容为其中一页
				}
			}

			String page = mergeContent(templateContent,index);
			result.add(page);
		}
		
		//若稿件是分页的，则需要再合成一个全部阅读页
		if (pageContents.length > 1) {
			content = content.replace(splitor, ""); //去掉分页符
			
			context.getArticle().setContent(content);
			context.getArticle().setLastPage(true);
			//合成一个"....._0.html"
			if(index == 0){
				context.getArticle().setUrl(pathOfSplit(url, -1));
			}else{
				context.getArticle().setUrlPad(pathOfSplit(url, -1));
			}
			String page = mergeContent(templateContent, index);
			result.add(page);
		}
		return result;
	}
	
	// 分页阅读
	private String[] splitPages(String content,String url) throws IOException {
		String[] pages = null;
		if (content.indexOf(splitor) >= 0 && !preview) {
			pages = content.split(splitor);
			for (int i = 0; i < pages.length; i++) {
				pages[i] += pageContent(pages.length, i, url);
			}
		} else if(preview){
			pages = new String[]{content.replace(splitor, "<hr/>")};
		} else {
			pages = new String[]{content};
		}
		return pages;
	}

	//分页区域的html代码
	private String pageContent(int pages, int current, String url) {
		if ("是".equals(InfoHelper.getConfig("发布服务", "分页样式采用旧版翔宇样式"))) {
			if (pages <= 1) return "";
			String result = "<div width=\"100%\" id=\"autopage\"><center>";
			if (current > 0) {
				result = result + "<a href='" + this.urlOfSplitPages(current - 1, url) + "'><</a>&nbsp;&nbsp;";
			}
			for (int i = 0; i < pages; i++) {
				if (i == current) {
					result += "<span>" + (i + 1) + "</span>&nbsp;&nbsp;";
				} else {
					result += "<a href='" + urlOfSplitPages(i, url) + "'>" + (i + 1) + "</a>&nbsp;&nbsp;";
				}
			}
			if (current < pages - 1) {
				result = result + "<a href='" + this.urlOfSplitPages(current + 1, url) + "'>></a>&nbsp;&nbsp;";
			}
			result += "</center> <br/><br/></div>";
			return result;
		} else {
			String result = "<ul pages='" + pages + "' current='" + (current + 1) + "' class='pages'>";

			if (current > 0)
				result += "<li class='page-previous'><a href='" + urlOfSplitPages(current - 1, url) + "'>上一页</a></li>";
			for (int i = 0; i < pages; i++) {
				result += "<li class='page page" + (i + 1);
				if (i == current) {
					result += " page-active";
					result += "'>" + (i + 1) + "</li>";
				} else {
					result += "'><a href='" + urlOfSplitPages(i, url) + "'>" + (i + 1) + "</a></li>";
				}
			}
			if (current < pages - 1)
				result += "<li class='page-next'><a href='" + urlOfSplitPages(current + 1, url) + "'>下一页</a></li>";

			result += "<li class='page-all'><a href='" + urlOfSplitPages(-1, url) + "'>全文阅读</a></li>";

			result += "</ul>";
			return result;
		}
	}
	private String urlOfSplitPages(int page,String url) {
		if (page == 0)
			return url;
		return pathOfSplit(url, page);
	}

	//与模板合并，得到html页面内容
	private String mergeContent(String templateContent, int index) throws Exception {
		PubArticle article = context.getArticle();
		String oldContent = article.getContent(); //先记住原来的正文内容
		
		//合成前，在稿件正文头添加一些注释信息，如id等，做其它用途
		insertNotes(index);
		
		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(componentMatcher.group(1));
			cr = cr.replace("$", "\\$"); //注意$符号
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		//最后再改回原来的内容
		article.setContent(oldContent);
		
		return pageContent.toString();
	}
	
	//在稿件正文头添加一些注释信息，如id等，做其它用途
	private void insertNotes(int index) {
		PubArticle article = context.getArticle();

		String picUrl = article.getPicBig();
		if (StringUtils.isBlank(picUrl)) picUrl = article.getPicMiddle();
		if (StringUtils.isBlank(picUrl)) picUrl = article.getPicSmall();
		
		String pubTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(article.getPubTime());
		
		StringBuilder notes = new StringBuilder();
		//正文前后用标记包裹
		notes.append("<!--enpcontent-->");
		notes.append(article.getContent());
		notes.append("<!--/enpcontent-->");
		
		//正文属性
		notes.append("<!--enpproperty <articleid>")
			.append(article.getId()).append("</articleid><date>")
			.append(pubTime).append("</date><author>")
			.append(article.getAuthor()).append("</author><title>")
			.append(article.getTitle()).append("</title><keyword>")
			.append(article.getKeyword()).append("</keyword><subtitle>")
			.append(article.getSubTitle()).append("</subtitle><introtitle>")
			.append(article.getIntroTitle()).append("</introtitle><siteid>")
			.append(article.getSiteID()).append("</siteid><nodeid>")
			.append(context.getColumn().getId()).append("</nodeid><nodename>")
			.append(context.getColumn().getName()).append("</nodename><nodesearchname>")
			.append(context.getColumn().getSearchName()).append("</nodesearchname><picurl>")
			.append(picUrl).append("</picurl><picbig>")
			.append(article.getPicBig()).append("</picbig><picmiddle>")
			.append(article.getPicMiddle()).append("</picmiddle><picsmall>")
			.append(article.getPicSmall()).append("</picsmall><url>")
			.append(article.getUrl()).append("</url><urlpad>")
			.append(article.getUrlPad()).append("</urlpad><liability>")
			.append(article.getLiability()).append("</liability><sourcename>")
			.append(article.getSource()).append("</sourcename><abstract>")
			.append(article.getSummary()).append("</abstract><channel>")
			.append(index+1).append("</channel>/enpproperty-->");


		article.setContent(notes.toString());
	}

	//把稿件结果文件存储到外网
	private int pushArticleFile(List<String> pageContents,PageDir pageDirs) throws Exception{
		String pathName = pageDirs.getArticle();
		
		if(StringUtils.isEmpty(pathName)){
			log.error("发布路径为空，发布失败！");
			return PubArticle.ERROR_NO_PUBDIR;
		}
		String path;
		for (int i = 0; i < pageContents.size(); i++) {
			if (pageContents.size() == 1)
				path = pathName; //不分页时
			else if (i < pageContents.size() - 1)
				path = pathOfSplit(pathName, i); //分页时，最后一个是全文阅读
			else
				path = pathOfSplit(pathName, -1);//全文阅读
			
			String pageContent = pageContents.get(i);
			
			FileUtils.writeStringToFile(new File(path), pageContent, "UTF-8");
			
			//trans：生成分发信息文件
			PublishHelper.writeTransPath(path, pageDirs.getRoot());
		}
		
		return PubArticle.SUCCESS;
	}
	
	//分页显示的文件名（后缀部分加页号）
	private String pathOfSplit(String pathName, int pageNo) {
		//第一页，路径不变
		if (pageNo == 0)
			return pathName;
		
		int pos = pathName.lastIndexOf(".");
		return pathName.substring(0, pos) + "_" + (pageNo + 1) + pathName.substring(pos);
	}
	
	//发布后，修改稿件的url
	private void changeUrl(String pageUrl, int index) {
		if (pageUrl == null) pageUrl = "";
		
		Object[] params = null;
		String sql = null;
		
		String suffix = (index == 0) ? "" : "Pad";
		try {
			String table = LibHelper.getLibTable(context.getMessage().getDocLibID());
			sql = "update " + table + " set a_url" + suffix + "=? where SYS_DOCUMENTID=?";
			params = new Object[]{pageUrl, context.getMessage().getId()};
			
			InfoHelper.executeUpdate(context.getMessage().getDocLibID(), sql, params);
		} catch (E5Exception e) {
			log.error("changeUrl error:" + sql, e);
			for (Object param : params) {
				log.info(param);
			}
		}
	}

	//在发布之前，把稿件的标题图片改为发布url
	private void changePicTitleUrl(String picUrl) {
		if (preview) {
			BareArticle article = context.getArticle();
			article.setPicBig(_urlPreview(article.getPicBig()));
			article.setPicMiddle(_urlPreview(article.getPicMiddle()));
			article.setPicSmall(_urlPreview(article.getPicSmall()));
			
			return; //预览时不做
		}
		else {
			BareArticle article = context.getArticle();
			article.setPicBig(_url(article.getPicBig(), picUrl));
			article.setPicMiddle(_url(article.getPicMiddle(), picUrl));
			article.setPicSmall(_url(article.getPicSmall(), picUrl));
		}
	}

	/**
	 * 发布后的处理：
	 * 1）改变稿件发布状态
	 * 2）若是Web库的专题稿件，则加入专题稿相关栏目Hash
	 * 3）把<主栏目ID，当前时间>写入Redis中小红点的栏目最新发布时间Hash
	 */
	private void afterPublish() {
		//改变稿件发布状态
		changeStatus();
	
		//Web库的专题稿件，加入专题稿相关栏目Hash
		if (context.getArticle().getChannel() == 1 
				&& context.getArticle().getType() == Article.TYPE_SPECIAL) {
			Template template = context.getTemplate()[0];
			if (template != null && template.getColRelated() != null) {
				String expire = null;
				if (template.getExpireDate() == null)
					expire = "";
				else
					expire = DateUtils.format(template.getExpireDate());
				//Redis里<articleID, {expire:"",colID:"",colRelated:""}>
				String json = "{\"expire\":\"" + expire
						+ "\",\"colID\":\"" + context.getArticle().getColumnID()
						+ "\",\"colRelated\":\"" + com.founder.e5.commons.StringUtils.join(template.getColRelated())
						+ "\"}";
				RedisManager.hset(RedisKey.SPECIAL_ARTICLES, context.getMessage().getId(), json);
			}
		}
		
		//把<主栏目ID，当前时间>写入Redis中小红点的栏目最新发布时间Hash
		long colID = context.getColumn().getId();
		String time = String.valueOf(context.getArticle().getPubTimeReal().getTime());
		RedisManager.hset(RedisKey.RED_DOT_ARTICLE, colID, time);
		
		//删除草稿
		deleteDraft();
	}
	//删除草稿
	private void deleteDraft() {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			int draftLibID = LibHelper.getLibIDByOtherLib(DocTypes.DRAFT.typeID(), 
					context.getMessage().getDocLibID());
			docManager.delete(draftLibID, context.getMessage().getId());
		} catch (Exception e) {
			e.printStackTrace() ;
	    }
	}

	//发布后，修改稿件的发布状态、流程状态
	private void changeStatus() {
		int docLibID = context.getMessage().getDocLibID();
		long docID = context.getMessage().getId();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(docLibID, docID);
			
			int flowID = doc.getCurrentFlow();
			
			FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
			FlowNode[] nodes = flowReader.getFlowNodes(flowID);
			
			//已发布：是流程的最后一个阶段
			int lastNodeID = nodes[nodes.length - 1].getID();
			
			doc.setCurrentNode(lastNodeID);
			doc.set("a_status", Article.STATUS_PUB_DONE);
			
			//设置实发时间、第一次发布时设置发布时间
			setPubTime(doc);
			
			docManager.save(doc);
		} catch (E5Exception e) {
			log.error(e);
		}
	}

	//设置实发时间、第一次发布时间
	private void setPubTime(Document doc) {
		//若还没有实发时间，证明是第一次发布，设置发布时间。撤稿重发时不改变发布时间
		Timestamp lastPubTime = doc.getTimestamp("a_realPubTime");
		if (lastPubTime == null) {
			//在context里已经设置了一次发布时间了，保持一致，避免零点前后的错误
			Timestamp pubTime = new Timestamp(context.getArticle().getPubTime().getTime());
			doc.set("a_pubTime", pubTime);
		}
		
		//前面模板生成前已经设置了实际发布时间
		doc.set("a_realPubTime", new Timestamp(context.getArticle().getPubTimeReal().getTime()));
	}

	private String _url(String oldUrl, String prefix) {
		if (StringUtils.isBlank(oldUrl)) return "";

		int pos = oldUrl.lastIndexOf("/");
		if (pos < 0) return "";
		
		oldUrl = oldUrl.substring(pos);
		return prefix + oldUrl;
	}

	private String _urlPreview(String oldUrl) {
		if (StringUtils.isBlank(oldUrl)) return "";
		
		return "../../xy/image.do?path=" + oldUrl;
	}
}
