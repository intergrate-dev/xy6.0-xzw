package com.founder.xy.nis.web;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.sys.StorageDevice;

import com.founder.xy.nis.NisManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.FilePublishHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

@Controller
@RequestMapping("/xy/nis")
public class NisController {

	@Autowired
	private NisManager nisManager;

	/** 评论参数设置 */
	@RequestMapping(value = "DiscussConfig.do")
	public ModelAndView discussConfig(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		String siteConfig = getConfig(siteLibID, siteID, "discuss");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("discussConfig", siteConfig);
		model.put("UUID", WebUtil.get(request, "UUID"));
	
		return new ModelAndView("/xy/nis/DiscussConfig", model);
	}

	/**评论参数设置提交*/
	@RequestMapping(value = {"DiscussConfigSubmit.do"})
	public String discussConfigSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		String disConfig = WebUtil.get(request, "discussConfig");
		
		//把图标发布到外网，改写icon的url为外网地址
		JSONObject qa = JsonHelper.getJson(disConfig);
		String iconPath = JsonHelper.getString(qa, "defaultIcon");
		iconPath = FilePublishHelper.pubAndGetUrl(siteID, iconPath);
		qa.put("defaultIcon", iconPath);
		
		//取出站点的配置json，替换其中的评论设置json
		saveSiteConfig(siteLibID, siteID, qa, "discuss");
	
		LogHelper.writeLog(siteLibID, siteID, request, "评论参数设置");
		
		RedisManager.clear(RedisKey.SITE_CONFIG_DISCUSS_KEY + siteID);
		
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
			url += "&DocIDs=" + siteID + "&DocLibID=" + siteLibID;
		return "redirect:" + url;
	}

	/**问答参数设置*/
	@RequestMapping(value = "QAConfig.do")
	public ModelAndView qaConfig(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		String siteConfig = getConfig(siteLibID, siteID, "qa");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("qaConfig", siteConfig);
		model.put("UUID", WebUtil.get(request, "UUID"));
		return new ModelAndView("/xy/nis/QAConfig", model);
	}

	/**问答参数设置提交*/
	@RequestMapping(value = {"QaConfigSubmit.do"})
	public String qaConfigSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String qaConfig = WebUtil.get(request, "qaConfig");
	
		//把图标发布到外网，改写icon的url为外网地址
		JSONObject qa = JsonHelper.getJson(qaConfig);
		String iconPath = JsonHelper.getString(qa, "defaultIcon");
		iconPath = FilePublishHelper.pubAndGetUrl(siteID, iconPath);
		qa.put("defaultIcon", iconPath);
		
		//取出站点的配置json，替换其中的设置json
		saveSiteConfig(siteLibID, siteID, qa, "qa");
		
		LogHelper.writeLog(siteLibID, siteID, request, "问答参数设置");
		
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
			url += "&DocIDs=" + siteID + "&DocLibID=" + siteLibID;
		return "redirect:" + url;
	}

	/** 字体包设置 */
	@RequestMapping(value = "Font.do")
	public ModelAndView fontConfig(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		String siteConfig = getConfig(siteLibID, siteID, "font");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("font", siteConfig);
		model.put("UUID", WebUtil.get(request, "UUID"));
	
		return new ModelAndView("/xy/nis/Font", model);
	}

	/**字体包设置提交*/
	@RequestMapping(value = {"FontSubmit.do"})
	public String fontConfigSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		
		String disConfig = WebUtil.get(request, "font");
		
		//把文件发布到外网，改写url为外网地址
		JSONObject qa = JsonHelper.getJson(disConfig);
		JSONArray fontList = qa.getJSONArray("list");
		for (Object object : fontList) {
			JSONObject font = (JSONObject)object;
			
			String iconPath = FilenameUtils.normalize(JsonHelper.getString(font, "url"));
			iconPath = FilePublishHelper.pubAndGetUrl(siteID, iconPath);
			font.put("url", iconPath);
		}
		
		//取出站点的配置json，替换其中的字体json
		saveSiteConfig(siteLibID, siteID, qa, "font");
	
		LogHelper.writeLog(siteLibID, siteID, request, "字体包设置");

		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
			url += "&DocIDs=" + siteID + "&DocLibID=" + siteLibID;
		return "redirect:" + url;
	}

	/** 外部系统设置 */
	@RequestMapping(value = "External.do")
	public ModelAndView externalConfig(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		int siteID = WebUtil.getInt(request, "siteID", 0);

//		String siteConfig = getConfig(siteLibID, siteID, "font");
		String externalConfig = nisManager.getExternal();
		if(externalConfig != null && !externalConfig.isEmpty()){
			externalConfig = externalConfig.replace("\"", "'");
		}

//		if(externalConfig != null && !externalConfig.isEmpty()){
//			JSONObject jsonConfig = JsonHelper.getJson(externalConfig);
//			externalConfig = getConfig(jsonConfig, "externals");
//		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("external", externalConfig);
		model.put("UUID", WebUtil.get(request, "UUID"));

		return new ModelAndView("/xy/nis/External", model);
	}

	@RequestMapping(value = {"externalName.do"})
	public void externalName(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		StorageDevice device = InfoHelper.getAttachDevice();
//
//		String savePath = InfoHelper.getPicSavePath(request);

//		String attachmentPath = FilenameUtils.normalize(InfoHelper.getDevicePath(device) + "/" + savePath);

		String name = WebUtil.get(request,"name");

		int userExtLibID = LibHelper.getUserExtLibID();
		int docTypeID = DomHelper.getDocTypeIDByLibID(userExtLibID);
		long id = EUID.getID("DocID" + docTypeID);

		String key = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(name.getBytes("UTF-8"));
			key = toHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("key", key);

		InfoHelper.outputText(json.toString(), response);
	}

	private String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}
		return sb.toString();
	}

	@RequestMapping(value = {"ExternalSubmit.do"})
	public String ExternalSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);

		String externalConfig = WebUtil.get(request, "external");

		//取出站点的配置json，替换其中的字体json
		saveExternal(siteLibID, siteID, externalConfig);
		// 清空 redis
		RedisManager.clear(RedisKey.EXTERNAL_KEY);

		LogHelper.writeLog(siteLibID, siteID, request, "外部系统设置");

		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		url += "&DocIDs=" + siteID + "&DocLibID=" + siteLibID;
		return "redirect:" + url;
	}

	private void saveExternal(int siteLibID, int siteID, String externalConfig) throws E5Exception {

		DBSession conn = null;
		IResultSet rs = null;
		try{
			conn = Context.getDBSession();

			StringBuilder sql = new StringBuilder();
			//以后多租户要改
			sql.append("update g_tenant set te_externals = '").append(externalConfig).append("' where te_code = 'xy'");
			conn.executeUpdate(sql.toString(), new Object[]{});
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**其它站点参数设置*/
	@RequestMapping(value = "SiteConfig.do")
	public ModelAndView siteConfig(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	
		int siteLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteID = WebUtil.getInt(request, "DocIDs", 0);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("UUID", WebUtil.get(request, "UUID"));
		
		JSONObject siteConfig = getSiteConfig(siteLibID, siteID);
		if (siteConfig != null) {
			model.put("app", getConfig(siteConfig, "app"));
			model.put("member", getConfig(siteConfig, "member"));
		}
		return new ModelAndView("/xy/nis/SiteConfig", model);
	}

	/**其它站点参数设置提交*/
	@RequestMapping(value = {"SiteConfigSubmit.do"})
	public String siteConfigSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String app = WebUtil.get(request, "app");
		String member = WebUtil.get(request, "member");
	
		JSONObject appJson = JsonHelper.getJson(app);
		JSONObject memberJson = JsonHelper.getJson(member);
		
		//取出站点的配置json，替换其中的设置json
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		
		String siteConfig = site.getString("site_config");
		
		JSONObject jsonConfig = null;
		if (StringUtils.isBlank(siteConfig)) {
			jsonConfig = new JSONObject();
		} else {
			jsonConfig = JsonHelper.getJson(siteConfig);
		}
		jsonConfig.put("app", appJson);
		jsonConfig.put("member", memberJson);
		
		String config = jsonConfig.toString();
		site.set("site_config", config);
		
		docManager.save(site);
		
		RedisManager.set(RedisKey.SITE_CONF_KEY + siteID, config);
		clearRedis(siteLibID,siteID,docManager);
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID")
				+ "&DocIDs=" + siteID;
		return "redirect:" + url;
	}

	/**提前确定一个要上传的文件的存放位置，返回格式：附件存储;xy/201505/13/uuid*/
	@RequestMapping(value = {"FilePath.do"})
	public void getFilePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String deviceName = InfoHelper.getConfig("存储设备", "附件存储设备");
		String savePath = deviceName + ";" + InfoHelper.getPicSavePath(request);

		InfoHelper.outputText(savePath, response);
	}

	/**
	 * 字体包上传时需要的两个路径：实际存储全路径、带存储设备名的系统内部识别路径。
	 */
	@RequestMapping(value = {"fontFilePath.do"})
	public void fontFilePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StorageDevice device = InfoHelper.getAttachDevice();
		
		String savePath = InfoHelper.getPicSavePath(request);
		
		String attachmentPath = FilenameUtils.normalize(InfoHelper.getDevicePath(device) + "/" + savePath);

		JSONObject json = new JSONObject();
		json.put("uploadPath", attachmentPath);
		json.put("savePath", device.getDeviceName() + ";" + savePath);

		InfoHelper.outputText(json.toString(), response);
	}

	private JSONObject getSiteConfig(int siteLibID, int siteID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		
		String siteConfig = site.getString("site_config");
		if (!StringUtils.isBlank(siteConfig)) {
			JSONObject jsonConfig = JsonHelper.getJson(siteConfig);
			return jsonConfig;
		}
		return null;
	}

	private String getConfig(int siteLibID, int siteID, String fieldInConfig) throws E5Exception {
		JSONObject jsonConfig = getSiteConfig(siteLibID, siteID);
		return getConfig(jsonConfig, fieldInConfig);
	}
	private String getConfig(JSONObject jsonConfig, String fieldInConfig) throws E5Exception {
		if (jsonConfig != null && !jsonConfig.isEmpty()) {
			JSONObject obj = jsonConfig.getJSONObject(fieldInConfig);
			
			String value = (obj == null || obj.isEmpty()) ? "" : obj.toString();
			
			return value.replace("\"", "'");
		}
		return null;
	}

	private void saveSiteConfig(int siteLibID, int siteID, JSONObject qa, String fieldInConfig) throws E5Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		
		String siteConfig = site.getString("site_config");
		
		JSONObject jsonConfig = null;
		if (StringUtils.isBlank(siteConfig)) {
			jsonConfig = new JSONObject();
		} else {
			jsonConfig = JsonHelper.getJson(siteConfig);
		}
		jsonConfig.put(fieldInConfig, qa);
		
		String config = jsonConfig.toString();
		site.set("site_config", config);
		
		docManager.save(site);
		
		RedisManager.set(RedisKey.SITE_CONF_KEY + siteID, config);
		clearRedis(siteLibID,siteID,docManager);
	}
	
	private void clearRedis(int siteLibID,int siteID,DocumentManager docManager) throws E5Exception{
		int mobileOSLibID = LibHelper.getLibIDByOtherLib(DocTypes.MOBILEOS.typeID(), siteLibID);
		Document[] mobiles = docManager.find(mobileOSLibID, "SYS_DELETEFLAG=0 and os_siteID=?", new Object[]{siteID});
		if(null != mobiles){
			for(int i = 0 ; i < mobiles.length ; i++){
				RedisManager.clear(RedisKey.APP_START_KEY+mobiles[i].getDocID());
			}
		}
	}
}
