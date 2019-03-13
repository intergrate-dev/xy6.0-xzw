package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.context.Context;
import com.founder.e5.sys.SysConfigReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.context.ContextLoader;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

import com.founder.e5.commons.Log;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.LRUCache;
import com.founder.amuc.commons.RedisManager;
import com.founder.amuc.commons.RedisToolUtil;
import com.founder.amuc.commons.ValidateHelper;
import com.founder.amuc.member.MemberHelper;
import com.founder.amuc.member.MemberManager;
import com.founder.amuc.member.MemberReader;
import com.founder.amuc.member.input.HttpClientUtil;
import com.founder.amuc.commons.JedisClient;

import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

/**
 * 会员实时入库接口 访问方式：<webroot>/member/
 * 
 * @author Gong Lijie 2014-8-4
 */
@Controller
@RequestMapping("/api/member")
public class MemberAdapter {
	//private static JedisCluster jedisCluster = RedisManager.getJedisCluster();
	private static LRUCache<String, String> portrait = new LRUCache<String, String>(1000);
	
	  public static LRUCache<String, String> getPortraitCache() {
		    return portrait;
		  }

	@Autowired
	MemberManager mManager;
	@Autowired
	JedisClient jedisClient;

	/**
	 * 注册会员接口 注册接口添加设备码、邀请码字段
	 *
	 * @param nickname
	 *            昵称 必填
	 * @param mobile
	 *            手机号 必填
	 * @param email
	 *            邮箱 不必填
	 * @param password
	 *            密码
	 * @param code
	 *            邀请码
	 * @param deviceid
	 *            设备码
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/register.do"},consumes={"application/x-www-form-urlencoded"})
	public void register(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String nickname = request.getParameter("nickname");
		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String code = request.getParameter("code");
		String deviceid = request.getParameter("deviceid");
		Map<String, Object> maps = new HashMap<String, Object>();
		// 判断必填参数是否存在，不是""或null
		if (StringUtils.isBlank(nickname) || StringUtils.isBlank(mobile) || StringUtils.isBlank(password)) {
			maps.put("code", 0);
			maps.put("msg", "注册会员接口:nickname，mobile，password必填");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		// 调用sso同步接口
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", nickname));
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("phone", mobile));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		String res = new HttpClientUtil().callSsoAPI("/api/syn/register", params);
		JSONObject resJson = JSONObject.fromObject(res);
		if (!"1".equals(resJson.getString("code"))) {
			maps.put("code", 0);
			maps.put("msg", "调用SSO同步接口失败");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		// 注册会员
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.register(nickname, mobile, email, password, tenantcode, code, deviceid);

		outputJson(String.valueOf(result), response);
	}

	/**
	 * 注册会员接口增强版，返回用户注册信息
	 * 
	 * @param nickname
	 *            昵称 必填
	 * @param mobile
	 *            手机号 必填
	 * @param email
	 *            邮箱 不必填
	 * @param password
	 *            密码
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value={"/registerEx.do"},consumes={"application/x-www-form-urlencoded"})
	public void registerEx(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		String nickname = request.getParameter("nickname");
		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(nickname) || StringUtils.isBlank(mobile) || StringUtils.isBlank(password)) {
			maps.put("code", 0);
			maps.put("msg", "注册会员接口:nickname，mobile，password必填");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(mobile)) {
			if (!ValidateHelper.mobilephone(mobile)) {
				maps.put("code", 0);
				maps.put("msg", "手机号格式不对");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
		}
		// 检查当前访问的key是否存在redis中，存在阻止本次请求。
		boolean keyExist = RedisToolUtil.isNotIntented("amuc.m.registerEx." + mobile + "." + request.getRemoteAddr(), 3,
				jedisClient);
		if (!keyExist) {//
			maps.put("code", 0);
			maps.put("msg", "操作过于频繁,请稍后再试");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		// 调用sso同步接口
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", nickname));
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("phone", mobile));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		String res = new HttpClientUtil().callSsoAPI("/api/syn/register", params);
		JSONObject resJson = JSONObject.fromObject(res);
		if (!"1".equals(resJson.getString("code"))) {
			maps.put("code", 0);
			maps.put("msg", "调用SSO同步接口失败");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
		}
		String ssoid = resJson.getJSONObject("value").getString("ssoid");
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.registerEx(ssoid, nickname, nickname, mobile, email, password, tenantcode);
		outputJson(String.valueOf(result), response);
	}

	/**
	 * 登录接口
	 * 
	 * @param mobile
	 * @param email
	 * @param password
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Login.do")
	public void Login(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(mobile) && StringUtils.isBlank(email)) {
			maps.put("code", 0);
			maps.put("msg", "登录账号（手机、邮箱均为空）");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if (StringUtils.isBlank(password)) {
			maps.put("code", 0);
			maps.put("msg", "密码为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(mobile)) {
			if (!ValidateHelper.mobilephone(mobile)) {
				maps.put("code", 0);
				maps.put("msg", "手机号格式不对");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
		}
		// 检查当前访问的key是否存在redis中，存在阻止本次请求。
		boolean keyExist = RedisToolUtil.isNotIntented("amuc.m.login." + mobile + "." + request.getRemoteAddr(), 3,
				jedisClient);
		if (!keyExist) {//
			maps.put("code", 0);
			maps.put("msg", "操作过于频繁,请稍后再试");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.Login4south(mobile, email, password, tenantcode);

		outputJson(String.valueOf(result), response);

	}

	/**
	 * 第三方登录接口
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loginByOther.do")
	public void loginByOther(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		int type = Integer.parseInt(request.getParameter("type"));
		String oid = request.getParameter("oid");
		String name = request.getParameter("name");
		Map<String, Object> maps = new HashMap<String, Object>();
		// 检查当前访问的key是否存在redis中，存在阻止本次请求。
		boolean keyExist = RedisToolUtil.isNotIntented(
				"amuc.m.loginByOther." + type + "." + oid + "." + request.getRemoteAddr(), 3, jedisClient);
		if (!keyExist) {//
			maps.put("code", 0);
			maps.put("msg", "操作过于频繁,请稍后再试");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.loginByOther(type, oid, name, tenantcode);

		outputJson(String.valueOf(result), response);
	}

	/**
	 * 上传头像
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/uploadPortrait.do")
	public void uploadPortrait(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String uid = request.getParameter("uid");
		System.out.println("================ MemberAdapter uploadPortrait, uid: " + uid);
		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(uid)) {
			maps.put("msg", "上传头像接口：uid为空");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		String tenantcode = InfoHelper.getTenantCode(request);
		// 检查会员状态是否正常，mStatus=1 正常 mStatus=0禁用
		Document mDoc = MemberHelper.getMember(tenantcode, Long.parseLong(uid));
		if (mDoc == null) {
			maps.put("code", 0);
			maps.put("msg", "会员不存在");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if (mDoc != null && mDoc.getInt("mStatus") == 0) {
			maps.put("msg", "该会员被禁用");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}

		System.out.println("================ MemberAdapter uploadPortrait, isThirdAccount check ... ");
		if (mManager.isThirdAccount(mDoc)) {
			maps.put("msg", "第三方账户不支持头像修改");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}

		// 1.上传头像图片
		FileItem file = getFileItem(request);

		if (file==null||file.getSize() <= 0) {
			maps.put("msg", "上传头像接口：图片文件为空");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}

		String result = mManager.uploadPortrait(uid, file, tenantcode);
		// 如果上传成功，且在缓存中存在,更新缓存中的数据
		JSONObject jsobject = JSONObject.fromObject(result);
		if ("1".equals(jsobject.getString("code")) && jsobject.containsKey("headImg")) {
			portrait.put(uid, jsobject.getString("headImg"));
		}
		System.out.println("================== MemberAdapter uploadPortrait, end result: " + result);
		outputJson(result, response);
	}

	/**
	 * 修改会员信息接口
	 * 
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/modify.do")
	public void modify(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=UTF-8");
		String uid = request.getParameter("uid");
		String nickname = request.getParameter("nickname");
		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String birthday = request.getParameter("birthday");
		String region = request.getParameter("region");
		String sex = request.getParameter("sex");
		String address = request.getParameter("address");
		String code = request.getParameter("code");
		String deviceid = request.getParameter("deviceid");

		String tenantcode = InfoHelper.getTenantCode(request);

		Map<String, Object> maps = new HashMap<String, Object>();
		// 检查uid、 手机号、昵称、密码 不为空
		if (StringUtils.isBlank(uid)) {
			maps.put("msg", "修改资料 接口：uid为null或空字符串");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(mobile)) {
			if (!ValidateHelper.mobilephone(mobile)) {
				maps.put("msg", "手机号格式不对");
				maps.put("code", "0");
				JSONObject jsonres = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonres), response);
				return;
			}
		}
		
		// 检查会员状态是否正常，mStatus=1 正常 mStatus=0禁用
		Document mDoc = MemberHelper.getMember(tenantcode, Long.parseLong(uid));
		if (mDoc == null) {
			maps.put("code", 0);
			maps.put("msg", "手机号不存在");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if (mDoc != null && mDoc.getInt("mStatus") == 0) {
			maps.put("msg", "该会员被禁用");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		// 组装传来的参数
		HashMap<String, String> memberData = new HashMap<String, String>();
		memberData.put("nickname", nickname);
		memberData.put("email", email);
		memberData.put("password", password);
		memberData.put("birthday", birthday);
		memberData.put("region", region);
		memberData.put("sex", sex);
		memberData.put("address", address);
		memberData.put("code", code);
		memberData.put("deviceid", deviceid);

		String result = mManager.modifyMember(uid, memberData, tenantcode);

		outputJson(result, response);
	}

	/**
	 * 忘记密码接口 1表示成功。0表示不做处理
	 * 
	 * @param request
	 * @return
	 * @throws E5Exception
	 */
	@RequestMapping("/ForgetPassword.do")
	public void ForgetPassword(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		Map<String, Object> maps = new HashMap<String, Object>();
		String password = request.getParameter("password");
		String mobile = request.getParameter("mobile");

		if (StringUtils.isBlank(mobile)) {
			maps.put("code", "0");
			maps.put("msg", "mobile为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		if (StringUtils.isBlank(password)) {
			maps.put("code", "0");
			maps.put("msg", "password为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(mobile)) {
			if (!ValidateHelper.mobilephone(mobile)) {
				maps.put("code", "0");
				maps.put("msg", "手机号格式不对");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
		}
		// 检查当前访问的key是否存在redis中，存在阻止本次请求。
		boolean keyExist = RedisToolUtil
				.isNotIntented("amuc.m.forgetpassword." + mobile + "." + request.getRemoteAddr(), 3, jedisClient);
		if (!keyExist) {//
			maps.put("code", 0);
			maps.put("msg", "操作过于频繁,请稍后再试");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String TenantCode = InfoHelper.getTenantCode(request);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, TenantCode);

		DBSession conn = null;
		try {
			// 事务处理
			conn = com.founder.e5.context.Context.getDBSession();
			conn.beginTransaction();
			String[] column = { "mPassword", "mStatus" };
			Document[] members = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG = 0 and mMobile = ? ",
					new Object[] { mobile }, column);
			if (members != null && members.length > 0) {
				if (members[0].getInt("mStatus") == 1) {
					// 调用sso同步接口
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("phone", mobile));
					params.add(new BasicNameValuePair("password", password));
					String res = new HttpClientUtil().callSsoAPI("/api/syn/updatePwd", params);
					JSONObject resJson = JSONObject.fromObject(res);
					if (!"1".equals(resJson.getString("code"))) {
						maps.put("code", 0);
						maps.put("msg", "调用SSO同步接口失败");
						JSONObject jsonstr = JSONObject.fromObject(maps);
						outputJson(String.valueOf(jsonstr), response);
						return;
					}

					members[0].set("mPassword", password);
					docManager.save(members[0], conn);
					conn.commitTransaction();
				} else {
					maps.put("code", "0");
					maps.put("msg", "该会员被禁用");
					JSONObject jsonstr = JSONObject.fromObject(maps);
					outputJson(String.valueOf(jsonstr), response);
					return;
				}
			} else {
				maps.put("code", "0");
				maps.put("msg", "fail");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResourceMgr.rollbackQuietly(conn);
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
		maps.put("code", "1");
		maps.put("msg", "sucess");
		JSONObject jsonstr = JSONObject.fromObject(maps);
		outputJson(String.valueOf(jsonstr), response);
	}

	/**
	 * 获取头像接口
	 *            amuc系统内的唯一标识
	 * @return
	 * @throws E5Exception
	 * @throws IOException
	 */
	@RequestMapping("/getPortrait.do")
	public void getPortrait(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		String uid = request.getParameter("uid");
		Map<String, Object> maps = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		if (StringUtils.isBlank(uid)||uid == "") {
			maps.put("code", 0);
			maps.put("msg", "获取头像接口：uid为空");
			maps.put("url", "notexist");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		// 判断缓存中是否含有,不含有从数据库中去读取,含有从缓存中取
		if (portrait.containKey(uid)) {
			String portraitpath = portrait.get(uid);
			if (!StringUtils.isBlank(portraitpath)) {
				// response.sendRedirect(portraitpath);
				String url = portraitpath;
				json.put("url", url);
				outputJson(String.valueOf(json), response);
				return;
			}
			/*else {
				// response.sendRedirect("/amuc/member/headImg/default.bmp");
				String url = "/amuc/member/headImg/default.bmp";
				json.put("url", url);
				outputJson(String.valueOf(json), response);
				return;
			}*/
		} else {

			String tenantcode = InfoHelper.getTenantCode(request);
			String result = mManager.getPortrait(uid, tenantcode);

			System.out.println(result);

			JSONObject jsonobject = JSONObject.fromObject(result);

			String headImgName = null;
			if (!StringUtils.isBlank(jsonobject.getString("msg"))) {
				headImgName = jsonobject.getString("msg");
			} else {
				headImgName = mManager.setDefaultImg();
			}
			if ("1".equals(jsonobject.getString("code"))) {

				//response.sendRedirect("/amuc/member/headImg/" + headImgName);
				json.put("url", headImgName);
				outputJson(String.valueOf(json), response);
				// 将结果加入缓存
				portrait.put(uid, headImgName);
				return;

			} else {// 不存在会员重定向空
				//response.sendRedirect("");
				String url = "notexist";
				String msg = "不存在id为"+uid+"的会员";
				json.put("msg", msg);
				json.put("url", url);
				outputJson(String.valueOf(json), response);
				return;
			}
		}

	}

	/**
	 * 获取用户信息接口
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getUserMessage.do")
	public void getUserMessage(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=UTF-8");
		String ssoid = request.getParameter("ssoid");
		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(ssoid)) {
			maps.put("code", 0);
			maps.put("msg", "会员id为空");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(jsonstr.toString(), response);
			return;
		}
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.getUserMessage(ssoid, tenantcode);

		outputJson(result, response);
	}

	/**
	 * 查询会员积分 访问方式：<webroot>/member/score?source=&id=
	 *            在数据来源系统中的会员表名，可选 可选参数：tc（租户代号），table（源系统的会员表）
	 */
	@RequestMapping("/score.do")
	public void score(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String source = request.getParameter("source");
		String oriID = request.getParameter("id");
		String tc = request.getParameter("tc");
		String table = request.getParameter("table");
		if (!StringUtils.isBlank(source) && !StringUtils.isBlank(oriID)) {
			MemberReader memberReader = new MemberReader();
			long score = memberReader.score(source, oriID, tc, table);
			outputJson(String.valueOf(score), response);
		} else {
			outputJson(String.valueOf("接口调用失败，请查看传递的参数是否正确"), response);
		}

	}

	/**
	 * 查询会员的积分记录 访问方式：<webroot>/member/scoreList?source=&id=&curPage=&pageSize=
	 *            每页的数量 可选参数：tc（租户代号），table（源系统的会员表）
	 */
	@RequestMapping("/scoreList.do")
	public void scoreList(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String source = request.getParameter("source");
		String oriID = request.getParameter("id");
		String tc = request.getParameter("tc");
		String table = request.getParameter("table");
		String getAll = request.getParameter("getAll");
		String curPage = request.getParameter("curPage");
		String pageSize = request.getParameter("pageSize");
		outputJson(String.valueOf(""), response);
	}

	/**
	 * 获取上传文件
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private static FileItem getFileItem(HttpServletRequest request) throws FileUploadException {
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		upload.setHeaderEncoding("UTF-8");
		upload.setSizeMax(10000000L);

		List<FileItem> items = upload.parseRequest(request);
		if(items.size()==0){
			return null;
		}
		FileItem file = (FileItem) items.get(0);
		return file;
	}

	public static void outputJson(String result, HttpServletResponse response) {
		if (result == null)
			return;

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
	
	/**
	 * sso注册会员是调用接口，用于同步ssoid和uid，返回用户注册信息
	 * 			方法废除
	 * @param request
	 * @return
	 * @throws Exception
	 */

	/*@RequestMapping(value={"/ssoRegisterEx.do"},consumes={"application/x-www-form-urlencoded"})
	public void ssoRegisterEx(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=UTF-8");
	    String username = request.getParameter("username");
		String nickname = request.getParameter("nickname");
		//String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String ssoid = request.getParameter("ssoid");
		String password = request.getParameter("password");
		String m_siteID = request.getParameter("siteid");
		String headImg = request.getParameter("headImg");

		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(ssoid) || StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
			maps.put("code", 0);
			maps.put("msg", "注册会员接口:email，password必填");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(email)) {
			if (!ValidateHelper.email(email)) {
				maps.put("code", 0);
				maps.put("msg", "邮箱格式不对");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
		}
	
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = mManager.registerEx(m_siteID,ssoid, username, nickname, null, email, password, headImg,tenantcode);
		outputJson(String.valueOf(result), response);
	}*/
	
	@RequestMapping("/del.do")
	public void del(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");

		String ssoids = request.getParameter("ssoid");
		Map<String, Object> maps = new HashMap<String, Object>();
		if (StringUtils.isBlank(ssoids)) {
			maps.put("code", 0);
			maps.put("msg", "删除会员接口:ssoid必填");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}

		String tenantcode = InfoHelper.getTenantCode(request);
		String code = "";
		StringBuilder msg = new StringBuilder();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);
		String[] ssoid = ssoids.split(",");
		for (int i = 0 ; i <ssoid.length ; i++ ) {
			//System.out.println("--"+ssoid[i]); 
			Document[] members = docManager.find(docLib.getDocLibID(), " SYS_DELETEFLAG = 0 and uid_sso = ? ", new Object[]{ssoid[i]});
			if(members != null && members.length > 0){
				members[0].set("SYS_DELETEFLAG", 1);
				docManager.save(members[0]);
				code = "1";
				msg.append("会员删除成功");
			}else{
				code = "0";
				msg.append("不存在该会员");
			}
	    }
		
		maps.put("code", code);
		maps.put("msg", msg.toString());
		JSONObject result = JSONObject.fromObject(maps);
		outputJson(String.valueOf(result), response);
		return;
	}
}
