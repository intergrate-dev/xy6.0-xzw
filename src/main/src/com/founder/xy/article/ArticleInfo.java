package com.founder.xy.article;

/**
 * Created by Administrator on 2017/3/17.
 * 用于存放稿件ID，是否置顶，固定位置属性
 */
public class ArticleInfo {
    private long docID;//稿件ID
    private boolean isTop;//是否置顶
    private int position;//固定位置

    public long getDocID() {
        return docID;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
