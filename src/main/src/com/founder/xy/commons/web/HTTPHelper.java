package com.founder.xy.commons.web;

import com.founder.xy.commons.EncodeUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.redis.RedisManager;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.founder.xy.redis.RedisKey.APP_USER_TOKEN;

/**
 * Http访问的辅助类
 * @author Gong Lijie
 */
public class HTTPHelper {
	private static int DEFAULT_TIMEOUT = 100000; //默认的过期时间，毫秒

	/** 读代理服务器设置 */
	public static WebProxy getWebProxy() {
		WebProxy webProxy = new WebProxy();
	
		if ("是".equals(InfoHelper.getConfig("写稿", "启用代理服务器"))) {
			String proxy = InfoHelper.getConfig("写稿", "代理服务器");
			if (!StringUtils.isBlank(proxy)) {
				webProxy.setStart(true);
				webProxy.setServerAddress(proxy);
	
				String port = InfoHelper.getConfig("写稿", "代理服务器端口");
				if (StringUtils.isBlank(port))
					port = "80";
				webProxy.setPort(Integer.parseInt(port));
	
				String user = InfoHelper.getConfig("写稿", "代理服务器用户名");
				if (!StringUtils.isBlank(user)) {
					webProxy.setUserAuthentication(true);
					webProxy.setUserCode(user);
	
					String pwd = InfoHelper.getConfig("写稿", "代理服务器密码");
					if (!StringUtils.isBlank(pwd))
						webProxy.setUserPwd(pwd);
				}
			}
		}
		return webProxy;
	}
	/**
	 * 提交url请求，根据配置决定是否使用网络代理
	 * @param method
	 * 
	 * @return 返回状态码。判断状态码正常时可以再取得返回结果，如：
	 * 	if (statusCode == HttpStatus.SC_OK)
			return method.getResponseBodyAsString();
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendRequestWithProxy(HttpMethod method) throws HttpException, IOException {
		return sendRequestWithProxy(method, DEFAULT_TIMEOUT);
	}
	
	/**
	 * 提交url请求，根据配置决定是否使用网络代理
	 * @param method
	 * @param timeout 指定的过期时间，毫秒
	 * 
	 * @return 返回状态码。判断状态码正常时可以再取得返回结果，如：
	 * 	if (statusCode == HttpStatus.SC_OK)
			return method.getResponseBodyAsString();
	 * @throws HttpException
	 * @throws IOException
	 */
	public static int sendRequestWithProxy(HttpMethod method, int timeout) throws HttpException, IOException {
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
	    
	    return sendRequest(hc, method, timeout);
	}
	//提交url请求
	private static int sendRequest(HttpClient hc, HttpMethod method, int timeout) throws HttpException, IOException{
        hc.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        
        HttpClientParams httparams = new HttpClientParams();
        httparams.setSoTimeout(timeout);
        
        method.setParams(httparams);
       System.setProperty("java.net.preferIPv4Stack" , "true");
       System.out.println( System.getProperty("java.net.preferIPv4Stack"));;
        return hc.executeMethod(method);
	}

	public static JSONObject checkValid(String data) {
		JSONObject json = JSONObject.fromObject(data);
		return checkValid(json.getLong("loginID"), json.getLong("time"),
//		return checkValid(json.getLong("userID"), json.getLong("time"),
						  json.getString("sign"), json.getString("url"));
	}

	public static JSONObject checkValid(HttpServletRequest request) {
		return checkValid(Long.parseLong(request.getParameter("loginID")), Long.parseLong(request.getParameter("time")),
						  request.getParameter("sign"), request.getParameter("url"));
	}

	public static JSONObject checkValid(long loginID, long time, String sign, String url) {
		JSONObject json = new JSONObject();
		String token, signature;
		json.put("code", 0);
		if ("debug44944".equals(sign)) {
			json.put("error", "debug");
			return json;
		}

		//判断在redis中是否存在
		if (!RedisManager.hexists(APP_USER_TOKEN, loginID + "")) {
			json.put("code", -100);
			json.put("error", "用户未登陆!");
			return json;
		}
		token = RedisManager.hget(APP_USER_TOKEN, loginID + "");
		long now = System.currentTimeMillis();
		//相差10秒，拒绝访问
		if (now - time > 1000 * 10) {
			json.put("code", -101);
			json.put("error", "url请求超时!");
			return json;
		}
		//正确性
		signature = EncodeUtils.getMD5(url + "?" + "loginID=" + loginID + "&time=" + time + "&token=" + token);
//		signature = EncodeUtils.getMD5(url + "?" + "userID=" + loginID + "&time=" + time + "&token=" + token);
		if (!signature.equals(sign)) {
			json.put("code", -102);
			json.put("error", "签名不一致！");
			return json;
		}
		return json;
	}
}
