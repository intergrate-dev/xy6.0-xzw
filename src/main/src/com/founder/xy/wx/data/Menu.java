package com.founder.xy.wx.data;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.doc.Document;

/**
 * 微信菜单
 * @author Gong Lijie
 */
public class Menu {
	private long id;
	private int libID;
	private int accountID;
	private String name;
	private String type;
	private String url;
	private String key;
	
	private long parentID;
	private int order;
	private String mediaId;
	
	private String dir; //发布时使用：发布目录
	private String path;//发布时使用：发布完整路径
	
	private List<Menu> children = new ArrayList<>();

	public Menu() {
		
	}
	public Menu(Document menu) {
		id = menu.getDocID();
		libID = menu.getDocLibID();
		accountID = menu.getInt("wxm_accountID");
		name = menu.getString("wxm_name");
		type = menu.getString("wxm_type");
		order = menu.getInt("wxm_order");
		url = menu.getString("wxm_url");
		key = menu.getString("wxm_key");
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public long getParentID() {
		return parentID;
	}

	public int getOrder() {
		return order;
	}

	public String getKey() {
		return key;
	}

	public String getMediaId() {
		return mediaId;
	}

	public int getLibID() {
		return libID;
	}
	public int getAccountID() {
		return accountID;
	}
	
	public String getDir() {
		return dir;
	}
	public String getPath() {
		return path;
	}
	
	public List<Menu> getChildren() {
		return children;
	}

	public void addChild(Menu child) {
		children.add(child);
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setLibID(int libID) {
		this.libID = libID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setParentID(long parentId) {
		this.parentID = parentId;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public void setDir(String dir) {
		this.dir = dir;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public JSONObject json() {
		return json(false);
	}
	
	public JSONObject json4Publish() {
		return json(true);
	}
	
	/**
	 * @param changeView true时，把view0改为view。用于上传到微信服务器进行发布时
	 * @return
	 */
	private JSONObject json(boolean changeView) {
		//子菜单
		JSONArray sub_button = new JSONArray();
		for (Menu child : getChildren()) {
			sub_button.add(child.json(changeView));
		}
		
		String type = getType();
		if (changeView && "view0".equals(type))
			type = "view";
		
		JSONObject button = new JSONObject();
		button.put("id", getId());
		button.put("name", getName());
		button.put("type", type);
		
		if ("view".equals(type))
			button.put("url", getUrl());
		else if ("click".equals(type))
			button.put("key", getKey());
		
		button.put("sub_button", sub_button);
		
		return button;
	}
}
