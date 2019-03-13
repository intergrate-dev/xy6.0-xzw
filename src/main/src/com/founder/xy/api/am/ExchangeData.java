package com.founder.xy.api.am;

import java.util.Date;
import java.util.List;

public class ExchangeData {

    private int operationCode;

    private int currentUserID;
    
    private String currentUser;

    private Date operationTime;

    private List<DocContent> docContentList;

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public List<DocContent> getDocContentList() {
        return docContentList;
    }

    public void setDocContentList(List<DocContent> docContentList) {
        this.docContentList = docContentList;
    }

	public int getCurrentUserID() {
		return currentUserID;
	}

	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

}
