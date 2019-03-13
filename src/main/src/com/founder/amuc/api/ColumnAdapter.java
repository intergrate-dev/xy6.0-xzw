package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.collection.CollectHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.DateUtil;
import com.founder.amuc.commons.FormViewerHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.RedisToolUtil;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.flow.FlowNode;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

/** 
 * @created 2016年7月18日 
 * @author  lijingzhou
 * 类说明 ： 栏目设置接口
 */
@Controller
@RequestMapping("/api/column")
public class ColumnAdapter {
	
	/**
	 * 获取栏目id
	 */
	@RequestMapping("/getColsId.do")
	public void getColsId(HttpServletRequest request, HttpServletResponse response, Map model) throws E5Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "会员id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return ;
		}
		String result = getColsId(userId,siteID);
		outputJson(String.valueOf(result), response);
	}
	
	@RequestMapping("/getColumnId.do")
	public void getColumnId(HttpServletRequest request, HttpServletResponse response, Map model) throws E5Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		String userId = request.getParameter("userId");
		String pId = request.getParameter("pId");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		Map<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isBlank(userId)){
			maps.put("code", "0000");
			maps.put("msg", "会员id不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return ;
		}
		if(StringUtils.isBlank(pId)){
			maps.put("code", "0001");
			maps.put("msg", "pId不能为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return ;
		}
		String result = getColsId(userId, pId, siteID);
		outputJson(String.valueOf(result), response);
		return ;
	}
	
	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null)
			return;

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
	
	/**
	 * 获取栏目id
	 * @param userId
	 * @return
	 * @throws E5Exception 
	 */
	public String getColsId(String userId, int siteID) throws E5Exception {
		
		Map<String, Object> maps = new HashMap<String, Object>();
		String levelId = "";
		
		if("-1".equals(userId) || "0".equals(userId)){
			levelId = "0";
		}else{
			levelId = getLevelId(userId);
		}
		System.out.println("---------------userId="+userId+"-----------levelId="+levelId);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERLEVEL.typeID(), "xy");
		String [] columnsId = {"lColumnId"};
		Document[] colsId = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and lLevel = ? and m_siteID = ?", new Object[]{levelId,siteID}, columnsId);
		if(colsId != null && colsId.length > 0){
			String lColumnId = colsId[0].getString("lColumnId");
			//lColumnId = lColumnId.replaceAll("\","");
			JSONArray colsArray = JSONArray.fromObject(lColumnId);
			if(colsArray.size() > 0){
				maps.put("code", "0001");
				maps.put("ColumnId", colsArray);
				maps.put("msg", "栏目id获取成功");
			}else{
				maps.put("code", "0002");
				maps.put("ColumnId", "");
				maps.put("msg", "栏目id获取失败");
			}
		}else{
			maps.put("code", "0004");
			maps.put("msg", "不存在该会员");
		}
		JSONObject result = JSONObject.fromObject(maps);
		return result.toString();
	}
	
	public String getColsId(String userId, String pId, int siteID) throws E5Exception {
			
			Map<String, Object> maps = new HashMap<String, Object>();
			String levelId = "";
			
			if("-1".equals(userId) || "0".equals(userId)){
				levelId = "0";
			}else{
				levelId = getLevelId(userId);
			}
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			DocLib docLib = LibHelper.getLib(DocTypes.MEMBERLEVEL.typeID(), "xy");
			String [] columnsId = {"lColumnId"};
			Document[] colsId = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and lLevel = ? and m_siteID = ?", new Object[]{levelId,siteID}, columnsId);
			if(colsId != null && colsId.length > 0){
				String lColumnId = colsId[0].getString("lColumnId");
				String colIds = "";
				if(!StringUtils.isBlank(lColumnId)){
					JSONArray colsArray = JSONArray.fromObject(lColumnId);
					for(int i=0;i<colsArray.size();i++){
						JSONObject job = colsArray.getJSONObject(i);
						if(pId.equals(job.get("pId"))){
							colIds += job.get("id")+","; 
						}
					}
					if(!StringUtils.isBlank(colIds)){
					    String colspIds = colIds.substring(0,colIds.length()-1);
									  
					    maps.put("code", "0002");
						maps.put("ColumnId", colspIds);
						maps.put("msg", "栏目id获取成功");
					}else{
						maps.put("code", "0003");
						maps.put("msg", "栏目id获取失败");
					}
				}else{
					maps.put("code", "0003");
					maps.put("msg", "栏目id获取失败");
				}
			}else{
				maps.put("code", "0004");
				maps.put("msg", "不存在该会员");
			}
			JSONObject result = JSONObject.fromObject(maps);
			return result.toString();
		}
	
	private String getLevelId(String userId) throws E5Exception {
		
		Map<String, Object> maps = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBER.typeID(), "xy");
		String [] levelId = {"mLevel"};
		Document[] lId = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{userId}, levelId);
		if(lId != null && lId.length > 0){
			String levelID = lId[0].getString("mLevel");	
			return levelID;
		}
	
		maps.put("code", "0004");
		maps.put("msg", "不存在该会员");		
		JSONObject jsonstr = JSONObject.fromObject(maps);
		
		return jsonstr.toString();
	}
}