package com.founder.xy.workspace;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.RoleReader;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.workspace.mergerole.controller.SysUser;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.TabHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.system.site.UserUtilDAO;

/**
 * 身份验证相关功能
 * 
 * @author yuanying
 */
@Controller
@RequestMapping("/tokenHelp")
public class GetTokenController {
	@Autowired
	private SiteUserReader siteUserReader;
	@Autowired
	private SiteUserManager siteUserManager;
	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private SiteManager siteManager;
	@Autowired
	private UserUtilDAO utilDAO;
	@Autowired
	private UserManager userManager = (UserManager) Context
			.getBean("UserManager");
	private SSO sso;

	private static int expiredHour = 1;// token有效时间默认设置为1小时

	private static Long expireTime = 0L;// token过期时间

	@RequestMapping(value = "/getToken.do", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getToken(HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		String status = InfoHelper.getConfig("其它", "开启EIP自动登录");
		if (!StringUtil.isBlank(status) && "是".equalsIgnoreCase(status)) {
			String userCode = req.getParameter("userCode");
			userCode = DESUtils.decrypt(userCode);
			if ("非法操作".equals(userCode)) {
				return getResultJson("5", "非法操作");
			}
			User user = userManager.getUserByCode(userCode);
			if (user == null) {
				return getResultJson("2", "用户不存在");
			}
			if (user.getBPNumber() != null && !"".equals(user.getBPNumber())) {
				String json = checkToken(user.getBPNumber(),
						user.getTelHomeNumber(), null);
				JSONObject s = JSONObject.fromObject(json);
				if ("1".equals(s.getString("code"))) {// 没有过期直接返回
					return json;
				} else {// 过期更新token
					expireTime = System.currentTimeMillis();

					user.setTelHomeNumber(System.currentTimeMillis() + "");
					user.setBPNumber(generateToken(userCode));
					userManager.update(user);
					return getResultJson("1", user.getBPNumber());
				}
			} else {// 更新最新token
				expireTime = System.currentTimeMillis();
				user.setTelHomeNumber(System.currentTimeMillis() + "");
				user.setBPNumber(generateToken(userCode));
				userManager.update(user);
				return getResultJson("1", user.getBPNumber());
			}
		} else {
			return getResultJson("4", "请配置自动登录开关");
		}
	}

	@RequestMapping(value = "/validToken.do", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String validToken(String token, String type) throws Exception {
		long timeInterval;
		if ("login".equalsIgnoreCase(type)) {// token过期后仍然可以继续使用一个小时
			timeInterval = (expiredHour + 1) * 60 * 60 * 1000;
		} else {
			timeInterval = expiredHour * 60 * 60 * 1000;
		}

		List<Map<String, String>> user = utilDAO.getUserCodeByToken(token);

		if (user != null && user.size() > 0) {
			long lastmodifiedLong = Long.valueOf(
					user.get(0).get("lastmodified")).longValue();

			if ((System.currentTimeMillis() - lastmodifiedLong - timeInterval) < 0) {
				expireTime = lastmodifiedLong;
				return getResultJson("1", token);
			}
		}
		return getResultJson("3", "token过期");
	}

	private String checkToken(String token, String lastmodified, String type)
			throws Exception {
		long timeInterval;
		if ("login".equalsIgnoreCase(type)) {// token过期后仍然可以继续使用一个小时
			timeInterval = (expiredHour + 1) * 60 * 60 * 1000;
		} else {
			timeInterval = expiredHour * 60 * 60 * 1000;
		}
		long lastmodifiedLong = Long.valueOf(lastmodified).longValue();

		if ((System.currentTimeMillis() - lastmodifiedLong - timeInterval) < 0) {
			expireTime = lastmodifiedLong;
			return getResultJson("1", token);
		}

		return getResultJson("3", "token过期");
	}

	@RequestMapping(value = "/getUserInfo.do", produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getUserInfo(HttpServletRequest request,
			HttpServletResponse response, String token) throws Exception {
		List<Map<String, String>> userList = utilDAO.getUserCodeByToken(token);

		if (userList == null || userList.size() == 0) {
			return getResultJson("3", "token过期");
		}

		String json = checkToken(token, userList.get(0).get("lastmodified"),
				null);// 登录时token有效期延长一个小时后过期
		JSONObject s = JSONObject.fromObject(json);

		if ("1".equals(s.getString("code"))) {
			String result = login(request, userList.get(0).get("userCode"));
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", "1");
			map.put("result", result);
			map.put("userCode", userList.get(0).get("userCode"));
			map.put("token", token);
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValueAsString(map);
			return mapper.writeValueAsString(map);
		} else {
			return getResultJson("3", "token过期");
		}
	}

	private String login(HttpServletRequest request, String usercode)
			throws Exception {
		ssoInit();
		try {

			UserReader userReader = (UserReader) Context
					.getBean(UserReader.class);
			User curUser = userReader.getUserByCode(usercode);

			int siteID = 0;
			Role[] roles = null;

			String tCode = curUser.getProperty1();// 扩展字段1里是租户代号
			if (StringUtils.isBlank(tCode))
				tCode = Tenant.DEFAULTCODE;
			int userLibID = LibHelper
					.getLibID(DocTypes.USEREXT.typeID(), tCode);

			if ("1".equals(curUser.getProperty3())) {
				// 若是租户管理员，则不判断站点权限，取出租户下的所有站点，取出管理员角色
				siteID = getAdminSite(curUser);
				RoleReader roleReader = (RoleReader) Context
						.getBean(RoleReader.class);
				roles = roleReader.getRolesByUser(curUser.getUserID());
				if (roles == null || roles.length == 0) {
					roles = getRolesBySite(userLibID, curUser.getUserID(),
							siteID);
				}
			} else {
				// 检查用户有效期
				if (!validDate(userLibID, curUser.getUserID()))
					return "expired";

				// 取用户可管理的站点列表
				List<Site> sites = siteUserReader.getSites(userLibID,
						curUser.getUserID());
				if (sites.size() > 0) {
					siteID = sites.get(0).getId();
					roles = getRolesBySite(userLibID, curUser.getUserID(),
							siteID);
				}
			}
			if (roles == null || roles.length == 0)
				return "norole";
			int roleID = roles[0].getRoleID();
			int newRoleID = (roles.length == 1) ? roleID : PermissionHelper
					.mergeRoles(roles);

			String[] rets = sso.login(usercode, roleID,
					request.getRemoteAddr(), request.getServerName(), true);
			int nID = Integer.parseInt(rets[0]);
			if (nID > 0) {
				clearSession(request);
				putSession(curUser, curUser.getUserPassword(), roleID, nID,
						newRoleID, request);

				return "siteID:" + String.valueOf(siteID); // 返回站点ID
			} else {
				return rets[1];
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "haserror";
	}

	public String getResultJson(String code, String msg) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(code)) {
			map.put("code", code);
			map.put("token", msg);
			map.put("expireTime",
					DateUtils.format(new Timestamp(expireTime + expiredHour
							* 60 * 60 * 1000), "yyyy-MM-dd HH:mm:ss"));
		} else {
			map.put("code", code);
			map.put("msg", msg);
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(map);
	}

	private String generateToken(String usercode) {
		try {
			long current = System.currentTimeMillis();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(String.valueOf(usercode).getBytes());
			md.update(String.valueOf(current).getBytes());
			return toHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private String toHex(byte buffer[]) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}
		return sb.toString();
	}

	// 判断用户有效期（无有效期设置、或还未到期）
	private boolean validDate(int userLibID, int userID) {
		Document siteUser = siteUserManager.getUser(userLibID, userID);
		Date validDate = siteUser.getDate("u_validDate");
		int status = siteUser.getInt("u_status");
		return ((validDate == null || validDate.after(DateUtils.getDate())) && status == 0);
	}

	private void ssoInit() {
		if (sso == null) {
			sso = (SSO) Context.getBeanByID("ssoReader");
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

	// 验证通过后，把用户的信息存到session里
	private void putSession(User curUser, String sPass2, int roleID,
			int loginID, int newRoleID, HttpServletRequest request) {
		SysUser user = new SysUser();

		user.setUserID(curUser.getUserID());
		user.setUserName(curUser.getUserName());
		user.setUserCode(curUser.getUserCode());

		user.setAdmin(true); // 设为系统管理员，使前台可以设置操作权限（此时不判断文档类型管理权限）
		// user.setUserPassword(sPass2);
		user.setRoleID(roleID);
		user.setLoginID(loginID);
		user.setIp(request.getRemoteAddr());

		user.setRealRoleID(roleID);
		user.setRoleID(newRoleID);

		request.getSession().setAttribute(SysUser.sessionName, user);

		if (needSysAdmin(newRoleID)) {
			// 为了能在前台设置分类、部门角色和权限，加管理端需要的session
			request.getSession().setAttribute(SysUser.sessionAdminName, user);
		}

		// 把租户放在session中（租户代号用于读对应文档库，租户机构ID用于管理部门和角色）
		// 扩展字段1中保存租户代号，扩展字段2保存的是否管理员
		Tenant tenant = tenantManager.get(curUser.getProperty1());
		request.getSession().setAttribute(Tenant.SESSIONNAME, tenant);
	}

	// 判断角色是否有部门角色的主界面权限
	private boolean needSysAdmin(int roleID) {
		if (roleID <= 0)
			return false;

		// 读取tabs
		String[] roleTabs = TabHelper.readRoleTabs(roleID, "MainPermission");
		return ArrayUtils.contains(roleTabs, "scat")
				|| ArrayUtils.contains(roleTabs, "sorg");
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

	// 取租户管理员的站点中的第一个站点ID
	private int getAdminSite(User curUser) throws E5Exception {
		// 若是租户管理员，则不判断权限，取出租户下的所有站点，取出租户角色
		String tenantCode = curUser.getProperty1();

		Document[] docs = siteManager.getSites(tenantCode);
		if (docs == null || docs.length == 0)
			return 0;
		return (int) docs[0].getDocID();
	}

	public static void main(String[] args) {
		String configFile = "E:\\java_JDK\\Workspaces\\xy5.0\\src\\com\\founder\\xy\\workspace\\allmedia-config.xml";
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(configFile);

		SAXReader reader = new SAXReader();
		try {
			org.dom4j.Document doc = reader.read(in);
			if (doc == null)
				return;

			org.dom4j.Element el = doc.getRootElement();
			el = (Element) el.selectSingleNode("/autoLogin");
			if (el != null) {
				System.out.println(el.attributeValue("status"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(in);
		}
	}

}
