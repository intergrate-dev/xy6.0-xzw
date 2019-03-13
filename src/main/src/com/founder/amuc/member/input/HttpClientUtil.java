package com.founder.amuc.member.input;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.founder.amuc.commons.BaseHelper;
import com.founder.e5.commons.Log;

public class HttpClientUtil {
	
	private Log log = com.founder.e5.context.Context.getLog("amuc.api");
	
	public static String doPost(String url,List<NameValuePair> list,String charset){
		String result = null;  
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// httppost    
        HttpPost httppost = new HttpPost(url);
        UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
			httppost.setEntity(uefEntity);  
	        CloseableHttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        if(entity != null){  
	            result = EntityUtils.toString(entity,charset);  
	        }
	        response.close();
	        httpclient.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return result;
	}
	
	public String callSsoAPI(String api,List<NameValuePair> params){
		String ssoUrl = BaseHelper.getConfig(1, "推广", "SSO根地址"); 
		String url = ssoUrl + api;
		String charset = "utf-8";
		params.add(new BasicNameValuePair("fromAMUC","true"));
		String result = doPost(url,params,charset); 
		log.info(String.format("同步sso用户表返回结果：%s", result));
		return result;
	}
}