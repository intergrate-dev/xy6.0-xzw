package com.founder.xy.article;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.DBException;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.rel.service.RelTableReader;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.ArticleMsg;


/**
 * 稿件管理器
 * 
 * @author Gong Lijie
 */
@Component
public class ArticleManager {
	@Autowired
	private ColumnReader colReader;
	
	/**
	 * 把稿件的主栏目、关联栏目、聚合栏目都加起来作为a_columnAll。
	 * 用于稿件新建、修改、移动，以及原稿签发时
	 */
	public void setColumnAll(Document article) {
		Set<Long> allIdSet = new TreeSet<Long>();
		//主栏目
		long mainColumnId = article.getLong("a_columnID");
		if (mainColumnId > 0){
			allIdSet.add(mainColumnId);
			
			//关联栏目
			String relColumnId = article.getString("a_columnRelID");
			if (!StringUtils.isBlank(relColumnId)){
				long[] ids = StringUtils.getLongArray(relColumnId);
				for (long id : ids) {
					allIdSet.add(id);
				}
			}
			
			//聚合栏目
			int colLibID = getColLibID(article.getDocLibID());
			Set<Long> aggregatedSet = colReader.getAggregators(colLibID, mainColumnId);
			if (aggregatedSet != null) {
				for (Long id : aggregatedSet) {
					allIdSet.add(id);
				}
			}
		}
		//以;为分隔（存储过程的拆分要求）
		StringBuilder column_allId = new StringBuilder();
		for (Long l : allIdSet){
			if (column_allId.length() > 0)
				column_allId.append(";");
			column_allId.append(l);
		}
		article.set("a_columnAll", column_allId.toString());
	}
	
	/**
	 * 按主栏目设置流程。
	 * 用于稿件新建、复制、移动、以及原稿签发时
	 */
	public void setFlowByColumn(Document doc) {
		//若无栏目（可能是原稿），则无流程
		int colID = doc.getInt("a_columnID");
		if (colID <= 0) {
			removeFlow(doc);
			return;
		}
		try {
			int colLibID = getColLibID(doc.getDocLibID());
			
			int flowID = getFlowID(colLibID, colID);
			if (flowID <= 0) {
				removeFlow(doc);
				return;
			}
			
			FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
			Flow flow = flowReader.getFlow(flowID);
			FlowNode node = flowReader.getFlowNode(flow.getFirstFlowNodeID());
			
			doc.setCurrentFlow(flowID);
			doc.setCurrentNode(flow.getFirstFlowNodeID());
			doc.setCurrentStatus(node.getWaitingStatus());
			doc.set("a_status", Article.STATUS_PUB_NOT);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setFlowByCat(Document doc, String tenantCode) {
		int catID = doc.getInt("a_catID");
		try {
			int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), tenantCode);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document columnori = docManager.get(colLibID, catID);
			
			int flowID = columnori.getInt("COL_FLOW_ID");
			if (flowID <= 0) {
				removeFlow(doc);
				return;
			}
			
			FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
			Flow flow = flowReader.getFlow(flowID);
			FlowNode node = flowReader.getFlowNode(flow.getFirstFlowNodeID());
			
			doc.setCurrentFlow(flowID);
			doc.setCurrentNode(flow.getFirstFlowNodeID());
			doc.setCurrentStatus(node.getWaitingStatus());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清除稿件的流程。
	 * 用于其它渠道稿件
	 */
	public void removeFlow(Document doc) {
		doc.setCurrentFlow(0);
		doc.setCurrentNode(0);
		doc.setCurrentStatus("");
	}
	
	/**
	 * 彻底删除稿件，包括流程记录、附件、挂件
	 */
	public int delete( int docLibID, long docID ) throws E5Exception {
		DBSession dbSession = null;
		int result = 0;
		try {
			dbSession = E5docHelper.getDBSession( docLibID );
			dbSession.beginTransaction();

			result = delete(docLibID, docID, dbSession);

			dbSession.commitTransaction();
		} catch ( Exception e ) {
			ResourceMgr.rollbackQuietly( dbSession );
			
			if ( e instanceof E5Exception )
				throw (E5Exception)e;
			else
				throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly( dbSession );
		}
		return result;
	}

	/** 删除稿件的挂件 */
	public void deleteWidgets(int docLibID, long docID, DBSession dbSession) throws E5Exception {
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(), docLibID);
		String tableName = LibHelper.getLibTable(libID);
		
		String sql = "delete from " + tableName + " where w_articleID=? and w_articleLibID=?";
		Object[] params = new Object[]{docID, docLibID};
		
		InfoHelper.executeUpdate(sql, params, dbSession);
	}
	
	/** 读稿件的附件 */
	public Document[] getAttachments(int docLibID, long docID) throws E5Exception {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		return docManager.find(attLibID, "att_articleID=? and att_articleLibID=?",
				new Object[]{docID, docLibID});
	}
	
	/** 读稿件的挂件 */
	public Document[] getWidgets(int docLibID, long docID) throws E5Exception {
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(), docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		return docManager.find(libID, "w_articleID=? and w_articleLibID=?",
				new Object[]{docID, docLibID});
	}
	
	/** 读稿件的相关稿件 */
	public Document[] getRels(int docLibID, long docID) throws E5Exception {
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEREL.typeID(), docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		return docManager.find(libID, "a_articleID=? and a_articleLibID=?",
				new Object[]{docID, docLibID});
	}
	
	/**
	 * 取新稿件的顺序号，根据发布时间、稿件ID综合计算
	 * @param article
	 */
	public double getNewOrder(Document article) {
		Timestamp pubTime = article.getTimestamp("a_pubTime");
		if (pubTime == null) return 0;
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(pubTime);
		
		double order = createDisplayOrder(ca, 0, 0, article.getDocID());
		return order;
	}
	
	/**
	 * 原翔宇系统中的计算稿件排序的算法
	 * 
     * 本方法旨在保证原有稿件顺序数据可用的情况下减少初始稿件顺序的有效数字个数
     * @param cd 指定稿件顺序时指定的日期
     * @param daycnt 变更的天数
     * @param ord 指定稿件顺序时指定的顺序(0 ~ 9)
     * @param id 稿件id
     * @return 计算出的稿件顺序，取负数，避免sql中使用倒序
     * 
     * 计算方法:  距离起始点的天数*10000 + 指定稿件顺序时指定的顺序*1000 + 稿件id后六位/100
     * 这里为了减少初始稿件顺序时的有效数字个数,只取了稿件id后六位作为标识
     * 考虑到每100万条数据才可能出现重复 本算法忽略这种可能
	 */
	@SuppressWarnings("deprecation")
	public double createDisplayOrder(Calendar cd, int daycnt, int ord, long id) {
		if (cd == null)
		    cd = Calendar.getInstance();

        int nHour = cd.get(Calendar.HOUR_OF_DAY);
        int nMinute = cd.get(Calendar.MINUTE);

		cd.set(Calendar.HOUR_OF_DAY, 0);
		cd.set(Calendar.MINUTE, 0);
		cd.set(Calendar.SECOND, 0);
		cd.set(Calendar.MILLISECOND, 0);

		Calendar dd = Calendar.getInstance();

		String startDate = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
		if (startDate == null || "".equals(startDate)) {
			dd.set(2015, 4, 13, 0, 0, 0); // 新起点~
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date date = sdf.parse(startDate);
					dd.set(date.getYear() + 1900, date.getMonth() + 1, date.getDate(),
							0, 0, 0);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		}
        dd.set(java.util.Calendar.HOUR_OF_DAY, 0);
        dd.set(java.util.Calendar.MINUTE, 0);
        dd.set(java.util.Calendar.SECOND, 0);
        dd.set(java.util.Calendar.MILLISECOND, 0);

		long tt = cd.getTimeInMillis() - dd.getTimeInMillis();
		double ret = 0;
		
	/*	
		87654321.12345678：
		[------][0][00][0.0][000000] 
		万位以上是天数，千位的是优先级，百位和十位是小时数，个位和小数点后第一位是分钟，小数点后第二位开始连续6位是ID
		 long days = (tt / 1000 * 60 * 60 * 24); //毫秒数转成天数
		 days += daycnt;		//天数再加上置顶天数
		 ret = days * 10000 + priority * 1000 + hour * 10 + minutes * 0.1 + ID后六位放在小数点后第二位开始 
	 */		
		ret = (long) ( (double) (tt) / 8640.0)
				+ daycnt * 10000
				+ (double)ord * 1000 
				+ (double)nHour*10.0 + (double)nMinute*0.1
				+ (double)(id % 1000000)*0.0000001;
		return ret * -1;
	}
	
	/**
	 * 根据主栏目与用户设置流程
	 */
	public void tryPublish(Document doc, long colID, int roleID) throws E5Exception {
		//获取主栏目的对应流程ID
		int colLibID = getColLibID(doc.getDocLibID());
		int flowID = getFlowID(colLibID, colID);
		
		//一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
	
		for(FlowNode fn : nodes){
			System.out.println(fn.getID() + "--" + fn.getWaitingStatus());
		}
		
		if (1 == flowID){//无审批流程
			judgeAndPublish(doc, roleID, flowID, nodes, nodes[0]);
		} else{//审批流程，设置为下一个流程节点
			FlowNode curNode = getCurrentNode(nodes, doc.getCurrentNode());
			curNode = getNextNode(nodes, curNode.getID());
			judgeAndPublish(doc, roleID, flowID, nodes, curNode);
			
			if (isPublishing(nodes, doc.getCurrentNode()))
				doc.set("a_status", Article.STATUS_PUB_ING);
			else
				doc.set("a_status", Article.STATUS_AUDITING);
		}
	}

    /**
     * 根据主栏目与用户设置流程
     */
    public void tryPublish(Document doc, int colID, int roleID) throws E5Exception {
        //获取主栏目的对应流程ID
        int colLibID = getColLibID(doc.getDocLibID());
        int flowID = getFlowID(colLibID, Long.valueOf(colID));

        //一个流程的所有的流程节点
        FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
        FlowNode[] nodes = flowReader.getFlowNodes(flowID);

        for(FlowNode fn : nodes){
            System.out.println(fn.getID() + "--" + fn.getWaitingStatus());
        }

        if (1 == flowID){//无审批流程
            judgeAndPublish(doc, roleID, flowID, nodes, nodes[0]);
        } else{//审批流程，设置为下一个流程节点
            FlowNode curNode = getCurrentNode(nodes, doc.getCurrentNode());
            curNode = getNextNode(nodes, curNode.getID());
            judgeAndPublish(doc, roleID, flowID, nodes, curNode);

            if (isPublishing(nodes, doc.getCurrentNode()))
                doc.set("a_status", Article.STATUS_PUB_ING);
            else
                doc.set("a_status", Article.STATUS_AUDITING);
        }
    }

	/**
	 * 根据主栏目与发布权限设置流程
	 * @param doc
	 * @param colID
	 * @param canPublish
	 * @throws E5Exception
	 */
	public void tryPublish(Document doc,int colID, boolean canPublish) throws E5Exception {
		//获取主栏目的对应流程ID
		int colLibID = getColLibID(doc.getDocLibID());
		int flowID = getFlowID(colLibID, colID);
		
		//一个流程的所有的流程节点
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		FlowNode[] nodes = flowReader.getFlowNodes(flowID);
	
		if (1 == flowID){//无审批流程
			if(canPublish){//有发布权限，且用户要求发布才可以发布，设置为第二个流程节点的对应值，并调用发布处理
				initFlowDoc(doc,flowID,nodes[1]);
				doc.set("a_status", Article.STATUS_PUB_ING);
			} else {//设置当前稿件的currentFlow/currentNode/currentStatus为第一个流程节点的对应值
				initFlowDoc(doc,flowID,nodes[0]);
				doc.set("a_status", Article.STATUS_PUB_NOT);
			}
		} else if (2 == flowID){//审批流程，取出流程的第二个流程节点设置currentFlow/currentNode/currentStatus
			initFlowDoc(doc,flowID,nodes[1]);
			doc.set("a_status", Article.STATUS_AUDITING);
		}
	}
	
	/**根据稿件库，稿件ID触发发布事件*/
	public void publish(List<Document> articles){
		for (Document article : articles) {
			PublishTrigger.article(article);
		}
	}
	
	/**根据稿件库，稿件ID触发撤稿事件*/
	public void revoke(List<Document> articles){
		for (Document article : articles) {
			PublishTrigger.articleRevoke(article);
		}
	}
	
	/**判断一个稿件库的渠道，0表示渠道1，1表示渠道2，etc*/
	public int getChannelForLib(HttpServletRequest request, int docLibID) throws E5Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);
		for (int i = 0; i < articleLibs.size(); i++) {
			if (articleLibs.get(i).getDocLibID() == docLibID)
				return i;
		}
		return -1;
	}
	
	/**
	 * 按栏目发布时，取出所有需要重新发布的稿件的ID（查主栏目是指定栏目ID的稿件）
	 * 
	 * @param articleLibID 操作所在的稿件发布库ID
	 * @param cID 指定的栏目
	 * @param begin 起始时间
	 * @param end	截止时间
	 */
	public List<Document> getIDs4ColPub(int articleLibID, long cID, Date begin, Date end) throws E5Exception {
		//按时间条件组织SQL
		String sql = null;
		Object[] params = null;
		
		if (begin != null && end != null) {
			sql = "a_columnID=? and (a_pubTime between ? and ?) and a_status=" + Article.STATUS_PUB_DONE;
			params = new Object[]{cID, begin, end};
		} else if (begin != null) {
			sql = "a_columnID=? and a_pubTime>=? and a_status=" + Article.STATUS_PUB_DONE;
			params = new Object[]{cID, begin};
		} else if (end != null) {
			sql = "a_columnID=? and a_pubTime<=? and a_status=" + Article.STATUS_PUB_DONE;
			params = new Object[]{cID, end};
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		params[0] = cID;
		Document[] docs = docManager.find(articleLibID, sql, params);

		return Arrays.asList(docs);
	}
	
	/**
	 * 在稿件设置为发布后，检查是否定时发布：
	 * 若稿件的发布时间晚于当前时间，则改成定时发布状态。
	 * 
	 * 用于稿件保存、稿件发布操作
	 * @param doc
	 */
	public void changeTimedPublish(Document doc) {
		if (doc.getInt("a_status") == Article.STATUS_PUB_ING) {
			Timestamp pubTime = doc.getTimestamp("a_pubTime");
			
			if (pubTime != null && pubTime.after(DateUtils.getTimestamp())) {
				doc.set("a_status", Article.STATUS_PUB_TIMED);
			}
		}
	}
	
	/**
	 * 判断一个文件后缀是否是“文档”稿件（word、ppt、excel、pdf文件）
	 * @param suffix
	 * @return
	 */
	public boolean isFile(String suffix) {
		return "docx".equals(suffix)
				|| "doc".equals(suffix)
				|| "pptx".equals(suffix)
				|| "ppt".equals(suffix)
				|| "xlsx".equals(suffix)
				|| "xls".equals(suffix)
				|| "pdf".equals(suffix)
				|| "txt".equals(suffix)
				|| "odt".equals(suffix)
				;
	}
	
	/**
	 * Gets i ds 4 col pub bycount.
	 *
	 * @param articleLibID the article lib id
	 * @param cID        the col id
	 * @param count        发布稿件数量
	 * @return the i ds 4 col pub bycount
	 * @throws E5Exception the e 5 exception
	 */
	public List<Document> getIDs4ColPubByCount(int articleLibID, long cID, int count) throws E5Exception {

		String sql = "SELECT SYS_DOCUMENTID FROM "+getRelTableName(articleLibID)+" WHERE class_1 = ? AND a_status = " + Article.STATUS_PUB_DONE+ " ORDER BY a_order ";
		Object[] params;
		//按每个栏目查出数量范围内的稿件ID
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		IResultSet rs;
		List<Long> docIDs= new ArrayList<>();
		Document[] docs;
		try {
			conn = Context.getDBSession();
			sql = conn.getDialect().getLimitString(sql, 0, count);

			params = new Object[]{cID};
			rs = conn.executeQuery(sql, params);
			while (rs.next()) {
				//取稿件ID
				docIDs.add(rs.getLong("SYS_DOCUMENTID"));
			}
			long[] idArr = InfoHelper.getLongArray(docIDs);
			//取稿件
			docs = docManager.get(articleLibID, idArr);
			//按栏目分别加入结果集

		} catch (Exception e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}

		return Arrays.asList(docs);
	}

	/**
	 * 读出稿件的上下篇，用于发布服务中重新发布稿件的上下篇
	 */
	public List<ArticleMsg> getNearArticles(ArticleMsg data) {
		//只对网站稿件，且需参数开关打开
		if (data.getChannel() != 1 || !"是".equals(InfoHelper.getConfig("发布服务", "重发上下篇")))
			return null;
		
		String tableName = getRelTableName(data.getDocLibID());
		
		//从稿件所在主栏目的列表中取上一个
		String sql = " SELECT SYS_DOCUMENTID FROM " + tableName
				+ " WHERE CLASS_1=? and a_order=("
				+ " SELECT min(a_order) FROM " + tableName
				+ " WHERE CLASS_1=?"
				+ " AND a_order>(SELECT a_order FROM " + tableName + " WHERE SYS_DOCUMENTID=? and CLASS_1=?)"
				+ " AND a_status in (1,3))";
		ArticleMsg prevArt =  findArticle(sql, data);
		
		//从稿件所在主栏目的列表中取下一个
		sql = " SELECT SYS_DOCUMENTID FROM " + tableName
				+ " WHERE CLASS_1=? and a_order=("
				+ " SELECT max(a_order) FROM " + tableName
				+ " WHERE CLASS_1=?"
				+ " AND a_order<(SELECT a_order FROM " + tableName + " WHERE SYS_DOCUMENTID=? and CLASS_1=?)"
				+ " AND a_status in (1,3))";
		ArticleMsg nextArt = findArticle(sql, data);
		
		List<ArticleMsg> nearArticles = new ArrayList<ArticleMsg>();
		if (prevArt != null) nearArticles.add(prevArt);
		if (nextArt != null) nearArticles.add(nextArt);
		
		return nearArticles.size() > 0 ? nearArticles : null;
	}
	
	
	/**
	 * 判断一篇稿件是否已发布
	 */
	public static boolean hasPublished(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(docLibID, docID);
			return (doc != null && doc.getInt("a_status") == Article.STATUS_PUB_DONE);
		} catch (E5Exception e) {
			return false;
		}
	}

	//按要求取出所有的栏目ID
	public List<Long> getColumnIDs(int colLibID, long colID, int type) throws E5Exception {
		List<Long> colIDs = new ArrayList<>();
		
		colIDs.add(colID);
		if (type == 1) {
			List<Column> subs = colReader.getSub(colLibID, colID);
			if (subs != null) {
				for (Column column : subs) {
					colIDs.add(column.getId());
				}
			}
		} else if (type == 2) {
			addColChildren(colLibID, colIDs, colID);
		}
		return colIDs;
	}

	public void saveLinkTitle(Document doc0, int currentColID, boolean syncLinkTitle) {
		// 更新栏目稿件的链接标题：根据SYS_DOCUMENTID和CLASS_1（栏目ID）
		int DocLibID = doc0.getDocLibID();
		String relTable = InfoHelper.getRelTable(DocLibID);
		String sql = "update " + relTable
				+ " set a_linkTitle=? where SYS_DOCUMENTID=? ";
		try {
			if(syncLinkTitle){
			InfoHelper.executeUpdate(DocLibID, sql, new Object[] { doc0.getString("a_linkTitle"),
							doc0.getDocID()});
			}
			sql = "update " + relTable + " set a_linkTitle=?, a_attr=? where SYS_DOCUMENTID=?  and CLASS_1=? ";
			InfoHelper.executeUpdate(DocLibID, sql, new Object[] { doc0.getString("a_linkTitle"),doc0.getInt("a_attr"),
					doc0.getDocID(), currentColID });
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	//稿件修改界面，点“发布”时，根据权限修改稿件状态
	private void judgeAndPublish(Document doc, int roleID, int flowID, FlowNode[] nodes, FlowNode curNode) throws E5Exception {
		boolean canPublish = false;
		boolean canAuditing = false;
		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		if (flowID == 1) {
			//若主栏目是无审批流程，那么得需要判断是否有发布权限
			canPublish = fpReader.hasPermission(roleID, flowID, curNode.getID(), "发布");
		}else{
			//若主栏目有审批流程，那么得需要判断是否有送审权限
			canAuditing = fpReader.hasPermission(roleID, flowID, curNode.getID(), "送审");
		}
		
		if (canPublish){//有发布权限，且用户要求发布才可以发布，设置为第二个流程节点的对应值，并调用发布处理
			FlowNode nextNode = getNextNode(nodes, curNode.getID());
			if (nextNode != null) {
				initFlowDoc(doc, flowID, nextNode);
				doc.set("a_status", Article.STATUS_PUB_ING);
			}
		} else if (canAuditing) {
			if(nodes[0].getID() == curNode.getID())
				curNode = getNextNode(nodes, curNode.getID());
			if (curNode != null) {
				initFlowDoc(doc, flowID, curNode);
				doc.set("a_status", Article.STATUS_AUDITING);
			}
		} else {//设置当前稿件的currentFlow/currentNode/currentStatus为第一个流程节点的对应值
			initFlowDoc(doc, flowID, curNode);
			doc.set("a_status", Article.STATUS_PUB_NOT);
		}
	}

	//审批状态时，取下一个审批节点
	private FlowNode getNextNode(FlowNode[] nodes, int curNodeID) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].getID() == curNodeID && i < nodes.length - 1)
				return nodes[i + 1];
		}
		return nodes[0];
	}

	//审批状态时，取当前审批节点
	private FlowNode getCurrentNode(FlowNode[] nodes, int curNodeID) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].getID() == curNodeID)
				return nodes[i];
		}
		return nodes[0]; //新建稿被初始化为无流程的节点1，若不符合当前流程，改为当前流程的节点1
	}

	//判断审批状态提交时是不是该发布了。倒数第二个节点是在发布
	private boolean isPublishing(FlowNode[] nodes, int curNodeID) {
		return (nodes.length > 2 && nodes[nodes.length - 2].getID() == curNodeID);
	}

	//初始化稿件箱的流程节点
	private void initFlowDoc(Document doc,int flowID,FlowNode node) throws E5Exception {
		doc.setCurrentFlow(flowID);
		doc.setCurrentNode(node.getID());
		doc.setCurrentStatus(node.getWaitingStatus());
	}

	private int delete( int docLibID, long docID, DBSession dbSession ) throws E5Exception {
		String tablename = E5docHelper.getDocLib( docLibID ).getDocLibTable();
	
		StringBuffer sb = new StringBuffer();
		sb.append( "delete from " ).append( tablename ).append( " where " ).append(
				E5docHelper.DOCUMENTID ).append( "=?" );
		String sql = sb.toString();
		Object[] param = { new Long( docID ) };
	
		try {
			// 删除稿件
			int rt = dbSession.executeUpdate( sql, param );
			if ( rt > 0 ) {
				// 删除流程记录
				E5docHelper.deleteAssociatedFRs( dbSession, docLibID, docID );
				
				//删除附件
				sql = "delete from xy_attachment where att_articleID=? and att_articleLibID=?";
				Object[] params = new Object[]{docID, docLibID};
				InfoHelper.executeUpdate(sql, params, dbSession);
				
				//删除挂件
				deleteWidgets(docLibID, docID, dbSession);
			}
			return rt;
		} catch ( SQLException e ) {
			throw new DBException( e );
		}
	}

	public int getFlowID(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? 0 : col.getFlowID();
	}

	//递归方法：取栏目的子孙栏目
	private void addColChildren(int colLibID, List<Long> colIDs, long colID) throws E5Exception {
		List<Column> subs = colReader.getSub(colLibID, colID);
		if (subs != null) {
			for (Column column : subs) {
				colIDs.add(column.getId());
				addColChildren(colLibID, colIDs, column.getId());
			}
		}
	}
	private int getColLibID(int otherLibID) {
		return LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), otherLibID);
	}

	//找到上下篇ArticleMsg参数
	private ArticleMsg findArticle(String sql, ArticleMsg data){
		//按Order查出ID
		DBSession conn = null;
		IResultSet rs = null;
		
		long nextArtID = 0;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, new Object[] {data.getColID(), data.getColID(), data.getId(), data.getColID()});
			if (rs.next())
				nextArtID = rs.getLong("SYS_DOCUMENTID");
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

		//拼出参数
		if (nextArtID > 0) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			try {
				Document article = docManager.get(data.getDocLibID(), nextArtID);
				if (article != null) {
					return PublishTrigger.getArticleMsg(article);
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 取稿件栏目关联表的表名，用于查询
	 * @param docLibID
	 * @return
	 */
	private String getRelTableName(int docLibID) {
		RelTableReader relReader = (RelTableReader)Context.getBean(RelTableReader.class);
		return relReader.getRelTableName(docLibID, CatTypes.CAT_COLUMNARTICLE.typeID());
	}

	public void deleteAttachment(int docLibID, long docID) {

		DBSession dbSession = null;
		try {
			dbSession = E5docHelper.getDBSession( docLibID );
			dbSession.beginTransaction();

			//删除附件
			String sql = "delete from xy_attachment where att_articleID=? and att_articleLibID=?";
			Object[] params = new Object[]{docID, docLibID};
			InfoHelper.executeUpdate(sql, params, dbSession);

			dbSession.commitTransaction();
		} catch ( Exception e ) {
			ResourceMgr.rollbackQuietly( dbSession );
		} finally {
			ResourceMgr.closeQuietly( dbSession );
		}
	}

	public void recordHistoryVersion(DocumentManager docManager, Document article, 
			String tenantCode, String operation, String username) throws E5Exception {
		if(docManager == null) docManager = DocumentManagerFactory.getInstance();
		long newHistoryID = InfoHelper.getNextDocID(DocTypes.HISTORYORI.typeID());
		int hisLibID = LibHelper.getLibID(DocTypes.HISTORYORI.typeID(), tenantCode);
        Document newHistory = docManager.newDocument(article, hisLibID, newHistoryID);
        newHistory.set("SYS_CREATED", article.getCreated());
        newHistory.set("SYS_LASTMODIFIED", article.getLastmodified());
        System.out.println(article.getAuthors());
        newHistory.set("SYS_TOPIC", article.getString("SYS_TOPIC"));
        newHistory.set("a_originalID", article.getDocID());
        newHistory.set("a_operation", operation);
        newHistory.set("a_operator", username);
        newHistory.setCreated(DateUtils.getTimestamp());
        docManager.save(newHistory);
	}
	public void recordHistoryVersionForWXGroup(DocumentManager docManager, Document article,
			String tenantCode, String operation, String username) throws E5Exception {
		if(docManager == null) docManager = DocumentManagerFactory.getInstance();
		long newHistoryID = InfoHelper.getNextDocID(DocTypes.HISTORYORI.typeID());
		int hisLibID = LibHelper.getLibID(DocTypes.HISTORYORI.typeID(), tenantCode);
        Document newHistory = docManager.newDocument(article, hisLibID, newHistoryID);
        newHistory.set("SYS_CREATED", article.getCreated());
        newHistory.set("SYS_LASTMODIFIED", article.getLastmodified());
        System.out.println(article.getAuthors());
        newHistory.set("SYS_TOPIC", article.getString("SYS_TOPIC"));
        newHistory.set("a_originalID", article.get("WX_ARTICLEID"));
        newHistory.set("a_operation", operation);
        newHistory.set("a_operator", username);
        newHistory.setCreated(DateUtils.getTimestamp());
        newHistory.set("a_abstract",newHistory.get("WX_ABSTRACT"));
        newHistory.set("a_content",newHistory.get("WX_CONTENT"));
        newHistory.set("a_status",newHistory.get("WX_STATUS"));
        newHistory.set("a_picBig",newHistory.get("WX_PIC"));
        newHistory.set("wx_groupid",newHistory.get("wx_groupId"));
        newHistory.set("type",1);
        docManager.save(newHistory);
	}

	//清除app稿件缓存
    public void clearAppKey(Document doc) {
	    long id = doc.getDocID();
	    //清除当前稿件缓存
        RedisManager.clear(RedisKey.APP_ARTICLE_KEY + id);
        RedisManager.clear(RedisKey.APP_ARTICLELIST_ONE_KEY + id);

        //清除栏目稿件缓存
        String columnAll = doc.getString("a_columnAll");
        if(!StringUtils.isBlank(columnAll)){
            String[] columnArray = columnAll.split(";");
            int columnLength = columnArray.length;
            for(int i=0;i<columnLength;i++){
                String colID = columnArray[i];
                RedisManager.clearLongKeys(RedisKey.APP_ARTICLELIST_AD_KEY + colID);
                //子栏目稿件列表
                RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".0");
                RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".1");
                RedisManager.clear(RedisKey.APP_ARTICLELIST_SUBCOLUMN_KEY + colID + ".2");
            }

        }
    }
}