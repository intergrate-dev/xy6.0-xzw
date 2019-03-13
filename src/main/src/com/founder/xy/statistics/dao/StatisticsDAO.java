package com.founder.xy.statistics.dao;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.article.Article;
import com.founder.xy.statistics.util.TimeUtil;

import org.apache.commons.lang.StringUtils;
import org.hibernate.sql.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ethan on 2016/12/16.
 */
@Repository
public class StatisticsDAO {
	private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
	
    /**
     * 获取当月稿件发布量 直接从渠道对应的稿件表中统计
     * @param siteIDstr 站点id
     * @param departmentIDstr 部门id
     * @param tableName APP/WEB
     * @return
     * @throws E5Exception
     */
    public Map<String, Object> getCurrentMonthWorkStatisticsOfDepartmentByTableName(String siteIDstr, String departmentIDstr, String tableName) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        Timestamp currentMonthFirstDay = TimeUtil.getMonthFirstDay(null);
        Timestamp currentMonthLastDay = TimeUtil.getMonthLastDay(null);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        sql.append("select /*+ index(XY_ARTICLE IDX16_XY_ARTICLE) */ count(1),a_type from " + tableName);
        sql.append(" where ");
    	if (isOracle())
            sql.append(" a_pubTime >= ? AND a_pubTime <= ? ");
            else
            sql.append(" a_pubTime between ? and ? ");
    	sql.append(" and a_siteID=? AND a_status=? ");
        if (departmentIDstr != null && !departmentIDstr.equals("")) {
            sql.append(" and a_orgID =? ");
        } else {
            sql.append(" and a_orgID >?");
        }
        sql.append(" and SYS_AUTHORID>0 ");
        sql.append(" AND a_sourceType<=2 ");
        sql.append(" group by a_type order by a_type");
        DBSession conn = null;
        IResultSet resultSet = null;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            conn = Context.getDBSession();
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{currentMonthFirstDay, currentMonthLastDay,siteID, Article.STATUS_PUB_DONE, departmentID});
                log.error("StatisticsDAO.java:66--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:66--params--"+ currentMonthFirstDay + "," + currentMonthLastDay + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时66："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{currentMonthFirstDay, currentMonthLastDay,siteID,  Article.STATUS_PUB_DONE,0});
                log.error("StatisticsDAO.java:66--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:66--params--"+ currentMonthFirstDay + "," + currentMonthLastDay + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时66："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                resultMap.put(resultSet.getString(2), resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultMap;
    }
    /**
     * 个人工作量统计 把APP和WEB端对应作者（联合下用户表xy_userext）的各种互动计数SUM出来  在把两个结果集合并 再次SUM
     * @param siteIDstr
     * @param departmentIDstr
     * @param webTableName
     * @param appTableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @param operation
     * @return
     * @throws E5Exception
     */

    public List getPersonalTotalWorkStatisticsOfTimeByDepartmentID(String siteIDstr, String departmentIDstr, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum, String operation) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
        //TODO
        // Oracle分页待调整
        if (isOracle()){
        	limitEnd = (pageNum - 1) * pageSize + 1;
        	limitBegin = (pageNum - 1) * pageSize + pageSize;
        }
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, departmentIDstr, null, 0, sql, formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteIDstr, limitBegin, limitEnd);
        }else{
        	sqlForMySql(webTableName, appTableName, departmentIDstr, null, 0, sql, beginTime, endTime, Article.STATUS_PUB_DONE, siteIDstr, limitBegin, limitEnd);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            //String testsql = conn.getDialect().getLimitString(sql.toString(), limitBegin, limitEnd);
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
            	log.error("StatisticsDAO.java:120--SQL--"+ sql.toString());
            	log.error("StatisticsDAO.java:120--params--"+ beginTime + "," + endTime + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
                resultSet = conn.executeQuery(sql.toString(), new Object[]{departmentID, Article.STATUS_PUB_DONE, siteID, departmentID, Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
            	log.error("StatisticsDAO.java:120--SQL--"+ sql.toString());
            	log.error("StatisticsDAO.java:120--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
                resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            int i = 0;
            if(isOracle())
            	i = 1;
            if (operation != null && operation.equals("statistics")) {
                while (resultSet.next()) {
                	Map<String, Object> rowData = new LinkedHashMap<>();
                    rowData.put("authorName", resultSet.getString(i + 1));
                    rowData.put("authorID", resultSet.getString(i + 2));
                    rowData.put("totalArticle", resultSet.getInt(i + 3));
                    rowData.put("wapTotalDays", resultSet.getInt(i + 16));
                    rowData.put("appTotalDays", resultSet.getInt(i + 17));
                    rowData.put("totalClick", resultSet.getInt(i + 4));
                    rowData.put("pcClick", resultSet.getInt(i + 5));
                    rowData.put("wapClick", resultSet.getInt(i + 6));
                    rowData.put("appClick", resultSet.getInt(i + 7));
                    rowData.put("totalForward", resultSet.getInt(i + 8));
                    rowData.put("pcForward", resultSet.getInt(i + 9));
                    rowData.put("wapForward", resultSet.getInt(i + 10));
                    rowData.put("appForward", resultSet.getInt(i + 11));
                    rowData.put("totalDiscussion", resultSet.getInt(i + 12));
                    rowData.put("pcDiscussion", resultSet.getInt(i + 13));
                    rowData.put("wapDiscussion", resultSet.getInt(i + 14));
                    rowData.put("appDiscussion", resultSet.getInt(i + 15));
                    resultList.add(rowData);
                }
            } else if (operation != null && operation.equals("export")) {
                while (resultSet.next()) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    rowData.put("authorName", resultSet.getString(i + 1));
                    rowData.put("totalArticle", resultSet.getInt(i + 3));
                    rowData.put("wapTotalDays", resultSet.getInt(i + 16));
                    rowData.put("appTotalDays", resultSet.getInt(i + 17));
                    rowData.put("totalClick", resultSet.getInt(i + 4));
                    rowData.put("pcClick", resultSet.getInt(i + 5));
                    rowData.put("wapClick", resultSet.getInt(i + 6));
                    rowData.put("appClick", resultSet.getInt(i + 7));
                    rowData.put("totalForward", resultSet.getInt(i + 8));
                    rowData.put("pcForward", resultSet.getInt(i + 9));
                    rowData.put("wapForward", resultSet.getInt(i + 10));
                    rowData.put("appForward", resultSet.getInt(i + 11));
                    rowData.put("totalDiscussion", resultSet.getInt(i + 12));
                    rowData.put("pcDiscussion", resultSet.getInt(i + 13));
                    rowData.put("wapDiscussion", resultSet.getInt(i + 14));
                    rowData.put("appDiscussion", resultSet.getInt(i + 15));
                    resultList.add(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }

    private void sqlForMySql(String webTableName, String appTableName, String departmentIDstr, Object departmentID2, int i,
			StringBuilder sql, Timestamp beginTime, Timestamp endTime, int status, String siteIDstr, int limitBegin, int limitEnd) {
    	/*int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
    	*/if(i == 1)
    		sql.append("select count(1) as total from (");
    	sql.append("select b.u_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss, SUM(x.wap_totalDays) AS wap_totalDays, SUM(x.app_totalDays) AS app_totalDays ");
        sql.append(" from ( ");
        sql.append("select a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss, COUNT(DISTINCT DATE_FORMAT(a.a_pubtime,'%Y-%m-%d')) AS wap_totalDays,  0 AS app_totalDays ");
        sql.append(" from "+ webTableName + " a ");
        sql.append(" where a.a_pubTime between '"+beginTime+"' and '"+endTime+"' ");
        if (departmentIDstr != null && !departmentIDstr.equals("")) {
            sql.append(" and a.a_orgID =? ");
        } else {
            sql.append(" and a.a_orgID >0");
        }
        sql.append(" and a.SYS_AUTHORID > 0  ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" AND a.a_sourceType<=2 ");
        sql.append(" group by a.SYS_AUTHORID");
        sql.append(" union all ( ");
        sql.append("select d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss, 0 AS wap_totalDays, COUNT(DISTINCT DATE_FORMAT(d.a_pubtime,'%Y-%m-%d')) AS app_totalDays ");
        sql.append(" from "+ appTableName + " d ");
        sql.append(" where d.a_pubTime between '"+beginTime+"' and '"+endTime+"' ");
        if (departmentIDstr != null && !departmentIDstr.equals("")) {
            sql.append(" and d.a_orgID =? ");
        } else {
            sql.append(" and d.a_orgID >0");
        }
        sql.append(" and d.SYS_AUTHORID>0 ");
        sql.append(" AND d.a_status=? ");
        sql.append(" AND d.a_siteID=? ");
        sql.append(" AND d.a_sourceType<=2 ");
        sql.append(" group by d.SYS_AUTHORID ");
        sql.append(" )) x join xy_userext b on b.SYS_DOCUMENTID=x.SYS_AUTHORID");
        
        if(i ==1){
        	sql.append(" group by x.SYS_AUTHORID,b.u_name");
            sql.append(" ) z ");
        }else{
	        sql.append(" group by x.SYS_AUTHORID,b.u_name order by totalArticle desc");
	        sql.append(" LIMIT ?,?");
        }
	}
	private void sqlForOracle(String webTableName, String appTableName, String departmentIDstr, Object departmentID2,
			int i, StringBuilder sql, String beginTime, String endTime, int status, String siteIDstr, int limitBegin, int limitEnd) {
		/*int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
		*/if(i == 1)
    		sql.append("select count(1) as total from (");
		else{
			sql.append("select * from (");
			sql.append("select rownum rn, f.* from (");
		}
		sql.append("select b.u_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss, SUM(x.wap_totalDays) AS wap_totalDays, SUM(x.app_totalDays) AS app_totalDays ");
        sql.append(" from ( ");
        sql.append("select /*+ index(XY_ARTICLE IDX16_XY_ARTICLE) */ a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss, COUNT( DISTINCT to_date(to_char(a.a_pubtime,'yyyy-mm-dd'),'yyyy-mm-dd')) AS wap_totalDays, 0 AS app_totalDays ");
        sql.append(" from "+ webTableName + " a");
        sql.append(" where a.a_pubTime BETWEEN to_date('"+beginTime+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+endTime+"','yyyy-MM-dd hh24:mi:ss') ");
        if (departmentIDstr != null && !departmentIDstr.equals("")) {
            sql.append(" and a.a_orgID =? ");
        } else {
            sql.append(" and a.a_orgID >0");
        }
        sql.append( " and a.SYS_AUTHORID>0");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" AND a.a_sourceType<=2 ");
        sql.append(" group by a.SYS_AUTHORID");
        sql.append(" union all ( ");
        sql.append("select d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss, 0 AS wap_totalDays, COUNT( DISTINCT to_date(to_char(d.a_pubtime,'yyyy-mm-dd'),'yyyy-mm-dd')) AS app_totalDays ");
        sql.append(" from "+ appTableName + " d ");
        sql.append(" where d.a_pubTime BETWEEN to_date('"+beginTime+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+endTime+"','yyyy-MM-dd hh24:mi:ss') ");
        if (departmentIDstr != null && !departmentIDstr.equals("")) {
            sql.append(" and d.a_orgID =? ");
        } else {
            sql.append(" and d.a_orgID >0");
        }
        sql.append(" and d.SYS_AUTHORID>0 ");
        sql.append(" AND d.a_status=? ");
        sql.append(" AND d.a_siteID=? ");
        sql.append(" AND d.a_sourceType<=2 ");
        sql.append(" group by d.SYS_AUTHORID ");
        sql.append(" )) x join xy_userext b on b.SYS_DOCUMENTID=x.SYS_AUTHORID");
        if(i ==1){
        	sql.append(" group by x.SYS_AUTHORID,b.u_name");
            sql.append(" ) z ");
        }else{
	        sql.append(" group by x.SYS_AUTHORID,b.u_name order by totalArticle desc");
	        sql.append(" )f where rownum <=? ");
	        sql.append(" ) where rn >=? ");
        }
	}
	/**
     * 有点击量的用户数量 在上面结果基础上加个 count(1)
     * @param siteIDstr
     * @param departmentIDstr
     * @param webTableName
     * @param appTableName
     * @param beginTime
     * @param endTime
     * @return
     * @throws E5Exception
     */
    public int countPersonalTotalWorkStatisticsOfTimeByDepartmentID(String siteIDstr, String departmentIDstr, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, departmentIDstr, null, 1, sql, formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteIDstr, 0, 0);
        }else{
        	sqlForMySql(webTableName, appTableName, departmentIDstr, null, 1, sql, beginTime, endTime, Article.STATUS_PUB_DONE, siteIDstr, 0, 0);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{departmentID, Article.STATUS_PUB_DONE, siteID, departmentID, Article.STATUS_PUB_DONE, siteID});
                log.error("StatisticsDAO.java:283--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:283--params--"+ beginTime + "," + endTime + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID});
                log.error("StatisticsDAO.java:283--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:283--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }
    /**
     * 稿件排行--部门
     * @param siteIDstr
     * @param departmentID
     * @param tableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getTotalArticleStatisticsOfTimeByDepartment(String siteIDstr, String departmentID, String tableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        
        int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
        // Oracle分页待调整
        if (isOracle()){
        	limitEnd = (pageNum - 1) * pageSize + 1;
        	limitBegin = (pageNum - 1) * pageSize + pageSize;
        }
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) {
        	sql.append("select * from (");
        	sql.append("select rownum rn, z.* from (");
        }
        sql.append("Select count(1) AS article_num,a.a_orgID as department_id,sum(a.a_countClick) AS total_click,sum(a.a_countClick0) AS web_click,sum(a.a_countClick1) AS wap_click,sum(a.a_countClick2) AS app_click,sum(a.a_countShare) AS total_share,sum(a.a_countShare0) AS web_share,");
        sql.append("sum(a.a_countShare1) as wap_share,sum(a.a_countShare2) as app_share,sum(a.a_countDiscuss) as total_discuss,sum(a.a_countDiscuss0) as web_discuss,sum(a.a_countDiscuss1) as wap_discuss,sum(a.a_countDiscuss2) as app_discuss,b.STRNODENAME as department_name");
        sql.append(" FROM fsys_node b");
        sql.append(" INNER JOIN ");
        sql.append(tableName);
        sql.append(" a ON a.a_orgID = b.NNODEID AND a_sourceType<=2 ");
        if(isOracle()) 
        	sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        if (departmentID != null && !departmentID.equals("")) {
        	sql.append(" AND a.a_orgID in ("+departmentID+")");
        } else {
        	sql.append(" AND a.a_orgID >0");
        }
        
        if (isOracle()) {
        	sql.append(" GROUP BY a.a_orgID,b.STRNODENAME ORDER BY a.a_orgID ASC");
            sql.append(" )z where rownum <=? ");
            sql.append(" ) where rn >=? ");
        }else{
	        sql.append(" GROUP BY a.a_orgID ORDER BY a.a_orgID ASC");
	        sql.append(" LIMIT ?,?");
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID,limitBegin, limitEnd});
            log.error("StatisticsDAO.java:357--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:357--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID,limitBegin + "," + limitEnd);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            
            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if(isOracle())
                	 i = 1;
                rowData.put("departmentName", resultSet.getString(i + 15));
                rowData.put("articleNum", resultSet.getString(i + 1));
                //rowData.put("departmentID", resultSet.getString(i + 2));
                rowData.put("totalClick", resultSet.getString(i + 3));
                rowData.put("pcClick", resultSet.getString(i + 4));
                rowData.put("wapClick", resultSet.getString(i + 5));
                rowData.put("appClick", resultSet.getString(i + 6));
                rowData.put("totalShare", resultSet.getString(i + 7));
                rowData.put("pcShare", resultSet.getString(i + 8));
                rowData.put("wapShare", resultSet.getString(i + 9));
                rowData.put("appShare", resultSet.getString(i + 10));
                rowData.put("totalDiscuss", resultSet.getString(i + 11));
                rowData.put("pcDiscuss", resultSet.getString(i + 12));
                rowData.put("wapDiscuss", resultSet.getString(i + 13));
                rowData.put("appDiscuss", resultSet.getString(i + 14));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }

    public int countTotalArticleStatisticsOfTimeByDepartment(String siteIDstr, String departmentID, String tableName, Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("select count(1) FROM (");
        sql.append("select count(1) FROM fsys_node b INNER JOIN ");
        sql.append(tableName);
        sql.append(" a ON a.a_orgID = b.NNODEID AND a_sourceType<=2 ");
        if(isOracle())
        	sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        if (departmentID != null && !departmentID.equals("")) {
            sql.append(" AND a.a_orgID in ("+departmentID+")");
        } else {
            sql.append(" AND a.a_orgID >0");
        }
        sql.append(" GROUP BY a.a_orgID ORDER BY a.a_orgID ASC ");
        sql.append(")z");
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:410--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:410--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }
    /**
     * 稿件排行--来源
     * @param siteIDstr
     * @param sourceName
     * @param tableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getTotalArticleStatisticsOfTimeBySourceName(String siteIDstr, String sourceName, String tableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
        // Oracle分页待调整
        if (isOracle()){
        	limitEnd = (pageNum - 1) * pageSize + 1;
        	limitBegin = (pageNum - 1) * pageSize + pageSize;
        }
        if (isOracle()) {
        	sql.append("select * from (");
        	sql.append("select rownum rn, z.* from (");
        }
        sql.append("Select count(1) AS article_num,a.a_source AS a_source,sum(a.a_countClick) AS total_click,sum(a.a_countClick0) AS web_click,sum(a.a_countClick1) AS wap_click,sum(a.a_countClick2) AS app_click,sum(a.a_countShare) AS total_share,");
        sql.append("sum(a.a_countShare0) AS web_share,sum(a.a_countShare1) as wap_share,sum(a.a_countShare2) as app_share,sum(a.a_countDiscuss) as total_discuss,sum(a.a_countDiscuss0) as web_discuss,sum(a.a_countDiscuss1) as wap_discuss,sum(a.a_countDiscuss2) as app_discuss");
        sql.append(" FROM "+tableName+" a");
        if(isOracle())
        	sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
        sql.append(" and a.a_orgID >0 ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" AND a.a_sourceType<=2 ");
        if(sourceName!=null&&sourceName.length()>0){
        	sql.append(" AND a.a_source =? ");
        }
        if (isOracle()) {
        	sql.append(" GROUP BY a.a_source ORDER BY a.a_source ASC");
        	sql.append(" )z where rownum <=? ");
            sql.append(" ) where rn >=? ");
        }else{
	        sql.append(" GROUP BY a.a_source ORDER BY a.a_source ASC");
	        sql.append(" LIMIT ?,?");
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            if(sourceName!=null&&sourceName.length()>0){
            	Date start = new Date();
            	 resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID,sourceName, limitBegin, limitEnd});
            	 log.error("StatisticsDAO.java:475--SQL--"+ sql.toString());
                 log.error("StatisticsDAO.java:475--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID,sourceName + "," + limitBegin + "," + limitEnd);
                 Date end = new Date();
                 System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }else{
            	Date start = new Date();
            	 resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
            	 log.error("StatisticsDAO.java:475--SQL--"+ sql.toString());
                 log.error("StatisticsDAO.java:475--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
                 Date end = new Date();
                 System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if(isOracle())
                	 i = 1;
                rowData.put("sourceName", resultSet.getString(i + 2));
                rowData.put("articleNum", resultSet.getString(i + 1));
                rowData.put("totalClick", resultSet.getString(i + 3));
                rowData.put("pcClick", resultSet.getString(i + 4));
                rowData.put("wapClick", resultSet.getString(i + 5));
                rowData.put("appClick", resultSet.getString(i + 6));
                rowData.put("totalShare", resultSet.getString(i + 7));
                rowData.put("pcShare", resultSet.getString(i + 8));
                rowData.put("wapShare", resultSet.getString(i + 9));
                rowData.put("appShare", resultSet.getString(i + 10));
                rowData.put("totalDiscuss", resultSet.getString(i + 11));
                rowData.put("pcDiscuss", resultSet.getString(i + 12));
                rowData.put("wapDiscuss", resultSet.getString(i + 13));
                rowData.put("appDiscuss", resultSet.getString(i + 14));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 稿件排行--来源
     * @param siteIDstr
     * @param sourceName
     * @param tableName
     * @param beginTime
     * @param endTime
     * @return
     * @throws E5Exception
     */
    public int countTotalArticleStatisticsOfTimeBySourceName(String siteIDstr, String sourceName, String tableName, Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("select count(1) FROM (");
        sql.append("select count(1) FROM ");
        sql.append(tableName);
        sql.append(" a");
        if(isOracle())
        	sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
        sql.append(" and a.a_orgID >0 ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
        sql.append(" AND a.a_sourceType<=2 ");
        if(sourceName!=null&&sourceName.length()>0){
        	sql.append(" AND a.a_source =? ");
        }
        sql.append(" GROUP BY a.a_source");
        sql.append(")z");
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            if(sourceName!=null&&sourceName.length()>0){
            	Date start = new Date();
           	 	resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, sourceName});
           	 log.error("StatisticsDAO.java:540--SQL--"+ sql.toString());
             log.error("StatisticsDAO.java:540--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + sourceName);
             Date end = new Date();
             System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }else{
            	Date start = new Date();
           	 	resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID});
           	 log.error("StatisticsDAO.java:540--SQL--"+ sql.toString());
             log.error("StatisticsDAO.java:540--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
             Date end = new Date();
             System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }
    /**
     * 稿件排行--栏目
     * @param siteIDstr
     * @param columnID
     * @param currentUserColumnIDs
     * @param tableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getTotalArticleStatisticsOfTimeByColumnID(String siteIDstr, String columnID, String currentUserColumnIDs, String tableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) {
        	sql.append("select * from (");
        	sql.append("select rownum rn, z.* from (");
        }
        sql.append("select count(1) AS article_count,");
        sql.append("b.col_name AS column_name,");
        sql.append("a.a_columnID AS column_id,");
        sql.append("sum(a.a_countClick) AS total_click,");
        sql.append("sum(a.a_countClick0) AS web_click,");
        sql.append("sum(a.a_countClick1) AS wap_click,");
        sql.append("sum(a.a_countClick2) AS app_click,");
        sql.append("sum(a.a_countShare) AS total_share,");
        sql.append("sum(a.a_countShare0) AS web_share,");
        sql.append("sum(a.a_countShare1) as wap_share,");
        sql.append("sum(a.a_countShare2) as app_share,");
        sql.append("sum(a.a_countDiscuss) as total_discuss,");
        sql.append("sum(a.a_countDiscuss0) as web_discuss,");
        sql.append("sum(a.a_countDiscuss1) as wap_discuss,");
        sql.append("sum(a.a_countDiscuss2) as app_discuss");
        sql.append(" FROM ");
        sql.append(tableName);
        sql.append(" a inner join xy_column b on a.a_columnID=b.sys_documentid ");
        if(isOracle())
        	sql.append(" WHERE a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss')");
        else
        	sql.append(" WHERE a.a_pubTime BETWEEN ? AND ?");
        sql.append(" and a.a_status = ? AND a.a_siteID = ? AND a_sourceType<=2 ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        sql.append(" and a.a_orgID >0 ");
        if (columnID != null && !columnID.equals("")) {
            sql.append(" AND a.a_columnID IN (" + columnID + ")");
        }else if(currentUserColumnIDs != null && !currentUserColumnIDs.equals("")){
        	sql.append(" AND a.a_columnID IN (" + currentUserColumnIDs + ")");
        }
        if (isOracle()){
        sql.append(" GROUP BY a.a_columnID,b.col_name ORDER BY a.a_columnID ASC");
        }else{
        sql.append(" GROUP BY a.a_columnID,b.col_name ORDER BY a.a_columnID ASC");
        }
        if (isOracle()){
        	sql.append(" )z where rownum <= ?");
        	sql.append(" ) where rn >= ?");
        }
        if (!isOracle())
        	sql.append(" LIMIT ?,?");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            int limitBegin = (pageNum - 1) * pageSize;
            int limitEnd = pageSize;
         // Oracle分页待调整
            if (isOracle()){
          	  limitEnd = (pageNum - 1) * pageSize + 1;
          	  limitBegin = (pageNum - 1) * pageSize + pageSize;
          }
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
            log.error("StatisticsDAO.java:628--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:628--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                rowData.put("columnName", resultSet.getString("column_name"));
                rowData.put("articleNum", resultSet.getString("article_count"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("pcClick", resultSet.getString("web_click"));
                rowData.put("wapClick", resultSet.getString("wap_click"));
                rowData.put("appClick", resultSet.getString("app_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("pcShare", resultSet.getString("web_share"));
                rowData.put("wapShare", resultSet.getString("wap_share"));
                rowData.put("appShare", resultSet.getString("app_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                rowData.put("pcDiscuss", resultSet.getString("web_discuss"));
                rowData.put("wapDiscuss", resultSet.getString("wap_discuss"));
                rowData.put("appDiscuss", resultSet.getString("app_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 稿件排行--栏目
     * @param siteIDstr
     * @param columnID
     * @param currentUserColumnIDs
     * @param tableName
     * @param beginTime
     * @param endTime
     * @return
     * @throws E5Exception
     */
    public int countTotalArticleStatisticsOfTimeByColumnID(String siteIDstr, String columnID, String currentUserColumnIDs, String tableName, Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("select count(1) FROM (");
        sql.append("select count(1) FROM ");
        sql.append(tableName);
        sql.append(" a");
        if (columnID != null && !columnID.equals("")) {
        	sql.append(" where a.a_columnID IN (" + columnID + ")");
        	if(isOracle())
        		sql.append(" and a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        	else
        		sql.append(" and a.a_pubTime BETWEEN ? AND ?");
        }else if(currentUserColumnIDs != null && !currentUserColumnIDs.equals("")){
        	sql.append(" where a.a_columnID IN (" + currentUserColumnIDs + ")");
        	if(isOracle())
        		sql.append(" and a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        	else
        		sql.append(" and a.a_pubTime BETWEEN ? AND ?");
        }else{
        	if(isOracle())
        		sql.append(" where a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        	else
        		sql.append(" where a.a_pubTime BETWEEN ? AND ?");
        }
        sql.append(" and a.a_status = ? AND a.a_siteID = ? AND a_sourceType<=2 ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        sql.append(" and a.a_orgID >0 ");
        sql.append(" GROUP BY a.a_columnID");
        sql.append(")z");
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:694--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:694--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }

    /**
     * 本月稿件点击量，分享数，评论数，通过SQL函数处理时间（获取当月起始时间点）
     * @param siteIDstr
     * @param departmentIDstr
     * @param webTableName
     * @param appTableName
     * @return
     * @throws E5Exception
     */
    //TODO 优化时间函数
    public List<Map<String, Object>> getArticleStatisticsCurrentMonthByDepartmentID(String siteIDstr, String departmentIDstr, String webTableName, String appTableName) throws E5Exception {
        Timestamp currentMonthFirstDay = TimeUtil.getMonthFirstDay(null);
        Timestamp currentMonthlastDay = TimeUtil.getMonthLastDay(null);
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentMonthFirstDay), formatToDays(currentMonthlastDay), departmentIDstr, null, sql, "days", false);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentMonthFirstDay), formatToDays(currentMonthlastDay), departmentIDstr, null, sql, "days", false);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, departmentID, Article.STATUS_PUB_DONE, siteID, departmentID});
                log.error("StatisticsDAO.java:735--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:735--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID});
                log.error("StatisticsDAO.java:735--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:735--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("days"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }

    private String formatToDays(Timestamp date) {
    	if(isOracle()){
	    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); 
	        return format.format(date);
        }else{
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            return format.format(date);
        }
	}
    private String formatDate(Timestamp date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        return format.format(date);
	}
    
	/**
     * 针对MySQL的检索条件拼接
     * @param webTableName
     * @param appTableName
     * @param first
     * @param last
     * @param departmentID
     * @param sourceType
     * @param sql
     * @param str
     */
    private void sqlForMySql(String webTableName, String appTableName, String first, String last, String departmentID,
		String sourceType, StringBuilder sql, String str, Boolean isPersonal) {
		sql.append("SELECT c.days as "+str+",sum(c.count) AS article_num,sum(c.total_click) AS total_click,sum(c.total_share) AS total_share,sum(c.total_discuss) AS total_discuss");
		if(str == "hours")
			sql.append(" FROM ((SELECT DATE_FORMAT(a.a_pubTime, '%m%d%H') days,COUNT(1) count,sum(a.a_countClick) AS total_click,sum(a.a_countShare) AS total_share,	sum(a.a_countDiscuss) AS total_discuss");
		else
			sql.append(" FROM ((SELECT DATE_FORMAT(a.a_pubTime, '%Y%m%d') days,COUNT(1) count,sum(a.a_countClick) AS total_click,sum(a.a_countShare) AS total_share,	sum(a.a_countDiscuss) AS total_discuss");
		sql.append(" FROM " + webTableName + " a ");
		if(isPersonal){
			sql.append(" where a.SYS_AUTHORID = ?");
			sql.append(" and (a.a_pubTime between DATE_FORMAT('"+first+"', '%Y-%m-%d %T:')  and DATE_FORMAT('"+last+"', '%Y-%m-%d %T:') )");
		}else{
			sql.append(" where (a.a_pubTime between DATE_FORMAT('"+first+"', '%Y-%m-%d %T:')  and DATE_FORMAT('"+last+"', '%Y-%m-%d %T:') )");
			sql.append("and a.SYS_AUTHORID>0 ");
		}
		sql.append(" and a.a_status = ? AND a.a_siteID = ?  ");
    
    	if(sourceType != null && !sourceType.equals("")){
    		if("3".equals(sourceType)){
            sql.append(" AND a_sourceType = 3");
    		}else{
    			sql.append("AND a_sourceType<=2 ");
    		}
        }else{
            sql.append("AND a_sourceType<=2 ");
            if (departmentID != null && !departmentID.equals("")) {
            	sql.append(" AND a.a_orgID = ?");
            }else{
            	sql.append(" AND a.a_orgID >0");
            }       
        }
    	
    	sql.append(" GROUP BY days)");
    	if(str == "hours")
    		sql.append(" UNION ALL(SELECT DATE_FORMAT(b.a_pubTime, '%m%d%H') days,COUNT(1) count,sum(b.a_countClick) AS total_click,sum(b.a_countShare) AS total_share,	sum(b.a_countDiscuss) AS total_discuss");
    	else
    		sql.append(" UNION ALL(SELECT DATE_FORMAT(b.a_pubTime, '%Y%m%d') days,COUNT(1) count,sum(b.a_countClick) AS total_click,sum(b.a_countShare) AS total_share,	sum(b.a_countDiscuss) AS total_discuss");
    	sql.append(" FROM " + appTableName + " b ");
    	if(isPersonal){
    		sql.append(" where b.SYS_AUTHORID = ?");
    		sql.append(" and (b.a_pubTime between DATE_FORMAT('"+first+"', '%Y-%m-%d %T:')  and DATE_FORMAT('"+last+"', '%Y-%m-%d %T:'))");
    	}else{
    		sql.append(" where (b.a_pubTime between DATE_FORMAT('"+first+"', '%Y-%m-%d %T:')  and DATE_FORMAT('"+last+"', '%Y-%m-%d %T:'))");
    		sql.append("and b.SYS_AUTHORID>0 ");
    	}
    	sql.append(" and b.a_status = ?  AND b.a_siteID = ? ");
    	if(sourceType != null && !sourceType.equals("")){
    		if("3".equals(sourceType)){
            sql.append(" AND a_sourceType = 3");
    		}else{
    			sql.append(" AND a_sourceType<=2 ");
    		}
        }else{
            sql.append(" AND a_sourceType<=2 ");
            if (departmentID != null && !departmentID.equals("")) {
            	sql.append(" AND b.a_orgID = ?");
            }else{
            	sql.append(" AND b.a_orgID >0");
            }       
        }
    	sql.append(" GROUP BY days)) c GROUP BY "+str+" ORDER BY c.days ASC");
		
	}
	/**
	 * 针对Oracle的检索条件拼接
	 * @param webTableName
	 * @param appTableName
	 * @param first
	 * @param last
	 * @param departmentID
	 * @param sourceType
	 * @param sql
	 * @param str
	 */
    private void sqlForOracle(String webTableName, String appTableName, String first, String last, String departmentID,
		String sourceType, StringBuilder sql, String str, Boolean isPersonal) {
    	
    	sql.append("SELECT c.days as "+str+",sum(c.count) AS article_num,sum(c.total_click) AS total_click,sum(c.total_share) AS total_share,sum(c.total_discuss) AS total_discuss");
    	if(str == "hours")
    		sql.append(" FROM ((SELECT to_char(a.a_pubTime, 'MMddhh24') days,COUNT(1) count,sum(a.a_countClick) AS total_click,sum(a.a_countShare) AS total_share,sum(a.a_countDiscuss) AS total_discuss");
    	else	
    		sql.append(" FROM ((SELECT to_char(a.a_pubTime, 'yyyyMMdd') days,COUNT(1) count,sum(a.a_countClick) AS total_click,sum(a.a_countShare) AS total_share,sum(a.a_countDiscuss) AS total_discuss");
    	sql.append(" FROM " + webTableName + " a");
    	if(isPersonal){
    		sql.append(" where a.SYS_AUTHORID = ? ");
    		sql.append(" and (a.a_pubTime BETWEEN to_date('"+first+"','yyyyMMdd hh24:mi:ss') AND to_date('"+last+"','yyyyMMdd hh24:mi:ss'))");
            sql.append(" AND a.a_status = ? AND a.a_siteID = ? ");
    	}else{
    		sql.append(" where (a.a_pubTime BETWEEN to_date('"+first+"','yyyyMMdd hh24:mi:ss') AND to_date('"+last+"','yyyyMMdd hh24:mi:ss'))");
            sql.append("  and a.SYS_AUTHORID>0  AND a.a_status = ? AND a.a_siteID = ? ");
    	}
        if(sourceType != null && !sourceType.equals("")){
    		if("3".equals(sourceType)){
            sql.append(" AND a.a_sourceType = 3");
    		}else{
    			sql.append(" AND a.a_sourceType<=2 ");
    		}
        }else{
        	sql.append(" AND a.a_sourceType<=2 ");
            if (departmentID != null && !departmentID.equals("")) {
            	sql.append(" AND a.a_orgID = ?");
            }else{
            	sql.append(" AND a.a_orgID >0");
            }       
        }
        
        sql.append(" GROUP BY a.a_pubTime)");
        if(str == "hours")
        	sql.append(" UNION ALL (SELECT to_char(b.a_pubTime, 'MMddhh24') days,COUNT(1) count,sum(b.a_countClick) AS total_click,sum(b.a_countShare) AS total_share,  sum(b.a_countDiscuss) AS total_discuss");
        else
        	sql.append(" UNION ALL (SELECT to_char(b.a_pubTime, 'yyyyMMdd') days,COUNT(1) count,sum(b.a_countClick) AS total_click,sum(b.a_countShare) AS total_share,  sum(b.a_countDiscuss) AS total_discuss");
        sql.append(" FROM " + appTableName + " b ");
        if(isPersonal){
        	sql.append(" where b.SYS_AUTHORID = ?");
        	sql.append(" and (b.a_pubTime BETWEEN to_date('"+first+"','yyyyMMdd hh24:mi:ss') AND to_date('"+last+"','yyyyMMdd hh24:mi:ss'))");
            sql.append(" AND b.a_status = ?  AND b.a_siteID = ? ");    		
    	}else{
    		sql.append(" where (b.a_pubTime BETWEEN to_date('"+first+"','yyyyMMdd hh24:mi:ss') AND to_date('"+last+"','yyyyMMdd hh24:mi:ss'))");
    		sql.append(" and b.SYS_AUTHORID>0  AND b.a_status = ?  AND b.a_siteID = ? ");
    	}
        if(sourceType != null && !sourceType.equals("")){
    		if("3".equals(sourceType)){
            sql.append(" AND b.a_sourceType = 3");
    		}else{
    			sql.append(" AND b.a_sourceType<=2 ");
    		}
        }else{
        	sql.append(" AND b.a_sourceType<=2 ");
            if (departmentID != null && !departmentID.equals("")) {
            	sql.append(" AND b.a_orgID = ?");
            }else{
            	sql.append(" AND b.a_orgID >0");
            }       
        }
        sql.append(" GROUP BY b.a_pubTime))c GROUP BY c.days ORDER BY c.days ASC");
        
		
	}
	/**
	 * 本周稿件点击量，分享数，评论数，通过SQL函数处理时间（获取本周起始时间点）
	 * @param siteIDstr
	 * @param departmentIDstr
	 * @param webTableName
	 * @param appTableName
	 * @return
	 * @throws E5Exception
	 */
    public List<Map<String, Object>> getArticleStatisticsCurrentWeekByDepartmentID(String siteIDstr, String departmentIDstr, String webTableName, String appTableName) throws E5Exception {
        Timestamp currentWeekFirstDay = TimeUtil.getWeekFirstDay(null);
        Timestamp currentWeeklastDay = TimeUtil.getWeekLastDay(null);
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentWeekFirstDay), formatToDays(currentWeeklastDay), departmentIDstr, null, sql, "days", false);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentWeekFirstDay), formatToDays(currentWeeklastDay), departmentIDstr, null, sql, "days", false);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, departmentID, Article.STATUS_PUB_DONE, siteID, departmentID});
                log.error("StatisticsDAO.java:948--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:948--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID});
                log.error("StatisticsDAO.java:949--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:949--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("days"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 24H稿件点击量，分享数，评论数，通过SQL函数处理时间（获取当天起始时间点）
     * @param siteIDstr
     * @param departmentIDstr
     * @param webTableName
     * @param appTableName
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getArticleStatisticsCurrent24HoursByDepartmentID(String siteIDstr, String departmentIDstr, String webTableName, String appTableName) throws E5Exception {
        Timestamp currentToday0 = TimeUtil.getTodayZero(null);
        Timestamp currentToday24 = TimeUtil.getTodayTwe(null);
        
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentToday0), formatToDays(currentToday24), departmentIDstr, null, sql, "hours", false);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentToday0), formatToDays(currentToday24), departmentIDstr, null, sql, "hours", false);
        }
    	
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            if (departmentIDstr != null && !departmentIDstr.equals("")) {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, departmentID, Article.STATUS_PUB_DONE, siteID, departmentID});
                log.error("StatisticsDAO.java:1001--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:1001--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + departmentID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            } else {
            	Date start = new Date();
                resultSet = conn.executeQuery(sql.toString(), new Object[]{Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID});
                log.error("StatisticsDAO.java:1002--SQL--"+ sql.toString());
                log.error("StatisticsDAO.java:1002--params--"+ Article.STATUS_PUB_DONE + "," + siteID + "," + Article.STATUS_PUB_DONE + "," + siteID);
                Date end = new Date();
                System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            }
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("hours"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }
    /**
     * 个人本月稿件点击量，分享数，评论数，通过SQL函数处理时间（获取当月起始时间点）
     * @param siteIDstr
     * @param personIDstr
     * @param webTableName
     * @param appTableName
     * @param sourceType
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getArticleStatisticsCurrentMonthByPersonID(String siteIDstr, String personIDstr, String webTableName, String appTableName, String sourceType) throws E5Exception {
    	Timestamp currentMonthFirstDay = TimeUtil.getMonthFirstDay(null);
        Timestamp currentMonthlastDay = TimeUtil.getMonthLastDay(null);
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int personID = !StringUtils.isBlank(personIDstr)?Integer.parseInt(personIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentMonthFirstDay), formatToDays(currentMonthlastDay), null, sourceType, sql, "days", true);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentMonthFirstDay), formatToDays(currentMonthlastDay), null, sourceType, sql, "days", true);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{personID, Article.STATUS_PUB_DONE, siteID, personID, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1052--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1052--params--"+ personID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + personID + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("days"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }
    /**
     * 个人本周稿件点击量，分享数，评论数，通过SQL函数处理时间（获取本周起始时间点）
     * @param siteIDstr
     * @param personIDstr
     * @param webTableName
     * @param appTableName
     * @param sourceType
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getArticleStatisticsCurrentWeekByPersonID(String siteIDstr, String personIDstr, String webTableName, String appTableName, String sourceType) throws E5Exception {
    	Timestamp currentWeekFirstDay = TimeUtil.getWeekFirstDay(null);
        Timestamp currentWeeklastDay = TimeUtil.getWeekLastDay(null);
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int personID = !StringUtils.isBlank(personIDstr)?Integer.parseInt(personIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentWeekFirstDay), formatToDays(currentWeeklastDay), null, sourceType, sql, "days", true);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentWeekFirstDay), formatToDays(currentWeeklastDay), null, sourceType, sql, "days", true);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{personID, Article.STATUS_PUB_DONE, siteID, personID, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1100--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1100--params--"+ personID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + personID + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("days"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 个人24H稿件点击量，分享数，评论数，通过SQL函数处理时间（获取当天起始时间点）
     * @param siteIDstr
     * @param personIDstr
     * @param webTableName
     * @param appTableName
     * @param sourceType
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getArticleStatisticsCurrent24HoursByPersonID(String siteIDstr, String personIDstr, String webTableName, String appTableName,String sourceType) throws E5Exception {
    	Timestamp currentToday0 = TimeUtil.getTodayZero(null);
        Timestamp currentToday24 = TimeUtil.getTodayTwe(null);
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int personID = !StringUtils.isBlank(personIDstr)?Integer.parseInt(personIDstr):0;
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, formatToDays(currentToday0), formatToDays(currentToday24), null, sourceType, sql, "hours", true);
        }else{
        	sqlForMySql(webTableName, appTableName, formatToDays(currentToday0), formatToDays(currentToday24), null, sourceType, sql, "hours", true);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{personID, Article.STATUS_PUB_DONE, siteID, personID, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1147--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1147--params--"+ personID + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + personID + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("date", resultSet.getString("hours"));
                rowData.put("articleNum", resultSet.getString("article_num"));
                rowData.put("totalClick", resultSet.getString("total_click"));
                rowData.put("totalShare", resultSet.getString("total_share"));
                rowData.put("totalDiscuss", resultSet.getString("total_discuss"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }

    public Map<String, Object> getCurrentMonthWorkStatisticsOfPersonByTableName(String siteIDstr, String personIDstr, String tableName, String sourceType) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int personID = Integer.parseInt(personIDstr);
        Timestamp currentMonthFirstDay = TimeUtil.getMonthFirstDay(null);
        Timestamp currentMonthLastDay = TimeUtil.getMonthLastDay(null);
        sql.append("select count(1),a_type from " + tableName);
        sql.append(" where SYS_AUTHORID =? ");
        if(isOracle())
        	sql.append(" and a_pubTime BETWEEN to_date('"+formatDate(currentMonthFirstDay)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(currentMonthLastDay)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" and a_pubTime BETWEEN '"+currentMonthFirstDay+"' and '"+currentMonthLastDay+"' ");
        sql.append(" AND a_status=? ");
        sql.append(" AND a_siteID=? ");
        if("3".equals(sourceType)){
        	sql.append(" AND a_sourceType = 3");
        }else{
        	sql.append(" AND a_sourceType<=2 ");
        }
        sql.append(" group by a_type order by a_type");
        DBSession conn = null;
        IResultSet resultSet = null;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{personID, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1188--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1188--params--"+ personID + "," + currentMonthFirstDay + "," + currentMonthLastDay + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                resultMap.put(resultSet.getString(2), resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultMap;
    }

    public List<Map<String, Object>> getArticleRankingGeneral(String tableName, String linkedTableName, String countClass, String orderClass, String siteIDstr, String otherCondition, String str) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) {
        	sql.append("select * from (");
        }
        sql.append("SELECT a.st_id as articleID,");
        sql.append("b.SYS_TOPIC as articleName,c.col_name as columnName,b.SYS_AUTHORS as authorName, ");
        sql.append("sum(a.st_" + countClass + ") AS " + countClass);
        sql.append(" FROM (" + tableName + " a");
    	sql.append(" INNER JOIN " + linkedTableName + " b ON a.st_id = b.SYS_DOCUMENTID) inner join xy_column c on b.a_columnid = c.sys_documentid ");
    	sql.append(" WHERE " + otherCondition);
        sql.append(" AND b.a_siteID = ? AND a_sourceType<=2 and b.sys_deleteflag=0 ");
        if (isOracle()) {
        	sql.append(" GROUP BY a.st_id,b.SYS_TOPIC,c.col_name,b.SYS_AUTHORS");
            sql.append(" ORDER BY " + countClass + " " + orderClass);
        	sql.append(") where rownum <= 100");
        }else{
        	sql.append(" GROUP BY a.st_id");
        	sql.append(" ORDER BY " + countClass + " " + orderClass);
        	sql.append(" LIMIT 0,100");
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            log.error("StatisticsDAO.java:1230--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1230--params--"+ siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                rowData.put("articleName", resultSet.getString("articleName"));
                if("countDiscuss".equals(countClass)){
                	if("0".equals(resultSet.getString("countDiscuss"))) continue;
                	rowData.put("countDiscuss", resultSet.getString("countDiscuss"));
                }else{
                	rowData.put("countClick", resultSet.getString("countClick"));
                }
                //rowData.put("articleID", resultSet.getString("articleID"));
                String colName = resultSet.getString("columnName");
                if(StringUtils.isBlank(str))
                	rowData.put("columnNameAll", colName);
                if(colName.contains("~")){
                	colName = colName.substring(colName.lastIndexOf("~")+1);
                }
                rowData.put("columnName", colName);
                rowData.put("authorName", resultSet.getString("authorName"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }
    /**
     * 栏目点击量
     * @param tableName
     * @param orderClass
     * @param channelCode
     * @param otherCondition
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getColumnClickRanking(String tableName, String orderClass, String channelCode, String otherCondition, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) 
        	sql.append("select * from(");
        sql.append("select * from(");
        sql.append("SELECT a.st_id as column_id,");
        sql.append("b.col_name as column_name,b.col_cascadeName as columnAll,");
        sql.append("sum(a.st_countClick) as count_click");
        sql.append(" FROM " + tableName + " a");
    	sql.append(" INNER JOIN xy_column b ON a.st_id = b.SYS_DOCUMENTID");
    	sql.append(" WHERE " + otherCondition);
        switch (channelCode) {
            case "channelWeb":
                sql.append(" a.st_channel = 0");
                break;
            case "channelApp":
                sql.append(" a.st_channel = 1");
                break;
            default:
                sql.append(" a.st_channel is not null");
                break;
        }
        sql.append(" AND a.st_siteID = ?");
        sql.append(" GROUP BY a.st_id");
        if (isOracle())
        	sql.append(",b.col_name,b.col_cascadeName");
        sql.append(" ORDER BY count_click");
        switch (orderClass) {
        case "top":
        	sql.append(" DESC");
        	break;
        case "last":
        	sql.append(" ASC");
        	break;
        default:
        	break;
        }
        sql.append(")o where o.count_click>0");
        if (isOracle()) 
        	sql.append(") where rownum <= 20");
        if (!isOracle())
        	sql.append(" LIMIT 0,20");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            log.error("StatisticsDAO.java:1319--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1319--params--"+ siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("columnID", resultSet.getString("column_id"));
                rowData.put("columnName", resultSet.getString("column_name"));
                rowData.put("columnNameAll", resultSet.getString("columnAll"));
                rowData.put("countClick", resultSet.getString("count_click"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }
    /**
     * 栏目订阅量排行榜
     * @param tableName
     * @param orderClass
     * @param channelCode
     * @param otherCondition
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getColumnSubscribeRanking(String tableName, String orderClass, String channelCode, String otherCondition, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        
        if (isOracle()) 
        	sql.append("select * from(");
        sql.append("select * from(");
        sql.append("SELECT a.st_id as column_id,");
        sql.append("b.col_name as column_name,b.col_cascadeName as columnAll,");
        sql.append("sum(a.st_countSub) as count_sub");
        sql.append(" FROM " + tableName + " a");
        sql.append(" INNER JOIN xy_column b ON a.st_id = b.SYS_DOCUMENTID");
        sql.append(" WHERE " + otherCondition);
        switch (channelCode) {
            case "channelWeb":
                sql.append(" a.st_channel = 0");
                break;
            case "channelApp":
                sql.append(" a.st_channel = 1");
                break;
            default:
                sql.append(" a.st_channel is not null");
                break;
        }
        sql.append(" AND a.st_siteID = ?");
        sql.append(" GROUP BY a.st_id");
        if (isOracle())
        	sql.append(",b.col_name,b.col_cascadeName");
        sql.append(" ORDER BY count_sub");
        switch (orderClass) {
        case "top":
        	sql.append(" DESC");
        	break;
        case "last":
        	sql.append(" ASC");
        	break;
        default:
        	break;
        }
        sql.append(")o where o.count_sub>0");
        if (isOracle())
        	sql.append(") where rownum <= 20");
        if (!isOracle())
        	sql.append(" LIMIT 0,20");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            log.error("StatisticsDAO.java:1340--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1340--params--"+ siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("columnID", resultSet.getString("column_id"));
                rowData.put("columnName", resultSet.getString("column_name"));
                rowData.put("columnNameAll", resultSet.getString("columnAll"));
                rowData.put("countSub", resultSet.getString("count_sub"));
                if(!"0".equals(resultSet.getString("count_sub"))){
                	resultList.add(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;

    }
    /**
     * 栏目稿件量排行榜
     * @param tableName
     * @param orderClass
     * @param beginTime
     * @param endTime
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getColumnArticleRanking(String tableName, String orderClass, Timestamp beginTime, Timestamp endTime, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) 
        	sql.append("select * from(");
        sql.append("select * from(");
        sql.append("SELECT count(1) as article_num, a.a_columnID as column_id, b.col_name as column_name");
        sql.append(" FROM " + tableName + " a inner join xy_column b on a.a_columnID = b.sys_documentid ");
        if (isOracle())
            sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
            else
            sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
            sql.append(" AND a.a_status=? ");
            sql.append(" AND a.a_siteID=? ");
            sql.append(" AND a.a_sourceType<=2 ");
        sql.append(" GROUP BY a.a_columnID,b.col_name ORDER BY article_num ");
        switch (orderClass) {
        case "top":
        	sql.append(" DESC");
        	break;
        case "last":
        	sql.append(" ASC");
        	break;
        default:
        	break;
        }
        sql.append(")o where o.article_num>0");
        if (isOracle())
        	sql.append(") where rownum <= 20");
        if (!isOracle())
        	sql.append(" LIMIT 0,20");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{ Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1463--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1463--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("columnID", resultSet.getString("column_id"));
                String colName = resultSet.getString("column_name");
                rowData.put("columnNameAll", colName);
                if(colName.contains("~")){
                	colName = colName.substring(colName.lastIndexOf("~")+1);
                }
                rowData.put("columnName", colName);
                rowData.put("countArticle", resultSet.getString("article_num"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 栏目稿件点击量排行榜
     * @param tableName
     * @param column
     * @param orderClass
     * @param otherCondition
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getColumnArticleClickRanking(String tableName, String column, String orderClass, String otherCondition, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) 
        	sql.append("select * from(");
        sql.append("select * from(");
        sql.append("SELECT sum(a.st_countClick) as count_click,");
        sql.append(" a." + column + " as column_id,b.col_cascadeName as columnAll,");
        sql.append("b.col_name as column_name");
        sql.append(" FROM " + tableName + " a");
    	sql.append(" INNER JOIN xy_column b ON a." + column + " = b.SYS_DOCUMENTID");
    	sql.append(" WHERE " + otherCondition);
        sql.append(" AND a.st_siteID = ?");
        if (isOracle()){ 
        	sql.append(" GROUP BY a." + column + ",b.col_name,b.col_cascadeName");
        	sql.append(") where rownum <= 20");
        }else
        	 sql.append(" GROUP BY column_id");
        sql.append(" ORDER BY count_click");
        switch (orderClass) {
            case "top":
                sql.append(" DESC");
                break;
            case "last":
                sql.append(" ASC");
                break;
            default:
                break;
        }
        sql.append(")o where o.count_click>0");
        if (!isOracle()) 
        	sql.append(" LIMIT 0,20");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            log.error("StatisticsDAO.java:1533--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1533--params--"+ siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("columnID", resultSet.getString("column_id"));
                rowData.put("columnName", resultSet.getString("column_name"));
                rowData.put("columnNameAll", resultSet.getString("columnAll"));
                rowData.put("countArticleClick", resultSet.getString("count_click"));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;


    }
    /**
     * 栏目稿件评论排行榜
     * @param tableName
     * @param column
     * @param orderClass
     * @param otherCondition
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public List<Map<String, Object>> getColumnArticleDiscussRanking(String tableName, String column, String orderClass, String otherCondition, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) 
        	sql.append("select * from(");
        sql.append("select * from(");
        sql.append("SELECT sum(a.st_countDiscuss) as count_discuss,");
        sql.append(" a." + column + " as column_id,b.col_cascadeName as columnAll,");
        sql.append("b.col_name as column_name");
        sql.append(" FROM " + tableName + " a");
        sql.append(" INNER JOIN xy_column b ON a." + column + " = b.SYS_DOCUMENTID");
        sql.append(" WHERE " + otherCondition);
        sql.append(" AND a.st_siteID = ?");
        if (isOracle()) {
        	sql.append(" GROUP BY a." + column + ",b.col_name,b.col_cascadeName");
        	sql.append(") where rownum <= 20");
        }else
        sql.append(" GROUP BY column_id");
        sql.append(" ORDER BY count_discuss");
        switch (orderClass) {
            case "top":
                sql.append(" DESC");
                break;
            case "last":
                sql.append(" ASC");
                break;
            default:
                break;
        }
        sql.append(")o where o.count_discuss>0");
        if (!isOracle()) 
        	sql.append(" LIMIT 0,20");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            log.error("StatisticsDAO.java:1601--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1601--params--"+ siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("columnID", resultSet.getString("column_id"));
                rowData.put("columnName", resultSet.getString("column_name"));
                rowData.put("columnNameAll", resultSet.getString("columnAll"));
                rowData.put("countDiscuss", resultSet.getString("count_discuss"));
                if(!"0".equals(resultSet.getString("count_discuss"))){
                	resultList.add(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    
    private Object formatToDaysHour(Timestamp date) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); 
        return format.format(date);
	
	}
    /**
     * 统计--栏目总览：栏目明细，统计有数据的根级栏目
     * @param channel
     * @param colTableName
     * @param beginTime
     * @param endTime
     * @param siteIDstr
     * @param pageSize
     * @param pageNum
     * @param columnIds 
     * @return
     * @throws E5Exception
     */
	public List<Map<String, Object>> getColumnInfoXyStatCol(int channel, String colTableName, Timestamp beginTime, Timestamp endTime, String siteIDstr, int pageSize, int pageNum, String columnIds) throws E5Exception {
    	
    	String dateStr = colTableName.equals("xy_statcolhour")?"st_hour":"st_date";
    	List<Object> sqlParamList = new ArrayList<Object>();
    	StringBuilder sql = new StringBuilder();
    	int siteID = Integer.parseInt(siteIDstr);
    	if (isOracle()) {
    		sql.append("select * from (");
        	sql.append("select rownum rn, z.* from (");
        }
    	sql.append(" SELECT DISTINCT a.st_id AS column_id, sum(a.st_countClick) AS column_click, b.col_name AS column_name, b.col_cascadeName AS column_cascadeName FROM xy_column b,");
    	sql.append(colTableName);
    	if(StringUtils.isNotBlank(columnIds)){
    		sql.append(" a where b.SYS_DOCUMENTID in ("+columnIds+") ");
    		if(isOracle() && !"st_hour".equals(dateStr))
    			sql.append(" and a.st_id = b.sys_documentid AND a."+dateStr+" BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') AND a.st_siteid = ? AND a.st_channel=?");
    		else
    			sql.append(" and a.st_id = b.sys_documentid AND a."+dateStr+" between ?  and ? AND a.st_siteid = ? AND a.st_channel=?");
    	}
    	else{
    		if(isOracle() && !"st_hour".equals(dateStr))
    			sql.append(" a where a.st_id = b.sys_documentid AND a."+dateStr+" BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') AND a.st_siteid = ? AND a.st_channel=?");
    		else
    			sql.append(" a where a.st_id = b.sys_documentid AND a."+dateStr+" between ?  and ? AND a.st_siteid = ? AND a.st_channel=?");
    	}
    	sql.append(" and a.st_countClick > 0 ");
    	if(isOracle())
    		sql.append(" GROUP BY a.st_id, b.col_name, b.col_cascadeName ORDER BY column_click desc ");
    	else
    		sql.append(" GROUP BY column_id ORDER BY column_click desc ");
    	if("st_hour".equals(dateStr)){
        	sqlParamList.add(formatToDaysHour(beginTime));
        	sqlParamList.add(formatToDaysHour(endTime));
        	}else{
        		sqlParamList.add(formatDate(beginTime));
            	sqlParamList.add(formatDate(endTime));
        	}
    	sqlParamList.add(siteID);
    	sqlParamList.add(channel);
    	if (isOracle()){
    		sql.append(" )z where rownum <= ?");
            sql.append(" ) where rn >= ?");
    	}else
    		sql.append(" LIMIT ?,?");
    	int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
     // Oracle分页待调整
        if (isOracle()){
      	  limitEnd = (pageNum - 1) * pageSize + 1;
      	  limitBegin = (pageNum - 1) * pageSize + pageSize;
      }
        sqlParamList.add(limitBegin);
        sqlParamList.add(limitEnd);
    	DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
             conn = Context.getDBSession();
             Date start = new Date();
             resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
             log.error("StatisticsDAO.java:1691--SQL--"+ sql.toString());
             log.error("StatisticsDAO.java:1691--params--"+ sqlParamList);
             Date end = new Date();
             System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
             while (resultSet.next()) {
            	 Map<String, Object> rowData = new LinkedHashMap<>();
            	 int columnID = resultSet.getInt("column_id");
                 if(columnID==0) continue;
                 rowData.put("columnID", columnID);
                 rowData.put("columnName", resultSet.getString("column_name"));
                 rowData.put("cascadeName", resultSet.getString("column_cascadeName"));
                 resultList.add(rowData);
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             ResourceMgr.closeQuietly(resultSet);
             ResourceMgr.closeQuietly(conn);
         }
         
         return resultList;
    }
	/**
     * 统计--栏目总览：栏目明细，统计有数据的根级栏目数量
	 * @param channel
	 * @param colTableName
	 * @param beginTime
	 * @param endTime
	 * @param siteIDstr
	 * @param columnIds 
	 * @return
	 * @throws E5Exception
	 */
	public int countColumnInfoXyStatCol(int channel, String colTableName, Timestamp beginTime, Timestamp endTime, String siteIDstr, String columnIds) throws E5Exception {
		String dateStr = colTableName.equals("xy_statcolhour")?"st_hour":"st_date";
    	List<Object> sqlParamList = new ArrayList<Object>();
    	StringBuilder sql = new StringBuilder();
    	int siteID = Integer.parseInt(siteIDstr);
    	sql.append(" SELECT count(DISTINCT a.st_id) as column_count FROM xy_column b,");
    	sql.append(colTableName);
    	if(StringUtils.isNotBlank(columnIds)){
    		sql.append(" a where b.SYS_DOCUMENTID in ("+columnIds+") ");
    		if(isOracle() && !"st_hour".equals(dateStr))
    			sql.append(" and a.st_id = b.sys_documentid AND a."+dateStr+" BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') AND a.st_siteid = ? AND a.st_channel=?");
    		else
    			sql.append(" and a.st_id = b.sys_documentid AND a."+dateStr+" between ?  and ? AND a.st_siteid = ? AND a.st_channel=?");
    	}
    	else{
    		if(isOracle() && !"st_hour".equals(dateStr))
    			sql.append(" a where a.st_id = b.sys_documentid AND a."+dateStr+" BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') AND a.st_siteid = ? AND a.st_channel=?");
    		else
    			sql.append(" a where a.st_id = b.sys_documentid AND a."+dateStr+" between ?  and ? AND a.st_siteid = ? AND a.st_channel=?");
    	}
    	sql.append(" and a.st_countClick > 0 ");
    	if("st_hour".equals(dateStr)){
        	sqlParamList.add(formatToDaysHour(beginTime));
        	sqlParamList.add(formatToDaysHour(endTime));
        }else{
        	sqlParamList.add(formatDate(beginTime));
            sqlParamList.add(formatDate(endTime));
        }
    	sqlParamList.add(siteID);
    	sqlParamList.add(channel);
    	DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
            log.error("StatisticsDAO.java:1749--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1749--params--"+ sqlParamList);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            if (resultSet.next()) {
            	countNum = resultSet.getInt("column_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
	}
   /**
    * 统计--栏目总览：点击栏目下拉框，根据勾选栏目进行检索展示统计结果
    * @param columnID
    * @return
    */
	public List<Map<String, Object>> getColumnInfoByid(String columnID){
		
    	List<Object> sqlParamList = new ArrayList<Object>();
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT a.sys_documentid column_id, a.col_name AS column_name, a.col_cascadeName AS column_cascadeName FROM xy_column a ");
    	sql.append(" where a.sys_documentid in (" + columnID + ")");
    	
    	DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
             conn = Context.getDBSession();
             Date start = new Date();
             resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
             log.error("StatisticsDAO.java:1781--SQL--"+ sql.toString());
             log.error("StatisticsDAO.java:1781--params--"+ sqlParamList);
             Date end = new Date();
             System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
             while (resultSet.next()) {
            	 Map<String, Object> rowData = new LinkedHashMap<>();
                 rowData.put("columnID", resultSet.getString("column_id"));
                 rowData.put("columnName", resultSet.getString("column_name"));
                 rowData.put("cascadeName", resultSet.getString("column_cascadeName"));
                 resultList.add(rowData);
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             ResourceMgr.closeQuietly(resultSet);
             ResourceMgr.closeQuietly(conn);
         }
         
         return resultList;
	}
	
	
    public List<Map<String, Object>> countArticleClickOfTimeByColumnIDs(String newColumnIDS, String tableName, Timestamp beginTime, Timestamp endTime, int channel, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("SELECT b.sys_documentid as column_id, sum(a.st_countClick) as column_click,");
        sql.append(" sum(a.st_countSub) column_sub");
        sql.append(" FROM " + tableName + " a, xy_column b ");
        sql.append(" WHERE a.st_id = b.sys_documentid AND a.st_channel=?");
        if(StringUtils.isNotEmpty(newColumnIDS)){
	        sql.append(" AND a.st_id in (");
	        sql.append(newColumnIDS);
	        sql.append(")");
        }
        List<Object> sqlParamList = new ArrayList<>();
        sqlParamList.add(channel);
        switch (tableName) {
            case "xy_statcolhour":
                break;
            case "xy_statcol":
            	if (isOracle()){
                    sql.append(" AND a.st_date BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
                    sqlParamList.add(formatDate(beginTime));
	                sqlParamList.add(formatDate(endTime));
            	}else{
	                sql.append(" AND a.st_date between ?  and ?");
	                sqlParamList.add(beginTime);
	                sqlParamList.add(endTime);
                }
                break;
            default:
                throw new E5Exception("Wrong Table Name!");
        }
        sql.append(" AND a.st_siteID = ?");
        sqlParamList.add(siteID);
        sql.append(" GROUP BY a.st_id ");
        sql.append(" order by column_click desc ");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
            log.error("StatisticsDAO.java:1838--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1838--params--"+ sqlParamList);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
            	Map<String, Object> resultData = new LinkedHashMap<>();
            	resultData.put("columnID", resultSet.getInt("column_id"));
                resultData.put("columnClick", resultSet.getString("column_click"));
                if(channel==1)resultData.put("columnSub", resultSet.getString("column_sub"));
                resultList.add(resultData);
            }
//            else{
//            	resultData.put("columnClick", "0");
//                if(channel==1)resultData.put("columnSub", "0");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    
    public Map<String, Object> countArticleClickOfTimeByColumnID(String columnIDstr, String tableName, Timestamp beginTime, Timestamp endTime, int channel, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int columnID = !StringUtils.isBlank(columnIDstr)?Integer.parseInt(columnIDstr):0;
        
        sql.append("SELECT sum(a.st_countClick) as column_click,");
        sql.append(" sum(a.st_countSub) column_sub");
        sql.append(" FROM " + tableName + " a");
        sql.append(" WHERE a.st_channel=?");
        sql.append(" AND a.st_id=?");
        List<Object> sqlParamList = new ArrayList<>();
        sqlParamList.add(channel);
        sqlParamList.add(columnID);
        switch (tableName) {
            case "xy_statcolhour":
                break;
            case "xy_statcol":
            	if (isOracle())
                    sql.append(" AND a.st_date BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
                    else
                    	sql.append(" AND a.st_date between ?  and ?");
                sqlParamList.add(formatDate(beginTime));
                sqlParamList.add(formatDate(endTime));
                break;
            default:
                throw new E5Exception("Wrong Table Name!");
        }
        sql.append(" AND a.st_siteID = ?");
        sqlParamList.add(siteID);
        sql.append(" GROUP BY a.st_id ");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultData = new LinkedHashMap<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
            log.error("StatisticsDAO.java:1895--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1895--params--"+ sqlParamList);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            if (resultSet.next()) {
                resultData.put("columnClick", resultSet.getString("column_click"));
                if(channel==1)resultData.put("columnSub", resultSet.getString("column_sub"));
            }else{
            	resultData.put("columnClick", "0");
                if(channel==1)resultData.put("columnSub", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultData;
    }
    
    public List<Map<String, Object>> countArticleOfTimeByColumnIDs(String newColumnIDS, String tableName, Timestamp beginTime, Timestamp endTime, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("SELECT a.a_columnID as column_id, count(1) as article_num");
        sql.append(" FROM " + tableName + " a ");
        if(isOracle())
        	sql.append(" where a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss')");
        else
        	sql.append(" where a.a_pubTime between ? AND ?");
        sql.append(" and  a.a_status = ? AND a.a_siteID = ? and (a.a_sourceType = 0 or a.a_sourceType = 1 or a.a_sourceType = 2)");
        if(StringUtils.isNotEmpty(newColumnIDS)){
	        sql.append(" AND a.a_columnID in (");
	        sql.append(newColumnIDS);
	        sql.append(")");
        }
        sql.append(" GROUP BY a.a_columnID");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{beginTime, endTime, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:1933--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1933--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
            	Map<String, Object> resultData = new LinkedHashMap<>();
            	resultData.put("columnID", resultSet.getInt("column_id"));
            	//resultData.put("columnName", resultSet.getString("column_name"));
                resultData.put("articleNum", resultSet.getString("article_num"));
                resultList.add(resultData);
            }
//            else{
//            	resultData.put("articleNum", "0");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    /**
     * 统计--栏目总览：栏目明细，统计结果列表对应列统计数量，如栏目订阅量等
     * @param columnIDstr
     * @param tableName
     * @param beginTime
     * @param endTime
     * @param siteIDstr
     * @return
     * @throws E5Exception
     */
    public Map<String, Object> countArticleOfTimeByColumnID(String columnIDstr, String tableName, Timestamp beginTime, Timestamp endTime, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int columnID = !StringUtils.isBlank(columnIDstr)?Integer.parseInt(columnIDstr):0;
        sql.append("SELECT count(1) as article_num");
        sql.append(" FROM " + tableName + " a");
        if(isOracle())
        	sql.append(" where a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        else
        	sql.append(" where a.a_pubTime between ?  and ? ");
        sql.append(" and a.a_status = ? AND a.a_siteID = ? and a.a_sourceType = 0 ");
        sql.append(" AND a.a_columnID = ? ");
        sql.append(" GROUP BY a.a_columnID");
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultData = new LinkedHashMap<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, columnID});
            log.error("StatisticsDAO.java:1978--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:1978--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + columnID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            if (resultSet.next()) {
                resultData.put("articleNum", resultSet.getString("article_num"));
            }else{
            	resultData.put("articleNum", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultData;
    }

    public List<Map<String, Object>> countArticleInteractionOfTimeByColumnIDs(String newColumnIDS, String tableName, Timestamp beginTime, Timestamp endTime, String channelColumn, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        
        sql.append("SELECT b.sys_documentid as column_id, sum(a.st_countClick) as article_click,");
        sql.append(" sum(a.st_countDiscuss) as article_discuss,");
        sql.append(" sum(a.st_countShare) as article_share");
        sql.append(" FROM " + tableName + " a, xy_column b where a." + channelColumn + " = b.sys_documentid");
        if(StringUtils.isNotEmpty(newColumnIDS)){
	        sql.append(" AND a." + channelColumn + " in (");
	        sql.append(newColumnIDS);
	        sql.append(") and");
        }
        List<Object> sqlParamList = new ArrayList<>();
        switch (tableName) {
            case "xy_stathour":
                break;
            case "xy_stat":
            	if (isOracle())
                    sql.append(" a.st_date BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') and ");
                    else
                sql.append(" a.st_date between ?  and ? and ");
                sqlParamList.add(formatDate(beginTime));
                sqlParamList.add(formatDate(endTime));
                break;
            default:
                throw new E5Exception("Wrong Table Name!");
        }
        sql.append(" a.st_siteID = ?");
        sqlParamList.add(siteID);
        sql.append(" GROUP BY a." + channelColumn);
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
            log.error("StatisticsDAO.java:2029--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2029--params--"+ sqlParamList);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            while (resultSet.next()) {
            	Map<String, Object> resultData = new LinkedHashMap<>();
            	resultData.put("columnID", resultSet.getInt("column_id"));
                resultData.put("articleClick", resultSet.getString("article_click"));
                resultData.put("articleShare", resultSet.getString("article_share"));
                resultData.put("articleDiscuss", resultSet.getString("article_discuss"));
                resultList.add(resultData);
            }
//            else{
//            	resultData.put("articleClick", "0");
//                resultData.put("articleShare", "0");
//                resultData.put("articleDiscuss", "0");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }
    
    public Map<String, Object> countArticleInteractionOfTimeByColumnID(String columnIDstr, String tableName, Timestamp beginTime, Timestamp endTime, String channelColumn, String siteIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        int columnID = !StringUtils.isBlank(columnIDstr)?Integer.parseInt(columnIDstr):0;
        sql.append("SELECT sum(a.st_countClick) as article_click,");
        sql.append(" sum(a.st_countDiscuss) as article_discuss,");
        sql.append(" sum(a.st_countShare) as article_share");
        sql.append(" FROM " + tableName + " a");
        sql.append(" WHERE a." + channelColumn + "=?");
        List<Object> sqlParamList = new ArrayList<>();
        sqlParamList.add(columnID);
        switch (tableName) {
            case "xy_stathour":
                break;
            case "xy_stat":
            	if (isOracle())
                    sql.append(" AND a.st_date BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
                    else
                sql.append(" AND a.st_date between ?  and ? ");
                sqlParamList.add(formatDate(beginTime));
                sqlParamList.add(formatDate(endTime));
                break;
            default:
                throw new E5Exception("Wrong Table Name!");
        }
        sql.append(" AND a.st_siteID = ?");
        sqlParamList.add(siteID);
        sql.append(" GROUP BY a." + channelColumn);
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultData = new LinkedHashMap<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), sqlParamList.toArray());
            log.error("StatisticsDAO.java:2088--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2088--params--"+ sqlParamList);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            if (resultSet.next()) {
                resultData.put("articleClick", resultSet.getString("article_click"));
                resultData.put("articleShare", resultSet.getString("article_share"));
                resultData.put("articleDiscuss", resultSet.getString("article_discuss"));
            }else{
            	resultData.put("articleClick", "0");
                resultData.put("articleShare", "0");
                resultData.put("articleDiscuss", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultData;
    }

    
    /**
     * 部门人员工作量统计
     * @param siteIDstr
     * @param userIDs
     * @param departmentIDstr
     * @param webTableName
     * @param appTableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @param operation
     * @return
     */
	public List<Map<String, Object>> getUserTotalWorkStatisticsOfTimeByUserIDs(String siteIDstr, String userIDs, String departmentIDstr, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum, String operation) {
		StringBuilder sql = new StringBuilder();
		int siteID = Integer.parseInt(siteIDstr);
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, userIDs, departmentIDstr, sql, 0);
        }else{
        	sqlForMySql(webTableName, appTableName, userIDs, departmentIDstr,sql, 0);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            int limitBegin = (pageNum - 1) * pageSize;
            int limitEnd = pageSize;
         // Oracle分页待调整
            if (isOracle()){
          	  limitEnd = (pageNum - 1) * pageSize + 1;
          	  limitBegin = (pageNum - 1) * pageSize + pageSize;
          }
            Date start = new Date();
            log.error("StatisticsDAO.java:2145--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2145--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, beginTime, endTime, Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            
            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i=0;
                if(isOracle())
                	i = 1;
                rowData.put("authorName", resultSet.getString(i + 1));
                rowData.put("totalArticle", resultSet.getInt(i + 3));
                rowData.put("wapTotalDays", resultSet.getInt(i + 16));
                rowData.put("appTotalDays", resultSet.getInt(i + 17));
                rowData.put("totalClick", resultSet.getInt(i + 4));
                rowData.put("pcClick", resultSet.getInt(i + 5));
                rowData.put("wapClick", resultSet.getInt(i + 6));
                rowData.put("appClick", resultSet.getInt(i + 7));
                rowData.put("totalForward", resultSet.getInt(i + 8));
                rowData.put("pcForward", resultSet.getInt(i + 9));
                rowData.put("wapForward", resultSet.getInt(i + 10));
                rowData.put("appForward", resultSet.getInt(i + 11));
                rowData.put("totalDiscussion", resultSet.getInt(i + 12));
                rowData.put("pcDiscussion", resultSet.getInt(i + 13));
                rowData.put("wapDiscussion", resultSet.getInt(i + 14));
                rowData.put("appDiscussion", resultSet.getInt(i + 15));
                if (operation != null && operation.equals("statistics")) {
                    rowData.put("authorID", resultSet.getInt(i + 2));
                }
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
	}
    
	private boolean isOracle() {
		String dbType = DomHelper.getDBType();
		//String dbType = DBType.ORACLE;
        return dbType.equals(DBType.ORACLE);
	}
	private void sqlForMySql(String webTableName, String appTableName, String userIDs, String departmentIDstr, StringBuilder sql, int type) {
		int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
		if(type == 1){
			sql.append("select count(1) as total from (");
		}
		sql.append("select x.u_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss, SUM(x.wap_totalDays) AS wap_totalDays, SUM(x.app_totalDays) AS app_totalDays ");
        sql.append(" from ( ");
        sql.append("select b.u_name,a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss, COUNT(DISTINCT DATE_FORMAT(a.a_pubtime,'%Y-%m-%d')) AS wap_totalDays,  0 AS app_totalDays ");
        sql.append(" from xy_userext b join " + webTableName + " a on b.SYS_DOCUMENTID = a.SYS_AUTHORID ");
        if(departmentIDstr !=null && !departmentIDstr.equals("")){
            sql.append(" and b.u_orgID ="+departmentID);
        }
        if (userIDs != null && !userIDs.equals("")) {
            sql.append(" and a.SYS_AUTHORID in ("+userIDs+")");
        } else {
            sql.append(" and a.SYS_AUTHORID>0 ");
        }
        sql.append(" where a.a_pubTime between ?  and ?");
        sql.append(" AND a.a_status=? AND a.a_siteID=? ");
        sql.append(" group by a.SYS_AUTHORID,b.u_name");
        sql.append(" union all ( ");
        sql.append("select c.u_name,d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss, 0 AS wap_totalDays, COUNT(DISTINCT DATE_FORMAT(d.a_pubtime,'%Y-%m-%d')) AS app_totalDays ");
        sql.append(" from xy_userext c join  " + appTableName + " d on c.SYS_DOCUMENTID = d.SYS_AUTHORID ");
        if(departmentIDstr !=null && !departmentIDstr.equals("")){
            sql.append(" and c.u_orgID ="+departmentID);
        }
        if (userIDs != null && !userIDs.equals("")) {
            sql.append(" and d.SYS_AUTHORID in ("+userIDs+")");
        } else {
            sql.append(" and d.SYS_AUTHORID>0 ");
        }
        sql.append(" where d.a_pubTime between ?  and ?");
        sql.append(" AND d.a_status=? AND d.a_siteID=? ");
        sql.append(" group by d.SYS_AUTHORID,c.u_name");
        sql.append(" )) x ");
        if(type == 1){
        	sql.append(" group by x.SYS_AUTHORID,x.u_name ");
            sql.append(" ) z ");
        }else{
        	sql.append(" group by x.SYS_AUTHORID,x.u_name order by totalArticle desc");
        	sql.append(" LIMIT ?,?");
        }
        
		
	}
	private void sqlForOracle(String webTableName, String appTableName, String userIDs, String departmentIDstr, StringBuilder sql, int type) {
		int departmentID = !StringUtils.isBlank(departmentIDstr)?Integer.parseInt(departmentIDstr):0;
		if(type == 1){
			sql.append("select count(1) as total from (");
		}else{
			sql.append("select * from (");
			sql.append("select rownum rn, f.* from (");
		}
		sql.append("select x.u_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss, SUM(x.wap_totalDays) AS wap_totalDays, SUM(x.app_totalDays) AS app_totalDays ");
        sql.append(" from ( ");
        sql.append("select b.u_name,a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss, COUNT( DISTINCT to_date(to_char(a.a_pubtime,'yyyy-mm-dd'),'yyyy-mm-dd')) AS wap_totalDays, 0 AS app_totalDays ");
        sql.append(" from xy_userext b , " + webTableName + " a where b.SYS_DOCUMENTID = a.SYS_AUTHORID ");
        if(departmentIDstr !=null && !departmentIDstr.equals("")){
            sql.append(" and b.u_orgID ="+departmentID);
        }
        if (userIDs != null && !userIDs.equals("")) {
            sql.append(" and a.SYS_AUTHORID in ("+userIDs+")");
        } else {
            sql.append(" and a.SYS_AUTHORID>0 ");
        }
            sql.append(" AND a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        sql.append(" AND a.a_status=? AND a.a_siteID=? ");
        sql.append(" group by b.u_name,a.SYS_AUTHORID");
        sql.append(" union all ( ");
        sql.append("select c.u_name,d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss,0 AS wap_totalDays, COUNT( DISTINCT to_date(to_char(d.a_pubtime,'yyyy-mm-dd'),'yyyy-mm-dd')) AS app_totalDays ");
        sql.append(" from xy_userext c , " + appTableName + " d where c.SYS_DOCUMENTID = d.SYS_AUTHORID ");
        if(departmentIDstr !=null && !departmentIDstr.equals("")){
            sql.append(" and c.u_orgID ="+departmentID);
        }
        if (userIDs != null && !userIDs.equals("")) {
            sql.append(" and d.SYS_AUTHORID in ("+userIDs+")");
        } else {
            sql.append(" and d.SYS_AUTHORID>0 ");
        }
            sql.append(" AND d.a_pubTime BETWEEN ? AND ?");
        sql.append(" AND d.a_status=? AND d.a_siteID=? ");
        sql.append(" group by c.u_name,d.SYS_AUTHORID");
        sql.append(" )) x ");
        if(type == 1){
        	sql.append(" group by x.SYS_AUTHORID,x.u_name ");
            sql.append(" ) z ");
        }else{
	        sql.append(" group by x.SYS_AUTHORID,x.u_name order by totalArticle desc");
	        sql.append(" )f where rownum <= ? ");
	        sql.append(" ) where rn >= ?");
        }
	
		
	}
	/**
	 * 部门人员工作量统计--统计总数量
	 * @param siteIDstr
	 * @param userIDs
	 * @param webTableName
	 * @param appTableName
	 * @param beginTime
	 * @param endTime
	 * @param departmentIDstr
	 * @return
	 * @throws E5Exception
	 */
	public int countUserTotalWorkStatisticsOfTimeByUserIDs(String siteIDstr, String userIDs, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, String departmentIDstr) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, userIDs, departmentIDstr, sql, 1);
        }else{
        	sqlForMySql(webTableName, appTableName, userIDs, departmentIDstr,sql, 1);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, beginTime, endTime, Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:2300--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2300--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }

	
    public List<Map<String, Object>> getBatmanTotalWorkStatisticsOfTimeByBatmanID(String siteIDstr, String batmanIDstr, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum, String operation) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);

        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, batmanIDstr, 0, sql);
        }else{
        	sqlForMySql(webTableName, appTableName, batmanIDstr, 0, sql);
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            int limitBegin = (pageNum - 1) * pageSize;
            int limitEnd = pageSize;
         // Oracle分页待调整
            if (isOracle()){
          	  limitEnd = (pageNum - 1) * pageSize + 1;
          	  limitBegin = (pageNum - 1) * pageSize + pageSize;
          }
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
            log.error("StatisticsDAO.java:2340--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2340--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + limitBegin + "," + limitEnd);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));

            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if(isOracle())
                	 i = 1;
                rowData.put("authorName", resultSet.getString(i + 1));
                rowData.put("totalArticle", resultSet.getInt(i + 3));
                rowData.put("totalClick", resultSet.getInt(i + 4));
                rowData.put("pcClick", resultSet.getInt(i + 5));
                rowData.put("wapClick", resultSet.getInt(i + 6));
                rowData.put("appClick", resultSet.getInt(i + 7));
                rowData.put("totalForward", resultSet.getInt(i + 8));
                rowData.put("pcForward", resultSet.getInt(i + 9));
                rowData.put("wapForward", resultSet.getInt(i + 10));
                rowData.put("appForward", resultSet.getInt(i + 11));
                rowData.put("totalDiscussion", resultSet.getInt(i + 12));
                rowData.put("pcDiscussion", resultSet.getInt(i + 13));
                rowData.put("wapDiscussion", resultSet.getInt(i + 14));
                rowData.put("appDiscussion", resultSet.getInt(i + 15));
                if (operation != null && operation.equals("statistics")) {
                    rowData.put("authorID", resultSet.getInt(i + 2));
                }
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }

    private void sqlForMySql(String webTableName, String appTableName, String batmanID, int i, StringBuilder sql) {
    	if(i == 1)
    		sql.append("select count(1) as total from (");
    	sql.append("select x.bm_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss");
        sql.append(" from ( ");
        sql.append("select b.bm_name,a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss ");
        sql.append(" from xy_batman b join " + webTableName + " a on b.SYS_DOCUMENTID = a.SYS_AUTHORID and a.a_sourceType = 3 ");
        if (batmanID != null && !batmanID.equals("")) {
        	sql.append(" and a.SYS_AUTHORID in ("+batmanID+")");
        } else {
            sql.append(" and a.SYS_AUTHORID>0 ");
        }
        sql.append(" where a.a_pubTime between ?  and ?");
        sql.append(" AND a.a_status=? AND a.a_siteID=? ");
        sql.append(" group by a.SYS_AUTHORID");
        sql.append(" union all ( ");
        sql.append("select c.bm_name,d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss ");
        sql.append(" from xy_batman c join  " + appTableName + " d on c.SYS_DOCUMENTID = d.SYS_AUTHORID and d.a_sourceType = 3 ");
        if (batmanID != null && !batmanID.equals("")) {
        	sql.append(" and d.SYS_AUTHORID in ("+batmanID+")");
        } else {
            sql.append(" and d.SYS_AUTHORID>0 ");
        }
        sql.append(" where d.a_pubTime between ?  and ?");
        sql.append(" AND d.a_status=? AND d.a_siteID=? ");
        sql.append(" group by d.SYS_AUTHORID");
        sql.append(" )) x ");
        sql.append(" group by x.SYS_AUTHORID,x.bm_name order by x.SYS_AUTHORID");
        if(i==1)
            sql.append(" ) z ");
        else
        	sql.append(" LIMIT ?,?");		
	}
	private void sqlForOracle(String webTableName, String appTableName, String batmanID, int i, StringBuilder sql) {
		if(i == 1)
			sql.append("select count(1) as total from (");
		else{
			sql.append("select * from (");
			sql.append("select rownum rn, f.* from (");
		}
		sql.append("select x.bm_name as userName, x.SYS_AUTHORID as userID, sum(x.article) as totalArticle, sum(x.totalCountClick) as total_count_click, sum(x.pc_countclick) as pc_countClick, sum(x.wap_countclick) as wap_countClick, sum(x.app_countclick) as app_countClick,sum(x.totalCountShare) as total_count_share ,sum(x.pc_countshare) as pc_countShare,sum(x.wap_countshare) as wap_countShare,sum(x.app_countshare) as app_countShare,sum(x.totalCountDiscuss) as total_count_discuss,sum(x.pc_countdiscuss) as pc_countDiscuss,sum(x.wap_countdiscuss) as wap_countDiscuss,sum(x.app_countdiscuss) app_countDiscuss");
        sql.append(" from ( ");
        sql.append("select b.bm_name,a.SYS_AUTHORID, count(1) as article,sum(a.a_countClick) as totalCountClick, sum(a.a_countClick0) as pc_countclick, sum(a.a_countClick1) as wap_countclick, sum(a.a_countClick2) as app_countclick,sum(a.a_countShare) as totalCountShare,sum(a.a_countShare0) as pc_countshare,sum(a.a_countShare1) as wap_countshare,sum(a.a_countShare2) as app_countshare,sum(a.a_countDiscuss) as totalCountDiscuss,sum(a.a_countDiscuss0) as pc_countdiscuss,sum(a.a_countDiscuss1) as wap_countdiscuss,sum(a.a_countDiscuss2) as app_countdiscuss ");
        sql.append(" from xy_batman b join " + webTableName + " a on b.SYS_DOCUMENTID = a.SYS_AUTHORID and a.a_sourceType = 3 ");
        if (batmanID != null && !batmanID.equals("")) {
        	sql.append(" and a.SYS_AUTHORID in ("+batmanID+")");
        } else {
            sql.append(" and a.SYS_AUTHORID>0 ");
        }
        sql.append(" AND a.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        sql.append(" AND a.a_status=? AND a.a_siteID=? ");
        sql.append(" group by a.SYS_AUTHORID,b.bm_name");
        sql.append(" union all ( ");
        sql.append("select c.bm_name,d.SYS_AUTHORID, count(1) as article,sum(d.a_countClick) as totalCountClick, sum(d.a_countClick0) as pc_countclick, sum(d.a_countClick1) as wap_countclick, sum(d.a_countClick2) as app_countclick,sum(d.a_countShare) as totalCountShare,sum(d.a_countShare0) as pc_countshare,sum(d.a_countShare1) as wap_countshare,sum(d.a_countShare2) as app_countshare,sum(d.a_countDiscuss) as totalCountDiscuss,sum(d.a_countDiscuss0) as pc_countdiscuss,sum(d.a_countDiscuss1) as wap_countdiscuss,sum(d.a_countDiscuss2) as app_countdiscuss ");
        sql.append(" from xy_batman c join  " + appTableName + " d on c.SYS_DOCUMENTID = d.SYS_AUTHORID and d.a_sourceType = 3 ");
        if (batmanID != null && !batmanID.equals("")) {
        	sql.append(" and d.SYS_AUTHORID in ("+batmanID+")");
        } else {
            sql.append(" and d.SYS_AUTHORID>0 ");
        }
        sql.append(" AND d.a_pubTime BETWEEN to_date(?,'yyyy-MM-dd hh24:mi:ss') AND to_date(?,'yyyy-MM-dd hh24:mi:ss') ");
        sql.append(" AND d.a_status=? AND d.a_siteID=? ");
        sql.append(" group by d.SYS_AUTHORID,c.bm_name");
        sql.append(" )) x ");
        if(i==1){
        	sql.append(" group by x.SYS_AUTHORID,x.bm_name order by x.SYS_AUTHORID");
            sql.append(" ) z ");
        }else{
	        sql.append(" group by x.SYS_AUTHORID,x.bm_name order by x.SYS_AUTHORID");
	        sql.append(" )f where rownum <= ? ");
	        sql.append(" ) where rn >= ?");
        }
	}
	public int countBatmanTotalWorkStatisticsOfTimeByBatmanID(String siteIDstr, String batmanID, String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime) throws E5Exception {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        
        if (isOracle()) {
        	sqlForOracle(webTableName, appTableName, batmanID, 1, sql);
        }else{
        	sqlForMySql(webTableName, appTableName, batmanID, 1, sql);
        }
        
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID, formatDate(beginTime), formatDate(endTime), Article.STATUS_PUB_DONE, siteID});
            log.error("StatisticsDAO.java:2461--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:2461--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID + "," + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));
            
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }


    /**
     * 稿件总览-按话题统计-查看话题组列表xy_columntopic
     * @param siteID
     * @return
     */
    public List<Map<String,Object>> getColumnTopicData(int siteID) {
        String sql = "select sys_documentid, col_name from xy_columntopic where col_siteID = ? and SYS_DELETEFLAG = 0";

        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql, new Object[]{siteID});
            log.error("StatisticsDAO.java:2745--SQL--"+ sql);

            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if(isOracle())
                    i = 1;
                rowData.put("groupID", resultSet.getString(i + 1));
                rowData.put("topicGroupName", resultSet.getString(i + 2));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }


    /**
     * 稿件总览-按话题统计
     * @param siteIDstr
     * @param groupID
     * @param tableName
     * @param beginTime
     * @param endTime
     * @param pageSize
     * @param pageNum
     * @return
     */
    public List<Map<String,Object>> getTotalArticleStatisticsOfTimeByTopic(String siteIDstr, String groupID, String tableName, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum,String channelCode) {
        StringBuilder sql = new StringBuilder();

        int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
        // Oracle分页待调整
        if (isOracle()){
            limitEnd = (pageNum - 1) * pageSize + 1;
            limitBegin = (pageNum - 1) * pageSize + pageSize;
        }
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) {
            sql.append("select * from (");
            sql.append("select rownum rn, z.* from (");
        }
        String channel = "0";
        if(channelCode.startsWith("channelWeb")){
            channel = "1";
        }else if(channelCode.startsWith("channelApp")){
            channel = "2";
        }

        sql.append("select e.SYS_TOPIC,e.SYS_DELETEFLAG,f.* from xy_topics e left join (");
        sql.append("select sum(a_countClick) as total_click, sum(a_countClick0) as pc_click, sum(a_countClick1) as wap_click,");
        sql.append("sum(a_countClick2) as app_click, sum(a_countShare) as total_share, sum(a_countShare0) as pc_share,");
        sql.append("sum(a_countShare1) as wap_share, sum(a_countShare2) as app_share, sum(a_countDiscuss) as total_discuss,");
        sql.append("sum(a_countDiscuss0) as pc_discuss, sum(a_countDiscuss1) as wap_discuss, sum(a_countDiscuss2) as app_discuss, ");
        sql.append(" c.SYS_DOCUMENTID,c.a_groupID, count(1) as a_articleCount from ");
        sql.append(tableName);
        sql.append(" a left JOIN xy_topicrelart b on b.a_articleID = a.SYS_DOCUMENTID ");
        sql.append(" left JOIN xy_topics c on  b.a_topicID= c.SYS_DOCUMENTID");

        if(isOracle())
            sql.append(" where a.a_pubTime BETWEEN to_date('"+formatDate(beginTime)+"','yyyy-MM-dd hh24:mi:ss') AND to_date('"+formatDate(endTime)+"','yyyy-MM-dd hh24:mi:ss') ");
        else
            sql.append(" where a.a_pubTime BETWEEN '"+beginTime+"' and '"+endTime+"' ");
        sql.append(" and b.a_channel = ? ");
        sql.append(" AND a.a_status=? ");
        sql.append(" AND a.a_siteID=? ");
//        sql.append(" AND c.a_status=0 ");
        sql.append(" and a.SYS_AUTHORID>0 ");
        sql.append(" AND c.a_groupID >0 ");

        if (isOracle()) {
            sql.append(" GROUP BY SYS_DOCUMENTID,SYS_TOPIC,a_articleCount ) f on e.SYS_DOCUMENTID = f.SYS_DOCUMENTID ");
            sql.append(" where SYS_DELETEFLAG=0 ");
            if (groupID != null && !groupID.equals("")) {
                sql.append(" and e.a_groupID in ("+groupID +")");
            }
            sql.append(" ORDER BY a_articleCount desc,sys_documentid ASC");
            sql.append(" )z where rownum <=? ");
            sql.append(" ) where rn >=? ");
        }else{
            sql.append(" GROUP BY SYS_DOCUMENTID ) f on e.SYS_DOCUMENTID = f.SYS_DOCUMENTID ");
            sql.append(" where SYS_DELETEFLAG=0 ");
            if (groupID != null && !groupID.equals("")) {
                sql.append(" and e.a_groupID in ("+groupID +")");
            }

            sql.append(" ORDER BY a_articleCount desc,sys_documentid desc");
            sql.append(" LIMIT ?,?");
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{channel,Article.STATUS_PUB_DONE, siteID,limitBegin, limitEnd});
            log.error("StatisticsDAO.java:2883--SQL--"+ sql.toString());
            log.error("StatisticsDAO.java:357--params--"+ beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID,limitBegin + "," + limitEnd);
            Date end = new Date();
            System.err.println("统计模块耗时："+(end.getTime()-start.getTime()));

            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                rowData.put("topicName", resultSet.getString("SYS_TOPIC"));
                rowData.put("articleNum", getNotNull(resultSet.getString("a_articleCount")));
                rowData.put("totalClick", getNotNull(resultSet.getString("total_click")));
                rowData.put("pcClick", getNotNull(resultSet.getString("pc_click")));
                rowData.put("wapClick", getNotNull(resultSet.getString("wap_click")));
                rowData.put("appClick", getNotNull(resultSet.getString("app_click")));
                rowData.put("totalShare", getNotNull(resultSet.getString("total_share")));
                rowData.put("pcShare", getNotNull(resultSet.getString("pc_share")));
                rowData.put("wapShare", getNotNull(resultSet.getString("wap_share")));
                rowData.put("appShare", getNotNull(resultSet.getString("app_share")));
                rowData.put("totalDiscuss", getNotNull(resultSet.getString("total_discuss")));
                rowData.put("pcDiscuss", getNotNull(resultSet.getString("pc_discuss")));
                rowData.put("wapDiscuss", getNotNull(resultSet.getString("wap_discuss")));
                rowData.put("appDiscuss", getNotNull(resultSet.getString("app_discuss")));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }

    private String getNotNull(String param) {
        return StringUtils.isBlank(param)?"0":param;
    }

    public int countTotalArticleStatisticsOfTimeByTopic(String siteIDstr, String groupID, String tableName, Timestamp beginTime, Timestamp endTime, String channelCode) {
        StringBuilder sql = new StringBuilder();
        int siteID = Integer.parseInt(siteIDstr);
        sql.append("select count(1) from xy_topics WHERE SYS_DELETEFLAG=0 and a_siteID = ?");
        if (groupID != null && !groupID.equals("")) {
            sql.append(" and a_groupID in ("+groupID +")");
        }else {
            sql.append(" and a_groupID >0 ");
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        int countNum = 0;
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql.toString(), new Object[]{siteID});
            while (resultSet.next()) {
                countNum = Integer.parseInt(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return countNum;
    }
}
