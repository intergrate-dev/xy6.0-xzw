package com.founder.xy.wx.data;

import net.sf.json.JSONObject;

import com.founder.e5.commons.StringUtils;

public class GroupArticle {
	private int id;
	private String mediaID;
	private String author;
	private String title;
	private String content;
	private String pic; //封面图地址，如"图片存储;201602/05/abcdeffg.jpg"
	
	public GroupArticle() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMediaID() {
		return mediaID;
	}

	public void setMediaID(String thumb_media_id) {
		this.mediaID = thumb_media_id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
	
	public JSONObject json() {
		JSONObject json = new JSONObject();
		json.put("thumb_media_id", getMediaID());
		json.put("author", getAuthor());
		json.put("title", getTitle());
		json.put("content", getContent());
		json.put("show_cover_pic", (StringUtils.isBlank(pic) ? "0" : "1"));
		
		return json;
	}
}
