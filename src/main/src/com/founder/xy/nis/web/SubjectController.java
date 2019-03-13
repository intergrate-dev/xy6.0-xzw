package com.founder.xy.nis.web;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.web.SysUser;
import com.founder.xy.commons.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.nis.Activity;
import com.founder.xy.nis.DiscussManager;
import com.founder.xy.nis.ForumManager;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 互动话题
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/nis")
public class SubjectController {
	@Autowired
	private DiscussManager discussManager;
	
	@Autowired
    private ForumManager forumManager;
	
	/**
	 * 新建活动
	 */
	@RequestMapping(value = "Activity.do")
	public ModelAndView activity(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
	    long docID = WebUtil.getLong(request, "DocIDs", 0);
	    int docLibID = WebUtil.getInt(request, "DocLibID", 0);
	    boolean isNew = docID == 0;
	
	    Activity article;
	    if (isNew) {
	    	docID = InfoHelper.getNextDocID(DocTypes.ACTIVITY.typeID());
	    	
	        article = new Activity(docLibID, docID);
	        int siteID = WebUtil.getInt(request, "siteID", 1);
	        article.setSiteID(siteID);
	        article.setAuthor(ProcHelper.getUserName(request));
	    } else {
	        DocumentManager docManager = DocumentManagerFactory.getInstance();
	        Document doc = docManager.get(docLibID, docID);
	        article = new Activity(doc);
	    }
	    Map<String, Object> model = new HashMap<String, Object>();
		model.put("article", article);
		model.put("isNew", isNew);
		
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("sessionID", request.getSession().getId()); // 解决Firefox下flash上传控件session丢失
	
		return new ModelAndView("/xy/nis/Activity", model);
	}

	/**
	 * 新建/修改话题
	 */
	@RequestMapping(value = "Subject.do")
	public ModelAndView subject(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);

		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("isNew", docID == 0);
		if (docID == 0) {
			docID = InfoHelper.getNextDocID(docTypeID);
		}
		
		//使用定制的表单界面
		String formCode = getFormCode(docTypeID);
		prepareForm(request, model, formCode, docLibID, docID);
		
		return new ModelAndView("/xy/nis/Subject", model);
	}

	/**
	 * 互动话题的提交保存、活动的提交保存
	 */
	@RequestMapping(value = "SubjectSubmit.do")
	public String subjectSubmit(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		long docID = WebUtil.getLong(request, "DocID", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		boolean isNew = WebUtil.getBoolParam(request, "isNew");
		
	
		//保存
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		if (isNew) {
			doc = docManager.newDocument(docLibID, docID);
			ProcHelper.initDoc(doc, request);
			doc.set("a_order", docID);
		} else {
			doc = docManager.get(docLibID, docID);
			doc.set("a_answerTime", DateUtils.getTimestamp());
		}
		FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
		saver.handle(doc, request);
		
		boolean isActivity = doc.getDocTypeID() == DocTypes.ACTIVITY.typeID();
		//互动活动
		if (isActivity) {
			String endTime = request.getParameter("a_endTime");
			if (!StringUtils.isBlank(endTime)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				doc.set("a_endTime", new Timestamp(df.parse(endTime).getTime()));
				docManager.save(doc);
			}
		}
		//处理附件
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), docLibID);
		if (!isNew) { //先删附件
			deleteAttachments(attLibID, docLibID, docID);
		}
		JSONObject atts = JsonHelper.getJson(doc.getString("a_attachments"));
		if (atts != null) { //保存附件
			if (isActivity) { //互动活动
				saveAttachments(atts, "pics", attLibID, doc, 1);
				saveAttachments(atts, "videos", attLibID, doc, 2);
				//标题图，记录在附件表里，以便发布的时候记录外网地址
				savePicTitle(atts, attLibID, doc);
			}else{ //互动话题
				JSONArray pics = JsonHelper.getJsonArray(atts, "pics");
				saveAttachments(atts, "pics", attLibID, doc, 1);
				int result = forumManager.transWhenPass(doc);
				if (result == 0) {
					JSONObject attr = this.addUrlToJson(docID, docLibID, attLibID, pics) ;
					doc.set("a_attachments",attr) ; //附件key改变,话题列表图片取不出来
					docManager.save(doc);
				}
			}
		}
		if (!isActivity && doc.getInt("a_status") == 1) { //互动话题已发布，则修改后清理redis
       		RedisManager.clear(RedisKey.APP_SUBJECT_KEY + docID); //话题详情
			
       		int siteID = doc.getInt("a_siteID");
       		long catID =doc.getLong("a_group_ID");
    		long answererID =doc.getLong("a_answererID");
       		
			String key = RedisManager.getKeyBySite(RedisKey.APP_SUBJECT_LIST_KEY, siteID);
       		RedisManager.clearKeyPages(key); //话题列表
       		RedisManager.clearKeyPages(RedisKey.MY_SUBJECT_KEY+answererID); //题主的话题列表
       		if(catID > 0)
       			RedisManager.clearKeyPages(RedisKey.APP_SUBJECT_CAT_KEY+catID); //相关分类的话题列表
        }
		//返回
		String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
				+ "&DocIDs=" + docID;
		return url;
	}
	/**
	 * 互动问答
	 */
	@RequestMapping(value = "QAAnswer.do")
	public ModelAndView qaAnswer(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int type = WebUtil.getInt(request, "type", 2);//type:0新建，1修改，2回答
		
		if (type == 0) {
			docID = InfoHelper.getNextDocID(DocTypes.QA.typeID());
		}
	
		Map<String, Object> model = new HashMap<String, Object>();
		
		//使用定制的表单界面
		String formCode = "formQA2";
		prepareForm(request, model, formCode, docLibID, docID);
		model.put("type", type);
		model.put("siteID", WebUtil.get(request, "siteID"));
		
		return new ModelAndView("/xy/nis/QAAnswer", model);
	}

	/**
	 * 话题回答
	 */
	@RequestMapping(value = "AnswerSubmit.do")
	public String answerSubmit(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		long docID = WebUtil.getLong(request, "DocID", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int type = WebUtil.getInt(request, "type", 2);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		if (type == 0) {
			doc = docManager.newDocument(docLibID, docID);
			ProcHelper.initDoc(doc, request);
			doc.set("a_siteID", WebUtil.getInt(request, "siteID", 0));
			doc.set("a_order", docID);
		} else {
			doc = docManager.get(docLibID, docID);
			//type:0新建，1修改，2回答
			if (type == 2) {
				doc.set("a_answerTime", DateUtils.getTimestamp());
			}
		}

		int oldCatID = doc.getInt("a_group_ID");
		FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
		saver.handle(doc, request);
		
		//处理问政附件,存入互动附件表
		if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.QA.typeID()){
			int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), docLibID);
			deleteAttachments(attLibID, docLibID, docID);
			JSONObject atts = JsonHelper.getJson(doc.getString("a_attachments"));
			if(atts != null){
				saveAttachments(atts, "pics", attLibID, doc, 1);
				forumManager.transWhenPass(doc);
			}
		}
		
		if(type == 2) {
			if (DomHelper.getDocTypeIDByLibID(docLibID) == DocTypes.QA.typeID()){
				//若是互动问答，增加后续处理
				afterQAAnswer(oldCatID, doc);
			} else {
				afterSubjectAnswer(doc);
			}
		}
		//返回
		String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
				+ "&DocIDs=" + docID;
		return url;
    }

	/**
	 * 会员选择
	 */
	@RequestMapping(value = "findMember.do")
	public void findMember(HttpServletRequest request, HttpServletResponse response)throws E5Exception{
		int docLibID = WebUtil.getInt(request, "docLibID", 0) ;
		String DocIDs = WebUtil.get(request,"docIDs") ;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		long[] docIDs = StringUtils.getLongArray(DocIDs);
		JSONArray arr = new JSONArray();
		if(docIDs != null){
			for(long docID : docIDs){
				Document doc = docManager.get(docLibID, docID);
				JSONObject obj = new JSONObject();
				obj.put("mID", doc.getDocID()) ;
				obj.put("mNickname", doc.get("mNickname")) ;
				obj.put("mHead", doc.get("mHead")) ;
				arr.add(obj) ;
			}
		}
	    InfoHelper.outputJson(arr.toString(), response);
	}

	/**
	 * 问答稿件，选择话题后，返回名称
	 */
	@RequestMapping(value = "/findSubject.do", method = RequestMethod.GET)
	public void findSubject(HttpServletRequest request, HttpServletResponse response,
			int docLibID, long docID) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);

		InfoHelper.outputText(doc.getTopic(), response);
	}

	@RequestMapping(value = "findQAInfo.do")
	public String findQAInfo(HttpServletRequest request, HttpServletResponse response, long id, Model model) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
	
		try {
			Document doc = docManager.get(LibHelper.getNisQaID(), id);
			model.addAttribute("SYS_TOPIC", doc.getString("SYS_TOPIC"));
			model.addAttribute("a_realName", doc.getString("a_realName"));
			model.addAttribute("a_phone", doc.getString("a_phone"));
			model.addAttribute("a_region", doc.getString("a_region"));
			model.addAttribute("a_askTo", doc.getString("a_askTo"));
			model.addAttribute("SYS_CREATED", InfoHelper.formatDate(doc.getCreated()));
			model.addAttribute("a_content", doc.getString("a_content"));
			String attachment = doc.getString("a_attachments");
			JSONArray imgs = new JSONArray();
			if (attachment != null && !"".equals(attachment)) {
				JSONObject jsonObject = JSONObject.fromObject(attachment);
				if (jsonObject.has("pics")) {
					String pics = jsonObject.getString("pics");
					if (pics != null && !"".equals(pics)) {
						JSONArray array = JSONArray.fromObject(pics);
						for (int i = 0; i < array.size(); i++) {
							//http://10.21.13.11:8080/
							imgs.add("../xy/image.do?path=" + array.getString(i));
						}
					}
				}
			}
			model.addAttribute("pics", imgs.toArray());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return "/xy/nis/qaPage";
	}

	@RequestMapping(value = "findActivityInfo.do")
	public String findActivityInfo(HttpServletRequest request, HttpServletResponse response, long id, Model model) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
	
		try {
			Document doc = docManager.get(LibHelper.getNisActivityID(), id);
			model.addAttribute("SYS_TOPIC", doc.getString("SYS_TOPIC"));
			String sTime = doc.getString("a_startTime");
			sTime = StringUtils.isBlank(sTime) ? "" : sTime.substring(0, 10);
			model.addAttribute("a_startTime", sTime);
			sTime = doc.getString("a_endTime");
			sTime = StringUtils.isBlank(sTime) ? "" : sTime.substring(0, 10);
			model.addAttribute("a_endTime", sTime);
			model.addAttribute("a_location", doc.getString("a_location"));
			model.addAttribute("a_count", doc.getString("a_count"));
			model.addAttribute("a_countLimited", doc.getString("a_countLimited"));
			model.addAttribute("SYS_AUTHORS", doc.getString("SYS_AUTHORS"));
			sTime = DateUtils.format(doc.getTimestamp("SYS_LASTMODIFIED"), "yyyy-MM-dd HH:mm:ss");
			model.addAttribute("SYS_LASTMODIFIED", sTime);
	
			int type = doc.getInt("a_type");
			String typeStr = type == 0 ? "普通报名" : "评论报名";
			model.addAttribute("a_type", typeStr);
			model.addAttribute("a_rule", doc.getString("a_rule"));
			model.addAttribute("a_content", doc.getString("a_content"));
			model.addAttribute("a_otherInfo", doc.getString("a_otherInfo"));
			String picSmall = doc.getString("a_picSmall");
			model.addAttribute("picSmall", "../image.do?path=" + picSmall);
	
			/*String attachment = doc.getString("a_attachments");
			JSONArray imgs = new JSONArray();
			if (attachment != null && !"".equals(attachment)) {
				JSONObject jsonObject = JSONObject.fromObject(attachment);
				if(jsonObject.has("pics")){
					String pics = jsonObject.getString("pics");
					if (pics != null && !"".equals(pics)) {
						JSONArray array = JSONArray.fromObject(pics);
						for (int i = 0 ; i < array.size(); i ++ ) {
							//http://10.21.13.11:8080/
							imgs.add("../xy/image.do?path=" + array.getString(i));
						}
					}
				}
			}
			model.addAttribute("pics", imgs.toArray());*/
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return "/xy/nis/activityPage";
	}

	@RequestMapping(value = "memInfo.do")
	public ModelAndView memInfo(HttpServletRequest request) throws Exception {
	
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
	
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		String result = null;
	
		try {
			doc = docManager.get(docLibID, docID);
			long userId = doc.getLong("SYS_AUTHORID");
			result = discussManager.memberInfo(userId);
	
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	
		Map<String, Object> model = new HashMap<String, Object>();
		if (null != result) {
			JSONObject jsonObj = JSONObject.fromObject(result);
			if (jsonObj.getString("msg").equals("登录成功")) {
				JSONObject jsonObject = (JSONObject) jsonObj.get("member");
				model.put("region", jsonObject.getString("region"));
				model.put("sex", jsonObject.getString("sex"));
				model.put("birthday", jsonObject.getString("birthday"));
				model.put("address", jsonObject.getString("address"));
				model.put("email", jsonObject.getString("email"));
				model.put("nickname", jsonObject.getString("nickname"));
				model.put("name", jsonObject.getString("name"));
				model.put("mobile", jsonObject.getString("mobile"));
				model.put("org", jsonObject.getString("org"));
				model.put("head", jsonObject.getString("head"));
			}
		}
		model.put("UUID", WebUtil.get(request, "UUID"));
		return new ModelAndView("/xy/nis/userMessage", model);
	}

	/**
	 * 标记切换（评论、订阅：用于活动报名（评论形式、报名形式）
	 * 目前在 评论审核 与 报名列表中都有
	 * 1. 报名列表中的切换标记： a. 判断是否是评论报名，如果是，需要同时修改评论表里的状态
	 * 2. 评论列表中的切换标记： a. 判断是否是活动报名，如果是，需要判断是否是评论报名，如果是修改报名表里的状态
	 */
	@RequestMapping(value = "Mark.do")
	public void mark(HttpServletRequest request, HttpServletResponse response) {
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			doc = docManager.get(docLibID, docID);
			int isMark = doc.getInt("entry_isMark") ;
			if(isMark == 0){
				doc.set("entry_isMark", 1);
			}else{
				doc.set("entry_isMark", 0);
			}
			docManager.save(doc);
			
			InfoHelper.outputText("@refresh@操作成功", response); // 操作成功
		} catch (E5Exception e) {
			e.printStackTrace();
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}
	
	@RequestMapping(value = "EntryStatus.do")
	public void EntryStatus(HttpServletRequest request,HttpServletResponse response){
		int siteID = WebUtil.getInt(request, "siteID",1) ;
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			doc = docManager.get(docLibID, docID);
			doc.set("a_statusEntry",1);
			docManager.save(doc);
			
			int entryLibId = DomHelper.getDocLibID(DocTypes.ENTRY.typeID());
			Document[] entrys = docManager.find(entryLibId,"entry_targetID=?",new Object[]{docID});
			for(int i = 0 ; i < entrys.length ; i++){
				long entryUserID = entrys[i].getLong("entry_userID") ;
				
				RedisManager.sadd(RedisKey.RED_DOT_ACTIVITY, entryUserID); 
				RedisManager.clearKeyPages(RedisKey.MY_ENTRY_KEY + entryUserID) ; //我报名的活动列表
			}
			
			RedisManager.clearLongKeys(RedisManager.getKeyBySite(RedisKey.APP_ACTIVITY_KEY, siteID)); //活动列表
			RedisManager.clear(RedisKey.APP_ACTIVITY_DETAIL_KEY + docID); //活动详情
			RedisManager.clear(RedisKey.APP_ACTIVITY_ENTRY + docID);
			
			InfoHelper.outputText("@refresh@操作成功", response); // 操作成功
		} catch (E5Exception e) {
			e.printStackTrace();
			
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}
	
	private JSONObject addUrlToJson(long docId, int docLibId,int attLibID,JSONArray pics) {
		JSONObject json = new JSONObject() ;
		String appBanner = "" ;
		String appBannerUrl = "" ;
		if(!pics.isEmpty()){
			appBanner = pics.getString(0) ;
			try {
	            DocumentManager docManager = DocumentManagerFactory.getInstance();
	            Document[] attDocs = docManager.find(attLibID, "att_articleID=? and att_articleLibID=? and att_type=?",new Object[]{docId, docLibId, 1});
	            String url;
	            for (Document doc : attDocs) {
	                url = doc.getString("att_path");
	                if (!StringUtils.isBlank(url) && url.equals(appBanner)) {
	                	appBannerUrl = doc.getString("att_url") ;
	                }
	            }
	        } catch (E5Exception e) {
	            e.printStackTrace();
	        }
		}
		json.put("appBanner", appBanner);
		json.put("appBannerUrl", appBannerUrl);
		return json;
	}

	private String getFormCode(int docTypeID) {
		if (docTypeID == DocTypes.SUBJECT.typeID())
			return "formSubject";
		else if (docTypeID == DocTypes.QA.typeID())
			return "formQA";
		else if (docTypeID == DocTypes.ACTIVITY.typeID())
			return "formActivity";

		return null;
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
			JSONObject atts, String tag, int attLibID, Document doc, int type) throws E5Exception {
		JSONArray pics = JsonHelper.getJsonArray(atts, tag);
		if (pics.isEmpty()) return;

		int attTypeID = DocTypes.NISATTACHMENT.typeID();
		long idStart = EUID.getID("DocID" + attTypeID, pics.size());

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (int i = 0; i < pics.size(); i++) {
			Document attach = docManager.newDocument(attLibID, idStart++);
			attach.set("att_articleID", doc.getDocID());
			attach.set("att_articleLibID", doc.getDocLibID());
			attach.set("att_type", type); //0:文件，1:图片;2:视频;3:标题图(大);4:标题图(中);5:标题图(小)
			if (type == 2) {
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
			} else {
				String url = pics.getString(i);
				attach.set("att_path", url);
			}

			docManager.save(attach);
		}
	}

	//保存标题图
	private void savePicTitle(JSONObject atts, int attLibID, Document doc) throws E5Exception {
		int attTypeID = DocTypes.NISATTACHMENT.typeID();
		long idStart = EUID.getID("DocID" + attTypeID, 3);

		String picPath = JsonHelper.getString(atts, "picBig");
		if (picPath != null) {
			saveOneAtt(doc, attLibID, idStart++, 3, picPath);
		}
		picPath = JsonHelper.getString(atts, "picMiddle");
		if (picPath != null) {
			saveOneAtt(doc, attLibID, idStart++, 4, picPath);
		}
		picPath = JsonHelper.getString(atts, "picSmall");
		if (picPath != null) {
			saveOneAtt(doc, attLibID, idStart++, 5, picPath);
		}
	}

	private void saveOneAtt(Document doc, int attLibID, long attID, int type,
			String url) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		Document attach = docManager.newDocument(attLibID, attID);
		attach.set("att_articleID", doc.getDocID());
		attach.set("att_articleLibID", doc.getDocLibID());
		attach.set("att_type", type); //0:文件，1:图片;2:视频;3:标题图(大);4:标题图(中);5:标题图(小)
		if (type == 2)
			attach.set("att_url", url); //视频写url
		else
			attach.set("att_path", url);//图片写path，发布后才有url

		docManager.save(attach);
	}

	/**
	 * 互动话题回答后：
	 * 1)清理Redis里的话题详情（回答数有变化）<br/>
	 * 2)清理Redis里的话题回答列表（多页）<br/>
	 * 3)把被回复的用户ID记录到集合RED_DOT_SUBJECTQA中<br/>
	 * 4)按用户ID清理“我的问吧提问”（多页） <br/>
	 */
	private void afterSubjectAnswer(Document doc) {
		long docID = doc.getDocID();
		
		RedisManager.clear(RedisKey.APP_SUBJECT_KEY + docID); //话题详情
		RedisManager.clearKeyPages(RedisKey.APP_SUBJECT_QALIST_KEY + docID); //话题回答列表
		RedisManager.clear(RedisKey.APP_QA_KEY + doc.getDocID());
		
		int userID = doc.getInt("SYS_AUTHORID");
		if (userID > 0) {
			RedisManager.sadd(RedisKey.RED_DOT_SUBJECTQA, userID);
			
			RedisManager.clearKeyPages(RedisKey.MY_SUBJICTQA_KEY + userID);
		}
	}

	/**
	 * 清理问答列表、旧分类的问答列表、新分类的问答列表、问答详情；
	 * 把被回复的用户ID记录到Redis的小红点集合中；
	 * 按用户ID清理Redis中“我的提问”列表。
	 */
	private void afterQAAnswer(int oldCatID, Document doc) throws E5Exception {
		//清理问答列表、旧分类的问答列表、新分类的问答列表、问答详情
		int siteID = doc.getInt("a_siteID");
		String key = RedisManager.getKeyBySite(RedisKey.APP_QALIST_KEY, siteID);
		
		RedisManager.clearLongKeys(key + "-1");
		if (oldCatID > 0) {
			RedisManager.clearLongKeys(key + oldCatID);
		}
		RedisManager.clearLongKeys(key + doc.getInt("a_group_ID"));
		
		RedisManager.clear(RedisKey.APP_QA_KEY + doc.getDocID());
		
		//小红点、我的提问
		int userID = doc.getInt("SYS_AUTHORID");
		if (userID > 0) {
			RedisManager.sadd(RedisKey.RED_DOT_QA, userID);
			RedisManager.clearKeyPages(RedisKey.MY_QA_KEY + userID);
		}
	}

	/**
	 * 爆料回答
	 */
	@RequestMapping(value = "tipoffAnswerSubmit.do")
	public String feedAnswerSubmit(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//long docID = WebUtil.getLong(request, "DocID", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteID=WebUtil.getInt(request,"a_siteID",1 );
		String answer=WebUtil.get(request,"a_answers","");
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		System.out.println(docIDs);
		//int userID= WebUtil.getInt(request, "SYS_AUTHORID", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (long docID : docIDs) {
			Document doc= docManager.get(docLibID, docID);
			FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
			if(!answer.equals("")&&answer!=null){
				doc.set("a_isAnswer",1);
			}else{
				doc.set("a_isAnswer",0);
			}
			saver.handle(doc, request);
			//清理redis缓存
			int userID=doc.getInt("SYS_AUTHORID");
			clearRedis(docID,userID,siteID);
		}
		//返回
		String url = "redirect:/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
				+ "&DocIDs=" + request.getParameter("DocIDs");
		return url;
	}
	/**爆料采用*/
	@RequestMapping(value = {"tipoffAdopt.do"})
	public String tipoffAdopt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String message = Adopt(docLibID, docIDs, true);

		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		JSONObject jo = new JSONObject();
		if (message == null){
			url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功

		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}
	/**爆料不采用*/
	@RequestMapping(value = {"tipoffNoAdopt.do"})
	public String tipoffNoAdopt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);

		String message = noAdopt(docLibID, docIDs, true);

		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");

		if (message == null){
				url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功
		} else {
				url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示

		}
		return "redirect:" + url;
	}
	/**爆料删除*/
	@RequestMapping(value = "tipoffDelete.do")
	public void tipoffDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);

		//删除评论
		String error = delete(docLibID, docIDs);
		if(error == null){
			InfoHelper.outputText("@refresh@", response);	//操作成功
		} else {
			InfoHelper.outputText("@refresh@"+error, response);	//操作成功
		}
	}

	/**
	 * 互动爆料新建
	 */
	@RequestMapping(value = "TipoffAdd.do")
	public ModelAndView qaAdd(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = InfoHelper.getNextDocID(DocTypes.QA.typeID());

		Map<String, Object> model = new HashMap<String, Object>();

		//使用定制的表单界面
		String formCode = "formTipoff";
		prepareForm(request, model, formCode, docLibID, docID);
		model.put("siteID", WebUtil.get(request, "siteID"));
		model.put("isUpdate", false);
		return new ModelAndView("/xy/nis/TipoffAdd", model);
	}

	/**
	 * 爆料修改功能
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "TipoffUpdate.do")
	public ModelAndView TipoffUpdate(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String UUID = WebUtil.get(request,"UUID");
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		try {
			SysUser user = ProcHelper.getUser(request);
			model.put("docLibID", docLibID);
			model.put("UUID", UUID);
			model.put("userName", user.getUserName());
			model.put("isUpdate", true);

			prepareForm(request, model, "formTipoff", docLibID, docID);

			String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
			model.put("videoPlugin", _videoPluginUrl);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			model.put("SYS_AUTHORS",doc.getString("SYS_AUTHORS"));
			model.put("a_content",doc.getString("a_content"));
			//model.put("a_sourceType",doc.getInt("a_sourceType"));
			model.put("a_contactNo",doc.getInt("a_contactNo"));
			model.put("a_location",doc.getInt("a_location"));
			model.put("SYS_TOPIC",doc.getInt("SYS_TOPIC"));
			model.put("a_sourceType",doc.getInt("a_sourceType"));
			model.put("attachments", doc.getString("a_attachments").replace('\"','\''));

		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/xy/nis/TipoffAdd", model);
	}

	/**
	 * 爆料详情功能
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "TipoffEdtia.do")
	public ModelAndView TipoffEdtia(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String UUID = WebUtil.get(request,"UUID");
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int FVID=WebUtil.getInt(request, "FVID", 0);
		try {
			SysUser user = ProcHelper.getUser(request);
			model.put("docLibID", docLibID);
			model.put("UUID", UUID);
			model.put("userName", user.getUserName());
			model.put("isUpdate", true);
            model.put("docID",docID);
			model.put("FVID",FVID);

			prepareForm(request, model, "formTipoff", docLibID, docID);

			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			model.put("SYS_AUTHORS",doc.getString("SYS_AUTHORS"));
			model.put("a_content",doc.getString("a_content"));
			model.put("a_sourceType",doc.getInt("a_sourceType"));
			model.put("a_contactNo",doc.getString("a_contactNo"));
			model.put("a_location",doc.getString("a_location"));
			model.put("a_status",doc.getInt("a_status"));
			model.put("a_answers",doc.getString("a_answers"));
			model.put("SYS_TOPIC",doc.getString("SYS_TOPIC"));
			model.put("attachments", doc.getString("a_attachments").replace('\"','\''));

		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/xy/nis/TipoffEdtia", model);
	}

	/**
	 * 提交互动爆料表单
	 * @param request
	 * @param UUID
	 * @param siteID
	 * @return
	 */
	@RequestMapping(value = "TipoffSubmit.do")
	public String TipoffSubmit(HttpServletRequest request, String UUID, int siteID) {
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
				String a_attachments = WebUtil.get(request, "a_attachments");
				//判断附件是否改变了
				boolean isChanged = liveItemNisAttachmentsChanged(doc, a_attachments);
				//保存live对象
				FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
				doc.set("a_siteID", siteID);
				saver.handle(doc, request);
				if(isChanged){//如果附件改变了,更新附件
					doc.set("a_siteID", siteID);
					doc.set("a_attachments",a_attachments);
					saveLiveItemAttachments(doc.getDocID(), doc.getDocLibID(), doc, true);
				}
			}else{

				docID = InfoHelper.getNextDocID(DocTypes.TIPOFF.typeID());
				doc = docManager.newDocument(LibHelper.getTipoff(), docID);
				ProcHelper.initDoc(doc, request);
				SysUser user = ProcHelper.getUser(request);
				doc.set("SYS_AUTHORID", user.getUserID());
				doc.set("a_siteID", siteID);
				doc.set("a_sourceType", 2);
				FormSaver saver = (FormSaver) Context.getBean(FormSaver.class);
				saver.handle(doc, request);
				//保存附件
				String a_attachments = WebUtil.get(request, "a_attachments");
				doc.set("a_attachments",a_attachments);
				saveLiveItemAttachments(doc.getDocID(), doc.getDocLibID(), doc, false);
			}
			//清楚redis
			//cleanLiveItemRedis(groupID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//若是从主界面点击的操作
		return UUID != null ? "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docID : "";
	}
	private Document getLiveItemModel(long DocID, int DocLibID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(DocLibID, DocID);

		return doc;
	}

	/**
	 * 取视频稿
	 */
	@RequestMapping(value = "TipoffVideos.do")
	public void getVideos(HttpServletRequest request, HttpServletResponse response,
						  @RequestParam("DocIDs") long docID, @RequestParam("DocLibID") int docLibID)
			throws Exception {

		int attLibID = LibHelper.getTipoff();
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

	private void saveLiveItemAttachments(long DocID, int DocLibID, Document doc ,boolean b) throws E5Exception {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.NISATTACHMENT.typeID(), DocLibID);
		if (b) {//附件更新，先删除历史附件
			//先删附件
			deleteAttachments(attLibID, DocLibID, DocID);
		}
		//保存附件
		JSONObject atts = JsonHelper.getJson(doc.getString("a_attachments"));
		if (atts != null) {
			saveAttachments(atts, "pics", attLibID, doc, 1);
			saveAttachments(atts, "videos", attLibID, doc, 2);
		}

		//发布图片
		forumManager.transWhenPass(doc);
	}
	public String Adopt(int docLibID, long[] docIDs, boolean changeStatus) throws E5Exception {
		//若需要手动改变流程节点（app端提交），则取“审批通过”节点
		FlowNode nextNode = null;
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		int docTypeID = DocTypes.TIPOFF.typeID();
		String status = null;
		int flowNodeID = 0;
		if (changeStatus) {
			//nextNode = getDisucssFlowNode(1);
			Flow[] flows = flowReader.getFlows(docTypeID);
			int flowID = 0;

			if (flows != null) {
				flowID = flows[0].getID();
				FlowNode[] nodes = flowReader.getFlowNodes(flowID);
				if (nodes != null) {
					flowNodeID = nodes[2].getID();
					status = nodes[2].getWaitingStatus();
				}
			}
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> tipoffs = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document tipoff = docManager.get(docLibID, docID);
			tipoff.set("a_status", 2);//设置状态为1，通过
			if (changeStatus) {
				tipoff.setCurrentNode(flowNodeID);
				tipoff.setCurrentStatus(status);
			}
			tipoffs.add(tipoff);
			int userID=tipoff.getInt("SYS_AUTHORID");
			int siteID=tipoff.getInt("a_siteID");
			clearRedis(docID,userID,siteID);
		}

		//同时提交多个稿件，使用事务
		String message = save(docLibID, tipoffs);

		return message;
	}

	//改变流程
	public String noAdopt(int docLibID, long[] docIDs, boolean changeStatus) throws E5Exception {
		//若需要手动改变流程节点（app端提交），则取“审批通过”节点
		FlowNode nextNode = null;
		FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
		int docTypeID = DocTypes.TIPOFF.typeID();
		String status = null;
		int flowNodeID = 0;
		if (changeStatus) {
			//nextNode = getDisucssFlowNode(1);
			Flow[] flows = flowReader.getFlows(docTypeID);
			int flowID = 0;

			if (flows != null) {
				flowID = flows[0].getID();
				FlowNode[] nodes = flowReader.getFlowNodes(flowID);
				if (nodes != null) {
					flowNodeID = nodes[1].getID();
					status = nodes[1].getWaitingStatus();
				}
			}
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Document> tipoffs = new ArrayList<Document>();
		for (long docID : docIDs) {
			Document tipoff = docManager.get(docLibID, docID);
			tipoff.set("a_status", 1);//设置状态为1，通过
			if (changeStatus) {
				tipoff.setCurrentNode(flowNodeID);
				tipoff.setCurrentStatus(status);
			}
			tipoffs.add(tipoff);
			int userID=tipoff.getInt("SYS_AUTHORID");
			int siteID=tipoff.getInt("a_siteID");
			clearRedis(docID,userID,siteID);
		}

		//同时提交多个稿件，使用事务
		String message = save(docLibID, tipoffs);

		return message;
	}

	/** 多个采用的统一提交， 出错时返回错误信息 */
	private String save(int docLibID, List<Document> tipoffs) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();
			for (Document tipoff : tipoffs) {
				docManager.save(tipoff, conn);
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

	/**爆料删除*/
	public String delete(int docLibID, long[] docIDs){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//创建评论删除列表
		List<Document> tipoffs = new ArrayList<Document>();
		try {
			for (long docID : docIDs) {
				Document tipoff = docManager.get(docLibID, docID);
				tipoffs.add(tipoff);
				int userID=tipoff.getInt("SYS_AUTHORID");
				int siteID=tipoff.getInt("a_siteID");
				clearRedis(docID,userID,siteID);
			}
		} catch (E5Exception e1) {
			System.out.println("爆料删除时异常：" + e1.getLocalizedMessage());
			e1.printStackTrace();
		}

		//同时修改多个稿件，使用事务
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(docLibID);;
			conn.beginTransaction();
			for (Document tipoff : tipoffs){
				docManager.delete(tipoff.getDocLibID(), tipoff.getDocID(), conn);
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
	//清除redis爆料的key
	public void clearRedis(long docID,int userID,int siteID){
		RedisManager.clear(RedisManager.getKeyBySite(RedisKey.MY_TIPOFF_KEY,siteID) + userID);
		RedisManager.clear(RedisKey.APP_TIPOFF_KEY+ docID);
	}

}
