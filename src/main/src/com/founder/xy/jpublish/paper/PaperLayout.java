package com.founder.xy.jpublish.paper;

import java.util.Date;
import java.util.List;

import com.founder.e5.doc.Document;
import com.founder.xy.jpublish.data.Attachment;

/**
 * 报纸版面
 * @author Gong Lijie
 */
public class PaperLayout {
	private int docLibID;
	private long id;
	private String layout; //版次
	private String layoutName;
	private String paper;
	private int paperID;
	private Date date;
	private int height;
	private int width;
	private String picPath; //版面图
	private String picUrl;
	private String pdfUrl;//PDF地址
	private String url;
	private String urlPad;
	private String mapping;
	private int status;

	private String dir; //发布路径
	
	private List<PaperLayout> siblings; //同一天的版面列表
	private PaperLayout previous; //上一版
	private PaperLayout next; //下一版
	private List<PaperArticle> articles; //本版的稿件列表
	private List<Attachment> attachments; //版面的其它附件
	
	private String urlAbsolute; //发布过程中使用的，绝对路径Url，用于填写数据库
	private String mappingOriginal; //发布过程中使用的，原始mapping
	
	private String pile; //版面所在的叠的代号，如A/B/SZ
	
	public PaperLayout(Document doc) {
		id = doc.getDocID();
		docLibID = doc.getDocLibID();
		paperID = doc.getInt("pl_paperID");
		paper = doc.getString("pl_paper");
		layout = doc.getString("pl_layout");
		layoutName = doc.getString("pl_layoutName");
		date = doc.getDate("pl_date");
		height = doc.getInt("pl_height");
		width = doc.getInt("pl_width");
		picPath = doc.getString("pl_picPath");
		url = doc.getString("pl_url");
		urlPad = doc.getString("pl_urlPad");
		mapping = doc.getString("pl_mapping");
		status = doc.getInt("pl_status");
		mappingOriginal = mapping;
		
		pile = doc.getString("pl_pile");
	}
	
	public int getDocLibID() {
		return docLibID;
	}
	public long getId() {
		return id;
	}
	public String getLayout() {
		return layout;
	}
	public String getLayoutName() {
		return layoutName;
	}
	public String getPaper() {
		return paper;
	}
	public int getPaperID() {
		return paperID;
	}
	public Date getDate() {
		return date;
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public String getPicPath() {
		return picPath;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public String getUrl() {
		return url;
	}
	public String getUrlPad() {
		return urlPad;
	}
	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public String getMapping() {
		return mapping;
	}
	
	public void setMapping(String mapping){
		this.mapping = mapping;
	}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDir() {
		return dir;
	}

	public List<PaperLayout> getSiblings() {
		return siblings;
	}

	public PaperLayout getPrevious() {
		return previous;
	}
	public PaperLayout getNext() {
		return next;
	}
	public List<PaperArticle> getArticles() {
		return articles;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUrlPad(String urlPad) {
		this.urlPad = urlPad;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setSiblings(List<PaperLayout> siblings) {
		this.siblings = siblings;
	}

	public void setPrevious(PaperLayout previous) {
		this.previous = previous;
	}
	public void setNext(PaperLayout next) {
		this.next = next;
	}
	public void setArticles(List<PaperArticle> articles) {
		this.articles = articles;
	}
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getUrlAbsolute() {
		return urlAbsolute;
	}
	public void setUrlAbsolute(String urlAbsolute) {
		this.urlAbsolute = urlAbsolute;
	}

	public String getMappingOriginal() {
		return mappingOriginal;
	}

	public String getPile() {
		return pile;
	}
}
