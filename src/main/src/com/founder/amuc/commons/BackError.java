package com.founder.amuc.commons;

public class BackError {
	private int docLibId;
	private long docId;
	private String code;
	private String message;
	
	@SuppressWarnings("unused")
	private BackError() {
		super();
	}
	public BackError(int docLibId,int docId,String code,String message){
		this.docLibId = docLibId;
		this.docId = docId;
		this.message = message;
		this.code = code;
	}
	public BackError(int docLibId,int docId,String message){
		this.docLibId = docLibId;
		this.docId = docId;
		this.message = message;
		this.code = "";
	}
	
	public BackError(String code,String message){
		this.docLibId = 0;
		this.docId = 0;
		this.message = message;
		this.code = code;
	}
	
	public BackError(String message){
		this.docLibId = 0;
		this.docId = 0;
		this.message = message;
		this.code = "";
	}
	/**
	 * @return the docLibId
	 */
	public int getDocLibId() {
		return docLibId;
	}
	/**
	 * @param docLibId the docLibId to set
	 */
	public void setDocLibId(int docLibId) {
		this.docLibId = docLibId;
	}
	/**
	 * @return the docId
	 */
	public long getDocId() {
		return docId;
	}
	/**
	 * @param docId the docId to set
	 */
	public void setDocId(long docId) {
		this.docId = docId;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}