package com.founder.xy.article.web;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.dom.DocLib;
import com.founder.xy.api.ArticleJsonHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteSolrServerCache;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 稿件的几个服务的访问类：抽取关键词、摘要、文档转码
 * @author Gong Lijie
 */
public class ArticleServiceHelper {

	//----------------抽取关键词、摘要---------------------------------
	/** 访问关键词抽取服务，抽取关键词 */
	public static String extractKeywords(String content) {
        String url = UrlHelper.keyWordsServiceUrl();
        if (!StringUtils.isBlank(url)) {
        	// 访问分析服务获得result
            String size = InfoHelper.getConfig("写稿服务", "关键词提取个数");
	        return accessAnalyzeService(content, url, "keyword", "keywordList", size, " ");
        } else {
        	return "";
        }
	}

	/** 访问摘要抽取服务，抽取摘要 */
	public static String extractSummary(String content) {
        String url = UrlHelper.summaryServiceUrl();

        if (!StringUtils.isBlank(url)) {
        	// 访问分析服务获得result
            String size = InfoHelper.getConfig("写稿服务", "摘要提取语句数");
            String summary;
            //内容中包含中文，摘要用中文句号分隔，否则用英文句号
            if (isChinese(content)){
                summary = accessAnalyzeService(content, url, "summary", "SummaryList", size, "。") ;
            }else {
                summary = accessAnalyzeService(content, url, "summary", "SummaryList", size, ".");
            }
			if(!StringUtils.isBlank(summary))
			    summary +="。";
			if(summary.length()>600){
				summary = summary.substring(0,600)+"...";
			}
			return summary;
        } else {
        	return "";
        }
	}

    private static boolean isChinese(String content) {
        char[] chars = content.toCharArray();
        boolean result = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 19968 && chars[i] <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
                result = true;
                break;
            }
        }
        return result;
    }
	
	/** 通知文档转码服务，进行转码 */
	public static void fileTranscoding(String filePath) {
        String url = UrlHelper.fileTransServiceUrl();
        if (!StringUtils.isBlank(url)) {
			try {
				//把带存储设备名的
				filePath = InfoHelper.getFilePathInDevice(filePath);
				
				HttpPost httpPost = new HttpPost(url);
				
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("method", "transcoding"));
				nvps.add(new BasicNameValuePair("filePath", filePath));
				
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

				executePost(httpPost, false);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
	}

	public static JSONObject executePost(HttpPost httpRequest, boolean readResult) {
		return executeHttpRequest(httpRequest, readResult);
	}

	public static JSONObject executeGet(HttpGet httpRequest, boolean readResult) {
		return executeHttpRequest(httpRequest, readResult);
	}

    /** 处理获得的数据，并返回一个json对象 */
	public static JSONObject executeHttpRequest(HttpRequestBase httpRequest, boolean readResult) {
	    // 初始化 client端
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    // 发送数据，并获得response
	    CloseableHttpResponse httpResponse = null;
	    JSONObject json = null;
	    try {
	    	httpResponse = httpclient.execute(httpRequest);
	    	if (!readResult) {
	    		return null;
	    	} else {
	            // 从response中获得对象
	            HttpEntity entity = httpResponse.getEntity();
	            if (entity != null) {
		            // 转成UTF-8格式的字符串
		            String result = IOUtils.toString(entity.getContent(), "UTF-8");
		            json = JSONObject.fromObject(result);
		            // 销毁对象
		            EntityUtils.consume(entity);
	            }
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	ResourceMgr.closeQuietly(httpResponse);
	    }
	    return json;
	}
	public static String relsPossibleArticles(int siteID, int channel, String keyword) {
		String qStr = "a_status:\"1\" AND SYS_DELETEFLAG:\"0\" AND  a_siteID:"+"\""+siteID+"\" AND ";
		keyword = keyword.trim();
		
        if (keyword.contains(" ")) {
            String[] tagArr = keyword.split(" ");
            String sql = "";
            for (String str : tagArr) {
                sql += "text:\"" + str + "\" OR " ;
            }
            qStr += "(" + sql.substring(0,sql.length()-4) + ")";
        } else {
//            qStr += " text:\"" + keyword + "\"";
            keyword = keyword.replace("，",",");
            String[] tagArr = keyword.split(",");
            String sql = "";
            for (String str : tagArr) {
                sql += "text:\"" + str + "\" OR " ;
            }
            qStr += "(" + sql.substring(0,sql.length()-4) + ")";
        }
        /*
		if (keyword.contains(" ")) {
            String[] tagArr = keyword.split(" ");
            String sql = "";
            for (String str : tagArr) {
                sql += " a_keyword like '%" + str + "%' OR ";
            }
            qStr += "(" + sql.substring(4) + ")";
        } else {
            qStr += "a_keyword like '%" + keyword + "%'";
        }
        */
        List<Long> docIDs = solrQuery(qStr, siteID, channel, 0, 10);

        // 根据ID读稿件库，返回json
        return solrResult(docIDs, channel);
	}
	
	// 按keyword查询solr
    private static List<Long> solrQuery(
            String qStr, int siteID, int ch, int start,
            int count) {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", qStr);
        params.set("sort", "a_order asc");
        params.set("start", start);
        params.set("rows", count);

        SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache) (CacheReader
                .find(SiteSolrServerCache.class));
        SolrServer server = (ch == 1) ? siteSolrServerCache
                .getSolrArticleAppServerBySiteID(siteID) : siteSolrServerCache
                .getSolrArticleServerBySiteID(siteID);
        QueryResponse response = null;
        try {
            response = server.query(params);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        SolrDocumentList list = response.getResults();

        List<Long> docIDs = new ArrayList<>();
        for (SolrDocument doc : list) {
            int type = (Integer) doc.getFieldValue("a_type");
            if (type > 1)
                continue; // 只返回文章稿

            long fileId = Long.parseLong((String) doc
                    .getFieldValue("SYS_DOCUMENTID"));
            if (!docIDs.contains(fileId))
                docIDs.add(fileId);
        }

        return docIDs;
    }
	
    // 组装solr查询的结果，返回稿件列表json
    private static String solrResult(List<Long> docIDs, int ch) {
        String tenantCode = Tenant.DEFAULTCODE;

        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                                                     tenantCode);
        int articleLibID = articleLibs.get(ch).getDocLibID();

        int attLibID = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(),
                                          tenantCode);
        int artExtLibID = LibHelper.getLibID(DocTypes.ARTICLEEXT.typeID(),
                                             tenantCode);

        JSONArray jsonArr = listArticles(docIDs, articleLibID, attLibID,
                                         artExtLibID);

        return jsonArr.toString();
    }
    
    /**
     * 组装稿件列表 
     */
    private static JSONArray listArticles(
            List<Long> docIDs, int docLibID,
            int attLibID, int artExtLibID) {
        JSONArray jsonArr = new JSONArray();
        try {
            for (long docID : docIDs) {
                JSONObject inJson =
                		ArticleJsonHelper.listArticleOne(docLibID, docID, attLibID, artExtLibID);
                if (inJson != null)
                    jsonArr.add(inJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArr;
    }
    
	/** 访问提取服务 - 公用方法 */
	private static String accessAnalyzeService(String content, String serviceURL, String serviceName,
            String resultListName, String size, String resultSeperator) {

        try {
			// 1. 去掉文章中的空格 - 从网上粘贴的文章会有格式奇怪的空格
			content = content.replaceAll(" ", "").replaceAll("　", "");
			// 2. 组织成 post 格式的数据
			HttpPost httpPost = assembleData(serviceURL, content, serviceName, size);
			// 3. a. 发送数据并获得response; b.处理获得的数据，并返回一个json对象
			JSONObject json = executeHttpRequest(httpPost, true);
			// 4. 处理返回来的json数据
			return handleResult(resultListName, resultSeperator, json);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
	
    /** 组织成 post 格式的数据 */
	private static HttpPost assembleData(String keywordURL, String content, String method, String size)
            throws Exception {
        // 初始化一个post对象
        HttpPost httpPost = new HttpPost(keywordURL);
        // 封装参数列表
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("method", method));
        nvps.add(new BasicNameValuePair("content", content));
        nvps.add(new BasicNameValuePair("keyWordSize", "5"));
        // 封装成form对象
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        return httpPost;
    }
	
    /** 处理返回来的json数据 */
    @SuppressWarnings("unchecked")
	private static String handleResult(String resultList, String resultSeperator, JSONObject json)
            throws Exception {
        if (json == null)
            return "";

        // 获得状态
        String status = json.getString("status");
        List<String> list = new ArrayList<>();
        String result = "";
        // 如果成功， 组装result; 如果失败,返回""
        if ("success".equals(status)) {
            list = (List<String>) json.get(resultList);
            result = org.apache.commons.lang.StringUtils.join(list.iterator(), resultSeperator);
        }
        return result;
    }
}
