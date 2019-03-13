package com.founder.amuc.api.vote.entity;

import java.util.List;

public class VoteThemes {
	
	//private Integer id;
	private Integer vt_id;
	
	//private String vtName;
	private String vt_name;
	
	//private Integer vtVoteID;
	private Integer vt_voteid;
	
	//private String vtCreated;
	private String vt_created;
	
	//private String vtLastModified;
	private String vt_last_modified;
	
	//private Integer vtIndex;
	private Integer vt_index;
	
	//private Integer vtOptionNum;
	private Integer vt_option_num;
	
	//private List<VoteOptions> voteOptionList;
	private List<VoteOptions> vt_voteoption_list;
	
	private Integer vt_most_choose_num;
	
	private Integer vt_min_choose_num;

	public Integer getVt_id() {
		return vt_id;
	}

	public void setVt_id(Integer vt_id) {
		this.vt_id = vt_id;
	}

	public String getVt_name() {
		return vt_name;
	}

	public void setVt_name(String vt_name) {
		this.vt_name = vt_name;
	}



	public String getVt_created() {
		return vt_created;
	}

	public void setVt_created(String vt_created) {
		this.vt_created = vt_created;
	}

	public String getVt_last_modified() {
		return vt_last_modified;
	}

	public void setVt_last_modified(String vt_last_modified) {
		this.vt_last_modified = vt_last_modified;
	}

	public Integer getVt_index() {
		return vt_index;
	}

	public void setVt_index(Integer vt_index) {
		this.vt_index = vt_index;
	}

	public Integer getVt_option_num() {
		return vt_option_num;
	}

	public void setVt_option_num(Integer vt_option_num) {
		this.vt_option_num = vt_option_num;
	}

	public List<VoteOptions> getVt_voteoption_list() {
		return vt_voteoption_list;
	}

	public void setVt_voteoption_list(List<VoteOptions> vt_voteoption_list) {
		this.vt_voteoption_list = vt_voteoption_list;
	}

	public Integer getVt_voteid() {
		return vt_voteid;
	}

	public void setVt_voteid(Integer vt_voteid) {
		this.vt_voteid = vt_voteid;
	}

	public Integer getVt_most_choose_num() {
		return vt_most_choose_num;
	}

	public void setVt_most_choose_num(Integer vt_most_choose_num) {
		this.vt_most_choose_num = vt_most_choose_num;
	}

	public Integer getVt_min_choose_num() {
		return vt_min_choose_num;
	}

	public void setVt_min_choose_num(Integer vt_min_choose_num) {
		this.vt_min_choose_num = vt_min_choose_num;
	}
	
}
