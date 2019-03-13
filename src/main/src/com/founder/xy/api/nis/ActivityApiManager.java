package com.founder.xy.api.nis;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

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
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 活动Api功能
 */
@Service
public class ActivityApiManager extends BaseApiManager {
	private static final String SQL_PREFIX = "select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,"
			+ "SYS_LASTMODIFIED,a_abstract,SYS_CREATED as publishTime, "
			+ "a_startTime,a_countClick,a_countClickInitial,a_countShareClick,a_countDiscuss,"
			+ "a_countPraise,a_endTime,a_count,a_status,a_content,"
			+ "a_picBig,a_picMiddle,a_picSmall,a_statusEntry,a_organizer,a_shareUrl from ";
	//活动列表
	public boolean activityList(int siteID, int start, int count) {
		count = ApiManager.CACHE_LENGTH;
		
		String tenantCode = Tenant.DEFAULTCODE;
		int activityLibId = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(),
				tenantCode);

		Object[] params = new Object[] { siteID};
		DBSession conn = null;
		IResultSet rs = null;
		try {
			String tableName = LibHelper.getLibTable(activityLibId);
			String sql = SQL_PREFIX + tableName
					+ " where a_status=1 and a_siteID =? order by SYS_CREATED desc";
			
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(sql, start, count);

			rs = conn.executeQuery(querySql, params);

			JSONArray datas = new JSONArray();
			while (rs.next()) {
				JSONObject data = this.assembleActivity(siteID, rs, activityLibId);
				datas.add(data);
			}
			
			String key = RedisManager.getKeyBySite(RedisKey.APP_ACTIVITY_KEY, siteID) + start;
			RedisManager.setLonger(key, datas.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}
	//活动详情
    public boolean activityDetail(int siteId,int fileId) throws E5Exception {
		int docLibID = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(), Tenant.DEFAULTCODE);
		JSONObject data = ActivityJsonHelper.article(docLibID, fileId);

		if(data!=null){
		    data.put("siteID",siteId);
        }

		String redisK = RedisKey.APP_ACTIVITY_DETAIL_KEY + fileId;
		RedisManager.set(redisK, data.toString());
		
		return true;
	}

	//活动报名名单
    public boolean activityEntryList(int fileId)  {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String tenantCode = Tenant.DEFAULTCODE;
        JSONObject json=new JSONObject();
        JSONArray jsonArr=new JSONArray();
        String count = "0";//报名人数
        try {
        	
        	int docLibID = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(), Tenant.DEFAULTCODE);
    		Document doc = docManager.get(docLibID, fileId);
    		if (doc == null) {
    			 JSONObject jsonEnt=new JSONObject();
    	         jsonArr.add(jsonEnt);
    	         json.put("list", jsonArr);
    	         json.put("count", count);
    	         RedisManager.set(RedisKey.APP_ACTIVITY_ENTRY+fileId,json.toString());
    	         return true;
    		}
    		if(doc.getInt("a_statusEntry")!=1){
                json.put("list", jsonArr);
   	         	json.put("count", count);
   	         	RedisManager.set(RedisKey.APP_ACTIVITY_ENTRY+fileId,json.toString());
   	         	return true;
    		}
			
        	int entryLibId = LibHelper.getLibID(DocTypes.ENTRY.typeID(),tenantCode); 
        	Document[] entryDocs = docManager.find(entryLibId, "entry_targetID=? ", new Object[] {fileId});
            count = String.valueOf(entryDocs.length);
        	for(Document document:entryDocs){
                JSONObject jsonEnt=new JSONObject();
                long userID = document.getLong("entry_userID") ;
                jsonEnt.put("id", document.getLong("SYS_DOCUMENTID"));
                jsonEnt.put("userID", userID);
                jsonEnt.put("name", document.getString("entry_userName"));
                jsonEnt.put("userIcon", UrlHelper.apiUserIcon() + "?uid=" + userID);
                jsonArr.add(jsonEnt);
            }
        	
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        json.put("list", jsonArr);
        json.put("count", count);
		RedisManager.setWeekly(RedisKey.APP_ACTIVITY_ENTRY+fileId,json.toString());
		return true;
    }
    
	public boolean myActivityList(int siteID, int userID,int page) throws E5Exception{

		String tenantCode = Tenant.DEFAULTCODE;
		int activityLibId = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(),tenantCode); 
		int entryLibId = LibHelper.getLibID(DocTypes.ENTRY.typeID(),tenantCode); 
		String tableName=LibHelper.getLibTable(activityLibId);
		DBSession conn = null;
		IResultSet rs = null;
		JSONObject entjson = new JSONObject();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] entryDocs = docManager.find(entryLibId, "entry_userID=? ", new Object[] {userID});
			String fileID="";
			for (Document document : entryDocs) {
				fileID+=","+ document.getLong("entry_targetID");
			}
			if(fileID.startsWith(",")){
				fileID=fileID.substring(1);
			}
			if(fileID.equals("")){
				entjson.put("list", new JSONArray());
				RedisManager.setWeekly(RedisKey.MY_ENTRY_KEY+userID+"."+page, entjson.toString());
				return true;
			}
			String sql = "select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC, "
					+  " SYS_LASTMODIFIED,a_abstract,"
					+ "SYS_CREATED as publishTime, "
					+ "a_startTime,a_countClick,a_countClickInitial,a_countShareClick,a_countDiscuss,"
					+ "a_countPraise,a_endTime,a_count,a_status,a_content,"
					+ " a_picBig,a_picMiddle,a_picSmall,a_statusEntry,a_organizer from "
					+ tableName
					+ " where SYS_DOCUMENTID in ( " +fileID +" ) "
					+ " order by SYS_DOCUMENTID desc ";
			conn = Context.getDBSession();
			
			int start =page * ApiManager.ALIST_COUNT;
			int count=(page +1)* ApiManager.ALIST_COUNT;
			String querySql = conn.getDialect().getLimitString(sql, start, count);

			rs = conn.executeQuery(querySql, null);
			
			JSONArray dates = new JSONArray();
			while (rs.next()) {
				JSONObject date = this.assembleActivity(siteID, rs, activityLibId);
				dates.add(date);
			}
			entjson.put("list", dates);
			RedisManager.setWeekly(RedisKey.MY_ENTRY_KEY+userID+"."+page, entjson.toString());

		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}

	/**
	 * 推荐模块项中需要的列表，从Redis的列表里读
	 */
	public String getModuleList(int siteID, int count) {
		//读出Redis里的列表
		String key = RedisManager.getKeyBySite(RedisKey.APP_ACTIVITY_KEY, siteID) + 0;
		String list = RedisManager.get(key);
		if (list == null) {
			boolean ok = activityList(siteID, 0, ApiManager.CACHE_LENGTH);
			if (ok) list = RedisManager.get(key);
		}
		
		//读列表的前几个做为结果
		JSONArray jsonNewArr = getSomeFromList(list, count);
		return jsonNewArr.toString();
	}
	
	public JSONObject assembleActivity(int siteID, int docLibID, long docID)  {
		JSONObject data = new JSONObject();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			data.put("fileId", "" + doc.getDocID());
			data.put("version", doc.getTimestamp("SYS_LASTMODIFIED").getTime());
			data.put("title", StringUtils.getNotNull(doc.getString("SYS_TOPIC")));
			data.put("publishtime", InfoHelper.formatDate(doc.getCreated()));
			data.put("startTime", StringUtils.getNotNull(doc.getString("a_startTime")));
			data.put("endTime", StringUtils.getNotNull(doc.getString("a_endTime")));
			data.put("abstract", StringUtils.getNotNull(doc.getString("a_abstract")));
			
			data.put("organizer",StringUtils.getNotNull(doc.getString("a_organizer")));  //主办方
			data.put("countClick", doc.getInt("a_countClick")+doc.getInt("a_countClickInitial")+doc.getInt("a_countShareClick"));
			data.put("countDiscuss", doc.getInt("a_countDiscuss"));
			data.put("countPraise", doc.getInt("a_countPraise"));
			data.put("participatorNum", doc.getInt("a_count"));
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(doc.getDate("a_startTime").getTime()>df.parse(df.format(new Date())).getTime()){
				data.put("activityStatus","活动未开始");
			}else if(doc.getDate("a_endTime").getTime()>df.parse(df.format(new Date())).getTime()){
					data.put("activityStatus","活动进行中");
			}else{
				data.put("activityStatus","活动已结束");
			}
			JSONArray inJsonArr = jsonAttachments(docLibID, docID);
			String imgUrl = getImgUrlFromAtts(inJsonArr);
			data.put("imageUrl", imgUrl);
			data.put("attachments", imgUrl);//旧版本的做法，attachments里存了第一个图片
			
			String theShareUrl = UrlHelper.getActivityContentUrl(siteID, doc.getDocID());
			data.put("theContentUrl", theShareUrl);
	
			data.put("entryType",0);
			data.put("statusEntry", doc.getInt("a_statusEntry")); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	
	}

	private JSONObject assembleActivity(int siteID, IResultSet rs, Integer docLibId)
			throws E5Exception {
	
		JSONObject date = new JSONObject();
		try {
			date.put("fileId", StringUtils.getNotNull(rs.getString("SYS_DOCUMENTID")));
			date.put("version", rs.getTimestamp("SYS_LASTMODIFIED").getTime());
			date.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
			date.put("publishtime", InfoHelper.formatDate(rs,"publishtime"));//活动的创建时间作为发布时间
			date.put("startTime", StringUtils.getNotNull(rs.getString("a_startTime")));
			date.put("endTime", StringUtils.getNotNull(rs.getString("a_endTime")));
			date.put("abstract", StringUtils.getNotNull(rs.getString("a_abstract")));
			
			date.put("organizer",StringUtils.getNotNull(rs.getString("a_organizer")));  //主办方
			date.put("countClick", rs.getInt("a_countClick")+rs.getInt("a_countClickInitial")+rs.getInt("a_countShareClick"));
			date.put("countDiscuss", rs.getInt("a_countDiscuss"));
			date.put("countPraise", rs.getInt("a_countPraise"));
			date.put("participatorNum", rs.getInt("a_count"));
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (rs.getTimestamp("a_startTime").getTime() > df.parse(df.format(new Date())).getTime()) {
				date.put("activityStatus", "活动未开始");
			} else if (rs.getTimestamp("a_endTime").getTime() > df.parse(df.format(new Date())).getTime()) {
				date.put("activityStatus", "活动进行中");
			} else {
				date.put("activityStatus", "活动已结束");
			}
			
			JSONArray inJsonArr = jsonAttachments(docLibId, rs.getInt("SYS_DOCUMENTID"));
			String imgUrl = getImgUrlFromAtts(inJsonArr);
			date.put("imageUrl", imgUrl);
			date.put("attachments", imgUrl);//旧版本的做法，attachments里存了第一个图片
	
			date.put("articleType", 102);
			
			//旧版中使用这里的shareUrl，新版改用启动api中的参数
//			String theShareUrl = InfoHelper.getConfig("互动", "活动分享页地址")
//					+"?siteId=" + siteID + "&fileId="+rs.getInt("SYS_DOCUMENTID");

			date.put("theShareUrl", StringUtils.getNotNull(rs.getString("a_shareUrl")));
			
			String theShareUrl = UrlHelper.getActivityContentUrl(siteID, rs.getInt("SYS_DOCUMENTID"));
			date.put("theContentUrl", theShareUrl);
			//entryType=1为评论报名，0为活动报名，现废弃1：评论报名功能，设置固定值为0：活动报名
			date.put("entryType",0);
			date.put("statusEntry", rs.getInt("a_statusEntry")); 
			
		} catch (SQLException e) {
			throw new E5Exception(e);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
