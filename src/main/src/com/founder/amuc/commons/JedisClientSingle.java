package com.founder.amuc.commons;

import java.util.Set;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientSingle implements JedisClient{
	
	@Autowired
	private JedisPool jedisPool;
	
	@Override
	public void hset(String key, String field, String value){
		Jedis jedis = jedisPool.getResource();
		jedis.hset(key, field, value);
		
		jedis.close();
	}
	
	@Override
	public void set(String key, String field){
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, field);
		
		jedis.close();
	}
	
	@Override
	public void hset(String key, long id, String value) {
		hset(key, String.valueOf(id), value);
	}
	
	@Override
	public void hmset(String key, Map<String,String> value) {
		Jedis jedis = jedisPool.getResource();
		jedis.hmset(key, value);
		jedis.close();
	}
	
	@Override
	public void hincr(String key, long id) {
		Jedis jedis = jedisPool.getResource();
		jedis.hincrBy(key, String.valueOf(id), 1);
		
		jedis.close();
	}
	
	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.incr(key);
		
		jedis.close();
		return result;
	}
	
	@Override
	public String hget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.hget(key, field);
		
		jedis.close();
		return result;
	}
	
	@Override
	public String hget(String key, long id) {
		return hget(key, String.valueOf(id));
	}
	
	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = jedisPool.getResource();
		Map<String, String> result = jedis.hgetAll(key);
		
		jedis.close();
		return result;
	}
	
	@Override
	public List<String> hmget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		List<String> result = jedis.hmget(key, field);
		
		jedis.close();
		return result;
	}
	
	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = jedisPool.getResource();
		Set<String> result = jedis.hkeys(key);
		
		jedis.close();
		return result;
	}
	
	@Override
	public void hclear(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		if (jedis.hexists(key, field))
			jedis.hdel(key, field);
		jedis.close();
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
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.expire(key, expireTime);
		
		jedis.close();
	}
	
	@Override
	public void setAndExpireAt(String key, String value, long expireTime) {
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		jedis.expireAt(key, expireTime);
		
		jedis.close();
	}
	
	@Override
	public void incrAndExpire(String key, int expireTime) {
		Jedis jedis = jedisPool.getResource();
		jedis.incr(key);
		jedis.expire(key, expireTime);
		
		jedis.close();
	}
	
	@Override
	public void setTimeless(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		jedis.set(key, value);
		
		jedis.close();
	}
	
	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		
		jedis.close();
		return result;
	}
	
	@Override
	public void clear(String key) {
		Jedis jedis = jedisPool.getResource();
		if (jedis.exists(key))
			jedis.del(key);
		
		jedis.close();
	}
	
	@Override
	public boolean hexists(String key, String field) {
		return exists(key) && jedisPool.getResource().hexists(key, field);
	}
  
	@Override
	public boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		boolean result = jedis.exists(key);
	
		jedis.close();
		return result;
	}
	
	@Override
	public void lpushx(String key,String value,String field) {
		Jedis jedis = jedisPool.getResource();
		jedis.lpushx(key,value,field);
		
		jedis.close();
	}
	
	@Override
	public String lpop(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.lpop(key);
		
		jedis.close();
		return result;
	}
}
