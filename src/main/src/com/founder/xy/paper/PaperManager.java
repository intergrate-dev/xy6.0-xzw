package com.founder.xy.paper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.paper.PaperLayout;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 数字报功能管理
 * @author Gong Lijie
 */
@Service
public class PaperManager {
	
	/**
	 * 报纸删除，只设删除标记
	 */
	public String delete(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(docLibID, docID);
			doc.setDeleteFlag(1);
			docManager.save(doc);
			
			//清除Redis
			RedisManager.clear(RedisKey.APP_PAPER_KEY + doc.getInt("pa_siteID"));
			return null;
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
	}
	
	/**
	 * 刊期删除
	 * 
	 * 参照版面删除清理刊期下所有版面的数据，再删除刊期表数据。
	 * 清理redis里的key
	 * 
	 * 正常结束时返回null，否则返回错误信息
	 */
	public String dateDelete(int paperLibID, int paperID, String paperDate) throws Exception {
		String tenantCode = LibHelper.getTenantCodeByLib(paperLibID);
		int dateLibID = LibHelper.getLibID(DocTypes.PAPERDATE.typeID(),tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Date periodDate = DateUtils.parse(paperDate,"yyyy-MM-dd");
		Document[] dates = docManager.find(dateLibID, "pd_paperID=? and pd_date=?",
				new Object[]{paperID, periodDate});
		if (dates.length > 0 && dates[0].getInt("pd_status") == Article.STATUS_PUB_DONE)
			return "请先撤回本期报纸";

		//查出该期报纸的所有版面ID
		int layoutLibID = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), tenantCode);
		String sql = "select SYS_DOCUMENTID from "+ LibHelper.getLibTable(layoutLibID)
				+" where pl_paperID=? and pl_date=? and SYS_DELETEFLAG=0";

		Object[] params = new Object[] {paperID,periodDate};
		DBSession conn = null;
		IResultSet rs = null;
		try {
			List<String> deleteList = new ArrayList<>();
			conn = E5docHelper.getDBSession(paperLibID);
			conn.beginTransaction();
			rs = conn.executeQuery(sql, params);
			
			List<Long> docIDs = new ArrayList<Long>();
			while(rs.next()){
				docIDs.add(rs.getLong("SYS_DOCUMENTID"));
			}
			
			//分别删除每个版面
			String pDate = DateUtils.format(periodDate, "yyyyMMdd");
			for(int i = 0;i < docIDs.size();i++){
				deleteLayout(layoutLibID, docIDs.get(i), paperID, pDate, conn, docManager, deleteList);
			}
			
			//删除报纸日期
			String dateLib = LibHelper.getLibTable(DocTypes.PAPERDATE.typeID(), tenantCode);
			String delDate = "delete from " + dateLib + " where pd_paperID=? and pd_date=?";
			InfoHelper.executeUpdate(delDate, new Object[]{paperID,periodDate}, conn);
			
			conn.commitTransaction();
			clear(deleteList);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	
		RedisManager.clear(RedisKey.APP_PAPER_DATE_KEY+paperID);
		return null;
	}
	
	/**
	 * 版面删除
	 * 
	 * 删除版面、版面附件、版面的稿件、版面稿件的附件
	 * 清理redis里的稿件key
	 */
	public String layoutDelete(int layoutLibID, long layoutID) throws Exception {
		
		//得到报纸ID和日期
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document layout = docManager.get(layoutLibID, layoutID);
		int status = layout.getInt("pl_status");
		if(status == Article.STATUS_PUB_DONE){
			return "请先撤版！";
		}
		int paperID = layout.getInt("pl_paperID");
		String paperDate = DateUtils.format(layout.getDate("pl_date"), "yyyyMMdd");

		DBSession conn = null;
		try {
			List<String> deleteList = new ArrayList<>();
			conn = E5docHelper.getDBSession(layoutLibID);
			conn.beginTransaction();	
			
			deleteLayout(layoutLibID, layoutID,paperID,paperDate,conn,docManager,deleteList);
	
			conn.commitTransaction();
			clear(deleteList);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		return null;
	}
	
	/**
	 * 版面重新发布
	 */
	public void layoutPublish(int docLibID, long docID) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document layout = docManager.get(docLibID, docID);
		layout.set("pl_status",Article.STATUS_PUB_ING);
		docManager.save(layout);
		PublishTrigger.pubOthers(docLibID, docID, null, DocIDMsg.TYPE_PAPERDATE);
	}
	
	/**
	 * 读版面发布页Url
	 */
	public String[] layoutUrls(int docLibID, long docID) throws Exception {
		String[] result = new String[2];
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document layout = docManager.get(docLibID, docID);
		
		result[0] = layout.getString("pl_url");
		result[1] = layout.getString("pl_urlPad");
		
		return result;
	}
	
	/**
	 * 报纸稿件删除
	 * 
	 * 删除附件
	 * 清理redis里的稿件key
	 */
	public String articleDelete(int docLibID, long docID) throws Exception {
		String tenantCode = LibHelper.getTenantCodeByLib(docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		int status = doc.getInt("a_status");
		if(status == Article.STATUS_PUB_DONE)
			return "请先下版！";
		int layoutID = doc.getInt("a_layoutID");
		
		//删除附件的SQL
		String attLib = LibHelper.getLibTable(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
		String delAtt = "delete from " + attLib + " where att_articleID=? and att_articleLibID=?";
		
		DBSession conn = null;
		try {
			List<String> deleteList = new ArrayList<>();
			getFilesToDelete(docLibID, docID, deleteList, docManager);
			conn = E5docHelper.getDBSession(docLibID);
			conn.beginTransaction();	
			
			docManager.delete(docLibID, docID, conn); //删除稿件
			InfoHelper.executeUpdate(delAtt,new Object[]{docID, docLibID}, conn);//删除稿件附件
			
			conn.commitTransaction();
			clear(deleteList);
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		//清除redis
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layoutID);
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + docID);
		return null;
	}
	
	/**
	 * 报纸稿件撤回 
	 * 
	 * 清除redis中的key
	 */
	public String revokeArticle(int docLibID, long docID) throws Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		int layoutID = doc.getInt("a_layoutID");
		int LayoutLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERLAYOUT.typeID(),docLibID);
		Document layout = docManager.get(LayoutLibID,layoutID);
		
/*		String tabName = LibHelper.getLibTable(docLibID);
		String updateSql = "update "+ tabName + " set a_status=7 where SYS_DOCUMENTID=?";
		try{
			InfoHelper.executeUpdate(updateSql, new Object[]{docID});
		}catch(Exception e){
			return e.getLocalizedMessage();
		}*/
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String date=sdf.format(layout.getDate("pl_date"));
		PublishTrigger.pubOthers(docLibID, docID, date, DocIDMsg.TYPE_REVOKE_PAPER_ARTICLE);
		//清除redis
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layoutID);
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + docID);
		return null;
	}
	
	/**
	 * 报纸稿件发布
	 */
	public String publishArticle(int docLibID, long docID) throws Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		int layoutID = doc.getInt("a_layoutID");
		String tabName = LibHelper.getLibTable(docLibID);
		String updateSql = "update "+ tabName + " set a_status= "+Article.STATUS_PUB_NOT+" where SYS_DOCUMENTID=?";
		try{
			InfoHelper.executeUpdate(updateSql, new Object[]{docID});
		}catch(Exception e){
			return e.getLocalizedMessage();
		}
		//清除redis
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layoutID);
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + docID);
		
		return null;
	}
	
	//重新发布某一天的所有版面
	public void pubByDate(int paperLibID, long paperID, String paperDate) throws Exception {
		paperDate = paperDate.replace("-", ""); //从yyyy-MM-dd变成yyyyMMdd格式
		PublishTrigger.pubOthers(paperLibID, paperID, paperDate, DocIDMsg.TYPE_PAPERDATE);
	}
	//删除版面、版面附件、版面稿件、版面稿件附件
	private void deleteLayout(int layoutLibID, long layoutID, int paperID, String paperDate
			, DBSession conn, DocumentManager docManager, List<String> deleteList) throws Exception{
		String tenantCode = LibHelper.getTenantCodeByLib(layoutLibID);
       //取出版面下的稿件
		int articleLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), layoutLibID);
		Document[] articles = docManager.find(articleLibID, "a_layoutID=?", new Object[]{layoutID});
		
		//删除附件的SQL
		String attLib = LibHelper.getLibTable(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
		String delAtt = "delete from " + attLib + " where att_articleID=? and att_articleLibID=?";
		
		try {
			docManager.delete(layoutLibID, layoutID, conn); //删除版面
			InfoHelper.executeUpdate(delAtt,new Object[]{layoutID, layoutLibID}, conn);//删除版面附件
			
			//删除版面下的稿件和稿件的附件
			for (Document article : articles) {
				getFilesToDelete(article.getDocLibID(), article.getDocID(), deleteList, docManager);
				docManager.delete(article.getDocLibID(), article.getDocID(), conn);
				InfoHelper.executeUpdate(delAtt,new Object[]{article.getDocID(), article.getDocLibID()}, conn);
			}
		} catch (Exception e) {
			throw new E5Exception(e);
		}
		//清除redis
		RedisManager.clear(RedisKey.APP_PAPER_LAYOUT_KEY + paperID + "." + paperDate);
		RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layoutID);
		for (Document article : articles) {
			RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + article.getDocID());
		}
	}
	
	private void getFilesToDelete(int docLibID, long docID,List<String> deleteList,DocumentManager docManager) throws E5Exception {
		int attLib = LibHelper.getLibID(DocTypes.PAPERATTACHMENT.typeID(), Tenant.DEFAULTCODE) ;
		Document[] atts = docManager.find(attLib, "att_articleID=? and att_articleLibID=?", new Object[]{docID,docLibID});
		for(Document att : atts){
			deleteList.add(att.getString("att_path"));
		}
	}
	
	//删掉没被引用的图片文件，同时删掉抽图文件
  	private void clear(List<String> deleteList) {
  		for(String path : deleteList){
        	File file = new File(InfoHelper.getFilePathInDevice(path));
        	try {
      			if (file.exists()) file.delete();

      			String fileName = file.getCanonicalPath();
      			
      			file = new File(fileName + ".2");
      			if (file.exists()) file.delete();
      			
      			file = new File(fileName + ".2.jpg");
      			if (file.exists()) file.delete();
      			
      			file = new File(fileName + ".1");
      			if (file.exists()) file.delete();
      			
      			file = new File(fileName + ".1.jpg");
      			if (file.exists()) file.delete();
      			
      			file = new File(fileName + ".0");
      			if (file.exists()) file.delete();
      			
      			file = new File(fileName + ".0.jpg");
      			if (file.exists()) file.delete();
      		} catch (IOException e) {
      			e.printStackTrace();
      		}
        }
  	}

	public void revokeLayout(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document layout = null;
		try {
			layout = docManager.get(docLibID, docID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String date=sdf.format(layout.getDate("pl_date"));
		PublishTrigger.pubOthers(docLibID, docID, date, DocIDMsg.TYPE_REVOKE_PAPER_LAYOUT);
	}

	public String revokeByDate(int paperLibID, int paperID, String paperDate) {
		paperDate = paperDate.replace("-", "");
		PublishTrigger.pubOthers(paperLibID, paperID, paperDate, DocIDMsg.TYPE_REVOKE_PAPER);
		return null;
	}
	
	/**
	 * 查询已转版的稿件
	 * @param articleID 目标稿件ID
	 * @return
	 * @throws E5Exception
	 */
	public String getRelPaperArticles(long targetID,String tenantCode) throws E5Exception{
		int docLibID = LibHelper.getLibID(DocTypes.PAPERARTICLE.typeID(), tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, targetID);
		List<Long> transIDs = getTransIDs(doc);
		JSONArray articles = new JSONArray();
		for(Long transID : transIDs){
			if(transID>0 && transID!=targetID){
				doc = docManager.get(docLibID, transID);
				JSONObject article = new JSONObject();
				article.put("id", doc.getDocID());
				article.put("title", doc.getTopic());
				article.put("layoutName", doc.getString("a_layout"));
				article.put("transStatus", doc.getString("a_transStatus"));
				articles.add(article);
			}
		}
		return articles.toString();
	}
	
	/**
	 * 获取某一期报纸下的所有版次
	 * @param siteId
	 * @param paperId
	 * @param date
	 * @param tenantCode
	 * @return
	 * @throws E5Exception
	 */
	public List<PaperLayout> getPaperLayouts(int siteId,int paperId,Date date,String tenantCode) throws E5Exception{
		int layoutLibId = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), tenantCode);
		String conditions = "pl_date=? and pl_paperID=? and pl_status=1 order by ";
		String sortFlag = InfoHelper.getConfig("数字报", "版面排序");
		if(sortFlag.equals("是")){
			conditions += "pl_order";
		}else {
			conditions += "SYS_DOCUMENTID";
		}
		Object[] params = new Object[] {date, paperId};
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(layoutLibId, conditions, params);
		List<PaperLayout> layouts = new ArrayList<>();
		for(Document doc : docs){
			PaperLayout layout = new PaperLayout(doc);
			layouts.add(layout);
		}
		return layouts;
	}
	/**
	 * 获取某一版面下的所有稿件
	 * 已合成到其他稿件的转版稿件排除
	 * @param libID
	 * @param layoutID
	 * @param keyword
	 * @return
	 * @throws E5Exception
	 */
	public String getLayoutArticles(int libID, long layoutID, String keyword) throws E5Exception{
		Object[] params;
		String conditions = "a_layoutID=?";
		if(!StringUtils.isBlank(keyword)){
			conditions +=" AND (SYS_TOPIC LIKE ? OR a_content LIKE ?) ";
			keyword = "%" + keyword + "%";
			params = new Object[] {layoutID, keyword, keyword};
		}else{
			params = new Object[] {layoutID};
		}
		conditions += " AND SYS_DELETEFLAG=0 order by a_order";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(libID, conditions, params);
		JSONArray articles = new JSONArray();
		for(Document doc : docs){
			JSONObject article = new JSONObject();
			article.put("id", doc.getDocID());
			article.put("type", doc.getInt("a_type"));
			article.put("title", doc.getTopic());
			article.put("author", doc.getAuthors());
			articles.add(article);
		}
		JSONObject result = new JSONObject();
		result.put("layoutID", layoutID);
		result.put("keyword", keyword); 
		result.put("articles", articles); 
		return result.toString();
	}
	
	public String transferArticle(int flag, long targetID, long articleID, String tenantCode) throws E5Exception{
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = LibHelper.getLibID(DocTypes.PAPERARTICLE.typeID(), tenantCode);
		int layoutLibID = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), tenantCode);
		JSONObject result = new JSONObject();
		if (flag>0 && docLibID>0 && targetID>0 && articleID>0){
			Document targetDoc = docManager.get(docLibID,targetID);
			List<Long> transIDs = getTransIDs(targetDoc);
			if(transIDs.size()==0) transIDs.add(targetID);
			boolean hasThis = transIDs.contains(articleID);
			if (flag==1 && !hasThis) { // 合成
				transIDs.add(articleID);
			} else if (flag==2 && hasThis) { //还原
				transIDs.remove(articleID);
			}
			String transArticleIDs = StringUtils.join(transIDs.toArray(new Long[0]), ",");
			int transStatus = 0;
			if(transIDs.size()>1) {
				transStatus=1;
			}
			if (flag==2) {
				transIDs.add(articleID);
			}
			
			for(Long docID : transIDs){
				Document currDoc = docManager.get(docLibID,docID);
				if (flag == 2 && docID == articleID) {
					currDoc.set("a_transArticleIDs", "");
					currDoc.set("a_transStatus", 0);
				} else {
					currDoc.set("a_transArticleIDs", transArticleIDs);
					currDoc.set("a_transStatus", transStatus);
				}
				docManager.save(currDoc);
				
				Document layout = docManager.get(layoutLibID, currDoc.getLong("a_layoutID"));
				if(layout!=null){
					int compilesStatus = layout.getInt("pl_syntheticCount");
					int compilesStatus_new = getSyntheticCount(currDoc.getDocLibID(),layout.getDocID());
					if(compilesStatus != compilesStatus_new){
						layout.set("pl_syntheticCount", compilesStatus_new);
						docManager.save(layout);
					}
				}
				result.put("success", true);
			}
			
		}else{
			result.put("success", false);
			result.put("errMsg", "参数不对");
		}
		return result.toString();
	}
	
	private int getSyntheticCount(int paLibID, long layoutID) throws E5Exception{
		String sql = "select count(SYS_DOCUMENTID) from "+ LibHelper.getLibTable(paLibID)
				+" where a_layoutID="+layoutID+" and a_transStatus=1 and SYS_DELETEFLAG=0";
		int count = 0;
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = E5docHelper.getDBSession(paLibID);
			rs = conn.executeQuery(sql);
			if(rs.next()){
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return count;
	}
	
	private List<Long> getTransIDs(Document doc){
		List<Long> transIDs = new ArrayList<>();
		if(doc!=null){
			String transArticleIDs = doc.getString("a_transArticleIDs");
			if(StringUtils.isBlank(transArticleIDs)){
				transArticleIDs = "";
			}
			String[] transIDStrs =  StringUtils.split(transArticleIDs,",");
			for(String transIDStr : transIDStrs){
				transIDs.add(NumberUtils.toLong(transIDStr));
			}
		}
		return transIDs;
	}
	
	/**
	 * 报纸稿件的转版稿件发布
	 */
	public String publishTransArticle(int docLibID, long currDocID,StringBuilder successDocIDs) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, currDocID);
		List<Long> transIDs = getTransIDs(doc);
		for(Long docID : transIDs){
			if(docID == currDocID) continue;
			Document tdoc = docManager.get(docLibID, docID);
			int layoutID = tdoc.getInt("a_layoutID");
			String tabName = LibHelper.getLibTable(docLibID);
			String updateSql = "update "+ tabName + " set a_status= "+Article.STATUS_PUB_NOT+" where SYS_DOCUMENTID=?";
			try{
				InfoHelper.executeUpdate(updateSql, new Object[]{docID});
			}catch(Exception e){
				return e.getLocalizedMessage();
			}
			//清除redis
			RedisManager.clear(RedisKey.APP_PAPER_ARTICLELIST_KEY + layoutID);
			RedisManager.clear(RedisKey.APP_PAPER_ARTICLE_KEY + docID);
			if (successDocIDs.length() > 0)
				successDocIDs.append(",");
			successDocIDs.append(docID);
		}
		return null;
	}
}
