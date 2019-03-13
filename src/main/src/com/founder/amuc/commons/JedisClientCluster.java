package com.founder.amuc.commons;

import java.util.Set;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisCluster;

public class JedisClientCluster implements JedisClient{

	@Autowired
	private JedisCluster jedisCluster;
	
	@Override
	public void hset(String key, String field, String value) {
		jedisCluster.hset(key, field, value);
	}
	
	@Override
	public void set(String key, String field) {
		jedisCluster.set(key, field);
	}
	
	@Override
	public void hset(String key, long id, String value) {
		hset(key, String.valueOf(id), value);
	}
	
	@Override
	public void hmset(String key, Map<String,String> value) {
		jedisCluster.hmset(key, value);
	}
	
	@Override
	public void hincr(String key, long id) {
		jedisCluster.hincrBy(key, String.valueOf(id), 1);
	}
	
	@Override
	public long incr(String key) {
		return jedisCluster.incr(key);
	}
	
	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}
	
	@Override
	public String hget(String key, long id) {
		return hget(key, String.valueOf(id));
	}
	
	@Override
	public Map<String, String> hgetAll(String key) {
		return jedisCluster.hgetAll(key);
	}
	
	@Override
	public List<String> hmget(String key, String field) {
		return jedisCluster.hmget(key, field);
	}
	
	@Override
	public Set<String> hkeys(String key) {
		return jedisCluster.hkeys(key);
	}
	
	@Override
	public void hclear(String key, String field) {
		if (jedisCluster.hexists(key, field))
			jedisCluster.hdel(key, field);
	}
	
	@Override
	public void hclear(String key, long id) {
		hclear(key, String.valueOf(id));
	}
	
	@Override
	public void setOneHour(String key, String value) {
		set(key, value, 60 * 60);
	}
	
	@Override
	public void setHalfHour(String key, String value) {
		set(key, value, 60 * 30);
	}
	
	@Override
	public void setOneMinute(String key, String value) {
		set(key, value, 60);
	}
	
	@Override
	public void set(String key, String value, int expireTime) {
		jedisCluster.set(key, value);
		jedisCluster.expire(key, expireTime);
	}
	
	@Override
	public void setAndExpireAt(String key, String value, long expireTime) {
		jedisCluster.set(key, value);
		jedisCluster.expireAt(key, expireTime);
	}
	
	@Override
	public void incrAndExpire(String key, int expireTime) {
		jedisCluster.incr(key);
		jedisCluster.expire(key, expireTime);
	}
	
	@Override
	public void setTimeless(String key, String value) {
		jedisCluster.set(key, value);
	}
	
	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}
	
	@Override
	public void clear(String key) {
		if (jedisCluster.exists(key))
			jedisCluster.del(key);
	}
	
	@Override
	public boolean hexists(String key, String field) {
		return exists(key) && jedisCluster.hexists(key, field);
	}
  
	@Override
	public boolean exists(String key) {
		return jedisCluster.exists(key);
	}
	
	@Override
	public void lpushx(String key,String value,String field) {
		jedisCluster.lpushx(key,value,field);
	}
	
	@Override
	public String lpop(String key) {
		return jedisCluster.lpop(key);
	}
}
