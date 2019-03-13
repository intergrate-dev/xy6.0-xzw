package com.founder.xy.system.site;

import java.io.Serializable;


/**
 * @author cyq
 */
public class ColumnUser implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String sysId; // documentID
	private String userCode; // 用户名(code)
	private String userName; // 真实姓名
	private String penName; // 笔名
	private String org; // 所在部门（机构）
	
	public String getSysId() {
		return sysId;
	}
	public void setSysId(String sysId) {
		this.sysId = sysId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPenName() {
		return penName;
	}
	public void setPenName(String penName) {
		this.penName = penName;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
}
