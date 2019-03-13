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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Ethan on 2017/2/14.
 */
@Service
public class ColumnDetailExportByColumn implements IStatisticsExport {
    @Autowired
    StatisticsDAO statisticsDAO;

    @Override
    public Map<String, Object> export(Map<String, Object> inParam) throws E5Exception {
        //long columnID = MapUtils.getLong(inParam, "columnID", 0L);
        String columnID = MapUtils.getString(inParam, "columnID");
        int pageSize = MapUtils.getIntValue(inParam, "pageSize");
        int pageNum = MapUtils.getIntValue(inParam, "pageNum");
        String timeTag = MapUtils.getString(inParam, "timeTag");
        String channelCode = MapUtils.getString(inParam, "channelCode");
        String columnIds = MapUtils.getString(inParam, "columnIds");
        Timestamp beginTime;
        Timestamp endTime;
        String articleInteractionTableName;
        String articleClickTableName;
        if (timeTag != null && timeTag.equals("day")) {
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim(), timeFormat);
                endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim(), timeFormat);
                articleInteractionTableName = "xy_stat";
                articleClickTableName = "xy_statcol";
            } catch (Exception e) {
                throw new E5Exception("Invalid Time Data!", e);
            }
        } else if (timeTag != null && timeTag.equals("hour")) {
            try {
                beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
                endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
                articleInteractionTableName = "xy_stathour";
                articleClickTableName = "xy_statcolhour";
            } catch (Exception e) {
                throw new E5Exception("Invalid Time Data!", e);
            }
        } else {
            throw new E5Exception("Invalid Time Data!");
        }
        List<Map<String, Object>> columnList = new ArrayList<>();
        String siteID = MapUtils.getString(inParam, "siteID");
        String articleTableName = TableUtil.getArticleTableName(MapUtils.getString(inParam, "tenantCode"), channelCode);
        String channelColumn;
        int channel;
        switch (channelCode) {
            case "channelWeb":
                channelColumn = "st_webCol";
                channel=0;
                break;
            case "channelApp":
                channelColumn = "st_appCol";
                channel=1;
                break;
            default:
                throw new E5Exception("Wrong Channel Code!");
        }
        
        if (columnID == null||"".equals(columnID.trim())) {
        	columnList = statisticsDAO.getColumnInfoXyStatCol(channel,articleClickTableName, beginTime, endTime, siteID, pageSize, pageNum, columnIds);
        }else{
        	columnList = statisticsDAO.getColumnInfoByid(columnID);
        }
        
        for (int i = 0; i < columnList.size(); i++) {
            //Map rowData = new LinkedHashMap();
        	Map rowData = columnList.get(i);
            ExecutorService executorService = Executors.newCachedThreadPool();
            try {
                String rowColumnID = MapUtils.getString(rowData, "columnID");
                rowData.remove("columnID");
                Future<Map<String, Object>> articleClickFuture = executorService.submit(new ArticleClickThread(rowColumnID, articleClickTableName , beginTime, endTime, channel, siteID));
                Future<Map<String, Object>> articleFuture = executorService.submit(new ArticleThread(rowColumnID, articleTableName, beginTime, endTime, siteID));
                Future<Map<String, Object>> articleInteractionFuture = executorService.submit(new ColumnDetailExportByColumn.ArticleInteractionThread(rowColumnID, articleInteractionTableName, beginTime, endTime, channelColumn, siteID));
                while (true) {
                    if (articleClickFuture.isDone() && articleFuture.isDone() && articleInteractionFuture.isDone()) {
                        executorService.shutdown();
                        break;
                    }
                }
                rowData.putAll(articleClickFuture.get());
                rowData.putAll(articleFuture.get());
                rowData.putAll(articleInteractionFuture.get());
                columnList.set(i, rowData);
            } catch (Exception e) {
                throw new E5Exception("Error!", e);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("exportData", columnList);
        return resultMap;
    }

    private class ArticleClickThread implements Callable<Map<String, Object>> {
        private String columnID;
        private String tableName;
        private Timestamp beginTime;
        private Timestamp endTime;
        private int channel;
        private String siteID;

        public ArticleClickThread(String columnID, String tableName, Timestamp beginTime, Timestamp endTime, int channel, String siteID) {
            this.columnID = columnID;
            this.tableName = tableName;
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.channel = channel;
            this.siteID = siteID;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            return statisticsDAO.countArticleClickOfTimeByColumnID(columnID, tableName, beginTime, endTime, channel, siteID);
        }

    }
    
    private class ArticleThread implements Callable<Map<String, Object>> {
        private String columnID;
        private String tableName;
        private Timestamp beginTime;
        private Timestamp endTime;
        private String siteID;

        public ArticleThread(String columnID, String tableName, Timestamp beginTime, Timestamp endTime, String siteID) {
            this.columnID = columnID;
            this.tableName = tableName;
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.siteID = siteID;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            return statisticsDAO.countArticleOfTimeByColumnID(columnID, tableName, beginTime, endTime, siteID);
        }

    }

    private class ArticleInteractionThread implements Callable<Map<String, Object>> {
        private String columnID;
        private String tableName;
        private Timestamp beginTime;
        private Timestamp endTime;
        private String channelColumn;
        private String siteID;

        public ArticleInteractionThread(String columnID, String tableName, Timestamp beginTime, Timestamp endTime, String channelColumn, String siteID) {
            this.columnID = columnID;
            this.tableName = tableName;
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.channelColumn = channelColumn;
            this.siteID = siteID;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            return statisticsDAO.countArticleInteractionOfTimeByColumnID(columnID, tableName, beginTime, endTime, channelColumn, siteID);
        }

    }
}
