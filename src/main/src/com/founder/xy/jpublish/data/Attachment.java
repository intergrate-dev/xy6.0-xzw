package com.founder.xy.jpublish.data;

import org.apache.commons.lang.StringUtils;

import com.founder.e5.doc.Document;

/**
 * 附件类，用于发布服务
 * @author Gong Lijie
 */
public class Attachment {
	private long id;
	private int docLibID;
	private long articleID;
	private int type;
	private String path;
	private String url;
	private String urlPad;
	private String content;
	private String fileName;
	
	private String picPath;//显示图片地址（视频关键帧）
	private String picUrl; //显示图片发布地址
	private String picUrlPad;//显示图片发布地址（触屏）
	
	private int order;
	private int duration;
	/**
	 * 正文附件
	 * @param doc
	 */
	public Attachment(Document doc){
		id = doc.getDocID();
		docLibID = doc.getDocLibID();
		articleID = doc.getLong("att_articleID");
		type = doc.getInt("att_type");
		path = doc.getString("att_path");
		url = doc.getString("att_url");
		urlPad = doc.getString("att_urlPad");
		content = doc.getString("att_content");
		order = doc.getInt("att_order");
		duration = doc.getInt("att_duration");
		
		if (StringUtils.isBlank(url)) url = path;
		if (StringUtils.isBlank(urlPad)) urlPad = path;
		if (!StringUtils.isBlank(path)) {
			path = path.replace('\\', '/');
			
			int pos = path.lastIndexOf("/");
			if (pos >= 0)
				fileName = path.substring(pos + 1);
		}
		//视频的显示图片（关键帧）
		picPath = doc.getString("att_picPath");
	}
	
	/**
	 * 挂件中的附件
	 * @param doc
	 * @param isWidget
	 */
	public Attachment(Document doc, boolean isWidget){
		id = doc.getDocID();
		path = doc.getString("w_path");
		url = doc.getString("w_url");
		urlPad = doc.getString("w_urlPad");
		content = doc.getString("w_content");
		
		if (StringUtils.isBlank(url)) url = path;
		if (StringUtils.isBlank(urlPad)) urlPad = path;
		
		if (!StringUtils.isBlank(path)) {
			path = path.replace('\\', '/');
			
			int pos = path.lastIndexOf("/");
			if (pos >= 0)
				fileName = path.substring(pos + 1);
		}
	}
	
	public long getId() {
		return id;
	}

	public void setId(long docID) {
		this.id = docID;
	}

	public int getDocLibID() {
		return docLibID;
	}

	public long getArticleID() {
		return articleID;
	}

	public void setArticleID(long articleID) {
		this.articleID = articleID;
	}

	public int getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getOrder() {
		return order;
	}

	public int getDuration() {
		return duration;
	}

	public String getFileName() {
		return fileName;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlPad() {
		return urlPad;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrlPad(String urlPad) {
		this.urlPad = urlPad;
	}

	public String getPicPath() {
		return picPath;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getPicUrlPad() {
		return picUrlPad;
	}

	public void setPicUrlPad(String picUrlPad) {
		this.picUrlPad = picUrlPad;
	}
}
