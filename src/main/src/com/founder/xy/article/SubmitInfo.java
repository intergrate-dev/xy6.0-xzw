package com.founder.xy.article;


/**
 * 用于草稿签发到栏目、关联栏目的一些字段 
 * 传输的参数字段 
 * 
 * @author Deng ChaoChen
 * 
 */
public class SubmitInfo{
	/******************************** 传输的参数字段   ********************************/
	
	private String siteID;			//站点
	private String docIDs;			//签发的稿件IDs
	private String DocLibID;		//稿件库的ID
	private String mainColId;		//签发的主栏目ID
	private String refColIds;		//签发的关联栏目IDs
	private String mainColName;		//签发的主栏目名称
	private String refColNames;	    //签发的关联栏目名称
	private String linkTitle;		//签发的稿件的链接标题
	private String isTransfer;		//true 是发布  false是保存
	
	public String getDocIDs() {
		return docIDs;
	}
	public void setDocIDs(String docIDs) {
		this.docIDs = docIDs;
	}
	public String getDocLibID() {
		return DocLibID;
	}
	public void setDocLibID(String docLibID) {
		DocLibID = docLibID;
	}
	public String getMainColId() {
		return mainColId;
	}
	public void setMainColId(String mainColId) {
		this.mainColId = mainColId;
	}
	public String getRefColIds() {
		return refColIds;
	}
	public void setRefColIds(String refColIds) {
		this.refColIds = refColIds;
	}
	public String getMainColName() {
		return mainColName;
	}
	public void setMainColName(String mainColName) {
		this.mainColName = mainColName;
	}
	public String getRefColNames() {
		return refColNames;
	}
	public void setRefColNames(String refColNames) {
		this.refColNames = refColNames;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public String getSiteID() {
		return siteID;
	}
	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}
	public String getIsTransfer() {
		return isTransfer;
	}
	public void setIsTransfer(String isTransfer) {
		this.isTransfer = isTransfer;
	}
}