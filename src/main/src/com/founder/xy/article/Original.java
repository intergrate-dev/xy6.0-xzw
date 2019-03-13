package com.founder.xy.article;

import java.util.Date;

import com.founder.e5.doc.Document;

public class Original{
	
	/**
     * 稿件类型：文章
     */
    public static final int TYPE_ARTICLE = 0;
    /**
     * 稿件类型：组图
     */
    public static final int TYPE_PIC = 1;
    /**
     * 稿件类型：视频
     */
    public static final int TYPE_VIDEO = 2;
    /**
     * 稿件类型：微信
     */
    public static final int TYPE_WEIXIN = 13;
    
    
    /**
     * 草稿
     */
    public static final int STATUS_DRAFT = 0;
    /**
     * 待审核
     */
    public static final int STATUS_AUDITING = 1;
    /**
     * 未签发
     */
    public static final int STATUS_PUBNOT = 2;
    /**
     * 已签发
     */
    public static final int STATUS_PUBDONE = 3;
    /**
     * 已驳回
     */
    public static final int STATUS_REJECTED = 4;
    /**
     * 已提交
     */
    public static final int STATUS_SUBMIT = 5;
    /**
     * 一审审核通过
     */
    public static final int STATUS_PASSFIRST = 6;

    
    private int docLibID;
    private long docID;
    private String author;
    private String editor;
    private String catName;
    private int catID;
    private String source;
    private int sourceID;
    private String url;
    private String topic;
    private String subTitle;
    private String keyword;
    private String summary;
    private String content;

    private boolean isNew;
    private int type;

    // 稿件类型 名称
    private String typeName;
    // 稿件状态
    private int status;
    private String statusName;
    // 创建时间
    private Date createDate;

    public Original() {

    }

    public Original(Document doc) {
        docLibID = doc.getDocLibID();
        docID = doc.getDocID();
        author = doc.getAuthors();
        editor = doc.getString("a_editor");
        catID = doc.getInt("a_catID");
        source = doc.getString("a_source");
        sourceID = doc.getInt("a_sourceID");

        url = doc.getString("a_url");
        topic = doc.getTopic();
        subTitle = doc.getString("a_subTitle");
        keyword = doc.getString("a_keyword");
        summary = doc.getString("a_abstract");
        content = doc.getString("a_content");
        content = content.replaceAll("\r|\n", "");

        type = doc.getInt("a_type");
        status = doc.getInt("a_status");

        setTypeName(type);
        setStatusName(status);
        
        createDate = doc.getDate("SYS_CREATED");
    }

	public void setTypeName(int type) {
        switch (type) {
            case 0:
                this.typeName = "文章";
                break;
            case 1:
                this.typeName = "图片";
                break;
            case 2:
                this.typeName = "视频";
                break;
            case 3:
                this.typeName = "专题";
                break;
            case 4:
                this.typeName = "链接";
                break;
            case 5:
                this.typeName = "多标题";
                break;
            case 6:
                this.typeName = "直播";
                break;
            case 7:
                this.typeName = "广告";
                break;
            case 11:
                this.typeName = "全景图";
                break;
            case 13:
                this.typeName = "微信";
                break;
            case 15:
                this.typeName = "H5";
                break;
        }
    }


    public void setStatusName(int status) {
        switch (status) {
            case 0:
                this.statusName = "草稿";
                break;
            case 1:
                this.statusName = "待审核";
                break;
            case 2:
                this.statusName = "未发布";
                break;
            case 3:
                this.statusName = "已发布";
                break;
            case 4:
                this.statusName = "已驳回";
                break;
            case 5:
                this.statusName = "已提交";
                break;
        }
    }

	public int getDocLibID() {
		return docLibID;
	}

	public void setDocLibID(int docLibID) {
		this.docLibID = docLibID;
	}

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public int getCatID() {
		return catID;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public static int getStatusAuditing() {
		return STATUS_AUDITING;
	}
    
}
