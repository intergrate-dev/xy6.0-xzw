package com.founder.xy.system.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.BaseCache;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;

/**
 * 站点用户的缓存
 * @author zhangmc
 */
public class SiteUserCache extends BaseCache{
	private SiteUserManager userManager;
	private SiteManager siteManager;
	
	/** 站点List <siteLibID, <siteID, site>>*/
	private Map<Integer, Map<Integer, Site>> siteMap = new HashMap<>();
	
	/** 用户对应的站点List <userLibID, <userID, userSiteIDs>*/
	private Map<Integer, Map<Integer, int[]>> userSiteMap = new HashMap<>();
	
	/** 用户对应的site和role， site和role为json字符串*/
	private Map<Integer, Map<Integer, String>> user_SiteRoleMap = new HashMap<>();
	
	/** 用户对应的可操作的栏目IDList, <userLibID, <userID_siteID_type, ids>> */
	private Map<Integer, Map<String, long[]>> userColumnsMap = new HashMap<>();

	public SiteUserCache() {
		
	}
	@Override
	protected int getDocTypeID() {
		return DocTypes.USEREXT.typeID();
	}

	@Override
	public void refresh(int userLibID) throws E5Exception {
		if (userManager == null) {
			userManager = new SiteUserManager();
			siteManager = new SiteManager();
		}
		// 将user所对应的siteID放进缓存
		Map<Integer, int[]> userSites = userManager.getUserSites(userLibID);
		userSiteMap.put(userLibID, userSites);
		
		// 将user所对应的siteID和roleID的Map放进缓存
		Map<Integer, String> user_SiteRoles = userManager.getSiteAndRoles(userLibID);
		user_SiteRoleMap.put(userLibID, user_SiteRoles);
		
		// 站点Map
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), userLibID);
		List<Site> siteList = siteManager.getSites(siteLibID);
		Map<Integer, Site> sites = new HashMap<>();
		for (Site site : siteList) {
			sites.put(site.getId(), site);
		}
		siteMap.put(siteLibID, sites);
		
		// 将user所对应的栏目ID放进缓存
		refreshRel(userLibID);
	}
	
	/**
	 * 用户关联信息缓存刷新
	 * @param userLibID 用户库ID
	 */
	public void refreshRel(int userLibID) throws E5Exception {
		// 将user所对应的栏目ID放进缓存
		int userRelLibID = LibHelper.getLibIDByOtherLib(DocTypes.USERREL.typeID(), userLibID);
		Map<String, long[]> userColumns = userManager.getRels(userRelLibID);
		userColumnsMap.put(userLibID, userColumns);
	}

	/** 根据userID得到Site的List */
	public List<Site> getSites(int userLibID, int userID){
		int[] siteIDs = userSiteMap.get(userLibID).get(userID);
		
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), userLibID);
		List<Site> result = new ArrayList<Site>();
		Map<Integer, Site> sites = siteMap.get(siteLibID);
		
		if (siteIDs != null) {
			for (int i = 0; i < siteIDs.length; i++) {
				Site site = sites.get(siteIDs[i]);
				if (site != null) {
					result.add(site.clone());
				}
			}
		}
		return result;
	}
	
	/** 根据userID和siteID得到roleID的数组 */
	public int[] getRoles(int userLibID, int userID, int siteID){
		//根据userID得到site和role的json串
		String siteRoleStr = user_SiteRoleMap.get(userLibID).get(userID);
		
		int[] roleIDs = null;
		if(!StringUtils.isBlank(siteRoleStr)){
			JSONArray arr = (JSONArray) JSONArray.fromObject(siteRoleStr);
			for (int i = 0; i < arr.size(); i++) {
				JSONObject obj = (JSONObject) arr.get(i);
				String siteIDStr = obj.get("k").toString();
				
				//（2）查看json串中是否有siteID对应的roleID
				if(siteIDStr.equals(siteID + "")){
					JSONArray roleArr = (JSONArray) JSONArray.fromObject(obj.get("v").toString());
					roleIDs = new int[roleArr.size()];
					for (int k = 0; k < roleArr.size(); k++) {
						roleIDs[k] = Integer.parseInt(roleArr.get(k).toString());
					}
					break;
				}
			}
		}
		return roleIDs;
	}
	
	/** 根据用户ID得到可操作的栏目ID数组 */
	public long[] getColumns(int userLibID, String key){
		if (userColumnsMap.get(userLibID) != null)
			return userColumnsMap.get(userLibID).get(key);
		else
			return null;
	}

}