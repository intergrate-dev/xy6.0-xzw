package com.founder.xy.magazine.web;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 期刊树
 * @author binLee
 */
@Controller
@RequestMapping("/xy/magazine")
public class MagazineTreeController {

	private String iconColRoot ; // 栏目树的图标
	private String iconCol ;
	
	@RequestMapping("/Tree.do")
	public void tree(HttpServletRequest request, HttpServletResponse response) throws Exception{
		initIcon(request) ;
		
		String result = null;
		if (request.getParameter("siteID") != null){
			// 读取期刊，第一层
			result = getMagazines(request);
		} else if (request.getParameter("date") !=null && !"undefined".equals(request.getParameter("date"))){
			// 读取栏目，第五层
			result = getMagsColumns(request);
		} else if (request.getParameter("month") != null && !"undefined".equals(request.getParameter("month"))) {
			// 读日，第四层
			result = getMagsDays(request);
		} else if (request.getParameter("year") != null && !"undefined".equals(request.getParameter("year"))) {
			// 读月，第三层
			result = getMagsMonths(request);
		} else if (request.getParameter("magazine") != null) {
			// 读年、最近几日，第二层
			result = getMagazineDates(request);
		}
		InfoHelper.outputJson(result, response);
		
	}
	
	private String getMagazines(HttpServletRequest request) throws E5Exception{
		int magsLibID = LibHelper.getLibID(DocTypes.MAGAZINE.typeID(), InfoHelper.getTenantCode(request));
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] magazines = docManager.find(magsLibID,
				"pa_siteID=? and pa_status=0 and SYS_DELETEFLAG=0 order by pa_order", 
				new Object[]{siteID});
		String result = jsonMagsTree(magazines);
		
		return result ;
	} 
	
	private String getMagazineDates(HttpServletRequest request) throws E5Exception{
		int magsID = WebUtil.getInt(request, "magazine", 0);
		int isArticle = WebUtil.getInt(request, "isArticle", 0);
		
		DocLib mdLib = LibHelper.getLib(DocTypes.MAGAZINEDATE.typeID(),InfoHelper.getTenantCode(request));
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		DBSession conn = Context.getDBSession(mdLib.getDsID());
		
		try {
			// 获取最近刊期
			getRecentlyDates(magsID, isArticle, mdLib, result, conn );
			// 获取期刊的年份
			getYears(magsID, mdLib, result, conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		result.append("]");
		
		return result.toString();
	}
	
	// 获取最近几天的日期
	private void getRecentlyDates(int magsID, int isArticle, DocLib mdLib,
			StringBuilder result, DBSession conn){
		IResultSet rs = null;
		String sql = "select pd_date from " + mdLib.getDocLibTable()
				+ " where pd_paperID=? order by pd_date desc";
		
		try {
			sql = conn.getDialect().getLimitString(sql, 0, 3);
			rs = conn.executeQuery(sql, new Object[]{magsID});
			
			jsonDateTree(magsID, isArticle , rs, result);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
		}
		
	}
	
	// 获取年的信息
	private void getYears(int magsID, DocLib mdLib, StringBuilder result,DBSession conn){
		IResultSet rs = null;
		String sql = "select distinct pd_year from " + mdLib.getDocLibTable() 
				+ " where pd_paperID=? order by pd_year desc";
		try {
			rs = conn.executeQuery(sql, new Object[]{magsID});
			
			jsonYearTree(magsID, rs, result);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
		}
	}
	
	// 获取月的信息
	private String getMagsMonths(HttpServletRequest request) throws E5Exception{
		int magsID = WebUtil.getInt(request, "magazine", 0);
		int year = WebUtil.getInt(request, "year", 0);
		
		DocLib mdLib = LibHelper.getLib(DocTypes.MAGAZINEDATE.typeID(),InfoHelper.getTenantCode(request));
		
		IResultSet rs = null;
		String sql = "select distinct pd_month from " + mdLib.getDocLibTable()
				+ " where pd_paperID=? and pd_year=? order by pd_month";
		
		DBSession conn = Context.getDBSession(mdLib.getDsID());
		
		try {
			rs = conn.executeQuery(sql, new Object[]{magsID,year});
			return jsonMonthTree(magsID, year, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	// 获取天的信息
	private String getMagsDays(HttpServletRequest request) throws E5Exception{
		int magsID = WebUtil.getInt(request, "magazine", 0);
		int year = WebUtil.getInt(request, "year", 0);
		int month = WebUtil.getInt(request, "month", 0);
		int isArticle = WebUtil.getInt(request, "isArticle", 0);
		
		int mdLibID = LibHelper.getLibID(DocTypes.MAGAZINEDATE.typeID(), InfoHelper.getTenantCode(request));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] magazines = docManager.find(mdLibID, "pd_paperID=? and pd_year=? and pd_month=? order by pd_day",
				new Object[]{magsID, year, month});
		
		return jsonDayTree(magsID, isArticle, magazines);
	}
	
	// 获取期刊栏目的信息
	private String getMagsColumns(HttpServletRequest request) throws Exception{
		int magsID = WebUtil.getInt(request, "magazine", 0);
		String date = WebUtil.get(request, "date");
		Date mcDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		
		int mcLibID = LibHelper.getLibID(DocTypes.MAGAZINECOLUMN.typeID(), 
				InfoHelper.getTenantCode(request));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] columns = docManager.find(mcLibID, "pl_magID=? and pl_date=? order by SYS_DOCUMENTID", 
				new Object[]{magsID, mcDate});
		
		return jsonLColumnTree(magsID, columns);
	}
	
	private void jsonDateTree(int paperID, int isArticle, IResultSet rs, StringBuilder result) throws SQLException {
		String icon = (isArticle == 0) ? iconCol : iconColRoot;
		String isParent = (isArticle == 0) ? "false" : "true";
		
		while (rs.next()) {
			if (result.length() > 1) result.append(",");
			String name = String.valueOf(rs.getDate("pd_date"));
			result.append("{\"date\":\"").append(name)
					.append("\",\"magazine\":\"").append(paperID)
					.append("\",\"name\":\"").append(name)
					.append("\",\"title\":\"").append(name)
					.append("\",\"isParent\":\"").append(isParent)
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
	}
	
	private void jsonYearTree(int paperID, IResultSet rs, StringBuilder result) throws SQLException {
		String icon = iconColRoot;
		while (rs.next()) {
			if (result.length() > 1) result.append(",");
			String name = String.valueOf(rs.getInt(1));
			result.append("{\"year\":\"").append(name)
					.append("\",\"magazine\":\"").append(paperID)
					.append("\",\"name\":\"").append(name)
					.append("\",\"title\":\"").append(name)
					.append("\",\"isParent\":\"true")
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
	}
	
	private String jsonMonthTree(int paperID, int year, IResultSet rs) throws SQLException {
		String icon = iconColRoot;
		
		StringBuilder result = new StringBuilder();
		result.append("[");

		while (rs.next()) {
			if (result.length() > 1) result.append(",");
			
			String name = String.valueOf(rs.getInt(1));
			result.append("{\"magazine\":\"").append(paperID)
					.append("\",\"year\":\"").append(year)
					.append("\",\"month\":\"").append(name)
					.append("\",\"name\":\"").append(name)
					.append("\",\"isParent\":\"true")
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		
		result.append("]");

		return result.toString();
	}
	
	private String jsonDayTree(int paperID, int isArticle, Document[] pds) throws E5Exception {
		StringBuilder result = new StringBuilder();
		result.append("[");

		String icon = (isArticle == 0) ? iconCol : iconColRoot;
		String isParent = (isArticle == 0) ? "false" : "true";
		for (Document pd : pds) {
			
			if (result.length() > 1) result.append(",");
			
			String name = pd.getString("pd_day");
			result.append("{\"magazine\":\"").append(paperID)
					.append("\",\"date\":\"").append(pd.getString("pd_date"))
					.append("\",\"name\":\"").append(name)
					.append("\",\"isParent\":\"").append(isParent)
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		result.append("]");

		return result.toString();
	}
	
	private String jsonMagsTree(Document[] Magazines) throws E5Exception{
		String icon = iconColRoot ;
		
		StringBuilder result = new StringBuilder() ;
		result.append("[");
		for(Document magazine : Magazines){
			if(result.length() > 1)
				result.append(",") ;
			String name = InfoHelper.filter4Json(magazine.getString("pa_name"));
			result.append("{\"magazine\":\"").append(String.valueOf(magazine.getDocID()))
				  .append("\",\"name\":\"").append(name)
				  .append("\",\"title\":\"").append(name + " [" + magazine.getDocID() + "]")
				  .append("\",\"isParent\":\"true")
				  .append("\",\"icon\":\"").append(icon)
				  .append("\"}");
		}
		result.append("]");
		
		return result.toString();
	}
	
	private String jsonLColumnTree(int magsID, Document[] mds) throws E5Exception {
		String icon = iconCol;
		
		StringBuilder result = new StringBuilder();
		result.append("[");

		for (Document md : mds) {
			if (result.length() > 1) result.append(",");
			
			String name = md.getString("pl_name");
			result.append("{\"magazine\":\"").append(magsID)
					.append("\",\"layout\":\"").append(md.getDocID())
					.append("\",\"name\":\"").append(name)
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		result.append("]");

		return result.toString();
	}
	
	private void initIcon(HttpServletRequest request){
		if(iconColRoot == null){
			String root = WebUtil.getRoot(request);
			iconColRoot = root + "xy/img/home.png" ;
			iconCol = root + "xy/img/col.png" ;
		}
	}
}
