package com.founder.amuc.api.vote.entity;

import java.util.List;


public class VoteCount {
	
	private String vote_count;
	
	private List<VoteOptionCount> option_list;
	
	private String vote_access_count;

	public String getVote_count() {
		return vote_count;
	}

	public void setVote_count(String vote_count) {
		this.vote_count = vote_count;
	}

	public List<VoteOptionCount> getOption_list() {
		return option_list;
	}

	public void setOption_list(List<VoteOptionCount> option_list) {
		this.option_list = option_list;
	}

	public String getVote_access_count() {
		return vote_access_count;
	}

	public void setVote_access_count(String vote_access_count) {
		this.vote_access_count = vote_access_count;
	}


	
}
