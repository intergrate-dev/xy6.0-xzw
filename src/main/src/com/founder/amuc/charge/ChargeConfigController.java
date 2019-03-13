package com.founder.amuc.charge;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.founder.amuc.commons.DateFormatAmend;
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
@RequestMapping("/amuc/charge")
public class ChargeConfigController extends BaseController {
	
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "a");

		if (action.equals("getConfigList")) {
			getConfigList(request, response, model);

		}
	}
	
	@RequestMapping("/getConfigList.do")
	public void getConfigList(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception{
		int siteID = Integer.parseInt(request.getParameter("siteID"));
		JSONObject map = new JSONObject();
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAYCONFIG.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			String dbType = DomHelper.getDBType();
			String sql = "";
			sql = " m_siteID= ? and SYS_DELETEFLAG=0 ";
			Document[] docs = docManager.find(docLib.getDocLibID(),	sql, new Object[] {siteID});
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Document document : docs) {
				Map<String, Object> doc = new HashMap<String, Object>();
				doc.put("SYS_DOCUMENTID", document.getDocID());
				doc.put("payWay", document.getString("payWay"));
				doc.put("configuration", document.getString("configuration"));
				doc.put("parameter", document.getString("parameter"));
				doc.put("Description", document.getString("Description"));
				doc.put("m_siteID", document.getString("m_siteID"));
				list.add(doc);
			}
			map.put("code", 1);
			map.put("data", list);
		} catch (Exception e) {
			map.put("code", 0);
			map.put("data", "错误");
		}		
		
		//return map.toString();	
		output(map.toString(), response);
	}
	
	@RequestMapping("/CreateConfig.do")
	private void CreateConfig(HttpServletRequest request,
			HttpServletResponse response,
			 Map model) throws Exception {

		String payWay = request.getParameter("payWay");
		String configuration = request.getParameter("configuration");
		String parameter = request.getParameter("parameter");
		String Description = request.getParameter("Description");
		int siteID = Integer.parseInt(request.getParameter("siteID"));

		int docTypeID = DocTypes.MEMBERPAYCONFIG.typeID();
		DocLib docLib = LibHelper.getLib(docTypeID, "xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID(),InfoHelper.getID(docLib.getDocTypeID()));
		doc.set("payWay", payWay);
		doc.set("configuration", configuration);
		doc.set("parameter", parameter);
		doc.set("Description", Description);
		doc.set("m_siteID", siteID);
		try {
			docManager.save(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/UpdateConfig.do")
	private void UpdateConfig(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {

		long DocID = Long.parseLong(request.getParameter("DocID"));
		String parameter = request.getParameter("parameter");
		String Description = request.getParameter("Description");

		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAYCONFIG.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sql = " SYS_DOCUMENTID= ? and SYS_DELETEFLAG=0 ";
		Document[] doc = docManager.find(docLib.getDocLibID(),	sql, new Object[] {DocID});
		doc[0].set("parameter", parameter);
		doc[0].set("Description", Description);
		try {
			docManager.save(doc[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getConfig(String payway, String configuration){
		try {
			DocLib docLib = LibHelper.getLib(DocTypes.MEMBERPAYCONFIG.typeID(),"xy");
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			String sql = " payWay= ? and configuration= ? and SYS_DELETEFLAG=0 ";
			Document[] doc = docManager.find(docLib.getDocLibID(),	sql, new Object[] {payway,configuration});
			if(doc != null && doc.length > 0){
				return doc[0].getString("parameter");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
