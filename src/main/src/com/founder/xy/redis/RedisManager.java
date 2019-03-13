package com.founder.xy.redis;

import com.founder.e5.context.Context;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisManager {
	
	public static RedisService redisService = null;
	public static final int minute1 = 60; //设置Key的过期时间，1分钟
	public static final int hour1 = minute1 * 60 ; //设置Key的过期时间，1小时
	public static final int day1 = hour1 * 24; //设置Key的过期时间，1天
	public static final int week1 = day1 * 7; //1周

	public static final int hour2 = hour1 * 2 ; //2小时，用于按小时区分的点击数
	public static final int hour25 = hour1 * 25 ; //25小时，用于按天区分的点击数

	public static int THREE_DAYS = day1 * 3;

	public static RedisService FindRedisService(){
		try {
			JedisCluster jedisCluster = (JedisCluster) Context.getBean("jedisCluster");
			return new RedisClusterService(jedisCluster);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("没找到redis集群配置，尝试使用单节点");
			JedisPool jedisPool = (JedisPool) Context.getBean("jedisPool");
			return new RedisPoolService(jedisPool);
		}
	}
	public static RedisService getRedisService() {
		if(redisService == null){
			redisService = FindRedisService();
		}
		return  redisService;
	}
	
	/**
	 * 多站点时的Key命名。
	 * 在siteID>1时，Redis中的key需要带站点ID。
	 * 注意，不会因多站点而混淆的Key不需要加站点ID
	 * @param key
	 * @param siteID
	 * @return
	 */
	public static String getKeyBySite(String key, int siteID) {
		if (siteID <= 1) return key;
		
		boolean endsWithDot = key.endsWith(".");
		
		if (!endsWithDot) key += ".";
		key += "site" + siteID;
		
		if (endsWithDot) key += ".";
		
		return key;
	}
	
	/**
	 * 多站点时取分类的key,部分分类按站点区分,部分分类不按站点区分
	 * @param siteID 站点ID
	 * @param code 分类码
	 * @return 分类的key
	 */
	public static String getCatKeyBySite(int siteID,String code) {
		if(code.equals("PHOTO") || code.equals("VIDEO") || code.equals("SPECIAL")
				|| code.equals("SOURCE") || code.equals("TEMPLATE") || code.equals("BLOCK")
				|| code.equals("RESOURCE") || code.equals("EXTFIELD") || code.equals("VOTE")
				|| code.equals("DISCUSSTYPE") || code.equals("QA") || code.equals("ARTICLETRADE")){
			return getKeyBySite(RedisKey.APP_CATS_KEY,siteID) + code;
		}
		return RedisKey.APP_CATS_KEY + code ;
	}
	
	public static void hset(String key, String field, String value) {
		RedisService redisService = getRedisService();
		redisService.hset(key, field, value);
	}

	public static void hset(String key, String field, long value) {
		RedisService redisService = getRedisService();
		redisService.hset(key, field, value);
	}

	/**hash的hmset操作 */
	public static void hmset(String key, Map<String, String> values) {
		RedisService redisService = getRedisService();
		redisService.hmset(key, values);
	}

	public static void hset(String key, long id, String value) {
		hset(key, String.valueOf(id), value);
	}

	/**给hash的一个值+1，用于点击数、分享数等 */
	public static void hincr(String key, long id) {
		hincr(key, String.valueOf(id));
	}
	/**给hash的一个值+1，用于点击数、分享数等 */
	public static void hincr(String key, String field) {
		RedisService redisService = getRedisService();
		redisService.hincr(key, field);
	}

	/**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
	public static void hincr(String key, long id, int expireTime) {
		hincr(key, String.valueOf(id), expireTime);
	}
	/**给hash的一个值+1，并重置过期时间，用于按小时/天计数的稿件点击数/评论数等 */
	public static void hincr(String key, String field, int expireTime) {
		RedisService redisService = getRedisService();
		redisService.hincr(key, field, expireTime);
	}

	/**给hash的一个值-1，用于取消订阅数等*/
	public static void hdecr(String key, long id){
		RedisService redisService = getRedisService();
		redisService.hdecr(key, id);
	}

	/**
	 * 从Redis中取出一个值
	 */
	public static String hget(String key, String field) {
		RedisService redisService = getRedisService();
		return redisService.hget(key,field);
	}
	/**
	 * field是整数时，从Redis中取出一个值
	 */
	public static String hget(String key, long id) {
		return hget(key, String.valueOf(id));
	}

	public static Set<String> hkeys(String key) {
		RedisService redisService = getRedisService();
		return redisService.hkeys(key);
	}

    public static boolean hexists(String key, String field) {
		RedisService redisService = getRedisService();
		return redisService.hexists(key,field);
    }

    /**
	 * 清空一个值
	 */
	public static void hclear(String key, String field) {
		RedisService redisService = getRedisService();
		redisService.hclear(key, field);
	}
	/**
	 * 清空一个值
	 */
	public static void hclear(String key, long id) {
		hclear(key, String.valueOf(id));
	}
	public static void hclear(String key, String[] field) {
		RedisService redisService = getRedisService();
		redisService.hclear(key, field);
	}

	/**
	 * 设置Key的值，默认保存1小时
	 */
	public static void set(String key, String value) {
		set(key, value, hour1);
	}

	/**
	 * 设置Key的值，保存比较长的时间，1天
	 */
	public static void setLonger(String key, String value) {
		set(key, value, day1);
	}

	/**
	 * 设置Key的值，保存1周
	 */
	public static void setWeekly(String key, String value) {
		set(key, value, week1);
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
		RedisService redisService = getRedisService();
		redisService.set(key, value,expireTime);
	}

	/**
	 * 设置Key的值，无过期时间
	 */
	public static void setTimeless(String key, String value) {
		RedisService redisService = getRedisService();
		redisService.setTimeless(key, value);
	}

	/**
	 * 设置一个Key的过期时间
	 */
	public static void setTime(String key, int expireTime) {
		RedisService redisService = getRedisService();
		redisService.setTime(key, expireTime);
	}

	/**
	 * 从Redis中取出一个值
	 */
	public static String get(String key) {
		RedisService redisService = getRedisService();
		return redisService.get(key);
	}
	/**
	 * 从Redis中取出一个集合的所有成员
	 */
	public static Set<String> smembers(String key) {
		RedisService redisService = getRedisService();

		return redisService.smembers(key);
	}
	/**
	 * 向Redis集合插入成员，整数
	 */
	public static void sadd(String key, long id) {
		sadd(key, String.valueOf(id));
	}
	/**
	 * 去掉Redis集合的成员
	 */
	public static void srem(String key, String field) {
		RedisService redisService = getRedisService();

		redisService.srem(key, field);
	}
	/**
	 * 向Redis集合插入成员
	 */
	public static void sadd(String key, String value) {
		RedisService redisService = getRedisService();

		redisService.sadd(key, value);
	}
	/**
	 * 查询Redis集合里是否存在某值
	 */
	public static boolean sismember(String key, String value) {
		RedisService redisService = getRedisService();

		return redisService.sismember(key, value);
	}

	/**
	 * 清空一个值
	 */
	public static void clear(String key) {
		RedisService redisService = getRedisService();
		redisService.del(key);
	}

	/**
	 * 清空列表keys，一般是多页
	 */
	public static void clearKeys(String key) {
		RedisService redisService = getRedisService();

		redisService.clearKeys(key);

	}
	/**
	 * 清空列表keys，一般是多页
	 */
	public static void clearKeyPages(String key) {

		RedisService redisService = getRedisService();
		redisService.clearKeyPages(key);
	}
	/**
	 * 清空长列表keys，一个列表按200
	 */
	public static void clearLongKeys(String key) {

		RedisService redisService = getRedisService();

		redisService.clearLongKeys(key);
	}

	/**
	 * 判断一个key是否存在
	 */
	public static boolean exists(String key) {
		RedisService redisService = getRedisService();
		return redisService.exists(key);
	}

	public static void del(String docKey) {
		RedisService redisService = getRedisService();
		redisService.del(docKey);
	}

	/**
	 * 移出并获取列表的第一个元素
	 */
	public static String lpop(String key){
		RedisService redisService = getRedisService();
		return redisService.lpop(key);
	}
	/**
	 * 向列表表尾插入数据，用于我的评论
	 */
	public static void rpush(String key, String value) {
		RedisService redisService = getRedisService();
		redisService.rpush(key, value);
	}
	/**
	 * 从列表表尾开始插入多条数据，有效期为1小时。
	 * 用于稿件列表的一次性200条初始化。
	 */
	public static void resetLongList(String key, String... value) {
		RedisService redisService = getRedisService();
		redisService.resetLongList(key, value);
	}
	/**
	 * 获取列表指定区间内的元素
	 */
	public static List<String> lrange(String key, long start, long end) {
		RedisService redisService = getRedisService();
		return redisService.lrange(key, start, end);
	}
	/**
	 * 移除列表中的指定数据
	 */
	public static void lrem(String key,  String value) {
		RedisService redisService = getRedisService();
		redisService.lrem(key, value);
	}

	/**
	 * 获取list长度
	 */
	public static long llen(String key){
		RedisService redisService = getRedisService();
		return redisService.llen(key);
	}

	private static final int COUNT_DELAY_DISCUSS = 2000;
	private static final int COUNT_DELAY_SUBJECTQA = 200;
	private static final int COUNT_DELAY_QA = 200;
	private static final int COUNT_DELAY_FAVORITE = 1000;
	private static final int COUNT_DELAY_EXPOSE = 100;
	private static final int COUNT_DELAY_ENTRY = 100;
	//我的(评论,收藏)列表大小

	/** 我的评论,评论我的 增加一条。左边PUSH，左边TRIM，所以新数据会挤掉旧数据*/
	public static void addMy(String key, String value){
		RedisService redisService = getRedisService();
		redisService.addMy(key, value);
	}

	/** 延迟入库的评论：增加一个新评论 */
	public static void addDelayDiscuss(String value) {
		addDelay(RedisKey.APP_DELAY_DISCUSS_KEY, value, COUNT_DELAY_DISCUSS);
	}
	/** 延迟入库的话题（问吧）：增加一个新提问 */
	public static void addDelaySubjectQA(String value) {
		addDelay(RedisKey.APP_DELAY_SUBJECTQA_KEY, value, COUNT_DELAY_SUBJECTQA);
	}
	/** 延迟入库的问答（问政）：增加一个新提问 */
	public static void addDelayQA(String value) {
		addDelay(RedisKey.APP_DELAY_QA_KEY, value, COUNT_DELAY_QA);
	}

	/** 延迟入库的收藏 */
	public static void addDelayFavorite(String value) {
		addDelay(RedisKey.APP_DELAY_FAVORITE_KEY, value, COUNT_DELAY_FAVORITE);
	}
	/** 延迟入库的举报 */
	public static void addDelayExpose(String value) {
		addDelay(RedisKey.APP_DELAY_EXPOSE_KEY, value, COUNT_DELAY_EXPOSE);
	}
	/** 延迟入库的活动报名 */
	public static void addDelayEntry(String value) {
		addDelay(RedisKey.APP_DELAY_ENTRY_KEY, value, COUNT_DELAY_ENTRY);
	}

	/** 延迟入库的任务数据，右边PUSH，左边TRIM，所以满了后新数据进不来 */
	private static void addDelay(String key, String value, int count) {
		RedisService redisService = getRedisService();
		redisService.addDelay(key, value,count);
	}

	public static long ttl(String key) {
		RedisService redisService = getRedisService();
		return redisService.ttl(key);
	}
}
