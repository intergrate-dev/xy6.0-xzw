package com.founder.xy.paper.web;

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
 * 报纸树
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/paper")
public class PaperTreeController  {

	private String iconColRoot; //栏目树的图标
	private String iconCol;

	@RequestMapping(value = "Tree.do")
	public void tree(HttpServletRequest request, HttpServletResponse response) throws Exception {

		initIcon(request);
		
		String result = null;
		if (request.getParameter("siteID") != null) {
			//读报纸，第一层
			result = getPapers(request);
		} else if (request.getParameter("date") != null && !"undefined".equals(request.getParameter("date"))) {
			//读版面，第5层
			result = getPaperLayouts(request);
		} else if (request.getParameter("month") != null && !"undefined".equals(request.getParameter("month"))) {
			//读日，第四层
			result = getPaperDays(request);
		} else if (request.getParameter("year") != null && !"undefined".equals(request.getParameter("year"))) {
			//读月，第三层
			result = getPaperMonths(request);
		} else if (request.getParameter("paper") != null) {
			//读年、最近几日，第二层
			result = getPaperDates(request);
		}
		InfoHelper.outputJson(result, response);
	}

	private String getPapers(HttpServletRequest request) throws E5Exception {
		int paperLibID = LibHelper.getLibID(DocTypes.PAPER.typeID(), InfoHelper.getTenantCode(request));
		int siteID = WebUtil.getInt(request, "siteID", 0);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] papers = docManager.find(paperLibID, "pa_siteID=? and pa_status=0 and SYS_DELETEFLAG=0", 
				new Object[]{siteID});
		
		String result = jsonPaperTree(papers);
		
		return result;
	}

	private String getPaperDates(HttpServletRequest request) throws E5Exception {
		int paperID = WebUtil.getInt(request, "paper", 0);
		int isArticle = WebUtil.getInt(request, "isArticle", 0);
		
		DocLib pdLib = LibHelper.getLib(DocTypes.PAPERDATE.typeID(), InfoHelper.getTenantCode(request));
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		DBSession conn = Context.getDBSession(pdLib.getDsID());
		
		try {
			getRecentlyDates(paperID, isArticle, pdLib, result, conn);
			getYears(paperID, pdLib, result, conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		result.append("]");
		
		return result.toString();
	}

	//取最近几天的日期
	private void getRecentlyDates(int paperID, int isArticle, DocLib pdLib,
			StringBuilder result, DBSession conn) {
		IResultSet rs = null;
		String sql = "select pd_date from " + pdLib.getDocLibTable() 
				+ " where pd_paperID=? order by pd_date desc";
		try {
			sql = conn.getDialect().getLimitString(sql, 0, 3);
			rs = conn.executeQuery(sql, new Object[]{paperID});
			
			jsonDateTree(paperID, isArticle, rs, result);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
		}
	}

	private void getYears(int paperID, DocLib pdLib,
			StringBuilder result, DBSession conn) {
		IResultSet rs = null;
		String sql = "select distinct pd_year from " + pdLib.getDocLibTable() 
				+ " where pd_paperID=? order by pd_year desc";
		try {
			rs = conn.executeQuery(sql, new Object[]{paperID});
			
			jsonYearTree(paperID, rs, result);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
		}
	}

	private String getPaperMonths(HttpServletRequest request) throws E5Exception {
		int paperID = WebUtil.getInt(request, "paper", 0);
		int year = WebUtil.getInt(request, "year", 0);
		
		DocLib pdLib = LibHelper.getLib(DocTypes.PAPERDATE.typeID(), InfoHelper.getTenantCode(request));
		
		IResultSet rs = null;
		String sql = "select distinct pd_month from " + pdLib.getDocLibTable() 
				+ " where pd_paperID=? and pd_year=? order by pd_month";
		
		DBSession conn = Context.getDBSession(pdLib.getDsID());
		try {
			rs = conn.executeQuery(sql, new Object[]{paperID, year});
			
			return jsonMonthTree(paperID, year, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	private String getPaperDays(HttpServletRequest request) throws E5Exception {
		int paperID = WebUtil.getInt(request, "paper", 0);
		int year = WebUtil.getInt(request, "year", 0);
		int month = WebUtil.getInt(request, "month", 0);
		int isArticle = WebUtil.getInt(request, "isArticle", 0);
		
		int pdLibID = LibHelper.getLibID(DocTypes.PAPERDATE.typeID(), InfoHelper.getTenantCode(request));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] papers = docManager.find(pdLibID, "pd_paperID=? and pd_year=? and pd_month=? order by pd_day",
				new Object[]{paperID, year, month});
		
		return jsonDayTree(paperID, isArticle, papers);
	}

	private String getPaperLayouts(HttpServletRequest request) throws Exception {
		int paperID = WebUtil.getInt(request, "paper", 0);
		String date = WebUtil.get(request, "date");
		Date plDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		
		int plLibID = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), InfoHelper.getTenantCode(request));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] layouts = docManager.find(plLibID, "pl_paperID=? and pl_date=? order by pl_layout",
				new Object[]{paperID, plDate});
		
		return jsonLayoutTree(paperID, layouts);
	}

	private String jsonPaperTree(Document[] papers) throws E5Exception {
		String icon = iconColRoot;
		
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		for (Document paper : papers) {
			if (result.length() > 1)
				result.append(",");
			String name = InfoHelper.filter4Json(paper.getString("pa_name"));
			result.append("{\"paper\":\"").append(String.valueOf(paper.getDocID()))
					.append("\",\"name\":\"").append(name)
					.append("\",\"title\":\"").append(name + " [" + paper.getDocID() + "]")
					.append("\",\"isParent\":\"true")
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		result.append("]");

		return result.toString();
	}
	private void jsonDateTree(int paperID, int isArticle, IResultSet rs, StringBuilder result) throws SQLException {
		String icon = (isArticle == 0) ? iconCol : iconColRoot;
		String isParent = (isArticle == 0) ? "false" : "true";
		
		while (rs.next()) {
			if (result.length() > 1) result.append(",");
			String name = String.valueOf(rs.getDate("pd_date"));
			result.append("{\"date\":\"").append(name)
					.append("\",\"paper\":\"").append(paperID)
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
					.append("\",\"paper\":\"").append(paperID)
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
			result.append("{\"paper\":\"").append(paperID)
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
			result.append("{\"paper\":\"").append(paperID)
					.append("\",\"date\":\"").append(pd.getString("pd_date"))
					.append("\",\"name\":\"").append(name)
					.append("\",\"isParent\":\"").append(isParent)
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		result.append("]");

		return result.toString();
	}
	private String jsonLayoutTree(int paperID, Document[] pds) throws E5Exception {
		String icon = iconCol;
		
		StringBuilder result = new StringBuilder();
		result.append("[");

		for (Document pd : pds) {
			if (result.length() > 1) result.append(",");
			
			String name = pd.getString("pl_layout");
			result.append("{\"paper\":\"").append(paperID)
					.append("\",\"layout\":\"").append(pd.getDocID())
					.append("\",\"name\":\"").append(name)
					.append("\",\"icon\":\"").append(icon)
					.append("\"}");
		}
		result.append("]");

		return result.toString();
	}
	private void initIcon(HttpServletRequest request) {
		if (iconColRoot == null) {
			String root = WebUtil.getRoot(request);
			iconColRoot = root + "xy/img/home.png";
			iconCol = root + "xy/img/col.png";
		}
	}
}
