package com.founder.amuc.commons;

import com.founder.e5.commons.DateUtils;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;
import com.founder.amuc.commons.JedisClient;

public class RedisToolUtil {

	/**
	 * 判断key是否存在，存在的话不再进行后续操作
	 * 不存在的话，加到redis缓存中，并设置有效期
	 * @param key
	 * @param expireTime
	 * @param jedisCluster
	 * @return
	 */
	public static boolean isNotIntented(String key, int expireTime, JedisClient jedisClient) {

		if (jedisClient.exists(key)) {
			return false;
		} else {
			jedisClient.incrAndExpire(key, expireTime);// +1
			/*System.out.println(key);
			System.out.println(jedisCluster.exists(key));*/
		}
		return true;
	}

	/**
	 * 设置key当天23:59:59过期
	 * @param key
	 * @param jedisCluster
	 */
	public static void setOneDay(String key,String value,JedisClient jedisClient){
		
		String dateTime = DateHelper.getSubDate() + " 23:59:59";
		long unixTime = DateUtils.parse(dateTime, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
		jedisClient.setAndExpireAt(key, value, unixTime);
	
	}
}
