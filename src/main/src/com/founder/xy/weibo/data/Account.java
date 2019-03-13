package com.founder.xy.weibo.data;

import com.founder.e5.doc.Document;

/**
 * 微信账号
 * @author Gong Lijie
 */
public class Account {
	
	private long id;
	private int libID;
	private String name;
	private String appKey;
	private String appSecret;
	private String appUser; //微博账号的登录名和密码，自动授权时用，实际上未调通自动授权，因此没用
	private String appPassword;
	
	private String accessToken; //访问令牌
	private int siteID;
	private boolean multiPic;
	
	public Account(Document account) {
		id = account.getDocID();
		libID = account.getDocLibID();
		name = account.getString("wba_name");
		appKey = account.getString("wba_appID");
		appSecret = account.getString("wba_appSecret");
		
		accessToken = account.getString("wba_token");
		
		siteID = account.getInt("wba_siteID");
		multiPic = account.getInt("wba_multiPic") == 1;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public int getLibID() {
		return libID;
	}

	public int getSiteID() {
		return siteID;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getAppUser() {
		return appUser;
	}

	public String getAppPassword() {
		return appPassword;
	}

	public boolean isMultiPic() {
		return multiPic;
	}
}
