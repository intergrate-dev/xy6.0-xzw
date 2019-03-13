package com.founder.xy.statistics.service.impl;

import com.founder.e5.context.E5Exception;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.statistics.dao.StatisticsDAO;
import com.founder.xy.statistics.dao.UtilDAO;
import com.founder.xy.statistics.interfaces.IStatisticsExport;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;
import com.founder.xy.system.Tenant;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2017/2/14.
 */
@Service
public class ArticleExportByColumn implements IStatisticsExport {
    @Autowired
    StatisticsDAO statisticsDAO;
    @Autowired
    ColumnReader columnReader;
    @Autowired
    UtilDAO utilDAO;

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
        List<Map<String, Object>> statisticsDataList =null;
        if(currentUserColumnIDs != null && currentUserColumnIDs.length() > 0){
        	statisticsDataList = statisticsDAO.getTotalArticleStatisticsOfTimeByColumnID(siteID, columnID, currentUserColumnIDs, tableName, beginTime, endTime, pageSize, pageNum);
        }
        
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("exportData", statisticsDataList);
        return returnMap;
    }
}
