package com.founder.xy.jms.data;


/**
 * 稿件发布传送的消息
 * 
 * @author Gong Lijie
 */
public class ArticleMsg {
	protected int docLibID; // 稿件库ID
	protected long id; // 稿件ID
	protected int type; // 稿件类型
	protected int channel; // 发布渠道 1-Web, 2-App
	
	protected int colID; // 主栏目ID
	protected String colAll; // 所有栏目的ID数组（主栏目、关联栏目、聚合栏目）

	public ArticleMsg() {
	}

	public ArticleMsg(int aDocLibID, long aID, int colID, String colAll,
			int aType, int aChannel) {
		this.docLibID = aDocLibID;
		this.id = aID;
		this.colID = colID;
		this.colAll = colAll;
		this.type = aType;
		this.channel = aChannel;
	}

	public int getColID() {
		return colID;
	}

	public long getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getChannel() {
		return channel;
	}

	public int getDocLibID() {
		return docLibID;
	}

	public String getColAll() {
		return colAll;
	}

}
