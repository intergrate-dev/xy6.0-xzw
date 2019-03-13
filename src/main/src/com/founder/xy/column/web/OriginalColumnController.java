package com.founder.xy.column.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.opensagres.xdocreport.template.velocity.internal.Foreach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowManager;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.OriginalColumnManager;
import com.founder.xy.column.web.ColumnTreeHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.system.site.SiteUserManager;

/**
 * 源稿分类树，用于主界面的原稿库
 *
 * @author JiangYu
 */
@Controller
@RequestMapping("/xy/column")
public class OriginalColumnController extends AbstractResourcer {

    @Autowired
    private SiteUserManager userManager;

    @Autowired
    private OriginalColumnManager orgManager;

    @Autowired
    private ColumnManager colManager;

    /** 源稿栏目树（第一层），带权限 */
    @RequestMapping(value = "OrgTree.do", params = "admin")
    public void orgTreeAdmin(HttpServletRequest request,
                             HttpServletResponse response, @RequestParam int siteID)
            throws Exception {
        ColumnTreeHelper.initIcon(request);

        int userID = ProcHelper.getUserID(request);

        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),
                tenantCode);

        // 若是站点管理员，则可管理所有源稿栏目
        if (isAdmin(userID)) {
            Document[] cols = orgManager.getRoot(colLibID, siteID);
            String result = ColumnTreeHelper.jsonTree(cols);

            InfoHelper.outputJson(result, response);
        } else {
            int roleID = ProcHelper.getRoleID(request);
            Document[] cols = orgManager.getRoleColumns(colLibID, siteID, roleID);
            String result = jsonTreeWithParent(cols);

            InfoHelper.outputJson(result, response);
        }
    }

    /** 源稿栏目树（第一层以及子层） */
    @RequestMapping(value = "OrgTree.do", params = "parentID")
    public void orgTree(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam int siteID, @RequestParam int parentID)
            throws Exception {
        ColumnTreeHelper.initIcon(request);

        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),
                tenantCode);

        Document[] cols = null;
        if (parentID == 0) {
            cols = orgManager.getRoot(colLibID, siteID);
        } else {
            cols = colManager.getSub(colLibID, parentID);
        }
        String result = ColumnTreeHelper.jsonTree(cols);

        InfoHelper.outputJson(result, response);
    }

    /** 源稿栏目树（第一层），根据角色ID的权限取 */
    @RequestMapping(value = "OrgTree.do", params = "role")
    public void orgTreeRole(HttpServletRequest request,
                            HttpServletResponse response, @RequestParam int siteID, @RequestParam int roleID)
            throws Exception {
        int colLibID = getOriLibID(request);

        Document[] cols = orgManager.getPermissionColumns(colLibID, siteID, roleID);
        String result = jsonTreeWithParent(cols);

        InfoHelper.outputJson(result, response);

    }

    /** 读源稿栏目库ID */
    @RequestMapping(value = "DatasInit.do")
    public void datasInit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String result = "{\"docLibID\":\"" + getOriLibID(request) + "\"}";
        InfoHelper.outputJson(result, response);
    }

    /**
     * 源稿分类栏目删除
     */
    @RequestMapping(value = "OrgDelete.do")
    public void orgDelete(HttpServletRequest request, @RequestParam long colID,
                          HttpServletResponse response) throws Exception {
        try {
            int colLibID = getOriLibID(request);

            colManager.delete(colLibID, colID);
            //log(request, colLibID, colID, "删除", null);

            InfoHelper.outputText("ok", response);
        } catch (Exception e) {
            InfoHelper.outputText(e.getLocalizedMessage(), response);
        }
    }

    /** 栏目表单保存。自己处理保存后的写日志、刷新栏目树、自动设置根栏目权限 */
    @RequestMapping(value = "OrgFormSubmit.do")
    public ModelAndView orgFormSubmit(HttpServletRequest request, HttpServletResponse response,
                                      Map<String, Object> model)throws Exception {

        int colLibID = getOriLibID(request);
        long docID = docID(request);
        boolean isNew = docID == 0;
        //获取id下的子栏目
//        Document[] sub = colManager.getSub(colLibID, parentID);
        //前台传值
        String colNameFromTable =request.getParameter("col_name");
        String colName= null;
        int siteID = WebUtil.getInt(request, "siteID", 0);
        //通过siteid查询到本站点所有的父栏目
        Document[] cols = orgManager.getRoot(colLibID, siteID);
        //如果前端是以;来分隔的,将前端传过来的栏目转换成数组
        String[] colName1= colNameFromTable.split(";");
        //非子栏目
        for(int j=0;j< colName1.length;j++) {
            colName = colName1[j];
            for (int i = 0; i < cols.length; i++) {
                //如果是修改,不需要对比自己
                    if (!isNew&&cols[i].getDocID() == docID){
                        continue;
                    }
                if (cols[i].getString("COL_NAME").equals(colName)) {
                    //如果名称重复则报错
                    model.put("error", "名称不能重复");
                    return new ModelAndView("/xy/column/OriginalSubmit", model);
                }
            }
        }

        //获取id下的子栏目
        //子栏目修改
        if(!isNew){
            //获取栏目的父栏目
            Document doc = colManager.get(colLibID, docID);
            int parentID = doc.getInt("col_parentID");
            if(parentID>0) {
                Document[] cols1 = colManager.getSub(colLibID, parentID);
                for (int i = 0; i < cols1.length; i++) {
                    if (!isNew && cols1[i].getDocID() == docID) {
                        continue;
                    }
                    if (cols1[i].getString("COL_NAME").equals(colName)) {
                        //如果名称重复则报错
                        model.put("error", "名称不能重复");
                        return new ModelAndView("/xy/column/OriginalSubmit", model);
                    }
                }
            }
        }
//        cols = colManager.getSub(colLibID, parentID);
        //保存
        for(int j=0;j< colName1.length;j++) {
            colName = colName1[j];
            FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
            Pair changed;
            try {
                changed = formSaver.handleChanged(request);
                //若是栏目，则给父栏目的childCount+1，设置级联ID和级联名称
                docID = Long.parseLong(changed.getKey());
                Document doc = colManager.get(colLibID, docID);
                int parentID = doc.getInt("col_parentID");
                //子栏目新增
                if (isNew && parentID>0){
                    Document[] cols2 = colManager.getSub(colLibID, parentID);
                    for(int i= 0;i<cols2.length;i++){
                        if (!isNew&&cols2[i].getDocID() == docID){
                            continue;
                        }
                        if (cols2[i].getString("COL_NAME").equals(colName)) {
                            colManager.undoSave(colLibID, docID);
                            //如果名称重复则报错
                            model.put("error", "名称不能重复");
                            return new ModelAndView("/xy/column/OriginalSubmit", model);
                        }
                    }
                }
                fill4Form(colLibID, isNew, docID, colName);
            } catch (Exception e) {
                if (isNew && docID > 0) { //新建栏目在加其它数据时异常，则删掉刚新建的栏目
                    colManager.undoSave(colLibID, docID);
                }
                model.put("error", e.getLocalizedMessage());
                return new ModelAndView("/xy/column/OriginalSubmit", model);
            }
        }
        //写操作日志
        //String procName = isNew ? "创建" : "修改";
        //log(request, colLibID, docID, procName, changed.getStringValue());

        //返回
        model.put("colID", docID);
        model.put("colName", request.getParameter("col_name"));
        model.put("parentID", request.getParameter("col_parentID"));
        model.put("isNew", isNew);
        model.put("isBat", "false");
        //是否需要定位到新建的栏目节点上
        model.put("needLocation", "true".equals(request.getParameter("needLocation")));

        return new ModelAndView("/xy/column/OriginalSubmit", model);
    }

    //批量添加子栏目
    @RequestMapping(value = "OrgFormSubmitBat.do")
    public ModelAndView orgFormSubmitBat(HttpServletRequest request, HttpServletResponse response,
                                         Map<String, Object> model)
            throws Exception {

        int colLibID = getOriLibID(request);
        long docID = docID(request);
        boolean isNew = docID == 0;

        FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);

        Pair changed;
        String [] colNames =  request.getParameter("col_name").split(";");
        for(String colName :colNames) {
            request.setAttribute("col_name", colName);
            try {
                changed = formSaver.handleChanged(request);

                //若是栏目，则给父栏目的childCount+1，设置级联ID和级联名称
                fill4Form(colLibID, isNew, Long.parseLong(changed.getKey()),colName);
            } catch (Exception e) {
                model.put("error", e.getLocalizedMessage());
                return new ModelAndView("/xy/column/OriginalSubmit", model);
            }

            //写操作日志
            //String procName = isNew ? "创建" : "修改";
            docID = Long.parseLong(changed.getKey());
            //log(request, colLibID, docID, procName, changed.getStringValue());

            //返回
            model.put("colID", docID);
            model.put("colName", colName);
            model.put("parentID", request.getParameter("col_parentID"));
            model.put("isNew", isNew);
            model.put("isBat", "true");
        }
        return new ModelAndView("/xy/column/OriginalSubmit",model);
    }

    /** 栏目表单中：取出稿件的流程 */
    @RequestMapping(value = "OrgFlows.do")
    public void orgFlows(HttpServletRequest request, HttpServletResponse response) throws Exception {

        int docTypeID = DocTypes.ORIGINAL.typeID();
        FlowManager flowManager = (FlowManager) Context.getBean(FlowManager.class);
        Flow[] flows = flowManager.getFlows(docTypeID);

        StringBuilder result = new StringBuilder();
        result.append("[");

        for (Flow flow : flows) {
            if (result.length() > 1)
                result.append(",");
            result.append("{\"key\":\"").append(String.valueOf(flow.getID()))
                    .append("\",\"value\":\"").append(InfoHelper.filter4Json(flow.getName()))
                    .append("\"}");
        }
        result.append("]");

        InfoHelper.outputJson(result.toString(), response);
    }

    /**
     * 栏目名称查重（扩展E5平台的查重）
     * 返回值：重复，返回1；不重复，返回0
     */
    @RequestMapping(value = "OrgDuplicate.do")
    public void duplicate(HttpServletRequest request, HttpServletResponse response)
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


    /** 源稿栏目树查找 */
    @RequestMapping(value = "OrgFind.do")
    public void orgFind(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam int siteID, @RequestParam String q) throws Exception {

        int colLibID = getOriLibID(request);
        if (q != null) q = q.trim();
        boolean flag=q.matches("[0-9]+");
        Document[] cols = orgManager.find(colLibID, siteID, q,flag);
        String result = ColumnTreeHelper.json(cols);
        InfoHelper.outputJson(result, response);
    }

    private int getOriLibID(HttpServletRequest request){
        String tenantCode = InfoHelper.getTenantCode(request);
        int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),
                tenantCode);
        return colLibID;
    }

    private void log(HttpServletRequest request, int colLibID, long docID, String procName, String detail) {

        SysUser user = ProcHelper.getUser(request);

        LogHelper.writeLog(colLibID, docID, user, procName, detail);
    }

    private void fill4Form(int colLibID, boolean isNew, long colID, String colName) throws E5Exception{
        colManager.fill4Form(colLibID, isNew, colID, colName);
    }

    /**
     * 判断当前用户是否管理员。
     *
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

    // 带父节点（无权限）的栏目树的json
    private String jsonTreeWithParent(Document[] cols) throws E5Exception {
        List<Column> roots = getRoots(cols);

        return ColumnTreeHelper.jsonTreeWithParent(roots);
    }

    // 根据指定的栏目，得到从根栏目开始的栏目树对象
    private List<Column> getRoots(Document[] cols) throws E5Exception {
        if (cols == null)
            return null;

        // 保证顺序
        Map<Integer, Column> tree = new HashMap<Integer, Column>();

        for (Document col : cols) {
            // 把无权限的父节点也带上
            int[] path = StringUtils.getIntArray(
                    col.getString("col_cascadeID"), "~");
            Column pCol = tree.get(path[0]);
            if (pCol == null) {
                pCol = getCol(col.getDocLibID(), path[0]);
                tree.put(path[0], pCol);
            } else if (pCol.isEnable()) {
                // 栏目拖动会造成栏目的父子结构发生变化。若发现父栏目已经有权限，则不必加子栏目
                continue;
            }

            if (path.length == 1) {
                pCol.setEnable(true);
                pCol.removeChildren(); // 节点设置为enable后就可动态展开，不需要设置children
                continue;
            }

            Column parent = pCol;
            for (int i = 1; i < path.length; i++) {
                Column son = parent.getChild(path[i]);
                if (son == null) {
                    son = getCol(col.getDocLibID(), path[i]);
                    parent.addChild(son);
                } else if (son.isEnable()) {
                    break;
                }
                // 最后一级的栏目，是确实有权限的，所以enable=true
                if (i == path.length - 1) {
                    son.setEnable(true);
                }
                parent = son;
            }
        }
        return ColumnTreeHelper.sortColByOrder(tree);
    }

    // 根据栏目ID得到Col对象
    private Column getCol(int colLibID, long id) throws E5Exception {
        boolean enable = false;

        Document parent = colManager.get(colLibID, id);
        return new Column(id, parent.getString("col_name"),
                parent.getString("col_cascadeID"),
                parent.getString("col_cascadeName"), enable,
                parent.getInt("col_childCount") > 0,
                parent.getInt("col_displayOrder"),
                parent.getInt("col_status") > 0);
    }
}
