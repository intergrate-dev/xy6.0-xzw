package com.founder.xy.api.nis;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.api.ApiManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class TopicsApiManager extends BaseApiManager {

    public boolean hotTopics(int siteID) throws E5Exception {
        String tenantCode = Tenant.DEFAULTCODE;
        int topicsLibId = LibHelper.getLibID(DocTypes.TOPICS.typeID(),
                tenantCode);

        String tableName=LibHelper.getLibTable(topicsLibId);
        DBSession conn = null;
        IResultSet rs = null;
        JSONObject json = new JSONObject();
        try {
//            DocumentManager docManager = DocumentManagerFactory.getInstance();
//            Document[] topicDocs = docManager.find(topicsLibId, "entry_userID=? ", new Object[] {userID});
//            String fileID="";
//            for (Document document : entryDocs) {
//                fileID+=","+ document.getLong("entry_targetID");
//            }
//            if(fileID.startsWith(",")){
//                fileID=fileID.substring(1);
//            }
//            if(fileID.equals("")){
//                entjson.put("list", new JSONArray());
//                RedisManager.setWeekly(RedisKey.MY_ENTRY_KEY+userID+"."+page, entjson.toString());
//                return true;
//            }
            String sql = "select SYS_DOCUMENTID,SYS_TOPIC,a_lastPubTime from "
                    + tableName
                    + " where a_status = 0 and a_siteID = " + siteID
                    + " order by a_lastPubTime desc ";
            conn = Context.getDBSession();

//            int start =page * ApiManager.ALIST_COUNT;
//            int count=(page +1)* ApiManager.ALIST_COUNT;
            String querySql = conn.getDialect().getLimitString(sql, 0, 10);

            rs = conn.executeQuery(querySql, null);

            JSONArray dates = new JSONArray();
            while (rs.next()) {
                JSONObject date = this.assembleTopics(siteID, rs, topicsLibId);
                dates.add(date);
            }
            json.put("list", dates);
            RedisManager.set(RedisKey.APP_HOT_TOPICS_KEY + siteID, json.toString());

        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        return true;
    }

    private JSONObject assembleTopics(int siteID, IResultSet rs, Integer docLibId)
            throws E5Exception {

        JSONObject date = new JSONObject();
        try {
            date.put("topicID", StringUtils.getNotNull(rs.getString("SYS_DOCUMENTID")));
            date.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
            date.put("publishtime", InfoHelper.formatDate(rs,"a_lastPubTime"));
//            date.put("startTime", StringUtils.getNotNull(rs.getString("a_startTime")));
//            date.put("endTime", StringUtils.getNotNull(rs.getString("a_endTime")));
//            date.put("abstract", StringUtils.getNotNull(rs.getString("a_abstract")));

//            date.put("organizer",StringUtils.getNotNull(rs.getString("a_organizer")));  //主办方
//            date.put("countClick", rs.getInt("a_countClick")+rs.getInt("a_countClickInitial")+rs.getInt("a_countShareClick"));
//            date.put("countDiscuss", rs.getInt("a_countDiscuss"));
//            date.put("countPraise", rs.getInt("a_countPraise"));
//            date.put("participatorNum", rs.getInt("a_count"));

//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            if (rs.getTimestamp("a_startTime").getTime() > df.parse(df.format(new Date())).getTime()) {
//                date.put("activityStatus", "活动未开始");
//            } else if (rs.getTimestamp("a_endTime").getTime() > df.parse(df.format(new Date())).getTime()) {
//                date.put("activityStatus", "活动进行中");
//            } else {
//                date.put("activityStatus", "活动已结束");
//            }

//            JSONArray inJsonArr = jsonAttachments(docLibId, rs.getInt("SYS_DOCUMENTID"));
//            String imgUrl = getImgUrlFromAtts(inJsonArr);
//            date.put("imageUrl", imgUrl);
//            date.put("attachments", imgUrl);//旧版本的做法，attachments里存了第一个图片
//
//            date.put("articleType", 102);
//
//            //旧版中使用这里的shareUrl，新版改用启动api中的参数
//            String theShareUrl = InfoHelper.getConfig("互动", "活动分享页地址")
//                    +"?siteId=" + siteID + "&fileId="+rs.getInt("SYS_DOCUMENTID");
//            date.put("theShareUrl", theShareUrl);
//
//            theShareUrl = UrlHelper.getActivityContentUrl(siteID, rs.getInt("SYS_DOCUMENTID"));
//            date.put("theContentUrl", theShareUrl);
//            //entryType=1为评论报名，0为活动报名，现废弃1：评论报名功能，设置固定值为0：活动报名
//            date.put("entryType",0);
//            date.put("statusEntry", rs.getInt("a_statusEntry"));

        } catch (SQLException e) {
            throw new E5Exception(e);
        }
        return date;
    }

    public boolean topicsByGroup(int siteID) throws E5Exception {
        String tenantCode = Tenant.DEFAULTCODE;
        int columnTopicsLibId = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(),
                tenantCode);
        int topicsLibId = LibHelper.getLibID(DocTypes.TOPICS.typeID(),
                tenantCode);

//        String columnTopicsTableName=LibHelper.getLibTable(columnTopicsLibId);
        String topicsTableName=LibHelper.getLibTable(topicsLibId);
        DBSession conn = null;
        IResultSet rs = null;
        JSONObject json = new JSONObject();
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] columnTopicDocs = docManager.find(columnTopicsLibId, "col_siteID=? order by col_displayOrder asc,sys_documentid desc ", new Object[] {siteID});

            conn = Context.getDBSession();
            JSONArray columnTopicsArr = new JSONArray();
            for (Document document : columnTopicDocs) {
                JSONObject columnTopics = new JSONObject();
                long topicGroupID = document.getDocID();
                columnTopics.put("topicGroupID", document.getDocID());
                columnTopics.put("topicGroupName", document.getString("col_name"));

                String sql = "select SYS_DOCUMENTID,SYS_TOPIC,a_lastPubTime from "
                    + topicsTableName
                    + " where a_status = 0 and a_groupID = " + topicGroupID
                    + " order by a_lastPubTime desc ";

//            int start =page * ApiManager.ALIST_COUNT;
//            int count=(page +1)* ApiManager.ALIST_COUNT;
                String querySql = conn.getDialect().getLimitString(sql, 0, 10);
                try {
                    rs = conn.executeQuery(querySql, null);

                    JSONArray dates = new JSONArray();
                    while (rs.next()) {
                        JSONObject date = this.assembleTopics(siteID, rs, topicsLibId);
                        dates.add(date);
                    }
                    columnTopics.put("topics",dates);

                    columnTopicsArr.add(columnTopics);
                }catch (SQLException e) {
                    throw new E5Exception(e);
                } finally {
                    ResourceMgr.closeQuietly(rs);
                }
            }
            json.put("list", columnTopicsArr);
            RedisManager.set(RedisKey.APP_TOPICSBYGROUP_KEY + siteID, json.toString());
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
        return true;
    }

    public boolean topics(int siteID) throws E5Exception {
        String tenantCode = Tenant.DEFAULTCODE;
        int topicsLibId = LibHelper.getLibID(DocTypes.TOPICS.typeID(),
                tenantCode);

        String tableName=LibHelper.getLibTable(topicsLibId);
        DBSession conn = null;
        IResultSet rs = null;
        try {
        	RedisManager.del(RedisKey.WEB_TOPICS_KEY + siteID);
//            DocumentManager docManager = DocumentManagerFactory.getInstance();
//            Document[] topicDocs = docManager.find(topicsLibId, "entry_userID=? ", new Object[] {userID});
//            String fileID="";
//            for (Document document : entryDocs) {
//                fileID+=","+ document.getLong("entry_targetID");
//            }
//            if(fileID.startsWith(",")){
//                fileID=fileID.substring(1);
//            }
//            if(fileID.equals("")){
//                entjson.put("list", new JSONArray());
//                RedisManager.setWeekly(RedisKey.MY_ENTRY_KEY+userID+"."+page, entjson.toString());
//                return true;
//            }
            String sql = "select SYS_DOCUMENTID,SYS_TOPIC,a_lastPubTime from "
                    + tableName
                    + " where a_status = 0 and a_siteID = " + siteID
                    + " order by a_lastPubTime desc ";
            conn = Context.getDBSession();

//            int start =page * ApiManager.ALIST_COUNT;
//            int count=(page +1)* ApiManager.ALIST_COUNT;
//            String querySql = conn.getDialect().getLimitString(sql, 0, 10);

            rs = conn.executeQuery(sql, null);

            while (rs.next()) {
                JSONObject date = this.assembleTopics(siteID, rs, topicsLibId);
                
                RedisManager.rpush(RedisKey.WEB_TOPICS_KEY + siteID, date.toString());
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        //若没有，则插入一个空对象
  		if(RedisManager.llen(RedisKey.WEB_TOPICS_KEY + siteID)==0){
  			RedisManager.rpush(RedisKey.WEB_TOPICS_KEY + siteID, new JSONObject().toString());
  		}
  		//设置过期时间
  		RedisManager.setTime(RedisKey.WEB_TOPICS_KEY + siteID, RedisManager.minute1);
        return true;
    }
    
    public boolean articleTopics(int articleID, int channel) throws E5Exception {
    	String SQL =  "select a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
		Object[] params = new Object[]{articleID, channel+1};
		DBSession db = null;
		IResultSet rs = null;
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			db = Context.getDBSession();
			rs = db.executeQuery(SQL, params);
			while (rs.next()) {
				JSONObject topicJson = new JSONObject();
				topicJson.put("topicID",rs.getInt("a_topicID"));
				topicJson.put("topicName",rs.getString("a_topicName"));
//				linkTitle = rs.getString("a_linkTitle");
//				topicID = rs.getInt("a_attr");

				array.add(topicJson);
			}
			json.put("list", array);
	        RedisManager.set(RedisKey.ARTICLE_TOPICS_KEY + channel + "." + articleID, json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
        return true;
    }

    public boolean articleTopicsByGroup(int articleID, int channel, int groupID) {
        String SQL =  "select a.a_topicID, a.a_topicName from xy_topicrelart a left JOIN xy_topics b on a.a_topicID " +
                "= b.SYS_DOCUMENTID where a.a_articleID=? and a.a_channel=? and b.SYS_DELETEFLAG = 0";
        Object[] params;
        if(groupID!=0){
            SQL += "  and a_groupID = ? ";
            params= new Object[]{articleID, channel+1,groupID};
        }else{
            params= new Object[]{articleID, channel+1};
        }
        SQL += " order by a.a_articleID desc,a.a_topicID desc";


        DBSession db = null;
        IResultSet rs = null;
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            db = Context.getDBSession();
            rs = db.executeQuery(SQL, params);
            while (rs.next()) {
                JSONObject topicJson = new JSONObject();
                topicJson.put("topicID",rs.getInt("a_topicID"));
                topicJson.put("topicName",rs.getString("a_topicName"));

                array.add(topicJson);
            }
            json.put("list", array);
            RedisManager.set(RedisKey.ARTICLE_TOPICSGROUP_KEY + channel + "." + articleID+"."+groupID, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
        return true;
    }

    public boolean webTopicsByGroup(int siteID, int groupID) throws E5Exception {
        String tenantCode = Tenant.DEFAULTCODE;
        int topicsLibId = LibHelper.getLibID(DocTypes.TOPICS.typeID(),
                tenantCode);

        String tableName=LibHelper.getLibTable(topicsLibId);
        DBSession conn = null;
        IResultSet rs = null;
        try {
            RedisManager.del(RedisKey.WEB_TOPICSBYGROUP_KEY + siteID +"."+ groupID);
            String sql = "select SYS_DOCUMENTID,SYS_TOPIC,a_lastPubTime from "
                    + tableName
                    + " where a_status = 0 and a_siteID = " + siteID
                    + " and a_groupID = " + groupID
                    + " order by a_lastPubTime desc ";
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, null);

            while (rs.next()) {
                JSONObject date = this.assembleTopics(siteID, rs, topicsLibId);

                RedisManager.rpush(RedisKey.WEB_TOPICSBYGROUP_KEY + siteID +"."+ groupID, date.toString());
            }
        } catch (SQLException e) {
            throw new E5Exception(e);
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
        //若没有，则插入一个空对象
        if(RedisManager.llen(RedisKey.WEB_TOPICSBYGROUP_KEY + siteID +"."+ groupID)==0){
            RedisManager.rpush(RedisKey.WEB_TOPICSBYGROUP_KEY + siteID +"."+ groupID, new JSONObject().toString());
        }
        //设置过期时间
        RedisManager.setTime(RedisKey.WEB_TOPICSBYGROUP_KEY + siteID +"."+ groupID, 30);

        return true;
    }
}
