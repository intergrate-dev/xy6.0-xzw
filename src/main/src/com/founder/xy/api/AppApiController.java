package com.founder.xy.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.founder.e5.context.E5Exception;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

/**
 * 与App外网api通讯的Api
 *
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/api/app")
public class AppApiController {
    @Autowired
    private ApiManager apiManager;
    
    @Autowired
    private ArticleApiManager articleApiManager;

    @Autowired
    private ArticleImpManager articleImpManager;
    
    /**
     * 获取一篇稿件
     */
    @RequestMapping(value = "getArticle.do")
    public void getArticle(
            HttpServletRequest request, HttpServletResponse response,
            long docID, int siteID) throws E5Exception {
        long colID = WebUtil.getLong(request, "colID", 0);
        int docLibID = LibHelper.getArticleAppLibID();
        boolean result = articleApiManager.getArticle(docLibID, docID, colID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取某个栏目下指定数目的稿件列表
     */
    @RequestMapping(value = "getArticles.do")
    public void getArticles(HttpServletRequest request,HttpServletResponse response,
    		long colID, int start, int siteID) throws E5Exception {
        //暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        int typeScreen =WebUtil.getInt(request,"typeScreen",0);
        boolean result = articleApiManager.getArticles(colLibID, colID, start,typeScreen);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 按标签得到稿件列表
     * @return
     */
    @RequestMapping(value = "tagArticles.do")
    public void tagArticles(
            HttpServletRequest request, HttpServletResponse response,
            String tag, int siteID, int page) {
        int ch = WebUtil.getInt(request, "ch", 0);
        if (ch < 0 || ch > 1) ch = 0;

        String result = articleApiManager.tagArticles(tag, siteID, ch, page);

        InfoHelper.outputJson(result, response);
    }

    /**
     * 得到行业分类的稿件列表
     * 访问全文检索服务（全文检索服务中，稿件的行业分类字段设置为多值字段）
     *
     * @return
     */
    @RequestMapping(value = "tradeArticles.do")
    public void tradeArticles(
            HttpServletRequest request, HttpServletResponse response,
            String tradeIDs, int siteID, int page) {
        int ch = WebUtil.getInt(request, "ch", 0);
        if (ch < 0 || ch > 1) ch = 0;

        String result = articleApiManager.tradeArticles(tradeIDs, siteID, ch, page);

        InfoHelper.outputJson(result, response);
    }
    
    /**
     * 得到会员收藏的稿件列表信息
     *
     */
    @RequestMapping(value = "collectArticles.do")
    public void collectArticles(
            HttpServletRequest request, HttpServletResponse response,
            String articleIDs, int ch) {
        if (ch < 0 || ch > 1) ch = 0;

        String result = articleApiManager.collectArticles(articleIDs, ch);

        InfoHelper.outputJson(result, response);
    }
    
    /**
     * 获取某个栏目的推荐模块列表
     */
    @RequestMapping(value = "getColumnModules.do")
    public void getColumnModules(HttpServletResponse response, long colID, int siteID) throws E5Exception {
        boolean result = articleApiManager.getColumnModules(colID, siteID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取最热稿件列表
     */
    @RequestMapping(value = "articleHot.do")
    public void getArticlesHot(
            HttpServletResponse response, int siteID, int type) throws E5Exception {
        //暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        boolean result = articleApiManager.getArticlesHot(colLibID, siteID, type);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 搜索某栏目下的稿件
     */
    @RequestMapping(value = "search.do")
    public void getSearchArticles(
            HttpServletResponse response, int siteID,
            long colID, String key, int start, int count) throws E5Exception {
        String result = articleApiManager.search(colID, key, siteID, start, count);
        InfoHelper.outputText(result, response);
    }

    /**
     * 搜索某栏目及其子孙栏目下的稿件
     */
    @RequestMapping(value = "searchAll.do")
    public void getSearchAllArticles(
            HttpServletResponse response, int siteID, long colID,
            String key, int start, int count) throws E5Exception {
        // 暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        try {
            key = URLDecoder.decode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = articleApiManager.searchAll(colLibID, colID, key, siteID, start, count);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 搜索某WEB栏目及其子孙栏目下的稿件
     */
    @RequestMapping(value = "searchWebArticles.do")
    public void getSearchWebArticles(
            HttpServletResponse response, int siteID, long colID,
            String key, int start, int count) throws E5Exception {
        // 暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        try {
            key = URLDecoder.decode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = articleApiManager.searchWebArticle(colLibID, colID, key, siteID, start, count);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取栏目的子孙栏目列表
     */
    @RequestMapping(value = "getColumnsAll.do")
    public void getColumnsAll(
            HttpServletResponse response, int siteID,
            long colID) throws E5Exception {
        //暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        boolean result = apiManager.getColumnsAll(siteID, colLibID, colID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 通过父栏目ID获取所有的子栏目信息
     */
    @RequestMapping(value = "getColumns.do")
    public void getColumns(
            HttpServletResponse response, int siteID,
            long colID) throws E5Exception {
        //暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        boolean result = apiManager.getColumns(siteID, colLibID, colID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取一个栏目
     */
    @RequestMapping(value = "getColumn.do")
    public void getColumn(
            HttpServletResponse response, int siteID,
            long colID) throws E5Exception {
        boolean result = apiManager.getColumn(siteID, colID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 我订阅的栏目
     *//*
	@RequestMapping(value = "myColumn.do")
	public void myColumn(HttpServletResponse response, int id, int siteID) throws Exception {
		String result = apiManager.myColumnSubscribed(id);
		InfoHelper.outputText(result, response);
	}*/

    /**
     * 读分类
     */
    @RequestMapping(value = "getCats.do")
    public void getCats(HttpServletRequest request, HttpServletResponse response, String code)
            throws E5Exception {
        int siteID = WebUtil.getInt(request, "siteID", 1); //默认为站点1
        boolean result = apiManager.getCats(code, siteID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
	 * 读我的分类
	 */
	@RequestMapping(value = "myCats.do")
	public void myCats(HttpServletRequest request, HttpServletResponse response, String code) 
			throws E5Exception {
		int siteID = WebUtil.getInt(request, "siteID", 1); //默认为站点1
		String keywords = request.getParameter("keywords"); //已订阅的标签ID
		String result = apiManager.getMyCats(code, siteID, keywords);
		InfoHelper.outputText(String.valueOf(result), response);
	}
    
    /**
     * App端启动：返回移动模板zip包下载地址、<启动图片url，显示秒数>
     */
    @RequestMapping(value = "startup.do")
    public void startup(HttpServletResponse response, long appID, int siteID) throws E5Exception {
        boolean result = apiManager.getMobiles(appID);
        InfoHelper.outputText(String.valueOf(result), response);
    }
    
	/**
	 * 获取站点的设置参数，用于网站
	 */
	@RequestMapping(value = "siteConf.do")
	public void siteConf(HttpServletRequest request,
							 HttpServletResponse response, int siteID)
			throws E5Exception {

		boolean result = apiManager.siteConf(siteID);
		InfoHelper.outputText(String.valueOf(result), response);
	}

    @RequestMapping(value = "getAppInfo.do")
    public void getAppInfo(HttpServletResponse response) throws E5Exception {
        String result = apiManager.getAppInfo();
        InfoHelper.outputText(result, response);
    }

    /**
     * 获取订阅栏目及稿件
     */
    @RequestMapping(value = "subcribeView.do")
    public void subcribeView(
            HttpServletResponse response, int siteID,
            int userID, String columnId, String device) throws E5Exception {
        String result = articleApiManager.subscribeView(siteID, userID, columnId, device);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 获取订阅栏目及稿件
     */
    @RequestMapping(value = "subcribeXY.do")
    public void subcribeXY(
            HttpServletResponse response, int siteID,
            int userID, String columnId, String device) throws E5Exception {
        String result = articleApiManager.subscribeXY(siteID, userID, columnId, device);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 在订阅栏目下按名称搜索栏目
     */
    @RequestMapping(value = "getSubcribeCols.do")
    public void getSubcribeCols(
            HttpServletResponse response,
            int siteID, String key, long colID) throws E5Exception {
        String result = apiManager.searchSubcribeCols(siteID, key, colID);
        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "getColSubscribeCount.do")
	public void getColSubscribeCount(HttpServletRequest request, HttpServletResponse response, long id) {
	    long info = apiManager.getColSubscribeCount(LibHelper.getLibID(DocTypes.COLUMN.typeID(),
	                                                                   Tenant.DEFAULTCODE), id);
	    InfoHelper.outputJson(String.valueOf(info), response);
	}

	/**
     * 查找订阅栏目id
     */
    @RequestMapping(value = "myColumnIDs.do")
    public void myColumnIDs(HttpServletResponse response, long userID) throws E5Exception {
        boolean result = apiManager.getSubColIDs(userID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取所有站点和栏目
     */
    @RequestMapping(value = "getNodeTree.do")
    public void getNodeTree(HttpServletResponse response) throws E5Exception {
        String result = apiManager.getNodeTree();
        InfoHelper.outputText(result, response);
    }

    /**
     * 推荐接口
     */
    @RequestMapping(value = "recommendList.do")
    public void getRecommendList(
            HttpServletResponse response,
            String data, int start, int count, int siteID) throws E5Exception {
        String result = articleApiManager.getRecommendList(data, start, count, siteID);
        InfoHelper.outputJson(result, response);
    }

    /**
     * 历史推荐接口
     */
    @RequestMapping(value = "historyRecList.do")
    public void getHistoryRecList(
            HttpServletResponse response,
            String data, int start, int count, int siteID) throws E5Exception {
        String result = articleApiManager.getHistoryRecList(data, start, count, siteID);
        InfoHelper.outputJson(result, response);
    }

    /**
     *  文章关键词
     */
    @RequestMapping(value = "recKeywords.do")
    public void getRecKeywords(
            HttpServletResponse response,
            String data) throws E5Exception {
        boolean result = articleApiManager.getRecKeywords(data);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    @RequestMapping(value = "moduleView.do")
    public void moduleView(HttpServletRequest request, HttpServletResponse response, long id) {
        boolean result = articleApiManager.getModuleView(id);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    @RequestMapping(value = "message.do")
    public void message(
            HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int siteID) {
        boolean result = apiManager.message(page, siteID);
        InfoHelper.outputText(String.valueOf(result), response);
    }
    @RequestMapping(value = "getAdvs.do")
    public void getAdvs(
            HttpServletRequest request, HttpServletResponse response, long columnID) {
        boolean result = articleApiManager.getAdvs(columnID,-1,-1);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    /**
     * 获取微信分享的jsapi_ticket，并缓存到Redis中
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "getJsApiTicket.do")
    public void getJsApiTicket(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String result = articleApiManager.getJsApiTicket();
    	InfoHelper.outputText(result, response);
    }

    /**
     * 保存邮件订阅信息
     * @param siteID
     * @param name
     * @param email
     * @param request
     * @param response
     */
    @RequestMapping(value = "emailSubscribe.do")
    public void saveEmailSubscribe(int siteID, String name, String email, HttpServletRequest request, HttpServletResponse response){
        String result = apiManager.saveEmainSubscribe(siteID, name, email);
        InfoHelper.outputText(result, response);
    }
    
    /**
     * 查看热门稿件列表（新）
     * @param siteID
     * @param articleType
     * @param timeType
     * @param orderType
     */
    @RequestMapping(value = "newArticleHotList.do")
    public void getNewArticleHotList(int siteID, int articleType, int timeType, int orderType, int channel, HttpServletRequest request, HttpServletResponse response) {
        boolean result = articleApiManager.getNewArticleHotList(siteID,articleType,timeType,orderType,channel);

        InfoHelper.outputText(String.valueOf(result), response);
    }
    
    @RequestMapping(value = "getSubColArticles.do")
    public void getSubColArticles(HttpServletRequest request,HttpServletResponse response,
    		long colID, int siteID) throws E5Exception {
        //暂时做单租户处理
        int colLibID = LibHelper.getColumnLibID();
        boolean result = articleApiManager.getSubColArticles(colLibID, colID, siteID);
        InfoHelper.outputText(String.valueOf(result), response);
    }
    
    @RequestMapping(value = "getTopicArticles.do")
    public void getTopicArticles(HttpServletRequest request,HttpServletResponse response,
    		long topicID, int start, int siteID, int channel) throws E5Exception {
        //暂时做单租户处理
        int type =WebUtil.getInt(request,"type",100);
        boolean result = articleApiManager.getTopicArticles(topicID, start, siteID, type,channel);
        InfoHelper.outputText(String.valueOf(result), response);
    }
    
    @RequestMapping(value = "getArticleCountInfo.do")
    public void getArticleCountInfo(long colID, long docID, HttpServletRequest request, HttpServletResponse response) {
    	String result = articleApiManager.getArticleCountInfo(colID, docID);
    	InfoHelper.outputText(result, response);
    }

    @RequestMapping(value = "getExternalSystemAuth.do")
    public void getExternalSystemAuth(HttpServletRequest request,HttpServletResponse response, int eid){
        boolean result = apiManager.getExternalSystemAuth(eid);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    @RequestMapping(value = "getColumnsAllEasy.do")
    public void getColumnsAllEasy(HttpServletRequest request,HttpServletResponse response,int siteID, long parentID) throws E5Exception {
        int colLibID = LibHelper.getColumnLibID();
        boolean result = apiManager.getColumnsAllEasy(siteID,parentID,colLibID);
        InfoHelper.outputText(String.valueOf(result), response);
    }

    @RequestMapping(value = "addArticleForBig.do")
    public void addArticleForBig(HttpServletRequest request, HttpServletResponse response, String data){
        String info = articleImpManager.addArticleForBig(data);
        InfoHelper.outputText(String.valueOf(info), response);
    }

    @RequestMapping(value = "deleteArticleForBig.do")
    public void deleteArticleForBig(HttpServletRequest request, HttpServletResponse response, String data){
        String info = articleImpManager.deleteArticleForBig(data);
        InfoHelper.outputText(String.valueOf(info), response);
    }
}
