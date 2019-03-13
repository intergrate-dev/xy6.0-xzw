package com.founder.xy.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wenkx on 2017/12/27.
 */
public interface RedisService {


    void hset(String key, String field, String value);

    void hset(String key, String field, long value);

    /**
     * hash的hmset操作
     */
    void hmset(String key, Map<String, String> values);

    void hset(String key, long id, String value);

    /**
     * 给hash的一个值+1，用于点击数、分享数等
     */
    void hincr(String key, long id);

    /**
     * 给hash的一个值+1，用于点击数、分享数等
     */
    void hincr(String key, String field);

    /**
     * 给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等
     */
    void hincr(String key, long id, int expireTime);

    /**
     * 给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等
     */
    void hincr(String key, String field, int expireTime);

    /**
     * 给hash的一个值-1，用于取消订阅数等
     */
    void hdecr(String key, long id);

    /**
     * 从Redis中取出一个值
     */
    String hget(String key, String field);

    /**
     * field是整数时，从Redis中取出一个值
     */
    String hget(String key, long id);

    Set<String> hkeys(String key);

    boolean hexists(String key, String field);

    /**
     * 清空一个值
     */
    void hclear(String key, String field);

    /**
     * 清空一个值
     */
    void hclear(String key, long id);

    void hclear(String key, String[] field);


    /**
     * 设置Key的值，指定过期时间
     */
    void set(String key, String value, int expireTime);

    /**
     * 设置Key的值，无过期时间
     */
    void setTimeless(String key, String value);

    /**
     * 设置一个Key的过期时间
     */
    void setTime(String key, int expireTime);

    /**
     * 从Redis中取出一个值
     */
    String get(String key);

    /**
     * 从Redis中取出一个集合的所有成员
     */
    Set<String> smembers(String key);

    /**
     * 向Redis集合插入成员，整数
     */
    void sadd(String key, long id);

    /**
     * 去掉Redis集合的成员
     */
    void srem(String key, String field);

    /**
     * 向Redis集合插入成员
     */
    void sadd(String key, String value);

    /**
     * 查询Redis集合里是否存在某值
     */
    boolean sismember(String key, String value);

    /**
     * 清空一个值
     */
    void clear(String key);

    /**
     * 清空列表keys，一般是多页
     */
    void clearKeys(String key);

    /**
     * 清空列表keys，一般是多页
     */
    void clearKeyPages(String key);

    /**
     * 清空长列表keys，一个列表按200
     */
    void clearLongKeys(String key);


    /**
     * 判断一个key是否存在
     */
    boolean exists(String key);

    void del(String docKey);

    /**
     * 移出并获取列表的第一个元素
     */
    String lpop(String key);

    /**
     * 向列表表尾插入数据，用于我的评论
     */
    void rpush(String key, String value);

    /**
     * 从列表表尾开始插入多条数据，有效期为1小时。
     * 用于稿件列表的一次性200条初始化。
     */
    void resetLongList(String key, String... value);

    /**
     * 获取列表指定区间内的元素
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 移除列表中的指定数据
     */
    void lrem(String key, String value);

    /**
     * 获取list长度
     */
    long llen(String key);


    /**
     * 我的评论,评论我的 增加一条。左边PUSH，左边TRIM，所以新数据会挤掉旧数据
     */
    void addMy(String key, String value);


    /**
     * 延迟入库的任务数据，右边PUSH，左边TRIM，所以满了后新数据进不来
     */
    void addDelay(String key, String value, int count);

    /**
     * 获取过期时间
     */
    long ttl(String key);
}
