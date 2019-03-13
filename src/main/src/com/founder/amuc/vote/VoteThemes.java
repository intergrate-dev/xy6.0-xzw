package com.founder.amuc.vote;

import java.util.List;


/**
 * @author libin1.dz
 * 投票主题的实体类
 */
public class VoteThemes {

	private int themeId;  //投票主题ID
	
	private int deleteFlag; //删除标志位
	
	private String themeName; // 主题内容
	
	private int voteId;   // 投票ID
	
	private String createdTime; // 创建时间
	
	private String lastModifiedTime; // 修改时间
	
	private int themeIndex; //主题排序码
	
	private int optionNums; // 主题个数
	
	private List<VoteOptions> voteOptionList; // 主题下所有项目的list
	
	private int mostChooseNums; // 每个主题下最多选择几项
	
	private int minChooseNums; // 每个主题下最少选择几项

	public int getOptionNums() {
		return optionNums;
	}

	public void setOptionNums(int optionNums) {
		this.optionNums = optionNums;
	}

	public int getThemeId() {
		return themeId;
	}

	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public int getVoteId() {
		return voteId;
	}

	public void setVoteId(int voteId) {
		this.voteId = voteId;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public int getThemeIndex() {
		return themeIndex;
	}

	public void setThemeIndex(int themeIndex) {
		this.themeIndex = themeIndex;
	}

	public List<VoteOptions> getVoteOptionList() {
		return voteOptionList;
	}

	public void setVoteOptionList(List<VoteOptions> voteOptionList) {
		this.voteOptionList = voteOptionList;
	}

	public int getMostChooseNums() {
		return mostChooseNums;
	}

	public void setMostChooseNums(int mostChooseNums) {
		this.mostChooseNums = mostChooseNums;
	}
	
	public int getMinChooseNums() {
		return minChooseNums;
	}

	public void setMinChooseNums(int minChooseNums) {
		this.minChooseNums = minChooseNums;
	}
}
