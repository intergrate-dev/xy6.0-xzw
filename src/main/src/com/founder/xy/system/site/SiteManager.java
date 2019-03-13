package com.founder.xy.system.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 站点、域、域目录、发布规则的管理器
 */
@Component
public class SiteManager {
	private String SQL_VALID_SITES = "site_status is null or site_status=0";
	/**
	 * 取所有的站点
	 */
	public List<Site> getSites(int siteLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		List<Site> sites = new ArrayList<Site>();
		try {
			Document[] docs = docManager.find(siteLibID, SQL_VALID_SITES, null);
			for (Document doc : docs) {
				sites.add(siteObj(doc));
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return sites;
	}
	
	/**
	 * 取所有的站点
	 */
	public Document[] getSites(String tenantCode) throws E5Exception {
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), tenantCode);
	
		DocumentManager docManager = DocumentManagerFactory.getInstance();	
		Document[] sites = docManager.find(siteLibID, SQL_VALID_SITES, null);
		return sites;
	}

	/**
	 * 取站点数
	 */
	public int getSiteCount(int siteLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] docs = docManager.find(siteLibID, "SYS_DELETEFLAG=0", null);
			return docs.length;
		} catch (E5Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	//去查询相关的站点
	public Document[] find(String tenantCode, String name) throws E5Exception {
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), tenantCode);
		String sql = "site_name like ? and " + SQL_VALID_SITES;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(siteLibID, sql, new Object[]{name + "%"});
		return cols;
	}
	
	public Map<Long, DomainDir> getDomainDirs(int docLibID) {
		Map<Long, DomainDir> dirs = new HashMap<Long,DomainDir>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try{
			Document[] docs = docManager.find(docLibID, "SYS_DELETEFLAG=0", null);
			for (Document doc : docs) {
				dirs.put(doc.getDocID(), new DomainDir(doc));
			}
		}catch (E5Exception e) {
			e.printStackTrace();
		}
		return dirs;
	}
	
	//判断站点是否包含有栏目
	public boolean containCols(int siteLibID,int siteID) throws E5Exception {

		try{
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] cols = null;
			String sql = "col_siteID = ? ";
			int columnLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), siteLibID);
			
			cols = docManager.find(columnLibID, sql, new Object[]{siteID});
			if((cols == null || cols.length == 0)){
				return false;
			}
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//删除站点
	public String deleteSite(int siteLibID,int siteID) throws E5Exception {
		String tenantCode = LibHelper.getTenantCodeByLib(siteLibID);
		
		//删除站点的发布规则
		String ruleLib = LibHelper.getLibTable(DocTypes.SITERULE.typeID(), tenantCode);
		String delRule = "delete from " + ruleLib + " where rule_siteID=?";
		
		//删除站点的域名目录
		String domainLib = LibHelper.getLibTable(DocTypes.DOMAINDIR.typeID(), tenantCode);
		String delDir = "delete from " + domainLib + " where dir_siteID=?";
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = E5docHelper.getDBSession(siteLibID);;
			conn.beginTransaction();	
			
			docManager.delete(siteLibID, siteID, conn);
			InfoHelper.executeUpdate(delRule,new Object[]{siteID}, conn);
			InfoHelper.executeUpdate(delDir,new Object[]{siteID}, conn);
	
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 读App的若干配置
	 */
	public JSONObject getAppConfig(int siteID) {
		JSONObject jsonConfig = getConfig(siteID);
		return JsonHelper.getJsonObject(jsonConfig, "app");
	}
	
	/**
	 * 取出系统配置的App专题头图宽高，返回数组[宽，高]
	 */
	public int[] getSpecialCoverSize(int siteID) {
		JSONObject jsonConfig = getAppConfig(siteID);
		
		return new int[]{
				JsonHelper.getInt(jsonConfig, "specialCoverWidth", 0),
				JsonHelper.getInt(jsonConfig, "specialCoverHeight", 0),
		};
	}
	
	/**
	 * 取出系统配置的App订阅栏目的稿件显示个数，默认为3
	 */
	public int getSubscribeArticleCount(int siteID) {
		JSONObject jsonConfig = getAppConfig(siteID);
		return JsonHelper.getInt(jsonConfig, "subscribeArticleCount", 3);
	}
	
	private Site siteObj(Document doc) {
		String webRoot = InfoHelper.getWebRoot(doc.getDocID());
		if (StringUtils.isBlank(webRoot))
			webRoot = doc.getString("site_webRoot");
		return new Site(doc.getString("site_name"), 
				(int)doc.getDocID(),
				webRoot);
	}

	/**
	 * 读参数
	 */
	private JSONObject getConfig(int siteID) {
		String key = RedisKey.SITE_CONF_KEY + siteID;
		String discussConfig = RedisManager.get(key);
	
		if (discussConfig == null) {
			int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			JSONObject jsonConfig = null;
			try {
				Document site = docManager.get(siteLibID, siteID);
				String siteConfig = site.getString("site_config");
				if (!StringUtils.isBlank(siteConfig)) {
					jsonConfig = JsonHelper.getJson(siteConfig);
				}
			} catch (E5Exception e) {
			}
			if (jsonConfig == null) jsonConfig = new JSONObject();
			RedisManager.set(key, jsonConfig.toString());
			
			return jsonConfig;
		} else {
			return JsonHelper.getJson(discussConfig);
		}
	}
}
