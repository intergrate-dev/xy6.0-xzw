package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.statistics.dao.StatisticsDAO;
import com.founder.xy.statistics.interfaces.IStatisticsExport;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuantk on 2018/09/29.
 */
@Service
public class ArticleExportByTopic implements IStatisticsExport {
    @Autowired
    StatisticsDAO statisticsDAO;

    @Override
    public Map<String, Object> export(Map<String, Object> inParam) throws E5Exception {
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
        String groupID = MapUtils.getString(inParam, "particularParam");
        Timestamp beginTime;
        Timestamp endTime;
        try {
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e) {
            throw new E5Exception("Invalid Time Data!");
        }
        int pageSize = MapUtils.getIntValue(inParam, "pageSize", 999999999);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
        List<Map<String, Object>> statisticsDataList = statisticsDAO.getTotalArticleStatisticsOfTimeByTopic(siteID, groupID, tableName, beginTime, endTime, pageSize, pageNum, channelCode);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("exportData", statisticsDataList);
        return returnMap;
    }
}
