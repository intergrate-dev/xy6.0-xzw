package com.founder.xy.jpublish.magazine;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
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
 * 期刊发布生成器
 * 
 * @author Gong Lijie
 */
public class MagazineGenerator extends AbstractGenerator{
	private MagazineContext context;

	public boolean generator(DocIDMsg data){
		if (log.isDebugEnabled()) log.debug("---期刊发布 " + data.getDocLibID()
				+ "," + data.getDocID() + "," + data.getRelIDs());
		
		context = new MagazineContext();
		context.init(data, log);
		
		if (context.templateLack()) {
			log.error("先配置模板才可发布");
			return false;
		}
		
		boolean result = pubChannel(0);
		if (result) {
			result = pubChannel(1);
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
		
		//对每个稿件先计算url，以便模板中有“上一篇”、“下一篇”时正确引用url
		List<MagazineColumn> columns = context.getColumns();
		for (MagazineColumn column : columns) {
			setArticleUrls(column.getArticles(), pageDirs[0], pageUrls[0], templateArticle, index);
		}
		
		//发布稿件
		int success = 0;
		for (MagazineColumn column : columns) {
			success = pubArticles(column, templateArticle, pageDirs, pageUrls, index);
			if (success != PubArticle.SUCCESS) return false;
		}
		
		//再发布版面
		Template template = (index == 0) ? context.getTemplateLayout() : context.getTemplateLayoutPad();
		if (template != null) {
			success = pubLayout(columns, template, pageDirs, pageUrls, index);
		}
		return true;
	}

	//一个版面发布（包括下面的稿件发布）
	private int pubArticles(MagazineColumn layout, Template templateArticle, 
			String[] pageDirs, String[] pageUrls, int index) {
		int success = PubArticle.SUCCESS;
		
		//发布版面下的稿件
		for (MagazineArticle article : layout.getArticles()) {
			pubAttachments(article.getAttachments(), pageDirs, pageUrls, index);
			
			success = pubArticle(article, templateArticle, index);
			if (success != PubArticle.SUCCESS) break;
		}
		if (success != PubArticle.SUCCESS) {
			log.error("发布中止");
			return success;
		}
		return success;
	}
	
	//发布报纸稿件
	private int pubArticle(MagazineArticle article, Template template, int index) {
		String dir = (index == 0) ? article.getDir() : article.getDirPad();
		String url = (index == 0) ? article.getUrl() : article.getUrlPad();
		String field = index == 0 ? "a_url" : "a_urlPad";
		try {
			String templateContent = getTemplateContent(template);
			String pageContent = mergeContent(article, templateContent);

			int result = pushFile(pageContent, dir);
			
			changeUrl(article.getDocLibID(), article.getId(), url, field);
			
			log.info("发布：" + dir);
			
			return result;
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
	}
	
	//期刊目录页发布过程
	private int pubLayout(List<MagazineColumn> columns, Template template, 
			String[] pageDirs, String[] pageUrls, int index) {
		//发布刊期的附件（封面图、pdf）
		pubAttachments(context.getMagazine().getAttachments(), pageDirs, pageUrls, index);
		
		//期刊目录的发布地址：存储路径、发布url
		String[] dirUrl = getDirUrl(pageDirs[5], pageUrls[4], template);
		
		String field = index == 0 ? "pd_url" : "pd_urlPad";
		try {
			String templateContent = getTemplateContent(template);
			String pageContent = mergeContent(columns, templateContent);
			
			int result = pushFile(pageContent, dirUrl[0]);
			if (result != PubArticle.SUCCESS) return result;
			
			changeUrl(dirUrl[1], field);
			
			log.info("发布：" + dirUrl[0]);
		} catch (Exception e) {
			log.error("发布失败：" + e.getLocalizedMessage(), e);
			
			return PubArticle.ERROR_PUBLISH;
		}
		//最后，添加period.xml文件
		pubPeriodFile(index, pageDirs[5], dirUrl[1]);
		
		return PubArticle.SUCCESS;
	}

	//把附件复制到外网，并记录url
	private void pubAttachments(List<Attachment> atts, String[] pageDirs, String[] pageUrls, int index) {
		if (atts == null || atts.size() == 0) return;
		
		for (Attachment att : atts) {
			pubAttOne(att, pageDirs, pageUrls, index);
		}
	}
	
	//发布一个附件
	private void pubAttOne(Attachment att, String[] pageDirs, String[] pageUrls, int index) {
		//若附件是外网图片，不需要发布
		if (att.getPath().toLowerCase().startsWith("http")) return;
		
		String pageUrl = pageUrls[1];
		if (att.getType() == Article.ATTACH_PIC || att.getType() == Article.ATTACH_LAYOUT_PIC)
			 //复制图片、大中小图，生成分发信息文件
			copyPicFile(att.getPath(), pageDirs, att.getFileName());
		else {
			//复制文件到外网，生成分发信息文件
			if (InfoHelper.copyFile(att.getUrl(), pageDirs[2], att.getFileName())) {
				String path = pageDirs[2] + "/" + att.getFileName();
				PublishHelper.writeTransPath(path, pageDirs[4]);
			}
			pageUrl = pageUrls[2];
		}
		
		String url = pageUrl + "/" + att.getFileName();
		String field = (index == 0) ? "att_url" : "att_urlPad";
		
		//修改附件的发布地址
		changeUrl(att.getDocLibID(), att.getId(), url, field);
		
		//修改对象中的url
		if (index == 0)
			att.setUrl(url);
		else
			att.setUrlPad(url);
	}
	
	//把刊期添加到period.xml文件
	private void pubPeriodFile(int index, String columnDir, String columnUrl) {
		//得到存放路径，放在期刊目录页路径下（期刊目录页不要按日期组织，不然只能看到本月的）
		String savePath = columnDir + "/period.xml";
		
		PeriodMagDealer periodFileDealer = new PeriodMagDealer();
		try {
			String content = periodFileDealer.createXml(savePath, context.getMagazine(), columnUrl, index);
			if (content != null) {
				pushFile(content, savePath);
				log.info("发布：" + savePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			System.out.println("【期刊发布】复制图片异常。message:" + context.getMessage().toString() + ",deviceName:" + deviceName);
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
	
	private String[] getDirUrl(String layoutDir, String layoutUrl, Template template) {
		String[] result = new String[2];
		
		String fileName = "/mag" + context.getMessage().getDocID() 
				+ "." + context.getMessage().getRelIDs()
				+ "." + getSuffix(template);
		
		result[0] = layoutDir + fileName;
		result[1] = layoutUrl + fileName;
		return result;
	}

	//对版面下每个稿件先计算url，以便模板中有“上一篇”、“下一篇”时正确引用url
	private void setArticleUrls(List<MagazineArticle> articles, String pageDir, String pageUrl,
			Template template, int index) {
		String suffix = getSuffix(template);
		
		for (MagazineArticle article : articles) {
			String fileName = "/c" + article.getId() + "." + suffix;
			if (index == 0) {
				article.setDir(pageDir + fileName);
				article.setUrl(pageUrl + fileName);
			} else {
				article.setDirPad(pageDir + fileName);
				article.setUrlPad(pageUrl + fileName);
			}
		}
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
	private void changeUrl(String url, String field) {
		if (url == null) return;
		
		Object[] params = null;
		String sql = null;
		
		try {
			int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINEDATE.typeID(), 
					context.getMessage().getDocLibID());
			String table = LibHelper.getLibTable(docLibID);
			
			sql = "update " + table + " set " + field + "=? where pd_date=? and pd_paperID=?";
			params = new Object[]{url, context.getMagazine().getDate(), context.getMessage().getDocID()};
			
			InfoHelper.executeUpdate(docLibID, sql, params);
		} catch (E5Exception e) {
			log.error("changeUrl error:" + sql, e);
			log.info("url=" + url);
		}
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

	private String mergeContent(MagazineArticle article, String templateContent) throws Exception {
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

	private String mergeContent(List<MagazineColumn> columns, String templateContent) throws Exception {
		StringBuffer pageContent = new StringBuffer();
		
		Matcher componentMatcher = PARSER_PATTERN.matcher(templateContent);
		String cr = null;
		while (componentMatcher.find()) {
			cr = getComponentResult(columns, componentMatcher.group(1));
			cr = cr.replace("$", "\\$"); //注意$符号
			
			componentMatcher.appendReplacement(pageContent, cr);
		}
		componentMatcher.appendTail(pageContent);
		
		return pageContent.toString();
	}

	private String getComponentResult(List<MagazineColumn> columns, String coID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, coID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			component.setData("magazine", context.getMagazine());
			component.setData("columns", columns);
			
			return component.getComponentResult();
		}
	}
	
	private String getComponentResult(MagazineArticle article, String coID) throws Exception{
		String componentObj = RedisManager.hget(RedisKey.CO_KEY, coID);
		if (componentObj == null) {
			return "";
		} else {
			Component component = ComponentFactory.newComponent(param, componentObj);
			component.setData("article", article);
			return component.getComponentResult();
		}
	}

	//发布后，修改版面的发布状态
	private void changeStatus() {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		if (context.getMessage().getRelIDs() != null) {
			int dateLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINEDATE.typeID(),
					context.getMessage().getDocLibID());
			try {
				Document[] dates = docManager.find(dateLibID, "pd_date=? and pd_paperID=?", 
						new Object[]{context.getMagazine().getDate(), context.getMessage().getDocID()});
				if (dates.length > 0) {
					dates[0].set("pd_status", Article.STATUS_PUB_DONE);
					docManager.save(dates[0]);
				}
			} catch (E5Exception e) {
				log.error(e);
			}
		}
	}
}
