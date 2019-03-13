package com.founder.xy.api.newmobile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.founder.e5.context.DBException;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.imp.ArticleDetailHelper;
import com.founder.xy.article.Original;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteUserCache;
import com.founder.xy.wx.WeixinManager;
import com.founder.xy.wx.data.Account;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class WeixinApiManager {

	@Autowired
	private WeixinManager wxManager;
	@Autowired
	OriginalApiManager originalApiManager;
    @Autowired
    private SiteUserReader siteUserReader;
    @Autowired
    SiteUserManager siteUserManager;
	
	public static final int ALIST_COUNT = 5; // 一般列表一次获取的数量
	
	/**
	 * 微信账号（公众号）列表
	 * @param data
	 * @param userID
	 * @return
	 * @throws E5Exception 
	 */
	public String getWxAccounts(String data, int userID, int userRelLibID) 
			throws E5Exception {
		JSONObject ret = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(data);
		String tCode = Tenant.DEFAULTCODE;
		int siteID = jsonObject.getInt("siteID");

		try {

            String nodeAudiByUserID = originalApiManager.getNodeAudiByUserID(userID, siteID, tCode);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] documents = docManager.find(DocTypes.ORIGINAL.typeID(), "SYS_CURRENTNODE IN(?) AND a_status not in (0,5,4)", new Object[]{nodeAudiByUserID});
            ArrayList<Long> associatedFRs = getAssociatedFRs(userID, tCode);


            if((documents==null || (documents!=null && documents.length==0)) &&(associatedFRs == null ||(associatedFRs!=null && associatedFRs.size()==0)) ){
                ret.put("success", "false");
                ret.put("errorInfo", "");
                ret.put("results", "");
                return ret.toString();
            }

			List<Account> accounts = wxManager.getAdminAccounts(Tenant.DEFAULTCODE, userID, siteID, false, false);
			JSONArray jsonArray = new JSONArray();
			for (Account account : accounts) {
				JSONObject json = new JSONObject();
				json.put("accountID", account.getId());
				json.put("accountLibID", account.getLibID());
				json.put("accountName", account.getName());
				jsonArray.add(json);
			}
			System.out.println(jsonArray.toString());
			ret.put("success",true);
			ret.put("errorInfo","");
			ret.put("results",jsonArray);
		} catch (Exception e) {
			ret.put("success",false);
            ret.put("errorInfo","获取失败");
			e.printStackTrace();
		}
		return ret.toString();
	}

	/**
	 * 微信图文组列表接口
	 * @param userID
	 * @param data
	 * @throws E5Exception
	 */
	public String getWxGroupArticles(String data, int userID) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		int siteID = jsonObject.getInt("siteID");
        int accountID = jsonObject.optInt("accountID", -1);
        int lastID = jsonObject.optInt("lastID",-1);
        String tCode = Tenant.DEFAULTCODE;
        JSONObject ret = new JSONObject();


        ArrayList<Long> associatedFRs = null;
        try {
            associatedFRs = getAssociatedFRs(userID, tCode);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        //判断user下之前的审核过的图文组--获取图文组id
        String s ="";
        if (associatedFRs != null && associatedFRs.size()!=0){
            s=associatedFRs.toString().replace("[","").replace("]","");;
        }else {
            ret.put("success","false");
            ret.put("errorInfo","");
            ret.put("results","");
            return ret.toString();
        }
        String related = null;

        //如果前端没有传值微信公众号过来
        if (accountID == -1) {
            int userRelLibID = LibHelper.getLibID(DocTypes.USERREL.typeID(), tCode);
            try {
                related = siteUserManager.getRelated(userRelLibID, userID, siteID, 10);
            } catch (E5Exception e) {
                e.printStackTrace();
                ret.put("success","false");
                ret.put("errorInfo","查询用户的公众号失败");
                ret.put("results","");
                return ret.toString();
            }
        }

        StringBuffer relWhere = new StringBuffer(" SYS_DELETEFLAG=0 AND wxg_status=1 ");
        StringBuilder relWhere1 = new StringBuilder();
        if(accountID != -1){
        	relWhere.append(" AND wxg_accountID = ").append(accountID);
        	relWhere1.append(" AND wxg_accountID = ").append(accountID);
        }else{
            relWhere.append(" AND wxg_accountid in (").append(related).append(")");
            relWhere1.append(" AND wxg_accountid in (").append(related).append(")");
        }
        if(lastID != -1){
        	relWhere.append(" AND SYS_DOCUMENTID < ");
            relWhere.append(lastID);
			relWhere1.append(" AND SYS_DOCUMENTID < ");
			relWhere1.append(lastID);
        }
        if(s!= ""){
            relWhere1.append(" AND sys_DOCUMENTID in (").append(s).append(")");
        }

        String sqlStr = "SELECT SYS_DOCUMENTID,SYS_DOCLIBID,SYS_LASTMODIFIED,SYS_CURRENTNODE "
        		+ "FROM xy_wxgroup WHERE";

//        sqlStr + relWhere  ----查询数据库中user的所有的微信账号下的待审核的稿件,如果前端传过来就只查询此公众号
//        sqlStr +" 1=1 "+ relWhere1-----查询数据库中user审核过的稿件
        String relSql = sqlStr + relWhere  /*+ " union "  + sqlStr +" 1=1 "+ relWhere1*/  +
                " order by SYS_DOCUMENTID desc ";

        JSONArray arr = getListGroup(ret, siteID, userID, relSql.toString(), 0, ALIST_COUNT);
        if(arr == null){
            ret.put("success","false");
            ret.put("errorInfo","获取失败");
            return ret.toString();
        }
        ret.put("success","true");
        ret.put("errorInfo","");
        ret.put("results",arr);
        return ret.toString();
	}

	private JSONArray getListGroup(JSONObject ret, int siteID, int userID, 
			String sql, int paramBegin, int paramCount) {
		JSONArray result = new JSONArray();
		JSONArray operations = new JSONArray();
		
        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            sql = db.getDialect().getLimitString(sql, paramBegin, paramCount);
            rs = db.executeQuery(sql, null);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                long groupID = rs.getLong("SYS_DOCUMENTID");
                int groupLibID = rs.getInt("SYS_DOCLIBID");
                json.put("groupID", groupID);
                json.put("groupLibID", groupLibID);
                json.put("lastTime", StringUtils.getNotNull(rs.getString("SYS_LASTMODIFIED")));
                json.put("articles", getGroupArticles(groupID));
                //if(rs.getInt("wxg_status") != Original.STATUS_AUDITING) return null;
                if(rs.isFirst()){
                	int nodeID = rs.getInt("SYS_CURRENTNODE");
                	setOperationJsonArray(operations, nodeID, userID, siteID);
                }
                result.add(json);
            }
            ret.put("operation", operations);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
        return result;
	}

	private void setOperationJsonArray(JSONArray operations, int nodeID, 
			int userID, int siteID) {
		int userLibID = LibHelper.getUserExtLibID();
		Role[] roles = getRolesBySite(userLibID, userID, siteID);
        try {
			//按角色个数，单个取角色ID，多个取合并后的角色ID
			int roleID = roles[0].getRoleID();
			int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
			FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
			FlowNode currNode = flowReader.getFlowNode(nodeID);
			ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
			ProcFlow[] procs = procReader.getProcs(currNode.getID());
			FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
			JSONObject params = new JSONObject();
			params.put("code", "record");
			params.put("name", "流程记录");
			operations.add(params);
			for(ProcFlow proc : procs){
				Operation op = procReader.getOperation(proc.getOpID());
				if(op.getName().equals("审核通过")) {
					boolean isCensorshipThrough = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
							currNode.getID(), proc.getProcName());
					if(isCensorshipThrough){
						params.put("code", "censorship");
						params.put("name", "审核通过");
						operations.add(params);
					}
				}else if(op.getName().equals("驳回")) {
					boolean isReback = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
							currNode.getID(), proc.getProcName());
					if(isReback){
						params.put("code", "reject");
						params.put("name", "驳回");
						operations.add(params);
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private JSONArray getGroupArticles(long groupID) {
		JSONArray result = new JSONArray();
		String sql = "SELECT * FROM xy_wxgrouparticle WHERE wx_groupID = " + groupID;
		DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            rs = db.executeQuery(sql);
            String appUrl = InfoHelper.getConfig("互动", "外网Api地址");
            while (rs.next()) {
            	JSONObject json = new JSONObject();
            	json.put("articleID", rs.getLong("SYS_DOCUMENTID"));
                json.put("articleLibID", rs.getInt("SYS_DOCLIBID"));
            	json.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
            	json.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
            	json.put("lastTime", StringUtils.getNotNull(rs.getString("SYS_LASTMODIFIED")));
                json.put("url", StringUtils.getNotNull(rs.getString("wx_url")));
                json.put("status", rs.getInt("wx_status"));
                json.put("abstract", StringUtils.getNotNull(rs.getString("wx_abstract")));
                json.put("content", getContent(StringUtils.
                		getNotNull(rs.getString("wx_content")), appUrl));
                if(rs.getString("wx_pic") == null || "".equals(rs.getString("wx_pic"))){
                	json.put("pic", "");
                }else{
                	json.put("pic", appUrl+"/getImage?path="+rs.getString("wx_pic"));
                }
                result.add(json);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        ResourceMgr.closeQuietly(rs);
	        ResourceMgr.closeQuietly(db);
	    }
	    return result;
	}

	// 处理内容
	private String getContent(String content, String appUrl)
			throws E5Exception {
		String url = appUrl + "getImage";
		String regex = "../../xy/image.do";
		content = content.replaceAll(regex, url);
		return content;
	}

	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		int[] roleIDs =  siteUserCache.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}

	/**
	 * 微信图文组 审核全部通过\全部驳回 接口
	 * @param pass  true:通过  false:驳回
	 * @param userID
	 * @param data
	 * @throws Exception
	 */
	public String transfer(String data, int userID, boolean pass) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		long docID = jsonObject.getLong("groupID");
		int docLibID = jsonObject.getInt("groupLibID");
		String detail = jsonObject.optString("detail","");
		int siteID = jsonObject.getInt("siteID");
		
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		JSONObject result = new JSONObject();
		result.put("fileId", docID);
		result.put("success", false);
		//获取用户，用来记录流程记录
		User user;
		try {
			user = userReader.getUserByID(userID);
		} catch (E5Exception e1) {
			result.put("errorInfo", "获取用户信息失败！");
			return result.toString();
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
        	Document group = docManager.get(docLibID, docID);
        	if(group != null && (!group.isLocked() || group.getCurrentUserID()==userID)){
            	if(group.getInt("wxg_status") != Original.STATUS_AUDITING){
            		result.put("errorInfo", "当前图文组不是待审核状态！");
            		return result.toString();
            	}
            	boolean isPower = isHavePower(group, userID, siteID, pass);
    			if(!isPower) {
    				result.put("errorInfo", "没有权限！");
    				return result.toString();
    			} 
    			Document[] groupArticles = wxManager.getArticles(docLibID, docID);
    			group.setCurrentUserID(user.getUserID());
    			group.setCurrentUserName(user.getUserName());
    			int status = pass?Original.STATUS_PUBNOT : Original.STATUS_REJECTED;
    			wxManager.setAllStatus(docManager, groupArticles, status);
    			group.set("wxg_status", status);
    			wxManager.setNextFlow(group, pass?1:2);
    			docManager.save(group);
    			LogHelper.writeLog(docLibID, docID, user.getUserName(), user.getUserID(), 
    					pass?"全部通过":"全部驳回", detail);
        	}else{
        		result.put("errorInfo", "操作失败！可能是别人正在处理，或已删除。");
				return result.toString();
        	}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
        result.put("success", true);
		result.put("errorInfo", "");
		return result.toString();
	}

	/**
	 * 判断是否有审核通过或驳回权限
	 * @throws E5Exception 
	 */
	private boolean isHavePower(Document group, int userID, int siteID, boolean pass) 
			throws E5Exception {
		boolean isPower = false;
		Role[] roles = getRolesBySite(LibHelper.getUserExtLibID(), userID, siteID);
		//按角色个数，单个取角色ID，多个取合并后的角色ID
		int roleID = roles[0].getRoleID();
		int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
		
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		FlowNode currNode = flowReader.getFlowNode(group.getCurrentNode());
		ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
		ProcFlow[] procs = procReader.getProcs(currNode.getID());
		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		String operation = pass?"审核通过":"驳回";
		for(ProcFlow proc : procs){
			Operation op = procReader.getOperation(proc.getOpID());
			if(op.getName().equals(operation)) {
				isPower = fpReader.hasPermission(newRoleID, currNode.getFlowID(), 
						currNode.getID(), proc.getProcName());
				break;
			}
		}
		return isPower;
	}

	/**
	 * 微信图文组中 单篇稿件 审核通过\驳回 接口
	 * @param pass  true:通过  false:驳回
	 * @return
	 */
	public String transferOne(String data, int userID, boolean pass) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		long groupID = jsonObject.getLong("groupID");
		int groupLibID = jsonObject.getInt("groupLibID");
		long articleID = jsonObject.getLong("articleID");
		int articleLibID = jsonObject.getInt("articleLibID");
		String detail = jsonObject.optString("detail","");
		int siteID = jsonObject.getInt("siteID");
		
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		JSONObject result = new JSONObject();
		result.put("fileId", articleID);
		result.put("success", false);
		//获取用户，用来记录流程记录
		User user;
		try {
			user = userReader.getUserByID(userID);
		} catch (E5Exception e1) {
			result.put("errorInfo", "获取用户信息失败！");
			return result.toString();
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
        	Document group = docManager.get(groupLibID, groupID);
        	Document article = docManager.get(articleLibID, articleID);
        	if(group != null && (!group.isLocked() || group.getCurrentUserID()==userID)){
            	if(group.getInt("wxg_status") != Original.STATUS_AUDITING){
            		result.put("errorInfo", "当前图文组不是待审核状态！");
            		return result.toString();
            	}
            	if(article.getInt("wx_status") != Original.STATUS_AUDITING){
            		result.put("errorInfo", "当前稿件不是待审核状态！");
            		return result.toString();
            	}
            	boolean isPower = isHavePower(group, userID, siteID, pass);
    			if(!isPower) {
    				result.put("errorInfo", "没有权限！");
    				return result.toString();
    			} 
    			int status = pass?Original.STATUS_PUBNOT : Original.STATUS_REJECTED;
    			article.set("wx_status", status);
    			//更新稿件的状态
    			docManager.save(article);
    			//获取图文组内的稿件
    			Document[] groupArticles = wxManager.getArticles(groupLibID, groupID);
    			boolean isAllpub = wxManager.isAllSameStatus(groupArticles, status);
//    			if(isAllpub){//如果当前图文组中的稿件全部为同一状态  将这个图文组设置对应的流程
				if(status==Original.STATUS_REJECTED || isAllpub) {
                    //如果驳回某一篇稿件,则图文组的状态 为驳回
                    group.set("wxg_status", status);
                    //审核某一篇稿件,如果图文组内的稿件状态不同,则不用修改,如果同一状态,则修改为审核通过

                    group.setCurrentUserID(user.getUserID());
                    group.setCurrentUserName(user.getUserName());
                    wxManager.setNextFlow(group, pass ? 1 : 2);
                    docManager.save(group);
                    LogHelper.writeLog(articleLibID, articleID, user.getUserName(), user.getUserID(),
                            pass ? "审核通过" : "驳回", detail);
                    LogHelper.writeLog(groupLibID, groupID, user.getUserName(), user.getUserID(),
                            pass ? "审核通过" : "驳回", detail);
                }
        	}else{
        		result.put("errorInfo", "操作失败！可能是别人正在处理，或已删除。");
				return result.toString();
        	}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
        result.put("success", true);
		result.put("errorInfo", "");
		return result.toString();
	}
	
	/**
	 * 微信图文详情
	 */
	public String getWxGroupArticleDetail(String data, int userID) {
		JSONObject jsonObject = JSONObject.fromObject(data);
        long docID = jsonObject.getLong("docID");
        int docLibID = jsonObject.getInt("docLibID");
        JSONObject ret = articleDetail(docLibID, docID, userID);
        return ret.toString();
	}
	/**
	 * 微信图文详情-上一篇下一篇
	 */
	public String getWxGroupArticleDetailUPOrDown(String data, int userID) {
		JSONObject jsonObject = JSONObject.fromObject(data);
        long docID = jsonObject.getLong("docID");
        int docLibID = jsonObject.getInt("docLibID");
        int isUp = jsonObject.getInt("isUp");
		List<String> docIDAllList = Arrays.asList(jsonObject.getString("docIDAll").split(","));
        for (int i = 0;i<docIDAllList.size();i++){
            if (Long.parseLong(docIDAllList.get(i))==docID){
                if (isUp == 0 && i==0){
                    //上一篇
                    docID = 0L;
                }else if(isUp == 0 && i != 0){
                    docID = Long.parseLong(docIDAllList.get(--i));
                }else if (isUp == 1 && i == docIDAllList.size()-1){
                    docID=0L;
                }else {
                    docID = Long.parseLong(docIDAllList.get(++i));
                }
            }
        }
		JSONObject ret = articleDetail(docLibID, docID, userID);
        return ret.toString();
	}

	private JSONObject articleDetail(int docLibID, long docID, int userID) {
		JSONObject result = new JSONObject();
		JSONObject article = new JSONObject();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			String appUrl = InfoHelper.getConfig("互动", "外网Api地址");
			if (doc == null) {
				result.put("success", false);
				result.put("errorInfo", "稿件不存在！");
				result.put("article", article);
			} else {
				JSONArray imageArray = new JSONArray();
				JSONArray videoArray = new JSONArray();
				// 读出附件表的附件
				Document[] atts = readAtts(doc);
				
				setBasicField(article, doc, appUrl);
				// 处理内容
				article.put("content", getContent(StringUtils.
		        		getNotNull(doc.getString("wx_content")), appUrl));
				// 图片
				setImageJsonArray(imageArray, atts);
				// 视频
				setVideoJsonArray(videoArray, atts);
				
				article.put("imageArray", imageArray);
				article.put("videoArray", videoArray);
				result.put("success", true);
				result.put("errorInfo", "");
				result.put("article", article);
			}
		} catch (Exception e) {
			result.put("success", false);
			result.put("errorInfo", e.getMessage());
			result.put("article", article);
		}
		return result;
	}

	private Document[] readAtts(Document doc) throws E5Exception {
		int attTypeID = (doc.getDocTypeID() == DocTypes.WXGROUPARTICLE.typeID()) ? DocTypes.ATTACHMENT
				.typeID() : DocTypes.PAPERATTACHMENT.typeID();
		int attDocLibID = LibHelper.getLibIDByOtherLib(attTypeID,
				doc.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] atts = docManager.find(attDocLibID,
				"att_articleID=? and att_articleLibID=? ORDER BY att_order",
				new Object[] { doc.getLong("wx_articleID"), doc.getInt("wx_articleLibID") });
		return atts;
	}

	private void setBasicField(JSONObject article, Document doc, String appUrl) {
		article.put("articleID", doc.getDocID());// 稿件ID
		article.put("articleLibID", doc.getDocLibID());// 稿件库ID
		article.put("title", doc.getString("SYS_TOPIC"));
		article.put("author", doc.getString("SYS_AUTHORS"));
		article.put("lastTime", doc.getLastmodified());//ArticleDetailHelper.getDateString(doc.getLastmodified())
		article.put("status", doc.getInt("wx_status"));
		article.put("url", doc.getString("wx_url"));
		article.put("abstract", StringUtils.getNotNull(doc.getString("wx_abstract")));
        if(doc.getString("wx_pic") == null || "".equals(doc.getString("wx_pic"))){
        	article.put("pic","");
        }else{
        	article.put("pic",appUrl+"/getImage?path="+doc.getString("wx_pic"));
        }
        article.put("checkID", doc.getLong("wx_articleID"));
        article.put("checkLibID", doc.getInt("wx_articleLibID"));
	}
	
	private static void setImageJsonArray(JSONArray imageArray, Document[] atts) {
		Document[] docs = getAttsByType(atts, 0); // 图片
		if(docs != null && docs.length > 0){
			for(Document doc : docs){
				JSONObject image = new JSONObject();
				image.put("path", ArticleDetailHelper.getWanPicPath(doc.getString("ATT_PATH")));
				image.put("content", StringUtils.getNotNull(doc.getString("ATT_CONTENT")));
				imageArray.add(image);
			}
		}
	}
	
	private static void setVideoJsonArray(JSONArray videoArray, Document[] atts) {
		Document[] docs = getAttsByType(atts, 1); // 视频
		if(docs != null && docs.length > 0){
			for(Document doc : docs){
				JSONObject video = new JSONObject();
				video.put("picPath", ArticleDetailHelper.getWanPicPath(doc.getString("ATT_PICPATH")));
				video.put("url", StringUtils.getNotNull(doc.getString("ATT_URL")));
				video.put("urlPad", StringUtils.getNotNull(doc.getString("ATT_URLPAD")));
				video.put("duration", doc.getInt("ATT_DURATION"));
				videoArray.add(video);
			}
		}
		
	}
	
	private static Document[] getAttsByType(Document[] atts, int type) {
		List<Document> list = new ArrayList<>();
		
		for (int i = 0; i < atts.length; i++) {
			if (atts[i].getInt("att_type") == type)
				list.add(atts[i]);
		}
		return list.toArray(new Document[0]);
	}
    /**
     * 获取审核过的图文组
     * @param userID
     * @param siteID
     * @return
     * @throws Exception
     */
    public List<Long> getWXGroupAudiByRole(int userID,int siteID,String tenantCode) throws Exception {

        ArrayList<Long> docIDS = new ArrayList<Long> ();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int attLibID = LibHelper.getLibID(DocTypes.HISTORYORI.typeID(), tenantCode);
//        6一审通过
        Document[] documents = docManager.find(attLibID, "SYS_CURRENTUSERID=? and a_siteID=? and a_status in(2,4,6)",
                new Object[]{userID, siteID});

        //遍历
        for (int i = 0; i<documents.length;i++){
            Long docID = (long)documents[i].get("a_originalID");
            if (docIDS.contains(docID)){
                continue;
            }else {
                docIDS.add(docID);
            }
        }
        return docIDS;
    }



    public ArrayList<Long> getAssociatedFRs(int userID,String tCode) throws E5Exception {
        int attLibID = LibHelper.getLibID(DocTypes.WXGROUP.typeID(), tCode);
        StringBuffer sbSQL = new StringBuffer(200);
        sbSQL.append("select distinct(DOCUMENTID) from xy_wxgroup_log where operatorid= ?  and operation not in ('新建','送审')");
        String sql = sbSQL.toString();
        ArrayList<Long> result = new ArrayList<Long>();
        Object[] params = new Object[]{new Integer(userID)};
        DBSession dbsession = E5docHelper.getFRDBSession(attLibID);
        try {
            IResultSet rs = dbsession.executeQuery(sql, params);
            while(rs.next()) {
                FlowRecord bean = new FlowRecord();
                bean.setDocLibID(attLibID);
                long aLong = rs.getLong(1);
                result.add(aLong);
            }
            rs.close();
        } catch (SQLException var16) {
            throw new DBException(var16);
        }finally {
            dbsession.closeQuietly();
        }
        return result;
    }

}
