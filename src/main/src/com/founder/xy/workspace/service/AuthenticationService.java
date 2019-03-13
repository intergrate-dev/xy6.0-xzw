package com.founder.xy.workspace.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;

public class AuthenticationService {

	static Properties pros = null; // 可以帮助读取和处理资源文件中的信息

	static { // 加载JDBCUtil类的时候调用
		pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("checkcode.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String requestData(String phone, String inputCode) {
		// httpURL：接口地址； appkey：APPKey； zone：国家代码；
		String httpSendURL = pros.getProperty("httpSendURL");
		String httpCheckURL = pros.getProperty("httpCheckURL");
		String appkey = pros.getProperty("appkey");
		String zone = pros.getProperty("zone");
		String result = null;
		String params = "appkey=" + appkey + "&zone=" + zone + "&phone="
				+ phone;
		// inputCode为空则发送验证码，不为空则校验验证码
		if (StringUtils.isBlank(inputCode)) {
			result = sendCode(httpSendURL, params);
		} else {
			params = params + "&code=" + inputCode;
			result = checkCode(httpCheckURL, params);
		}

		return result;
	}

	private static String checkCode(String address, String params) {
		HttpURLConnection conn = null;
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
				public X509Certificate[] getAcceptedIssuers(){return null;}
				public void checkClientTrusted(X509Certificate[] certs, String authType){}
				public void checkServerTrusted(X509Certificate[] certs, String authType){}
			}};
	 
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
	 
			//ip host verify
			HostnameVerifier hv = new HostnameVerifier() {
				 public boolean verify(String urlHostName, SSLSession session) {
				 return urlHostName.equals(session.getPeerHost());
				 }
			};
	 
			//set ip host verify
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
	 
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 
			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// POST
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			// set params ;post params 
			if (params!=null) {
				conn.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.write(params.getBytes(Charset.forName("UTF-8")));
				out.flush();
				out.close();
			}
			conn.connect();
			//get result 
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String result = parsRtn(conn.getInputStream());
				return result;
			} else {
				System.out.println(conn.getResponseCode() + " "+ conn.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

	private static String sendCode(String address, String params) {
		HttpURLConnection conn = null;
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());

			// ip host verify
			HostnameVerifier hv = new HostnameVerifier() {
				@Override
				public boolean verify(String urlHostName, SSLSession session) {
					return urlHostName.equals(session.getPeerHost());
				}
			};

			// set ip host verify
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// POST
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			// set params ;post params
			if (params != null) {
				conn.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(
						conn.getOutputStream());
				out.write(params.getBytes(Charset.forName("UTF-8")));
				out.flush();
				out.close();
			}
			conn.connect();
			// get result
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String result = parsRtn(conn.getInputStream());
				return result;
			} else {
				System.out.println(conn.getResponseCode() + " "
						+ conn.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

	private static String parsRtn(InputStream inputStream) {
		String result = "";
		// 定义 BufferedReader输入流来读取URL的响应
		BufferedReader in = null;
		String line;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
