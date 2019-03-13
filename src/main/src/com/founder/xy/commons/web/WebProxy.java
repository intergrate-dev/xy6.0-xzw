package com.founder.xy.commons.web;

public class WebProxy {
	private String serverAddress;
	private int port;
	private String userCode;
	private String userPwd;
	private boolean start;
	private boolean userAuthentication;

	public boolean isStart() {
		return start;
	}

	public boolean isUserAuthentication() {
		return userAuthentication;
	}

	public void setUserAuthentication(boolean userAuthentication) {
		this.userAuthentication = userAuthentication;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
}
