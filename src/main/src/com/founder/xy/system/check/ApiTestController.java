package com.founder.xy.system.check;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.xy.commons.InfoHelper;

import net.sf.json.JSONObject;

/**
 * 外网api测试
 */
@SuppressWarnings({ "deprecation", "resource", "unchecked" })
@Controller
@RequestMapping("/xy/system/")
public class ApiTestController {
	
	/**post提交*/
	@RequestMapping(value = "commit.do")
	public void commit(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = JSONObject.fromObject(request.getParameter("data"));
		String type = json.getString("_type");
		String result = "";
		try {
			if("post".equals(type)){
				result = postData(request.getParameter("url"), json);
				InfoHelper.outputText(result, response);
			}else if("get".equals(type)){
				result = postData(json.getString("url"), null);
				InfoHelper.outputJson(result, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("data is:" + json.toString());
		}
	}
	
	private static String postData(String url, JSONObject json) throws Exception {
		String strEntity = null;
		HttpParams param = new BasicHttpParams();
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setDefaultMaxPerRoute(50);
		cm.setMaxTotal(50);
		HttpPost request = new HttpPost();
		HttpResponse response = null;
		HttpClient httpClient = null;
		if (null != json) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Iterator<String> iter = json.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				params.add(new BasicNameValuePair(key, json.getString(key)));
			}
			request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		}
		try {
			request.setURI(new URI(url));
			httpClient = new DefaultHttpClient(cm, param);
			response = httpClient.execute(request);

			if (200 == response.getStatusLine().getStatusCode()) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) 
					strEntity = EntityUtils.toString(responseEntity, "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			request.abort();
			httpClient.getConnectionManager().shutdown();
		}
		if(json == null) return strEntity;
		boolean canGetData = false;
		try {
			canGetData = Boolean.parseBoolean(strEntity);
		} catch (Exception e) {
			canGetData = false;
		}
		return canGetData ? "提交成功！":"提交失败！";
	}
}
