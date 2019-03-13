package com.founder.xy.system.job;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.wx.WeixinAPI;

import net.sf.json.JSONObject;

/**
 * 向微信服务器发布图文消息、菜单消息时需要提供访问Token（accessToken），
 * 这是微信服务器提供的，有效期2小时左右。因此需要做一个后台任务，
 * 每隔2小时左右访问一次微信服务器，得到Token，保存在微信账号里。
 * 
 * @author Gong Lijie
 */
public class WXTokenJob extends BaseJob{
	
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=<AppID>&secret=<secret>";
	
	public WXTokenJob() {
		super();
		log = Context.getLog("xy.WXToken");
	}

	@Override
	public void execute() throws E5Exception {
		log.info("-----读取微信Token服务，开始启动-----");
		DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.WXACCOUNT.typeID()); // 多租户
		for (DocLib docLib : docLibIDs) {
			oneLib(docLib.getDocLibID());
		}
		log.info("-----读取微信Token服务，结束-----");
	}
	
	/** 
	 * 处理每个租户任务
	 */
	private void oneLib(int docLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 读微信账号表，取出所有微信账号
		Document[] docs = docManager.find(docLibID, "SYS_DELETEFLAG=0", null);
		
		for (Document wxDoc : docs) {
			String resultJson = null;
			try {
				resultJson = WeixinAPI.getAccess(ACCESS_TOKEN_URL.replace("<AppID>", wxDoc.getString("wxa_appID"))
						.replace("<secret>", wxDoc.getString("wxa_appSecret")));
				JSONObject jsonObj = JSONObject.fromObject(resultJson);
				wxDoc.set("wxa_accessToken", jsonObj.getString("access_token"));
				docManager.save(wxDoc);
			
			} catch (Exception e) {
				e.printStackTrace();
				log.error("-----读取微信Token服务，失败，返回信息-----AppID = " + wxDoc.getString("wxa_appID"));
				if(resultJson != null) log.error("返回信息 : " + resultJson);
			}
		}
	}
}
