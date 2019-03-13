package com.founder.xy.nis;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/** 黑名单工具类
 * Created by Wenkx on 2017/3/20.
 */
public  class BlackListHelper {
    private static String[] keys = {
		    	RedisKey.APP_NIS_SHUTUP_USER_KEY,
		    	RedisKey.APP_NIS_SHUTUP_IP_KEY,
		    	RedisKey.APP_NIS_WHITELIST_KEY
    	};
    private static String[] fields = {"shut_userID", "shut_user", "shut_userID"};

    /**
     * 按type初始化Redis中对应的黑/白名单集合
     *
     *
     * @param type 0禁言用户、1禁言IP、2白名单
     */
    public static void initSet(int type){
        if (!RedisManager.exists(keys[type])){
            String field = fields[type];
            Object[] params = new Object[]{type};

            DBSession conn = null;
            IResultSet rs = null;
            try {
                //翔宇启动时 LibHelper.getLib() 会报 BeanFactory not initialized or already closed错误，改成直接写死表名
                String sql = "select " + field + " from xy_nisshutup where shut_type =  ?" ;

                conn = Context.getDBSession();
                rs = conn.executeQuery(sql,params);
                while (rs.next()) {
                    if(!StringUtils.isBlank(rs.getString(field))) {
                        RedisManager.sadd(keys[type], rs.getString(field));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ResourceMgr.closeQuietly(rs);
                ResourceMgr.closeQuietly(conn);
            }
        }
    }

    /**
     *向指定的黑/白名单集合中添加成员
     *
     * @param type  0禁言用户、1禁言IP、2白名单
     * @param member 用户名/IP
     */
    public static void addSet(int type,String member){
        initSet(type);
        RedisManager.sadd(keys[type],member);
    }


    /** 删除 */
	public static void remove(int type, String value) {
	    RedisManager.srem(keys[type], value); 
	}

    /** 是否已存在 */
	public static boolean has(int type, String value) {
        initSet(type);
	    return RedisManager.sismember(keys[type], value); 
	}

	/**
     * 检查给定用户id/IP是否在黑/白名单中
     *
     * @param userID the user id
     * @param ip     the ip
     * @return 0 不在名单中、1黑名单、2白名单
     */
    public static int check(long userID ,String ip){
        if(RedisManager.sismember(keys[0], String.valueOf(userID)) 
        		|| RedisManager.sismember(keys[1],ip) 
        		|| RedisManager.sismember(keys[1] , ip.substring(0,ip.lastIndexOf(".") )+ "*"))
            return 1;
        else if(RedisManager.sismember(keys[2], String.valueOf(userID)))
            return 2;
        return 0;
    }
}
