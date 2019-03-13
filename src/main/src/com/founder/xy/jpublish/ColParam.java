package com.founder.xy.jpublish;

/**
 * 栏目页的参数
 * @author Gong Lijie
 */
public class ColParam {
	private int colLibID;
	private long colID;
	private int page;

	public ColParam(int colLibID, long colID, int page) {
		this.colLibID = colLibID;
		this.colID = colID;
		this.page = page;
	}

	public long getColID() {
		return colID;
	}

	public int getPage() {
		return page;
	}

	public int getColLibID() {
		return colLibID;
	}

	public void setColLibID(int colLibID) {
		this.colLibID = colLibID;
	}

	public void setColID(long colID) {
		this.colID = colID;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
