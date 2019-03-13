package com.founder.amuc.score;

import java.util.Date;

import org.springframework.web.context.ContextLoader;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.Event;
import com.founder.amuc.tenant.Tenant;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.workspace.ProcHelper;

/**
 * 根据积分规则计算积分的处理器
 * @author Gong Lijie
 * 2014-6-6
 */
public class RuleProcessor {
	private final String rule1Filter = "rpMemberID=? and rpEventTypeID=? and rpRuleID=? and rpSourceID=? and rpTime=?";
	private final String rule4Filter = "rpMemberID=? and rpEventTypeID=? and rpRuleID=? and rpSourceID=?";

	/**
	 * 行为入库后的处理：
	 * 查找是否有匹配的积分规则，若有，则按规则计算出积分，添加到积分表，并修改会员积分。
	 * 若积分超出监控线，则加到积分监控表。
	 * @param rule
	 * @param event
	 * @throws E5Exception
	 */
	public void process(Rule rule, Event event, int siteID) {
		if (rule.getLimit() < 1)
			rule.setLimit(1);
		
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();
			
			switchProcess(rule, event, conn, siteID);
			
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
	
	/**
	 * 与process(Rule, Event)类似，数据库连接由外部控制
	 */
	public int process(Rule rule, Event event, DBSession conn, int siteID) throws Exception {
		if (rule.getLimit() < 1)
			rule.setLimit(1);
		
		return switchProcess(rule, event, conn, siteID);
	}
	
	private int switchProcess(Rule rule, Event event, DBSession conn, int siteID) throws Exception {
		int rescore = -1;
		switch (rule.getRuleType()) {
		case 1:
			rescore = process1(rule, event, conn, siteID);
			break;
		case 2:
			rescore = process2(rule, event, conn, siteID);
			break;
		case 3:
			rescore = process3(rule, event, conn, siteID);
			break;
		case 4:
			rescore = process4(rule, event, conn, siteID);
			break;
		case 5:
			rescore = process5(rule, event, conn, siteID);
			break;
		case 6:
			rescore = process6(rule, event, conn, siteID);
			break;
		default:
			break;
		}
		return rescore ;
	}
	/**
	 * 类型一：每日前几次任务奖励
		1)	一个行为提交进来时，根据规则ID、应用平台、会员ID、行为类型、日期，查找此表，若找得到，则次数+1，否则insert新记录。
			次数+1时要有update **** set 次数=次数+1 where次数=?的控制，避免并发错误。
		2)	判断次数是否超过要求次数，若超过则不做任何处理，return
		3)	若未超过要求，则产生会员积分记录、会员经验记录。
		4)	按照行为ID找到行为表，设置“是否有积分”属性。
		5)	按照会员ID找到会员表，累加积分和经验值
		6)	清除过期天的数据。
		7)  返回会员新增的积分，没有新增则返回0
	 */
	private int process1(Rule rule, Event event, DBSession conn,int siteID) throws Exception {
		int count = increase(rule, conn);
		int rescore = -1;
		//次数<=要求次数
		if (count <= rule.getLimit()) {
			rescore = matchDeal(rule, event, conn, siteID);
		}
		/*
		//清除过期天的数据
		clear(rule, conn);
		*/
		return rescore;
	}
	/**
	 * 类型二：每日累计次数后奖励
	 * 与类型一的做法类似，区别是：判断次数=要求次数时，才产生会员积分记录、会员经验记录
	 */
	private int process2(Rule rule, Event event, DBSession conn, int siteID) throws Exception {
		int count = increase(rule, conn);
		int rescore = -1;
		//次数=要求次数
		if (count == rule.getLimit()) {
			rescore = matchDeal(rule, event, conn, siteID);
		}
		/*
		//清除过期天的数据
		clear(rule, conn);
		*/
		return rescore;
	}
	/**
	 * 类型三：每次任务奖励
	 * 不需要有表做记录。
		一个行为提交进来时，相关的积分记录
	 */
	private int process3(Rule rule, Event event, DBSession conn,int siteID) throws Exception {
		int rescore = matchDeal(rule, event, conn, siteID);
		/*
		//清除过期天的数据
		clear(rule, conn);
		*/
		return rescore;
	}
	/**
	* 类型四：连续几天完成任务后奖励，要求>=2
	1)	一个行为提交进来时，根据用户ID、行为类型，查找此表，若找不到，则insert,return。
	2)	若上次完成日期是今天，则不处理，return。
	3)	若上次完成日期早于昨天，则证明没有连续，改成今天，次数改成1，return
	4)	若上次完成日期是昨天，则改成今天，次数+1。
	5)	判断次数是否=要求次数，若相等，则产生相关的积分记录。
		若次数>要求次数，则应该是昨天已经满足条件得到奖励了，此时改次数为1，重新计数。
	 */
	private int process4(Rule rule, Event event, DBSession conn,int siteID) throws Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_RULEPROCESS, rule.getTenantCode());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		
		Document[] docs = docManager.find(docLib.getDocLibID(), rule4Filter, new Object[]{
				rule.getMemberID(), rule.getEventTypeID(), rule.getRuleID(), rule.getSourceID(),
				}, conn);
		//若找不到，则insert,return
		int rescore = -1 ;
		if (docs == null || docs.length == 0) {
			insert(docLib, rule, conn);				
		} else {
			Document doc = docs[0];
			int interval = dayInterval(rule.getEventDate(), doc.getDate("rpTime"));
			//若上次完成日期早于昨天，则证明没有连续，改成今天，次数改成1，return
			if (interval > 1) {
				doc.set("rpTime", rule.getEventDate());
				doc.set("rpCount", 1);
				docManager.save(doc, conn);
			} else if (interval == 1){
				//若上次完成日期是昨天，则改成今天，次数+1
				int count = doc.getInt("rpCount") + 1;
				doc.set("rpTime", rule.getEventDate());
				doc.set("rpCount", count);
				if (count == rule.getLimit()) {
					//次数=要求次数，则产生会员积分记录，并且按照行为ID找到行为表，设置“是否有积分”属性。
					rescore = matchDeal(rule, event, conn, siteID);
				} else if (count > rule.getLimit()){
					//若次数>要求次数，则应该是昨天已经满足条件得到奖励了，此时改次数为1，重新计数。
					doc.set("rpCount", 1);
				}
				docManager.save(doc, conn);
			}
		}
		return rescore;
	}
	/**
	 * 类型五：一次性任务奖励
	不需要表中的日期和次数列。
	1)	一个行为提交进来时，根据用户ID、行为类型，查找此表，若找得到，则证明已经发生过，则return。
	2)	Insert一条规则处理记录。
	3)	相关的积分记录
	 */
	private int process5(Rule rule, Event event, DBSession conn, int siteID) throws Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_RULEPROCESS, rule.getTenantCode());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int rescore = -1;
		Document[] docs = docManager.find(docLib.getDocLibID(), rule4Filter, new Object[]{
				rule.getMemberID(), rule.getEventTypeID(), rule.getRuleID(), rule.getSourceID(),
				}, conn);
		if (docs == null || docs.length == 0) {
			insert(docLib, rule, conn);
			rescore = matchDeal(rule, event, conn, siteID);
		}
		return rescore;
	}		
	/**
	 * 类型六：按金额比例充值
		不需要有表做记录。
		一个行为提交进来时，产生会员积分记录，按设置的比例进行处理。
	 */
	private int process6(Rule rule, Event event, DBSession conn,int siteID) throws Exception {
		int rescore = -1;
		//按设置的比例进行处理
		float m = event.getMoney() * rule.getLimit() / 100;
		rule.setScore((int)(rule.getScore() * m));
		rule.setExperience((int)(rule.getExperience() * m));
		
		rescore = matchDeal(rule, event, conn, siteID);
			
		//清除过期天的数据
		clear(rule, conn);
		return rescore;
	}
	//积分规则处理表添加一条记录
	private void insert(DocLib docLib, Rule rule, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID());
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.set("rpMemberID", rule.getMemberID());
		doc.set("rpSourceID", rule.getSourceID());
		doc.set("rpEventTypeID", rule.getEventTypeID());
		doc.set("rpTime", rule.getEventDate());
		doc.set("rpCount", 1);
		doc.set("rpRuleID", rule.getRuleID());
		docManager.save(doc, conn);
	}
	//计算次数+1
	private int increase(Rule rule, DBSession conn) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_RULEPROCESS, rule.getTenantCode());

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(), rule1Filter, 
				new Object[]{rule.getMemberID(), rule.getEventTypeID(), rule.getRuleID(), rule.getSourceID(),
				rule.getEventDate()}, conn);
		Document doc = null;
		if (docs != null && docs.length >= 1) {
			doc = docs[0];
			int count = doc.getInt("rpCount") + 1;
			doc.set("rpCount", count);
			docManager.save(doc, conn);
			
			return count;
		} else {
			insert(docLib, rule, conn);
			return 1;
		}
	}
	
	/**
	 * 满足规则要求时的处理：
	 * 1）产生会员积分和会员经验记录
	 * 2）按照行为ID找到行为表，设置“是否有积分”属性
	 * 3）按照会员ID找到会员表，累加积分和经验值
	 * 4)返回本次行为增加的积分
	 */
	private int matchDeal(Rule rule, Event event, DBSession conn, int siteID) throws Exception {
		//产生会员积分和会员经验记录
		String monitor = createScore(rule, event, conn, siteID);//超过积分警戒线会返回"true"

		//按照行为ID找到行为表，设置“是否有积分”属性
		setEvent(event, conn);
		
		//按照会员ID找到会员表，累加积分和经验值
		if ("false".equals(monitor)) {
			addScore(rule, conn);
			return rule.getScore();
		}else if("islimit".equals(monitor)){
			return -2;//超过每日会员积分上限返回-2
		}else{
			return -3;
		}
	}
	//产生会员积分和会员经验记录
	private String createScore(Rule rule, Event event, DBSession conn,int siteID) throws Exception {
		DocLib scoreLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERSCORE, rule.getTenantCode());
		DocLib unusualLib = InfoHelper.getLib(Constant.DOCTYPE_SCOREUNUSUAL, rule.getTenantCode());
		
		//超过监控线，则送到异常积分表，否则送到积分表
		boolean monitor = needMonitor(rule);  //判断是否为异常积分，true为异常积分，false 不是异常积分

		DocLib docLib = monitor ? unusualLib : scoreLib;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.newDocument(docLib.getDocLibID(), InfoHelper.getID(scoreLib.getDocTypeID()));
		
		doc.setFolderID(docLib.getFolderID());
		doc.setDeleteFlag(0);
		doc.set("msMember_ID", event.getMemberID());
		doc.set("msMember", event.getMember());
		doc.set("msTime", event.getTime());
		doc.set("msEvent", event.getDescription());
		doc.set("msEventType", event.getType());
		doc.set("msEventType_ID", event.getTypeID());
		doc.set("msIsTrading", event.isTrading() ? 1 : 0);
		doc.set("msEventID", event.getId());
		
		doc.set("msRuleType", rule.getRuleType());
		doc.set("msRule_ID", rule.getRuleID());
		doc.set("msScore", rule.getScore());
		doc.set("m_siteID", siteID);
		
		if (monitor) {
			ProcHelper.initDoc(doc);
		}
		
		docManager.save(doc, conn);
		
		return monitor+"";
	}
	//判断是否为异常积分，积分监控为单笔积分额度监控
	private boolean needMonitor(Rule rule) throws E5Exception {
		//TenantManager tenantManager = (TenantManager)Context.getBean(TenantManager.class);
		/*
		TenantManager tenantManager = ContextLoader.getCurrentWebApplicationContext().getBean(TenantManager.class);
		Tenant tenant = tenantManager.get(rule.getTenantCode());

		boolean monitor = (tenant != null)
				&& tenant.getScoreMonitor() > 0
				&& rule.getScore() > tenant.getScoreMonitor();
		return monitor;
		*/
		return false;
	}
	//按照会员ID找到会员表，累加积分和经验值
	private void addScore(Rule rule, DBSession conn) throws Exception {
		String table = InfoHelper.getLibTable(Constant.DOCTYPE_MEMBER, rule.getTenantCode());
		String sql = "update " + table + " set mScore=mScore+? where SYS_DOCUMENTID=?";
		conn.executeUpdate(sql, new Object[]{rule.getScore(), rule.getMemberID()});
	}
	
	//按照行为ID找到行为表，设置“是否有积分”属性
	private void setEvent(Event event, DBSession conn) throws Exception {
		String docTypeCode = (event.isTrading())
				? Constant.DOCTYPE_EVENTTRADING
				: Constant.DOCTYPE_MEMBEREVENT;
		String table = InfoHelper.getLibTable(docTypeCode, event.getTenantCode());
		String sql = "update " + table + " set eHasScore=1 where SYS_DOCUMENTID=?";
		conn.executeUpdate(sql, new Object[]{event.getId()});
	}
	//清除过期天的数据
	private void clear(Rule rule, DBSession conn) {
		try {
			String table = InfoHelper.getLibTable(Constant.DOCTYPE_RULEPROCESS, rule.getTenantCode());
			String sql = "delete from " + table + " where rpRuleID=? and rpTime<?";
			
			InfoHelper.executeUpdate(sql, new Object[]{rule.getRuleID(), rule.getEventDate()}, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//两个日期的相差天数
	private int dayInterval(Date date1, Date date2) {
		long intervalMilli = date1.getTime() - date2.getTime();
		return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	}
}