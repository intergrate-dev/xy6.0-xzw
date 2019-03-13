package com.founder.xy.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wenkx on 2017/12/27.
 */
public class RedisPoolService implements RedisService {
    private JedisPool jedisPool;

    public final int minute1 = 60; //设置Key的过期时间，1分钟
    public final int hour1 = minute1 * 60 ; //设置Key的过期时间，1小时
    public final int day1 = hour1 * 24; //设置Key的过期时间，1天
    public final int week1 = day1 * 7; //1周

    public final int hour2 = hour1 * 2 ; //2小时，用于按小时区分的点击数
    public final int hour25 = hour1 * 25 ; //25小时，用于按天区分的点击数

    public int THREE_DAYS = day1 * 3;
    

    public  RedisPoolService (JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }



    public void hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.hset(key, field, value);
        jedis.close();
    }

    public void hset(String key, String field, long value) {
        Jedis jedis = jedisPool.getResource();
        jedis.hset(key, field, String.valueOf(value));
        jedis.close();
    }

    /**hash的hmset操作 */
    public void hmset(String key, Map<String, String> values) {
        Jedis jedis = jedisPool.getResource();
        jedis.hmset(key, values);
        jedis.close();
    }

    public void hset(String key, long id, String value) {
        hset(key, String.valueOf(id), value);
    }

    /**给hash的一个值+1，用于点击数、分享数等 */
    public void hincr(String key, long id) {
        hincr(key, String.valueOf(id));
    }
    /**给hash的一个值+1，用于点击数、分享数等 */
    public void hincr(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        jedis.hincrBy(key, field, 1);
        jedis.close();
    }

    /**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
    public void hincr(String key, long id, int expireTime) {
        hincr(key, String.valueOf(id), expireTime);
    }
    /**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
    public void hincr(String key, String field, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.hincrBy(key, field, 1);

        jedis.expire(key, expireTime);
        jedis.close();
    }

    /**给hash的一个值-1，用于取消订阅数等*/
    public void hdecr(String key, long id){
        Jedis jedis = jedisPool.getResource();
        jedis.hincrBy(key, String.valueOf(id), -1);
        jedis.close();
    }

    /**
     * 从Redis中取出一个值
     */
    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        String result = (jedis.hexists(key, field)) ? jedis.hget(key, field) : null;
        jedis.close();
        return result;

    }
    /**
     * field是整数时，从Redis中取出一个值
     */
    public String hget(String key, long id) {
        return hget(key, String.valueOf(id));
    }

    public Set<String> hkeys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = null;
        if (jedis.exists(key))
            result = jedis.hkeys(key);
        jedis.close();
        return result;
    }

    public boolean hexists(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        boolean result = exists(key) && jedis.hexists(key, field);
        jedis.close();
        return result;
    }

    /**
     * 清空一个值
     */
    public void hclear(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.hexists(key, field))
            jedis.hdel(key, field);
        jedis.close();
    }
    /**
     * 清空一个值
     */
    public void hclear(String key, long id) {
        hclear(key, String.valueOf(id));
    }
    public void hclear(String key, String[] field) {
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(key, field);
        jedis.close();
    }


    /**
     * 设置Key的值，指定过期时间
     */
    public void set(String key, String value, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.expire(key, expireTime);
        jedis.close();
    }

    /**
     * 设置Key的值，无过期时间
     */
    public void setTimeless(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    /**
     * 设置一个Key的过期时间
     */
    public void setTime(String key, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.expire(key, expireTime);
        jedis.close();
    }

    /**
     * 从Redis中取出一个值
     */
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = null;
        if (jedis.exists(key))
            result = jedis.get(key);
        jedis.close();
        return result;
    }
    /**
     * 从Redis中取出一个集合的所有成员
     */
    public Set<String> smembers(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.smembers(key);
        jedis.close();
        return result;
    }
    /**
     * 向Redis集合插入成员，整数
     */
    public void sadd(String key, long id) {
        sadd(key, String.valueOf(id));

    }
    /**
     * 去掉Redis集合的成员
     */
    public void srem(String key, String field) {
        Jedis jedis = jedisPool.getResource();

        jedis.srem(key, field);
        jedis.close();
    }
    /**
     * 向Redis集合插入成员
     */
    public void sadd(String key, String value) {
        Jedis jedis = jedisPool.getResource();

        jedis.sadd(key, value);
        jedis.close();
    }
    /**
     * 查询Redis集合里是否存在某值
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        boolean result = jedis.sismember(key, value);
        jedis.close();
        return result;

    }

    /**
     * 清空一个值
     */
    public void clear(String key) {
        Jedis jedis = jedisPool.getResource();
        if (jedis.exists(key))
            jedis.del(key);
        jedis.close();
    }

    /**
     * 清空列表keys，一般是多页
     */
    public void clearKeys(String key) {
        if (!key.endsWith(".")) key += ".";

        Jedis jedis = jedisPool.getResource();

        int start = 0;
        int count = 10;
        for (int i = 0; i < 20; i++) {
            String key1 = key + start;
            start += count;

            if (jedis.exists(key1))
                jedis.del(key1);
        }
        jedis.close();
    }
    /**
     * 清空列表keys，一般是多页
     */
    public void clearKeyPages(String key) {
        if (!key.endsWith(".")) key += ".";

        Jedis jedis = jedisPool.getResource();

        for (int i = 0; i < 20; i++) {
            String key1 = key + i;

            if (jedis.exists(key1))
                jedis.del(key1);
        }
        jedis.close();
    }
    /**
     * 清空长列表keys，一个列表按200
     */
    public void clearLongKeys(String key) {
        if (!key.endsWith(".")) key += ".";

        Jedis jedis = jedisPool.getResource();

        int start = 0;
        int count = 200;
        for (int i = 0; i < 5; i++) {
            String key1 = key + start;
            start += count;

            if (jedis.exists(key1))
                jedis.del(key1);
        }
        jedis.close();
    }

    /**
     * 判断一个key是否存在
     */
    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        boolean result =  jedis.exists(key);
        jedis.close();
        return result;
    }

    public void del(String docKey) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(docKey);
        jedis.close();
    }

    /**
     * 移出并获取列表的第一个元素
     */
    public String lpop(String key){
        Jedis jedis = jedisPool.getResource();
        String result =  jedis.lpop(key);
        jedis.close();
        return result;
    }
    /**
     * 向列表表尾插入数据，用于我的评论
     */
    public void rpush(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.rpush(key, value);
        jedis.close();
    }
    /**
     * 从列表表尾开始插入多条数据，有效期为1小时。
     * 用于稿件列表的一次性200条初始化。
     */
    public void resetLongList(String key, String... value) {
        clear(key);

        Jedis jedis = jedisPool.getResource();
        jedis.rpush(key, value);
        jedis.expire(key, hour1);
        jedis.close();
    }
    /**
     * 获取列表指定区间内的元素
     */
    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        List<String> result = jedis.lrange(key, start, end);
        jedis.close();
        return result;
    }
    /**
     * 移除列表中的指定数据
     */
    public void lrem(String key,  String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.lrem(key, 0, value);
        jedis.close();
    }

    /**
     * 获取list长度
     */
    public long llen(String key){
        Jedis jedis = jedisPool.getResource();
        long result = jedis.llen(key);
        jedis.close();
        return result;
    }


    //我的(评论,收藏)列表大小
    private final int COUNT_MY = 100;

    /** 我的评论,评论我的 增加一条。左边PUSH，左边TRIM，所以新数据会挤掉旧数据*/
    public void addMy(String key, String value){
        Jedis jedis = jedisPool.getResource();
        jedis.lpush(key, value);
        jedis.ltrim(key, 0, COUNT_MY);
        jedis.close();
        setTime(key,week1);
    }



    /** 延迟入库的任务数据，右边PUSH，左边TRIM，所以满了后新数据进不来 */
    public void addDelay(String key, String value, int count) {
        Jedis jedis = jedisPool.getResource();
        jedis.rpush(key, value);
        jedis.ltrim(key, 0, count);
        jedis.close();
    }

    /**
     * 获取过期时间
     *
     * @param key
     */
    @Override
    public long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        long result =  jedis.ttl(key);
        jedis.close();
        return  result;
    }

}
