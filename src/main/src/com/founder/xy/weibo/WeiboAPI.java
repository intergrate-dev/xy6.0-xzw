package com.founder.xy.weibo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.founder.e5.commons.FileUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.web.HTTPHelper;
import com.founder.xy.weibo.data.Account;

public class WeiboAPI {
	/** 发布微博，纯文本 */
	private static final String POST_WEIBO_URL_WITH_CONTENT = "https://api.weibo.com/2/statuses/update.json?";
	
	/** 发布微博，单图 */
	private static final String POST_WEIBO_URL_WITH_IMAGE = "https://upload.api.weibo.com/2/statuses/upload.json?";
	
	/** 发布微博，多图 */
	private static final String POST_WEIBO_URL_WITH_CONTENT_URL = "https://api.weibo.com/2/statuses/upload_url_text.json?";

	/**上传图片 */
	private static final String POST_WEIBO_UPLOAD_IMAGE = "https://api.weibo.com/2/statuses/upload_pic.json?";
	
	/**删除 */
	private static final String POST_WEIBO_DESTROY = "https://api.weibo.com/2/statuses/destroy.json?";

	/**评论 、回复评论*/
	private static final String POST_COMMENT = "https://api.weibo.com/2/comments/create.json?";
	private static final String POST_REPLY = "https://api.weibo.com/2/comments/reply.json?";
		
	/**读转发数和评论数 */
	private static final String GET_WEIBO_COUNT = "https://api.weibo.com/2/statuses/count.json?";

	/**读评论列表、转发列表 */
	private static final String GET_COMMENTS = "https://api.weibo.com/2/comments/show.json?";
	private static final String GET_REPOSTS = "https://api.weibo.com/2/statuses/repost_timeline.json?";
	
	/** 授权、得到AccessToken */
	private static final String POST_GRANT = "https://api.weibo.com/oauth2/authorize?";
	private static final String POST_TOKEN = "https://api.weibo.com/oauth2/access_token?";
	//private static final String POST_GRANT_REDIRECT = "http://app.weibo.com/detail/5Mu4Cs";
	private static final String POST_GRANT_REDIRECT = "https://api.weibo.com/oauth2/default.html";
	
	private static Log log = Context.getLog("xy");

	// 发布文本微博
	public static String postWeibo(String token, String content) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		
		return postMethodRequest(POST_WEIBO_URL_WITH_CONTENT, params, header);
	}

	// 上传图片并发布一条新微博
	public static String postWeiboOneImage(String token, String content, String filePath) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		
		Map<String, byte[]> items = new HashMap<String, byte[]>();
		
		byte[] targetFileBytes = FileUtils.readFile(filePath);
		items.put("pic", targetFileBytes);

		return postMethodRequestWithFile(POST_WEIBO_URL_WITH_IMAGE, params, header, items);
		
	}

	// 发布微博，带多个图片
	public static String postWeiboMoreImages(String token, String content,
			String picIDs) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		params.put("pic_id", picIDs);
		
		return postMethodRequest(POST_WEIBO_URL_WITH_CONTENT_URL, params, header);
	}

	// 上传图片
	public static String uploadImage(String token, String filePath) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
	
		Map<String, byte[]> items = new HashMap<String, byte[]>();
		byte[] targetFileBytes = FileUtils.readFile(filePath);
		items.put("pic", targetFileBytes);

		return postMethodRequestWithFile(POST_WEIBO_UPLOAD_IMAGE, params, header, items);
	}
	
	// 删除微博
	public static String deleteWeibo(String token, String wid) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		
		return postMethodRequest(POST_WEIBO_DESTROY, params, header);
	}
	
	// 发布评论
	public static String postComment(String token, String wid, String comment) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("comment", comment);
		
		return postMethodRequest(POST_COMMENT, params, header);
	}

	// 发布评论回复
	public static String postReply(String token, String wid, String cid, String comment) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("cid", cid);
		params.put("comment", comment);
		
		return postMethodRequest(POST_REPLY, params, header);
	}

	//得到微博的转发数和评论数
	public static String getCount(String token, String ids) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("ids", ids);
		
		return getMethodRequest(GET_WEIBO_COUNT, params, header);
	}
	
	//得到微博的转发列表（某一页）
	public static String getReposts(String token, String wid, int page, int count) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("page", String.valueOf(page));
		params.put("count", String.valueOf(count));
		
		return getMethodRequest(GET_REPOSTS, params, header);
	}

	//得到微博的评论列表（某一页）
	public static String getComments(String token, String wid, int page, int count) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("page", String.valueOf(page));
		params.put("count", String.valueOf(count));
		
		return getMethodRequest(GET_COMMENTS, params, header);
	}
	
	/** 获取AccessToken */
	public static String getAccessToken(Account account, String code) throws Exception {
	    if (code == null) code = getAuthCode(account.getAppKey(), account.getAppUser(), account.getAppPassword());
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", account.getAppKey());
		params.put("client_secret", account.getAppSecret());
		params.put("grant_type", "authorization_code");
		params.put("code", code);
		params.put("redirect_uri", POST_GRANT_REDIRECT);
		
		String result = postMethodRequest(POST_TOKEN, params, header);
		
		JSONObject json = JsonHelper.getJson(result);
		String accessToken = JsonHelper.getString(json, "access_token");
		
		return accessToken;
	}
	
	/**
	 * 未用
	 * 
	 * 试图自动进行新浪微博的授权并取得code，未成功。
	 * 因为并不会自动带code参数跳转到指定地址，而是出现确认界面。
	 */
	private static String getAuthCode(String appKey, String user,String pwd) throws Exception{
		//String redirectUrl = "https://api.weibo.com/oauth2/default.html"; //使用这个地址会返回失败，使用下面的地址才可以到确认授权页面
		
		Map<String, String> params = new HashMap<String, String>();
        params.put("action", "submit");
        params.put("withOfficalFlag", "0");
        params.put("isLoginSina", "0");
        params.put("state", "");
        
        params.put("client_id", appKey);
        params.put("redirect_uri", POST_GRANT_REDIRECT);
        params.put("response_type", "code");
        
        params.put("userId", user);
        params.put("passwd", pwd);
		
		PostMethod postMethod = new PostMethod(POST_GRANT);
		setHeaderParams(postMethod, header);

		setParams(postMethod, params);
		
		postMethod.addRequestHeader("Referer", "https://api.weibo.com/oauth2/authorize?client_id=" + appKey
				+ "&redirect_uri=" + POST_GRANT_REDIRECT + "&response_type=code");
		try {
			post(postMethod);
			
			// 从返回的Header里获取Location
			String redirect = postMethod.getResponseHeader("Location").getValue();
			
			// 解析Code
			String values = redirect.substring(redirect.lastIndexOf("?") + 1);
			Map<String, String> paramsMap = new HashMap<String, String>();
			for (String s : values.split("&")) {
			    String[] t = s.split("=");
			    paramsMap.put(t[0], t[1]);
			}
			String code = paramsMap.get("code");
			    
			return code;
		} finally {
			postMethod.releaseConnection();
		}
	}

	private static String postMethodRequest(String url,
		Map<String, String> params, Map<String, String> header) throws Exception {
		PostMethod pm = new PostMethod(url);
		try {
			setHeaderParams(pm, header);

			setParams(pm, params);
			
			return post(pm);
		} finally {
			pm.releaseConnection();
		}
	}

	private static String postMethodRequestWithFile(String url,
		Map<String, String> params, Map<String, String> header,
		Map<String, byte[]> itemsMap) throws Exception {

		PostMethod pm = new PostMethod(url);
		try {
			setHeaderParams(pm, header);
			
			Part[] parts = assembleParts(itemsMap, params);
			
			pm.setRequestEntity(new MultipartRequestEntity(parts, pm.getParams()));
			
			return post(pm);
		} finally {
			pm.releaseConnection();
		}
	}

	private static String getMethodRequest(String url,
			Map<String, String> params, Map<String, String> header) throws Exception {

		url = addUrlParams(url, params);
		GetMethod pm = new GetMethod(url);
		
		try {
			setHeaderParams(pm, header);
			return post(pm);
		} finally {
			pm.releaseConnection();
		}
	}

	private static String addUrlParams(String url, Map<String, String> params) {
		StringBuilder sb = new StringBuilder(url);
		if (params != null) {
			int count = 0;
			for (String param_key : params.keySet()) {
				if (param_key == null || params.get(param_key) == null)
					continue;
				count++;
				if (count > 1) sb.append("&");
				
				sb.append(param_key).append("=").append(params.get(param_key));
			}
		}
		return sb.toString();
	}

	private static void setHeaderParams(HttpMethod pm, Map<String, String> header) {
		if (header != null) {
			for (String head_key : header.keySet()) {
				if (head_key == null || header.get(head_key) == null)
					continue;
				pm.addRequestHeader(head_key, header.get(head_key));
			}
		}
	}

	private static void setParams(PostMethod pm, Map<String, String> params) {
		if (params != null) {
			for (String param_key : params.keySet()) {
				if (param_key == null || params.get(param_key) == null)
					continue;
				pm.addParameter(param_key, params.get(param_key));
			}
		}
	}

	private static Part[] assembleParts(Map<String, byte[]> itemsMap,
			Map<String, String> params) {
		int part_size = 1;
		if (params != null) part_size = params.size();
		if (itemsMap != null) part_size = part_size + itemsMap.size();
		
		Part[] parts = new Part[part_size];
		int index = 0;
		if (itemsMap != null) {
			for (String item_name : itemsMap.keySet()) {
				if (itemsMap.get(item_name) == null)
					continue;
				parts[index++] = new FilePart(item_name,
						new ByteArrayPartSource(item_name, itemsMap.get(item_name)),
						"multipart/form-data;", "utf-8");
			}
		}
	
		if (params != null) {
			for (String param_key : params.keySet()) {
				if (param_key == null || params.get(param_key) == null)
					continue;
				parts[index++] = new StringPart(param_key, params.get(param_key), "utf-8");
			}
		}
		return parts;
	}

	//真正访问url提交请求
	private static String post(HttpMethod method) throws E5Exception, IOException {
		int statusCode = HTTPHelper.sendRequestWithProxy(method);
		
		if (statusCode == HttpStatus.SC_OK) {
			return method.getResponseBodyAsString();
		} else if (statusCode == 403) {
			throw new E5Exception(statusCode, "拒绝访问！微博服务器不允许频繁访问。");
		} else {
			throw new E5Exception(statusCode, "访问失败，error_code:" + statusCode);
		}
	}
	//从一个图片url读到图片文件内容
	@SuppressWarnings("unused")
	private static byte[] readFromURL(String url) {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(url);

		// 加入同步避免被防盗链机制屏蔽而取不到内容
		Header h = new Header("referer", "hupan.com");
		getMethod.setRequestHeader(h);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				return new byte[] {};
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();

			// 处理内容
			return responseBody;
		} catch (Exception e) {
			System.out.println("读取url失败：url->" + url + ":" + e);
			log.error(e);
			return new byte[] {};
		} finally {
			getMethod.releaseConnection();
		}
	}

	/** 新浪api请求的参数 */
	private static Map<String, String> header = new HashMap<String, String>();
	static {
		header.put("Accept-Language", "zh-CN,zh;q=0.8");
		header.put("User-Agent", "test sina api");
		header.put("Accept-Charset", "utf-8;q=0.7,*;q=0.3");
	}
}
