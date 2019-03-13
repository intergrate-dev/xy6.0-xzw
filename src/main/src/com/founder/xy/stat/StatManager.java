package com.founder.xy.stat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
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
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.sys.org.Org;
import com.founder.e5.sys.org.OrgReader;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;

/**
 * 我的工作量的管理器
 */
@Component
public class StatManager {
	
	@Autowired
	private ColumnReader colReader;
	/**
	 * 根据登陆用户ID获得我的工作量页面初始化
	 * @param date 
	 * @param cal 
	 */
	public Map<String, Object> myloadInit(int usrSysId, int siteID, String tenantCode) throws Exception {
		// 稿件种类
		Pair[] articleTypes = getArticleTypes();
		// 4月,5月,6月,7月,8月,9月,10月,11月,12月,1月,2月,3月
		String serialMonth = getMonthDescription();
		
		List<DocLib> list = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		
		String webTable = LibHelper.getLibTable(list.get(0).getDocLibID());
		String appTable = LibHelper.getLibTable(list.get(1).getDocLibID());
		
		String[] serialMonthArr = serialMonth.replace("月", "").split(",");
		
		Channel[] chs = ConfigReader.getChannels();
		Map<String, Object> map = new HashMap<String, Object>();
		if (chs[0] != null) {
			Map<String, String> thisMonthDatas = getStatByMonth(usrSysId, siteID, webTable);// 本月工作量总计（饼状图）
			Map<String, String> yearMap = getStatByYear(usrSysId, siteID, webTable);// 各月工作量统计（线状图）
			Map<String, Object> detailMap = getDetailStatList(usrSysId, siteID, webTable);// 工作量分条件细览（列表）
			
			map.put("monthMap", thisMonthDatas);
			map.put("detailMapList", detailMap.get("list"));
			map.put("monthlyDatas", monthlyDatas(articleTypes, serialMonthArr, yearMap)); // 按稿件种类分别统计
		}
		if (chs[1] != null) {
			Map<String, String> monthMap_App = getStatByMonth(usrSysId, siteID, appTable);// 本月工作量总计（饼状图）
			Map<String, String> yearMap_App = getStatByYear(usrSysId, siteID, appTable);// 各月工作量统计（线状图）
			Map<String, Object> detailMap_App = getDetailStatList(usrSysId, siteID, appTable);// 工作量分条件细览（列表）

			map.put("monthMap_App", monthMap_App);
			map.put("detailMapList_App", detailMap_App.get("list"));
			map.put("monthlyDatas_App", monthlyDatas(articleTypes, serialMonthArr, yearMap_App)); // 按稿件种类分别统计
		}
		
		map.put("articleTypes", articleTypes);
		map.put("serialMonth", serialMonth); // 月份X轴
		map.put("channels", chs);
		map.put("siteID", siteID); // 所属站点（搜索栏目用）
		
		return map;
	}
	
	/**
	 * 根据登陆用户ID获得我的工作量明细页面
	 */
	public Map<String, Object> myloadSearch(HttpServletRequest request) throws Exception {
		
		// 工作量分条件细览（列表）
		Map<String, Object> detailMap = getDetailStatList(request);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("detailMapList", detailMap.get("list"));
		map.put("count", detailMap.get("count"));
		map.put("discuss", detailMap.get("discuss"));
		map.put("click", detailMap.get("click"));
		
		return map;
	}
	
	/**
	 * 工作量统计初始化
	 */
	public Map<String, Object> workloadInit(int siteID, String tenantCode) throws Exception {
		// 获得所有用户列表
		Map<Long, String> userList = getSiteUsers(siteID);
		
		//默认查的是上个月的范围
		Timestamp[] dateRange = getDateRangeDefault();

		List<DocLib> docLibList = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		String _article = LibHelper.getLibTable(docLibList.get(0).getDocLibID());
		String _articleapp = LibHelper.getLibTable(docLibList.get(1).getDocLibID());
		
		List<StatData> detailMap = wlStatList(userList, siteID, _article, dateRange[0], dateRange[1]);
		List<StatData> detailMap_App = wlStatList(userList, siteID, _articleapp, dateRange[0], dateRange[1]);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("usrList", userList);
		rtnMap.put("detailMap", detailMap);
		rtnMap.put("detailMap_App", detailMap_App);
		
		return rtnMap;
	}
	
	/**
	 * 工作量统计页面检索
	 */
	public Map<String, Object> workloadSearch(HttpServletRequest request) throws Exception {
		// 获得所有用户列表
		int siteID = WebUtil.getInt(request, "siteID", 0);
		Map<Long, String> userList = getSiteUsers(siteID);
		
		// 工作量分条件细览（列表）
		List<StatData> detailMap = wlStatList(userList, request);

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("detailMap", detailMap);
		rtnMap.put("usrList", userList);
		
		return rtnMap;
	}
	
	/**
	 * 稿件统计初始化
	 */
	public Map<String, Object> articleInit(String siteID, String tenantCode) throws Exception {
		
		//默认查的是上个月的范围
		Timestamp[] dateRange = getDateRangeDefault();
		
		String libTable = getStatTableDefault(tenantCode);
		
		// 部门统计
		List<StatData> deptStatList = deptStatList(libTable, siteID, dateRange[0], dateRange[1]);
		// 来源统计
		List<StatData> srcStatList = srcStatList(libTable, siteID, dateRange[0], dateRange[1]);
		// 栏目统计无初期化
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deptStatList", deptStatList);
		map.put("srcStatList", srcStatList);
		
		return map;
	}
	
	/**
	 * 发稿量统计初始化
	 */
	public Map<String, Object> articleBatmanInit(String siteID, String tenantCode) throws Exception {
		
		//默认查的是上个月的范围
		Timestamp[] dateRange = getDateRangeDefault();
		
		String libTable = getStatTableDefault(tenantCode);
		
		//发稿量统计
		List<StatData> statList = authorsStatList(libTable, siteID, dateRange[0], dateRange[1]);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statList", statList);
		
		return map;
	}
	/**
	 * 发稿量统计页面检索
	 */
	public Map<String, Object> articleBatmanSearch(HttpServletRequest request) throws Exception {
		List<StatData> statList = null;
		statList = authorsStatList(request);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statList", statList);
		
		return map;
	}
	/**
	 * 稿件统计页面检索
	 */
	public Map<String, Object> articleSearch(HttpServletRequest request) throws Exception {
		List<StatData> statList = null;
		
		String tab = request.getParameter("tab");
		if ("".equals(tab)){ // 部门统计
			statList = deptStatList(request);
		} else if("_src".equals(tab)){ // 来源统计
			statList = srcStatList(request);
		} else { // 栏目统计
			statList = colStatList(request);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("statList", statList);
		
		return map;
	}
	
	/**
	 * 栏目订阅统计初始化
	 */
	public Map<String, Object> colStatInit(String tenantCode) throws Exception {
		int docLib = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(), tenantCode);
		String sql = "SELECT COUNT(*), sub_topicID FROM " + LibHelper.getLibTable(docLib)
				+ " WHERE sub_type=1 GROUP BY sub_topicID";
		List<StatData> list = getColStatList(sql);
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("list", list);
		return rtnMap;
	}

	private List<StatData> getColStatList(String sql) throws Exception {
		List<StatData> list = new ArrayList<>();
		DBSession conn = null;
		IResultSet rs = null;
		int colLibID = LibHelper.getColumnLibID();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql);
			while (rs.next()){
				StatData data = new StatData();
				data.count = rs.getLong(1);
				long id = rs.getLong(2);
				data.id = id;
				Column col = colReader.get(colLibID, id);
				if( col != null){
					data.name = col.getName();
					list.add(data);
				}
			}
		}catch(Exception e){
			throw e;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return list;
	}
	
	/**
	 * 查询来源列表
	 */
	public Document[] find(int siteID, String name) throws E5Exception {
		int docLibID = LibHelper.getSourceLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, "src_siteID=? AND src_name LIKE ? AND SYS_DELETEFLAG=0",
				new Object[] { siteID, name + "%" });
		return docs;
	}
	
	private String getMonthDescription() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -11);
		String serialMonth = (cal.get(Calendar.MONTH) + 1) + "月";
		for (int i = 0; i < 11; i++) {
			cal.add(Calendar.MONTH, 1);
			serialMonth += "," + (cal.get(Calendar.MONTH) + 1) + "月";
		}
		return serialMonth;
	}

	//读每个月的统计数据
	private List<String> monthlyDatas(Pair[] articleTypes, String[] serialMonthArr, Map<String, String> yearMap) {
		if (yearMap != null) {
			List<String> artDataList = new ArrayList<String>(articleTypes.length);
			String artData;
	        for (int i = 0; i < articleTypes.length; i++) {
	        	artData = "";
	        	int index = Integer.parseInt(articleTypes[i].getKey());
	        	for (int j = 0; j < 12; j++) {
	        		artData += "," + NVL0(yearMap.get(index + "_" + serialMonthArr[j]));
	        	}
	        	artDataList.add(artData.substring(1));
	        }
	        return artDataList;
	    }
		return null;
	}

	/**
	 * null --> "0"
	 */
	private String NVL0(String param){
		return (param == null) ? "0" : param;
	}

	/**
	 * 读出系统内定义的稿件类型。从稿件字段里取。
	 * 在稿件类型字段里定义格式为：
	 * 0=文章,1=组图,2=视频,3=专题,4=链接,5=多标题,7=活动,8=广告
	 * @return
	 */
	private Pair[] getArticleTypes() {
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		try {
			DocTypeField typeField = docTypeReader.getField(DocTypes.ARTICLE.typeID(), "a_type");
			
			String[] optionArr = typeField.getOptions().split(",");
			
			Pair[] result = new Pair[optionArr.length];
			for (int i = 0; i < optionArr.length; i++) {
				String[] split = optionArr[i].split("=");
				result[i] = new Pair(split[0], split[1]);
			}
			
			return result;
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//确定查询的是web发布库还是app发布库
	private String getStatTable(HttpServletRequest request) throws E5Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		
		List<DocLib> docLibList = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		
		String tableName = LibHelper.getLibTable(docLibList.get(0).getDocLibID());
		if (request.getParameter("channel").startsWith("channelApp")){
			tableName = LibHelper.getLibTable(docLibList.get(1).getDocLibID());
		}
		return tableName;
	}

	//统计界面初入时，取默认查询的稿件库表名
	private String getStatTableDefault(String tenantCode) {
		String _article = null;
		List<DocLib> docLibList = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		Channel[] chs = ConfigReader.getChannels();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] != null) {
				_article = docLibList.get(i).getDocLibTable();
				break;
			}
		}
		return _article;
	}

	//读起止时间
	private Timestamp[]  getDateRange(HttpServletRequest request) throws Exception {
		String type = request.getParameter("type");
		return getDateRange(request, type);
	}

	private Timestamp[] getDateRange(HttpServletRequest request, String type) {
		Timestamp startDate;
		Timestamp endDate;
		if("lastMonth".equals(type)){
			//上月
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			startDate = getMonth1st(cal);
			endDate = getMonth1st(null);
		} else if("thisMonth".equals(type)){
			//本月
			startDate = getMonth1st(null);
			endDate = getToday();
		} else if("thisYear".equals(type)){
			//本年

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, 1);
			
			startDate = getMonth1st(cal);
			endDate = getToday();
		}else if("thisWeek".equals(type)){
			//本周
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_WEEK, 2);
			//cal.add(Calendar.MONTH, 1);
			startDate = getBeginDate(cal);
			endDate = getToday();
		}  else {
			//指定日期
			startDate = new Timestamp(DateUtils.parse(request.getParameter("pubTime_from"), "yyyy-MM-dd").getTime());
			endDate = new Timestamp(DateUtils.parse(request.getParameter("pubTime_to") + " 23:59:59", "yyyy-MM-dd HH:mm:ss").getTime());
		}
		return new Timestamp[]{startDate, endDate};
	}
	
	//读默认的起止时间，上个月
	private Timestamp[]  getDateRangeDefault() {
		Timestamp endDate = getMonth1st(null);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Timestamp startDate = getMonth1st(cal);
		
		return new Timestamp[]{startDate, endDate};
	}

	/**
	 * 获得所有用户信息
	 */
	private Map<Long, String> getSiteUsers(int siteID) throws Exception {
		
		int docLibID = LibHelper.getUserExtLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, "u_siteID=? and SYS_DELETEFLAG=0", 
				new Object[]{siteID});
		int length = docs.length;
		
		Map<Long, String> map = new HashMap<>();
		for (int i = 0; i < length; i++) {
			map.put(docs[i].getDocID(), docs[i].getString("u_name"));
		}
		return map;
	}

	/**
	 * 本月工作量
	 */
	private Map<String, String> getStatByMonth(int usrSysId, int siteID,
			String tableName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1), a_type FROM " + tableName);
		sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
		sql.append(getCommonSql("SYS_AUTHORID"));
		sql.append("GROUP BY a_type ");
		sql.append("ORDER BY a_type");
		
		Timestamp mon1st = getMonth1st(null);
		Timestamp today = getToday();
		
		DBSession conn = null;
		IResultSet rs = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql.toString(), new Object[] { mon1st,
				today, Article.STATUS_PUB_DONE, siteID, usrSysId });
			while (rs.next()){
				map.put(rs.getString(2), rs.getString(1));
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return map;
	}
	
	//取截止到今天的日期表示
	private Timestamp getToday() {
		//return DateUtils.parse(DateUtils.format("yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		Calendar cal = Calendar.getInstance();
		return getEndDate(cal);
	}
	
	//取月第一天
	private Timestamp getMonth1st(Calendar cal) {
		//Timestamp mon1st = DateUtils.parse(DateUtils.format("yyyy-MM-01") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		if (cal == null) 
			cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		return getBeginDate(cal);
	}
	
	//把日期的时间部分改为23:59:59
	private Timestamp getEndDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		return new Timestamp(cal.getTime().getTime());
	}
	
	//把日期的时间部分改为00:00:00
	private static Timestamp getBeginDate(Calendar ca) {
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.clear(Calendar.MINUTE);
		ca.clear(Calendar.SECOND);
		ca.clear(Calendar.MILLISECOND);
		
		return new Timestamp(ca.getTime().getTime());
	}
	
	/**
	 * 各月工作量统计
	 */
	private Map<String, String> getStatByYear(int usrSysId, int siteID, String tableName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1, a_pubTime, a_type FROM " + tableName);
		sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
		sql.append(getCommonSql("SYS_AUTHORID"));
		sql.append("ORDER BY a_pubTime, a_type");
		
		Timestamp today = getToday();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -11);
		cal.set(Calendar.DATE, 1);
		
		Timestamp last11Month = getBeginDate(cal); //时分秒清零
		
		String key;
		DBSession conn = null;
		IResultSet rs = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql.toString(), new Object[] {
				last11Month, today, Article.STATUS_PUB_DONE, siteID, usrSysId
			});
			while (rs.next()){
				//1_3  稿件类型ID_月份
				key = rs.getString(3) + "_" + trimLZero(rs.getString(2).split("-")[1]);
				if (map.containsKey(key)) {
					map.put(key, String.valueOf(rs.getInt(1) + Integer.valueOf(map.get(key))));
				} else {
					map.put(key, rs.getString(1));
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return map;
	}
	
	/**
	 * 工作量分条件细览
	 * @param usrSysId 当前登录用户ID
	 * @param tableName 表名
	 * @param Timestamp 
	 * @param cal 
	 */
	//TODO   添加点击和评论统计信息
	private Map<String, Object> getDetailStatList(int usrSysId,int siteID, String tableName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1), a_pubTime, sum(a_countDiscuss) as disscuss, sum(a_countClick) as click FROM " + tableName);
		sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
		sql.append(getCommonSql("SYS_AUTHORID"));
		sql.append("GROUP BY a_pubTime ");
		sql.append("ORDER BY a_pubTime");
		
		Map<String, String> map = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		Timestamp mon1st = getMonth1st(null);
		Timestamp today = getToday();
		
		DBSession conn = null;
		IResultSet rs = null;
		Map<String, String> mapKV = new HashMap<String, String>();
		String key;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql.toString(), new Object[] {
				mon1st, today, Article.STATUS_PUB_DONE, siteID, usrSysId
			});
			while (rs.next()){
				key = rs.getString(2).substring(0, 10);
				int discuss = rs.getInt(3);
				int click = rs.getInt(4);
				if(mapKV.containsKey(key)){
					String[] mapkeyValue = mapKV.get(key).split(";");
					String keyValue = (rs.getInt(1) + Integer.valueOf(mapkeyValue[0])) + ";" + (discuss + Integer.valueOf(mapkeyValue[1])) + ";" + (click + Integer.valueOf(mapkeyValue[2]));
					mapKV.put(key, keyValue);
				}else{
					String keyValue = rs.getInt(1) + ";" + discuss + ";" + click ;
					mapKV.put(key, keyValue);
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		// 今天
		Date date = Calendar.getInstance().getTime();
		int loop = Integer.valueOf(trimLZero(new SimpleDateFormat("dd").format(date)));
		String yyyyMM = new SimpleDateFormat("yyyy-MM-").format(date);
		
		for (int i = 0; i < loop; i++) {
			map = new HashMap<String, String>();
			key = yyyyMM + addLZero(i + 1);
			if(mapKV.containsKey(key)){
				String[] mapkeyValue = mapKV.get(key).split(";");
				map.put("count", mapkeyValue[0]);
				map.put("discuss", mapkeyValue[1]);
				map.put("click", mapkeyValue[2]);
			}else{
				map.put("count", "0");
				map.put("discuss", "0");
				map.put("click", "0");
			}
			map.put("date", key);
			
			list.add(map);
		}
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("list", list);
		return rtnMap;
	}
	
	/**
	 * 工作量分条件细览 查询
	 * @param request request
	 */
	private Map<String, Object> getDetailStatList(HttpServletRequest request) throws Exception {
		
		// 当前用户ID
		int usrSysId = ProcHelper.getUser(request).getUserID();
		String type = request.getParameter("type");
		String colID = request.getParameter("colID");
		int artSelect = Integer.valueOf(request.getParameter("artSelect"));



		int siteID = Integer.valueOf(request.getParameter("siteID"));
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		
		List<DocLib> docLibList = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				InfoHelper.getTenantCode(request));
		
		String tableName = LibHelper.getLibTable(docLibList.get(0).getDocLibID());
		if("_App".equals(request.getParameter("webApp"))){
			tableName = LibHelper.getLibTable(docLibList.get(1).getDocLibID());
		}
		
		// 点击	今天	昨天
		if(type.matches("today|yesterday")){
			return getToyesdayRtnMap(type, tableName, colID, usrSysId, siteID, artSelect, cal);
		// 点击	最近一周内
		}else if("thisWeek".equals(type)){
			return getThisWeekRtnMap(tableName, colID, artSelect, usrSysId, siteID, cal);
			
		}else{
			Timestamp[] dateRange = getDateRange(request, null);
			// 点击查看按钮   栏目   类型
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String pubTime_from = request.getParameter("pubTime_from");
			String pubTime_to = request.getParameter("pubTime_to");
			// 开始与结束日期之间天数
			Long duration = 1 + (df.parse(pubTime_to).getTime() - df.parse(pubTime_from).getTime()) / (1000 * 60 * 60 * 24);
			// 开始与今天日期之间天数
			Long todayToFrom = 1 + (df.parse(df.format(date)).getTime() - df.parse(pubTime_from).getTime()) / (1000 * 60 * 60 * 24);
			
			return getOtherRtnMap(tableName, colID, artSelect, usrSysId, siteID, cal, duration.intValue(),
					todayToFrom.intValue(), dateRange[0], dateRange[1]);
		}
	}
	
	/**
	 * 获得其他触发事件返回MAP
	 */
	private Map<String, Object> getOtherRtnMap(String tableName, String colID,
			int artSelect, int usrSysId, int siteID, Calendar cal, int durationInt,
			int todayToFromInt, Timestamp pubTime_from, Timestamp pubTime_to) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		DBSession conn = null;
		IResultSet rs = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		
		List<Object> sqlList = new ArrayList<Object>();
		sqlList.add(pubTime_from);
		sqlList.add(pubTime_to);
		sqlList.add(Article.STATUS_PUB_DONE);
		sqlList.add(siteID);
		sqlList.add(usrSysId);
		
		if(durationInt > 31) {
			sql.append("SELECT COUNT(1), sum(a_countDiscuss) as disscuss, sum(a_countClick) as click FROM " + tableName);
			sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
			sql.append(getCommonSql("SYS_AUTHORID"));
			if(colID != null && !"".equals(colID)){
				sql.append(" AND a_columnID = ?");
				sqlList.add(colID);
			}
			if(-1 != artSelect){
				sql.append(" AND a_type = ?");
				sqlList.add(artSelect);
			}
			try {
				conn = Context.getDBSession();
				rs = conn.executeQuery(sql.toString(), sqlList.toArray());
				rs.next();
				for (int i = 0; i < durationInt; i++) {
					list.add(null); // 和其他查询方法统一在jsp中取天数的方法，取list的size为天数
				}
				rtnMap.put("count", rs.getInt(1));
				rtnMap.put("discuss", rs.getInt(2));
				rtnMap.put("click", rs.getInt(3));
				rtnMap.put("list", list);
				return rtnMap;
				
			} catch (Exception e) {
				throw new Exception(e);
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}
		}else{
			sql.append("SELECT COUNT(1), a_pubTime, sum(a_countDiscuss) as disscuss, sum(a_countClick) as click FROM " + tableName);
			sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
			sql.append(getCommonSql("SYS_AUTHORID"));
			if(colID != null && !"".equals(colID)){
				sql.append(" AND a_columnID = ?");
				sqlList.add(colID);
			}
			if(-1 != artSelect){
				sql.append(" AND a_type = ?");
				sqlList.add(artSelect);
			}
			sql.append(" GROUP BY a_pubTime");
			
			Map<String, String> map = null;
			Map<String, String> mapKV = new HashMap<String, String>();
			String key = "";
			try {
				conn = Context.getDBSession();
				rs = conn.executeQuery(sql.toString(), sqlList.toArray());
				while (rs.next()){
					key = rs.getString(2).substring(0, 10);
					int discuss = rs.getInt(3);
					int click = rs.getInt(4);
					if(mapKV.containsKey(key)){
						String[] mapkeyValue = mapKV.get(key).split(";");
						String keyValue = (rs.getInt(1) + Integer.valueOf(mapkeyValue[0])) + ";" + (discuss + Integer.valueOf(mapkeyValue[1])) + ";" + (click + Integer.valueOf(mapkeyValue[2]));
						mapKV.put(key, keyValue);
					}else{
						String keyValue = rs.getInt(1) + ";" + discuss + ";" + click ;
						mapKV.put(key, keyValue);
					}
				}
				cal.add(Calendar.DATE, -todayToFromInt);
				for (int i = 0; i < durationInt; i++) {
					map = new HashMap<String, String>();
					cal.add(Calendar.DATE, 1);
					key = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
					if(mapKV.containsKey(key)){
						String[] mapkeyValue = mapKV.get(key).split(";");
						map.put("count", mapkeyValue[0]);
						map.put("discuss", mapkeyValue[1]);
						map.put("click", mapkeyValue[2]);
					}else{
						map.put("count", "0");
						map.put("discuss", "0");
						map.put("click", "0");
					}
					map.put("date", key);
					list.add(map);
				}
			} catch (Exception e) {
				throw new Exception(e);
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}
			rtnMap.put("list", list);
			return rtnMap;
		}
	}

	/**
	 * 获得近一周返回MAP
	 */
	private Map<String, Object> getThisWeekRtnMap(String tableName, String colID,
			int artSelect,int usrSysId, int siteID, Calendar cal) throws Exception {
		
		Timestamp day_end = getEndDate(cal);
		
		cal.add(Calendar.DATE, -6);
		Timestamp day_start = getBeginDate(cal);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1), a_pubTime, sum(a_countDiscuss) as disscuss, sum(a_countClick) as click FROM " + tableName);
		sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
		sql.append(getCommonSql("SYS_AUTHORID"));
		
		List<Object> sqlList = new ArrayList<Object>();
		sqlList.add(day_start);
		sqlList.add(day_end);
		sqlList.add(Article.STATUS_PUB_DONE);
		sqlList.add(siteID);
		sqlList.add(usrSysId);
		
		if(colID != null && !"".equals(colID)){
			sql.append(" AND a_columnID = ?");
			sqlList.add(colID);
		}
		if(-1 != artSelect){
			sql.append(" AND a_type = ?");
			sqlList.add(artSelect);
		}
		sql.append(" GROUP BY a_pubTime");
		
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapKV = new HashMap<String, String>();
		String key = "";
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql.toString(), sqlList.toArray());
			while (rs.next()){
				key = rs.getString(2).substring(0, 10);
				int discuss = rs.getInt(3);
				int click = rs.getInt(4);
				if(mapKV.containsKey(key)){
					String[] mapkeyValue = mapKV.get(key).split(";");
					String keyValue = (rs.getInt(1) + Integer.valueOf(mapkeyValue[0])) + ";" + (discuss + Integer.valueOf(mapkeyValue[1])) + ";" + (click + Integer.valueOf(mapkeyValue[2]));
					mapKV.put(key, keyValue);
				}else{
					String keyValue = rs.getInt(1) + ";" + discuss + ";" + click ;
					mapKV.put(key, keyValue);
				}
			}
			cal.add(Calendar.DATE, -1);
			for (int i = 0; i < 7; i++) {
				map = new HashMap<String, String>();
				cal.add(Calendar.DATE, 1);
				key = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
				if(mapKV.containsKey(key)){
					String[] mapkeyValue = mapKV.get(key).split(";");
					map.put("count", mapkeyValue[0]);
					map.put("discuss", mapkeyValue[1]);
					map.put("click", mapkeyValue[2]);
				}else{
					map.put("count", "0");
					map.put("discuss", "0");
					map.put("click", "0");
				}
				map.put("date", key);
				list.add(map);
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("list", list);
		return rtnMap;
	}

	/**
	 * 获得今天昨天返回MAP
	 */
	private Map<String, Object> getToyesdayRtnMap(String type, String tableName,
			String colID, int usrSysId, int siteID, int artSelect,
			Calendar cal) throws Exception {
		
		if("yesterday".equals(type)){
			cal.add(Calendar.DATE, -1);
		}
		Timestamp day_end = getEndDate(cal);
		Timestamp day_start = getBeginDate(cal);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1), a_pubTime, sum(a_countDiscuss) as disscuss, sum(a_countClick) as click FROM " + tableName);
		sql.append(" WHERE a_pubTime BETWEEN ? AND ? ");
		sql.append(getCommonSql("SYS_AUTHORID"));
		
		List<Object> sqlList = new ArrayList<Object>();
		sqlList.add(day_start);
		sqlList.add(day_end);
		sqlList.add(Article.STATUS_PUB_DONE);
		sqlList.add(siteID);
		sqlList.add(usrSysId);
		
		if(colID != null && !"".equals(colID)){
			sql.append(" AND a_columnID = ?");
			sqlList.add(colID);
		}
		if(-1 != artSelect){
			sql.append(" AND a_type = ?");
			sqlList.add(artSelect);
		}
		sql.append(" GROUP BY a_pubTime");
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> mapKV = new HashMap<String, String>();
		String key = "";
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql.toString(), sqlList.toArray());
			while (rs.next()){
				key = rs.getString(2).substring(0, 10);
				int discuss = rs.getInt(3);
				int click = rs.getInt(4);
				if(mapKV.containsKey(key)){
					String[] mapkeyValue = mapKV.get(key).split(";");
					String keyValue = (rs.getInt(1) + Integer.valueOf(mapkeyValue[0])) + ";" + (discuss + Integer.valueOf(mapkeyValue[1])) + ";" + (click + Integer.valueOf(mapkeyValue[2]));
					mapKV.put(key, keyValue);
				}else{
					String keyValue = rs.getInt(1) + ";" + discuss + ";" + click ;
					mapKV.put(key, keyValue);
				}
			}
			key = DateUtils.format(day_start, "yyyy-MM-dd");
			if(mapKV.containsKey(key)){
				String[] mapkeyValue = mapKV.get(key).split(";");
				map.put("count", mapkeyValue[0]);
				map.put("discuss", mapkeyValue[1]);
				map.put("click", mapkeyValue[2]);
				map.put("date", key);
			}else{
				map.put("count", "0");
				map.put("discuss", "0");
				map.put("click", "0");
				map.put("date", key);
			}
			list.add(map);
			
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("list", list);
		return rtnMap;
	}

	/**
	 * 上月工作量（初始化表示）
	 */
	private List<StatData> wlStatList(Map<Long, String> userList, int siteID,
			String tableName, Timestamp startDate, Timestamp endDate) throws Exception {
		
		String sql = "select SYS_AUTHORID, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? group by SYS_AUTHORID";
		Object[] params = new Object[]{startDate, endDate, Article.STATUS_PUB_DONE, siteID};
		
		return wlStatList(userList, sql, params);
	}
	
	/**
	 * 编辑工作量分条件细览 查询
	 * @param request request
	 */
	private List<StatData> wlStatList(Map<Long, String> userList, HttpServletRequest request)
			throws Exception {
		Timestamp[] dateRange = getDateRange(request);//日期范围
		String siteID = request.getParameter("siteID");
		String tableName = getStatTable(request);
		long userID = WebUtil.getLong(request, "usrSelect", 0);
		
		String sql;
		Object[] params;
		if (userID > 0) {//指定用户
			sql = "select SYS_AUTHORID, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? and SYS_AUTHORID=? group by SYS_AUTHORID";
			params = new Object[]{dateRange[0], dateRange[1], Article.STATUS_PUB_DONE, siteID, userID};
		} else {
			sql = "select SYS_AUTHORID, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
					+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? group by SYS_AUTHORID";
			params = new Object[]{dateRange[0], dateRange[1], Article.STATUS_PUB_DONE, siteID};
		}
		return wlStatList(userList, sql, params);
	}

	private List<StatData> wlStatList(Map<Long, String> userList, String sql, Object[] params) throws Exception {
		
		List<StatData> list = commonStatList(sql, params);
		
		for (StatData data : list) {
			if (!StringUtils.isBlank(data.name)) {
				try {
					data.id = Integer.parseInt(data.name);
					data.name = userList.get(data.id);
				} catch (Exception e) {
				}
			}
		}
		return list;
	}
	
	/**
	 * 部门统计（查询）
	 * @param tableName 表名
	 */
	private List<StatData> deptStatList(HttpServletRequest request) throws Exception {
		Timestamp[] dateRange = getDateRange(request);
		
		String siteID = request.getParameter("siteID");
		String tableName = getStatTable(request);
		
		return deptStatList(tableName, siteID, dateRange[0], dateRange[1]);
	}

	/**
	 * 部门统计，指定条件，查询数据库
	 */
	private List<StatData> deptStatList(String tableName, String siteID, Timestamp startDate, Timestamp endDate)
	throws Exception {
		
		String sql = "select a_orgID, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? group by a_orgID";
		Object[] params = new Object[]{startDate, endDate, Article.STATUS_PUB_DONE, siteID};
		
		List<StatData> list = commonStatList(sql, params);
		
		OrgReader orgReader = (OrgReader) Context.getBean(OrgReader.class);
		for (StatData data : list) {
			if (!StringUtils.isBlank(data.name)) {
				try {
					data.id = Integer.parseInt(data.name);
					
					Org org = orgReader.get((int)data.id);
					if (org != null) data.name = org.getName();
				} catch (Exception e) {
				}
			}
		}
		return list;
	}
	
	/**
	 * 来源统计（检索）
	 * @param tableName 表名
	 */
	private List<StatData> srcStatList(HttpServletRequest request) throws Exception {
		//日期范围
		Timestamp[] dateRange = getDateRange(request);
		
		String siteID = request.getParameter("siteID");
		String tableName = getStatTable(request);
		
		String sql = "select a_source, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? group by a_source";
		Object[] params = new Object[]{dateRange[0], dateRange[1], Article.STATUS_PUB_DONE, siteID};
	
		long srcID = WebUtil.getLong(request, "srcID", 0);
		if (srcID > 0) {
			//指定来源ID
			sql = "select a_source, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
					+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? and a_sourceID=? group by a_source";
			params = new Object[]{dateRange[0], dateRange[1], Article.STATUS_PUB_DONE, siteID, srcID};
		} else {
			//指定来源名
			String srcName = request.getParameter("srcName");
			srcName = (null == srcName) ? "" : srcName.trim();
			if (!"".equals(srcName)) {
				sql = "select a_source, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
						+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? and a_source=? group by a_source";
				params = new Object[]{dateRange[0], dateRange[1], Article.STATUS_PUB_DONE, siteID, srcName};
			}
		}
		return commonStatList(sql, params);
	}

	/**
	 * 来源统计（初始化表示）
	 */
	private List<StatData> srcStatList(String tableName, String siteID, Timestamp day_start, Timestamp day_end)
			throws Exception {
		String sql = "select a_source, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_status=? and a_siteID=? group by a_source";
		Object[] params = new Object[]{day_start, day_end, Article.STATUS_PUB_DONE, siteID};
		
		return commonStatList(sql, params);
	}
	/**
	 * 发稿量统计（查询）
	 * @param tableName 表名
	 */
	private List<StatData> authorsStatList(HttpServletRequest request) throws Exception {
		Timestamp[] dateRange = getDateRange(request);
		
		String siteID = request.getParameter("siteID");
		String tableName = getStatTable(request);
		
		return authorsStatList(tableName, siteID, dateRange[0], dateRange[1]);
	}
	/**
	 * 发稿量统计，指定条件，查询数据库
	 */
	private List<StatData> authorsStatList(String tableName, String siteID, Timestamp startDate, Timestamp endDate)
	throws Exception {
		
		String sql = "select xa1.sys_authors, count(*),"
				+"(select count(*) from xy_article xa2 where xa2.a_sourceType=3 and xa2.a_status = ? and xa2.SYS_AUTHORS =  xa1.SYS_AUTHORS "
				+ "and xa2.a_pubTime between ? and ? and xa2.a_siteID=? group by xa2.sys_authors) countRelease "
				+"from xy_article xa1 where xa1.a_sourceType=3 and xa1.a_pubTime between ? and ? and xa1.a_siteID=? group by xa1.sys_authors";

		Object[] params = new Object[]{Article.STATUS_PUB_DONE,startDate, endDate,  siteID,startDate, endDate,  siteID};
				
		List<StatData> list = batmanStatList(sql, params);
		return list;
	}
	/**
	 * 栏目统计（检索）
	 * @param tableName 表名
	 */
	private List<StatData> colStatList(HttpServletRequest request) throws Exception {
		
		//指定的栏目ID
		long[] colIDArr = StringUtils.getLongArray(request.getParameter("colID"), ",");
		if (colIDArr == null || colIDArr.length == 0) {
			
		}
	
		//日期范围
		Timestamp[] dateRange = getDateRange(request);
		
		String siteID = request.getParameter("siteID");
		String tableName = getStatTable(request);
		
		String sql = "select a_column, count(*), sum(a_countClick), sum(a_countDiscuss) from " + tableName
				+ " where a_pubTime between ? and ? and a_columnID in (";
		List<Object> params = new ArrayList<>();
		params.add(dateRange[0]);
		params.add(dateRange[1]);
		
		for (int i = 0; i < colIDArr.length; i++) {
			sql += (i == 0) ? "?" : ",?";
			
			params.add(colIDArr[i]);
		}
		sql += ") and a_status=? and a_siteID=? group by a_column";
		params.add(Article.STATUS_PUB_DONE);
		params.add(siteID);
	
		return commonStatList(sql, params.toArray(new Object[0]));
	}

	//通用统计，查询数据库，返回名字、计数
	private List<StatData> commonStatList(String sql, Object[] params) throws Exception {
		List<StatData> list = new ArrayList<>();
		
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			while (rs.next()){
				StatData data = new StatData();
				data.name = rs.getString(1);
				data.count = rs.getLong(2);
				data.countClick = rs.getLong(3);
				data.countDiscuss = rs.getLong(4);
				
				list.add(data);
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return list;
	}

	//发稿量统计，查询数据库，返回投稿人、投稿量、发布量
	private List<StatData> batmanStatList(String sql, Object[] params) throws Exception {
		List<StatData> list = new ArrayList<>();
		
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			while (rs.next()){
				StatData data = new StatData();
				data.name = rs.getString(1);
				data.count = rs.getLong(2);
				data.countRelease = rs.getLong(3);
				
				list.add(data);
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return list;
	}
	
	/**
	 * 去掉前面的0
	 */
	private String trimLZero(String str){
		if(str.startsWith("0")){
			return str.substring(1);
		}
		return str;
	}
	
	/**
	 * 前补0
	 */
	private String addLZero(int day){
		if(day < 10){
			return "0" + day;
		}
		return String.valueOf(day);
	}
	
	//取出某租户的根机构ID
	@SuppressWarnings("unused")
	private int getRootOrgID(String tenantCode) throws E5Exception {
		// 租户
		int tLibID = LibHelper.getLib(DocTypes.TENANT.typeID()).getDocLibID();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tenant = docManager.find(tLibID, "te_code = ?", new Object[]{tenantCode});
		
		int orgID = tenant[0].getInt("te_orgID"); // 父机构ID
		return orgID;
	}

	/**
	 * 返回通用查询条件
	 */
	private String getCommonSql(String filedName){
		return "AND a_status=? AND a_siteID=? AND " + filedName + "=? ";
	}
}