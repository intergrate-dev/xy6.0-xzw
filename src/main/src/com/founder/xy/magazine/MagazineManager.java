package com.founder.xy.magazine;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;

/**
 * 期刊功能管理 
 * @author binLee
 */
@Service
public class MagazineManager {

	/**
	 * 稿件删除
	 */
	public String articleDelete(int docLibID, long docID) throws Exception{
		String tenantCode = LibHelper.getTenantCodeByLib(docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		// 删除稿件附件的sql
		String attLib = LibHelper.getLibTable(DocTypes.ATTACHMENT.typeID(), tenantCode);
		String delAttSql = "delete from " + attLib + " where att_articleID=? and att_articleLibID=?";
		
		DBSession conn = null ;
		try {
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();
			
			docManager.delete(docLibID, docID, conn);
			InfoHelper.executeUpdate(delAttSql, new Object[]{docID, docLibID},conn);
			
			conn.commitTransaction();
		} catch (Exception e){
			ResourceMgr.rollbackQuietly(conn);
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		
		// 清除 redis 暂时不做
		
		return null;
	}
	
	//重新发布某一天的期刊
	public void pubByDate(int paperLibID, long paperID, String paperDate) throws Exception {
		paperDate = paperDate.replace("-", ""); //从yyyy-MM-dd变成yyyyMMdd格式
		PublishTrigger.pubOthers(paperLibID, paperID, paperDate, DocIDMsg.TYPE_MAGAZINEDATE);
	}
	
	/**
	 * 刊期删除
	 * 
	 * 清理刊期下所有版面的数据，再删除刊期表数据。
	 * 
	 * 正常结束时返回null，否则返回错误信息
	 */
	public String dateDelete(int paperLibID, int paperID, String paperDate) throws Exception {
		String tenantCode = LibHelper.getTenantCodeByLib(paperLibID);
		
		Date periodDate = DateUtils.parse(paperDate,"yyyy-MM-dd");
		Object[] params = new Object[] {paperID,periodDate};
		
		String delDate = "delete from " + LibHelper.getLibTable(DocTypes.MAGAZINEDATE.typeID(), tenantCode)
				+ " where pd_paperID=? and pd_date=?";
		String delColumns = "delete from " + LibHelper.getLibTable(DocTypes.MAGAZINECOLUMN.typeID(), tenantCode)
				+ " where pl_magID=? and pl_date=?";
		String delArticles = "delete from " + LibHelper.getLibTable(DocTypes.MAGAZINEARTICLE.typeID(), tenantCode)
				+ " where a_magazineID=? and a_pubTime=?";
		
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(paperLibID);
			conn.beginTransaction();
			
			//删除
			InfoHelper.executeUpdate(delDate, params, conn);
			InfoHelper.executeUpdate(delColumns, params, conn);
			InfoHelper.executeUpdate(delArticles, params, conn);
			
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	
		return null;
	}
	
	/**
	 * 读一个刊期的发布页Url
	 */
	public String[] getUrls(int libID, long magazineID, String date) throws Exception {
		Timestamp pubDate = new Timestamp(DateUtils.parse(date, "yyyy-MM-dd").getTime());
		
		String[] result = new String[2];
		
		int dateLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINEDATE.typeID(), libID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] layouts = docManager.find(dateLibID, "pd_date=? and pd_paperID=?", 
				new Object[]{pubDate, magazineID});
		
		if (layouts.length > 0) {
			result[0] = layouts[0].getString("pd_url");
			result[1] = layouts[0].getString("pd_urlPad");
		}
		
		return result;
	}
	
}
