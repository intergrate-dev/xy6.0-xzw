package com.founder.amuc.api.vote;

public class VoteInfoTime {
	
	private Integer isBegin;
	
	public VoteInfoTime(Integer isBegin, String state, String message) {
		super();
		this.isBegin = isBegin;
		this.state = state;
		this.message = message;
	}

	private String state;
	
	private String message;

	public Integer getIsBegin() {
		return isBegin;
	}

	public void setIsBegin(Integer isBegin) {
		this.isBegin = isBegin;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
