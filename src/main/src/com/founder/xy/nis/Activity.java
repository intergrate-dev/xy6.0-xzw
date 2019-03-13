package com.founder.xy.nis;

import java.sql.Timestamp;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.doc.Document;

public class Activity {
	private int siteID;
	private int docLibID;
	private long docID;
	private String topic;
	private String summary;
	private String content;
	private String location; //活动地点
	private String organizer; //主办方
	private String author;
	private String picBig; // = "图片存储;201505/20/0b927b03-cb3e-43b0-8155-df3e813341ed.jpg";
	private String picMiddle;
	private String picSmall;

	private String startTime;
	private String endTime;
	
	private int countLimited; //报名数限制
	
	private int status;//状态
	private int count; //报名数
	
	public Activity(int docLibID, long docID) {
		this.docLibID = docLibID;
		this.docID = docID;
	}
	public Activity(Document doc) {
		siteID = doc.getInt("a_siteID");
		
		docLibID = doc.getDocLibID();
		docID = doc.getDocID();
		
		topic = doc.getTopic();
		summary = doc.getString("a_abstract");
		content = doc.getString("a_content");
		content = content.replaceAll("\r|\n", "");
		location = doc.getString("a_location");
		organizer = doc.getString("a_organizer");
		author = doc.getAuthors();
		picBig = doc.getString("a_picBig");
		picMiddle = doc.getString("a_picMiddle");
		picSmall = doc.getString("a_picSmall");

		status = doc.getInt("a_status");

		Timestamp pubTime = doc.getTimestamp("a_startTime");
		if (pubTime != null)
			startTime = DateUtils.format(pubTime, "yyyy-MM-dd HH:mm:ss");
		pubTime = doc.getTimestamp("a_endTime");
		if (pubTime != null)
			endTime = DateUtils.format(pubTime, "yyyy-MM-dd HH:mm:ss");
		
		countLimited = doc.getInt("a_countLimited");
		count = doc.getInt("a_count");
	}

	public int getSiteID() {
		return siteID;
	}

	public void setSiteID(int siteID) {
		this.siteID = siteID;
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

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getOrganizer() {
		return organizer;
	}
	
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getCountLimited() {
		return countLimited;
	}

	public void setCountLimited(int countLimited) {
		this.countLimited = countLimited;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
}
