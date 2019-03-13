package com.founder.xy.statistics.entity;

/**
 * Created by Ethan on 2016/12/19.
 */
public class PersonalTotalWorkStatistics {
    private String authorName;
    private int authorID;
    private int totalArticle;
    private int totalClick;
    private int pcClick;
    private int wapClick;
    private int appClick;
    private int totalForward;
    private int pcForward;
    private int wapForward;
    private int appForward;
    private int totalDiscussion;
    private int pcDiscussion;
    private int wapDiscussion;
    private int appDiscussion;

    public PersonalTotalWorkStatistics() {
    }

    public PersonalTotalWorkStatistics(String authorName, int authorID, int totalArticle, int totalClick, int pcClick, int wapClick, int appClick, int totalForward, int pcForward, int wapForward, int appForward, int totalDiscussion, int pcDiscussion, int wapDiscussion, int appDiscussion) {
        this.authorName = authorName;
        this.authorID = authorID;
        this.totalArticle = totalArticle;
        this.totalClick = totalClick;
        this.pcClick = pcClick;
        this.wapClick = wapClick;
        this.appClick = appClick;
        this.totalForward = totalForward;
        this.pcForward = pcForward;
        this.wapForward = wapForward;
        this.appForward = appForward;
        this.totalDiscussion = totalDiscussion;
        this.pcDiscussion = pcDiscussion;
        this.wapDiscussion = wapDiscussion;
        this.appDiscussion = appDiscussion;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getTotalArticle() {
        return totalArticle;
    }

    public void setTotalArticle(int totalArticle) {
        this.totalArticle = totalArticle;
    }

    public int getTotalClick() {
        return totalClick;
    }

    public void setTotalClick(int totalClick) {
        this.totalClick = totalClick;
    }

    public int getPcClick() {
        return pcClick;
    }

    public void setPcClick(int pcClick) {
        this.pcClick = pcClick;
    }

    public int getWapClick() {
        return wapClick;
    }

    public void setWapClick(int wapClick) {
        this.wapClick = wapClick;
    }

    public int getAppClick() {
        return appClick;
    }

    public void setAppClick(int appClick) {
        this.appClick = appClick;
    }

    public int getTotalForward() {
        return totalForward;
    }

    public void setTotalForward(int totalForward) {
        this.totalForward = totalForward;
    }

    public int getPcForward() {
        return pcForward;
    }

    public void setPcForward(int pcForward) {
        this.pcForward = pcForward;
    }

    public int getWapForward() {
        return wapForward;
    }

    public void setWapForward(int wapForward) {
        this.wapForward = wapForward;
    }

    public int getAppForward() {
        return appForward;
    }

    public void setAppForward(int appForward) {
        this.appForward = appForward;
    }

    public int getTotalDiscussion() {
        return totalDiscussion;
    }

    public void setTotalDiscussion(int totalDiscussion) {
        this.totalDiscussion = totalDiscussion;
    }

    public int getPcDiscussion() {
        return pcDiscussion;
    }

    public void setPcDiscussion(int pcDiscussion) {
        this.pcDiscussion = pcDiscussion;
    }

    public int getWapDiscussion() {
        return wapDiscussion;
    }

    public void setWapDiscussion(int wapDiscussion) {
        this.wapDiscussion = wapDiscussion;
    }

    public int getAppDiscussion() {
        return appDiscussion;
    }

    public void setAppDiscussion(int appDiscussion) {
        this.appDiscussion = appDiscussion;
    }
}
