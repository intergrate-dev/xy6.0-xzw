package com.founder.xy.topics;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.DBType;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.statistics.util.TableUtil;
import com.founder.xy.statistics.util.TimeUtil;
import com.founder.xy.system.Tenant;
import com.founder.xy.topics.dao.TopicsDAO;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

@Service
public class TopicsManager {

    @Autowired
    TopicsDAO topicsDAO;

    public int topicsCountByGroup(int siteID,long colID){
        int count = 0;

        String tenantCode = Tenant.DEFAULTCODE;
        int topicsLibId = LibHelper.getLibID(DocTypes.TOPICS.typeID(), tenantCode);

        DBSession conn = null;
        IResultSet rs = null;
        try {
            String tableName = LibHelper.getLibTable(topicsLibId);
            String sql = null;
            Object[] params = null;

            sql = "select count(1) count from " + tableName + " where a_groupID=? and a_siteID =? and SYS_DELETEFLAG=0";
            params = new Object[] { colID, siteID };

            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, params);

            while (rs.next()) {
                count = rs.getInt("count");
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
    }

    public String saveCopy(HttpServletRequest request, int docLibID, long docID, long[] colIDs,long oldColID) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long[] newDocIDS = new long[colIDs.length];
        //同时修改多个稿件，使用事务
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(docLibID);
            conn.beginTransaction();

            Document topic = docManager.get(docLibID, docID);
            for (int i = 0; i < colIDs.length; i++) {
                long groupID = colIDs[i];

                //复制稿件
                long newTopicID = InfoHelper.getNextDocID(DocTypes.TOPICS.typeID());
                newDocIDS[i] = newTopicID;
                Document newTopic = docManager.newDocument(topic, docLibID, newTopicID);
                newTopic.setLocked(false);
                newTopic.set("a_groupID", groupID);
                newTopic.set("SYS_TOPIC", "（复制）" + topic.getTopic());
                newTopic.set("a_lastPubTime", null);
                newTopic.set("a_articleCount", 0);

                docManager.save(newTopic, conn);
            }
            conn.commitTransaction();
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }

        //走到这里说明已经保存成功了，应该添加日志
        SysUser sysUser = ProcHelper.getUser(request);
        writeCopyLog(sysUser, newDocIDS, docID, docLibID,colIDs,oldColID);
        return null;
    }

    private void writeCopyLog(SysUser sysUser,
                              long[] newDocIDS, long docID, int docLibID, long[] colIDs, long oldColID) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int catLibID = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(), Tenant.DEFAULTCODE);
        Document cat = docManager.get(catLibID, oldColID);
        long catID = cat.getDocID();
        String catName = cat.getString("col_name");

        for (int i = 0; i < newDocIDS.length; i++) {
            LogHelper.writeLog(docLibID, newDocIDS[i], sysUser, "复制", "来自：" + catName + "（" + catID + "）");
        }
    }

    /**
     * 查看话题列表
     * @param siteID
     * @param status
     * @param topicID
     * @param topicName
     * @return
     */
    public List<Map<String,Object>> getTopicsData(int siteID, String status, String topicID, String topicName) {
        String sql = "select sys_documentid, sys_topic from xy_topics where a_siteID = ? and SYS_DELETEFLAG = 0 ";

        if(!StringUtils.isBlank(status)){
            sql+=" and a_status = " + status;
        }

        if(!StringUtils.isBlank(topicID)){
            sql+=" and sys_documentid like '%" + topicID + "%' ";
        }

        if(!StringUtils.isBlank(topicName)){
            sql+=" and sys_topic like '%" + topicName + "%'";
        }



        DBSession conn = null;
        IResultSet resultSet = null;
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            conn = Context.getDBSession();
            resultSet = conn.executeQuery(sql, new Object[]{siteID});

            while (resultSet.next()) {
                Map<String, Object> rowData = new LinkedHashMap<>();
                int i = 0;
                if(isOracle())
                    i = 1;
                rowData.put("topicID", resultSet.getString(i + 1));
                rowData.put("topicName", resultSet.getString(i + 2));
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

    private boolean isOracle() {
        String dbType = DomHelper.getDBType();
        return dbType.equals(DBType.ORACLE);
    }

    public Map<String,Object> export(Map inParam) throws E5Exception {
        String siteID = MapUtils.getString(inParam, "siteID").trim();
        String topicID = MapUtils.getString(inParam, "particularParam");
        Timestamp beginTime;
        Timestamp endTime;
        try {
            beginTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "beginTime").trim());
            endTime = TimeUtil.StringToTimestamp(MapUtils.getString(inParam, "endTime").trim());
        } catch (Exception e) {
            throw new E5Exception("Invalid Time Data!");
        }
        int pageSize = MapUtils.getIntValue(inParam, "pageSize", 9999999);
        int pageNum = MapUtils.getIntValue(inParam, "pageNum", 1);
        String likeTopicID = MapUtils.getString(inParam, "topicID");
        String likeTopicName = MapUtils.getString(inParam, "topicName");

        List<Map<String, Object>> statisticsDataList = topicsDAO.getArticleByTopic(siteID, topicID, beginTime, endTime, pageSize, pageNum,likeTopicID,likeTopicName);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("exportData", statisticsDataList);
        return returnMap;
    }


    public String deleteTopicrelart(String docIDs, String topicID, String channel,String deleteAll) {
        String result = "1";
        try {
            topicsDAO.deleteTopicrelart(docIDs,topicID,channel,deleteAll);
            //删除后清一下话题稿件缓存；
            topicsDAO.clearTopicRedisCache(docIDs,topicID,"99");
        } catch (E5Exception e) {
            e.printStackTrace();
            result = "0";
        }
        return result;
    }
}
