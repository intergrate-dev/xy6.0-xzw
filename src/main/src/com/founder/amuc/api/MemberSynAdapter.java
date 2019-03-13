package com.founder.amuc.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.amuc.commons.BaseHelper;
import com.founder.e5.context.Context;
import com.founder.e5.sys.SysConfigReader;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.founder.amuc.collection.CollectHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.commons.ValidateHelper;
import com.founder.amuc.member.MemberHelper;
import com.founder.amuc.member.MemberManager;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.commons.ResourceMgr;

@Controller
@RequestMapping("/api/member/syn")
public class MemberSynAdapter {

	@Autowired
	MemberManager mManager;

	@RequestMapping(value = {"/registerEx.do"},consumes={"application/x-www-form-urlencoded"})
	public void registerEx(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		System.out.println("================ MemberSynAdapter registerEx,provider entry ....... " );
		String ssoid = request.getParameter("ssoid");
		String username = request.getParameter("username");
		String nickname = request.getParameter("nickname");
		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String m_siteID = request.getParameter("siteid");
		String headImg = request.getParameter("headImg");
		String provider = request.getParameter("provider");
		String oauthid = request.getParameter("oauthid");
		System.out.println("================ MemberSynAdapter registerEx,provider: " + provider + ", oauthid: " + oauthid + ", nickname: " + nickname);
		if (StringUtils.isBlank(headImg)) {
			headImg = mManager.setDefaultImg();
		}
		JSONObject json = new JSONObject();
		if (StringUtils.isBlank(ssoid) || StringUtils.isBlank(password)) {
			json.put("code", 0);
			json.put("msg", "注册会员接口:ssoid，password必填");
			outputJson(json.toString(), response);
			return;
		}
		// 检查手机号格式是否正确
		if (!StringUtils.isBlank(email)) {
			if (!ValidateHelper.email(email)) {
				json.put("code", 0);
				json.put("msg", "邮箱格式不对");
				outputJson(json.toString(), response);
				return;
			}
		}
		String tenantcode = InfoHelper.getTenantCode(request);
		String result = null;
		if (StringUtils.isBlank(provider) && StringUtils.isBlank(oauthid)) {
			result = mManager.registerEx(m_siteID, ssoid, username, nickname, mobile, email, password, headImg, tenantcode);
		} else {
			if (StringUtils.isBlank(provider) || StringUtils.isBlank(oauthid)) {
				json.put("code", 0);
				json.put("msg", "第三方账户注册，参数：provider、oauthid不能为空");
				outputJson(json.toString(), response);
				return;
			}
			result = mManager.registerExByOther(m_siteID, ssoid, username, nickname, mobile, email, password, headImg, provider, oauthid, tenantcode);
		}

		outputJson(String.valueOf(result), response);

	}

	@RequestMapping("/modify.do")
	public void modify(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String tenantcode = InfoHelper.getTenantCode(request);
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email");
		String sex = request.getParameter("sex");
		String birthday = request.getParameter("birthday");
		String address = request.getParameter("address");
		String uid = request.getParameter("uid");

		Map<String, Object> maps = new HashMap<String, Object>();
		// 检查uid、 手机号、昵称、密码 不为空
		/*if (StringUtils.isBlank(email)) {
			maps.put("msg", "修改资料 接口：参数email为空");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}*/
		/*if (StringUtils.isBlank(nickname)) {
			maps.put("msg", "修改资料 接口：昵称为null或空字符串");
			maps.put("code", "0");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}*/
		//Document mDoc = MemberHelper.getMemberByCondition(tenantcode, "email", email);
		Document mDoc = MemberHelper.getMemberByCondition(tenantcode, "uid_sso", uid);
		if (mDoc == null) {
			/*maps.put("code", 0);
			maps.put("msg", "email未注册会员");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;*/
			maps.put("code", 0);
			maps.put("msg", "会员不存在");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		} else {
			System.out.println("================ MemberSynAdpapter modify, isThirdAccount check ... ");
			if (mManager.isThirdAccount(mDoc)) {
				maps.put("msg", "第三方账户不支持资料修改");
				maps.put("code", "0");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
			if (!StringUtils.isBlank(nickname)) {
				mDoc.set("mNickname", nickname);
			}
			if (!StringUtils.isBlank(sex)) {
				mDoc.set("mSex", Integer.parseInt(sex));
			}
			if (!StringUtils.isBlank(address)) {
				mDoc.set("mAddress", address);
			}

			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				if (!StringUtils.isBlank(birthday)) {
					date = sdf.parse(birthday);
					mDoc.set("mBirthday", date);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				maps.put("msg", "参数birthday格式不正确");
				maps.put("code", "0");
				JSONObject jsonstr = JSONObject.fromObject(maps);
				outputJson(String.valueOf(jsonstr), response);
				return;
			}
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			docManager.save(mDoc);
			maps.put("code", 1);
			maps.put("msg", "success");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
		}
	}

	/**
	 * 按email修改密码
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/updatePassword.do")
	public void updatePassword(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {

		String password = request.getParameter("password");
		String mobile = request.getParameter("mobile");
		String email = request.getParameter("email");
		Map<String, Object> maps = new HashMap<String, Object>();

		if (StringUtils.isBlank(email)) {
			maps.put("code", "0");
			maps.put("msg", "email为空");
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
		if (!ValidateHelper.email(email)) {
			maps.put("code", "0");
			maps.put("msg", "email格式不对");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		}
		String TenantCode = InfoHelper.getTenantCode(request);
		// Document mDoc = MemberHelper.getMemberByMobile(TenantCode, mobile);
		Document mDoc = MemberHelper.getMemberByCondition(TenantCode, "email", email);
		if (mDoc == null) {
			maps.put("code", 0);
			maps.put("msg", "email未注册会员");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
			return;
		} else {
			mDoc.set("mPassword", password);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			docManager.save(mDoc);
			maps.put("code", "1");
			maps.put("msg", "success");
			JSONObject jsonstr = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonstr), response);
		}
	}

	@RequestMapping("/loginByOther.do")
	public void loginByOther(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		int type = Integer.parseInt(request.getParameter("type"));
		String oid = request.getParameter("oid");
		String name = request.getParameter("name");
		String ssoid = request.getParameter("ssoid");
		String headimg = request.getParameter("headImg");

		String tenantcode = InfoHelper.getTenantCode(request);
		Map<String, Object> maps = new HashMap<String, Object>();

		if (StringUtils.isBlank(oid) || StringUtils.isBlank(name)) {
			maps.put("code", "0");
			maps.put("msg", "第三方账号或昵称为空");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}
		if (type > 4 || type <= 0) {// 1 facebook 2 google 3 twitter 4 wechat
			maps.put("code", "0");
			maps.put("msg", "第三方账号类型不对");
			JSONObject jsonres = JSONObject.fromObject(maps);
			outputJson(String.valueOf(jsonres), response);
			return;
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantcode);

		StringBuilder condition = new StringBuilder();
		switch (type) {// 1 facebook 2 google 3 twitter 4 wechat
		case 1:
			condition.append(" fbid = ?  and SYS_DELETEFLAG = 0 ");
			break;
		case 2:
			condition.append(" ggid = ?  and SYS_DELETEFLAG = 0 ");
			break;
		case 3:
			condition.append(" twid = ?  and SYS_DELETEFLAG = 0 ");
			break;
		case 4:
			condition.append(" mWechatId = ?  and SYS_DELETEFLAG = 0 ");
			break;
		default:
			break;
		}
		String[] columns = { "mName", "mMobile", "mEmail", "mHead" };
		Document[] members = docManager.find(docLib.getDocLibID(), condition.toString(), new Object[] { oid }, columns);
		if (members != null && members.length > 0) {
			Document doc = members[0];
			doc.set("mHead", URLDecoder.decode(headimg, "UTF-8"));
			docManager.save(doc);
		} else {
			// 新建一条记录，并设置mName=name
			FlowNode flowNode = DomHelper.getFlowNode(docLib.getDocTypeID());
			Document doc = CollectHelper.newData(docLib, 0, flowNode);

			if (type == 1) {
				doc.set("fbid", oid);
				doc.set("fbName", name);
			} else if (type == 2) {
				doc.set("ggid", oid);
				doc.set("ggName", name);
			} else if (type == 3) {
				doc.set("twid", oid);
				doc.set("twName", name);
			} else if (type == 4) {
				doc.set("mWechatId", oid);
				doc.set("mWechatName", name);
			}
			doc.set("mName", name);
			doc.set("mNickname", name);
			doc.set("uid_sso", ssoid);
			doc.set("mStatus", 1);
			doc.set("mSource", "线上API");
			doc.set("mSource_ID", InfoHelper.getMemberSourceCat("线上API"));
			doc.set("mHead", URLDecoder.decode(headimg, "UTF-8"));
			docManager.save(doc);
		}

		maps.put("code", "1");
		maps.put("msg", "success");
		JSONObject jsonres = JSONObject.fromObject(maps);
		outputJson(String.valueOf(jsonres), response);

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

}
