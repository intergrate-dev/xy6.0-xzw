package com.founder.xy.system.job;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.weibo.WeiboAPI;

/**
 * 微博定时任务：定时向微博服务器读微博的评论数和转发数
 * @author Gong Lijie
 */
public class WeiboCountJob extends BaseJob{
	
	public WeiboCountJob() {
		super();
		
		log = Context.getLog("xy.job.WeiboCount");
	}

	@Override
	public void execute() throws E5Exception {
		log.info("-----【读微博评论数服务】启动-----");
		
		DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.WBARTICLE.typeID()); // 多租户
		for (DocLib docLib : docLibIDs) {
			oneLib(docLib.getDocLibID());
		}
		
		log.info("-----【读微博评论数服务】结束-----");
	}
	
	/** 
	 * 处理每个租户任务
	 */
	private void oneLib(int docLibID) throws E5Exception {
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WBACCOUNT.typeID(), docLibID);
		HashMap<Long, String> tokens = getTokens(accountLibID);
		
		for (long accountID : tokens.keySet()) {
			dealCount(docLibID, accountID, tokens.get(accountID));
		}
	}
	/** 
	 * 读评论数和转发数
	 */
	private void dealCount(int docLibID, long accountID, String token) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 取最近三天的已发布微博的评论数和转发数
		Document[] weibos = docManager.find(docLibID, "wb_status=1 and wb_pubTime>? and wb_accountID=?", 
				new Object[]{DateUtils.getCalendarBefore(3).getTime(), accountID});
		
		int count = 0;
		StringBuilder ids = new StringBuilder();
		for (Document weibo : weibos) {
			if (ids.length() > 0) ids.append(",");
			ids.append(weibo.getString("wb_wid"));
			
			count++;
			if (count == 100) {
				//每100个访问一次
				access(docLibID, ids.toString(), token);
				
				ids.delete(0, ids.length());
				count = 0;
			}
		}
		if (ids.length() > 0) {
			access(docLibID, ids.toString(), token);
		}
	}

	private void access(int docLibID, String wids, String token) throws E5Exception {
		String result = null;
		try {
			result = WeiboAPI.getCount(token, wids);
		} catch (Exception e) {
			log.error("访问微博api异常：" + e.getLocalizedMessage(), e);
			return;
		}
		if (result == null) {
			log.error("无法访问服务器");
			return;
		}
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		JSONArray list = JsonHelper.getJsonArray(result);
		for (int i = 0; i < list.size(); i++) {
			JSONObject json = list.getJSONObject(i);
			
			String wid = json.getString("id");
			long comments = JsonHelper.getLong(json, "comments");
			long reposts = JsonHelper.getLong(json, "reposts");
			
			//更新数据库
			Document[] weibos = docManager.find(docLibID, "wb_wid=?", new Object[]{wid});
			if (weibos.length > 0) {
				weibos[0].set("wb_countDiscuss", comments);
				weibos[0].set("wb_countRepost", reposts);
				
				docManager.save(weibos[0]);
			}
		}
	}

	//读出所有的微博账号，得到每个账号的token
	private HashMap<Long, String> getTokens(int docLibID) {
		HashMap<Long, String> result = new HashMap<>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] accounts = docManager.find(docLibID, "wba_status=0", null);
			for (Document account : accounts) {
				result.put(account.getDocID(), account.getString("wba_token"));
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
