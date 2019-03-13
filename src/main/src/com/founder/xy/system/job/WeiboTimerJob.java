package com.founder.xy.system.job;

import java.util.HashMap;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.weibo.WeiboManager;

/**
 * 微博定时任务：处理定时发布的微博
 * @author Gong Lijie
 */
public class WeiboTimerJob extends BaseJob{
	
	public WeiboTimerJob() {
		super();
		
		log = Context.getLog("xy.job.WeiboTimer");
	}

	@Override
	public void execute() throws E5Exception {
		log.info("-----【微博定时发布服务】启动-----");
		
		DocLib[] docLibIDs = LibHelper.getLibs(DocTypes.WBARTICLE.typeID()); // 多租户
		for (DocLib docLib : docLibIDs) {
			oneLib(docLib.getDocLibID());
		}
		
		log.info("-----【微博定时发布服务】结束-----");
	}
	
	/** 
	 * 处理每个租户任务
	 */
	private void oneLib(int docLibID) throws E5Exception {
		int accountLibID = LibHelper.getLibIDByOtherLib(DocTypes.WBACCOUNT.typeID(), docLibID);
		HashMap<Long, String> tokens = getTokens(accountLibID);
		
		for (long accountID : tokens.keySet()) {
			dealPubTimer(docLibID, accountID, tokens.get(accountID));
		}
	}

	/** 
	 * 处理定时发布的微博
	 */
	private void dealPubTimer(int docLibID, long accountID, String token) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 取已到发布时间的定时发布的微博
		Document[] weibos = docManager.find(docLibID, "wb_status=2 and wb_pubTime<? and wb_accountID=?", 
				new Object[]{DateUtils.getTimestamp(), accountID});
		
		WeiboManager wbManager = (WeiboManager)Context.getBean("weiboManager");
		for (Document weibo : weibos) {
			String error = wbManager.publish(token, weibo);
			if (error != null)
				log.error(weibo.getDocID() + ":" + error);
			else {
				// 写流程记录
				LogHelper.writeLog(weibo.getDocLibID(), weibo.getDocID(), "定时服务", 0, "发布", null);
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
