package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.statistics.dao.StatisticsDAO;
import com.founder.xy.statistics.interfaces.IStatistics;
import com.founder.xy.statistics.util.TableUtil;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/27.
 */
@Service
public class WorkStatisticsByPerson implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
    /*@Autowired
    ColumnManager columnManager;*/

    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String tenantCode = MapUtils.getString(inParam, "tenantCode");
        String webTableName = TableUtil.getArticleTableName(tenantCode, "channelWeb");
        String appTableName = TableUtil.getArticleTableName(tenantCode, "channelApp");
        String siteID = MapUtils.getString(inParam, "siteID").trim();
        String personID = MapUtils.getString(inParam, "userID");
        String sourceType ="0";
        if("batman".equals(MapUtils.getString(inParam, "isBatman"))){
        	sourceType = "3";
        }
        /*Timestamp beginTime;
        Timestamp endTime;
        try {
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e) {
            throw new E5Exception("Invalid Time Data!");
        }*/
        Map<String, Object> resultMap = new HashMap<>();
        log.error("---快闪开，WorkStatisticsByPerson.java要开始统计了---");
        resultMap.put("statisticsTotalData", getCurrentMonthWorkStatisticsOfPerson(siteID, personID, sourceType, webTableName, appTableName).get("TotalData"));
        /*int pageSize = MapUtils.getIntValue(inParam, "pageSize", 40);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
        long columnID = MapUtils.getLong(inParam, "columnID", 0L);
        Map<String, Object> personalDetailData = getPersonalDetailWorkStatisticsOfTimeByPersonalIDAndColumnID(webTableName, appTableName, beginTime, endTime, siteID, personID, columnID, pageSize, pageNum);
        resultMap.put("personalTotalData", personalDetailData.get("PersonalDetailData"));
        int totalCount = 0;
        totalCount = MapUtils.getIntValue(personalDetailData, "totalCount");
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        resultMap.put("totalCount", totalCount);
        resultMap.put("pageCount", pageCount);*/
        resultMap.put("currentMonthData", getArticleStatisticsCurrentMonthByPerson(webTableName, appTableName, siteID, personID, sourceType).get("currentMonthData"));
        resultMap.put("currentWeekData", getArticleStatisticsCurrentWeekByPerson(webTableName, appTableName, siteID, personID, sourceType).get("currentWeekData"));
        resultMap.put("current24HoursData", getArticleStatisticsCurrent24HoursByPerson(webTableName, appTableName, siteID, personID, sourceType).get("current24HoursData"));
        resultMap.put("viewName", "xy/statistics/xy/statistics/WorkStatisticsByPersonalDetail");
        return resultMap;
    }

    private Map<String, Object> getCurrentMonthWorkStatisticsOfPerson(String siteID, String personID, String sourceType, String webTableName, String appTableName) throws E5Exception {
        Map<String, Object> webData = new HashMap<>();
        if (webTableName != null && !webTableName.equals("")) {
            webData = statisticsDAO.getCurrentMonthWorkStatisticsOfPersonByTableName(siteID, personID, webTableName, sourceType);
        }
        Map<String, Object> appData = new HashMap<>();
        if (appTableName != null && !appTableName.equals("")) {
            appData = statisticsDAO.getCurrentMonthWorkStatisticsOfPersonByTableName(siteID, personID, appTableName, sourceType);
        }
        Map<String, Object> totalData = new HashMap<>();
        if (webData.isEmpty()) {
            if (appData.isEmpty()) {
                totalData.put("TotalData", null);
                return totalData;
            } else {
                totalData.put("TotalData", appData);
                return totalData;
            }
        } else {
            if (appData.isEmpty()) {
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
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    if (!webData.containsKey(key)) {
                        Object value = entry.getValue();
                        webData.put(key.toString(), value);
                    }
                }
                totalData.put("TotalData", webData);
            }
            return totalData;
        }
    }


    private Map<String, Object> getArticleStatisticsCurrentMonthByPerson(String webTableName, String appTableName, String siteID, String personID, String sourceType) throws E5Exception {
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrentMonthByPersonID(siteID, personID, webTableName, appTableName, sourceType);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentMonthData", articleStatisticsDataList);
        return resultMap;
    }

    private Map<String, Object> getArticleStatisticsCurrentWeekByPerson(String webTableName, String appTableName, String siteID, String personID, String sourceType) throws E5Exception {
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrentWeekByPersonID(siteID, personID, webTableName, appTableName, sourceType);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentWeekData", articleStatisticsDataList);
        return resultMap;
    }

    private Map<String, Object> getArticleStatisticsCurrent24HoursByPerson(String webTableName, String appTableName, String siteID, String personID, String sourceType) throws E5Exception {
        List<Map<String, Object>> articleStatisticsDataList = statisticsDAO.getArticleStatisticsCurrent24HoursByPersonID(siteID, personID, webTableName, appTableName, sourceType);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("current24HoursData", articleStatisticsDataList);
        return resultMap;
    }


   /* private Map<String, Object> getPersonalDetailWorkStatisticsOfTimeByPersonalIDAndColumnID(String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, String siteID, String personID, long columnID, int pageSize, int pageNum) throws E5Exception {
        Map<String, Object> resultMap = new HashMap<>();
        if (columnID == 0) {
            List<Map<String, Object>> statisticsDataList = statisticsDAO.getPersonalStatisticsOfTimeByPersonIDAndColumnIDs(siteID, personID, null, webTableName, appTableName, beginTime, endTime, pageSize, pageNum);
            int totalCount = statisticsDAO.countPersonalStatisticsOfTimeByPersonIDAndColumnIDs(siteID, personID, null, webTableName, appTableName, beginTime, endTime);
            resultMap.put("PersonalDetailData", statisticsDataList);
            resultMap.put("totalCount", totalCount);
        } else {
            int colLibID = LibHelper.getColumnLibID();
            ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
            Column column = colReader.get(colLibID, columnID);
            long[] columnLong = columnManager.getChildrenIDs(column);
            StringBuilder columnStr = new StringBuilder();
            columnStr.append(columnID);
            for (long num : columnLong) {
                columnStr.append("," + num);
            }
            List<Map<String, Object>> statisticsDataList = statisticsDAO.getPersonalStatisticsOfTimeByPersonIDAndColumnIDs(siteID, personID, columnStr.toString(), webTableName, appTableName, beginTime, endTime, pageSize, pageNum);
            int totalCount = statisticsDAO.countPersonalStatisticsOfTimeByPersonIDAndColumnIDs(siteID, personID, columnStr.toString(), webTableName, appTableName, beginTime, endTime);
            resultMap.put("PersonalDetailData", statisticsDataList);
            resultMap.put("totalCount", totalCount);
        }
    }*/
}
