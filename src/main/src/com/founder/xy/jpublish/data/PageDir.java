package com.founder.xy.jpublish.data;

public class PageDir {
	private String article;
	private String pic;
	private String attachment;
	private String picRoot;
	private String root;
	
	public PageDir(String article, String pic, String attachment,
			String picRoot, String root) {
		super();
		this.article = article;
		this.pic = pic;
		this.attachment = attachment;
		this.picRoot = picRoot;
		this.root = root;
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
	public String getPicRoot() {
		return picRoot;
	}
	public String getRoot() {
		return root;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
}
