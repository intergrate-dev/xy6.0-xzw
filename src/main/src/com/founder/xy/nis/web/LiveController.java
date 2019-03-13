package com.founder.xy.nis.web;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.*;
import com.founder.xy.commons.web.HttpClientUtil;
import com.founder.xy.nis.DiscussManager;
import com.founder.xy.nis.ForumManager;
import com.founder.xy.nis.QQLive;
import com.founder.xy.nis.qcloud.Module.Vod;
import com.founder.xy.nis.qcloud.QcloudApiModuleCenter;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static com.founder.xy.redis.RedisKey.*;

/**
 * 直播功能
 *
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/nis")
public class LiveController {
    @Autowired
    ForumManager forumManager;

    @Autowired
    DiscussManager discussManager;

    public static String LIVE_SERVER_URL = "liveplay.myqcloud.com/live/";

    /**
     * 直播，发布话题/继续报道
     */
    @RequestMapping(value = "Live.do")
    public ModelAndView live(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> model = prepareModel(request, true);

        long parentID = WebUtil.getLong(request, "DocIDs", 0);
        if (parentID > 0) {
            int docLibID = WebUtil.getInt(request, "DocLibID", 0);
            //这是“继续报道”操作，要设置话题的标题
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document parent = docManager.get(docLibID, parentID);
            if (parent != null)
                model.put("topic", parent.getTopic());
        }
        model.put("parentID", parentID);
        model.put("author", ProcHelper.getUserName(request));

        return new ModelAndView("/xy/nis/Live", model);
    }

    /**
     * 直播修改
     */
    @RequestMapping(value = "LiveUpdate.do")
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();

        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);

        //使用定制的表单界面
        String formCode = "formLive";
        prepareForm(request, model, formCode, docLibID, docID);

        //读库，取出其父帖id（对于直播，父帖就是主贴）
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);
        long parentID = doc.getLong("a_parentID");

        model.put("isNew", false);
        model.put("parentID", parentID);

        return new ModelAndView("/xy/nis/Live", model);
    }

    /**
     * 直播，保存
     */
    @RequestMapping(value = "LiveSubmit.do")
    public String submit(
            HttpServletRequest request, HttpServletResponse response,
            long DocID, int DocLibID, String a_attachments, int a_status)
            throws Exception {
        boolean isNew = WebUtil.getBoolParam(request, "isNew");

        //获得live对象
        Document doc = getLiveModel(request, DocID, DocLibID, isNew);
        //判断附件是否改变了
        boolean isChanged = isNew || liveAttachmentsChanged(doc, a_attachments);
        String config="";
        if(!isNew){
            config=doc.getString("a_config");
        }
        //保存live对象
        FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
        saver.handle(doc, request);

        changeArticleStatus(isNew, doc);

        if (isChanged) {
            saveLiveAttachments(DocID, DocLibID, isNew, doc);
        }else{
            doc.set("a_config", config);
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            docManager.save(doc);
        }

        //如果是修改清除 redis
        cleanRelatedRedisKey(doc.getInt("a_siteID"), isNew, doc.getDocID(), false);

        //返回
        String uuid = WebUtil.get(request, "UUID");
        if (uuid != null) {
            //若是从主界面点击的操作
            String url = "redirect:/e5workspace/after.do?UUID=" + uuid
                    + "&DocIDs=" + DocID;
            return url;
        } else {
            return "";
        }
    }

    private void changeArticleStatus(boolean isNew, Document doc) throws E5Exception {
        if(isNew || doc == null)return;
		/*String sql = "update xy_articleapp set a_liveStatus=" + doc.getInt("a_status") + " where a_linkID=" + doc.getDocID();
		InfoHelper.executeUpdate(sql, null);*/

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] attDocs = docManager.find(LibHelper.getArticleAppLibID(), " a_linkID=? ", new Object[]{doc.getDocID()});
        if(attDocs.length>0)
            for(Document attDoc : attDocs){
                attDoc.set("a_liveStatus", doc.getInt("a_status"));
                String key = RedisKey.APP_ARTICLELIST_ONE_KEY + attDoc.getDocID();
                if(RedisManager.exists(key)){
                    JSONObject inJson = JSONObject.fromObject(RedisManager.get(key));;
                    inJson.put("liveStatus", doc.getInt("a_status"));

                    RedisManager.setLonger(key, inJson.toString());
                }
            }
    }

    /**
     * 保存完 live对象之后，清除redis缓存
     *
     * @param isNew
     * @param docId
     */
    private void cleanRelatedRedisKey(int siteID, boolean isNew, long docId, boolean isDelete) {
        if (!isNew) {
            if (RedisManager.exists(APP_LIVE_MAIN_KEY + docId))
                RedisManager.del(APP_LIVE_MAIN_KEY + docId);
            //修改清楚
            if (!isDelete && RedisManager.exists(APP_LIVELIST_KEY + docId + ".0"))
                RedisManager.del(APP_LIVELIST_KEY + docId + ".0");
            if (isDelete) {
                RedisManager.clearKeyPages(APP_LIVELIST_KEY + docId);
                RedisManager.clearKeyPages(APP_LIVE_SUBLIST_KEY + docId);
            }
        }

        String key = RedisManager.getKeyBySite(RedisKey.APP_LIVE_COMING_KEY, siteID);
        RedisManager.clear(key);
    }

    private boolean liveAttachmentsChanged(Document doc, String attachment) {
        String _attachment = doc.getString("a_attachments");
        if (StringUtils.isBlank(_attachment) || StringUtils.isBlank(attachment)) {
            return true;
        }
        JSONObject _json = JSONObject.fromObject(_attachment);
        JSONObject json = JSONObject.fromObject(attachment);
        if (_json == null || json == null) {
            return true;
        }
        JSONArray _pics = JsonHelper.getJsonArray(_json.getString("pics"));
        JSONArray pics = JsonHelper.getJsonArray(json.getString("pics"));
        if (_pics == null || pics == null) {
            return true;
        }

        if (!pics.equals(_pics)) {
            return true;
        }

        return false;
    }

    private void saveLiveAttachments(long DocID, int DocLibID, boolean isNew, Document doc) throws E5Exception {
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
        if (!isNew) {
            //先删附件
            deleteAttachments(attLibID, DocLibID, DocID);
        }

        //保存附件
        JSONObject atts = JsonHelper.getJson(doc.getString("a_attachments"));
        if (atts != null) {
            saveAttachments(atts, "pics", attLibID, DocLibID, DocID, 1);
            //saveAttachments(atts, "videos", attLibID, DocLibID, DocID, 2);
        }

        //发布图片
        int result = forumManager.transWhenPass(doc);
        //发布成功, 将发布后的地址放到a_config中
        if (result == 0) {
            String config = doc.getString("a_config");
            JSONObject json = JsonHelper.getJson(config);
            if (json != null) {
                String appBanner = JsonHelper.getString(json, "appBanner");
                String webBanner = JsonHelper.getString(json, "webBanner");
                if (!StringUtils.isBlank(appBanner) || !StringUtils.isBlank(webBanner)) {
                    boolean changed = addUrlToJson(DocID, DocLibID, attLibID, json, appBanner, webBanner);
                    if (changed) {
                        doc.set("a_config", json.toString());
                        DocumentManager docManager = DocumentManagerFactory.getInstance();
                        docManager.save(doc);
                    }
                }
            }
        }
    }

    private boolean addUrlToJson(
            long docId, int docLibId, int attLibID, JSONObject json, String appBanner, String webBanner) {
        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document[] attDocs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=? and att_type=?",
                                                 new Object[]{docId, docLibId, 1});
            String url;
            boolean isChanged = false;
            for (Document doc : attDocs) {
                url = doc.getString("att_path");
                if (!StringUtils.isBlank(url) && url.equals(appBanner)) {
                    json.put("appBannerUrl", doc.getString("att_url"));
                    isChanged = true;
                }
                if (!StringUtils.isBlank(url) && url.equals(webBanner)) {
                    json.put("webBannerUrl", doc.getString("att_url"));
                    isChanged = true;
                }
            }
            return isChanged;
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Document getLiveModel(
            HttpServletRequest request, long DocID, int DocLibID, boolean isNew) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc;
        if (isNew) {
            doc = docManager.newDocument(DocLibID, DocID);
            ProcHelper.initDoc(doc, request);
            doc.set("a_order", DocID);
            SysUser user = ProcHelper.getUser(request);
            doc.set("SYS_AUTHORID", user.getUserID());
        } else {
            doc = docManager.get(DocLibID, DocID);
        }
        return doc;
    }

    /**
     * 直播话题详情
     * 直播列表上点标题，查看直播的话题详情。
     * 以论坛盖楼的形式展示话题以及话题下的所有跟帖。
     */
    /*@RequestMapping(value = "LiveView.do")
    public void liveView(
            HttpServletResponse response, long id,
            int start, int count) throws Exception {
        JSONObject result = nisViewManager.getLiveView(id, start, count, -1, -1);
        InfoHelper.outputText(String.valueOf(result), response);
    }*/
    @RequestMapping(value = "LiveView.do")
    public String liveView(
            HttpServletResponse response, int DocLibID, long DocIDs, int siteID, Model model) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document document = docManager.get(DocLibID, DocIDs);
        model.addAttribute("topic", document.getTopic());
        String startTime = document.getString("a_startTime");
        String endTime = document.getString("a_endTime");
        model.addAttribute("startTime",
                           StringUtils.isBlank(startTime) ? "" : startTime.substring(0, startTime.length() - 2));
        model.addAttribute("endTime", StringUtils.isBlank(endTime) ? "" : endTime.substring(0, endTime.length() - 2));
        model.addAttribute("createUser", document.getString("SYS_CURRENTUSERNAME"));
        model.addAttribute("content", document.getString("a_content"));
        model.addAttribute("DocIDs", DocIDs);
        model.addAttribute("DocLibID", DocLibID);
        model.addAttribute("a_config", document.getString("a_config"));
        model.addAttribute("liveType", (document.getInt("a_type") == 0 ? "图文直播" : "视频直播"));
        model.addAttribute("siteID", siteID);
        int type = document.getInt("a_type");
        model.addAttribute("type", type);

        int status = document.getInt("a_status");
        model.addAttribute("status", status);

        String config = document.getString("a_config");
        if (!StringUtils.isBlank(config)) {
	        JSONObject json = JSONObject.fromObject(config);
	        //如果是图片的话，获得 banner
	        if (type == 0 && json.containsKey("webBanner")) {
	            model.addAttribute("webBanner", "../../xy/image.do?path=" + json.getString("webBanner"));
	        }
	
	        if (type == 0 && json.containsKey("appBanner")) {
	            model.addAttribute("appBanner", "../../xy/image.do?path=" + json.getString("appBanner"));
	        }
	
	        //如果是视频的话，获得url - 使用标清
	        if (type == 1 && json.containsKey("videos")) {
	            getVideoUrlJson(model, json, status);
	        }
        }

        return "/xy/nis/LiveView";
    }

    //直播中 .f230.av.m3u8
    private void getVideoUrlJson(Model model, JSONObject json, int status) {
        JSONArray videos = json.getJSONArray("videos");
        model.addAttribute("liveSize", videos.size());
        boolean canCut = true;

        String url = null;
        JSONObject video;
        for (int i = 0, len = videos.size(); i < len; i++) {
            url = null;
            video = videos.getJSONObject(i);
            if (status == 1 && video.containsKey("webLiveUrl")&&(video.getInt("type")==0||(video.getInt("type")==1&&"".equals(video.getString("webPlaybackUrl"))))) {
                url = video.getString("webLiveUrl");
                //url = url.substring(0, url.lastIndexOf(".")) + "_900.m3u8";
            } else {
                if (status == 2 && video.containsKey("webPlaybackUrl")) {
                    url = video.getString("webPlaybackUrl");
                    /*if (!StringUtils.isBlank(url) && url.contains("."))
                        url = url.substring(0, url.lastIndexOf(".")) + ".f230.av.m3u8";*/
                }
            }
            //没有的话，默认是允许的
            int channelStatus = 1;
            if(status == 1 && video.containsKey("channelStatus")){
                channelStatus = video.getInt("channelStatus");
            }
            String streamId = null;
            if (status == 1 && video.containsKey("streamID")) {
                streamId = video.getString("streamID");
            }

            canCut = canCut && !"1".equals(RedisManager.get(APP_LIVE_STATUS_KEY + "current." + streamId));

            model.addAttribute("url_" + i, url);
            model.addAttribute("channel_status_" + i, channelStatus);
            model.addAttribute("streamId_" + i, streamId);
            String key=APP_LIVE_STATUS_KEY + "current." + streamId;
            String a=RedisManager.get(key);

            model.addAttribute("liveCurrentStatus_" + i, "1".equals(RedisManager.get(APP_LIVE_STATUS_KEY + "current." + streamId)));
            model.addAttribute("type_"+i,video.getInt("type"));
        }

        model.addAttribute(status);
        model.addAttribute("canCut", canCut);

    }

    /**
     * 准备相关数据
     */
    private Map<String, Object> prepareModel(HttpServletRequest request, boolean isNew)
            throws E5Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long docID = WebUtil.getLong(request, "DocIDs", 0);

        int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
        //继续报道是在父直播上操作，所以不能按docID=0来判断，要直接传入isNew=true。其它情况下按docID自动判断
        if (isNew || docID == 0) {
            docID = InfoHelper.getNextDocID(docTypeID);
            isNew = true;
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("isNew", isNew);
        model.put("isActivity", docTypeID == DocTypes.ACTIVITY.typeID()); //标记是否活动

        //使用定制的表单界面
        String formCode = "formLive";
        prepareForm(request, model, formCode, docLibID, docID);

        return model;
    }

    /**
     * 准备表单
     */
    private void prepareForm(
            HttpServletRequest request, Map<String, Object> model, String formCode,
            int docLibID, long docID) throws E5Exception {
        String uuid = WebUtil.get(request, "UUID");
        //取出定制的表单，数组里0是js和css等引用文件语句，1是form里的字段内容
        String[] jsp = FormViewHelper.getFormJsp(docLibID, docID, formCode, uuid);
        model.put("formHead", jsp[0]);
        model.put("formContent", jsp[1]);
        model.put("siteID", WebUtil.get(request, "siteID"));
        model.put("fromPage", "live");

        String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
        model.put("videoPlugin", _videoPluginUrl);//视频播放控件地址
        model.put("sessionID", request.getSession().getId()); // 解决Firefox下flash上传控件session丢失
    }

    /**
     * 删除附件
     */
    private void deleteAttachments(int attLibID, int docLibID, long docID) throws E5Exception {
        String tableName = LibHelper.getLibTable(attLibID);

        String sql = "delete from " + tableName + " where att_articleID=? and att_articleLibID=?";
        Object[] params = new Object[]{docID, docLibID};

        InfoHelper.executeUpdate(attLibID, sql, params);
    }

    //保存附件
    private void saveAttachments(
            JSONObject atts, String tag, int attLibID, int docLibID, long docID, int type) throws E5Exception {
        JSONArray pics = JsonHelper.getJsonArray(atts, tag);
        if (pics == null || pics.isEmpty()) return;

        int attTypeID = DocTypes.NISATTACHMENT.typeID();
        long idStart = EUID.getID("DocID" + attTypeID, pics.size());

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        for (int i = 0; i < pics.size(); i++) {
            Document attach = docManager.newDocument(attLibID, idStart++);
            attach.set("att_articleID", docID);
            attach.set("att_articleLibID", docLibID);
            attach.set("att_type", type); //0:文件，1:图片;2:视频
            if (type == 1) {
                String url = pics.getString(i);
                attach.set("att_path", url);
            } else if (type == 2){
				JSONObject att = (JSONObject)pics.get(i);
				String url = JsonHelper.getString(att, "urlApp");
				attach.set("att_url", url);

				String picID = JsonHelper.getString(att, "videoID");// 来自视频库时，视频ID
				if (!StringUtils.isBlank(picID)) {
					long[] ids = StringUtils.getLongArray(picID);
					// 把视频的视频时长,关键帧图片地址放到附件里
					Document v = docManager.get((int) ids[0], ids[1]);
					//attach.set("att_picPath", v != null ? v.getString("v_picPath") : "");
					attach.set("att_duration",InfoHelper.parseTime(v != null ? v.getString("v_time") : null));
				}
            }

            docManager.save(attach);
        }
    }

    /**
     * 获得推流、RTMP、FLV、HLS的地址
     *
     * @param streamId docId + userId
     * @param extDate  过期时间，格式yyyy-MM-dd HH:mm:ss
     */
    @RequestMapping(value = "getLiveUrl.do")
    public void getLiveUrl(HttpServletResponse response, String streamId, String extDate) {
        //结果json
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", 0);

        try {
            checkConfig();
        } catch (E5Exception e) {
            e.printStackTrace();
            resultJson.put("code", 1);
            resultJson.put("msg", "后台没有配置直播相关参数！请联系管理员进行配置！");
            InfoHelper.outputJson(resultJson.toString(), response);
            return;
        }

        streamId += "_" + new Date().getTime();
        //从后台读取bizid - 从直播间管理页面获得
        JSONObject json = buildUrlJson(streamId, extDate);

        resultJson.put("url", json);
        InfoHelper.outputJson(resultJson.toString(), response);

    }
    /**
     * 单独获得直播流唯一ID，为第三方直播使用
     *
     * @param streamId docId + userId
     * @param extDate  过期时间，格式yyyy-MM-dd HH:mm:ss
     */
    @RequestMapping(value = "getStreamID.do")
    public void getStreamID(HttpServletResponse response, String streamId, String extDate) {
        //结果json
        String bizid = InfoHelper.getConfig("直播", "bizid");

        streamId =bizid + "_" +streamId+ "_" + new Date().getTime();
        //从后台读取bizid - 从直播间管理页面获得
        JSONObject json = buildUrlJson(streamId, extDate);

        InfoHelper.outputText(streamId, response);

    }

    private JSONObject buildUrlJson(String streamId, String extDate) {
        String bizid = InfoHelper.getConfig("直播", "bizid");
        //拼接 streamId
        streamId = bizid + "_" + streamId;

        //推流secretKey
        String pushParamKey = QQLive.buildLiveUrl(InfoHelper.getConfig("直播", "推流防盗链key"), streamId, extDate);
        // 公用url
        String url = bizid + "." + LIVE_SERVER_URL + streamId;

        //拼装Json
        JSONObject json = new JSONObject();
        json.put("streamID", streamId);
        json.put("livePush", "rtmp://" + url + "?bizid=" + bizid + "&" + pushParamKey + "&record_interval=5400&record=hls");
        json.put("RTMP", "rtmp://" + url);
        json.put("FLV", "http://" + url + ".flv");
        json.put("HLS", "http://" + url + ".m3u8");

        json.put("uploadUrl", "rtmp://" + bizid + ".livepush.myqcloud.com/live/"+ streamId + "?bizid=" + bizid + "&" + pushParamKey + "&record_interval=5400&record=hls");
        json.put("appLiveUrl", "rtmp://" + url);
        json.put("webLiveUrl", "http://" + url + ".m3u8");
        return json;
    }

    private void checkConfig() throws E5Exception {
        if (InfoHelper.getConfig("直播", "bizid") == null)
            throw new E5Exception("没有配置 直播-bizid");
        if (InfoHelper.getConfig("直播", "推流防盗链key") == null)
            throw new E5Exception("没有配置 直播-推流防盗链key");
    }

    /**
     * 获得所有用户
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "querySuggestion.do")
    public void querySuggestion(HttpServletRequest request, HttpServletResponse response) {
        JSONArray array = new JSONArray();
        int libId = LibHelper.getUserExtLibID(request);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document[] docs = docManager.find(libId, "1=1", new Object[]{});
            JSONObject json;
            for (Document doc : docs) {
                json = new JSONObject();
                json.put("showName", doc.getString("u_name")  + "(" + doc.getString("u_code") + ")");
                json.put("userName", doc.getString("u_name"));
                json.put("userId", doc.getDocID());
                json.put("userCode", doc.getString("u_code"));
                array.add(json);
            }

        } catch (E5Exception e) {
            e.printStackTrace();
        }
        InfoHelper.outputJson(array.toString(), response);
    }

    @RequestMapping("shutDownVideoLive.do")
    public void shutDownVideoLive(HttpServletResponse response, int docLibID, long docID) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document document = docManager.get(docLibID, docID);

            document.set("a_status", 2);
            docManager.save(document);
        } catch (E5Exception e) {
            json.put("code", 1);
            json.put("msg", "获取稿件时出错！");
            json.put("error", e.getMessage());
            e.printStackTrace();
        }

        //清楚redis
        try {
            if (RedisManager.exists(APP_LIVE_MAIN_KEY + docID))
                RedisManager.del(APP_LIVE_MAIN_KEY + docID);

            if (RedisManager.exists(APP_LIVELIST_KEY + docID + ".0"))
                RedisManager.del(APP_LIVELIST_KEY + docID + ".0");
        } catch (Exception e) {
            json.put("code", 1);
            json.put("msg", "清楚Redis时出错！");
            json.put("error", e.getMessage());
            e.printStackTrace();
        }

        InfoHelper.outputJson(json.toString(), response);
    }

    @RequestMapping("queryReplayUrl.do")
    public void queryReplayUrl(HttpServletResponse response, int docLibID, long docID) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document document = docManager.get(docLibID, docID);
            String config = document.getString("a_config");
            if (StringUtils.isBlank(config) || !isJson(config)) {
                throw new RuntimeException("没有视频信息！");
            }
            JSONObject configJson = JSONObject.fromObject(config);

            //如果没有concatStatus这个字段，说明是第一次合成
            // currentStatus: 0: 正在合成中；1: 已合成；2：全部合成；3: 重新合成
            //1. 查询录播列表；2. 合并； 3. 返回结果
            if (!configJson.containsKey("currentStatus")) {
                //查询录播列表，并发出合并录播指令
                getRecordingList(configJson);
                if(!configJson.getBoolean("currentStatus")){
                    throw new RuntimeException("没有需要合成的视频！");
                }
                document.set("a_config", configJson.toString());
                docManager.save(document);
                json.put("msg", "已发送录播视频合并指令...视频正在合并...请稍后再试..");
            } else {
                int status = configJson.getInt("currentStatus");
                if (status == 0) {
                    JSONArray videos = configJson.getJSONArray("videos");
                    for(int i = 0, len = videos.size(); i < len ; i ++) {
                        //1. 查询合并结果
                        JSONObject _json = pullEvent(videos.getJSONObject(i).getString("vodId"));
                        if (_json == null) {
                            InfoHelper.outputJson("视频正在合并中... 请稍后再试...", response);
                            return;
                        }
                        configJson.put("vodId", "");
                        configJson.put("concatStatus", 1);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", 1);
            json.put("error", e.getLocalizedMessage());
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    private JSONObject pullEvent(String vodId) throws Exception {
        TreeMap<String, Object> config = getConfig();
        QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Vod(), config);
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        String result;
        try {
            result = module.call("PullEvent", params);
            JSONObject json_result = JSONObject.fromObject(result);
            if (!json_result.containsKey("code") || json_result.getInt("code") != 0) {
                throw new Exception("获取腾讯云信息列表出错! json: " + json_result.toString());
            }

            JSONArray eventList = json_result.getJSONArray("eventList");

            JSONObject json;
            String _vodId;
            for(int i = 0, len = eventList.size() ; i < len; i++) {
                json = eventList.getJSONObject(i);
                _vodId = json.getJSONObject("data").getString("vodTaskId");
                if (!StringUtils.isBlank(_vodId) && _vodId.equals(vodId)) {
                    return json;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("pullEvent throw exception!");
        }

        return null;
    }

    private String concatVideos(JSONArray fileList) throws Exception {
        String streamId = "";
        TreeMap<String, Object> config = getConfig();
        QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Vod(), config);
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        JSONObject json = null;
        for (int i = 0, len = fileList.size(); i < len; i++) {
            json = fileList.getJSONObject(i);
            if (json.containsKey("file_format") && json.getInt("file_format") == 2) {
                params.put("srcFileList." + i + ".fileId", json.getString("file_id"));
                if (StringUtils.isBlank(streamId)) {
                    streamId = json.getString("stream_id") + "_" + (System.currentTimeMillis() / 10000);
                }
            }
        }
        params.put("name", streamId);
        params.put("dstType.0", "m3u8");
        String result;
        try {
            result = module.call("ConcatVideo", params);
            JSONObject json_result = JSONObject.fromObject(result);
            if (json_result.getInt("code") == 0) {
                return json_result.getString("vodTaskId");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("concatVideos throw exception!");
        }
        return null;
    }

    private void getRecordingList(JSONObject configJson) throws Exception {
        JSONObject vJson;
        //从config中 获得stream
        if (configJson.has("videos")) {
            JSONArray videoArray = configJson.getJSONArray("videos");
            //遍历 stream
            for (int i = 0, len = videoArray.size(); i < len; i++) {
                vJson = videoArray.getJSONObject(i);
                //给腾讯云发送http请求，并获得返回数据
                String _r = getLiveResponseString(vJson);
                if (!isJson(_r)) {
                    throw new RuntimeException("腾讯云返回的数据不是Json数据:" + _r);
                }

                JSONObject data = JSONObject.fromObject(_r);
                if (!data.containsKey("output")) {
                    throw new RuntimeException("腾讯云录播视频还未保存成功！稍后再试！");
                }

                JSONArray fileList = data.getJSONObject("output").getJSONArray("file_list");
                if (fileList != null && fileList.size() == 0) {
                    throw new RuntimeException("腾讯云录播视频还未保存成功！稍后再试！");
                }

                //2. 合成视频
                String vodId = concatVideos(fileList);

                if (StringUtils.isBlank(vodId)) {
                    throw new RuntimeException("调用腾讯云接口出错！请稍后再试！");
                }

                //3. 在config中记录状态
                vJson.put("vodId", vodId);
                vJson.put("concatStatus", 0);
                configJson.put("currentStatus", 0);
            }
        }
    }

    private String getLiveResponseString(JSONObject stream) {
        String streamId = stream.getString("streamID");
        long t = System.currentTimeMillis() / 1000 + 60 * 60;
        String sign = MD5Utils.getMD5(InfoHelper.getConfig("直播", "API鉴权key") + t);
        String url = new StringBuilder(InfoHelper.getConfig("直播", "查询录制文件地址"))
                .append("?cmd=").append(InfoHelper.getConfig("直播", "appid"))
                .append("&interface=Live_Tape_GetFilelist")
                .append("&Param.s.channel_id=").append(streamId)
                .append("&Param.s.stream_id=").append(streamId)
                .append("&stream_id=").append(streamId)
                .append("&Param.n.page_no=").append(1)
                .append("&Param.n.page_size=").append(100)
                .append("&t=").append(t)
                .append("&sign=").append(sign).toString();

        return HttpClientUtil.doGet(url);
    }

    public static boolean isJson(String json) {

        try {
            JSONObject.fromObject(json);
            return true;
        } catch (Exception e) {
            System.out.println("bad json: " + json);
            return false;
        }
    }

    @RequestMapping(value = "LiveItem.do")
    public ModelAndView liveItem(
            HttpServletRequest request, @RequestParam("DocLibID") int docLibID, String UUID,
            long groupID, Map<String, Object> model) {
        try {
            SysUser user = ProcHelper.getUser(request);
            model.put("docLibID", docLibID);
            model.put("groupID", groupID);
            model.put("UUID", UUID);
            model.put("userName", user.getUserName());
            prepareForm(request, model, "formLiveItem", docLibID, groupID);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("/xy/nis/LiveItem", model);
    }

    @RequestMapping(value = "LiveItemSubmit.do")
    public String LiveItemSubmit(HttpServletRequest request, String UUID, long groupID, int siteID) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc;
        long docID = 0;
        try {
            boolean isUpdate = WebUtil.getBoolParam(request, "isUpdate");
            if(isUpdate){
                //获得liveItem对象
                int docLibID = WebUtil.getInt(request, "DocLibID", 0);
                docID = WebUtil.getLong(request, "DocID", 0);
                doc = getLiveItemModel(docID, docLibID);
                ProcHelper.initDoc(doc, request);
                SysUser user = ProcHelper.getUser(request);
                doc.set("SYS_AUTHORID", user.getUserID());
                doc.set("a_rootID", groupID);
                String a_attachments = WebUtil.get(request, "a_attachments");
                //判断附件是否改变了
                boolean isChanged = liveItemNisAttachmentsChanged(doc, a_attachments);
                //保存live对象
                FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
                saver.handle(doc, request);
                if(isChanged){//如果附件改变了,更新附件
                    doc.set("a_siteID", siteID);
                    saveLiveItemAttachments(doc.getDocID(), doc.getDocLibID(), doc, true);
                }
            }else{

                docID = InfoHelper.getNextDocID(DocTypes.LIVE.typeID());
                doc = docManager.newDocument(LibHelper.getLiveItem(), docID);
                ProcHelper.initDoc(doc, request);
                SysUser user = ProcHelper.getUser(request);
                doc.set("SYS_AUTHORID", user.getUserID());
                doc.set("a_rootID", groupID);
                FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
                saver.handle(doc, request);

                doc.set("a_siteID", siteID);

                //保存附件
                saveLiveItemAttachments(doc.getDocID(), doc.getDocLibID(), doc, false);
            }
            //清楚redis
            cleanLiveItemRedis(groupID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //若是从主界面点击的操作
        return UUID != null ? "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docID : "";
    }

    private void cleanLiveItemRedis(long groupID) {
        RedisManager.clearKeyPages(APP_LIVE_SUBLIST_KEY + groupID);
        RedisManager.clearKeyPages(APP_LIVELIST_KEY + groupID);
        /*if (RedisManager.exists(APP_LIVE_SUBLIST_KEY + groupID))
            RedisManager.del(APP_LIVE_SUBLIST_KEY + groupID);

		if (RedisManager.exists(APP_LIVELIST_KEY + groupID))
			RedisManager.del(APP_LIVELIST_KEY + groupID);*/
    }

    private void saveLiveItemAttachments(long DocID, int DocLibID, Document doc ,boolean b) throws E5Exception {
        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
        if (b) {//附件更新，先删除历史附件
            //先删附件
            deleteAttachments(attLibID, DocLibID, DocID);
        }
        //保存附件
        JSONObject atts = JsonHelper.getJson(doc.getString("a_attachments"));
        if (atts != null) {
            saveAttachments(atts, "pics", attLibID, DocLibID, DocID, 1);
            saveAttachments(atts, "videos", attLibID, DocLibID, DocID, 2);
        }

        //发布图片
        forumManager.transWhenPass(doc);
    }

    @RequestMapping(value = "LiveItemDelete.do")
    public String LiveItemDelete(String UUID, int DocLibID, long DocIDs, long groupID) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            //删除话题
            docManager.delete(DocLibID, DocIDs);
            //删除附件
            int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
            deleteAttachments(attLibID, DocLibID, DocIDs);
            //清除缓存
            cleanLiveItemRedis(groupID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return UUID != null ? "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs : "";
    }

    @RequestMapping("saveReplayUrl.do")
    public void saveReplayUrl(HttpServletResponse response, int docLibID, long docID, String config) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            Document doc = docManager.get(docLibID, docID);
            doc.set("a_config", config);
            docManager.save(doc);
        } catch (Exception e) {
            json.put("code", 1);
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);

    }

    /**
     * 1. 删除话题
     * 2. 删除话题附件
     * 3. 删除话题报道
     * 4. 删除报道附件
     * 5. 清除缓存
     *
     * @param response
     * @param DocLibID
     * @param DocIDs
     */
    @RequestMapping("LiveDelete.do")
    public void LiveDelete(
            HttpServletRequest request, HttpServletResponse response, int DocLibID, String DocIDs, String UUID) {
        long[] docIDs = StringUtils.getLongArray(DocIDs);

        JSONObject json = new JSONObject();
        json.put("code", 0);

        int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
        try {
            for (Long docId : docIDs) {
                //删除话题
                deleteLiveDoc(DocLibID, DocIDs, docId, attLibID);
            }
        } catch (Exception e) {
            json.put("code", 1);
            json.put("error", "操作中出现错误：" + e.getLocalizedMessage());
            e.printStackTrace();
        }

        for (long docID : docIDs) {
            LogHelper.writeLog(DocLibID, docID, request, "删除到垃圾箱，保留日志");
        }
        InfoHelper.outputText("@refresh@" + (json.containsKey("error") ? json.getString("error") : ""), response);

    }

    private void deleteLiveDoc(
            int DocLibID, String DocIDs, long docId, int attLibID) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(DocLibID, docId);
        int siteID = doc.getInt("a_siteID");

        Document[] liveItems = docManager.find(LibHelper.getLiveItem(), "a_rootID=?",
                                               new Object[]{DocIDs});
        List<Long> liveItemIds = new LinkedList<>();
        for (Document item : liveItems) {
            liveItemIds.add(item.getDocID());
            //3.
            docManager.delete(item);
        }

        //4.
        deleteAttachments(attLibID, LibHelper.getLiveItem(), liveItemIds);

        //2.
        deleteAttachments(attLibID, DocLibID, docId);
        //1.
        docManager.delete(doc);

        cleanRelatedRedisKey(siteID, false, docId, true);
    }


    /**
     * 删除附件
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void deleteAttachments(int attLibID, int docLibID, List<Long> docIDs) throws E5Exception {
        if (docIDs.isEmpty()) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        List condition = new LinkedList();
        condition.add(docLibID);
        for (Long id : docIDs) {
            sb.append(",?");
            condition.add(id);
        }

        String tableName = LibHelper.getLibTable(attLibID);
        String sql = "delete from " + tableName + " where att_articleLibID=? and att_articleID in (" +
                sb.substring(1).toString() +
                ")";

        InfoHelper.executeUpdate(attLibID, sql, condition.toArray());
    }

    public TreeMap<String, Object> getConfig() {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        config.put("SecretId", "AKIDKXZP3VDqiOsBLKwkJgrqeUAey6ETmz99");
        config.put("SecretKey", "1rT8eVAhMxSozzoSuAF7nHahbMv01vkO");
        config.put("RequestMethod", "GET");
        config.put("SignatureMethod", "HmacSHA256");
        return config;
    }

    @RequestMapping("cutStream.do")
    public void cutStream(HttpServletResponse response, int DocLibID, long DocIDs,String streamId, int status){
        JSONObject json = new JSONObject();
        String t = (System.currentTimeMillis() / 1000 + 60 * 60 * 10) + "";
        String sign = MD5Utils.getMD5(InfoHelper.getConfig("直播", "API鉴权key") + t);
        String fileListUrl = new StringBuilder("http://fcgi.video.qcloud.com/common_access")
                .append("?cmd=").append(InfoHelper.getConfig("直播", "appid"))
                .append("&interface=Live_Channel_SetStatus")
                .append("&Param.s.channel_id=").append(streamId)
                .append("&Param.s.stream_id=").append(streamId)
                .append("&Param.n.status=").append(status)
                .append("&t=").append(t)
                .append("&sign=").append(sign)
                .toString();

        try {
            String result = HttpClientUtil.doGet(fileListUrl);
            if (isJson(result)) {
                JSONObject _resultJson = JSONObject.fromObject(result);
                if (_resultJson.containsKey("ret") && _resultJson.getInt("ret") ==0) {
                    json.put("code", 0);
                    json.put("msg", "success");
                    DocumentManager docManager = DocumentManagerFactory.getInstance();
                    Document document = docManager.get(DocLibID, DocIDs);
                    String config = document.getString("a_config");
                    JSONObject configJson = JSONObject.fromObject(config);
                    JSONArray videos = configJson.getJSONArray("videos");
                    for(int i = 0, len = videos.size() ; i < len ; i++) {
                        JSONObject video = videos.getJSONObject(i);
                        if (streamId.equals(video.getString("streamID"))) {
                            video.put("channelStatus", status);
                        }
                    }

                    document.set("a_config", configJson.toString());
                    docManager.save(document);
                }else{
                    json.put("code", 2);
                    json.put("error", "fail");
                    json.put("json", result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", "1");
            json.put("error", e.getLocalizedMessage());
        }

        InfoHelper.outputText(json.toString(), response);
    }

    public static void main(String[] args) throws InterruptedException {
        /*String t = (System.currentTimeMillis() / 1000 + 60 * 60 * 10) + "";
        String sign = MD5Utils.getMD5("a59451ee0ead5c1824487bf7306e94cb" + t);

//        String secretId = "AKIDKXZP3VDqiOsBLKwkJgrqeUAey6ETmz99";
//        String secretKey = "1rT8eVAhMxSozzoSuAF7nHahbMv01vkO";

        String fileListUrl = new StringBuilder("http://fcgi.video.qcloud.com/common_access")
                .append("?cmd=").append(1253712496)
                .append("&interface=Live_Channel_GetStatus")
                .append("&Param.s.channel_id=").append("9390_171_1495707889160")
                .append("&t=").append(t)
                .append("&sign=").append(sign)
                .toString();

        try {
            System.out.println(HttpClientUtil.doGet(fileListUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //String sign = MD5Utils.getMD5("a59451ee0ead5c1824487bf7306e94cb" + "1495867434");

    }

    /**
     * 图文直播详情下报道修改功能
     * @param request
     * @return
     */
    @RequestMapping(value = "LiveItemUpdate.do")
    public ModelAndView liveItemUpdate(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();

        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        long groupID = WebUtil.getLong(request, "groupID", 0);
        String UUID = WebUtil.get(request,"UUID");
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        try {
            SysUser user = ProcHelper.getUser(request);
            model.put("docLibID", docLibID);
            model.put("groupID", groupID);
            model.put("UUID", UUID);
            model.put("userName", user.getUserName());
            model.put("isUpdate", true);

            prepareForm(request, model, "formLiveItem", docLibID, docID);

            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            model.put("title",doc.getString("a_title"));
            model.put("author",doc.getString("SYS_AUTHORS"));
            model.put("content",doc.getString("A_CONTENT"));
            model.put("sourceType",doc.getInt("a_sourceType"));
            model.put("attachments", doc.getString("a_attachments").replace('\"','\''));

        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("/xy/nis/LiveItem", model);
    }

    private Document getLiveItemModel(long DocID, int DocLibID) throws Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(DocLibID, DocID);

        return doc;
    }


    /**
     * 判断liveItem的图片和视频附件是否改变
     * @param doc
     * @param a_attachments
     * @return
     */
    private boolean liveItemNisAttachmentsChanged(Document doc, String a_attachments) {
        String db_attachment = doc.getString("a_attachments");//数据库中的附件
        if (StringUtils.isBlank(db_attachment) || StringUtils.isBlank(a_attachments)) {
            return true;
        }
        JSONObject db_json = JSONObject.fromObject(db_attachment);
        JSONObject json = JSONObject.fromObject(a_attachments);
        if (db_json == null || json == null) {
            return true;
        }
        JSONArray db_pics = JsonHelper.getJsonArray(db_json.getString("pics"));
        JSONArray pics = JsonHelper.getJsonArray(json.getString("pics"));
        if (db_pics == null || pics == null) {
            return true;
        }

        if (!pics.equals(db_pics)) {
            return true;
        }

        JSONArray db_videos = JsonHelper.getJsonArray(db_json.getString("videos"));
        JSONArray videos = JsonHelper.getJsonArray(json.getString("videos"));

        if (db_videos == null || videos == null) {
            return true;
        }

        if (!videos.equals(db_videos)) {
            return true;
        }


        return false;
    }

    /**
     * 取视频
     */
    @RequestMapping(value = "Videos.do")
    public void getVideos(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
            throws Exception {

        int attLibID = LibHelper.getLiveItem();
        String sql = "SYS_DOCUMENTID=?";

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document[] atts = docManager.find(attLibID, sql, new Object[]{docID});

        String result = "[";
        if (atts != null) {
            for (int j = 0; j < atts.length; j++) {
                if (j > 0) result += ",";

                String db_attachment = atts[j].getString("a_attachments");//数据库中的附件
                JSONObject db_json = JSONObject.fromObject(db_attachment);
                JSONArray videosArray = JsonHelper.getJsonArray(db_json.getString("videos"));
                if(videosArray==null||videosArray.size()==0){
                    continue;
                }
                JSONObject video = videosArray.getJSONObject(0);

                String videoID = video.getString("videoID");
                String url = video.getString("url");
                String urlapp = video.getString("urlApp");

                result += "{\"url\":\"" + url
                        + "\",\"urlApp\":\"" + urlapp
                        + "\",\"videoID\":\"" + videoID
                        + "\"}";
            }
        }
        result += "]";
        InfoHelper.outputJson(result, response);
    }
    
    @RequestMapping(value = "LiveItemTop.do")
    public String LiveItemTop(String UUID, int DocLibID, long DocIDs, long groupID) {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        try {
            //查询是否有置顶
        	Document[] docs = docManager.find(DocLibID, "a_rootID=? and SYS_DELETEFLAG=0 and a_isTop=1", new Object[]{groupID});
            if(docs != null && docs.length > 0){
            	docs[0].set("a_isTop", 0);
            	docManager.save(docs[0]);
            }
        	
            Document doc = docManager.get(DocLibID, DocIDs);
            doc.set("a_isTop", 1);
            docManager.save(doc);
            
            //清除缓存
            cleanLiveItemRedis(groupID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return UUID != null ? "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs : "";
    }

}
