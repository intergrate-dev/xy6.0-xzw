package com.founder.xy.topics;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.doc.Document;

import java.sql.Timestamp;

public class Topics {

    private int siteID;
    private int docLibID;
    private long docID;
    private String topic;//话题名称
    private String author;
    private int authorID;
    private String color;
    private int status;//状态
    private String icon;
    private int groupID;
    private int articleCount;

    private String createdTime;
    private String lastModified;
    private String lastPubTime;

    public Topics() {

    }

    public Topics(int docLibID, long docID) {
        this.docLibID = docLibID;
        this.docID = docID;
    }

    public Topics(Document doc) {

        this.siteID = doc.getInt("a_siteID");

        this.docLibID = doc.getDocLibID();
        this.docID = doc.getDocID();

        this.topic = doc.getTopic();
        this.author = doc.getAuthors();
        this.authorID = doc.getInt("SYS_AUTHORID");
        this.color = doc.getString("a_color");
        this.status = doc.getInt("a_status");
        this.icon = doc.getString("a_icon");
        this.groupID = doc.getInt("a_groupID");
        this.articleCount = doc.getInt("a_articleCount");

        Timestamp time = doc.getTimestamp("SYS_CREATED");
        if (time != null) {
            this.createdTime = DateUtils.format(time, "yyyy-MM-dd HH:mm:ss");
        }
        time = doc.getTimestamp("SYS_LASTMODIFIED");
        if (time != null) {
            this.lastModified = DateUtils.format(time, "yyyy-MM-dd HH:mm:ss");
        }
        time = doc.getTimestamp("a_lastPubTime");
        if (time != null) {
            this.lastPubTime = DateUtils.format(time, "yyyy-MM-dd HH:mm:ss");
        }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastPubTime() {
        return lastPubTime;
    }

    public void setLastPubTime(String lastPubTime) {
        this.lastPubTime = lastPubTime;
    }
}
