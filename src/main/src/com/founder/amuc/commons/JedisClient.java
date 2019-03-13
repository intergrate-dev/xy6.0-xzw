package com.founder.amuc.commons;

import java.util.Set;
import java.util.List;
import java.util.Map;

public interface JedisClient {

	public void hset(String key, String field, String value);
	
	public void hset(String key, long id, String value);
	
	public void hincr(String key, long id);
	
	public String hget(String key, String field);
	
	public List<String> hmget(String key, String field);
	
	public String hget(String key, long id);
	
	public Set<String> hkeys(String key);
	
	public void hclear(String key, String field);
	
	public void hclear(String key, long id);
	
	public void setOneHour(String key, String value);
	
	public void setHalfHour(String key, String value);
	
	public void setOneMinute(String key, String value);
	
	public void set(String key, String value, int expireTime);
	
	public void setAndExpireAt(String key, String value, long expireTime);
	
	public void incrAndExpire(String key,int expireTime);
	
	public void setTimeless(String key, String value);
	
	public String get(String key);
	
	public void clear(String key);
	
	public boolean hexists(String key, String field);
	
	public boolean exists(String key);
	
	public void lpushx(String key,String value,String field);
	
	public String lpop(String key);
	
	public long incr(String key);
	
	public void set(String key,String value);
	
	public void hmset(String key,Map<String,String> value);
	
	public Map<String, String> hgetAll(String key);
}