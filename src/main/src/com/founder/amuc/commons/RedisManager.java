package com.founder.amuc.commons;

import java.util.Set;

import com.founder.e5.context.Context;

import redis.clients.jedis.JedisCluster;

public class RedisManager {
	private static JedisCluster jedisCluster = null;
	private static int hour1 = 60 * 60 ; //设置Key的过期时间，1小时
	private static int halfhour = 60 * 30; // 30分钟
	private static int minute1 = 60; //1分钟
	
	public static JedisCluster getJedisCluster(){
		if (jedisCluster == null){
			jedisCluster = (JedisCluster)Context.getBean("jedisCluster");
		}
		return jedisCluster;
	}
	
	public static void hset(String key, String field, String value) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.hset(key, field, value);
	}
	
	public static void hset(String key, long id, String value) {
		hset(key, String.valueOf(id), value);
	}
	
	/**给hash的一个值+1，用于点击数、分享数等 */
	public static void hincr(String key, long id) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.hincrBy(key, String.valueOf(id), 1);
	}
	
	/**
	 * 从Redis中取出一个值
	 */
	public static String hget(String key, String field) {
		JedisCluster jedisCluster = getJedisCluster();
		
		if (jedisCluster.hexists(key, field))
			return jedisCluster.hget(key, field);
		else
			return null;
	}
	/**
	 * field是整数时，从Redis中取出一个值
	 */
	public static String hget(String key, long id) {
		return hget(key, String.valueOf(id));
	}
	
	public static Set<String> hkeys(String key) {
		JedisCluster jedisCluster = getJedisCluster();
		
		return jedisCluster.hkeys(key);
	}
	
	/**
	 * 清空一个值
	 */
	public static void hclear(String key, String field) {
		JedisCluster jedisCluster = getJedisCluster();
		if (jedisCluster.hexists(key, field))
			jedisCluster.hdel(key, field);
	}
	/**
	 * 清空一个值
	 */
	public static void hclear(String key, long id) {
		hclear(key, String.valueOf(id));
	}
	
	/**
	 * 设置Key的值，默认保存1小时
	 */
	public static void setOneHour(String key, String value) {
		set(key, value, hour1);
	}
	
	/**
	 * 设置Key的值，保存30分钟
	 */
	public static void setHalfHour(String key, String value) {
		set(key, value, halfhour);
	}
	
	/**
	 * 设置Key的值，保存1分钟
	 */
	public static void setOneMinute(String key, String value) {
		set(key, value, minute1);
	}
	
	/**
	 * 设置Key的值，指定过期时间
	 */
	public static void set(String key, String value, int expireTime) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.set(key, value);
		jedisCluster.expire(key, expireTime);
	}
	
	/**
	 * 设置Key的值，无过期时间
	 */
	public static void setTimeless(String key, String value) {
		JedisCluster jedisCluster = getJedisCluster();
		jedisCluster.set(key, value);
	}
	
	/**
	 * 从Redis中取出一个值
	 */
	public static String get(String key) {
		JedisCluster jedisCluster = getJedisCluster();
		if (jedisCluster.exists(key))
			return jedisCluster.get(key);
		else
			return null;
	}
	
	/**
	 * 清空一个值
	 */
	public static void clear(String key) {
		JedisCluster jedisCluster = getJedisCluster();
		if (jedisCluster.exists(key))
			jedisCluster.del(key);
	}
	
  public static boolean hexists(String key, String field) {
    return exists(key) && jedisCluster.hexists(key, field);
  }
  
  /**
   * 判断一个key是否存在
   */
  public static boolean exists(String key) {
    JedisCluster jedisCluster = getJedisCluster();
    return jedisCluster.exists(key);
  }
}
