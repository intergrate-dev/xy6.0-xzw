package com.founder.xy.weibo.web;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.weibo.WeiboManager;
import com.founder.xy.weibo.data.Account;
import com.founder.xy.weibo.data.WeiboArticle;
import com.founder.xy.weibo.util.WeiboConstants;


@Controller
@RequestMapping("xy/weibo")
public class WeiboController {
	private static Log log = Context.getLog( "xy");
	
	@Autowired
	private WeiboManager wbManager;
	
	/** 保存微博 */
	@RequestMapping(value = "formSave.do")
	public void formSave(HttpServletRequest request,
			HttpServletResponse response) {
		
		WeiboArticle article = getWeiboArticle(request);
		boolean isNew = article.getId() == 0;
		boolean pubTimer = article.getPubTime() != null; //是否定时发布，以是否设置了发布时间为准
		
		SysUser user = ProcHelper.getUser(request);
		String error = wbManager.save(user.getUserName(), article);
		
		if (error != null) {
			InfoHelper.outputText(error, response);
			return;
		}
		
		String procName = isNew ? "新建" : "修改";
		// 是否发布
		int isPublish = WebUtil.getInt(request, "isPublish", 0);
		if (1 == isPublish && !pubTimer) {
			error = wbManager.publish(article.getDocLibID(), article.getId());
			
			if (error != null) {
				// 没发布成功，写流程记录，然后返回
				LogHelper.writeLog(article.getDocLibID(), article.getId(), user, procName, null);
				
				InfoHelper.outputText(error, response);
				return;
			} else {
				procName += "并发布";
			}
		}
		// 写流程记录
		LogHelper.writeLog(article.getDocLibID(), article.getId(), user, procName, null);
	}
	
	/** 发布（未发布的）微博 */
	@RequestMapping(value = "publish.do")
	public void publish(HttpServletRequest request, HttpServletResponse response) {
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		
		String error = wbManager.publish(docLibID, docID);
		
		if (error != null) {
			InfoHelper.outputText(error, response);
		} else {
			// 写流程记录
			LogHelper.writeLog(docLibID, docID, ProcHelper.getUser(request), "发布", null);
		}
	}

	@RequestMapping("/delete.do")
	public String delete(HttpServletRequest request,HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		
		String error = wbManager.delete(docLibID, docID);
		
        //调用after.do进行后处理：改变流程状态、解锁、刷新列表
        String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
        if (error == null) {
            url += "&DocIDs=" + docID; //操作成功
        } else {
            url += "&Info=" + URLEncoder.encode(error, "UTF-8");//有错误，需返回前台做提示
        }
        return "redirect:" + url;
	}

	/** 读微博 */
	@RequestMapping(value = "getWeibo.do")
	public void getWeibo(HttpServletRequest request, HttpServletResponse response) throws E5Exception, IOException{
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		long docID = WebUtil.getLong(request, "docID", 0);
		
		DocumentManager docmanager = DocumentManagerFactory.getInstance();
		Document doc = docmanager.get(docLibID, docID);
		
		String content = htmlToText(doc.getString("wb_content"));
		
		JSONObject json = new JSONObject();
		json.put("content", content);
		json.put("attachments", doc.getString("wb_attachments"));
		json.put("pubTime", doc.getString("wb_pubTime"));
		
		InfoHelper.outputJson(json.toString(), response);
	}
	
	/** 读转发 */
	@RequestMapping("/getReposts")
	public void getReposts(HttpServletRequest request, HttpServletResponse response ) {
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		int docID = WebUtil.getInt(request, "docID", 0);
		
		int page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
		try {
			String result = wbManager.getReposts(docLibID, docID, page);
			InfoHelper.outputJson(result, response);
		} catch(Exception e) {
			log.error("【微博--获取转发列表】异常：" + e.getLocalizedMessage(), e);
		}
	}

	/** 读评论 */
	@RequestMapping("/getComments")
	public void getComments(HttpServletRequest request, HttpServletResponse response ) {
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		int docID = WebUtil.getInt(request, "docID", 0);
		
		int page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
		try {
			String result = wbManager.getComments(docLibID, docID, page);
			
			InfoHelper.outputJson(result, response);
		} catch(Exception e) {
			log.error("【微博--获取评论列表】异常：" + e.getLocalizedMessage(), e);
		}
	}
	
	/** 发布评论 */
	@RequestMapping(value = "addComment.do", method = RequestMethod.POST)
	@ResponseBody
	public void addComment(HttpServletRequest request, HttpServletResponse response){
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		int docID = WebUtil.getInt(request, "docID", 0);
		String text = WebUtil.get(request, "content");
		
		try{
			long id = wbManager.publishComment(docLibID, docID, text);
			
			if (id > 0) {
				LogHelper.writeLog(docLibID, docID, ProcHelper.getUser(request), "评论", null);
			}
			
			InfoHelper.outputText(String.valueOf(id), response);
		}catch(Exception e){
			log.error("发布评论时异常:" + e.getLocalizedMessage(), e);
		}
	}

	/** 发布回复 */
	@RequestMapping(value = "addReply.do", method = RequestMethod.POST)
	public void addReply(HttpServletRequest request, HttpServletResponse response){
		int docLibID = WebUtil.getInt(request, "docLibID", 0);
		int docID = WebUtil.getInt(request, "docID", 0);
		String text = WebUtil.get(request, "content");
		String cid = WebUtil.get(request, "cid"); //评论的微博id
		
		try{
			long id = wbManager.publishReply(docLibID, docID, cid, text);
			
			if (id > 0) {
				LogHelper.writeLog(docLibID, docID, ProcHelper.getUser(request), "评论回复", null);
			}
			
			InfoHelper.outputText(String.valueOf(id), response);
		}catch(Exception e){
			log.error("发布回复时异常:" + e.getLocalizedMessage(), e);
		}
	}

	/** 根据新浪上传接口上传图片，返回图片pid */
	@RequestMapping("uploadPic")
	public String sinaUploadPic(File file, String source){
		return null;
	}
	
	/** 微博授权 */
	@RequestMapping(value = "grant.do")
	public String grant(HttpServletRequest request, Map<String, Object> model) {
		
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		
		String code = WebUtil.get(request, "code");
		String uuid = WebUtil.get(request, "UUID");
		String FOUNDER_APPID = WeiboConstants.FOUNDER_APPID;
		String FOUNDER_APPSCRECT = WeiboConstants.FOUNDER_APPSCRECT;
		System.out.println(FOUNDER_APPID);
		System.out.println(FOUNDER_APPSCRECT);
		
		Account account = wbManager.getAccount(docLibID, docID);
		if (code == null) {
			model.put("docID", docID);
			model.put("docLibID", docLibID);
			model.put("appKey", account.getAppKey());
			model.put("UUID", uuid);
			
			return "xy/weibo/Grant";
		} else {
			String error = wbManager.readToken(docLibID, docID, code);
			
	        if (error == null) {
		        //完成操作，调用after.do
		        return "redirect:/e5workspace/after.do?UUID=" + uuid + "&DocIDs=" + docID;
	        } else {
				model.put("docID", docID);
				model.put("docLibID", docLibID);
				model.put("appKey", account.getAppKey());
				model.put("error", error);
				model.put("UUID", uuid);
				
				return "xy/weibo/Grant";
	        }
		}
	}
	
	private WeiboArticle getWeiboArticle(HttpServletRequest request) {
		WeiboArticle article = new WeiboArticle();
		
		article.setId(WebUtil.getLong(request, "docId", 0));
		article.setDocLibID(WebUtil.getInt(request, "docLibID", 0));
		article.setAccountID(WebUtil.getInt(request, "accountID", 0));
		article.setContent(WebUtil.get(request, "content"));
		article.setAttachments(WebUtil.get(request, "attachments")); //附件
		
		//若设置了定时发布，且定时发布时间晚于当前时间，则设置定时发布
		int pubTimer = WebUtil.getInt(request, "pubTimer", 0);
		if (pubTimer == 1) {
			String time = WebUtil.get(request, "pubTime");
			Timestamp pubTime = new Timestamp(DateUtils.parse(time, "yyyy-MM-dd HH:mm").getTime());
			
			if (pubTime.after(DateUtils.getDate()))
				article.setPubTime(pubTime);// 定时发布
		}
		return article;
	}

	/** html格式字符串转换text文本 */
	private String htmlToText(String content){
		if (content != null && !"".equals(content.trim())){
			Pattern pattern = Pattern.compile("<[^<|^>]*>");
			Matcher matcher = pattern.matcher(content);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String group = matcher.group();
				if (group.matches("<[\\s]*>"))
					matcher.appendReplacement(sb, group);
				else if (group.matches("<br[^<|^>]*>") || group.matches("<br/>")
						|| group.matches("</p>") || group.matches("</tr>")
						|| group.matches("</h1>") || group.matches("</h2>")
						|| group.matches("</h3>") || group.matches("</li>")
						|| group.matches("</ol>") || group.matches("</dl>")
						|| group.matches("</ul>"))
					matcher.appendReplacement(sb, "");
				else
					matcher.appendReplacement(sb, "");
	
			}
			matcher.appendTail(sb);
			content = sb.toString();
		}
		return content;
	}
}
