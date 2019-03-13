package com.founder.xy.wx.data;

import java.util.List;

import com.founder.e5.doc.Document;

/**
 * 微信账号
 * @author Gong Lijie
 */
public class Account {
	
	private long id;
	private int libID;
	private String name;
	private String accessToken; //访问令牌
	private String dir;
	private int dirID;
	private int templateID;
	private int siteID;
	
	private List<Menu> menus;
	
	public Account(Document menu) {
		id = menu.getDocID();
		libID = menu.getDocLibID();
		name = menu.getString("wxa_name");
		accessToken = menu.getString("wxa_accessToken");
		
		dir = menu.getString("wxa_dir");
		dirID = menu.getInt("wxa_dir_ID");
		templateID = menu.getInt("wxa_template_ID");
		siteID = menu.getInt("wxa_siteID");
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public String getDir() {
		return dir;
	}

	public int getDirID() {
		return dirID;
	}

	public List<Menu> getMenus() {
		return menus;
	}
	public String getAccessToken() {
		return accessToken;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public int getLibID() {
		return libID;
	}

	public int getTemplateID() {
		return templateID;
	}

	public int getSiteID() {
		return siteID;
	}
}
