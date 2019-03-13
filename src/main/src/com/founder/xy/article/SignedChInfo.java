package com.founder.xy.article;

/**
 * 发布渠道签发栏目信息
 * @author Deng Chaochen
 */
public class SignedChInfo {
	private int id;
	private String code;	//渠道代码
	private String name;	//渠道名称
	private String colName;	//主栏目名称
	private long colID;	//主栏目ID
	private String colRelName;	//关联栏目名称
	private String colRelID;  //关联栏目ID
	private String colPreName; //预签发主栏目名称
	private long colPreID; //预签发主栏目ID
	private String colPreRelName;	//预签发关联栏目名称
	private String colPreRelID;  //预签发关联栏目ID

	public SignedChInfo(int id, String code, String name) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public SignedChInfo(int id, String code, String name, String colName, String colRelName) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.colName = colName;
		this.colRelName = colRelName;
	}


	public int getId() {
		return id;
	}


	public String getColName() {
		return colName;
	}


	public String getColRelName() {
		return colRelName;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public long getColID() {
		return colID;
	}

	public String getColRelID() {
		return colRelID;
	}

	public void setColID(long colID) {
		this.colID = colID;
	}

	public void setColRelID(String colRelID) {
		this.colRelID = colRelID;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public void setColRelName(String colRelName) {
		this.colRelName = colRelName;
	}

	public String getColPreName() {
		return colPreName;
	}

	public void setColPreName(String colPreName) {
		this.colPreName = colPreName;
	}

	public long getColPreID() {
		return colPreID;
	}

	public void setColPreID(long colPreID) {
		this.colPreID = colPreID;
	}

	public String getColPreRelName() {
		return colPreRelName;
	}

	public void setColPreRelName(String colPreRelName) {
		this.colPreRelName = colPreRelName;
	}

	public String getColPreRelID() {
		return colPreRelID;
	}

	public void setColPreRelID(String colPreRelID) {
		this.colPreRelID = colPreRelID;
	}
	
	
}
