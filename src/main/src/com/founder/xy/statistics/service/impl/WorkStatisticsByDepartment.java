package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.statistics.dao.StatisticsDAO;
import com.founder.xy.statistics.interfaces.IStatistics;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/21.
 */

@Service
public class WorkStatisticsByDepartment implements IStatistics {

    @Autowired
    StatisticsDAO statisticsDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String tenantCode = MapUtils.getString(inParam, "tenantCode");
        String webTableName = TableUtil.getArticleTableName(tenantCode, "channelWeb");
        String appTableName = TableUtil.getArticleTableName(tenantCode, "channelApp");
        String siteID = MapUtils.getString(inParam, "siteID").trim();
        String departmentID = MapUtils.getString(inParam, "departmentID");
        Timestamp beginTime;
        Timestamp endTime;
        try{
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e){
            throw new E5Exception("Invalid Time Data!");
        }
        log.error("---快闪开，WorkStatisticsByDepartment.java要开始统计了---");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("statisticsTotalData", getCurrentMonthWorkStatisticsOfDepartment(siteID, departmentID, webTableName, appTableName).get("TotalData"));
        int pageSize = MapUtils.getIntValue(inParam, "pageSize", 40);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
        resultMap.put("personalTotalData",getPersonalTotalWorkStatistics(webTableName, appTableName, beginTime, endTime, siteID, departmentID, pageSize, pageNum).get("PersonalTotalData"));
        resultMap.put("currentMonthData",getArticleStatisticsCurrentMonthByDepartment(webTableName, appTableName, siteID, departmentID).get("currentMonthData"));
        resultMap.put("currentWeekData",getArticleStatisticsCurrentWeekByDepartment(webTableName, appTableName, siteID, departmentID).get("currentWeekData"));
        resultMap.put("current24HoursData",getArticleStatisticsCurrent24HoursByDepartment(webTableName, appTableName, siteID, departmentID).get("current24HoursData"));
        int totalCount = 0;
        totalCount = statisticsDAO.countPersonalTotalWorkStatisticsOfTimeByDepartmentID(siteID, departmentID, webTableName, appTableName, beginTime, endTime);
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        resultMap.put("totalCount", totalCount);
        resultMap.put("pageCount", pageCount);
        resultMap.put("viewName", "xy/statistics/xy/statistics/WorkStatisticsByDepartment");
        return resultMap;
    }

    private Map<String, Object> getCurrentMonthWorkStatisticsOfDepartment(String siteID, String departmentID, String webTableName, String appTableName) throws E5Exception{
        Map<String, Object> webData = new HashMap<>();
        if(webTableName != null && !webTableName.equals("")){
            webData =  statisticsDAO.getCurrentMonthWorkStatisticsOfDepartmentByTableName(siteID, departmentID, webTableName);
        }
        Map<String, Object> appData = new HashMap<>();
        if(appTableName !=null && !appTableName.equals("")){
            appData =  statisticsDAO.getCurrentMonthWorkStatisticsOfDepartmentByTableName(siteID, departmentID, appTableName);
        }
        Map<String, Object> totalData = new HashMap<>();
        if(webData.isEmpty()){
            if(appData.isEmpty()){
                totalData.put("TotalData", null);
                return totalData;
            } else {
                totalData.put("TotalData",appData);
                return totalData;
            }
        } else {
            if(appData.isEmpty()) {
                totalData.put("TotalData", webData);
            } else {
                Iterator iterator = webData.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    if (appData.containsKey(key)) {
                        int webVal = Integer.parseInt(entry.getValue().toString());
                        int appVal = Integer.parseInt(appData.get(key).toString());
                        webData.put(key.toString(), webVal + appVal);
                    }
                }
                iterator = appData.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    if(!webData.containsKey(key)){
                        Object value = entry.getValue();
                        webData.put(key.toString(), value);
                    }
                }
                totalData.put("TotalData", webData);
            }
            return totalData;
        }
    }

    private Map<String, Object> getPersonalTotalWorkStatistics(String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, String siteID, String departmentID, int pageSize, int pageNum) throws E5Exception {
        List personalTotalDataList =  statisticsDAO.getPersonalTotalWorkStatisticsOfTimeByDepartmentID(siteID, departmentID, webTableName, appTableName, beginTime, endTime, pageSize, pageNum, "statistics");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("PersonalTotalData", personalTotalDataList);
        return resultMap;
    }

    private Map<String, Object> getArticleStatisticsCurrentMonthByDepartment(String webTableName, String appTableName, String siteID, String departmentID) throws E5Exception{
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrentMonthByDepartmentID(siteID, departmentID, webTableName, appTableName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentMonthData", articleStatisticsDataList);
        return resultMap;
    }
    private Map<String, Object> getArticleStatisticsCurrentWeekByDepartment(String webTableName, String appTableName, String siteID, String departmentID) throws E5Exception{
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrentWeekByDepartmentID(siteID, departmentID, webTableName, appTableName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentWeekData", articleStatisticsDataList);
        return resultMap;
    }
    private Map<String, Object> getArticleStatisticsCurrent24HoursByDepartment(String webTableName, String appTableName, String siteID, String departmentID) throws E5Exception{
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrent24HoursByDepartmentID(siteID, departmentID, webTableName, appTableName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("current24HoursData", articleStatisticsDataList);
        return resultMap;
    }
}
