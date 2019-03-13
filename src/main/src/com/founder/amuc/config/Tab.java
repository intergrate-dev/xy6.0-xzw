package com.founder.amuc.config;

import java.util.List;

import com.founder.amuc.config.SubTab;
import com.founder.amuc.config.Tab;

/**
 * 界面主菜单
 * @author Gong Lijie
 */
public class Tab implements Cloneable{
	private String id;
	private String name;
	private String url;
	
	private List<SubTab> children;
	private int childrenCount;
	private String icon;
	private boolean noPermission;
	private boolean seperate;	//是否在单独的窗口打开
	
	public boolean isSeperate() {
		return seperate;
	}
	void setSeperate(boolean seperate) {
		this.seperate = seperate;
	}
	public boolean isNoPermission(){
		return noPermission;
	}
	
	void setNoPermission(boolean noPermission){
		this.noPermission = noPermission;
	}
	public String getId() {
		return id;
	}
	void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	
	public List<SubTab> getChildren() {
		return children;
	}
	
	public void setChildren(List<SubTab> children) {
		this.children = children;
		childrenCount = (children == null || children.size() == 0) ? 0 : children.size();
	}
	public int getChildrenCount() {
		return childrenCount;
	}
	
	/** 当前tab的url。若当前tab没有配置url，则取其第一个子节点的url */
	public String getUrl() {
		if (url != null) return url;
		
		return (children == null || children.size() == 0) ? null : children.get(0).getUrl();
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getIcon() {
		return icon;
	}
	void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Override
	public Tab clone() {
		//没有复制children内容
		try {
			return (Tab)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}