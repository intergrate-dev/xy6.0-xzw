package com.founder.xy.weibo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.founder.e5.db.util.StringUtils;

/**
 * ΢�������ṩ��
 * 
 * @since 2011-7-27
 * @author slim 
 * Copyright (c) 2011, ������������޹�˾����ý�忪����PTC��ƽ̨��
 *
 */
public class WeiboServiceProvider implements Serializable{
	private static final long serialVersionUID = 6793443252759400271L;

	private String providerName;//�ṩ�����
	private int providerId;//�ṩ��ID
	private int deleteFlag;//����״̬
	private Date lastModified;//����޸�ʱ��
	private String consumerKey;//Ӧ��AppKey
	private String consumerSecret;//Ӧ��Secret
	private List<WeiboAccount> weiboAccountList;
	
	public String getConsumerKey()
	{
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey)
	{
		this.consumerKey = consumerKey;
	}
	public String getConsumerSecret()
	{
		return consumerSecret;
	}
	public void setConsumerSecret(String consumerSecret)
	{
		this.consumerSecret = consumerSecret;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName( String providerName ) {
		this.providerName = providerName;
	}
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId( int providerId ) {
		this.providerId = providerId;
	}
	
	public int getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag( int deleteFlag ) {
		this.deleteFlag = deleteFlag;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified( Date lastModified ) {
		this.lastModified = lastModified;
	}
	public String toString() {
		//return StringUtils.toString( this );
		return this.toString();
	}
	/**
	 * @return the weiboAccountList
	 */
	public List<WeiboAccount> getWeiboAccountList() {
		return weiboAccountList;
	}
	/**
	 * @param weiboAccountList the weiboAccountList to set
	 */
	public void setWeiboAccountList(List<WeiboAccount> weiboAccountList) {
		this.weiboAccountList = weiboAccountList;
	}
	
}
