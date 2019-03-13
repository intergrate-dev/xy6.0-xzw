package com.founder.amuc.api.vote;

public class VoteInfo {

	private String info;
	
	private String state;
	
	public VoteInfo() {
	}
	
	public VoteInfo(String info, String state) {
		this.info = info;
		this.state = state;
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
