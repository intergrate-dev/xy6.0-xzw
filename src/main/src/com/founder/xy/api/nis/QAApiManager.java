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
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import static com.founder.xy.api.ApiManager.ALIST_COUNT;

/**
 * 互动问答Api功能
 */
@Service
public class QAApiManager extends BaseApiManager {
	private static final String SQL_QALIST=
			" select SYS_DOCUMENTID,a_countDiscuss,a_countPraise,SYS_TOPIC,SYS_CURRENTSTATUS,SYS_AUTHORID, "
			+ "SYS_LASTMODIFIED,"
			+ "SYS_CREATED as publishTime,  "
			+ "a_sourceType,a_content,a_attachments,a_group,a_answer";
	//问政列表
	public boolean qaList(int siteID, int page, int groupId){

		String tenantCode = Tenant.DEFAULTCODE;
		int qaLibId = LibHelper.getLibID(DocTypes.QA.typeID(), tenantCode);

		String shareUrl = InfoHelper.getConfig("互动", "问答分享页地址");
		DBSession conn = null;
		IResultSet rs = null;
		try {
			String tableName=LibHelper.getLibTable(qaLibId);
			String sql=null;
			Object[] params =null;
			
			if (groupId > 0) {
				sql = SQL_QALIST + " from " + tableName + " where a_group_ID=? and ";
				params = new Object[] { groupId, siteID };
			} else {
				sql = SQL_QALIST + ",a_group_ID " + " from " + tableName + " where ";
				params = new Object[] { siteID };
			}
			sql = sql + "a_siteID =? and a_status=1 and SYS_DELETEFLAG=0 order by SYS_DOCUMENTID desc";
			
			String querySql = getLimitSQL(qaLibId, sql, page * ALIST_COUNT, ALIST_COUNT);
			
			conn = Context.getDBSession();
			rs = conn.executeQuery(querySql, params);

			JSONArray datas = new JSONArray();
			while (rs.next()) {
				JSONObject data = assembleQA(siteID, shareUrl, rs, qaLibId,groupId);
				datas.add(data);
			}

			String key = RedisManager.getKeyBySite(RedisKey.APP_QALIST_KEY , siteID);
			RedisManager.setLonger(key + groupId + "." + page, datas.toString());
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}
	//改为由外网写入延迟入库，不需要内网api
	//问政提交
	public boolean qaDelay(JSONObject obj) throws E5Exception {
		int docLibID = LibHelper.getNisQaID();

		// 图片为帖子的附件，存入互动附件表
		String str=getNotNull(obj.getString("imgUrl"));
		String imgurl="";
		if(null!=str&!"".equals(str)){
			imgurl=setImgUrl(str, obj.getLong("id"), docLibID);
			obj.put("SYS_HAVEATTACH",1);
		}else{
			obj.put("SYS_HAVEATTACH",0);
		}
		obj.put("attachments", imgurl);
		return true;
		/*
		// 使用ProcHelper.init()来设置帖子的初始流程等信息
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLibID, DocID);
		ProcHelper.initDoc(doc);
		doc.set("a_siteID", obj.getInt("siteID"));
		doc.set("a_content", getNotNull(obj.getString("content")));
		doc.set("a_longitude", obj.getDouble("longitude"));
		doc.set("a_latitude", obj.getDouble("latitude"));
		doc.set("a_location", getNotNull(obj.getString("location")));
		doc.set("SYS_AUTHORID", obj.getInt("userID"));
		doc.set("SYS_AUTHORS", getNotNull(obj.getString("userName")));
		doc.set("a_sourceType", 2);
		doc.set("a_attachments", imgurl);
		doc.set("SYS_TOPIC", getNotNull(obj.getString("title")));
		doc.set("SYS_CREATED",InfoHelper.formatDate());
		doc.set("a_order",DocID);
		doc.set("a_region",getNotNull(obj.getString("regionName")));
		doc.set("a_regionID",getNotNull(obj.getString("regionId")));
		doc.set("a_realName",getNotNull(obj.getString("realName")));
		doc.set("a_phone",getNotNull(obj.getString("phone")));
		if(obj.has("groupId")){
			doc.set("a_group_ID",getNotNull(obj.getString("groupId")));
		}
		doc.set("a_userIcon",getNotNull(obj.getString("userIcon")));
		docManager.save(doc);*/
	}
	//问政详情
	public boolean qaDetail(int siteID,long docID) throws E5Exception {
		int QaLibId = LibHelper.getLibID(DocTypes.QA.typeID(), Tenant.DEFAULTCODE);
		String iconUrl = UrlHelper.apiUserIcon();
		try{
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(QaLibId, docID);

			JSONObject data = assembleQA(siteID, iconUrl, doc);	
			
			String redisK = RedisKey.APP_QA_KEY + docID;
			RedisManager.set(redisK, data.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean myQA(int siteID, int userID, int page) throws E5Exception {

		String tenantCode = Tenant.DEFAULTCODE;
		int qaLibId = LibHelper.getLibID(DocTypes.QA.typeID(),
				tenantCode);
		String tableName=LibHelper.getLibTable(qaLibId);

		String sql = SQL_QALIST+",a_group_ID from "+ tableName
				+ " where SYS_AUTHORID=? order by SYS_CREATED desc";

		String shareUrl = InfoHelper.getConfig("互动", "问答分享页地址");
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(sql, page * ALIST_COUNT, ALIST_COUNT);
			rs = conn.executeQuery(querySql, new Object[]{userID});

			JSONArray datas = new JSONArray();
			while (rs.next()) {
				JSONObject data = assembleQA(siteID, shareUrl, rs, qaLibId,-1);
				//我的问政 增加 审批流程字段
				data.put("status", StringUtils.getNotNull(rs.getString("SYS_CURRENTSTATUS"))); 
				datas.add(data);
			}
			RedisManager.setLonger(RedisKey.MY_QA_KEY  + userID + "." + page, datas.toString());
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
		String key = RedisManager.getKeyBySite(RedisKey.APP_QALIST_KEY , siteID) + "-1.0";
		String list = RedisManager.get(key);
		if (list == null) {
			boolean ok = qaList(siteID, 0, -1);
			if (ok) list = RedisManager.get(key);
		}
		
		//读列表的前几个做为结果
        JSONArray jsonNewArr = getSomeFromList(list, count);
		return jsonNewArr.toString();
	}
	
	/**
	 * 用于推荐模块的问答列表
	 */
	public JSONObject assembleQA(int siteID, int docLibID, long docID)
			throws E5Exception {
		JSONObject data = new JSONObject();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			data.put("fileId", "" + doc.getDocID());
			data.put("version", doc.getTimestamp("SYS_LASTMODIFIED").getTime());
			data.put("articleType", 101);
			data.put("title", StringUtils.getNotNull(doc.getString("SYS_TOPIC")));
			data.put("publishtime", InfoHelper.formatDate(doc.getCreated()));
			data.put("sourceType", StringUtils.getNotNull(doc.getString("a_sourceType")));
			data.put("content", StringUtils.getNotNull(doc.getString("a_content")));
			
	        String url= UrlHelper.getQAContentUrl(siteID, doc.getDocID());
	        
	        data.put("theContentUrl", url);
	        
			data.put("countDiscuss", doc.getInt("a_countDiscuss"));
			data.put("countPraise", doc.getInt("a_countDiscuss"));
	        
			JSONArray inJsonArr = jsonAttachments(doc.getDocLibID(), doc.getDocID());
			
			data.put("imageUrl", getImgUrlFromAtts(inJsonArr));
			data.put("pics",getImgUrlsFromAtts(inJsonArr));
			
			data.put("groupId", doc.getInt("a_group_ID"));
			data.put("groupName", StringUtils.getNotNull(doc.getString("a_group")));
			
			data.put("isAnswered", StringUtil.isBlank(doc.getString("a_answer")) ? "未回复" : "已回答");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	//问答详情里用的组装方法
	private JSONObject assembleQA(int siteID, String iconUrl, Document doc) throws E5Exception {
		JSONObject data = null;
		if (null != doc) {
			JSONArray inJsonArr = jsonAttachments(doc.getDocLibID(), doc.getDocID());
			JSONArray inJsonArr1 = jsonImages(inJsonArr);
			
			data = new JSONObject();
			data.put("fileId", doc.getDocID());
			data.put("version",doc.getTimestamp("SYS_LASTMODIFIED").getTime());
			data.put("title",doc.getString("SYS_TOPIC"));
			data.put("articleType", 101);
			
	        String url= UrlHelper.getQAContentUrl(siteID, doc.getDocID());
	        data.put("theContentUrl", url);
	        
			data.put("countDiscuss", doc.getInt("a_countDiscuss"));
			data.put("publishtime",InfoHelper.formatDate(doc.getCreated()));
			
			data.put("images",inJsonArr1);
			data.put("pics",getImgUrlsFromAtts(inJsonArr));
			
			data.put("answer", doc.getString("a_answer"));
			
			data.put("sourceType",doc.getInt("a_sourceType"));
			data.put("content",doc.getString("a_content"));
			data.put("location",doc.getString("a_location"));
	
			data.put("userName",doc.getString("SYS_AUTHORS"));
			data.put("userIcon",iconUrl + "?uid=" + doc.getString("SYS_AUTHORID"));
		}
		return data;
	}
	
	private JSONObject assembleQA(int siteID, String shareUrl, IResultSet rs, Integer docLibId,int groupId)
			throws E5Exception {
	
		JSONObject date = new JSONObject();
		try {
			JSONArray inJsonArr = jsonAttachments(docLibId,rs.getInt("SYS_DOCUMENTID"));
			date.put("fileId", rs.getInt("SYS_DOCUMENTID"));
			date.put("version", rs.getTimestamp("SYS_LASTMODIFIED").getTime());
			date.put("articleType", 101);

			date.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
			date.put("publishtime", InfoHelper.formatDate(rs,"publishTime"));
			date.put("sourceType", StringUtils.getNotNull(rs.getString("a_sourceType")));
			date.put("content", StringUtils.getNotNull(rs.getString("a_content")));
			date.put("countDiscuss", rs.getInt("a_countDiscuss"));
			date.put("countPraise", rs.getInt("a_countPraise"));
			
	        String url= UrlHelper.getQAContentUrl(siteID, rs.getLong("SYS_DOCUMENTID"));
	        date.put("theContentUrl", url);
	        
	        //新版不需要这个shareUrl
	        String theShareUrl = shareUrl + "?siteId=" + siteID + "&fileId="+rs.getInt("SYS_DOCUMENTID");
	        date.put("theShareUrl", theShareUrl);
	        
			date.put("imageUrl", getImgUrlFromAtts(inJsonArr));
			date.put("pics",getImgUrlsFromAtts(inJsonArr));
			//用户id
			date.put("userID", rs.getInt("SYS_AUTHORID"));
			//分类
			date.put("groupName", StringUtils.getNotNull(rs.getString("a_group")));
			if (-1 == groupId) {
				date.put("groupId", rs.getInt("a_group_ID"));
			} else {
				date.put("groupId", groupId);
			}
			
			date.put("isAnswered", StringUtil.isBlank(rs.getString("a_answer")) ? "未回复" : "已回答");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	private String getNotNull(String input){
		return ("null".equals(input)?"":input);
	}
	
	private JSONArray jsonImages(JSONArray atts) throws E5Exception {
		JSONArray inJsonArr1 = new JSONArray();
		
		int count=0;
		for (Object doc : atts) {
			JSONObject att = (JSONObject)doc;
			
			JSONObject json = new JSONObject();
			json.put("ref","<!--IMAGEARRAY#>" + count + "-->");
			json.put("picType", StringUtils.getNotNull(att.getString("type")));
			json.put("summary","");
			json.put("imageUrl",StringUtils.getNotNull(att.getString("url")));
			
			inJsonArr1.add(json);
			
			count++;
		}
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("ref","<!--IMAGES#1-->");
		jsonObj.put("imagearray",inJsonArr1);
		
		JSONArray inJsonArr = new JSONArray();
		inJsonArr.add(jsonObj);
		return inJsonArr;
	}
}
