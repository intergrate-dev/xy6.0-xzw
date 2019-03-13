package com.founder.xy.article;

import java.util.Date;

import com.founder.e5.doc.Document;

/**
 * 相关稿件
 * @author guzm
 *
 */
public class ArticleRel {
	private Long id;
	private int docLibId;
	private int folderId;
	private String topic;
	private int relId;
	private int relLibId;
	private int type;
	private Date pubTime;
	private String contentURL;
	private String contentURLPad;
	private String picBig;
	private String picMiddle;
	private String picSmall;
	private int articleId;
	private int articleLibId;

	public ArticleRel() {
	}

	public ArticleRel(Document doc) {
		super();
		this.id = doc.getDocID();
		this.docLibId = doc.getDocLibID();
		this.folderId = doc.getFolderID();
		this.topic = doc.getString("SYS_TOPIC");
		this.relId = doc.getInt("a_relID");
		this.relLibId = doc.getInt("a_relLibID");
		this.type = doc.getInt("a_type");
		this.pubTime = doc.getDate("a_pubTime");
		this.contentURL = doc.getString("a_url");
		this.contentURLPad = doc.getString("a_urlPad");
		this.picBig = doc.getString("a_picBig");
		this.picMiddle = doc.getString("a_picMiddle");
		this.picSmall = doc.getString("a_picSmall");
		this.articleId = doc.getInt("a_articleID");
		this.articleLibId = doc.getInt("a_articleLibID");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getDocLibId() {
		return docLibId;
	}

	public void setDocLibId(int docLibId) {
		this.docLibId = docLibId;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getRelId() {
		return relId;
	}

	public void setRelId(int relId) {
		this.relId = relId;
	}

	public int getRelLibId() {
		return relLibId;
	}

	public void setRelLibId(int relLibId) {
		this.relLibId = relLibId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getPubTime() {
		return pubTime;
	}

	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}

	public String getContentURL() {
		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}

	public String getContentURLPad() {
		return contentURLPad;
	}

	public void setContentURLPad(String contentURLPad) {
		this.contentURLPad = contentURLPad;
	}

	public String getPicBig() {
		return picBig;
	}

	public void setPicBig(String picBig) {
		this.picBig = picBig;
	}

	public String getPicMiddle() {
		return picMiddle;
	}

	public void setPicMiddle(String picMiddle) {
		this.picMiddle = picMiddle;
	}

	public String getPicSmall() {
		return picSmall;
	}

	public void setPicSmall(String picSmall) {
		this.picSmall = picSmall;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getArticleLibId() {
		return articleLibId;
	}

	public void setArticleLibId(int articleLibId) {
		this.articleLibId = articleLibId;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + articleId;
		result = prime * result + articleLibId;
		result = prime * result + docLibId;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + relId;
		result = prime * result + relLibId;
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + type;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleRel other = (ArticleRel) obj;
		if (articleId != other.articleId)
			return false;
		if (articleLibId != other.articleLibId)
			return false;
		if (docLibId != other.docLibId)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (relId != other.relId)
			return false;
		if (relLibId != other.relLibId)
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ArticleRel [id=" + id + ", docLibId=" + docLibId + ", folderId=" + folderId
				+ ", topic=" + topic + ", relId=" + relId + ", relLibId=" + relLibId + ", type="
				+ type + ", pubTime=" + pubTime + ", contentURL=" + contentURL + ", contentURLPad=" + contentURLPad +", picBig="
				+ picBig + ", picMiddle=" + picMiddle + ", picSmall=" + picSmall + ", articleId="
				+ articleId + ", articleLibId=" + articleLibId + "]";
	}

}
