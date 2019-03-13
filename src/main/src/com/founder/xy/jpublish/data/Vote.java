package com.founder.xy.jpublish.data;

import java.util.List;

/**
 * 投票对象
 * @author Gong Lijie
 */
public class Vote {
	private long id;
	private String title;
	private int type; //0：单选，1：多选
	private int countLimited; //多选的限制个数
	private String endDate;
	
	private List<VoteOption> options;
	
	public Vote(long id, String title, int type, int countLimited, String endDate, List<VoteOption> options) {
		this.id = id;
		this.title = title;
		this.type = type;
		this.countLimited = countLimited;
		this.endDate = endDate;
		this.options = options;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getType() {
		return type;
	}

	public int getCountLimited() {
		return countLimited;
	}

	public String getEndDate() {
		return endDate;
	}

	public List<VoteOption> getOptions() {
		return options;
	}
}
