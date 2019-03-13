package com.founder.xy.weibo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * ΢���˻�
 * 
 * @since 2011-7-27
 * @author slim 
 * Copyright (c) 2011, ������������޹�˾����ý�忪����PTC��ƽ̨��
 *
 */
public class WeiboAccount implements Serializable {
	private static final long serialVersionUID = 3044883307082751806L;
	
	private int accountId;//�˻�ID
	private String accessToken;//�˻���֤
	private String accessTokenSecret;//�˻���Կ
	private String accountName;//�˻���
	private String accountRealName;//��ʵ�˻���
	private String accountPassword;//����
	private int providerId;//�˻�������Id
	private String providerName;//�˻����������
	private int deleteFlag;//�Ƿ�����
	private java.util.Date lastModified;//����޸�ʱ��
	private AccountProperties accountProperties;//�˻���չ����
	/**����refresh_token�ֶ� mm.liu 2013-11-13 begin**/
	private String refreshToken;//ˢ��token
	/**********************end****************************/
	
	/**
	 * @return ��� accountRealName
	 */
	public String getAccountRealName() {
		return accountRealName;
	}
	/**
	 * @param accountRealName ���� accountRealName
	 */
	public void setAccountRealName(String accountRealName) {
		this.accountRealName = accountRealName;
	}
	/**
	 * @return ��� accountPassword
	 */
	public String getAccountPassword() {
		return accountPassword;
	}
	/**
	 * @param accountPassword ���� accountPassword
	 */
	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}
	public AccountProperties getAccountProperties()
	{
		return accountProperties;
	}
	public void setAccountProperties(AccountProperties accountProperties)
	{
		this.accountProperties = accountProperties;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId( int accountId ) {
		this.accountId = accountId;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken( String accessToken ) {
		this.accessToken = accessToken;
	}
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}
	public void setAccessTokenSecret( String accessTokenSecret ) {
		this.accessTokenSecret = accessTokenSecret;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName( String accountName ) {
		this.accountName = accountName;
	}
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId( int providerId ) {
		this.providerId = providerId;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName( String providerName ) {
		this.providerName = providerName;
	}
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag( int deleteFlag ) {
		this.deleteFlag = deleteFlag;
	}
	public java.util.Date getLastModified() {
		return lastModified;
	}
	public void setLastModified( java.util.Date lastModified ) {
		this.lastModified = lastModified;
	}
	public void setRefreshToken(String refreshToken){
		this.refreshToken = refreshToken;
	}
	public String getRefreshToken(){
		return this.refreshToken;
	}
	public String toString() {
		return this.toString();
	}
}
