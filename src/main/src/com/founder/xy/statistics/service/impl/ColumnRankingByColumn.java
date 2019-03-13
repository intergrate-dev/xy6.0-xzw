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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Ethan on 2016/12/30.
 */
@Service
public class ColumnRankingByColumn implements IStatistics {
    @Autowired
    StatisticsDAO statisticsDAO;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    @Override
    public Map<String, Object> statistics(Map<String, Object> inParam) throws E5Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();
        Map<String, Object> resultMap = new HashMap();
        try {
            Map<String, Object> newParam = new HashMap<String, Object>();
            Future<Map<String, Object>> columnClickRankFuture = executorService.submit(new ThreadWork("columnClickRank", inParam));
            Future<Map<String, Object>> columnArticleRankFuture = executorService.submit(new ThreadWork("columnArticleRank", inParam));
            Future<Map<String, Object>> columnArticleClickRankFuture = executorService.submit(new ThreadWork("columnArticleClickRank", inParam));
            Future<Map<String, Object>> columnArticleDiscussRankFuture = executorService.submit(new ThreadWork("columnArticleDiscussRank", inParam));
            String channelCode = MapUtils.getString(inParam, "channelCode");
            Future<Map<String, Object>> columnSubscribeRankFuture;
            log.error("---快闪开，ColumnRankingByColumn.java要开始统计了---");
            if(channelCode!= null && "channelApp".equals(channelCode)){
                columnSubscribeRankFuture = executorService.submit(new ThreadWork("columnSubscribeRank", inParam));
                while (true) {
                    if (columnClickRankFuture.isDone() && columnArticleRankFuture.isDone() && columnArticleClickRankFuture.isDone() && columnArticleDiscussRankFuture.isDone() && columnSubscribeRankFuture.isDone()) {
                        executorService.shutdown();
                        break;
                    }
                }
                resultMap.put("columnSubscribeRank", columnSubscribeRankFuture.get().get("columnSubscribeRankData"));
            } else {
                while (true) {
                    if (columnClickRankFuture.isDone() && columnArticleRankFuture.isDone() && columnArticleClickRankFuture.isDone() && columnArticleDiscussRankFuture.isDone()) {
                        executorService.shutdown();
                        break;
                    }
                }
            }

            resultMap.put("columnClickRank", columnClickRankFuture.get().get("columnClickRankData"));
            resultMap.put("columnArticleRank", columnArticleRankFuture.get().get("columnArticleRankData"));
            resultMap.put("columnArticleClickRank", columnArticleClickRankFuture.get().get("columnArticleClickRankData"));
            resultMap.put("columnArticleDiscussRank", columnArticleDiscussRankFuture.get().get("columnArticleDiscussRankData"));
        } catch (Exception e) {
            throw new E5Exception("Error!", e);
        }
        resultMap.put("viewName", "xy/statistics/ColumnRankingByColumn");
        return resultMap;
    }

    private class ThreadWork implements Callable<Map<String, Object>> {
        private Map<String, Object> inParam = new HashMap<>();
        private String functionName;

        public ThreadWork(String funcitonName, Map<String, Object> inParam) {
            this.functionName = funcitonName;
            this.inParam = inParam;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            //Map<String, Object> inData = threadMap.get();
            String timeTag = MapUtils.getString(inParam, "timeTag");
            Timestamp beginTime;
            Timestamp endTime;
            if (timeTag != null && timeTag.equals("day")) {
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                    beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim(), timeFormat);
                    endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim(), timeFormat);
                } catch (Exception e) {
                    throw new E5Exception("Invalid Time Data!", e);
                }
            } else if (timeTag != null && timeTag.equals("hour")) {
                beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
                endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
            } else {
                throw new E5Exception("Invalid Time Data!");
            }
            String channelCode = MapUtils.getString(inParam, "channelCode");
            String orderClass = MapUtils.getString(inParam, "orderClass");
            List<Map<String, Object>> rankDataList = new ArrayList<>();
            Map<String, Object> returnMap = new HashMap<String, Object>();
            String siteID = MapUtils.getString(inParam, "siteID");
            switch (functionName) {
                case "columnClickRank":
                    rankDataList = columnClickRank(channelCode, orderClass, beginTime, endTime, timeTag, siteID);
                    returnMap.put("columnClickRankData", rankDataList);
                    break;
                case "columnArticleRank":

                    String tenantCode = MapUtils.getString(inParam, "tenantCode");
                    rankDataList = columnArticleRank(channelCode, orderClass, beginTime, endTime, siteID, tenantCode);
                    returnMap.put("columnArticleRankData", rankDataList);
                    break;
                case "columnArticleClickRank":
                    rankDataList = columnArticleClickRank(channelCode, orderClass, beginTime, endTime, timeTag, siteID);
                    returnMap.put("columnArticleClickRankData", rankDataList);
                    break;
                case "columnArticleDiscussRank":
                    rankDataList = columnArticleDiscussRank(channelCode, orderClass, beginTime, endTime, timeTag, siteID);
                    returnMap.put("columnArticleDiscussRankData", rankDataList);
                    break;
                case "columnSubscribeRank":
                    rankDataList = columnSubscribeRank(channelCode, orderClass, beginTime, endTime, timeTag, siteID);
                    returnMap.put("columnSubscribeRankData", rankDataList);
                    break;
                default:
                    throw new E5Exception("Wrong Function!");
            }

            return returnMap;
        }
    }

    private List<Map<String, Object>> columnClickRank(String channelCode, String orderClass, Timestamp beginTime, Timestamp endTime, String timeTag, String siteID) throws E5Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (timeTag != null) {
            Map<String, Object> sqlCondition = TableUtil.getColumnClickRankingCondition(timeTag, beginTime, endTime);
            String tableName = MapUtils.getString(sqlCondition, "tableName");
            String selectCondition = MapUtils.getString(sqlCondition, "columnCondition");
            resultList = statisticsDAO.getColumnClickRanking(tableName, orderClass, channelCode, selectCondition, siteID);
            return resultList;
        } else {
            throw new E5Exception("Wrong TimeTag!");
        }
    }

    private List<Map<String, Object>> columnSubscribeRank(String channelCode, String orderClass, Timestamp beginTime, Timestamp endTime, String timeTag, String siteID) throws E5Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (timeTag != null) {
            Map<String, Object> sqlCondition = TableUtil.getColumnSubscribeRankingCondition(timeTag, beginTime, endTime);
            String tableName = MapUtils.getString(sqlCondition, "tableName");
            String selectCondition = MapUtils.getString(sqlCondition, "columnCondition");
            resultList = statisticsDAO.getColumnSubscribeRanking(tableName, orderClass, channelCode, selectCondition, siteID);
            return resultList;
        } else {
            throw new E5Exception("Wrong TimeTag!");
        }
    }

    private List<Map<String, Object>> columnArticleRank(String channelCode, String orderClass, Timestamp beginTime, Timestamp endTime, String siteID, String tenantCode) throws E5Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            System.out.println("columnArticleRank" + Thread.currentThread().getName());
            String tableName = TableUtil.getArticleTableName(tenantCode, channelCode);
            resultList = statisticsDAO.getColumnArticleRanking(tableName, orderClass, beginTime, endTime, siteID);
            System.out.println("columnArticleRank End!");
        }catch (Exception e){
            throw new E5Exception("Error!", e);
        }
        return resultList;
    }

    private List<Map<String, Object>> columnArticleClickRank(String channelCode, String orderClass, Timestamp beginTime, Timestamp endTime, String timeTag, String siteID) throws E5Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            System.out.println("columnArticleClickRank" + Thread.currentThread().getName());
            if (timeTag != null) {
                Map<String, Object> sqlCondition = TableUtil.getColumnArticleGeneralRankingCondition(channelCode, timeTag, beginTime, endTime);
                String tableName = MapUtils.getString(sqlCondition, "tableName");
                String selectCondition = MapUtils.getString(sqlCondition, "articleCondition");
                String column = MapUtils.getString(sqlCondition, "column");
                resultList = statisticsDAO.getColumnArticleClickRanking(tableName, column, orderClass, selectCondition, siteID);
                return resultList;
            }
            else {
                throw new E5Exception("Wrong TimeTag!");
            }
        }catch (Exception e){
            throw new E5Exception("Error!", e);
        }
    }

    private List<Map<String, Object>> columnArticleDiscussRank(String channelCode, String orderClass, Timestamp beginTime, Timestamp endTime, String timeTag, String siteID) throws E5Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            System.out.println("columnArticleDiscussRank" + Thread.currentThread().getName());
            if (timeTag != null) {
                Map<String, Object> sqlCondition = TableUtil.getColumnArticleGeneralRankingCondition(channelCode, timeTag, beginTime, endTime);
                String tableName = MapUtils.getString(sqlCondition, "tableName");
                String selectCondition = MapUtils.getString(sqlCondition, "articleCondition");
                String column = MapUtils.getString(sqlCondition, "column");
                resultList = statisticsDAO.getColumnArticleDiscussRanking(tableName, column, orderClass, selectCondition, siteID);
                return resultList;
            }
            else {
                throw new E5Exception("Wrong TimeTag!");
            }
        }catch (Exception e){
            throw new E5Exception("Error!", e);
        }
    }
}
