package com.founder.amuc.member;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 积分计算时需要用到的行为对象
 * @author Gong Lijie
 * 2014-6-6
 */
@XmlRootElement
public class Event {
	private long id;
	private String description; //行为描述
	private Date time;
	private int memberID; //会员ID
	private String member;//会员名称
	private String tenantCode;//租户代号
	private String type; //行为类型--来自分类
	private int typeID;
	private String source;//数据来源--来自分类
	private int sourceID;
	private boolean trading;//是否交易型
	private float money; //消费金额，在按类型6做积分计算时使用
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String name) {
		this.description = name;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getMemberID() {
		return memberID;
	}
	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String memberName) {
		this.member = memberName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTypeID() {
		return typeID;
	}
	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public boolean isTrading() {
		return trading;
	}
	public void setTrading(boolean trading) {
		this.trading = trading;
	}
	public float getMoney() {
		return money;
	}
	public void setMoney(float money) {
		this.money = money;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
}