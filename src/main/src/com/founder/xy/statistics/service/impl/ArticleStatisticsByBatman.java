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
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2017/2/10.
 */
@Service
public class ArticleStatisticsByBatman implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String tenantCode = MapUtils.getString(inParam, "tenantCode");
        String webTableName = TableUtil.getArticleTableName(tenantCode, "channelWeb");
        String appTableName = TableUtil.getArticleTableName(tenantCode, "channelApp");
        String siteID = MapUtils.getString(inParam, "siteID").trim();
        String batmanID = MapUtils.getString(inParam, "batmanID");
        Timestamp beginTime;
        Timestamp endTime;
        try{
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e){
            throw new E5Exception("Invalid Time Data!");
        }
        Map<String, Object> resultMap = new HashMap<>();
        int pageSize = MapUtils.getIntValue(inParam, "pageSize", 40);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
        resultMap.put("statisticsData",getPersonalTotalWorkStatistics(webTableName, appTableName, beginTime, endTime, siteID, batmanID, pageSize, pageNum).get("BatmanTotalData"));
        int totalCount = 0;
        log.error("---快闪开，ArticleStatisticsByBatman.java要开始统计了---");
        totalCount = statisticsDAO.countBatmanTotalWorkStatisticsOfTimeByBatmanID(siteID, batmanID, webTableName, appTableName, beginTime, endTime);
        int pageCount = (totalCount + pageSize - 1) / pageSize;
        resultMap.put("totalCount", totalCount);
        resultMap.put("pageCount", pageCount);
        resultMap.put("viewName", "xy/statistics/xy/statistics/ArticleStatisticsByBatman");
        return resultMap;
    }

    private Map<String, Object> getPersonalTotalWorkStatistics(String webTableName, String appTableName, Timestamp beginTime, Timestamp endTime, String siteID, String batmanID, int pageSize, int pageNum) throws E5Exception {
        List<Map<String, Object>> personalTotalDataList =  statisticsDAO.getBatmanTotalWorkStatisticsOfTimeByBatmanID(siteID, batmanID, webTableName, appTableName, beginTime, endTime, pageSize, pageNum, "statistics");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("BatmanTotalData", personalTotalDataList);
        return resultMap;
    }
}
