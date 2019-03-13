package com.founder.xy.weibo.util;

import weibo4j.http.HttpClient;
import weibo4j.model.*;

import java.util.HashMap;
import java.util.Map;


public class BizWeiBoHttpHelper extends WeiBoHttpHelper{
	

	// 发布文本微博
	public static String postWeibo(String token, String content) throws Exception {
		
		return postWeibo(getBizUrl(POST_WEIBO_URL_WITH_CONTENT), token, content);
	}

	// 上传图片并发布一条新微博
	public static String postWeiboOneImage(String token, String content, String filePath)throws Exception {
		
		return postWeiboOneImage(getBizUrl(POST_WEIBO_URL_WITH_IMAGE),token,content,filePath);
		
	}

	// 发布微博，带多个图片
	public static String postWeiboMoreImages(String token, String content,
			String picIDs) throws Exception {
		
		return postWeiboMoreImages(getBizUrl(POST_WEIBO_URL_WITH_CONTENT_URL), token, content,picIDs);
	}

	// 上传图片
	/*
	public static String uploadImage(String token, String filePath) throws Exception{
				return uploadImage(getBizUrl(POST_WEIBO_UPLOAD_IMAGE), token, filePath);
	}

	public static String uploadImage(String token, InputStream in)throws Exception {
	
			return uploadImage(getBizUrl(POST_WEIBO_UPLOAD_IMAGE), token, in);
	}
*/	
	// 上传图片
	// 删除微博
	public static String deleteWeibo(String token, String wid) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		
		return deleteWeibo(getBizUrl(POST_WEIBO_DESTROY), token,wid);
	}
	
	// 发布评论
	public static String postComment(String token, String wid, String comment) throws Exception {
		
		return postComment(getBizUrl(POST_COMMENT),token, wid, comment);
	}

	// 发布评论回复
	public static String postReply(String token, String wid, String cid, String comment) throws Exception {
		
		return postReply(getBizUrl(POST_REPLY), token,wid,cid,comment);
	}

	//得到微博的转发数和评论数
	public static String getCount(String token, String ids)throws Exception {
	
		return getCount(getBizUrl(GET_WEIBO_COUNT), token, ids);
	}
	
	//得到微博的转发列表（某一页）
	public static String getReposts(String token, String wid, int page, int count) throws Exception{
		
		return getReposts(getBizUrl(GET_REPOSTS), token,wid,page,count);
	}

	//得到微博的评论列表（某一页）
	public static String getComments(String token, String wid, int page, int count) throws Exception{
		
		return getComments(getBizUrl(GET_COMMENTS), token,wid,page, count);
	}
	//读取评论
	public static CommentWapper getComments(String token, String id, Paging page) throws Exception{
		HttpClient client = new HttpClient();
		return Comment.constructWapperComments(client.get(getBizUrl(GET_COMMENTS), new PostParameter[]{new PostParameter("id", id)}, page));
	}
	//读取转发
	public static StatusWapper getRepostTimeline(String token,String id, Paging page) throws Exception {
		HttpClient client = new HttpClient();
		return Status.constructWapperStatus(client.get(getBizUrl(GET_REPOSTS), new PostParameter[]{new PostParameter("id", id)}, page));
	}}
