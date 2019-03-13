package com.founder.xy.nis.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.nis.DiscussManager;

@Controller
@RequestMapping("/xy/nis")
public class DiscussController {
	@Autowired
	DiscussManager discussManager;
	
	/**评论审核通过*/
	@RequestMapping(value = {"DiscussPass.do"})
	public String discussPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		String message = discussManager.pass(docLibID, docIDs, false);

		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (message == null){
			url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}

	/**评论审核不通过*/
	@RequestMapping(value = {"DiscussReject.do"})
	public String discussReject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);

		String message = discussManager.reject(docLibID, docIDs, false);
		
		//调用after.do进行后处理：改变流程状态、解锁、刷新列表
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (message == null){
			url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}
	
	/**评论删除*/
	@RequestMapping(value = "DiscussDelete.do")
	public void discussDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		//删除评论
		String error = discussManager.delete(docLibID, docIDs);
		if(error == null){
			InfoHelper.outputText("@refresh@", response);	//操作成功
		} else {
			InfoHelper.outputText("@refresh@"+error, response);	//操作成功
		}
	}

    /**
	 * 禁言
	 */
	@RequestMapping(value={"Shutup.do"})
	public void shutUp(HttpServletRequest request,HttpServletResponse response) {
		int siteID = WebUtil.getInt(request, "siteID", 1);
		String userID = request.getParameter("userID");
		String userName = request.getParameter("userName");
		String UUID = WebUtil.get(request, "UUID");
		int type = WebUtil.getInt(request, "type", 0);

		String error = null;
		long[] userIDs = StringUtils.getLongArray(userID);
		String[] userNames = StringUtils.split(userName, ",");
		SysUser user = ProcHelper.getUser(request);

		int docLibID = LibHelper.getLibID(DocTypes.SHUTUP.typeID(),
				InfoHelper.getTenantCode(request));
		if(userIDs!=null){
			for (int j = 0; j < userIDs.length; j++) {
				error = discussManager.shutUp(docLibID, siteID, userIDs[j],
						userNames[j], type, user.getUserName());
			}
		}else{
			for (int j = 0; j < userNames.length; j++) {
				error = discussManager.shutUp(docLibID, siteID, 0,
						userNames[j], type, user.getUserName());
			}
		}
		if (UUID!=null||!"".equals(UUID)) {
			String url = "../../e5workspace/after.do?UUID="
					+ UUID + "&DocIDs=" + userID;
			try {
				response.sendRedirect(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (error == null) {
				InfoHelper.outputText("@refresh@", response);
			} else {
				InfoHelper.outputText(error, response);
			}
		}
	}

	/**
	 * 删除。“禁言用户管理”界面多选后操作。
	 */
	@RequestMapping(value="ShutCancel.do")
	public void shutCancel(HttpServletRequest request,HttpServletResponse response) {
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		
		String error = discussManager.shutCancel(docLibID, docIDs);
		
		if (error == null) {
			InfoHelper.outputText("@refresh@", response);
		} else {
			// 操作失败
			InfoHelper.outputText(error, response);
		}
	}

	/** 查看评论的对应稿件、对应直播话题 */
	@RequestMapping("ViewArticle.do")
	public String viewArticle(HttpServletRequest request, int type, Long id) throws Exception {
		String tenantCode = InfoHelper.getTenantCode(request);

		if (type == 0) {
			// 对应稿件
			
			//稿件不一定是Web稿还是App稿，依次查询
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			
			List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
			for (int i = 0; i < 2 && i < articleLibs.size(); i++) {
				int docLibID = articleLibs.get(i).getDocLibID();

				Document doc = docManager.get(docLibID, id);
				if (doc != null) {
					return "redirect:/xy/article/Preview.do?DocLibID=" + docLibID + "&DocIDs=" + id + "&ch=" + i;
				}
			}
		} else if (type == 1) {
			return "redirect:/xy/nis/livePage.jsp?id=" + id;// 对应直播
		} else if (type == 2) {
			return "redirect:/xy/nis/forumPage.jsp?id=" + id;
		} else if (type == 5) {
			return "redirect:/xy/nis/findQAInfo.do?id=" + id;
		} else if (type == 6) {
			return "redirect:/xy/nis/findActivityInfo.do?id=" + id;
		}
		return null;
	}

	//会员信息
	@RequestMapping(value = "memberInfo.do")
	public void memberInfo(HttpServletResponse response,long Uid)throws E5Exception{
		String result = discussManager.memberInfo(Uid);
		InfoHelper.outputJson(result, response);
	}
	
	/** 官方回复 */
	@RequestMapping(value = "DiscussReply.do")
	public ModelAndView discussReply(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		Document discuss= discussManager.discussReply(docLibID, docID, false);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("FVID", WebUtil.get(request, "FVID"));
		model.put("siteLibID", siteLibID);
		model.put("siteID",siteID);
		model.put("id", discuss.getLong("SYS_DOCUMENTID"));
		model.put("topic", StringUtils.getNotNull(discuss.getString("SYS_TOPIC")));
		model.put("content", StringUtils.getNotNull(discuss.getString("a_content")));
		model.put("userName", StringUtils.getNotNull(discuss.getString("SYS_AUTHORS")));
		model.put("userId", discuss.getLong("SYS_AUTHORID"));
		model.put("DocIDs", docID);
		model.put("DocLibID", docLibID);

		return new ModelAndView("/xy/nis/DiscussReply", model);
	}
	
	/** 官方回复提交 */
	@RequestMapping(value = "DiscussReplySubmit.do")
	public String discussReplySubmit(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long oldDocID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String a_answer = WebUtil.get(request, "a_answer");
		String message = discussManager.discussReplySubmit(docLibID, oldDocID, a_answer,request);
	
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (message == null){
			url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
		}
		return "redirect:" + url;
	}
	/** 清除举报 */
	@RequestMapping(value = "ExposeClear.do")
	public String exposeClear(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String message = discussManager.exposeClear(docLibID, docID);
		
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if (message == null){
			url += "&DocIDs=" + request.getParameter("DocIDs"); //操作成功
		} else {
			url += "&Info=" + URLEncoder.encode(message, "UTF-8");//有错误，需返回前台做提示
		}
		
		return "redirect:" + url;
	}
	
	/**
	 * 会员选择
	 */
	@RequestMapping(value = "FindMember.do")
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
				obj.put("mName", doc.get("mName")) ;
				obj.put("mHead", doc.get("mHead")) ;
				arr.add(obj) ;
			}
		}
        InfoHelper.outputJson(arr.toString(), response);
	}
}