package com.founder.xy.commons;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NewExternalSystemAuth {

    private static final int expireTime = 1;

    public static String extSystemAuth(int eid){
        String result = null;
        String esecret = RedisManager.hget(RedisKey.EXTERNAL_KEY,eid);
        if(StringUtils.isBlank(esecret)){
            DBSession conn = null;
            IResultSet rs = null;
            try{
                conn = Context.getDBSession();
                StringBuilder sql = new StringBuilder();
                //以后多租户要改
                sql.append("select te_externals from g_tenant where te_code = 'xy'");
                rs = conn.executeQuery(sql.toString(), new Object[]{});
                if(rs.next()){
                    JSONObject jsonObj = JSONObject.fromObject(rs.getString("te_externals"));
                    JSONArray array = jsonObj.getJSONArray("externals");
                    for(int i = 0;i<array.size();i++){
                        JSONObject jsonObject = array.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String key = jsonObject.getString("key");
                        RedisManager.hset(RedisKey.EXTERNAL_KEY, id, key);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                return "NewExternalSystemAuth.java的第三方授权处理接口报错了";
            } finally {
                ResourceMgr.closeQuietly(rs);
                ResourceMgr.closeQuietly(conn);
            }
        }
        return result;
    }

}
