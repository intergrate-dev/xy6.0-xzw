package com.founder.xy.config;

/**
 * 发布渠道
 * @author Gong Lijie
 */
public class Channel {
	private int id;
	private String code;	//渠道代码
	private String name;	//渠道名称
	
	public Channel(int id, String code, String name) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}
