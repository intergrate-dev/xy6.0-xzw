package com.founder.xy.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jms.data.ColumnArticleMsg;
import com.founder.xy.jms.data.DocIDMsg;

/**
 * 向消息队列发送消息
 */
public class PublishTrigger {
	
	/**
	 * 发送稿件发布消息。
	 * 
	 * 消息接收者处理：
	 * 1）	生成稿件html，放到外网目录下。
	 * 2）	修改a_columnAll中每个栏目ID在缓存中的更新时间。
	 * 3）	刷新缓存中对应到该稿件的所有稿件内容组件实例。这里是在栏目页出现的稿件内容组件。稿件页中不做这个组件的缓存。
	 */
	public static void article(int docLibID, long docID) {
		Document doc = getDoc(docLibID, docID);
		if (doc == null) return;
		
		article(doc);
	}
	public static void article(Document article) {
		if (article.getInt("a_status") == Article.STATUS_PUB_ING) {
			ArticleMsg msg = getArticleMsg(article);
			
			sendMsg("pubSender", msg);
		}
	}
	
	/**
	 * 发送稿件撤稿消息
	 * 
	 * 消息接收者处理：
	 * 1）	删除稿件html
	 * 2）	修改a_columnAll中每个栏目ID在缓存中的更新时间。
	 * 3）	主动刷新所有本稿件的单篇稿件内容组件。简单地清空好吗？
	 */
	public static void articleRevoke(Document doc) {
		ArticleMsg msg = getArticleMsg(doc);
		sendMsg("revokeSender", msg);
	}

	/**
	 * 发送稿件彻底删除消息。
	 * 该消息实际发布时还是撤稿消息，由于稿件已被彻底删除，所以无法在本方法调用时组织数据，因此要求传入稿件参数
	 */
	public static void articleDelete(ArticleMsg msg) {
		sendMsg("revokeSender", msg);
	}

	/**
	 * 发送栏目稿件顺序调整消息（更新排序、置顶、当前栏目稿件设置）
	 * 
	 * 消息接收者处理：
	 * 修改栏目ID在缓存中的更新时间
	 */
	public static void articleOrder(int colLibID, long colID) {
		DocIDMsg data = new DocIDMsg(colLibID, colID, null);
		
		sendMsg("orderSender", data);
	}
	
	/**
	 * 稿件关联操作消息（稿件已发布时）
	 * 
	 * 消息接收者处理：
	 * 按关联栏目ID（不包括聚合栏目）找出受影响的稿件列表组件实例，重置缓存中的组件实例的时间
	 */
	public static void articleRel(int docLibID, long docID, int[] colIDs) {
		DocIDMsg data = new DocIDMsg(docLibID, docID, StringUtils.join(colIDs, ","));
		
		sendMsg("relSender", data);
	}
	
	/**
	 * 触发区块的发布更新，只针对手动区块（推送、选稿、更新排序）
	 * 
	 * 消息接收者处理：
	 * 读出稿件，生成html
	 */
	public static void block(int blockLibID, long blockID) {
		DocIDMsg data = new DocIDMsg(blockLibID, blockID, null);
		data.setType(DocIDMsg.TYPE_BLOCK);
		
		sendMsg("blockSender", data);
	}
	/**
	 * 触发区块删除的更新
	 * 
	 * 消息接收者处理：
	 *   替换为空白html
	 */
	public static void blockRevoke(int blockLibID, long blockID) {
		DocIDMsg data = new DocIDMsg(blockLibID, blockID, null);
		data.setType(DocIDMsg.TYPE_BLOCK); 
		
		sendMsg("blockRevokeSender", data);
	}
	/**
	 * 触发区块的缓存更新（新建、修改、删除）
	 * 
	 * 消息接收者处理：
	 * 刷新发布服务中的缓存
	 */
	public static void blockChanged(int blockLibID, long blockID) {
		otherData(blockLibID, blockID, DocIDMsg.TYPE_BLOCK);
	}
	
	/**
	 * “按栏目发布”消息
	 * 按指定的时间范围查出需重新发布的稿件列表，逐个发送稿件发布消息
	 *  @param articleLibID 操作所在的稿件发布库ID
	 * @param colID 指定的栏目
	 * @param begin 起始时间
	 * @param end    截止时间
	 * @param pubContent
	 * @param type    0-本栏目，1-带子栏目，2-带子孙栏目
	 */
	public static boolean columnPublish(int articleLibID, long colID, Date begin, Date end, int type, int pubContent, int count) {
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), articleLibID);

		ArticleManager articleManager = (ArticleManager)Context.getBean("articleManager");
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		List<Document> colDocs ;
		try {
			List<Long> colIDs = articleManager.getColumnIDs(colLibID, colID, type);
			for(long cID :colIDs) {
				if (pubContent == 1 && count >= 0) {
					colDocs = articleManager.getIDs4ColPubByCount(articleLibID, cID, count);
				} else {
					colDocs = articleManager.getIDs4ColPub(articleLibID, cID, begin, end);
				}
				columnPublish(colLibID, cID, colDocs);
				Column col = colReader.get(colLibID, cID);
				if(col.getPushColumn() > 0 && articleLibID == LibHelper.getArticleLibID()){
					if (pubContent == 1 && count >= 0) {
						colDocs = articleManager.getIDs4ColPubByCount(LibHelper.getArticleAppLibID(), col.getPushColumn(), count);
					} else {
						colDocs = articleManager.getIDs4ColPub(LibHelper.getArticleAppLibID(), col.getPushColumn(), begin, end);
					}
					columnPublish(colLibID, col.getPushColumn(), colDocs);
				}
			}
			return true;
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 一次性发布栏目下的多个稿件.
	 * 作为独立消息，以方便优化
	 * @param colID
	 * @param articles
	 */
	public static void columnPublish(int colLibID, long colID, List<Document> articles) {
		Publisher sender = (Publisher) Context.getBean("pubByColSender");
		List<ArticleMsg> msgs = getArticleMsgs(articles);
		sender.send(new ColumnArticleMsg(colLibID, colID, msgs));
	}


	/**
	 * 没有稿件列表的按栏目发布，只发布栏目页
	 * 作为独立消息，以方便优化
	 * @param colID
	 */
	public static void columnPublish(int colLibID, long colID) {
		List<Document> colDoc = new ArrayList<>();
		columnPublish(colLibID,colID,colDoc);
	}
	
	/**
	 * 栏目修改消息
	 * 
	 * 消息接收者处理：
	 * 2）检查栏目的父节点是否有变化，若有变化，则刷新相关的稿件列表组件实例。
	 * 3）刷新发布服务中使用的栏目缓存。
	 * 4）检查栏目的修改时间是否有变化，若有变化，则刷新父栏目的子栏目列表（for App）
	 */
	public static void column(int colLibID, long colID) {
		otherData(colLibID, colID, DocIDMsg.TYPE_COLUMN);
	}
	
	/** 只刷新栏目缓存 */
	public static void columnRefresh(int colLibID, long colID) {
		otherData(colLibID, colID, DocIDMsg.TYPE_COLUMN_REFRESHONLY);
	}
	
	/** 栏目同步到子栏目 */
	public static void columnSync(int colLibID, long colID) {
		otherData(colLibID, colID, DocIDMsg.TYPE_COLUMN_SYNC);
	}
	
	/**
	 * 模板“挂接栏目”消息
	 * 
	 * 消息接收者处理：
	 * 1）	刷新栏目缓存。
	 * 2）	按挂接的栏目ID重新发布栏目jsp
	 */
	public static void templateGrant(int tLibID, long tID, int[] colIDs) {
		otherData(tLibID, tID, DocIDMsg.TYPE_TEMPLATE_GRANT);
	}

	public static void otherData(int tLibID, long tID, int type) {
		otherData(tLibID, tID, null, type);
	}
	public static void otherData(int tLibID, long tID, String otherMsg, int type) {
		DocIDMsg data = new DocIDMsg(tLibID, tID, otherMsg);
		data.setType(type);
		
		sendMsg("otherSender", data);
	}
	public static void pubOthers(int tLibID, long tID, String otherMsg, int type) {
		DocIDMsg data = new DocIDMsg(tLibID, tID, otherMsg);
		data.setType(type);
		
		sendMsg("pubOthersSender", data);
	}
	
	/**
	 * 触发微信菜单稿件的发布更新
	 * 
	 * 消息接收者处理：
	 * 读出稿件，生成html
	 */
	public static void wx(int blockLibID, long blockID) {
		pubOthers(blockLibID, blockID, null, DocIDMsg.TYPE_WX);
	}
	
	//从稿件组织消息数据
	public static ArticleMsg getArticleMsg(Document doc) {
		if (doc == null) return null;
		
		ArticleMsg msg = new ArticleMsg(doc.getDocLibID(), doc.getDocID(), 
				doc.getInt("a_columnID"), 
				doc.getString("a_columnAll"),
				doc.getInt("a_type"),
				doc.getInt("a_channel"));
		return msg;
	}
	
	//把Document列表转成ArticleMsg列表
	private static List<ArticleMsg> getArticleMsgs(List<Document> docs) {
		List<ArticleMsg> result = new ArrayList<>();
		for (Document doc : docs) {
			ArticleMsg data = getArticleMsg(doc);
			result.add(data);
		}
		return result;
	}
	//读Document
	private static Document getDoc(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			return docManager.get(docLibID, docID);
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//发送消息。捕捉异常
	private static void sendMsg(String senderName, Object msg) {
		try {
			Publisher sender = (Publisher)Context.getBean(senderName);
			sender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
