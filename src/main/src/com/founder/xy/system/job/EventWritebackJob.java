package com.founder.xy.system.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.scheduler.BaseJob;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 互动事件计数回写任务
 * 把缓存中记录的点击数、点赞数、分享数等回写到数据库中，清理redis
 * @author Gong Lijie
 */
public class EventWritebackJob extends BaseJob{
	private static String SQL_HOUR_DELETE = "delete from xy_statHour where st_hour=?";
	private static String SQL_HOUR_INSERT = "insert into xy_statHour(st_hour,st_id,st_siteID,st_countClick,st_countDiscuss,st_countShare,"
			+ "st_webCol,st_webCol0,st_webCol1,st_webCol2,st_webCol3,st_webCol4,"
			+ "st_appCol,st_appCol0,st_appCol1,st_appCol2,st_appCol3,st_appCol4) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static String SQL_HOUR_UPDATE = "update xy_statHour set st_countDiscuss=st_countDiscuss+? "
			+ "where st_id=? and st_hour=?";
	private static String SQL_DAY_DELETE = "delete from xy_stat where st_date<?";
	private static String SQL_TODAY_DELETE = "delete from xy_stat where st_date=?";
	private static String SQL_DAY_INSERT = "insert into xy_stat(st_date,st_id,st_siteID,st_countClick,st_countDiscuss,st_countShare,"
			+ "st_webCol,st_webCol0,st_webCol1,st_webCol2,st_webCol3,st_webCol4,"
			+ "st_appCol,st_appCol0,st_appCol1,st_appCol2,st_appCol3,st_appCol4) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static String SQL_HOUR_DELETE_COLUMN = "delete from xy_statColHour where st_hour=?";
	private static String SQL_HOUR_INSERT_COLUMN = "insert into xy_statColHour"
			+ "(st_hour,st_id,st_siteID,st_channel,st_countClick,st_countSub) values(?,?,?,?,?,?)";
	private static String SQL_DAY_DELETE_COLUMN = "delete from xy_statCol where st_date<?";
	private static String SQL_TODAY_DELETE_COLUMN = "delete from xy_statCol where st_date=?";
	private static String SQL_DAY_INSERT_COLUMN = "insert into xy_statCol"
			+ "(st_date,st_id,st_siteID,st_channel,st_countClick,st_countSub) values(?,?,?,?,?,?)";

	private static int lastHour=99;

	public EventWritebackJob() {
		super();
		log = Context.getLog("xy.eventWriteback");
	}

	@Override
	protected void execute() throws E5Exception {
		log.info("---开始互动事件计数回写任务---");
		
		defaultSite();
		
		log.info("本轮回写任务完成");
	}
	
	//系统默认租户的回写。以后可扩展为多租户的场景
	private void defaultSite() {
		writebackHourDay();

		writebackArticle();//稿件
		writebackLive();//直播
		writebackPaperArticle();//数字报
		writebackActivity();//活动
		writebackDiscuss();//评论
		writebackSubjectQA();//互动问答
		writebackQA();//话题问答
		
		writebackTrade();//行业分类回写
		
		writebackColumn();//栏目订阅回写
		writebackSubject();//问吧话题关注数回写
	}

	//把稿件的点击数、点赞数、分享数回写到数据库表里。稿件可能是web和app同时发，所以更新两个表
	private void writebackArticle() {
		List<DocLib> articleLibs = LibHelper.getLibs( DocTypes.ARTICLE.typeID(), Tenant.DEFAULTCODE );

		String segSql = " set a_countClick=?,a_countClick0=?,a_countClick1=?,a_countClick2=?,"
					+ "a_countShare=?,a_countShare0=?,a_countShare1=?,a_countShare2=?,"
					+ "a_countDiscuss=?,a_countDiscuss0=?,a_countDiscuss1=?,a_countDiscuss2=?,"
					+ "a_countShareClick=?,a_countPraise=? "
					+ " where SYS_DOCUMENTID=?";

		Long[] params = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		String key = RedisKey.NIS_EVENT_ARTICLE;
		try {
			Set<String> fs = RedisManager.smembers(RedisKey.NIS_EVENT_INDEX_ARTICLES);
			for (String id : fs) {
				//清理掉set中的id
				RedisManager.srem(RedisKey.NIS_EVENT_INDEX_ARTICLES, id);
				
				String idKey = key + id;
				if (!RedisManager.exists(idKey)) continue;
				
				readParamCount(idKey, "c", params, 0);
				readParamCount(idKey, "c0", params, 1);
				readParamCount(idKey, "c1", params, 2);
				readParamCount(idKey, "c2", params, 3);
				readParamCount(idKey, "s", params, 4);
				readParamCount(idKey, "s0", params, 5);
				readParamCount(idKey, "s1", params, 6);
				readParamCount(idKey, "s2", params, 7);
				readParamCount(idKey, "d", params, 8);
				readParamCount(idKey, "d0", params, 9);
				readParamCount(idKey, "d1", params, 10);
				readParamCount(idKey, "d2", params, 11);
				readParamCount(idKey, "sc", params, 12);
				readParamCount(idKey, "p", params, 13);
				params[14] = Long.parseLong(id);
				
				//回写数据库
				for (DocLib docLib : articleLibs) {
					String sql = "update " + docLib.getDocLibTable() + segSql;
					InfoHelper.executeUpdate(docLib.getDocLibID(), sql, params);
				}
				
				//稿件及时回写redis
				changeCountInRedis(params);
			}
		} catch (Exception e) {
			log.error("稿件回写出错：" + e.getLocalizedMessage(), e);
		}
	}
	

	private void writebackHourDay() {
		//先写小时计数表
		Calendar ca = Calendar.getInstance();
		int hour = ca.get(Calendar.HOUR_OF_DAY);
		boolean isZero = hour == 0; //是否零点
		
		hour = (hour == 0) ? 23 : hour - 1; //取当前小时的上一小时，作为本次任务的hour
		writebackArticleHour(hour);//稿件的点击数、分享数、评论数
		writebackColumnHour(hour);//栏目的点击数和订阅数
		
		//评论可能因为审批而延后写入Redis，这部分也要回写。检查前12个小时的数据
		for (int i = 0; i < 12; i++) {
			hour--;
			if (hour < 0) hour = 23;
			
			writebackArticleHourDelay(hour);
		}
		
		//若当前是零点，则写稿件的天计数表（上一天的总计数）
		if (isZero) {
			writebackArticleDay();
			writebackColumnDay();
		}
		
		//互动计数回写时间（long型），在回写任务中填写，在统计界面显示
		RedisManager.setTimeless(RedisKey.NIS_EVENT_WRITEBACK_TIME, String.valueOf(ca.getTimeInMillis()));
	}

	/**
	 * 把上一小时的稿件计数写入库
	 * 回写任务的触发器应该设置为一个小时执行一次，在整点过很短的时刻启动，则可以保证及时把上一小时的数据写入库。
	 	1）	取当前小时的上一小时，作为本次任务的hour
		2）	清空小时表里指定hour的所有记录
		3）	查找redis里的小时hash，遍历取出所有的稿件id和点击计数；然后按稿件id找到discuss评论计数。
		4）	按稿件id查Redis中的稿件栏目ID，若没有则读稿件库查出，写入Redis并重新设置其周期。
		5）	把稿件id、点击数、评论数、栏目id添加到小时表。insert语句。
		6）	点击数和评论数的redis key不需要清理，生命周期比较短，会自动清理。
	 */
	private void writebackArticleHour(int hour) {
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		int colLibID = LibHelper.getColumnLibID();
		Object[] params = new Object[18];
		try {
			//清空小时表里指定hour的所有记录
			InfoHelper.executeUpdate(SQL_HOUR_DELETE, new Object[]{hour});
			
			//查找redis里的小时hash
			Set<String> fs = RedisManager.hkeys(RedisKey.NIS_EVENT_ARTICLE_CLICK_HOUR + hour);
			if (fs != null) {
				params[0] = hour;
				for (String id : fs) {
					prepareParams(params, hour, colReader, colLibID, id, 
							RedisKey.NIS_EVENT_ARTICLE_CLICK_HOUR, 
							RedisKey.NIS_EVENT_ARTICLE_DISCUSS_HOUR,
							RedisKey.NIS_EVENT_ARTICLE_SHARE_HOUR);
					InfoHelper.executeUpdate(SQL_HOUR_INSERT, params);
				}

				//目前互动回写服务不是一个小时执行一次，导致回写bug
				//如果上一次清空评论缓存的小时数和这一次不同，则清空前一个小时的评论数缓存
				if (lastHour != hour){
                    //评论可能因审批延后，所以回写时需要检查更早的hour。及时清理掉当前hour
				    RedisManager.clear(RedisKey.NIS_EVENT_ARTICLE_DISCUSS_HOUR + lastHour);
                }
                lastHour = hour;
			}
		} catch (Exception e) {
			log.error("稿件回写上一小时计数时出错：" + hour, e);
		}
	}
	//补充由于审批而耽误的旧评论
	private void writebackArticleHourDelay(int hour) {
		Object[] params = new Object[]{0, 0, 0};
		try {
			//SQL = "update xy_statHour set st_countDiscuss=st_countDiscuss+? where st_id=? and st_hour=?";
			//查找redis里的小时hash
			String key = RedisKey.NIS_EVENT_ARTICLE_DISCUSS_HOUR + hour;
			Set<String> fs = RedisManager.hkeys(key);
			if (fs != null) {
				params[2] = hour;
				for (String id : fs) {
					params[0] = getInt(RedisManager.hget(key, id));
					params[1] = getInt(id);
					
					InfoHelper.executeUpdate(SQL_HOUR_UPDATE, params);
				}
				//评论可能因审批延后，所以回写时需要检查更早的hour。及时清理掉当前hour的Key
				RedisManager.clear(key);
			}
		} catch (Exception e) {
			log.error("稿件回写小时计数时出错：" + hour, e);
		}
	}
	
	/**
	 * 把上一天的稿件计数写入库
	 */
	private void writebackArticleDay() {
		Calendar ca = Calendar.getInstance();
		clearTime(ca); //清除时分秒
		
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		int colLibID = LibHelper.getColumnLibID();
		Object[] params = new Object[18];
		try {
			ca.add(Calendar.DAY_OF_MONTH, -1);//上一天
			int day = ca.get(Calendar.DATE);
			Date theDate = ca.getTime();
			
			//清空两个月前的记录，表里不保留太多天前的数据
			ca.add(Calendar.MONTH, -2);
			InfoHelper.executeUpdate(SQL_DAY_DELETE, new Object[]{ca.getTime()});
			
			//清除今天的数据，防止因为回写间隔太短导重复统计
			InfoHelper.executeUpdate(SQL_TODAY_DELETE, new Object[]{theDate});
			
			//查找redis里的hash
			Set<String> fs = RedisManager.hkeys(RedisKey.NIS_EVENT_ARTICLE_CLICK_DAY + day);
			if (fs != null) {
				params[0] = theDate;
				for (String id : fs) {
					prepareParams(params, day, colReader, colLibID, id, 
							RedisKey.NIS_EVENT_ARTICLE_CLICK_DAY, 
							RedisKey.NIS_EVENT_ARTICLE_DISCUSS_DAY,
							RedisKey.NIS_EVENT_ARTICLE_SHARE_DAY);
					InfoHelper.executeUpdate(SQL_DAY_INSERT, params);
				}
			}
		} catch (Exception e) {
			log.error("稿件回写上一天计数时出错：" + e.getLocalizedMessage(), e);
		}
	}

	private void writebackColumnHour(int hour) {
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		int colLibID = LibHelper.getColumnLibID();
		Object[] params = new Object[6];
		try {
			//清空小时表里指定hour的所有记录
			InfoHelper.executeUpdate(SQL_HOUR_DELETE_COLUMN, new Object[]{hour});
			
			//查找redis里的小时hash
			Set<String> fs = RedisManager.hkeys(RedisKey.NIS_EVENT_COLUMN_CLICK_HOUR + hour);
			Set<String> subfs = RedisManager.hkeys(RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_HOUR+hour);
			if(subfs!=null){
				fs .addAll(subfs);
			}
			if (fs != null) {
				params[0] = hour;
				for (String id : fs) {
					prepareParamsColumn(params, hour, colReader, colLibID, id, 
							RedisKey.NIS_EVENT_COLUMN_CLICK_HOUR, 
							RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_HOUR);
					InfoHelper.executeUpdate(SQL_HOUR_INSERT_COLUMN, params);
				}
			}
		} catch (Exception e) {
			log.error("栏目回写上一小时计数时出错：" + hour, e);
		}
	}

	/**
	 * 把上一天的栏目计数写入库
	 */
	private void writebackColumnDay() {
		Calendar ca = Calendar.getInstance();
		clearTime(ca); //清除时分秒
		
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		int colLibID = LibHelper.getColumnLibID();
		Object[] params = new Object[6];
		try {
			ca.add(Calendar.DAY_OF_MONTH, -1);//上一天
			int day = ca.get(Calendar.DATE);
			Date theDate = ca.getTime();
			
			//清空两个月前的记录，表里不保留太多天前的数据
			ca.add(Calendar.MONTH, -2);
			InfoHelper.executeUpdate(SQL_DAY_DELETE_COLUMN, new Object[]{ca.getTime()});
			
			//删除同一个时间的重复统计（若任务启动间隔<1小时，可能会同一个时刻重复入库）
			InfoHelper.executeUpdate(SQL_TODAY_DELETE_COLUMN, new Object[]{theDate});
			
			//查找redis里的hash
			Set<String> fs = RedisManager.hkeys(RedisKey.NIS_EVENT_COLUMN_CLICK_DAY + day);
			Set<String> subfs = RedisManager.hkeys(RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_DAY + day);
			if(subfs!=null){
				fs .addAll(subfs);
			}
			if (fs != null) {
				params[0] = theDate;
				for (String id : fs) {
					prepareParamsColumn(params, day, colReader, colLibID, id, 
							RedisKey.NIS_EVENT_COLUMN_CLICK_DAY, 
							RedisKey.NIS_EVENT_COLUMN_SUBSCRIBE_DAY);
					InfoHelper.executeUpdate(SQL_DAY_INSERT_COLUMN, params);
				}
			}
		} catch (Exception e) {
			log.error("栏目回写上一天计数时出错：" + e.getLocalizedMessage(), e);
		}
	}
	
	//直播回写到数据库表里
	private void writebackLive() {
		countMoreWriteback("LIVE", RedisKey.NIS_EVENT_INDEX_LIVES, RedisKey.NIS_EVENT_LIVE);
	}

	private void writebackPaperArticle() {
		countMoreWriteback("PAPERARTICLE", RedisKey.NIS_EVENT_INDEX_PAPERARTICLE, RedisKey.NIS_EVENT_PAPERARTICLE);
	}

	private void writebackActivity() {
		countMoreWriteback("ACTIVITY", RedisKey.NIS_EVENT_INDEX_ACTIVITY, RedisKey.NIS_EVENT_ACTIVITY);
	}

	private void writebackDiscuss() {
		countWriteback("DISCUSS", RedisKey.NIS_EVENT_INDEX_DISCUSS, RedisKey.NIS_EVENT_DISCUSS);
	}

	private void writebackSubjectQA() {
		countWriteback("SUBJECTQA", RedisKey.NIS_EVENT_INDEX_SUBJECTQA, RedisKey.NIS_EVENT_SUBJECTQA);
	}
	
	private void writebackQA() {
		countWriteback("QA", RedisKey.NIS_EVENT_INDEX_QA, RedisKey.NIS_EVENT_QA);
	}
	
	//栏目订阅回写
	private void writebackColumn() {
		countOneWriteback("COLUMN", RedisKey.NIS_EVENT_SUBSCRIBE_COLUMN, "col_rssCount");
	}
	
	//问吧话题关注数回写
	private void writebackSubject() {
		countOneWriteback("SUBJECT", RedisKey.NIS_EVENT_SUBCRIBE_SUBJECT, "a_countFollow");
	}

	/**
	 * 稿件行业分类点击量回写
	 */
	private void writebackTrade(){
		String sql = "UPDATE category_other SET  ENTRY_PUB_LEVEL = ? WHERE ENTRY_ID = ?";
		String key = RedisKey.NIS_EVENT_CLICK_TRADE;
		Long[] params = {0L, 0L};
		try {
			Set<String> fs = RedisManager.hkeys(key);
			if (fs != null)
				for (String id : fs) {
					String count = RedisManager.hget(key, id);
					if(count!=null) {
						//回写数据库
						params[0] = Long.parseLong(count);
						params[1] = Long.parseLong(id);
						InfoHelper.executeUpdate(sql, params);
	
						//清除field。唯一的清理redis之处。若不清理，则redis中会越积越多
						RedisManager.hclear(key, id);
					}
				}
			//删除redis中的分类缓存
			RedisManager.clear(RedisKey.APP_CATS_KEY+"ARTICLETRADE.*");
		} catch (Exception e) {
			log.error("回写出错（key：" + key + ",sql:" + sql + "）" + e.getLocalizedMessage(), e);
		}
	}

	//把点击数、点赞数、分享数、评论数、分享点击数回写到数据库表里
	private void countMoreWriteback(String docTypeCode, String indexKey, String key) {
		DocTypes oneType = Enum.valueOf(DocTypes.class, docTypeCode);
		DocLib docLib = LibHelper.getLib(oneType.typeID());
		
		String sql = "update " + docLib.getDocLibTable()
				+ " set a_countClick=?,a_countShare=?,a_countDiscuss=?,a_countShareClick=?,a_countPraise=? where SYS_DOCUMENTID=?";
	
		Long[] params = {0L, 0L, 0L, 0L, 0L, 0L};
		try {
			Set<String> fs = RedisManager.smembers(indexKey);
			if (fs != null) {
				for (String id : fs) {
					RedisManager.srem(indexKey, id);//清理掉set中的id
							
					String idKey = key + id;
					readParamCount(idKey, "c", params, 0);
					readParamCount(idKey, "s", params, 1);
					readParamCount(idKey, "d", params, 2);
					readParamCount(idKey, "sc", params, 3);
					readParamCount(idKey, "p", params, 4);
					params[5] = Long.parseLong(id);
					
					//回写数据库
					InfoHelper.executeUpdate(docLib.getDocLibID(), sql, params);
				}
			}
		} catch (Exception e) {
			log.error("回写出错：" + docTypeCode + "," + e.getLocalizedMessage(), e);
		}
	}

	//把点赞数、评论数回写到数据库表里
	private void countWriteback(String docTypeCode, String indexKey, String key) {
		DocTypes oneType = Enum.valueOf(DocTypes.class, docTypeCode);
		DocLib docLib = LibHelper.getLib(oneType.typeID());
		
		String sql = "update " + docLib.getDocLibTable()
				+ " set a_countDiscuss=?,a_countPraise=? where SYS_DOCUMENTID=?";
	
		Long[] params = {0L, 0L, 0L};
		try {
			Set<String> fs = RedisManager.smembers(indexKey);
			if (fs != null) {
				for (String id : fs) {
					RedisManager.srem(indexKey, id);//清理掉set中的id
							
					String idKey = key + id;
					readParamCount(idKey, "d", params, 0);
					readParamCount(idKey, "p", params, 1);
					params[2] = Long.parseLong(id);
					
					//回写数据库
					InfoHelper.executeUpdate(docLib.getDocLibID(), sql, params);
				}
			}
		} catch (Exception e) {
			log.error("回写出错：" + docTypeCode + "," + e.getLocalizedMessage(), e);
		}
	}

	/*
	 * 订阅回写：包括栏目订阅数、问吧话题关注数
	 */
	private void countOneWriteback(String docTypeCode, String key, String field) {
		DocTypes oneType = Enum.valueOf(DocTypes.class, docTypeCode);
		DocLib docLib = LibHelper.getLib(oneType.typeID());
		
		String sql = "update " + docLib.getDocLibTable()
				+ " set " + field + "=? where SYS_DOCUMENTID=?";

		Long[] params = {0L, 0L};
		try {
			Set<String> fs = RedisManager.hkeys(key);
			if (fs != null)
				for (String id : fs) {
					String count = RedisManager.hget(key, id);

					//回写数据库
					params[0] = Long.parseLong(count);
					params[1] = Long.parseLong(id);
					InfoHelper.executeUpdate(docLib.getDocLibID(), sql, params);

					//清除field
					//RedisManager.hclear(key, id);
				}
		} catch (Exception e) {
			log.error("回写出错（key：" + key + ",sql:" + sql + "）" + e.getLocalizedMessage(), e);
		}
	}
	
	private void prepareParams(Object[] params, int hour,
			ColumnReader colReader, int colLibID, String id, String clickKey, String discussKey, String shareKey) {
		// SQL_HOUR_INSERT = "insert into xy_statHour(st_hour,st_id,st_siteID,st_countClick,st_countDiscuss,"
		// 		+ "st_webCol,st_webCol0,st_webCol1,st_webCol2,st_webCol3,st_webCol4,"
		// 		+ "st_appCol,st_appCol0,st_appCol1,st_appCol2,st_appCol3,st_appCol4) "
		// 		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
		//重置参数。第0位是hour/date
		for (int i = 1; i < params.length; i++) {
			params[i] = 0;
		}
		
		String strCountClick = RedisManager.hget(clickKey + hour, id);
		String strCountDiscuss = RedisManager.hget(discussKey + hour, id);
		String strCountShare = RedisManager.hget(shareKey + hour, id);
	
		// 按稿件id查Redis中的稿件栏目ID
		String webColID = RedisManager.hget(RedisKey.NIS_EVENT_ARTICLE + id, "webCol");
		String appColID = RedisManager.hget(RedisKey.NIS_EVENT_ARTICLE + id, "appCol");
	
		params[1] = getInt(id);
		params[2] = getInt(RedisManager.hget(RedisKey.NIS_EVENT_ARTICLE + id, "site"));
		params[3] = getInt(strCountClick);
		params[4] = getInt(strCountDiscuss);
		params[5] = getInt(strCountShare);
	
		//WEB栏目ID，以及多级ID
		params[6] = getInt(webColID);
		int[] columnIDs = getColumns(colReader, colLibID, webColID);
		if (columnIDs != null) {
			for (int i = 0; i < columnIDs.length && i < 5; i++) {
				params[7 + i] = columnIDs[i];
			}
		}
	
		//APP栏目ID，以及多级ID
		params[12] = getInt(appColID);
		columnIDs = getColumns(colReader, colLibID, appColID);
		if (columnIDs != null) {
			for (int i = 0; i < columnIDs.length && i < 5; i++) {
				params[13 + i] = columnIDs[i];
			}
		}
	}

	private void prepareParamsColumn(Object[] params, int hour,
			ColumnReader colReader, int colLibID, String id, String clickKey, String subKey) {
		// SQL_HOUR_INSERT = "insert into xy_statColHour"
		//		+ "(st_hour,st_id,st_siteID,st_channel,st_countClick,st_countSub) values(?,?,?,?,?,?)";

		//重置参数。第0位是hour/date
		for (int i = 1; i < params.length; i++) {
			params[i] = 0;
		}
		
		params[1] = getInt(id);
		//读栏目得到站点ID、栏目渠道
		try {
			Column col = colReader.get(colLibID, getInt(id));
			if (col != null) {
				params[2] = col.getSiteID();
				params[3] = col.getChannel();
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		// 点击计数、订阅计数
		String strCountClick = RedisManager.hget(clickKey + hour, id);
		String strCountSub = RedisManager.hget(subKey + hour, id);
		params[4] = getInt(strCountClick);
		params[5] = getInt(strCountSub);
	}

	private void clearTime(Calendar ca) {
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.clear(Calendar.MINUTE);
		ca.clear(Calendar.SECOND);
		ca.clear(Calendar.MILLISECOND);
	}

	private int getInt(String value) {
		if (value == null) return 0;
		return Integer.parseInt(value);
	}

	private int[] getColumns(ColumnReader colReader, int colLibID, String colID) {
		if (colID == null) return null;
		
		int id = Integer.parseInt(colID);
		if (id <= 0) return null;
		
		try {
			Column col = colReader.get(colLibID, id);
			if (col == null) return null;
			
			return StringUtils.getIntArray(col.getCasIDs(), "~");
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void readParamCount(String key, String field, Long[] params, int index) {
		String count = RedisManager.hget(key, field);
		if (count != null) {
			params[index] = Long.parseLong(count);
		}
	}
	
	/**
	 * 回写redis里稿件列表中的数值，使及时更新
	 */
	private void changeCountInRedis(Long[] params) {
		try {
			String key = RedisKey.APP_ARTICLELIST_ONE_KEY + params[14];
			String article = RedisManager.get(key);
			if (article == null) return;
			
			JSONObject inJson = JSONObject.fromObject(article);
			//点击数、分享点击数：需要计算总点击数
			long countClickInitial = JsonHelper.getLong(inJson, "countClickInitial");
			long countClickAll0 = JsonHelper.getInt(inJson, "countClick");
			long countClickAll = countClickInitial + params[0] + params[12];
			if (countClickAll > countClickAll0)
				inJson.put("countClick", countClickAll); //所有点击数
			inJson.put("countClickReal", params[0]); //实际点击数
			inJson.put("countShareClick", params[12]);//分享点击数
			inJson.put("countShare", params[4]);
			inJson.put("countDiscuss", params[8]);
			inJson.put("countPraise", params[13]);
			
			RedisManager.setLonger(key, inJson.toString());
		} catch (Exception e) {
			System.out.println("" + e.getLocalizedMessage());
		}
	}
}
