package com.founder.xy.article;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.founder.e5.doc.Document;

/**
 * 用于存放授权栏目（把模版关联到栏目上）的一些字段
 * 1. 传输数据的字段
 * 2. 数据库查询的字段
 * 3. 返回值的字段
 * 
 * @author Isaac_Gu
 */
public class GrantInfo {
	/******************************** 1.传输数据的字段 ********************************/
	// xy/extfield/grantColumn.do所需字段
	private String action;
	private String docLibID; // 数据库表
	private String docIDs; // 模版id
	private String FVID;
	private String UUID;
	private String siteID;
	private String groupID;
	private String field;
	private String procType;
	private String procID;
	private String flowNodeID;
	private String opID;

	private String colID;
	private String extFieldGroupID;

	// grantColumnsAjax.do
	private String ids = "";
	private String ids_0 = "";
	private String ids_1 = "";
	private String newIds = "";
	private String ch = "";

	private String groupName = "";

	/******************************** 2.数据库查询的字段 ********************************/
	private String rt_ids;
	private String rt_operationResult; // 挂接栏目操作的返回结果
	private Map<String, List<String>> rt_AddAndDelMap; // 存放挂接栏目操作当中的添加列表与删除列表
	private JSONObject rt_operationJson = new JSONObject();

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDocLibID() {
		return docLibID;
	}

	public void setDocLibID(String docLibID) {
		this.docLibID = docLibID;
	}

	public String getDocIDs() {
		return docIDs;
	}

	public void setDocIDs(String docIDs) {
		this.docIDs = docIDs;
	}

	public String getFVID() {
		return FVID;
	}

	public void setFVID(String fVID) {
		FVID = fVID;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getProcType() {
		return procType;
	}

	public void setProcType(String procType) {
		this.procType = procType;
	}

	public String getProcID() {
		return procID;
	}

	public void setProcID(String procID) {
		this.procID = procID;
	}

	public String getFlowNodeID() {
		return flowNodeID;
	}

	public void setFlowNodeID(String flowNodeID) {
		this.flowNodeID = flowNodeID;
	}

	public String getOpID() {
		return opID;
	}

	public void setOpID(String opID) {
		this.opID = opID;
	}

	public String getRt_ids() {
		return rt_ids;
	}

	public void setRt_ids(String rt_ids) {
		this.rt_ids = rt_ids;
	}

	public void setRt_ids(Document[] doc) {
		if (doc != null && doc.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (Document d : doc) {
				sb.append(d.get("SYS_DOCUMENTID") + ",");
			}
			this.rt_ids = sb.toString().substring(0, sb.length() - 1);
		} else {
			this.rt_ids = "";
		}

	}

	public String getRt_operationResult() {
		return rt_operationResult;
	}

	public void setRt_operationResult(String rt_operationResult) {
		this.rt_operationResult = rt_operationResult;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds_0() {
		return ids_0;
	}

	public void setIds_0(String ids_0) {
		this.ids_0 = ids_0;
	}

	public String getIds_1() {
		return ids_1;
	}

	public void setIds_1(String ids_1) {
		this.ids_1 = ids_1;
	}

	public String getNewIds() {
		return newIds;
	}

	public void setNewIds(String newIds) {
		this.newIds = newIds;
	}

	public Map<String, List<String>> getRt_AddAndDelMap() {
		return rt_AddAndDelMap;
	}

	public void setRt_AddAndDelMap(Map<String, List<String>> rt_AddAndDelMap) {
		this.rt_AddAndDelMap = rt_AddAndDelMap;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getColID() {
		return colID;
	}

	public void setColID(String colID) {
		this.colID = colID;
	}

	public JSONObject getRt_operationJson() {
		return rt_operationJson;
	}

	public void setRt_operationJson(JSONObject rt_operationJson) {
		this.rt_operationJson = rt_operationJson;
	}

	public String getExtFieldGroupID() {
		return extFieldGroupID;
	}

	public void setExtFieldGroupID(String extFieldGroupID) {
		this.extFieldGroupID = extFieldGroupID;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	@Override
	public String toString() {
		return "GrantInfo [action=" + action + ", docLibID=" + docLibID + ", docIDs=" + docIDs
				+ ", FVID=" + FVID + ", UUID=" + UUID + ", siteID=" + siteID + ", groupID="
				+ groupID + ", field=" + field + ", procType=" + procType + ", procID=" + procID
				+ ", flowNodeID=" + flowNodeID + ", opID=" + opID + ", colID=" + colID
				+ ", extFieldGroupID=" + extFieldGroupID + ", ids=" + ids + ", ids_0=" + ids_0
				+ ", ids_1=" + ids_1 + ", newIds=" + newIds + ", ch=" + ch + ", groupName="
				+ groupName + ", rt_ids=" + rt_ids + ", rt_operationResult=" + rt_operationResult
				+ ", rt_AddAndDelMap=" + rt_AddAndDelMap + ", rt_operationJson=" + rt_operationJson
				+ "]";
	}


}
