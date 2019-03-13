package com.founder.xy.system.check;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.WebUtil;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 外网api测试
 */
@Controller
@RequestMapping("/xy/system/")
public class ApiCheckController {
	@Autowired
	private SystemCheckManager checkManager ;
	
	private JSONObject param = new JSONObject() ;

	@RequestMapping("initCheckParam")
	public void initCheckParam(HttpServletRequest request,HttpServletResponse response) {
		JSONObject param = checkManager.initCheckParam() ;
		this.setParam(param) ;
		InfoHelper.outputJson(param.toString(), response);
	}
	
	@RequestMapping("editCheckParam")
	public void editCheckParam(HttpServletRequest request,HttpServletResponse response) {
		JSONObject param = this.getParam() ;
		param.put("ROOTURL", WebUtil.get(request, "ROOTURL")) ; //站点ID
		param.put("siteID", WebUtil.getInt(request, "siteID", param.getInt("siteID"))) ; //站点ID
		param.put("colID", WebUtil.getLong(request, "colID", param.getLong("colID"))) ; //栏目ID
		param.put("articleID", WebUtil.getLong(request, "articleID", param.getLong("articleID"))) ; //测试稿件ID
		param.put("liveID", WebUtil.getLong(request, "liveID", param.getLong("liveID"))) ; //直播话题ID
		param.put("activeID", WebUtil.getLong(request, "activeID", param.getLong("articleID"))) ; //活动ID
		param.put("paperID", WebUtil.getLong(request, "paperID", param.getLong("articleID"))) ; //数字报ID
		this.setParam(param);
		InfoHelper.outputJson(this.getParam().toString(), response);
	}
	
	@RequestMapping(value="getApiNames.do")
	public void getApiNames(HttpServletRequest request,HttpServletResponse response){
		JSONArray arr = new JSONArray() ;
		Method[] methods = this.getClass().getDeclaredMethods() ;
		for(int i = 0; i < methods.length ; i++){
			String methodName = methods[i].getName() ;
			if(methodName.endsWith("Check")){
				arr.add(methodName) ;
			}
		}
		InfoHelper.outputJson(arr.toString(), response);
	}
	
	@RequestMapping(value="apiSingleCheck.do")
	public void checkSingle(HttpServletRequest request,HttpServletResponse response,String name){
		Method method = null ;
		try {
			method = this.getClass().getDeclaredMethod(name) ;
			long startTime=System.currentTimeMillis();  //执行开始
			JSONObject obj = (JSONObject)method.invoke(this) ;
			long endTime=System.currentTimeMillis(); //执行结束
			obj.put("time", endTime-startTime) ; //用时
			obj.put("name", name) ;
			obj.put("value", name.substring(0, name.indexOf("Check"))) ;
			InfoHelper.outputJson(obj.toString(), response);
		} catch (Exception e) {
			System.out.println(method.getName());
			e.printStackTrace();
		} 
	}
	
	/** 测试稿件详情接口 */
	public JSONObject getArticleContentCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		
		String url = this.getParam().getString("ROOTURL") + "getArticleContent" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("articleId", articleID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				JSONObject rs = json.getJSONObject("result") ;
				if(rs.getLong("fileId") == articleID){ 
						rs.getLong("countDiscuss") ; //评论数
						rs.getLong("countPraise") ; //点赞
						rs.getLong("countShare") ; //分享
						rs.getLong("countClick"); //分享点击数
						obj.put("result", 0) ; //正常
				}else{
					obj.put("result", 1) ; //异常
				}
			} catch (Exception e) {
				obj.put("result", 1) ;  //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件详情") ; //描述
		return obj ;
	}
	
	/** 测试 稿件评论数接口 */
	public JSONObject discussCountCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		
		String url = this.getParam().getString("ROOTURL") + "discussCount" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("id", articleID) ;
		urlParam.put("articleId", articleID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			//得到稿件详细信息中的稿件评论数
			try {
				long count = checkManager.executeHttpPostRequest(this.getParam().getString("ROOTURL")+"getArticleContent?",urlParam).getJSONObject("result").getLong("countDiscuss") ;
				obj.put("result", json.getLong("result") == count?0:1) ; //异常
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件评论数") ; //描述
		return obj ;
	}
	
	/** 测试 稿件 评论接口 */
	public JSONObject discussCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		long userID = param.getLong("userID") ;
		String content = param.getString("content") ;
		
		String url = this.getParam().getString("ROOTURL") + "discuss" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("rootID", articleID) ;
		urlParam.put("content", content) ;
		urlParam.put("userID", userID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ;
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件评论") ; //描述
		return obj ;
	}
	
	/** 测试 稿件 点赞 接口 */
	public JSONObject eventUPCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "event" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("id", articleID) ;
		urlParam.put("userID", userID) ;
		urlParam.put("eventType", 1) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件点赞") ; //描述
		return obj ;
	}
	
	/** 测试 稿件分享 接口 */
	public JSONObject eventShareCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "event" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("id", articleID) ;
		urlParam.put("userID", userID) ;
		urlParam.put("eventType", 2) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件分享") ; //描述
		return obj ;
	}
	
	/** 测试 稿件分享页点击 接口 */
	public JSONObject eventShareClickCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "event" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("id", articleID) ;
		urlParam.put("userID", userID) ;
		urlParam.put("eventType", 2) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件分享页点击") ; //描述
		return obj ;
	}
	
	/** 测试 稿件收藏 接口 */
	public JSONObject favCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "fav" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("userID", userID) ;
		urlParam.put("id", articleID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件收藏") ; //描述
		return obj ;
	}
	
	/** 测试 稿件检索 接口 */
	public JSONObject searchCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long colID = param.getLong("colID") ;
		String title = param.getString("title") ;
		
		String url = this.getParam().getString("ROOTURL") + "search" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("columnId", colID) ;
		urlParam.put("start", 0) ;
		urlParam.put("count", 0) ;
		urlParam.put("key", title) ;
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		
		if(json.getInt("status") ==200){
			try {
				obj.put("result", json.getJSONArray("result").isEmpty()?1:0) ;
			} catch (Exception e) {
				obj.put("result", 1) ;
			}
		}
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件搜索") ; //描述
		return obj ;
	}
	
	/** 测试 稿件评论列表 接口 */
	public JSONObject discussViewCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		
		String url = this.getParam().getString("ROOTURL") + "discussView" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("id", articleID) ;
		urlParam.put("page", 0) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj,json,"list",DocTypes.DISCUSS.typeID(), " a_parentID=0 AND a_articleID=? AND a_type=? AND a_sourceType=? AND a_status=?", new Object[]{articleID,0,0,Article.STATUS_PUB_DONE}) ;
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件评论列表") ; //描述
		return obj ;
	}
	
	/** 测试 稿件热门评论列表 接口 */
	public JSONObject discussHotCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long articleID = param.getLong("articleID") ;
		
		String url = this.getParam().getString("ROOTURL") + "discussHot" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("type", 0) ;
		urlParam.put("count", 20) ;
		urlParam.put("id", articleID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj,json,"list",DocTypes.DISCUSS.typeID(), " a_parentID=0 AND a_articleID=? AND a_type=? AND a_sourceType=? AND a_status=?", new Object[]{articleID,0,0,Article.STATUS_PUB_DONE}) ;
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "稿件热门评论列表") ; //描述
		return obj ;
	}
	
	/** 测试 我的收藏列表 接口 */
	public JSONObject myFavCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "myFav" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("page", 0) ;
		urlParam.put("userID", userID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				JSONArray list = json.getJSONObject("result").getJSONArray("list") ;
				if(list.isEmpty()){
					DBSession conn = null;
					IResultSet rs = null;
					try{
						DocLib docLib = LibHelper.getLib(DocTypes.SUBSCRIBE.typeID(), Tenant.DEFAULTCODE);
						String subTableName = docLib.getDocLibTable();
						String articleTableName = LibHelper.getLibTable(LibHelper.getArticleAppLibID());
						String sql = "SELECT COUNT(*) FROM "+subTableName+" sub, "+articleTableName+" app  WHERE sub.SYS_AUTHORID=?  AND sub.sub_type in( ?,?) AND sub.sub_topicID=app.SYS_DOCUMENTID  AND sub.SYS_DELETEFLAG=0 AND app.SYS_DELETEFLAG=0" ;
						conn = Context.getDBSession(LibHelper.getArticleLibID());
						rs = conn.executeQuery(sql, new Object[]{userID,5,8});
						while (rs.next()) {
							obj.put("result", rs.getInt(1) > 0 ? 1:0) ; //正常
						}
					}catch(Exception e ){
						obj.put("result", 1) ;
					}
				}else{
					obj.put("result", 0) ; //正常
				}
			} catch (Exception e) {
				e.printStackTrace();
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "我的收藏列表") ; //描述
		return obj ;
	}
	
	/** 测试 我的评论列表 接口 */
	public JSONObject myDiscussCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "myDiscuss" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("userID", userID) ;
		urlParam.put("page", 0) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "list", DocTypes.DISCUSS.typeID(), "SYS_AUTHORID=? AND a_type=? AND a_sourceType in(0,1,3,5,6,7,8,9)", new Object[]{userID,0});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "我的评论列表") ; //描述
		return obj ;
	}
	
	/** 测试 栏目稿件列表 接口 */
	public JSONObject getArticlesCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long colID = param.getLong("colID") ;
		
		String url = this.getParam().getString("ROOTURL") + "getArticles" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("columnId", colID) ;
		urlParam.put("page", 0) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				JSONArray list = json.getJSONObject("result").getJSONArray("list") ;
				if(list.isEmpty()){
					DBSession conn = null;
					IResultSet rs = null;
					try {
						int colLibID = LibHelper.getColumnLibID();
						String tenantCode = LibHelper.getTenantCodeByLib(colLibID);
						List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),tenantCode);
						int artAppLibID = articleLibs.get(1).getDocLibID(); // App发布库，是第二个稿件库
						String tableName = InfoHelper.getRelTable(artAppLibID);// 取出栏目稿件关联表的表名
						String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE CLASS_1=? and a_status=? ORDER BY a_order";
						conn = Context.getDBSession();
						rs = conn.executeQuery(sql, new Object[]{colID, Article.STATUS_PUB_DONE});
						while (rs.next()) {
							obj.put("result", rs.getInt(1) > 0 ? 1 : 0) ;
						}
					} catch (Exception e) {
						obj.put("result", 1) ;
					}
				}else{
					obj.put("result", 0) ;
				}
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "栏目稿件列表") ; //描述
		return obj ;
	}
	
	/** 测试 栏目子栏目列表 接口 */
	public JSONObject getColumnsCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long colID = param.getLong("colID") ;
		
		String url = this.getParam().getString("ROOTURL") + "getColumns" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("parentColumnId", colID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "columns", DocTypes.COLUMN.typeID(), "col_parentID=? AND SYS_DELETEFLAG=0", new Object[]{colID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "栏目子栏目列表") ; //描述
		return obj ;
	}
	
	/** 测试 报纸列表 接口 */
	public JSONObject getPapersCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		int siteID = param.getInt("siteID") ;

		String url = this.getParam().getString("ROOTURL") + "getPapers" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("siteId", siteID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "papers", DocTypes.PAPER.typeID(), "pa_siteID=? and pa_status=0 and SYS_DELETEFLAG=0", new Object[]{siteID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "报纸列表") ; //描述
		return obj ;
	}
	
	/** 测试 活动列表 接口 */
	public JSONObject activityListCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		int siteID = param.getInt("siteID") ;
		
		String url = this.getParam().getString("ROOTURL") + "activityList" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("siteId", siteID) ;
		urlParam.put("page", 0) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "list", DocTypes.ACTIVITY.typeID(), "a_siteID =? and a_status=1", new Object[]{siteID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "活动列表") ; //描述
		return obj ;
	}
	
	/** 测试 直播报道列表 接口 */
	public JSONObject liveViewCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long liveID = param.getLong("liveID") ;
		
		String url = this.getParam().getString("ROOTURL") + "liveView" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("id", liveID) ;
		urlParam.put("la", 0) ;
		urlParam.put("lo", 0) ;
			
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "list", DocTypes.LIVEITEM.typeID(), "a_rootID=?", new Object[]{liveID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "直播报道列表") ; //描述
		return obj ;
	}
	
	/** 测试 活动详情 接口 */
	public JSONObject activityDetailCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long activeID = param.getLong("activeID") ;
		
		String url = this.getParam().getString("ROOTURL") + "activityDetail" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("fileId", activeID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getJSONObject("result").getLong("fileId") == activeID ? 0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "活动详情") ; //描述
		return obj ;
	}
	
	/** 测试 活动报名(订阅)接口*/
	public JSONObject saveActivityCheck(){
		JSONObject obj = new JSONObject() ;
		long activeID = param.getLong("activeID") ;
		long userID = param.getLong("userID") ;
		
		String url = this.getParam().getString("ROOTURL") + "saveActivity" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("fileId", activeID) ;
		urlParam.put("userID", userID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try {
				obj.put("result", json.getBoolean("result")?0:1) ;
			} catch (Exception e) {
				obj.put("result", 1) ; //异常
			}
		}
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "活动报名(订阅)") ; //描述
		return obj ;
	}
	
	/** 测试  数字报最近期次列表 接口 */
	public JSONObject getPaperDaCheckest(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long paperID = param.getLong("paperID") ;
		
		String url = this.getParam().getString("ROOTURL") + "getPaperDates?count=20&id="+paperID ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("count", 20) ;
		urlParam.put("id", paperID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "dates", DocTypes.PAPERDATE.typeID(), "pd_paperID=? and pd_status=1 and SYS_DELETEFLAG=0", new Object[] {paperID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "数字报最近期次列表") ; //描述
		return obj ;
	}
	
	/** 测试  数字报 一期报纸的版面列表 接口 */
	public JSONObject getPaperLayoutsCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long paperID = param.getLong("paperID") ;
		String paperDate = param.getString("paperDate") ;
		
		String url = this.getParam().getString("ROOTURL") + "getPaperLayouts" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("date", paperDate) ;
		urlParam.put("id", paperID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "layouts", DocTypes.PAPERLAYOUT.typeID(), "pl_date=? and pl_paperID=? and pl_status=1", new Object[] {DateUtils.parse(paperDate, "yyyyMMdd"),paperID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "数字报 一期报纸的版面列表") ; //描述
		return obj ;
	}
	
	/** 测试 数字报 一期报纸的稿件列表 接口 */
	public JSONObject getPaperArticlesCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long paperID = param.getLong("paperID") ;
		String paperDate = param.getString("paperDate") ;
		
		String url = this.getParam().getString("ROOTURL") + "getPaperArticles" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("date", paperDate) ;
		urlParam.put("id", paperID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		checkManager.handleListResult(obj, json, "", DocTypes.PAPERARTICLE.typeID(), "a_layoutID=? and a_status<7 and SYS_DELETEFLAG=0", new Object[] {paperID});
		
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "数字报一期报纸的稿件列表") ; //描述
		return obj ;
	}
	
	/** 测试 数字报 某篇稿件的详情 接口 */
	public JSONObject getPaperArticleCheck(){
		JSONObject param = this.getParam() ;
		JSONObject obj = new JSONObject() ;
		long paperArticleID = param.getLong("paperArticleID") ;
		
		String url = this.getParam().getString("ROOTURL") + "getPaperArticle" ;
		JSONObject urlParam = new JSONObject() ;
		urlParam.put("id", paperArticleID) ;
		
		JSONObject json = checkManager.executeHttpPostRequest(url,urlParam) ;
		if(json.getInt("status") == 200){
			try{
				obj.put("result",json.getJSONObject("result").getLong("fileId") == paperArticleID?0: 1) ;
			}catch(Exception e){
				obj.put("result", 1) ;
			}
		}
		obj.put("status", json.getInt("status")) ; //状态码
		obj.put("item", "数字报某篇稿件详情") ; //描述
		return obj ;
	}
	private JSONObject getParam(){
		return param ;
	}
	private void setParam(JSONObject param){
		this.param = param ;
	}
}
