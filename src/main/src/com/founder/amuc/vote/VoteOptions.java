package com.founder.amuc.vote;


/**
 * @author libin1.dz
 * 投票选项的实体类
 */
public class VoteOptions {

	private int voteOpId;   //投票选项id
	
	private int deleteFlag; //删除标志位
	
	private int voteId;     //投票id
	
	private String voName;  // 投票选项主题
	
	private String voImgAdd; // 选项图片地址
	
	private String voLink;   // 选项链接
	
	private String voVideoAdd; // 选项视频链接
	
	private int voVotes;       // 选项投票数
	
	private String voClassification; // 选项分类
	
	private int voType;  // 选项类型
	
	private String voCreated; // 创建时间
	
	private String voLastModified; // 修改时间
	
	private int voIndex;  //选项排序码
	
	private int voImgInfoId;  // 图片信息id
	
	private int voThemeId;   // 投票主题id
	
	private String voViewContent; // 查看页内容
	
	private int voShowOpImgFlag;  // 是否显示选项图片
	
	public int getVoThemeId() {
		return voThemeId;
	}
	public void setVoThemeId(int voThemeId) {
		this.voThemeId = voThemeId;
	}
	public String getVoViewContent() {
		return voViewContent;
	}
	public void setVoViewContent(String voViewContent) {
		this.voViewContent = voViewContent;
	}
	public int getVoShowOpImgFlag() {
		return voShowOpImgFlag;
	}
	public void setVoShowOpImgFlag(int voShowOpImgFlag) {
		this.voShowOpImgFlag = voShowOpImgFlag;
	}
	public int getVoImgInfoId() {
		return voImgInfoId;
	}
	public void setVoImgInfoId(int voImgInfoId) {
		this.voImgInfoId = voImgInfoId;
	}
	public int getVoteOpId() {
		return voteOpId;
	}
	public void setVoteOpId(int voteOpId) {
		this.voteOpId = voteOpId;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public int getVoteId() {
		return voteId;
	}
	public void setVoteId(int voteId) {
		this.voteId = voteId;
	}
	public String getVoName() {
		return voName;
	}
	public void setVoName(String voName) {
		this.voName = voName;
	}
	public String getVoImgAdd() {
		return voImgAdd;
	}
	public void setVoImgAdd(String voImgAdd) {
		this.voImgAdd = voImgAdd;
	}
	public String getVoLink() {
		return voLink;
	}
	public void setVoLink(String voLink) {
		this.voLink = voLink;
	}
	public String getVoVideoAdd() {
		return voVideoAdd;
	}
	public void setVoVideoAdd(String voVideoAdd) {
		this.voVideoAdd = voVideoAdd;
	}
	public int getVoVotes() {
		return voVotes;
	}
	public void setVoVotes(int voVotes) {
		this.voVotes = voVotes;
	}
	public String getVoClassification() {
		return voClassification;
	}
	public void setVoClassification(String voClassification) {
		this.voClassification = voClassification;
	}
	public int getVoType() {
		return voType;
	}
	public void setVoType(int voType) {
		this.voType = voType;
	}
	public String getVoCreated() {
		return voCreated;
	}
	public void setVoCreated(String voCreated) {
		this.voCreated = voCreated;
	}
	public String getVoLastModified() {
		return voLastModified;
	}
	public void setVoLastModified(String voLastModified) {
		this.voLastModified = voLastModified;
	}
	public int getVoIndex() {
		return voIndex;
	}
	public void setVoIndex(int voIndex) {
		this.voIndex = voIndex;
	}	
}
