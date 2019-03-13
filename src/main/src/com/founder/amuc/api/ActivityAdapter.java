package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.SysUser;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.amuc.commons.DateFormatAmend;
@Controller
@RequestMapping("/api/activity")
public class ActivityAdapter{
		
	/**
	 * 获取收藏文章id接口
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/getActivityId.do")
	public void getCollectId(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "用户id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String sql="select * from xy_memberActivity where aUserId = " + userId;
		DBSession dbSession = null;
		IResultSet rs = null;
		JSONObject collectObj = new JSONObject();
		JSONArray collectArray = new JSONArray();
		int i = 0;
			
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){
				collectObj.put("activityId", rs.getInt("aActivityId"));
				collectObj.put("activityName", rs.getString("aActivityName"));
				collectObj.put("createdTime", rs.getString("aCreated"));
				collectArray.add(i, collectObj);
				i ++;
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		
		if(collectArray.size() > 0){
			maps.put("code", "0001");
			maps.put("Array", collectArray);
			maps.put("msg", "获取成功");
		}else{
			maps.put("code", "0002");
			maps.put("msg", "获取失败");
		}
		
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
	}
	/**
	 * 获取收藏文章ids接口
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/getActivityIds.do")
	public void getCollectIds(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "用户id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String sql="select * from xy_memberActivity where aUserId = " + userId;
		DBSession dbSession = null;
		IResultSet rs = null;
		String ActivityIds = "";
			
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){
				ActivityIds = ActivityIds + rs.getInt("aActivityId") + ",";
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		
		if(ActivityIds != ""){
			maps.put("code", "0001");
			maps.put("articleIds", ActivityIds.substring(0, ActivityIds.length()-1));
			maps.put("msg", "获取成功");
		}else{
			maps.put("code", "0002");
			maps.put("msg", "获取失败");
		}
		
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
	}
	
	/**
	 * 收藏接口
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/getActivity.do")
	public void getCollect(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		String activityId = request.getParameter("activityId");
		String activityName = request.getParameter("activityName");
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "用户id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if(StringUtils.isBlank(activityId)){
			maps.put("code", "0001");
			maps.put("msg", "文章id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if(StringUtils.isBlank(activityName)){
			maps.put("code", "0002");
			maps.put("msg", "文章名字不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//DocLib amLib = InfoHelper.getLib(Constant.DOCTYPE_COLLECTION);
		DocLib amLib = LibHelper.getLib(DocTypes.MEMBERACTIVITY.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document amDoc = docManager.newDocument(amLib.getDocLibID(), InfoHelper.getID(amLib.getDocTypeID()));
		amDoc.setFolderID(amLib.getFolderID());
		amDoc.setDeleteFlag(0);
		//amDoc.setLocked(false);
		amDoc.set("aUserId", userId);
		amDoc.set("aActivityId", activityId);
		amDoc.set("aActivityName", activityName);
		amDoc.set("aCreated", DateFormatAmend.timeStampDispose(time.format(new Date())));
		docManager.save(amDoc);
		maps.put("code", "0003");
		maps.put("msg", "收藏成功");
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
		
	}
	/**
	 * 删除接口
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/delActivity.do")
	public void delCollect(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, Object> maps = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		String activityId = request.getParameter("activityId");
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "用户id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if(StringUtils.isBlank(activityId)){
			maps.put("code", "0001");
			maps.put("msg", "文章id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String sql="delete from xy_memberActivity where aUserId=" + userId + " and aActivityId=" + activityId;
		DBSession dbSession = null;
		IResultSet rs = null;
		int isSuccess = 0;
		
		try {
			dbSession = Context.getDBSession();
			dbSession.beginTransaction();
			isSuccess = dbSession.executeUpdate(sql, null);
			dbSession.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		if(isSuccess > 0){
			maps.put("code", "0002");
			maps.put("msg", "取消成功");
			
		}else{
			maps.put("code", "0003");
			maps.put("msg", "取消失败");
		}
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
	}
	
	/**
	 * 判断当篇稿件是否已收藏的接口
	 * @param 
	 * @return
	 * @throws E5Exception 
	 */
	@RequestMapping("/checkActivity.do")
	public void checkCollection(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception {
		
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		String activityId = request.getParameter("activityId");
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "用户id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if(StringUtils.isBlank(activityId)){
			maps.put("code", "0001");
			maps.put("msg", "文章id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String sql="select * from xy_memberActivity where aUserId = " + userId + " and aActivityId = " + activityId;
		DBSession dbSession = null;
		IResultSet rs = null;
	
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			if(rs.next()){
				maps.put("code", "0002");
				maps.put("msg", "已收藏");
			}else{
				maps.put("code", "0003");
				maps.put("msg", "未收藏");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}	
		
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
	}
	
	 /** 向response输出json数据 */
	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null) return;
		
		response.setContentType("application/json; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(out);
		}
	}
}
