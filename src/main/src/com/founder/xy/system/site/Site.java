package com.founder.xy.system.site;

public class Site implements Cloneable{
	private String name;
	private int id;
	private String webRoot;
	
	public Site(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}
	public Site(String name, int id, String webRoot) {
		super();
		this.name = name;
		this.id = id;
		this.webRoot = webRoot;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	
	public String getWebRoot() {
		return webRoot;
	}
	@Override
	public Site clone() {
		try {
			return (Site)super.clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
