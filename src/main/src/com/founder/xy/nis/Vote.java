package com.founder.xy.nis;

import com.founder.e5.doc.Document;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 投票
 */
public class Vote {
    String UUID;
    int DocLibID;
    Long docID;

    String author;
    Integer authorId;
    Integer vote_siteID;     //所属站点
    Integer vote_groupID;    //所属分组
    String vote_topic;      //标题
    Integer vote_type;       //选择方式
    Integer vote_selectLimited;   //多选限制选择个数
    String vote_endDate;    //截止日期
    String deleteOptionIds;
    String deleteQuestionIds;
    Integer vote_cycle;   //投票周期
    Integer vote_cyclenum;   //周期次数
    Integer vote_needlogin;   //需要登录
    Integer vote_onlyapp;   //仅在App
    Integer vote_totalNum;   //投票次数

    boolean isNew;


    public Vote() {
    }

    public Vote(Document doc) {
        this.docID = doc.getDocID();
        this.DocLibID = doc.getDocLibID();
        this.vote_siteID = doc.getInt("vote_siteID");
        this.vote_groupID = doc.getInt("vote_groupID");
        this.vote_topic = doc.getString("vote_topic");
        this.vote_type = doc.getInt("vote_type");
        this.vote_selectLimited = doc.getInt("vote_selectLimited");
        this.vote_cycle = doc.getInt("vote_cycle");
        this.vote_cyclenum = doc.getInt("vote_cyclenum");
        this.vote_needlogin = doc.getInt("vote_needlogin");
        this.vote_onlyapp = doc.getInt("vote_onlyapp");
        this.vote_totalNum = doc.getInt("vote_totalNum");

        //修改日期格式
        Timestamp ts = doc.getTimestamp("vote_endDate");
        if(ts!=null)
            this.vote_endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(ts);

    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getDocLibID() {
        return DocLibID;
    }

    public void setDocLibID(int docLibID) {
        DocLibID = docLibID;
    }

    public Long getDocID() {
        return docID;
    }

    public void setDocID(Long docID) {
        this.docID = docID;
    }

    public Integer getVote_siteID() {
        return vote_siteID;
    }

    public void setVote_siteID(Integer vote_siteID) {
        this.vote_siteID = vote_siteID;
    }

    public Integer getVote_groupID() {
        return vote_groupID;
    }

    public void setVote_groupID(Integer vote_groupID) {
        this.vote_groupID = vote_groupID;
    }

    public String getVote_topic() {
        return vote_topic;
    }

    public void setVote_topic(String vote_topic) {
        this.vote_topic = vote_topic;
    }

    public Integer getVote_type() {
        return vote_type;
    }

    public void setVote_type(Integer vote_type) {
        this.vote_type = vote_type;
    }

    public Integer getVote_selectLimited() {
        return vote_selectLimited;
    }

    public void setVote_selectLimited(Integer vote_selectLimited) {
        this.vote_selectLimited = vote_selectLimited;
    }

    public String getVote_endDate() {
        return vote_endDate;
    }

    public void setVote_endDate(String vote_endDate) {
        this.vote_endDate = vote_endDate;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getDeleteOptionIds() {
        return deleteOptionIds;
    }

    public void setDeleteOptionIds(String deleteOptionIds) {
        this.deleteOptionIds = deleteOptionIds;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getDeleteQuestionIds() {
        return deleteQuestionIds;
    }

    public void setDeleteQuestionIds(String deleteQuestionIds) {
        this.deleteQuestionIds = deleteQuestionIds;
    }

    public Integer getVote_cycle() {
        return vote_cycle;
    }

    public void setVote_cycle(Integer vote_cycle) {
        this.vote_cycle = vote_cycle;
    }

    public Integer getVote_cyclenum() {
        return vote_cyclenum;
    }

    public void setVote_cyclenum(Integer vote_cyclenum) {
        this.vote_cyclenum = vote_cyclenum;
    }

    public Integer getVote_needlogin() {
        return vote_needlogin;
    }

    public void setVote_needlogin(Integer vote_needlogin) {
        this.vote_needlogin = vote_needlogin;
    }

    public Integer getVote_onlyapp() {
        return vote_onlyapp;
    }

    public void setVote_onlyapp(Integer vote_onlyapp) {
        this.vote_onlyapp = vote_onlyapp;
    }

    public Integer getVote_totalNum() {
        return vote_totalNum;
    }

    public void setVote_totalNum(Integer vote_totalNum) {
        this.vote_totalNum = vote_totalNum;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "UUID='" + UUID + '\'' +
                ", DocLibID=" + DocLibID +
                ", docID=" + docID +
                ", author='" + author + '\'' +
                ", authorId=" + authorId +
                ", vote_siteID=" + vote_siteID +
                ", vote_groupID=" + vote_groupID +
                ", vote_topic='" + vote_topic + '\'' +
                ", vote_type=" + vote_type +
                ", vote_selectLimited=" + vote_selectLimited +
                ", vote_cycle=" + vote_cycle +
                ", vote_cyclenum=" + vote_cyclenum +
                ", vote_needlogin=" + vote_needlogin +
                ", vote_onlyapp=" + vote_onlyapp +
                ", vote_totalNum=" + vote_totalNum +
                ", vote_endDate='" + vote_endDate + '\'' +
                ", deleteOptionIds='" + deleteOptionIds + '\'' +
                ", isNew=" + isNew +
                '}';
    }

}