package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.statistics.interfaces.IStatistics;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;
import com.founder.xy.statistics.dao.StatisticsDAO;
import com.founder.xy.statistics.dao.UtilDAO;
import com.founder.xy.system.Tenant;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/20.
 */

@Component
public class ArticleStatisticsByColumn implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
    @Autowired
    ColumnReader columnReader;
    @Autowired
    UtilDAO utilDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
    
    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String channelCode = MapUtils.getString(inParam, "channelCode");
        String tenantCode = MapUtils.getString(inParam, "tenantCode");
        String tableName;
        if (channelCode.equals("channelAll")) {
            tableName = null;
        } else {
            tableName = TableUtil.getArticleTableName(tenantCode, channelCode);
        }

        if (tableName == null || tableName.equals("")) {
            throw new E5Exception("The table doesn't exist!");
        }
        String siteID = MapUtils.getString(inParam, "siteID").trim();
        Timestamp beginTime;
        Timestamp endTime;
        try {
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e) {
            throw new E5Exception("Invalid Time Data!");
        }
        int pageSize = MapUtils.getIntValue(inParam, "pageSize", 40);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
//      long columnID = MapUtils.getLong(inParam, "particularParam", 0L);
        String columnID = MapUtils.getString(inParam, "particularParam");
        String userCode = MapUtils.getString(inParam, "userCode");
        String userID = utilDAO.getCurrentUserId(userCode,siteID).get("userID");
        int colLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
        Long[] subList = columnReader.getOpColumnIds(colLibID, Integer.parseInt(userID), Integer.parseInt(siteID), channelCode.equals("channelWeb")?0:1);
        String currentUserColumnIDs = StringUtils.join(subList, ",");
        List<Map<String, Object>> statisticsDataList = null;
        int totalCount = 0;
        /*
        int colLibID = LibHelper.getColumnLibID();
        ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
        Column column = colReader.get(colLibID, Long.parseLong(columnID));
        List<Long> columnLong = columnManager.getChildrenIDs(column);
        StringBuilder columnStr = new StringBuilder();
        columnStr.append(columnID);
        for (long num : columnLong) {
           columnStr.append("," + num);
        }
        System.out.println(columnStr.toString());
        statisticsDataList = statisticsDAO.getTotalArticleStatisticsOfTimeByColumnID(siteID, columnStr.toString(), tableName, beginTime, endTime, pageSize, pageNum);
      	totalCount = statisticsDAO.countTotalArticleStatisticsOfTimeByColumnID(siteID, columnStr.toString(), tableName, beginTime, endTime);
        */         
        log.error("---快闪开，ArticleStatisticsByColumn.java要开始统计了---");
        if(currentUserColumnIDs != null && currentUserColumnIDs.length() > 0){
        	statisticsDataList = statisticsDAO.getTotalArticleStatisticsOfTimeByColumnID(siteID, columnID, currentUserColumnIDs, tableName, beginTime, endTime, pageSize, pageNum);
            totalCount = statisticsDAO.countTotalArticleStatisticsOfTimeByColumnID(siteID, columnID, currentUserColumnIDs, tableName, beginTime, endTime);
        }
        
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("statisticsData", statisticsDataList);
        returnMap.put("viewName", "xy/statistics/ArticleOverviewStatisticsByColumn");

        int pageCount = (totalCount + pageSize - 1) / pageSize;
        returnMap.put("totalCount", totalCount);
        returnMap.put("pageCount", pageCount);
        return returnMap;
    }
}
