package com.founder.xy.jpublish.paper;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.page.AbstractGenerator;
import com.founder.xy.jpublish.template.ComponentFactory;
import com.founder.xy.jpublish.template.component.Component;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.template.Template;

/**
 * 数字报发布生成器
 * 包括两种场景：
 * 1）入库服务，发布某一刊期的所有版面
 * 2）单版重发，发布某一个版面
 * 
 * @author Gong Lijie
 */
public class PaperGenerator extends AbstractGenerator{
	private PaperContext context;
	String lprefix = InfoHelper.getConfig( "发布服务", "栏目生成页前缀")==null||InfoHelper.getConfig( "发布服务", "栏目生成页前缀").contains("$")?"l":InfoHelper.getConfig( "发布服务", "栏目生成页前缀");

	String cprefix = InfoHelper.getConfig( "发布服务", "稿件生成页前缀")==null||InfoHelper.getConfig( "发布服务", "稿件生成页前缀").contains("$")?"c":InfoHelper.getConfig( "发布服务", "稿件生成页前缀");


	//是否为滚动模板
	boolean scrollFlag=false;
	public boolean generator(DocIDMsg data){
		if (log.isDebugEnabled()) log.debug("---数字报发布 " + data.getDocLibID()
				+ "," + data.getDocID() + "," + data.getRelIDs());
		
		context = new PaperContext();
		if (context.init(data, log) != PubArticle.SUCCESS) {
			return false;
		}


		Template template = context.getTemplateLayoutPad();
		if(template!=null){
			//判断模板是不是横向滚动的
			scrollFlag = checkTemplate(template);
		}
		if(scrollFlag && data.getRelIDs() == null){

			context.readLayouts(context.getLayouts(),context.getLayouts().get(0).getDate(),false);
			//把第一个去掉，重复了
			context.getLayouts().remove(0);
		}

		boolean result = pubChannel(1);
		if (result) {
			result = pubChannel(0);
		}
		
		//改变发布状态
		if (result) {
			changeStatus();
		}
		
		if (log.isDebugEnabled()) log.debug("发布完成： " + data.getDocLibID()
				+ "," + data.getDocID() + "," + data.getRelIDs());
		return true;
	}
	
	//一个发布渠道
	private boolean pubChannel(int index) {
		//无模板
		Template templateArticle = (index == 0) ? context.getTemplateArticle() : context.getTemplateArticlePad();
		if (templateArticle == null) {
			if (log.isDebugEnabled()) log.debug(getChannelName(index) + ":无模板，不发布");
			return true;
		}
		
		//无发布目录，不继续发布
		String[] pageDirs = getPageDir(index);
		if (pageDirs[0] == null) {
			if (log.isDebugEnabled()) log.debug(getChannelName(index) + ":没有发布目录");
			return true;
		}
		
		String[] pageUrls = getPageUrl(index);
		Template template = (index == 0) ? context.getTemplateLayout() : context.getTemplateLayoutPad();

		//第一步先发布所有的附件，附件Url赋值
		boolean needPubLayout = (template != null);
		int success = pub1Attachments(needPubLayout, pageDirs, pageUrls, index);
		if (success != PubArticle.SUCCESS) return false;
		
		//计算出版面和稿件的绝对Url，用于发布后回写数据库
		setAbsoluteUrls(index, pageDirs, pageUrls, template, templateArticle);
		
		//第二步，发布所有版面下的稿件
		success = pub2Articles(index, pageUrls, templateArticle);
		if (success != PubArticle.SUCCESS) return false;
		
		//第三步，发布版面
		pub3Layouts(index, pageUrls, template);
		if (success != PubArticle.SUCCESS) return false;
		
		//第四步，发布首页
		pub4Home(index, pageDirs, pageUrls);
		if (success != PubArticle.SUCCESS) return false;
		
		//第五步，添加period.xml文件
		pub5PeriodFile(index, pageDirs[4]);

		//第六布，发布报纸信息
		updatePapersInfo(index);
		return true;
	}


	private boolean checkTemplate(Template template) {
		String templateContent = getTemplateContent(template);
		if(templateContent != null) {
			Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			while (componentMatcher.find()) {
				String componentObj = RedisManager.hget(RedisKey.CO_KEY, componentMatcher.group(1));
				String componentData = "";
				if(StringUtils.isBlank(componentObj)){ //如果redis中没有缓存,从数据库中获取
					try {
						Document doc = docManager.get(LibHelper.getComponentObjLibID(), Integer.parseInt(componentMatcher.group(1)));
						componentData = doc.getString("co_data");
					} catch (E5Exception e) {
						e.printStackTrace();
					}
				}else{
					JSONObject componentJSON= JSONObject.fromObject(componentObj);
					componentData = componentJSON.getString("data");
				}
				JSONObject dataJSON= JSONObject.fromObject(componentData);
				if(dataJSON.containsKey("scrollFlag")&&"true".equals(dataJSON.getString("scrollFlag")))
					return true;
			}
		}
		return false;
	}

	//第一步，发布所有附件文件：把附件送到发布目录、得到附件的Url并写入数据库
	private int pub1Attachments(boolean needPubLayout, String[] pageDirs, String[] pageUrls, int index) {
		int success = PubArticle.SUCCESS;

		List<PaperLayout> layouts = context.getLayouts();
		for (PaperLayout layout : layouts) {
			//先发布版面下稿件的附件
			for (PaperArticle article : layout.getArticles()) {
				for (Attachment att : article.getAttachments()) {
					pubAttOne(att, pageDirs, pageUrls, index);
				}
			}

			//再发布版面的附件
			if (needPubLayout) {
				for (Attachment att : layout.getAttachments()) {
					pubAttOne(att, pageDirs, pageUrls, index);
				}
			}
		}
		return success;
	}

	//第二步，发布所有版面下的稿件
	private int pub2Articles(int index, String[] pageUrls, Template templateArticle) {
		//以稿件路径为当前路径，计算版面、图片、附件的相对路径
		String[] relativeUrls = relativePageUrlsByArticle(pageUrls);

		//填写版面url、稿件Url、附件Url、版面图Url、Pdf Url
		setUrls(index, relativeUrls);

		int success = PubArticle.SUCCESS;

		for (PaperLayout layout : context.getLayouts()) {
			//发布一个版面的所有稿件
			List<PaperArticle> articles = layout.getArticles();
			for (PaperArticle article : articles) {
				if(!StringUtils.isBlank(article.getContent()) && !article.getContent().contains("<!--enpcontent-->")) {
						String summary = article.getContent();
						if (article.getContent().length()>=120)
							summary = summary.substring(0, 119);
						article.setSummary(summary);
				}
                String content = article.getContent();
                insertNotes(index, article);
				success = pubArticle(article, templateArticle, index);
                article.setContent(content);
				if (success != PubArticle.SUCCESS)
					return success;
			}
		}
		return success;
	}

    private void insertNotes(int index, PaperArticle article) {
        String picUrl = article.getPicBig();
        if (StringUtils.isBlank(picUrl)) picUrl = article.getPicMiddle();
        if (StringUtils.isBlank(picUrl)) picUrl = article.getPicSmall();
        String pubTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(article.getPubTime());
        String notes = "<!--enpcontent-->" +
                article.getContent() +
                "<!--/enpcontent-->" +
                "<!--enpproperty <date>" +
                pubTime + "</date><author>" +
                article.getAuthor() + "</author><subtitle>" +
                article.getSubTitle() + "</subtitle><introtitle>" +
                article.getIntroTitle() + "</introtitle><keyword>" +
                article.getKeyword() + "</keyword><id>" +
                article.getId() + "</id><nodeid>" +
                article.getColumnID() + "</nodeid><nodename>" +
                context.getPaper().getName() + "</nodename><url>" +
                article.getUrlAbsolute() + "</url><siteid>" +
                article.getSiteID() + "</siteid><channel>" +
                (index + 3) + "</channel><title>" +
                article.getTitle() + "</title>/enpproperty-->";

        article.setContent(notes);
    }

	//第三步，发布所有版面
	private int pub3Layouts(int index, String[] pageUrls, Template template) {
		int success = PubArticle.SUCCESS;
		if (template == null) return success;
		
		//以版面路径为当前路径，计算稿件、图片、附件的相对路径
		String[] relativeUrls = relativePageUrlsByLayout(pageUrls);

		//填写版面url、稿件Url、附件Url、版面图Url、Pdf Url
		setUrls(index, relativeUrls);
		
		for (PaperLayout layout : context.getLayouts()) {
			if(scrollFlag&&layout.getId() != context.getMessage().getDocID()&&context.getMessage().getRelIDs()==null)
				continue;
			else success = pubLayout(layout, template, index);
			if (success != PubArticle.SUCCESS) return success;
		}
		return success;
	}

	//发布首页
	private int pub4Home(int index, String[] pageDirs, String[] pageUrls) {
		int success = PubArticle.SUCCESS;
		
		//单版发布，不处理
		if (context.getMessage().getRelIDs() == null) return success;
		
		//无首页模板，不处理
		Template template = (index == 0) ? context.getTemplateHome() : context.getTemplateHomePad();
		if (template == null) return success;
		
		String layoutDir = pageDirs[5];
		//1. 这时版面已经发布了，首页index.html放在版面同目录下，相对路径不必再改
		String fileName = "index." + getSuffix(template);
		String filePath = layoutDir + fileName;
		success = pubHome(context.getLayouts(), template, filePath);
		if (success != PubArticle.SUCCESS) return success;
		
		//若不是最新的一期刊期，则不必做下面的步骤
		if (!isNewest()) return success;
		
		//2. 首页还要在版面根目录（无日期部分，如layout/201606/07/）下放一份，重新计算相对路径
		
		//在以版面路径为当前路径，计算稿件、图片、附件的相对路径
		String[] relativeUrls = relativePageUrlsByHome(pageUrls);

		//填写版面url、稿件Url、附件Url、版面图Url、Pdf Url
		setUrls(index, relativeUrls);
		
		//存放目录
		int pos = layoutDir.lastIndexOf("/", layoutDir.lastIndexOf("/") - 1);
		pos = layoutDir.lastIndexOf("/", pos - 1);
		String homeDir = layoutDir.substring(0, pos + 1) + fileName;
		
		pubHome(context.getLayouts(), template, homeDir);
		//第一个版面的版面图放到首页目录下，供第三方调用名称为index.jpg
		pubLayoutPic(context.getLayouts().get(0).getPicPath(), layoutDir.substring(0, pos + 1));

		return success;
	}
	//第一个版面的版面图放到首页目录下，供第三方调用名称为index.jpg
	private void pubLayoutPic(String picPath, String homeDir) {

		//复制文件到外网，生成分发信息文件
		if (InfoHelper.copyFile(picPath, homeDir, "index.jpg")) {
			String path = homeDir + "index.jpg";
			//trans：生成分发信息文件
			String root = context.getPageDir("root");
			PublishHelper.writeTransPath(path, root);
		}
	}

	//把刊期添加到period.xml文件
	private void pub5PeriodFile(int index, String root) {
		//若是单版发布，不处理
		if (context.getMessage().getRelIDs() == null) return;
		
		List<PaperLayout> layouts = context.getLayouts();
		if (layouts.size() == 0) return;
		
		PaperLayout layout = layouts.get(0);
		
		//得到存放路径，不包括日一级路径
		int pos = layout.getDir().lastIndexOf("/", layout.getDir().lastIndexOf("/") - 1);
		String tempPath = layout.getDir().substring(0, pos);
		String savePath = tempPath + "/period.xml";
		
		PeriodFileDealer periodFileDealer = new PeriodFileDealer();
		try {
			String content = periodFileDealer.createXml(savePath, layout, index);
			if (content != null) {
				pushFile(content, savePath);
				log.info("发布：" + savePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//第六布，发布报纸信息
	private void updatePapersInfo(int index) {
		//若是单版发布，不处理
		if (context.getMessage().getRelIDs() == null) return;
		List<PaperLayout> layouts = context.getLayouts();
		if (layouts.size() == 0) return;
		for(PaperLayout layout : layouts) {
			if (index == 0)
				layout.setUrl(layout.getUrlAbsolute());
			else
				layout.setUrlPad(layout.getUrlAbsolute());
		}
		PeriodFileDealer periodFileDealer = new PeriodFileDealer();
		String jsfilePath =context.getPageDir("root") + File.separator+"papersInfo"+File.separator+DateUtils.format(layouts.get(0).getDate(),"yyyyMMdd")+".js";
		String filePath =context.getPageDir("root") + File.separator+"papersInfo"+File.separator+DateUtils.format(layouts.get(0).getDate(),"yyyyMMdd")+".xml";
		try {
			String content = periodFileDealer.updatePapersInfo(filePath,layouts);
			if (content != null) {
				pushFile(content, filePath);
				pushFile("paperinfo(\""+content.replaceAll("\n","").replaceAll("\"","\\\\\"")+"\")", jsfilePath);
				log.info("发布：" + filePath);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发布之前填写绝对路径和绝对Url，稿件发布、版面发布后按绝对Url回写数据库
	 */
	private void setAbsoluteUrls(int index, String[] pageDirs, String[] pageUrls, 
			Template template, Template templateArticle) {
		
		String suffix = getSuffix(template);
		String suffixArticle = getSuffix(templateArticle);
		
		for (PaperLayout layout : context.getLayouts()) {
			//版面的发布路径、绝对Url
			String fileName = lprefix + layout.getLayout() + "." + suffix;
			layout.setDir(pageDirs[5] + fileName);
			layout.setUrlAbsolute(pageUrls[4] + fileName);
			
			//稿件的发布路径、绝对Url
			for (PaperArticle article : layout.getArticles()) {
				fileName = cprefix + article.getId() + "." + suffixArticle;
				article.setDir(pageDirs[0] + fileName);
				article.setUrlAbsolute(pageUrls[0] + fileName);
			}
		}
	}

	private String[] relativePageUrlsByArticle(String[] pageUrls) {
		String[] relativeUrls = new String[pageUrls.length];
		for (int i = 0; i < relativeUrls.length; i++) {
			relativeUrls[i] = pageUrls[i];
		}
		//按稿件Url对其它做相对路径
		String rel = relativeUrl(pageUrls[0], pageUrls[4]); //版面
		if (rel != null) relativeUrls[4] = rel;
		
		rel = relativeUrl(pageUrls[0], pageUrls[1]); //图片
		if (rel != null) relativeUrls[1] = rel;
		
		rel = relativeUrl(pageUrls[0], pageUrls[2]); //附件
		if (rel != null) relativeUrls[2] = rel;
	
		relativeUrls[0] = ""; //同刊期的所有稿件都在同一个目录，所以相对路径是“”
		
		return relativeUrls;
	}

	private String[] relativePageUrlsByLayout(String[] pageUrls) {
		String[] relativeUrls = new String[pageUrls.length];
		for (int i = 0; i < relativeUrls.length; i++) {
			relativeUrls[i] = pageUrls[i];
		}
		String rel = relativeUrl(pageUrls[4], pageUrls[0]); //稿件
		if (rel != null) relativeUrls[0] = rel;
		
		rel = relativeUrl(pageUrls[4], pageUrls[1]); //图片
		if (rel != null) relativeUrls[1] = rel;
		
		rel = relativeUrl(pageUrls[4], pageUrls[2]); //附件
		if (rel != null) relativeUrls[2] = rel;
	
		relativeUrls[4] = ""; //同刊期的所有版面都在同一个目录，所以相对路径是“”
		
		return relativeUrls;
	}

	//按首页做相对位置，首页放在layout根目录下（去掉了年月/日目录）
	private String[] relativePageUrlsByHome(String[] pageUrls) {
		String[] relativeUrls = new String[pageUrls.length];
		
		int pos = pageUrls[4].lastIndexOf("/", pageUrls[4].lastIndexOf("/") - 1);
		pos = pageUrls[4].lastIndexOf("/", pos - 1);
		String homeUrl = pageUrls[4].substring(0, pos);

		String rel = relativeUrl(homeUrl, pageUrls[0]); //稿件
		if (rel != null) relativeUrls[0] = rel;
		
		rel = relativeUrl(homeUrl, pageUrls[1]); //图片
		if (rel != null) relativeUrls[1] = rel;
		
		rel = relativeUrl(homeUrl, pageUrls[2]); //附件
		if (rel != null) relativeUrls[2] = rel;
	
		rel = relativeUrl(homeUrl, pageUrls[4]); //版面
		if (rel != null) relativeUrls[4] = rel;
		
		return relativeUrls;
	}

	//按稿件Url来设置版面的相对Url
	private String relativeUrl(String srcUrl, String destUrl) {
		if (srcUrl == null || destUrl == null) return null;
		
		//先比较协议头http，若不同，则需完整路径
		String prefix0 = srcUrl.substring(0, srcUrl.indexOf(':'));
		String prefix1 = destUrl.substring(0, destUrl.indexOf(':'));
		if (!prefix0.equals(prefix1)) return null;
	
		//去掉协议头
		srcUrl = srcUrl.substring(srcUrl.indexOf("://") + 3);
		destUrl = destUrl.substring(destUrl.indexOf("://") + 3);
		
		//去掉最后的/
		if (srcUrl.endsWith("/")) srcUrl = srcUrl.substring(0, srcUrl.length() - 1);
		if (destUrl.endsWith("/")) destUrl = destUrl.substring(0, destUrl.length() - 1);
		
		//按/拆分成多段，以便每段比较
		String[] srcUrls = srcUrl.split("/");
		String[] destUrls = destUrl.split("/");
		
		//每段比较，找出路径开始不同的位置
		int diff = -1;
		for (int i = 0; i < destUrls.length; i++) {
			//目标路径是源路径的下级路径，或开始分岔路
			if (i >= srcUrls.length || !srcUrls[i].equals(destUrls[i])) {
				diff = i;
				break;
			}
		}
		//源路径是目标路径的下级路径
		if (diff < 0) diff = destUrls.length;
		
		//拼出相对路径
		String relativePath = "";
		for (int i = diff; i < srcUrls.length; i++) {
			relativePath += "../";
		}
		for (int i = diff; i < destUrls.length; i++) {
			relativePath += destUrls[i] + "/";
		}
		if (relativePath.length() == 0) relativePath = null;
		
		return relativePath;
	}

	/**
	 * 相对路径Url填充：
	 * 1）版面url：以便模板中有“上一版”、“下一版”时正确引用url
	 * 2）版面图Url、Pdf Url：附件发布后有Url，从中取
	 * 3）稿件url：以便模板中有“上一篇”、“下一篇”时正确引用url
	 */
	private void setUrls(int index, String[] pageUrls) {
		//每个版修改版面Url、稿件Url、版面图Url、Pdf Url
		for (PaperLayout layout : context.getLayouts()) {
			setUrlsOneLayout(layout, index, pageUrls);
			
			setLayoutMappings(layout, index);
			
			//若是单版发布，则需要再给版面的siblings设置url
			if (context.getMessage().getRelIDs() == null) {
				for (PaperLayout layout0 : layout.getSiblings()) {
					//if (layout0.getId() != context.getMessage().getDocID())
					setUrlsOneLayout(layout0, index, pageUrls);
				}
			}
		}
	}

	//填充一个版的所有需要的Url，是相对Url
	private void setUrlsOneLayout(PaperLayout layout, int index, String[] pageUrls) {
		//设置版面的相对Url
		String fileName = _fileName(layout, index);
		if (index == 0) {
			layout.setUrl(pageUrls[4] + fileName);
		} else {
			layout.setUrlPad(pageUrls[4] + fileName);
		}
		
		//设置附件的相对Url
		setUrlsAttachments(layout.getAttachments(), index, pageUrls);
		
		//设置版面图url、pdf Url
		setUrlsPicPdf(layout, index, pageUrls);
		
		//计算稿件的相对url
		if (layout.getArticles() == null) return;
		for (PaperArticle article : layout.getArticles()) {
			fileName = _fileName(article, index);
			if (index == 0) {
				article.setUrl(pageUrls[0] + fileName);
			} else {
				article.setUrlPad(pageUrls[0] + fileName);
			}
			//稿件的附件的相对Url
			setUrlsAttachments(article.getAttachments(), index, pageUrls);
		}
	}
	//设置附件的相对Url
	private void setUrlsAttachments(List<Attachment> atts, int index, String[] pageUrls) {
		if (atts == null) return;
		
		for (Attachment att : atts) {
			if (att.getType() == Article.ATTACH_PIC || att.getType() == Article.ATTACH_LAYOUT_PIC) {
				String url = pageUrls[1] + att.getFileName();
				if (index == 0)
					att.setUrl(url + ".1");
				else
					att.setUrlPad(url + ".1");
			} else if (att.getType() == Article.ATTACH_LAYOUT_PDF) {
				String url = pageUrls[2] + att.getFileName();
				if (index == 0)
					att.setUrl(url);
				else
					att.setUrlPad(url);
			}
		}
	}
	
	private void setUrlsPicPdf(PaperLayout layout, int index, String[] pageUrls) {
		if (layout.getAttachments() == null) return;
		
		for (Attachment att : layout.getAttachments()) {
			String url = index == 0 ? att.getUrl() : att.getUrlPad();
			if (att.getType() == Article.ATTACH_LAYOUT_PIC) {
				layout.setPicUrl(url); //版面图
			} else if (att.getType() == Article.ATTACH_LAYOUT_PDF) {
				layout.setPdfUrl(url); //Pdf
			}
		}
	}

	//取版面的发布文件名
	private String _fileName(PaperLayout layout, int index) {
		String url = layout.getDir();
		if (url == null) //单版发布时，siblings里getDir是空的
			url = (index == 0) ? layout.getUrl() : layout.getUrlPad();
		return url.substring(url.lastIndexOf("/") + 1);
	}

	//取稿件的发布文件名
	private String _fileName(PaperArticle article, int index) {
		String url = article.getDir();
		if (url == null) //单版发布时，siblings里getDir是空的
			url = (index == 0) ? article.getUrl() : article.getUrlPad();
		return url.substring(url.lastIndexOf("/") + 1);
	}

	//生成版面map信息
	private void setLayoutMappings(PaperLayout layout, int index) {
		if (index == 0) {
			setMapping(layout,index);
		} else {
			setUrlMapping(layout,index);
		}
	}

	/**
	 * 生成版面map信息
	 * 387*595 ["66.387303%,84.493671%","97.757127%,84.493671%","97.757127%,68.196203%","66.387303%,68.196203%"]
	 * <Area coords="199,14,390,14,390,98,199,98" shape="polygon" href="content_138701.htm">
	 */
	private void setMapping(PaperLayout layout, int index){
		JSONObject mappingObject = null;
		JSONArray articleMap = null;
		StringBuffer articleMapStr = null;
		
		JSONArray mappingJsonArray = JsonHelper.getJsonArray(layout.getMappingOriginal());
		if (mappingJsonArray == null) return;

		String layoutSizeStr = InfoHelper.getConfig( "数字报", "版面图尺寸") == null ? "350*500" :InfoHelper.getConfig( "数字报", "版面图尺寸");
		String [] temp = layoutSizeStr.split("\\*");
		int layoutWeight= Integer.valueOf(temp[0]);
		int layoutHeigh = Integer.valueOf(temp[1]);

		Map<Long,String> mappingMap = new HashMap<Long,String>();
		for (int i = 0; i < mappingJsonArray.size(); i++) {
			mappingObject = (JSONObject) mappingJsonArray.get(i);
			articleMap = mappingObject.getJSONArray("mapping");
			
			articleMapStr = new StringBuffer();
			for (int j = 0; j < articleMap.size(); j++) {
				String point = (String) articleMap.get(j);
				point = StringUtils.replace(point, "%", "");
				String[] points = point.split(",");
				if (j > 0) articleMapStr.append(",");
				articleMapStr.append(Double.parseDouble(points[0]) * layoutWeight / 100);
				articleMapStr.append(",");
				articleMapStr.append(Double.parseDouble(points[1]) * layoutHeigh / 100);
			}
			mappingMap.put(mappingObject.getLong("articleID"), articleMapStr.toString());
		}
		
		StringBuffer mappingStr = new StringBuffer("<MAP NAME=\"PagePicMap\">");
		for(PaperArticle article : layout.getArticles()){
			mappingStr.append("<Area coords=\"").append(mappingMap.get(article.getId()));
			mappingStr.append("\" shape=\"polygon\" href=\"").append(article.getUrl());
			mappingStr.append("\">");
		}
		mappingStr.append("</MAP>");
		
		layout.setMapping(mappingStr.toString());
	}



	//触屏版面map信息，给每个Mapping添加href
	private void setUrlMapping(PaperLayout layout, int index) {
		JSONArray mappingJsonArray = JsonHelper.getJsonArray(layout.getMappingOriginal());
		if (mappingJsonArray == null) return;
		if(scrollFlag){
			StringBuffer mappings = new StringBuffer("{\"a\":\"[");
			List<String> articleUrl = new ArrayList<>();
			for (Object obj : mappingJsonArray) {
				JSONObject mappingObject = (JSONObject) obj;
				long id = mappingObject.getLong("articleID");

				    mappings.append(mappingObject.getString("mapping").replaceAll("\"","").replaceAll("%",""));
				mappings.append(',');
				List<PaperArticle> articles = layout.getArticles();
				for (PaperArticle article : articles) {
					if (id == article.getId()) {
						articleUrl.add(article.getUrlPad());
						break;
					}
				}
			}
			mappings.append("]\",\"l\":\""+articleUrl.toString()+"\"}");
			layout.setMapping(mappings.toString());
		}
		else {
			for (Object obj : mappingJsonArray) {
				JSONObject mappingObject = (JSONObject) obj;
				long id = mappingObject.getLong("articleID");

				List<PaperArticle> articles = layout.getArticles();
				for (PaperArticle article : articles) {
					if (id == article.getId()) {
						mappingObject.put("href", article.getUrlPad());
						break;
					}
				}
			}

		layout.setMapping(mappingJsonArray.toString());
		}
	}

	//发布报纸稿件
	private int pubArticle(PaperArticle article, Template template, int index) {
		String field = index == 0 ? "a_url" : "a_urlPad";
		try {
			String templateContent = getTemplateContent(template);
			String pageContent = mergeContent(article, templateContent);

			int result = pushFile(pageContent, article.getDir());
			
			changeUrl(article.getDocLibID(), article.getId(), article.getUrlAbsolute(), field);
			
			log.info("发布：" + article.getDir());
			
			return result;
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}
	
	//版面发布过程
	private int pubLayout(PaperLayout layout, Template template, int index) {
		String field = index == 0 ? "pl_url" : "pl_urlPad";
		try {
			String templateContent = getTemplateContent(template);
			String pageContent = mergeContent(layout, templateContent);
			
			int result = pushFile(pageContent, layout.getDir());
			
			changeUrl(layout.getDocLibID(), layout.getId(), layout.getUrlAbsolute(), field);

			log.info("发布：" + layout.getDir());
			
			return result;
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}

	//版面首页发布过程
	private int pubHome(List<PaperLayout> layouts, Template template, String filePath) {
		try {
			String templateContent = getTemplateContent(template);
			String pageContent = mergeContent(layouts, templateContent);
			
			int result = pushFile(pageContent, filePath);
			
			log.info("发布：" + filePath);
			
			return result;
		} catch (Exception e) {
			log.error("发布失败（首页）：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}

	//发布一个附件
	private void pubAttOne(Attachment att, String[] pageDirs, String[] pageUrls, int index) {
		//若附件是外网图片，不需要发布
		if (att.getPath().toLowerCase().startsWith("http")) return;
		
		String url = null;
		if (att.getType() == Article.ATTACH_PIC || att.getType() == Article.ATTACH_LAYOUT_PIC) {
			 //复制图片、大中小图，生成分发信息文件
			copyPicFile(att.getPath(), pageDirs, att.getFileName());
			url = pageUrls[1] + att.getFileName();
		} else {
			//复制文件到外网，生成分发信息文件
			if (InfoHelper.copyFile(att.getPath(), pageDirs[2], att.getFileName())) {
				String path = pageDirs[2] + att.getFileName();
				PublishHelper.writeTransPath(path, pageDirs[4]);
			}
			url = pageUrls[2] + att.getFileName();
		}
		
		//修改附件的发布地址
		String field = (index == 0) ? "att_url" : "att_urlPad";
		changeUrl(att.getDocLibID(), att.getId(), url, field);
		
		log.info("发布附件：" + url);
		
		//修改对象中的url
		if (index == 0)
			att.setUrl(url);
		else
			att.setUrlPad(url);
	}
	
	/**
	 * 发布时复制图片到外网，同时包括3个额外的文件（.0/.1/.2）
	 */
	private void copyPicFile(String srcPathFile, String[] pageDirs, String destFileName) {
		int pos = srcPathFile.indexOf(";");
		if (pos < 0) return;
		
		String deviceName = srcPathFile.substring(0, pos);
		String savePath = srcPathFile.substring(pos + 1);
	
		String destPath = pageDirs[1];
		if (!destPath.endsWith("/")) destPath += "/";
		
		destPath += (destFileName == null) ? savePath : destFileName;
		
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			//存储设备可能是编码过的，先解码
			deviceName = URLDecoder.decode(deviceName, "UTF-8");
			StorageDevice device = sdManager.getByName(deviceName);
			String devicePath = InfoHelper.getDevicePath(device);
			
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs[4], null);
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs[4], ".0");
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs[4], ".1");
			onePicCopyTrans(devicePath, savePath, destPath, pageDirs[4], ".2");
			
		} catch (E5Exception | IOException e1) {
			e1.printStackTrace();
			System.out.println("【数字报发布】复制图片异常。message:" + context.getMessage().toString() + ",deviceName:" + deviceName);
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
			System.out.println("message:" + context.getMessage().toString());
			System.out.println("devicePath:" + devicePath);
			System.out.println("srcPath:" + savePath);
			System.out.println("destPath:" + destPath);
			System.out.println("transDir:" + transDir);
		}
	}
	//取源图片文件供复制。
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
	
	private String[] getPageDir(int index) {
		String[] result = new String[6];
		if (index == 0) {
			result[0] = context.getPageDir("article");
			result[1] = context.getPageDir("pic");
			result[2] = context.getPageDir("att");
			result[3] = context.getPageDir("picRoot"); //z:\webroot/xy/pic 图片根路径，为抽图服务
			result[4] = context.getPageDir("root"); //站点根目录
			result[5] = context.getPageDir("column");
		} else {
			result[0] = context.getPageDir("articlePad");
			result[1] = context.getPageDir("picPad");
			result[2] = context.getPageDir("attPad");
			result[3] = context.getPageDir("picRootPad");
			result[4] = context.getPageDir("root"); //站点根目录
			result[5] = context.getPageDir("columnPad");
		}
		return result;
	}

	private String[] getPageUrl(int index) {
		String[] result = new String[5];
		if (index == 0) {
			result[0] = context.getPageUrl("article");
			result[1] = context.getPageUrl("pic");
			result[2] = context.getPageUrl("att");
			result[3] = context.getPageUrl("articlePad");
			result[4] = context.getPageUrl("column");
		} else {
			result[0] = context.getPageUrl("articlePad");
			result[1] = context.getPageUrl("picPad");
			result[2] = context.getPageUrl("attPad");
			result[3] = context.getPageUrl("article");
			result[4] = context.getPageUrl("columnPad");
		}
		return result;
	}

	//由模板确定的发布文件的后缀，html/json/xml
	private String getSuffix(Template t) {
		if (t == null || StringUtils.isBlank(t.getFileType()))
			return "html";
		else
			return t.getFileType();
			
	}
	
	//把文件存储到外网
	private int pushFile(String pageContent,String pathName) throws Exception{
		if(StringUtils.isEmpty(pathName)){
			log.error("发布路径为空，发布失败！");
			return PubArticle.ERROR_NO_PUBDIR;
		}
		FileUtils.writeStringToFile(new File(pathName), pageContent, "UTF-8");
		
		//trans：生成分发信息文件
		String root = context.getPageDir("root");
		PublishHelper.writeTransPath(pathName, root);
	
		return PubArticle.SUCCESS;
	}

	//发布后修改url
	private void changeUrl(int docLibID, long docID, String url, String field) {
		if (url == null) return;
		
		Object[] params = null;
		String sql = null;
		
		try {
			String table = LibHelper.getLibTable(docLibID);
			sql = "update " + table + " set " + field + "=? where SYS_DOCUMENTID=?";
			params = new Object[]{url, docID};
			
			InfoHelper.executeUpdate(docLibID, sql, params);
		} catch (E5Exception e) {
			log.error("changeUrl error:" + sql, e);
			log.info("url=" + url);
			log.info("docID=" + docID);
		}
	}

	private String mergeContent(PaperArticle article, String templateContent) throws Exception {
		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(article, componentMatcher.group(1));
			cr = cr.replace("$", "\\$"); //注意$符号
			
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		return pageContent.toString();
	}

	private String mergeContent(PaperLayout layout, String templateContent) throws Exception {
		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(layout, componentMatcher.group(1));
			cr = cr.replace("$", "\\$"); //注意$符号
			
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		return pageContent.toString();
	}

	private String mergeContent(List<PaperLayout> layouts, String templateContent) throws Exception {
		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(layouts, componentMatcher.group(1));
			cr = cr.replace("$", "\\$"); //注意$符号
			
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		return pageContent.toString();
	}

	private String getComponentResult(PaperLayout layout, String coID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, coID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			if(scrollFlag)
				component.setData("layouts",context.getLayouts());
			component.setData("layout", layout);
			component.setData("piles", context.getPiles()); //把叠的信息加到版面模板里
			return component.getComponentResult();
		}
	}
	
	private String getComponentResult(PaperArticle article, String coID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, coID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			component.setData("article", article);
			return component.getComponentResult();
		}
	}

	//版面按首页模板合成
	private String getComponentResult(List<PaperLayout> layouts, String coID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, coID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			component.setData("layouts", layouts);
			return component.getComponentResult();
		}
	}
	
	//发布后，修改版面的发布状态
	private void changeStatus() {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		List<PaperLayout> layouts = context.getLayouts();
		for (PaperLayout layout : layouts) {
			try {
				Document doc = docManager.get(layout.getDocLibID(), layout.getId());
				
				doc.set("pl_status", Article.STATUS_PUB_DONE);
				
				docManager.save(doc);
				for(PaperArticle  paperArticle: layout.getArticles()){
					//稿件状态也修改
					doc = docManager.get(paperArticle.getDocLibID(), paperArticle.getId());
					doc.set("a_status", Article.STATUS_PUB_DONE);
					docManager.save(doc);
				}
			} catch (E5Exception e) {
				log.error(e);
			}
		}
		
		if (context.getMessage().getRelIDs() != null) {
			int dateLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERDATE.typeID(),
					context.getMessage().getDocLibID());
			try {
				Document[] dates = docManager.find(dateLibID, "pd_paperID=? and pd_date=?", 
						new Object[]{context.getMessage().getDocID(),
						DateUtils.parse(context.getMessage().getRelIDs(), "yyyyMMdd")
						});
				if (dates.length > 0) {
					dates[0].set("pd_status", Article.STATUS_PUB_DONE);
					docManager.save(dates[0]);
				}
			} catch (E5Exception e) {
				log.error(e);
			}
		}
	}
	
	//判断是否最新一期，若是则需要发布首页
	private boolean isNewest() {
		DBSession conn = null;
		IResultSet rs = null;
		try {
			String tCode = LibHelper.getTenantCodeByLib(context.getMessage().getDocLibID());
			DocLib dateLib = LibHelper.getLib(DocTypes.PAPERDATE.typeID(), tCode);
			String sql = "select max(pd_date) from " + dateLib.getDocLibTable() + " where pd_paperID=?";
			
			conn = Context.getDBSession(dateLib.getDsID());
			rs = conn.executeQuery(sql, new Object[]{context.getMessage().getDocID()});
			if (rs.next()) {
				String newest = DateUtils.format(rs.getDate(1), "yyyyMMdd");
				return newest.equals(context.getMessage().getRelIDs());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		return false;
	}
}
