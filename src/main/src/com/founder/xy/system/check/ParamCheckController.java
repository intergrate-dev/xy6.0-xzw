package com.founder.xy.system.check;

import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceReader;
import com.founder.e5.web.WebUtil;
import com.founder.xy.article.web.ArticleServiceHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.commons.web.HTTPHelper;
import com.founder.xy.commons.web.WebProxy;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.set.web.SensitiveWordControllerHelper;
import com.founder.xy.system.site.SiteSolrServerCache;
import com.founder.xy.ueditor.Params;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/xy/system/")
public class ParamCheckController {
	@Autowired
	private SystemCheckManager checkManager ;
	
	private JSONObject param = new JSONObject();
	
	@RequestMapping("initParam.do")
	public void initCheckParam(HttpServletRequest request,HttpServletResponse response) {
		setSiteID(WebUtil.getInt(request, "siteID", 1));
		InfoHelper.outputJson(String.valueOf(getSiteID()),response);
	}
	
	@RequestMapping(value="getParamNames.do")
	public void getParamNames(HttpServletRequest request,HttpServletResponse response){
		JSONArray arr = new JSONArray() ;
		Method[] methods = this.getClass().getDeclaredMethods() ;
		for(int i = 0; i < methods.length ; i++){
			String methodName = methods[i].getName() ;
			if(methodName.endsWith("Check")){
				arr.add(methodName) ;
			}
		}
		InfoHelper.outputJson(arr.toString(), response);
	}
	
	@RequestMapping(value="paramSingleCheck.do")
	public void checkSingle(HttpServletRequest request,HttpServletResponse response,String name){
        System.out.println(name);
        Method method = null ;
		try {
			method = this.getClass().getDeclaredMethod(name) ;
			long startTime=System.currentTimeMillis();  //执行开始
			JSONObject obj = (JSONObject)method.invoke(this) ;
			long endTime=System.currentTimeMillis(); //执行结束
			obj.put("time", endTime-startTime) ; //用时
			obj.put("name", name) ;
			InfoHelper.outputJson(obj.toString(), response);
		} catch (Exception e) {
			System.out.println(method.getName());
			e.printStackTrace();
		} 
	}
	
	/** 代理服务器 检测 */
	public JSONObject proxyCheck(){
		JSONObject obj = new JSONObject() ;
		WebProxy webProxy = HTTPHelper.getWebProxy();
		if (webProxy.isStart()){
			GetMethod getMethod = new GetMethod("http://www.baidu.com") ;
			try {
				int statusCode = HTTPHelper.sendRequestWithProxy(getMethod,1000) ;
				obj.put("status",statusCode) ; 
				obj.put("result",statusCode == 200 ? 0 : 1) ; // 检测结果:0:成功|1:失败
			} catch (IOException e) {
				obj.put("result",1) ;
				e.printStackTrace();
			}
			obj.put("value",webProxy.getServerAddress()+":"+webProxy.getPort()) ; 
		}else{
			obj.put("value","代理服务未启用!") ;
			obj.put("result",0) ;
		}
		obj.put("item","代理服务") ;
		return obj ;
	}
	/** 原稿库 */
	public JSONObject solrOriginalServerCheck(){
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache)(CacheReader.find(SiteSolrServerCache.class));
		SolrServer server  = siteSolrServerCache.getSolrOriginalServerBySiteID(getSiteID());
		JSONObject obj = checkManager.testSolr(server) ;
		obj.put("item","全文检索:原稿库") ;
		return obj ;
	}
	/** web稿件库 */
	public JSONObject solrArticleServerCheck(){
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache)(CacheReader.find(SiteSolrServerCache.class));
		SolrServer server = siteSolrServerCache.getSolrArticleServerBySiteID(getSiteID());
		JSONObject obj = checkManager.testSolr(server) ;
		obj.put("item","全文检索:WEB稿件库") ; 
		return obj ;
	}
	/** WEB发布库 */
	public JSONObject solrServerCheck(){
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache)(CacheReader.find(SiteSolrServerCache.class));
		SolrServer server = siteSolrServerCache.getSolrServerBySiteID(getSiteID());
		JSONObject obj = checkManager.testSolr(server) ;
		obj.put("item","全文检索:WEB发布库") ;
		return obj ;
	}
	/** APP稿件库 */
	public JSONObject solrArticleAppServerCheck(){
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache)(CacheReader.find(SiteSolrServerCache.class));
		SolrServer server = siteSolrServerCache.getSolrArticleAppServerBySiteID(getSiteID());
		JSONObject obj = checkManager.testSolr(server) ;
		obj.put("item","全文检索:APP稿件库") ; 
		return obj ;
	}
	/** APP发布库 */
	public JSONObject solrAppServerCheck(){
		SiteSolrServerCache siteSolrServerCache = (SiteSolrServerCache)(CacheReader.find(SiteSolrServerCache.class));
		SolrServer server = siteSolrServerCache.getSolrAppServerBySiteID(getSiteID());
		JSONObject obj = checkManager.testSolr(server) ;
		obj.put("item","全文检索:APP发布库") ;
		return obj ;
	}
	/** 关键词 */
	public JSONObject extractKeywordCheck() throws Exception{
		return checkManager.testAnalyzeServer("keyword", "关键词提取服务", "关键字") ;
	}
	/** 摘要 */
	public JSONObject extractSummaryCheck() throws Exception{
		return checkManager.testAnalyzeServer("summary", "摘要提取服务", "摘要") ;
	}
	/** 敏感词 */
	public JSONObject sensitiveServerCheck(){
		JSONObject obj = new JSONObject() ;
		boolean isStart = InfoHelper.sensitiveInNis() ;
		if(isStart){
			String ip = InfoHelper.getConfig("写稿服务", "敏感词服务器地址");
            String port = InfoHelper.getConfig("写稿服务", "敏感词服务器端口");
            String url = "http://" + ip + ":" + port+"/servlet/SensitiveServlet";
			String jsonStr = SensitiveWordControllerHelper.sensitive("checkSensitive", "1", null, "法轮功");
			JSONObject json = JSONObject.fromObject(jsonStr);
			int code = json.getInt("code");
			obj.put("result", code != 0 ? 0 : 1) ;
			obj.put("value", url) ;
		}else{
			obj.put("result",0) ;
			obj.put("value","服务未启动") ;
		}
		obj.put("item","敏感词服务") ;
		return obj ;
	}
	/** 图片服务 */
	public JSONObject picServerCheck() throws Exception{
		JSONObject obj = new JSONObject() ;
		String url =UrlHelper.imageServiceUrl();
        
        Params params = new Params() ;
        params.setCommand("gray");
        params.setKeepRadio(false);
        params.setWholePath(checkManager.prepareTestFile());
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        JSONObject paramJson = JSONObject.fromObject(params);
        nvps.add(new BasicNameValuePair("json", paramJson.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        JSONObject json = ArticleServiceHelper.executeHttpRequest(httpPost, true);
		obj.put("result","success".equals(json.getString("status")) ? 0 : 1) ;
        obj.put("value",url) ;
        obj.put("item","图片服务") ;
        return obj ;
	}
	/** 直播分享页地址 */
	public JSONObject liveShareCheck(){
		JSONObject obj = checkManager.testNisUrl(InfoHelper.getConfig("互动", "直播分享页地址")) ;
		obj.put("item","直播分享页地址") ;
		return obj ;
	}
	/** WEB直播分享页地址 */
	public JSONObject webLiveShareCheck(){
		JSONObject obj = checkManager.testNisUrl(UrlHelper.getWebLiveShareUrl()) ;
		obj.put("item","直播分享页地址") ;
		return obj ;
	}
	/** 专题分享页地址 */
	public JSONObject subjectShareCheck(){
		JSONObject obj = checkManager.testNisUrl(UrlHelper.getLiveShareUrl()) ;
		obj.put("item","专题分享页地址") ;
		return obj ;
	}
	/** SSO访问地址 */
	public JSONObject SSOCheck(){
		JSONObject obj = checkManager.testNisUrl(InfoHelper.getConfig("互动", "SSO访问地址")) ;
		obj.put("item","SSO访问地址") ;
		return obj ;
	}
	/** 天气服务访问地址 */
	public JSONObject weatherServerCheck(){
		JSONObject obj = checkManager.testNisUrl(InfoHelper.getConfig("互动", "天气服务访问地址")) ;
		obj.put("item","天气服务访问地址") ;
		return obj ;
	}
	/** 问答分享页地址 */
	public JSONObject qaCheck(){
		JSONObject obj = checkManager.testNisUrl(UrlHelper.getQAShareUrl()) ;
		obj.put("item","问答分享页地址") ;
		return obj ;
	}
	/** 活动分享页地址 */
	public JSONObject activityShareCheck(){
		JSONObject obj = checkManager.testNisUrl(UrlHelper.getActivityShareUrl()) ;
		obj.put("item","活动分享页地址") ;
		return obj ;
	}

	/** 图片存储 */
	public JSONObject picDeviceCheck(){
		return checkManager.testDevice("存储设备", "图片存储设备") ;
	}
	/** 附件存储 */
	public JSONObject attachmentDeviceCheck(){
		return checkManager.testDevice("存储设备", "附件存储设备") ;
	}
	/** 水印存储 */
	public JSONObject waterDeviceCheck(){
		return checkManager.testDevice("存储设备", "水印存储设备") ;
	}
	/** 模板存储 */
	public JSONObject templateDeviceCheck(){
		return checkManager.testDevice("存储设备", "模板存储设备") ;
	}
	/** 报纸存储 */
	public JSONObject paperDeviceCheck(){
		if(!SecurityHelper.epaperUsable())
			return null ;
		return checkManager.testDevice("存储设备", "报纸存储设备") ;
	}
	/** 外部稿件存储 */
	public JSONObject outsideArticleDeviceCheck(){
		if(InfoHelper.getOtherSystemDevice() == null)
			return null ;
		return checkManager.testDevice("存储设备", "外部系统稿件存储") ;
	}
	/** 互动外网图片 */
	public JSONObject nisExtractPicCheck()throws Exception{
		JSONObject obj = new JSONObject() ;
		String deviceName = URLDecoder.decode("互动外网图片存储", "UTF-8");
		StorageDeviceReader sdReader = (StorageDeviceReader) Context.getBean(StorageDeviceReader.class);
		StorageDevice device = sdReader.getByName(deviceName) ;
		obj.put("item", deviceName) ;
		obj.put("value","请检查抽图服务配置文件:"+InfoHelper.getDevicePath(device)) ;
		obj.put("result",0) ;
		return obj ;
	}
	/** 图片 */
	public JSONObject extractPicCheck()throws Exception{
		JSONObject obj = new JSONObject() ;
		String deviceName = URLDecoder.decode("图片存储", "UTF-8");
		StorageDeviceReader sdReader = (StorageDeviceReader) Context.getBean(StorageDeviceReader.class);
		StorageDevice device = sdReader.getByName(deviceName) ;
		obj.put("item", deviceName) ;
		obj.put("value","请检查抽图服务配置文件:"+InfoHelper.getDevicePath(device)) ;
		obj.put("result",0) ;
		return obj ;
	}
	private int getSiteID(){
		return param.getInt("siteID") ;
	}
	private void setSiteID(int siteID){
        System.out.println(this.param);
        this.param.put("siteID",siteID) ;
	}
}
