package com.founder.xy.weibo.data;

import java.sql.Timestamp;

public class WeiboArticle {
	private long id;
	private int docLibID;
	private int accountID;
	private String content;
	private Timestamp pubTime;
	
	private String attachments;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getDocLibID() {
		return docLibID;
	}
	public void setDocLibID(int docLibID) {
		this.docLibID = docLibID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getPubTime() {
		return pubTime;
	}
	public void setPubTime(Timestamp pubTime) {
		this.pubTime = pubTime;
	}

	public int getAccountID() {
		return accountID;
	}
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	public String getAttachments() {
		return attachments;
	}
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
}
