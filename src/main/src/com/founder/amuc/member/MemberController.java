package com.founder.amuc.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.commons.BaseHelper;
import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.FormHelper;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.member.input.HttpClientUtil;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocTypeField;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.web.BaseController;

/**
 * 会员操作
 * 
 * @author Hu Yong 2017-3-2
 */
@Controller
@RequestMapping("/amuc/member")
@SuppressWarnings({ "rawtypes" })
public class MemberController extends BaseController {
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String action = get(request, "a");
		if ("update".equals(action)) {
			// 会员修改
			update(request, response, model);
		} else if ("find".equals(action)) {
			// 查找会员：积分添加/扣减时根据姓名查找
			find(request, response, model);
		} else if ("mFields".equals(action)) {
			// 查重规则定义时：取字段（会员属性字段）
			memberFields(request, response, model);
		} else if ("dupRules".equals(action)) {
			// 会员表单：读出查重规则
			dupRules(request, response, model);
		} else if ("checkDup".equals(action)) {
			// 会员表单：按一个查重规则检查是否有重复
			dupCheck(request, response, model);
		} else if ("hasMemberByMobile".equals(action)) {
			// 通过手机号查询是否有该会员，有则返回该会员的信息。新增会员界面变修改页面
			hasMemberByMobile(request, response, model);
		}
	}

	/**
	 * 会员禁用,置标志位为0
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws E5Exception
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/MemberDisable.do")
	private void MemberDisable(HttpServletRequest request, HttpServletResponse response, Map model)
			throws E5Exception, ServletException, IOException {

		String memberIDs = get(request, "DocIDs");
		if (memberIDs == null || memberIDs.length() == 0)
			return;
		String condition = "SYS_DOCUMENTID in ( " + memberIDs + " ) and  SYS_DELETEFLAG=0 ";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, null);
		if (docs != null && docs.length > 0) {
			for (Document doc : docs) {
				doc.set("mStatus", 0);
				docManager.save(doc);
			}
		}
		String url = "../../e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + memberIDs + "&UUID="
				+ get(request, "UUID");
		request.getRequestDispatcher(url).forward(request, response);
	}

	/**
	 * 会员启用,置标志位为1
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws E5Exception
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/MemberEnable.do")
	private void MemberEnable(HttpServletRequest request, HttpServletResponse response, Map model)
			throws E5Exception, ServletException, IOException {

		String memberIDs = get(request, "DocIDs");
		if (memberIDs == null || memberIDs.length() == 0)
			return;
		String condition = "SYS_DOCUMENTID in ( " + memberIDs + " ) and  SYS_DELETEFLAG=0 ";
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, null);
		if (docs != null && docs.length > 0) {
			for (Document doc : docs) {
				doc.set("mStatus", 1);
				docManager.save(doc);
			}
		}
		String url = "../../e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + memberIDs + "&UUID="
				+ get(request, "UUID");
		request.getRequestDispatcher(url).forward(request, response);
	}

	/**
	 * 通过手机号查询是否有该会员，有则返回该会员的信息。新增会员界面变修改页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	private void hasMemberByMobile(HttpServletRequest request, HttpServletResponse response, Map model)
			throws Exception {

		String mMobile = get(request, "mMobile");
		Map<String, Object> maps = new HashMap<String, Object>();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));

		DocTypeReader docTypeReader = (DocTypeReader) Context.getBean(DocTypeReader.class);
		DocTypeField[] fields = docTypeReader.getFields(docLib.getDocTypeID());// 取出会员的所有字段

		Document[] members = null;
		if (!"".equals(mMobile)) {
			members = docManager.find(docLib.getDocLibID(), "mMobile = ?  and SYS_DELETEFLAG=0",
					new Object[] { mMobile });
			if (members != null && members.length > 0) {
				for (DocTypeField field : fields) {
					if (members[0].get(field.getColumnCode()) != null) {
						if ("mBirthday".equals(field.getColumnCode())) {
							maps.put(field.getColumnCode(), members[0].getDate(field.getColumnCode()).toString());
							continue;
						}
						maps.put(field.getColumnCode(), members[0].get(field.getColumnCode()));
					}

				}
			}
			JSONArray jsonarray = JSONArray.fromObject(maps);
			System.out.println(jsonarray.toString());
			output(jsonarray.toString(), response);
		}

	}

	/**
	 * 会员注销操作
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("/Mcancel.do")
	private void Mcancel(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String memberIDs = get(request, "DocIDs");
		if (memberIDs == null || memberIDs.length() == 0)
			return;

		DBSession conn = null;
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);


		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLib.getDocLibID(),
				" SYS_DOCUMENTID in ( " + memberIDs + " )  and SYS_DELETEFLAG = 0 ", null);
		if (docs != null && docs.length > 0) {
			for (Document doc : docs) {
				doc.set("SYS_DELETEFLAG", 1);
				docManager.save(doc);
				// 调用sso同步接口
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid_sso", doc.getString("uid_sso")));
				String res = new HttpClientUtil().callSsoAPI("/api/syn/delete", params);
				JSONObject resJson = JSONObject.fromObject(res);
				System.out.println(resJson.toString());
			}
		}

		String url = "../../e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + memberIDs + "&UUID="
				+ get(request, "UUID");
		request.getRequestDispatcher(url).forward(request, response);
	}

	// 会员名称模糊查询：用于会员积分手工添加/扣减时表单的自动提示
	@RequestMapping("/MemberFind.do")
	private void find(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		// 取得租户代号
		String tenantCode = InfoHelper.getTenantCode(request);
		String name = get(request, "q"); // 固定参数名，q
		String siteId = request.getParameter("siteID");
		int siteID = 1;
		if(!StringUtils.isBlank(siteId)){
			siteID = Integer.parseInt(siteId);
		}

		StringBuilder result = new StringBuilder();
		result.append("[");
		int i = 0;
		int maxCount = 10;

		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);
		String sql = "select SYS_DOCUMENTID,mName from " + docLib.getDocLibTable()
				+ " where mName like ? and SYS_DELETEFLAG=0 and m_siteID = " + siteID;

		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession(docLib.getDsID());
			// 按数据库类型获得不同的查询个数限制语句
			sql = conn.getDialect().getLimitString(sql, 0, maxCount);

			rs = conn.executeQuery(sql, new Object[] { name + "%" });
			while (rs.next()) {
				if (i++ > 0)
					result.append(",");
				result.append("{key:\"").append(rs.getLong("SYS_DOCUMENTID")).append("\",value:\"")
						.append(rs.getString("mName")).append("（id=").append(rs.getLong("SYS_DOCUMENTID"))
						.append("）\"}");
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		result.append("]");

		output(result.toString(), response);
	}

	// 会员修改
	private void update(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		// 取得租户代号
		// String tenantCode = InfoHelper.getTenantCode(request);
		long memberID = getInt(request, "DocID");
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));
		Document doc = docManager.get(docLib.getDocLibID(), memberID);
		FormHelper.assembleDoc(doc, request);
		docManager.save(doc);

		String url = "../../e5workspace/after.do?DocLibID=" + docLib.getDocLibID() + "&DocIDs=" + memberID + "&UUID="
				+ get(request, "UUID");
		request.getRequestDispatcher(url).forward(request, response);
	}

	/**
	 * 查重规则定义时、导入会员前自动对应时，读会员属性字段 去掉一些属性：积分等
	 */
	private void memberFields(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		MemberReader memberReader = new MemberReader();
		List<Pair> result = memberReader.getFields();

		output(JSONArray.fromObject(result).toString(), response);
	}

	// 会员表单：读出查重规则
	private void dupRules(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String tenantCode = InfoHelper.getTenantCode(request);
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_DUPLICATIONRULE, tenantCode);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] rules = docManager.find(docLib.getDocLibID(), "SYS_DELETEFLAG=0", null);

		List<Pair> result = new ArrayList<Pair>();
		if (rules != null && rules.length > 0) {
			for (Document rule : rules) {
				result.add(new Pair(rule.getString("drField"), rule.getString("drFieldName")));
			}
		}
		output(JSONArray.fromObject(result).toString(), response);
	}

	// 会员表单：提交前按一个查重规则检查是否有重复
	private void dupCheck(HttpServletRequest request, HttpServletResponse response, Map model) throws Exception {
		String[] fields = get(request, "fields").split(",");
		String[] values = get(request, "values").split("    ");// 分隔符4个空格，减少值中含该字符的可能性

		StringBuffer condition = new StringBuffer();
		for (int i = 0; i < fields.length; i++) {
			condition.append(fields[i]).append("=? and ");
		}
		condition.append("SYS_DOCUMENTID<>? and SYS_DELETEFLAG=0");

		String tenantCode = InfoHelper.getTenantCode(request);
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, tenantCode);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] dups = docManager.find(docLib.getDocLibID(), condition.toString(), values);

		if (dups.length > 0)
			output("1", response); // 有重复的会员
		else
			output("0", response);
	}
}
