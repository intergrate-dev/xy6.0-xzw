package com.founder.amuc.api.vote.entity;

/**
 * @author fei.lai
 * 投票选项的实体类
 */
public class VoteOptions {

	//private Integer id;   //投票选项id
	private Integer vo_id;
	
	//private Integer voteId;     //投票id
	private Integer vo_voteid; 
	
	//private String voName;  // 投票选项主题
	private String vo_name;
	
	//private String voVideoAdd; // 选项视频链接
	private String vo_videoadd;
	
	//private Integer voVotes;       // 选项投票数
	private Integer vo_votes;
	
	//private String voClassification; // 选项分类
	private String vo_classification;
	
	//private Integer voType;  // 选项类型
	private Integer vo_type;
	
	//private String voCreated; // 创建时间
	private String vo_created;
	
	//private String voLastModified; // 修改时间
	private String vo_last_modified;
	
	//private Integer voIndex;  //选项排序码
	private Integer vo_index;
	
	//private VoteImage voteImage;
	private VoteImage vo_voteimage;
	
	//private Integer voThemeID;
	private Integer vo_themeid;
	
	//private String voViewPageContent;
	private String vo_view_pagecontent;
	
	//private Integer voShowOpImgOnpage;
	private Integer vo_show_op_imgonpage;

	public Integer getVo_id() {
		return vo_id;
	}

	public void setVo_id(Integer vo_id) {
		this.vo_id = vo_id;
	}

	public Integer getVo_voteid() {
		return vo_voteid;
	}

	public void setVo_voteid(Integer vo_voteid) {
		this.vo_voteid = vo_voteid;
	}

	public String getVo_name() {
		return vo_name;
	}

	public void setVo_name(String vo_name) {
		this.vo_name = vo_name;
	}

	public String getVo_videoadd() {
		return vo_videoadd;
	}

	public void setVo_videoadd(String vo_videoadd) {
		this.vo_videoadd = vo_videoadd;
	}

	public Integer getVo_votes() {
		return vo_votes;
	}

	public void setVo_votes(Integer vo_votes) {
		this.vo_votes = vo_votes;
	}

	public String getVo_classification() {
		return vo_classification;
	}

	public void setVo_classification(String vo_classification) {
		this.vo_classification = vo_classification;
	}

	public Integer getVo_type() {
		return vo_type;
	}

	public void setVo_type(Integer vo_type) {
		this.vo_type = vo_type;
	}

	public String getVo_created() {
		return vo_created;
	}

	public void setVo_created(String vo_created) {
		this.vo_created = vo_created;
	}

	public String getVo_last_modified() {
		return vo_last_modified;
	}

	public void setVo_last_modified(String vo_last_modified) {
		this.vo_last_modified = vo_last_modified;
	}

	public Integer getVo_index() {
		return vo_index;
	}

	public void setVo_index(Integer vo_index) {
		this.vo_index = vo_index;
	}

	public VoteImage getVo_voteimage() {
		return vo_voteimage;
	}

	public void setVo_voteimage(VoteImage vo_voteimage) {
		this.vo_voteimage = vo_voteimage;
	}

	public Integer getVo_themeid() {
		return vo_themeid;
	}

	public void setVo_themeid(Integer vo_themeid) {
		this.vo_themeid = vo_themeid;
	}

	public String getVo_view_pagecontent() {
		return vo_view_pagecontent;
	}

	public void setVo_view_pagecontent(String vo_view_pagecontent) {
		this.vo_view_pagecontent = vo_view_pagecontent;
	}

	public Integer getVo_show_op_imgonpage() {
		return vo_show_op_imgonpage;
	}

	public void setVo_show_op_imgonpage(Integer vo_show_op_imgonpage) {
		this.vo_show_op_imgonpage = vo_show_op_imgonpage;
	}



}
