package com.founder.xy.block;

import com.founder.e5.doc.Document;

public class Block implements Cloneable{
	private long id;
	private int siteID;
	private String name;
	private String format;
	private int type; //0-手动,1-自动
	private int channel; //0-网站,1-触屏
	private int picType;//0-大,1-中,2-小
	private int dirID;
	private int count; //显示个数
	private String templateParsed;
	
	private String dir; //区块的发布根目录
	private String url; //区块的url
	private String path; //区块页面的发布路径
	private String fileName; //区块文件名
	
	public Block(Document doc) {
		id = doc.getDocID();
		name = doc.getString("b_name");
		format = doc.getString("b_format");
		siteID = doc.getInt("b_siteID");
		type = doc.getInt("b_type");
		channel = doc.getInt("b_channel");
		picType = doc.getInt("b_picType");
		dirID = doc.getInt("b_dir_ID");
		dir = doc.getString("b_dir");
		templateParsed = doc.getString("b_templateParsed");
		
		count = doc.getInt("b_count");
		if (count < 0) count = 0;
		
		fileName = "b" + id + "." + format; //区块文件名：b213.html
		url = dir + "/" + fileName; //区块发布Url：http://172.19.33.95/block/b213.html
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getFormat() {
		return format;
	}
	public int getType() {
		return type;
	}
	public int getChannel() {
		return channel;
	}
	public int getPicType() {
		return picType;
	}
	public int getDirID() {
		return dirID;
	}
	public String getDir() {
		return dir;
	}
	public String getUrl() {
		return url;
	}
	public String getTemplateParsed() {
		return templateParsed;
	}
	public int getSiteID() {
		return siteID;
	}
	
	public String getPath() {
		return path;
	}
	public String getFileName() {
		return fileName;
	}
	public int getCount() {
		return count;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public Block clone() {
		try {
			return (Block)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
