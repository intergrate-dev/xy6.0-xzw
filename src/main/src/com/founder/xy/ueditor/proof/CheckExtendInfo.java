package com.founder.xy.ueditor.proof;


/**
 * <p>
 * Java class for CheckExtendInfo complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="CheckExtendInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtensionData" type="{http://tempuri.org/}ExtensionDataObject" minOccurs="0"/>
 *         &lt;element name="ChkDefault" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ChkEng" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ChkReturnResultType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FanTi" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="HtmlTag" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Leader" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Needsug" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ProfType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TaiWan" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UserErr" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Weight" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class CheckExtendInfo {
  
	public int chkDefault =1;
	public int chkEng=1;
	public int chkReturnResultType =0;
	public int fanTi=1;
	public int htmlTag=1;
	public int leader =1;
	public int needsug=1;
	public int profType=1;
	public int taiWan=1;
	public int userErr=1;
	public int weight =1;

	/**
	 * Gets the value of the chkDefault property.
	 * 
	 */
	public int getChkDefault() {
		return chkDefault;
	}

	/**
	 * Sets the value of the chkDefault property.
	 * 
	 */
	public void setChkDefault(int value) {
		this.chkDefault = value;
	}

	/**
	 * Gets the value of the chkEng property.
	 * 
	 */
	public int getChkEng() {
		return chkEng;
	}

	/**
	 * Sets the value of the chkEng property.
	 * 
	 */
	public void setChkEng(int value) {
		this.chkEng = value;
	}

	/**
	 * Gets the value of the chkReturnResultType property.
	 * 
	 */
	public int getChkReturnResultType() {
		return chkReturnResultType;
	}

	/**
	 * Sets the value of the chkReturnResultType property.
	 * 
	 */
	public void setChkReturnResultType(int value) {
		this.chkReturnResultType = value;
	}

	/**
	 * Gets the value of the fanTi property.
	 * 
	 */
	public int getFanTi() {
		return fanTi;
	}

	/**
	 * Sets the value of the fanTi property.
	 * 
	 */
	public void setFanTi(int value) {
		this.fanTi = value;
	}

	/**
	 * Gets the value of the htmlTag property.
	 * 
	 */
	public int getHtmlTag() {
		return htmlTag;
	}

	/**
	 * Sets the value of the htmlTag property.
	 * 
	 */
	public void setHtmlTag(int value) {
		this.htmlTag = value;
	}

	/**
	 * Gets the value of the leader property.
	 * 
	 */
	public int getLeader() {
		return leader;
	}

	/**
	 * Sets the value of the leader property.
	 * 
	 */
	public void setLeader(int value) {
		this.leader = value;
	}

	/**
	 * Gets the value of the needsug property.
	 * 
	 */
	public int getNeedsug() {
		return needsug;
	}

	/**
	 * Sets the value of the needsug property.
	 * 
	 */
	public void setNeedsug(int value) {
		this.needsug = value;
	}

	/**
	 * Gets the value of the profType property.
	 * 
	 */
	public int getProfType() {
		return profType;
	}

	/**
	 * Sets the value of the profType property.
	 * 
	 */
	public void setProfType(int value) {
		this.profType = value;
	}

	/**
	 * Gets the value of the taiWan property.
	 * 
	 */
	public int getTaiWan() {
		return taiWan;
	}

	/**
	 * Sets the value of the taiWan property.
	 * 
	 */
	public void setTaiWan(int value) {
		this.taiWan = value;
	}

	/**
	 * Gets the value of the userErr property.
	 * 
	 */
	public int getUserErr() {
		return userErr;
	}

	/**
	 * Sets the value of the userErr property.
	 * 
	 */
	public void setUserErr(int value) {
		this.userErr = value;
	}

	/**
	 * Gets the value of the weight property.
	 * 
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Sets the value of the weight property.
	 * 
	 */
	public void setWeight(int value) {
		this.weight = value;
	}

}
