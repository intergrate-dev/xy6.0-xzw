package com.founder.xy.system.check;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceHelper;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.xy.article.web.ArticleServiceHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class SystemCheckManager {
    public JSONObject initCheckParam() {
        JSONObject param = new JSONObject();
        try {
            String conditions = "a_status=1 AND a_realPubTime = ("
                    + " SELECT MAX(a_realPubTime) FROM " + LibHelper.getArticleAppLib().getDocLibTable()
                    + " WHERE a_status=1)";

            DocumentManager docManager = DocumentManagerFactory.getInstance();

            param.put("ROOTURL", UrlHelper.apiUrl());

            Document article = getDoc(docManager, LibHelper.getArticleAppLibID(), conditions, null); //得到最新发布的稿件
            param.put("siteID", article != null ? article.getInt("a_siteID") : 0); //站点ID
            param.put("colID", article != null ? article.getLong("a_columnID") : 0); //栏目ID
            param.put("articleID", article != null ? article.getDocID() : 0); //测试稿件ID
            param.put("title", article != null ? article.getTopic() : ""); //稿件标题

            Document member = docManager.get(LibHelper.getLib(DocTypes.MEMBER.typeID(), Tenant.DEFAULTCODE).getDocLibID(), 1);
            param.put("userID", member != null ? member.getDocID() : 0); //会员ID
            param.put("userName", member != null ? member.get("mName") : 0);

            Document live = getDoc(docManager, getDocLibID(DocTypes.LIVE.typeID()), "a_status=? AND SYS_DELETEFLAG = 0", new Object[]{1});
            param.put("liveID", live != null ? live.getDocID() : 0); //直播话题ID

            Document activity = getDoc(docManager, getDocLibID(DocTypes.ACTIVITY.typeID()), "a_status=? AND SYS_DELETEFLAG = 0", new Object[]{1});
            param.put("activeID", activity != null ? activity.getDocID() : 0); //活动ID

            Document paper = getDoc(docManager, getDocLibID(DocTypes.PAPER.typeID()), "SYS_DELETEFLAG=0 AND pa_status=0", null);
            param.put("paperID", paper != null ? paper.getDocID() : 0); //数字报ID

            Document paperDate = getDoc(docManager, getDocLibID(DocTypes.PAPERDATE.typeID()), " pd_status=1 AND SYS_DELETEFLAG=0", null);
            param.put("paperDate", DateUtils.format(paperDate != null ? paperDate.getDate("pd_date") : new java.util.Date(), "yyyyMMdd")); //数字报 最近期次

            Document paperArticle = getDoc(docManager, getDocLibID(DocTypes.PAPERARTICLE.typeID()), "a_status<7 and SYS_DELETEFLAG=0", null);
            param.put("paperArticleID", paperArticle != null ? paperArticle.getDocID() : 0); //数字报某一篇稿件ID

            param.put("content", "评论"); //评论内容

            return param;
        } catch (Exception e) {
            e.printStackTrace();
            return param;
        }
    }

    /**
     * solr
     */
    public JSONObject testSolr(SolrServer server) {
        JSONObject obj = new JSONObject();

        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("fl", "SYS_DOCUMENTID");
        params.set("q", "*:*");
        params.set("fq", "SYS_DOCUMENTID:[0 TO *]");

        try {
            QueryResponse queryResponse = server.query(params);
            SolrDocumentList list = queryResponse.getResults();
            obj.put("result", list != null ? 0 : 1);
        } catch (Exception e) {
            obj.put("result", 1);
        }
        String url = InfoHelper.getConfig("全文检索", "全文检索服务URL地址");
        if(!url.endsWith("/"))
        	url = url + "/";
        obj.put("value", url);
        return obj;
    }

    public JSONObject testAnalyzeServer(String method, String item, String content) throws UnsupportedEncodingException {
        JSONObject obj = new JSONObject();
        String url = InfoHelper.getConfig("写稿服务", item);
        String size = "";
        if ("摘要提取服务".equals(item)) {
            url = UrlHelper.summaryServiceUrl();
            size = size = InfoHelper.getConfig("写稿服务", "摘要提取语句数");
        }else {
            url = UrlHelper.keyWordsServiceUrl();
            size = size = InfoHelper.getConfig("写稿服务", "关键词提取个数");
        }

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("method", method));
        nvps.add(new BasicNameValuePair("content", content));
        nvps.add(new BasicNameValuePair("keyWordSize", size));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        JSONObject json = ArticleServiceHelper.executeHttpRequest(httpPost, true);
        String status = json.getString("status");
        obj.put("result", "success".equals(status) ? 0 : 1);
        obj.put("value", url);
        obj.put("item", item);
        return obj;
    }

    /**
     * 互动
     */
    public JSONObject testNisUrl(String url) {
        JSONObject obj = new JSONObject();
        if (url != null && !"".endsWith(url)) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(1000);
                conn.connect();
                obj.put("status", conn.getResponseCode());
                obj.put("result", conn.getResponseCode() == 200 ? 0 : 1);
            } catch (IOException e) {
                obj.put("result", 1);
                e.printStackTrace();
            }
            obj.put("value", url);
        } else {
            obj.put("value", "无参数");
            obj.put("result", 0);
        }
        return obj;
    }

    /**
     * 存储设备
     */
    public JSONObject testDevice(String project, String item) {
        JSONObject obj = new JSONObject();
        StorageDevice device = InfoHelper.getDevice(project, item);
        String savePath = getSavePath();
        obj.put("result", uploadFile(device, getTestFilePath(), savePath) && exists(device, savePath) ? 0 : 1);
        obj.put("value", device == null ? "无参数" : device.getDeviceName());
        obj.put("item", item);
        return obj;
    }

    private boolean exists(StorageDevice device, String rltPath) {
        int deviceType = device.getDeviceType();
        switch (deviceType) {
            case 1:
                return new File(device.getNfsDevicePath() + File.separator + rltPath).exists();
            case 2:
                return new File(device.getNtfsDevicePath() + File.separator + rltPath).exists();
            case 3:
                try {
                    String hostName = StorageDeviceHelper.getHostName(device);
                    String path = StorageDeviceHelper.getWholeFtpPath(device, rltPath);
                    int port = StorageDeviceHelper.getFtpPort(device);
                    FTPClient client = new FTPClient();
                    client.setDefaultPort(port);
                    client.connect(hostName);
                    client.login(device.getUserName(), device.getUserPassword());
                    return client.changeWorkingDirectory(path);
                } catch (Exception e1) {
                    return false;
                }
            case 4:
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con = (HttpURLConnection) new URL(device.getHttpDeviceURL() + "/" + rltPath).openConnection();
                    con.setRequestMethod("HEAD");
                    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (Exception e) {
                    return false;
                }
        }
        return false;
    }

    public JSONObject executeHttpPostRequest(String url, JSONObject param) {
        JSONObject json = new JSONObject();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();

            if (null != param) {
                @SuppressWarnings("unchecked")
                Iterator<String> iter = param.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    nvps.add(new BasicNameValuePair(key, param.getString(key)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            httpResponse = httpclient.execute(httpPost);
            int status = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();

            json.put("status", status); //状态码
            if (entity != null) { // API返回结果
                String result = IOUtils.toString(entity.getContent(), "UTF-8");
                json.put("result", result);
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(httpResponse);
        }
        return json;
    }

    public void handleListResult(JSONObject obj, JSONObject json, String tag, int docTypeID, String conditions, Object[] params) {
        if (json.getInt("status") == 200) {
            try {
                JSONArray list = null;
                if ("".equals(tag)) {
                    list = json.getJSONArray("result");
                } else {
                    list = json.getJSONObject("result").getJSONArray(tag);
                }
                if (list.isEmpty()) {
                    DocLib docLib = LibHelper.getLib(docTypeID, Tenant.DEFAULTCODE);
                    int docLibID = docLib.getDocLibID();
                    DocumentManager docManager = DocumentManagerFactory.getInstance();
                    try {
                        int length = docManager.find(docLibID, conditions, params).length;
                        obj.put("result", length > 0 ? 1 : 0);
                    } catch (E5Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    obj.put("result", 0);
                }
            } catch (Exception e) {
                obj.put("result", 1); //异常
            }
        }
    }

    public String prepareTestFile() {
        StorageDevice device = InfoHelper.getWaterMarkDevice();
        uploadFile(device, getTestFilePath(), getSavePath());
        return InfoHelper.getDevicePath(device) + File.separator + getSavePath();
    }

    public boolean uploadFile(StorageDevice device, String targetPath, String savePath) {
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(targetPath));
            sdManager.write(device, savePath, is);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) {
                ResourceMgr.closeQuietly(is);
            }
        }
    }

    private Document getDoc(DocumentManager docManager, int docLibID, String conditions, Object[] params) {
        try {
            Document[] doc = docManager.find(docLibID, conditions, params);
            if (doc != null && doc.length > 0) {
                return doc[0];
            }
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getDocLibID(int typeID){
        return LibHelper.getLib(typeID, Tenant.DEFAULTCODE).getDocLibID() ;
    }

    private String getSavePath() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String savePath = InfoHelper.getPicSavePath(request) + UUID.randomUUID() + ".png";
        return savePath;
    }

    private String getTestFilePath() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String testFilePath = request.getSession().getServletContext().getRealPath("/") + "xy" + File.separator + "img" + File.separator + "test.png";
        return testFilePath;
    }
}
