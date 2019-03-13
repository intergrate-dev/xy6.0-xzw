package com.founder.xy.weibo.util;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeiBoHttpHelper extends WeiBoBaseHttpHelper {
	/*
	 * 商业接口
	 * */
	static private String WeiBo_BASE_STATUS_BIZ_URL="https://c.api.weibo.com/2/statuses/";
	static private String WeiBo_BASE_COMMENT_BIZ_URL="https://c.api.weibo.com/2/comments/";

	/**
	 * 开发接口
	 */
	protected static final String WeiBo_BASE_STATUS_URL="https://api.weibo.com/2/statuses/";
	protected static final String WeiBo_BASE_COMMENT_URL="https://api.weibo.com/2/comments/";

	/** 发布微博，纯文本 */
	protected static final String POST_WEIBO_URL_WITH_CONTENT = WeiBo_BASE_STATUS_URL+"update.json?";
	
	/** 发布微博，单图 */
	protected static final String POST_WEIBO_URL_WITH_IMAGE = WeiBo_BASE_STATUS_URL+"upload.json?";
	
	/** 发布微博，多图 */
	protected static final String POST_WEIBO_URL_WITH_CONTENT_URL = WeiBo_BASE_STATUS_URL+"upload_url_text.json?";

	/**上传图片 */
	protected static final String POST_WEIBO_UPLOAD_IMAGE = WeiBo_BASE_STATUS_URL+"upload_pic.json?";
	
	/**删除 */
	protected static final String POST_WEIBO_DESTROY = WeiBo_BASE_STATUS_URL+"destroy.json?";

		
	/**读转发数和评论数 */
	protected static final String GET_WEIBO_COUNT = WeiBo_BASE_STATUS_URL+"count.json?";

	protected static final String GET_REPOSTS = WeiBo_BASE_STATUS_URL+"repost_timeline.json?";
	
	/**评论 、回复评论*/
	protected static final String POST_COMMENT = WeiBo_BASE_COMMENT_URL+"create.json?";
	protected static final String POST_REPLY = WeiBo_BASE_COMMENT_URL+"reply.json?";
	/**读评论列表、转发列表 */
	protected static final String GET_COMMENTS = WeiBo_BASE_COMMENT_URL+"show.json?";
	private static final String LONGLINK_SHORTEN = "https://api.weibo.com/2/short_url/shorten.json?";


	protected static String getBizUrl(String url) throws Exception
	{
		
		int index = url.indexOf(WeiBo_BASE_STATUS_URL);
		if(index==-1)
		{
			index = url.indexOf(WeiBo_BASE_COMMENT_URL);
			if(index==-1)
			{
				throw new Exception("其他地址："+url);
			}
			String operation = url.substring(WeiBo_BASE_COMMENT_URL.length());
			index = operation.indexOf(".json?");
			if(index==-1)
			{
				throw new Exception("其他地址："+url);
			}
			operation = operation.substring(0, index);
			return WeiBo_BASE_COMMENT_BIZ_URL+operation+"/biz.json?";
				
		}
		else
		{
			String operation = url.substring(WeiBo_BASE_STATUS_URL.length());
			index = operation.indexOf(".json?");
			if(index==-1)
			{
				throw new Exception("其他地址："+url);
			}
			operation = operation.substring(0, index);
			return WeiBo_BASE_STATUS_BIZ_URL+operation+"/biz.json?";
		}
	}

	// 发布文本微博
	public static String postWeibo(String token, String content) throws Exception {
		
		return postWeibo(POST_WEIBO_URL_WITH_CONTENT, token, content);
	}

	// 上传图片并发布一条新微博
	public static String postWeiboOneImage(String token, String content, String filePath) throws Exception{
		
		return postWeiboOneImage(POST_WEIBO_URL_WITH_IMAGE,token,content,filePath);
		
	}

	// 发布微博，带多个图片
	public static String postWeiboMoreImages(String token, String content,
			String picIDs) throws Exception {
		
		return postWeiboMoreImages(POST_WEIBO_URL_WITH_CONTENT_URL, token, content,picIDs);
	}

	// 上传图片
	public static String uploadImage(String token, String filePath) throws Exception{
				return uploadImage(POST_WEIBO_UPLOAD_IMAGE, token, filePath);
	}

	// 上传图片
	public static String uploadImage(String token, InputStream in) throws Exception{
	
			return uploadImage(POST_WEIBO_UPLOAD_IMAGE, token, in);
	}
	
	// 删除微博
	public static String deleteWeibo(String token, String wid) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		
		return deleteWeibo(POST_WEIBO_DESTROY, token,wid);
	}
	
	// 发布评论
	public static String postComment(String token, String wid, String comment) throws Exception {
		
		return postComment(POST_COMMENT, token,wid, comment);
	}

	// 发布评论回复
	public static String postReply(String token, String wid, String cid, String comment) throws Exception {
		
		return postReply(POST_REPLY, token,wid,cid,comment);
	}

	//得到微博的转发数和评论数
	public static String getCount(String token, String ids) throws Exception{
	
		return getCount(GET_WEIBO_COUNT, token, ids);
	}
	
	//得到微博的转发列表（某一页）
	public static String getReposts(String token, String wid, int page, int count) throws Exception{
		
		return getReposts(GET_REPOSTS, token,wid,page,count);
	}

	//得到微博的评论列表（某一页）
	public static String getComments(String token, String wid, int page, int count) throws Exception{
		
		return getComments(GET_COMMENTS, token,wid,page, count);
	}

	public static String shorten(String token, List<String> urlList) {
		return shorten(LONGLINK_SHORTEN,token,urlList);
	}
}
