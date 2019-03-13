package com.founder.amuc.config;

import java.util.List;

import com.founder.amuc.config.SubTab;

/**
 * 界面子菜单
 * @author Gong Lijie
 */
public class SubTab implements Cloneable{
	private String id;	//TAB的ID，可用于唯一确定一个TAB，做权限控制等特殊处理
	private String name;	//TAB页的标题
	private String url;		//点击的URL地址
	private int docTypeID;	//对应的文档类型
	private int catTypeID;	//导航可用的分类类型ID
	private String docTypeCode; //文档类型编码
	
	private String rule; //规则公式
	private String query;	//查询条件编码
	private String list; //列表名称
	
	private List<SubTab> children;//允许主界面显示分TAB
	private int childrenCount;
	
	private boolean noPermission;
	private boolean seperate;	//是否在单独的窗口打开
	
	private String pId;
	private String pName;
	
	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public SubTab() {
	}

	//--------following : getter & setter ---------
	public int getDocTypeID() {
		return docTypeID;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	void setDocTypeID(int docTypeID) {
		this.docTypeID = docTypeID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

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

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDocTypeCode() {
		return docTypeCode;
	}

	void setDocTypeCode(String icon) {
		this.docTypeCode = icon;
	}

	public List<SubTab> getChildren() {
		return children;
	}
	
	void setChildren(List<SubTab> children) {
		this.children = children;
		childrenCount = (children == null || children.size() == 0) ? 0 : children.size();
	}
	public int getChildrenCount() {
		return childrenCount;
	}
	
	public String getUrl() {
		if (url != null) return url;
		
		return (children == null || children.size() == 0) ? null : children.get(0).getUrl();
	}

	public int getCatTypeID() {
		return catTypeID;
	}

	void setCatTypeID(int catTypeID) {
		this.catTypeID = catTypeID;
	}
	public String getQuery() {
		return query;
	}

	void setQuery(String queryCode) {
		this.query = queryCode;
	}

	public String getList() {
		return list;
	}

	void setList(String list) {
		this.list = list;
	}

	@Override
	public SubTab clone() {
		//没有复制children内容
		try {
			SubTab c = (SubTab)super.clone();
			/*
			if (getChildrenCount() > 0) {
				List<SubTab> subs = new ArrayList<SubTab>();
				for (SubTab subTab : children) {
					subs.add(subTab.clone());
				}
				c.setChildren(subs);
			}
			*/
			return c;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}