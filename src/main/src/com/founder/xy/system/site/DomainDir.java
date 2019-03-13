package com.founder.xy.system.site;

import com.founder.e5.doc.Document;

/**
 * 域名目录类
 * @author Deng Chaochen
 */
public class DomainDir {
	private long id; //域名的ID
	private int siteID;
	private int parentID;//是否是父节点
	
	private String name; //域名的名字
	private String url; //域名的详细地址
	private String big5Url;
	private String path;
	
	public DomainDir(Document doc) {
		this.id = doc.getDocID();
		this.parentID= doc.getInt("dir_parentID");
		this.siteID = doc.getInt("dir_siteID");
		
		this.name = doc.getString("dir_name");
		this.url = doc.getString("dir_url");
		this.big5Url = doc.getString("dir_big5Url");
		this.path = doc.getString("dir_path");
	}
	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public int getParentID() {
		return parentID;
	}

	public int getSiteID() {
		return siteID;
	}
	public String getBig5Url() {
		return big5Url;
	}
	public String getPath() {
		return path;
	}
}
