package com.founder.amuc.column;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;

import com.founder.amuc.commons.AttachHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.FormHelper;
import com.founder.amuc.commons.FormViewerHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.MemberHelper;
import com.founder.amuc.member.MemberReader;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.db.LaterDataTransferException;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.FolderReader;
import com.founder.e5.sys.SysConfig;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

/**
 * 栏目相关操作
 * @author lijingzhou
 * 2016-7-18
 */
@Controller
@RequestMapping("/amuc/column")
public class ColumnsController extends BaseController{

	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "action");
		
		if ("save".equals(action)) {
			
			save(request, response, model);
		} else if ("getChecked".equals(action)) {
			
			getChecked(request, response, model);
		} else {
			// 栏目设置界面初始化
			columnInit(request, response, model);
		}
	}
	
	/**
	 * 栏目设置界面初始化
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/columnInit.do")
	private void columnInit(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		int siteID = Integer.parseInt(request.getParameter("siteID"));
	
		String sql="select * from xy_column where SYS_DELETEFLAG = 0 and col_siteID = " + siteID;
		DBSession dbSession = null;
		IResultSet rs = null;
		PrintWriter out = response.getWriter();
		JSONObject colsObj = new JSONObject();
		JSONArray colsArray = new JSONArray();
		int i = 0;
			
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){
				colsObj.put("id", rs.getInt("SYS_DOCUMENTID"));
				colsObj.put("pId", rs.getInt("col_parentID"));
				colsObj.put("name", rs.getString("col_name"));
				colsArray.add(i, colsObj);
				i ++;
			}
			
		} catch (E5Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}
		out.print(colsArray.toString());
		
		/*model.put("colsArray", colsArray);
		model.put("UUID", get(request, "UUID"));		
		model.put("@VIEWNAME@", "amuc/column/AddColumn");*///跳转至.jsp页面
	}
	
	/**
	 * 栏目设置
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/save.do")
	private void save(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		request.setCharacterEncoding("UTF-8");
		int docID = getInt(request, "DocIDs", 0);
		String colsName = request.getParameter("columnName");
		String colsId = request.getParameter("columnId");
		String colspId = request.getParameter("columnpId");
		
		colsName = URLDecoder.decode(colsName, "UTF-8"); 
		colsId = URLDecoder.decode(colsId, "UTF-8"); 
		colspId = URLDecoder.decode(colspId, "UTF-8"); 
		colsName = colsName.substring(0,colsName.length()-1);
		String colsIds = colsId.substring(0,colsId.length()-1);
		String colspIds = colspId.substring(0,colspId.length()-1);
		String[] colsIdss = colsIds.split(",");
		String[] colspIdss = colspIds.split(",");
		if(docID > 0){
			JSONObject colsObj = new JSONObject();
			JSONArray colsArray = new JSONArray();

			for(int i=0;i<colsIdss.length;i++){
				colsObj.put("id", colsIdss[i]);
				if(colspIdss[i] != null  && colspIdss[i].trim().length()!=0 && !"null".equalsIgnoreCase(colspIdss[i])){
					//colspIdss[i].trim().length()!=0
					colsObj.put("pId", colspIdss[i]);
				}else{
					
					colsObj.put("pId", "0");
				}
				
				colsArray.add(i, colsObj);
			}
			String cols = colsArray.toString();
			String sql="update xy_memberLevel set lColumnName = '" + colsName + "' , lColumnId = '" + colsArray + "' where SYS_DOCUMENTID = " + docID;
			DBSession dbSession = null;
			IResultSet rs = null;
			try {
				dbSession = Context.getDBSession();
				dbSession.beginTransaction();
				dbSession.executeUpdate(sql, null);
				dbSession.commitTransaction();
			} catch (E5Exception e) {
				e.printStackTrace();
			}finally{
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(dbSession);
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/getChecked.do")
	private void getChecked(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String docID = request.getParameter("DocIDs");

		String sql="select * from xy_memberLevel where SYS_DELETEFLAG = 0 and SYS_DOCUMENTID = " + docID;
		DBSession dbSession = null;
		IResultSet rs = null;
		PrintWriter out = response.getWriter();
		String checkedColsIds = "";
		String checkedColsId ="";
			
		try {
			dbSession = Context.getDBSession();
			rs = dbSession.executeQuery(sql);
			while(rs.next()){
				checkedColsIds = rs.getString("lColumnId");			
			}
			JSONArray jsoncols = JSONArray.fromObject(checkedColsIds); 
			if(jsoncols.size()>0){
			  for(int i=0;i<jsoncols.size();i++){
			    JSONObject job = jsoncols.getJSONObject(i);  
			    checkedColsId += job.get("id")+","; 
			  }
			  String colspIds = checkedColsId.substring(0,checkedColsId.length()-1);
			  out.print(colspIds);
			//model.put("column", column);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(dbSession);
		}	
	}
	
	@RequestMapping("/del.do")
	private void deleteLevel(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		String DocIDs = get(request, "DocIDs");
		if(DocIDs == null || DocIDs.length() == 0) return;
		
		DocLib docLib = LibHelper.getLib(DocTypes.MEMBERLEVEL.typeID(),"xy");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] doc = docManager.find(docLib.getDocLibID()," SYS_DOCUMENTID in(" 
    	        + DocIDs + ") and SYS_DELETEFLAG=0 ",new Object[] {});
		if(doc != null && doc.length > 0){
			for(int i=0;i<doc.length;i++){
				//System.out.println(doc[i].getString("SYS_DELETEFLAG"));
				doc[i].set("SYS_DELETEFLAG", 1);
				docManager.save(doc[i]);
			}
			String url = "../../e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + DocIDs;
			request.getRequestDispatcher(url).forward(request, response);
		}
	}
}
