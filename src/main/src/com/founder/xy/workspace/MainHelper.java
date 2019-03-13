package com.founder.xy.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.DomInfo;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.SubTab;
import com.founder.xy.config.Tab;
import com.founder.xy.config.TabHelper;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteUserReader;

public class MainHelper {
	/**
	 * 读siteID，若传入的是无权限站点，则改指向有权限站点列表的第一个。
	 * 防止客户端直接输入的形式访问无权限站点
	 * @param request
	 */
	public static int getSiteEnable(HttpServletRequest request) {
		int siteID = WebUtil.getInt(request, "siteID", 0);
		return getSiteEnable(request, siteID);
	}
	
	public static int getSiteEnable(HttpServletRequest request, int siteID) {
		//读有权限的站点
		
		List<Site> sites = getSites(request);
		if (sites == null || sites.size() == 0)
			return 0;
		
		//检验当前站点是否有权限，防止url输入方式访问
		if (!siteEnable(sites, siteID))
			siteID = sites.get(0).getId();
		
		return siteID;
	}
	
	/**
	 * 取有权限的站点，若是租户管理员，则取租户下的所有站点
	 * @param request
	 * @return
	 */
	public static List<Site> getSites(HttpServletRequest request){
		int userID = ProcHelper.getUserID(request);
		
		UserReader uReader = (UserReader)Context.getBean(UserReader.class);
		try {
			User curUser = uReader.getUserByID(userID);

			//若是管理员，则不判断权限，取出租户下的所有站点
			if ("1".equals(curUser.getProperty3())) {
				String tenantCode = curUser.getProperty1();
				
				SiteManager siteManager = (SiteManager)Context.getBean("siteManager");
				Document[] docs = siteManager.getSites(tenantCode);
				List<Site> sites = new ArrayList<Site>();
				if (docs != null) {
					for (Document doc : docs) {
						sites.add(new Site(doc.getString("site_name"), (int)doc.getDocID()));
					}
				}
				return sites;
			} else {
				int userLibID = LibHelper.getUserExtLibID(request);
				
				SiteUserReader siteUserReader = (SiteUserReader)Context.getBean("siteUserReader");
				return siteUserReader.getSites(userLibID, userID);
			}
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 判断一个站点是否可访问。
	 * 防止客户端直接输入的形式访问无权限站点
	 */
	public static boolean siteEnable(List<Site> sites, int siteID) {
		for (Site site : sites) {
			if (site.getId() == siteID) return true;
		}
		return false;
	}

	/**
	 * 主界面点击菜单项时，根据参数中的t找到菜单（判断权限，屏蔽非法访问），
	 * 然后准备好DomInfo参数（文档库ID、ruleFormula等）
	 * @param request
	 * @return 返回model，包含subTab/domInfo/siteID
	 */
	public static Map<String, Object> fillMainModel(HttpServletRequest request)
			throws Exception {
		// 防止非法访问
		SubTab subTab = accessSubTab(request);
		if (subTab == null) {
			// subTab = defaultSubTab(tabs);
			return null;
		}

		int siteID = MainHelper.getSiteEnable(request);

		DomInfo domInfo = getDomInfo(request, subTab);
		// 规则公式若有@SITE@，则替换成实际站点ID
		if (domInfo.getRule() != null) {
			domInfo.setRule(domInfo.getRule().replaceAll("@SITE@",
					String.valueOf(siteID)));
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("subTab", subTab);
		model.put("domInfo", domInfo);
		model.put("siteID", siteID); // 当前站点ID
		return model;
	}

	/**
	 * 主界面点击菜单项时，根据参数中的t找到菜单（判断权限，屏蔽非法访问）
	 * @param request
	 * @return
	 */
	public static SubTab accessSubTab(HttpServletRequest request) {
		int roleID = ProcHelper.getRoleID(request);

		// 重新判断一次权限，防止非法访问
		List<Tab> tabs = getRoleTabs(roleID);
		if (tabs == null) {
			return null;
		}

		String id = WebUtil.get(request, "t");
		return getSubTab(tabs, id);
	}

	/**
	 * 给定Tab组，从中找到符合id的第二层TAB
	 */
	public static SubTab getSubTab(List<Tab> tabs, String id) {
		if (id == null)
			return null;

		for (Tab tab : tabs) {
			if (tab.getChildren() == null)
				continue;
			for (SubTab subTab : tab.getChildren()) {
				if (subTab.getId().equals(id)) {
					return subTab;
				} else if (subTab.getChildrenCount() > 0) {
					for (SubTab thirdTab : subTab.getChildren()) {
						if (thirdTab.getId().equals(id))
							// 返回第二层TAB
							return subTab;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据角色权限，取出可见的TAB
	 * 
	 * @param roleID
	 * @return
	 */
	public static List<Tab> getRoleTabs(int roleID) {
		return TabHelper.getRoleTabs(roleID);
	}

	/**
	 * 根据菜单项SubTab，读出文档类型ID、文档库ID、文件夹ID信息。
	 * 菜单项配置中指定了文档类型。
	 */
	public static DomInfo getDomInfo(HttpServletRequest request, SubTab subTab)
			throws E5Exception {
		DomInfo domInfo = new DomInfo();

		domInfo.setDocTypeID(subTab.getDocTypeID());
		domInfo.setRule(subTab.getRule());

		// 读文档库和文件夹
		if (domInfo.getDocTypeID() > 0) {
			String tenantCode = InfoHelper.getTenantCode(request);

			DocLib docLib = LibHelper
					.getLib(domInfo.getDocTypeID(), tenantCode);

			domInfo.setDocLibID(docLib.getDocLibID());
			domInfo.setFolderID(docLib.getFolderID());
		}

		// 列表方式
		domInfo.setListIDs(subTab.getListID());
		int[] listIDs = StringUtils.getIntArray(subTab.getListID());
		if (listIDs != null && listIDs.length >= 1)
			domInfo.setListID(listIDs[0]);

		// 查询条件、查询条件中定义的js引用
		domInfo.setQueryID(subTab.getQueryID());
		domInfo.setQueryScripts(subTab.getQueryScripts());

		return domInfo;
	}

}
