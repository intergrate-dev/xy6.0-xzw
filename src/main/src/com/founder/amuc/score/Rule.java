package com.founder.amuc.score;

import java.util.Date;

/**
 * 一条积分规则处理实例
 * @author Gong Lijie
 * 2014-6-9
 */
public class Rule {
	private String tenantCode;//租户代号
	private int memberID; //行为的会员
	private Date eventDate; //行为发生的日期，要求时分秒清零
	
	private int ruleID; //规则ID
	private int ruleType; //规则类型
	private int sourceID; //数据来源--来自分类
	private int eventTypeID;//行为类型--来自分类
	private int limit; //积分规则定义的限制数
	private int score; 
	private int experience;
	private String eventType;//行为类型
	private String srName;//规则名字
	
	public String getSrName() {
		return srName;
	}
	public void setSrName(String srName) {
		this.srName = srName;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public int getRuleID() {
		return ruleID;
	}
	public void setRuleID(int id) {
		this.ruleID = id;
	}
	public int getRuleType() {
		return ruleType;
	}
	public void setRuleType(int type) {
		this.ruleType = type;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public int getEventTypeID() {
		return eventTypeID;
	}
	public void setEventTypeID(int eventTypeID) {
		this.eventTypeID = eventTypeID;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public int getMemberID() {
		return memberID;
	}
	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
}