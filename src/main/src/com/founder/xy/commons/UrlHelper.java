package com.founder.xy.commons;

import com.founder.e5.commons.StringUtils;

/**
 * 一些Url相关的读取方法
 * @author Gong Lijie
 */
public class UrlHelper {

	/** 外网稿件内容Url */
	public static String getArticleContentUrl(long id) {
		return apiArticleContent() + "?articleId=" + id;
	}

	/** 外网数字报稿件内容Url */
	public static String getPaperArticleContentUrl(int siteID, long id) {
		return apiPaperArticleContent() + "?id=" + id;
	}

	/** 外网问答内容Url */
	public static String getQAContentUrl(int siteID, long id) {
		return apiQAContent() + "?siteID=" + siteID + "&fileId=" + id;
	}

	/** 外网活动内容Url */
	public static String getActivityContentUrl(int siteID, long id) {
		return apiActivityContent() + "?siteID=" + siteID + "&fileId=" + id;
	}

	/**
     * 外网Api访问地址，从系统参数中读得。
     * 如http://172.19.32.212:8080/app_if/
     */
    public static String apiUrl() {
    	String url = InfoHelper.getConfig("互动", "外网Api地址");
    	if (!url.endsWith("/")) url += "/";
    	
    	return url;	
    }

    /** 外网Api：用户头像访问api，/amuc/api/member/getPortrait */
    public static String apiUserIcon() {
    	//return InfoHelper.getConfig("互动", "用户头像访问地址");
    	return apiUrl() + "amuc/api/member/getPortrait";
    }
    
	/** 外网api：稿件内容api，/getArticleContent */
	public static String apiArticleContent() {
		return apiUrl() + "getArticleContent";
	}

	/** 外网api：数字报稿件内容api，/getPaperArticle */
	public static String apiPaperArticleContent() {
		return apiUrl() + "getPaperArticle";
	}

	/** 外网api：问答内容api，/qaDetail */
	public static String apiQAContent() {
		return apiUrl() + "qaDetail";
	}

	/** 外网api：活动内容api，/activityDetail */
	public static String apiActivityContent() {
		return apiUrl() + "activityDetail";
	}



	/** 智能分析服务地址*/
	private static String articleServiceUrl(){
		String url = InfoHelper.getConfig("写稿服务", "智能分析地址");
		if (StringUtils.isBlank(url)) {
			String ip = InfoHelper.getConfig("写稿服务", "智能分析服务地址");
			if (!StringUtils.isBlank(ip)) {
				url = ip +":"+InfoHelper.getConfig("写稿服务", "智能分析服务端口");
			}
		}
		return url;
	}
	public static String sensitiveServiceUrl(){
		String url =articleServiceUrl() ;
		return  StringUtils.isBlank(url)? "" : url + "/xyservice/servlet/SensitiveServlet";
	}
	/** 关键词提取服务地址*/
	public static String keyWordsServiceUrl(){
		String url = articleServiceUrl() ;
		if(StringUtils.isBlank(url)){
			url = InfoHelper.getConfig("写稿服务", "关键词提取服务");
		}else{
			url += "/xyservice/servlet/AnalyzeServlet";
		}
		return url ;
	}

	/** 摘要提取服务地址*/
	public static String summaryServiceUrl(){
		String url = articleServiceUrl() ;
		if(StringUtils.isBlank(url)){
			url = InfoHelper.getConfig("写稿服务", "摘要提取服务");
		}else{
			url += "/xyservice/servlet/AnalyzeServlet";
		}
		return url ;
	}

	/** 摘要文档转码服务地址*/
	public static String fileTransServiceUrl(){
		String url = articleServiceUrl() ;
		if(StringUtils.isBlank(url)){
			url = InfoHelper.getConfig("写稿服务", "文档转码服务");
		}else{
			url += "/xyservice/servlet/OfficeTransServlet";
		}
		return url ;
	}

	/** 图片服务地址*/
	public static String imageServiceUrl(){
		String url = articleServiceUrl() ;
		if(StringUtils.isBlank(url)){
			url = InfoHelper.getConfig("写稿服务", "图片处理服务");
		}else{
			url += "/xyservice/servlet/ImageServlet";
		}
		return url ;
	}

	/** 校对服务器地址 */
	public static String proofUrl() {
		String url = InfoHelper.getConfig("写稿服务", "校对服务器地址");
		if(!url.startsWith("http://")) {
			url = "http://" + url;
	    }
		if(!url.endsWith("/")) {
			url+="/";
		}
		url+="CheckWordsService.asmx";
		return url;
	}
	//-------------------资源文件---------------------
	public static String getArticleShareUrl() {
		String rootUrl = InfoHelper.getConfig("互动", "外网资源地址");
		return getArticleShareUrl(rootUrl);
	}
	
	public static String getArticleShareUrl(String rootUrl) {
		return rootUrl + "/pad/index.html#/detail";
	}
	public static String getPicShareUrl(String rootUrl) {
		return rootUrl + "/pad/index.html#/detailPicture";
	}
	public static String getVideoShareUrl(String rootUrl) {
		return rootUrl + "/pad/index.html#/detailVideo";
	}

	public static String getSpecialShareUrl() {
		return resourceUrl("专题分享页地址", "/pad/index.html#/special");
	}

	public static String getSpecialShareUrl(String rootUrl) {
		//return resourceUrl("专题分享页地址", rootUrl, "/share/specialPage.html");
		return resourceUrl("专题分享页地址", rootUrl, "/pad/index.html#/special");
	}

	public static String getWebLiveShareUrl() {
		//return resourceUrl("WEB直播分享页地址", "/share/webLivePage.html");
		return resourceUrl("WEB直播分享页地址", "/web/index.html#/detailLive");
	}

	public static String getWebLiveShareUrl(String rootUrl) {
		return resourceUrl("WEB直播分享页地址", rootUrl, "/web/index.html#/detailLive");
	}

	public static String getLiveShareUrl() {
		//return resourceUrl("直播分享页地址", "/share/livePage.html");
		return resourceUrl("直播分享页地址", "/pad/index.html#/detailLive");
	}

	public static String getLiveShareUrl(String rootUrl) {
		return resourceUrl("直播分享页地址", rootUrl, "/pad/index.html#/detailLive");
	}

	public static String getActivityShareUrl() {
		//return resourceUrl("活动分享页地址", "/share/activityPage.html");
		return resourceUrl("活动分享页地址", "/pad/index.html#/detailActivity");
	}

	public static String getActivityShareUrl(String rootUrl) {
		return resourceUrl("活动分享页地址", rootUrl, "/pad/index.html#/detailActivity");
	}

	public static String getSubjectShareUrl() {
		//return resourceUrl("问吧分享页地址", "/share/subjectqaPage.html");
		return resourceUrl("问吧分享页地址", "/pad/index.html#/detailSubject");
	}

	public static String getSubjectShareUrl(String rootUrl) {
		return resourceUrl("问吧分享页地址", rootUrl, "/pad/index.html#/detailSubject");
	}

	public static String getQAShareUrl() {
		//return resourceUrl("问答分享页地址", "/share/qaPage.html");
		return resourceUrl("问答分享页地址", "/pad/index.html#/detailQa");
	}

	public static String getQAShareUrl(String rootUrl) {
		return resourceUrl("问答分享页地址", rootUrl, "/pad/index.html#/detailQa");
	}

	private static String resourceUrl(String param, String relativeUrl) {
		String pageUrl = InfoHelper.getConfig("互动", param) ;
		if (pageUrl != null) return pageUrl;
		
		String rootUrl = InfoHelper.getConfig("互动", "外网资源地址");
		return rootUrl + relativeUrl;
	}

	private static String resourceUrl(String param, String rootUrl, String relativeUrl) {
		String pageUrl = InfoHelper.getConfig("互动", param) ;
		return (pageUrl != null ? pageUrl : rootUrl + relativeUrl);
	}
}
