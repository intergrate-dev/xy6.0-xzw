package com.founder.xy.system.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 归档服务
 */
public class ArchiveJob extends BaseJob{

	public ArchiveJob() {
		super();
		log = Context.getLog("xy.archive");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("开始归档");
		
		//具体的归档逻辑
		//每执行一次都重新读出系统参数，这样方便管理员随时修改
		String reservedCnts = InfoHelper.getConfig("归档", "栏目稿件保留条数");
		String interval = InfoHelper.getConfig("归档", "间隔");
		
		//读租户表，得到部署了归档数据库的租户，对每个租户进行处理
		DocLib docLib = LibHelper.getLib(DocTypes.TENANT.typeID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), "te_archiveDeployed=1 AND SYS_DELETEFLAG=0", null);
		for ( Document doc : docs ) {
			String tenantCode = doc.getString("te_code");
			String tenantName = doc.getString("te_name");
			log.info("开始归档租户-" + tenantName );
			
			//原稿库归档
			log.info("开始归档原稿库：租户-" + tenantName );
			ArchiveOriginalLib( tenantCode, interval );
			log.info("租户-" + tenantName + "原稿库归档完毕");
			
			
			//稿件库归档
			log.info("开始归档稿件库：租户-" + tenantName );
			try {
				ArchiveArticleLib( tenantCode, reservedCnts, interval );
			} catch (Exception e) {
				log.error(e);
			}
			log.info("租户-" + tenantName + "稿件库归档完毕");
			
			log.info("租户-" + tenantName + "归档完毕！");
		}
		log.info("归档完毕");
	}

	/**
	 * 稿件库的归档
	 * @param tenantCode 租户代号
	 * @param reservedCnts 栏目稿件保留条数
	 * @param interval 间隔
	 * @throws Exception 
	 */
	private void ArchiveArticleLib(String tenantCode, String reservedCnts,
			String interval) throws Exception {
		//取租户对应的稿件文档库
		List<DocLib> articleLibs = LibHelper.getLibs( DocTypes.ARTICLE.typeID(), tenantCode );
		//取租户对应的稿件归档文档库
		List<DocLib> archiveLibs = LibHelper.getLibs( DocTypes.ARTICLE.typeID(), "archive_" + tenantCode );

		//一次对稿件库进行归档
		for(int i = 0; i < articleLibs.size(); i++ ){
			DocLib articleLib = articleLibs.get(i);
			DocLib archiveLib = archiveLibs.get(i);
			
			String intervalTime = getIntervalTime(interval);
			//先按主栏目ID统计稿件库中的每个栏目下的没过期的稿件数,把所有稿件数不够的栏目放到Hash
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(*),a_columnID FROM " + articleLib.getDocLibTable() + " ");
			sql.append("WHERE SYS_CREATED >= " + intervalTime + " GROUP BY a_columnID");
			
			DBSession conn = null;
			IResultSet rs = null;	
			Map<String,Long> map = new HashMap<String,Long>();
			try {
				conn = Context.getDBSession(articleLib.getDsID());
				rs = conn.executeQuery(sql.toString());
				while (rs.next()){
					if( rs.getLong(1) < Long.parseLong(reservedCnts) ){
						map.put(rs.getString(2), rs.getLong(1));
					}
				}
			} catch (Exception e) {
				throw new Exception(e);
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}
			
			//查找所有过期的稿件，按创建时间倒序
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = docManager.find( articleLib.getDocLibID(), "SYS_CREATED < ? order by SYS_CREATED desc", new Object[]{intervalTime});
			
			for ( Document doc : docs ) {
				String columnID = doc.getString("a_columnID");
				Long mapValue = map.get(columnID);
				if ( mapValue != null){//稿件的主栏目ID在Hash中，则不归档本稿件
					mapValue ++;
					//count超过限制数时，把栏目ID从Hash中去掉
					if( mapValue > Long.parseLong(reservedCnts) ){
						map.remove(columnID);
					} else {
						map.put(columnID, mapValue);
					}
				} else {
					//若主栏目ID不在Hash中，则直接归档
					//将原稿归档到归档库，这个方法会自动把流程记录也转移到归档库。
					docManager.moveTo(doc, archiveLib.getDocLibID(), doc.getDocID());
					
					//归档稿件的相关扩展字段
					archiveExtFields( doc, tenantCode );
					//归档稿件的相关挂件
					archiveWidgets( doc, tenantCode );
					//归档稿件的相关附件
					archiveAttachments( doc, tenantCode );
					//归档相关稿件库
					archiveArticleRel( doc, tenantCode );
				}
			}	
		}	
	}

	/**
	 * 原稿库的归档
	 * @param tenantCode 租户代号
	 * @param interval 间隔
	 * @throws E5Exception 
	 */
	private void ArchiveOriginalLib(String tenantCode, String interval) throws E5Exception {
		//取租户对应的原稿文档库
		DocLib originalLib = LibHelper.getLib( DocTypes.ORIGINAL.typeID(), tenantCode );
		//取租户对应的原稿归档文档库
		DocLib archiveLib = LibHelper.getLib( DocTypes.ORIGINAL.typeID(), "archive_" + tenantCode );
		
		String intervalTime = getIntervalTime(interval);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find( originalLib.getDocLibID(), "SYS_CREATED < ? order by SYS_CREATED desc", new Object[]{intervalTime});
		
		for ( Document doc : docs ) {
			//将原稿归档到归档库，这个方法会自动把流程记录也转移到归档库。
			docManager.moveTo(doc, archiveLib.getDocLibID(), doc.getDocID());
			
			//归档稿件的相关扩展字段
			archiveExtFields( doc, tenantCode );
			//归档稿件的相关挂件
			archiveWidgets( doc, tenantCode );
			//归档稿件的相关附件
			archiveAttachments( doc, tenantCode );
			
		}
	}

	//归档稿件相关的附件
	private void archiveAttachments(Document doc, String tenantCode) throws E5Exception {
		//取租户对应的附件表
		DocLib attaLib = LibHelper.getLib( DocTypes.ATTACHMENT.typeID(), tenantCode );
		//取租户对应的附件归档文档库
		DocLib attaArchiveLib = LibHelper.getLib( DocTypes.ATTACHMENT.typeID(), "archive_" + tenantCode );
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attas = docManager.find( attaLib.getDocLibID(), "att_articleLibID=? and att_articleID=?",
				new Object[]{doc.getDocLibID(), doc.getDocID()});
		if (attas != null) {
			for (Document atta : attas) {
				//将附件归档到归档库
				docManager.moveTo(atta, attaArchiveLib.getDocLibID(), atta.getDocID());
			}
		}		
	}

	//归档稿件相关的挂件
	private void archiveWidgets(Document doc, String tenantCode) throws E5Exception {
		//取租户对应的挂件表
		DocLib widgetLib = LibHelper.getLib( DocTypes.WIDGET.typeID(), tenantCode );
		//取租户对应的挂件归档文档库
		DocLib widgetArchiveLib = LibHelper.getLib( DocTypes.WIDGET.typeID(), "archive_" + tenantCode );
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] widgets = docManager.find( widgetLib.getDocLibID(), "w_articleLibID=? and w_articleID=?",
				new Object[]{doc.getDocLibID(), doc.getDocID()});
		if (widgets != null) {
			for (Document widget : widgets) {
				//将附件归档到归档库
				docManager.moveTo(widget, widgetArchiveLib.getDocLibID(), widget.getDocID());
			}
		}	
	}

	//归档稿件相关的扩展字段
	private void archiveExtFields(Document doc, String tenantCode) throws E5Exception {
		//取租户对应的扩展字段表
		DocLib extLib = LibHelper.getLib( DocTypes.ARTICLEEXT.typeID(), tenantCode );
		//取租户对应的扩展字段归档文档库
		DocLib extArchiveLib = LibHelper.getLib( DocTypes.ARTICLEEXT.typeID(), "archive_" + tenantCode );
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] extFields = docManager.find( extLib.getDocLibID(), "ext_articleLibID=? and ext_articleID=?",
				new Object[]{doc.getDocLibID(), doc.getDocID()});
		if (extFields != null) {
			for (Document extField : extFields) {
				//将附件归档到归档库
				docManager.moveTo(extField, extArchiveLib.getDocLibID(), extField.getDocID());
			}
		}
	}
	
	//稿件库的相关稿件归档处理
	private void archiveArticleRel(Document doc, String tenantCode) throws E5Exception {
		//取租户对应的相关稿件字段表
		DocLib relLib = LibHelper.getLib( DocTypes.ARTICLEREL.typeID(), tenantCode );
		//取租户对应的相关稿件归档文档库
		DocLib relArchiveLib = LibHelper.getLib( DocTypes.ARTICLEREL.typeID(), "archive_" + tenantCode );
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rels = docManager.find( relLib.getDocLibID(), "a_articleLibID=? and a_articleID=?",
				new Object[]{doc.getDocLibID(), doc.getDocID()});
		if (rels != null) {
			for (Document rel : rels) {
				//将相关稿件归档到归档库
				docManager.moveTo(rel, relArchiveLib.getDocLibID(), rel.getDocID());
			}
		}
	}
	
	
	//获取interval个月前的时间格式
	private String getIntervalTime(String interval){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();  
		cal.setTime(nowDate);  
		cal.add(Calendar.MONTH, - Integer.parseInt(interval));  
		Date intervalDate = cal.getTime();		
		String reStr = sdf.format(intervalDate);
		return reStr;
	}
}