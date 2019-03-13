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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/29.
 */
@Service
public class ArticleRankingByColumn implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {
        String columnIDs = MapUtils.getString(inParam, "columnID");
        /*
        int columnID;
        int columnLv = 0;
        if (columnIDs == null || columnIDs.equals("")) {
            columnID = 0;
        } else {
            String[] column;
            column = columnIDs.trim().split("~");
            columnLv = column.length - 1;
            columnID = Integer.parseInt(column[columnLv]);
        }
        */
        String channelCode = MapUtils.getString(inParam, "channelCode");
        String channel;
        switch (channelCode) {
            case "channelWeb":
                channel = "web";
                break;
            case "channelApp":
                channel = "app";
                break;
            default:
                throw new E5Exception("Wrong Channel Code!");
        }
        String timeTag = MapUtils.getString(inParam, "timeTag");
        Timestamp beginTime;
        Timestamp endTime;
        if (timeTag != null && timeTag.equals("day")) {
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim(), timeFormat);
                endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim(), timeFormat);
            } catch (Exception e) {
                throw new E5Exception("Invalid Time Data!");
            }
        } else if (timeTag != null && timeTag.equals("hour")) {
            beginTime = null;
            endTime = null;
        } else {
            throw new E5Exception("Invalid Time Data!");
        }

        //Map<String, Object> condition = TableUtil.getArticleRankingCondition(columnID, channel, columnLv, timeTag, beginTime, endTime);
        Map<String, Object> condition = TableUtil.getArticleRankingCondition(columnIDs, channel, timeTag, beginTime, endTime);
        String tenantCode = MapUtils.getString(inParam, "tenantCode");
        String linkedTableName = TableUtil.getArticleTableName(tenantCode, channelCode);
        if (linkedTableName == null || linkedTableName.equals("")) {
            throw new E5Exception("The linked table doesn't exist!");
        } else {
            condition.put("linkedTableName", linkedTableName);
        }
        String tableName = MapUtils.getString(condition, "tableName");
        String selectCondition = MapUtils.getString(condition, "columnCondition");
        String orderClass = MapUtils.getString(inParam, "orderClass");
        String order;
        switch (orderClass) {
            case "top":
                order = "DESC";
                break;
            case "last":
                order = "ASC";
                break;
            default:
                throw new E5Exception("Wrong Order!");
        }
        String siteID = MapUtils.getString(inParam, "siteID");
        log.error("---快闪开，ArticleRankingByColumn.java要开始统计了---");
        List<Map<String, Object>> clickList = statisticsDAO.getArticleRankingGeneral(tableName, linkedTableName, "countClick", order, siteID, selectCondition, null);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("statisticsClick", clickList);
        List<Map<String, Object>> discussList = statisticsDAO.getArticleRankingGeneral(tableName, linkedTableName, "countDiscuss", order, siteID, selectCondition, null);
        returnMap.put("statisticsDiscuss", discussList);
        returnMap.put("viewName", "xy/statistics/ArticleRankingByColumn");
        return returnMap;
    }
}
