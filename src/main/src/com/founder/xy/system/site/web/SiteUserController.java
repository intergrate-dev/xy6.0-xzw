package com.founder.xy.system.site.web;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.org.*;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.set.web.ResDir;
import com.founder.xy.statistics.util.FileUtil;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteUserManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 站点用户的相关操作
 */
@Controller
@RequestMapping("/xy/user")
public class SiteUserController extends AbstractResourcer{
	@Autowired
	private SiteUserManager userManager;
	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private SiteManager siteManager;
	@Autowired
	private UserFrozenManager userFrozenManager;
	/** 用户设置栏目（可管理、可操作）的主界面 */
	@RequestMapping(value = "Columns.do")
	public ModelAndView columns(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("DocIDs") int userID, String DocLibID, String UUID) throws Exception {
		
		int userLibID = LibHelper.getUserExtLibID(request);
		
		List<Site> sites = userManager.getUserSites(userLibID, userID);
		
        Channel[] chs = ConfigReader.getChannels();
		
		Map<String, Object> model = new HashMap<String, Object>();
		boolean groupPower = "是".equals(InfoHelper.getConfig("其它", "是否启用分组权限管理"));
		model.put("groupPower",groupPower);
		model.put("sites", sites);
		model.put("userID", userID);
		model.put("type", 0);//默认是设置可操作栏目权限
		model.put("DocLibID", DocLibID);
		model.put("DocIDs", userID);
		model.put("UUID", UUID);
		model.put("channels", chs);

		return new ModelAndView("/xy/system/UserColumn", model);
	}

	/** 读用户关联信息 */
	@RequestMapping(value = "Rel.do")
	public void rel(HttpServletRequest request, HttpServletResponse response, @RequestParam int userID,
			@RequestParam int siteID, @RequestParam int type) throws Exception {
		
		int userRelLibID = LibHelper.getUserRelLibID(request);
		String result = userManager.getRelated(userRelLibID, userID, siteID, type);

		InfoHelper.outputText(result, response);
	}

	/** 读用户关联信息 取出用户有权限的栏目id 以及父节点id
	 *  */
	@RequestMapping(value = "RelCol.do")
	public void RelCol(HttpServletRequest request, HttpServletResponse response, @RequestParam int userID,
					@RequestParam int siteID, @RequestParam int type) throws Exception {

		int userRelLibID = LibHelper.getUserRelLibID(request);
		String colIDs = userManager.getRelated(userRelLibID, userID, siteID, type);
		String parentIDs = getparentIDs(request,colIDs);
		String result = "{\"ids\":\"" + colIDs +"\",\"parent\":\""+parentIDs+"\"}";
		InfoHelper.outputText(result, response);
	}

	private String getparentIDs(HttpServletRequest request, String colIDsStr) throws E5Exception {
		int[] colIDs = StringUtils.getIntArray(colIDsStr);
		int colLibID = LibHelper.getColumnLibID(request);
		Set<String> ids = new HashSet<>();
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		if(colIDs != null && colIDs.length > 0){
			for (int colID : colIDs){
				Column col = colReader.get(colLibID,colID);
				if(col != null) {
					String[] caseIDs = col.getCasIDs().split("~");
					ids.addAll(Arrays.asList(caseIDs).subList(0, caseIDs.length - 1));
				}
			}
		}
		if(ids.size()>0)
			return StringUtils.join(ids.toArray(),",");
		return "";
	}


	/** 保存用户关联信息 */
	@RequestMapping(value = "RelSave.do")
	public void relSave(HttpServletRequest request, HttpServletResponse response, @RequestParam int userID,
			@RequestParam int siteID, @RequestParam int type, @RequestParam String ids)
			throws Exception {

		int userRelLibID = LibHelper.getUserRelLibID(request);
		userManager.saveRelated(userRelLibID, userID, siteID, type, ids);

		InfoHelper.outputText("ok", response);
	}

	/** 栏目权限保存，需要对处理未展开的栏目 */
	@RequestMapping(value = "RelColumnSave.do")
	public void relColumnSave(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int userID, @RequestParam int siteID, @RequestParam int type, 
			@RequestParam String ids, @RequestParam String notExpanded)
			throws Exception {
		
		int userRelLibID = LibHelper.getUserRelLibID(request);
		String oldIDs = userManager.getRelated(userRelLibID, userID, siteID, type);
		if (!StringUtils.isBlank(oldIDs) && !StringUtils.isBlank(notExpanded)) {
			//取出没被这次选中的旧栏目ID，检查是否是未展开
			int colLibID = LibHelper.getColumnLibID(request);
			long[] ids2 = filterOldIDs(oldIDs, ids, notExpanded, colLibID);
			
			if (ids2 != null && ids2.length > 0) {
				if (!StringUtils.isBlank(ids)) ids += ",";
				ids += StringUtils.join(ids2);
			}
		}
		
		userManager.saveRelated(userRelLibID, userID, siteID, type, ids);

		InfoHelper.outputText("ok", response);
	}
	/** 创建用户表单中，选择部门 */
	@RequestMapping(value = "Org.do")
	public void orgs(HttpServletRequest request, HttpServletResponse response) throws Exception {

		//根据用户表的库ID，得到租户代号，找到租户对应的根机构ID
		int orgID = rootOrgID(request);
		
		StringBuilder result = new StringBuilder();
		result.append("[");

		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Org[] orgs = orgID == 0 ? null : orgManger.getChildOrgs(orgID);
		if (orgs != null) {
			for (Org org : orgs) {
				if (result.length() > 1) {
					result.append(",");
				}
				result.append("{\"key\":\"").append(org.getOrgID()).append("\",\"value\":\"")
						.append(filter4Json(org.getName())).append("\"}");
			}
		}
		result.append("]");

		InfoHelper.outputJson(result.toString(), response);
	}

	/** 创建用户表单中，选择角色 */
	@RequestMapping(value = "Role.do")
	public void roles(HttpServletRequest request, HttpServletResponse response) throws Exception {

		//根据用户表的库ID，得到租户代号，找到租户对应的根机构ID
		int orgID = rootOrgID(request);
		
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Role[] roles = orgID == 0 ? null : orgManger.getRoles(orgID);
		if (roles != null) {
			for (Role role : roles) {
				if (result.length() > 1) {
					result.append(",");
				}
				result.append("{\"key\":\"").append(role.getRoleID()).append("\",\"value\":\"")
						.append(filter4Json(role.getRoleName())).append("\"}");
			}
		}
		result.append("]");

		InfoHelper.outputJson(result.toString(), response);
	}

	/** 创建用户表单中，选择站点 */
	@RequestMapping(value = "Site.do")
	public void sites(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int userLibID = WebUtil.getInt(request, "DocLibID", 0);
		if (userLibID == 0) return;
		
		String tenantCode = LibHelper.getTenantCodeByLib(userLibID);
		
		StringBuilder result = new StringBuilder();
		result.append("[");
		Document[] sites = siteManager.getSites(tenantCode);
		
		if (sites != null) {
			for (Document site : sites) {
				if (result.length() > 1) {
					result.append(",");
				}
				result.append("{\"key\":\"").append(site.getDocID()).append("\",\"value\":\"")
						.append(filter4Json(site.getString("site_name"))).append("\"}");
			}
		}
		result.append("]");

		InfoHelper.outputJson(result.toString(), response);
	}

	/** 保存用户，不仅保存用户扩展信息，也要保存E5平台的用户 */
	@RequestMapping(value = "FormSubmit.do")
	public String formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		int docLibID = docLibID(request);
		long docID = docID(request);
		int siteID = WebUtil.getInt(request, "u_siteID", 0);
		
		//对于新建的情况，判断授权
		if (docID == 0) {
			int userCount = userManager.getUserCounts(docLibID);
			if (userCount >= SecurityHelper.getUserCount()) {
				model.put("error", "已超用户数");
				return "/xy/site/error";
			}
		}
		
		//检查文件名的合法性
		String filePath =  request.getParameter("u_icon");
		if (!isImgFile(filePath)){
			model.put("error", "对不起，请上传jpg,gif,png格式");
			return "/xy/site/error";
		}
		
		//检查站点的资源目录是否已配置
		ResDir siteDir = getSiteDirs(filePath, docLibID, docID, siteID, "u_icon");
		if (siteDir.noSiteDir) {
			model.put("error", "请先检查站点的资源目录设置");
			return "/xy/site/error";
		}
		
		//保存用户扩展信息表 userext
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);

		String UUID = request.getParameter("UUID");
		Pair changed = null;
		try {
			changed = formSaver.handleChanged(request);
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "redirect:e5workspace/after.do?UUID=" + UUID;
		}
		
		int userLibID = WebUtil.getInt(request, "DocLibID", 0);
		int userID = Integer.parseInt(changed.getKey());
		
		//去掉用户扩展表中的明文密码
		if (!StringUtils.isBlank(request.getParameter("u_password"))) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(userLibID, userID);
			doc.set("u_password", "");
			docManager.save(doc);
		}
		//保存E5用户信息
		createUser(request, userLibID, userID);
		
		//创建修改站点用户后，刷新站点用户缓存、机构用户缓存
		PublishTrigger.otherData(userLibID, userID, DocIDMsg.TYPE_ORGUSER);
		
		//发布头像到外网
		pubAndWriteUrl(siteDir, filePath, docLibID, userID, "u_iconUrl");
		
		return "redirect:../../e5workspace/after.do?DocIDs=" + userID + "&UUID=" + UUID;
	}
	/** 删除用户，不仅删除用户扩展信息，也要删除E5平台的用户 */
	@RequestMapping(value = "Delete.do")
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int[] userIDs = StringUtils.getIntArray(request.getParameter("DocIDs"));
		int currentUser = ProcHelper.getUserID(request);
		
		if (ArrayUtils.contains(userIDs, currentUser)) {
			return "redirect:../../e5workspace/after.do?UUID=" + request.getParameter("UUID")
					+ "&Info=" + URLEncoder.encode("不允许删除自己", "UTF-8");
		}
		int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), InfoHelper.getTenantCode(request));
		
		for (int userID : userIDs) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			docManager.delete(LibHelper.getUserExtLibID(), userID);

			UserManager userManager = (UserManager) Context.getBean(UserManager.class);
			userManager.delete(userID);
		}
		PublishTrigger.otherData(userLibID, 0, DocIDMsg.TYPE_ORGUSER);
		
		return "redirect:../../e5workspace/after.do?DocIDs=" + request.getParameter("DocIDs") + "&UUID="
				+ request.getParameter("UUID");
	}
	
	/**
	 * 检查部门和角色是否重名
	 */
	@RequestMapping(value = "CheckOrgRole.do")
	public void checkOrgRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int parentID = WebUtil.getInt(request, "parentID", 0);
		int type = WebUtil.getInt(request, "type", 0);
		int id = WebUtil.getInt(request, "id", 0);
		String name = WebUtil.get(request, "name");
		
		String found = "0";
		if (type == 0) {
			OrgManager orgManager = (OrgManager)Context.getBean(OrgManager.class);
			Org[] orgs = orgManager.getOrgsByName(name);
			if (orgs != null) {
				for (int i = 0; i < orgs.length; i++) {
					if (orgs[i].getParentID() == parentID && orgs[i].getOrgID() != id) {
						found = "1";
						break;
					}
				}
			}
		} else {
			RoleManager roleManager = (RoleManager)Context.getBean(RoleManager.class);
			Role[] roles = roleManager.getRolesByName(name);
			if (roles != null) {
				for (int i = 0; i < roles.length; i++) {
					if (roles[i].getOrgID() == parentID && roles[i].getRoleID() != id) {
						found = "1";
						break;
					}
				}
			}
		}
		InfoHelper.outputText(found, response);
	}
	
	/** 用户表单中，显示口令 */
	@RequestMapping(value = "Pwd.do", method = RequestMethod.POST)
	public void getPwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = WebUtil.getInt(request, "id", 0);
		UserManager userManager = (UserManager) Context.getBean(UserManager.class);
		User user = userManager.getUserByID(id);
		
		if (user != null)
			InfoHelper.outputText(user.getUserPassword(), response);
	}

	/** 通讯员表单中，显示对应用户列表 */
	@RequestMapping(value = "Users.do")
	public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String tenantCode = InfoHelper.getTenantCode(request);
		int userLibID = LibHelper.getLibID(DocTypes.USEREXT.typeID(), tenantCode);
		
		Document[] users = userManager.getUsers(userLibID, siteID);
		JSONArray result = new JSONArray();
		for (Document user : users) {
			JSONObject one = new JSONObject();
			one.put("key", user.getDocID());
			one.put("value", user.getString("u_name"));
			
			result.add(one);
		}
		
		InfoHelper.outputJson(result.toString(), response);
	}

	/** 通讯员表单中，显示单位自动搜索列表 */
	@RequestMapping(value = "Corporations.do")
	public void getCorporations(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String query = WebUtil.get(request, "q");
		String tenantCode = InfoHelper.getTenantCode(request);
		int userLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), tenantCode);
		
		Document[] users = userManager.getCorporations(userLibID, siteID, query);
		JSONArray result = new JSONArray();
		for (Document user : users) {
			JSONObject one = new JSONObject();
			one.put("value", user.getString("corp_name"));
			one.put("key", user.getDocID());
			
			result.add(one);
		}
		
		InfoHelper.outputJson(result.toString(), response);
	}

	/** 根据单位名称取站点下单位，用于通讯员校验单位名称是否存在 */
	@RequestMapping(value = "Corporation.do")
	public void getCorporation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String corpName = WebUtil.get(request, "corpName");
		String tenantCode = InfoHelper.getTenantCode(request);
		int userLibID = LibHelper.getLibID(DocTypes.CORPORATION.typeID(), tenantCode);
		
		Document[] users = userManager.getCorporation(userLibID, siteID, corpName);
		JSONArray result = new JSONArray();
		//如无此单位 则创建
		if(users.length==0){
			Long corpId = this.createCorp(request);
			//封装返回键值
			JSONObject one = new JSONObject();
			one.put("value", corpName);
			one.put("key", corpId);
			result.add(one);
		}else{
			for (Document user : users) {
				JSONObject one = new JSONObject();
				one.put("value", user.getString("corp_name"));
				one.put("key", user.getDocID());
				result.add(one);
			}
		}
		InfoHelper.outputJson(result.toString(), response);
	}
	
	/** 用户管理中展示的部门树 */
	@RequestMapping(value = "OrgTree.do")
	public void orgTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int orgID = WebUtil.getInt(request, "parentID", 0);
		if (orgID == 0) {
			//指定带部门管理员权限，则取当前用户所在的部门树
			int auth = WebUtil.getInt(request, "auth", 0);
			if (auth == 1) {
				orgTreeAuth(request, response);
				return;
			}
			
			//根据用户表的库ID，得到租户代号，找到租户对应的根机构ID
			orgID = rootOrgID(request);
		}
		JSONArray result = getSubOrgs(orgID);
		InfoHelper.outputJson(result.toString(), response);
	}

	private void orgTreeAuth(HttpServletRequest request,
			HttpServletResponse response) throws E5Exception {
		//指定带部门管理员权限，则取当前用户所在的部门
		int userID = ProcHelper.getUserID(request);
		int userLibID = LibHelper.getUserExtLibID(request);
		Document user = userManager.getUser(userLibID, userID);
		int orgID = user.getInt("u_orgID");
		
		JSONArray children = getSubOrgs(orgID);

		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Org org = orgManger.get(orgID);
		JSONObject json = new JSONObject();
		json.put("id", org.getOrgID());
		json.put("name", org.getName());
		json.put("title", org.getName() + " [" + org.getOrgID() + "]");
		json.put("icon", "../../images/org.gif");

		if (!children.isEmpty()) {
			json.put("isParent", "true");//有子节点，可展开
			json.put("children", children);
		}
		
		JSONArray result = new JSONArray();
		result.add(json);
		InfoHelper.outputJson(result.toString(), response);
	}

	private JSONArray getSubOrgs(int orgID) throws E5Exception {
		JSONArray result = new JSONArray();

		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Org[] orgs = orgID == 0 ? null : orgManger.getNextChildOrgs(orgID);
		if (orgs != null) {
			for (Org org : orgs) {
				JSONObject json = new JSONObject();
				json.put("id", org.getOrgID());
				json.put("name", org.getName());
				json.put("title", org.getName() + " [" + org.getOrgID() + "]");
				json.put("icon", "../../images/org.gif");

				if (orgManger.getNextChildOrgs(org.getOrgID()) != null)
					json.put("isParent", "true");//有子节点，可展开
				result.add(json);
			}
		}
		return result;
	}
	/** 创建用户表单中，部门名称 */
	@RequestMapping(value = "OrgName.do")
	public void orgName(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int orgID = WebUtil.getInt(request, "orgID", 0);
		
		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Org org = orgManger.get(orgID);
		if (org != null) {
			InfoHelper.outputText(org.getName(), response);
		}
	}

	private Long createCorp(HttpServletRequest request)throws Exception {
		//加新的单位
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docTypeID = DocTypes.CORPORATION.typeID();
		int docLibID = LibHelper.getLibID(docTypeID, Tenant.DEFAULTCODE);
		long corpID = EUID.getID("DocID" + docTypeID);
		
		Document doc = docManager.newDocument(docLibID, corpID);
		ProcHelper.initDoc(doc);
		
		doc.set("corp_name", WebUtil.get(request, "corpName"));
		doc.set("corp_siteID", WebUtil.getInt(request, "siteID", 0));
		//保存
		docManager.save(doc);
		return corpID;
	}
	private void createUser(HttpServletRequest request, int userLibID, int userID) throws E5Exception {
		String u_userCode = request.getParameter("u_code");
		String u_userName = request.getParameter("u_name");
		String u_userPassword = request.getParameter("u_password");
		String u_isAdmin = request.getParameter("u_isAdmin");
	
		boolean isNew = false;
		UserManager userManager = (UserManager) Context.getBean(UserManager.class);
		User user = userManager.getUserByID(userID);
		if (user == null) {
			isNew = true;
			user = new User();
			user.setUserID(userID);
			user.setUserCode(u_userCode);
			user.setOrgID(0); //设机构为0，使在部门角色树上不会显示出用户
			
			String tenantCode = LibHelper.getTenantCodeByLib(userLibID);
			user.setProperty1(tenantCode); //扩展字段1：租户代号
		}
		
		StringBuilder stars = new StringBuilder();
		int pwdlength=u_userPassword.length();
		for(int i=0;i<pwdlength;i++){
			stars.append("*");
		}
		if(!u_userPassword.equals(stars.toString())){
			user.setUserPassword(u_userPassword);
		}
		
		user.setUserName(u_userName);
		user.setProperty2(u_isAdmin);//扩展字段2：是否管理员
		
		if (isNew) {
			userManager.create(user);
		} else {
			userManager.update(user);
		}
		
	}

	//转换json非法符号
	private String filter4Json(String value) {
		value = value.replace("\r", "\\r");
		value = value.replace("\n", "\\n");
		value = value.replace("\"", "\\\"");
		return value;
	}
	private int rootOrgID(HttpServletRequest request) throws Exception {
		//从session中得到租户
		Tenant tenant = (Tenant)request.getSession().getAttribute(Tenant.SESSIONNAME);
		if (tenant == null) {
			//根据用户表的库ID，得到租户代号，找到租户对应的根机构ID
			int userLibID = WebUtil.getInt(request, "DocLibID", 0);
			if (userLibID == 0) return 0;
			
			String code = LibHelper.getTenantCodeByLib(userLibID);
			
			tenant = tenantManager.get(code);
		}
		return tenant.getOrgID();
	}

	//取出没被这次选中的旧栏目ID，找出藏在未展开里面的。
	private long[] filterOldIDs(String oldIDs, String ids, String notExpanded, int colLibID) throws E5Exception {
		
		long[] idArr = StringUtils.getLongArray(ids);
		long[] oldIDArr = StringUtils.getLongArray(oldIDs);
		long[] notExpandedArr = null;
		try {
			notExpandedArr = StringUtils.getLongArray(notExpanded);
		} catch (Exception e) {
		}
		//取出没被这次选中的旧栏目ID
		List<Long> result = new ArrayList<>();
		for (long oldID : oldIDArr) {
			if (!ArrayUtils.contains(idArr, oldID)&&!ArrayUtils.contains(notExpandedArr, oldID)) {
				result.add(oldID);
			}
		}
		if (result.size() == 0) return null;
		
		//检查是否在未展开节点里

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (int i = result.size() - 1; i >= 0; i--) {
			long oldID = result.get(i);
			Document col = docManager.get(colLibID, oldID);
			if (col != null ){
				long[] path = StringUtils.getLongArray(col.getString("col_cascadeID"), "~");
				if (!contains(path, notExpandedArr)) {
					result.remove(i);
				}
			} else {
				result.remove(i);
			}
		}
		return InfoHelper.getLongArray(result);
	}

	//检查栏目的父路径中是否有未展开的
	private boolean contains(long[] path, long[] notExpandedArr) {
		for (long l : path) {
			if (ArrayUtils.contains(notExpandedArr, l))
				return true;
		}
		return false;
	}
	
	//添加一个栏目到我的收藏
    @RequestMapping(value = "RelAdd.do")
    public void RelAdd(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam int siteID, @RequestParam int type, @RequestParam long id){
    	boolean flag=true;
    	try{
    		int userID = ProcHelper.getUserID(request);
    		int userRelLibID = LibHelper.getUserRelLibID(request);
    		userManager.addRelated(userRelLibID, userID, siteID,id, type);
    		//刷新缓存
    		userManager.cacheUserRelTrigger(userRelLibID);
    	}catch(Exception e){
    		flag = false;
    	}
    	if(flag){
    		InfoHelper.outputText("ok", response);
    	}else{
    		InfoHelper.outputText("!ok", response);
    	}
	}	
	
	//从我的收藏中删除一个栏目
    @RequestMapping(value = "RelDel.do")
	public void RelDel(HttpServletRequest request, HttpServletResponse response,
							@RequestParam int siteID, @RequestParam int type, @RequestParam long id){
			 
    	boolean flag=true;
    	try{
    		int userID = ProcHelper.getUserID(request);
    		int userRelLibID = LibHelper.getUserRelLibID(request);
    		String oldIDsStr = userManager.getRelated(userRelLibID, userID, siteID, type);
    		long[] oldIDs = StringUtils.getLongArray(oldIDsStr);
    		
    		if (oldIDs != null && oldIDs.length > 0) {
    			Long[] newIDs = new Long[oldIDs.length-1];
    			for(int i =0,j=0;i<oldIDs.length&&j<newIDs.length;i++){
    				if(oldIDs[i]!=id){
    					newIDs[j++] = oldIDs[i];
    				}
    			}
    			userManager.saveRelated(userRelLibID, userID, siteID, type, StringUtils.join(newIDs));
				userManager.cacheUserRelTrigger(userRelLibID);
    		}
    	}catch(Exception e){
    		flag = false;
    	}
        if(flag){
        	InfoHelper.outputText("ok", response);
        }else{
        	InfoHelper.outputText("!ok", response);
        }

	}
    
    @RequestMapping(value="unFronzenUser.do")
	public String unFrozenBatman(HttpServletRequest request,HttpServletResponse response) throws E5Exception, UnsupportedEncodingException{
		
		int docID = Integer.valueOf(request.getParameter("DocIDs"));
		boolean frozen = userFrozenManager.isFrozen(docID);
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
		if(frozen){
			userFrozenManager.validationSucceed(docID);
			//调用after.do进行后处理：改变流程状态、解锁、刷新列表
	        String redirectUrl = url +  "&Info=" + URLEncoder.encode("解冻成功! ", "UTF-8");
	        return "redirect:"+redirectUrl;
		}else{
		    String redirectUrl = url + "&Info=" + URLEncoder.encode("解冻失败，可能该用户未曾被冻结! ", "UTF-8");
		    return "redirect:"+redirectUrl;
		}
		
	}

	/**
	 * 导出用户角色信息
	 * @param request
	 * @param response
	 * @return
	 * @throws E5Exception
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="exportUserRoleInfo.do")
	public void exportUserRoleInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		int userLibID = LibHelper.getUserExtLibID(request);
		//1 取所有站点
		String tenantCode = LibHelper.getTenantCodeByLib(userLibID);
		HashMap<Long,String> siteID_siteName = getAllsites(tenantCode);
		//2 取所有角色
		int orgID = rootOrgID(request);
		HashMap<Integer,String> roleID_roleName = getAllRoles(orgID);
		//3 取所有用户

		String fileName = "userRoles.csv" + System.currentTimeMillis();
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		userManager.getAllUser(userLibID,siteID_siteName,roleID_roleName,fileName,filePath);
		//下载CSV文件
		FileUtil.downLoadCSVFile(response, fileName, filePath);
		//删除生成的CSV文件
		FileUtil.deleteFile(fileName, filePath);

	}

	private HashMap<Integer,String> getAllRoles(int orgID) throws E5Exception {
		//根据用户表的库ID，得到租户代号，找到租户对应的根机构ID
		HashMap<Integer,String> roleID_roleName = new HashMap<>();

		OrgManager orgManger = (OrgManager) Context.getBean(OrgManager.class);
		Role[] roles = orgID == 0 ? null : orgManger.getRoles(orgID);
		if (roles != null) {
			for (Role role : roles) {
				roleID_roleName.put(role.getRoleID(),role.getRoleName());
			}
		}
		return roleID_roleName;

	}

	private HashMap<Long,String> getAllsites(String tenantCode) throws E5Exception {
		HashMap<Long,String> siteID_siteName = new HashMap<>();
		Document[] sites = siteManager.getSites(tenantCode);
		for (Document site : sites) {
			siteID_siteName.put(site.getDocID(),site.getString("site_name"));
		}
		return siteID_siteName;
	}

	//用户权限复制
	@RequestMapping(value = "permissionCopy.do")
	public void permissionCopy(HttpServletRequest request, HttpServletResponse response,
							    @RequestParam long srcID, @RequestParam String destIDs) throws E5Exception {
		Boolean flag = userManager.permissionCopy(request, srcID, destIDs);
		if(flag){
			InfoHelper.outputText("ok", response);
		}else{
			InfoHelper.outputText("!ok", response);
		}
	}



}
