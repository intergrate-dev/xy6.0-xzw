package com.founder.xy.system;

import java.io.Serializable;

/**
 * 租户
 * @author Gong Lijie
 */
public class Tenant implements Serializable{
	private static final long serialVersionUID = -6473897573793640518L;
	
	public static final String DEFAULTCODE = "xy";
	public static final String SESSIONNAME = "tenant"; //用户登录后，租户代号在session中的变量名

	private long id;
	private String name;
	private String code;
	private int orgID;
	
	public Tenant() {
	}

	public Tenant(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
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
}
