package com.founder.xy.api.nis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.nis.BlackListHelper;
import com.founder.xy.nis.DiscussManager;
import com.founder.xy.nis.EventCountHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 与外网api通讯的互动评论的Api
 */
@Service
public class DiscussApiManager extends BaseApiManager{
	@Autowired
	private DiscussManager discussManager;
	
	private static final String SQL_PREFIX = 
			"SELECT SYS_DOCUMENTID,SYS_AUTHORS,SYS_CREATED,SYS_AUTHORID,a_content,"
					+ "a_countDiscuss, a_countPraise, a_longitude, a_latitude, a_location,"
					+ "a_articleID,a_sourceType,SYS_TOPIC,a_channel,"
					+ "a_parentID,a_parentUserID,a_info,a_type from ";
	/**
	 * @deprecated 评论直接提交，已不再使用
	 */
	public boolean discuss(String data) throws E5Exception {
		int docLibID = LibHelper.getDiscuss();
		long DocID = InfoHelper.getNextDocID(DocTypes.DISCUSS.typeID());

		JSONObject obj = JSONObject.fromObject(data);

		// 图片和视频作为帖子的附件，存入互动附件表
		String attachments = setImgVioUrl(obj, DocID, docLibID);

		// 使用ProcHelper.init()来设置帖子的初始流程等信息
		setOtherDiscuss(obj, DocID, attachments);

		return true;
	}
	
	public boolean discussDelay(JSONObject obj) throws E5Exception {
		DocLib docLib = LibHelper.getLib(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		obj.put("SYS_DOCLIBID", docLib.getDocLibID());
		obj.put("SYS_FOLDERID", docLib.getFolderID());
		
		objInit(obj);
		
		// 检查敏感词
		checkSensitiveDelay("discuss", obj);
		
		setOtherInfo( obj);
		
		autoDiscussDelay(obj);

		return true;
	}

	/**
	 * 获取稿件的热门评论
	 */
	public boolean getDiscussHot(long id,int source, int siteID)
			throws E5Exception, SQLException {
		int count = discussManager.getCountHot(siteID); //固定个数
		
		String sqlWhere = " where a_articleID=? and a_sourceType=? and a_parentID=0 and a_countPraise>0"
				+ " and a_status=1 order by a_countPraise desc";
		Object[] params = new Object[]{id, source};
		
		JSONObject redisJson = queryDiscussJson(sqlWhere, params, 0,  count);
		
		RedisManager.setOneMinute(RedisKey.APP_DISCUSS_HOT_KEY + source + "." + id, redisJson.toString());
		return true;
	}

	/**
	 * 获取稿件的最新评论
	 */
	public boolean getDiscussView(int id, int page, int source,int siteID) throws E5Exception, SQLException {
		int count = discussManager.getCountNew(siteID);
		int start = page * count;
		
		String sqlWhere = " where a_articleID=? and a_sourceType=? and a_parentID=0 and a_status=1"
				+ " order by SYS_DOCUMENTID desc";
		Object[] params = new Object[]{id, source};
		
		JSONObject redisJson = queryDiscussJson(sqlWhere, params, start,  count);
        
		// 每篇评论显示它的头几条回复
		sqlWhere = " where a_parentID=? and a_status=1 order by SYS_DOCUMENTID desc";
		int countReply =  discussManager.getCountReply(siteID); //回复个数
        JSONArray list = redisJson.getJSONArray("list");
        for (Object one : list) {
        	JSONObject inJson = (JSONObject) one;
        	String countKey = RedisKey.NIS_EVENT_DISCUSS + inJson.getLong("id");

        	long countDiscuss=inJson.getLong("countDiscuss");
        	long redisCount=0;
        	String redisCountStr = RedisManager.hget(countKey, "d");
        	if(redisCountStr!=null&&!"".equals(redisCountStr)){
        		redisCount=Long.valueOf(redisCountStr);
        	}

    		if (countDiscuss > 0||redisCount>0){
    			long discussID = inJson.getLong("id");
    			JSONObject topDiscuss = queryDiscussJson(sqlWhere, new Object[]{discussID}, 0,  countReply);
    			inJson.put("topDiscuss", topDiscuss);
    		}
		}
        
        //总数
        long totalCount = getDiscussParentCount(id,  source);
		redisJson.put("totalCount", totalCount);
		redisJson.put("hasMore", totalCount > start + count);

		RedisManager.set(RedisKey.APP_DISCUSS_VIEW_KEY + source + "." + id + "." + page,
				redisJson.toString());
		return true;
	}

    /**
     * 获取稿件的最新评论
     * @param isOrderByPraise 是否按照点赞数排序，直播评论的需求，默认为按照点赞数排序
     */
    public boolean getDiscussViewOrderByPraise(int id, int page, int source, int siteID, int isOrderByPraise) throws E5Exception, SQLException {
        int count = discussManager.getCountNew(siteID);
        int start = page * count;
        String sqlWhere;
        if(isOrderByPraise==1){
            sqlWhere = " where a_articleID=? and a_sourceType=? and a_parentID=0 and a_status=1"
                    + " order by a_countPraise desc, SYS_DOCUMENTID desc";
        }else{
            sqlWhere = " where a_articleID=? and a_sourceType=? and a_parentID=0 and a_status=1"
                    + " order by SYS_DOCUMENTID desc";
        }

        Object[] params = new Object[]{id, source};

        JSONObject redisJson = queryDiscussJson(sqlWhere, params, start,  count);

        // 每篇评论显示它的头几条回复
        sqlWhere = " where a_parentID=? and a_status=1 order by SYS_DOCUMENTID desc";
        int countReply =  discussManager.getCountReply(siteID); //回复个数
        JSONArray list = redisJson.getJSONArray("list");
        for (Object one : list) {
            JSONObject inJson = (JSONObject) one;
            String countKey = RedisKey.NIS_EVENT_DISCUSS + inJson.getLong("id");

            long countDiscuss=inJson.getLong("countDiscuss");
            long redisCount=0;
            String redisCountStr = RedisManager.hget(countKey, "d");
            if(redisCountStr!=null&&!"".equals(redisCountStr)){
                redisCount=Long.valueOf(redisCountStr);
            }

            if (countDiscuss > 0||redisCount>0){
                long discussID = inJson.getLong("id");
                JSONObject topDiscuss = queryDiscussJson(sqlWhere, new Object[]{discussID}, 0,  countReply);
                inJson.put("topDiscuss", topDiscuss);
            }
        }

        //总数
        long totalCount = getDiscussParentCount(id,  source);
        redisJson.put("totalCount", totalCount);
        redisJson.put("hasMore", totalCount > start + count);

        RedisManager.set(RedisKey.APP_DISCUSS_VIEW_KEY + source + "." + id + "." + page + "." + isOrderByPraise,
                redisJson.toString());
        return true;
    }

	/**
	 * 获取最新评论列表，不区分是否回复，所以是扁平的（flat）评论列表。
	 * 直播的评论区使用这种形式。
	 * 稿件/直播/问答等确定只使用上一方法/本方法的其中一种。
	 */
	public boolean getDiscussFlat(int id, int page, int source,int siteID) throws E5Exception, SQLException {
		int count = discussManager.getCountNew(siteID);
		int start = page * count;
		
		String sqlWhere = " where a_articleID=? and a_sourceType=? and a_status=1 order by SYS_DOCUMENTID desc";
		Object[] params = new Object[]{id, source};
		
		JSONObject redisJson = queryDiscussJson(sqlWhere, params, start,  count);
	    
	    //总数
		 long totalCount = getDiscussParentCount(id,  source);
		redisJson.put("totalCount", totalCount);
		redisJson.put("hasMore", totalCount > start + count);
	
		RedisManager.set(RedisKey.APP_DISCUSS_VIEW_KEY + source + "." + id + "." + page,
				redisJson.toString());
		return true;
	}

	/**
	 * 获取评论的最新评论
	 * @throws E5Exception
	 */
	public boolean getDiscussReply(long id, int page,int siteID)
			throws E5Exception, SQLException {
		int count =  discussManager.getCountNew(siteID); //固定个数
		int start = page * count;
		
		String sqlWhere = " where a_parentID=? and a_status=1 order by SYS_DOCUMENTID desc";
		Object[] params = new Object[]{id};
		
		JSONObject redisJson = queryDiscussJson(sqlWhere, params, start,  count);
		
		//回复总数
		int totalCount = readDiscussCount(id);
		redisJson.put("totalCount", totalCount);
		redisJson.put("hasMore", totalCount > start + count);
		
		RedisManager.setOneMinute(RedisKey.APP_DISCUSS_REPLY_KEY + id + "." + page, redisJson.toString());
		return true;
	}

	/**
	 * 我的评论
	 */
	public boolean getMyDiscuss(int userID,int siteID) throws E5Exception {
		DocLib discussLib = LibHelper.getLib(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		String tableName = discussLib.getDocLibTable();

		//准备查询参数
		List<Object> sqlList = new ArrayList<Object>();
		sqlList.add(userID);
		sqlList.add(siteID);
		
		String iconUrl = UrlHelper.apiUserIcon();

		DBSession conn = null;
		IResultSet rs = null;
		try {
			//删除已存在缓存
			RedisManager.del(RedisManager.getKeyBySite(RedisKey.MY_DISCUSS_KEY,siteID)+userID);
			conn = Context.getDBSession(discussLib.getDsID());
			String sql = conn.getDialect().getLimitString(getMyDisSql(tableName), 0, 100);
			
			rs = conn.executeQuery(sql, sqlList.toArray());
			while (rs.next()) {
				JSONObject inJson = setOneDiscuss(discussLib.getDocLibID(), iconUrl, rs);
				
				//我的评论里带稿件标题、url、渠道、来源类型
				inJson = setOtherJson(inJson, rs);
				
				RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_DISCUSS_KEY,siteID)+ userID, inJson.toString());
			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

		//若没有，则插入一个空对象
		if(RedisManager.llen(RedisKey.MY_DISCUSS_KEY+userID)==0){
			RedisManager.rpush(RedisManager.getKeyBySite(RedisKey.MY_DISCUSS_KEY,siteID)+userID, new JSONObject().toString());
		}
		//设置过期时间
		RedisManager.setTime(RedisKey.MY_DISCUSS_KEY+userID, RedisManager.week1);
		return true;
	}
	/**
	 * 取评论数、点赞数
	 * @param id
	 * @param type 2表示点赞数
	 * @param source
	 * @return
	 */
	public String getDiscussCount(int id, String type, int source) {
		String field = "d";
		if ("2".equals(type)) field = "p";
		
		switch (source) {
		case 0:
			return EventCountHelper.getArticleCount(id, field);
		case 1:
			//直播
			return EventCountHelper.getLiveCount(id, field);
		case 3:
			return EventCountHelper.getPaperArticleCount(id, field);
		case 4:
			return EventCountHelper.getSubjectQACount(id, field);
		case 5:
			return EventCountHelper.getQACount(id, field);
		case 6:
			return EventCountHelper.getActivityCount(id, field);
		default:
			return "0";
		}
	}
	/**
	 * 取评论数（不计算评论回复数）
	 * @param id
	 * @param source
	 * @return
	 */
	public long getDiscussParentCount(int id, int source) throws E5Exception, SQLException {
		DocLib discussLib = LibHelper.getLib(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		String sql = "select count(1) as totalCount  from " + discussLib.getDocLibTable()
		+ " where a_articleID=? and a_sourceType=? and a_parentID=0 and a_status=1";

		long inJson =0;
		DBSession conn = null;
		IResultSet rs = null;
		try{
			conn = Context.getDBSession(discussLib.getDsID());
			Object[] params = new Object[]{id, source};
			
			rs = conn.executeQuery(sql, params);
			
			while (rs.next()) {
				 inJson = getLong(rs, "totalCount");
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return inJson;
	}
	/**
	 * 评论我的
	 */
	public boolean myDiscussReply(int userID) throws E5Exception {
		DocLib discussLib = LibHelper.getLib(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		String tableName = discussLib.getDocLibTable();


		//准备查询参数
		List<Object> sqlList = new ArrayList<Object>();
		sqlList.add(userID);
		
		DBSession conn = null;
		IResultSet rs = null;
		String iconUrl = UrlHelper.apiUserIcon();
		try {
			//删除已存在缓存
			RedisManager.del(RedisKey.MY_REPLY_KEY+userID);
			conn = Context.getDBSession(discussLib.getDsID());
			String sql = conn.getDialect().getLimitString(getMyDisRepSql(tableName), 0, 100);
			
			rs = conn.executeQuery(sql, sqlList.toArray());
			while (rs.next()) {
				JSONObject inJson = setOneDiscuss(discussLib.getDocLibID(), iconUrl, rs);
				//评论我的里带稿件标题、url、渠道、来源类型
				inJson = setOtherJson(inJson, rs);
				RedisManager.rpush(RedisKey.MY_REPLY_KEY+userID, inJson.toString());
			}

		} catch (SQLException e) {
			//return false;
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		if(RedisManager.llen(RedisKey.MY_REPLY_KEY+userID)==0){
			RedisManager.rpush(RedisKey.MY_REPLY_KEY+userID, new JSONObject().toString());
		}
		RedisManager.setTime(RedisKey.MY_REPLY_KEY+userID, RedisManager.week1);
		return true;
	}

	/**
	 * 使用ProcHelper.init()来设置帖子的初始流程等信息
	 */
	private void setOtherDiscuss(JSONObject obj, long docID, String attachments)
			throws E5Exception {
		int channel = JsonHelper.getInt(obj, "channel", 2); // 来自渠道
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = setCommonField(obj, docManager, docID, LibHelper.getDiscuss(), "discuss");
		doc.setTopic(getTopic(obj));
		int sourceType = obj.getInt("sourceType");
		doc.set("a_articleID", obj.getLong("rootID")); // 评论稿件的ID
		doc.set("a_parentID", obj.getLong("parentID")); // 父评论ID，若是对评论进行回复，则有这个参数
		doc.set("a_sourceType", sourceType);
		doc.set("a_type", StringUtils.getNotNull(obj.getString("type")));
		doc.set("a_channel", channel);
		doc.set("a_attachments", attachments);
		
		docManager.save(doc);
		
		//自动审批通过的处理
		autoDiscuss(doc);
	}

	/**
	 * 设置评论topic字段，先从redis中找对应的文档，找不到再读数据库
	 * @param obj 评论json
	 * @return  topic
	 * @throws E5Exception
	 */
	private String getTopic(JSONObject obj) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		long rootID = obj.getLong("rootID");
		String topic = "";
		// "sourceType"（评论来源类型，0是稿件，1是直播，2是论坛（目前论坛无评论）,3是报纸稿件。默认是0）
		int sourceType = obj.getInt("sourceType");
		String key = RedisKey.APP_ARTICLE_KEY;
		int docTypeID = DocTypes.ARTICLE.typeID();

		//a_info初始化
		JSONObject infoObj = new JSONObject();
		infoObj.put("articleType", 0);
		infoObj.put("articleUrl", "");
		infoObj.put("articleUrlPad", "");
		infoObj.put("parentUser", "");
		infoObj.put("parentContent",  "");
		infoObj.put("parentTime",  "");
		infoObj.put("parentAtts", new JSONArray().toString());
		infoObj.put("parentType", 0);
		if(2 == sourceType){//互动直播条目
			long parentID = obj.getLong("parentID");
			Document indoc = docManager.get(LibHelper.getLib(DocTypes.LIVEITEM.typeID(),
					Tenant.DEFAULTCODE).getDocLibID(), parentID);
			
			//评论互动直播条目 时,source=1（直播），articleID=直播报道的a_rootID也就是直播话题ID。
			sourceType=1;

			obj.put("sourceType", sourceType);
			obj.put("parentID", 0);
			obj.put("parentID", 0);
			
			infoObj.put("parentUser", indoc.getString("SYS_AUTHORS"));
			infoObj.put("parentContent", indoc.getString("a_content"));
			infoObj.put("parentTime", InfoHelper.formatDate(indoc.getCreated()));
			infoObj.put("parentAtts", jsonAttachments(indoc.getDocLibID(), indoc.getDocID()));
		}
		
		if (0 == sourceType) {//稿件
			String docStr = RedisManager.get(key+rootID);
			if(!StringUtils.isBlank(docStr)){
				JSONObject docJSON = JSONObject.fromObject(docStr);
				topic = docJSON.getString("title");
				infoObj.put("articleType", docJSON.getInt("articleType"));
				infoObj.put("articleUrl", docJSON.containsKey("url")?docJSON.getString("url"):"");
				infoObj.put("articleUrlPad", docJSON.containsKey("shareUrl")?docJSON.getString("shareUrl"):"");
			}
			else {
				List<DocLib> list = LibHelper.getLibs(docTypeID, Tenant.DEFAULTCODE);
				Document indoc = docManager.get(list.get(1).getDocLibID(), rootID);
				if (indoc == null) {
					indoc = docManager.get(list.get(0).getDocLibID(), rootID);
				}
				topic = StringUtils.getNotNull(indoc.getString("SYS_TOPIC"));
				if (null != topic) { // 过滤html标签
					topic = InfoHelper.getTextFromHtml(topic);
				}
				infoObj.put("articleType",indoc.getInt("a_type"));
				infoObj.put("articleUrl",indoc.getString("a_url"));
				infoObj.put("articleUrlPad", indoc.getString("a_urlPad"));
			}
		} else if(3 == sourceType){//数字报
			String docStr = RedisManager.get(RedisKey.APP_PAPER_ARTICLE_KEY+rootID);
			if(!StringUtils.isBlank(docStr)){
				JSONObject docJSON = JSONObject.fromObject(docStr);
				topic = docJSON.getString("title");
				
				infoObj.put("articleType", 3);
				infoObj.put("articleUrl", docJSON.containsKey("url")?docJSON.getString("url"):"");
				infoObj.put("articleUrlPad", docJSON.containsKey("shareUrl")?docJSON.getString("shareUrl"):"");
			}
			else {
				Document indoc = docManager.get(DocTypes.PAPERARTICLE.typeID(), rootID);
				topic = StringUtils.getNotNull(indoc.getString("SYS_TOPIC"));
				if (null != topic) { // 过滤html标签
					topic = InfoHelper.getTextFromHtml(topic);
				}

				infoObj.put("articleType", 3);
				
				infoObj.put("a_articleUrl", indoc.getString("a_url"));
				infoObj.put("a_articleUrlPad", indoc.getString("a_urlPad"));
			}
		
		}else{
			switch (sourceType){
				case 1:
					key = RedisKey.APP_LIVE_MAIN_KEY;
					docTypeID = DocTypes.LIVE.typeID();
					break;
				case 4:
					key = RedisKey.APP_SUBJECT_QA_KEY;
					docTypeID = DocTypes.SUBJECTQA.typeID();
					infoObj.put("articleType", 103);
					break;
				case 5:
					key = RedisKey.APP_QA_KEY;
					docTypeID = DocTypes.QA.typeID();
					infoObj.put("articleType", 101);
					break;
				case 6:
					key = RedisKey.APP_ACTIVITY_DETAIL_KEY;
					docTypeID = DocTypes.ACTIVITY.typeID();
					infoObj.put("articleType", 102);
					break;
			}
			String docStr = RedisManager.get(key+rootID);
			if(!StringUtils.isBlank(docStr)){
				JSONObject docJSON = JSONObject.fromObject(docStr);
				topic = docJSON.getString("title");
			}
			else {
				Document indoc = docManager.get(LibHelper.getLib(docTypeID,
						Tenant.DEFAULTCODE).getDocLibID(), rootID);
				topic = indoc.getString("SYS_TOPIC");
			}
			
		}
		obj.put("topic",topic);
		obj.put("parentUserID", 0);
		obj.put("a_info",infoObj.toString());

		return topic;
	}
	/**
	 * 设置评论topic字段，先从redis中找对应的文档，找不到再读数据库
	 * @param obj 评论json
	 * @return  topic
	 * @throws E5Exception
	 */
	private void setOtherInfo(JSONObject obj) throws E5Exception {
		long userID = obj.getLong("userID");
		if (userID > 0 ){
			obj.put("a_shutup", BlackListHelper.check(userID,obj.getString("ipaddress")));
		}
		if(obj.getInt("sourceType")!=2&&obj.getLong("parentID")>0){////互动直播条目时，没有评论回复
			getParent(obj);//评论回复
		}else{
			getTopic(obj);//稿件等评论时
		}
		
		if(obj.getJSONArray("imgUrl").size()>0){
			obj.put("SYS_HAVEATTACH",1);
		}else{
			obj.put("SYS_HAVEATTACH",0);
		}
	}

	/**
	 * 设置评论的Parent相关字段，及topic字段，先从redis中找对应的文档，找不到再读数据库
	 * @param obj 评论json
	 * @return  topic
	 * @throws E5Exception
	 */
	private void getParent(JSONObject obj) throws E5Exception {
		long parentID = obj.getLong("parentID");
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		int docTypeID = DocTypes.DISCUSS.typeID();
		Document indoc = docManager.get(LibHelper.getLib(docTypeID,
						Tenant.DEFAULTCODE).getDocLibID(), parentID);
		if(indoc.getLong("a_parentID")>0){//不允许盖楼
			obj.put("parentID", indoc.getInt("a_parentID"));
			obj.put("parentUserID", indoc.getInt("a_parentUserID"));
			obj.put("a_info", indoc.getString("a_info"));
			obj.put("topic", indoc.getString("SYS_TOPIC"));
			
		}else{//作为评论回复时
			obj.put("topic", indoc.getString("SYS_TOPIC"));
			obj.put("parentUserID", indoc.getLong("SYS_AUTHORID"));
			String info=indoc.getString("a_info");
			JSONObject infoObj =JSONObject.fromObject(info);
			JSONObject parObj = new JSONObject();
			parObj.put("articleType", infoObj.getInt("articleType"));
			parObj.put("articleUrl", infoObj.getString("articleUrl"));
			parObj.put("articleUrlPad", infoObj.getString("articleUrlPad"));
			parObj.put("parentUser", indoc.getString("SYS_AUTHORS"));
			parObj.put("parentContent", indoc.getString("a_content"));
			parObj.put("parentTime", InfoHelper.formatDate(indoc.getCreated()));
			parObj.put("parentAtts", jsonAttachments(indoc.getDocLibID(), indoc.getDocID()));
			parObj.put("parentType", indoc.getInt("a_type"));
			obj.put("a_info", parObj.toString());
		}
	}

	private void objInit(JSONObject obj) throws E5Exception {
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		Flow[] flows = flowReader.getFlows(DocTypes.DISCUSS.typeID());
		
		int flowID = 0, flowNodeID = 0;
		String status = null;
		if (flows != null) {
			flowID = flows[0].getID();
			FlowNode[] nodes = flowReader.getFlowNodes(flowID);
			if (nodes != null) {
				flowNodeID = nodes[0].getID();
				status = nodes[0].getWaitingStatus();
			}
		}
		
		obj.put("SYS_CURRENTFLOW", flowID);
		obj.put("SYS_CURRENTNODE", flowNodeID);
		obj.put("SYS_CURRENTSTATUS", status);
		if (obj.getInt("SYS_FOLDERID") < 1) {
			DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
			int folderID = libReader.get(obj.getInt("SYS_DOCLIBID")).getFolderID();
			obj.put("SYS_FOLDERID", folderID);
		}
		
		obj.put("SYS_DELETEFLAG", 0);
		obj.put("SYS_ISLOCKED", 0);
		obj.put("a_status", 0);

		String date = InfoHelper.formatDate();
		obj.put("SYS_CREATED", date);
		obj.put("SYS_LASTMODIFIED", date);

		//设置内容Url
		obj.put("articleID", obj.getInt("rootID"));
		discussManager.setContentUrl(obj);
	}

	/**
	 * 浏览评论
	 */
	private JSONObject queryDiscussJson(String sqlWhere, Object[] params, int start, int count)
			throws E5Exception, SQLException {
		JSONArray jsonArr = new JSONArray();

		DocLib discussLib = LibHelper.getLib(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		String sql = SQL_PREFIX + discussLib.getDocLibTable() + sqlWhere;
		
		String iconUrl = UrlHelper.apiUserIcon();

		DBSession conn = null;
		IResultSet rs = null;
		try{
			conn = Context.getDBSession(discussLib.getDsID());
			sql = conn.getDialect().getLimitString(sql, start, count);
			
			rs = conn.executeQuery(sql, params);
			
			while (rs.next()) {
				JSONObject inJson = setOneDiscuss(discussLib.getDocLibID(), iconUrl, rs);
				jsonArr.add(inJson);
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		JSONObject redisJson = new JSONObject();
		redisJson.put("list", jsonArr);
		
		return redisJson;
	}

	private JSONObject setOneDiscuss(int discussLibID, String iconUrl,
			IResultSet rs) throws SQLException, E5Exception {
		long discussID = getLong(rs, "SYS_DOCUMENTID");
		long usrID = getLong(rs, "SYS_AUTHORID");
		
		JSONObject inJson = new JSONObject();

		inJson.put("articleID", getLong(rs, "a_articleID"));
		inJson.put("sourceType", getLong(rs, "a_sourceType"));

		inJson.put("parentID", getLong(rs, "a_parentID"));
		inJson.put("parentUserID", getLong(rs, "a_parentUserID"));
		
		inJson.put("content", StringUtils.getNotNull(rs.getString("a_content")));
		inJson.put("countDiscuss", getLong(rs, "a_countDiscuss"));
		inJson.put("countPraise", getLong(rs, "a_countPraise"));
		inJson.put("longitude", rs.getLong("a_longitude"));
		inJson.put("latitude", rs.getLong("a_latitude"));
		inJson.put("location", StringUtils.getNotNull(rs.getString("a_location")));
		inJson.put("created", InfoHelper.formatDate(rs, "SYS_CREATED"));
		inJson.put("userID", usrID);
		inJson.put("userIcon", iconUrl + "?uid=" + usrID);
		inJson.put("userName", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
		inJson.put("id", discussID);
		inJson.put("info",rs.getString("a_info"));
		inJson.put("type",rs.getInt("a_type"));
		
		//附件
		inJson.put("attachments", jsonAttachments(discussLibID, rs.getLong("SYS_DOCUMENTID")));
		
		return inJson;
	}
	
	//读出评论的回复数
	private int readDiscussCount(long id) throws E5Exception {
		int discussLibID = LibHelper.getLibID(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(discussLibID, id);
		
		return (doc == null) ? 0 : doc.getInt("a_countDiscuss");
	}
	
	//读附件，由于评论暂无附件，此方法没有被引用
	@SuppressWarnings("unused")
	private void readAtts(JSONObject inJson, long discussID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(LibHelper.getNisattachment(),
				"att_articleID=? AND (att_type=? OR att_type=?)", 
				new Object[] {discussID, 2, 1 });
	
		JSONArray imgUrl = new JSONArray();
		JSONArray videoUrl = new JSONArray();
		for (Document doc : docs) {
			if (1 == doc.getInt("att_type")) {
				imgUrl.add(StringUtils.getNotNull(doc.getString("att_url")));
			} else {
				videoUrl.add(StringUtils.getNotNull(doc.getString("att_url")));
			}
		}
		inJson.put("imgUrl", imgUrl); // 图片url，多个
		inJson.put("videoUrl", videoUrl); // 视频url，多个
	}

	/**
	 * 我的评论SQL
	 */
	private String getMyDisSql(String tableName) {
		String sqlWhere=" WHERE SYS_AUTHORID=? and a_siteID=? and a_sourceType != 1 ORDER BY SYS_DOCUMENTID DESC";
		String sql = SQL_PREFIX + tableName + sqlWhere;
		return sql.toString();
	}
	/**
	 * 评论我的SQL
	 */
	private String getMyDisRepSql(String tableName ) {
		String sqlWhere=" WHERE a_parentUserID=? ORDER BY SYS_DOCUMENTID DESC";
		String sql = SQL_PREFIX + tableName + sqlWhere;
		return sql.toString();
	}
	/**
	 * 我的评论SQL
	 * @throws SQLException 
	 */
	private JSONObject setOtherJson(JSONObject inJson,IResultSet rs) throws SQLException {

		inJson.put("siteID", getInt(rs, "a_siteID"));
		inJson.put("topic", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
		inJson.put("channel", StringUtils.getNotNull(rs.getString("a_channel")));
		
		discussManager.setContentUrl(inJson);
		
		return inJson;
	}
	/**
	 * 自动做评论通过（参数为先发后审）、不通过（含敏感词）
	 */
	private void autoDiscuss(Document doc) {
		try {
			if (doc.getInt("a_isSensitive") == 2 || doc.getInt("a_isSensitive") ==3){
				// 若含敏感词，则自动不通过
				discussManager.reject(doc.getDocLibID(), new long[]{doc.getDocID()}, true);
			} else if(doc.getInt("a_isSensitive") == 0 
					&& isPassFirst(doc.getInt("a_siteID"))){
				// 不含敏感词，若参数“互动——>先发后审”=“是”，则自动通过
				discussManager.pass(doc.getDocLibID(), new long[]{doc.getDocID()}, true);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	private void autoDiscussDelay(JSONObject obj) {
		try {
			int a_shutup = 0;
	        if (obj.containsKey("a_shutup"))
	            a_shutup = obj.getInt("a_shutup");
	        if (a_shutup==2){
				discussManager.passDelay(obj, true);
	        }
	        
			else if (obj.getInt("a_isSensitive") == 2 || obj.getInt("a_isSensitive") == 3 ){
				// 若含敏感词，则自动不通过
				discussManager.rejectDelay(obj, true);
			} else if(obj.getInt("a_isSensitive") == 0 
					&& isPassFirst(obj.getInt("siteID"))){
				// 不含敏感词，先发后审，则自动通过
				discussManager.passDelay(obj, true);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isPassFirst(int siteID) {
		return discussManager.getAuditType(siteID) == DiscussManager.AUDITTYPE_PASSFIRST;
	}
	
	/**
	 * 删除评论
	 */
	public boolean discussDelete(String jsonStr) throws E5Exception {
		JSONObject obj = JSONObject.fromObject(jsonStr);
		
		long discussID = obj.getLong("discussID");
		int siteID = obj.getInt("siteID");
		int userID = obj.getInt("userID");
		
		int discussLibID = LibHelper.getLibID(DocTypes.DISCUSS.typeID(), Tenant.DEFAULTCODE);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		docManager.delete(discussLibID, discussID);

		//删除我的评论后，清除我的评论列表缓存key
		String key = RedisManager.getKeyBySite(RedisKey.MY_DISCUSS_KEY,siteID) + userID;
		if(RedisManager.exists(key)){
            RedisManager.clear(key);
        }

		return true;
	}

}
