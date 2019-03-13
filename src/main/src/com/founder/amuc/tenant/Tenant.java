package com.founder.amuc.tenant;

/**
 * 租户对象
 * @author Gong Lijie
 * 2014-5-29
 */
public class Tenant {

	private long id;
	private String name;
	private String code;
	private int orgID;
	private int type; //有效期类型。0：自然年，1：时间间隔
	private int scorePeriod;//积分有效期
	private int scoreMonitor;//积分监控线
	private int levelPeriod;//等级有效期
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getOrgID() {
		return orgID;
	}
	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getScorePeriod() {
		return scorePeriod;
	}
	public void setScorePeriod(int scorePeriod) {
		this.scorePeriod = scorePeriod;
	}
	public int getScoreMonitor() {
		return scoreMonitor;
	}
	public void setScoreMonitor(int scoreMonitor) {
		this.scoreMonitor = scoreMonitor;
	}
	public int getLevelPeriod() {
		return levelPeriod;
	}
	public void setLevelPeriod(int levelPeriod) {
		this.levelPeriod = levelPeriod;
	}
}
