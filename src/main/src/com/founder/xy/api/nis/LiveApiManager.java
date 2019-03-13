package com.founder.xy.api.nis;

import static com.founder.xy.api.ApiManager.ALIST_COUNT;
import static com.founder.xy.redis.RedisKey.APP_LIVELIST_KEY;
import static com.founder.xy.redis.RedisKey.APP_LIVE_MAIN_KEY;
import static com.founder.xy.redis.RedisKey.APP_LIVE_STATUS_KEY;
import static com.founder.xy.redis.RedisKey.APP_LIVE_SUBLIST_KEY;

import java.util.List;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 与外网api通讯的互动直播Api
 */
@Service
public class LiveApiManager extends BaseApiManager {
    /**
     * 预告列表
     * 1. 直接从数据库中查询status == 0 的直播
     * 2. 放到redis中
     *
     * @return
     */
    public boolean liveComing(int siteID) {
        //1. 直接从数据库中查询status == 0 的直播
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] liveComingDocuments = docManager.find(LibHelper.getLive(),
                                                             "a_status=0 and a_siteID=? order by a_startTime",
                                                             new Object[]{siteID});
            //把document组装成 json对象
            JSONObject json = new JSONObject();
            assembleLiveComingJson(liveComingDocuments, json);
            //2. 放到redis中
            String key = RedisManager.getKeyBySite(RedisKey.APP_LIVE_COMING_KEY, siteID);
            RedisManager.setTimeless(key, json.toString());
            return true;
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean liveView(long id, int page) {
        JSONObject json = new JSONObject();
        json.put("main", queryMainJson(id, page));
        json.put("list", queryListJson(id, page));
        RedisManager.set(APP_LIVELIST_KEY + id + "." + page, json.toString());
        return true;
    }

    public String liveDetail(long id, int page) {
        JSONObject json = new JSONObject();
        json.put("main", queryMainJson(id, page));
        json.put("list", queryListJson(id, page));
        return json.toString();
    }

    /**
     * 现场直播App：提交直播报道
     */
    public boolean commitLive(String data) throws E5Exception {
        int docLibID = LibHelper.getLibID(DocTypes.LIVEITEM.typeID(), Tenant.DEFAULTCODE);
        long docID = InfoHelper.getNextDocID(DocTypes.LIVE.typeID());//与直播话题统一计数，以兼容旧版本

        JSONObject obj = JSONObject.fromObject(data);

        // 图片和视频作为帖子的附件，存入互动附件表
        String attachments = setImgVioUrl(obj, docID, docLibID);

        // 使用ProcHelper.init()来设置帖子的初始流程等信息
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.newDocument(docLibID, docID);
        ProcHelper.initDoc(doc);

        long rootId = obj.getLong("rootID");
        doc.set("SYS_AUTHORS", obj.getString("userName"));
        doc.set("SYS_AUTHORID", obj.getLong("userID"));
        doc.set("a_rootID", rootId);
        doc.set("a_sourceType", obj.getInt("sourceType")); //直播员类型
        doc.set("a_content", obj.getString("content"));
        doc.set("a_attachments", attachments); //附件json
        doc.set("a_longitude", obj.getDouble("longitude"));
        doc.set("a_latitude", obj.getDouble("latitude"));
        doc.set("a_location", StringUtils.getNotNull(obj.getString("location")));
        docManager.save(doc);

        //清除 redis 缓存
        RedisManager.clearKeyPages(APP_LIVE_SUBLIST_KEY + rootId);
        RedisManager.clearKeyPages(APP_LIVELIST_KEY + rootId);
        return true;
    }

    /**
     * 现场直播App：读在进行中的直播列表。无缓存。
     */
    public String lives(int siteID) {
        JSONObject _json, resultJson = new JSONObject();
        JSONArray array = new JSONArray();
        resultJson.put("code", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document[] documents = docManager.find(LibHelper.getLive(),
                                                   "(a_status=1 or a_status=0) and a_siteID="+siteID+" order by SYS_DOCUMENTID desc", null);
            for (Document document : documents) {
                _json = new JSONObject();
                _json.put("fileId", document.getDocID());
                _json.put("title", document.getTopic());
                if(null!=document.getTimestamp("a_startTime")&&!"".equals(document.getTimestamp("a_startTime"))){
                    _json.put("startTime", InfoHelper.formatDate(document.getTimestamp("a_startTime")));
                }else{
                    _json.put("startTime", "");
                }
                _json.put("type", document.getInt("a_type"));
                String config = document.getString("a_config");
                if (StringUtils.isBlank(config)) {
                    config = "{}";
                }
                _json.put("config", config);

                _json.put("authorID", document.getInt("SYS_AUTHORID"));
                _json.put("author", document.getString("SYS_AUTHORS"));
                _json.put("status", document.getInt("a_status"));

                array.add(_json);
            }
            resultJson.put("list", array);

        } catch (E5Exception e) {
            resultJson.put("code", -1);
            resultJson.put("error", "查询时出现异常！");
            e.printStackTrace();
        }

        return resultJson.toString();
    }

    /**
     * 现场直播App：读各状态的直播列表。无缓存。
     */
    public String allLives(int siteID,String status,int lastID) {
        JSONObject resultJson = new JSONObject();
        JSONArray array = new JSONArray();
        resultJson.put("code", 0);

        StringBuilder sql = new StringBuilder("select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED,SYS_LASTMODIFIED,SYS_CURRENTFLOW,SYS_ISLOCKED,SYS_CURRENTNODE,SYS_CURRENTSTATUS,SYS_CURRENTUSERID,SYS_CURRENTUSERNAME,SYS_AUTHORS,SYS_TOPIC,SYS_HAVERELATION,SYS_ISKEEP,SYS_HAVEATTACH,SYS_AUTHORID,a_siteID,a_startTime,a_endTime,a_type,a_status,a_config,a_streamIDs,a_tag,a_url,a_countClick,a_countClickInitial,a_countDiscuss,a_countPraise,a_countShare,a_countShareClick,a_isSensitive,a_discussClosed,a_order,a_longitude,a_latitude,a_location from xy_nisLive where ");
        StringBuilder whereSql = new StringBuilder();
        String[] statuses = status.split(",");
        if(statuses.length == 1){
            if(statuses[0].equals("-1")){
                whereSql.append("");
            }else{
                whereSql.append("a_status=").append(statuses[0]).append(" and ");
            }
        }else {
            whereSql.append("a_status in (").append(status).append(") and ");
        }

        if(lastID != 0){
            whereSql.append("SYS_DOCUMENTID < ").append(lastID).append(" and ");
        }

        whereSql.append("a_siteID=").append(siteID).append(" order by SYS_DOCUMENTID desc ");

        sql.append(whereSql);
        try {
            queryLives(array,sql.toString());
            resultJson.put("list", array);
        } catch (Exception e) {
            resultJson.put("code", -1);
            resultJson.put("error", "查询时出现异常！");
            e.printStackTrace();
        }

        return resultJson.toString();
    }

    private void queryLives(JSONArray array,String sql) throws Exception{
        JSONObject jsonObject = new JSONObject();
        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            sql = db.getDialect().getLimitString(sql, 0, LIST_COUNT);

            rs = db.executeQuery(sql, null);
            while (rs.next()){
                jsonObject = new JSONObject();
                jsonObject.put("fileId", rs.getInt("SYS_DOCUMENTID"));
                jsonObject.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                jsonObject.put("startTime", StringUtils.getNotNull(rs.getString("a_startTime")));
                jsonObject.put("type", rs.getInt("a_type"));
                String config = rs.getString("a_config");
                if (StringUtils.isBlank(config)) {
                    config = "{}";
                }
                jsonObject.put("config", config);

                jsonObject.put("authorID", rs.getInt("SYS_AUTHORID"));
                jsonObject.put("author", rs.getString("SYS_AUTHORS"));
                jsonObject.put("status", rs.getInt("a_status"));

                array.add(jsonObject);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    private int getInt(int value) {
        if (value < 0) return 0;
        return value;
    }

    private void assembleLiveComingJson(Document[] liveComingDocuments, JSONObject json) {
        JSONArray array = new JSONArray();
        JSONObject _json, configJson;
        String config;
        for (Document document : liveComingDocuments) {
            _json = new JSONObject();
            _json.put("fileId", document.getDocID());
            _json.put("title", document.getTopic());
            _json.put("startTime", InfoHelper.formatDate(document.getTimestamp("a_startTime")));
            _json.put("type", document.getInt("a_type"));
            config = document.getString("a_config");
            if (!StringUtils.isBlank(config)) {
                configJson = JSONObject.fromObject(config);
                if (configJson.containsKey("webBanner")) {
                    _json.put("webBannerUrl", configJson.getString("webBanner"));
                }
                if (configJson.containsKey("appBanner")) {
                    _json.put("appBannerUrl", configJson.getString("appBanner"));
                }
            }
            array.add(_json);
        }
        json.put("list", array);
    }


    private JSONArray queryListJson(long id, int page) {
        JSONArray array = new JSONArray();
        String key = APP_LIVE_SUBLIST_KEY + id + "." + page;
        if (RedisManager.exists(key)) {
            String value = RedisManager.get(key);
            if (value != null) {
                array = JSONArray.fromObject(value);
                return array;
            }
        }

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        JSONObject json;
        String iconUrl = UrlHelper.apiUserIcon();
        try {
            DocLib docLib = LibHelper.getLib(DocTypes.LIVEITEM.typeID(), Tenant.DEFAULTCODE);
            String sql = "select SYS_DOCUMENTID from " + docLib.getDocLibTable()
            		+ " where a_rootID=? ORDER BY a_isTop desc,SYS_DOCUMENTID desc";
            List<Long> ids = queryOneField(docLib, sql, new Object[]{id}, page * ALIST_COUNT, ALIST_COUNT);
            
            long[] docIDs = new long[ids.size()];
            for (int i = 0; i < docIDs.length; i++) {
				docIDs[i] = ids.get(i);
			}
            
            Document[] documents = docManager.get(docLib.getDocLibID(), docIDs);
            for (Document doc : documents) {
                json = new JSONObject();
                json.put("isTop", getInt(doc.getInt("a_isTop")));
                json.put("fileId", doc.getDocID());
                json.put("title", doc.getString("a_title"));
                json.put("publishtime", InfoHelper.formatDate(doc.getCreated()));
                json.put("user", doc.getAuthors());
                long usrID = doc.getLong("SYS_AUTHORID");
                json.put("userID", usrID);
                json.put("userIcon", iconUrl + "?uid=" + usrID);
                json.put("content", StringUtils.getNotNull(doc.getString("a_content")));
                json.put("sourceType", getInt(doc.getInt("a_sourceType")));

                //附件
                JSONArray inJsonArr = jsonAttachments(docLib.getDocLibID(), doc.getDocID());
                json.put("attachments", inJsonArr);

                array.add(json);
            }
            RedisManager.set(key, array.toString());

        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    private JSONObject queryMainJson(long id, int page) {
        JSONObject json = new JSONObject();
        if (page != 0) {
            return json;
        }

        if (RedisManager.exists(APP_LIVE_MAIN_KEY + id)) {
            String value = RedisManager.get(APP_LIVE_MAIN_KEY + id);
            if (value != null) {
                json = JSONObject.fromObject(value);
                return json;
            }
        }

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document doc = docManager.get(LibHelper.getLibID(DocTypes.LIVE.typeID(), Tenant.DEFAULTCODE), id);

            json.put("startTime", InfoHelper.formatDate(doc.getTimestamp("a_startTime")));
            json.put("type", doc.getInt("a_type"));

            json.put("fileId", doc.getDocID());
            json.put("title", doc.getTopic());
            json.put("publishtime", InfoHelper.formatDate(doc.getCreated()));

            json.put("tag", StringUtils.getNotNull(doc.getString("a_tag")));

            json.put("user", doc.getAuthors());
            json.put("url", StringUtils.getNotNull(doc.getString("a_url")));
            long usrID = doc.getLong("SYS_AUTHORID");
            json.put("userID", usrID);
            json.put("userIcon", UrlHelper.apiUserIcon() + "?uid=" + usrID);
            json.put("countDiscuss", doc.getLong("a_countDiscuss"));
            json.put("countPraise", doc.getLong("a_countPraise"));
            json.put("content", StringUtils.getNotNull(doc.getString("a_content")));

            json.put("sourceType", getInt(doc.getInt("a_sourceType")));
            json.put("discussClosed", getInt(doc.getInt("a_discussClosed")));
            String config = doc.getString("a_config");
            if (StringUtils.isBlank(config)) {
                config = "{}";
            }
            json.put("config", config);
            json.put("streamIds", doc.getString("a_streamIDs"));
            json.put("status", doc.getInt("a_status"));

            RedisManager.set(APP_LIVE_MAIN_KEY + id, json.toString());
        } catch (E5Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public void saveLivePlaybackUrl(String data) throws Exception {
        JSONObject json = JSONObject.fromObject(data);
        if (json != null && json.containsKey("video_url") && json.containsKey("stream_id")) {
            String streamId = json.getString("stream_id");
            String url = json.getString("video_url");
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] lives = docManager.find(LibHelper.getLive(),
                                               "a_streamIDs like ?", new Object[]{"%" + streamId + ",%"});
            JSONObject configJson, j;
            JSONArray videoArray;
            for (Document live : lives) {
                configJson = JSONObject.fromObject(live.getString("a_config"));
                videoArray = configJson.getJSONArray("videos");
                for (int i = 0, len = videoArray.size(); i < len; i++) {
                    j = videoArray.getJSONObject(i);
                    if (streamId.equals(j.getString("streamID"))) {
                        j.put("appPlaybackUrl", url);
                        j.put("webPlaybackUrl", url);
                    }
                }
                live.set("a_config", configJson.toString());
                docManager.save(live);
            }

        }

    }

    public String queryLiveStatus(String data) {
        JSONObject json = JSONObject.fromObject(data);
        JSONObject result = new JSONObject();
        String _s;
        if (!json.has("streamIDs")) {
            throw new RuntimeException("没有streamIDs");
        }
        JSONArray streamIDs = json.getJSONArray("streamIDs");
        if (streamIDs == null || streamIDs.isEmpty()) {
            throw new RuntimeException("streamIDs 为空！streamIDs: " + data);
        }
        for (int i = 0, len = streamIDs.size(); i < len; i++) {
            _s = streamIDs.getString(i);
            if (RedisManager.exists(APP_LIVE_STATUS_KEY + "current." + _s)) {
                result.put(_s, RedisManager.get(APP_LIVE_STATUS_KEY + "current." + _s));
            } else {
                result.put(_s, "501");//没存在 - 可能是还从未使用
            }
        }
        result.put("code", 0);
        return result.toString();
    }
}
