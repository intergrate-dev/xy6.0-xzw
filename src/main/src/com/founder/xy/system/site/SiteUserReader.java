package com.founder.xy.system.site;

import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sys.org.Role;
import com.founder.xy.config.TabHelper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 站点用户的读取器，从缓存中读站点用户的信息
 */
@Component
public class SiteUserReader {
	/** 根据userID得到Site的List */
	public List<Site> getSites(int userLibID, int userID){
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		return siteUserCache.getSites(userLibID, userID);
	}

	/** 根据userID和siteID得到roleID的数组 */
	public int[] getRoles(int userLibID, int userID, int siteID){
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		return siteUserCache.getRoles(userLibID, userID, siteID);
	}
	
	/** 获得用户在某站点下的关联信息 
	 * @param userID 用户系统ID
	 * @param siteID 站点ID
	 * @param type 关联类型：0-可操作的栏目；1-可管理的栏目；2-视频分类；3-页面区块组
	 */
	public long[] getRelated(int userLibID, int userID, int siteID, int type) throws E5Exception {
		SiteUserCache siteUserCache = (SiteUserCache)(CacheReader.find(SiteUserCache.class));
		if(type ==0 || type == 1|| type == 4|| type == 5){
			long ids[] = siteUserCache.getColumns(userLibID, userID + "_" + siteID + "_" + type);
			//取栏目时，合并按角色设置的栏目
			return megerRoleColIDs(ids,userLibID,type,userID,siteID);
		}
		return siteUserCache.getColumns(userLibID, userID + "_" + siteID + "_" + type);
	}

	//合并按角色设置的栏目
	private long[] megerRoleColIDs(long[] ids, int userLibID, int type, int userID, int siteID) {
		try {
			String opType = (type == 0|| type == 4) ? "Op":"Admin";
			Role[] roles = getRolesBySite(userLibID,userID,siteID);
			int roleID = roles[0].getRoleID();
			int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
			//取出按角色设置的栏目权限，准备合并
			String sourceType = getSourceType(siteID,type,opType);
			long[] roleColIDs = TabHelper.getColIDsByRole(newRoleID,sourceType);
			//权限合并
			ids = unite(ids,roleColIDs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	/**
	 * 取栏目权限类型
	 * @param siteID 站点ID
	 * @param channelType 渠道类型 0 网站 ；1 APP
	 * @param type 权限类型
	 * @return
	 */
	private String getSourceType(int siteID, int channelType, String type) {
		String result = "Column"+type;
		switch (channelType){
			case 0:
				return result+"Web"+siteID;
			case 4:
				return result+"App"+siteID;
		}
		return result;
	}

	//两个long数组取并集
	private long[] unite(long[] data1,long[] data2){
		Set<Long> set = new HashSet<>();

		for (int i = 0; data1 != null && i < data1.length; i++) {
			set.add(data1[i]);
		}
		for (int i = 0; data2 != null && i < data2.length; i++) {
			set.add(data2[i]);
		}

		long[] result = new long[set.size()];
		int j = 0;
		for (long i : set) {
			result[j] = i;
			j++;
		}
		return result;
	}

	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		int[] roleIDs = getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles =  new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}
}
