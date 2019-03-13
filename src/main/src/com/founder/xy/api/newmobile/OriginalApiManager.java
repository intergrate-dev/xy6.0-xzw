package com.founder.xy.api.newmobile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.*;
import com.founder.e5.context.DAOHelper;
import com.founder.e5.doc.*;
import com.founder.e5.dom.DocType;
import com.founder.e5.flow.*;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.workspace.ProcHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.imp.ArticleDetailHelper;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.Original;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.OriginalColumnManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteUserReader;

import javax.servlet.http.HttpServletRequest;

/**
 * @author JiangYu 2018.03.20
 */
@Service
public class OriginalApiManager {

    @Autowired
    private SiteUserReader siteUserReader;
    @Autowired
    private ColumnManager colManager;
    @Autowired
    private ArticleManager articleManager;
    @Autowired
    private PermissionManager permissionManager;
    @Autowired
    private OriginalColumnManager originalColumnManager;
    @Autowired
    WeixinApiManager weixinApiManager;

    public static final int ALIST_COUNT = 40; // 一般列表一次获取的数量

    /**
     * 审核通过  或  驳回
     *
     * @param data
     * @param userID
     * @param pass   true:通过  false:驳回
     * @return
     */
    public String transfer(String data, int userID, boolean pass) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        long docID = jsonObject.getLong("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        String detail = jsonObject.optString("detail", "");
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
        FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
        try {
            Document doc = docManager.get(docLibID, docID);
            if (doc != null && (!doc.isLocked() || doc.getCurrentUserID() == userID)) {
                int userLibID = LibHelper.getUserExtLibID();
                long catID = doc.getLong("a_catID");
                Role[] roles = getRolesBySite(userLibID, userID, siteID);
                boolean canThrough = false; // 是否有权限
                int throughID = 0;
                int rejectedID = 0;
                String operation = null;
                //取当前流程节点下的所有操作按钮 拿到审核通过和驳回的
                FlowNode currNode = flowReader.getFlowNode(doc.getCurrentNode());
                ProcReader procReader = (ProcReader) Context.getBean(ProcReader.class);
                ProcFlow[] procs = procReader.getProcs(currNode.getID());
                for (ProcFlow proc : procs) {
                    Operation op = procReader.getOperation(proc.getOpID());
                    if (pass && op.getName().equals("审核通过")) {
                        throughID = proc.getProcID();
                        operation = proc.getProcName();
                    }
                    if (!pass && op.getName().equals("驳回")) {
                        rejectedID = proc.getProcID();
                        operation = proc.getProcName();
                    }
                }
                if (pass && throughID == 0) {
                    result.put("errorInfo", "当前流程不存在“审核通过”功能！");
                    return result.toString();
                }
                if (!pass && rejectedID == 0) {
                    result.put("errorInfo", "当前流程不存在“驳回”功能！");
                    return result.toString();
                }
                //取用户在当前栏目下的所有操作按钮
                int value = pass ? throughID : rejectedID;
                for (Role role : roles) {
                    String procStr = getProcs(role.getRoleID(), catID + "OriginalFlow" + siteID);
                    String[] powerIds = procStr.split(",");
                    for (int i = 0; i < powerIds.length; i++) {
                        if (value == Integer.valueOf(powerIds[i])) {
                            canThrough = true;
                            break;
                        }
                    }
                    if (canThrough) break;
                }
                if (canThrough) {
                    FlowNode[] nodes = flowReader.getFlowNodes(currNode.getFlowID());
                    //通过获取下一个节点，驳回获取第一个流程
                    FlowNode nextNode = pass ? flowReader.getNextFlowNode(doc.getCurrentNode()) : nodes[0];
                    doc.setCurrentFlow(nextNode.getFlowID());
                    doc.setCurrentNode(nextNode.getID());
                    doc.setCurrentStatus(nextNode.getWaitingStatus());
                    doc.setCurrentUserID(user.getUserID());
                    doc.setCurrentUserName(user.getUserName());
                    doc.setLastmodified(new Timestamp((new Date()).getTime()));

                    //根据当前流程节点，获取稿件状态和流程操作
                    if (pass) { //true:通过  false:驳回
                        if (nextNode.getName() == nodes[nodes.length - 1].getName()) {
                            doc.set("a_status", Original.STATUS_PUBNOT);
                        } else {
                            doc.set("a_status", Original.STATUS_PASSFIRST);
                        }
                    } else {
                        doc.set("a_status", Original.STATUS_REJECTED);
                    }
                    doc.setLocked(false);
                    //保存修改后的稿件
                    docManager.save(doc);

                    //记录流程记录
//                    LogHelper.writeLog(docLibID, docID, user.getUserName(), user.getUserID(), operation, detail);
                    writeLog(doc,docLibID, docID, user.getUserName(), user.getUserID(), operation, detail);
                    //记录历史版本
                    articleManager.recordHistoryVersion(docManager, doc,
                            Tenant.DEFAULTCODE, pass ? "审核通过" : "驳回", user.getUserName());
                } else {
                    result.put("errorInfo", "没有权限。");
                    return result.toString();
                }
            } else {
                result.put("errorInfo", "操作失败！可能是别人正在处理，或已删除。");
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("success", true);
        result.put("errorInfo", "");
        return result.toString();
    }

    /**
     * 操作日志
     * @param docLibID
     * @param docID
     * @param userName
     * @param userID
     * @param operation
     * @param detail
     */
    public void writeLog(Document doc,int docLibID, long docID, String userName, int userID, String operation, String detail) {
        FlowRecord log = new FlowRecord();
        FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
        int flowNodeID = doc.getCurrentNode();
        FlowNode flowNode = null;
        try {
            flowNode = (flowNodeID > 0) ? flowReader.getFlowNode(flowNodeID) : null;
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        int curNode = doc.getCurrentFlow();
        if (curNode < 0) {
            curNode = 0;
        }
        if (flowNode != null){
            log.setFromPosition(flowNode.getName()); 	//上一个流程节点的名称
            log.setLastFlowNode(flowNode.getID()); 		//上一个流程节点的ID
        }else {
            log.setLastFlowNode(0);
        }
        log.setCurrentFlowNode(curNode);
        log.setFromPosition("");
        log.setToPosition("");
        log.setEndTime(DateUtils.getTimestamp());
        log.setStartTime(log.getEndTime());

        log.setOperator(userName);
        log.setOperatorID(userID);

        log.setDetail(detail);

        log.setOperation(operation);

        try {
            FlowRecordManager logManager = FlowRecordManagerFactory.getInstance();
            logManager.createFlowRecord(docLibID, docID, log );
        } catch (E5Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 驳回到一审
     *
     * @param data
     * @param userID
     * @param pass   true:通过  false:驳回
     * @return
     */
    public String rejectOriginalFir(String data, int userID, boolean pass) {

        JSONObject jsonObject = JSONObject.fromObject(data);
        long docID = jsonObject.getLong("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        String detail = jsonObject.optString("detail", "");
        int siteID = jsonObject.getInt("siteID");

        JSONObject result = new JSONObject();

        UserReader userReader = (UserReader) Context.getBean(UserReader.class);
        //获取用户，用来记录流程记录
        User user;
        try {
            user = userReader.getUserByID(userID);
        } catch (E5Exception e1) {
            result.put("errorInfo", "获取用户信息失败！");
            return result.toString();
        }

        Document doc= null;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            doc = docManager.get(docLibID,docID);
            if (doc != null && (!doc.isLocked() || doc.getCurrentUserID() == userID)) {
                FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
                //取当前流程节点下的所有操作按钮 拿到审核通过和驳回的
                FlowNode currNode = flowReader.getFlowNode(doc.getCurrentNode());
                FlowNode[] nodes = flowReader.getFlowNodes(currNode.getFlowID());
                FlowNode nextNode = nodes[1];
                doc.setCurrentFlow(nextNode.getFlowID());
                doc.setCurrentNode(nextNode.getID());
                doc.setCurrentStatus(nextNode.getWaitingStatus());
                doc.setCurrentUserID(user.getUserID());
                doc.setCurrentUserName(user.getUserName());
                doc.setLastmodified(new Timestamp((new Date()).getTime()));
                doc.set("a_status", Original.STATUS_AUDITING);
                doc.setLocked(false);
                //保存修改后的稿件
                docManager.save(doc);

                //记录流程记录
                writeLog(doc,docLibID, docID, user.getUserName(), user.getUserID(), "二审驳回到一审", detail);
                //记录历史版本
                articleManager.recordHistoryVersion(docManager, doc,
                        Tenant.DEFAULTCODE, "待审核", user.getUserName());
            }else {
                result.put("errorInfo", "操作失败！可能是别人正在处理，或已删除。");
                return result.toString();
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            result.put("errorInfo", "操作异常");
            result.put("success", false);
        }
        result.put("success", true);
        result.put("errorInfo", "");

        return result.toString();
    }


    public String getProcs(int roleID, String originalType) throws Exception {
        Permission[] permission = permissionManager.getPermissions(roleID, originalType);
        String resource = permission == null ? "" : permission[0].getResource();
        return resource;
    }

    private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
        int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
        if (roleIDs == null || roleIDs.length == 0)
            return null;

        Role[] roles = new Role[roleIDs.length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = new Role();
            roles[i].setRoleID(roleIDs[i]);
        }
        return roles;
    }

    /**
     * 源稿栏目列表
     *
     * @param data
     * @param userID
     * @return
     */
    public String getOriAuditCats(String data, int userID) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int siteID = jsonObject.getInt("siteID");
        int parentID = jsonObject.getInt("parentID");
        String tCode = Tenant.DEFAULTCODE;
        JSONObject ret = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            String nodeAudiByUserID = getNodeAudiByUserID(userID, siteID, tCode);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] documents = docManager.find(DocTypes.ORIGINAL.typeID(), "SYS_CURRENTNODE IN(?) AND a_status not in (0,5,4)", new Object[]{nodeAudiByUserID});

            ArrayList<Long> associatedFRs = weixinApiManager.getAssociatedFRs(userID, tCode);


            if((documents==null || (documents!=null && documents.length==0)) &&(associatedFRs == null ||(associatedFRs!=null && associatedFRs.size()==0)) ){
                ret.put("success", "false");
                ret.put("errorInfo", "");
                ret.put("results", "");
                return ret.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document[] colList = null;
        try {
            int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), Tenant.DEFAULTCODE);
            if (parentID == 0) {
                colList = originalColumnManager.getRoot(colLibID, siteID);
            } else {
                colList = colManager.getSub(colLibID, parentID);
            }

            int userLibID = LibHelper.getUserExtLibID();
            Role[] roles = getRolesBySite(userLibID, userID, siteID);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < roles.length; i++) {
                Permission[] permission = permissionManager.getPermissions(roles[i].getRoleID(),
                        "OriginalColumn" + siteID);
                if (permission == null) continue;
                sb.append(permission[0].getResource());
                String roleColStr = sb.toString();
                if (!roleColStr.endsWith(",")) sb.append(",");
                if (roleColStr.length() > 0) {
                    String[] roleColIDs = roleColStr.split(",");
                    for (String idstr : roleColIDs) {
                        //Document[] subCats = colManager.getSub(colLibID, Long.valueOf(idstr));
                        List<Document> children = iterateColumnIds(Long.valueOf(idstr), colLibID, null);
                        for (Document document : children) {
                            if (document.getInt("col_flow_ID") > 1) {
                                sb.append(document.getDocID());
                                sb.append(",");
                            }
                        }
                    }
                }
            }
            long[] roleColIDs = StringUtils.getLongArray(sb.toString(), ",");

            filter(colLibID, jsonArray, colList, roleColIDs);

			/*
			Document[] colList = originalColumnManager.getAuditCols(colLibID, siteID, parentID);
			int userLibID = LibHelper.getUserExtLibID();
			Role[] roles = getRolesBySite(userLibID, userID, siteID);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < roles.length; i++) {
				Permission[] permission = permissionManager.getPermissions(roles[i].getRoleID(),
						"OriginalColumn" + siteID);
				if (permission == null) continue;
				sb.append(permission[0].getResource());
				String roleColStr = sb.toString();
				if(roleColStr.length() > 0 && !roleColStr.endsWith(",") && i != roles.length-1){
					sb.append(",");
				}
			}
			long[] roleColIDs = StringUtils.getLongArray(sb.toString(),",");
			filter(jsonArray, colList, roleColIDs);
			*/
            ret.put("success", true);
            ret.put("errorInfo", "");
            ret.put("results", jsonArray);
            return ret.toString();
        } catch (E5Exception e) {
            e.printStackTrace();
            ret.put("success", false);
            ret.put("errorInfo", "获取失败");
            return ret.toString();
        }
    }

    private void filter(int colLibID, JSONArray jsonArray, Document[] colList,
                        long[] ids) throws E5Exception {
        for (Document col : colList) {
            // 栏目的级联子栏目ID。任意一级有权限, 就返回该栏目。
            //int[] cascades = StringUtils.getIntArray(col.getString("col_cascadeID"), "~");
            List<Document> children = new ArrayList<>();
            children.add(col);
            children = iterateColumnIds(col.getDocID(), colLibID, children);
            for (int i = 0; i < children.size(); i++) {
                Long currentChild = children.get(i).getDocID();
                if (ArrayUtils.contains(ids, currentChild)) {
                    Document cat = colManager.get(colLibID, currentChild);
                    if (cat.getInt("col_flow_ID") <= 1) continue;//过滤无审批流程的栏目
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("columnID", col.getDocID());
                    jsonObject.put("columnName", col.getString("col_name"));
                    jsonObject.put("cascadeName", col.getString("col_cascadeName"));
                    jsonObject.put("isParent", col.getLong("col_parentID") == 0 ? true : false);
                    jsonObject.put("isPower", i == 0 ? true : false);
                    jsonArray.add(jsonObject);
                    break;
                }
            }
        }
    }

    //迭代子孙栏目ID
    public List<Document> iterateColumnIds(long id, int colLibID, List<Document> idList) throws E5Exception {
        if (idList == null) idList = new ArrayList<>();
        Document[] subCats = colManager.getSub(colLibID, id);
        if (subCats != null) {
            for (Document cat : subCats) {
                idList.add(cat);
                iterateColumnIds(cat.getDocID(), colLibID, idList);
            }
        }
        return idList;
    }

    /**
     * 源稿稿件列表
     *
     * @param data
     * @param userID
     * @return
     */
    public String getOriginalArticles(String data, int userID,String tenantCode) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        //int siteID = jsonObject.getInt("siteID");
        int type = jsonObject.optInt("type", -1);
        int catID = jsonObject.optInt("catID", -1);
        int siteID = jsonObject.optInt("siteID", 1);
        int status = jsonObject.optInt("status", 0);
        int lastID = jsonObject.getInt("lastID");
        JSONObject ret = new JSONObject();
        //获取工号下的角色
        String tCode = Tenant.DEFAULTCODE;
        int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);
        int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);

        try {
            String nodeAudiByUserID = getNodeAudiByUserID(userID, siteID, tCode);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] documents = docManager.find(DocTypes.ORIGINAL.typeID(), "SYS_CURRENTNODE IN(?) AND a_status not in (0,5,4)", new Object[]{nodeAudiByUserID});
            if(documents==null || (documents!=null && documents.length==0)){
                ret.put("success", "false");
                ret.put("errorInfo", "");
                ret.put("results", "");
                return ret.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Integer> flowNodeIDs = new ArrayList<Integer>();
        List<Integer> flowNodeAllIDs = new ArrayList<Integer>();
        List<Integer> docIDS = new ArrayList<Integer>();
        List<Long> docIDAllS = new ArrayList<Long>();
        try {
        //遍历角色
            for(int i = 0;i<roleIDs.length;i++) {
                int roleID = roleIDs[i];
                //获取角色的栏目
                //稿件flowid
                flowNodeIDs = getDocByRole(roleID, siteID);
                if (flowNodeIDs!=null && !flowNodeIDs.isEmpty()){
                    for(int j= 0;j<flowNodeIDs.size();j++){
                        if (flowNodeAllIDs!=null && !flowNodeAllIDs.isEmpty() && flowNodeAllIDs.contains(flowNodeIDs.get(j))){
                            continue;
                        }else {
                            flowNodeAllIDs.add(flowNodeIDs.get(j));
                        }
                    }
                }
            }
            //稿件id
            docIDAllS = getDocAudiByRole(userID, siteID,tenantCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //转为string
        String docIDAllStr = docIDAllS.toString().replace("[","").replace("]","");
        String flowNodeAllIDStr = flowNodeAllIDs.toString().replace("[","").replace("]","");

        StringBuilder relWhere = new StringBuilder(" a_siteID = " + siteID + " AND SYS_DELETEFLAG=0 "+ " ");
        StringBuilder relWhere1 = new StringBuilder();
        StringBuilder relWhere2 = new StringBuilder();
        StringBuilder relWhere3 = new StringBuilder();


        if(docIDAllStr!= null && !docIDAllStr.isEmpty()){
            relWhere1.append(" AND SYS_DOCUMENTID in (").append(docIDAllStr).append(")");
        }
        if(flowNodeAllIDStr!= null && !flowNodeAllIDStr.isEmpty()){
            relWhere2.append(" AND SYS_CURRENTNODE in (").append(flowNodeAllIDStr).append(")");
        }
        if (catID != -1) {
            relWhere.append(" AND a_catID =  ").append(catID);
        }
        if (type != -1) {
            relWhere.append(" AND a_type= ").append(type);
        }
        if (lastID != 0) {
            relWhere.append(" AND SYS_DOCUMENTID < ");
            relWhere.append(lastID);
        }
//        relWhere3.append(" AND a_status not in(0,,2,3) ");
        String sqlStr = "SELECT SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,a_type,SYS_LASTMODIFIED,a_editor,a_check,SYS_CURRENTNODE,SYS_CURRENTSTATUS,"
                + "a_catID,a_picSmall,a_picMiddle,a_picBig FROM xy_original WHERE";

        String relSql = sqlStr + relWhere + relWhere1+ " union "+ sqlStr + relWhere + relWhere2 +
                " order by SYS_DOCUMENTID desc ";
        //分页
        JSONArray arr = getListOriginal(relSql, 0, ALIST_COUNT);
        StringBuilder docIDsRet = new StringBuilder();
        for (int i = 0; i<arr.size();i++) {
            JSONObject json = new JSONObject();
            JSONObject job = arr.getJSONObject(i);
            Integer fileId = (Integer)job.get("fileId");
            docIDsRet.append(fileId).append(",");
        }
        String b = ""+docIDsRet;
        if (arr == null) {
            ret.put("success", "false");
            ret.put("errorInfo", "获取失败");
            return ret.toString();
        }
        ret.put("success", "true");
        ret.put("errorInfo", "");
        ret.put("results", arr);
        ret.put("docIDsRet", b);
        return ret.toString();
    }

    private JSONArray getListOriginal(String sql, int paramBegin, int paramCount) {
        JSONArray result = new JSONArray();

        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            sql = db.getDialect().getLimitString(sql, paramBegin, paramCount);
            rs = db.executeQuery(sql, null);
            String appUrl = InfoHelper.getConfig("互动", "外网Api地址");
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("fileId", rs.getInt("SYS_DOCUMENTID"));
                json.put("docLibID", rs.getInt("SYS_DOCLIBID"));
                json.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                json.put("articleType", rs.getInt("a_type"));
                json.put("lastTime", StringUtils.getNotNull(rs.getString("SYS_LASTMODIFIED")));
                json.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
                json.put("catID", rs.getInt("a_catID"));
                int a_catID = rs.getInt("a_catID");
                int docLibID = DomHelper.getDocLibID(DocTypes.COLUMNORI.typeID());
                String catName = originalColumnManager.getCatName(rs.getInt("a_catID"),
                        DomHelper.getDocLibID(DocTypes.COLUMNORI.typeID()));
                if(catName.equals("0")){
                    continue;
                }
                json.put("catName", catName);
                json.put("check", rs.getInt("a_check"));
                json.put("currentNode", rs.getInt("SYS_CURRENTNODE"));
                json.put("currentStatus", rs.getString("SYS_CURRENTSTATUS"));
                if (rs.getString("a_picSmall") == null || "".equals(rs.getString("a_picSmall"))) {
                    json.put("picSmall", "");
                } else {
                    json.put("picSmall", appUrl + "/getImage?path=" + rs.getString("a_picSmall"));
                }

                if (rs.getString("a_picMiddle") == null || "".equals(rs.getString("a_picMiddle"))) {
                    json.put("picMiddle", "");
                } else {
                    json.put("picMiddle", appUrl + "/getImage?path=" + rs.getString("a_picMiddle"));
                }

                if (rs.getString("a_picBig") == null || "".equals(rs.getString("a_picBig"))) {
                    json.put("picBig", "");
                } else {
                    json.put("picBig", appUrl + "/getImage?path=" + rs.getString("a_picBig"));
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

    /**
     * 源稿详情页信息
     *
     * @param data
     * @param userID
     * @return
     * @throws E5Exception
     */
    public String getOriginalDetail(String data, int userID) throws E5Exception {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        JSONObject ret = originalDetail(docLibID, docID, userID);
        return ret.toString();
    }

    public String getDocId(String data, int userID) throws E5Exception {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        int isUp = jsonObject.getInt("isUp");
        String docIDsRets = jsonObject.getString("docIDsRet");
        int docIDUp = 0;
        int docIDDown = 0;
        String[] docIDsRet = docIDsRets.split(",");
        for (int i= 0;i<docIDsRet.length;i++){
            if (isUp==0&&Integer.parseInt(docIDsRet[0])==docID){
                docIDUp = 0;
                break;
            }else if(isUp==0&&Integer.parseInt(docIDsRet[i])==docID){
                docIDUp = Integer.parseInt(docIDsRet[--i]);
                break;
            }else if (isUp==1&&Integer.parseInt(docIDsRet[docIDsRet.length-1])==docID){
                docIDDown = 0;
                break;
            }else if (isUp==1&&Integer.parseInt(docIDsRet[i])==docID){
                docIDDown = Integer.parseInt(docIDsRet[++i]);
                break;
            }
        }
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long docID1 = 0L;
        Document documents;
        if (isUp == 0) {
//            上
            docID1 = docIDUp;
        } else {
//            下
            docID1 = docIDDown;
        }

        JSONObject ret = originalDetail(docLibID,docID1, userID);
//        ret.put("docIDsRets",docIDsRets);
        return ret.toString();
    }


    public JSONObject originalDetail(int docLibID, long docID, int userID) {

        JSONObject result = new JSONObject();
        JSONObject article = new JSONObject();
        JSONArray operations = new JSONArray();
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            if (doc == null) {
                result.put("success", false);
                result.put("errorInfo", "稿件不存在！");
                result.put("article", article);
                result.put("operation", operations);
            } else {
                JSONArray imageArray = new JSONArray();
                JSONArray videoArray = new JSONArray();

                int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), Tenant.DEFAULTCODE);
                String catName = originalColumnManager.getCatName(doc.getLong("A_CATID"), colLibID);
                // 设置基础属性
                article.put("catName", catName);
                setBasicField(article, doc);

                // 读出附件表的附件
                Document[] atts = readAtts(doc);
                // 处理内容
                setContent(article, doc); // ?
                // 图片
                setImageJsonArray(imageArray, atts); // ?
                // 视频
                setVideoJsonArray(videoArray, atts); // ?
                // 操作权限
                setOperationJsonArray(operations, doc, userID);

                article.put("imageArray", imageArray);
                article.put("videoArray", videoArray);
                result.put("success", true);
                result.put("errorInfo", "");
                result.put("article", article);
                result.put("operation", operations);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorInfo", e.getMessage());
            result.put("article", article);
            result.put("operation", operations);
        }
        return result;
    }

    /**
     * 设置基本属性
     */
    private void setBasicField(JSONObject article, Document doc) {
        article.put("fileId", doc.getDocID());// 稿件ID
        article.put("docLibID", doc.getDocLibID());// 稿件库ID
        article.put("title", doc.getTopic());
        article.put("articleType", doc.getInt("A_TYPE"));
        article.put("status", doc.getInt("A_STATUS"));
        article.put("siteID", doc.getInt("A_SITEID"));
        article.put("lastTime", ArticleDetailHelper.getDateString(doc.getLastmodified()));
        article.put("editor", doc.getString("A_EDITOR"));
        article.put("author", doc.getAuthors());
        article.put("wordCount", doc.getInt("A_WORDCOUNT"));
        article.put("linkID", 0);
        article.put("subtitle", doc.getString("A_SUBTITLE"));
        article.put("longitude", doc.getDouble("A_LONGITUDE"));
        article.put("latitude", doc.getDouble("A_LATITUDE"));
        article.put("location", getNotNull(doc.getString("A_LOCATION")));
        article.put("catID", doc.getLong("A_CATID"));
        article.put("url", doc.getString("A_URL"));
        article.put("abstract", doc.getString("A_ABSTRACT"));
        article.put("picSmall", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICSMALL")));
        article.put("picMiddle", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICMIDDLE")));
        article.put("picBig", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICBIG")));
    }

    private static Document[] readAtts(Document doc) throws E5Exception {
        int attTypeID = (doc.getDocTypeID() == DocTypes.ORIGINAL.typeID()) ? DocTypes.ATTACHMENT
                .typeID() : DocTypes.PAPERATTACHMENT.typeID();
        int attDocLibID = LibHelper.getLibIDByOtherLib(attTypeID,
                doc.getDocLibID());
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] atts = docManager.find(attDocLibID,
                "att_articleID=? and att_articleLibID=? ORDER BY att_order",
                new Object[]{doc.getDocID(), doc.getDocLibID()});
        return atts;
    }

    // 处理内容
    private static void setContent(JSONObject article, Document doc)
            throws E5Exception {
        String content = doc.getString("A_CONTENT");
        // 按稿件类型处理内容
        int type = doc.getInt("A_TYPE");
        if (type == 0) { // 文章稿
            String url = getWebRoot() + "getImage";
            //String regex = "\\.\\.\\/\\.\\.\\/xy\\/image\\.do";
            String regex = "../../xy/image.do";
            content = content.replaceAll(regex, url);
        }
        article.put("content", content); // 内容
    }

    private static void setImageJsonArray(JSONArray imageArray, Document[] atts) {
        Document[] docs = getAttsByType(atts, 0); // 图片
        if (docs != null && docs.length > 0) {
            for (Document doc : docs) {
                JSONObject image = new JSONObject();
                image.put("path", ArticleDetailHelper.getWanPicPath(doc.getString("ATT_PATH")));
                image.put("content", getNotNull(doc.getString("ATT_CONTENT")));
                imageArray.add(image);
            }
        }
    }

    private static void setVideoJsonArray(JSONArray videoArray, Document[] atts) {
        Document[] docs = getAttsByType(atts, 1); // 视频
        if (docs != null && docs.length > 0) {
            for (Document doc : docs) {
                JSONObject video = new JSONObject();
                video.put("picPath", ArticleDetailHelper.getWanPicPath(doc.getString("ATT_PICPATH")));
                video.put("url", getNotNull(doc.getString("ATT_URL")));
                video.put("urlPad", getNotNull(doc.getString("ATT_URLPAD")));
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

    private void setOperationJsonArray(JSONArray operations, Document doc, int userID) throws Exception {
        int userLibID = LibHelper.getUserExtLibID();
        int siteID = doc.getInt("A_SITEID");
        if (siteID < 1) {
            siteID = 1;
        }
        //SiteUserReader siteRoleReader = (SiteUserReader) Context.getBean("siteUserReader");
        int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
        FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
        int throughID = 0;
        int rejectedID = 0;
        int rejectedFirID = 0;
        boolean canThrough = false;
        boolean canRejected = false;
        boolean canRejectedFir = false;
        FlowNode currNode = flowReader.getFlowNode(doc.getCurrentNode());
        ProcReader procReader = (ProcReader) Context.getBean(ProcReader.class);
        ProcFlow[] procs = procReader.getProcs(currNode.getID());
        for (ProcFlow proc : procs) {
            Operation op = procReader.getOperation(proc.getOpID());
            if (op.getName().equals("审核通过")) {
                throughID = proc.getProcID();
            }
            if (op.getName().equals("驳回")) {
                rejectedID = proc.getProcID();
            }
            if (op.getName().equals("驳回到一审")) {
                rejectedFirID = proc.getProcID();
            }
        }
        JSONObject params = new JSONObject();
        params.put("code", "record");
        params.put("name", "流程记录");
        operations.add(params);
        if (throughID == 0 && rejectedID == 0 && rejectedFirID == 0) return;
        String catID = doc.getString("a_catID");
        for (int roleID : roleIDs) {
            String procStr = this.getProcs(roleID, catID + "OriginalFlow" + siteID);
            String[] powerIds = procStr.split(",");
            for (int i = 0; i < powerIds.length; i++) {
                if (!canThrough && throughID > 0) {
                    if (throughID == Integer.valueOf(powerIds[i])) {
                        params.put("code", "censorship");
                        params.put("name", "审核通过");
                        operations.add(params);
                        canThrough = true;
                        continue;
                    }
                }
                if (!canRejected && rejectedID > 0) {
                    if (rejectedID == Integer.valueOf(powerIds[i])) {
                        params.put("code", "reject");
                        params.put("name", "驳回");
                        operations.add(params);
                        canRejected = true;
                        continue;
                    }
                }
                if (!canRejectedFir && rejectedFirID > 0) {
                    if (rejectedFirID == Integer.valueOf(powerIds[i])) {
                        params.put("code", "rejectFir");
                        params.put("name", "驳回到一审");
                        operations.add(params);
                        canRejectedFir = true;
                        continue;
                    }
                }
                if (canThrough && canRejected && canRejectedFir) break;
            }
            if (canThrough && canRejected && canRejectedFir) break;
        }
    }

    private static String getNotNull(String value) {
        if (value == null)
            return "";
        return value;
    }

    private static String getWebRoot() {
        return InfoHelper.getConfig("互动", "外网Api地址");
    }
    /**
     * 通过角色获取所有有权限的栏目下的有操作权限的(除了草稿状态)稿件
     */

    public List<Integer> getDocByRole(int roleID,int siteID) throws Exception{

        Permission[] permissions = permissionManager.find(roleID, "%OriginalFlow" + siteID);
        //获取role下的所有操作id
        ArrayList<String> resourceAll = new ArrayList<String> ();
        ArrayList<Integer> flowNodeIDs = new ArrayList<Integer> ();
//        ArrayList<Document> documentAll = new ArrayList<Document> ();
        for(int i = 0;i<permissions.length;i++){
            String resource = permissions[i].getResource();

            String[] res = resource.split(",");
            for (int j = 0;j<res.length;j++){
                if (resourceAll.contains(res[j])){
                    continue;
                }else {
                    resourceAll.add(res[j]);
                }
            }
        }
       //遍历re
        ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
        for (int i = 0; i < resourceAll.size(); i++) {
            int procID = Integer.parseInt(resourceAll.get(i));
            //从procs表中获取流程和流程节点
            ProcFlow proc =(ProcFlow) procReader.get(procID);
            if (proc==null){
                continue;
            }
            int flowNodeID = proc.getFlowNodeID();
            if (flowNodeIDs.contains(flowNodeID)){
                continue;
            }else {
                flowNodeIDs.add(flowNodeID);
            }
        }

        /*DocumentManager docManager = DocumentManagerFactory.getInstance();
        for (int i = 0; i<flowNodeIDs.size();i++){
            Document[] documents = docManager.find( DocTypes.ORIGINAL.typeID(), "SYS_CURRENTNODE=?",
                    new Object[]{flowNodeIDs.get(i)});
            for (Document doc: documents) {
                documentAll.add(doc);
            }
        }*/
        return flowNodeIDs;

    }

    /**
     * 获取审核过的稿件
     * @param userID
     * @param siteID
     * @return
     * @throws Exception
     */
    public List<Long> getDocAudiByRole(int userID,int siteID,String tenantCode) throws Exception {

        ArrayList<Long> docIDS = new ArrayList<Long> ();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int i1 = DocTypes.HISTORYORI.typeID();
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

    /**
     * 返回user下的有权限的操作节点
     * @param userID
     * @param siteID
     * @param tenantCode
     * @return
     * @throws Exception
     */
    public String getNodeAudiByUserID(int userID,int siteID,String tenantCode) throws Exception {
        int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tenantCode);
        int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
        List<Integer> flowNodeIDs = new ArrayList<Integer>();
        List<Integer> flowNodeAllIDs = new ArrayList<Integer>();
        List<Integer> docIDS = new ArrayList<Integer>();
        List<Long> docIDAllS = new ArrayList<Long>();
        try {
            //遍历角色
            for (int i = 0; i < roleIDs.length; i++) {
                int roleID = roleIDs[i];
                //获取角色的栏目
                //稿件flowid
                flowNodeIDs = getDocByRole(roleID, siteID);
                if (flowNodeIDs != null && !flowNodeIDs.isEmpty()) {
                    for (int j = 0; j < flowNodeIDs.size(); j++) {
                        if (flowNodeAllIDs != null && !flowNodeAllIDs.isEmpty() && flowNodeAllIDs.contains(flowNodeIDs.get(j))) {
                            continue;
                        } else {
                            flowNodeAllIDs.add(flowNodeIDs.get(j));
                        }
                    }
                }
            }
            //稿件id
        } catch (Exception e) {
            e.printStackTrace();
        }
        //转为string
        String flowNodeAllIDStr = flowNodeAllIDs.toString().replace("[", "").replace("]", "");
        return flowNodeAllIDStr;
    }
}
