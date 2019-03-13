package com.founder.xy.ueditor.proof;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


public class HTTPRequest {
	
	public URL url;
	private int timeout = 30000;
	private String cookie;
	private String SOAPAction;
	private String referer;
	private String postData;
	private String useragent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
	private String contenttype = "text/xml; charset=utf-8";
	private Proxy proxy;
	private boolean setFollowRedirects = true; 
	private BufferedReader reader;
	private DataOutputStream writer;
	private HttpURLConnection connection;
	private Set<Entry<String, List<String>>> lastConnectionHeaders;
	
	public HTTPRequest(URL url) {
		this.url = url;
	}
	
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public void setSOAPAction(String SOAPAction) {
		this.SOAPAction = SOAPAction;
	}
	
	public void setPostData(String postData) {
		this.postData = postData;
	}
	
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
	
	public Set<Entry<String, List<String>>> getLastConnectionHeaders() {
		return lastConnectionHeaders;
	}
	
	public void setFollowRedirects(boolean setFollowRedirects) {
		this.setFollowRedirects = setFollowRedirects;
	}
	
	
	public String sendPost(String url, String param,boolean isproxy) {
        OutputStreamWriter out = null;
        InputStreamReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = null;
            if(isproxy){

            }else{
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");    
             
            conn.addRequestProperty("SOAPAction", this.SOAPAction);
            conn.setRequestProperty("user-agent",this.useragent);
            conn.setRequestProperty("Content-Type", this.contenttype);
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.connect();
             
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(param);
            out.flush();
            in = new InputStreamReader(conn.getInputStream(),"UTF-8");
            int tmp;
            while((tmp = in.read()) != -1){
            	result += (char)tmp;
            }
//            String line;
//            while ((line = in.readLine()) != null) {
//                result = result + line;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
	
	
	private void setup() throws Exception {
		if(proxy != null)
			connection = (HttpURLConnection) url.openConnection(proxy);
		else
			connection = (HttpURLConnection) url.openConnection();

		if(cookie != null)
			connection.setRequestProperty("Cookie", cookie);
		if(referer != null)
			connection.addRequestProperty("Referer", referer);
		if(SOAPAction != null)
			connection.addRequestProperty("SOAPAction", SOAPAction);
		
		connection.setRequestProperty("User-Agent", useragent);
		connection.setRequestProperty("Content-Type", contenttype);
		connection.setReadTimeout(timeout);
		connection.setConnectTimeout(timeout);
		connection.setUseCaches(false);

	    HttpURLConnection.setFollowRedirects(setFollowRedirects);
		
		if(postData != null) {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			writer = new DataOutputStream(connection.getOutputStream());
			writer.write(postData.getBytes());
			writer.flush();
		}

		reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	}
	
	public String[] read() throws Exception {
		ArrayList<String> st;
		
		try {
			setup();
			
			st = new ArrayList<String>();
			String s;
			while((s = reader.readLine()) != null)
				st.add(s);
			
			lastConnectionHeaders = connection.getHeaderFields().entrySet();
		} catch(Exception e) {
			cleanup();
			throw e;
		} finally {
			cleanup();
		}
		
		return st.toArray(new String[st.size()]);
	}
	  
	public String[] read(int linesToRead) throws Exception {
		ArrayList<String> st;
		
		try {
			setup();
	
			st = new ArrayList<String>();
			for(int i = 0; i < linesToRead; i++) {
				String s = reader.readLine();
				if(s != null)
					st.add(s);
			}
			
			lastConnectionHeaders = connection.getHeaderFields().entrySet();
		} catch(Exception e) {
			cleanup();
			throw e;
		} finally {
			cleanup();
		}
		
		return st.toArray(new String[st.size()]);
	}
	
	public String readSingle() throws Exception {
		String s;
		
		try {
			setup();
			
			s = reader.readLine();
			
			lastConnectionHeaders = connection.getHeaderFields().entrySet();
		} catch(Exception e) {
			cleanup();
			throw e;
		} finally {
			cleanup();
		}
		
		return s;
	}
	
	public String readSingle(int linesToRead) throws Exception {
		String s;
		
		try {
			setup();
			
			for(int i = 0; i < linesToRead-1; i++)
				reader.readLine();
			
			s = reader.readLine();
			
			lastConnectionHeaders = connection.getHeaderFields().entrySet();
		} catch(Exception e) {
			cleanup();
			throw e;
		} finally {
			cleanup();
		}
		
		return s;
	}
	
	private void cleanup() {
		try { reader.close(); } catch(Exception e) {}
		try { writer.close(); } catch(Exception e) {}
		try { connection.disconnect(); } catch(Exception e) {}
		reader = null;
		writer = null;
		connection = null;
	}

}
