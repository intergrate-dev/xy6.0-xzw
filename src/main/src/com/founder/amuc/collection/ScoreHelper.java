package com.founder.amuc.collection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.DateHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.Event;
import com.founder.amuc.score.Rule;
import com.founder.amuc.score.RuleProcessor;
import com.founder.amuc.score.ScoreManager;
import com.founder.amuc.tenant.Tenant;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

/**
 * 采集时行为的积分相关处理，涉及行为修改时重新计算积分的复杂逻辑
 * @author Gong Lijie
 * 2014-7-31
 */
public class ScoreHelper {
	/**
	 * 新行为采集时计算积分
	 * @param memberName
	 * @param data
	 * @param isTrading
	 * @throws Exception
	 */
	public static String createScore(String tenantCode, String memberName, 
			Document data, boolean isTrading, int siteID) throws Exception{
		Event event = assembleEvent(data, isTrading);
		event.setTenantCode(tenantCode);
		event.setMember(memberName);
		
		//找出符合的积分规则
		ScoreManager scoreManager = ContextLoader.getCurrentWebApplicationContext().getBean(ScoreManager.class);
		List<Rule> rules = scoreManager.getRules(event,siteID);
		if (rules == null)
			return "null";
		
		//多个积分规则的计算，放在事务中
		RuleProcessor rp = new RuleProcessor();
		DBSession conn = null;
		List<JSONObject> ruleList=new ArrayList<JSONObject>();
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();
			
			for (Rule rule : rules) {
				
				int reScore = rp.process(rule, event, conn, siteID);
				JSONObject obj = new JSONObject();
				obj.put("score", reScore);
				obj.put("srname", rule.getSrName());
				if(reScore == -1){
					obj.put("code","1003");
					obj.put("msg", "计算积分失败,超出积分规则计分次数");
				}else if(reScore == -2){
					obj.put("code","1003");
					obj.put("msg", "计算积分失败,超出会员每日积分上限");
				}else if(reScore == -3){
					obj.put("code","1003");
					obj.put("msg", "计算积分失败,属于异常积分");
				}else{
					obj.put("code","1004");
					obj.put("msg","计算积分成功");
				}
				ruleList.add(obj);
			}
			conn.commitTransaction();
			
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
			throw e;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		JSONArray jsonArray=new JSONArray();
		jsonArray.addAll(ruleList);
		  
		return jsonArray.toJSONString();
	}
	
	/**
	 * 删除一个行为时，删除其积分记录：
	 * 1）正常积分：加一个负总分、会员表里扣减
	 * 2）异常积分：删除
	 * @param memberName 会员名
	 * @param data 行为
	 * @throws Exception
	 */
	/*public static void deleteScore(String tenantCode, String memberName, Document data, long score, DBSession conn) throws Exception {
		//处于事务处理中
		if (score > 0) {
			score = -1 * score;
			saveScore(tenantCode, memberName, score, data, "数据采集-行为删除", null, conn);
		}
		//异常积分：删除
		String sql = "update ucScoreUnusual set SYS_DELETEFLAG=1 where msEventID=?";
		sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_SCOREUNUSUAL, "ucScoreUnusual");
		InfoHelper.executeUpdate(sql, new Object[]{data.getDocID()}, conn);
	}*/
	
	/**
	 * 修改行为数据时，改变积分：
	 * 1）正常积分
	 * 2）异常积分
	 * 
	 * @param memberName
	 * @param event
	 * @throws E5Exception
	 */
	/*public static void changeScore(String tenantCode, String memberName, Document event, DBSession conn) throws E5Exception {
		changeScoreNormal(tenantCode, memberName, event, conn);
		changeScoreUnusual(tenantCode, memberName, event, conn);
	}*/
	
	/**
	 * 修改行为数据时，改变正常积分：
	 * 找出原来计算积分的规则，一个一个重新计算，差值存储
	 * 
	 * @param memberName
	 * @param event
	 * @throws E5Exception
	 */
	/*private static void changeScoreNormal(String tenantCode, String memberName, Document event, DBSession conn) throws E5Exception {
		List<Integer[]> ruleIDs = getRulesByEvent(tenantCode, event.getDocID());
		if (ruleIDs == null || ruleIDs.size() == 0) return;
		
		List<Rule> rules = getRules(tenantCode, ruleIDs);
		
		for (int i = 0; i < ruleIDs.size(); i++) {
			Rule rule = rules.get(i);
			if (rule == null) continue;//积分规则被删了，不再计算
			
			int newScore = calScore(event, rule);
			int scoreDiff = newScore - (ruleIDs.get(i))[1];
			
			if (scoreDiff != 0)
				saveScore(tenantCode, memberName, scoreDiff, event, "采集-行为修改", rule, conn);
		}
	}*/
	
	/**
	 * 修改行为数据时，改变异常积分记录：
	 * 若有异常积分记录，则找出原来计算积分的规则，一个一个重新计算。
	 * 若新积分还是异常积分，则修改原记录。
	 * 若新积分不是异常积分，则加到正常积分表中，删除原异常积分。
	 * 
	 * @param memberName
	 * @param event
	 * @throws E5Exception
	 */
	/*private static void changeScoreUnusual(String tenantCode, String memberName, Document event, DBSession conn) throws E5Exception {
		List<Integer[]> ruleIDs = getRulesFromUnusal(tenantCode, event.getDocID());
		if (ruleIDs == null || ruleIDs.size() == 0) return;
		
		List<Rule> rules = getRules(tenantCode, ruleIDs);
		
		for (int i = 0; i < ruleIDs.size(); i++) {
			Rule rule = rules.get(i);
			if (rule == null) continue;//积分规则被删了，不再计算
			
			int newScore = calScore(event, rule);
			
			//若积分仍是异常积分，则修改原记录
			if (needMonitor(tenantCode, newScore)) {
				String sql = "update ucScoreUnusual set msScore=? where msRule_ID=? and msEventID=?"; //一个行为在积分规则（类型6）下只有一个积分记录
				sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_SCOREUNUSUAL, "ucScoreUnusual");
				InfoHelper.executeUpdate(sql, new Object[]{newScore, rule.getRuleID(), ruleIDs.get(i)[0]}, conn);
			} else {
				//若新积分不是异常积分，则加到正常积分表中，删除原异常积分。
				saveScore(tenantCode, memberName, newScore, event, "", rule, conn);
				String sql = "update ucScoreUnusual set SYS_DELETEFLAG=1 where msRule_ID=? and msEventID=?"; //一个行为在积分规则（类型6）下只有一个积分记录
				sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_SCOREUNUSUAL, "ucScoreUnusual");
				InfoHelper.executeUpdate(sql, new Object[]{rule.getRuleID(), ruleIDs.get(i)[0]}, conn);
			}
		}
	}*/
	
	//从Document对象转换为Event对象，以便计算积分
	private static Event assembleEvent(Document data, boolean isTrading) {
		Event event = new Event();
		event.setId(data.getDocID());
		event.setMemberID(data.getInt("eMemberID"));
		event.setSource(data.getString("eSource"));
		event.setSourceID(data.getInt("eSource_ID"));
		event.setTime(data.getTimestamp("eStartTime"));
		event.setTrading(isTrading);
		if (isTrading) {
			event.setMoney(data.getFloat("eTotalPrice"));
		}
		event.setTypeID(data.getInt("eType_ID"));
		event.setType(data.getString("eType"));
		event.setDescription(data.getString("eType"));
		
		return event;
	}

	private static void addMemberScore(String tenantCode, float score, long memberID, DBSession conn) throws E5Exception {
		String table = InfoHelper.getLibTable(Constant.DOCTYPE_MEMBER, tenantCode);
		String sql = "update " + table + " set mScore=mScore+? where SYS_DOCUMENTID=?";
		
		InfoHelper.executeUpdate(sql, new Object[]{score, memberID}, conn);
	}
	private static int calScore(Document event, Rule rule) {
		//按设置的比例进行处理
		float m = event.getFloat("eTotalPrice") * rule.getLimit() / 100;
		int newScore = ((int)(rule.getScore() * m));
		return newScore;
	}
	
	/**
	 * 判断一个积分是否异常
	 * @param tenantCode 租户代号。不同租户的积分警戒线不同
	 * @param score 积分
	 * @throws E5Exception
	 */
	private static boolean needMonitor(String tenantCode, int score) throws E5Exception {
		TenantManager tenantManager = (TenantManager)Context.getBean(TenantManager.class);
		Tenant tenant = tenantManager.get(tenantCode);

		boolean monitor = (tenant != null)
				&& tenant.getScoreMonitor() > 0
				&& score > tenant.getScoreMonitor();
		return monitor;
	}
	
	/**
	 * 取一个行为的按积分规则六（按金额比例返利）计算的积分，用于修改时依此做积分变化
	 */
	private static List<Integer[]> getRulesByEvent(String tenantCode, long eventID) throws E5Exception {
		String sql = "select msRule_ID,sum(msScore) as msScore from ucMemberScore where msEventID=? and msRuleType=6 group by msRule_ID";
		sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_MEMBERSCORE, "ucMemberScore");
		return queryScoreRules(sql, eventID);
	}
	/**
	 * 取一个行为的按积分规则六（按金额比例返利）计算的异常积分
	 */
	private static List<Integer[]> getRulesFromUnusal(String tenantCode, long eventID) throws E5Exception {
		//取没有审批通过的（还未审批的、拒绝的）
		String sql = "select msRule_ID,msScore from ucScoreUnusual where msEventID=? and msRuleType=6 and msIsApproved!=1";
		sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_SCOREUNUSUAL, "ucScoreUnusual");
		
		return queryScoreRules(sql, eventID);
	}
	private static List<Integer[]> queryScoreRules(String sql, long eventID) {
		IResultSet rs = null;
		DBSession conn = null;
		List<Integer[]> rules = new ArrayList<Integer[]>();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, new Object[]{eventID});
			while (rs.next()) {
				rules.add(new Integer[]{rs.getInt("msRule_ID"), rs.getInt("msScore")});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return rules;
	}
	private static List<Rule> getRules(String tenantCode, List<Integer[]> ruleIDs) throws E5Exception {
		List<Rule> rules = new ArrayList<Rule>();
		
		int docLibID = InfoHelper.getLibID(Constant.DOCTYPE_SCORERULE, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		for (Integer[] ruleID : ruleIDs) {
			Document doc = docManager.get(docLibID, ruleID[0]);
			if (doc == null) {
				//积分规则被删
				rules.add(null);
				continue;
			}
		
			Rule rule = new Rule();
			rule.setTenantCode(tenantCode);
			
			rule.setRuleID((int)doc.getDocID());
			rule.setEventTypeID(doc.getInt("srEventType"));
			rule.setSourceID(doc.getInt("srSource_ID"));
			rule.setRuleType(doc.getInt("srType"));
			rule.setLimit(doc.getInt("srLimit"));
			rule.setScore(doc.getInt("srScore"));
			rule.setExperience(doc.getInt("srExperience"));
			
			rules.add(rule);
		}
		return rules;
	}

	/**
	 * 找到一个行为产生的所有积分的和
	 * @param eventID
	 * @return
	 * @throws Exception
	 */
	public static long sumScore(String tenantCode, long eventID) throws Exception {
		String sql = "select sum(msScore) from ucMemberScore where msEventID=? and SYS_DELETEFLAG=0";
		sql = InfoHelper.replaceSQL(tenantCode, sql, Constant.DOCTYPE_MEMBERSCORE, "ucMemberScore");
	
		IResultSet rs = null;
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, new Object[]{eventID});
			if (rs.next()) {
				return rs.getLong(1);
			}
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return 0;
	}
	/**
	 * 在积分记录表里加一条积分记录：删除时、修改时
	 */
	/*private static void saveScore(String tenantCode, String memberName, long score, Document event, String description, Rule rule, DBSession conn) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, tenantCode);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(docLib.getDocTypeID()));
		
		fillScoreData(doc, docLib, tenantCode, memberName, score, event, description, rule);		
		docManager.save(doc, conn);
		
		//会员表里的积分变化
		addMemberScore(tenantCode, score, event.getLong("eMemberID"), conn);
	}*/
	
	//组装会员积分表的数据。参数比较多，只内部使用
	private static void fillScoreData(Document doc, DocLib docLib, String tenantCode, String memberName, 
			long score, Document event, String description, Rule rule) throws E5Exception {
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.set("msMember_ID", event.getInt("eMemberID"));
		doc.set("msMember", memberName);
		doc.set("msTime", event.getTimestamp("eStartTime"));
		doc.set("msEvent", description);
		doc.set("msEventID", event.getDocID());
		doc.set("msEventType", event.getString("eType"));
		doc.set("msEventType_ID", event.getInt("eType_ID"));
		
		doc.set("msSource", event.get("eSource"));
		doc.set("msSource_ID", event.get("eSource_ID"));
		
		if (rule != null) {
			doc.set("msRuleType", rule.getRuleType());
			doc.set("msRule_ID", rule.getRuleID());
		}
		if (event.getDocTypeID() == InfoHelper.getTypeIDByCode(Constant.DOCTYPE_EVENTTRADING)) {
			doc.set("msIsTrading", 1);
		}
		
		doc.set("msScore", score);
		doc.set("msExperience", 0);
		doc.set("msMemo", description);
	}
	
	/**
	 * 在会员积分表里新插入一条记录
	 * （新华在使用：汽车抽奖、大转盘抽奖）
	 * @param mDoc  会员doc
	 * @param newDoc_ms  一个新的文档，会员积分表
	 * @param score  积分值
	 * @param type  活动类型
	 * @param source  来源
	 */
	public static void setOneMSRecd(Document mDoc,Document newDoc_ms,String score,String type,String source){
		//获取需要的值
		String mName = mDoc.getString("mName");
		String uid = mDoc.getString("SYS_DOCUMENTID");
		//保存记录
		newDoc_ms.set("msMember", mName);  //会员名称
		newDoc_ms.set("msMember_ID", uid);  //会员ID
		newDoc_ms.set("msEvent", type);  //类型名称
		newDoc_ms.set("msEventType", type);  //会员ID
		int msEventType_ID = InfoHelper.getEventTypeCat(type);
		newDoc_ms.set("msEventType_ID", msEventType_ID);  //类型名称
		newDoc_ms.set("msSource", source);  //数据来源
		int msSource_ID = InfoHelper.getEventSourceCat(source);
		newDoc_ms.set("msSource_ID", msSource_ID);  //数据来源ID
		newDoc_ms.set("msTime", DateHelper.getFormat());  //时间
		newDoc_ms.set("msScore", score);
		newDoc_ms.set("msMemo", "参与"+type+"，"+score+"交子");  //描述
	}
}
