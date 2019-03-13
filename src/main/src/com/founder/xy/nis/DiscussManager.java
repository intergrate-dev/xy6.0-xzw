package com.founder.xy.nis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.article.web.ArticleServiceHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

@Component
public class DiscussManager {
	public static final int AUDITTYPE_AUDITFIRST = 0;//先审后发
	public static final int AUDITTYPE_PASSFIRST = 1;//先发后审
	public static final int AUDITTYPE_CLOSE = 2;//全站关闭评论
	
	/**
	 * 评论审批通过
	 */
	public String pass(int docLibID, long[] docIDs, boolean changeStatus) throws E5Exception {
		//若需要手动改变流程节点（app端提交），则取“审批通过”节点
		FlowNode nextNode = null;
		if (changeStatus) {
			nextNode = getDisucssFlowNode(1);
		}
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> discusses = new ArrayList<Document>();
		for (long docID : docIDs) {	
			Document discuss = docManager.get(docLibID, docID);
			discuss.set("a_status", 1);//设置状态为1，通过
			if (changeStatus) {
				discuss.setCurrentNode(nextNode.getID());
				discuss.setCurrentStatus(nextNode.getWaitingStatus());
			}
			discusses.add(discuss);
			
			clearKeyAfterPass(discuss);
			//加评论数
			increaseCountInRedis(discuss);
		}

		//同时提交多个稿件，使用事务
		String message = save(docLibID, discusses);
		
		return message;
	}


	/**
	 * 延迟入库中的自动审批通过
	 */
	public String passDelay(JSONObject obj, boolean changeStatus) throws E5Exception {
		obj.put("a_status", 1);
		
		//若需要手动改变流程节点（app端提交），则取“审批通过”节点
		if (changeStatus) {
			FlowNode nextNode = getDisucssFlowNode(1);
			
			obj.put("SYS_CURRENTNODE", nextNode.getID());
			obj.put("SYS_CURRENTSTATUS", nextNode.getWaitingStatus());
		}

		// 加评论数
		increaseDelay(obj);

		return null;
	}
	
	/**
	 * 评论审批不通过
	 */
	public String reject(int docLibID, long[] docIDs, boolean changeStatus) throws E5Exception {
		//取出当前租户的所有稿件库，以便修改评论数
		String tenantCode = LibHelper.getTenantCodeByLib(docLibID);
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		DocLib liveLib = LibHelper.getLib(DocTypes.LIVE.typeID(), tenantCode);
		DocLib qaLib = LibHelper.getLib(DocTypes.QA.typeID(), tenantCode);
		DocLib activityLib = LibHelper.getLib(DocTypes.ACTIVITY.typeID(), tenantCode);
		
		//若需要手动改变流程节点（app端提交），则取“审批不通过”节点
		FlowNode nextNode = null;
		if (changeStatus) {
			nextNode = getDisucssFlowNode(2);
		}
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> discusses = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document discuss = docManager.get(docLibID, docID);
			int oldStatus = discuss.getInt("a_status");
			discuss.set("a_status", 2);//设置状态为2，不通过
			if (changeStatus) {
				discuss.setCurrentNode(nextNode.getID());
				discuss.setCurrentStatus(nextNode.getWaitingStatus());
			}
			discusses.add(discuss);
			
			//若是已审批通过的评论再次被不通过，则需减评论数
			if (oldStatus == 1){
				clearKeyAfterReject(discuss);
				decreaseCountInRedis(articleLibs, liveLib,qaLib,activityLib, discuss);
			}
		}
		//同时提交多个稿件，使用事务
		String message = save(docLibID, discusses);
		
		return message;
	}
	
	public String rejectDelay(JSONObject obj, boolean changeStatus) throws E5Exception {
		obj.put("a_status", 2);//设置状态为2，不通过
		
		//若需要手动改变流程节点（app端提交），则取“审批不通过”节点
		FlowNode nextNode = null;
		if (changeStatus) {
			nextNode = getDisucssFlowNode(2);
			obj.put("SYS_CURRENTNODE", nextNode.getID());
			obj.put("SYS_CURRENTSTATUS", nextNode.getWaitingStatus());
		}
		return null;
	}

	/**删除评论*/
	public String delete(int docLibID, long[] docIDs){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//创建评论删除列表
		List<Document> discusses = new ArrayList<Document>();
		try {
			//取出当前租户的所有稿件库，以便修改评论数
			String tenantCode = LibHelper.getTenantCodeByLib(docLibID);
			List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
			DocLib liveLib = LibHelper.getLib(DocTypes.LIVE.typeID(), tenantCode);
			DocLib qaLib = LibHelper.getLib(DocTypes.QA.typeID(), tenantCode);
			DocLib activityLib = LibHelper.getLib(DocTypes.ACTIVITY.typeID(), tenantCode);
			
			for (long docID : docIDs) {
				Document discuss = docManager.get(docLibID, docID);
				int status = discuss.getInt("a_status");
				
				//若是已审批通过的评论，则删除时需减评论数
				if( status == 1 ){
					decreaseCountInRedis(articleLibs, liveLib, qaLib,activityLib,discuss);
				}
				
				discusses.add(discuss);
			}
		} catch (E5Exception e1) {
			System.out.println("评论删除时异常：" + e1.getLocalizedMessage());
			e1.printStackTrace();
		}
		
		//同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();	
			for (Document discuss : discusses){
				docManager.delete(discuss.getDocLibID(), discuss.getDocID(), conn);
			}
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**
	 * 禁言
	 * type: 0是对用户禁言；1是对IP禁言；2是白名单
	 */
	public String shutUp(int docLibID, int siteID, long userID, String userName, int type, String sysAuthors) {
		//检查是否已有
		String value = (type == 1) ? userName : String.valueOf(userID);
		if (BlackListHelper.has(type, value))
			return null;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			long docID = InfoHelper.getNextDocID(DocTypes.SHUTUP.typeID());
			Document shutup = docManager.newDocument(docLibID, docID);
			
			shutup.set("SYS_AUTHORS", sysAuthors);
			shutup.set("shut_siteID", siteID);
			shutup.set("shut_type", type);
			shutup.set("shut_user", userName);
			shutup.set("shut_userID", userID);
			
			docManager.save(shutup);
			
			BlackListHelper.addSet(type, value);
			
			return null;
		} catch (Exception e) {
			return "操作中出现错误：" + e.getLocalizedMessage();
		}
	}
	/**
	 * 取消禁言，删除
	 */
	public String shutCancel(int docLibID, long[] userIDs) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			
			for (long docID : userIDs) {
				Document doc = docManager.get(docLibID, docID, conn);
				if (doc != null) {
					int type = doc.getInt("shut_type");
					String value = (type == 1) 
							? doc.getString("shut_user")
							: String.valueOf(doc.getString("shut_userID"));
					BlackListHelper.remove(type, value);
					
					docManager.delete(docLibID, docID, conn);
				}
			}
			conn.commitTransaction();
			
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**
	 * 设置稿件/活动/问答/数字报稿件的内容Url，用于“我的评论”和“评论我的”列表
	 * @param jsonObj
	 */
	public void setContentUrl(JSONObject jsonObj) {
		if (jsonObj.containsKey("sourceType")) {
			int sourceType = jsonObj.getInt("sourceType");
			int siteID = jsonObj.getInt("siteID");
			
			if (sourceType == 0) {
				jsonObj.put("contentUrl", UrlHelper.getArticleContentUrl(jsonObj.getLong("articleID")));
			} else if (sourceType == 3) {
				jsonObj.put("contentUrl", UrlHelper.getPaperArticleContentUrl(siteID, jsonObj.getLong("articleID")));
			} else if (sourceType == 5) {
				jsonObj.put("contentUrl", UrlHelper.getQAContentUrl(siteID, jsonObj.getLong("articleID")));
			} else if (sourceType == 6) {
				jsonObj.put("contentUrl", UrlHelper.getActivityContentUrl(siteID, jsonObj.getLong("articleID")));
			}
		}
	}

	private void clearKeyAfterPass(Document discuss) {
		if(discuss.getInt("a_status")==1) {
			//清空最新评论列表
			String key = RedisKey.APP_DISCUSS_VIEW_KEY + discuss.getInt("a_sourceType") 
					+ "." + discuss.getString("a_articleID");
			RedisManager.clearKeyPages(key);
			
			long parentID = discuss.getLong("a_parentID");
			if (parentID > 0){
				//清空评论的最新评论列表
				RedisManager.clearKeyPages(RedisKey.APP_DISCUSS_REPLY_KEY + parentID);
				
				//添加到回复我的评论列表
				long parentUserID = discuss.getLong("a_parentUserID");
				if (parentUserID > 0) {
					String myobj = changeField(discuss);
					RedisManager.addMy(RedisKey.MY_REPLY_KEY + parentUserID, myobj);
				}
			}
		}
	}

	private void clearKeyAfterReject(Document discuss) {
			//清空最新评论列表
			String key = RedisKey.APP_DISCUSS_VIEW_KEY + discuss.getInt("a_sourceType") 
					+ "." + discuss.getString("a_articleID");
			RedisManager.clearKeyPages(key);
			
			long parentID = discuss.getLong("a_parentID");
			if (parentID > 0){
				//清空评论的最新评论列表
				RedisManager.clearKeyPages(RedisKey.APP_DISCUSS_REPLY_KEY + parentID);
			}
	}

	//放到我的评论中的字段名称和数据库中的字段不同，取出必要字段，进行一次转换
	private String changeField(Document discuss) {
		String iconUrl = UrlHelper.apiUserIcon();
		
		JSONObject myobj = new JSONObject();
		myobj.put("siteID",discuss.getInt("a_siteID"));
		myobj.put("id",discuss.getDocID());
		myobj.put("created",InfoHelper.formatDate(discuss.getCreated()));
		myobj.put("content",StringUtils.getNotNull(discuss.getString("a_content")));
		myobj.put("topic",discuss.getString("SYS_TOPIC"));
		myobj.put("topicID",discuss.getLong("a_articleID"));
		myobj.put("articleID",discuss.getString("a_articleID"));
		myobj.put("sourceType",discuss.getString("a_sourceType"));
		myobj.put("countDiscuss",0);
		myobj.put("parentID", discuss.getLong("a_parentID"));
		myobj.put("parentUserID", discuss.getLong("a_parentUserID"));
		myobj.put("countDiscuss", 0);
		myobj.put("countPraise", 0);
		myobj.put("longitude", discuss.getDouble("a_longitude"));
		myobj.put("latitude", discuss.getDouble("a_latitude"));
		myobj.put("location", StringUtils.getNotNull(discuss.getString("a_location")));
		myobj.put("userID", discuss.getLong("SYS_AUTHORID"));
		myobj.put("userIcon", iconUrl + "?uid=" + discuss.getLong("SYS_AUTHORID"));
		
		String userName = StringUtils.getNotNull(discuss.getString("SYS_AUTHORS")); // 用户名
		if (null == userName || "".equals(userName)) {
			myobj.put("userName", StringUtils.getNotNull(discuss.getString("a_userOtherID")));
		} else {
			myobj.put("userName", userName);
		}

		myobj.put("info",discuss.getString("a_info"));
		myobj.put("type",0);
		
		setContentUrl(myobj);
		
		return myobj.toString();
	}

	/**
	 * 取评论的流程节点
	 * @param index 1：取审批通过节点，2：取审批不通过节点
	 */
	private FlowNode getDisucssFlowNode(int index) throws E5Exception {
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		int flowID = flowReader.getFlows(DocTypes.DISCUSS.typeID())[0].getID();
		
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
		return nodes[index];
	}

	/**
	 * 评论审批通过时，增加Redis中的评论数，包括父评论的回复数
	 */
	private void increaseCountInRedis(Document discuss) {
		long articleID = discuss.getLong("a_articleID");
		long parentID = discuss.getLong("a_parentID");
		int sourceType = discuss.getInt("a_sourceType");
		int channel = discuss.getInt("a_channel");

		//加父评论的评论数
		if (parentID > 0){
			EventCountHelper.addDiscussCountDiscuss(parentID);
		}
		
		if (sourceType == 0){//0表示稿件	
			EventCountHelper.addArticleCountDiscuss(articleID, channel, discuss.getCreated());
		} else if (sourceType == 1){//1表示直播的评论
			EventCountHelper.addLiveCountDiscuss(articleID, channel);
		} else if (sourceType == 3){//3表示数字报的评论
			EventCountHelper.addPaperArticleCountDiscuss(articleID);
		} else if (sourceType == 4){//4表示互动话题问答的评论
			EventCountHelper.addSubjectQACountDiscuss(articleID);
		} else if (sourceType == 5){//5表示问政的评论
			EventCountHelper.addQACountDiscuss(articleID);
		}else if (sourceType == 6){//6表示活动的评论
			EventCountHelper.addActivityCountDiscuss(articleID);
		}
	}
	
	/**
	 * 评论延迟入库中自动审批通过时（白名单、先发后审），增加Redis中的评论数，包括父评论的回复数
	 */
	private void increaseDelay(JSONObject obj) {
		long articleID = obj.getLong("rootID");
		long parentID = obj.getLong("parentID");
		int sourceType = obj.getInt("sourceType");
		int channel = JsonHelper.getInt(obj, "channel", 2);

		//加父评论的评论数
		if (parentID > 0){
			EventCountHelper.addDiscussCountDiscuss(parentID);
		}
		if (sourceType == 0){//0表示稿件	
			//对于延迟（几分钟）提交评论，评论时间一定就是现在。
			EventCountHelper.addArticleCountDiscuss(articleID, channel, Calendar.getInstance().getTime());
		} else if (sourceType == 1){//1表示直播的评论
			EventCountHelper.addLiveCountDiscuss(articleID, channel);
		} else if (sourceType == 3){//3表示数字报的评论
			EventCountHelper.addPaperArticleCountDiscuss(articleID);
		} else if (sourceType == 4){//4表示互动话题问答的评论
			EventCountHelper.addSubjectQACountDiscuss(articleID);
		} else if (sourceType == 5){//5表示问政的评论
			EventCountHelper.addQACountDiscuss(articleID);
		}else if (sourceType == 6){//6表示活动的评论
			EventCountHelper.addActivityCountDiscuss(articleID);
		}
	}

	/**
	 * 已经审批通过的评论删除或撤回时，减少评论数。
	 * 这个功能不太必要，属于允许的误差。
	 */
	private void decreaseCountInRedis(List<DocLib> articleLibs, DocLib liveLib, DocLib qaLib,DocLib activityLib,Document discuss) {
		long articleID = discuss.getLong("a_articleID");
		long parentID = discuss.getLong("a_parentID");
		int sourceType = discuss.getInt("a_sourceType");

		//减父评论的评论数
		if (parentID > 0){
			decreaseCount(RedisKey.NIS_EVENT_DISCUSS, parentID);
		}
		
		if (sourceType == 0) //稿件评论
			decreaseCount(RedisKey.NIS_EVENT_ARTICLE, articleID);
		else if (sourceType == 1)//直播
			decreaseCount(RedisKey.NIS_EVENT_LIVE, articleID);
		else if (sourceType == 3)
			decreaseCount(RedisKey.NIS_EVENT_PAPERARTICLE, articleID);
		else if (sourceType == 4)
			decreaseCount(RedisKey.NIS_EVENT_SUBJECTQA, articleID);
		else if (sourceType == 5)
			decreaseCount(RedisKey.NIS_EVENT_QA, articleID);
		else if (sourceType == 6)
			decreaseCount(RedisKey.NIS_EVENT_ACTIVITY, articleID);
	}
	
	/**
	 * Redis中的评论数-1。
	 * （参考微博）少发生的场景，属于系统允许的误差。因此只在Redis中有计数时-1，无时不再读库初始化。
	 */
	private void decreaseCount(String eventKey, long articleID) {
		String key = eventKey + articleID;
		
		String count0 = RedisManager.hget(key, "d");
		if (count0 != null) {
			long count = Long.parseLong(count0) - 1;
			if (count < 0) count = 0;
			
			RedisManager.hset(key, "d", count);
		}
	}
	
	/** 多个评论的统一提交， 出错时返回错误信息 */
	private String save(int docLibID, List<Document> discusses) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();
			for (Document discuss : discusses) {
				docManager.save(discuss, conn);
			}	
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "error";
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private HttpPost assembleData(
			String url, long Uid) throws Exception {
		// 初始化一个post对象
		HttpPost httpPost = new HttpPost(url);
		// 封装参数列表
		List<NameValuePair> nvps = new ArrayList<>();

		nvps.add(new BasicNameValuePair("uid", String.valueOf(Uid)));

		// 封装成form对象
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		return httpPost;
	}
	public String memberInfo(long Uid){
		String url=InfoHelper.getConfig("互动","会员信息获取地址");
		HttpPost httpPost = null;
		String result=null;
		try {
			httpPost = assembleData(url, Uid);
			// 3. a. 发送数据并获得response; b.处理获得的数据，并返回一个json对象
			JSONObject json = ArticleServiceHelper.executeHttpRequest(httpPost, true);
	
			result = json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return result;
	}


	public Document discussReply(int docLibID, long docIDs, boolean changeStatus){
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document discuss = docManager.get(docLibID, docIDs);
			return discuss;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String discussReplySubmit(int docLibID, long docIDs, String a_answer,HttpServletRequest request){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document discuss = docManager.get(docLibID, docIDs);

			long docID = InfoHelper.getNextDocID(DocTypes.DISCUSS.typeID());

			Document doc = docManager.newDocument(docLibID, docID);
			ProcHelper.initDoc(doc, request);
			
			doc.set("a_content", a_answer);
			doc.set("a_articleID", discuss.getLong("a_articleID"));
			long parentID=discuss.getLong("a_parentID");

			String info = discuss.getString("a_info");
			JSONObject infoJson =JSONObject.fromObject(info);
			
			if(parentID>0){
				doc.set("a_parentID", parentID);
				doc.set("a_parentUserID", discuss.getLong("a_parentUserID"));
			}else{
				doc.set("a_parentID", docIDs);
				doc.set("a_parentUserID", discuss.getLong("SYS_AUTHORID"));
				infoJson.put("parentUser", discuss.getString("SYS_AUTHORS"));
				infoJson.put("parentContent", discuss.getString("a_content"));
				infoJson.put("parentTime", InfoHelper.formatDate(discuss.getCreated()));
				infoJson.put("parentAtts", jsonAttachments(discuss.getDocLibID(), discuss.getDocID()));
				infoJson.put("parentType", discuss.getInt("a_type"));
			}
			
			doc.set("a_siteID", discuss.getLong("a_siteID"));
			doc.set("SYS_TOPIC", discuss.getString("SYS_TOPIC"));
			doc.set("a_channel", 0);
			doc.set("a_type", 0);
			doc.set("a_sourceType", discuss.getInt("a_sourceType"));
			doc.set("a_status", 1);
			
			doc.set("SYS_AUTHORID", -100);
			doc.set("SYS_AUTHORS", "官方回复");
			doc.set("a_info",infoJson.toString());

			FlowNode nextNode = getDisucssFlowNode(1);
			doc.setCurrentNode(nextNode.getID());
			doc.setCurrentStatus(nextNode.getWaitingStatus());
			
			doc.set("id", docID); //后面会用到？

			clearKeyAfterPass(doc);
			// 加评论数
			increaseCountInRedis(doc);

			docManager.save(doc);
			return null;
		} catch (Exception e) {
			return "error";
		}
	}
	protected JSONArray jsonAttachments(int articleLibID, long articleID) throws E5Exception {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), articleLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
				new Object[]{articleID, articleLibID});
		JSONArray inJsonArr = new JSONArray();
		for (Document doc : docs) {
			JSONObject _inJson = new JSONObject();
			_inJson.put("type", doc.getInt("att_type"));
			_inJson.put("url", StringUtils.getNotNull(doc.getString("att_url")));

			inJsonArr.add(_inJson);
		}
		return inJsonArr;
	}
	public String exposeClear(int docLibID, long docIDs){
		String sql = null;
		
		//若是话题问答，则传入的是话题问答库ID 和 话题ID
		try {	

			int exposeLibID = LibHelper.getLibIDByOtherLib(DocTypes.EXPOSE.typeID(), docLibID);
			String exposeLibTable = LibHelper.getLibTable(exposeLibID);
			//清理举报
			sql = "delete from " + exposeLibTable + " where a_rootID=?";
			InfoHelper.executeUpdate(docLibID, sql, new Object[]{docIDs});
			
			//评论的举报数清掉
			String docLibTable = LibHelper.getLibTable(docLibID);
			sql = "update " + docLibTable + " set a_countExpose=0,a_isExposed=0 where SYS_DOCUMENTID=?";
			InfoHelper.executeUpdate(docLibID, sql, new Object[]{docIDs});
			
			return null;
		} catch (E5Exception e) {
			e.printStackTrace();

			return "error";
		}
	}

	/**
	 * 读审批类型
	 */
	public int getAuditType(int siteID) {
		JSONObject jsonConfig = getConfig(siteID);
		return JsonHelper.getInt(jsonConfig, "auditType", AUDITTYPE_AUDITFIRST);
	}

	/**
	 * 读热门评论个数
	 */
	public int getCountHot(int siteID) {
		JSONObject jsonConfig = getConfig(siteID);
		return JsonHelper.getInt(jsonConfig, "countHot", 5);
	}

	/**
	 * 读评论列表个数
	 */
	public int getCountNew(int siteID) {
		JSONObject jsonConfig = getConfig(siteID);
		return JsonHelper.getInt(jsonConfig, "countNew", 20);
	}

	/**
	 * 读评论回复个数
	 */
	public int getCountReply(int siteID) {
		JSONObject jsonConfig = getConfig(siteID);
		if (jsonConfig == null) return 0;
		
		return JsonHelper.getInt(jsonConfig, "countReply", 2);
	}

	/**
	 * 读评论参数
	 */
	private JSONObject getConfig(int siteID) {
		String key = RedisKey.SITE_CONFIG_DISCUSS_KEY + siteID;
		String discussConfig = RedisManager.get(key);
	
		if (discussConfig == null) {
			int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			JSONObject jsonConfig = null;
			try {
				Document site = docManager.get(siteLibID, siteID);
				String siteConfig = site.getString("site_config");
				if (!StringUtils.isBlank(siteConfig)) {
					jsonConfig = JsonHelper.getJson(siteConfig);
					jsonConfig = jsonConfig.getJSONObject("discuss");
				}
			} catch (E5Exception e) {
			}
			if (jsonConfig == null) jsonConfig = new JSONObject();
			RedisManager.set(key, jsonConfig.toString());
			
			return jsonConfig;
		} else {
			return JsonHelper.getJson(discussConfig);
		}
	}
}