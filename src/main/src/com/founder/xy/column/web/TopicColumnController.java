package com.founder.xy.column.web;

import com.founder.e5.commons.Pair;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.TopicColumnManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.topics.TopicsManager;
import com.founder.xy.workspace.MainHelper;
import com.founder.xy.workspace.form.FormSaver;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/xy/column")
public class TopicColumnController extends AbstractResourcer {

    @Autowired
    private TopicColumnManager topicColumnManager;

    @Autowired
    private TopicsManager topicsManager;

    @Autowired
    private ColumnManager colManager;

    @RequestMapping(value = "TopicInit.do")
    public void topicInit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String result = "{\"docLibID\":\"" + getTopicLibID(request) + "\"}";
        InfoHelper.outputJson(result, response);
    }

    private int getTopicLibID(HttpServletRequest request){
        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(),
                tenantCode);
        return colLibID;
    }

    @RequestMapping(value = "TopicTree.do", params = "admin")
    public void topicTreeAdmin(HttpServletRequest request,
                                  HttpServletResponse response, @RequestParam int siteID)
            throws Exception {
        ColumnTreeHelper.initIcon(request);

//        int userID = ProcHelper.getUserID(request);

        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(),
                tenantCode);

        // 若是站点管理员，则可管理所有活动栏目
//        if (isAdmin(userID)) {
            Document[] cols = topicColumnManager.getRoot(colLibID, siteID);
            String result = ColumnTreeHelper.jsonTree(cols);

        JSONArray array = JSONArray.fromObject(result);
        JSONObject ret = new JSONObject();
        ret.put("list",array);
        ret.put("docLibID",colLibID);

            InfoHelper.outputJson(ret.toString(), response);
//        } else {
//            int roleID = ProcHelper.getRoleID(request);
//            Document[] cols = topicManager.getRoleColumns(colLibID, siteID, roleID);
//            String result = jsonTreeWithParent(cols);

//            InfoHelper.outputJson(result, response);
//        }
    }

    @RequestMapping(value = "TopicTree.do", params = "parentID")
    public void topicTree(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam int siteID, @RequestParam int parentID)
            throws Exception {
        ColumnTreeHelper.initIcon(request);

        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(),
                tenantCode);

        Document[] cols = null;
        if (parentID == 0) {
            cols = topicColumnManager.getRoot(colLibID, siteID);
        } else {
            cols = colManager.getSub(colLibID, parentID);
        }
        String result = ColumnTreeHelper.jsonTree(cols);

        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "TopicDelete.do")
    public void topicDelete(HttpServletRequest request, @RequestParam long colID,
                               HttpServletResponse response) throws Exception {
        try {
            int colLibID = getTopicLibID(request);
            int siteID = MainHelper.getSiteEnable(request);

            int count = topicsManager.topicsCountByGroup(siteID,colID);

            if(count == 0){
                DocumentManager docManager = DocumentManagerFactory.getInstance();
                docManager.delete(colLibID,colID);
//                Document doc = docManager.get(colLibID,colID);
//                doc.setDeleteFlag(1);
//                docManager.save(doc);

                RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + siteID);
                log(request, colLibID, colID, "删除", null);

                InfoHelper.outputText("ok", response);
            }else{
                InfoHelper.outputText("该话题组下面有话题，无法删除", response);
            }
        } catch (Exception e) {
            InfoHelper.outputText(e.getLocalizedMessage(), response);
        }
    }

    private void log(HttpServletRequest request, int colLibID, long docID, String procName, String detail) {

        SysUser user = ProcHelper.getUser(request);

        LogHelper.writeLog(colLibID, docID, user, procName, detail);
    }

    /**
     * 话题组名称查重（扩展E5平台的查重）
     * 返回值：重复，返回1；不重复，返回0
     */
    @RequestMapping(value = "TopicDuplicate.do")
    public void topicDuplicate(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        long parentID = WebUtil.getLong(request, "parentID", 0);
        int siteID = WebUtil.getInt(request, "siteID", 0);
        String value = WebUtil.get(request, "value");

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] docs = null;

        String sql = "col_name=? and col_parentID=? and col_siteID=? and SYS_DOCUMENTID<>?";
        docs = docManager.find(docLibID, sql, new Object[] { value, parentID, siteID, docID });

        String result = (docs == null || docs.length == 0) ? "0" : "1";
        InfoHelper.outputText(result, response);
    }

    /** 话题组表单保存。自己处理保存后的写日志、刷新栏目树、自动设置根栏目权限 */
    @RequestMapping(value = "TopicFormSubmit.do")
    public ModelAndView FormSubmit(HttpServletRequest request, HttpServletResponse response,
                                   Map<String, Object> model)
            throws Exception {

//        String iconPath =  request.getParameter("col_icon");
//        if (!isImgFile(iconPath)){
//            model.put("error", "对不起，请上传jpg,gif,png格式");
//            return new ModelAndView("/xy/column/TopicSubmit", model);
//        }

        int colLibID = getTopicLibID(request);
        long docID = docID(request);
        boolean isNew = docID == 0;
        int siteID = WebUtil.getInt(request, "col_siteID", 0);

        //检查站点的资源目录是否已配置
//        ResDir siteDir = getSiteDirs(iconPath, colLibID, docID, siteID);
//        if (siteDir.noSiteDir) {
//            model.put("error", "请先检查站点的资源目录设置");
//            return new ModelAndView("/xy/column/TopicSubmit", model);
//        }

        FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
        Pair changed;
        try {
            changed = formSaver.handleChanged(request);
            String colName =request.getParameter("col_name");

            //若是栏目，则给父栏目的childCount+1，设置级联ID和级联名称
            docID = Long.parseLong(changed.getKey());
            fill4Form(colLibID, isNew, docID,colName);
        } catch (Exception e) {
            if (isNew && docID > 0) { //新建栏目在加其它数据时异常，则删掉刚新建的栏目
                colManager.undoSave(colLibID, docID);
            }
            model.put("error", e.getLocalizedMessage());
            return new ModelAndView("/xy/column/TopicSubmit", model);
        }

        //发布到外网
//        pubAndWriteUrl(siteDir, iconPath, colLibID, docID, "col_icon");

        RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + siteID);
        //写操作日志
        String procName = isNew ? "创建" : "修改";
        log(request, colLibID, docID, procName, changed.getStringValue());

        //返回
        model.put("colID", docID);
        model.put("colName", request.getParameter("col_name"));
        model.put("parentID", request.getParameter("col_parentID"));
        model.put("isNew", isNew);
        model.put("isBat", "false");
        //是否需要定位到新建的栏目节点上
        model.put("needLocation", "true".equals(request.getParameter("needLocation")));

//        RedisManager.clear(RedisKey.APP_QAA_MODULE_VIEW + siteID);
//        RedisManager.clear(RedisKey.APP_QAA_CATS + siteID);

        return new ModelAndView("/xy/column/TopicSubmit", model);
    }

    private void fill4Form(int colLibID, boolean isNew, long colID, String colName) throws E5Exception {
        colManager.fill4Form(colLibID, isNew, colID, colName);
    }

    @RequestMapping(value = "TopicDrag.do")
    public void drag(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam int srcID, @RequestParam int destID, @RequestParam String moveType)
            throws Exception {

        int type = ("inner".equals(moveType)) ? 0 : (("prev".equals(moveType)) ? 1 : 2);
        int colLibID = getTopicLibID(request);
        int siteID = MainHelper.getSiteEnable(request);

        String result = "ok";
        try {
            colManager.move(colLibID, srcID, destID, type);
            RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + siteID);
//            RedisManager.clear(RedisKey.APP_QAA_MODULE_VIEW + siteID);
//            RedisManager.clear(RedisKey.APP_QAA_CATS + siteID);
            //PublishTrigger.column(colLibID, srcID);
        } catch (Exception e) {
            result = e.getLocalizedMessage();
        }

        String position = ("inner".equals(moveType)) ? "之下" : (("prev".equals(moveType)) ? "之前"
                : "之后");
        log(request, colLibID, srcID, "移动", "移动到节点" + destID + position);

        InfoHelper.outputJson(result, response);
    }

    @RequestMapping(value = "TopicFind.do")
    public void topicFind(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam int siteID, @RequestParam String q) throws Exception {

        int colLibID = getTopicLibID(request);
        if (q != null) q = q.trim();
        boolean flag=q.matches("[0-9]+");
        Document[] cols = topicColumnManager.find(colLibID, siteID, q,flag);
        String result = ColumnTreeHelper.json(cols);
        InfoHelper.outputJson(result, response);
    }
}
