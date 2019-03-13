/*package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.amuc.commons.EncodeUtils;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.RedisManager;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import com.founder.amuc.commons.HTTPHelper;

import static com.founder.amuc.commons.RedisKey.AMUC_TOKEN_USER;
import static com.founder.amuc.commons.RedisKey.AMUC_USER_TOKEN;

*//**
 * 会员中心安全入口
 * Created by codingnuts on 2017/3/22.
 *//*
@Controller
@RequestMapping("/api/auth")
public class AuthenApiAdapter {
    *//**
     * 登录
     *//*
    @RequestMapping(value = "login.do")
    public void login(HttpServletResponse response, String data) throws E5Exception {
        String returnData = "{}";
        LoginUser user;
        try {
            JSONObject json = JSONObject.fromObject(data);
            String userStr = json.getString("user");
            String password = json.getString("pwd");
            SSO sso = (SSO) Context.getBeanByID("ssoReader");
            //判断登录是否成功 0：成功
            int verifyResult = sso.verifyUserPassword(userStr, password);
            if (verifyResult == 0) {
                UserReader userReader = (UserReader) Context.getBean(UserReader.class);
                User currentUser = userReader.getUserByCode(userStr);
                user = new LoginUser(currentUser);
            } else {
                user = new LoginUser();
            }

            returnData = getSecretReturnData(user);
        } catch (E5Exception e) {
            e.printStackTrace();
        }
        outputJson(String.valueOf(returnData), response);
    }

    @RequestMapping(value = "logout.do")
    public void logout(HttpServletResponse response, String data) {
        JSONObject json = HTTPHelper.checkValid(data);
        if (json.getInt("code") == 0) {
            //清理redis
            try {
                JSONObject dataJson = JSONObject.fromObject(data);
                String userID = dataJson.getString("userID");
                String token = null;
                if (RedisManager.hexists(AMUC_USER_TOKEN, userID)) {
                    token = RedisManager.hget(AMUC_USER_TOKEN, userID);
                    RedisManager.hclear(AMUC_USER_TOKEN, userID);
                }

                if (!StringUtils.isBlank(token) && RedisManager.hexists(AMUC_TOKEN_USER, token)) {
                    RedisManager.hclear(AMUC_TOKEN_USER, token);
                }
            } catch (Exception e) {
                json.put("code", 1);
                json.put("error", "清除redis时出错!");
                e.printStackTrace();
            }
        }
        outputJson(String.valueOf(json), response);
    }

    *//**
     * 获得加密后的结果
     *
     * @param user 系统用户对象
     * @return 返回加密后的结果
     *//*
    private String getSecretReturnData(LoginUser user) {
        String returnData, api_data, secretKey;
        JSONObject json = new JSONObject();
        //如果token冲突，设置userId=0；
        try {
            createToken(user);
        } catch (Exception e) {
            user.setUserId(0);
            e.printStackTrace();
        }
        //获得返回数据 - 未加密
        returnData = getReturnData(user);
        //获得密钥字符串
        api_data = EncodeUtils.encodeBase64(user.getTime() + "" + user.getTime() + "" + user.getTime());
        //用当前时间的base64编码后的19位作为secretKey
        secretKey = api_data.substring(40, 45) + api_data.substring(5, 21) + api_data.substring(33, 36);
        //获得加密后的返回数据
        returnData = EncodeUtils.encodeBase64(EncodeUtils.encrypt(secretKey, returnData));
        //returnData = EncodeUtils.decrypt(secretKey, EncodeUtils.decodeBase64(returnData));

        //放到json数据中，传回给外网api
        json.put("api-version", "5.1");
        json.put("api-data", api_data);
        json.put("secretReturnData", returnData);
        return json.toString();
    }

    //未加密的返回数据
    private String getReturnData(LoginUser user) {
        JSONObject json = new JSONObject();
        json.put("userID", user.getUserId());
        json.put("userName", user.getUserName());
        json.put("time", user.getTime());
        json.put("token", user.getToken());
        return json.toString();
    }

    //1. 生成token；2.检查hash中是否存在；若存在重复试10次；仍冲突则设置userId = 0： 表明登录失败
    private void createToken(LoginUser user) throws Exception {
        String token;
        if (user.getUserId() == 0) {
            user.setToken(EncodeUtils.getMD5(user.getUserId() + "_" + user.getUserCode() + "_" + user.getTime()));
            return;
        }
        for (int i = 0; i < 10; i++) {
            token = EncodeUtils.getMD5(user.getUserId() + "_" + user.getUserCode() + "_" + user.getTime());
            if (!RedisManager.hexists(AMUC_TOKEN_USER, token)) {
                user.setToken(token);
                //先清除原来的key是token的值，id是可以覆盖的不需要清除
                if (RedisManager.hexists(AMUC_USER_TOKEN, user.getUserId() + "")) {
                    String _token = RedisManager.hget(AMUC_USER_TOKEN, user.getUserId() + "");
                    RedisManager.hclear(AMUC_TOKEN_USER, _token);
                }
                RedisManager.hset(AMUC_TOKEN_USER, token, user.getUserId() + "");
                RedisManager.hset(AMUC_USER_TOKEN, user.getUserId() + "", token);
                break;
            } else {
                user.setTime(user.getTime() + 10);
            }
            if (i == 9) {
                throw new Exception("token被占用 - user:" + user.toString());
            }
        }
    }
    
    *//** 向response输出json数据 *//*
    public static void outputJson(String result, HttpServletResponse response) {
      if (result == null) return;
      
      response.setContentType("application/json; charset=UTF-8");

      PrintWriter out = null;
      try {
        out = response.getWriter();
        out.write(result);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        ResourceMgr.closeQuietly(out);
      }
    }
}

class LoginUser {
    private long userId;
    private String userCode;
    private String userName;
    private long time;

    private String token;

    public LoginUser() {
        this.userId = 0;
        this.userCode = "";
        this.userName = "";
        this.time = System.currentTimeMillis();
    }


    *//**
     * 登陆时用的
     *
     * @param user
     *//*
    public LoginUser(User user) {
        this.userId = user.getUserID();
        this.userCode = user.getUserCode();
        this.userName = user.getUserName();
        this.time = System.currentTimeMillis();
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginUser loginUser = (LoginUser) o;

        return userId == loginUser.userId;

    }

    @Override
    public int hashCode() {
        return (int) (userId ^ (userId >>> 32));
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "userId=" + userId +
                ", userCode='" + userCode + '\'' +
                ", userName='" + userName + '\'' +
                ", time=" + time +
                ", token='" + token + '\'' +
                '}';
    }
}
*/