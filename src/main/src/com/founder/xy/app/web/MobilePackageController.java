package com.founder.xy.app.web;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.api.app.MobileAppApiManager;
import com.founder.xy.app.ApkUtils;
import com.founder.xy.app.MobileAppManager;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.DomainDirManager;

@Controller
@RequestMapping("/xy/mPackage")
public class MobilePackageController extends AbstractResourcer {

	@Autowired
	private MobileAppManager mobileAppManager;
	@Autowired
	private DomainDirManager domainDirManager;
	@Autowired
	private MobileAppApiManager mobileAppApiManager;

	/** 表单提交 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("formSubmit.do")
	public String formSubmit(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model)
			throws Exception {
		String url = "/e5workspace/after.do?UUID="
				+ WebUtil.get(request, "UUID");
		String tenantCode = Tenant.DEFAULTCODE;
		int docLibID = LibHelper.getLibID(DocTypes.MOBILEPACKAGE.typeID(),
				tenantCode);

		long docID = new Long(0);
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			
			Document doc = null;
			String docIdS = request.getParameter("DocID");
			//是否新建
			boolean isNew = (StringUtils.isBlank(docIdS) || "0".equals(docIdS));
			if(isNew){
				docID = InfoHelper.getNextDocID(DocTypes.MOBILEPACKAGE.typeID());
				
				doc = docManager.newDocument(docLibID, docID);
				ProcHelper.initDoc(doc);
				doc.set("mp_maId",
						StringUtils.getNotNull(request.getParameter("mp_maId")));
			}else{
				docID = Long.parseLong(docIdS);
				doc = docManager.get(docLibID, docID);
			}
	
			doc.set("mp_log",
					StringUtils.getNotNull(request.getParameter("mp_log")));
			String mpType = StringUtils.getNotNull(request
					.getParameter("mp_type"));

			// 得到app相关信息
			Map<String, String> docApp = mobileAppManager.getInfoById(doc
					.getString("mp_maId"));

			String redisKey = RedisKey.APP_MOBILEAPP + docApp.get("appKey");
			
			CatManager catManager = (CatManager) Context
					.getBean(CatManager.class);
			// 1为安卓要处理apk包
			if ("1".equals(mpType)) {
				String filePath = StringUtils.getNotNull(request.getParameter("mp_package"));
				String oldPackage = StringUtils.getNotNull(request.getParameter("oldPackage"));
				//如果为新建或者修改了包 则对包进行处理
				if(isNew || (!isNew && !oldPackage.equals(filePath))){
					if (!"apk".equals(filePath
							.substring(filePath.lastIndexOf(".") + 1,
									filePath.length()).trim().toLowerCase())) {
						model.put("error", "对不起，请上传app格式");
						return "/xy/site/error";
					}
					// 得到发布目录ID
					int pubDir = Integer.parseInt(docApp.get("dirId"));
					String[] dirs = getDirs(pubDir);
	
					// 检查站点的资源目录是否已配置
					boolean noSiteDir = (StringUtils.isBlank(dirs[0]) || StringUtils
							.isBlank(dirs[1]));
					if (noSiteDir) {
						model.put("error", "请先检查站点的资源目录设置");
						return "/xy/site/error";
					}
					// 发布到外网并返回文件路径
					String[] path = pubAndWriteUrl(dirs, filePath);
					String apkPath = path[0];

					if (null == apkPath) {
						url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
						return "redirect:" + url;
					}
					// 文件大小
					File f = new File(apkPath);
					if (!f.exists() && !f.isFile()) {
						url += "&Info=" + URLEncoder.encode("上传失败", "UTF-8");
						return "redirect:" + url;
					}
					doc.set("mp_size", f.length());
					// 校验MD5
					/*
					 * String mmd5 = apkUtils.getMd5(f);
					 * 
					 * if(!mmd5.equals(docApp.get("md5"))){ f.delete();
					 * model.put("error", "MD5码不符，	请重新上传！"); return
					 * "/xy/site/error"; }
					 */
					// 设置发布路径
					doc.set("mp_url", path[1]);
					// 版本号 版本Code
					Map apkInfo = ApkUtils.getApkInfo(apkPath);
					doc.set("mp_version",apkInfo.get("versionCode"));
					doc.set("mp_versionCode", apkInfo.get("versionName"));
					doc.set("mp_package", StringUtils.getNotNull(request
							.getParameter("mp_package")));
				}

				doc.set("mp_channel_ID",new BigDecimal(StringUtils.getNotNull(request
						.getParameter("mp_channel_ID"))));
				doc.set("mp_channel", StringUtils.getNotNull(request
						.getParameter("mp_channel")));
				Category cateG = catManager.getCat(CatTypes.CAT_APP.typeID(),
						doc.getInt("mp_channel_ID"));
				String channelType = cateG.getCatCode();
				// Android redis
				redisKey += ("." + channelType);
			} else {
				//IOS
				doc.set("mp_versionCode", StringUtils.getNotNull(request
						.getParameter("mp_versionCode")));
				doc.set("mp_url",
						StringUtils.getNotNull(request.getParameter("mp_url")));
			}
			// 保存表单
			docManager.save(doc);
			// 清空 redis
			RedisManager.clear(redisKey);
			// 如果是修改 可能会修改渠道 则清空修改前的该渠道包redis
			if(!isNew){
				String oldChannel = StringUtils.getNotNull(request.getParameter("oldChannel"));
				if(!doc.getString("mp_channel_ID").equals(oldChannel)){
					Category cateG = catManager.getCat(CatTypes.CAT_APP.typeID(),
							Integer.parseInt(oldChannel));
					String channelType = cateG.getCatCode();
					RedisManager.clear(RedisKey.APP_MOBILEAPP + docApp.get("appKey")+"."+channelType);
				}
			}
		} catch (Exception e) {
			url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
			return "redirect:" + url;
		}
		url += "&DocIDs=" + docID;
		return "redirect:" + url;
	}
	/**
	 * 删除选中的APP包
	 */
	@RequestMapping(value = "Delete.do")
	public void deleteRule(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] docIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try{

			CatManager catManager = (CatManager) Context
					.getBean(CatManager.class);
			Document doc = docManager.get(docLibID, docIDs[0]);
			// 得到app相关信息 清空相关redis
			Map<String, String> docApp = mobileAppManager.getInfoById(doc
					.getString("mp_maId"));

			StringBuffer redisKey = new StringBuffer();
			boolean isIOS = "0".equals(docApp.get("maType"));
			for (long docID : docIDs) {	
				//android 渠道 需要渠道code
				redisKey = new StringBuffer(RedisKey.APP_MOBILEAPP + docApp.get("appKey"));
				if(!isIOS){
					Category cateG = catManager.getCat(CatTypes.CAT_APP.typeID(),
							doc.getInt("mp_channel_ID"));
					if(cateG!=null){
						String channelType = cateG.getCatCode();
						redisKey.append(".").append(channelType);
					}
				}
				RedisManager.clear(redisKey.toString());
				//删除数据
				docManager.delete(docLibID, docID);
				InfoHelper.outputText("@refresh@", response);	//操作成功	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected long docID(HttpServletRequest request) {
		return WebUtil.getLong(request, "DocID", 0);
	}

	/**
	 * 发布文件 返回发布路径
	 */
	protected String[] pubAndWriteUrl(String[] dirs, String filePath) {
		String[] path = new String[2];
		if (dirs != null && !StringUtils.isBlank(filePath)) {
			if (filePath.startsWith("http"))
				return null;

			String fileName = randomFileName(filePath); // 随机文件名
			String resPath = dirs[0] + dirs[1];
			publishTo(filePath, resPath, fileName, dirs[0]);
			// 记录保存地址
			String apkPath = resPath + "/" + fileName;
			// 记录发布地址
			String pubUrl = dirs[2] + "/" + fileName;
			path[0] = apkPath;
			path[1] = pubUrl;
		}
		return path;
	}

}
