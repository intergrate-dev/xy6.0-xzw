package com.founder.xy.api.nis;

import java.sql.SQLException;
import java.util.List;

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
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 互动话题
 */
@Service
public class SubjectApiManager extends BaseApiManager {
	private static final int LIST_COUNT = 20; // 固定列表个数，避免恶意调用冲了redis中的缓存数据
	private static final String SQL_SUBJECT=
			" select SYS_DOCUMENTID,SYS_TOPIC,SYS_LASTMODIFIED,SYS_CREATED,a_discussClosed,"
			+ " a_sourceType,a_content,a_questionClosed,a_countQuestion,a_attachments,"
			+ " a_countFollow,a_group,a_answerer,a_answererID,a_answererIcon,"
			+ " '' as isSubsc from ";
	private static final String SQL_SUBJECTQA =
			" select SYS_DOCUMENTID,SYS_CREATED,SYS_AUTHORID,SYS_AUTHORS,SYS_TOPIC,"
			+ "a_rootID,a_content,a_answer,a_answerTime,a_countDiscuss,a_countPraise from ";
	/**
	 * 获得话题列表
     * 0-获得所有话题列表，1-获得分类话题列表，2-获得我的问吧（发起）话题类表
	 */
	public boolean getSubjectList(int siteID, int catID,int page, Long userid,int type) {
		int start = page * LIST_COUNT;

		String iconUrl = UrlHelper.apiUserIcon();
		int subjectLibId = LibHelper.getLibID(DocTypes.SUBJECT.typeID(), Tenant.DEFAULTCODE);

		String key = null;
        Object[] params = null;

		JSONArray datas = new JSONArray();
		
		DBSession conn = null;
		IResultSet rs = null;
		try {
			String sql = SQL_SUBJECT + LibHelper.getLibTable(subjectLibId);
	        
			if (1 == type) {
				sql = sql + " where a_group_ID=? and a_siteID=? and a_status=1 ";
				params = new Object[] {catID, siteID};
				key = RedisKey.APP_SUBJECT_CAT_KEY + catID + "." + page;
			} else if (2 == type) {
				sql = sql + " where a_answererID=? and a_siteID=? and a_status=1 ";
				params = new Object[] {userid, siteID};
				key = RedisKey.MY_SUBJECT_KEY + userid + "." + page;
	        } else {
	        	sql = SQL_SUBJECT + LibHelper.getLibTable(subjectLibId) + " where a_siteID =?  and a_status=1 ";
	        	params = new Object[] { siteID };
				key = RedisManager.getKeyBySite(RedisKey.APP_SUBJECT_LIST_KEY, siteID) + page;
	        }
	        sql=sql+" order by a_order desc ";
	        
			conn = Context.getDBSession();
			sql = conn.getDialect().getLimitString(sql, start, LIST_COUNT);
			
			rs = conn.executeQuery(sql, params);
			while (rs.next()) {
				JSONObject data = assembleSubject(iconUrl, rs, subjectLibId);
				datas.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		JSONObject result = new JSONObject();
		result.put("list", datas);
		
        RedisManager.setLonger(key, result.toString());
		return true;
	}

	/**
	 * 获得单个话题
	 */
	public boolean getSubject(int siteID, long id) {
		
		String key = RedisKey.APP_SUBJECT_KEY + id;

		String iconUrl = UrlHelper.apiUserIcon();
		
		int subjectLibId = LibHelper.getLibID(DocTypes.SUBJECT.typeID(), Tenant.DEFAULTCODE);
		try {
			JSONObject date = assembleSubject(iconUrl, subjectLibId, id);
			
			//获得话题的回答数
			date.put("countAnswer", getCountAnswer(id));
			
			RedisManager.setLonger(key, date.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean getMySubscribes(int siteID, Long userid, int page)
			throws E5Exception {
		String tenantCode = Tenant.DEFAULTCODE;
		DocLib subscribeLib = LibHelper.getLib(DocTypes.SUBSCRIBE.typeID(),tenantCode);

		//读出订阅的问吧话题ID，处理翻页
		String sql = "select sub_topicID from " + subscribeLib.getDocLibTable()
				+ " where SYS_AUTHORID=? and sub_type=3 order by SYS_CREATED desc";
		List<Long> ids = queryOneField(subscribeLib, sql, new Object[]{userid}, 
				page * LIST_COUNT, LIST_COUNT);

		JSONArray datas = new JSONArray();
		if (ids.size() > 0) {
			DocLib subjectLib = LibHelper.getLib(DocTypes.SUBJECT.typeID(),tenantCode);
			
			String iconUrl = UrlHelper.apiUserIcon();

			Object[] params = new Object[ids.size()];
			sql = SQL_SUBJECT + subjectLib.getDocLibTable() + " WHERE SYS_DOCUMENTID in (";
			for (int i = 0; i < ids.size(); i++) {
				params[i] = ids.get(i);
				sql += (i > 0) ? ",?" : "?";
			}
			sql += ")" ;
			
			DBSession conn = null;
			IResultSet rs = null;
			try {
				conn = Context.getDBSession();
				rs = conn.executeQuery(sql, params);
				while (rs.next()) {
					JSONObject data = assembleSubject(iconUrl, rs, subjectLib.getDocLibID());
					datas.add(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}
		}
		JSONObject result = new JSONObject();
		result.put("list", datas);
		
		RedisManager.setOneMinute(RedisKey.MY_SUBJECT_SUBS_KEY +userid + "." + page, result.toString());
		return true;
	}

	public boolean getMySubscribeIDs(Long userid)
			throws E5Exception {

		String tenantCode = Tenant.DEFAULTCODE;
		int subscribeLibId = LibHelper.getLibID(DocTypes.SUBSCRIBE.typeID(),
				tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document [] docs = docManager.find(subscribeLibId,
				"SYS_AUTHORID=? and sub_type=3 order by SYS_CREATED desc",
				new Object[] { userid});

		String ids="";
		for(Document doc:docs){
			if(ids.equals("")){
				ids=ids+doc.getString("sub_topicID");
			}else{
				ids=ids+","+doc.getString("sub_topicID");
			}
		}

		RedisManager.set(RedisKey.MY_SUBJECT_SUBIDS_KEY+userid,ids);

		return true;
	}

	/**
	 * 互动话题的问答列表、热门问答列表
	 */
	public boolean getQuestionList(int siteID, int subjectID, int page, String orderS) throws E5Exception {
		//读出话题对象，以便后面添加题主头像等信息
		JSONObject subject = getSubjectByID(siteID, subjectID);
		
		JSONArray dates = new JSONArray();
		int countAnswer = 0;

		String tenantCode = Tenant.DEFAULTCODE;
		
		DocLib subjectQALib = LibHelper.getLib(DocTypes.SUBJECTQA.typeID(), tenantCode);

		String sql = SQL_SUBJECTQA + subjectQALib.getDocLibTable()
				+ " where a_rootID=? and a_answerTime is not null order by "
				+ ("hot".equals(orderS) ? "a_countPraise" : "a_answerTime") + " desc";
		Object[] params = new Object[] { subjectID };

		String iconUrl = UrlHelper.apiUserIcon();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(sql, page*LIST_COUNT, LIST_COUNT);
			rs = conn.executeQuery(querySql, params);
			
			while (rs.next()) {
				JSONObject data = assembelQA(iconUrl, subjectQALib.getDocLibID(), rs);
				
				//添加题主头像等信息
				data.put("answererIcon", subject.getString("answererIcon"));
				data.put("answerer", subject.getString("answerer"));
				data.put("answererID", subjectID);
				
				dates.add(data);
			}
			//获得话题的回答数
			sql = "select count(*) from "
					+ subjectQALib.getDocLibTable()
					+ " where a_rootID=? and a_status=1 and a_answerTime is not null ";
			rs = conn.executeQuery(sql, params);
			if (rs.next()) {
				countAnswer = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		JSONObject result = new JSONObject();
		result.put("list", dates);
		result.put("countAnswer",countAnswer);
		result.put("main", subject); //问答列表里加话题对象，以便在界面显示话题信息
		
		String key = null;
		if ("hot".equals(orderS)) {
			key = RedisKey.APP_SUBJECT_HOT_KEY + subjectID + "." + page;
		} else {
			key = RedisKey.APP_SUBJECT_QALIST_KEY + subjectID + "." + page;
		}
		RedisManager.setOneMinute(key, result.toString());
		
		return true;
	}

	/**
	 * 我提问的列表
	 */
	public boolean getMyQuestions(int siteID, Long userID, int page)
			throws E5Exception {

		String tenantCode = Tenant.DEFAULTCODE;
		DocLib subjectQALib = LibHelper.getLib(DocTypes.SUBJECTQA.typeID(), tenantCode);

		String sql = SQL_SUBJECTQA + subjectQALib.getDocLibTable()
				+ " where SYS_AUTHORID=? order by SYS_DOCUMENTID desc";

		JSONArray datas = new JSONArray();

		String iconUrl = UrlHelper.apiUserIcon();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(sql, LIST_COUNT * page, LIST_COUNT);
			rs = conn.executeQuery(querySql, new Object[] { userID });
			while (rs.next()) {
				JSONObject data = assembelQA(iconUrl, subjectQALib.getDocLibID(), rs);
				
				if (rs.getString("a_answerTime") != null) {
					data.put("answered", 1);
				} else {
					data.put("answered", 0);
				}
				
				//添加题主头像等信息
				long subjectID = data.getLong("topicId");
				JSONObject subject = getSubjectByID(siteID, subjectID);
				data.put("answererIcon", subject.getString("answererIcon"));
				data.put("answerer", subject.getString("answerer"));
				data.put("answererID", subjectID);
				
				datas.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		JSONObject result = new JSONObject();
		result.put("list", datas);
		
		RedisManager.setLonger(RedisKey.MY_SUBJICTQA_KEY + userID + "." + page, result.toString());
		return true;
	}
	/**
	 * 问答详情
	 */
	public boolean getQuestionDetail(int siteID,int subjectQAID){
		String tenantCode = Tenant.DEFAULTCODE;
		int subQaLibId = LibHelper.getLibID(DocTypes.SUBJECTQA.typeID(), tenantCode);
		try{
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc=docManager.get(subQaLibId, subjectQAID);
			if(null!=doc) {
				JSONObject data = new JSONObject();
				data.put("fileId", subjectQAID);
                data.put("subjectID", doc.getInt("a_rootID")); //话题ID

				data.put("version", doc.getTimestamp("SYS_LASTMODIFIED").getTime());
				data.put("title", doc.getString("SYS_TOPIC"));
				data.put("publishtime", InfoHelper.formatDate(doc.getCreated()));
				data.put("userName", doc.getString("SYS_AUTHORS")); //提问人
				
                data.put("answerTime", InfoHelper.formatDate(doc.getDate("a_answerTime"))); //回答时间
				data.put("answer", doc.getString("a_answer")); //回答内容
				
				data.put("content", doc.getString("a_content"));
				data.put("location", doc.getString("a_location"));

				data.put("countDiscuss", doc.getInt("a_countDiscuss"));
                data.put("countPraise", doc.getInt("a_countPraise"));
                
				String userIcon = UrlHelper.apiUserIcon() + "?uid=" + doc.getString("SYS_AUTHORID");
				data.put("userIcon", userIcon);
				
				//附件
				data.put("attachments", jsonAttachments(subQaLibId, subjectQAID));
				
				String redisK = RedisKey.APP_SUBJECT_QA_KEY + subjectQAID;
				RedisManager.set(redisK, data.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 推荐模块项中需要的列表，从Redis的列表里读
	 */
	public String getModuleList(int siteID, int count) {
		//读出Redis里的列表
		String key = RedisManager.getKeyBySite(RedisKey.APP_SUBJECT_LIST_KEY, siteID) + 0;
		String list = RedisManager.get(key);
		if (list == null) {
			boolean ok = getSubjectList(siteID, 0, 0, 0L, 0);
			if (ok) list = RedisManager.get(key);
		}
		
		//读列表的前几个做为结果
		JSONArray jsonNewArr = getSomeFromList(list, count);
		return jsonNewArr.toString();
	}
	
	// 组装话题数据，用于推荐模块
	public JSONObject assembleSubject(String iconUrl, int docLibID, long docID)
			throws E5Exception {
		JSONObject data = new JSONObject();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			long version = doc.getLastmodified().getTime();
			data.put("fileId", doc.getDocID());
			data.put("version", version);
			data.put("title", StringUtils.getNotNull(doc.getString("SYS_TOPIC")));
			data.put("pubtime", InfoHelper.formatDate(doc.getCreated()));
			data.put("sourceType", getInt(doc, "a_sourceType"));
			data.put("content", StringUtils.getNotNull(doc.getString("a_content")));
			//附件
			data.put("attachments", jsonAttachments(doc.getDocLibID(), doc.getDocID()));
			
			data.put("discussClosed", getInt(doc, "a_discussClosed"));
			data.put("questionClosed", getInt(doc, "a_questionClosed"));
			
			//关注数
			data.put("countFan", getSubCount(docID, getInt(doc, "a_countFollow")));
			data.put("countQuestion", getInt(doc, "a_countQuestion"));
	
			data.put("group", StringUtils.getNotNull(doc.getString("a_group")));
	
			data.put("answererIcon", iconUrl + "?uid=" + doc.getString("a_answererID"));
			data.put("answerer", StringUtils.getNotNull(doc.getString("a_answerer")));
			data.put("answererID", getLong(doc, "a_answererID"));
	
			JSONObject jsonObj=JSONObject.fromObject(StringUtils.getNotNull(doc.getString("a_attachments")));
			if(jsonObj.containsKey("appBannerUrl"))
				data.put("appBanner", StringUtils.getNotNull(jsonObj.getString("appBannerUrl")));
			if(jsonObj.containsKey("webBannerUrl"))
				data.put("webBanner", StringUtils.getNotNull(jsonObj.getString("webBannerUrl")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private JSONObject getSubjectByID(int siteID, long id) {
		String key = RedisKey.APP_SUBJECT_KEY + id;
		String value = RedisManager.get(key);
		if (value == null) {
			boolean ok = getSubject(siteID, id);
			if (ok) value = RedisManager.get(key);
		}
		if (value != null) {
			return JSONObject.fromObject(value);
		} else {
			return new JSONObject();
		}
	}

	private int getCountAnswer(long id) throws E5Exception {
		int subjectQALibID=LibHelper.getLibID(DocTypes.SUBJECTQA.typeID(), Tenant.DEFAULTCODE);
		String sql="select count(*) from "+LibHelper.getLibTable(subjectQALibID)+" where a_rootID=? and a_status=1 and a_answerTime is not null ";
	
		DBSession conn = null;
		IResultSet rs = null;
		int count=0;
		try {
			conn = Context.getDBSession();
			rs=conn.executeQuery(sql, new Object[] { id });
			if (rs.next()) {
				count=rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return count;
	}

	// 组装数据
	private JSONObject assembleSubject(String iconUrl, IResultSet rs, Integer docLibId)
			throws E5Exception {
	
		JSONObject data = new JSONObject();
		try {
			long docID = rs.getLong("SYS_DOCUMENTID");
			long version = rs.getTimestamp("SYS_LASTMODIFIED").getTime();
			data.put("fileId", docID);
			data.put("version", version);
			data.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
			data.put("pubtime", InfoHelper.formatDate(rs, "SYS_CREATED"));
			data.put("sourceType", getInt(rs, "a_sourceType"));
			data.put("content", StringUtils.getNotNull(rs.getString("a_content")));
			data.put("group", StringUtils.getNotNull(rs.getString("a_group")));
			data.put("groupID", getInt(rs, "a_group_ID"));
			
			data.put("discussClosed", getInt(rs, "a_discussClosed"));
			data.put("questionClosed", getInt(rs, "a_questionClosed"));
			//附件
			data.put("attachments", jsonAttachments(docLibId, docID));
			
			//关注数
			data.put("countFan", getInt(rs, "a_countFollow"));
			data.put("countQuestion", getInt(rs, "a_countQuestion"));
	
			//题主
			data.put("answererIcon", iconUrl + "?uid=" + rs.getString("a_answererID"));
			data.put("answerer", StringUtils.getNotNull(rs.getString("a_answerer")));
			data.put("answererID", getLong(rs, "a_answererID"));
	
			//头图
			JSONObject jsonObj=JSONObject.fromObject(StringUtils.getNotNull(rs.getString("a_attachments")));
			if(jsonObj.containsKey("appBannerUrl"))
				data.put("appBanner", StringUtils.getNotNull(jsonObj.getString("appBannerUrl")));
			if(jsonObj.containsKey("webBannerUrl"))
				data.put("webBanner", StringUtils.getNotNull(jsonObj.getString("webBannerUrl")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	//互动话题的问答对象
	private JSONObject assembelQA(String iconUrl, int docLibID, IResultSet rs)
			throws SQLException, E5Exception {
		JSONObject data = new JSONObject();
	
		data.put("fileId", rs.getLong("SYS_DOCUMENTID"));
		data.put("topic", rs.getString("SYS_TOPIC")); //话题的标题
		data.put("topicId", rs.getLong("a_rootID"));
		data.put("time", InfoHelper.formatDate(rs, "SYS_CREATED"));
		data.put("question", StringUtils.getNotNull(rs.getString("a_content")));
		
		long authorID = getLong(rs, "SYS_AUTHORID");
		data.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
		data.put("authorID", authorID);
		data.put("authorIcon", iconUrl + "?uid=" + authorID); //提问人头像
		
		data.put("answer", StringUtils.getNotNull(rs.getString("a_answer")));
		data.put("answerTime",InfoHelper.formatDate(rs,"a_answerTime"));
		
		data.put("countDiscuss", getLong(rs, "a_countDiscuss"));
		data.put("countPraise", getLong(rs, "a_countPraise"));
	
		data.put("discussClosed", 0);// 无此字段，默认设为0允许评论
		//附件
		data.put("attachments", jsonAttachments(docLibID, rs.getLong("SYS_DOCUMENTID")));
		return data;
	}

	// 获取话题关注按数
	private long getSubCount(long id, int countInDB) {
		if (RedisManager.hget(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, id) != null) {
			long count = Long.parseLong(RedisManager.hget(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, id));
			return count < 0 ? 0 : count ;
		} else {
			RedisManager.hset(RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, id, String.valueOf(countInDB));
			return countInDB;
		}
	}
}
