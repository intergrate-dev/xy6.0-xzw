package com.founder.xy.api.imp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteSolrServerCache;

public class SearchArticleHelper {

	public static JSONObject pubSearch(String key, int siteID, int channel, long lastID, long columnID,int status, int type ) {
		boolean isrel = true;
		JSONObject result = new JSONObject();
		JSONArray articles = new JSONArray();
		if(siteID < 0){
			try {
				siteID = getSiteIDByColumnID(columnID);
			} catch (E5Exception e) {
				return buildFailureResult(e.getMessage());
			}
		}
        String qStr = "a_siteID:"+"\""+ siteID+"\" AND CLASS_1:\"" + columnID + "\" AND SYS_DELETEFLAG:\"0\"";
        if (status > -1) qStr += " AND a_status:\"" + status + "\"";
        if (type > -1) qStr += " AND a_type:\"" + type + "\"";
        if (StringUtils.isNotBlank(key)) qStr += " AND SYS_TOPIC:\"" + ClientUtils.escapeQueryChars(key) + "\"";
        
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", qStr);
        params.set("sort", "a_order asc");
        if(lastID > 0){
        	String a_order = getLastOrderby(lastID, siteID, channel, isrel);
        	params.set("fq", "a_order:["+a_order+" TO *]");
        	params.set("start", 1);
        	params.set("rows", 21);
        }else{
        	params.set("start", 0);
        	params.set("rows", 20);
        }
        List<Long> docIDs = solrQuery(params, siteID, channel, isrel);
      //如果关键字为纯数字，作为稿件ID查询稿件，
        if(lastID < 1 && key.matches("[0-9]+")){
        	long docid = Long.parseLong(key);
        	if(docIDs.contains(docid))
        		docIDs.remove(docid);
        	docIDs.add(0, docid); //按ID查找的稿件放到首位
        }
        if(docIDs != null && docIDs.size()>0){
        	try {
        		// 根据ID读稿件库，返回json
        		getArticles(docIDs, channel, articles);
        		result.put("success", true);
				result.put("errorInfo", "");
				result.put("results", articles);
        	} catch (E5Exception e) {
        		return buildFailureResult(e.getMessage());
        	}
        }else{
        	return buildFailureResult("未查询到稿件！");
        }
		return result;
	}
	
	public static JSONObject revokeSearch(String key, int siteID, int channel, long lastID, long fileId, int status, int type ){
		boolean isrel = false;
		JSONObject result = new JSONObject();
		JSONArray articles = new JSONArray();
		int docLibID = getDocLibIDByChannel(channel);
		if(fileId>0){ //按稿件ID查找稿件
			try {
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document doc = docManager.get(docLibID, fileId);
				JSONObject article = new JSONObject();
				getArticleJson(doc, article);
				articles.add(article);
			} catch (E5Exception e) {
				return buildFailureResult(e.getMessage());
			}
		}else{ //按关键字查找稿件
	        String qStr = "a_siteID:"+"\""+ siteID+"\" AND SYS_DELETEFLAG:\"0\"";
	        if (status > -1) qStr += " AND a_status:\"" + status + "\"";
	        if (type > -1) qStr += " AND a_type:\"" + type + "\"";
	        if (StringUtils.isNotBlank(key)) qStr += " AND SYS_TOPIC:\"" + ClientUtils.escapeQueryChars(key) + "\"";
	        
	        ModifiableSolrParams params = new ModifiableSolrParams();
	        params.set("q", qStr);
	        params.set("sort", "a_pubTime desc");
	        if(lastID > 0){
	        	String a_pubTime = getLastPubTime(lastID, channel);
	        	params.set("fq", "a_pubTime:[* TO "+a_pubTime+"]");
	        	params.set("start", 1);
	        	params.set("rows", 21);
	        }else{
	        	params.set("start", 0);
	        	params.set("rows", 20);
	        }
	        List<Long> docIDs = solrQuery(params, siteID, channel, isrel);
	        //如果关键字为纯数字，作为稿件ID查询稿件，
	        if(lastID < 1 && key.matches("[0-9]+")){
	        	long docid = Long.parseLong(key);
	        	if(docIDs.contains(docid))
	        		docIDs.remove(docid);
	        	docIDs.add(0, docid); //按ID查找的稿件放到首位
	        }
	        if(docIDs != null && docIDs.size()>0){
	        	try {
	        		// 根据ID读稿件库，返回json
	        		getArticles(docIDs, channel, articles);
	        	} catch (E5Exception e) {
	        		return buildFailureResult(e.getMessage());
	        	}
	        }else{
	        	return buildFailureResult("未查询到稿件！");
	        }
		}
		result.put("success", true);
		result.put("errorInfo", "");
		result.put("results", articles);
		
		return result;
	}
	
	private static List<Long> solrQuery(ModifiableSolrParams params, int siteID, int channel, boolean isrel) {
        SolrServer server = getSolrServer(siteID, channel, isrel);
        QueryResponse response = null;
        try {
            response = server.query(params);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        SolrDocumentList list = response.getResults();
        List<Long> docIDs = new ArrayList<>();
        for (SolrDocument doc : list) {
//            int type = (Integer) doc.getFieldValue("a_type");
//            if (type > 1)
//                continue; // 只返回文章稿
        	Object obj = doc.getFieldValue("SYS_DOCUMENTID");
        	long fileId = (obj instanceof String)?Long.parseLong((String)obj):((Long)obj);
            if (!docIDs.contains(fileId))
                docIDs.add(fileId);
        }
        return docIDs;
    }
	
	private static String getLastOrderby(long lastID, int siteID, int channel, boolean isrel){
		SolrDocument doc = solrQueryOne(lastID, siteID, channel, isrel);
		return String.valueOf(doc.getFieldValue("a_order"));
	}
	
	private static String getLastPubTime(long lastID, int channel){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = (channel==1)?LibHelper.getArticleLibID():LibHelper.getArticleAppLibID();
		Document doc;
		try {
			doc = docManager.get(docLibID, lastID);
			Date pubTime = doc.getDate("a_pubTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String yyMMdd = sdf.format(pubTime);
			sdf = new SimpleDateFormat("HH:mm:ss");
			String HHmmss = sdf.format(pubTime);
			return yyMMdd + "T" + HHmmss + "Z";
		} catch (E5Exception e) {
			return "*";
		}
	}
	private static String getLastPubTime(int lastID, int siteID, int channel, boolean isrel){
		SolrDocument doc = solrQueryOne(lastID, siteID, channel, isrel);
		Date pubTime = (Date)doc.getFieldValue("a_pubTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yyMMdd = sdf.format(pubTime);
		sdf = new SimpleDateFormat("HH:mm:ss");
		String HHmmss = sdf.format(pubTime);
		return yyMMdd + "T" + HHmmss + "Z";
	}
	
	private static SolrDocument solrQueryOne(long lastID, int siteID, int channel, boolean isrel){
		SolrServer server = getSolrServer(siteID, channel, isrel);
		ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", "SYS_DOCUMENTID:\""+lastID+"\"");
        QueryResponse response = null;
        try {
            response = server.query(params);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        SolrDocumentList list = response.getResults();
        if(list != null && list.size()>0) return list.get(0);
        else return null;
	}
	
	private static void getArticles(List<Long> docIDs, int channel, JSONArray articles) throws E5Exception {
		int docLibID = getDocLibIDByChannel(channel);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		if(docIDs != null){
			for(Long docID : docIDs) {
				Document doc = docManager.get(docLibID, docID);
				if (doc != null) {
					JSONObject article = new JSONObject();
					getArticleJson(doc, article);
					articles.add(article);
				}
			}
		}
	}
	
	private static void getArticleJson(Document doc, JSONObject article) {
		article.put("fileId", doc.getDocID());// 稿件ID
		article.put("docLibID", doc.getDocLibID());// 稿件库ID
		article.put("title", doc.getTopic());
		article.put("articleType", doc.getInt("A_TYPE"));
		article.put("status", doc.getInt("A_STATUS"));
		article.put("publishtime", ArticleDetailHelper.getDateString(doc.getDate("A_PUBTIME")));
		article.put("editor", doc.getString("A_EDITOR"));
		article.put("colID", doc.getInt("A_COLUMNID"));
		article.put("channel", doc.getInt("A_CHANNEL"));
		article.put("linkID",doc.getInt("a_linkID"));
		article.put("linkName", doc.getString("a_linkName"));
		
		article.put("picSmall", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICBIG")));
		article.put("picMiddle", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICMIDDLE")));
		article.put("picBig", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICSMALL")));
	}
	
	private static SolrServer getSolrServer(int siteID, int channel, boolean isrel){
		SolrServer server = null;
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache) (CacheReader.find(SiteSolrServerCache.class));
        if (isrel) server = (channel == 2) ? siteSolrServerCache.getSolrAppServerBySiteID(siteID) : siteSolrServerCache.getSolrServerBySiteID(siteID);
        else server = (channel == 2) ? siteSolrServerCache.getSolrArticleAppServerBySiteID(siteID) : siteSolrServerCache.getSolrArticleServerBySiteID(siteID);
        return server;
	}
	
	private static int getSiteIDByColumnID(long columnID) throws E5Exception{
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int colLibID = LibHelper.getColumnLibID();
		Document col = docManager.get(colLibID, columnID);
		return col.getInt("COL_SITEID");
	}
	
	private static int getDocLibIDByChannel(int channel){
		if(channel==0) return LibHelper.getOriginalLibID();
		if(channel==1) return LibHelper.getArticleLibID();
		if(channel==2) return LibHelper.getArticleAppLibID();
		return 0;
	}
	
	private static JSONObject buildFailureResult(String errorInfo){
		JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("errorInfo", errorInfo);
		result.put("results", new JSONArray());
		return result;
	}
		
}
