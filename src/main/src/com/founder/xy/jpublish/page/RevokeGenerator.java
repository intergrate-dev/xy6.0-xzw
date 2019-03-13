package com.founder.xy.jpublish.page;

import java.io.File;
import java.io.IOException;

import com.founder.xy.commons.InfoHelper;
import org.apache.commons.io.FileUtils;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.system.site.SiteRule;

/**
 * 撤稿处理器
 * @author Gong Lijie
 */
public class RevokeGenerator {
	//有发布页的稿件类型：文/图/视频/活动
	private int[] TYPES_WITH_FILE = {Article.TYPE_ARTICLE, Article.TYPE_PIC, 
			Article.TYPE_VIDEO, Article.TYPE_SPECIAL, Article.TYPE_ACTIVITY,
			Article.TYPE_PANORAMA, Article.TYPE_FILE,
			};
	private int siteID;
	protected Log log = Context.getLog("xy.publish");
	
	public int generator(ArticleMsg data){
		//若该稿件是不生成稿件页的，则直接退出
		if (!ArrayUtils.contains(TYPES_WITH_FILE, data.getType())) return PubArticle.SUCCESS;
		
		Document article = getArticle(data);
		if (article == null) return PubArticle.SUCCESS;
		
		//若稿件没有发布url，则不必撤稿
		String url0 = article.getString("a_url");
		String url1 = article.getString("a_urlPad");
		
		if (StringUtils.isBlank(url0) && StringUtils.isBlank(url1) )
			return PubArticle.SUCCESS;
		
		//读附件
		Document[] atts = getAttPics(data);
		
		//读栏目
		Column column = readColumn(data);
		
		//站点根目录
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), data.getDocLibID());
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		String siteWebRoot = siteCache.getSiteWebRootByID(libID, column.getSiteID());
		siteID = column.getSiteID();
		//日期目录
		String format = InfoHelper.getConfig( "发布服务", "稿件日期目录格式")==null?"yyyyMM/dd":InfoHelper.getConfig( "发布服务", "稿件日期目录格式");
		String datePath = DateUtils.format(article.getTimestamp("a_pubTime"), format);
		
		if (!StringUtils.isBlank(url0)) {
			revokeFile(siteWebRoot, datePath, column.getPubRule(), data, atts, url0);
		}
		
		if (!StringUtils.isBlank(url1)) {
			revokeFile(siteWebRoot, datePath, column.getPubRulePad(), data, atts, url1);
		}
		
		return PubArticle.SUCCESS;
	}
	
	//撤销一个渠道
	private void revokeFile(String siteWebRoot, String datePath, long ruleID, ArticleMsg message, Document[] atts, String url){
		//发布规则
		SiteRule pubRule = getPubRule(ruleID, message);
		if (pubRule == null) return;
		
		//得到稿件页的发布位置，包括目录和文件名
		String filePath = getArticleDir(siteWebRoot, pubRule, url);
		
		//用空字符串写文件，生成trans分发信息
		overwrite(filePath, siteWebRoot);
		int pos = filePath.lastIndexOf(".");
		//多页稿件删除全部页面，否则会影响检索结果
		String fullContentPath = filePath.substring(0, pos) + "_0" + filePath.substring(pos);
		File fullContentPage = new File(fullContentPath);
		if(fullContentPage.exists()) {
			overwrite(fullContentPath, siteWebRoot);
			for(int nextpage = 2;;nextpage++){
				String nextPagePath = filePath.substring(0, pos) + "_"+ nextpage + filePath.substring(pos);
				File nextPage = new File(nextPagePath);
				if (nextPage.exists()){
					overwrite(nextPagePath, siteWebRoot);
				}
				else break;
			}




		}
		//撤销附件图片
		revokePics(siteWebRoot, datePath, pubRule, message, atts);
	}
	
	private void revokePics(String siteWebRoot, String datePath, SiteRule pubRule, ArticleMsg message, Document[] atts) {
		String picDir = getPicDir(siteWebRoot, datePath, pubRule, message);
		for (Document att : atts) {
			if (att.getInt("att_type") != Article.ATTACH_VIDEO) {
				String path = att.getString("att_path");
				//若附件是外网图片，不需要
				if (path.toLowerCase().startsWith("http")) continue;
				
				if (!StringUtils.isBlank(path)) {
					int pos = path.lastIndexOf("/");
					if (pos >= 0) {
						String picName = path.substring(pos + 1);
						String picPath = picDir + picName;
						overwrite(picPath, siteWebRoot);
						overwrite(picPath + ".0", siteWebRoot);
						overwrite(picPath + ".1", siteWebRoot);
						overwrite(picPath + ".2", siteWebRoot);
					}
				}
			}
		}
	}
	
	private void overwrite(String filePath, String siteWebRoot) {
		//用空字符串写文件
		if (rewriteFile(filePath,siteWebRoot)) {
			//trans分发信息
			PublishHelper.writeTransPath(filePath, siteWebRoot);
			
			log.info("撤回" + filePath);
		}
	}
	
	//读稿件
	private Document getArticle(ArticleMsg data) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document article = null;
		try {
			article = docManager.get(data.getDocLibID(), data.getId());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return article;
	}
	//读稿件的附件图片
	private Document[] getAttPics(ArticleMsg data) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] result = null;
		try {
			int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), data.getDocLibID());
			result = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?", 
					new Object[]{data.getId(), data.getDocLibID()});
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//读出主栏目
	private Column readColumn(ArticleMsg message) {
		try {
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), message.getDocLibID());
			
			ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
			return columnReader.get(colLibID, message.getColID());
		} catch (E5Exception e) {
			log.error("读栏目信息时出错：" + e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	//得到稿件页的发布位置，包括目录和文件名
	private String getArticleDir(String siteWebRoot, SiteRule pubRule, String url){
		/*
		 * url是：http://172.19.33.95/content/201511/17/c1209.shtml
		 * 发布规则的发布路径是：http://172.19.33.95/content
		 * 截掉前缀后，得到相对路径和文件名
		 */
		String relPath = url.substring(pubRule.getArticleDir().length());
		
		StringBuffer dir = new StringBuffer();
		dir.append(siteWebRoot)
			.append(pubRule.getArticlePath())
			.append(relPath);
		
		return dir.toString();
	}
	//得到附件图片的发布目录
	private String getPicDir(String siteWebRoot, String datePath, SiteRule pubRule, ArticleMsg message){
		StringBuffer dir = new StringBuffer();
		
		dir.append(siteWebRoot).append(pubRule.getPhotoPath())
			.append("/").append(datePath).append("/");
		
		return dir.toString();
	}
	
	//得到发布规则对象
	private SiteRule getPubRule(long ruleID, ArticleMsg message){
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), message.getDocLibID());
		
		BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		SiteRule pubRule = siteRuleCache.getSiteRuleByID(libID, ruleID);
		
		return pubRule;
	}
	
	//用空字符串写文件
	private boolean rewriteFile(String filePath, String siteWebRoot) {
		try {
			File reFile = new File(siteWebRoot + File.separator + "deleted" + siteID + ".html");
			if(!reFile.exists())
				reFile = new File(siteWebRoot + File.separator + "deleted.html");
			//只替换html页面为deleted.html	
			if(reFile.exists() && filePath.substring(filePath.lastIndexOf(".")).contains("htm")){
				FileUtils.copyFile(reFile,new File(filePath));
			} else {
				FileUtils.writeStringToFile(new File(filePath), " ", false);
			}
			return true;
		} catch (IOException e) {
			System.out.println("撤稿覆盖空白文件时错误:" + filePath + "." + e.getLocalizedMessage());
		}
		return false;
	}
}
