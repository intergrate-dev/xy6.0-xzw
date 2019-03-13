package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.statistics.interfaces.IStatistics;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;
import com.founder.xy.statistics.dao.StatisticsDAO;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/19.
 */

@Component
public class ArticleStatisticsByDepartment implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
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
        String departmentID = MapUtils.getString(inParam, "particularParam");
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
        List<Map<String, Object>> statisticsDataList = statisticsDAO.getTotalArticleStatisticsOfTimeByDepartment(siteID, departmentID, tableName, beginTime, endTime, pageSize, pageNum);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("statisticsData", statisticsDataList);
        returnMap.put("viewName", "xy/statistics/ArticleOverviewStatisticsByDepartment");
        log.error("---快闪开，ArticleRankingByDepartment.java要开始统计了---");
        int totalCount = statisticsDAO.countTotalArticleStatisticsOfTimeByDepartment(siteID, departmentID, tableName, beginTime, endTime);
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        returnMap.put("totalCount", totalCount);
        returnMap.put("pageCount", pageCount);
        return returnMap;
    }
}
