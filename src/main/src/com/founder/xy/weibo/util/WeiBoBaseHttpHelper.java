package com.founder.xy.weibo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.nodes.Element;

import com.founder.e5.commons.FileUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.web.HTTPHelper;
import com.founder.xy.commons.web.WebProxy;
import com.founder.xy.weibo.html.xpath.model.JXDocument;
import com.founder.xy.weibo.model.OathResult;
import com.founder.xy.weibo.model.WeiboServiceProvider;

import net.sf.json.JSONObject;
import weibo4j.http.BASE64Encoder;

public class WeiBoBaseHttpHelper {
	
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
	'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
	'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static final String authorizeURL="https://api.weibo.com/oauth2/authorize";
	private static final String OAUTH_CODE_URL = authorizeURL+"?client_id=<client_id>&client_secret=<client_secret>"+
               "&redirect_uri=https://api.weibo.com/oauth2/default.html&response_type=code";

	//private static final String redirect_URI="http://app.weibo.com/detail/5Mu4Cs";
	private static final String accessTokenURL = "https://api.weibo.com/oauth2/access_token";

	private static final String redirect_URI="https://api.weibo.com/oauth2/default.html";

	private static final String PRE_LOGIN_URL = "https://login.sina.com.cn/sso/prelogin.php?"+
             "entry=openapi&callback=sinaSSOController.preloginCallBack"+
             "&su=<username>&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.15)&_=";
	
	private static final String LOGIN_URL = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)&_=";

	private static String replaceNewValue(String url,String label,String value)
	{
		String urlLabel ="<"+label+">";
		int index = url.indexOf(urlLabel);
		return url.substring(0,index)+value+url.substring(index+urlLabel.length());
	}
/**
 * 自动获取token	
 * @param userName
 * @param password
 * @return
 */
	public static OathResult getOathResult(String userName,String password,String appID,String appSecret)
	{
		OathResult oathResult=null;
		String encodeUserName = convertBASE64Encoder(userName);
		String url = PRE_LOGIN_URL;
		String repUrl =replaceNewValue(url,"username",encodeUserName)+new Date().getTime();

		long timeStamp=new Date().getTime();
		try
		{
			String retStr = postMethodRequest(repUrl,new HashMap<String, String>(),header);
			int pIndex = retStr.indexOf('(');
			int endIndex = retStr.lastIndexOf(')');
			if(pIndex!=-1 && endIndex!=-1)
			{
				String json = retStr.substring(pIndex+1, endIndex);
				JSONObject jsonObj = JsonUtil.getJson(json);
				String pcid = JsonUtil.getString(jsonObj, "pcid");
				String nonce = JsonUtil.getString(jsonObj, "nonce");
				String pubkey = JsonUtil.getString(jsonObj, "pubkey");
				String rsakv = JsonUtil.getString(jsonObj, "rsakv");
				String servertime = JsonUtil.getString(jsonObj, "servertime");
				String exectime = JsonUtil.getString(jsonObj, "exectime");
				long longExeTime=154;
				try
				{
					longExeTime = Long.parseLong(exectime);
				}
				catch(Exception ex){}
				String rsa_password = getSP(password, pubkey, servertime, nonce);
				Map<String, String> params = new HashMap<String, String>();
				params.put("appkey", appID);
				params.put("entry", "openapi");
				
				
				params.put("gateway", "1");
				params.put("from", "");
				params.put("savestate", "0");
				params.put("userticket", "1");
				params.put("vsnf", "1");
				params.put("service", "miniblog");
				params.put("encoding", "UTF-8");
				params.put("pwencode", "rsa2");
				params.put("sr", "1280*800");
				params.put("prelt", String.valueOf(new Date().getTime()-timeStamp+longExeTime));
				params.put("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
				params.put("rsakv", rsakv);
				params.put("servertime",servertime);
				params.put("nonce",nonce);
				params.put("su", encodeUserName);
				params.put("sp", rsa_password);
				params.put("returntype", "TEXT");
				params.put("pcid", pcid);
				params.put("door", "");
				retStr = postMethodRequest(LOGIN_URL+new Date().getTime(),params,header);
				
				jsonObj = JsonUtil.getJson(retStr);
				
		        String retcode = JsonUtil.getString(jsonObj, "retcode");
		        String ticket = JsonUtil.getString(jsonObj, "ticket");
		        String uid = JsonUtil.getString(jsonObj, "uid");
		        if(retcode.equals("4049"))
		        {
		        	throw new Exception("'Need Identifying Code");
		        }
		        params.clear();
		        params.put("action", "login");
		        params.put("display","js");
		        params.put("withOfficalFlag", "0");
		        params.put("acwithOfficalAccounttion", "");
		        params.put("quick_auth","null");
		        params.put("scope","");
		        params.put("transport","");
		        params.put("isLoginSina", "");
		        String oathUrl = replaceNewValue(OAUTH_CODE_URL,"client_id",appID);
		        oathUrl = replaceNewValue(oathUrl,"client_secret",appSecret);
		        params.put("regCallback", oathUrl);
		        params.put("response_type","code");
		        params.put("client_id",appID);
		        params.put("ticket",ticket);
		        params.put("userId",userName);
		        params.put("verifyToken","null");
		        params.put("appkey62","oav2R");
		        params.put("state","");
		        params.put("from", "");
		        params.put("passwd","");
		        params.put("redirect_uri","https://api.weibo.com/oauth2/default.html");

		        Map<String, String> oathHeader = new HashMap<String, String>();
		        oathHeader.putAll(header);
		        oathHeader.put("Referer", oathUrl);
		        oathHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
	
		        
		        
				retStr = postMethodRequest(oathUrl,params,oathHeader);
				String jsonStr =getScriptJson(retStr);
				if(jsonStr==null)
				{
					Map<String, String> reOathMap = new HashMap<String, String>();
					String loginUrl = getFirstLoginSoup(retStr,reOathMap);
					if(loginUrl==null)
					{
						throw new Exception("认证返回错误！");
					}
					retStr = postMethodRequest(loginUrl,reOathMap,oathHeader);
					if(retStr==null)
					{
						throw new Exception("认证返回错误,返回的Url有错误！");
					}
					jsonStr =getScriptJson(retStr);
				}
				jsonObj = JsonUtil.getJson(jsonStr);
				String code = JsonUtil.getString(jsonObj, "code");
				
				jsonStr = getToken(appID,appSecret,code);
				if(jsonStr!=null)
				{
					jsonObj = JsonUtil.getJson(jsonStr);
					String token = JsonUtil.getString(jsonObj, "access_token");
					uid = JsonUtil.getString(jsonObj, "uid");
					if(token!=null && uid!=null)
					{
						oathResult = new OathResult();
						oathResult.token=token;
						oathResult.uid=uid;
					}
				}

	
		     }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return oathResult;
	}
	
	private static String getFirstLoginSoup(String content,Map<String, String> reOathMap)
	{
		String loginVal=null;
		try
		{
			JXDocument jxDocument = new JXDocument(content);
			List<Object> rs = jxDocument.sel("//form[@name='authZForm']/input[@type='hidden']");
			for (Object o:rs){
				if (o instanceof Element){
					String name = ((Element) o).attr("name");
					if(name!=null && name.equals("url"))
					{
						loginVal = ((Element) o).attr("value");

					}
					else
					{
						String value = ((Element) o).attr("value");
						reOathMap.put(name, value);
					}
				}
			 }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return loginVal;
	}
	
	private static String getScriptJson(String content)
	{
		int index = content.indexOf("opener.Authorize(");
		if(index==-1)
		{
			return null;
		}
		index+="opener.Authorize(".length();
		int lastIndex = content.indexOf(");}</script>",index);
		if(lastIndex==-1)
		{
			return null;
		}
		return content.substring(index, lastIndex);
	}
	
	public static String getSP(String pwd,String pubkey,String servertime,String nonce) {
		String t = "10001";
		String message = servertime + "\t" + nonce + "\n" + pwd;
		String result = null;
		try {
		result = rsa(pubkey, t , message);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("RSA加密后的密码：" + result);
		return result;
	}
	
	public static String rsa(String pubkey, String exponentHex, String pwd)
	throws IllegalBlockSizeException, BadPaddingException,
		NoSuchAlgorithmException, InvalidKeySpecException,
		NoSuchPaddingException, InvalidKeyException,
		UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		BigInteger m = new BigInteger(pubkey, 16);
		BigInteger e = new BigInteger(exponentHex, 16);
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
		
		//创建公钥
		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);
		
		byte[] encryptedContentKey = enc.doFinal(pwd.getBytes("UTF-8"));
		
		return new String(encodeHex(encryptedContentKey));
	}
	


	public static String convertBASE64Encoder(String src){
		return BASE64Encoder.encode(src.getBytes()).replace('+', '_').replace('/', '-').replace("=", "");
	}


	private static Log log = Context.getLog("newsedit");

	public static String getToken(WeiboServiceProvider provider,String code)throws Exception
	{
		return getToken(provider.getConsumerKey(), provider.getConsumerSecret(), code);

	}
	
	private static String getToken(String appID,String appSecret,String code) throws Exception
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("client_id", appID);
		params.put("client_secret", appSecret);
		params.put("grant_type", "authorization_code");
		params.put("code", code);
		params.put("redirect_uri",redirect_URI);
		return postMethodRequest(accessTokenURL, params, header);
	
	}
	
	public static String getAuthorizeUrl(WeiboServiceProvider weiboServiceProvider )
	{
		String szCrekey = "";
		if(weiboServiceProvider!=null)
		{
			szCrekey = weiboServiceProvider.getConsumerKey();
		}

		  return authorizeURL + "?client_id=" + szCrekey.trim()
		  			+"&client_secret="+weiboServiceProvider.getConsumerSecret()
			       + "&redirect_uri=" + redirect_URI
			      +  "&response_type=code";

	}
	
	public static String getWid(String result) {
		if(result==null)
		{
			return null;
		}
		JSONObject json = JsonUtil.getJson(result);
		if (json != null)
			return json.getString("id"); //wid
		else
			return null;
	}

	// 发布文本微博
	public static String postWeibo(String url, String token, String content) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		
		return postMethodRequest(url, params, header);
	}

	// 上传图片并发布一条新微博
	public static String postWeiboOneImage(String url,String token, String content, String filePath) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		
		Map<String, byte[]> items = new HashMap<String, byte[]>();
		try {
			byte[] targetFileBytes = FileUtils.readFile(filePath);
			items.put("pic", targetFileBytes);

			return postMethodRequestWithFile(url, params, header, items);
		} catch (IOException e) {
			log.error(e);
			return null;
		}
	}

	// 发布微博，带多个图片
	public static String postWeiboMoreImages(String url,String token, String content,
			String picIDs) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("status", content);
		params.put("pic_id", picIDs);
		
		return postMethodRequest(url, params, header);
	}

	// 上传图片
	public static String uploadImage(String url,String token, String filePath) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
	
		Map<String, byte[]> items = new HashMap<String, byte[]>();
		try {
			byte[] targetFileBytes = FileUtils.readFile(filePath);
			items.put("pic", targetFileBytes);
	
			return postMethodRequestWithFile(url, params, header, items);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	// 上传图片
	public static String uploadImage(String url,String token, InputStream in) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
	
		Map<String, byte[]> items = new HashMap<String, byte[]>();
		try {
			byte[] targetFileBytes = getOutBytes(in);
			if(targetFileBytes==null)
			{
				throw new Exception("没有读到图片！");
			}
			items.put("pic", targetFileBytes);
	
			return postMethodRequestWithFile(url, params, header, items);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	private static byte [] getOutBytes(InputStream in)throws Exception
	{
		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		byte [] outBytes = null;
        try
        {
			byte[] buffer = new byte[64*1024];
	        for(;;)
	        {
	            int count = in.read(buffer);
	            if (count < 0)
	                break;
	            os.write(buffer,0,count);
	         }
	        outBytes = os.toByteArray();
        }
        finally
        {
        	ResourceMgr.closeQuietly(os);
        }
        return  outBytes;
	}

	// 删除微博
	public static String deleteWeibo(String url,String token, String wid) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		
		return postMethodRequest(url, params, header);
	}
	
	// 发布评论
	public static String postComment(String url,String token, String wid, String comment) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("comment", comment);
		
		return postMethodRequest(url, params, header);
	}

	// 发布评论回复
	public static String postReply(String url,String token, String wid, String cid, String comment) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("cid", cid);
		params.put("comment", comment);
		
		return postMethodRequest(url, params, header);
	}

	//得到微博的转发数和评论数
	public static String getCount(String url,String token, String ids) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("ids", ids);
		
		return getMethodRequest(url, params, header);
	}
	
	//得到微博的转发列表（某一页）
	public static String getReposts(String url,String token, String wid, int page, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("page", String.valueOf(page));
		params.put("count", String.valueOf(count));
		
		return getMethodRequest(url, params, header);
	}

	//得到微博的评论列表（某一页）
	public static String getComments(String url,String token, String wid, int page, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("id", wid);
		params.put("page", String.valueOf(page));
		params.put("count", String.valueOf(count));
		
		return getMethodRequest(url, params, header);
	}
	//长链接转换成短链接
	public static String shorten(String url,String token,List<String> urlList){
		try {
			StringBuffer sbf = new StringBuffer(url);
			sbf.append("access_token="+token);
			for(String longUrl:urlList){
				sbf.append("&url_long="+longUrl);
			}
			GetMethod pm = new GetMethod(sbf.toString());

			setHeaderParams(pm, header);

			return post(pm);
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	private static String postMethodRequest(String url,
		Map<String, String> params, Map<String, String> header) throws Exception {
		PostMethod pm = new PostMethod(url);
		setHeaderParams(pm, header);

		setParams(pm, params);
		
		return post(pm);
	}

	private static String postMethodRequestWithFile(String url,
			Map<String, String> params, Map<String, String> header,
			Map<String, byte[]> itemsMap) {

		try {
			PostMethod pm = new PostMethod(url);
			setHeaderParams(pm, header);
			
			Part[] parts = assembleParts(itemsMap, params);
			
			pm.setRequestEntity(new MultipartRequestEntity(parts, pm.getParams()));
			
			return post(pm);

		} catch (Exception e) {
			log.error(e);
			return "";
		}
	}

	private static String getMethodRequest(String url,
			Map<String, String> params, Map<String, String> header) {
		try {
			url = addUrlParams(url, params);
			
			GetMethod pm = new GetMethod(url);
			
			setHeaderParams(pm, header);
			
			return post(pm);
		} catch (Exception e) {
			log.error(e);
		}
		return null;
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
		} else if (statusCode ==HttpStatus.SC_FORBIDDEN) {
			throw new E5Exception(statusCode, "拒绝访问！微博服务器不允许频繁访问。");
		}
		else if(statusCode == HttpStatus.SC_BAD_REQUEST)
		{
			throw new E5Exception(statusCode, "你做了新浪微博服务器不支持的操作，如:超过了新浪事先规定的字数。");
			
		}
		 else {
			throw new E5Exception(statusCode, "访问失败，error_code:" + statusCode);
		}
	}

/*	//提交url请求，根据配置决定是否使用网络代理
	private static int sendRequestWithProxy(HttpMethod method) throws HttpException, IOException, E5Exception{
		HttpClient hc = new HttpClient();
		
		WebProxy webProxy = getWebProxy();
		if (webProxy.isStart()){
			hc.getHostConfiguration().setProxy(webProxy.getServerAddress(), webProxy.getPort());
			if (webProxy.isUserAuthentication()) {
		        hc.getParams().setAuthenticationPreemptive(true);
		        hc.getState().setProxyCredentials(AuthScope.ANY
		        		, new UsernamePasswordCredentials(webProxy.getUserCode(),webProxy.getUserPwd()));
			}
		}
	    
	    return sendRequest(hc, method);
	}*/
	//提交url请求
	private static int sendRequest(HttpClient hc, HttpMethod method) throws HttpException, IOException{
        hc.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        
        HttpClientParams httparams = new HttpClientParams();
        httparams.setSoTimeout(30000);
        
        method.setParams(httparams);
        
        return hc.executeMethod(method);
	}
/*	
    *//** 读代理服务器设置 *//*
	private static WebProxy getWebProxy() {
		return NetworkConfigHelper.getWebProxy();
	}
*/	
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

	protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
	final int l = data.length;
	final char[] out = new char[l << 1];
	
	for (int i = 0, j = 0; i < l; i++) {
	out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
	out[j++] = toDigits[0x0F & data[i]];
	}
	return out;
	}
	
	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
	return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}
	
	public static char[] encodeHex(final byte[] data) {
	return encodeHex(data, true);
	}
}
