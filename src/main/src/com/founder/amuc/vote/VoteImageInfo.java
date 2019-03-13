package com.founder.amuc.vote;

public class VoteImageInfo {

	private int voteImageId; // 图片信息id
	
	private int imgDeleteFlag; // 删除标志位
	
	private int viVoteId;    // 投票id
	
	private int viHeight;    // 图片高
	
	private int viWidth;    // 图片宽
	
	private String viAddress; // 图片地址
	
	private int viSize;       // 图片尺寸
	
	private int viClassification;  // 图片分类，页眉，选项还是设置
	
	private String viName;     // 图片名
	
	private String viUploadTimeString;  // 上传时间
	
	private String viType;   // 图片类型，png等
	
	private int viOptionId;  // 投票选项ID

	public int getVoteImageId() {
		return voteImageId;
	}

	public void setVoteImageId(int voteImageId) {
		this.voteImageId = voteImageId;
	}

	public int getImgDeleteFlag() {
		return imgDeleteFlag;
	}

	public void setImgDeleteFlag(int imgDeleteFlag) {
		this.imgDeleteFlag = imgDeleteFlag;
	}

	public int getViVoteId() {
		return viVoteId;
	}

	public void setViVoteId(int viVoteId) {
		this.viVoteId = viVoteId;
	}

	public int getViHeight() {
		return viHeight;
	}

	public void setViHeight(int viHeight) {
		this.viHeight = viHeight;
	}

	public int getViWidth() {
		return viWidth;
	}

	public void setViWidth(int viWidth) {
		this.viWidth = viWidth;
	}

	public String getViAddress() {
		return viAddress;
	}

	public void setViAddress(String viAddress) {
		this.viAddress = viAddress;
	}

	public int getViSize() {
		return viSize;
	}

	public void setViSize(int viSize) {
		this.viSize = viSize;
	}

	public int getViClassification() {
		return viClassification;
	}

	public void setViClassification(int viClassification) {
		this.viClassification = viClassification;
	}

	public String getViName() {
		return viName;
	}

	public void setViName(String viName) {
		this.viName = viName;
	}

	public String getViUploadTimeString() {
		return viUploadTimeString;
	}

	public void setViUploadTimeString(String viUploadTimeString) {
		this.viUploadTimeString = viUploadTimeString;
	}

	public String getViType() {
		return viType;
	}

	public void setViType(String viType) {
		this.viType = viType;
	}

	public int getViOptionId() {
		return viOptionId;
	}

	public void setViOptionId(int viOptionId) {
		this.viOptionId = viOptionId;
	}
	
	
}
