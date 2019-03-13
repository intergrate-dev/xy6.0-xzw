package com.founder.xy.system.site;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;

/**
 * 站点检索服务缓存
 * @author Guo Qixun
 */
public class SiteSolrServerCache extends BaseCache{
	private SiteManager siteManager;
	//原稿
	private static Map<Integer,SolrServer> solrOriginalServerMap = new HashMap<Integer,SolrServer>();
	//Web发布库
	private static Map<Integer,SolrServer> solrServerMap = new HashMap<Integer,SolrServer>();
	//App发布库
	private static Map<Integer,SolrServer> solrServerAppMap = new HashMap<Integer,SolrServer>();
	//我的稿件中的Web稿件
	private static Map<Integer,SolrServer> solrArticleServerMap = new HashMap<Integer,SolrServer>();
	//我的稿件中的App稿件
	private static Map<Integer,SolrServer> solrArticleAppServerMap = new HashMap<Integer,SolrServer>();

	public SiteSolrServerCache() {
		
	}

	@Override
	protected int getDocTypeID() {
		return DocTypes.SITE.typeID();
	}
	@Override
	public void refresh(int docLibID) throws E5Exception {
		String solrURL = InfoHelper.getConfig("全文检索","全文检索服务URL地址");
        if(!solrURL.endsWith("/"))
        	solrURL = solrURL + "/";
		List<Site> sites = getSites(docLibID);
		for(Site site : sites){
			int siteID = site.getId();
		
			//这个缓存一般只在系统管理端手工刷新，因此不必按租户区分
			solrOriginalServerMap.put(siteID, new HttpSolrServer(solrURL+"xy"+"_original"));
			solrServerMap.put(siteID, new HttpSolrServer(solrURL+"xy"));
			solrServerAppMap.put(siteID, new HttpSolrServer(solrURL+"xy"+"_app"));
			solrArticleServerMap.put(siteID, new HttpSolrServer(solrURL+"xy"+"_article"));
			solrArticleAppServerMap.put(siteID, new HttpSolrServer(solrURL+"xy"+"_articleapp"));
		}
	}
	
	/**
	 * 取所有的站点
	 */
	private List<Site> getSites(int docLibID) throws E5Exception {
		if (siteManager == null)
			siteManager = new SiteManager();
		
		return siteManager.getSites(docLibID);
	}
	
	public SolrServer getSolrOriginalServerBySiteID(int siteID){
		return solrOriginalServerMap.get(siteID);
	}
	
	public SolrServer getSolrServerBySiteID(int siteID){
		return solrServerMap.get(siteID);
	}
	
	public SolrServer getSolrAppServerBySiteID(int siteID){
		return solrServerAppMap.get(siteID);
	}
	
	public SolrServer getSolrArticleServerBySiteID(int siteID){
		return solrArticleServerMap.get(siteID);
	}
	
	public SolrServer getSolrArticleAppServerBySiteID(int siteID){
		return solrArticleAppServerMap.get(siteID);
	}

	@Override
	public void reset() {
		
	}
}