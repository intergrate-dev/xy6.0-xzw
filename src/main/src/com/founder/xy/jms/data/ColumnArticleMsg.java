package com.founder.xy.jms.data;

import java.util.List;


/**
 * 稿件发布传送的消息
 * 
 * @author Gong Lijie
 */
public class ColumnArticleMsg {
	private int colLibID;
	private long colID; // 栏目ID
	private List<ArticleMsg> articles; //栏目下需要重新发布的稿件数据

	public ColumnArticleMsg() {
	}

	public ColumnArticleMsg(int colLibID, long colID, List<ArticleMsg> articles) {
		super();
		this.colLibID = colLibID;
		this.colID = colID;
		this.articles = articles;
	}

	public void setColID(long colID) {
		this.colID = colID;
	}

	public void setArticles(List<ArticleMsg> articles) {
		this.articles = articles;
	}

	public int getColLibID() {
		return colLibID;
	}

	public long getColID() {
		return colID;
	}

	public List<ArticleMsg> getArticles() {
		return articles;
	}
}
