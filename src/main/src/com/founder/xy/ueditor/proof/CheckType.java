package com.founder.xy.ueditor.proof;

 
/**
 * <p>
 * Java class for CheckType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="CheckType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtensionData" type="{http://tempuri.org/}ExtensionDataObject" minOccurs="0"/>
 *         &lt;element name="CheckLeaderSort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CheckMatchDots" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CheckSentence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class CheckType {

	public int checkLeaderSort =1;
	public int checkMatchDots =1;
	public int checkSentence =1;

	

	/**
	 * Gets the value of the checkLeaderSort property.
	 * 
	 */
	public int getCheckLeaderSort() {
		return checkLeaderSort;
	}
 
	/**
	 * Sets the value of the checkLeaderSort property.
	 * 
	 */
	public void setCheckLeaderSort(int value) {
		this.checkLeaderSort = value;
	}

	/**
	 * Gets the value of the checkMatchDots property.
	 * 
	 */
	public int getCheckMatchDots() {
		return checkMatchDots;
	}

	/**
	 * Sets the value of the checkMatchDots property.
	 * 
	 */
	public void setCheckMatchDots(int value) {
		this.checkMatchDots = value;
	}

	/**
	 * Gets the value of the checkSentence property.
	 * 
	 */
	public int getCheckSentence() {
		return checkSentence;
	}

	/**
	 * Sets the value of the checkSentence property.
	 * 
	 */
	public void setCheckSentence(int value) {
		this.checkSentence = value;
	}

}
