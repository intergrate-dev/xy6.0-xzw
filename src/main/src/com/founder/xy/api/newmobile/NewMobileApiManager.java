package com.founder.xy.api.newmobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.FlowRecordManager;
import com.founder.e5.doc.FlowRecordManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.flow.Operation;
import com.founder.e5.flow.ProcFlow;
import com.founder.e5.flow.ProcReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.imp.ArticleDetailHelper;
import com.founder.xy.api.imp.ImpResult;
import com.founder.xy.api.imp.SearchArticleHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.EncodeUtils;
import com.founder.xy.commons.FilePathUtil;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.config.SubTab;
import com.founder.xy.config.Tab;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.video.VideoManager;
import com.founder.xy.workspace.MainHelper;

import VJVAS.MediaFileInfo;
import VJVAS.holders.MediaFileInfoHolder;
import localhost.VJVASPortType;
import localhost.VJVodServicePortType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by yu.feng on 2017/9/25.
 */
@Service
public class NewMobileApiManager {

    @Autowired
    private SiteUserReader siteUserReader;
    @Autowired
    private ColumnReader colReader;
    @Autowired
    private ColumnManager colManager;
    @Autowired
    private ArticleManager articleManager;
    @Autowired
    private SiteUserManager userManager;

    public static final int ALIST_COUNT = 40; // 一般列表一次获取的数量
    private static final String[] APP_TABS_ID = {"cweb","capp","crevoke","myaudit","nislive"};//"resv",视频一期app端不实现，暂时不返给app

    public String getPubArticles(int userID, String data){
        JSONObject jsonObject = JSONObject.fromObject(data);

        int siteID = jsonObject.getInt("siteID");
        int channel = jsonObject.getInt("channel");
        int type = jsonObject.optInt("type",-1);
        int status = jsonObject.optInt("status",-1);
        int columnID = jsonObject.getInt("columnID");
        int lastID = jsonObject.getInt("lastID");
        int flag = jsonObject.getInt("flag");

        JSONObject ret = new JSONObject();

        if(flag == 0){
            String tableName = "";
            String relTable = "";
            if(channel == 1){
                tableName = "xy_article ";
                relTable = "DOM_REL_Web ";
            }else if(channel == 2){
                tableName = "xy_articleApp ";
                relTable = "DOM_REL_App ";
            }else{
                ret.put("success","false");
                ret.put("errorInfo","渠道参数错误");
                return ret.toString();
            }

            StringBuffer relWhere = new StringBuffer(" CLASS_1=");
            relWhere.append(columnID);
//            relWhere.append(" AND a_siteID=").append(siteID);
            if(status != -1){
                relWhere.append(" AND a_status=").append(status);
            }
            if(type != -1){
                relWhere.append(" AND a_type=").append(type);
            }
            if(lastID != 0){
                StringBuilder queryOrder = new StringBuilder("select a_order from ");
                queryOrder.append(relTable).append(" WHERE SYS_DOCUMENTID = ").append(lastID);
                double order = getOrderbyID(queryOrder.toString());

                if(order == 0){
                    ret.put("success","false");
                    ret.put("errorInfo","最后一篇稿件ID错误");
                    return ret.toString();
                }else{
                    relWhere.append(" AND a_order > ");
                    relWhere.append(order);
                }
            }
            relWhere.append(" AND SYS_DELETEFLAG=0");

            StringBuffer relSql = new StringBuffer("select SYS_DOCUMENTID,a_linkTitle,a_order,a_priority,a_position from ");
            relSql.append(relTable);
            relSql.append(" where");
            relSql.append(relWhere);
            relSql.append(" order by a_order asc ");

            JSONArray docIDList = getListFromRel(relSql.toString(), 0, ALIST_COUNT);

            if(docIDList == null){
                ret.put("success","false");
                ret.put("errorInfo","获取失败");
                return ret.toString();
            }else if(docIDList.isEmpty()){
                ret.put("success","true");
                ret.put("errorInfo","");
                ret.put("results","[]");
                return ret.toString();
            }

            //当没有非必要查询条件时，处理固定位置稿件
            if(status == -1 && type == -1){

                //读固定位置的稿件，若没有固定位置的稿件，不必处理
                StringBuffer posSql = new StringBuffer();
                posSql.append("select SYS_DOCUMENTID,a_linkTitle,a_order,a_priority,a_position from ").append(relTable);
                posSql.append(" where a_position>0 and");
                posSql.append(relWhere);
                posSql.append(" order by a_position");
                JSONArray posList = getListFromRel(posSql.toString(), 0, 1000);

                if (posList != null && !posList.isEmpty()) {
                    //对每个固定位置的稿件进行处理
                    for (Object object : posList) {
                        JSONObject posOne = (JSONObject)object;
                        fixOnePosition(docIDList, posOne, lastID);
                    }

                    //若是加了或减了固定位稿件，则整理序号
                    for (int i = 0; i < docIDList.size(); i++) {
                        JSONObject ele = (JSONObject)docIDList.get(i);

                        int oldOrder = Integer.parseInt(ele.getString("DocOrder"));
                        int newOrder = i + 1;
                        if (oldOrder != newOrder)
                            ele.put("DocOrder", String.valueOf(newOrder));
                    }
                }
            }

            StringBuffer where = new StringBuffer(" where SYS_DOCUMENTID in (");
            for (int i = 0; i < docIDList.size(); i++) {
                if (i > 0) {
                    where.append(",");
                }

                JSONObject ele = (JSONObject)docIDList.get(i);
                where.append(ele.getString("SYS_DOCUMENTID"));
            }
            where.append(")");

            StringBuffer sql = new StringBuffer("select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,a_type,a_status,a_pubTime,a_editor,a_columnID,a_picSmall,a_picMiddle,a_picBig,a_linkID,a_linkName,a_channel from ");
            sql.append(tableName);
            sql.append(where);

            JSONArray arr = getDocList(sql.toString(), docIDList);
            if(arr == null){
                ret.put("success","false");
                ret.put("errorInfo","获取失败");
                return ret.toString();
            }

            ret.put("success","true");
            ret.put("errorInfo","");
            ret.put("results",arr);
            return ret.toString();
        } else if(flag == 1) {
            String tableName = "";
            if(channel == 1){
                tableName = "xy_article ";
            }else if(channel == 2){
                tableName = "xy_articleApp ";
            }else {
                ret.put("success","false");
                ret.put("errorInfo","渠道参数错误");
                return ret.toString();
            }

            Object[] param = null;

            StringBuffer where = new StringBuffer(" where a_columnID=");
            where.append(columnID);
//            where.append(" AND a_siteID=").append(siteID);

            //用户ID去判断有权限的sys_currentNode
            String nodeIDs = StringUtils.join(getAuditNodeIDs(userID, siteID, false), ",");
            if(StringUtils.isBlank(nodeIDs)){
                ret.put("success","false");
                ret.put("errorInfo","没有审核和驳回权限");
                return ret.toString();
            }
            where.append(" AND SYS_CURRENTNODE IN (").append(nodeIDs).append(") ");

            if(status != -1){
                where.append(" AND a_status=").append(status);
            }
            if(type != -1){
                where.append(" AND a_type=").append(type);
            }
            if(lastID != 0){
                StringBuilder queryPubtime = new StringBuilder("select a_pubTime from ");
                queryPubtime.append(tableName).append(" WHERE SYS_DOCUMENTID = ").append(lastID);
                String pubtime = getPubtimebyID(queryPubtime.toString());

                if(pubtime == null){
                    ret.put("success","false");
                    ret.put("errorInfo","最后一篇稿件ID错误");
                    return ret.toString();
                }else{
                    where.append(" AND a_pubTime < ?");
                    param = new Object[]{pubtime};
                }
            }
            where.append(" AND SYS_DELETEFLAG=0");

            StringBuffer sql = new StringBuffer("select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,a_type,a_status,a_pubTime,a_editor,a_columnID,a_picSmall,a_picMiddle,a_picBig,a_linkID,a_linkName,a_channel from ");
            sql.append(tableName);
            sql.append(where);
            sql.append(" order by a_pubTime desc ");

            JSONArray arr = getDocListBylimit(sql.toString(),param);
            if(arr == null){
                ret.put("success","false");
                ret.put("errorInfo","获取失败");
                return ret.toString();
            }

            ret.put("success","true");
            ret.put("errorInfo","");
            ret.put("results",arr);
            return ret.toString();
        } else if(flag == 2) {
            String tableName = "";
            if(channel == 1){
                tableName = "xy_article ";
            }else if(channel == 2){
                tableName = "xy_articleApp ";
            }else {
                ret.put("success","false");
                ret.put("errorInfo","渠道参数错误");
                return ret.toString();
            }

            Object[] param = null;

            StringBuffer where = new StringBuffer(" where a_siteID=");
            where.append(siteID);
            if(status != -1){
                where.append(" AND a_status=").append(status);
            }
            if(type != -1){
                where.append(" AND a_type=").append(type);
            }
            if(lastID != 0){
                StringBuilder queryPubtime = new StringBuilder("select a_pubTime from ");
                queryPubtime.append(tableName).append(" WHERE SYS_DOCUMENTID = ").append(lastID);
                String pubtime = getPubtimebyID(queryPubtime.toString());

                if(pubtime == null){
                    ret.put("success","false");
                    ret.put("errorInfo","最后一篇稿件ID错误");
                    return ret.toString();
                }else{
                    where.append(" AND a_pubTime < ?");
                    param = new Object[]{pubtime};
                }
            }
            where.append(" AND SYS_DELETEFLAG=0");

            StringBuffer sql = new StringBuffer("select SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,a_type,a_status,a_pubTime,a_editor,a_columnID,a_picSmall,a_picMiddle,a_picBig,a_linkID,a_linkName,a_channel from ");
            sql.append(tableName);
            sql.append(where);
            sql.append(" order by a_pubTime desc ");

            JSONArray arr = getDocListBylimit(sql.toString(), param);
            if(arr == null){
                ret.put("success","false");
                ret.put("errorInfo","获取失败");
                return ret.toString();
            }

            ret.put("success","true");
            ret.put("errorInfo","");
            ret.put("results",arr);
            return ret.toString();
        } else {
            ret.put("success","false");
            ret.put("errorInfo","标志位参数错误");
            return ret.toString();
        }
    }

    private JSONArray getListFromRel(String sql, int paramBegin, int paramCount) {
        JSONArray result = new JSONArray();

        DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            sql = db.getDialect().getLimitString(sql, paramBegin, paramCount);
            rs = db.executeQuery(sql, null);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("SYS_DOCUMENTID", rs.getString("SYS_DOCUMENTID"));
                json.put("a_position", StringUtils.getNotNull(rs.getString("a_position")));
                json.put("DocOrder", String.valueOf(++paramBegin));

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
     * 处理一个固定位稿件：列表中有，则先删掉。然后加到固定位
     */
    private void fixOnePosition(JSONArray docIDList, JSONObject posOne, int lastID) {
        long docID = Long.parseLong(posOne.getString("SYS_DOCUMENTID"));

        //先把原列表中的删掉
        for (int i = 0; i < docIDList.size(); i++) {
            JSONObject ele = (JSONObject)docIDList.get(i);
            long curDocID = Long.parseLong(ele.getString("SYS_DOCUMENTID"));
            if (curDocID == docID) {
                docIDList.remove(i); //删除
                break;
            }
        }

        //若是第一页，则加进去。其它页只删不加，因为固定位稿件都在第一页显示
        if (lastID == 0) {
            int pos = Integer.parseInt(posOne.getString("a_position"));
            if (docIDList.size() >= pos) {
                docIDList.add(pos - 1, posOne);
            } else {
                docIDList.add(posOne);
            }
        }
    }

//    private String getDocIDsBylimit(String sql){
//        DBSession db = null;
//        IResultSet rs = null;
//        try
//        {
//            db = Context.getDBSession();
//
//            sql = db.getDialect().getLimitString(sql, 0, ALIST_COUNT);
//
//            rs = db.executeQuery(sql, null);
//
//            boolean isFirst = true;
//            StringBuffer docIDs = new StringBuffer("");
//            while (rs.next()){
//                if(isFirst){
//                    docIDs.append(rs.getString("SYS_DOCUMENTID"));
//                    isFirst = false;
//                    continue;
//                }
//                docIDs.append(",");
//                docIDs.append(rs.getString("SYS_DOCUMENTID"));
//            }
//            return docIDs.toString();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return null;
//        }
//        finally
//        {
//            ResourceMgr.closeQuietly(rs);
//            ResourceMgr.closeQuietly(db);
//        }
//    }

    private JSONArray getDocList(String sql, JSONArray docIDList){
        DBSession db = null;
        IResultSet rs = null;
        try
        {
            db = Context.getDBSession();

            rs = db.executeQuery(sql, null);

            String appUrl = InfoHelper.getConfig("互动", "翔宇移动采编外网Api地址");

            while (rs.next()){
                //按稿件ID找到list中的稿件
                long docID = rs.getLong("SYS_DOCUMENTID");
                JSONObject oneDoc = findArticleInList(docIDList, docID);
                if (oneDoc == null) {
                    continue;
                }

                //补充新字段
                oneDoc.put("fileId", rs.getInt("SYS_DOCUMENTID"));
                oneDoc.put("docLibID", rs.getInt("SYS_DOCLIBID"));
                oneDoc.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                oneDoc.put("articleType", rs.getInt("a_type"));
                oneDoc.put("status", rs.getInt("a_status"));
                oneDoc.put("publishtime", StringUtils.getNotNull(rs.getString("a_pubTime")));
                oneDoc.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
                oneDoc.put("colID", rs.getInt("a_columnID"));
//                oneDoc.put("picSmall", StringUtils.getNotNull(rs.getString("a_picSmall")));
                if(rs.getString("a_picSmall") == null || "".equals(rs.getString("a_picSmall"))){
                    oneDoc.put("picSmall","");
                }else{
                    oneDoc.put("picSmall",appUrl+"/getImage?path="+rs.getString("a_picSmall"));
                }

                if(rs.getString("a_picMiddle") == null || "".equals(rs.getString("a_picMiddle"))){
                    oneDoc.put("picMiddle","");
                }else{
                    oneDoc.put("picMiddle",appUrl+"/getImage?path="+rs.getString("a_picMiddle"));
                }

                if(rs.getString("a_picBig") == null || "".equals(rs.getString("a_picBig"))){
                    oneDoc.put("picBig","");
                }else{
                    oneDoc.put("picBig",appUrl+"/getImage?path="+rs.getString("a_picBig"));
                }

                oneDoc.put("linkID",rs.getInt("a_linkID"));
                oneDoc.put("linkName",StringUtils.getNotNull(rs.getString("a_linkName")));
                oneDoc.put("channel",rs.getInt("a_channel"));

//                oneDoc.put("picMiddle", StringUtils.getNotNull(rs.getString("a_picMiddle")));
//                oneDoc.put("picBig", StringUtils.getNotNull(rs.getString("a_picBig")));
            }
            return docIDList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    private JSONObject findArticleInList(JSONArray list, long docID) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject ele = (JSONObject)list.get(i);

            long curDocID = Long.parseLong(ele.getString("SYS_DOCUMENTID"));
            if (curDocID == docID) {
                return ele;
            }
        }
        return null;
    }

    private JSONArray getDocListBylimit(String sql, Object[] param){
        DBSession db = null;
        IResultSet rs = null;
        JSONArray arr = new JSONArray();
        try
        {
            db = Context.getDBSession();

            sql = db.getDialect().getLimitString(sql, 0, ALIST_COUNT);

            rs = db.executeQuery(sql, param);

            String appUrl = InfoHelper.getConfig("互动", "翔宇移动采编外网Api地址");

            while (rs.next()){
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("fileId", rs.getInt("SYS_DOCUMENTID"));
                jsonObj.put("docLibID", rs.getInt("SYS_DOCLIBID"));
                jsonObj.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                jsonObj.put("articleType", rs.getInt("a_type"));
                jsonObj.put("status", rs.getInt("a_status"));
                jsonObj.put("publishtime", StringUtils.getNotNull(rs.getString("a_pubTime")));
                jsonObj.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
                jsonObj.put("colID", rs.getInt("a_columnID"));
//                jsonObj.put("picSmall", StringUtils.getNotNull(rs.getString("a_picSmall")));
                if(rs.getString("a_picSmall") == null || "".equals(rs.getString("a_picSmall"))){
                    jsonObj.put("picSmall","");
                }else{
                    jsonObj.put("picSmall",appUrl+"/getImage?path="+rs.getString("a_picSmall"));
                }

                if(rs.getString("a_picMiddle") == null || "".equals(rs.getString("a_picMiddle"))){
                    jsonObj.put("picMiddle","");
                }else{
                    jsonObj.put("picMiddle",appUrl+"/getImage?path="+rs.getString("a_picMiddle"));
                }

                if(rs.getString("a_picBig") == null || "".equals(rs.getString("a_picBig"))){
                    jsonObj.put("picBig","");
                }else{
                    jsonObj.put("picBig",appUrl+"/getImage?path="+rs.getString("a_picBig"));
                }

                jsonObj.put("linkID",rs.getInt("a_linkID"));
                jsonObj.put("linkName",StringUtils.getNotNull(rs.getString("a_linkName")));

                jsonObj.put("channel",rs.getInt("a_channel"));
//                jsonObj.put("picMiddle", StringUtils.getNotNull(rs.getString("a_picMiddle")));
//                jsonObj.put("picBig", StringUtils.getNotNull(rs.getString("a_picBig")));

                arr.add(jsonObj);
            }
            return arr;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    private double getOrderbyID(String sql){
        DBSession db = null;
        IResultSet rs = null;
        double order = 0;
        try
        {
            db = Context.getDBSession();

            rs = db.executeQuery(sql, null);

            if (rs.next()){
                order = rs.getDouble("a_order");
            }
            return order;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
        finally
        {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    private String getPubtimebyID(String sql){
        DBSession db = null;
        IResultSet rs = null;
        try
        {
            db = Context.getDBSession();

            rs = db.executeQuery(sql, null);

            if (rs.next()){
                return rs.getString("a_pubTime");
            }
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    public String getTabs(int userID, String data){
        JSONObject jsonObject = JSONObject.fromObject(data);

        int siteID = jsonObject.getInt("siteID");

        JSONObject ret = new JSONObject();

        try {
            UserReader userReader = (UserReader) Context.getBean(UserReader.class);
            User currentUser = userReader.getUserByID(userID);
            String tCode = currentUser.getProperty1();//扩展字段1里是租户代号
            if (StringUtils.isBlank(tCode)) {
                tCode = Tenant.DEFAULTCODE;
            }
            int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);

            Role[] roles = getRolesBySite(userLibID, userID, siteID);
            if (roles == null || roles.length == 0) {
                ret.put("success","false");
                ret.put("errorInfo","无可用角色");
                return ret.toString();
            } else {
                int roleID = roles[0].getRoleID();
                //取合并后的角色
				int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
                //读有权限的Tab
                List<Tab> tabs = MainHelper.getRoleTabs(newRoleID);

                JSONArray arr = new JSONArray();
                if(tabs == null){
                    ret.put("success","true");
                    ret.put("errorInfo","");
                    ret.put("results",arr);
                    return ret.toString();
                }

                for(String appTabId:APP_TABS_ID){
                    SubTab st = MainHelper.getSubTab(tabs,appTabId);
                    if(st != null){
                        JSONObject json = new JSONObject();
                        json.put("value",st.getId());
                        json.put("name",st.getName());
                        arr.add(json);
                    }
                }

                ret.put("success","true");
                ret.put("errorInfo","");
                ret.put("results",arr);
                return ret.toString();
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            ret.put("success","false");
            ret.put("errorInfo","获取失败");
            return ret.toString();
        }
    }

    public String addArticle(int userID, String data){

        JSONObject jsonObject = JSONObject.fromObject(data);

        JSONObject jsonObj = jsonObject.getJSONObject("article");

        JSONObject ret = new JSONObject();

        if(jsonObj.size() <= 0){
            System.out.println("the record is null");
            ret.put("success","true");
            ret.put("results","{}");
            return ret.toString();
        }

        String result = null;
        try {
            //以后有批量投稿的需求时，可以将参数改为JSON数组，不必过多修改代码，功能扩展性较好
            List<ImpResult> results = parseArticle(userID,jsonObj);

            int docID = jsonObj.getInt("fileId");
            if(docID == 0){
                result = convertReturn(results, 1);
            } else {
                result = convertReturn(results, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
	public String uploadImage(HttpServletRequest request) throws Exception{

        long start = System.currentTimeMillis();
        // 获得目录
        Date date = Calendar.getInstance().getTime();
        String ymdAddr = new SimpleDateFormat("yyyyMM/dd/").format(date);

        // 获得类型，用于返回给App
        String fileType = request.getParameter("fileType");

        // 获得文件数据流
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 50); // 50兆

        List<DiskFileItem> items = upload.parseRequest(request);
        Iterator<DiskFileItem> iter = items.iterator();

        //获得路径
        StorageDevice device = InfoHelper.getPicDevice();
        String appUrl = InfoHelper.getConfig("互动", "翔宇移动采编外网Api地址");

        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        InputStream fis = null;
        long end = System.currentTimeMillis();
        System.out.println("上传使用了" + (end - start) + " ms");

        JSONArray jsonArr = new JSONArray();		// fileList
        JSONObject rtnJson = new JSONObject();		// resultJson
        try {
            while (iter.hasNext()) {
                uploadFile(iter, fis, sdManager, ymdAddr, jsonArr, device, fileType, appUrl);
            }
            rtnJson.put("success", true);
            rtnJson.put("errorInfo", "");
            rtnJson.put("fileList", jsonArr);
        } catch (Exception ex) {
            ex.printStackTrace();
            rtnJson.put("success", false);
            rtnJson.put("errorInfo", "上传失败:" + ex.getMessage());
            rtnJson.put("fileList", "[]");
        } finally {
            ResourceMgr.closeQuietly(fis);
        }
        return rtnJson.toString();
    }

    private void uploadFile(Iterator<DiskFileItem> iter, InputStream fis,
                            StorageDeviceManager sdManager, String ymdAddr, JSONArray jsonArr,
                            StorageDevice device, String fileType, String appUrl) throws Exception {

        JSONObject json = new JSONObject();
        DiskFileItem item = iter.next();
        fis = item.getInputStream();
        String fileName = item.getName();
        fileName = UUID.randomUUID()
                + fileName.substring(fileName.lastIndexOf("."));

        sdManager.write(device, "/xy/"+ymdAddr + fileName, fis);
//        String recordPath = appUrl + "getImage?path=图片存储;xy/" + ymdAddr + fileName;
        json.put("refID", 0);
        json.put("type", Integer.parseInt(fileType));
        json.put("path", appUrl + "/getImage?path=图片存储;xy/" + ymdAddr + fileName);
        jsonArr.add(json);

        // 生成抽图信息文件
        extractingImg("xy/" + ymdAddr + fileName);

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

    private List<ImpResult> parseArticle(int userID,JSONObject jsonObject) throws Exception {
        List<ImpResult> results = new ArrayList<>();

        //根据userID判断权限  如果没有当前栏目权限  返回错误信息
//        int colLibID = LibHelper.getColumnLibID();
//        long columnID = jsonObject.getLong("colID");
//        int channel = jsonObject.getInt("channel");
//        int siteID = jsonObject.getInt("siteID");
//
//        Long[] ids = null;
//        if(channel == 1){
//            ids = colReader.getOpColumnIds(colLibID,userID,siteID,0);
//        }else if(channel == 2){
//            ids = colReader.getOpColumnIds(colLibID,userID,siteID,4);
//        }
//        List idLists = Arrays.asList(ids);
//
//        if(idLists.contains(columnID)){
        ImpResult impResult =  convertArticle(userID, jsonObject);
        results.add(impResult);
//        } else {
//            ImpResult impResult = new ImpResult();
//
//            //impResult.setOriginalId(jsonObject.getInt("OriginalId"));
//            impResult.setPublishId(jsonObject.getInt("fileId"));
//            impResult.setType(jsonObject.getInt("articleType"));
//            impResult.setChannel(channel);
//            impResult.setPublish(jsonObject.getInt("publish"));
//
//            impResult.setArticle(null);
//            impResult.setAttachList(null);
//            impResult.setArticleRelIDs(null);
//
//            impResult.setSuccess("false");
//            impResult.setErrorCode("101");
//            impResult.setErrorCause("主栏目不存在或主栏目无操作权限");
//
//            results.add(impResult);
//        }

        return results;
    }

    private ImpResult convertArticle(int userID, JSONObject jsonObject) throws Exception {

        int channel = jsonObject.getInt("channel");
        int docLibID = jsonObject.optInt("docLibID");

        ImpResult impResult = new ImpResult();
//        impResult.setOriginalId(jsonObject.getInt("OriginalId"));
        impResult.setPublishId(jsonObject.getInt("fileId"));
        impResult.setType(jsonObject.getInt("articleType"));
        impResult.setChannel(channel);
        impResult.setPublish(jsonObject.getInt("publish"));

        //取E5 稿件对象
        com.founder.e5.doc.Document article = null;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        long docID = jsonObject.getInt("fileId");

        boolean isNew = false;

        if(docID == 0){
            isNew = true;
            if(channel==1){
                docLibID = LibHelper.getArticleLibID();
            }else if(channel == 2){
                docLibID = LibHelper.getArticleAppLibID();
            }
            // 取稿件ID
            docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
            article = docManager.newDocument(docLibID,docID);
            ProcHelper.initDoc(article);
            impResult.setPublishId((int)docID);
        }else{
            isNew = false;
            article = docManager.get(docLibID, docID);
            if(article == null){
                impResult.setArticle(null);
                impResult.setAttachList(null);
                impResult.setArticleRelIDs(null);

                impResult.setSuccess("false");
                impResult.setErrorCode("102");
                impResult.setErrorCause("稿件ID错误");
                return impResult;

            }
        }

//		article.set("sys_documentid",jsonObject.getInt("ArticleId"));
        article.set("a_siteID", jsonObject.getInt("siteID"));
        article.set("a_channel", channel);

        article.set("a_type", jsonObject.getInt("articleType"));
        article.set("sys_topic", jsonObject.getString("title").replaceAll("[\n\r]", ""));

        if("".equals(jsonObject.optString("linktitle"))){
            article.set("a_linkTitle", jsonObject.getString("title").replaceAll("[\n\r]", ""));
        }else {
            article.set("a_linkTitle", jsonObject.optString("linktitle").replaceAll("[\n\r]", ""));
        }
//        article.set("a_linkTitle", jsonObject.getString("linktitle").replaceAll("[\n\r]", ""));
        article.set("a_subTitle", jsonObject.optString("subtitle"));

        // 直播稿件
        article.set("a_linkID",jsonObject.optInt("linkID"));
        article.set("a_linkName",jsonObject.optString("linkName"));

        //直播稿件设置WEB直播地址
        if (article.getInt("a_type") == Article.TYPE_LIVE ){
            article.set("a_url", UrlHelper.getWebLiveShareUrl()+"/"+article.getDocID()+"/"+article.getLong("a_linkID"));
        }


        article.set("a_leadTitle",jsonObject.optString("introtitle"));
        article.set("a_mark",jsonObject.optString("mark"));
        article.set("a_isBigPic",jsonObject.optInt("isBigPic"));

        UserReader userReader = (UserReader)Context.getBean(UserReader.class);
        User user = userReader.getUserByID(userID);
        if(isNew){
            article.set("a_pubTime", DateUtils.getTimestamp());
            article.set("SYS_AUTHORID", user.getUserID());
            article.set("sys_authors", user.getUserName());
            article.set("a_editor", user.getUserName());
        } else {
            if(jsonObject.getInt("colID") != article.getInt("a_columnID")){
                impResult.setArticle(null);
                impResult.setAttachList(null);
                impResult.setArticleRelIDs(null);

                impResult.setSuccess("false");
                impResult.setErrorCode("107");
                impResult.setErrorCause("主栏目不可修改");
                return impResult;
            }
        }

        article.setLastmodified(DateUtils.getTimestamp());
        article.setLocked(false);
        article.setCurrentUserName(user.getUserName());
        article.setCurrentUserID(user.getUserID());

        // 设部门 、来源类型
        int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(),
                article.getDocLibID());
//        SiteUserManager userManager = (SiteUserManager)Context.getBean(SiteUserManager.class);
        Document siteUser = userManager.getUser(userLibID, user.getUserID());
        int uType = siteUser.getInt("u_type");
        if (uType < 0){
            uType = 0;
        }
        article.set("a_orgID", siteUser.getInt("u_orgID"));// 部门ID
        article.set("a_sourceType", uType);// 来源类型，0：编辑，1：记者

        article.set("a_columnID", jsonObject.getInt("colID"));
        article.set("a_column", jsonObject.getString("colName"));
        article.set("a_columnAll", jsonObject.getString("colID"));

        article.set("a_discussClosed",jsonObject.optInt("discussClosed"));
        article.set("a_longitude",jsonObject.optDouble("longitude"));
        article.set("a_latitude",jsonObject.optDouble("latitude"));
        article.set("a_location",jsonObject.optString("location"));

        article.set("a_abstract", jsonObject.optString("attAbstract",""));
//        article.set("a_keyword", jsonObject.getString("Keyword"));
//        article.set("a_tag", jsonObject.getString("Tag"));
//        if(op == 1){
//            article.set("sys_created", jsonObject.getString("Nsdate"));
//        }
//        article.set("a_source", jsonObject.getString("Source"));
//        article.set("sys_authors", jsonObject.getString("Author"));
//        article.set("a_editor", jsonObject.getString("Editor"));
//        article.set("a_liability", jsonObject.getString("Liability"));

        article.set("a_content", jsonObject.optString("content"));

        article.set("a_status", Article.STATUS_PUB_NOT);

//		article.set("a_docLibID", docLibID);
//		article.set("a_isSensitive", 0);
//		article.set("a_templatePadID", WebUtil.getInt(request, "templatePadID", 0));
//		article.set("a_templateID", WebUtil.getInt(request, "templateID", 0));

//		String content = WebUtil.getStringParam(request, "Content");
//
//		if (content != null) {
//			//增加翔宇分页符
//			content = content.replaceAll("<hr>", "_ueditor_page_break_tag_");
//			content = content.replaceAll("<hr/>", "_ueditor_page_break_tag_");
//			article.set("a_content", content);
//
//		}
        String picBig = jsonObject.optString("picBig");
        if(picBig.isEmpty()){
            article.set("a_picBig",null);
        } else {
            if(picBig.contains("getImage?path=")){
                String[] picBigArray = picBig.split("path=");
                article.set("a_picBig",picBigArray[1]);
            }else {
                article.set("a_picBig",picBig);
            }
        }

        String picMiddle = jsonObject.optString("picMiddle");
        if(picMiddle.isEmpty()){
            article.set("a_picMiddle",null);
        } else {
            if(picMiddle.contains("getImage?path=")){
                String[] picMiddleArray = picMiddle.split("path=");
                article.set("a_picMiddle",picMiddleArray[1]);
            }else {
                article.set("a_picMiddle",picMiddle);
            }
        }

        String picSmall = jsonObject.optString("picSmall");
        if(picSmall.isEmpty()){
            article.set("a_picSmall",null);
        } else {
            if(picSmall.contains("getImage?path=")){
                String[] picSmallArray = picSmall.split("path=");
                article.set("a_picSmall",picSmallArray[1]);
            }else {
                article.set("a_picSmall",picSmall);
            }
        }

        int userExtLibID = LibHelper.getUserExtLibID();
        int siteID = article.getInt("A_SITEID");

        Role[] roles = getRolesBySite(userExtLibID, userID, siteID);
        int roleID = roles[0].getRoleID();
        int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);

        if(impResult.getPublish() == 1){
            // 发布送审
            articleManager.tryPublish(article, article.getInt("a_columnID"), newRoleID);
        } else {
            articleManager.setFlowByColumn(article);
        }

        JSONArray attachment = null;
        if(article.getInt("a_type") == Article.TYPE_ARTICLE || article.getInt("a_type") == Article.TYPE_PIC ){
            attachment = jsonObject.optJSONArray("imageArray");
        }else if(article.getInt("a_type") == Article.TYPE_VIDEO){
            attachment = jsonObject.optJSONArray("videoArray");
        }
        List<Document> attachList = null;
        try {
            attachList = convertAttachemnt(attachment, article);
        } catch (Exception e) {
            e.printStackTrace();

            impResult.setArticle(null);
            impResult.setAttachList(null);
            impResult.setArticleRelIDs(null);

            impResult.setSuccess("false");
            impResult.setErrorCode("104");
            impResult.setErrorCause("获取附件失败");
            return impResult;
        }
        impResult.setAttachList(attachList);

        impResult.setArticle(article);

        return impResult;
    }

    private List<com.founder.e5.doc.Document> convertAttachemnt(JSONArray attachment,
                                                                com.founder.e5.doc.Document article) throws Exception {
        if((attachment == null ||attachment.size()==0) && article.get("a_picSmall")==null
                && article.get("a_picMiddle")==null
                && article.get("a_picBig")==null){
            if(article.getString("a_type").equals("0")){
                article.set("SYS_HAVEATTACH",0);
            }
            return null;
        }
        List<com.founder.e5.doc.Document> newList = new ArrayList<>();
        //需要拿到xy_attachment的文档库
//		com.founder.e5.doc.Document attach = null;
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());

        if(attachment != null){
            if(article.getString("a_type").equals("0")){
                if(attachment.size() == 0){
                    article.set("SYS_HAVEATTACH",0);
                }else{
                    article.set("SYS_HAVEATTACH",1);
                }

            }
            int index = 0;
            String type = article.getString("a_type");
            for(int t=0; t<attachment.size(); ++t){
                // 每次循环 传建一个新的对象
//				attach = null;
                long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
                com.founder.e5.doc.Document attach = docManager.newDocument(attLibID, attDocID);

                JSONObject file = attachment.getJSONObject(t);
                boolean result = false;
                if("0".equals(type)){
                    result = executeArticleType(attach, file, article);
                }else if("1".equals(type)){
                    result = executeMultiImageType(attach, file, index);
                }else if("2".equals(type)){
                    result = executeVideoType(attach, file);
                }
                if(result){
                    attach.set("att_articleID", article.getDocID()); // 所属稿件
                    attach.set("att_articleLibID", article.getDocLibID());
                    attach.set("att_order", index++);
                    newList.add(attach);
                }
            }
        } else {
            if(article.getString("a_type").equals("0")){
                article.set("SYS_HAVEATTACH",0);
            }
        }

        insertTitlePicAtt(newList, article, 2);
        insertTitlePicAtt(newList, article, 3);
        insertTitlePicAtt(newList, article, 4);
        return newList;
    }

    /**
     * 插标题图入附件表
     * @param article
     * @param type
     */
    private void insertTitlePicAtt(List<com.founder.e5.doc.Document> newList,
                                   com.founder.e5.doc.Document article, int type) throws E5Exception {
        Object titlePic = null;
        if(2 == type) titlePic = article.get("a_picBig");
        else if(3 == type) titlePic = article.get("a_picMiddle");
        else if(4 == type) titlePic = article.get("a_picSmall");
        if(titlePic != null){
            //需要拿到xy_attachment的文档库
//			com.founder.e5.doc.Document attach = null;
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
            long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
            com.founder.e5.doc.Document attach = docManager.newDocument(attLibID, attDocID);

            attach.set("att_articleID", article.getDocID()); // 所属稿件
            attach.set("att_articleLibID", article.getDocLibID());
            attach.set("att_type",type);
            attach.set("att_path",titlePic);
            newList.add(attach);
        }
    }
    /**
     * 视频稿的附件处理
     */
    private boolean executeVideoType(com.founder.e5.doc.Document attachement,	JSONObject file){

        attachement.set("att_type", 1);
        attachement.set("att_objID", file.getInt("videoID"));
        attachement.set("att_objLibID",LibHelper.getVideoLibID());
        //视频连接
        attachement.set("att_path",	file.getString("url"));
        attachement.set("att_url",	file.getString("url"));
        attachement.set("att_urlPad",	file.getString("urlPad"));

        String[] attachArray = file.getString("picPath").split("path=");
        attachement.set("att_picPath", attachArray[1]);

        attachement.set("att_duration", file.getInt("duration"));
        return true;

    }

    /**
     * 组图稿的附件处理
     */
    private boolean executeMultiImageType(com.founder.e5.doc.Document attachement,
                                            JSONObject file, int index) throws Exception{

        attachement.set("att_type",0);

        String[] attachArray = file.getString("path").split("path=");
        attachement.set("att_path",attachArray[1]);

        attachement.set("att_content",file.getString("content"));
        return true;

    }

    /**
     * 文章稿的附件处理
     */
    private boolean executeArticleType(com.founder.e5.doc.Document attachement, JSONObject file,
                                         com.founder.e5.doc.Document article) throws Exception{

        attachement.set("att_type",0);

        if(file.getString("path").contains("getImage?path=")){
            String[] attachArray = file.getString("path").split("path=");
            attachement.set("att_path",attachArray[1]);

            String fileName = file.getString("content");
            String replCont = replaceAttPath(article.get("a_content").toString(), fileName, "../../xy/image.do?path="+attachArray[1]);//将正文中的图片地址替换成E5中的地址
            article.set("a_content", replCont);
        }else {
            attachement.set("att_path",file.getString("path"));
            attachement.set("att_url",file.getString("path"));
            attachement.set("att_urlPad",file.getString("path"));
        }

        attachement.set("att_content",file.getString("content"));
        return true;
    }

    private String replaceAttPath(String content, String filePath, String recordPath){

        org.jsoup.nodes.Document html = Jsoup.parse(content);
        Elements list = html.select("img");
        if (list.isEmpty()) return content;
        for (Element img : list) {
            if(img.attr("src").contains("image.do?path=")
                    || !img.attr("src").contains("getImage?path="))
                continue;
            img.replaceWith(Jsoup.parse("<img src=\"" + recordPath + "\">").select("img").get(0));
            break;
        }
        return html.body().html();
    }

    /**
     * 操作数据库   推送发布   组装请求返回值
     */
    private String convertReturn(List<ImpResult> results, int op) {
        ImpResult impResult = null;
        JSONObject ret = new JSONObject();
//        JSONArray arr = new JSONArray();
        String result = null;
//        String relResult = null;
        for (int i = 0; i < results.size(); i++) {
            impResult = results.get(i);
            //操作数据库  判断op值  对稿件和附件增删改
            //保存稿件之前，需要调用getNewOrder方法获取排序字段值
            //保存附件之后，需要调用extractingImg方法生成抽图文件信息
            Document article = impResult.getArticle();

            if(article!=null){
                if(op == 1){
                    if(impResult.getChannel()==1 || impResult.getChannel()==2){
                        double order = getNewOrder(article);
                        article.set("a_order", order);
                    }
                }

                List<com.founder.e5.doc.Document> attachList = impResult.getAttachList();

                result = save(article, attachList, op);

                if(result == null){

                    impResult.setSuccess("true");

//                    if(attachList != null){
//                        for(int j = 0; j < attachList.size(); j++){
//                            Document attach = attachList.get(j);
//                            extractingImg(attach.getString("att_path"));
//                        }
//                    }

                    //推送发布或送审
                    if(impResult.getPublish() == 1){
                        System.out.println("publish");
                        PublishTrigger.article(article);
                    }

                    // 记录日志
                    if(op == 1){
                        if(impResult.getPublish() == 1){
                            LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                                    article.getCurrentUserName(), article.getCurrentUserID(),
                                    "写稿并发布", "");
                        }else {
                            LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                                    article.getCurrentUserName(), article.getCurrentUserID(),
                                    "写稿", "");
                        }
                    }else if(op == 2) {
                        // 记录稿件的修改详情
                        String detail = LogHelper.whatChanged(article);
                        if (StringUtils.isBlank(detail)) {
                            detail = "";
                        }
                        if(impResult.getPublish() == 1){
                            LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                                    article.getCurrentUserName(), article.getCurrentUserID(),
                                    "修改并发布", detail);
                        }else {
                            LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                                    article.getCurrentUserName(), article.getCurrentUserID(),
                                    "修改", detail);
                        }
                    }
                }else{
                    impResult.setSuccess("false");
                    impResult.setErrorCode("105");
                    impResult.setErrorCause("保存稿件失败");
                }
            }

            JSONObject json = new JSONObject();//组装返回值
            json.put("docID", impResult.getPublishId());
            json.put("channel", impResult.getChannel());
//            json.put("errorInfo", impResult.getErrorCause());

            ret.put("results",json);
            ret.put("errorInfo",impResult.getErrorCause());
            ret.put("success", impResult.getSuccess());
//            arr.add(json);

        }
//        ret.put("ImpResult",arr);
        return ret.toString();
    }

    /**
     * 计算稿件排序字段值
     */
    private double getNewOrder(Document article) {
        Timestamp pubTime = article.getTimestamp("a_pubTime");

        if (pubTime == null) return 0;

        Calendar ca = Calendar.getInstance();
        ca.setTime(pubTime);

        double order = createDisplayOrder(ca, 0, 0, article.getDocID());
        return order;
    }

    private double createDisplayOrder(Calendar cd, int daycnt, int ord, long id) {
        if (cd == null)
            cd = Calendar.getInstance();

        int nHour = cd.get(Calendar.HOUR_OF_DAY);
        int nMinute = cd.get(Calendar.MINUTE);

        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);

        String articleTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");

        Calendar dd = Calendar.getInstance();

        if(articleTime.trim().equals("")||articleTime==null){
            dd.set(2015, 4, 13, 0, 0, 0); //新起点~
            //dd.set(2000, 9, 12, 0, 0, 0);
            //dd.set(2000, 2, 12, 0, 0, 0);
        }else{
            String[] atime = articleTime.trim().split("-");
            dd.set(Integer.parseInt(atime[0]), Integer.parseInt(atime[1]), Integer.parseInt(atime[2]), 0, 0, 0);
        }

        dd.set(java.util.Calendar.HOUR_OF_DAY, 0);
        dd.set(java.util.Calendar.MINUTE, 0);
        dd.set(java.util.Calendar.SECOND, 0);
        dd.set(java.util.Calendar.MILLISECOND, 0);

        long tt = cd.getTimeInMillis() - dd.getTimeInMillis();
        double ret = 0;

	/*
		87654321.12345678：
		[------][0][00][0.0][000000]
		万位以上是天数，千位的是优先级，百位和十位是小时数，个位和小数点后第一位是分钟，小数点后第二位开始连续6位是ID
		 long days = (tt / 1000 * 60 * 60 * 24); //毫秒数转成天数
		 days += daycnt;		//天数再加上置顶天数
		 ret = days * 10000 + priority * 1000 + hour * 10 + minutes * 0.1 + ID后六位放在小数点后第二位开始
	 */
        ret = (long) ( (double) (tt) / 8640.0)
                + daycnt * 10000
                + (double)ord * 1000
                + (double)nHour*10.0 + (double)nMinute*0.1
                + (double)(id % 1000000)*0.0000001;
        return ret * -1;
    }

    private String save(Document article, List<Document> attachList, int op) {

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            conn.beginTransaction();

            docManager.save(article, conn);

            if(op == 1){
                if(attachList!=null) {
                    for (Document attach : attachList) {
                        docManager.save(attach, conn);
                    }
                }
            } else if(op == 2){
                int docLibID = article.getDocLibID();
                long docID = article.getDocID();
                Document[] oldAttachments = articleManager.getAttachments(docLibID, docID);
                // 保存图片
                saveAttachmentPic(oldAttachments, article, attachList, conn);
                // 标题图片
                savePicTitle(oldAttachments, article, attachList, conn);
                //保存链接标题
                saveLinkTitle(article, conn, false);
                // 去掉不用了的附件
                deleteOldPic(oldAttachments, conn);
            }

            // 提交transaction
            conn.commitTransaction();
            return null;
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    private void saveAttachmentPic(Document[] oldAttachments, Document article,
                                   List<Document> attachList, DBSession conn) throws E5Exception {

        if (attachList != null && attachList.size() > 0) {

//            int articleType = article.getInt("a_type");

            DocumentManager docManager = DocumentManagerFactory.getInstance();
            for (int i = 0; i < attachList.size(); i++) {
                Document attach = attachList.get(i);

                if(attach.getInt("att_type") == 0){
                    int index = find(oldAttachments, attach.getString("att_path"), attach.getInt("att_type"));
                    if (index < 0) {
                        docManager.save(attach, conn);
                    } else {
                        // 是组图稿，则还要检查content/order/isindexed是否有变化，有变化就更新附件表
                        // if (articleType == Article.TYPE_PIC)
//                    changeOldPic(oldAttachments[index], attach, i, articleType, conn);
                        changeOldPic(oldAttachments[index], attach, i, conn);
                        oldAttachments[index] = null;
                    }
                }
            }
        }
    }

    // 保存标题图片
    private void savePicTitle(Document[] oldAttachments, Document article,
                              List<Document> attachList, DBSession conn) throws E5Exception {
        if (attachList != null && attachList.size() > 0) {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            for (int i = 0; i < attachList.size(); i++) {
                Document attach = attachList.get(i);

                if(attach.getInt("att_type") == 2 || attach.getInt("att_type") == 3
                        || attach.getInt("att_type") == 4){
                    int index = find(oldAttachments, attach.getString("att_path"), attach.getInt("att_type"));
                    if (index < 0) {
                        docManager.save(attach, conn);
                    } else {
                        oldAttachments[index] = null;
                    }
                }
            }
        }
    }
    
    private void saveLinkTitle(Document doc0, DBSession conn, boolean syncLinkTitle) {
		// 更新栏目稿件的链接标题：根据SYS_DOCUMENTID和CLASS_1（栏目ID）
    	long currentColID = doc0.getLong("a_columnID");
    	if(currentColID != 0) {
			String relTable = InfoHelper.getRelTable(doc0.getDocLibID());
			String sql = "update " + relTable + " set a_linkTitle=? where SYS_DOCUMENTID=? ";
			try {
				if(syncLinkTitle){
					InfoHelper.executeUpdate(sql, new Object[] { doc0.getString("a_linkTitle"), doc0.getDocID()}, conn);
				}
				sql = "update " + relTable + " set a_linkTitle=?, a_attr=? where SYS_DOCUMENTID=?  and CLASS_1=? ";
				InfoHelper.executeUpdate(sql, new Object[] { doc0.getString("a_linkTitle"), doc0.getInt("a_attr"),
						doc0.getDocID(), currentColID }, conn);
			} catch (E5Exception e) {
				e.printStackTrace();
			}
    	}
	}
    
    private void deleteOldPic(Document[] old, DBSession conn)
            throws E5Exception {
        if (old == null)
            return;

        DocumentManager docManager = DocumentManagerFactory.getInstance();

        // 前面没找到，是不需要的附件，删除
        for (int i = 0; i < old.length; i++) {
            if (old[i] != null) {
                docManager.delete(old[i].getDocLibID(), old[i].getDocID(), conn);

                // 若有对应的图片库数据，也删除（2015.7.6 实际上已经没有图片库对应了）
//                if (old[i].getInt("att_type") == 0
//                        && old[i].getInt("att_objID") > 0) {
//                    docManager.delete(old[i].getInt("att_objLibID"),
//                            old[i].getLong("att_objID"), conn);
//                }
            }
        }
    }

    // 找附件，若找到，则把附件数组中置为null
    private int find(Document[] old, String path, int type) {
        if (old == null)
            return -1;

        for (int i = 0; i < old.length; i++) {
            if (old[i] == null)
                continue;

            if (old[i].getInt("att_type") == type
                    && old[i].getString("att_path").equals(path)) {
                return i;
            }
        }
        return -1;
    }

    private void changeOldPic(Document old, Document attach, int i, DBSession conn) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        // 是组图稿，则还要检查content/order/isindexed是否有变化，有变化就更新附件表
        boolean changed = false;
        if (!attach.getString("att_content").equals(old.getString("att_content"))) {
            changed = true;
            old.set("att_content", attach.getString("att_content"));
        }
//        if ((articleType == Article.TYPE_PIC || articleType == Article.TYPE_PANORAMA)
//                && picInfo.isIndexed != (old.getDeleteFlag() == 0)) {
//            changed = true;
//            old.set("att_indexed", picInfo.isIndexed ? 1 : 0);
//        }
        if (i != old.getInt("att_order")) {
            changed = true;
            old.set("att_order", i);
        }
        if (changed) {
            docManager.save(old, conn);
			/*
			 * //更新到对应的图片库（2015.7.6 实际上已经没有图片库对应了） long objID =
			 * old.getLong("att_objID"); if (objID > 0) { Document oldPic =
			 * docManager.get(old.getInt("att_objLibID"), objID); if (oldPic !=
			 * null) { oldPic.set("p_content", picInfo.content);
			 * oldPic.set("p_groupOrder", i);
			 * oldPic.setDeleteFlag(picInfo.isIndexed ? 0 : 2);
			 *
			 * docManager.save(oldPic, conn); } }
			 */
        }
    }

    /**
     * 生成抽图文件信息
     */
    public void extractingImg(String attrPath){

        StorageDevice device = InfoHelper.getPicDevice();
        String storeBasePath = InfoHelper.getDevicePath(device);

        attrPath = attrPath.replace("/", "~");
        File infoDir = new File(FilePathUtil.normalPath(storeBasePath,"extracting"));
        if(!infoDir.exists()){
            infoDir.mkdirs();
        }
        File infoFile = new File(infoDir, attrPath);
        if(!infoFile.exists()){
            try {
                infoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getArticleDetail(HttpServletRequest request, String data) throws E5Exception {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        int userID = NumberUtils.toInt(request.getParameter("userID"));
        JSONObject ret = ArticleDetailHelper.articleDetail(docLibID, docID, userID);
        return ret.toString();
    }
    
    public String pubSearch(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		long columnID = jsonObject.getInt("columnID");
		int siteID = jsonObject.optInt("siteID", -1); //站点ID 默认为-1 后续会根据栏目获取
		int lastID = jsonObject.getInt("lastID");
		int channel = jsonObject.getInt("channel");
		String key = jsonObject.getString("key");
		int status = jsonObject.optInt("status", -1); //稿件状态默认为-1 表示全部
		int type = jsonObject.optInt("type", -1);     //稿件类型默认为-1 表示全部
		JSONObject ret = SearchArticleHelper.pubSearch(key, siteID, channel, lastID, columnID, status, type);
		return ret.toString();
	}
    
    public String revokeSearch(HttpServletRequest request, String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		long fileId = jsonObject.optInt("fileId", -1); //稿件ID为非必填项，默认为0
		int siteID = jsonObject.getInt("siteID");
		int lastID = jsonObject.getInt("lastID");
		int channel = jsonObject.getInt("channel");
		String key = jsonObject.optString("key", ""); //关键字为非必填项，为空时不作为查询条件
		int status = jsonObject.optInt("status", -1);  //稿件状态默认为1    已发布
		int type = jsonObject.optInt("type", -1);      //稿件类型默认为-1 表示全部
		JSONObject ret = SearchArticleHelper.revokeSearch(key, siteID, channel, lastID, fileId, status, type);
		return ret.toString();
	}

    public String getPubCols(int userID, String data) {

        JSONObject jsonObject = JSONObject.fromObject(data);
        int siteID = jsonObject.getInt("siteID");
        int channel = jsonObject.getInt("channel")-1;
        int parentID = jsonObject.getInt("parentID");
        int hasParentPer = jsonObject.optInt("hasParentPer",0);

        JSONObject ret = new JSONObject();

        JSONArray arr = new JSONArray();

        if(parentID==0){
            try {
                int colLibID = LibHelper.getColumnLibID();

                //若是站点管理员，则可操作所有栏目
                if (isAdmin(userID)) {
                    Document[] cols = colManager.getRoot(colLibID, siteID, channel);
                    arr = ColumnTreeHelper.jsonTree(cols);
                    ret.put("success",true);
                    ret.put("errorInfo","");
                    ret.put("results",arr);
                    return ret.toString();
                }else {
                    int userLibID = LibHelper.getUserExtLibID();

                    Role[] roles = getRolesBySite(userLibID, userID, siteID);
                    //按角色个数，单个取角色ID，多个取合并后的角色ID
                    int roleID = (roles.length == 1) ? roles[0].getRoleID() : PermissionHelper.mergeRoles(roles);

                    Column[] cols = colReader.getOpColumns(colLibID, userID, siteID, channel, roleID);
                    arr = jsonTreeWithParent(cols);
                    ret.put("success",true);
                    ret.put("errorInfo","");
                    ret.put("results",arr);
                    return ret.toString();
                }
            } catch (E5Exception e) {
                e.printStackTrace();
                ret.put("success", false);
                ret.put("errorInfo", "获取失败");
                return ret.toString();
            }
        }else {
            try {
                int colLibID = LibHelper.getColumnLibID();
                if (isAdmin(userID)) {
                    List<Column> cols = colReader.getSub(colLibID, parentID);
                    Collections.sort(cols, new Comparator<Column>() {
                        public int compare(Column me1, Column me2) {
                            return new Integer(me1.getOrder()).compareTo(me2.getOrder());
                        }
                    });
                    arr = ColumnTreeHelper.jsonTree(cols);
                    ret.put("success",true);
                    ret.put("errorInfo","");
                    ret.put("results",arr);
                    return ret.toString();
                }else{
                    boolean hasParent = false;
                    if(hasParentPer == 1){
                        hasParent = true;
                    }

                    if(hasParent){
                        List<Column> cols = colReader.getSub(colLibID, parentID);
                        Collections.sort(cols, new Comparator<Column>() {
                            public int compare(Column me1, Column me2) {
                                return new Integer(me1.getOrder()).compareTo(me2.getOrder());
                            }
                        });
                        arr = ColumnTreeHelper.jsonTree(cols);
                        ret.put("success",true);
                        ret.put("errorInfo","");
                        ret.put("results",arr);
                        return ret.toString();
                    }else {
                        int userLibID = LibHelper.getUserExtLibID();

                        Role[] roles = getRolesBySite(userLibID, userID, siteID);
                        //按角色个数，单个取角色ID，多个取合并后的角色ID
                        int roleID = (roles.length == 1) ? roles[0].getRoleID() : PermissionHelper.mergeRoles(roles);

                        Column[] columns = colReader.getOpColumns(colLibID, userID, siteID, channel, roleID);

                        arr = jsonTreeWithParentNew(columns,parentID);
                        ret.put("success",true);
                        ret.put("errorInfo","");
                        ret.put("results",arr);
                        return ret.toString();
                    }
                }

//                int colLibID = LibHelper.getColumnLibID();
//                List<Column> cols = colReader.getSub(colLibID, parentID);
//                Collections.sort(cols, new Comparator<Column>() {
//                    public int compare(Column me1, Column me2) {
//                        return new Integer(me1.getOrder()).compareTo(me2.getOrder());
//                    }
//                });
//                arr = ColumnTreeHelper.jsonTree(cols);
//                ret.put("success",true);
//                ret.put("errorInfo","");
//                ret.put("results",arr);
//                return ret.toString();
            } catch (E5Exception e) {
                e.printStackTrace();
                ret.put("success", false);
                ret.put("errorInfo", "获取失败");
                return ret.toString();
            }
        }

    }

	public String flowRecordList(String data) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        JSONObject result = new JSONObject();
        FlowRecordManager manager = FlowRecordManagerFactory.getInstance();
        try {
			FlowRecord[] frs = manager.getAssociatedFRs(docLibID,docID, true); 
			JSONArray results = new JSONArray();
			for (FlowRecord fr : frs) {
				JSONObject frJson = new JSONObject();
				frJson.put("id", fr.getFrID());
				frJson.put("start_time", DateUtils.format(fr.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				frJson.put("operator", fr.getOperator());
				frJson.put("operation", fr.getOperation());
				frJson.put("notes", fr.getDetail());
				frJson.put("end_time", DateUtils.format(fr.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				results.add(frJson);
			}
			result.put("success",true);
			result.put("errorInfo","");
			result.put("results",results);
			return result.toString();
		} catch (E5Exception e) {
			result.put("success",false);
			result.put("errorInfo",e.getMessage());
			return result.toString();
		}
	}
	
	/**
	 * 预留多个字段，以备不时之需
	 * description 推送内容  默认为稿件标题，可传参（推送内容）
	 * pushTime    定时发布  默认为空，可传参(格式  yyyy-MM-dd HH:mm:ss)
	 *
	 * targetUser  目标用户  默认为0：全部用户，可指定1：标签用户
	 * regionIDS   区域代码  默认为空，目标用户为标签用户时，需指定区域代码
	 * 
	 * type        推送类型  默认为0，应该用不到
	 * topicID     相关选题  默认为0，应该用不到
	 */
	public String pushApp(HttpServletRequest request, String data) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = JSONObject.fromObject(data);
		int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
        //此功能是面向APP发布库独有的功能，web发布库不支持此功能
        if(docLibID != LibHelper.getArticleAppLibID()){ 
        	result.put("success",false);
			result.put("errorInfo","此功能是面向APP发布库独有的功能，web发布库不支持此功能");
			return result.toString();
        }
        int userID = NumberUtils.toInt(request.getParameter("userID"));
        try {
        	DocumentManager docManager = DocumentManagerFactory.getInstance();
        	//获取到发布库的文章
			Document doc = docManager.get(docLibID, docID);
			//获取消息推送任务的库ID
			int taskLibID = LibHelper.getLib(DocTypes.PUSHTASK.typeID()).getDocLibID();
			//创建新的消息推送任务
			Document task = docManager.newDocument(taskLibID);
			
			
			String description = jsonObject.optString("description", doc.getTopic());
			if(description.isEmpty()) {
                description = doc.getTopic();
            }
			task.setTopic(description); //标题
	        task.set("a_articleLibID", docLibID);
	        task.set("a_articleID", docID);
	        task.set("a_siteID", doc.getInt("A_SITEID"));
	        int atype = doc.getInt("A_TYPE");
	        task.set("a_type", atype);
	        if (atype == Article.TYPE_SPECIAL || atype == Article.TYPE_LIVE || atype == Article.TYPE_SUBJECT) {
	        	//若是专题或直播，则没有发布地址，此时需传递对应栏目ID
	        	String linkID = doc.getString("A_LINKID");
	            task.set("a_url", linkID);
	            task.set("a_urlPad", linkID);
	        } else {
	            task.set("a_url", doc.getString("A_URL"));
	            task.set("a_urlPad", doc.getString("A_URLPAD"));
	        }
	        String pushTime = jsonObject.optString("pushTime", "");
	        //若不为空，则设置定时发布
	        if(pushTime !=null && !pushTime.isEmpty() && !"".equals(pushTime)){
	        	task.set("push_time", pushTime);	
	        }
			int targetUser = jsonObject.optInt("targetUser", 0);
			String regionIDS = jsonObject.optString("regionIDS", "");
			int type = jsonObject.optInt("type", 0);
			int topicID = jsonObject.optInt("topicID", 0);
	        //若选择区域发送,则设置地区代码,若没有选择,则按全国推送处理
	    	task.set("push_region",getPushRegionCode(targetUser, regionIDS));
	        task.set("push_type", type); 
	        task.set("push_topicID", topicID);
	        docManager.save(task);
	        //获取用户，用来记录流程记录
	        UserReader userReader = (UserReader) Context.getBean(UserReader.class);
	        User user = userReader.getUserByID(userID);
	        //记录流程记录
	        LogHelper.writeLog(docLibID, docID, user.getUserName(), user.getUserID(), "APP消息推送", "手机端操作");
	        result.put("success",true);
	        result.put("errorInfo","");
	        JSONObject results = new JSONObject();
	        results.put("fileId",docID);
	        
	        int channel = -1;
	        if(docLibID == LibHelper.getOriginalLibID()) channel = 0;
	        if(docLibID == LibHelper.getArticleLibID()) channel = 1;
	        if(docLibID == LibHelper.getArticleAppLibID()) channel = 2;
	        
	        results.put("channel",channel);
	        result.put("results",results);
		} catch (E5Exception e) {
			result.put("success",false);
			result.put("errorInfo",e.getMessage());
		}
        return result.toString();
	}
	
	private String getPushRegionCode(int targetUser,String regionIDS)throws E5Exception{
		JSONArray codeJson = new JSONArray() ;
        if(targetUser == 1 && regionIDS != null && !"".equals(regionIDS)){
    		String[] ids = regionIDS.split(";") ;
    		Map<String,String> pIdsMap = new HashMap<>() ;
    		List<Integer> idslList = new ArrayList<>() ;
    		for(String i : ids){
    			//代码中的省份id
    			if(!i.contains("_")){
    				pIdsMap.put(i, "") ;
    				idslList.add(Integer.parseInt(i)) ;
        		}
    		}
    		Map<String,String> cIdsMap = new HashMap<>() ;
    		CatReader catReader = (CatReader) Context.getBean(CatManager.class);
    		for(String c : ids){
        		if(c.contains("_")){
        			//去除已有省份下的城市
        			String pID = c.split("_")[0] ;
        			String cID = c.split("_")[1] ;
        			if(pIdsMap.get(pID) == null){
        				if(cIdsMap.get(pID) == null){
        					cIdsMap.put(pID, cID) ;
        				}else{
        					cIdsMap.put(pID,cID + ";" + cIdsMap.get(pID));
        				}
        			}
        		}
        	}
    		Set<String> pIDSet = cIdsMap.keySet() ;
    		Iterator<String> iter = pIDSet.iterator() ;
    		while(iter.hasNext()){
    			String pID = iter.next() ;
    			Category[] cats = catReader.getChildrenCats(CatTypes.CAT_PUSHREGION.typeID(), Integer.parseInt(pID)) ;
    			String[] cIDs = cIdsMap.get(pID).split(";") ;
    			if(cIDs.length == cats.length){
    				idslList.add(Integer.parseInt(pID)) ;
    			}else{
    				for(String c : cIDs){
    					idslList.add(Integer.parseInt(c)) ;
    				}
    			}
    		}
    		Iterator<Integer> idIter = idslList.iterator() ;
    		
    		while(idIter.hasNext()){
    			Integer id = idIter.next() ;
    			codeJson.add(catReader.getCat(CatTypes.CAT_PUSHREGION.typeID(),id).getCatCode()) ;
    		}
    	}
        return codeJson.toString() ;
	}

    public String colIsOp(int userID, String data) {

        JSONObject jsonObject = JSONObject.fromObject(data);
        int siteID = jsonObject.getInt("siteID");
        int channel = jsonObject.getInt("channel");
        long colID = jsonObject.getLong("colID");

        JSONObject ret = new JSONObject();

        try {
            int colLibID = LibHelper.getColumnLibID();

            //根据用户id找到有操作权限的栏目
            Long[] ids = null;
            if(channel == 1){
                ids = colReader.getOpColumnIds(colLibID,userID,siteID,0);
            }else if(channel == 2){
                ids = colReader.getOpColumnIds(colLibID,userID,siteID,4);
            }
            List<Long> idLists = Arrays.asList(ids);

            if(idLists.contains(colID)) {
                ret.put("success",true);
                ret.put("errorInfo","");
                ret.put("op",true);
            } else {
                ret.put("success",true);
                ret.put("errorInfo","");
                ret.put("op",false);
            }

            return ret.toString();
        } catch (E5Exception e) {
            e.printStackTrace();
            ret.put("success",false);
            ret.put("errorInfo","获取失败");
            return ret.toString();
        }
    }

	public String colSearch(HttpServletRequest request, String data,int userID){
		JSONObject jsonObject = JSONObject.fromObject(data);
		int siteID = jsonObject.getInt("siteID");
		int channel = jsonObject.optInt("channel",-1);
		String key = jsonObject.getString("key");
		
		JSONObject result = new JSONObject();
		if(!StringUtils.isBlank(key) && !"null".equals(key)){
			try {
				int colLibID = LibHelper.getColumnLibID();
				boolean flag=key.matches("[0-9]+");
				Document[] cols = findColumn(colLibID, siteID, key, flag);
				
				//根据用户id找到有操作权限的栏目
				List<Long> idLists = new ArrayList<>();
				Long[] webOpColIDs = colReader.getOpColumnIds(colLibID,userID,siteID,0);
				Long[] appOpColIDs = colReader.getOpColumnIds(colLibID,userID,siteID,4);
	            if(channel == 1){
	            	idLists.addAll(Arrays.asList(webOpColIDs));
	            }else if(channel == 2){
	            	idLists.addAll(Arrays.asList(appOpColIDs));
	            }else if(channel == -1){
	            	idLists.addAll(Arrays.asList(webOpColIDs));
	            	idLists.addAll(Arrays.asList(appOpColIDs));
	            }
				
				JSONArray results = new JSONArray();
				for(Document col : cols) {
					//判断该栏目是否为有权限的栏目
					if(idLists.contains(col.getDocID())){
						JSONObject colJson = new JSONObject();
						colJson.put("columnID", col.getDocID());
						colJson.put("columnName", col.getString("COL_NAME"));
						colJson.put("cascadeID", col.getString("COL_CASCADEID"));
						colJson.put("cascadeName", col.getString("COL_CASCADENAME"));
						colJson.put("channel", col.getInt("COL_CHANNEL")+1);
						results.add(colJson);
					}
				}
				result.put("success",true);
				result.put("errorInfo","");
				result.put("results",results);
			} catch (E5Exception e) {
				result.put("success",false);
				result.put("errorInfo",e.getMessage());
			}
		}else{
			result.put("success",false);
			result.put("errorInfo","关键字不能为空！");
		}
		return result.toString();
	}
	
	private Document[] findColumn(int colLibID, int siteID, String name, boolean flag) throws E5Exception {
		//该方法参考com.founder.xy.column.ColumnManager.find()
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		System.out.println("%" + name + "%");
		if(flag==false){
			Document[] cols = docManager.find(colLibID,
					"(col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0", 
					new Object[] {"%" + name + "%", "%" + name + "%", siteID});
			return cols;
		}else{
			int sys_documentId=Integer.parseInt(name);
			Document[] cols = docManager.find(colLibID,
					//"SYS_DOCUMENTID like ? or ((col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0 ) ", 
					"SYS_DOCUMENTID like ? or (col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0 ", 
					new Object[] {sys_documentId+"%", "%" + name + "%", "%" + name + "%",  siteID });
			return cols;
		}
	}

    public String deleteArticle(int userID, String data){

        JSONObject jsonObject = JSONObject.fromObject(data);

        int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");
//        int channel = jsonObject.getInt("channel");
        //组装返回值
        JSONObject ret = new JSONObject();

        JSONObject json = new JSONObject();
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document article = docManager.get(docLibID, docID);

            if(article == null){
                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "稿件ID错误");

                ret.put("success",false);
                ret.put("errorInfo","稿件ID错误");
                ret.put("results",json);
                return ret.toString();
            }

            article.setDeleteFlag(1);

            String result = save(article, null, 3);
            if(result == null){

                // 记录日志
                UserReader userReader = (UserReader)Context.getBean(UserReader.class);
                User user = userReader.getUserByID(userID);
                LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                        user.getUserName(), user.getUserID(),
                        "删除", "");

                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "");

                ret.put("success",true);
                ret.put("errorInfo","");
                ret.put("results",json);
            }else{
                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "删除稿件失败");

                ret.put("success",false);
                ret.put("errorInfo", "删除稿件失败");
                ret.put("results",json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("fileId", docID);
//            json.put("channel", channel);
//            json.put("errorInfo", "获取稿件失败");

            ret.put("success",false);
            ret.put("errorInfo", e.getMessage());
            ret.put("results",json);
            return ret.toString();
        }

        return ret.toString();
    }

    public String getAuditArticles(HttpServletRequest request, String data) {
    	JSONObject jsonObject = JSONObject.fromObject(data);
    	int siteID = jsonObject.getInt("siteID");
    	int type = jsonObject.optInt("type", -1);
    	long lastID = jsonObject.optLong("lastID", 0);
    	int userID = NumberUtils.toInt(request.getParameter("userID"));
    	JSONObject result = new JSONObject();
    	//用户ID去判断有权限的sys_currentNode
    	String nodeIDs = StringUtils.join(getAuditNodeIDs(userID, siteID, false), ",");
    	String colIDs = StringUtils.join(getAuditColIDs(userID, siteID), ",");
    	if(StringUtils.isBlank(nodeIDs)){
    		result.put("success",false);
    		result.put("errorInfo","没有审核和驳回权限");
    		return result.toString();
    	}
    	if(StringUtils.isBlank(colIDs)){
    		result.put("success",false);
    		result.put("errorInfo","没有可操作的栏目");
    		return result.toString();
    	}
    	DocumentManager docManager = DocumentManagerFactory.getInstance();
    	String lastModified = null;
    	if (lastID > 0) {
    		try {
    			Document doc = docManager.get(LibHelper.getArticleLibID(), lastID);
    			if (doc == null) doc = docManager.get(LibHelper.getArticleAppLibID(), lastID);
    			if (doc != null) lastModified = doc.getString("SYS_LASTMODIFIED");
    		} catch (E5Exception e) { }
    	}
    	JSONArray results = new JSONArray();
    	Date oneMonth = DateUtils.getCalendarBefore(30).getTime();//默认30天时间内的待审核稿件
    	String select = "SELECT SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,a_type,a_status,a_pubTime,a_editor,a_columnID,a_picSmall,a_picMiddle,a_picBig,a_linkID,a_linkName,a_order,a_channel,SYS_LASTMODIFIED FROM ";
    	StringBuffer where = new StringBuffer(" WHERE ");
    	Object[] params = null;
    	String dbType = Context.getDBType();
        if("oracle".equals(dbType)){
            where.append(" SYS_LASTMODIFIED>=TO_DATE('").append(DateUtils.format(oneMonth,"yyyy-MM-dd HH:mm:ss")).append("','yyyy-mm-dd hh24:mi:ss')");
        }
        else if("mysql".equals(dbType)){
            where.append(" SYS_LASTMODIFIED>='").append(DateUtils.format(oneMonth,"yyyy-MM-dd HH:mm:ss")).append("'");
        }

        if(lastModified != null) {
    		where.append(" AND SYS_LASTMODIFIED<?");
    		params = new Object[]{lastModified,lastModified};
    	}
    	if (type > -1) {
    		where.append(" AND a_type=").append(type);
    	}
    	if(colIDs.split(",").length<1000) {
    		where.append(" AND a_columnid IN (").append(colIDs).append(") ");
    	}
    	where.append(" AND SYS_CURRENTNODE IN (").append(nodeIDs).append(") ")
//    	.append(" AND a_status=").append(Article.STATUS_AUDITING)
    	.append(" AND a_siteID=").append(siteID)
    	.append(" AND SYS_DELETEFLAG=0");
    	String orderBy = " ORDER BY SYS_LASTMODIFIED DESC"; //按照最新时间排序
    	
    	String sql = select + " ( " + select + " XY_ARTICLE " + where + " UNION " + select + " XY_ARTICLEAPP " + where + " ) UNIONTABLE " + orderBy;
    	DBSession db = null;
    	IResultSet rs = null;
    	try {
    		db = Context.getDBSession();
    		sql = db.getDialect().getLimitString(sql, 0, ALIST_COUNT);//sql做分页处理
    		rs = db.executeQuery(sql, params);
    		while (rs.next()){
    			JSONObject article = new JSONObject();
    			article.put("fileId", rs.getInt("SYS_DOCUMENTID"));
    			article.put("docLibID", rs.getInt("SYS_DOCLIBID"));
    			article.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
    			article.put("articleType", rs.getInt("a_type"));
    			article.put("status", rs.getInt("a_status"));
    			article.put("publishtime", ArticleDetailHelper.getDateString(rs.getTimestamp("a_pubTime")));
    			article.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
    			article.put("colID", rs.getInt("a_columnID"));
    			article.put("picSmall", ArticleDetailHelper.getWanPicPath(rs.getString("a_picSmall")));
    			article.put("picMiddle", ArticleDetailHelper.getWanPicPath(rs.getString("a_picMiddle")));
    			article.put("picBig", ArticleDetailHelper.getWanPicPath(rs.getString("a_picBig")));
                article.put("linkID",rs.getInt("a_linkID"));
                article.put("linkName",StringUtils.getNotNull(rs.getString("a_linkName")));
                article.put("channel",rs.getInt("a_channel"));
                results.add(article);
    		}
    		result.put("success",true);
    		result.put("errorInfo","");
    		result.put("results",results);
    	} catch (Exception e) {
    		result.put("success",false);
    		result.put("errorInfo",e.getMessage());
    	} finally {
    		ResourceMgr.closeQuietly(rs);
    		ResourceMgr.closeQuietly(db);
    	}
    	return result.toString();
    }
	/**
	 * 有审核流程的栏目
	 * @param userID
	 * @param siteID
	 * @return
	 */
	private long[] getAuditColIDs(int userID, int siteID) {
		int colLibID  = LibHelper.getColumnLibID();
		int userLibID = LibHelper.getUserExtLibID();
		List<Long> result =new ArrayList<>() ;
        try {
        	// 需审核的栏目
        	List<Column> cols = colReader.getAuditColumns(colLibID, siteID);
        	// 用户Web版可操作的栏目ID
        	long[] webIds = siteUserReader.getRelated(userLibID, userID, siteID, 0);

        	// 用户App版可操作的栏目ID
			long[] appIds = siteUserReader.getRelated(userLibID, userID, siteID, 4);

            for (Column col : cols) {
                // 栏目的父路径。任意一级路径有权限即可
                int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");

                for (long colID : path) {
                    if (ArrayUtils.contains(webIds, colID)
                            || ArrayUtils.contains(appIds, colID)) {
                        result.add(col.getId());
                        break;
                    }
                }
            }
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return ArrayUtils.getArrayLong(result);
	}

	/**
	 * 审核阶段的有操作权限的流程节点
	 * @param userID
	 * @param siteID
     * @param isAll
	 * @return
	 */
	private int[] getAuditNodeIDs(int userID, int siteID, boolean isAll) {
		List<Integer> flowNodeIDs = new ArrayList<Integer>();
		int userLibID = LibHelper.getUserExtLibID();
		SiteUserReader siteRoleReader = (SiteUserReader) Context.getBean("siteUserReader");
		int[] roleIDs = siteRoleReader.getRoles(userLibID, userID, siteID);
		if (roleIDs != null && roleIDs.length > 0) {
			FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
			FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
			int docTypeID = InfoHelper.getArticleTypeID();
			for (int roleID : roleIDs) {
				try {
					Flow[] flows = flowReader.getFlows(docTypeID);
					if (flows != null) {
						//找出稿件的所有流程，不包括第一个“无审批流程”
						for (int i = 1; i < flows.length; i++) {
							int flowID = flows[i].getID();
							//当取审核稿件列表时，得到这些流程的中间流程节点ID，也就是去掉第一个节点（第一个是未发布阶段，不是审批阶段）
							//和最后两个节点（在发布阶段、已发布阶段）
//                          //当取审核或者送审权限时，只去掉最后两个节点，因为驳回到第一个节点需要送审权限，也是提交，需要包含在内
							FlowNode[] nodes = flowReader.getFlowNodes(flowID);
							if (nodes != null) {
							    if(isAll){
                                    for (int j = 0; j < nodes.length - 2; j++) {
                                        //判断用户是否有审核权限
                                        int permission = fpReader.get(roleID, nodes[j].getFlowID(), nodes[j].getID());
                                        if (permission > 0) flowNodeIDs.add(nodes[j].getID());
                                    }
                                }else {
                                    for (int j = 1; j < nodes.length - 2; j++) {
                                        //判断用户是否有审核权限
                                        int permission = fpReader.get(roleID, nodes[j].getFlowID(), nodes[j].getID());
                                        if (permission > 0) flowNodeIDs.add(nodes[j].getID());
                                    }
                                }
							}
						}
					}
				} catch (E5Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ArrayUtils.getArray(flowNodeIDs);
	}

    public String pubArticle(int userID, String data){

        JSONObject jsonObject = JSONObject.fromObject(data);

        int docID = jsonObject.getInt("fileId");
        int docLibID = jsonObject.getInt("docLibID");

        //组装返回值
        JSONObject ret = new JSONObject();

        JSONObject json = new JSONObject();
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document article = docManager.get(docLibID, docID);

            if(article == null){
                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "稿件ID错误");

                ret.put("success",false);
                ret.put("errorInfo","稿件ID错误");
                ret.put("results",json);
                return ret.toString();
            }

            article.set("a_status", Article.STATUS_PUB_ING);
            UserReader userReader = (UserReader)Context.getBean(UserReader.class);
            User user = userReader.getUserByID(userID);
            article.set("a_lastPublish", user.getUserName());
            article.set("a_lastPublishID", user.getUserID());

            String result = save(article, null, 4);//4 发布
            if(result == null){

                PublishTrigger.article(article);

                // 记录日志
                LogHelper.writeLog(article.getDocLibID(), article.getDocID(),
                        user.getUserName(), user.getUserID(),
                        "发布", "");

                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "");

                ret.put("success",true);
                ret.put("errorInfo","");
                ret.put("results",json);
            }else{
                json.put("fileId", docID);
//                json.put("channel", channel);
//                json.put("errorInfo", "发布稿件失败");

                ret.put("success",false);
                ret.put("errorInfo", "发布稿件失败");
                ret.put("results",json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("fileId", docID);
//            json.put("channel", channel);
//            json.put("errorInfo", "获取稿件失败");

            ret.put("success",false);
            ret.put("errorInfo", e.getMessage());
            ret.put("results",json);
            return ret.toString();
        }

        return ret.toString();
    }

    /**
	 * 审核通过或驳回
	 * @param data
	 * @param userID
	 * @param pass  true:通过  false:驳回
	 * @return
	 */
	public String transfer(String data, int userID, boolean pass) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		JSONArray articles = jsonObject.getJSONArray("articles");
		String detail = jsonObject.optString("detail","");
		int siteID = jsonObject.getInt("siteID");
        UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		JSONObject result = new JSONObject();

		//获取用户，用来记录流程记录
		User user;
		try {
			user = userReader.getUserByID(userID);
		} catch (E5Exception e1) {
			result.put("success", false);
			result.put("errorInfo", "获取用户信息失败！");
			return result.toString();
		}
		
		JSONArray results = new JSONArray();
		JSONArray faileds = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		int[] nodeIDs = getAuditNodeIDs(userID, siteID, true);
    	long[] colIDs = getAuditColIDs(userID, siteID);
		for (int i = 0; i < articles.size(); i++) {
			JSONObject article = articles.getJSONObject(i);
			long docID = article.getLong("fileId");
			int docLibID = article.getInt("docLibID");
			JSONObject fileId = new JSONObject();
			fileId.put("fileId", docID);
			try {
				Document doc = docManager.get(docLibID, docID);
				if(doc != null && (!doc.isLocked() || doc.getCurrentUserID()==userID)){
					//每次操作仍然需要验证权限，因为手机端操作时没有锁，因此可能与网页版的有重复操作或非法操作
					if(!ArrayUtils.contains(nodeIDs, doc.getCurrentNode()) || 
							!ArrayUtils.contains(colIDs,doc.getLong("a_columnId"))){
						faileds.add(fileId);
						continue;
					}
					
					int userLibID = LibHelper.getUserExtLibID();
					Role[] roles = getRolesBySite(userLibID, userID, siteID);
	                //按角色个数，单个取角色ID，多个取合并后的角色ID
					int roleID = roles[0].getRoleID();
			        int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
					FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
					boolean canPublish  = false; // 是否有发布权限
					boolean canAuditing = false; // 是否有送审权限
					boolean canRejected = false; // 是否有驳回权限
					String pubProcName = null, audProcName = null, rejProcName = null;
					FlowNode currNode = flowReader.getFlowNode(doc.getCurrentNode());
					ProcReader procReader = (ProcReader)Context.getBean(ProcReader.class);
					ProcFlow[] procs = procReader.getProcs(currNode.getID());
					for(ProcFlow proc : procs){
						Operation op = procReader.getOperation(proc.getOpID());
						if(pass && !canPublish && op.getName().equals("发布")) {
							pubProcName = proc.getProcName();
							canPublish = fpReader.hasPermission(newRoleID, currNode.getFlowID(), currNode.getID(), pubProcName);
						}
						if(pass && !canAuditing && op.getName().equals("提交")) {
							audProcName = proc.getProcName();
							canAuditing = fpReader.hasPermission(newRoleID, currNode.getFlowID(), currNode.getID(), audProcName);
						}
						if(!pass && !canRejected && op.getName().equals("驳回")) {
							rejProcName = proc.getProcName();
							canRejected = fpReader.hasPermission(newRoleID, currNode.getFlowID(), currNode.getID(), rejProcName);
						}
					}
					
					if ((pass && (canPublish || canAuditing)) | (!pass && canRejected)) {
						//通过获取下一个节点，驳回获取前一个流程
						FlowNode nextNode = pass ? flowReader.getNextFlowNode(doc.getCurrentNode()) : flowReader.getPreFlowNode(doc.getCurrentNode());
						doc.setCurrentFlow(nextNode.getFlowID());
						doc.setCurrentNode(nextNode.getID());
						doc.setCurrentStatus(nextNode.getWaitingStatus());
						doc.setCurrentUserID(user.getUserID());
						doc.setCurrentUserName(user.getUserName());
						doc.setLastmodified(new Timestamp((new Date()).getTime()));
						
						//根据当前流程节点，获取稿件状态和流程操作
						String operation = null;
						if (pass) { //true:通过  false:驳回
							if(canAuditing) {
								doc.set("a_status", Article.STATUS_AUDITING);
								operation = audProcName;
							} else {
								doc.set("a_status", Article.STATUS_PUB_ING);
								//发布时，设置最后发布人
								doc.set("a_lastPublish", user.getUserName());
								doc.set("a_lastPublishID", user.getUserID());
								operation = pubProcName;
							} 
						} else {
							doc.set("a_status", Article.STATUS_REJECTED);
//							operation = rejProcName;
                            operation = "驳回";
						}
						doc.setLocked(false);
						//保存修改后的稿件
						docManager.save(doc);
						
						//如果稿件为在发布状态，发送稿件发布消息
						PublishTrigger.article(doc);
						
						//记录流程记录
						LogHelper.writeLog(docLibID, docID, user.getUserName(), user.getUserID(), operation, detail);
						results.add(fileId);
					} else { //稿件当前非审核流程节点，不做审核通过操作
						faileds.add(fileId);
					}

				}else{ //doc为空，记录ID到失败
					faileds.add(fileId);
				}
			} catch (Exception e) {
				faileds.add(fileId);
			}
		}
		result.put("success", true);
		result.put("errorInfo", "");
		result.put("results", results);
		result.put("faileds", faileds);
		return result.toString();
	}

    public String revokeArticle(int userID, String data){

        JSONObject jsonObject = JSONObject.fromObject(data);

        JSONArray fileArray = jsonObject.getJSONArray("articles");
        String detail = jsonObject.getString("detail");

        //组装返回值
        JSONObject ret = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        StringBuilder sbFileIds = new StringBuilder();
        int docLibID = 0;
        List<Long> docIDs = new ArrayList<Long>();

        for(int i = 0;i < fileArray.size();i++){
            JSONObject json = fileArray.getJSONObject(i);
            if(i == 0){
                docLibID = json.getInt("docLibID");
                sbFileIds.append(json.getString("fileId"));
            }else {
                sbFileIds.append(",").append(json.getString("fileId"));
            }
            docIDs.add(json.getLong("fileId"));
        }

        String fileIds = sbFileIds.toString();

        if(!StringUtils.isBlank(fileIds)){
            try {
                String queryResult = getLinkDocIDs(docLibID,fileIds,docIDs);

                if(queryResult == null){
                    FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
                    DocumentManager docManager = DocumentManagerFactory.getInstance();
                    List<Document> articles = new ArrayList<>();
                    for(long docID : docIDs){
                        Document article = docManager.get(docLibID, docID);
                        if(article == null){
                            System.out.println("稿件ID错误"+docID);
                            ret.put("success",false);
                            ret.put("errorInfo","稿件ID错误");
                            ret.put("results",jsonArray);
                            return ret.toString();
                        }

                        int curflowID = article.getCurrentFlow();
                        FlowNode[] nodes = flowReader.getFlowNodes(curflowID);
                        article.setCurrentNode(nodes[0].getID());
                        article.setCurrentStatus(nodes[0].getWaitingStatus());
                        //解锁操作
                        article.setLocked(false);
                        article.set("a_status", Article.STATUS_REVOKE);

                        articles.add(article);
                    }

                    //同时修改多个稿件，使用事务
                    String result = save(docLibID, articles);
                    if (result == null) {
                        //发布撤稿消息
                        articleManager.revoke(articles);
                        //撤稿中心调用的操作，手工写操作日志、返回
                        UserReader userReader = (UserReader)Context.getBean(UserReader.class);
                        User user = userReader.getUserByID(userID);
                        // 记录日志
                        for (long docID : docIDs) {
                            LogHelper.writeLog(docLibID, docID, user.getUserName(),
                                    user.getUserID(), "撤稿", detail);
                        }
                    } else {
                        ret.put("success",false);
                        ret.put("errorInfo", "撤稿失败");
                        ret.put("results",jsonArray);
                        return ret.toString();
                    }
                }else{
                    ret.put("success",false);
                    ret.put("errorInfo", "查询链接稿件失败");
                    ret.put("results",jsonArray);
                    return ret.toString();
                }
            } catch (E5Exception e) {
                e.printStackTrace();
                ret.put("success",false);
                ret.put("errorInfo", e.getMessage());
                ret.put("results",jsonArray);
                return ret.toString();
            }
        }

        ret.put("success",true);
        ret.put("errorInfo", "");
        ret.put("results",jsonArray);
        return ret.toString();

    }

    private String getLinkDocIDs(int docLibID, String fileIds, List<Long> docIDs) throws E5Exception{

        String sql = "SELECT SYS_DOCUMENTID FROM " + LibHelper.getLibTable(docLibID)
                +" WHERE sys_deleteFlag = 0 AND a_originalID in ( "+fileIds+" ) AND a_type = " + Article.TYPE_LINK +" AND a_status = "+ Article.STATUS_PUB_DONE ;
        DBSession db = null;
        IResultSet rs = null;
        try {
            db = InfoHelper.getDBSession(docLibID);
            rs = db.executeQuery(sql);

            while (rs.next()) {
                docIDs.add(rs.getLong("SYS_DOCUMENTID"));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(sql);
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
        }
    }

    /**
     * 多个稿件的统一提交， 出错时返回错误信息
     */
    private String save(int docLibID, List<Document> articles) {
        if (articles == null || articles.size() == 0) return null;

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        //同时修改多个稿件，使用事务
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(docLibID);
            conn.beginTransaction();

            for (Document article : articles) {
                docManager.save(article, conn);
            }
            conn.commitTransaction();
            return null;
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    public String getAuditCols(int userID, String data) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        int siteID = jsonObject.getInt("siteID");

        JSONObject ret = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            // 取出本站点下所有的审批栏目
            int colLibID = LibHelper.getColumnLibID();
            List<Column> cols = colReader.getAuditColumns(colLibID, siteID);

            int userLibID = LibHelper.getUserExtLibID();
            // 用户Web版可操作的栏目ID
            long[] ids = siteUserReader.getRelated(userLibID, userID, siteID, 0);
            filter(jsonArray, cols, ids);

            // 用户App版可操作的栏目ID
            ids = siteUserReader.getRelated(userLibID, userID, siteID, 4);
            filter(jsonArray, cols, ids);

            ret.put("success",true);
            ret.put("errorInfo","");
            ret.put("results",jsonArray);
            return ret.toString();
        } catch (E5Exception e) {
            e.printStackTrace();
            ret.put("success",false);
            ret.put("errorInfo","获取失败");
            return ret.toString();
        }
    }

    private void filter(JSONArray jsonArray, List<Column> cols, long[] ids) {
        for (Column col : cols) {
            // 栏目的父路径。任意一级路径有权限即可
            int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
            for (long colID : path) {
                if (ArrayUtils.contains(ids, colID)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("columnID",col.getId());
                    jsonObject.put("columnName",col.getName());
                    jsonObject.put("cascadeName",col.getCasNames());
                    jsonObject.put("channel",col.getChannel()+1);
                    jsonArray.add(jsonObject);
                    break;
                }
            }
        }
    }
    public String getVideoGroup(String data, HttpServletRequest request) {
		//如果后续有可能有图片分组、模板分组、专题分组，参考com.founder.xy.workspace.Main#groupSelect,图片和专题涉及分组权限
		JSONObject jsonObject = JSONObject.fromObject(data);
		int siteID = jsonObject.getInt("siteID");
		int catTypeID = CatTypes.CAT_VIDEO.typeID();
		JSONObject result = new JSONObject();
		JSONArray results = new JSONArray();
		Category[] roots = InfoHelper.getCatGroups(request, catTypeID, siteID);
		if (roots != null && roots.length > 0) {
			try {
				for (Category root : roots) {
					JSONObject cat = new JSONObject();
					cat.put("id", root.getCatID());
					cat.put("name", root.getCatName());
					results.add(cat);
				}
				result.put("success",true);
				result.put("errorInfo","");
				result.put("results",results);
			} catch (Exception e) {
				result.put("success",false);
				result.put("errorInfo",e.getMessage());
			}
		} else {
			result.put("success",false);
			result.put("errorInfo","未查询到分组！");
		}
		return result.toString();
	}

	public String getVideos(String data, int userID) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		int groupID = jsonObject.getInt("groupID");
		int lastID = jsonObject.optInt("lastID",0);
		JSONObject result = new JSONObject();
		JSONArray results = new JSONArray();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		int videoLibID = LibHelper.getVideoLibID();
		int userLibID = LibHelper.getUserExtLibID();
		
		Date createdTime = null;
		if (lastID > 0) {
			try {
				Document video = docManager.get(videoLibID, lastID);
				if (video != null) createdTime = video.getCreated();
			} catch (E5Exception e) { }
		}
		String sql = "SELECT SYS_DOCUMENTID,SYS_DOCLIBID,SYS_TOPIC,SYS_CURRENTFLOW,SYS_CURRENTNODE,v_siteID,v_status,v_process,v_time,v_picPath FROM xy_video WHERE v_catID=?";
		if(createdTime != null) sql += " AND SYS_CREATED<?";
		sql += " AND SYS_DELETEFLAG=0 ORDER BY SYS_CREATED DESC";
		Object[] values = createdTime == null ? new Object[]{groupID} : new Object[]{groupID,createdTime};
		DBSession db = null;
        IResultSet rs = null;
        try {
            db = Context.getDBSession();
            sql = db.getDialect().getLimitString(sql, 0, ALIST_COUNT);//sql做分页处理
            rs = db.executeQuery(sql, values);
	        while (rs.next()){
	            JSONObject video = new JSONObject();
	            video.put("fileId", rs.getInt("SYS_DOCUMENTID"));
	            video.put("docLibID", rs.getInt("SYS_DOCLIBID"));
	            video.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
	            video.put("status", rs.getInt("v_status"));
	            //video.put("time", rs.getDate("v_time"));
	            video.put("time", StringUtils.getNotNull(rs.getString("v_time")));
	            video.put("picPath", ArticleDetailHelper.getWanPicPath(rs.getString("v_picPath")));
	            video.put("process", rs.getInt("v_process"));
	            
	            JSONArray operations = new JSONArray();
	            int flowID = rs.getInt("SYS_CURRENTFLOW");
	            int nodeID = rs.getInt("SYS_CURRENTNODE");
	            int siteID = rs.getInt("v_siteID");
				Role[] roles = getRolesBySite(userLibID, userID, siteID);
				int roleID = (roles.length == 1) ? roles[0].getRoleID() : PermissionHelper.mergeRoles(roles);
				if (fpReader.hasPermission(roleID, flowID, nodeID, "发布")) {
					JSONObject operation = new JSONObject();
					operation.put("code", "publish");
					operation.put("name", "发布");
					operations.add(operation);
				}
				if (fpReader.hasPermission(roleID, flowID, nodeID, "取消发布")) {
					JSONObject operation = new JSONObject();
					operation.put("code", "revoke");
					operation.put("name", "取消发布");
					operations.add(operation);
				}
	            video.put("operation", operations);
	            
	            results.add(video);
	        }
	        result.put("success",true);
	        result.put("errorInfo","");
	        result.put("results",results);
	    } catch (Exception e) {
	    	result.put("success",false);
			result.put("errorInfo",e.getMessage());
	    } finally {
	        ResourceMgr.closeQuietly(rs);
	        ResourceMgr.closeQuietly(db);
	    }
		
		return result.toString();
	}

	public String pubVideo(String data, int userID, boolean isPub) {
		JSONObject jsonObject = JSONObject.fromObject(data);
		int docLibID = jsonObject.getInt("docLibID");
		long docID = jsonObject.getLong("fileId");
		JSONObject result = new JSONObject();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
        UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		
		//获取用户，用来记录流程记录
		User user = null;
		try {
			user = userReader.getUserByID(userID);
		} catch (E5Exception e) {
			result.put("success", false);
			result.put("errorInfo", "获取用户信息失败！");
			return result.toString();
		}
				
		try {
			Document video = docManager.get(docLibID, docID);
			int siteID = video.getInt("v_siteID");
			int userLibID = LibHelper.getUserExtLibID();
			Role[] roles = getRolesBySite(userLibID, userID, siteID);
            //按角色个数，单个取角色ID，多个取合并后的角色ID
			int roleID = (roles.length == 1) ? roles[0].getRoleID() : PermissionHelper.mergeRoles(roles);
			//是否有发布或取消发布权限
			boolean canPublish = fpReader.hasPermission(roleID, video.getCurrentFlow(), video.getCurrentNode(), "发布");
			boolean canCancel = fpReader.hasPermission(roleID, video.getCurrentFlow(), video.getCurrentNode(), "取消发布");
			if((isPub & canPublish) || (!isPub & canCancel)){
				result = publishVideo(video, user, isPub);
			}
		} catch (Exception e) {
			result.put("success",false);
			result.put("errorInfo",e.getMessage());
		}
		return result.toString();
	}
	
    private JSONObject publishVideo(Document video, User user,boolean isPub){
    	JSONObject result = new JSONObject();
    	//纳加视频服务器密码
		String passWord = InfoHelper.getConfig("视频系统", "服务器密码");
		String playerUrl = InfoHelper.getConfig("视频系统", "视频播放地址");
		String transDisk = InfoHelper.getConfig("视频系统", "转码文件根目录");
		String mappedDisk = InfoHelper.getConfig("视频系统", "点播映射磁盘");
		VideoManager videoManager = (VideoManager) Context.getBean(VideoManager.class);
		VJVodServicePortType vodPortType = videoManager.getVodService();

		boolean succ = true;
		String errorInfo = "";
		String operation = "";

		String transDirFile = video.getString("v_transPath");
		String format = video.getString("v_format");
		String[] pubFileTypes = format.split(",");

		for (int i = 0; i < pubFileTypes.length; i++) {
			StringHolder hash = new StringHolder();
			String path = transDirFile + "." + pubFileTypes[i];
			//需要对映射文件路径进行转换
			String filePath = null;
			if (mappedDisk != null && !mappedDisk.isEmpty()) {
				filePath = path.replaceFirst(Matcher.quoteReplacement(transDisk), Matcher.quoteReplacement(mappedDisk));
			} else {
				filePath = path;
			}
			// 发布状态返回值
			IntHolder statusResult = new IntHolder();
			boolean flag = true;
			for (int x = 0, len = 3; x < len && flag; x++) { //发布或取消发布失败重复三次
				if (flag) {
					try {
						if (isPub) //发布操作
							vodPortType.publishFile(passWord, filePath, hash, statusResult);
						else //取消发布操作
							vodPortType.unpublishFile(passWord, filePath, hash, statusResult);
						flag = false;
					} catch (RemoteException e) {
						flag = true;
					}
				}
			}
			
			if (isPub) { //发布后续操作
				video.set("v_status", 2);
				operation = "发布";
				if (statusResult.value == 0 || statusResult.value == -32) {
					Pattern hashPattern = Pattern.compile("^hash=\\s*([^&]*)");
					Matcher ma = hashPattern.matcher(hash.value);
					String hashCode = "";
					if (ma.find()) {
						hashCode = ma.group(1);
					}
					String player = playerUrl + hashCode;
					if ("flv".equals(pubFileTypes[i])) {
						video.set("v_url", player + ".flv");
						video.set("v_hashcode", hashCode);
					} else if ("mp3".equals(pubFileTypes[i])) {
						video.set("v_url", player + ".mp3");
						video.set("v_urlApp", player + ".mp3");
						video.set("v_hashcodeApp", hashCode);
						video.set("v_hashcode", hashCode);
					} else {
						video.set("v_urlApp", player + ".mp4");
						video.set("v_hashcodeApp", hashCode);
					}
				} else { //取消发布后续操作
					errorInfo = "发布失败,返回值：" + statusResult.value;
					succ = false;
					break;
				}
			} else {
				video.set("v_status", 1);
				operation = "取消发布";
				 if (statusResult.value != 0 && statusResult.value != -35) {
					 errorInfo = "取消发布失败,返回值：" + statusResult.value;
					 succ = false;
					 break;
				 }
			}
		}
		
		if (succ){
			//进行后处理：改变流程状态、解锁、刷新列表
			try {
				FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
				FlowNode nextNode = isPub ? flowReader.getNextFlowNode(video.getCurrentNode()) : flowReader.getPreFlowNode(video.getCurrentNode());
				video.setCurrentFlow(nextNode.getFlowID());
				video.setCurrentNode(nextNode.getID());
				video.setCurrentStatus(nextNode.getWaitingStatus());
				video.setCurrentUserID(user.getUserID());
				video.setCurrentUserName(user.getUserName());
				video.setLastmodified(new Timestamp((new Date()).getTime()));
				//保存
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				docManager.save(video);
				LogHelper.writeLog(video.getDocLibID(), video.getDocID(), user.getUserName(), user.getUserID(), operation, "");
				JSONObject results = new JSONObject();
				results.put("fileId", video.getDocID());
				result.put("success",true);
				result.put("errorInfo","");
				result.put("results",results);
			} catch (Exception e) {
				result.put("success",false);
				result.put("errorInfo","保存失败");
			}
		} else {
			result.put("success",succ);
			result.put("errorInfo",errorInfo);
		}
		//记录流程记录
		return result;
    }

	public String uploadVideo(HttpServletRequest request, boolean isVideo){
		JSONObject result = new JSONObject();
		
		//获取用户信息
		int userID = Integer.valueOf(request.getParameter("userID"));
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		User currentUser = null;
		try {
			currentUser = userReader.getUserByID(userID);
		} catch (E5Exception e1) { }
		if (currentUser == null) {
			result.put("success", false);
			result.put("errorInfo", "获取用户信息失败");
			return result.toString();
		}
			
		try {
			//实际存储路径
			String tenantCode = currentUser.getProperty1();
			String fileDatePath = DateUtils.format("yyyyMM\\dd\\");
			long uploadTime = System.currentTimeMillis();
			String fileMd5Path = EncodeUtils.getMD5(String.valueOf(uploadTime + '|' + new Random().nextInt(1)));
			StorageDevice device = InfoHelper.getVideoDevice();
			String videoPath = InfoHelper.getDevicePath(device);
			String filename = request.getParameter("filename");
			String targetPath = FilenameUtils.normalize(videoPath + "\\" + tenantCode + "\\source\\" + fileDatePath + fileMd5Path + "\\" + filename);
			String transPath = FilenameUtils.normalize(videoPath + "\\" + tenantCode + "\\trans\\" + fileDatePath + fileMd5Path + "\\" + filename.substring(0, filename.lastIndexOf(".")));
			
			//存储文件
			MultipartHttpServletRequest multipartRequest = new CommonsMultipartResolver( request.getSession().getServletContext()).resolveMultipart(request);
			MultipartFile file = multipartRequest.getFile("file");
			InputStream is = null;
			OutputStream out = null;
			try {
			    is = file.getInputStream();
			    File targetFile = new File(targetPath);
			    if (!targetFile.getParentFile().exists()) {
			        targetFile.getParentFile().mkdirs();
			    }
			    out = new FileOutputStream(targetFile);
			    IOUtils.copy(is, out);
			} catch(Exception e) {
				
			} finally {
			    IOUtils.closeQuietly(is);
			    IOUtils.closeQuietly(out);
			}
			
			//创建新的Document
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int docLibID = LibHelper.getVideoLibID();
			long docID = EUID.getID("DocID" + docLibID);
			Document doc = docManager.newDocument(docLibID, docID);
			//初始化doc
			ProcHelper.initDoc(doc);
			//设置用户信息
			doc.setAuthors(currentUser.getUserName());
			doc.setCurrentUserID(currentUser.getUserID());
			doc.setCurrentUserName(currentUser.getUserName());
			
			//设置其他信息
			doc.set("v_fileName", filename);
			doc.set("v_path", targetPath);
			doc.set("v_transPath", transPath);
			doc.setTopic(StringUtils.getNotNull(request.getParameter("title")));
			doc.set("v_siteID", NumberUtils.toInt(request.getParameter("siteID")));//必须
			doc.set("v_catID", NumberUtils.toInt(request.getParameter("catID")));//必须
			doc.set("v_tag", request.getParameter("tag"));
			doc.set("v_content", request.getParameter("content"));
			
			String operation = null;
			if (isVideo) {
				operation = "上传";
				String videoSize = InfoHelper.getConfig("视频系统", "默认分辨率");
				String resWidth = StringUtils.getNotNull(request.getParameter("resWidth"));
				if (resWidth.isEmpty()) resWidth = videoSize.substring(0, videoSize.lastIndexOf("*"));
				String resHeight = StringUtils.getNotNull(request.getParameter("resHeight"));
				if (resHeight.isEmpty()) resHeight = videoSize.substring(videoSize.lastIndexOf("*") + 1, videoSize.length());
				String bitrate = StringUtils.getNotNull(request.getParameter("bitrate"));
				if (bitrate.isEmpty()) bitrate = InfoHelper.getConfig("视频系统", "默认码率");
				String format = InfoHelper.getConfig("视频系统", "转码文件后缀");
				doc.set("v_format", format);
				doc.set("v_resWidth", NumberUtils.toInt(resWidth));
				doc.set("v_resHeight", NumberUtils.toInt(resHeight));
				doc.set("v_bitrate", NumberUtils.toInt(bitrate));
				//doc.set("v_time", request.getParameter("vtime"));
				doc.set("v_picPath", StringUtils.getNotNull(request.getParameter("picPath")));//截图采用上传图片接口,获取地址
				docManager.save(doc);
		        //-------------------------------------------------------------------------------
				doc = docManager.get(doc.getDocLibID(), doc.getDocID());
				if (StringUtils.isBlank(doc.getString("v_path")) || !doc.getString("v_path").toLowerCase().endsWith(".mp3")) {
					VideoManager videoManager = (VideoManager) Context.getBean("videoManager");
					videoManager.transCode(doc);

					//获取视频的播放时长
					IntHolder mfiResult = new IntHolder();
					MediaFileInfoHolder mfi = new MediaFileInfoHolder();

					//纳加视频服务器密码
					String passWord = InfoHelper.getConfig("视频系统", "服务器密码");

					VJVASPortType vasPortType = null;
					// 纳加点播管理操作
					vasPortType = videoManager.getVasMisService();
					vasPortType.getMediaFileInfo(passWord, doc.getString("v_path"), mfi, mfiResult);
					String time = null;
					if (mfiResult.value == 0) {
						MediaFileInfo mediaInfo = mfi.value;
						time = InfoHelper.formatTime(mediaInfo.getDuration());

					}
					doc.set("v_time", time);
					if (doc.getString("v_picPath").isEmpty()) {
						//判断有无裁图
						videoManager.snapMediaPicture(request, doc);
					} else {
						videoManager.copyPicture(request, doc);
					}
				}
			} else {
				operation = "上传音频";
				doc.set("v_format","mp3");
				doc.set("v_status", 1);
				//设为下一个流程节点
				FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
				FlowNode nextNode = flowReader.getNextFlowNode(doc.getCurrentNode());
				if (nextNode != null) {
					doc.setCurrentNode(nextNode.getID());
					doc.setCurrentStatus(nextNode.getWaitingStatus());
				}
			}
			//保存doc
			docManager.save(doc);
			
			//增加流程记录 上传  上传音频
			LogHelper.writeLog(doc.getDocLibID(), doc.getDocID(), currentUser.getUserName(), currentUser.getUserID(), operation, "");
			
			JSONArray fileList = new JSONArray();		// fileList
			JSONObject fileJson = new JSONObject();
			fileJson.put("fileId", doc.getDocID());
			fileJson.put("path", ArticleDetailHelper.getWanPicPath(doc.getString("v_picPath")));
			fileList.add(fileJson);
			result.put("success", true);
			result.put("errorInfo", "");
			result.put("fileList", fileList);
		} catch (Exception e) {
			result.put("success", true);
			result.put("errorInfo", e.getMessage());
			result.put("fileList", "[]");
		}
        return result.toString();
	}

	public String getUrl(){
        JSONObject ret = new JSONObject();
        JSONObject json = new JSONObject();

        String xyUrl = InfoHelper.getConfig("互动", "翔宇移动采编外网Api地址");
        String cbUrl = InfoHelper.getConfig("互动", "采编Api地址");

        json.put("server_url_xy",StringUtils.getNotNull(xyUrl).trim());
        json.put("server_url_cb",StringUtils.getNotNull(cbUrl).trim());

        ret.put("success",true);
        ret.put("errorInfo","");
        ret.put("results",json);

        return ret.toString();
    }
	

	public String myInfo(int userID) {
		JSONObject result = new JSONObject();
		try {
			int docLibID = LibHelper.getUserExtLibID();
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document user = docManager.get(docLibID, userID);
			if(user == null) throw new Exception("用户不存在");
			JSONObject results = new JSONObject();
			results.put("code", user.getString("u_code"));
			results.put("name", user.getString("u_name"));
			results.put("org", user.getString("u_org"));
			results.put("email", StringUtils.getNotNull(user.getString("u_email")));
			results.put("mobile", StringUtils.getNotNull(user.getString("u_mobile")));
			results.put("phone", StringUtils.getNotNull(user.getString("u_phone")));
			results.put("icon", ArticleDetailHelper.getWanPicPath(user.getString("u_icon")));
			result.put("success", true);
			result.put("errorInfo", "");
			result.put("results", results);
		} catch (Exception e) {
			result.put("success", false);
			result.put("errorInfo", e.getMessage());
			result.put("results", "{}");
		}
		return result.toString();
	}

    /**
     * 判断当前用户是否管理员。
     * @param userID
     * @return
     */
    private boolean isAdmin(int userID) {
        UserReader userReader = (UserReader) Context.getBean(UserReader.class);
        try {
            User user = userReader.getUserByID(userID);
            return "1".equals(user.getProperty2());
        } catch (E5Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //带父节点（无权限）的栏目树的json
    private JSONArray jsonTreeWithParentNew(Column[] cols,int parentID) throws E5Exception {
        List<Column> roots = getRootsNew(cols,parentID);

        return ColumnTreeHelper.jsonTreeWithParent(roots);
    }

    //根据指定的栏目，得到从根栏目开始的栏目树对象
    private List<Column> getRootsNew(Column[] cols, int parentID) throws E5Exception {
        if (cols == null)
            return null;

        //保证顺序
        Map<Integer, Column> tree = new HashMap<Integer, Column>();

        int depth = 0;

        for (Column col : cols) {
            //把无权限的父节点也带上
            int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
            if(path.length > depth){
                for(int i = 0;i<path.length;i++){
                    if(path[i] == parentID){
                        if(depth == 0){
                            depth = i+1;
                        }

                        Column pCol = tree.get(path[depth]);
                        if (pCol == null) {
                            pCol = getCol(col.getLibID(), path[depth]);
                            if (pCol != null)
                                tree.put(path[depth], pCol);
                        } else if (pCol.isEnable()) {
                            //栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
                            continue;
                        }

                        if (path.length == depth+1) {
                            pCol.setEnable(true);
                            pCol.removeChildren(); //节点设置为enable后就可动态展开，不需要设置children
                            continue;
                        }

                        Column parent = pCol;
                        for (int j = depth+1; j < path.length; j++) {
                            Column son = parent.getChild(path[j]);
                            if (son == null) {
                                son = getCol(col.getLibID(), path[j]);
                                if (son != null)
                                    parent.addChild(son);
                            } else if (son.isEnable()) {
                                break;
                            }
                            //最后一级的栏目，是确实有权限的，所以enable=true
                            if (j == path.length - 1) {
                                son.setEnable(true);
                            }
                            parent = son;
                        }
                    }
                }
            }

        }
        return ColumnTreeHelper.sortColByOrder(tree);
    }

    //带父节点（无权限）的栏目树的json
    private JSONArray jsonTreeWithParent(Column[] cols) throws E5Exception {
        List<Column> roots = getRoots(cols);

        return ColumnTreeHelper.jsonTreeWithParent(roots);
    }

    //根据指定的栏目，得到从根栏目开始的栏目树对象
    private List<Column> getRoots(Column[] cols) throws E5Exception {
        if (cols == null)
            return null;

        //保证顺序
        Map<Integer, Column> tree = new HashMap<Integer, Column>();

        for (Column col : cols) {
            //把无权限的父节点也带上
            int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");
            Column pCol = tree.get(path[0]);
            if (pCol == null) {
                pCol = getCol(col.getLibID(), path[0]);
                if (pCol != null)
                    tree.put(path[0], pCol);
            } else if (pCol.isEnable()) {
                //栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
                continue;
            }

            if (path.length == 1) {
                pCol.setEnable(true);
                pCol.removeChildren(); //节点设置为enable后就可动态展开，不需要设置children
                continue;
            }

            Column parent = pCol;
            for (int i = 1; i < path.length; i++) {
                Column son = parent.getChild(path[i]);
                if (son == null) {
                    son = getCol(col.getLibID(), path[i]);
                    if (son != null)
                        parent.addChild(son);
                } else if (son.isEnable()) {
                    break;
                }
                //最后一级的栏目，是确实有权限的，所以enable=true
                if (i == path.length - 1) {
                    son.setEnable(true);
                }
                parent = son;
            }
        }
        return ColumnTreeHelper.sortColByOrder(tree);
    }

    //根据栏目ID得到Col对象
    private Column getCol(int colLibID, long id) throws E5Exception {
        boolean enable = false;

        Column parent = colReader.get(colLibID, id);
        if (parent == null) return null;

        return new Column(id, parent.getName(), parent.getCasIDs(), parent.getCasNames(),
                enable, parent.isExpandable(), parent.getOrder(), parent.isForbidden(), parent.getParentID());
    }

	public String getVideoSocketParams() {
		JSONObject result = new JSONObject();
		String videoSocketPort = InfoHelper.getConfig("融合媒体移动采编", "视频上传端口");
		String videoStartSocket = InfoHelper.getConfig("融合媒体移动采编", "启用视频上传接口");
		result.put("socketPort", NumberUtils.toInt(videoSocketPort));
		result.put("isSocketStart", "是".equals(videoStartSocket));
		return result.toString();
	}

    public String revokeDelete(int userID, String data){

        JSONObject jsonObject = JSONObject.fromObject(data);

        JSONArray fileArray = jsonObject.getJSONArray("articles");
//        String detail = jsonObject.getString("detail");

        long[] docIDs = new long[fileArray.size()];
//        long[] originalIDs = new long[fileArray.size()];
        ArticleMsg[] articleMsgs = new ArticleMsg[fileArray.size()];

        //组装返回值
        JSONObject ret = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        DocumentManager docManager = DocumentManagerFactory.getInstance();

        JSONObject json;
        int docLibID = 0;
        Document doc;
        //取出稿件的原稿ID，以及获取彻底删除的稿件信息，准备好撤稿消息需要的数据ArticleMsg（PublishTrigger.getArticleMsg）
        for(int i = 0;i < fileArray.size();i++){
            json = fileArray.getJSONObject(i);
            docLibID = json.getInt("docLibID");
            int docID = json.getInt("fileId");
            try {
                doc = docManager.get(docLibID, docID);
            } catch (E5Exception e) {
                e.printStackTrace();
                ret.put("success",false);
                ret.put("errorInfo", e.getMessage());
                ret.put("results",jsonArray);
                return ret.toString();
            }

            if(doc == null){
                System.out.println("稿件ID错误"+docID);
                ret.put("success",false);
                ret.put("errorInfo","稿件ID错误");
                ret.put("results",jsonArray);
                return ret.toString();
            }

            docIDs[i] = doc.getLong("SYS_DOCUMENTID");
//            originalIDs[i] = doc.getLong("a_originalID");

            articleMsgs[i] = PublishTrigger.getArticleMsg(doc);
        }

        String error;
        try {
            error = deleteOriginals(docLibID, docIDs);
        } catch (E5Exception e) {
            e.printStackTrace();
            ret.put("success",false);
            ret.put("errorInfo", e.getMessage());
            ret.put("results",jsonArray);
            return ret.toString();
        }
        if (error == null) {
            //由于彻底删除了稿件，所以需要手动存日志
            UserReader userReader = (UserReader)Context.getBean(UserReader.class);
            User user = null;
            try {
                user = userReader.getUserByID(userID);
            } catch (E5Exception e) {
                e.printStackTrace();
                user = new User();
                user.setUserID(0);
                user.setUserName("");
            }
            for (long docID : docIDs) {
                LogHelper.writeLog(docLibID, docID, user.getUserName(),
                        user.getUserID(), "删除", "彻底删除，保留日志");
            }

            //发布撤稿消息
            for (ArticleMsg articleMsg : articleMsgs) {
                PublishTrigger.articleDelete(articleMsg);
            }

            //修改原稿库中的原稿的渠道
//            int originalLibID = LibHelper.getLibIDByOtherLib(DocTypes.ORIGINAL.typeID(), docLibID);
//
//            List<Document> originals = new ArrayList<Document>();
//            for (int i = 0; i < docIDs.length; i++) {
//                if (originalIDs[i] > 0 && originalIDs[i] == docIDs[i]) {
//                    Document original = docManager.get(originalLibID, originalIDs[i]);
//                    if (original != null) {
//                        int channel = original.getInt("a_channel");
//                        if (channel < 0) channel = 0;
//                        //去掉当前渠道：第0位表示WEB版，第1位表示App版
//                        channel = channel ^ (int) Math.pow(2, articleManager.getChannelForLib(request, docLibID));
//                        original.set("a_channel", channel);
//                        originals.add(original);
//                    }
//                }
//            }
//            save(originalLibID, originals);
        }else {
            System.out.println(error);
            ret.put("success",false);
            ret.put("errorInfo", error);
            ret.put("results",jsonArray);
            return ret.toString();
        }

        ret.put("success",true);
        ret.put("errorInfo", "");
        ret.put("results",jsonArray);
        return ret.toString();
    }

    /**
     * 删除多个稿件以及把稿件对应的挂件、附件、扩展字段都删掉
     * @throws E5Exception
     */
    private String deleteOriginals(int docLibID, long[] docIDs) throws E5Exception {
        String tCode = LibHelper.getTenantCodeByLib(docLibID);

        //稿件删除语句。用sql执行而不是调用docManager.delete，是避免同时清理日志
        String delSql = "delete from " + LibHelper.getLibTable(docLibID) + " where SYS_DOCUMENTID=?";

        //删除稿件对应的挂件的SQL
        String delWidSql = "delete from " + LibHelper.getLibTable(DocTypes.WIDGET.typeID(), tCode)
                + " where w_articleID=? and w_articleLibID=?";
        //删除稿件对应附件的SQL
        String delAttaSql = "delete from " + LibHelper.getLibTable(DocTypes.ATTACHMENT.typeID(), tCode)
                + " where att_articleID=? and att_articleLibID=?";
        //删除稿件对应扩展字段的SQL
        String delExtSql = "delete from " + LibHelper.getLibTable(DocTypes.ARTICLEEXT.typeID(), tCode)
                + " where ext_articleID=? and ext_articleLibID=?";

        //同时修改多个稿件，使用事务
        DBSession conn = null;
        try {
            List<String> deleteList = getFilesToDelete(docLibID, docIDs) ;
            conn = E5docHelper.getDBSession(docLibID);
            conn.beginTransaction();
            for (long docID : docIDs) {
                InfoHelper.executeUpdate(delSql, new Object[]{docID}, conn);
                //删除对应的挂件、附件、扩展字段
                InfoHelper.executeUpdate(delWidSql, new Object[]{docID, docLibID}, conn);
                InfoHelper.executeUpdate(delAttaSql, new Object[]{docID, docLibID}, conn);
                InfoHelper.executeUpdate(delExtSql, new Object[]{docID, docLibID}, conn);
            }
            conn.commitTransaction();
            for(String path : deleteList){
                clear(InfoHelper.getFilePathInDevice(path));
            }
            return null;
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            return "操作中出现错误：" + e.getLocalizedMessage();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    private List<String> getFilesToDelete(int docLibID, long[] docIDs) throws E5Exception{
        int widgetLibID = LibHelper.getWidgetLibID() ;
        int attLibID = LibHelper.getAttaLibID() ;

        String wsql = " w_articleID = ? and w_articleLibID = ? and w_type = 0 " ;
        String asql = " att_articleID = ? and att_articleLibID = ? and att_type <> 1 and (att_objID is null or att_objID = 0) " ;

        DocumentManager docManager = DocumentManagerFactory.getInstance() ;
        List<String> deleteList = new ArrayList<>() ;
        for(long docID : docIDs){
            Document[] widgets = docManager.find(widgetLibID, wsql, new Object[]{docID, docLibID}) ;
            Document[] atts = docManager.find(attLibID, asql, new Object[]{docID, docLibID}) ;

            for(Document widget : widgets){
                deleteList.add(widget.getString("w_path")) ;
            }
            for(Document att : atts){
                String path = att.getString("att_path") ;
                if (StringUtils.isBlank(path)) continue;

                String fileName = path.substring(path.lastIndexOf("/")+1) ;

                if (att.getInt("att_type") == 0) {
                    if (!path.startsWith("http")){ //有可能是外网图片
                        deleteList.add(path) ;
                    }
                } else if(fileName.startsWith("t0_") || fileName.startsWith("t1_") || fileName.startsWith("t2_")){
                    deleteList.add(path) ;
                }
            }
        }
        return deleteList ;
    }

    //删掉没被引用的文件，同时删掉抽图文件
    private void clear(String path) {
        File file = new File(path);
        try {
            if (file.exists()) file.delete();

            String fileName = file.getCanonicalPath();

            file = new File(fileName + ".2");
            if (file.exists()) file.delete();

            file = new File(fileName + ".2.jpg");
            if (file.exists()) file.delete();

            file = new File(fileName + ".1");
            if (file.exists()) file.delete();

            file = new File(fileName + ".1.jpg");
            if (file.exists()) file.delete();

            file = new File(fileName + ".0");
            if (file.exists()) file.delete();

            file = new File(fileName + ".0.jpg");
            if (file.exists()) file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
