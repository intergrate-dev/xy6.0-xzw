package com.founder.xy.weibo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.weibo.data.Account;
import com.founder.xy.weibo.data.WeiboArticle;

@Component
public class WeiboManager {
	private Log log = Context.getLog("xy");

	/** 保存微博稿件 */
	public String save(String userName, WeiboArticle weibo) {
		Document doc = null;
		DocumentManager docmanager = DocumentManagerFactory.getInstance();
		try {
			if (weibo.getId() == 0) {
				weibo.setId(InfoHelper.getNextDocID(DocTypes.WBARTICLE.typeID()));
				doc = docmanager.newDocument(weibo.getDocLibID(), weibo.getId());
				ProcHelper.initDoc(doc);
			} else {
				doc = docmanager.get(weibo.getDocLibID(), weibo.getId());
			}
			
			String content = weibo.getContent();
			String Topic = truncate(content, 140);
			if (Topic.length() == 0) Topic = "无标题";

			doc.setTopic(Topic);
			doc.setAuthors(userName);
			doc.set("wb_content", content);
			doc.set("wb_accountID", weibo.getAccountID());
			
			//定时发布
			if (weibo.getPubTime() != null) {
				doc.set("wb_pubTime", weibo.getPubTime());
				doc.set("wb_status", 2);
			}
			
			int wordCount = getLength(content);
			doc.set("wb_wordCount", wordCount);// 字数
			doc.set("wb_attachments", weibo.getAttachments());// 字数
			
			docmanager.save(doc);
			return null;
		} catch (Exception e) {
			log.error(e);
			return e.getLocalizedMessage();
		}
	}
	/** 发布微博稿件 */
	public String publish(int docLibID, long docID) {
		Document weibo = null;
		DocumentManager docmanager = DocumentManagerFactory.getInstance();
		try {
			weibo = docmanager.get(docLibID, docID);
		} catch (Exception e) {
			log.error("读数据失败", e);
			return "读数据失败：" + e.getLocalizedMessage();
		}
		
		return publish(null, weibo);
	}
	
	public String publish(String token, Document weibo) {
		String result = null;
		try {
			result = WeiboPublisher.publish(token, weibo);
			
			if (result != null) {
				weibo.set("wb_pubTime", DateUtils.getTimestamp());
				weibo.set("wb_status", 1);
				weibo.set("wb_wid", result); //微博里的id
				
				DocumentManager docmanager = DocumentManagerFactory.getInstance();
				docmanager.save(weibo);
			} else {
				return "发布失败";
			}
		} catch (Exception e) {
			log.error("发布失败", e);
			return "发布失败：" + e.getLocalizedMessage();
		}
		return null;
	}
	
	/** 删除微博稿件 */
	public String delete(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document weibo = docManager.get(docLibID, docID);
			
			boolean isPublish = weibo.getInt("wb_status") == 1;
			if (isPublish) {// 已发布，需撤回
				String error = WeiboPublisher.delete(weibo);
				if (error == null) {
					docManager.delete(weibo);
				} else {
					return error;
				}
			} else {
				docManager.delete(weibo);
			}
			return null;
		} catch (Exception e) {
			log.error("删除微博失败", e);
			return "删除微博失败" + e.getLocalizedMessage();
		}
	}
	
	/** 撤回微博，暂未使用 */
	public String revoke(int docLibID, long docID){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document weibo = docManager.get(docLibID, docID);
			String error = WeiboPublisher.delete(weibo);
			if (error != null) {
				return error;
			} else {
				weibo.set("wb_status", 0);
				docManager.save(weibo);
				return null;
			}
		} catch (Exception e) {
			log.error("微博撤消发布失败", e);
			return "微博撤消发布失败" + e.getLocalizedMessage();
		}
	}

	/** 得到转发列表*/
	public String getReposts(int docLibID, long docID, int page) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document weibo = docManager.get(docLibID, docID);
		
		return WeiboPublisher.getReposts(weibo, page);
	}

	/** 得到评论列表 */
	public String getComments(int docLibID, long docID, int page) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document weibo = docManager.get(docLibID, docID);
		
		return WeiboPublisher.getComments(weibo, page);
	}

	/** 发布评论 */
	public long publishComment(int wbLibID, long wbID, String text) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document weibo = docManager.get(wbLibID, wbID);
			
			return WeiboPublisher.publishComment(weibo, text);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** 发布回复 */
	public long publishReply(int wbLibID, long wbID, String commentID, String text) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document weibo = docManager.get(wbLibID, wbID);
			
			return WeiboPublisher.publishReply(weibo, commentID, text);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** 得到指定的账号 */
	public Account getAccount(int docLibID, long docID) {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			return new Account(doc);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 得到账号 */
	public List<Account> getAccounts(String tCode, int siteID) {
		List<Account> result = new ArrayList<>();
		try {
			int docLibID = LibHelper.getLibID(DocTypes.WBACCOUNT.typeID(), tCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] accounts = docManager.find(docLibID, "wba_siteID=? and SYS_DELETEFLAG=0", new Object[]{siteID});
			for (Document account : accounts) {
				Account ac = new Account(account);
				result.add(ac);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** 获取账号的AccessToken */
	public String readToken(int docLibID, long docID, String code) {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			Account account = new Account(doc);

			String token = WeiboPublisher.getAccessToken(account, code);
			
			if (token != null) {
				doc.set("wba_token", token);
				docManager.save(doc);
				
				return null;
			} else {
				return "无法取得AccessToken";
			}
		} catch (Exception e) {
			return "操作失败:" + e.getLocalizedMessage();
		}
	}
	/**截取长度*/
	private String truncate(String str, int length) {
		StringBuffer buffer = new StringBuffer();
		char[] ch = str.toCharArray();
		if (ch.length < length) {
			return str;
		} else {
			for (int i = 0; i < length; i++) {
				buffer.append(String.valueOf(ch[i]));
			}
		}
		return buffer.toString();
	}
	
	//按字节来数
	private int getLength(String s) {
		int size = 0;
		int length = s.getBytes().length;
		if (length % 2 == 0) {
			size = length / 2;
		} else {
			size = (length - 1) / 2;
		}
		return size;
	}
}
