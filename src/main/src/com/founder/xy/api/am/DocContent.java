package com.founder.xy.api.am;

import java.sql.Timestamp;
import java.util.List;

import com.founder.e5.doc.Document;

public class DocContent {
	
	private String id;

    private String guid;
    
    private int columnId;//栏目ID

    private long publishId;//文档ID

    private int publishStatus;//发布状态 0表示预发布
    
    private Timestamp publishTime;
    
    private Document article;
    
    private List<Document> attachList;
    
    private Document[] oldAttachList;

    private int batmanId;//通讯员ID
    
    private String industry;
    
    private String docregion;
    
    private String category;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public long getPublishId() {
		return publishId;
	}

	public void setPublishId(long publishId) {
		this.publishId = publishId;
	}

	public Timestamp getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}

	public Document getArticle() {
		return article;
	}

	public void setArticle(Document article) {
		this.article = article;
	}

	public List<Document> getAttachementList() {
		return attachList;
	}

	public void setAttachementList(List<Document> attachList) {
		this.attachList = attachList;
	}

	public Document[] getOldAttachList() {
		return oldAttachList;
	}

	public void setOldAttachList(Document[] old) {
		this.oldAttachList = old;
	}

	public int getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(int publishStatus) {
		this.publishStatus = publishStatus;
	}

	public int getBatmanId() {
		return batmanId;
	}

	public void setBatmanId(int batmanId) {
		this.batmanId = batmanId;
	}

	public List<Document> getAttachList() {
		return attachList;
	}

	public void setAttachList(List<Document> attachList) {
		this.attachList = attachList;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getDocregion() {
		return docregion;
	}

	public void setDocregion(String docregion) {
		this.docregion = docregion;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
