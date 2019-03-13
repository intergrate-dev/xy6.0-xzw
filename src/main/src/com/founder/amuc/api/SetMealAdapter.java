package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONObject;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.ResourceMgr;
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
import com.founder.e5.commons.DomHelper;
import com.founder.e5.db.DBType;

@Controller
@RequestMapping("/api/setmeal")
public class SetMealAdapter{
	
	@RequestMapping("/FindSetMeal.do")
	public void FindSetMeal(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject map = new JSONObject();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			String dbType = DomHelper.getDBType();
			String sql = "";
			if (dbType.equals(DBType.ORACLE)) {
				sql = " Status = ? and m_siteID= ? and SYS_DELETEFLAG=0 and ((expiryDate = '---' AND TO_CHAR(sysdate,'YYYY-MM-DD') <= TO_CHAR(ExpireTime,'YYYY-MM-DD') ) or (expiryDate <> '---')) ";
			}else{
				sql = " Status = ? and m_siteID= ? and SYS_DELETEFLAG=0 and ((expiryDate = '---' AND date_format(now(),'%Y-%m-%d') <= ExpireTime ) or (expiryDate <> '---')) ";
			}
			Document[] docs = docManager.find(docLib.getDocLibID(), sql, new Object[] { "在售",siteID });
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Document document : docs) {
				Map<String, Object> doc = new HashMap<String, Object>();
				doc.put("docid", document.getDocID());
				doc.put("setMealNmae", document.getString("setMealNmae"));
				doc.put("paperNumber", document.getString("paperNumber"));
				doc.put("setMealMoney", document.getString("setMealMoney"));
				doc.put("EffectTime", document.getString("EffectTime"));
				doc.put("ExpireTime", document.getString("ExpireTime"));
				doc.put("expiryDate", document.getString("expiryDate"));
				doc.put("setMealContent", document.getString("setMealContent"));
				list.add(doc);
			}
			map.put("code", 1);
			map.put("data", list);
		} catch (Exception e) {
			map.put("code", 0);
			map.put("data", "错误");
		}		
		
		//output(map.toString(), response);
		outputJson(String.valueOf(map), response);
	}
	
    @RequestMapping("/FindSetMealByIds.do")
    public void FindSetMealByIds(HttpServletRequest request,
			HttpServletResponse response, @SuppressWarnings("rawtypes") Map model) throws Exception{
    	response.setHeader("Access-Control-Allow-Origin","*");
		response.setContentType("text/html;charset=UTF-8");
    	String ids = request.getParameter("ids");
		JSONObject map = new JSONObject();
		map.put("code", 0);
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERSETMEAL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			if (ids != null && ids.trim() != "") {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (String id : ids.split(",")) {
					Document document = docManager.get(docLib.getDocLibID(),
							Long.parseLong(id));
					if (document != null) {
						Map<String, Object> doc = new HashMap<String, Object>();
						doc.put("docid", document.getDocID());
						doc.put("setMealNmae",
								document.getString("setMealNmae"));
						doc.put("paperNumber",
								document.getString("paperNumber"));
						doc.put("setMealMoney",
								document.getString("setMealMoney"));
						doc.put("EffectTime", document.getString("EffectTime"));
						doc.put("ExpireTime", document.getString("ExpireTime"));
						doc.put("expiryDate", document.getString("expiryDate"));
						doc.put("setMealContent",
								document.getString("setMealContent"));
						list.add(doc);
					}
				}
				map.put("data", list);
			} else {
				map.put("code", 2);
				map.put("data", "ids值为空");
			}
		} catch (Exception e) {
			map.put("code", 1);
			map.put("data", "错误");
		}
		//output(map.toString(), response);
		outputJson(String.valueOf(map), response);
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
