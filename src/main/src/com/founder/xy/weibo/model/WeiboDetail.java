package com.founder.xy.weibo.model;


/**
 * @Description: ΢�����Ext_propertiesֵ�ж�Ӧ��pubAccBean
 * 
 * @author: tongxs
 * @date: 2015-6-15 ����3:12:36
 */
public class WeiboDetail {
	private long ID;// ID
	private long docID;// ���ID
	private int docLibID;// �����ID
	private long groupID;// ��ҵ�˺�ID	
	private int accountID;// �˻�ID
	private int userID; // ΢���˺�userID
	private String wid; // ΢��ID
	private long reposts;// ת����
	private long comments;// ������
	private String source; // ��Դ
	private String status; // ����״̬: 0Ϊ���棬 1-�����У�2-�����ɹ������������ã�
	// weiboType��΢�����ͣ��� 1-��ͨ-text��2-ͼƬ-pic��3-��Ƶ-music��4-��Ƶ-video��5-����-topic��6-��΢��-ltext;7-����-comment,8-�ظ�-reply,9-ת��-repost��
	private int weiboType; 
	private String type; // �˺����ͣ�sina��qq
	private String imageUrl; // 
	private String musicUrl; // 
//	private String musicAuthor; 
//	private String musicTitle; 
	private String videoUrl; // 
//	private String extendType; 
//	private String replyWeiboID; 
	/**
	 * @return the iD
	 */
	public long getID() {
		return ID;
	}
	/**
	 * @param iD the iD to set
	 */
	public void setID(long iD) {
		ID = iD;
	}
	/**
	 * @return the docID
	 */
	public long getDocID() {
		return docID;
	}
	/**
	 * @param docID the docID to set
	 */
	public void setDocID(long docID) {
		this.docID = docID;
	}
	/**
	 * @return the docLibID
	 */
	public long getDocLibID() {
		return docLibID;
	}
	/**
	 * @param docLibID the docLibID to set
	 */
	public void setDocLibID(int docLibID) {
		this.docLibID = docLibID;
	}
	/**
	 * @return the groupID
	 */
	public long getGroupID() {
		return groupID;
	}
	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(long groupID) {
		this.groupID = groupID;
	}
	/**
	 * @return the accountID
	 */
	public int getAccountID() {
		return accountID;
	}
	/**
	 * @param accountID the accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	/**
	 * @return the wid
	 */
	public String getWid() {
		return wid;
	}
	/**
	 * @param wid the wid to set
	 */
	public void setWid(String wid) {
		this.wid = wid;
	}
	/**
	 * @return the reposts
	 */
	public long getReposts() {
		return reposts;
	}
	/**
	 * @param reposts the reposts to set
	 */
	public void setReposts(long reposts) {
		this.reposts = reposts;
	}
	/**
	 * @return the comments
	 */
	public long getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(long comments) {
		this.comments = comments;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the weiboType
	 */
	public int getWeiboType() {
		return weiboType;
	}
	/**
	 * @param weiboType the weiboType to set
	 */
	public void setWeiboType(int weiboType) {
		this.weiboType = weiboType;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the musicUrl
	 */
	public String getMusicUrl() {
		return musicUrl;
	}
	/**
	 * @param musicUrl the musicUrl to set
	 */
	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}
	/**
	 * @return the videoUrl
	 */
	public String getVideoUrl() {
		return videoUrl;
	}
	/**
	 * @param videoUrl the videoUrl to set
	 */
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	

}
