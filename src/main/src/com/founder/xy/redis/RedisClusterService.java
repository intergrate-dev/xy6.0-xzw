package com.founder.xy.redis;

import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wenkx on 2017/12/28.
 */
public class RedisClusterService implements RedisService {

    private JedisCluster jedisCluster;

    private final int minute1 = 60; //设置Key的过期时间，1分钟
    private final int hour1 = minute1 * 60 ; //设置Key的过期时间，1小时
    private final int day1 = hour1 * 24; //设置Key的过期时间，1天
    private final int week1 = day1 * 7; //1周

    public final int hour2 = hour1 * 2 ; //2小时，用于按小时区分的点击数
    public final int hour25 = hour1 * 25 ; //25小时，用于按天区分的点击数

    public int THREE_DAYS = day1 * 3;



      RedisClusterService(JedisCluster jedisCluster){
        this.jedisCluster = jedisCluster;
    }



    public void hset(String key, String field, String value) {
        
        jedisCluster.hset(key, field, value);
    }

    public void hset(String key, String field, long value) {
        jedisCluster.hset(key, field, String.valueOf(value));
    }

    /**hash的hmset操作 */
    public void hmset(String key, Map<String, String> values) {
        jedisCluster.hmset(key, values);
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
        jedisCluster.hincrBy(key, field, 1);
    }

    /**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
    public void hincr(String key, long id, int expireTime) {
        hincr(key, String.valueOf(id), expireTime);
    }
    /**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
    public void hincr(String key, String field, int expireTime) {
        jedisCluster.hincrBy(key, field, 1);

        jedisCluster.expire(key, expireTime);
    }

    /**给hash的一个值-1，用于取消订阅数等*/
    public void hdecr(String key, long id){
        jedisCluster.hincrBy(key, String.valueOf(id), -1);
    }

    /**
     * 从Redis中取出一个值
     */
    public String hget(String key, String field) {

        if (jedisCluster.hexists(key, field))
            return jedisCluster.hget(key, field);
        else
            return null;
    }
    /**
     * field是整数时，从Redis中取出一个值
     */
    public String hget(String key, long id) {
        return hget(key, String.valueOf(id));
    }

    public Set<String> hkeys(String key) {
        if (!jedisCluster.exists(key)) return null;

        return jedisCluster.hkeys(key);
    }

    public boolean hexists(String key, String field) {
        return exists(key) && jedisCluster.hexists(key, field);
    }

    /**
     * 清空一个值
     */
    public void hclear(String key, String field) {
        if (jedisCluster.hexists(key, field))
            jedisCluster.hdel(key, field);
    }
    /**
     * 清空一个值
     */
    public void hclear(String key, long id) {
        hclear(key, String.valueOf(id));
    }
    public void hclear(String key, String[] field) {
        jedisCluster.hdel(key, field);
    }


    /**
     * 设置Key的值，指定过期时间
     */
    public void set(String key, String value, int expireTime) {
        jedisCluster.set(key, value);
        jedisCluster.expire(key, expireTime);
    }

    /**
     * 设置Key的值，无过期时间
     */
    public void setTimeless(String key, String value) {
        jedisCluster.set(key, value);
    }

    /**
     * 设置一个Key的过期时间
     */
    public void setTime(String key, int expireTime) {
        jedisCluster.expire(key, expireTime);
    }

    /**
     * 从Redis中取出一个值
     */
    public String get(String key) {
        if (jedisCluster.exists(key))
            return jedisCluster.get(key);
        else
            return null;
    }
    /**
     * 从Redis中取出一个集合的所有成员
     */
    public Set<String> smembers(String key) {

        return jedisCluster.smembers(key);
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

        jedisCluster.srem(key, field);
    }
    /**
     * 向Redis集合插入成员
     */
    public void sadd(String key, String value) {

        jedisCluster.sadd(key, value);
    }
    /**
     * 查询Redis集合里是否存在某值
     */
    public boolean sismember(String key, String value) {

        return jedisCluster.sismember(key, value);
    }

    /**
     * 清空一个值
     */
    public void clear(String key) {
        if (jedisCluster.exists(key))
            jedisCluster.del(key);
    }

    /**
     * 清空列表keys，一般是多页
     */
    public void clearKeys(String key) {
        if (!key.endsWith(".")) key += ".";
        int start = 0;
        int count = 10;
        for (int i = 0; i < 20; i++) {
            String key1 = key + start;
            start += count;

            if (jedisCluster.exists(key1))
                jedisCluster.del(key1);
        }
    }
    /**
     * 清空列表keys，一般是多页
     */
    public void clearKeyPages(String key) {
        if (!key.endsWith(".")) key += ".";

        for (int i = 0; i < 20; i++) {
            String key1 = key + i;

            if (jedisCluster.exists(key1))
                jedisCluster.del(key1);
        }
    }
    /**
     * 清空长列表keys，一个列表按200
     */
    public void clearLongKeys(String key) {
        if (!key.endsWith(".")) key += ".";

        int start = 0;
        int count = 200;
        for (int i = 0; i < 5; i++) {
            String key1 = key + start;
            start += count;

            if (jedisCluster.exists(key1))
                jedisCluster.del(key1);
        }
    }

    /**
     * 判断一个key是否存在
     */
    public boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    public void del(String docKey) {
        jedisCluster.del(docKey);
    }

    /**
     * 移出并获取列表的第一个元素
     */
    public String lpop(String key){
        return jedisCluster.lpop(key);
    }
    /**
     * 向列表表尾插入数据，用于我的评论
     */
    public void rpush(String key, String value) {
        jedisCluster.rpush(key, value);
    }
    /**
     * 从列表表尾开始插入多条数据，有效期为1小时。
     * 用于稿件列表的一次性200条初始化。
     */
    public void resetLongList(String key, String... value) {
        clear(key);

        jedisCluster.rpush(key, value);
        jedisCluster.expire(key, hour1);
    }
    /**
     * 获取列表指定区间内的元素
     */
    public List<String> lrange(String key, long start, long end) {
        return jedisCluster.lrange(key, start, end);
    }
    /**
     * 移除列表中的指定数据
     */
    public void lrem(String key,  String value) {
        jedisCluster.lrem(key, 0, value);
    }

    /**
     * 获取list长度
     */
    public long llen(String key){
        return jedisCluster.llen(key);
    }

    //我的(评论,收藏)列表大小
    private final int COUNT_MY = 100;

    /** 我的评论,评论我的 增加一条。左边PUSH，左边TRIM，所以新数据会挤掉旧数据*/
    public void addMy(String key, String value){
        jedisCluster.lpush(key, value);
        jedisCluster.ltrim(key, 0, COUNT_MY);

        setTime(key,week1);
    }

    /** 延迟入库的任务数据，右边PUSH，左边TRIM，所以满了后新数据进不来 */
    public void addDelay(String key, String value, int count) {
        jedisCluster.rpush(key, value);
        jedisCluster.ltrim(key, 0, count);
    }

    /**
     * 获取过期时间
     *
     * @param key
     */
    @Override
    public long ttl(String key) {
        return jedisCluster.ttl(key);
    }

}
