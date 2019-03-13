package com.founder.xy.config;

import java.util.ArrayList;

import java.util.List;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionReader;

/**
 * TAB权限判断辅助类
 * @author Gong Lijie
 */
public class TabHelper {
	/**
	 * 取角色可见的主界面TAB
	 * @param roleID
	 * @return
	 */
	public static List<Tab> getRoleTabs(int roleID) {
		//从配置文件当中获取tabs
		List<Tab> tabs = ConfigReader.getTabs();
		//读取tabs
		String[] roleTabs = readRoleTabs(roleID, "MainPermission");
		
		return filterTabs(tabs, getRoleAndNoPermissionTabs(roleTabs,getNoPermissionTabs(tabs)));
	}
	
	private static List<Tab> filterTabs(List<Tab> tabs, String[] roleTabs) {
		List<Tab> myTabs = new ArrayList<Tab>(tabs.size());
		try {
			for (Tab tab : tabs) {
				if (tab.getChildren() == null || tab.getChildren().size() == 0)
					myTabs.add(tab.clone());
				else {
					List<SubTab> subTabs = new ArrayList<SubTab>(tab.getChildren().size());
					int count = 0;
					SubTab sepTab = null;
					for (SubTab subTab : tab.getChildren()) {
						//若是分隔符，则可见；若有权限，则可见；若有子层TAB有权限，则可见
						if (subTab.getId().equals("separator"))
							sepTab = subTab.clone();
							//subTabs.add(subTab.clone());
						else {
							SubTab clone = checkSubTab(subTab, roleTabs);
							if (clone != null) {
								if(sepTab!=null && count>0){
									subTabs.add(sepTab);
									sepTab = null;
								}
								subTabs.add(clone);
								count++;
							}
						}
					}
					if (count > 0) {
						Tab myTab = tab.clone();
						myTab.setChildren(subTabs);
						
						myTabs.add(myTab);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return myTabs;
	}

	/**
	 * 过滤一个二层TAB，包含有权限的三层TAB
	 * @param subTab
	 * @param roleTabs
	 * @return
	 */
	private static SubTab checkSubTab(SubTab subTab, String[] roleTabs) {
		if (subTab.getChildrenCount() > 0) {
			List<SubTab> myTabs = new ArrayList<SubTab>();
			
			for (SubTab thirdTab : subTab.getChildren()) {
				if (contains(roleTabs, thirdTab.getId())) {
					myTabs.add(thirdTab.clone());
				}
			}
			if (myTabs.size() > 0) {
				SubTab clone = subTab.clone();
				clone.setChildren(myTabs);
				return clone;
			}
		} else if(contains(roleTabs, subTab.getId())) {
			return subTab.clone();
		}
		
		return null;
	}
	
	public static String[] readRoleTabs(int roleID, String resourceType) {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		try {
			Permission[] permission = pReader.getPermissions(roleID, resourceType);
			if (permission == null) return null;
			
			return permission[0].getResource().split(",");
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static long[] getColIDsByRole(int roleID, String resourceType) {
		PermissionReader pReader = (PermissionReader)Context.getBean(PermissionReader.class);
		try {
			Permission[] permission = pReader.getPermissions(roleID, resourceType);
			if (permission == null) return null;

			return StringUtils.getLongArray(permission[0].getResource(),",");
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static boolean contains(String[] roleTabs, String id){
		if (roleTabs == null) return false;
		
		for (String tab : roleTabs) {
			if (tab.equals(id))
				return true;
		}
		return false;
	}
	
	private static String[] getNoPermissionTabs(List<Tab> tabs){
		if(tabs == null) return null;
		String tabIds = "";
		for (Tab tab : tabs) {
			if(tab.isFree()){
				if(!StringUtils.isBlank(tabIds)) tabIds +=",";
				tabIds += tab.getId();
			}
			String[] subTabs = getNoPermissionSubTabs(tab.getChildren());
			if(subTabs!=null){
				for (String subTab : subTabs) {
					if(!StringUtils.isBlank(subTab)) tabIds += ",";
					tabIds += subTab;
				}
			}
		}
		return tabIds.split(",");
	}
	private static String[] getNoPermissionSubTabs(List<SubTab> tabs){
		if(tabs == null) return null;
		String tabIds = "";
		for (SubTab tab : tabs) {
			if(tab.isFree()){
				if(!StringUtils.isBlank(tabIds)) tabIds +=",";
				tabIds += tab.getId();
			}
			String[] subTabs = getNoPermissionSubTabs(tab.getChildren());
			if(subTabs!=null){
				for (String subTab : subTabs) {
					if(!StringUtils.isBlank(subTab)) tabIds += ",";
					tabIds += subTab;
				}
			}
		}
		return tabIds.split(",");
	}
	
	private static String[] getRoleAndNoPermissionTabs(String[] roleTabs,String[] noPermissionTabs){
		String tabs = "";
		if(noPermissionTabs!=null){
			for (String noPermissionTab : noPermissionTabs) {
				if(!StringUtils.isBlank(noPermissionTab)){
					if(!StringUtils.isBlank(noPermissionTab)) tabs += ",";
					tabs += noPermissionTab;
				}
			}
		}
		if(roleTabs!=null){
			for (String roleTab : roleTabs) {
				if(!StringUtils.isBlank(roleTab)){				
					boolean exist = false;
					if(noPermissionTabs!=null){
						for (String noPermissionTab : noPermissionTabs) {
							if(!StringUtils.isBlank(noPermissionTab)){
								if(roleTab.equalsIgnoreCase(noPermissionTab)){
									exist = true;
									break;
								}
							}
						}
					}
					if(!exist){
						if(!StringUtils.isBlank(tabs)) tabs += ",";
						tabs += roleTab;
					}
				}
			}
		}
		return tabs.split(",");
	}
	
}
