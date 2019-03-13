package com.founder.xy.nis;

import java.util.Calendar;
import java.util.Date;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONObject;

/**
 * 集中了各种事件提交时对Redis里的计数的处理逻辑
 * @author Gong Lijie
 */
public class EventCountHelper {
	private static String[] eventFields = {"c", "p", "s", "sc", "d"}; //点击、点赞、分享、分享点击。最后一个是评论，方便引用
	
	/**
	 * 提交稿件点击事件、点赞事件、分享事件、分享页点击事件
	 * 
	 * 事件类型eventType：0——点击；1——点赞；2——分享；3——分享页点击
	 */
	public static boolean addArticleCount(long articleID, int eventType, int channel) {
		addArticleCount(articleID, channel, eventFields[eventType], (eventType == 0 || eventType == 2), null);
		return true;
	}
	
	/**
	 * 增加Redis里的稿件评论数
	 */
	public static void addArticleCountDiscuss(long articleID, int channel, Date discussTime) {
		addArticleCount(articleID, channel, "d", true, discussTime);
	}

	/**
	 * 提交直播点击事件、点赞事件、分享事件、分享页点击事件
	 * 事件类型eventType：0——点击；1——点赞；2——分享；3——分享页点击
	 */
	public static boolean addLiveCount(long articleID, int eventType, int channel) {
		addCountInRedis(RedisKey.NIS_EVENT_LIVE, RedisKey.NIS_EVENT_INDEX_LIVES, 
				DocTypes.LIVE.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里的直播评论数
	 */
	public static void addLiveCountDiscuss(long articleID, int channel) {
		addLiveCount(articleID, 4, channel);
	}
	
	/**
	 * 提交评论事件（点赞事件）
	 */
	public static boolean addDiscussCount(long articleID, int eventType) {
		addCountInRedis(RedisKey.NIS_EVENT_DISCUSS, RedisKey.NIS_EVENT_INDEX_DISCUSS, 
				DocTypes.DISCUSS.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里评论的评论数
	 */
	public static void addDiscussCountDiscuss(long articleID) {
		addDiscussCount(articleID, 4);
	}
	
	/**
	 * 提交数字报稿件事件
	 */
	public static boolean addPaperArticleCount(long articleID, int eventType) {
		addCountInRedis(RedisKey.NIS_EVENT_PAPERARTICLE, RedisKey.NIS_EVENT_INDEX_PAPERARTICLE, 
				DocTypes.PAPERARTICLE.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里数字报稿件的评论数
	 */
	public static void addPaperArticleCountDiscuss(long articleID) {
		addPaperArticleCount(articleID, 4);
	}
	
	/**
	 * 提交活动事件
	 */
	public static boolean addActivityCount(long articleID, int eventType) {
		addCountInRedis(RedisKey.NIS_EVENT_ACTIVITY, RedisKey.NIS_EVENT_INDEX_ACTIVITY, 
				DocTypes.ACTIVITY.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里活动的评论数
	 */
	public static void addActivityCountDiscuss(long articleID) {
		addActivityCount(articleID, 4);
	}
	
	/**
	 * 提交互动话题问答事件
	 */
	public static boolean addSubjectQACount(long articleID, int eventType) {
		addCountInRedis(RedisKey.NIS_EVENT_SUBJECTQA, RedisKey.NIS_EVENT_INDEX_SUBJECTQA, 
				DocTypes.SUBJECTQA.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里互动话题问答的评论数
	 */
	public static void addSubjectQACountDiscuss(long articleID) {
		addSubjectQACount(articleID, 4);
	}
	
	/**
	 * 提交问答事件
	 */
	public static boolean addQACount(long articleID, int eventType) {
		addCountInRedis(RedisKey.NIS_EVENT_QA, RedisKey.NIS_EVENT_INDEX_QA, 
				DocTypes.QA.typeID(), articleID, eventFields[eventType]);
		return true;
	}
	
	/**
	 * 增加Redis里问答的评论数
	 */
	public static void addQACountDiscuss(long articleID) {
		addQACount(articleID, 4);
	}
	
	/**
	 * 提交栏目点击事件，目前只做按小时/天的计数，不永久保存所以不存栏目表
	 */
	public static boolean addColumnCount(long articleID, int eventType) {
		//--------增加按小时/天计数----------
		Calendar ca = Calendar.getInstance();
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		int day = ca.get(Calendar.DATE);
		RedisManager.hincr(RedisKey.NIS_EVENT_COLUMN_CLICK_HOUR + hour, articleID, RedisManager.hour2);
		RedisManager.hincr(RedisKey.NIS_EVENT_COLUMN_CLICK_DAY + day, articleID, RedisManager.hour25);
		return true;
	}
	
	/**
	 * 从Redis里读一个稿件的特定事件数
	 */
	public static String getArticleCount(long articleID, String field) {
		String key = RedisKey.NIS_EVENT_ARTICLE + articleID;
		
		if (!RedisManager.exists(key)) {
			//若Redis里没有，则可能是过期了或者Redis重启了，再取一次
			setArticleCount(articleID);
		}
		
		String count = RedisManager.hget(key, field);
		if (count == null) count = "0";
		
		return count;
	}
	
	/** 从Redis里读一个直播的特定事件数 */
	public static String getLiveCount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_LIVE, DocTypes.LIVE.typeID(), articleID, field);
	}

	/** 从Redis里读一个评论的特定事件数 */
	public static String getDiscussCount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_DISCUSS, DocTypes.DISCUSS.typeID(), articleID, field);
	}

	/** 从Redis里读一个数字报稿件的特定事件数 */
	public static String getPaperArticleCount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_PAPERARTICLE, DocTypes.PAPERARTICLE.typeID(), articleID, field);
	}

	/** 从Redis里读一个活动的特定事件数 */
	public static String getActivityCount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_ACTIVITY, DocTypes.ACTIVITY.typeID(), articleID, field);
	}

	/** 从Redis里读一个互动话题问答的特定事件数 */
	public static String getSubjectQACount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_SUBJECTQA, DocTypes.SUBJECTQA.typeID(), articleID, field);
	}

	/** 从Redis里读一个问答的特定事件数 */
	public static String getQACount(long articleID, String field) {
		return getCountInRedis(RedisKey.NIS_EVENT_QA, DocTypes.QA.typeID(), articleID, field);
	}
	
	private static void addArticleCount(long articleID, int channel, String field, 
			boolean useChannel, Date discussTime) {
		String key = RedisKey.NIS_EVENT_ARTICLE + articleID;
		
		if (!RedisManager.exists(key)) {
			//若Redis里没有，则可能是过期了或者Redis重启了，再取一次
			setArticleCount(articleID);
		}
		
		if (useChannel) {
			RedisManager.hincr(key, field + channel); //点击事件，记录渠道点击数
		}
		RedisManager.hincr(key, field, RedisManager.day1); //原子操作+1
		
		//最后把稿件ID写入set
		RedisManager.sadd(RedisKey.NIS_EVENT_INDEX_ARTICLES, articleID);
		
		//--------对于点击/评论/分享，增加按小时/天计数----------
		if ("c".equals(field)) {
			Calendar ca = Calendar.getInstance();
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int day = ca.get(Calendar.DATE);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_CLICK_HOUR + hour, articleID, RedisManager.hour2);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_CLICK_DAY + day, articleID, RedisManager.hour25);
		} else if ("d".equals(field)) {
			//评论可能有审批，要计的是评论入库时间而不是审批通过时间。注意若超过24小时才审批，会造成数据不准确，不管
			Calendar ca = Calendar.getInstance();
			if (discussTime != null) ca.setTime(discussTime);
			
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int day = ca.get(Calendar.DATE);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_DISCUSS_HOUR + hour, articleID, RedisManager.hour2);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_DISCUSS_DAY + day, articleID, RedisManager.hour25);
		} else if ("s".equals(field)) {
			Calendar ca = Calendar.getInstance();
			int hour = ca.get(Calendar.HOUR_OF_DAY);
			int day = ca.get(Calendar.DATE);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_SHARE_HOUR + hour, articleID, RedisManager.hour2);
			RedisManager.hincr(RedisKey.NIS_EVENT_ARTICLE_SHARE_DAY + day, articleID, RedisManager.hour25);
		}
	}

	//当Redis里没有稿件事件计数的key，就读稿件库取出来
	private static void setArticleCount(long id) {
		//先在Redis里添加这个Key（为暂时应付后续的提交事件，使不重复查库）
		String key = RedisKey.NIS_EVENT_ARTICLE + id;
		RedisManager.hset(key, "c", 1); //加一个field，redis里就有了key
		
		//取稿件，可能在web库，也可能在app库。顺便取出稿件所在栏目
		Document doc = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int webColumnID = 0, appColumnID = 0;
		try {
			DocLib[] articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID());
			doc = docManager.get(articleLibs[0].getDocLibID(), id);
			if (doc != null) {
				webColumnID = doc.getInt("a_columnID");
			}
			Document doc2 = docManager.get(articleLibs[1].getDocLibID(), id);
			if (doc2 != null) {
				appColumnID = doc2.getInt("a_columnID");
				doc = doc2;
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		if (doc == null) return;
		
		//写入web栏目和app栏目的ID
		RedisManager.hset(key, "webCol", webColumnID);
		RedisManager.hset(key, "appCol", appColumnID);
		//站点ID，用于小时点击数记录
		RedisManager.hset(key, "site", doc.getInt("a_siteID"));
		//读数据库得到各数值
		RedisManager.hset(key, "c", readCount(doc, "a_countClick"));
		RedisManager.hset(key, "c0", readCount(doc, "a_countClick0"));
		RedisManager.hset(key, "c1", readCount(doc, "a_countClick1"));
		RedisManager.hset(key, "c2", readCount(doc, "a_countClick2"));
		
		RedisManager.hset(key, "s", readCount(doc, "a_countShare"));
		RedisManager.hset(key, "s0", readCount(doc, "a_countShare0"));
		RedisManager.hset(key, "s1", readCount(doc, "a_countShare1"));
		RedisManager.hset(key, "s2", readCount(doc, "a_countShare2"));
		
		RedisManager.hset(key, "d", readCount(doc, "a_countDiscuss"));
		RedisManager.hset(key, "d0", readCount(doc, "a_countDiscuss0"));
		RedisManager.hset(key, "d1", readCount(doc, "a_countDiscuss1"));
		RedisManager.hset(key, "d2", readCount(doc, "a_countDiscuss2"));
		
		RedisManager.hset(key, "sc", readCount(doc, "a_countShareClick"));
		RedisManager.hset(key, "p", readCount(doc, "a_countPraise"));
	}

	/**
	 * 从Redis里读一个数据的特定事件数
	 */
	private static String getCountInRedis(String eventKey, int docTypeID, long articleID, String field) {
		String key = eventKey + articleID;
		
		if (!RedisManager.exists(key)) {
			//若Redis里没有，则可能是过期了或者Redis重启了，再取一次
			initRedisData(eventKey, docTypeID, articleID);
		}
		
		String count = RedisManager.hget(key, field);
		if (count == null) count = "0";
		
		return count;
	}

	private static void addCountInRedis(String eventKey, String indexKey, int docTypeID, long articleID, String field) {
		String key = eventKey + articleID;
		
		if (!RedisManager.exists(key)) {
			//若Redis里没有，则可能是过期了或者Redis重启了，再取一次
			initRedisData(eventKey, docTypeID, articleID);
		}
		
		RedisManager.hincr(key, field, RedisManager.day1); //原子操作+1
		
		//最后把ID写入set
		RedisManager.sadd(indexKey, articleID);
	}

	//初始化Redis中的互动事件计数
	private static void initRedisData(String eventKey, int docTypeID, long id) {
		//先在Redis里添加这个Key（为暂时应付后续的提交事件，使不重复查库）
		String key = eventKey + id;
		RedisManager.hset(key, "c", 1); //加一个field，redis里就有了key
		
		//读数据库
		Document doc = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			int docLibID = LibHelper.getLibID(docTypeID, Tenant.DEFAULTCODE);
			doc = docManager.get(docLibID, id);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		if (doc == null) return;
		
		//把数据库的各数值写入Redis
		RedisManager.hset(key, "c", readCount(doc, "a_countClick"));
		RedisManager.hset(key, "s", readCount(doc, "a_countShare"));
		RedisManager.hset(key, "d", readCount(doc, "a_countDiscuss"));
		RedisManager.hset(key, "sc", readCount(doc, "a_countShareClick"));
		RedisManager.hset(key, "p", readCount(doc, "a_countPraise"));
	}

	//处理点击数等数值<0的情况，改为0
	private static int readCount(Document doc, String field) {
		int count = doc.getInt(field);
		if (count < 0) count = 0;
		return count;
	}

	/**
	 * 提交行业分类点击事件
	 * 事件类型eventType：目前只有0——点击
	 */
	public static boolean eventTrade(JSONObject obj) throws E5Exception {
		long id = obj.getLong("id");
		int eventType = JsonHelper.getInt(obj, "eventType"); // 事件类型
		addTradeCount(id, eventType);
		return true;
	}

	/**
	 * 行业分类互动数据
	 * @param id 分类ID
	 * @param eventType  目前只有0 点击数
	 */
	public static void addTradeCount(long id, int eventType) {
		String key = RedisKey.NIS_EVENT_CLICK_TRADE;
		if (!RedisManager.exists(key)) {
			//若Redis里没有，则可能是过期了或者Redis重启了，再取一次
			setTradeCount(id);
		}
		RedisManager.hincr(key, id); //原子操作+1

	}

	private static void setTradeCount(long id) {
		String key =  RedisKey.NIS_EVENT_CLICK_TRADE;
		try {
			CatReader catReader = (CatReader) Context.getBean(CatManager.class);
			Category category = catReader.getCat( CatTypes.CAT_ARTICLETRADE.typeID(),(int)id);
			RedisManager.hset(key, String.valueOf(id),category.getPubLevel());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}
}
