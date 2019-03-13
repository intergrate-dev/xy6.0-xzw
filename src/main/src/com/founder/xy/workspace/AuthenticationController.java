package com.founder.xy.workspace;

import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.xy.redis.RedisManager;
import com.founder.xy.redis.RedisKey;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.LoginUserManager;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.RoleReader;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.mergerole.controller.SysUser;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.MD5Utils;
import com.founder.xy.commons.TwoDimensionCode;
import com.founder.xy.config.TabHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;

/**
 * 身份验证相关功能
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy")
public class AuthenticationController {
	@Autowired
	private SiteUserReader siteUserReader;
	@Autowired
	private SiteUserManager siteUserManager;
	@Autowired
	private LoginUserManager loginManager;
	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private SiteManager siteManager;
	
	private SSO sso;
	
	/**登录认证*/
	@RequestMapping(method = RequestMethod.POST, value = "auth.do")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = login(request);
		InfoHelper.outputText(result, response);
	}
	
	/**切换站点*/
	@RequestMapping(value = "changeSite.do")
	public void changeSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = changeSite(request);
		InfoHelper.outputText(result, response);
	}
	
	/**维持session*/
	@RequestMapping(value = "keeplive.do")
	public void keeplive(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = null;
		try {
			SysUser user = (SysUser)request.getSession().getAttribute(SysUser.sessionName);
			
			if (user != null) {
				UserManager userReader = (UserManager)Context.getBean(UserManager.class);
				User curUser = userReader.getUserByID(user.getUserID());
				if (curUser == null) {
					result = "nouser";
				} else {
					int nRet = (loginManager.access(user.getLoginID()) > 0) ? 0 : -2;
					if (nRet == 0)
						result = "ok";
					else if (nRet == -2)
						result = "adminaskquit";
				}
			} else {
				result = "nouser";
			}
		} catch(Exception e) {
			e.printStackTrace();
			result = "haserror";
		}
		InfoHelper.outputText(result, response);
	}
	
	/**
	 * 验证码验证，用于移动端登录时。
	 * 改写自E5的验证码登录，E5中会判断e5-config.xml中的验证策略，本系统可能只在移动端需要验证码。
	 * 为了不被session检查拦截，路径中添加/security/
	 */
	@RequestMapping(value = "/security/verifyCaptcha.do")
	public void verifyCaptcha(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		
		String checkCode = request.getParameter("checkCode");
		String rand = (String)request.getSession().getAttribute("rand");
		
		if (StringUtils.isBlank(rand)) {
			result = "-1";
		} else if (checkCode.equals(rand)) {
			result = "1";
		} else {
			result = "0";
		}
		InfoHelper.outputText(result, response);
	}

	/**生成移动采编登录二维码功能*/
	@RequestMapping(value = {"showCode.do"})
	public void showCode(HttpServletRequest request, HttpServletResponse response){
		ssoInit();

		SysUser user = (SysUser)request.getSession().getAttribute(SysUser.sessionName);
		//session过期
		if(user==null)
		{
			System.out.println("没有取到用户名，没有登录系统！");
			return;
		}
		try
		{
			String token = String.valueOf(new Date().getTime());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("usercode", user.getUserCode());
			jsonObject.put("server_url_xy",StringUtils.getNotNull(InfoHelper.getConfig("互动", "翔宇移动采编外网Api地址")).trim());
			jsonObject.put("server_url_cb",StringUtils.getNotNull(InfoHelper.getConfig("互动", "采编Api地址")).trim());
			jsonObject.put("from","xy");
			jsonObject.put("token", token);

			//生成的token存入redis
			RedisManager.hset(RedisKey.APP_USERCODE_TOKEN, user.getUserCode() + "", token);

			TwoDimensionCode codeMake = new TwoDimensionCode();
			OutputStream output = response.getOutputStream();
			codeMake.encoderQRCode(jsonObject.toString(), output);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private String login(HttpServletRequest request) throws Exception {
		ssoInit();
		
		String usercode = request.getParameter("UserCode");
		String sPass2 = request.getParameter("UserPassword");
		try {
			int ret = sso.verifyUserPassword(usercode,sPass2);
			if (ret == -1) return "nouser";
			if (ret == -2) return "nouser"; //用户名密码错的报错一样，避免登录错误消息凭证枚举
			if (ret == -3) return "frozen"; //冻结用户
			
			if (ret == 0) {
				UserReader userReader = (UserReader)Context.getBean(UserReader.class);
				User curUser = userReader.getUserByCode(usercode);
				//if (curUser == null) {
					//return "haserror";
				//}

				int siteID = 0;
				Role[] roles = null;
				
				String tCode = curUser.getProperty1();//扩展字段1里是租户代号
				if (StringUtils.isBlank(tCode)) tCode = Tenant.DEFAULTCODE;
				int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tCode);
				
				if ("1".equals(curUser.getProperty3())) {
					//若是租户管理员，则不判断站点权限，取出租户下的所有站点，取出管理员角色
					siteID = getAdminSite(curUser);
					RoleReader roleReader = (RoleReader)Context.getBean(RoleReader.class);
					roles = roleReader.getRolesByUser(curUser.getUserID());
					if (roles == null || roles.length == 0) {
						roles = getRolesBySite(userLibID, curUser.getUserID(), siteID);
					}
				} else {
					//检查用户有效期
					if (!validDate(userLibID, curUser.getUserID())) return "expired";
					
					//取用户可管理的站点列表
					List<Site> sites = siteUserReader.getSites(userLibID, curUser.getUserID());
					if (sites.size() > 0) {
						siteID = sites.get(0).getId();
						roles = getRolesBySite(userLibID, curUser.getUserID(), siteID);
					}
				}
				if (roles == null || roles.length == 0) return "norole";
				int roleID = roles[0].getRoleID();
				int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
				
				String[] rets = sso.login(usercode, roleID,request.getRemoteAddr(),
						request.getServerName(), true);
				int nID = Integer.parseInt(rets[0]);
				if (nID > 0) {
					clearSession(request);
					putSession(curUser, sPass2, roleID, nID, newRoleID, request);
					
					return "siteID:" + String.valueOf(siteID); //返回站点ID
				} else {
					return rets[1];
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "haserror";
	}

	//判断用户有效期（无有效期设置、或还未到期）
	private boolean validDate(int userLibID, int userID) {
		Document siteUser = siteUserManager.getUser(userLibID, userID);
		Date validDate = siteUser.getDate("u_validDate");
		int status = siteUser.getInt("u_status");
		return ((validDate == null || validDate.after(DateUtils.getDate()))&&status==0);
	}
	
	private String changeSite(HttpServletRequest request) throws Exception {
		ssoInit();
		
		SysUser user = (SysUser)request.getSession().getAttribute(SysUser.sessionName);
		//session过期
		if (user == null) {
			return "novaliduser";
		}
		
		int siteID = Integer.parseInt(request.getParameter("s"));
		try {
			//(1) 首先去掉上一个登录
			sso.logout(user.getLoginID());
			
			UserManager userReader = (UserManager)Context.getBean(UserManager.class);
			User curUser = userReader.getUserByID(user.getUserID());

			Role[] roles = null;
			int userLibID = LibHelper.getUserExtLibID(request);
			if ("1".equals(curUser.getProperty3())) {
				//若是租户管理员，则取出租户角色
				RoleReader roleReader = (RoleReader)Context.getBean(RoleReader.class);
				roles = roleReader.getRolesByUser(user.getUserID());
				if (roles == null || roles.length == 0) {
					roles = getRolesBySite(userLibID, curUser.getUserID(), siteID);
				}
			} else {
				//取用户可管理的站点列表
				roles = getRolesBySite(userLibID, user.getUserID(), siteID);
			}
			if (roles == null || roles.length == 0) return "norole";
			
			int roleID = roles[0].getRoleID();
			int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
			
			//(2) 尝试登录第二个
			String[] rets = sso.login(user.getUserCode(), roleID, request.getRemoteAddr(),
					request.getServerName(), true);
			int nID = Integer.parseInt(rets[0]);
			if (nID > 0) {
				user.setLoginID(nID);
				user.setRoleID(newRoleID);
				user.setRealRoleID(roleID);
				
				request.getSession().setAttribute(SysUser.sessionName, user);
				
				return "ok";
			} else {
				return rets[1];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "haserror";
	}
	private void ssoInit() {
		if (sso == null) {
			sso = (SSO)Context.getBeanByID("ssoReader");
		}
	}
	/** 生成新的会话，防止客户端操纵会话标识 */
	private void clearSession(HttpServletRequest request) {
		try {
			request.getSession().invalidate();
	
			if (request.getCookies() != null) {
				Cookie cookie = request.getCookies()[0];// 获取cookie
				cookie.setMaxAge(0);// 让cookie过期
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//验证通过后，把用户的信息存到session里
	private void putSession(User curUser, String sPass2, int roleID, int loginID, int newRoleID, 
			HttpServletRequest request) {
		SysUser user = new SysUser();
		
		user.setUserID(curUser.getUserID());
		user.setUserName(curUser.getUserName());
		user.setUserCode(curUser.getUserCode());
		
		user.setAdmin(true); //设为系统管理员，使前台可以设置操作权限（此时不判断文档类型管理权限） 
		//user.setUserPassword(sPass2);
		user.setRoleID(roleID);
		user.setLoginID(loginID);
		user.setIp(request.getRemoteAddr());
		
		user.setRealRoleID(roleID);
		user.setRoleID(newRoleID);
		
		request.getSession().setAttribute(SysUser.sessionName, user);
		
		if (needSysAdmin(newRoleID)) {
			//为了能在前台设置分类、部门角色和权限，加管理端需要的session
			request.getSession().setAttribute(SysUser.sessionAdminName, user);
		}
		
		//把租户放在session中（租户代号用于读对应文档库，租户机构ID用于管理部门和角色）
		//扩展字段1中保存租户代号，扩展字段2保存的是否管理员
		Tenant tenant = tenantManager.get(curUser.getProperty1());
		request.getSession().setAttribute(Tenant.SESSIONNAME, tenant);
	}
	
	//判断角色是否有部门角色的主界面权限
	private boolean needSysAdmin(int roleID) {
		if (roleID <= 0) return false;
		
		//读取tabs
		String[] roleTabs = TabHelper.readRoleTabs(roleID, "MainPermission");
		return ArrayUtils.contains(roleTabs, "scat") || ArrayUtils.contains(roleTabs, "sorg");
	}
	
	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;
		
		Role[] roles =  new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
	
	//取租户管理员的站点中的第一个站点ID
	private int getAdminSite(User curUser) throws E5Exception {
		//若是租户管理员，则不判断权限，取出租户下的所有站点，取出租户角色
		String tenantCode = curUser.getProperty1();
		
		Document[] docs = siteManager.getSites(tenantCode);
		if (docs == null || docs.length == 0)
			return 0;
		return (int)docs[0].getDocID();
	}
	
	
	@RequestMapping(value = "/security/verifyDynamicCode.do")
	public void verifyDynamicCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = "";
		
		String userCode = request.getParameter("UserCode");
		long dynamicCode = WebUtil.getLong(request, "DynamicCode", -1L);
		long timeMsec = System.currentTimeMillis();
		if (dynamicCode < 0) {
			result = "-1";
		} else if (check_code(userCode, dynamicCode, timeMsec)) {
			result = "1";
		} else {
			result = "0";
		}
		//result = "1";
		InfoHelper.outputText(result, response);
	}
	private boolean check_code(String userCode, long dynamicCode, long timeMsec) {  
        Base32 codec = new Base32();  
        String secret = codec.encodeAsString(MD5Utils.getMD5(userCode).getBytes()).substring(8, 24);
        byte[] decodedKey = codec.decode(secret);  
        long t = (timeMsec / 1000L) / 30L;  
        for (int i = -3; i <= 3; ++i) {  
            long hash;  
            try {  
                hash = verify_code(decodedKey, t + i);  
            }catch (Exception e) {  
                e.printStackTrace();  
                throw new RuntimeException(e.getMessage());  
            }  
            if (hash == dynamicCode) {  
                return true;  
            }  
        }  
        return false;  
    }  
      
    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {  
        byte[] data = new byte[8];  
        long value = t;  
        for (int i = 8; i-- > 0; value >>>= 8) {  
            data[i] = (byte) value;  
        }  
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");  
        Mac mac = Mac.getInstance("HmacSHA1");  
        mac.init(signKey);  
        byte[] hash = mac.doFinal(data);  
        int offset = hash[20 - 1] & 0xF;  
        long truncatedHash = 0;  
        for (int i = 0; i < 4; ++i) {  
            truncatedHash <<= 8;  
            truncatedHash |= (hash[offset + i] & 0xFF);  
        }  
        truncatedHash &= 0x7FFFFFFF;  
        truncatedHash %= 1000000;  
        return (int) truncatedHash;  
    }
    
    @RequestMapping(value = "/security/viewDynamicCode.do")
    @ResponseBody
	public void viewDynamicCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        long[] docIDs = StringUtils.getLongArray(strDocIDs);
        JSONArray result = new JSONArray();
        Base32 codec = new Base32();  
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        for(long docID : docIDs){
        	JSONObject obj = new JSONObject();
        	Document doc = docManager.get(docLibID, docID);
        	String secret = codec.encodeAsString(MD5Utils.getMD5(doc.getString("u_code")).getBytes()).substring(8, 24);
        	obj.put("name", doc.getString("u_name"));
        	obj.put("secret", secret);
        	result.add(obj);
        }
        InfoHelper.outputJson(result.toString(), response);
    }
}
