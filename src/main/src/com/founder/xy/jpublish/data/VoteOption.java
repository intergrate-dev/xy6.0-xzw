package com.founder.xy.jpublish.data;

import org.apache.commons.lang.StringUtils;

/**
 * 投票选项对象
 * @author Gong Lijie
 */
public class VoteOption {
	private long id;
	private String option;
	private String picUrl;
	private String picFile;
	
	public VoteOption(long id, String option, String picUrl) {
		super();
		this.id = id;
		this.option = option;
		this.picUrl = picUrl;
		
		if (!StringUtils.isBlank(picUrl)) {
			int pos = picUrl.lastIndexOf("/");
			if (pos >= 0)
				picFile = picUrl.substring(pos + 1);
		}
	}
	public long getId() {
		return id;
	}
	public String getOption() {
		return option;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public String getPicFile() {
		return picFile;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
}
