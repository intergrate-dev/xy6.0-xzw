package com.founder.xy.system.web;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.context.Context;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.ColumnReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.e5.web.BaseController;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.Tab;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;

/**
 * 权限配置：主界面功能项权限
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/system")
public class MainPermission extends BaseController {

	@Autowired
	private PermissionManager permissionManager;
	@Autowired
	private SiteManager siteManager;
	@Autowired
	private ColumnManager colManager;
	
	/** 主界面功能项配置 */
	@RequestMapping(value = "MainPermission.do")
	public ModelAndView mainPermission(HttpServletRequest request)
			throws Exception {
		List<Tab> tabs = ConfigReader.getTabs();

		int roleID = getIntInSession(request, "permissionRoleID");
		Permission[] permission = permissionManager.getPermissions(roleID,
				"MainPermission");

		String resource = permission == null ? "" : permission[0].getResource();

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("tabs", tabs);
		model.put("ids", resource);

		return new ModelAndView("/xy/system/MainPermission", model);
	}

	/**
	 * 角色栏目权限配置 1 先获取有哪些站点，那些渠道 ColumnPermission.do 2, 获取对应站点渠道的栏目树 3
	 * 获取对应角色在栏目树中已有的权限
	 * */
	@RequestMapping(value = "ColumnPermission.do")
	public ModelAndView ColumnPermission(HttpServletRequest request)
			throws Exception {

		int roleID = getIntInSession(request, "permissionRoleID");
		List<Site> sites = siteManager.getSites(DocTypes.SITE.typeID());
		Channel[] chs = ConfigReader.getChannels();

		Map<String, Object> model = new HashMap<>();
		model.put("sites", sites);
		model.put("type", 0);// 默认是设置可操作栏目权限
		model.put("DocLibID", LibHelper.getColumnLibID());
		model.put("roleID", roleID);
		model.put("channels", chs);

		return new ModelAndView("/xy/system/UserColumn", model);
	}

	/**
	 * 取出用户有权限的栏目id 以及父节点id
	 * */
	@RequestMapping(value = "getColPermission.do")
	public void getColPermission(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int roleID,
			@RequestParam int siteID, @RequestParam int type) throws Exception {
		String perType = getPerType(type);//当type大于或等于50 属于源稿栏目分类操作
		Permission[] permission = permissionManager.getPermissions(roleID,
				perType + siteID);
		String resource = permission == null ? "" : permission[0].getResource();
		String parentIDs = null;
		if(type < 50){
			parentIDs = getparentIDs(request, resource);
		}else{
			parentIDs = getOrgparentIDs(request, resource);
		}
		String result = "{\"ids\":\"" + resource + "\",\"parent\":\""
				+ parentIDs + "\"}";
		InfoHelper.outputText(result, response);
	}
	
	private String getparentIDs(HttpServletRequest request, String colIDsStr)
			throws E5Exception {
		int[] colIDs = StringUtils.getIntArray(colIDsStr);
		if (colIDs == null || colIDs.length == 0)
			return "";
		int colLibID = LibHelper.getColumnLibID(request);
		Set<String> ids = new HashSet<>();
		ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
		for (int colID : colIDs) {
			Column col = colReader.get(colLibID, colID);
			if (col != null) {
				String[] caseIDs = col.getCasIDs().split("~");
				ids.addAll(Arrays.asList(caseIDs)
						.subList(0, caseIDs.length - 1));
			}
		}
		if (ids.size() > 0)
			return StringUtils.join(ids.toArray(), ",");
		return "";
	}
	
	/**
	 * 角色栏目权限配置 1 先获取有哪些站点，那些渠道 ColumnPermission.do 2 获取对应角色在栏目树中已有的权限
	 * getColPermission.do 3 保存权限 saveColPermission.do
	 * */
	@RequestMapping(value = "saveColPermission.do")
	public void saveColPermission(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int roleID,
			@RequestParam int siteID, @RequestParam int type,
			@RequestParam String ids, @RequestParam String notExpanded)
			throws Exception {
		String perType = getPerType(type);
		Permission[] permission = permissionManager.getPermissions(roleID,
				perType + siteID);
		String oldIDs = permission == null ? "" : permission[0].getResource();

		if (!StringUtils.isBlank(oldIDs) && !StringUtils.isBlank(notExpanded)) {
			// 取出没被这次选中的旧栏目ID，检查是否是未展开
			int colLibID = LibHelper.getColumnLibID(request);
			if(type >= 50){
				colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),InfoHelper.getTenantCode(request));
			}
			long[] ids2 = filterOldIDs(oldIDs, ids, notExpanded, colLibID);

			if (ids2 != null && ids2.length > 0) {
				if (!StringUtils.isBlank(ids))
					ids += ",";
				ids += StringUtils.join(ids2);
			}
		}
		permissionManager.delete(roleID, perType + siteID);

		if (!StringUtils.isBlank(ids)) {
			Permission p = new Permission(roleID, perType + siteID, ids, 1);
			permissionManager.save(p);
		}

		// 填写系统记录
		StringBuffer info = new StringBuffer();
		info.append("给角色[").append(roleID).append("]设置栏目权限");

		super.reportInfo(request, "栏目权限设置", info.toString());

		InfoHelper.outputText("ok", response);
	}

	/** 主界面功能项配置的保存 */
	@RequestMapping(value = "MainPermissionSubmit.do")
	public String mainPermissionSubmit(HttpServletRequest request)
			throws Exception {
		int roleID = getInt(request, "roleID");
		String resource = get(request, "resource");

		permissionManager.delete(roleID, "MainPermission");
		if (!StringUtils.isBlank(resource)) {
			Permission p = new Permission(roleID, "MainPermission", resource, 1);
			permissionManager.save(p);
		}

		// 填写系统记录
		StringBuffer info = new StringBuffer();
		info.append("给角色[").append(roleID).append("]设置主界面功能权限");

		super.reportInfo(request, "主界面功能权限设置", info.toString());

		return "/e5permission/Submit";
	}

	/** 权限保存（Submit.jsp）后发出消息，通知各服务器一起刷新权限缓存 */
	@RequestMapping(value = "Refresh.do")
	public void refresh(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PublishTrigger.otherData(0, 0, DocIDMsg.TYPE_PERMISSION);

		InfoHelper.outputText("ok", response);
	}

	/**
	 * 角色源稿栏目权限配置
	 */
	@RequestMapping(value = "OrgColumnPermission.do")
	public ModelAndView OrgColumnPermission(HttpServletRequest request)
			throws Exception {

		int roleID = getIntInSession(request, "permissionRoleID");
		List<Site> sites = siteManager.getSites(DocTypes.SITE.typeID());

		Map<String, Object> model = new HashMap<>();
		model.put("sites", sites);
		model.put("type", 0);// 默认是设置可操作哪些源稿栏目的权限
		model.put(
				"DocLibID",
				LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),
						InfoHelper.getTenantCode(request)));
		model.put("roleID", roleID);

		return new ModelAndView("/xy/system/UserOrgColumn", model);
	}

	private String getOrgparentIDs(HttpServletRequest request, String colIDsStr)
			throws E5Exception {
		int[] colIDs = StringUtils.getIntArray(colIDsStr);
		if (colIDs == null || colIDs.length == 0)
			return "";
		int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(),InfoHelper.getTenantCode(request));
		Set<String> ids = new HashSet<>();
		for (int colID : colIDs) {
			Document col = colManager.get(colLibID, colID);
			if (col != null) {
				String[] caseIDs = col.getString("col_cascadeID").split("~");
				ids.addAll(Arrays.asList(caseIDs)
						.subList(0, caseIDs.length - 1));
			}
		}
		if (ids.size() > 0)
			return StringUtils.join(ids.toArray(), ",");
		return "";
	}
	
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response,
			@SuppressWarnings("rawtypes") Map model) throws Exception {
	}

	// 取出没被这次选中的旧栏目ID，找出藏在未展开里面的。
	private long[] filterOldIDs(String oldIDs, String ids, String notExpanded,
			int colLibID) throws E5Exception {

		long[] idArr = StringUtils.getLongArray(ids);
		long[] oldIDArr = StringUtils.getLongArray(oldIDs);
		long[] notExpandedArr = null;
		try {
			notExpandedArr = StringUtils.getLongArray(notExpanded);
		} catch (Exception e) {
		}
		// 取出没被这次选中的旧栏目ID
		List<Long> result = new ArrayList<>();
		for (long oldID : oldIDArr) {
			if (!ArrayUtils.contains(idArr, oldID)
					&& !ArrayUtils.contains(notExpandedArr, oldID)) {
				result.add(oldID);
			}
		}
		if (result.size() == 0)
			return null;

		// 检查是否在未展开节点里

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (int i = result.size() - 1; i >= 0; i--) {
			long oldID = result.get(i);
			Document col = docManager.get(colLibID, oldID);
			if (col != null) {
				long[] path = StringUtils.getLongArray(
						col.getString("col_cascadeID"), "~");
				if (!contains(path, notExpandedArr)) {
					result.remove(i);
				}
			} else {
				result.remove(i);
			}
		}
		return InfoHelper.getLongArray(result);
	}

	// 检查栏目的父路径中是否有未展开的
	private boolean contains(long[] path, long[] notExpandedArr) {
		for (long l : path) {
			if (ArrayUtils.contains(notExpandedArr, l))
				return true;
		}
		return false;
	}

	private String getPerType(int type) {
		switch (type) {
		case 0:
			return "ColumnOpWeb";
		case 1:
			return "ColumnAdminWeb";
		case 4:
			return "ColumnOpApp";
		case 5:
			return "ColumnAdminApp";
		case 50:
		case 51:
			return "OriginalColumn";
		default:
			return "ColumnAdminWeb";
		}

	}
}