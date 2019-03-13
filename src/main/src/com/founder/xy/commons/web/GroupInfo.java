package com.founder.xy.commons.web;

import net.sf.json.JSONObject;

/**
 * 分组逻辑中的参数
 */
public class GroupInfo {
	public static String RESULT = "result";
	public static String MESSAGE = "message";
	public static String OPERATION = "operation";
	public static String OPERATION_URL = "operation_url";
	private String operationUrl = "";
	
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	public static String HAS_THE_SAME_GROUP = "HasTheSameGroup";
	public static String GROUP_IS_RELATED_TO_OTHER_DATA = "Group_Is_Related_To_Other_Data";
	public static String COMFIRM_DELETE = "confirmDelete";
	public static String DELETE_OPERATION = "deleteOperation";

	private String categoryTypeId;
	private String newGroupName;
	private String siteID;
	private String groupID;

	private JSONObject rt_operationResult = new JSONObject();

	public String getCategoryTypeId() {
		return categoryTypeId;
	}

	public void setCategoryTypeId(String categoryTypeId) {
		this.categoryTypeId = categoryTypeId;
	}

	public String getNewGroupName() {
		return newGroupName;
	}

	public void setNewGroupName(String newGroupName) {
		this.newGroupName = newGroupName;
	}

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public JSONObject getRt_operationResult() {
		return rt_operationResult;
	}

	public void setRt_operationResult(JSONObject rt_operationResult) {
		this.rt_operationResult = rt_operationResult;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public static String getRESULT() {
		return RESULT;
	}

	public static void setRESULT(String rESULT) {
		RESULT = rESULT;
	}

	public static String getMESSAGE() {
		return MESSAGE;
	}

	public static void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}

	public static String getSUCCESS() {
		return SUCCESS;
	}

	public static void setSUCCESS(String sUCCESS) {
		SUCCESS = sUCCESS;
	}

	public static String getFAILURE() {
		return FAILURE;
	}

	public static void setFAILURE(String fAILURE) {
		FAILURE = fAILURE;
	}

	public static String getHAS_THE_SAME_GROUP() {
		return HAS_THE_SAME_GROUP;
	}

	public static void setHAS_THE_SAME_GROUP(String hAS_THE_SAME_GROUP) {
		HAS_THE_SAME_GROUP = hAS_THE_SAME_GROUP;
	}

	public static String getGROUP_IS_RELATED_TO_OTHER_DATA() {
		return GROUP_IS_RELATED_TO_OTHER_DATA;
	}

	public static void setGROUP_IS_RELATED_TO_OTHER_DATA(String gROUP_IS_RELATED_TO_OTHER_DATA) {
		GROUP_IS_RELATED_TO_OTHER_DATA = gROUP_IS_RELATED_TO_OTHER_DATA;
	}

	public static String getOPERATION() {
		return OPERATION;
	}

	public static void setOPERATION(String oPERATION) {
		OPERATION = oPERATION;
	}

	public String getOperationUrl() {
		return operationUrl;
	}

	public void setOperationUrl(String operationUrl) {
		this.operationUrl = operationUrl;
	}

	@Override
	public String toString() {
		return "GroupParameters [operationUrl=" + operationUrl + ", categoryTypeId="
				+ categoryTypeId + ", newGroupName=" + newGroupName + ", siteID=" + siteID
				+ ", groupID=" + groupID + ", rt_operationResult=" + rt_operationResult + "]";
	}


}
