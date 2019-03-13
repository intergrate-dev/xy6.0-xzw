package com.founder.amuc.commons;


public class RedisKey {

	//---------------====== App接口中使用 ======---------------------------
	/** 登录Token和ID的映射关系，Hash，《token, userID》 */
	public static final String AMUC_TOKEN_USER = "amuc.token.user";
	/** 登录Token和ID的映射关系，Hash，《userID, token》 */
	public static final String AMUC_USER_TOKEN = "amuc.user.token";
}