package com.founder.xy.jpublish.data;


/**
 * 一个挂件
 * @author Gong Lijie
 */
public class Widget {
	private long id;
	private String title = "";
	private String content = "";
	private String url = "";
	private String fileName = "";
	
	//构造方法：用于构造组图挂件的成员图
	public Widget(String content, String url) {
		this.content = content;
		this.url = url;
	}

	//构造方法：用于构造视频挂件
	public Widget(long id, String topic, String content, String url) {
		this.id = id;
		this.title = topic;
		this.content = content;
		this.url = url;
	}
	
	public long getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
}