package com.founder.amuc.collection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.sys.SysConfigReader;
import com.founder.e5.workspace.ImportSaver;

/**
 * 会员/行为采集辅助类
 * @author Gong Lijie
 * 2014-8-7
 */
public class CollectHelper {
	/**
	 * 取文档类型的扩展字段（非平台字段）
	 * @param docTypeID
	 * @return
	 * @throws E5Exception
	 */
	public static DocTypeField[] getFields(int docTypeID) throws E5Exception {
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] all = docTypeReader.getFieldsExt(docTypeID);
		return all;
	}
	/** 
	* @author  leijj 
	* 功能： 获取所有字段
	* @param docTypeID
	* @return
	* @throws E5Exception 
	*/ 
	public static DocTypeField[] getAllFields(int docTypeID) throws E5Exception {
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] all = docTypeReader.getFields(docTypeID);
		return all;
	}
	/**
	 * 根据ColumnCode找到字段对象
	 * @param all
	 * @param code
	 * @return
	 */
	public static DocTypeField getField(DocTypeField[] all, String code) {
		for (DocTypeField field : all) {
			if (code.equals(field.getColumnCode()))
				return field;
		}
		return null;
	}
	
	//读第一个流程节点
	public static FlowNode getFlowNode(int docTypeID) throws E5Exception {
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		Flow[] flows = flowReader.getFlows(docTypeID);
		if (flows == null) 
			return null;
		return flowReader.getFlowNode(flows[0].getFirstFlowNodeID());
	}
	
	/**
	 * 数据采集-更新规则：系统优先、时间优先
	 * @return 是否支持来源系统的信息覆盖
	 * @throws E5Exception 
	 */
	public static boolean canOverwrite() throws E5Exception {
		int appID = 1;
		String project = "数据采集";
		String item = "更新规则";
		
		SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
		String value = configReader.get(appID, project, item);
		return "时间优先".equals(value);
		
	}
	
	/**
	 * 新建一个对象（会员/行为）
	 * @param docLib
	 * @param docID
	 * @param flowNode
	 * @return
	 * @throws E5Exception
	 */
	public static Document newData(DocLib docLib, long docID, FlowNode flowNode) throws E5Exception {
		if (docID == 0){
			docID = InfoHelper.getID(docLib.getDocTypeID());
		}
		if(docID==1){
			docID=EUID.getID(docLib.getDocLibTable());
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		Document doc = docManager.newDocument(docLib.getDocLibID(), docID);
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.setLocked(false);
		
		Timestamp date = DateUtils.getTimestamp();
		doc.setCreated(date);
		doc.setLastmodified(date);
		
		if (flowNode != null) {
			doc.setCurrentFlow(flowNode.getFlowID());
			doc.setCurrentNode(flowNode.getID());
			doc.setCurrentStatus(flowNode.getWaitingStatus());
		}
		return doc;
	}
	
	/**
	 * 检查数据合法性，自动给键值对字段赋值
	 * @param saver
	 * @param doc
	 * @param fields
	 * @param webRoot
	 * @return
	 */
	public static String checkFields(ImportSaver saver, Document doc, List<DocTypeField> fields, String webRoot) {
		StringBuffer result = new StringBuffer();
		//检查单选、多选、分类等字段是否符合系统内的数据定义
		for (DocTypeField field : fields) {
			if (!saver.checkValue(doc, field, webRoot)) {
				result.append("数据错误，").append(field.getColumnName())
					.append(" 不能是 \"").append(doc.getString(field.getColumnCode()))
					.append("\"。");
			}
		}
		return result.toString();
	}
	
	/** 
	* @author  leijj 
	* 功能： 根据原始系统中的ID找到会员。
	 * 用于会员增量采集、行为数据采集
	* @param docLibID
	* @param oriID 原始ID
	* @param oriTable 原始表
	* @param sourceID
	* @return
	* @throws E5Exception 
	*/ 
	public static Document findMember(int docLibID, String oriID, String oriTable, int sourceID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int oriDocLibID = InfoHelper.getOriMemberLibID(sourceID);
		Document[] ms = null;

		if (!StringUtils.isBlank(oriTable)) {
			ms = docManager.find(oriDocLibID, "mOriID=? and mOriTable=? and mSource_ID=? and SYS_DELETEFLAG=0", new Object[]{oriID, oriTable, sourceID});
			//ms = docManager.find(oriDocLibID, "mOriID=? and mOriTable=? and mSource_ID=? and SYS_DELETEFLAG=0 and mMobile is not null and mMobile<>''", new Object[]{oriID, oriTable, sourceID});
		} else {
			ms = docManager.find(oriDocLibID, "mOriID=? and mSource_ID=? and SYS_DELETEFLAG=0", new Object[]{oriID, sourceID});
			//ms = docManager.find(oriDocLibID, "mOriID=? and mSource_ID=? and SYS_DELETEFLAG=0 and mMobile is not null and mMobile<>''", new Object[]{oriID, sourceID});
		}
		
		if (ms == null || ms.length == 0) {
			return null;
		} else {
			/*
			//修改为：根据原始表会员ID查询原始会员信息
			String memberId = ms[0].getString("SYS_DOCUMENTID");
			//根据原始表手机号查询原始会员信息
			//String mMobile = ms[0].getString("mMobile");
			if (StringUtils.isBlank(memberId)) return null;
			
			ms = docManager.find(oriDocLibID, "SYS_DOCUMENTID=?  and SYS_DELETEFLAG=0", new Object[]{memberId});
			if (ms == null || ms.length == 0){
				return null;
			}else{
				return ms[0];
			}
			*/
			//根据备表的docID在会员-原始数据关联中取出本系统内的会员ID
			String memberId = ms[0].getString("SYS_DOCUMENTID");//备表的docID
			DocLib mrDocLib = InfoHelper.getLib("MEMBERORI", null);
			ms = docManager.find(mrDocLib.getDocLibID(), "mrMemberOriID =? and mrMemberOriLibID=? and SYS_DELETEFLAG=0", new Object[]{memberId,oriDocLibID});
			if (ms == null || ms.length == 0){
				return null;
			}else{
				return ms[0];
//				//根据本系统会员ID返回会员记录document
//				long ucMemberID = ms[0].getDocID(); 
//				oldms =  docManager.find(docLibID, "SYS_DOCUMENTID =? and SYS_DELETEFLAG=0", new Object[]{ucMemberID});
//				if(oldms == null || oldms.length == 0){
//					return null;	
//				}else{
//					return oldms[0];
//				}
			}
			
		}
	}
	//根据原始系统中的ID找到行为。
	public static Document findEvent(int docLibID, String oriID, int sourceID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		Document[] ms = docManager.find(docLibID, "eOriID=? and eSource_ID=? and SYS_DELETEFLAG=0",
				new Object[]{oriID, sourceID});
		
		if (ms == null || ms.length == 0)
			return null;
		else
			return ms[0];
	}
	//取会员名，用于行为入库时
	public static String getMemberName(String tenantCode, long id) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib memberLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		Document member = docManager.get(memberLib.getDocLibID(), id);
		
		return (member == null) ? null : member.getString("mName");
	}
	//取是否潜在会员标识
	public static String getMpotential(String tenantCode, long id) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib memberLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		Document member = docManager.get(memberLib.getDocLibID(), id);
		
		return (member == null) ? null : member.getString("mPotential");
	}
	
	/** 
	* @author  leijj 
	* 功能： 批量保存数据
	* @param docLib
	* @param docs
	* @return
	* @throws E5Exception 
	*/ 
	public static boolean createBatch(DocLib docLib, List<Document> docs) throws E5Exception {
		if(docs == null || docs.size() == 0) return false;
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFields(docLib.getDocTypeID());
		Connection conn = null;
		PreparedStatement state = null;
		try {
			DBSession dbSession = Context.getDBSession(docLib.getDsID());
			conn = dbSession.getConnection();
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into " + docLib.getDocLibTable() + "(");
			StringBuilder column = new StringBuilder("");
			StringBuilder values = new StringBuilder("");
			for(int i = 0; i < fields.length; i++){
				DocTypeField field = fields[i];
				if(i > 0) {
					column.append(",");
					values.append(",");
				}
				column.append(field.getColumnCode());
				values.append("?");
			}
			sql.append(column.toString());
			sql.append(") values(");
			sql.append(values.toString()).append(")");
			
			state = conn.prepareStatement(sql.toString());
			for(int i = 0; i < docs.size(); i++){
				Document event = docs.get(i);
				if(event.get("mScore")==null||event.get("mExperience")==null){
					event.set("mScore", 0);
					event.set("mExperience", 0);
				}
				
				for(int j = 0; j < fields.length; j++){
					DocTypeField field = fields[j];
					int val = j+1;
					state.setObject(val, event.get(field.getColumnCode()));
				}
				state.addBatch();
			}
			state.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(state);
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}
	
	/**
	 * @author  fanjc
	 * 根据原始ID+来源+原始表获取原始会员对象
	 * 用于会员增量采集
	 * @param docLibID
	 * @param oriID
	 * @param oriTable
	 * @param sourceID
	 * @return
	 * @throws E5Exception
	 */
	public static Document findMemberOri(int docLibID, String oriID, String oriTable, int sourceID) throws E5Exception {
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int oriDocLibID = InfoHelper.getOriMemberLibID(sourceID);
		Document[] ms = null;
		if (!StringUtils.isBlank(oriTable)) {
			ms = docManager.find(oriDocLibID, "mOriID=? and mOriTable=? and mSource_ID=? and SYS_DELETEFLAG=0 ORDER BY SYS_DOCUMENTID DESC ", new Object[]{oriID, oriTable, sourceID});
		} else {
			ms = docManager.find(oriDocLibID, "mOriID=? and mSource_ID=? and SYS_DELETEFLAG=0 ORDER BY SYS_DOCUMENTID DESC ", new Object[]{oriID, sourceID});
		}
		if (ms == null || ms.length == 0) {
			return null;
		} 
		return ms[0];
	}
	/**
	 * @author  fanjc
	 * 根据原始ID+来源+原始表获取主会员表的对象
	 * 用于会员增量采集
	 * @param tenantCode
	 * @param oriID
	 * @param oriTable
	 * @param sourceID
	 * @return
	 * @throws E5Exception
	 */
	public static Document findUcMember(String tenantCode,String oriID, String oriTable, int sourceID) throws E5Exception {
		
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;

		String sql = null;
		Object[] params = null;
		if (!StringUtils.isBlank(oriTable)) {
			sql = "mrOriID=? and mrOriTable=? and mrSource_ID=? and SYS_DELETEFLAG=0";
			params = new Object[]{oriID, oriTable, sourceID};
		} else {
			sql = "mrOriID=? and mrSource_ID=? and SYS_DELETEFLAG=0";
			params = new Object[]{oriID, sourceID};
		}
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERORI, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] members = docManager.find(docLib.getDocLibID(), sql, params);
		if (members.length > 0) {
			Document member = members[0];
			while (member != null) {
				DocLib mdocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
				String querySql="SYS_DOCUMENTID=? and SYS_DELETEFLAG=0";
				Object[] queryParams=new Object[]{member.get("mrMemberID")};
				Document[] members2=docManager.find(mdocLib.getDocLibID(), querySql, queryParams);
				if(members2!=null&&members2.length>0){
					Document member2=members2[0];
					while (member2 != null) {
						long mergeID = member2.getLong("mMergeID");
						if (mergeID == 0 || mergeID == member2.getDocID()) {
							return member2;
						} 
						member2 = docManager.get(mdocLib.getDocLibID(), mergeID);
					}
				}
				
			}
		}
		return null;
	}
}