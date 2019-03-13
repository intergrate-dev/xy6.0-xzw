package com.founder.xy.weibo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.db.util.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.weibo.data.Account;
import com.founder.xy.weibo.util.BizWeiBoHttpHelper;

public class WeiboPublisher {
	public static String WEIBO_ERROR_NOTEXIST = "20101";
	
	//1-普通-text；2-图片-pic；3-音频-music；4-视频-video；5-话题-topic；6-长微博-ltext;7-评论-comment,8-回复-reply,9-转发-repost；
	public static final int WEIBO_TYPE_TEXT = 1;
	public static final int WEIBO_TYPE_PIC = 2;
	public static final int WEIBO_TYPE_MUSIC = 3;
	public static final int WEIBO_TYPE_VIDEO = 4;
	public static final int WEIBO_TYPE_TOPIC = 5;
	public static final int WEIBO_TYPE_LTEXT = 6;
	public static final int WEIBO_TYPE_COMMENT = 7;
	public static final int WEIBO_TYPE_REPLY = 8;
	public static final int WEIBO_TYPE_REPOST = 9;

	private static Log log = Context.getLog("xy");
	
	/**
	 * 发布微博
	 * @param token 账号令牌，只在定时发布任务时传入，单个微博发布时传为null
	 * @param weibo 微博文档对象
	 * @return 发布成功则返回null，否则返回错误消息
	 */
	public static String publish(String token, Document weibo) throws Exception {
		Account account = null;
		if (token == null) {
			account = getAccount(weibo);
			token = account.getAccessToken();
		}
		//token = "f86919e1612b3590aefaa03bdabc7841";//测试代码
		String atts = weibo.getString("wb_attachments");
		
		if (StringUtils.isBlank(atts)) {// 发表文字微博
			return pubOnlyText(token, weibo);
		} else {
			// 带附件的微博
			JSONObject attsJson = JSONObject.fromObject(atts);
			
			JSONArray attachList = attsJson.getJSONArray("pics");
			if (attachList == null || attachList.isEmpty()) {
				return pubOnlyText(token, weibo);
			}
			
			//单图
			if (attachList.size() == 1) {
				String filePath = attachList.getString(0);
				
				return pubWithOneImage(token, weibo, filePath);
			} else {
				//多图。需判断微博账号是否支持多图权限，不支持时就只发一个图。
				if (account == null)
					account = getAccount(weibo);
				if (account.isMultiPic()) {
					String picid = uploadPics(token, weibo, attachList);
					return pubWithMoreImages(token, weibo, picid);
				} else {
					String filePath = attachList.getString(0);
					return pubWithOneImage(token, weibo, filePath);
				}
			}
		}
	}

	/** 删除微博 */
	public static String delete(Document weibo) throws Exception {
		String token = getAccessToken(weibo);
		String wid = weibo.getString("wb_wid");
		try {
			BizWeiBoHttpHelper.deleteWeibo(token, wid);
		} catch (Exception e) {
			String errorCode = parseErrorCode(e.getMessage());
			if (errorCode.equals(WEIBO_ERROR_NOTEXIST)) {
				return null; //微博不存在，则认为删除成功
			} else
				return e.getLocalizedMessage();
		}
		return null;
	}

	/** 读微博的转发列表 
	 * @throws Exception */
	public static String getReposts(Document weibo, int page) throws Exception {
		String token = getAccessToken(weibo);
		String wid = weibo.getString("wb_wid");
		
		/*
		Timeline tm = new Timeline(token);
	
		Paging paging = new Paging();
		paging.setPage(page);
		paging.setCount(3);//(WeiboUtil.REPOST_COUNT);
		
		try {
			StatusWapper statusWapper = tm.getRepostTimeline(wid, paging);
			List<Status> reposts = statusWapper.getStatuses();
			
			return jsonReposts(reposts);
	
		} catch (Exception e) {
			log.error("读微博转发列表失败：" + e.getLocalizedMessage(), e);
			return null;
		}
		*/
		return BizWeiBoHttpHelper.getReposts(token, wid, page, 5);//(WeiboUtil.REPOST_COUNT);
	}

	/** 读微博的评论列表 
	 * @throws Exception */
	public static String getComments(Document weibo, int page) throws Exception {
		String token = getAccessToken(weibo);
		String wid = weibo.getString("wb_wid");
		
		/*
		Comments com = new Comments(token);
		
		Paging paging = new Paging();
		paging.setPage(page);
		paging.setCount(3);//(COMENT_COUNT);
		
		try {
			CommentWapper commentWapper = com.getCommentById(wid, paging, 0);// 查询到的微博信息集合

			List<Comment> comments = commentWapper.getComments();
			return jsonComments(comments);
		} catch (Exception e) {
			log.error("读微博评论列表失败：" + e.getLocalizedMessage(), e);
			return null;
		}
		*/
		return BizWeiBoHttpHelper.getComments(token, wid, page, 5);//(COMENT_COUNT);
	}

	/** 发布评论 */
	public static long publishComment(Document weibo, String text) throws Exception {
		text = getPubContent(text);
		String wid = weibo.getString("wb_wid");
		
		String token = getAccessToken(weibo);
		
		/*
		Comments comments = new Comments(token);
		Comment comment = comments.createComment(text, wid);
		*/
		String result = BizWeiBoHttpHelper.postComment(token, wid, text);
		if (!StringUtils.isBlank(result)) {
			JSONObject json = JsonHelper.getJson(result);
			return JsonHelper.getLong(json, "id");
		} else {
			return 0;
		}
	}

	/** 发布评论的回复 */
	public static long publishReply(Document weibo, String cid, String text) throws Exception {
		text = getPubContent(text);

		String token = getAccessToken(weibo);
		String wid = weibo.getString("wb_wid");
		
		/*
		Comments comments = new Comments(token);
		Comment commentReply = comments.replyComment(cid, wid, text);
		return commentReply.getId();
		*/
		String result = BizWeiBoHttpHelper.postReply(token, wid, cid, text);
		if (!StringUtils.isBlank(result)) {
			JSONObject json = JsonHelper.getJson(result);
			return JsonHelper.getLong(json, "id");
		} else {
			return 0;
		}
	}

	/** 读accessToken */
	public static String getAccessToken(Account account, String code) throws Exception {
		return WeiboAPI.getAccessToken(account, code);
	}

	/** 上传多个图片 */
	private static String uploadPics(String token, Document weibo, JSONArray attachList) throws Exception {
		StringBuilder picIDs = new StringBuilder();
		
		for (int i = 0; i < attachList.size(); i++) {
			if (i >= 9) break;
			
			String filePath = attachList.getString(i);
			filePath = InfoHelper.getFilePathInDevice(filePath);
			
			String pid = uploadImage(token, weibo, filePath);
			
			if (picIDs.length() > 0) picIDs.append(",");
			picIDs.append(pid);
		}
		
		return picIDs.toString();
	}
	
	/** 上传图片 */
	private static String uploadImage(String token, Document weibo, String picPath) throws Exception {
		String ret = BizWeiBoHttpHelper.uploadImage(token, picPath);
		if (!StringUtils.isBlank(ret)){
			JSONObject jsonObject = JSONObject.fromObject(ret);
			return jsonObject.getString("pic_id");
		}
		return null;
	}
	
	/**文字微博发布*/
	private static String pubOnlyText(String token, Document weibo)
			throws Exception {
		String text = getPubContent(weibo);
		//String result = WeiboAPI.postWeibo(token, text);
		String result = BizWeiBoHttpHelper.postWeibo(token, text);
		
		JSONObject json = JsonHelper.getJson(result);
		if (json != null)
			return JsonHelper.getString(json, "idstr");
		else
			return null;
	}

	/**单图片微博发布*/
	private static String pubWithOneImage(String token, Document weibo, String filePath) throws Exception {
		String text = getPubContent(weibo);
		filePath = InfoHelper.getFilePathInDevice(filePath);//图片文件
		//String result = WeiboAPI.postWeiboOneImage(token, text, filePath);
		String result = BizWeiBoHttpHelper.postWeiboOneImage(token, text, filePath);
		return getWid(result);
	}

	//带多图发布
	private static String pubWithMoreImages(String token, Document weibo, String picIDs) throws Exception {
		String text = getPubContent(weibo);
	
		String result = BizWeiBoHttpHelper.postWeiboMoreImages(token, text, picIDs);
		
		return getWid(result);
	}

	//解析api调用结果，得到微博id
	private static String getWid(String result) {
		JSONObject json = JsonHelper.getJson(result);
		if (json != null)
			return json.getString("id"); //wid
		else
			return null;
	}

	//从数据库中读微博账号，得到表中保存的token
	private static String getAccessToken(Document weibo) {
		Account account = getAccount(weibo);
		return account.getAccessToken();
	}

	//从数据库中读微博账号
	private static Account getAccount(Document weibo) {
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WBACCOUNT.typeID(), weibo.getDocLibID());
		long accountID = weibo.getLong("wb_accountID");
		
		WeiboManager wbManager = (WeiboManager)Context.getBean("weiboManager");
		return wbManager.getAccount(accountLibID, accountID);
	}

	//转换成发布的纯文本
	private static String getPubContent(Document doc){
		String oriContent = String.valueOf(doc.get("wb_content"));
		return getPubContent(oriContent);
	}

	//转换成发布的纯文本
	private static String getPubContent(String oriContent){
		String content = null;
		try{
			content = xhtml2Text(oriContent.trim());
			
			//如果微博正文为空，设置默认值发布
			if (StringUtils.isBlank(content)){
				//获取当前时间 
				DateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String pubTime = formate.format(new Date());
				
				content = "无内容\n" + pubTime;
			}
		}catch(Exception e){
			log.error("获取微博正文内容失败"+e);
		}
		return content;
	}

	/**将带html标签的字符串转换成纯文本 */
	private static String xhtml2Text(String content) {
		if (content == null || "".equals(content))
			return content;
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
		return sb.toString();
	}

	/**解析微博操作失败时的错误代码 */
	private static String parseErrorCode(String errorMessage){
		int start = errorMessage.indexOf("error_code:");
		if (start >= 0){
			return errorMessage.substring(start + 11, start + 16);
		} else {
			return "";
		}
	}
	/*
	 * 使用微博sdk时的转换json代码，无用
	//转发json
	@SuppressWarnings("unused")
	private static String jsonReposts(List<Status> reposts) {
		JSONArray result = new JSONArray();
		for (Status comment : reposts) {
			JSONObject one = new JSONObject();
			one.put("id", comment.getId());
			one.put("userUrl", comment.getUser().getProfileImageUrl());
			one.put("userName", comment.getUser().getScreenName());
			one.put("text", comment.getText());
			one.put("createdAt", DateUtils.format(comment.getCreatedAt(), "yyyy-MM-dd HH:mm"));
			
			result.add(one);
		}
		return result.toString();
	}

	//评论的createdAt是Date类型，无法用JSONObject直接转换，因此手动转成json
	@SuppressWarnings("unused")
	private static String jsonComments(List<Comment> comments) {
		JSONArray result = new JSONArray();
		for (Comment comment : comments) {
			JSONObject one = new JSONObject();
			one.put("id", comment.getId());
			one.put("wid", comment.getStatus().getId());
			one.put("userUrl", comment.getUser().getProfileImageUrl());
			one.put("userName", comment.getUser().getScreenName());
			one.put("text", comment.getText());
			one.put("createdAt", DateUtils.format(comment.getCreatedAt(), "yyyy-MM-dd HH:mm"));
			
			result.add(one);
		}
		return result.toString();
	}
	*/
}
