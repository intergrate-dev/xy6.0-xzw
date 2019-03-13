package com.founder.xy.commons.web;

import com.founder.xy.commons.InfoHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/*
 * 进行httpClient的post请求
 */
public class HttpClientUtil {
    public static String doGet(String url){
        return doRequest(new HttpGet(url));
    }

    private static String doRequest(HttpRequestBase request) {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        HttpEntity entity = null;
        try {
            client = getClient();
            if (client == null) {
                client = HttpClientBuilder.create().build();
            }
            response = client.execute(request);
            entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            EntityUtils.consumeQuietly(entity);
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(client);
        }
        return null;
    }

    private static CloseableHttpClient getClient() {
        try {
            if ("是".equals(InfoHelper.getConfig("写稿", "启用代理服务器"))) {
                String ip = InfoHelper.getConfig("写稿", "代理服务器");
                if (!StringUtils.isBlank(ip)) {
                    String port = InfoHelper.getConfig("写稿", "代理服务器端口");
                    if (StringUtils.isBlank(port))
                        port = "80";
                    String username = InfoHelper.getConfig("写稿", "代理服务器用户名");
                    if (!StringUtils.isBlank(username)) {
                        String password = InfoHelper.getConfig("写稿", "代理服务器密码");
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        AuthScope authscope = new AuthScope(ip, Integer.parseInt(port));
                        Credentials credentials = new UsernamePasswordCredentials(username, password);
                        credentialsProvider.setCredentials(authscope, credentials);
                        return HttpClients.custom().setDefaultCredentialsProvider(
                                credentialsProvider).build();
                    } else {
                        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
                        return HttpClients.custom().setProxy(proxy).build();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(doGet("http://www.baidu.com"));
    }

}