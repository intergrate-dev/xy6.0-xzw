package com.founder.xy.jpublish.wx;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.system.site.DomainDir;
import com.founder.xy.template.Template;
import com.founder.xy.wx.WeixinManager;
import com.founder.xy.wx.data.Account;
import com.founder.xy.wx.data.Menu;

/**
 * 微信菜单稿件发布的上下文环境
 * @author Gong Lijie
 */
public class WXContext {
	private DocIDMsg message;
	private Menu menu;
	private Account account;
	private String siteRoot;
	private Template template;
	private List<BareArticle> articles = new ArrayList<>();
	
	public void init(DocIDMsg message){
		this.message = message;
		
		WeixinManager wxManager = (WeixinManager)Context.getBean("weixinManager");
		menu = wxManager.getMenu(message.getDocLibID(), message.getDocID());
		
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXACCOUNT.typeID(), message.getDocLibID());
		account = wxManager.getAccount(accountLibID, menu.getAccountID());
		
		setTemplate();
		
		readArticles();
		
		setPageDir();
	}
	public DocIDMsg getMessage(){
		return message;
	}
	
	public List<BareArticle> getArticles() {
		return articles;
	}
	
	/**
	 * 分发信息文件位置
	 */
	public String getSiteRoot() {
		return siteRoot;
	}
	
	public Menu getMenu() {
		return menu;
	}
	public Account getAccount() {
		return account;
	}
	public Template getTemplate() {
		return template;
	}
	
	//读稿件列表
	private void readArticles() {
		try {
			int baLibID = LibHelper.getLibIDByOtherLib(DocTypes.WXARTICLE.typeID(), message.getDocLibID());
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = docManager.find(baLibID,
					"wx_menuID=? order by wx_order desc",
					new Object[]{message.getDocID()});
			
			for (int i = 0; i < docs.length; i++) {
				articles.add(newArticle(docs[i]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private BareArticle newArticle(Document doc) {
		BareArticle article = new BareArticle();
		
		article.setDocLibID(doc.getDocLibID());
		article.setId(doc.getDocID());
		
		article.setTitle(doc.getTopic());
		article.setSubTitle(doc.getString("wx_subTitle"));
		article.setSummary(doc.getString("wx_abstract"));
		article.setUrl(doc.getString("wx_url"));
		
		article.setPicBig(doc.getString("wx_pic"));
		
		Timestamp time = doc.getTimestamp("wx_pubTime");
		if (time != null)
			article.setPubTime(new Date(time.getTime()));
		
		return article;
	}
	private void setTemplate() {
		int templateLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), message.getDocLibID());
		
		BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		
		template = templateCache.getTemplateByID(templateLibID, account.getTemplateID());
	}
	
	//设置发布地址
	private void setPageDir(){
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), message.getDocLibID());
		int dirLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), message.getDocLibID());
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		siteRoot = siteCache.getSiteWebRootByID(siteLibID, account.getSiteID());
		
		DomainDir dir = siteCache.getDir(dirLibID, account.getDirID());
		String path = siteRoot + dir.getPath();
		
		menu.setDir(path); //发布的目录
		
		path += "/" + menu.getId() + ".html";
		
		menu.setPath(path);//发布路径
	}
}
