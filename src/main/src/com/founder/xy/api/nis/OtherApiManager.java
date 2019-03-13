package com.founder.xy.api.nis;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.DateUtils;
import com.founder.xy.article.Article;
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
import com.founder.e5.dom.DocLib;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.api.ArticleJsonHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.nis.EventCountHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 与外网api通讯的互动剩余功能Api
 */
@Service
public class OtherApiManager extends BaseApiManager {
	/**
	 * 订阅/关注
	 * type:1栏目订阅；3：话题订阅
	 */
	public boolean subscribe(String jsonStr) throws E5Exception {
		JSONObject obj = JSONObject.fromObject(jsonStr);

		int type = JsonHelper.getInt(obj, "type"); // 1：栏目订阅，3：问吧话题关注
		long topicID = obj.getLong("id");
		String device = obj.getString("device");
		long userID = obj.getLong("userID");

		// 检查，只能订阅一次
		if (hasSubscribed(obj, topicID, type))
			return true;
		// 栏目订阅设备号为空时不能订阅
		if (type == 1 && (device == null || "".equals(device)))
			return false;
		// 问吧订阅按用户id
		if (type > 1 && userID < 1)
			return false;

		// 创建订阅记录
		createSubscribe(obj, type, topicID);

		if (type == 1) {
			incrColSubCount(topicID);
			int siteID = obj.getInt("siteID");
			// 添加订阅缓存
			if(userID < 1){
				setColSubcribeByDevice(device, siteID, topicID);
			}else{
				setColSubcribe(userID, siteID, topicID);
			}
		} else if (type == 3) {
			incrSubject(topicID);
			// 清空相关redis
			clearSubjectSbsc(userID);
		}
		return true;
	}

	/**
	 * 订阅取消
	 */
	public boolean subCancel(String jsonStr) throws E5Exception {
		JSONObject obj = JSONObject.fromObject(jsonStr);

		int type = JsonHelper.getInt(obj, "type"); // 1：栏目订阅，3：问吧话题关注
		long topicID = obj.getLong("id");
		int siteID = obj.getInt("siteID");
		int userID = obj.getInt("userID");
		String device = obj.getString("device");
		int docLibID = LibHelper.getSubscribe();
		if (userID<1) {
			String sql = "DELETE FROM " + LibHelper.getLibTable(docLibID)+ " WHERE sub_type=? AND sub_device=? AND sub_topicID=?" ;
			InfoHelper.executeUpdate(docLibID, sql, new Object[] { type, device, topicID });
			clearColSubcribeRecordByDevice(device, siteID, topicID);
		} else {
			String sql = "DELETE FROM " + LibHelper.getLibTable(docLibID) + " WHERE sub_type=? AND SYS_AUTHORID=? AND sub_topicID=?" ;
			InfoHelper.executeUpdate(docLibID, sql, new Object[] { type, userID, topicID });
			clearColSubcribeRecord(userID, siteID, topicID); // 清除redis订阅记录
		}
		if (type == 1) {
			// 订阅数-1
            if(RedisManager.hget(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID)!=null){
                if(Integer.parseInt(RedisManager.hget(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID))>0)
                    RedisManager.hdecr(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID);
            }
		}
		if (type == 3) {
			// 关注数减1
			decrSubject(topicID) ;
			// 清空相关redis
			clearSubjectSbsc(userID);
		}
		return true;
	}

	/**
	 * 报料
	 */
	public boolean tipoff(String data) throws E5Exception {
		long DocID = InfoHelper.getNextDocID(DocTypes.TIPOFF.typeID());
		int docLibID = LibHelper.getTipoff();

		JSONObject obj = JSONObject.fromObject(data);

		// 图片和视频作为帖子的附件，存入互动附件表
		setImgVioUrl(obj, DocID, docLibID);

		// 使用ProcHelper.init()来设置帖子的初始流程等信息
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		setOtherTipoff(obj, docManager, docLibID, DocID);

		return true;
	}
	/**
	 * 我的报料
	 */
	public boolean myTipoff(long userID, int siteID)
			throws E5Exception {
		//count = LIST_COUNT; // 固定个数

		DocLib docLib = LibHelper.getLib(DocTypes.TIPOFF.typeID(),
				Tenant.DEFAULTCODE);
		String tableName = docLib.getDocLibTable();
		int docLibID = docLib.getDocLibID();

		DBSession conn = null;
		IResultSet rs = null;
		JSONArray redisJsonArr = new JSONArray();
		try {
			//删除已存在缓存
			RedisManager.del(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID);
			conn = Context.getDBSession(docLib.getDsID());
			String sql = conn.getDialect().getLimitString(
					getTipoffSql(tableName), 0, 100);
			rs = conn.executeQuery(sql, new Object[] { userID ,siteID});
			while (rs.next()) {
				long sysID = rs.getLong("SYS_DOCUMENTID");

				JSONObject inJson = new JSONObject();
				inJson.put("docID", sysID);
				inJson.put("title",
						StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
				inJson.put("time", InfoHelper.formatDate(rs, "SYS_CREATED"));
				inJson.put("content",
						StringUtils.getNotNull(rs.getString("a_content")));
				inJson.put("isAnswer",
						StringUtils.getNotNull(rs.getString("a_isAnswer")));
				inJson.put("status",
						StringUtils.getNotNull(rs.getString("a_status")));
				inJson.put("siteID",
						StringUtils.getNotNull(rs.getString("a_siteID")));
				//JSONArray inJsonArr = jsonAttachments(docLibID, sysID);
				//inJson.put("attachments", inJsonArr);

				RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID, inJson.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		if(RedisManager.llen(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID)==0){
			RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID, new JSONObject().toString());
		}
		RedisManager.setTime(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID, RedisManager.week1);
		// System.out.println(redisJsonArr);
		return true;
	}
	/**
	 * 报料详情
	 */
	public boolean tipoffContent(long docID, int siteID)
			throws E5Exception {
		DocLib docLib = LibHelper.getLib(DocTypes.TIPOFF.typeID(),
				Tenant.DEFAULTCODE);
		String tableName = docLib.getDocLibTable();
		int docLibID = docLib.getDocLibID();

		DBSession conn = null;
		IResultSet rs = null;
		JSONObject jsonObject = new JSONObject();
		try {
			conn = Context.getDBSession(docLib.getDsID());
			String sql = getTipoffContentSql(tableName);
			rs = conn.executeQuery(sql, new Object[] { docID ,siteID});
			if (rs.next()) {
				long sysID = rs.getLong("SYS_DOCUMENTID");

				jsonObject.put("docID", sysID);
				jsonObject.put("title",
						StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
				jsonObject.put("time", InfoHelper.formatDate(rs, "SYS_CREATED"));
				jsonObject.put("content",
						StringUtils.getNotNull(rs.getString("a_content")));
				jsonObject.put("isAnswer",
						StringUtils.getNotNull(rs.getString("a_isAnswer")));
				jsonObject.put("status",
						StringUtils.getNotNull(rs.getString("a_status")));
				jsonObject.put("siteID",
						StringUtils.getNotNull(rs.getString("a_siteID")));
				jsonObject.put("source",
						StringUtils.getNotNull(rs.getString("a_sourceType")));
				jsonObject.put("answer",
						StringUtils.getNotNull(rs.getString("a_answers")));
				JSONArray inJsonArr = jsonAttachments(docLibID, sysID);
				jsonObject.put("attachments", inJsonArr);
				JSONArray inJsonArr1 = jsonImages(inJsonArr);
				jsonObject.put("images",inJsonArr1);


			}
			RedisManager.set(RedisKey.APP_TIPOFF_KEY+ docID,jsonObject.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		/*if(RedisManager.llen(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID)==0){
			RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID, new JSONObject().toString());
		}
		RedisManager.setTime(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID)+userID, RedisManager.week1);*/
		// System.out.println(redisJsonArr);
		return true;
	}

	private JSONArray jsonImages(JSONArray atts) throws E5Exception {
		JSONArray inJsonArr1 = new JSONArray();

		int count=0;
		for (Object doc : atts) {
			JSONObject att = (JSONObject)doc;
			int type=Integer.parseInt(StringUtils.getNotNull(att.getString("type")));
			if(type==1){
				JSONObject json = new JSONObject();
				json.put("ref","<!--IMAGEARRAY#>" + count + "-->");
				json.put("picType", StringUtils.getNotNull(att.getString("type")));
				json.put("summary","");
				json.put("imageUrl",StringUtils.getNotNull(att.getString("url")));

				inJsonArr1.add(json);

				count++;
			}

		}
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("ref","<!--IMAGES#1-->");
		jsonObj.put("imagearray",inJsonArr1);

		JSONArray inJsonArr = new JSONArray();
		inJsonArr.add(jsonObj);
		return inJsonArr;
	}


	/**
	 * 获取已投票人数
	 */
	public boolean voteCount(int id) throws E5Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(LibHelper.getVoteResult(),
				"vote_voteID=?", new Object[] { id });

		int count = 0;
		if (null != docs) {
			count = docs.length;
		}
		JSONObject redisJson = new JSONObject();
		redisJson.put("count", count);
		// System.out.println(redisJson);
		RedisManager.setOneMinute(RedisKey.APP_VOTE_COUNT_KEY + id,
				redisJson.toString());
		return true;
	}

	/**
	 * 查看投票结果
	 */
	public boolean voteResult(int id) throws E5Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] quesDocs = docManager.find(LibHelper.getVote(),
				"vote_rootid=?", new Object[] { id });

		int votOptLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.VOTEOPTION.typeID(), LibHelper.getVote());
		DecimalFormat df = new DecimalFormat("##.##%");

		JSONArray redisJsonArr = new JSONArray();
		if (null != quesDocs) {
			for (Document quesDoc : quesDocs) {
				JSONObject quesJson = new JSONObject();
				quesJson.put("qid", quesDoc.getDocID());
				quesJson.put("qname", StringUtils.getNotNull(quesDoc.getString("vote_topic")));
				quesJson.put("qtype", quesDoc.getInt("vote_type"));

				// 投票选项
				Document[] options = docManager.find(votOptLibID, "vote_voteID=?",
						new Object[] { quesDoc.getDocID() });

				double totalCount = 0.00;
				for (Document option : options) {
					totalCount += option.getLong("vote_countInitial")
							+ option.getLong("vote_count");
				}

				JSONArray optionsJson = new JSONArray();
				for (Document option : options) {
					JSONObject json = new JSONObject();
					json.put("oid", option.getDocID());
					json.put("oname", StringUtils.getNotNull(option.getString("vote_option")));
					long count = option.getLong("vote_countInitial")
							+ option.getLong("vote_count");
					json.put("count", count);
					json.put("percentage", df.format(count / totalCount));

					optionsJson.add(json);
				}
				quesJson.put("options", optionsJson);

				redisJsonArr.add(quesJson);
			}
		}
		// System.out.println(redisJsonArr);
		RedisManager.setOneMinute(RedisKey.APP_VOTE_RESULT_KEY + id,
				redisJsonArr.toString());
		return true;
	}

	/**
	 * 提交投票
	 */
	public String vote(String jsonStr) throws E5Exception {

		JSONObject obj = JSONObject.fromObject(jsonStr);
		// int siteID = (Integer)obj.get("siteID"); // 站点ID
		long voteID = obj.getLong("voteID"); // 投票ID
//		long questionID = obj.getLong("questionID");//问题ID
//		long optionID = obj.getLong("optionID"); // 选项ID
		// double longitude = (Double)obj.get("longitude");
		// double latitude = (Double)obj.get("latitude");
		// String location = (String)obj.get("location");
		String device = obj.getString("userOtherID");

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 验证是否可以投票
		/*int voteLibID = LibHelper.getLibID(DocTypes.VOTE.typeID(), Tenant.DEFAULTCODE);
		Document vote = docManager.get(voteLibID, voteID);
		//0只允许一次 1每天一次 2每天N次 3不限 (按设备)
		if(vote.getInt("vote_cycle") == 0){
			int voteResultLibID = LibHelper.getLibID(DocTypes.VOTERESULT.typeID(), Tenant.DEFAULTCODE);
			Document[] voteResults = docManager.find(voteResultLibID,
					"vote_device=? and vote_voteID=?", new Object[] { device,voteID });
			if(null != voteResults && voteResults.length > 0){
				JSONObject json = new JSONObject();
				json.put("result", "false");
				json.put("failureInfo", "您已经投过票了");
				return json.toString();
			}
		}else if(vote.getInt("vote_cycle") == 1){
			Date day = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(day);

			int voteResultLibID = LibHelper.getLibID(DocTypes.VOTERESULT.typeID(), Tenant.DEFAULTCODE);
			Document[] voteResults = docManager.find(voteResultLibID,
					"vote_device=? and vote_voteID=? and (vote_time between ? and ?)",
					new Object[] { device,voteID,date+" 00:00:00",date+" 23:59:59" });
			if(null != voteResults && voteResults.length > 0){
				JSONObject json = new JSONObject();
				json.put("result", "false");
				json.put("failureInfo", "您今天已经投过票了");
				return json.toString();
			}
		}else if(vote.getInt("vote_cycle") == 2){
			int cyclenum = vote.getInt("vote_cyclenum");

			Date day = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(day);

			int voteResultLibID = LibHelper.getLibID(DocTypes.VOTERESULT.typeID(), Tenant.DEFAULTCODE);
			Document[] voteResults = docManager.find(voteResultLibID,
					"vote_device=? and vote_voteID=? and (vote_time between ? and ?) group by vote_time",
					new Object[] { device,voteID,date+" 00:00:00",date+" 23:59:59" },
					new String[] {"vote_time"});
			if(null != voteResults && voteResults.length >= cyclenum){
				JSONObject json = new JSONObject();
				json.put("result", "false");
				json.put("failureInfo", "您今天投票次数已达上限");
				return json.toString();
			}
		}*/

		// 可以投票
		// 记录到投票结果表
		List<String> voteResult = (List<String>)obj.get("voteResult"); // 投票结果
		for(String result : voteResult){
			String[] qidAndOid = result.split(":");

			Document doc = docManager.newDocument(LibHelper.getVoteResult(),
					InfoHelper.getNextDocID(DocTypes.VOTERESULT.typeID()));
			ProcHelper.initDoc(doc);

			doc.set("SYS_AUTHORID", obj.getLong("userID")); // 用户ID（会员ID）
			doc.set("vote_voteID", voteID);
			doc.set("vote_questionID", Long.parseLong(qidAndOid[0]));
			doc.set("vote_optionID", Long.parseLong(qidAndOid[1]));
			doc.set("vote_device", device);
			doc.set("vote_time", DateUtils.getTimestamp());

			String userName = StringUtils.getNotNull(obj.getString("userName")); // 用户名
			if (null == userName || "".equals(userName)) {
				doc.set("SYS_AUTHORS",
						StringUtils.getNotNull(obj.getString("userOtherID")));
			} else {
				doc.set("SYS_AUTHORS", userName);
			}
			docManager.save(doc);
			System.out.println("--------------保存投票结果-------------------------");
			// 投票结果+1
			try {
				DocLib doclib = LibHelper.getLib(DocTypes.VOTEOPTION.typeID(),
						Tenant.DEFAULTCODE);
				String sql = getVoteResultSql(doclib.getDocLibTable());

				InfoHelper.executeUpdate(doclib.getDocLibID(), sql,
						new Object[] { Long.parseLong(qidAndOid[1]) });
			} catch (Exception e) {
				e.printStackTrace();
				throw new E5Exception(e);
			} finally {
			}
		}

		JSONObject json = new JSONObject();
		json.put("result", "true");
		json.put("failureInfo", "");
		return json.toString();
	}

	/**
	 * 意见反馈
	 */
	public boolean feed(String jsonStr) throws E5Exception {

		JSONObject obj = JSONObject.fromObject(jsonStr);
		int siteID = (obj.containsKey("siteID")) ? (Integer) obj.get("siteID")
				: 1; // 站点ID

		// double longitude = (Double)obj.get("longitude");
		// double latitude = (Double)obj.get("latitude");
		// String location = (String)obj.get("location");

		// 意见反馈表
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(LibHelper.getFeedback(),
				InfoHelper.getNextDocID(DocTypes.FEEDBACK.typeID()));
		ProcHelper.initDoc(doc);

		String userName = StringUtils.getNotNull(obj.getString("userName")); // 用户名
		if (null == userName || "".equals(userName)) {
			doc.set("SYS_AUTHORS",
					StringUtils.getNotNull(obj.getString("userOtherID")));
		} else {
			doc.set("SYS_AUTHORS", userName);
		}
		doc.set("SYS_AUTHORID", obj.getLong("userID")); // 用户ID（会员ID）
		doc.set("feed_content",
				StringUtils.getNotNull(obj.getString("content")));
		doc.set("feed_siteID", siteID);

		docManager.save(doc);
		// System.out.println(obj);
		return true;
	}

	/**
	 * 接收登录信息
	 */
	public boolean loginfo(String data) throws E5Exception {
		/*
		JSONObject obj = JSONObject.fromObject(data);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String device = obj.getString("device");
		Document[] docs = docManager.find(LibHelper.getApplogin(),
				"log_deviceID=?", new Object[] { device });
		if (null == docs || docs.length == 0) {
			Document doc = docManager.newDocument(LibHelper.getApplogin(),
					InfoHelper.getNextDocID(DocTypes.APPLOGIN.typeID()));
			ProcHelper.initDoc(doc);
			doc.set("log_appID", obj.getInt("appID"));
			doc.set("log_siteID", obj.getInt("siteID"));
			doc.set("log_deviceID", obj.getString("device"));
			doc.set("log_token", obj.getString("token"));
			doc.set("log_deviceType", obj.getInt("deviceType"));
			doc.set("log_license", obj.getString("license"));
			docManager.save(doc);
		} else {
			DocLib lib = LibHelper.getLib(DocTypes.APPLOGIN.typeID(),
					Tenant.DEFAULTCODE);

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE ");
			sql.append(lib.getDocLibTable());
			sql.append(" SET SYS_LASTMODIFIED=?,log_token=?");
			sql.append(" WHERE log_deviceID=?");

			InfoHelper.executeUpdate(
					lib.getDocLibID(),
					sql.toString(),
					new Object[] {
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(Calendar.getInstance().getTime()),
							obj.getString("token"), device });
		}
		// System.out.println(obj);
		 */
		return true;
	}

	public boolean myFav(int siteID, int userID){
		IResultSet rs  = null;
		DBSession conn = null;
		try {
			//删除已存在缓存
			RedisManager.del(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,siteID)+userID);
			DocLib discussLib = LibHelper.getLib(DocTypes.FAVORITE.typeID(), Tenant.DEFAULTCODE);
			String tableName = discussLib.getDocLibTable();
			String sql = "select fav_articleID,fav_type,SYS_TOPIC,fav_column,fav_articleType,fav_time,fav_channel,fav_url,fav_urlPad,fav_siteID,fav_imgUrl"
				+ ",SYS_TOPICWeb,fav_urlWeb,fav_urlPadWeb,fav_columnWeb"
				+ " from " + tableName
				+ " where fav_userID=? and fav_siteID=? ORDER BY SYS_DOCUMENTID DESC";
			JSONObject jsonObject = new JSONObject();
			conn = Context.getDBSession(discussLib.getDsID());
			sql = conn.getDialect().getLimitString(sql, 0, 100);
			rs = conn.executeQuery(sql, new Object[]{userID,siteID});

			while (rs.next()) {
				jsonObject.put("articleID", rs.getLong("fav_articleID"));
				jsonObject.put("type", rs.getInt("fav_type"));
				jsonObject.put("siteID", rs.getInt("fav_siteID"));
				jsonObject.put("channel", rs.getInt("fav_channel"));
				if(rs.getInt("fav_type")==0||rs.getInt("fav_type")==3){
					jsonObject.put("url",rs.getString("fav_url"));
					jsonObject.put("urlPad", rs.getString("fav_urlPad"));
				}
				jsonObject.put("title", rs.getString("SYS_TOPIC"));
				jsonObject.put("column", rs.getString("fav_column"));
				jsonObject.put("articleType", rs.getInt("fav_articleType"));
				jsonObject.put("time",InfoHelper.formatDate(rs,"fav_time"));
				jsonObject.put("imgUrl",rs.getString("fav_imgUrl"));

                jsonObject.put("titleWeb",rs.getString("SYS_TOPICWeb"));
                jsonObject.put("urlWeb",rs.getString("fav_urlWeb"));
                jsonObject.put("urlPadWeb",rs.getString("fav_urlPadWeb"));
                jsonObject.put("columnWeb",rs.getString("fav_columnWeb"));

				setContent(jsonObject);

				setOther(jsonObject);
				RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,siteID)+userID, jsonObject.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		if(RedisManager.llen(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,siteID)+userID)==0){

			RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,siteID)+userID, new JSONObject().toString());
		}
		RedisManager.setTime(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,siteID)+userID, RedisManager.week1);

		return true;
	}

	private void setOther(JSONObject jsonOne) throws E5Exception {
		if(jsonOne.getInt("type") == 0||jsonOne.getInt("type") == 3){
			int docLibID;
			if(jsonOne.getInt("type")==0){//稿件
				docLibID = LibHelper.getArticleAppLibID();
			}else{
				docLibID = LibHelper.getLib(DocTypes.PAPERARTICLE.typeID(), Tenant.DEFAULTCODE).getDocLibID();
			}

			long docID=jsonOne.getLong("articleID");
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			jsonOne.put("source",StringUtils.getNotNull(doc.getString("a_source")));

			int articleType = doc.getInt("a_type");
			if(articleType == Article.TYPE_LIVE){
				jsonOne.put("linkID",StringUtils.getNotNull(doc.getString("a_linkID")));
			}else{
				jsonOne.put("linkID","");
			}

			if(articleType == Article.TYPE_PIC){
				jsonOne.put("picContent",StringUtils.getNotNull(doc.getString("a_content")));
			}else{
				jsonOne.put("picContent","");
			}
		}else{
			jsonOne.put("source","");
			jsonOne.put("linkID","");
			jsonOne.put("picContent","");
		}

	}

	/**
	 * 初始化一个对象在Redis里的互动计数，用于读互动计数的api
	 */
	public boolean getCounts(long id, int source) {
		String field = "c";
		switch (source) {
		case 0:
			//稿件：保证Redis里有稿件的计数、详情json对象
			EventCountHelper.getArticleCount(id, field);

			String key = RedisKey.APP_ARTICLE_KEY + id;
	        if (!RedisManager.exists(key)) {
	            try {
					ArticleJsonHelper.article(LibHelper.getArticleAppLibID(), id);
				} catch (E5Exception e) {
					e.printStackTrace();
				}
	        }
           break;
		case 1:
			//直播：保证Redis里有计数
			EventCountHelper.getLiveCount(id, field);
			break;
		case 3:
			EventCountHelper.getPaperArticleCount(id, field);
			break;
		case 4:
			EventCountHelper.getSubjectQACount(id, field);
			break;
		case 5:
			EventCountHelper.getQACount(id, field);
			break;
		case 6:
			EventCountHelper.getActivityCount(id, field);
			break;
		}
		return true;
	}

	private void setContent(JSONObject jsonOne) {
		if(jsonOne.containsKey("type")){
			int type = jsonOne.getInt("type");
			int siteID = jsonOne.getInt("siteID");

			if (type==0){
				jsonOne.put("contentUrl", UrlHelper.getArticleContentUrl(jsonOne.getLong("articleID")));
			}else if(type==3){
				jsonOne.put("contentUrl", UrlHelper.getPaperArticleContentUrl(siteID, jsonOne.getLong("articleID")));
			}else if(type==5){
				jsonOne.put("contentUrl",UrlHelper.getQAContentUrl(siteID, jsonOne.getLong("articleID")));
			}else if(type==6){
				jsonOne.put("contentUrl",UrlHelper.getActivityContentUrl(siteID, jsonOne.getLong("articleID")));
			}
		}
	}

	private boolean hasSubscribed(JSONObject obj, long topicID, int type)
			throws E5Exception {
		long userID = obj.getLong("userID");
		String device = StringUtils.getNotNull(obj.getString("device")); // 用户设备标识

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = null;
		if (null!=device&!"".equals(device)) { // 未登录用户同一设备，同一选题只能订阅一次
			docs = docManager
					.find(LibHelper.getSubscribe(),
							"sub_topicID=? AND sub_device=? AND SYS_AUTHORID=-1 and sub_type=?",
							new Object[] { topicID, device, type });
		} else { // 登录用户同一选题只能订阅一次
			docs = docManager.find(LibHelper.getSubscribe(),
					"sub_topicID=? AND SYS_AUTHORID=? and sub_type=?",
					new Object[] { topicID, userID, type });
		}
		if (null != docs && docs.length > 0) { // 不能重复订阅
			return true;
		}
		return false;
	}

	private void createSubscribe(JSONObject obj, int type, long topicID)
			throws E5Exception {
		// 创建订阅记录
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(LibHelper.getSubscribe(),
				InfoHelper.getNextDocID(DocTypes.SUBSCRIBE.typeID()));
		ProcHelper.initDoc(doc);

		long userID = obj.getLong("userID");
		String device = StringUtils.getNotNull(obj.getString("device")); // 用户设备标识
		doc.set("SYS_AUTHORID", userID); // 用户ID（会员ID）
		doc.set("SYS_AUTHORS",
				StringUtils.getNotNull(obj.getString("userName")));
		doc.set("sub_topicID", topicID);
		doc.set("sub_device", device);
		doc.set("sub_type", type);

		docManager.save(doc);
	}

	// 栏目订阅数+1
	private void incrColSubCount(long topicID) throws E5Exception {
		try {
			if (RedisManager.hget(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID) != null) {
                RedisManager.hincr(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID);
			} else {
				int docLibID = LibHelper.getLibID(DocTypes.COLUMN.typeID(), Tenant.DEFAULTCODE);
				long count = getColSubscribeCount(docLibID, topicID) + 1;
				RedisManager.hset(RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, topicID, String.valueOf(count));
			}

			//--------增加按小时/天计数----------
			Calendar ca = Calendar.getInstance();
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int day = ca.get(Calendar.DATE);
			RedisManager.hincr(RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_HOUR + hour, topicID, RedisManager.hour2);
			RedisManager.hincr(RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_DAY + day, topicID, RedisManager.hour25);
		} catch (Exception e) {
			throw new E5Exception(e);
		}
	}

	// 问吧话题关注数+1
	private void incrSubject(long topicID) throws E5Exception {
		try {
			if (RedisManager.hexists(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, String.valueOf(topicID))) {
				RedisManager.hincr(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, topicID);
			} else {
				int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(), Tenant.DEFAULTCODE);
				long count = getSubjectSubcribeCount(docLibID, topicID) + 1;
				RedisManager.hset(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, topicID, String.valueOf(count));
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	// 问吧话题关注数-1
	private void decrSubject(long topicID) throws E5Exception {
		try {
			if (RedisManager.hexists(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, String.valueOf(topicID))
					&& Long.parseLong(RedisManager.hget(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, String.valueOf(topicID))) > 0) {
				RedisManager.hdecr(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, topicID);
			} else {
				int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(), Tenant.DEFAULTCODE);
				long count = getSubjectSubcribeCount(docLibID, topicID) ;
				if(count > 0){
					RedisManager.hset(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, topicID, String.valueOf(count-1));
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private long getColSubscribeCount(int docLibID, long id) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			doc = docManager.get(docLibID, id);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		if (doc == null)
			return 0;

		long count = doc.getLong("col_rssCount");// redis里改为保存实际订阅数
		if (count < 0)
			count = 0;
		return count;
	}

	private long getSubjectSubcribeCount(int docLibID, long topicID)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		doc = docManager.get(docLibID, topicID);
		if (doc == null)
			return 0;
		long count = doc.getLong("a_countFollow");
		return count < 0 ? 0 : count;
	}

	/**
	 * 取得sql
	 */
	private String getTipoffSql(String tableName) {
		return new StringBuilder()
				.append("SELECT SYS_DOCUMENTID, SYS_TOPIC, SYS_CREATED, a_content, a_isAnswer, a_status, a_siteID")
				.append(" FROM " + tableName)
				.append(" WHERE SYS_AUTHORID=? AND a_siteID=? ORDER BY SYS_DOCUMENTID DESC")
				.toString();
	}
	/**
	 * 取得爆料详情sql
	 */
	private String getTipoffContentSql(String tableName) {
		return new StringBuilder()
				.append("SELECT SYS_DOCUMENTID, SYS_TOPIC, SYS_CREATED, a_content, a_isAnswer, a_status, a_siteID ,a_sourceType, a_answers")
				.append(" FROM " + tableName)
				.append(" WHERE SYS_DOCUMENTID=? AND a_siteID=? ORDER BY SYS_DOCUMENTID DESC")
				.toString();
	}

	/**
	 * Sql
	 */
	private String getVoteResultSql(String tableName) {
		return "UPDATE " + tableName
				+ " SET vote_count=vote_count+1 WHERE SYS_DOCUMENTID=?";
	}

	/**
	 * 使用ProcHelper.init()来设置帖子的初始流程等信息
	 */
	private void setOtherTipoff(JSONObject obj, DocumentManager docManager,
			int docLibID, long docID) throws E5Exception {

		Document doc = setCommonField(obj, docManager, docID, docLibID,
				"tipoff");
		doc.set("a_tag", StringUtils.getNotNull(obj.getString("tag")) + " "
				+ StringUtils.getNotNull(obj.getString("type")));
		docManager.save(doc);
	}

	private void setColSubcribe(long userID, int siteID, long colID)throws E5Exception {
		String key = RedisKey.MY_COLUMN_KEY;
		String ids = RedisManager.hget(key, userID);
		if (ids == null) {
			// 查数据库
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),Tenant.DEFAULTCODE);
			String sql = "SYS_AUTHORID=? and SYS_DELETEFLAG=0 and sub_type=1" ;
			Document[] docs = docManager.find(docLibID, sql,new Object[] { userID });
			String value = "";
			if (docs.length > 0) {
				for (int i = 0; i < docs.length; i++) {
					if (i == 0) {
						value = "" + docs[i].getLong("sub_topicID");
					} else {
						value = value + "," + docs[i].getLong("sub_topicID");
					}
				}
			}
			RedisManager.hset(key, userID, value);
		} else {
            if("".equals(ids)){
                RedisManager.hset(key, userID, ids + colID);
            }else{
                RedisManager.hset(key, userID, ids + "," + colID);
            }
		}
	}

	private void setColSubcribeByDevice(String device, int siteID, long colID)throws E5Exception {
		String key = RedisKey.MY_COLUMN_KEY;
		String ids = RedisManager.hget(key, device);
		if (ids == null) {
			// 查数据库
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int docLibID = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),Tenant.DEFAULTCODE);
			String sql ="sub_device=? and SYS_DELETEFLAG=0 and sub_type=1" ;
			Document[] docs = docManager.find(docLibID, sql, new Object[]{device});
			String value = "";
			if (docs.length > 0) {
				for (int i = 0; i < docs.length; i++) {
					if (i == 0) {
						value = "" + docs[i].getLong("sub_topicID");
					} else {
						value = value + "," + docs[i].getLong("sub_topicID");
					}
				}
			}
			RedisManager.hset(key, device, value);
		} else {
            if("".equals(ids)){
                RedisManager.hset(key, device, ids + colID);
            }else{
                RedisManager.hset(key, device, ids + "," + colID);
            }
		}
	}

	private void clearColSubcribeRecord(long userID, int siteID, long topicID) {
		String key = RedisKey.MY_COLUMN_KEY;
		String records = RedisManager.hget(key, userID);
		if (records != null && !"".equals(records)) {
			String[] ids = records.split(",");
			List<String> idList = new ArrayList<String>();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (!String.valueOf(topicID).equals(id)) {
					idList.add(id);
				}
			}

			if (idList.size() == 0){
				RedisManager.hclear(key, userID);
			}else{
				String value = null;
				for (int i = 0; i < idList.size(); i++) {
					if (i == 0)
						value = idList.get(i);
					else
						value = value + "," + idList.get(i);
				}
				RedisManager.hset(key, userID, value);
			}
		}
	}

	private void clearColSubcribeRecordByDevice(String device, int siteID, long topicID) {
		String key = RedisKey.MY_COLUMN_KEY;
		String records = RedisManager.hget(key, device);
		if (records != null && !"".equals(records)) {
			String[] ids = records.split(",");
			List<String> idList = new ArrayList<String>();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (!String.valueOf(topicID).equals(id)) {
					idList.add(id);
				}
			}

			if (idList.size() == 0){
				RedisManager.hclear(key, device);
			}else{
				String value = null;
				for (int i = 0; i < idList.size(); i++) {
					if (i == 0)
						value = idList.get(i);
					else
						value = value + "," + idList.get(i);
				}
				RedisManager.hset(key, device, value);
			}
		}
	}

	// 清空问吧话题关注redis
	private void clearSubjectSbsc(long userId) {
		// 清空获取话题redis
		RedisManager.clear(RedisKey.MY_SUBJECT_SUBIDS_KEY + userId);
		RedisManager.clearKeyPages(RedisKey.MY_SUBJECT_SUBS_KEY + userId);
	}

	/**
	 * 获取我的意见反馈回答
	 * @param siteID
	 * @param userID
	 * @return
	 */
	/*public boolean myFeedAnswer(int siteID, int userID) {
		IResultSet rs  = null;
		DBSession conn = null;
		try {
			//删除已存在缓存
			RedisManager.del(RedisManager.getKeyBySite(RedisKey.APP_FEED_ANSWER,siteID)+userID);
			DocLib docLib = LibHelper.getLib(DocTypes.FEEDBACK.typeID(), Tenant.DEFAULTCODE);
			String tableName = docLib.getDocLibTable();
			String sql = "SELECT SYS_DOCUMENTID ,SYS_DOCLIBID, SYS_AUTHORID,SYS_AUTHORS,feed_siteID,feed_content,feed_answer,SYS_CREATED,a_attachments "
					+ " from " + tableName
					+ " where SYS_DELETEFLAG=0 and SYS_AUTHORID=? and feed_siteID=? and feed_answer != ''";
			JSONObject jsonObject = new JSONObject();
			conn = Context.getDBSession(docLib.getDsID());
			sql = conn.getDialect().getLimitString(sql, 0, 100);
			rs = conn.executeQuery(sql, new Object[]{userID,siteID});

			while (rs.next()) {
				jsonObject.put("userID", rs.getInt("SYS_AUTHORID"));
				jsonObject.put("userName", rs.getString("SYS_AUTHORS"));
				jsonObject.put("siteID", rs.getInt("feed_siteID"));
				jsonObject.put("content", rs.getString("feed_content"));
				jsonObject.put("answer", rs.getString("feed_answer"));
				jsonObject.put("time", InfoHelper.formatDate(rs,"SYS_CREATED"));
				jsonObject.put("a_attachments", jsonAttachments(docLib.getDocLibID() , rs.getLong("SYS_DOCUMENTID")));

				RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.APP_FEED_ANSWER,siteID)+userID, jsonObject.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		if(RedisManager.llen(RedisManager.getKeyBySite(RedisKey.APP_FEED_ANSWER,siteID)+userID)!=0){
			 RedisManager.setTime(RedisManager.getKeyBySite(RedisKey.APP_FEED_ANSWER,siteID)+userID, RedisManager.week1);
			//RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.APP_FEED_ANSWER,siteID)+userID, new JSONObject().toString());
		}

		return true;
	}*/
}
