package com.founder.xy.topics.dao;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.article.Article;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class TopicsDAO {
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);

    public List<Map<String, Object>> getArticleByTopic(String siteIDstr, String topicID, Timestamp beginTime, Timestamp endTime, int pageSize, int pageNum, String likeTopicID, String likeTopicName) {
        String sql = "";

        int limitBegin = (pageNum - 1) * pageSize;
        int limitEnd = pageSize;
        // Oracle分页待调整
        if (isOracle()) {
            limitEnd = (pageNum - 1) * pageSize + 1;
            limitBegin = (pageNum - 1) * pageSize + pageSize;
        }
        int siteID = Integer.parseInt(siteIDstr);
        if (isOracle()) {
            sql += "select * from (";
            sql += "select rownum rn, z.* from (";
        }
        String selectSql = " select SYS_DOCUMENTID,a.a_type,SYS_TOPIC,SYS_AUTHORS,a_column,DATE_FORMAT(a_pubTime, '%Y-%m-%d %H:%i:%S') as a_pubTime," +
                "a_countClick,a_countClick0, a_countClick1,a_countClick2,a_countShare,a_countShare0,a_countShare1,a_countShare2," +
                "a_countDiscuss,a_countDiscuss0,a_countDiscuss1,a_countDiscuss2,a_location from";

        String whereSql="";
        if (isOracle()) {
            whereSql += " where a.a_pubTime BETWEEN to_date('" + formatDate(beginTime) + "','yyyy-MM-dd hh24:mi:ss') AND to_date('" + formatDate(endTime) + "','yyyy-MM-dd hh24:mi:ss') ";
        }else{
            whereSql += " where a.a_pubTime BETWEEN '" + beginTime + "' and '" + endTime + "' ";
        }
        whereSql += " AND a.a_status=? ";
        whereSql += " AND a.a_siteID=? ";
        whereSql += " AND a.sys_deleteflag=0 ";
        if (topicID != null && !topicID.equals("")) {
            whereSql += " AND b.a_topicID = " + topicID;
        } else {
            whereSql += " AND b.a_topicID >0";
        }

        if(!StringUtils.isBlank(likeTopicID)){
            whereSql += " AND b.a_topicID like '%" + likeTopicID + "%'";
        }

        if(!StringUtils.isBlank(likeTopicName)){
            whereSql += " AND b.a_topicName like '%" + likeTopicName + "%'";
        }

        sql += selectSql.replace("a_location", "'Web稿件' as a_location") + " xy_article a "
                + " left join xy_topicrelart b on a.sys_documentid = b.a_articleid"
                + whereSql + " and b.a_channel=1 "
                + " union all "
                + selectSql.replace("a_location", "'App稿件' as a_location") + " xy_articleapp a "
                + " left join xy_topicrelart b on a.sys_documentid = b.a_articleid"
                + whereSql + " and b.a_channel=2 "
                + " order by sys_documentid asc, a_location asc ";


        if (isOracle()) {
            sql += " )z where rownum <=? ";
            sql += " ) where rn >=? ";
        } else {
            sql += " LIMIT ?,?";
        }
        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            Date start = new Date();
            resultSet = conn.executeQuery(sql, new Object[]{Article.STATUS_PUB_DONE, siteID, Article.STATUS_PUB_DONE, siteID, limitBegin, limitEnd});
            log.error("TopicDAO.java:79--SQL--" + sql);
            log.error("TopicDAO.java:80--params--" + beginTime + "," + endTime + "," + Article.STATUS_PUB_DONE + "," + siteID, limitBegin + "," + limitEnd);
            Date end = new Date();
            System.err.println("模块耗时：" + (end.getTime() - start.getTime()));

            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if (isOracle())
                    i = 1;
                rowData.put("sys_documentid", resultSet.getString(i + 1));
                rowData.put("type", transType(resultSet.getString(i + 2)));
                rowData.put("articleType", resultSet.getString(i + 19));
                rowData.put("topic", resultSet.getString(i + 3));
                rowData.put("author", resultSet.getString(i + 4));
                rowData.put("column", resultSet.getString(i + 5));
                rowData.put("pubTime", resultSet.getString(i + 6));
                rowData.put("totalClick", resultSet.getString(i + 7));
                rowData.put("pcClick", resultSet.getString(i + 8));
                rowData.put("wapClick", resultSet.getString(i + 9));
                rowData.put("appClick", resultSet.getString(i + 10));
                rowData.put("totalShare", resultSet.getString(i + 11));
                rowData.put("pcShare", resultSet.getString(i + 12));
                rowData.put("wapShare", resultSet.getString(i + 13));
                rowData.put("appShare", resultSet.getString(i + 14));
                rowData.put("totalDiscuss", resultSet.getString(i + 15));
                rowData.put("pcDiscuss", resultSet.getString(i + 16));
                rowData.put("wapDiscuss", resultSet.getString(i + 17));
                rowData.put("appDiscuss", resultSet.getString(i + 18));
                resultList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        return resultList;
    }

    private String formatDate(Timestamp date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private boolean isOracle() {
        String dbType = DomHelper.getDBType();
        return dbType.equals(DBType.ORACLE);
    }

    private String transType(String type){
        String typeValue = "";
        switch (type){
            case "0":
                typeValue = "文章";
                break;
            case "1":
                typeValue = "组图";
                break;
            case "2":
                typeValue = "视频";
                break;
            case "3":
                typeValue = "专题";
                break;
            case "4":
                typeValue = "链接稿";
                break;
            case "5":
                typeValue = "多标题稿";
                break;
            case "6":
                typeValue = "直播稿";
                break;
            case "7":
                typeValue = "活动稿";
                break;
            case "8":
                typeValue = "广告稿";
                break;
            case "9":
                typeValue = "文档";
                break;
            case "10":
                typeValue = "问答";
                break;
            case "11":
                typeValue = "全景图";
                break;
            case "12":
                typeValue = "H5";
                break;
        }
        return typeValue;

    }

    public void deleteTopicrelart(String docIDs, String topicID, String channel,String deleteAll) throws E5Exception {
        String deleteSql = "delete from xy_topicrelart where a_articleID = ? and a_topicID=? ";
        if("1".equals(deleteAll)){
            deleteSql += "and a_channel = " + channel;
        }
        String[] ids = docIDs.split(",");

        DBSession conn = Context.getDBSession();
        try {
            conn.beginTransaction();
            for (int j = 0; j < ids.length; j++) {
                InfoHelper.executeUpdate(deleteSql, new Object[]{ids[j], topicID});
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    public void clearTopicRedisCache(String docIDs, String topicID,String channel) {
        String docIDs1 = "("+docIDs+")";
        String sql = "SELECT * from ( SELECT case when a_type=1 then 1 when a_type=2 then 2 else 0 end as a_type,a_siteID from xy_article where SYS_DOCUMENTID in "+docIDs1+" and a_status="+Article.STATUS_PUB_DONE +
                " union all " +
                " SELECT case when a_type=1 then 1 when a_type=2 then 2 else 0 end as a_type,a_siteID from xy_articleapp where SYS_DOCUMENTID in "+docIDs1+" and a_status="+Article.STATUS_PUB_DONE +
                " ) a GROUP BY a_type,a_siteID";
        DBSession conn = null;
        IResultSet resultSet = null;
        String siteID = "1";
        List<String> typeList = new ArrayList<String>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql);
            while (resultSet.next()){
                String type = resultSet.getString("a_type");
                siteID = resultSet.getString("a_siteID");
                typeList.add(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(resultSet);
            ResourceMgr.closeQuietly(conn);
        }
        if("99".equals(channel)){
            for(int i=0;i<typeList.size();i++){
                String type = typeList.get(i);
                String key1 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + 0+ "." + topicID + "."+type;
                String key2 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + 1+ "." + topicID + "."+type;
                RedisManager.clearLongKeys(key1);
                RedisManager.clearLongKeys(key2);
            }
        }else{
            for(int i=0;i<typeList.size();i++) {
                String type = typeList.get(i);
                String key5 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY, Integer.valueOf(siteID)) + (Integer.valueOf(channel) - 1) + "." + topicID + "." + type;
                RedisManager.clearLongKeys(key5);
            }
        }
        String key3 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + 0 + "." + topicID + "."+100;
        String key4 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(siteID)) + 1 + "." + topicID + "."+100;
        RedisManager.clearLongKeys(key3);
        RedisManager.clearLongKeys(key4);
    }
}