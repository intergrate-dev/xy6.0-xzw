package com.founder.xy.system.job;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;

import java.sql.Timestamp;

public class ArticleExpireJob extends BaseJob{
	
	public ArticleExpireJob(){
		super();
		log = Context.getLog("xy.articleExpire");
	}
	@Override
	protected void execute() throws E5Exception {
		log.info("---开始稿件顺序失效检查监控");
		DocLib[] expireLibs = LibHelper.getLibs(DocTypes.ARTICLEEXPIRE.typeID());
		for (DocLib expireLib : expireLibs) {
			this.oneLib(expireLib);
		}
		log.info("---结束本轮稿件顺序时效检查监控");
	}
	/**
	 * 处理单个租户
	 * @param expireLib
	 * @throws E5Exception
	 */
	private void oneLib(DocLib expireLib) throws E5Exception{
		int expireLibID = expireLib.getDocLibID() ;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] tasks = docManager.find(expireLibID, "a_expireTime < ? ", new Object[]{new Timestamp(System.currentTimeMillis())});
		
		if (tasks == null || tasks.length == 0) return;
		
		for(Document task : tasks){
			int docLibID = task.getInt("a_docLibID") ;
			int colID = task.getInt("a_columnID") ;
			long docID = task.getDocID() ;
			int type = task.getInt("a_type") ;
			if(type == 0){ //置顶
				this.cancelTop(docLibID, docID, colID,docManager);
			}else{ // 固定位置
				this.cancelPosition(docLibID, docID, colID);
			}
			this.delete(expireLib,docID,colID);
		}
	}
	
	/**
	 * 取消置顶
	 * @param docLibID 稿件库ID:web稿件库ID|app稿件库ID
	 * @param docID 稿件ID
	 * @param colID 栏目ID
	 * @throws Exception
	 */
	private void cancelTop(int docLibID, long docID, int colID ,DocumentManager docManager){
		String relTable = InfoHelper.getRelTable(docLibID);
		String sql = "update " + relTable + " set a_order=? where SYS_DOCUMENTID=? and CLASS_1=? and a_order<-100000000";
		try {
			Document doc = docManager.get(docLibID, docID);
			if(doc == null ) return ;
			InfoHelper.executeUpdate(docLibID, sql,new Object[]{doc.getBigDecimal("a_order"), docID, colID});
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
			PublishTrigger.articleOrder(colLibID, colID);
		} catch (E5Exception e) {
			log.error("取消置顶错误: " + e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 取消固定位置
	 * @param docLibID 稿件库ID:web稿件库|app稿件库
	 * @param docID 稿件ID
	 * @param colID 栏目ID
	 * @throws Exception
	 */
	private void cancelPosition(int docLibID, long docID, int colID){
		String relTable = InfoHelper.getRelTable(docLibID);
		String sql = "update " + relTable + " set a_position=? where SYS_DOCUMENTID=? and CLASS_1=? and a_position > 0";
		try {
			InfoHelper.executeUpdate(docLibID, sql,new Object[]{0, docID, colID});
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), docLibID);
			PublishTrigger.articleOrder(colLibID, colID);
		} catch (E5Exception e) {
			log.error("取消固定位置错误: " + e.getLocalizedMessage(), e);
		}
	}
	private void delete(DocLib expireLib ,long docID, int colID){
        int expireLibID = expireLib.getDocLibID() ;
        String tableName = expireLib.getDocLibTable() ;
		DBSession conn = null;
	    try {
	        conn = E5docHelper.getDBSession(expireLibID);
	        conn.beginTransaction();
	        String sql = " DELETE FROM " + tableName + "  WHERE SYS_DOCUMENTID = ? AND a_columnID = ? " ;
	        conn.executeUpdate(sql, new Object[]{docID,colID}) ;
	        conn.commitTransaction();
	    } catch (Exception e) {
	        ResourceMgr.rollbackQuietly(conn);
	        log.error("删除数据出现错误: " + e.getLocalizedMessage(), e);
	    } finally {
	        ResourceMgr.closeQuietly(conn);
	    }
	}
}
