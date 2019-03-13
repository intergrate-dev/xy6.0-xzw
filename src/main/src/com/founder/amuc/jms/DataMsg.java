package com.founder.amuc.jms;

/**
 * 数据同步时使用的消息体。使用了原来的会员消息体。
 * 
 * 会员修改和会员合并时对外发送的消息体。
 * 修改时，id并不变化，因此oldID和newID相同。
 * 会员合并时，id也发生变化，通知其它相关功能把id和name都替换成新的。
 * 
 * @author leijj
 * 2014-9-16
 */
public class DataMsg {
	private String oldName;
	private String newName;
	private String oldNo;
	private String newNo;
	private String oldMobile;
	private String newMobile;
	private String oldEmail;
	private String newEmail;
	private long oldID;
	private long newID;
	private String oldQQ;
	private String newQQ;
	private String oldNickName;
	private String newNickName;
	
	private String typecode; //可以放typecode
	
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public String getOldNo() {
		return oldNo;
	}
	public void setOldNo(String oldNo) {
		this.oldNo = oldNo;
	}
	public String getNewNo() {
		return newNo;
	}
	public void setNewNo(String newNo) {
		this.newNo = newNo;
	}
	public String getOldMobile() {
		return oldMobile;
	}
	public void setOldMobile(String oldMobile) {
		this.oldMobile = oldMobile;
	}
	public String getNewMobile() {
		return newMobile;
	}
	public void setNewMobile(String newMobile) {
		this.newMobile = newMobile;
	}
	public String getOldEmail() {
		return oldEmail;
	}
	public void setOldEmail(String oldEmail) {
		this.oldEmail = oldEmail;
	}
	public String getNewEmail() {
		return newEmail;
	}
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
	public long getOldID() {
		return oldID;
	}
	public void setOldID(long oldID) {
		this.oldID = oldID;
	}
	public long getNewID() {
		return newID;
	}
	public void setNewID(long newID) {
		this.newID = newID;
	}
	public String getTypecode() {
		return typecode;
	}
	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}

	public String getOldQQ() {
		return oldQQ;
	}
	public void setOldQQ(String oldQQ) {
		this.oldQQ = oldQQ;
	}
	public String getNewQQ() {
		return newQQ;
	}
	public void setNewQQ(String newQQ) {
		this.newQQ = newQQ;
	}
	public String getOldNickName() {
		return oldNickName;
	}
	public void setOldNickName(String oldNickName) {
		this.oldNickName = oldNickName;
	}
	public String getNewNickName() {
		return newNickName;
	}
	public void setNewNickName(String newNickName) {
		this.newNickName = newNickName;
	}
	public String toString() {
		return "oldName:" + oldName + ",newName:" + newName + ",oldID:" + oldID + ",newID:" + newID + ",typecode:" + typecode+",oldQQ:"+oldQQ+",newQQ:"+newQQ+",oldNickName:"+oldNickName+",newNickName:"+newNickName;
	}
}