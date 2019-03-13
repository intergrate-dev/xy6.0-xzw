package com.founder.xy.system.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.founder.xy.api.nis.TipoffsApiManager;
import com.founder.xy.article.Article;
import net.sf.json.JSONObject;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.api.nis.DiscussApiManager;
import com.founder.xy.api.nis.QAApiManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.nis.BlackListHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.web.SensitiveWordControllerHelper;
import com.founder.xy.system.Tenant;

/**
 * 互动（评论、问答、问政、收藏）回写任务
 * Created by Wenkx on 2017/3/14.
 */

public class NisDelayJob extends BaseJob {

    private static final int DISCUSSBatSize = 500;
    private static final int SUBJECTBatSize = 50;
    private static final int QABatSize = 50;
    private static final int FAVORITABatSize = 500;
    private static final int EXPOSEBatSize = 50;
    private static final int ENTRYBatSize = 50;
    private static final int TIPOFFBatSize = 50;
    private static final int FEEDBACKBatSize = 50;


    public NisDelayJob() {
        super();
        log = Context.getLog("xy.NisDelay");
    }

    protected void execute() throws E5Exception {

        BlackListHelper.initSet(0);
        BlackListHelper.initSet(1);
        BlackListHelper.initSet(2);

        int count = new FeedBackDelay().execute(FEEDBACKBatSize);
        if (count > 0)
            log.info("意见反馈入库：" + count + "条");

        count = new DiscussDelay().execute(DISCUSSBatSize);
        if (count > 0)
            log.info("评论入库：" + count + "条");

        count = new EntryDelay().execute(ENTRYBatSize);
        if (count > 0)
            log.info("活动报名入库：" + count + "条");

        count = new SubjectQADelay().execute(SUBJECTBatSize);
        if (count > 0)
            log.info("互动话题问答入库：" + count + "条");

        count = new QADelay().execute(QABatSize);
        if (count > 0)
            log.info("互动问答入库：" + count + "条");

        count = new FavoriteDelay().execute(FAVORITABatSize);
        if (count > 0)
            log.info("收藏入库：" + count + "条");

        count = new TipOffDelay().execute(TIPOFFBatSize);
        if (count > 0)
            log.info("报料入库：" + count + "条");

        count = new ExposeDelay().execute(EXPOSEBatSize);
        if (count > 0)
            log.info("举报入库：" + count + "条");

    }
}

/**
 * 延迟入库父类，提供一些通用方法
 * Created by Wenkx on 2017/3/16.
 */
abstract class NisDelay {

	public String key;
    int docTypeID;

    public NisDelay(String key, int docTypeID) {
        this.key = key;
        this.docTypeID = docTypeID;
    }

    /**
     * 初始化对象，添加一些基本字段
     */

    void init(JSONObject obj, int docTypeID) throws E5Exception {

        DocLib docLib = LibHelper.getLib(docTypeID, Tenant.DEFAULTCODE);
        if (docLib != null) {
            obj.put("SYS_DOCLIBID", docLib.getDocLibID());
            obj.put("SYS_FOLDERID", docLib.getFolderID());
        }
        obj.put("SYS_DELETEFLAG", 0);
        long userID = obj.getLong("userID");
        String date = InfoHelper.formatDate();
        obj.put("SYS_CREATED", date);
        obj.put("SYS_LASTMODIFIED", date);
        //话题问答和问答字特有段初始化
        if (docTypeID == DocTypes.SUBJECTQA.typeID() 
        		|| docTypeID == DocTypes.QA.typeID()
        		|| docTypeID == DocTypes.TIPOFF.typeID()) {
            FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
            checkSensitive(obj);
            //检查是否被禁言
            if (userID > 0 && RedisManager.sismember(RedisKey.APP_NIS_SHUTUP_USER_KEY , String.valueOf(userID))){
                obj.put("a_shutup", 1);
            }
            obj.put("SYS_ISLOCKED", 0);
            obj.put("SYS_LASTMODIFIED", date.toString());
            obj.put("a_status", 0);

            if (docTypeID > 0) {
                Flow[] flows = flowReader.getFlows(docTypeID);
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
                if (status == null) status = "";
                obj.put("SYS_CURRENTFLOW", flowID);
                obj.put("SYS_CURRENTNODE", flowNodeID);
                obj.put("SYS_CURRENTSTATUS", status);
            }
        }
    }

    void saveAttachment(JSONObject obj) throws E5Exception {
            DiscussApiManager discussApiManager = (DiscussApiManager) Context.getBean("discussApiManager");
            String attachments = discussApiManager.setImgVioUrl(obj, obj.getLong("id"), obj.getInt("SYS_DOCLIBID"));
        obj.put("a_attachments", attachments);
}


    private void checkSensitive(JSONObject obj) {
        obj.put("a_isSensitive", 0);
        if (InfoHelper.sensitiveInNis()) {
            String content="";
            if(obj.containsKey("content"))
             content = obj.getString("content");
            else if(obj.containsKey("question"))
                content = obj.getString("question");
            String jsonStr = SensitiveWordControllerHelper.sensitive("checkSensitive", "1", null, content);
            //判断字符串是否为空
            if (!StringUtils.isBlank(jsonStr)) {
                JSONObject json = JSONObject.fromObject(jsonStr);
                //判断是否有敏感词
                if (json.has("code")) {
                    int code = json.getInt("code");
                    //有
                    if (code == 1) {
                        //处理内容 - 把敏感词加上标签
                        /*content = handleContentWithTag(content, json, 1, "sensitiveWord" );
                        content = handleContentWithTag(content, json, 2, "illegalWord");*/
                        if (json.has("type")) {
                            obj.put("a_isSensitive", json.getInt("type"));
                           /* obj.put("a_sensitiveContent", content);*/
                        }

                    }
                }
            }
        }
    }

    /**
     * 执行入库
     *
     * @param size 入库数量
     * @return 成功入库数量
     */
    public int execute(int size) {
        long count = RedisManager.llen(key);
		if (count == 0) return 0;
		
		count = count > (long) size ? size : count;

        int len = 0;
        DBSession dbsession = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = getSql();
            
            dbsession = Context.getDBSession();
            connection = dbsession.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);
			
            long startID = EUID.getID("DocID" + docTypeID, (int)count);
            for (int j = 0; j < count; j++) {
            	String value = RedisManager.lpop(key);
                JSONObject obj = JSONObject.fromObject(value);
                if (obj != null && !obj.isNullObject() && checkData(obj)) {
                    obj.put("id",startID);
                    init(obj, docTypeID);
                    addBatch(startID++, ps, obj);
                    updateKeys(obj);
                    len++;
                }
            }
            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(ps);
            ResourceMgr.closeQuietly(connection);
        }
        return len;
    }

    /**
     * 检查数据是否符合入库条件
     * @param obj  互动内容JSON
     * @return 是否符合
     */
    protected boolean checkData(JSONObject obj) {
        return true;
    }

    /**
     * 添加SQL参数
     * @param startID 起始DocID
     * @param ps 预执行语句
     * @param obj 互动内容JSON
     * @throws SQLException
     */
    protected abstract void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException;

    /**
     * 获取SQL语句
     * @return SQL语句
     * @throws E5Exception
     */
    protected abstract String getSql() throws E5Exception;

    /**
     * 入库后 更新Redis中相关的key
     */
    abstract void updateKeys(JSONObject obj);
}

/**
 * 评论延时入库
 * Created by Wenkx on 2017/3/16.
 */

class DiscussDelay extends NisDelay {
    DiscussDelay() {
        super(RedisKey.APP_DELAY_DISCUSS_KEY, DocTypes.DISCUSS.typeID());
    }

    void init(JSONObject obj, int docTypeID) throws E5Exception {
    
        //评论调用原有API
        DiscussApiManager discussApiManager = (DiscussApiManager) Context.getBean("discussApiManager");
        discussApiManager.discussDelay(obj);
        super.saveAttachment(obj);
    }

    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
       
        //-------------------------------------------------------------------------------------
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));
        //-------------------------------------------------------------------------------------
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_LASTMODIFIED")));
        ps.setInt(index++, obj.getInt("SYS_CURRENTFLOW"));
        ps.setInt(index++, obj.getInt("SYS_ISLOCKED"));
        ps.setInt(index++, obj.getInt("SYS_CURRENTNODE"));
        ps.setString(index++, obj.getString("SYS_CURRENTSTATUS"));
        //-------------------------------------------------------------------------------------
      
        ps.setString(index++, StringUtils.getNotNull(obj.getString("topic")));
        String userName = StringUtils.getNotNull(obj.getString("userName")); // 用户名
        if (null == userName || "".equals(userName)) {
            ps.setString(index++, StringUtils.getNotNull(obj.getString("userOtherID")));
        } else {
            ps.setString(index++, userName);
        }
        ps.setLong(index++, obj.getLong("userID"));
        ps.setInt(index++, obj.getInt("siteID"));
        ps.setLong(index++, obj.getLong("rootID"));
        //-------------------------------------------------------------------------------------
        ps.setLong(index++, obj.getLong("parentID"));
        ps.setInt(index++, obj.getInt("sourceType"));
        ps.setInt(index++, JsonHelper.getInt(obj, "channel")); //增加渠道
        ps.setInt(index++, obj.getInt("a_status"));
        ps.setInt(index++, obj.getInt("SYS_HAVEATTACH"));
        //-------------------------------------------------------------------------------------
        ps.setString(index++,obj.getString("a_attachments"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("content")));
        int a_isSensitive = 0;
        if (obj.containsKey("a_isSensitive"))
            a_isSensitive = obj.getInt("a_isSensitive");
        ps.setInt(index++, a_isSensitive);
        String sensitiveContent = null;
        if (obj.containsKey("a_sensitiveContent")) {
            sensitiveContent = obj.getString("a_sensitiveContent");
        }
        ps.setString(index++, sensitiveContent);
        int a_shutup = 0;
        if (obj.containsKey("a_shutup"))
            a_shutup = obj.getInt("a_shutup");
        ps.setInt(index++, a_shutup);
        //-------------------------------------------------------------------------------------
        ps.setDouble(index++, obj.getDouble("longitude"));
        ps.setDouble(index++, obj.getDouble("latitude"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("location")));
        ps.setString(index++, obj.getString("ipaddress"));
        ps.setLong(index++, obj.getLong("parentUserID"));
//        ps.setString(index++, StringUtils.getNotNull(obj.getString("parentUser")));
//        ps.setString(index++, StringUtils.getNotNull(obj.getString("parentContent")));
        //-------------------------------------------------------------------------------------
        ps.setString(index++, obj.getString("a_info"));
        ps.setInt(index, obj.getInt("type"));
//        ps.setInt(index++, obj.containsKey("articleType")?obj.getInt("articleType"):0);
//        
//        ps.setString(index++, obj.containsKey("a_articleUrl")?obj.getString("a_articleUrl"):"");
//        ps.setString(index, obj.containsKey("a_articleUrlPad")?obj.getString("a_articleUrlPad"):"");

        ps.addBatch(); //把一条评论加进本批待提交的列表中。
    }

    /**
     * 禁言用户不入库
     */
    protected boolean checkData(JSONObject obj) {
    	long userID = obj.getLong("userID");
    	if (userID > 0 ){
			int shutup= BlackListHelper.check(userID,obj.getString("ipaddress"));
			   return shutup != 1;
		}
    	return true;

     
    }

    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " (" +
                "SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                "SYS_LASTMODIFIED, SYS_CURRENTFLOW,SYS_ISLOCKED,SYS_CURRENTNODE,SYS_CURRENTSTATUS," +
                "SYS_TOPIC,SYS_AUTHORS,SYS_AUTHORID, a_siteID,a_articleID," +
                "a_parentID,a_sourceType,a_channel,a_status,SYS_HAVEATTACH," +
                "a_attachments,a_content,a_isSensitive,a_sensitiveContent,a_shutup," +
                "a_longitude,a_latitude,a_location,a_ip,a_parentUserID," +
                "a_info,a_type" +
                //",a_articleType,a_articleUrl,a_articleUrlPad" +
                ")VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,? ,?,? )";
    }

    void updateKeys(JSONObject obj) {
        System.out.println("------评论入库obj："+obj);
    	String myobj = changeField(obj);
        //  我的评论
        System.out.println("------评论入库myobj："+myobj);
        int sourceType = obj.getInt("sourceType");
        System.out.println("------评论入库sourceType："+sourceType);
        if(obj.getLong("userID")>0&&obj.getInt("a_status") == 1 && sourceType!=1){
            System.out.println("评论缓存增加");
            RedisManager.addMy(RedisManager.getKeyBySite(RedisKey.MY_DISCUSS_KEY ,obj.getInt("siteID"))+ obj.getString("userID"), myobj);
        }

        if (obj.getInt("a_status") == 1) {
            //最新评论
            String key = RedisKey.APP_DISCUSS_VIEW_KEY + obj.getInt("sourceType") + "." + obj.getString("rootID");
            RedisManager.clearKeyPages(key);
            
            if (obj.getLong("parentID") > 0) {
                //评论的最新评论
                RedisManager.clearKeyPages(RedisKey.APP_DISCUSS_REPLY_KEY + obj.getLong("parentID"));
                //评论我的
                if(obj.getLong("parentUserID")>0)
                    RedisManager.addMy(RedisKey.MY_REPLY_KEY + obj.getLong("parentUserID"), myobj);
                //小红点
                RedisManager.sadd(RedisKey.RED_DOT_DISCUSS, obj.getLong("parentUserID"));
            }
        }
    }
    //放到我的评论中的字段名称和入库时的字段不同，取出必要字段，进行一次转换
    private String changeField(JSONObject obj) {
		String iconUrl = UrlHelper.apiUserIcon();
		
        JSONObject myobj = new JSONObject();
        myobj.put("id",obj.getString("id"));
        myobj.put("content",StringUtils.getNotNull(obj.getString("content")));
        myobj.put("topic",obj.get("topic"));
        myobj.put("topicID",obj.get("rootID"));
        myobj.put("articleID",obj.get("rootID"));

        myobj.put("parentID", obj.getLong("parentID"));
        myobj.put("parentUserID", obj.getLong("parentUserID"));

        myobj.put("countDiscuss", 0);
        myobj.put("countPraise", 0);
        myobj.put("longitude", obj.getDouble("longitude"));
        myobj.put("latitude", obj.getDouble("latitude"));
        myobj.put("location", StringUtils.getNotNull(obj.getString("location")));
        myobj.put("created", StringUtils.getNotNull(obj.getString("SYS_CREATED")));
        myobj.put("userID", obj.getLong("userID"));
		myobj.put("userIcon", iconUrl + "?uid=" + obj.getLong("userID"));
		String userName = StringUtils.getNotNull(obj.getString("userName")); // 用户名
        if (null == userName || "".equals(userName)) {
        	myobj.put("userName", StringUtils.getNotNull(obj.getString("userOtherID")));
        } else {
        	myobj.put("userName", userName);
        }
        myobj.put("channel",obj.getInt("channel"));

        myobj.put("info",obj.get("a_info"));
        myobj.put("contentUrl",obj.get("contentUrl"));
        myobj.put("type",obj.getInt("type"));
        return myobj.toString();
    }
}


/**
 * 举报延迟入库
 * Created by Wenkx on 2017/3/16.
 */

class ExposeDelay extends NisDelay {
    ExposeDelay() {
        super(RedisKey.APP_DELAY_EXPOSE_KEY, DocTypes.EXPOSE.typeID());
    }


    //被举报内容举报次数+1，标记为被举报
    @Override
    void updateKeys(JSONObject obj) {

        int sourcelibID = LibHelper.getLibID(DocTypes.DISCUSS.typeID(),Tenant.DEFAULTCODE);
        switch (obj.getInt("sourceType")){
            case 0:{ //举报评论
                sourcelibID = LibHelper.getLibID(DocTypes.DISCUSS.typeID(),Tenant.DEFAULTCODE);
                break;
            }
        }
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            //TODO 需要提高效率
            Document doc = docManager.get(sourcelibID,obj.getLong("rootID"));
            doc.set("a_countExpose",doc.getInt("a_countExpose")+1);
            doc.set("a_isExposed",1);
            docManager.save(doc);
        } catch (E5Exception e) {
            e.printStackTrace();
        }

    }

    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setLong(index++, obj.getLong("rootID"));

        ps.setInt(index++, obj.getInt("sourceType"));
        ps.setInt(index++, obj.getInt("type"));
        ps.setString(index++, obj.getString("reason"));
        ps.setString(index++, obj.getString("userName"));
        ps.setLong(index++, obj.getLong("userID"));
        ps.setTimestamp(index, Timestamp.valueOf(obj.getString("SYS_CREATED")));

        ps.addBatch(); //把一条数据加进本批待提交的列表中。
    }

    protected String getSql() throws E5Exception {

        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " (" +
                "SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,a_rootID," +
                "a_sourceType, a_type,a_reason,SYS_AUTHORS,SYS_AUTHORID," +
                "SYS_CREATED" +
                ")VALUES(?,?,?,?,?, ?,?,?,?,?, ?)";
    }
}


/**
 * 收藏延时入库
 * Created by Wenkx on 2017/3/16.
 */

class FavoriteDelay extends NisDelay {
    FavoriteDelay() {
        super(RedisKey.APP_DELAY_FAVORITE_KEY, DocTypes.FAVORITE.typeID());
    }

    /**
	 * 执行入库
	 *
	 * @param size 入库数量
	 * @return 成功入库数量
	 */
	public int execute(int size) {
	    int len = 0;
	    
	    DBSession dbsession = null;
	    Connection connection = null;
	    PreparedStatement ps = null;
	    try {
	        //没有更新数据
			long count = RedisManager.llen(key);
			count = count > (long) size ? size : count;
			if (count == 0) return len;
			
	        dbsession = Context.getDBSession();
	        connection = dbsession.getConnection();
	        
	        for (int j = 0; j < count; j++) {
	        	//一条一条收藏执行（因为可能中间有取消收藏）
	        	String value = RedisManager.lpop(key);
	        	
	            JSONObject obj = JSONObject.fromObject(value);
                System.out.println("-1-11-1-1-1-.........-1-1-1-1-1-1-");
	            if (obj != null && !obj.isNullObject() ) {
	            	int cancel= JsonHelper.getInt(obj, "cancel", 0);
					if (cancel == 1) {
                        System.out.println("开始收藏取消》》》》》》》》");
	            		deleteFavInRedis(obj);
	            		 
	            		ps = connection.prepareStatement(sqlDelete());
	            		deleteFavInDB(ps, obj);
	            	}else{
                        System.out.println("00000000.........0000000");
						boolean canAdd = checkData(obj);
						if (!canAdd) continue;//已收藏
                        System.out.println("000000-1-1-100.........0000-1-1-1000");
	            		
	            		init(obj, docTypeID);
	            		if (obj.isEmpty()) continue; //init中出异常则清空obj
	            		
	            		long startID = InfoHelper.getNextDocID(docTypeID);
	            		obj.put("id",startID);
	            		
	            		String sql = getSql();
	            		ps = connection.prepareStatement(sql);
                        System.out.println("66666666.........666666666");
	            		addBatch(startID, ps, obj);
                        System.out.println("777777777.........777777777");
	            		updateKeys(obj);
                        System.out.println("888888888.........88888888888");
	            	}
					len++;
	            }
	        }
	      
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        ResourceMgr.closeQuietly(ps);
	        ResourceMgr.closeQuietly(connection);
	        ResourceMgr.closeQuietly(dbsession);
	    }
	    return len;
	}

	void init(JSONObject obj, int docTypeID) throws E5Exception {
        super.init(obj, docTypeID);
        System.out.println("11111111.........11111");
        
        //根据收藏对象不同，设置收藏的标题、时间、栏目等信息
        long rootID = obj.getLong("articleID");
        try {
	        switch (obj.getInt("type")) {
	            case 0: {//稿件：取标题、稿件类型、栏目、发布时间
	            	initFavArticle(obj, rootID);
	                break;
	            }
	            case 1: {//直播：取标题、直播开始时间
	                initFavLive(obj, rootID);
	                break;
	            }
	            case 3: {//数字报稿件：取标题、版次、发布时间
	                initFavPaper(obj, rootID);
	                break;
	            }
	            case 4: {//话题问答：取标题、提交时间
	                initFavSubject(obj, rootID);
	                break;
	            }
	            case 5: {//问答：取标题、提交时间
	                initFavQA(obj, rootID);
	                break;
	            }
	            case 6: {// 活动：取标题、活动开始时间
	                initFavActivity(obj, rootID);
	                break;
	            }
	        }
        } catch (Exception e) {
            e.printStackTrace();
			obj.clear();// 异常时执行下一条
		}
    }

	protected String getSql() throws E5Exception {
	
	    //一行5个
	    return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " ( " +
	            " SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,fav_userID," +
	            "  fav_type,SYS_CREATED,fav_siteID ,SYS_TOPIC,fav_articleID, " +
	            " fav_articleType,fav_column,fav_time,fav_url,fav_urlPad,"+
                 " fav_channel,fav_imgUrl,SYS_TOPICWeb,fav_urlWeb,fav_urlPadWeb,fav_columnWeb"+
	            " )VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?)";
	}

	protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setLong(index++, obj.getLong("userID"));

        ps.setInt(index++, obj.getInt("type"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));
        ps.setLong(index++, obj.getLong("siteID"));
        ps.setString(index++, obj.getString("SYS_TOPIC"));
        ps.setLong(index++, obj.getLong("articleID"));

        if(obj.containsKey("fav_articleType"))
            ps.setInt(index++, obj.getInt("fav_articleType"));
        else ps.setInt(index++, 0);
        if(obj.containsKey("fav_column"))
            ps.setString(index++,obj.getString("fav_column"));
        else ps.setString(index++,null);
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("fav_time")));
        ps.setString(index++, obj.getString("fav_url"));
        ps.setString(index++, obj.getString("fav_urlPad"));
        ps.setInt(index++, obj.getInt("channel"));
        ps.setString(index++,obj.getString("imgUrl"));
        String SYS_TOPICWeb ="";
        if(obj.has("SYS_TOPICWeb")){
            SYS_TOPICWeb = obj.getString("SYS_TOPICWeb");
        }
        String fav_urlWeb ="";
        if(obj.has("fav_urlWeb")){
            fav_urlWeb = obj.getString("fav_urlWeb");
        }
        String fav_urlPadWeb ="";
        if(obj.has("fav_urlPadWeb")){
            fav_urlPadWeb = obj.getString("fav_urlPadWeb");
        }
        String fav_columnWeb ="";
        if(obj.has("fav_columnWeb")){
            fav_columnWeb = obj.getString("fav_columnWeb");
        }

        ps.setString(index++,SYS_TOPICWeb);
        ps.setString(index++,fav_urlWeb);
        ps.setString(index++,fav_urlPadWeb);
        ps.setString(index,fav_columnWeb);

        ps.execute(); //把一条数据加进本批待提交的列表中。
    }
    @Override
	protected boolean checkData(JSONObject obj) {
    	int docLibID = LibHelper.getLibID(DocTypes.FAVORITE.typeID(), Tenant.DEFAULTCODE);
    	
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    try {
			Document[] doc = docManager.find(docLibID, 
					"fav_userID=? AND fav_articleID=? AND fav_type=?",
					new Object[] {obj.getLong("userID"),obj.getLong("articleID"), obj.getInt("type")});
			if (doc != null && doc.length > 0) {
				return false;
			}
	    } catch (E5Exception e) {
			e.printStackTrace();
		}
	    
	    return true;
	}

	void updateKeys(JSONObject obj) {
	    //3,更新我的收藏
	    if(obj.getLong("userID")>0){
	    	String myobj =changeField(obj);
	        RedisManager.addMy(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,obj.getInt("siteID")) + obj.getLong("userID"),myobj);
	    }
	}

	private void initFavActivity(JSONObject obj, long rootID)
			throws E5Exception {
		String docStr = RedisManager.get(RedisKey.APP_ACTIVITY_DETAIL_KEY + rootID);
		if (!StringUtils.isBlank(docStr)) {
		    JSONObject docJSON = JSONObject.fromObject(docStr);
		    obj.put("SYS_TOPIC", docJSON.getString("title"));
		    obj.put("fav_time", docJSON.getString("startTime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		} else {
		    DocumentManager docManager = DocumentManagerFactory.getInstance();
		    Document indoc = docManager.get(LibHelper.getLib(DocTypes.ACTIVITY.typeID(),
		            Tenant.DEFAULTCODE).getDocLibID(), rootID);
		    obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
		    obj.put("fav_time", indoc.getString("a_startTime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		}
	}

	private void initFavQA(JSONObject obj, long rootID) throws E5Exception {
		String docStr = RedisManager.get(RedisKey.APP_QA_KEY + rootID);
		if (!StringUtils.isBlank(docStr)) {
		    JSONObject docJSON = JSONObject.fromObject(docStr);
		    obj.put("SYS_TOPIC", docJSON.getString("title"));
		    obj.put("fav_time", docJSON.getString("publishtime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		} else {
		    DocumentManager docManager = DocumentManagerFactory.getInstance();
		   Document indoc = docManager.get(LibHelper.getLib(DocTypes.QA.typeID(),
		            Tenant.DEFAULTCODE).getDocLibID(), rootID);
		    obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
		    obj.put("fav_time", InfoHelper.formatDate(indoc.getCreated()));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		}
	}

	private void initFavSubject(JSONObject obj, long rootID)
			throws E5Exception {
		String docStr = RedisManager.get(RedisKey.APP_SUBJECT_QA_KEY + rootID);
		if (!StringUtils.isBlank(docStr)) {
		    JSONObject docJSON = JSONObject.fromObject(docStr);
		    obj.put("SYS_TOPIC", docJSON.getString("title"));
		    obj.put("fav_time", docJSON.getString("publishtime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		} else {
		    DocumentManager docManager = DocumentManagerFactory.getInstance();
		    Document indoc = docManager.get(LibHelper.getLib(DocTypes.SUBJECTQA.typeID(),
		            Tenant.DEFAULTCODE).getDocLibID(), rootID);
		    obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
		    obj.put("fav_time", InfoHelper.formatDate(indoc.getCreated()));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		}
	}

	private void initFavPaper(JSONObject obj, long rootID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		    Document indoc = docManager.get(LibHelper.getLib(DocTypes.PAPERARTICLE.typeID(),
		            Tenant.DEFAULTCODE).getDocLibID(), rootID);
		    obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
		    obj.put("fav_articleType", indoc.getInt("a_type"));
		    obj.put("fav_column", indoc.getString("a_layout"));
		    obj.put("fav_time", indoc.getString("a_pubTime"));	
		    obj.put("fav_url", indoc.getString("a_url"));
		    obj.put("fav_urlPad", indoc.getString("a_urlPad"));
	}

	private void initFavLive(JSONObject obj, long rootID) throws E5Exception {
		String docStr = RedisManager.get(RedisKey.APP_LIVE_MAIN_KEY + rootID);
		if (!StringUtils.isBlank(docStr)) {
		    JSONObject docJSON = JSONObject.fromObject(docStr);
		    obj.put("SYS_TOPIC", docJSON.getString("title"));
		    obj.put("fav_time", docJSON.getString("startTime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		} else {
		    DocumentManager docManager = DocumentManagerFactory.getInstance();
		    Document indoc = docManager.get(LibHelper.getLib(DocTypes.LIVE.typeID(),
		            Tenant.DEFAULTCODE).getDocLibID(), rootID);
		    obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
		    obj.put("fav_time", indoc.getString("a_startTime"));
		    obj.put("fav_articleType", 0);
		    obj.put("fav_column", "");
		    obj.put("fav_url", "");
		    obj.put("fav_urlPad", "");
		}
	}

	private void initFavArticle(JSONObject obj, long rootID) throws E5Exception {
		List<DocLib> list = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), Tenant.DEFAULTCODE);
		
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
        System.out.println("2222222222.........2222222222222");
		Document indoc = docManager.get(list.get(1).getDocLibID(), rootID);
		if (indoc == null) {
            System.out.println("3333333333333.........333333333333");
            indoc = docManager.get(list.get(0).getDocLibID(), rootID);
        }
        System.out.println("44444444444.........4444444444");
        String topic = indoc.getString("SYS_TOPIC");

        if (null != topic) { // 过滤html标签
            obj.put("SYS_TOPIC", InfoHelper.getTextFromHtml(topic));
            obj.put("fav_articleType", indoc.getInt("a_type"));
            obj.put("fav_column", indoc.getString("a_column"));
            obj.put("fav_time", indoc.getString("a_pubTime"));
            obj.put("fav_url", indoc.getString("a_url"));
            obj.put("fav_urlPad", indoc.getString("a_urlPad"));
        }

        Document indocWeb = docManager.get(list.get(0).getDocLibID(), rootID);
        String topicWeb = "";
        if(indocWeb!=null){
            topicWeb = indocWeb.getString("SYS_TOPIC");
        }
        String fav_urlWeb = "";
        String fav_urlPadWeb = "";
        String fav_columnWeb = "";
        if (!StringUtils.isBlank(topicWeb)) { // 过滤html标签
            topicWeb = InfoHelper.getTextFromHtml(topicWeb);
            fav_urlWeb = indocWeb.getString("a_url");
            fav_urlPadWeb = indocWeb.getString("a_urlPad");
            fav_columnWeb = indocWeb.getString("a_column");
        }
        obj.put("SYS_TOPICWeb", topicWeb);
        obj.put("fav_urlWeb", fav_urlWeb);
        obj.put("fav_urlPadWeb", fav_urlPadWeb);
        obj.put("fav_columnWeb", fav_columnWeb);

        System.out.println("555555555.........5555555555");
	}

	private String sqlDelete() throws E5Exception {
		return "delete from " + LibHelper.getLibTable(docTypeID, Tenant.DEFAULTCODE) +
		    	" WHERE fav_userID=? AND fav_articleID=? and fav_type=?";
	}

	private void deleteFavInDB(PreparedStatement ps, JSONObject obj) throws SQLException {
    	ps.setLong(1, obj.getLong("userID"));
    	ps.setLong(2, obj.getLong("articleID"));
    	ps.setLong(3, obj.getInt("type"));
    	
    	ps.executeUpdate();
    }
	//移除我的收藏
    private void deleteFavInRedis(JSONObject obj) {
    	long userID = obj.getLong("userID");
    	
    	//Redis里我的收藏列表
        String key = RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,obj.getInt("siteID")) + userID;
        System.out.println("收藏取消》》》》》》》》key:"+key);
        long len =  RedisManager.llen(key);
    	List<String> redData = RedisManager.lrange(key, 0,len);
    	for (String data : redData) {
			JSONObject dataJson = JSONObject.fromObject(data);
			if(dataJson.getLong("articleID")==obj.getLong("articleID")&&
				dataJson.getInt("type")==obj.getInt("type")){
                RedisManager.lrem(RedisManager.getKeyBySite(RedisKey.MY_FAVORITE_KEY,obj.getInt("siteID")) + userID, data);
				return;
			}
		}
	}

	//放到我的收藏中的字段名称和入库时的字段不同，取出必要字段，进行一次转换
	private String changeField(JSONObject obj) {
		JSONObject myobj= new JSONObject();
        myobj.put("articleID", obj.getLong("articleID"));
        myobj.put("type", obj.getInt("type"));
		myobj.put("siteID", obj.getLong("siteID"));
		myobj.put("channel", obj.getInt("channel"));
        myobj.put("url",obj.getString("fav_url"));
        myobj.put("urlPad", obj.getString("fav_urlPad"));
		myobj.put("title", StringUtils.getNotNull(obj.getString("SYS_TOPIC")));
        myobj.put("column", StringUtils.getNotNull(obj.getString("fav_column")));
		myobj.put("articleType", obj.getInt("fav_articleType"));
		myobj.put("time", StringUtils.getNotNull(obj.getString("fav_time")));

        myobj.put("imgUrl",obj.getString("imgUrl"));
        String SYS_TOPICWeb ="";
        if(obj.has("SYS_TOPICWeb")){
            SYS_TOPICWeb = obj.getString("SYS_TOPICWeb");
        }
        String fav_urlWeb ="";
        if(obj.has("fav_urlWeb")){
            fav_urlWeb = obj.getString("fav_urlWeb");
        }
        String fav_urlPadWeb ="";
        if(obj.has("fav_urlPadWeb")){
            fav_urlPadWeb = obj.getString("fav_urlPadWeb");
        }
        String fav_columnWeb ="";
        if(obj.has("fav_columnWeb")){
            fav_columnWeb = obj.getString("fav_columnWeb");
        }

        myobj.put("titleWeb",SYS_TOPICWeb);
        myobj.put("urlWeb",fav_urlWeb);
        myobj.put("urlPadWeb",fav_urlPadWeb);
        myobj.put("columnWeb",fav_columnWeb);
        //设置内容url
        setContent(myobj);

        //增加来源字段
        int docLibID;
        if(obj.getInt("type")==3){//数字报来源处理
            docLibID = LibHelper.getLib(DocTypes.PAPERARTICLE.typeID(), Tenant.DEFAULTCODE).getDocLibID();
        }else{
            docLibID = LibHelper.getArticleAppLibID();
        }
        long docID=obj.getLong("articleID");
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = null;
        String source = "";
        try {
            doc = docManager.get(docLibID, docID);

            if(doc!=null){
                source = StringUtils.getNotNull(doc.getString("a_source"));
            }
            myobj.put("source",source);

            int articleType = obj.getInt("fav_articleType");
            if(articleType == Article.TYPE_LIVE){
                myobj.put("linkID",StringUtils.getNotNull(doc.getString("a_linkID")));
            }else{
                myobj.put("linkID","");
            }
            
            if(articleType == Article.TYPE_PIC){
            	myobj.put("picContent",StringUtils.getNotNull(doc.getString("a_content")));
			}else{
				myobj.put("picContent","");
			}

        } catch (E5Exception e) {
            e.printStackTrace();
        } finally{
            System.out.println("source>>>>>>>>>>>"+source);
            System.out.println("myObj>>>>>>>>>>>>>"+myobj);
            return myobj.toString();
        }
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
}

/**
 * 话题延时入库
 * Created by Wenkx on 2017/3/16.
 */

class QADelay extends NisDelay {
    QADelay() {
        super(RedisKey.APP_DELAY_QA_KEY, DocTypes.QA.typeID());
    }

    void init(JSONObject obj, int docTypeID) throws E5Exception {
        super.init(obj, docTypeID);
        //调用原有API
        QAApiManager otherApiManager = (QAApiManager) Context.getBean("QAApiManager");
        otherApiManager.qaDelay(obj);
    }

    @Override
    void updateKeys(JSONObject obj) {
        //3,更新我的问答
        if(obj.getLong("userID")>0)
            RedisManager.clearKeyPages(RedisKey.MY_QA_KEY + obj.getLong("userID"));
    }

    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));

        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_LASTMODIFIED")));
        ps.setInt(index++, obj.getInt("SYS_CURRENTFLOW"));
        ps.setInt(index++, obj.getInt("SYS_ISLOCKED"));
        ps.setInt(index++, obj.getInt("SYS_CURRENTNODE"));
        ps.setString(index++, obj.getString("SYS_CURRENTSTATUS"));
        ps.setInt(index++, obj.getInt("SYS_HAVEATTACH"));

        ps.setString(index++, StringUtils.getNotNull(obj.getString("title")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("userName")));
        ps.setLong(index++, obj.getLong("userID"));
        ps.setInt(index++, obj.getInt("siteID"));
        ps.setInt(index++,2);

        ps.setInt(index++, obj.getInt("groupID"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("group")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("content")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("attachments")));

        int a_shutup = 0;
        if (obj.containsKey("a_shutup"))
            a_shutup = obj.getInt("a_shutup");
        ps.setInt(index++, a_shutup);
        //order
        ps.setInt(index++, obj.getInt("id"));

        ps.setDouble(index++, obj.getDouble("longitude"));
        ps.setDouble(index++, obj.getDouble("latitude"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("location")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("regionName")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("regionId")));

        ps.setString(index++, StringUtils.getNotNull(obj.getString("department")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("realName")));
        ps.setString(index, StringUtils.getNotNull(obj.getString("phone")));

        ps.addBatch(); //把一条评论加进本批待提交的列表中。
    }

    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " (" +
                "SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                "SYS_LASTMODIFIED, SYS_CURRENTFLOW,SYS_ISLOCKED,SYS_CURRENTNODE,SYS_CURRENTSTATUS,SYS_HAVEATTACH," +
                "SYS_TOPIC,SYS_AUTHORS,SYS_AUTHORID, a_siteID,a_sourceType," +
                "a_group_ID,a_group,a_content,a_attachments," +
                "a_shutup,a_order," +
                "a_longitude,a_latitude,a_location,a_region,a_regionID," +
                "a_askTo,a_realName,a_phone" +

                ")VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";
    }
}


/**
 * 话题问答延时入库
 * Created by Wenkx on 2017/3/16.
 */

class SubjectQADelay extends NisDelay {

    SubjectQADelay() {
        super(RedisKey.APP_DELAY_SUBJECTQA_KEY, DocTypes.SUBJECTQA.typeID());
    }

    @Override
    void updateKeys(JSONObject obj) {
        if(obj.getLong("userID")>0){
            //3,更新我的话题问答
            RedisManager.clearKeyPages(RedisKey.MY_SUBJICTQA_KEY + obj.getLong("userID"));
            //更新话题列表
            RedisManager.clearKeyPages(RedisManager.getKeyBySite(RedisKey.APP_SUBJECT_LIST_KEY, obj.getInt("siteID")));
        }

    }

    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));

        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_LASTMODIFIED")));
        ps.setInt(index++, obj.getInt("SYS_CURRENTFLOW"));
        ps.setInt(index++, obj.getInt("SYS_ISLOCKED"));
        ps.setInt(index++, obj.getInt("SYS_CURRENTNODE"));
        ps.setString(index++, obj.getString("SYS_CURRENTSTATUS"));

        ps.setString(index++, StringUtils.getNotNull(obj.getString("topic")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("userName")));
        //作者ID
        ps.setLong(index++, obj.getLong("userID"));
        ps.setLong(index++, obj.getLong("subjectID"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("question")));

        if(obj.containsKey("answer")) {
            ps.setString(index++, StringUtils.getNotNull(obj.getString("answer")));
            ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("answerTime")));
        }
        else {
            ps.setString(index++, "");
            ps.setTimestamp(index++, null);
        }
        ps.setInt(index++, obj.getInt("a_isSensitive"));
        int a_shutup = 0;
        if (obj.containsKey("a_shutup"))
            a_shutup = obj.getInt("a_shutup");
        ps.setInt(index++, a_shutup);
        //order
        ps.setLong(index++, startID);

        ps.setDouble(index++, obj.getDouble("longitude"));
        ps.setDouble(index++, obj.getDouble("latitude"));
        ps.setString(index, StringUtils.getNotNull(obj.getString("location")));

        ps.addBatch(); //把一条评论加进本批待提交的列表中。
    }

    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " (" +
                "SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                "SYS_LASTMODIFIED, SYS_CURRENTFLOW,SYS_ISLOCKED,SYS_CURRENTNODE,SYS_CURRENTSTATUS," +
                "SYS_TOPIC,SYS_AUTHORS,SYS_AUTHORID, a_rootID,a_content," +
                "a_answer,a_answerTime,a_isSensitive,a_shutup,a_order," +
                "a_longitude,a_latitude,a_location" +
                ")VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)";
    }
}

/**
 * 活动报名延迟入库
 * Created by Wenkx on 2017/3/22.
 */
class EntryDelay extends NisDelay {
    EntryDelay() {
        super(RedisKey.APP_DELAY_ENTRY_KEY, DocTypes.ENTRY.typeID());
    }

    @Override
    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));

        ps.setString(index++, obj.getString("SYS_TOPIC"));
        ps.setLong(index++, obj.getLong("fileId"));
        ps.setLong(index++, obj.getLong("userID"));
        ps.setString(index++, obj.getString("userName"));
        ps.setString(index++, obj.getString("realName"));
        ps.setString(index, obj.getString("phone"));

        ps.addBatch(); //把一条数据加进本批待提交的列表中。
    }

    /**
     * 执行入库
     *
     * @param size 入库数量
     * @return 成功入库数量
     */
    public int execute(int size) {
        long count = RedisManager.llen(key);
		if (count == 0) return 0;
		
		count = count > (long) size ? size : count;

        int len = 0;
        DBSession dbsession = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = getSql();
            
            dbsession = Context.getDBSession();
            connection = dbsession.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);
			
            long startID = EUID.getID("DocID" + docTypeID, (int)count);
            Set<String> idSet= new HashSet<String>();
            for (int j = 0; j < count; j++) {
            	String value = RedisManager.lpop(key);
                JSONObject obj = JSONObject.fromObject(value);
                String id = obj.getLong("userID")+"-"+obj.getLong("fileId");
                if(idSet.contains(id)){//验证是否重复提交
                	continue;
                }else{
                	idSet.add(id);
                }
                if (obj != null && !obj.isNullObject() && checkData(obj)) {
                    obj.put("id",startID);
                    init(obj, docTypeID);
                    addBatch(startID++, ps, obj);
                    updateKeys(obj);
                    len++;
                }
            }
            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(ps);
            ResourceMgr.closeQuietly(connection);
        }
        return len;
    }
    @Override
    protected boolean checkData(JSONObject obj) {
        int docLibID = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(), Tenant.DEFAULTCODE);
        int entryDocLibID = LibHelper.getLibID(DocTypes.ENTRY.typeID(), Tenant.DEFAULTCODE);
        try {
        	DocumentManager docManager = DocumentManagerFactory.getInstance();
        	//已经报过名了
        	Document[] docs = docManager.find(entryDocLibID, "entry_userID=? AND entry_targetID = ? ", new Object[] {obj.getLong("userID"),obj.getLong("fileId")});
        	if(docs!=null&&docs.length>0) 
        		return false;
            Document doc = docManager.get(docLibID, obj.getLong("fileId"));
            obj.put("SYS_TOPIC", doc.getTopic());
            return (doc.getLong("a_countLimited") <= 0) ||  //没有人数限制
                    doc.getLong("a_count") < doc.getLong("a_countLimited"); //人数未满
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    void init(JSONObject obj, int docTypeID) throws E5Exception {
        super.init(obj, docTypeID);
        //设置报名活动的标题
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long rootID = obj.getLong("fileId");

        String docStr = RedisManager.get(RedisKey.APP_ACTIVITY_DETAIL_KEY + rootID);
        if (!StringUtils.isBlank(docStr)) {
            JSONObject docJSON = JSONObject.fromObject(docStr);
            obj.put("SYS_TOPIC", docJSON.getString("title"));
        } else {
            Document indoc = docManager.get(LibHelper.getLib(DocTypes.ACTIVITY.typeID(),
                    Tenant.DEFAULTCODE).getDocLibID(), rootID);
            obj.put("SYS_TOPIC", indoc.getString("SYS_TOPIC"));
        }
    
    }

    @Override
    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID,Tenant.DEFAULTCODE) + " (" +
                "SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                "SYS_TOPIC,entry_targetID,entry_userID,entry_userName,entry_realName," + 
                "entry_phone" +
                ")VALUES(?,?,?,?,?, ?,?,?,?,?, ?)";
    }

    @Override
    void updateKeys(JSONObject obj) {
        int docLibID = LibHelper.getLibID(DocTypes.ACTIVITY.typeID(), Tenant.DEFAULTCODE);
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, obj.getLong("fileId"));
            //报名数加1
            doc.set("a_count", doc.getLong("a_count") + 1);
            docManager.save(doc);
            //Redis中的活动报名数+1
            RedisManager.set(RedisKey.APP_ACTIVITY_ENTRYNUM + doc.getDocID(), doc.getString("a_count"), 7 * 24 * 60 * 60);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        //按报名人ID清理Redis中的“我报名的活动”
        if(obj.getLong("userID")>0)
            RedisManager.clearKeyPages(RedisKey.MY_ENTRY_KEY + obj.getLong("userID"));
    }
}

class TipOffDelay extends NisDelay {

    TipOffDelay() {
        super(RedisKey.APP_DELAY_TIPOFF_KEY, DocTypes.TIPOFF.typeID());
    }

    void init(JSONObject obj, int docTypeID) throws E5Exception {
        super.init(obj, docTypeID);
        saveAttachment(obj);
    }

    void saveAttachment(JSONObject obj) throws E5Exception {
        TipoffsApiManager tipoffsApiManager = (TipoffsApiManager) Context.getBean("tipoffsApiManager");
        String attachments = tipoffsApiManager.setImgVioUrl(obj, obj.getLong("id"), obj.getInt("SYS_DOCLIBID"));
        obj.put("a_attachments", attachments);
    }

    /**
     * 添加SQL参数
     *
     * @param startID 起始DocID
     * @param ps      预执行语句
     * @param obj     互动内容JSON
     * @throws SQLException
     */
    @Override
    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_LASTMODIFIED")));
        ps.setInt(index++, obj.getInt("SYS_CURRENTFLOW"));
        ps.setInt(index++, obj.getInt("SYS_ISLOCKED"));
        ps.setInt(index++, obj.getInt("SYS_CURRENTNODE"));
        ps.setString(index++, obj.getString("SYS_CURRENTSTATUS"));

        ps.setString(index++, StringUtils.getNotNull(obj.getString("topic")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("userName")));
        //作者ID
        ps.setLong(index++, obj.getLong("userID"));
        ps.setString(index++, obj.getString("a_attachments"));
        ps.setInt(index++, obj.getInt("siteID"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("tag")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("contactNo")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("content")));
        ps.setInt(index++, obj.getInt("a_isSensitive"));
        ps.setDouble(index++, obj.getDouble("longitude"));
        ps.setDouble(index++, obj.getDouble("latitude"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("location")));
        ps.setString(index, StringUtils.getNotNull(obj.getString("source")));
        ps.addBatch(); //把一条数据加进本批待提交的列表中。
        System.out.println("----------------------爆料入库--------------");
    }

    /**
     * 获取SQL语句
     *
     * @return SQL语句
     * @throws E5Exception
     */
    @Override
    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID, Tenant.DEFAULTCODE) + " (" +
                " SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                " SYS_LASTMODIFIED, SYS_CURRENTFLOW,SYS_ISLOCKED,SYS_CURRENTNODE,SYS_CURRENTSTATUS," +
                " SYS_TOPIC,SYS_AUTHORS, SYS_AUTHORID, a_attachments," +
                " a_siteID,a_tag,a_contactNo,a_content,a_isSensitive," +
                " a_longitude,a_latitude,a_location,a_sourceType" +
                " )VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)";
    }

    /**
     * 入库后 更新Redis中相关的key
     *
     * @param obj
     */
    @Override
    void updateKeys(JSONObject obj) {
        RedisManager.clear(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,obj.getInt("siteID")) + obj.getInt("userID"));
    }
}

class FeedBackDelay extends NisDelay {

    FeedBackDelay() {
        super(RedisKey.APP_DELAY_FEEDBACK_KEY, DocTypes.FEEDBACK.typeID());
    }

    void init(JSONObject obj, int docTypeID) throws E5Exception {
        super.init(obj, docTypeID);
        super.saveAttachment(obj);
    }

    /**
     * 添加SQL参数
     *
     * @param startID 起始DocID
     * @param ps      预执行语句
     * @param obj     互动内容JSON
     * @throws SQLException
     */
    @Override
    protected void addBatch(long startID, PreparedStatement ps, JSONObject obj) throws SQLException {
        int index = 1;
        ps.setLong(index++, startID);
        ps.setInt(index++, obj.getInt("SYS_DOCLIBID"));
        ps.setInt(index++, obj.getInt("SYS_FOLDERID"));
        ps.setInt(index++, obj.getInt("SYS_DELETEFLAG"));
        ps.setTimestamp(index++, Timestamp.valueOf(obj.getString("SYS_CREATED")));

        ps.setString(index++, StringUtils.getNotNull(obj.getString("userName")));
        //作者ID
        ps.setLong(index++, obj.getLong("userID"));
        ps.setString(index++, obj.getString("a_attachments"));
        ps.setInt(index++, obj.getInt("siteID"));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("content")));
        ps.setString(index++, StringUtils.getNotNull(obj.getString("phone")));
        ps.setString(index, StringUtils.getNotNull(obj.getString("email")));
        
        ps.addBatch(); //把一条数据加进本批待提交的列表中。
    }

    /**
     * 获取SQL语句
     *
     * @return SQL语句
     * @throws E5Exception
     */
    @Override
    protected String getSql() throws E5Exception {
        //一行5个
        return "INSERT INTO " + LibHelper.getLibTable(docTypeID, Tenant.DEFAULTCODE) + " (" +
                " SYS_DOCUMENTID,SYS_DOCLIBID,SYS_FOLDERID,SYS_DELETEFLAG,SYS_CREATED," +
                "  SYS_AUTHORS, SYS_AUTHORID,a_attachments,feed_siteID,feed_content," +
                "  feed_phone,feed_email" +
                "  )VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?)";
    }

    /**
     * 入库后 更新Redis中相关的key
     *
     * @param obj
     */
    @Override
    void updateKeys(JSONObject obj) {}
}