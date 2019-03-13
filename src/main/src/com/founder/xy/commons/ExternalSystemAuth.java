package com.founder.xy.commons;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yu.feng on 2017/9/4.
 */
public class ExternalSystemAuth {

    private static final int expireTime = 1;

    public static String extSystemAuth(int eid, long time, String sign, String data){

        String result = null;

        //根据eid得到esecret
        String esecret = RedisManager.hget(RedisKey.EXTERNAL_KEY,eid);

        if(esecret == null){

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

                    if(array.size() == 0){
                        result = "非法系统";
                        return result;
                    } else {
                        boolean hasID = false;
                        for(int i = 0;i<array.size();i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            int id = jsonObject.getInt("id");
//							String name = jsonObject.getString("name");
//							String key = jsonObject.getString("key");
//							RedisManager.hset(RedisKey.EXTERNAL_KEY, id, key);
                            if(id == eid){
                                hasID = true;
                                esecret = jsonObject.getString("key");
                                break;
                            }
                        }

                        if(hasID){
                            //有ID，写入Redis
                            for(int i = 0;i<array.size();i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                int id = jsonObject.getInt("id");
//								String name = jsonObject.getString("name");
                                String key = jsonObject.getString("key");
                                RedisManager.hset(RedisKey.EXTERNAL_KEY, id, key);
                            }
                        }else{
                            result = "非法系统";
                            return result;
                        }
                    }
                }else{
                    result = "非法系统";
                    return result;
                }
            }catch(Exception e){
                e.printStackTrace();
                result = "系统验证出错";
                return result;
            } finally {
                ResourceMgr.closeQuietly(rs);
                ResourceMgr.closeQuietly(conn);
            }

        }

        long timeInterval = expireTime * 1000 * 30;
        long currentTime = System.currentTimeMillis();
        if((currentTime-time-timeInterval) > 0){
            result = "请求超时，可能发生盗链";
            return result;
        }

        StringBuffer tmpSign = new StringBuffer();
        if(data == null){
            data = "";
        }
        tmpSign.append("eid=").append(eid).append("&time=").append(time).append("&data=").append(data).append("&esecret=").append(esecret);

        System.out.println("tmpSign---->"+tmpSign.toString());

        String currentSign = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(tmpSign.toString().getBytes("UTF-8"));
            currentSign = toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("currentSign---->"+currentSign);

        if(currentSign == null || !currentSign.equals(sign)){
            result = "身份认证失败";
            return result;
        }

        return result;
    }

    private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
        return sb.toString();
    }
}
