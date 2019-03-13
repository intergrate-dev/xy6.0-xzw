package com.founder.amuc.commons.attachment;

import java.text.DecimalFormat;


/**
 * 附件存储对象
 */
public class Attach {
	/* 附件Id */
	private long guid;

	/* 对应稿件的文档库ID */
	private int docLibID;

	/* 对应稿件的文档ID */
	private long docID;

	/* 附件类型 */
	private int attType;

	/* 附件扩展名jpg、gif... */
	private String attFormat;

	/* 附件标题 */
	private String attTopic;

	/* 附件说明 */
	private String attContent;

	/* 附件大小 :字节*/
	private long attSize;

	/* 与文档关联类型:1/附件,0/正文图片 */
	private int relType;

	/* 读取时从数据库取得的bfile */
	private String attPath;

	/* 获取附件的href，可通过href得到stream，非必填 */
	private String href;

	/* 关联ID，有些应用系统解析附件需要，非必填 */
	private String refID;
	/* ftp用户名，可选 */
	private int user;
	/* ftp用户密码，可选 */
	private String password;

	private String sizeDescription;/*附件大小的文字性描述，如1.7K，2.5M等*/
	private String topicHTML; /*符合HTML 格式的标题，有特殊字符的转码*/
	
	public String getTopicHTML() {
		return topicHTML;
	}

	public String getSizeDescription() {
		return sizeDescription;
	}

	public String getPath() {
		return AttachUtil.getAttachUrl(this.guid);
	}

	public String getAttTopic() {
		return attTopic;
	}

	public void setAttTopic(String mediaCaption) {
		this.attTopic = mediaCaption;
		//同时设置标题的HTML格式
		topicHTML = attTopic;
	}

	public String getAttContent() {
		return attContent;
	}

	public void setAttContent(String mediaDescript) {
		this.attContent = mediaDescript;
	}

	public long getGuid() {
		return guid;
	}

	public void setGuid(long mediaID) {
		this.guid = mediaID;
	}

	public int getAttType() {
		return attType;
	}

	public void setAttType(int mediaMainType) {
		this.attType = mediaMainType;
	}

	public long getAttSize() {
		return attSize;
	}

	public void setAttSize(long mediaSize) {
		this.attSize = mediaSize;
		
		//同时设置size的文字性描述
		DecimalFormat df = null;
		if (attSize < 1024 )
			sizeDescription = "1(K)";
		else if( attSize < 1024*1024)
		{
			df = new java.text.DecimalFormat("#0"); 
			sizeDescription = df.format( attSize/1024.0 ) + "(K)";
		}
		else {
			df = new DecimalFormat("#0.00"); 
			sizeDescription = df.format( attSize/1024.0/1024.0 ) +"(M)";
		}
	}

	public String getAttFormat() {
		return attFormat;
	}

	public void setAttFormat(String meidiaFormat) {
		this.attFormat = meidiaFormat;
	}

	public int getRelType() {
		return relType;
	}

	public void setRelType(int relType) {
		this.relType = relType;
	}

	public String getAttFileName() {
		return AttachUtil.getAttachName(this);
	}

	// ------------------------ 类型检查函数 -------------------------------

	/**
	 * @return the attPath
	 */
	public String getAttPath() {
		return attPath;
	}

	/**
	 * @param attPath
	 *            the attPath to set
	 */
	public void setAttPath(String attPath) {
		this.attPath = attPath;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getRefID() {
		return refID;
	}

	public void setRefID(String refID) {
		this.refID = refID;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public int getDocLibID() {
		return docLibID;
	}

	public void setDocLibID(int docLibID) {
		this.docLibID = docLibID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}
}
