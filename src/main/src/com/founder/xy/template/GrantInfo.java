package com.founder.xy.template;

import java.util.List;
import java.util.Map;

import com.founder.e5.doc.Document;

/**
 * 用于存放授权栏目（把模版关联到栏目上）的一些字段 
 * 1. 传输数据的字段 
 * 2. 数据库查询的字段
 * 3. 返回值的字段
 * 
 * @author Isaac_Gu
 * 
 */
public class GrantInfo {

	/******************* 传输字段 ***********************/
	//grantColumn.do所需字段
	private String action;
	private String docLibID; //数据库表
	private String docIDs; //模版id
	private String FVID;
	private String UUID;
	private String siteID;
	private String groupID;
	private String field;

	//changeColumnTemplateAjax.do
	private String ids = "";
	private String newIds = "";
	String notExpanded = "";

	/******************* 辅助查询字段 ***********************/
	private String db_templateColumnName; // 查询column表的时候，确定是查询哪个column的
	private String db_templateRealName; // 获取栏目的名称，在写栏目日志的时候，填写 挂接到了XX模版上

	/******************* 返回值字段 ***********************/
	//grantColumn.do所需字段
	private String rt_ids;
	private String rt_templateType;

	//changeColumnTemplateAjax.do
	private String rt_operationResult; //挂接栏目操作的返回结果
	private Map<String, List<String>> rt_AddAndDelMap; //存放挂接栏目操作当中的添加列表与删除列表

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

	public String getDb_templateColumnName() {
		return db_templateColumnName;
	}

	public void setDb_templateColumnName(String db_templateColumnName) {
		this.db_templateColumnName = db_templateColumnName;
	}

	public String getNewIds() {
		return newIds;
	}

	public void setNewIds(String newIds) {
		this.newIds = newIds;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getRt_operationResult() {
		return rt_operationResult;
	}

	public void setRt_operationResult(String rt_operationResult) {
		this.rt_operationResult = rt_operationResult;
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

	public String getRt_templateType() {
		return rt_templateType;
	}

	/**
	 * 0=文章模板=col_templateArticle_ID 
	 * 1=栏目模板=col_template_ID 
	 * 2=首页模板=不挂模版
	 * 3=平板文章模板=col_templateArticlePad_ID 
	 * 4=平板栏目模板=col_templatePad_ID
	 * 5=手机文章模板=col_templateArticlePhone_ID 
	 * 6=手机栏目模板=col_templatePhone_ID
	 * 
	 * @param rt_templateType
	 */
	public void setRt_templateType(String rt_templateType) {
		this.rt_templateType = rt_templateType;
		switch (Integer.parseInt(rt_templateType)) {
		case 0:
			this.db_templateColumnName = "col_templateArticle_ID";
			break;
		case 1:
			this.db_templateColumnName = "col_template_ID";
			break;
		case 2:
			this.db_templateColumnName = "";
			break;
		case 3:
			this.db_templateColumnName = "col_templateArticlePad_ID";
			break;
		case 4:
			this.db_templateColumnName = "col_templatePad_ID";
			break;
		case 5:
			this.db_templateColumnName = "col_templateArticlePhone_ID";
			break;
		case 6:
			this.db_templateColumnName = "col_templatePhone_ID";
			break;
		}

	}

	/**
	 * 
	 * type
	 * 栏目 = 0
	 * 文章 = 1
	 * 组图 = 2 
	 * 视频 = 3
	 * channel
	 * pc = 0
	 * 触屏 = 1
	 * @param type
	 * @param channel
	 */
	public void setRt_templateType(int type, int channel) {
		int choice = (type*10 + channel);
		this.rt_templateType = choice+"";
		switch (choice) {
		case 0:
			this.db_templateColumnName = "col_template_ID";
			break;
		case 10:
			this.db_templateColumnName = "col_templateArticle_ID";
			break;
		case 1:
			this.db_templateColumnName = "col_templatePad_ID";
			break;
		case 11:
			this.db_templateColumnName = "col_templateArticlePad_ID";
			break;
		case 20:
			this.db_templateColumnName = "col_templatePic";
			break;
		case 30:
			this.db_templateColumnName = "col_templateVideo";
			break;
		case 21:
			this.db_templateColumnName = "col_templatePicPad";
			break;
		case 31:
			this.db_templateColumnName = "col_templateVideoPad";
			break;
		
		}

	}

	public Map<String, List<String>> getRt_AddAndDelMap() {
		return rt_AddAndDelMap;
	}

	public void setRt_AddAndDelMap(Map<String, List<String>> rt_AddAndDelMap) {
		this.rt_AddAndDelMap = rt_AddAndDelMap;
	}

	public String getDb_templateRealName() {
		return db_templateRealName;
	}

	public void setDb_templateRealName(String db_templateRealName) {
		this.db_templateRealName = db_templateRealName;
	}

	public String getNotExpanded() {
		return notExpanded;
	}

	public void setNotExpanded(String notExpanded) {
		this.notExpanded = notExpanded;
	}

	@Override
	public String toString() {
		return "GrantColumnParameter [action=" + action + ", docLibID=" + docLibID + ", docIDs="
				+ docIDs + ", FVID=" + FVID + ", UUID=" + UUID + ", siteID=" + siteID
				+ ", groupID=" + groupID + ", field=" + field + ", ids=" + ids + ", newIds="
				+ newIds + ", db_templateColumnName=" + db_templateColumnName + ", rt_ids="
				+ rt_ids + ", rt_templateType=" + rt_templateType + ", rt_operationResult="
				+ rt_operationResult + ", rt_AddAndDelMap=" + rt_AddAndDelMap + "]";
	}

}
