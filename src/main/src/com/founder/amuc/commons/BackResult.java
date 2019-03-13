package com.founder.amuc.commons;

import java.util.ArrayList;
import java.util.List;

public class BackResult {
	private boolean success;
	private long docID;
	private int docLibID;
	private List<BackError> errors;
	
	public BackResult() {
		super();
	}
	public BackResult(boolean success,String error){
		this.docID = 0;
		this.docLibID = 0;
		List<BackError> errors = new ArrayList<BackError>();
		errors.add(new BackError(error));
		this.errors = errors;
		this.success = success;
	}
	public BackResult(boolean success,List<BackError> errors){
		this.docID = 0;
		this.docLibID = 0;
		this.errors = errors;
		this.success = success;
	}
	public BackResult(boolean success){
		this.docID = 0;
		this.docLibID = 0;
		this.errors = new ArrayList<BackError>();
		this.success = success;
	}
	
	public BackResult(int docLibID,long docID, boolean success,List<BackError> errors){
		this.docID = docID ;
		this.docLibID = docLibID;
		this.errors = errors;
		this.success = success;
	}
	public BackResult(int docLibID,long docID,boolean success){
		this.docID = docID;
		this.docLibID = docLibID;
		this.errors = new ArrayList<BackError>();
		this.success = success;
	}
	
	public long getDocID() {
		return docID;
	}
	public void setDocID(long docID) {
		this.docID = docID;
	}
	public int getDocLibID() {
		return docLibID;
	}
	public void setDocLibID(int docLibID) {
		this.docLibID = docLibID;
	}
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * @return the errors
	 */
	public List<BackError> getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<BackError> errors) {
		this.errors = errors;
	}
}