package com.founder.amuc.jms;

import java.util.List;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.jms.data.RuleEvent;
import com.founder.amuc.member.Event;
import com.founder.amuc.score.Rule;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.dom.DocLib;

/**
 * 消息发布辅助类
 * @author Gong Lijie
 */
public class PublishHelper {

	/**
	 * <未使用>
	 * 
	 * 通知积分计算服务
	 * @param rules
	 */
	public static void scoreNotify(List<Rule> rules, Event b) {
		Publisher sender = (Publisher)Context.getBean("scoreSender");
		for (Rule rule : rules) {
			RuleEvent rb = new RuleEvent();
			rb.setEvent(b);
			rb.setRule(rule);
			
			sender.send(rb);
		}
	}
	
	/** 
	* @author  leijj 
	* 功能： 会员修改时同步修改会员活动表中会员信息
	* @param oldID
	* @param newID
	* @param newName
	* @param newNo
	* @param newMobile
	* @param newEmail
	* @param typecode 
	*/ 
	public static void memberNotify(long oldID, long newID, String newName, String newNo, String newMobile, String newEmail, String typecode) {
		DataMsg changeInfo = new DataMsg();
		
		changeInfo.setNewID(newID);
		changeInfo.setNewName(newName);
		changeInfo.setNewNo(newNo);
		changeInfo.setNewMobile(newMobile);
		changeInfo.setNewEmail(newEmail);
		changeInfo.setOldID(oldID);
		changeInfo.setOldName(newName);
		changeInfo.setOldNo(newNo);
		changeInfo.setOldMobile(newMobile);
		changeInfo.setOldEmail(newEmail);
		changeInfo.setTypecode(typecode);

		Publisher sender = (Publisher)Context.getBean("memberSender");
		sender.send(changeInfo);
	}
	/** 
	* @author  
	* 功能： 会员修改时同步修改群组表中会员信息
	* @param oldID
	* @param newID
	* @param newName
	* @param newNo
	* @param newMobile
	* @param newEmail
	* @param typecode 
	*/ 
	public static void memberNotify(long oldID, long newID, String newName, String newNo, String newMobile, String newEmail,String newQQ,String newNickName, String typecode) {
		DataMsg changeInfo = new DataMsg();
		
		changeInfo.setNewID(newID);
		changeInfo.setNewName(newName);
		changeInfo.setNewNo(newNo);
		changeInfo.setNewMobile(newMobile);
		changeInfo.setNewEmail(newEmail);
		changeInfo.setNewQQ(newQQ);
		changeInfo.setNewNickName(newNickName);
		changeInfo.setOldID(oldID);
		changeInfo.setOldName(newName);
		changeInfo.setOldNo(newNo);
		changeInfo.setOldMobile(newMobile);
		changeInfo.setOldEmail(newEmail);
		changeInfo.setTypecode(typecode);
		changeInfo.setOldQQ(newQQ);
		changeInfo.setOldNickName(newNickName);

		Publisher sender = (Publisher)Context.getBean("memberSender");
		sender.send(changeInfo);
	}
	/** 
	* @author  
	* 功能： 会员修改时同步修改会员关系表中会员信息
	* @param memberID
	* @param memberName
	* @param tenantCode
	* @throws E5Exception 
	*/ 
	public static void updateMR(long memberID,String memberName,String tenantCode) throws E5Exception {
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERRELATION, tenantCode);
		String sql1 = "update " + docLib.getDocLibTable() + " set mrMemberName='" + memberName + "' where mrMemberID=" + memberID + " and SYS_DELETEFLAG=0";
		String sql2 = "update " + docLib.getDocLibTable() + " set mrTargetName='" + memberName + "' where mrTargetID=" + memberID + " and SYS_DELETEFLAG=0";
		DBSession conn = null;
		try {
			conn = Context.getDBSession(); 
			conn.beginTransaction();
			conn.executeUpdate(sql1, null);
			conn.executeUpdate(sql2, null);
			conn.commitTransaction();
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
        	return;
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}
}