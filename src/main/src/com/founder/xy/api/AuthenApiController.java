package com.founder.xy.api;

import static com.founder.xy.commons.web.HTTPHelper.checkValid;
import static com.founder.xy.redis.RedisKey.APP_TOKEN_USER;
import static com.founder.xy.redis.RedisKey.APP_USER_TOKEN;

import java.io.UnsupportedEncodingException;
import java.security.Permission;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocType;
import com.founder.e5.permission.PermissionHelper;
import com.founder.xy.config.SubTab;
import com.founder.xy.config.Tab;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.workspace.MainHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.EncodeUtils;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.api.newmobile.OriginalApiManager;

/**
 * 直播登录
 */
@Controller
@RequestMapping("/api/app")
public class AuthenApiController {
	@Autowired
	private SiteUserReader siteUserReader;
	@Autowired
	private SiteUserManager siteUserManager;
	@Autowired
    OriginalApiManager originalApiManager;

    private static final String[] APP_TABS_ID = {"cweb","capp","crevoke","myaudit","nislive"};//"resv",视频一期app端不实现，暂时不返给app
    /**
     * 登录
     */
    @RequestMapping(value = "login.do")
    public void login(HttpServletRequest request, HttpServletResponse response, String data) throws E5Exception {
        SSO sso = (SSO) Context.getBeanByID("ssoReader");
        
        JSONObject returnData = new JSONObject();
        LoginUser user = null;
        try {
            JSONObject json = JSONObject.fromObject(data);
            String userStr = json.getString("user");
            String password = json.getString("pwd");
            
            //判断密码是否正确。0：正确
            int result = sso.verifyUserPassword(userStr, password);
            if (result == 0) {
                UserReader userReader = (UserReader) Context.getBean(UserReader.class);
                User currentUser = userReader.getUserByCode(userStr);

				int siteID = 0;
				Role[] roles = null;

				String tCode = currentUser.getProperty1();//扩展字段1里是租户代号
				if (StringUtils.isBlank(tCode)) tCode = Tenant.DEFAULTCODE;
				int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);
				
				//检查用户有效期
				int userID = currentUser.getUserID();
				if (!validDate(userLibID, userID)) {
					result = 3; //过期用户
				} else {
					//取站点、角色
					SiteUserReader siteUserReader = (SiteUserReader)Context.getBean("siteUserReader");
					List<Site> sites = siteUserReader.getSites(userLibID, userID);
					if (sites.size() > 0) {
						siteID = sites.get(0).getId();
						roles = getRolesBySite(userLibID, userID, siteID);
					}
					if (roles == null || roles.length == 0) {
						result = 4; //无可用角色
					} else {
						int roleID = roles[0].getRoleID();
//						int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
						
						//登录，得到loginID
						String[] rets = sso.login(userStr, roleID, 
								request.getRemoteAddr(), request.getServerName(), true);
						int loginID = Integer.parseInt(rets[0]);
						if (loginID > 0) {
			                user = new LoginUser(currentUser, loginID);

                            JSONArray siteArr = new JSONArray();
			                for(Site site : sites){

                              /*  String nodeAudiByUserID = originalApiManager.getNodeAudiByUserID(userID, site.getId(), tCode);
                                DocumentManager docManager = DocumentManagerFactory.getInstance();
                                Document[] documents = docManager.find(DocTypes.ORIGINAL.typeID(), "SYS_CURRENTNODE IN(?) AND a_status not in (0,5,4)", new Object[]{nodeAudiByUserID});
                                if(documents==null || (documents!=null && documents.length==0)){
                                    result = 6;
                                    break;
                                }*/

                                roles = getRolesBySite(userLibID, userID, site.getId());

                                if(roles == null || roles.length == 0){
                                    continue;
                                }else{
                                    JSONObject siteJson = new JSONObject();
                                    siteJson.put("siteID",site.getId());
                                    siteJson.put("siteName",site.getName());

                                    int roleId = roles[0].getRoleID();
						            int newRoleId = (roles.length == 1) ? roleId : PermissionHelper.mergeRoles(roles);

                                    //读有权限的Tab
                                    List<Tab> tabs = MainHelper.getRoleTabs(newRoleId);
                                    if(tabs == null){
                                        siteJson.put("tabs","[]");
                                    }else{
                                        JSONArray stArray = new JSONArray();
                                        for(String appTabId:APP_TABS_ID){
                                            SubTab st = MainHelper.getSubTab(tabs,appTabId);
                                            if(st != null){
                                                JSONObject stjson = new JSONObject();
                                                stjson.put("value",st.getId());
                                                stjson.put("name",st.getName());
                                                stArray.add(stjson);
                                            }
                                        }
                                        siteJson.put("tabs",stArray);
                                    }
                                    siteArr.add(siteJson);
                                }
                            }
                            if(result != 5){
                                returnData.put("sites",siteArr);
                            }
						} else {
							result = 5; //登录失败
						}
					}
				}
            } else {
            	result = 2; //用户名密码错误
            }
            
            if (user != null) {
                returnData.put("code", "0");
            } else {
                user = new LoginUser();
                returnData.put("code", String.valueOf(result));
                returnData.put("error", "not exist!");
            }
            if(result == 6){
                returnData.put("code", String.valueOf(result));
            }
            //不管登录成功与否都需要加密 - 统一处理
            getSecretReturnData(user, returnData);
        } catch (Exception e) {
            e.printStackTrace();
            returnData.put("code", "1");
            returnData.put("error", e.getLocalizedMessage());
        }

        InfoHelper.outputText(returnData.toString(), response);
    }

    @RequestMapping(value = "userIsExist.do")
    public void userIsExist(HttpServletRequest request, HttpServletResponse response, String data) throws E5Exception {
        SSO sso = (SSO) Context.getBeanByID("ssoReader");

        JSONObject returnData = new JSONObject();
        LoginUser user = null;
        try {
            JSONObject json = JSONObject.fromObject(data);
            String userStr = json.getString("user");

            UserReader userReader = (UserReader) Context.getBean(UserReader.class);
            User currentUser = userReader.getUserByCode(userStr);

            int result = 0;
            //判断用户是否存在
            if (currentUser != null) {

                int siteID = 0;
                Role[] roles = null;

                String tCode = currentUser.getProperty1();//扩展字段1里是租户代号
                if (StringUtils.isBlank(tCode)) tCode = Tenant.DEFAULTCODE;
                int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);

                //检查用户有效期
                int userID = currentUser.getUserID();
                if (!validDate(userLibID, userID)) {
                    result = 3; //过期用户
                } else {
                    //取站点、角色
                    SiteUserReader siteUserReader = (SiteUserReader)Context.getBean("siteUserReader");
                    List<Site> sites = siteUserReader.getSites(userLibID, userID);
                    if (sites.size() > 0) {
                        siteID = sites.get(0).getId();
                        roles = getRolesBySite(userLibID, userID, siteID);
                    }
                    if (roles == null || roles.length == 0) {
                        result = 4; //无可用角色
                    } else {
                        int roleID = roles[0].getRoleID();
//						int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);

                        //登录，得到loginID
                        String[] rets = sso.login(userStr, roleID,
                                request.getRemoteAddr(), request.getServerName(), true);
                        int loginID = Integer.parseInt(rets[0]);
                        if (loginID > 0) {
                            user = new LoginUser(currentUser, loginID);

                            JSONArray siteArr = new JSONArray();
                            for(Site site : sites){

                                roles = getRolesBySite(userLibID, userID, site.getId());

                                if(roles == null || roles.length == 0){
                                    continue;
                                }else{
                                    JSONObject siteJson = new JSONObject();
                                    siteJson.put("siteID",site.getId());
                                    siteJson.put("siteName",site.getName());

                                    int roleId = roles[0].getRoleID();
                                    int newRoleId = (roles.length == 1) ? roleId : PermissionHelper.mergeRoles(roles);

                                    //读有权限的Tab
                                    List<Tab> tabs = MainHelper.getRoleTabs(newRoleId);
                                    if(tabs == null){
                                        siteJson.put("tabs","[]");
                                    }else{
                                        JSONArray stArray = new JSONArray();
                                        for(String appTabId:APP_TABS_ID){
                                            SubTab st = MainHelper.getSubTab(tabs,appTabId);
                                            if(st != null){
                                                JSONObject stjson = new JSONObject();
                                                stjson.put("value",st.getId());
                                                stjson.put("name",st.getName());
                                                stArray.add(stjson);
                                            }
                                        }
                                        siteJson.put("tabs",stArray);
                                    }
                                    siteArr.add(siteJson);
                                }
                            }
                            returnData.put("sites",siteArr);
                        } else {
                            result = 5; //登录失败
                        }
                    }
                }
            } else {
                result = 2;//用户不存在
            }

            if (user != null) {
                returnData.put("code", "0");
            } else {
                user = new LoginUser();
                returnData.put("code", String.valueOf(result));
                returnData.put("error", "not exist!");
            }

            //不管登录成功与否都需要加密 - 统一处理
            getSecretReturnData(user, returnData);
        } catch (Exception e) {
            e.printStackTrace();
            returnData.put("code", "1");
            returnData.put("error", e.getLocalizedMessage());
        }

        InfoHelper.outputText(returnData.toString(), response);
    }

    @RequestMapping(value = "qrCodeLogin.do")
    public void qrCodeLogin(HttpServletRequest request, HttpServletResponse response, String data) throws E5Exception {
        SSO sso = (SSO) Context.getBeanByID("ssoReader");

        JSONObject returnData = new JSONObject();
        LoginUser user = null;
        try {
            JSONObject json = JSONObject.fromObject(data);
            String userStr = json.getString("user");
            String token = json.getString("token");

            //判断扫码登录是否成功。0：成功
            int result = verifyUserToken(userStr, token);
            if (result == 0) {
                UserReader userReader = (UserReader) Context.getBean(UserReader.class);
                User currentUser = userReader.getUserByCode(userStr);

                int siteID = 0;
                Role[] roles = null;

                String tCode = currentUser.getProperty1();//扩展字段1里是租户代号
                if (StringUtils.isBlank(tCode)) tCode = Tenant.DEFAULTCODE;
                int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);

                //检查用户有效期
                int userID = currentUser.getUserID();
                if (!validDate(userLibID, userID)) {
                    result = 3; //过期用户
                } else {
                    //取站点、角色
                    SiteUserReader siteUserReader = (SiteUserReader)Context.getBean("siteUserReader");
                    List<Site> sites = siteUserReader.getSites(userLibID, userID);
                    if (sites.size() > 0) {
                        siteID = sites.get(0).getId();
                        roles = getRolesBySite(userLibID, userID, siteID);
                    }
                    if (roles == null || roles.length == 0) {
                        result = 4; //无可用角色
                    } else {
                        int roleID = roles[0].getRoleID();
//						int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);

                        //登录，得到loginID
                        String[] rets = sso.login(userStr, roleID,
                                request.getRemoteAddr(), request.getServerName(), true);
                        int loginID = Integer.parseInt(rets[0]);
                        if (loginID > 0) {
                            user = new LoginUser(currentUser, loginID);

                            JSONArray siteArr = new JSONArray();
                            for(Site site : sites){

                                roles = getRolesBySite(userLibID, userID, site.getId());

                                if(roles == null || roles.length == 0){
                                    continue;
                                }else{
                                    JSONObject siteJson = new JSONObject();
                                    siteJson.put("siteID",site.getId());
                                    siteJson.put("siteName",site.getName());

                                    int roleId = roles[0].getRoleID();
                                    int newRoleId = (roles.length == 1) ? roleId : PermissionHelper.mergeRoles(roles);

                                    //读有权限的Tab
                                    List<Tab> tabs = MainHelper.getRoleTabs(newRoleId);
                                    if(tabs == null){
                                        siteJson.put("tabs","[]");
                                    }else{
                                        JSONArray stArray = new JSONArray();
                                        for(String appTabId:APP_TABS_ID){
                                            SubTab st = MainHelper.getSubTab(tabs,appTabId);
                                            if(st != null){
                                                JSONObject stjson = new JSONObject();
                                                stjson.put("value",st.getId());
                                                stjson.put("name",st.getName());
                                                stArray.add(stjson);
                                            }
                                        }
                                        siteJson.put("tabs",stArray);
                                    }
                                    siteArr.add(siteJson);
                                }
                            }
                            returnData.put("sites",siteArr);
                        } else {
                            result = 5; //登录失败
                        }
                    }
                }
            } else {
                result = 2; //扫码登录不成功
            }

            if (user != null) {
                returnData.put("code", "0");
            } else {
                user = new LoginUser();
                returnData.put("code", String.valueOf(result));
                returnData.put("error", "not exist!");
            }

            //不管登录成功与否都需要加密 - 统一处理
            getSecretReturnData(user, returnData);
        } catch (Exception e) {
            e.printStackTrace();
            returnData.put("code", "1");
            returnData.put("error", e.getLocalizedMessage());
        }

        InfoHelper.outputText(returnData.toString(), response);
    }

    @RequestMapping(value = "logout.do")
    public void logout(HttpServletResponse response, String data) {
        JSONObject json = checkValid(data);
        if (json.getInt("code") == 0) {
            //清理redis
            try {
                JSONObject dataJson = JSONObject.fromObject(data);
                String loginID = dataJson.getString("loginID");
//                String userID = dataJson.getString("userID");
                String token = null;
                if (RedisManager.hexists(APP_USER_TOKEN, loginID)) {
                    token = RedisManager.hget(APP_USER_TOKEN, loginID);
                    RedisManager.hclear(APP_USER_TOKEN, loginID);
                }
//                if (RedisManager.hexists(APP_USER_TOKEN, userID)) {
//                    token = RedisManager.hget(APP_USER_TOKEN, userID);
//                    RedisManager.hclear(APP_USER_TOKEN, userID);
//                }

                if (!StringUtils.isBlank(token) && RedisManager.hexists(APP_TOKEN_USER, token)) {
                    RedisManager.hclear(APP_TOKEN_USER, token);
                }
            } catch (Exception e) {
                json.put("code", 1);
                json.put("errormsg", "清除redis时出错!");
                e.printStackTrace();
            }
        }
        InfoHelper.outputText(json.toString(), response);
    }

    /**
     * 获得加密后的结果
     *
     * @param user 系统用户对象
     * @return 返回加密后的结果
     */
    private void getSecretReturnData(LoginUser user, JSONObject json) throws UnsupportedEncodingException {
        String returnData, api_data, secretKey;
        //如果token冲突，设置login=0；
        try {
            createToken(user);
        } catch (Exception e) {
            user.setLoginId(0);
            user.setUserId(0);
            json.put("code", "1");
            json.put("error", e.getLocalizedMessage());
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
    }

    //未加密的返回数据
    private String getReturnData(LoginUser user) throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        json.put("loginID", user.getLoginId());
        json.put("userID", user.getUserId());
        json.put("userName", new String(user.getUserName().getBytes(), "UTF-8"));
        json.put("time", user.getTime());
        json.put("token", user.getToken());
        return json.toString();
    }

    //1. 生成token；2.检查hash中是否存在；若存在重复试10次；仍冲突则设置loginID = 0： 表明登录失败
    private void createToken(LoginUser user) throws Exception {
        String token;
        if (user.getLoginId() == 0) {
            user.setToken(EncodeUtils.getMD5(user.getLoginId() + "_" + user.getUserCode() + "_" + user.getTime()));
//            user.setToken(EncodeUtils.getMD5(user.getUserId() + "_" + user.getUserCode() + "_" + user.getTime()));
            return;
        }

        for (int i = 0; i < 10; i++) {
            token = EncodeUtils.getMD5(user.getLoginId() + "_" + user.getUserCode() + "_" + user.getTime());
//            token = EncodeUtils.getMD5(user.getUserId() + "_" + user.getUserCode() + "_" + user.getTime());
            if (!RedisManager.hexists(APP_TOKEN_USER, token)) {
                user.setToken(token);
                //先清除原来的key是token的值，id是可以覆盖的不需要清除
                if (RedisManager.hexists(APP_USER_TOKEN, user.getLoginId() + "")) {
                    String _token = RedisManager.hget(APP_USER_TOKEN, user.getLoginId() + "");
//                if (RedisManager.hexists(APP_USER_TOKEN, user.getUserId() + "")) {
//                    String _token = RedisManager.hget(APP_USER_TOKEN, user.getUserId() + "");
                    RedisManager.hclear(APP_TOKEN_USER, _token);
                }
                RedisManager.hset(APP_TOKEN_USER, token, user.getLoginId() + "");
                RedisManager.hset(APP_USER_TOKEN, user.getLoginId() + "", token);
//                RedisManager.hset(APP_TOKEN_USER, token, user.getUserId() + "");
//                RedisManager.hset(APP_USER_TOKEN, user.getUserId() + "", token);
                break;
            } else {
                user.setTime(user.getTime() + 10);
            }
            if (i == 9) {
                throw new Exception("token被占用 - user:" + user.toString());
            }
        }
    }

	// 判断用户有效期（无有效期设置、或还未到期）
	private boolean validDate(int userLibID, int userID) {
		Document siteUser = siteUserManager.getUser(userLibID, userID);
		Date validDate = siteUser.getDate("u_validDate");
		int status = siteUser.getInt("u_status");
		return ((validDate == null || validDate.after(DateUtils.getDate())) && status == 0);
	}

	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0)
			return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}

    private int verifyUserToken(String userStr, String token){
        long time = Long.parseLong(token);
        long currentTime = System.currentTimeMillis();
        if((currentTime-time)>1000*60*20){
            return -1;
        }

        String _token = RedisManager.hget(RedisKey.APP_USERCODE_TOKEN, userStr);
        if(token.equals(_token)){
            RedisManager.hclear(RedisKey.APP_USERCODE_TOKEN, userStr);
            return 0;
        }else {
            return -2;
        }

    }

}

class LoginUser {
    private long userId;
    private String userCode;
    private String userName;
    private int loginId;

    private long time;

    private String token;

    public LoginUser() {
        this.userCode = "";
        this.userName = "";
        this.time = System.currentTimeMillis();
    }


    /**
     * 登陆时用的
     *
     * @param user
     */
    public LoginUser(User user, int loginId) {
        this.userId = user.getUserID();
        this.userCode = user.getUserCode();
        this.userName = user.getUserName();
        this.loginId = loginId;
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

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
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
                ", loginId=" + loginId +
                ", time=" + time +
                ", token='" + token + '\'' +
                '}';
    }
}
