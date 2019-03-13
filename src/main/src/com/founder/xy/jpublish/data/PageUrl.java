package com.founder.xy.jpublish.data;

public class PageUrl {
	private String article;
	private String pic;
	private String attachment;
	
	private String anotherUrl;//另一个版的稿件url（触屏版Url、网站版url等）
	
	public PageUrl(String article, String pic, String attachment) {
		super();
		this.article = article;
		this.pic = pic;
		this.attachment = attachment;
	}
	
	public String getArticle() {
		return article;
	}
	public String getPic() {
		return pic;
	}
	public String getAttachment() {
		return attachment;
	}
	
	public String getAnotherUrl() {
		return anotherUrl;
	}

	public void setAnotherUrl(String another) {
		this.anotherUrl = another;
	}
}
