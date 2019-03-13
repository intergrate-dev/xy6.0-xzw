package com.founder.xy.article.web;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.context.Context;
import com.founder.e5.db.IResultSet;
import com.founder.e5.flow.Flow;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.Original;
import com.founder.xy.article.OriginalManager;
import com.founder.xy.article.SignedChInfo;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.ArticleMsg;

import net.sf.json.JSONObject;

/**
 * 源稿操作功能
 *
 * @author JiangYu
 */
@Controller
@RequestMapping("/xy/article")
public class OriginalController {

    @Autowired
    private OriginalManager originalManager;

    @Autowired
    private ArticleManager articleManager;
    /**
     * 源稿库  跳转至"签发/预签并送审"页面
     */
    @RequestMapping(value = { "OriginalCensorship.do" })
    public ModelAndView originalCensorship(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        String siteID = WebUtil.get(request, "siteID");
        int type = WebUtil.getInt(request, "type", 1);// 1：签发 2：预签并送审
        String tenantCode = InfoHelper.getTenantCode(request);
        int articleType = 0;
        if(100==Integer.valueOf(siteID)){
            articleType = 13;
        }
        String[] strDocIDs1 = strDocIDs.split(",");
        for (String strDocID:strDocIDs1) {
            if(strDocID.isEmpty()){
                break;
            }
            Document original = docManager.get(docLibID, Long.parseLong(strDocID));
            int channel = original.getInt("a_channel");
            if (channel < 0)
                channel = 0;
            // 取发布渠道。原稿中，不再显示已经发布的渠道
            //List<Channel> chs = originalManager.getChannels(channel);
            //model.put("channels", chs);
            //model.put("channelCount", chs.size());
            articleType = original.getInt("a_type");
            if (articleType != Original.TYPE_WEIXIN) {
                // 取已发布发布渠道。在界面中显示已发布的信息。
                List<SignedChInfo> originaledchs = originalManager.getSignedChs(channel,
                        Long.parseLong(strDocID), tenantCode, original.getString("a_pubsign"));
                model.put("originaledchs", originaledchs);
                model.put("originaledchsCount", originaledchs.size());
            } else {
                model.put("originaledchsCount", 0);
                //取已发布的微信公众号信息
                SignedChInfo originaledwx = originalManager.getSignedWeixin(original);
                model.put("originaledwx", originaledwx);
            }
        }
        model.put("DocLibID", docLibID);
        model.put("DocIDs", strDocIDs);
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("siteID", WebUtil.get(request, "siteID"));
        model.put("type", type);
        model.put("articleType", String.valueOf(articleType));
        model.put("IsEditor", (WebUtil.get(request, "IsEditor")));

        return new ModelAndView("/xy/article/Censorship", model);
    }

    /**
     * 源稿库 送审/预签并送审/签发
     */
    @RequestMapping("Censorship.do")
    public void censorship(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {

        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        String docIDs = WebUtil.get(request, "DocIDs"); // 稿件ID
        int scope = WebUtil.getInt(request, "scope", 0);// 0:发起全新审核 1:选择审核阶段
        int flownode = WebUtil.getInt(request, "flownode", 0); // 审核阶段
        int type = WebUtil.getInt(request, "type", 0); // 0：送审    1：签发    2：预签并送审   3:签发并发布
        String operation = null;
        String paramData = WebUtil.get(request, "paramData");
        JSONObject params = JSONObject.fromObject(paramData);
        // 获取稿件
        if(!docIDs.contains(",")){
            docIDs+=",";
        }
        String[] docIDs1 = docIDs.split(",");
        StringBuilder colsignAndIssue = new StringBuilder();
        for (String docID1:docIDs1) {
            if(docID1.isEmpty()){
                break;
            }
            Long docID = Long.valueOf(docID1);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            //预签 或 签发选择的栏目
            String checkedCols = originalManager.checkPubsign(params, doc);
            if (type == 1 || type == 3) {// 签发  签发并发布
                if (checkedCols.length() == 0) {
                    InfoHelper.outputText("failed", response);
                    return;
                }
                String tenantCode = InfoHelper.getTenantCode(request);
                //取所需校对总数
                long catID = doc.getLong("a_catID");
                int catLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), tenantCode);
                Document doCat = docManager.get(catLibID, catID);
                if (doc.getInt("a_check") == doCat.getInt("col_check")) {
                    //校对全部通过
                    request.setAttribute("DocIDs",Integer.parseInt(String.valueOf(docID)));
                    originalManager.signChannelArticles(params, request, type == 3);
                    doc.set("a_status", Original.STATUS_PUBDONE);
                    doc.setCurrentStatus("已签发");
                    operation = "签发";
                } else {
                    InfoHelper.outputText("uncheck", response);
                    return;
                }
            } else {//送审 预签并送审
                doc.set("a_status", Original.STATUS_AUDITING);
                operation = "送审";
                if (type == 2 && checkedCols.length() > 0) {// 预签并送审时  将预签栏目保存至数据库
                    doc.set("a_pubsign", paramData);
                    JSONObject jParamData = JSONObject.fromObject(paramData);
//					{"0_columnID":"3113","0_column":"新闻","0_columnRelID":"",
//							"0_columnRel":"","1_columnID":"0","1_column":"","1_columnRelID":"","1_columnRel":"","channel0":"on"}
                    if(jParamData.has("0_column")) {
                        String column0 = jParamData.getString("0_column");//Web
                        if(column0 != null && column0.length() != 0) {
                            colsignAndIssue.append("签发到web:").append(column0).append(",");
                        }
                    }
                    if(jParamData.has("0_columnRel")) {
                        String columnRel =jParamData.getString("0_columnRel") ;//web关联
                        if(columnRel != null && columnRel.length() != 0) {
                            colsignAndIssue.append("签发到web关联库:").append(columnRel).append(",");
                        }
                    }
                    if(jParamData.has("1_column")) {
                        String column1 = jParamData.getString("1_column"); //app
                        if(column1 != null && column1.length() != 0) {
                            colsignAndIssue.append("签发到app库:").append(column1).append(",");
                        }
                    }
                    if(jParamData.has("1_columnRel")) {
                        String columnRel1 = jParamData.getString("1_columnRel");
                        if(columnRel1 != null && columnRel1.length() != 0) {
                            colsignAndIssue.append("签发到app关联库:").append(columnRel1).append(",");
                        }
                    }
                    operation = "预签并送审";
                }
                if (scope == 1) {
                    doc.set("sys_currentnode", flownode);
                    doc.setCurrentStatus("待审核");
                }
                if(flownode == 76){
                    doc.set("SYS_CURRENTNODE",flownode);
                    doc.set("SYS_CURRENTSTATUS","一审通过");
                }
            }
            // 保存稿件
            if (operation != null) {
                SysUser user = ProcHelper.getUser(request);
                //如果是编辑器内部的送审\预签并送审
                if ((type == 0 || type == 2) && "true".equals(WebUtil.get(request, "IsEditor"))) {
                    LogHelper.writeLog(docLibID, docID, user, operation, WebUtil.get(request, "Reason"));
                    originalManager.setNextFlow(doc, 0, flownode);//设置流程
                }

                docManager.save(doc);
                //保存历史版本
                articleManager.recordHistoryVersion(docManager, doc,
                        InfoHelper.getTenantCode(request), operation,
                        user.getUserName());
            }
        }
        InfoHelper.outputText("success"+colsignAndIssue.toString(), response);
    }

    /**
     * 源稿库  驳回
     */
    @RequestMapping(value = "SendBack.do")
    public void sendBack(HttpServletRequest request,
                         HttpServletResponse response, @RequestParam String DocIDs,
                         @RequestParam String DocLibID,@RequestParam String BackType) throws Exception {
        int docLibID = Integer.parseInt(DocLibID);
        int backType=0;
        if(!BackType.isEmpty()){
            backType = Integer.parseInt(BackType);
        }
        long[] docIDs = StringUtils.getLongArray(DocIDs);

        // 读出稿件
        List<Document> originals = new ArrayList<Document>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        for (long docID : docIDs) {
            Document original = docManager.get(docLibID, docID);
            if(backType==1){
                original.set("a_status", Original.STATUS_PASSFIRST);
            }else{
                original.set("a_status", Original.STATUS_REJECTED);
            }
            original.set("sys_currentstatus", "已驳回");
            original.set("a_pubsign", "");//取消预签信息
            original.set("a_lastFlowNode", original.getCurrentNode());//记录驳回时的流程阶段信息
            originals.add(original);
        }

        // 同时修改多个稿件，使用事务
        String error = originalManager.save(docLibID, originals);
        SysUser user = ProcHelper.getUser(request);
        for (Document document : originals) {
            //记录历史版本
            articleManager.recordHistoryVersion(docManager, document,
                    InfoHelper.getTenantCode(request), "驳回", user.getUserName());
        }
        if (error == null) {
            InfoHelper.outputText("success", response);
        } else {
            InfoHelper.outputText(error, response);
        }
    }

    /**
     * 源稿库  审核通过
     */
    @RequestMapping("CensorshipThrough.do")
    public void censorshipThrough(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        String docIDs = WebUtil.get(request, "DocIDs");// 稿件ID
// 获取稿件
        String[] split = docIDs.split(",");
        for (String s: split) {
            long docID = Long.parseLong(s);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            //		String checkStr = "";
            if (originalManager.checkFlowNode(doc)) {
                doc.set("a_status", Original.STATUS_PUBNOT);

                //			int check = doc.getInt("a_check");
                //			String pubsign = doc.getString("a_pubsign");
                //			boolean flag = StringUtils.isBlank(pubsign);
                //			if(!flag){
                //				if(check == 0){//校对全部通过
                //					JSONObject params = JSONObject.fromObject(pubsign);
                //					originalManager.signChannelArticles(params, request);
                //					doc.set("a_status", Original.STATUS_PUBDONE);
                //					doc.setCurrentStatus("已签发");
                //					doc.set("a_pubsign", "");//取消预签信息
                //					checkStr = "success";
                //				} else if (check > 0){
                //					checkStr = "failure";
                //				}
                //			}
                // 保存稿件
                docManager.save(doc);
            }else{
                doc.set("a_status", Original.STATUS_PASSFIRST);
                docManager.save(doc);
            }
            //记录历史版本
            articleManager.recordHistoryVersion(docManager, doc,
                    InfoHelper.getTenantCode(request), "审核通过",
                    ProcHelper.getUser(request).getUserName());
        }
        InfoHelper.outputText("success", response);
    }

    /**
     * 源稿库 校对完成
     */
    @RequestMapping("CheckThrough.do")
    public void checkThrough(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        String docIDs = WebUtil.get(request, "DocIDs");// 稿件ID
        String[] split = docIDs.split(",");
        JSONObject result = new JSONObject();
        for (String s: split) {
            long docID = Long.parseLong(s);
            String tenantCode = InfoHelper.getTenantCode(request);
            SysUser user = ProcHelper.getUser(request);
            // 获取稿件
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            int checked = doc.getInt("a_check");//已校对次数
            //先判断还要不要校对
            //取所需校对总数
            long catID = doc.getLong("a_catID");
            int catLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), tenantCode);
            Document doCat = docManager.get(catLibID, catID);
            int needCheck = doCat.getInt("col_check") - checked;

            if (needCheck <= 0) { //不需要在校对
                needCheck = -1;
            } else {
                doc.set("a_check", ++checked);
                docManager.save(doc);
                needCheck--;
            }
            //如果是编辑器内部的校对完成
            String isEditor = WebUtil.get(request, "IsEditor");
            if ("true".equals(isEditor)) {
                String reason = checked + "校完成. " + WebUtil.get(request, "Reason");
                LogHelper.writeLog(docLibID, docID, user, "校对完成", reason);
            }
            result.put("checked", checked);//当前校对次数
            result.put("needCheck", needCheck);// 剩余校对次数
            //记录历史版本
            articleManager.recordHistoryVersion(docManager, doc,
                    tenantCode, "校对完成",
                    user.getUserName());
        }
        InfoHelper.outputJson(result.toString(), response);
    }

    /**
     * 源稿库  回显流程记录/日志信息
     */
    @RequestMapping(value = "CensorshipLog.do")
    @ResponseBody
    public Map<String, Object> censorshipInfo(HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        long docID = WebUtil.getInt(request, "DocIDs", 0); // 稿件ID
        int type = WebUtil.getInt(request, "Type", 0); // 0 送审流程记录  1全部流程记录
        int wxGroupId = WebUtil.getInt(request, "wxGroupId", 0);
        //获取日志
        List<FlowRecord> logList = originalManager.initLogRecord(docID, docLibID, type);
        //获取日志--微信图文组稿件
        if (type ==1 && wxGroupId!=0){
            List<FlowRecord> logWxGroupArt = originalManager.getLogWxGroupArt(docID, wxGroupId);
            for (FlowRecord flow: logWxGroupArt) {
                logList.add(flow);
            }
            Collections.sort(logList, new Comparator<FlowRecord>(){
                @Override
                public int compare(FlowRecord flow1, FlowRecord flow2) {
                    //按照Person的年龄进行升序排列
                    return flow1.getStartTime().compareTo(flow2.getStartTime());
                }
            });
        }
        // 获取稿件
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);
        Set<String> flowNames = originalManager.throughFlowNodes(doc);

        model.put("logList", logList);
        model.put("flowNames", flowNames);
        return model;
    }

    /**
     * 源稿库  预览
     */
    @RequestMapping("OriginalPreview.do")
    public String originalPreview(HttpServletRequest request, HttpServletResponse response, Model model,
                                  int DocLibID, long DocIDs) throws E5Exception {

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(DocLibID, DocIDs);
        long groupID = WebUtil.getLong(request, "groupID", 0); // 源稿分类
        int siteID = WebUtil.getInt(request, "siteID", 1);
        Channel[] chs = ConfigReader.getChannels();

        //未签发稿件不在发布库  微信稿件没有模板预览，直接查看
        if( doc.getInt("a_status")!=Original.STATUS_PUBDONE || doc.getInt("a_type") == Original.TYPE_WEIXIN){
            model.addAttribute("DocLibID", DocLibID);
            model.addAttribute("DocIDs", DocIDs);
            model.addAttribute("groupID", groupID);

            model.addAttribute("siteID", siteID);
            return "redirect:/xy/article/View.do" ;
        }else{
            originalManager.getPreviewParam(request, model, DocLibID, DocIDs, chs, doc);
            model.addAttribute("groupID", groupID);
            model.addAttribute("siteID", siteID);
//			if (request.getParameter("app") != null) {
//				JSONObject result = (JSONObject) model.asMap().get("result");
//				return "redirect:" + result.getString("urlPad"); // 移动端预览
//			} else {
            return "/xy/article/OriginalPreview";
//			}
        }
    }

    /**
     * 源稿  退回
     */
    @RequestMapping(value = "OriginalReback.do")
    public String originalReback(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String strDocIDs = WebUtil.get(request, "DocIDs");
        int artLibID = WebUtil.getInt(request, "DocLibID", 0);
        Document article = docManager.get(artLibID, Long.parseLong(strDocIDs));
        String originalID = article.getString("a_originalID");
        if("0".equals(originalID)) originalID=null;
        String url = "../../e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
                + "&DocIDs=" + strDocIDs;;
        if(org.apache.commons.lang.StringUtils.isNotBlank(originalID)){
            int oriLibID = LibHelper.getLibID(DocTypes.ORIGINAL.typeID(),InfoHelper.getTenantCode(request));
            Document original = docManager.get(oriLibID, Long.parseLong(strDocIDs));
            //修改对应源稿库稿件的签发状态  以便再次签发
            int channel = article.getInt("a_channel");
            if (channel < 0) channel = 0;

            int orichannel = original.getInt("a_channel") - channel;
            if(orichannel == 0) {
                original.set("a_status", Original.STATUS_PUBNOT);
            }
            original.set("a_channel", orichannel);

            docManager.save(original);
            //撤稿
            if(article.getInt("a_status") == Article.STATUS_PUB_DONE){
                ArticleMsg articleMsg = PublishTrigger.getArticleMsg(article);
                PublishTrigger.articleDelete(articleMsg);
            }
            //删除发布库中的稿件
            docManager.delete(article);

            LogHelper.writeLog(oriLibID, Long.parseLong(originalID), request, "退回");
        } else {
            url += "&Info=" + URLEncoder.encode("本稿件不是源稿渠道推送，无法退回！", "UTF-8");
        }
        return "redirect:" + url;
    }

    /**
     * 源稿  微信图文退回
     */
    @RequestMapping(value = "OriginalWXReback.do")
    public String originalWXReback(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        String strDocIDs = WebUtil.get(request, "DocIDs");
        int sourceLibID = WebUtil.getInt(request, "DocLibID", 0);
        int wxLibID = LibHelper.getLibID(DocTypes.WXARTICLE.typeID(),InfoHelper.getTenantCode(request));
        String AccountID = WebUtil.get(request, "AccountID");
        SysUser user = ProcHelper.getUser(request);

        String[] ids = strDocIDs.split(",");
        for (String idStr : ids) {
            //删除微信中的稿件
            Document wxarticle = originalManager.getWXdoc(docManager, wxLibID,
                    Long.parseLong(idStr), sourceLibID, Long.parseLong(AccountID));
            String articleID = wxarticle.getString("wx_articleID");
            docManager.delete(wxarticle);
            Document sourceDoc = docManager.get(sourceLibID, Long.parseLong(articleID));
            Document[] sourceDoc1 = docManager.find(wxLibID, "wx_articleID = ?", new Object[]{articleID});

            if(sourceDoc1==null || (sourceDoc1!=null && sourceDoc1.length==0) ){//源稿库渠道
                sourceDoc.set("a_status", Original.STATUS_PUBNOT);
                sourceDoc.set("SYS_CURRENTSTATUS", "未签发");
                docManager.save(sourceDoc);
                LogHelper.writeLog(sourceLibID, Long.parseLong(idStr),user,  "未签发", "退回");
            }
        }
        return "redirect:../../e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
                + "&DocIDs=" + strDocIDs;
    }

    /**
     * 源稿库  编辑器  新建微信图文
     * @throws E5Exception
     */
    @RequestMapping("OriginalWX.do")
    public String originalWX(HttpServletRequest request, Map<String, Object> model) throws E5Exception{
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        boolean isNew = (docID == 0); //是否新写稿
        if(isNew){
            // 打开写稿界面时就预取稿件ID
            docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
        }
        Document doc = docManager.get(docLibID, docID);
        long catID = WebUtil.getLong(request, "groupID", 0); // 源稿分类
        int siteID = WebUtil.getInt(request, "siteID", 1);
        int status = WebUtil.getInt(request,"status",0);
        boolean isCheckThrough = WebUtil.getBoolParam(request,"isCheckThrough");
        int page = WebUtil.getInt(request, "page", 1);
        int count = 20;//默认每页20条数据
        String docMaterial = getDocMaterial(request, page, count);
        model.put("siteID", siteID);
        model.put("catID", catID);
        model.put("docID", docID);
        model.put("docLibID", docLibID);
        model.put("isNew", isNew);
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("hasSensitive", InfoHelper.sensitiveInArticle());
        model.put("hasIllegal", InfoHelper.illegalInArticle());
        model.put("status", status);

        //历史版本和流程记录用docid和doclibid
        try {
            model.put("docidForhisAndFlow", doc.get("wx_articleID"));
            model.put("docLibIdForhisAndFlow", doc.get("wx_articleLibID"));
        } catch (Exception e) {
            model.put("docidForhisAndFlow", null);
            model.put("docLibIdForhisAndFlow", null);
        }

        model.put("isCheckThrough", isCheckThrough);
        model.put("docMaterial", docMaterial);

        SysUser user = ProcHelper.getUser(request);
        model.put("userName", user.getUserName());
        model.put("userId",user.getUserID());
        originalManager.setFlowPower(docID, docLibID, model,user.getUserID(), isNew, catID, siteID);

        model.put("useMugeda", SecurityHelper.mugedaUsable());
        return "xy/article/OriginalWX";
    }

    /**
     * 源稿库  编辑器  微信图文的保存
     */
    @RequestMapping("SubmitWX.do")
    public void submitWX(HttpServletRequest request, HttpServletResponse response) {
        int docLibID = WebUtil.getInt(request, "docLibID", 0);
        long docID = WebUtil.getLong(request, "docID", 0);
        String data = WebUtil.get(request, "data");
        int saveType = WebUtil.getInt(request, "saveType",0);
        boolean isNew = WebUtil.getBoolParam(request, "isNew");
        String tenantCode = InfoHelper.getTenantCode(request);
        int wxgroupArticleLibID = LibHelper.getLibID(DocTypes.WXGROUPARTICLE.typeID(), tenantCode);
        SysUser user = ProcHelper.getUser(request);
        //前端传入的稿件列表
        JSONObject params = JSONObject.fromObject(data);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int accouentLibID = LibHelper.getLibID(DocTypes.WXGROUPARTICLE.typeID(), InfoHelper.getTenantCode(request));
        Document doc = null;
        try {
            if (isNew) {
                doc = docManager.newDocument(docLibID, docID);
                int catID = WebUtil.getInt(request, "catID", 0);
                doc.set("a_catID", catID);
                doc.setLastmodified(doc.getCreated());
                articleManager.setFlowByCat(doc, tenantCode);
                doc.set("a_siteID", Integer.valueOf(WebUtil.get(request, "siteID")));
                doc.set("a_type", Original.TYPE_WEIXIN);
                doc.set("SYS_AUTHORID", user.getUserID());
            } else {
                doc = docManager.get(docLibID, docID);
            }
            if(docLibID == wxgroupArticleLibID) {
                doc.set("wx_content", params.getString("content"));
                doc.set("wx_url", params.getString("url"));
                doc.setTopic(params.getString("title"));
                doc.set("wx_abstract", params.getString("abstracts"));
                doc.set("wx_pic", params.getString("picBig"));
                //原创声明
                doc.set("SYS_AUTHORS", params.getString("author"));
                doc.setCurrentUserID(user.getUserID());
                doc.setCurrentUserName(user.getUserName());

            }else {
                doc.set("a_content", params.getString("content"));
                doc.set("a_url", params.getString("url"));
                doc.setTopic(params.getString("title"));
                doc.set("a_linkTitle", params.getString("title"));
                doc.set("a_abstract", params.getString("abstracts"));
                doc.set("a_picBig", params.getString("picBig"));
                //原创声明
                doc.set("a_copyright", params.getInt("copyright"));
                doc.set("SYS_AUTHORS", params.getString("author"));
                doc.setCurrentUserID(user.getUserID());
                doc.setCurrentUserName(user.getUserName());
            }



            docManager.save(doc);

            if(accouentLibID == docLibID){
                //微信图文组稿件
                //记录历史版本
                articleManager.recordHistoryVersionForWXGroup(docManager, doc,
                        tenantCode, isNew?"保存":"修改", user.getUserName());
            }else {
                //记录历史版本
                articleManager.recordHistoryVersion(docManager, doc,
                        tenantCode, isNew?"保存":"修改", user.getUserName());
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        //写日志
        if(saveType<1 || isNew) {
            String procName = isNew ? "创建" : "修改";
            if(accouentLibID == docLibID){
                LogHelper.writeLog(accouentLibID, Long.parseLong(doc.getString("WX_ARTICLEID")), user, procName,String.valueOf(doc.getInt("WX_GROUPID")));
            }else {
                LogHelper.writeLog(docLibID, docID, user, procName, "");
            }
        }
        InfoHelper.outputText("ok", response);
    }

    /**
     * 源稿库   编辑器    查询微信图文数据
     */
    @RequestMapping("ArticlesWX.do")
    public void getGroupArticles(HttpServletRequest request, HttpServletResponse response) throws E5Exception{
        int docLibID = WebUtil.getInt(request, "docLibID", 0);
        long docID = WebUtil.getLong(request, "docID", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);

        JSONObject jsonObject = new JSONObject();
        if (doc != null) {
            if (docLibID == DocTypes.WXARTICLE.typeID()){
                jsonObject.put("title", doc.getTopic());
                jsonObject.put("content", doc.getString("WX_CONTENT"));
                jsonObject.put("url", doc.getString("WX_URL"));
                jsonObject.put("picBig", doc.getString("WX_PIC"));
                jsonObject.put("abstracts", doc.getString("WX_ABSTRACT"));
                //是否原创微信稿件中无此字段
                jsonObject.put("copyright", doc.getString("a_copyright"));
                jsonObject.put("author", doc.getString("SYS_AUTHORS"));
            }else {
                jsonObject.put("title", doc.getTopic());
                jsonObject.put("content", doc.getString("a_content"));
                jsonObject.put("url", doc.getString("a_url"));
                jsonObject.put("picBig", doc.getString("a_picBig"));
                jsonObject.put("abstracts", doc.getString("a_abstract"));
                jsonObject.put("copyright", doc.getString("a_copyright"));
                jsonObject.put("author", doc.getString("SYS_AUTHORS"));
            }
        }

        InfoHelper.outputJson(jsonObject.toString(), response);
    }


    /**
     * 源稿库  获取签发信息
     */
    @RequestMapping(value = "SignInfo.do")
    @ResponseBody
    public Map<String, Object> signInfo(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        long docID = WebUtil.getInt(request, "DocIDs", 0); // 稿件ID
        int signType = WebUtil.getInt(request, "SignType", 0); // 0：预签发  1：已签发
        // 获取稿件
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document original = docManager.get(docLibID, docID);
        String signInfo = "没有预签或签发信息！";
        if(signType == 0){
            String pubsign = original.getString("a_pubsign");
            if(!StringUtils.isBlank(pubsign)){
                JSONObject params = JSONObject.fromObject(pubsign);
                signInfo = "预签发：" + params.getString("weixinName");
            }
        }else{
            SignedChInfo signedWXInfo = originalManager.getSignedWeixin(original);
            signInfo = "已签发：" + signedWXInfo.getColRelName();
        }
        model.put("signInfo", signInfo);
        return model;
    }

    /**
     * 源稿库 选用
     */
    @RequestMapping(value = "SelectUse.do")
    public void selectUse(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long catID = WebUtil.getLong(request, "catID", 0);
        String tenantCode = InfoHelper.getTenantCode(request);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document original = docManager.get(docLibID, docID);
        DBSession conn = null;
        try {
            conn = E5docHelper.getDBSession(docLibID);
            conn.beginTransaction();

            long newArticleID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID());
            Document newOriginal = docManager.newDocument(original, docLibID, newArticleID);
            newOriginal.setLocked(false);
            Timestamp now = DateUtils.getTimestamp();
            newOriginal.setCreated(now);
            newOriginal.setLastmodified(now);
            newOriginal.set("a_catID", catID);
            articleManager.setFlowByCat(newOriginal, tenantCode);
            newOriginal.set("a_status", Original.STATUS_SUBMIT);
            double order = articleManager.getNewOrder(newOriginal);
            newOriginal.set("a_order", order);
            newOriginal.set("a_channel", 0);
            newOriginal.set("a_pubsign", "");
            docManager.save(newOriginal, conn);
            //保存挂件
            originalManager.saveWidgets(newOriginal, original, conn);
            //保存附件
            originalManager.saveAttachments(newOriginal, original, conn);
            conn.commitTransaction();
            SysUser sysUser = ProcHelper.getUser(request);
            //记录历史版本
            articleManager.recordHistoryVersion(docManager, original,
                    tenantCode, "选用", sysUser.getUserName());
            //保存流程记录
            originalManager.saveLogs(newOriginal, original, sysUser);
            LogHelper.writeLog(docLibID, newArticleID, sysUser, "选用", "源ID：" + docID);
        } catch (Exception e) {
            ResourceMgr.rollbackQuietly(conn);
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
        }
    }

    /**
     * 源稿库  历史版本列表查询
     */
    @RequestMapping(value = "HistoryInfo.do")
    public void historyInfo(HttpServletRequest request,
                            HttpServletResponse response) {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int page = WebUtil.getInt(request, "Page", 1); //默认为第一页
        int pageCount = WebUtil.getInt(request, "PageCount", 4); //默认为第一页
        int type = WebUtil.getInt(request, "type", 0);
        int wxGroupId = WebUtil.getInt(request, "wx_groupId", 0);
        String tenantCode = InfoHelper.getTenantCode(request);
        DocLib hisLib = LibHelper.getLib(DocTypes.HISTORYORI.typeID(), tenantCode);
        String returnStr = originalManager.getHistoryInfo(page, pageCount, docID, hisLib,type,wxGroupId);
        InfoHelper.outputJson(returnStr, response);
    }

    /**
     * 源稿库 提交
     */
    @RequestMapping(value = "OriginalSend.do")
    public String originalSend(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        long docID = WebUtil.getInt(request, "DocIDs", 0); // 稿件ID
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document original = docManager.get(docLibID, docID);
        int status = original.getInt("a_status");
        String url = "../../e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
                + "&DocIDs=" + docID;;
        if(status == Original.STATUS_SUBMIT){
            url += "&Info=" + URLEncoder.encode("稿件是已提交状态，请勿重复提交！", "UTF-8");
        } else {
            original.set("a_status", Original.STATUS_SUBMIT);
            docManager.save(original);
            //保存历史版本
            articleManager.recordHistoryVersion(docManager, original,
                    InfoHelper.getTenantCode(request), "提交", ProcHelper.getUser(request).getUserName());
            url += "&Info=" + URLEncoder.encode("提交成功！", "UTF-8");
        }
        return "redirect:" + url;
    }

    /**
     * 源稿库 提交（编辑器内部）
     */
    @RequestMapping(value = "OriginalEditorSend.do")
    @ResponseBody
    public String originalEditorSend(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0); // 稿件库ID
        long docID = WebUtil.getInt(request, "DocIDs", 0); // 稿件ID
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document original = docManager.get(docLibID, docID);
        int status = original.getInt("a_status");
        original.set("SYS_CURRENTSTATUS","已提交");
        String returnData = "";
        if(status == Original.STATUS_SUBMIT){
            returnData = "failure";
        } else {
            original.set("a_status", Original.STATUS_SUBMIT);
            docManager.save(original);
            //保存历史版本
            articleManager.recordHistoryVersion(docManager, original,
                    InfoHelper.getTenantCode(request), "提交", ProcHelper.getUser(request).getUserName());
            returnData = "success";
        }
        return returnData;
    }


    public String getDocMaterial(HttpServletRequest request,int page, int paramCount){
        SysUser user = ProcHelper.getUser(request);
        int userID = user.getUserID();
//        DocumentManager docManager = DocumentManagerFactory.getInstance();
//        DocLib[] libs1 = new DocLib[0];
//        Document[] documents = null;
//        try {
//            libs1 = LibHelper.getLibs(DocTypes.ORIGINAL.typeID());
//            int docLibID1 = libs1[0].getDocLibID();
//            documents = docManager.find(docLibID1, "SYS_CURRENTUSERID=? and a_status != '0' order by SYS_DOCUMENTID",
//                    new Object[]{userID});
//            return documents;
//        } catch (E5Exception e) {
//            e.printStackTrace();
//        }
//        return documents;

        String sql = "SELECT * FROM xy_original WHERE SYS_CURRENTUSERID="+userID+" and a_status != '0'  ORDER BY SYS_DOCUMENTID DESC";
        JSONArray array = new JSONArray();
        JSONObject rtnJson = new JSONObject();

        DBSession db = null;
        IResultSet rs = null;
        DBSession countdb = null;
        IResultSet countrs = null;
        try {
            db = Context.getDBSession();
            String countSql = "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"));
            sql = db.getDialect().getLimitString(sql, (page - 1) * paramCount, paramCount);
            rs = db.executeQuery(sql, null);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("materialID", rs.getInt("SYS_DOCUMENTID"));
                json.put("materialLibID", rs.getInt("SYS_DOCLIBID"));
                json.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                json.put("createTime", StringUtils.getNotNull(rs.getString("SYS_CREATED")));
                json.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
                json.put("lastPerson", StringUtils.getNotNull(rs.getString("SYS_CURRENTUSERNAME")));
                json.put("currentstatus", StringUtils.getNotNull(rs.getString("SYS_CURRENTSTATUS")));
                json.put("abstract", StringUtils.getNotNull(rs.getString("a_abstract")));
                array.add(json);
            }
            int materialCount = 0;
            countdb = Context.getDBSession();
            countrs = countdb.executeQuery(countSql);
            if (countrs.next()) {
                materialCount = countrs.getInt(1);
            }
            int pagecount = materialCount/paramCount;
            if(materialCount%paramCount > 0) pagecount++;
            rtnJson.put("page", page);
            //rtnJson.put("pagesize", paramCount);
            rtnJson.put("pagecount", pagecount);
            rtnJson.put("list", array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(db);
            ResourceMgr.closeQuietly(countrs);
            ResourceMgr.closeQuietly(countdb);
        }
        return rtnJson.toString();
    }

}
