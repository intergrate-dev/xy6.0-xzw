package com.founder.xy.set.web;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.xy.commons.UrlHelper;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词/非法词的相关操作
 * 规则的增加，删除，敏感词/非法词的过滤
 * method:
 *      1.add(添加规则)、
 *      2.delete(根据规则ID、任务类型删除规则)、
 *      3.deleteAll(根据任务类型删除该任务类型下所有的规则)、
 *      4.checkSensitive(检查敏感词/非法词);
 *      5.batchAdd(本地批量导入规则)
 * )
 * code:
 *      0:  操作失败,
 *      1:  操作成功,
 *      2:  其他异常：没有找到敏感词/非法词;
 * type:
 *      type==0 表示是写稿,
 *      type==1 表示是评论;
 * data:
 *      是查询敏感词/非法词的时候的敏感词/非法词信息(
 *      SensitiveWord:敏感词
 *      IllegalWord：非法词
 *      keyNum: 命中的规则在文本中对应的命中的敏感词/非法词的个数,
 *      keywords:命中的敏感词/非法词,
 *      ruleId:规则的id,
 *      position:命中的敏感词/非法词在文中的位置,index从0开始,若是单字,则返回单字位置,比如：1-1;
 *  )
 */
public class SensitiveWordControllerHelper {
    private final static String SEVER_UNCOMMON = "服务异常！";

    private static String url = "";
    private static String[] addArray = {"method", "rule", "ruleId", "taskType"};//规则的添加,method="add"
    private static String[] batchAddArray = {"method", "rules"};//规则的批量添加,method="batchAdd";
    private static String[] deleteArray = {"method", "ruleIds", "taskTypes"};//删除敏感词,method="delete";
    private static String[] allDelArray = {"method", "taskType"};//删除该任务类型下所有的规则,method="deleteAll";
    private static String[] checkArray = {"method", "type", "title", "content"};//检查敏感词/非法词,method="checkSensitive";
    private static String[] changeArray = {"method", "rule", "ruleId", "taskType"};//检查敏感词/非法词,method="checkSensitive";

    /**
     * 敏感词相关操作的入口
     * 不定长度参数列表  不同的方法名对应不同的参数数组。依次见上面的数组
     * 调用方法时，参数列表要按照数组一一对应
     * @param args
     * @return
     */
    public static String sensitive(String ... args) {
        JSONObject json = new JSONObject();
        try {
            url = UrlHelper.sensitiveServiceUrl();
            if (!StringUtils.isBlank(url)) {
                json = accessSensitiveService(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", "-1");
            json.put("inform", SEVER_UNCOMMON);
        } finally {
            return json.toString();
        }
    }

    private static JSONObject accessSensitiveService(String ... args) throws Exception {
        // 1. 组织成 post 格式的数据
        HttpPost httpPost = assembleData(args);
        // 2. a. 发送数据并获得response; b.处理获得的数据，并返回一个json对象
        JSONObject json = executePost(httpPost);
        // 3. 处理返回来的json数据
        return handleResult(args[0], json);
    }

    private static HttpPost assembleData(String ... args) throws Exception {
        String[] array;
        String method = args[0].toLowerCase();
        if (method.equals("add"))
            array = addArray;
        else if (method.equals("delete"))
            array = deleteArray;
        else if (method.equals("deleteall"))
            array = allDelArray;
        else if (method.equals("batchadd"))
            array = batchAddArray;
        else if(method.equals("change"))
            array=changeArray;
        else
            array = checkArray;
        // 初始化一个post对象
        HttpPost httpPost = new HttpPost(url);
        // 封装参数列表
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        int i = 0;
        for (String str : args){
            nvps.add(new BasicNameValuePair(array[i], str));
            i++;
        }
        // 封装成form对象
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        return httpPost;
    }

    /**
     * 处理获得的数据，并返回一个json对象
     */
    public static JSONObject executePost(HttpPost httpPost) throws IOException {
        // 初始化 client端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 发送数据，并获得response
        CloseableHttpResponse httpResponse = null;
        JSONObject resultJson = null;
        try {
            httpResponse = httpclient.execute(httpPost);
            // 从response中获得对象
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                // 转成UTF-8格式的字符串
                String result = IOUtils.toString(entity.getContent(), "UTF-8");
                resultJson = JSONObject.fromObject(result);
                // 销毁对象
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(httpResponse);
        }
        return resultJson;
    }

    /**
     * 处理返回来的json数据
     */
    @SuppressWarnings("unchecked")
    private static JSONObject handleResult(String method, JSONObject json) throws Exception {
        JSONObject resJson = new JSONObject();
        if (json == null) {
            resJson.put("code", "0");
            resJson.put("inform", "The resJson is null");
        }else {
            // 获得状态
            String status = json.getString("status");
            if ("0".equals(status) || "3".equals(status)) {
                //出错
                resJson.put("code", "0");
                resJson.put("inform", json.getString("inform"));
            } else {
                //如果是敏感词检测操作，获取它的值
                method = (method == null ? "" : method.toLowerCase());
                if ("checksensitive".equals(method)) {
                    if ("2".equals(status)) {
                        resJson.put("code", "2");
                    } else {
                        resJson.put("code", "1");
                        JSONObject ja = JSONObject.fromObject(json.getString("data"));
                        resJson.put("sensitiveWord", ja.get("sensitiveWord"));
                        resJson.put("illegalWord", ja.get("illegalWord"));
                    }
                } else
                    resJson.put("code", "1");
            }
        }
        return resJson;
    }
}