package com.founder.xy.jpublish.data;

/**
 * 稿件作者信息
 * @author Gong Lijie
 */
public class AuthorInfo {
	private long id;
	private String name;
	private String url;
	private String duty;
	private String description;
	
	public AuthorInfo(long id, String name, String iconUrl, String duty,
			String description) {
		super();
		this.id = id;
		this.name = name;
		this.url = iconUrl;
		this.duty = duty;
		this.description = description;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getDuty() {
		return duty;
	}
	public String getDescription() {
		return description;
	}
	
}
