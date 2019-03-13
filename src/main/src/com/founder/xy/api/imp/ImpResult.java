package com.founder.xy.api.imp;

import java.util.List;

import com.founder.e5.doc.Document;

public class ImpResult {

	//是否成功
	private String success;
	
	//新闻源系统的稿件Id 
	private int originalId;
	
	//新闻源系统的稿件类型0：文章/1：组图/2：视频
	private int type;
	
	//翔宇发布系统的渠道1：web发布库；2：app发布库
	private int channel;
	
	//翔宇发布系统的生成稿件Id
	private int publishId;

	//------------------------
	
	//是否推送发布 0代表不发布，1代表发布
	private int publish;
	
	//稿件属性
	private Document article;
    
	//附件属性
    private List<Document> attachList;


	//错误代码
	private  String errorCode;

	//错误原因
	private  String errorCause;

	//相关稿件ID
	private  String[] articleRelIDs;

	//发布权限
	private int canPublish;

	//送审权限
	private int canApr;

	public int getCanPublish() {
		return canPublish;
	}

	public void setCanPublish(int canPublish) {
		this.canPublish = canPublish;
	}

	public int getCanApr() {
		return canApr;
	}

	public void setCanApr(int canApr) {
		this.canApr = canApr;
	}

	public String[] getArticleRelIDs() {
		return articleRelIDs;
	}

	public void setArticleRelIDs(String[] articleRelIDs) {
		this.articleRelIDs = articleRelIDs;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public int getOriginalId() {
		return originalId;
	}

	public void setOriginalId(int originalId) {
		this.originalId = originalId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPublishId() {
		return publishId;
	}

	public void setPublishId(int publishId) {
		this.publishId = publishId;
	}

	public int getPublish() {
		return publish;
	}

	public void setPublish(int publish) {
		this.publish = publish;
	}

	public Document getArticle() {
		return article;
	}

	public void setArticle(Document article) {
		this.article = article;
	}

	public List<Document> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<Document> attachList) {
		this.attachList = attachList;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCause() {
		return errorCause;
	}

	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}
	
}
