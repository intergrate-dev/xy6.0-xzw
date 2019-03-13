package com.founder.xy.topics.web;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.DomInfo;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.statistics.util.FileUtil;
import com.founder.xy.system.site.Site;
import com.founder.xy.topics.Topics;
import com.founder.xy.topics.TopicsManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import com.founder.xy.workspace.MainHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("/xy/topic")
public class TopicsController {

    @Autowired
    private TopicsManager topicsManager;

    @RequestMapping(value = "Topic.do")
    public ModelAndView topic(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        boolean isNew = docID == 0;
        int siteID = WebUtil.getInt(request, "siteID", 1);

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = null;
        if (isNew) {
            doc = docManager.newDocument(docLibID);
            doc.set("a_siteID",siteID);
            doc.set("a_status",0);
            doc.set("a_groupID",WebUtil.getLong(request, "groupID", 0));
        }else {
            doc = docManager.get(docLibID, docID);
        }

        Topics topic = new Topics(doc);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("topic", topic);
        model.put("isNew", isNew);
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("sessionID", request.getSession().getId());
        return new ModelAndView("/xy/topic/Topic", model);
    }

    /**
     * 话题名称查重（扩展E5平台的查重）
     * 返回值：重复，返回1；不重复，返回0
     */
    @RequestMapping(value = "TopicDuplicate.do")
    public void topicDuplicate(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int siteID = WebUtil.getInt(request, "siteID", 0);
        String value = WebUtil.get(request, "value");

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] docs = null;

        String sql = "SYS_TOPIC=? and a_siteID=? and SYS_DOCUMENTID<>?";
        docs = docManager.find(docLibID, sql, new Object[] { value, siteID, docID });

        String result = (docs == null || docs.length == 0) ? "0" : "1";
        InfoHelper.outputText(result, response);
    }

    @RequestMapping(value = "topicCommit.do")
    public String topicCommit(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        long docID = WebUtil.getLong(request, "DocID", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        boolean isNew = WebUtil.getBoolParam(request, "isNew");
        //保存
        String tenantCode = InfoHelper.getTenantCode(request);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = null;
        if (isNew) {
            doc = docManager.newDocument(docLibID, docID);
        }else{
            doc = docManager.get(docLibID, docID);
        }
        FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
        saver.handle(doc, request);
        SysUser user = ProcHelper.getUser(request);
        if (isNew) {
            doc.set("SYS_AUTHORID", user.getUserID());
            doc.set("SYS_AUTHORS", user.getUserName());
        }
        docManager.save(doc);

        //更新话题稿件关联表
        String sql = "update xy_topicrelart set a_topicName = ? where a_topicID = ?";
        InfoHelper.executeUpdate(sql,new Object[]{doc.getTopic(),doc.getDocID()});

        RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + doc.getInt("a_siteID"));
        RedisManager.clear(RedisKey.APP_HOT_TOPICS_KEY + doc.getInt("a_siteID"));
        //返回
        String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
                + "&DocIDs=" + docID;
        return url;
    }

    @RequestMapping(value = "TopicDelete.do")
    public void TopicDelete(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String DocIDs = request.getParameter("DocIDs");
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        int siteID = WebUtil.getInt(request, "siteID", 1);
        DBSession conn = null;
        IResultSet webRs = null;
        IResultSet appRs = null;

        String webSql = "select count(1) count from xy_topicrelart a,xy_article b"
        		+ " where a.a_topicID = ? and a.a_channel = 1 and a.a_articleID = b.SYS_DOCUMENTID"
        		+ " and b.SYS_DELETEFLAG = 0";
        
        String appSql = "select count(1) count from xy_topicrelart a,xy_articleApp b"
        		+ " where a.a_topicID = ? and a.a_channel = 2 and a.a_articleID = b.SYS_DOCUMENTID"
        		+ " and b.SYS_DELETEFLAG = 0";
        boolean hasArticle = false;
        int count = 0;
        for (long docID : docIDs) {
//            Document topic = docManager.get(docLibID, docID);
        	Object[] params=new Object[]{docID};
        	
        	try {
        		conn = Context.getDBSession();
				webRs = conn.executeQuery(webSql, params);

				if(webRs.next()){
					count += webRs.getInt("count");
				}
				
				if(count > 0){
					hasArticle = true;
				    break;
				}
				
				appRs = conn.executeQuery(appSql, params);
				
				if(appRs.next()){
					count += appRs.getInt("count");
				}
				
				if(count > 0){
					hasArticle = true;
				    break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw e;
			} finally {
				ResourceMgr.closeQuietly(webRs);
				ResourceMgr.closeQuietly(appRs);
				ResourceMgr.closeQuietly(conn);
			}
        }

        if(hasArticle){
            InfoHelper.outputText("该话题下有文章，无法删除", response);
        }else{
            for (long docID : docIDs) {
                docManager.delete(docLibID,docID);
            }
            RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + siteID);
            RedisManager.clear(RedisKey.APP_HOT_TOPICS_KEY + siteID);

            InfoHelper.outputText("@refresh@", response); // 操作成功
        }
    }

    @RequestMapping(value = "topicMove.do")
    public void topicMove(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam String DocIDs, @RequestParam String DocLibID,
                     @RequestParam long ColIDs) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int docLibID = Integer.parseInt(DocLibID);
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        if (docIDs.length <= 0) {
            InfoHelper.outputText("Failed", response);
            return;
        }

        //取出现有的主栏目ID，判断选中的是否是自己所述的主栏目
        long oldColID = 0;
        Document topic = docManager.get(docLibID, docIDs[0]);
        if (topic != null) {
            oldColID = Long.parseLong(topic.getString("a_groupID"));
        }
        if (oldColID == ColIDs) {
            InfoHelper.outputText("samecol", response);
            return;
        }

        String tenantCode = InfoHelper.getTenantCode(request);
        int catLibID = LibHelper.getLibID(DocTypes.COLUMNTOPIC.typeID(), tenantCode);

        Document oldCol = docManager.get(catLibID, oldColID);
        Document newCol = docManager.get(catLibID, ColIDs);
        if(newCol == null){
            InfoHelper.outputText("rootcol", response);
            return;
        }

        //移动的稿件的信息的修改
        topic.set("a_groupID",ColIDs);
        docManager.save(topic);

        RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + topic.getInt("a_siteID"));
        //操作成功后写日志
        StringBuilder operationResult = new StringBuilder();

        if(oldCol!=null){
            operationResult.append("来自：").append(oldCol.getString("col_name")).append("（").append(oldCol.getDocID()).append("）");
        }

        InfoHelper.outputText("success" + operationResult.toString(), response);
    }

    @RequestMapping(value = "topicCopy.do")
    public void topicCopy(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam String DocIDs, @RequestParam String DocLibID,
                     @RequestParam String ColIDs) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int docLibID = Integer.parseInt(DocLibID);
        long[] docIDs = StringUtils.getLongArray(DocIDs);
        if (docIDs.length <= 0) {
            InfoHelper.outputText("Failed", response);
            return;
        }
        long[] colIDs = StringUtils.getLongArray(ColIDs);
        if (colIDs.length > 0) {
            //取出现有的主栏目ID，判断选中的是否是自己所述的主栏目
            long oldColID = 0;
            Document topic = docManager.get(docLibID, docIDs[0]);
            if (topic != null) {
                oldColID = Long.parseLong(topic.getString("a_groupID"));
            }
            for(long l : colIDs){
                if (oldColID == l) {
                    InfoHelper.outputText("samecol", response);
                    return;
                }
            }

            String error = topicsManager.saveCopy(request, docLibID, docIDs[0], colIDs,oldColID);

            if (error == null) {
                RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + topic.getInt("a_siteID"));
                //操作成功后写日志
                InfoHelper.outputText("success" + "复制成功", response);
            } else {
                InfoHelper.outputText(error, response);
            }
        } else {
            InfoHelper.outputText("Failed", response);
            return;
        }
    }

    /**
     * 稿件明细-话题稿件-查看话题
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getTopics.do")
    public void getTopicsGroup(HttpServletRequest request, HttpServletResponse response, int siteID, String status, String topicID, String topicName){
        List<Map<String, Object>> model = topicsManager.getTopicsData(siteID, status, topicID, topicName);
        JSONArray jsonArray = JSONArray.fromObject(model);

        InfoHelper.outputJson(jsonArray.toString(), response);
    }

    /**
     * csv导出
     */

    @RequestMapping(value = "ExportCSV.do")
    public void exportCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取参数
        String inParamJson = request.getParameter("jsonData");
        ObjectMapper mapper = new ObjectMapper(); //转换器
        Map inParam = mapper.readValue(inParamJson, Map.class); //json转换成map
        String fileName = MapUtils.getString(inParam, "fileName") + System.currentTimeMillis();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if ((agent.indexOf("msie") > -1) || (agent.indexOf("rident") > -1)) {
            fileName = URLEncoder.encode(fileName, "ISO8859-1");
        }
        inParam.remove("fileName");

        String siteID = MapUtils.getString(inParam, "siteID");
        if (siteID == null || siteID.equals("")) {
            throw new E5Exception("Site_ID is null!");
        }
        Map dataParam = MapUtils.getMap(inParam, "dataParam");
        dataParam.put("siteID", siteID);
        //获取准备导出的数据
        Map<String, Object> exportData = topicsManager.export(dataParam);
        inParam.putAll(exportData);
        //生成CSV文件
        String newFileName = FileUtil.generateCSVFile(fileName, filePath, inParam);
        if (newFileName != null && !"".equals(newFileName)) {
            fileName = newFileName;
        }
        //下载CSV文件
        FileUtil.downLoadCSVFile(response, fileName, filePath);
        //删除生成的CSV文件
        FileUtil.deleteFile(fileName, filePath);
        System.out.println("执行");

    }


    @RequestMapping(value = { "TopicMergeSubmit.do" })
    public String TopicMergeSubmit(HttpServletRequest request, HttpServletResponse response, @RequestParam String DocIDs,
                              @RequestParam String DocLibID, @RequestParam int docID, @RequestParam int siteID,
                              @RequestParam String UUID) throws Exception {
        if (DocIDs == null || DocIDs.isEmpty()) {
            String url = "/e5workspace/after.do?UUID=" + UUID;
            return "redirect:" + url;
        }
        DocumentManager docManager = DocumentManagerFactory.getInstance();

        int docLibID = Integer.parseInt(DocLibID);
        long[] docIDs = StringUtils.getLongArray(DocIDs);

        StringBuilder topicName = new StringBuilder();
        // 同时修改多个稿件，使用事务
        DBSession conn = null;
        IResultSet rs = null;
        IResultSet rs1 = null;

        String querySql = "select a_articleID,a_channel from xy_topicrelart where a_topicID = ?";
        String querySql1 = "select 1 from xy_topicrelart where a_topicID = ? and a_articleID = ? and a_channel = ?";
        String delateSql = "delete from xy_topicrelart where a_topicID = ? and a_articleID = ? and a_channel = ?";
        String sql = "update xy_topicrelart set a_topicID = ?, a_topicName = ? where a_topicID = ? and a_articleID = ? and a_channel = ?";
        String topicSql = "delete from xy_topics where sys_documentid = ?";

        Document newDoc = docManager.get(docLibID,docID);
        String newName = newDoc.getTopic();
        try {
            conn = E5docHelper.getDBSession(docLibID);
            conn.beginTransaction();

            for (int i = 0; i < docIDs.length; i++) {
                long topicID = docIDs[i];
                Document doc = docManager.get(docLibID,topicID);
                topicName.append(doc.getTopic()).append("，");

                try {
                    rs = conn.executeQuery(querySql, new Object[]{topicID});
                    while (rs.next()){
                        long articleID = rs.getLong("a_articleID");
                        int channel = rs.getInt("a_channel");

                        try {
                            rs1 = conn.executeQuery(querySql1, new Object[]{docID,articleID,channel});
                            if(rs1.next()){
                                conn.executeUpdate(delateSql, new Object[]{topicID,articleID,channel});
                            }else{
                                conn.executeUpdate(sql, new Object[]{docID,newName,topicID,articleID,channel});
                            }
                        } finally {
                            ResourceMgr.closeQuietly(rs1);
                        }
                    }
                    conn.executeUpdate(topicSql,new Object[]{topicID});
                } finally {
                    ResourceMgr.closeQuietly(rs);
                }
            }
            conn.commitTransaction();

            RedisManager.clear(RedisKey.APP_TOPICSBYGROUP_KEY + siteID);
            RedisManager.clear(RedisKey.APP_HOT_TOPICS_KEY + siteID);
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
        SysUser sysUser = ProcHelper.getUser(request);
        LogHelper.writeLog(docLibID, docID, sysUser, "合并", "话题：" + topicName.substring(0,topicName.length()-1));
        String url = "/e5workspace/after.do?UUID=" + UUID;
        return "redirect:" + url;
    }


    /**
     * 话题库-话题-话题稿件-从话题中删除
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "topicArticleDeleteRelation.do")
    public void topicArticleDeleteRelation(HttpServletRequest request, HttpServletResponse response, String docIDs, String topicID, String channel, String deleteAll) throws Exception {
        String result = topicsManager.deleteTopicrelart(docIDs,topicID,channel,deleteAll);
        JSONObject json = new JSONObject();
        if("1".equals(result)){
            json.put("info","删除成功！");
        }else{
            json.put("info","删除失败！");
        }
        json.put("status",result);

        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 话题库-话题-话题稿件-修改
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "topicArticleUpdate.do")
    public void topicArticleUpdate(HttpServletRequest request, HttpServletResponse response, String docID, String channel) throws Exception {
        DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.ARTICLE.typeID());
        int docLibID;
        if("1".equals(channel)){
            docLibID = docLibIDs[0].getDocLibID();
        }else{
            docLibID = docLibIDs[1].getDocLibID();
        }
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, Long.parseLong(docID));

        int siteID = WebUtil.getInt(request, "siteID", 1);
        String uuid = "8c8c0e93-6f7f-4b52-8a22-3f69da8cd676";//话题稿件查看修改稿件功能特殊处理

        String url = "../../xy/article/Article.do?DocIDs="+docID+"&DocLibID="+docLibID+"&colID="+doc.get("a_columnID")+"&siteID="+siteID+"&UUID="+uuid;
        JSONObject json = new JSONObject();
        json.put("url",url);
        InfoHelper.outputJson(json.toString(), response);
    }
}
