package com.founder.amuc.score;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.Event;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.workspace.param.DocListParam;
import com.founder.e5.workspace.service.DocListService;

/**
 * 积分管理器
 * @author Gong Lijie
 * 2014-6-6
 */
@Component
public class ScoreManager {
	private String ruleFilter = "srEventType_ID=? and (srStartTime is null or srStartTime<=?) and (srEndTime is null or srEndTime>=?) and srStatus=1 and m_siteID=? ";

	/**
	 * 根据数据来源（即应用平台）、时间、行为类型，找到对应的积分规则。
	 * 用于行为入库时检查是否符合积分规则，以便触发积分计算。
	 * @param tenantCode 租户代号
	 * @param sourceID 数据来源（即应用平台）
	 * @param bTypeID 行为类型
	 * @param time 时间
	 * @return List<Rule> 需要再补充Rule中的memberID和eventDate属性，才能传给积分计算服务
	 * @throws E5Exception
	 */
	private List<Rule> getRules(String tenantCode, int bTypeID, Date time, int siteID) throws E5Exception {
		List<Rule> rules = new ArrayList<Rule>();
		
		int docLibID = InfoHelper.getLibID(Constant.DOCTYPE_SCORERULE, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, ruleFilter, 
				new Object[]{bTypeID, time, time, siteID});
		
		for (Document doc : docs) {
			Rule rule = new Rule();
			rule.setTenantCode(tenantCode);
			
			rule.setEventTypeID(bTypeID);
			rule.setRuleID((int)doc.getDocID());
			rule.setRuleType(doc.getInt("srType"));
			rule.setLimit(doc.getInt("srLimit"));
			rule.setScore(doc.getInt("srScore"));
			rule.setExperience(doc.getInt("srExperience"));
			rule.setEventType(doc.getString("srEventType"));
			rule.setSrName(doc.getString("srName"));
			rules.add(rule);
		}
		return rules;
	}
	
	/**
	 * 按行为对象得到规则
	 * @param event
	 * @return
	 * @throws E5Exception
	 */
	public List<Rule> getRules(Event event, int siteID) throws E5Exception {
		List<Rule> rules = getRules(event.getTenantCode(), event.getTypeID(), event.getTime(), siteID);
		if (rules.size() == 0) return null;
		
		//把行为日期的时分秒清零
		Date clearDate = DateUtils.getDate(event.getTime());
		
		for (Rule rule : rules) {
			rule.setMemberID(event.getMemberID());
			rule.setEventDate(clearDate);
		}
		return rules;
	}
	
	/**
	 * 异常积分：置为有效
	 * @param tenantCode
	 * @param docLibID
	 * @param docIDs
	 * @return
	 * @throws Exception
	 */
	public boolean enableUnusual(String tenantCode, int docLibID, long[] docIDs) throws Exception {
		//按租户代号取出积分记录表
		int scoreLibID = InfoHelper.getLibID(Constant.DOCTYPE_MEMBERSCORE, tenantCode);
		
		String memberTable = InfoHelper.getLibTable(Constant.DOCTYPE_MEMBER, tenantCode);
		String sqlAddScore = "update " + memberTable + " set mScore=mScore+?, mExperience=mExperience+? where SYS_DOCUMENTID=?";
		
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();
			
			for (long docID : docIDs) {
				enableOne(sqlAddScore, docLibID, scoreLibID, docID, conn);
			}
			conn.commitTransaction();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**
	 * 异常积分：置为无效
	 * @param docLibID
	 * @param docIDs
	 * @return
	 * @throws Exception
	 */
	public boolean disableUnusual(int docLibID, long[] docIDs) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();
			
			for (long docID : docIDs) {
				//取出异常积分
				Document doc = docManager.get(docLibID, docID, conn);
				doc.set("msIsApproved", 2); //已撤销
				docManager.save(doc, conn);
			}
			conn.commitTransaction();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
			return false;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	//对一个置为有效
	private void enableOne(String sqlAddScore, int docLibID, int scoreLibID, long docID, DBSession conn) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//取出异常积分
		Document doc = docManager.get(docLibID, docID, conn);
		if (doc.getInt("msIsApproved") == 0) {
			doc.set("msIsApproved", 1); //已批准
			docManager.save(doc, conn);
			
			//转存到积分表
			Document copy = docManager.newDocument(doc, scoreLibID, docID);
			copy.set("msMemo", ""); //问题描述，在积分表里不需要
			docManager.save(copy, conn);
			
			//按照会员ID找到会员表，累加积分和经验值
			conn.executeUpdate(sqlAddScore, new Object[]{doc.getInt("msScore"), 
					doc.getInt("msExperience"),  doc.getLong("msMember_ID")});
		}
	}
	
	public String scoreRuleList(String source, String tenantCode) throws E5Exception {
		DocListService listService = (DocListService)Context.getBean("APIDocListService");
		DocListParam param = assembleParam(source, tenantCode);
		listService.init(param);
		int pageSize = listService.getCount();
		param.setBegin(0);
		param.setCount(pageSize);
		param.setCountOfPage(pageSize);
		listService.init(param);
		String result = listService.getDocList();
		System.out.println(result);
		return result;
	}
	
	private DocListParam assembleParam(String source, String tenantCode) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_SCORERULE, tenantCode);
		int docTypeID = docLib.getDocTypeID();
		DocTypeReader docTypeReader = (DocTypeReader)Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFieldsExt(docTypeID);
		String[] nfields = null;
		if (fields != null && fields.length > 0) {
			nfields = new String[fields.length];
			int i = 0;
			for (DocTypeField field : fields) {
				nfields[i] = field.getColumnCode();
				i++;
			}
		}
		DocListParam param = new DocListParam();

		param.setDocLibID(docLib.getDocLibID());
		param.setRuleFormula("");
		param.setCondition("SYS_DELETEFLAG=0 and srSource='" + source + "'");
		param.setTableName(null);
		
		param.setUser(new SysUser());//当前用户
		param.addOrderBy("SYS_DOCUMENTID", true);
		param.setFields(nfields);
		return param;
	}
	
	/**
	 * 保存会员积分记录表中的字段
	 * @param srDoc
	 * @param mDoc
	 */
	public void saveMSValue(Document srDoc,Document mDoc,Document newDoc){
		//会员规则表中的字段值
		String sr_ID = srDoc.getString("SYS_DOCUMENTID");  //积分规则ID
		String source = srDoc.getString("srSource");  //积分规则来源
		String eType = srDoc.getString("srEventType");  //积分规则类型；取自行为类型
		String srEventType_ID = srDoc.getString("srEventType_ID");  //行为id
		String srType = srDoc.getString("srType");  //积分规则类型
		String srMemo = srDoc.getString("srMemo");  //规则描述
		//会员表中的字段值
		long uid = mDoc.getLong("SYS_DOCUMENTID");
		String uname = mDoc.getString("mName");  //会员名称
		//保存字段
		newDoc.set("msRule_ID", sr_ID);
		newDoc.set("msMember", uname);
		newDoc.set("msMember_ID", uid);
		newDoc.set("msEventType", eType);
		newDoc.set("msEvent", eType);
		newDoc.set("msTime", DateHelper.getFormat());
		newDoc.set("msSource", source); 
		newDoc.set("msRuleType", srType);
		newDoc.set("msEventType_ID", srEventType_ID);
		newDoc.set("msMemo", srMemo);
	}
}