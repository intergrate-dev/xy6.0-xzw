package com.founder.xy.system.site;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 域、域名、域目录管理器
 */
@Component
public class DomainDirManager {

	/**
	 * 取所有域名目录
	 */
	public Document[] getDomainDir(int siteID) throws E5Exception {
		int domainDirLibID = LibHelper.getDomainDirLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] dirs = docManager.find(domainDirLibID,
				"dir_siteID=? and SYS_DELETEFLAG=0", new Object[] { siteID });

		return dirs;
	}

	/**
	 * 根据父目录取子节点
	 */
	public Document[] getDomainDirByPId(int siteID, int parentId)
			throws E5Exception {
		int domainDirLibID = LibHelper.getDomainDirLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] dirs = docManager
				.find(domainDirLibID,
						"dir_siteID=? and dir_parentID=? and SYS_DELETEFLAG=0 order by dir_name asc",
						new Object[] { siteID, parentId });

		return dirs;
	}

	/**
	 * 取站点的所有域名（根目录）
	 */
	public List<DomainDir> getDomains(int docLibID, long siteID)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] dirs = docManager.find(docLibID,
				"dir_siteID=? and dir_parentID=0 and SYS_DELETEFLAG=0",
				new Object[] { siteID });
		List<DomainDir> result = new ArrayList<>();
		for (Document dir : dirs) {
			result.add(new DomainDir(dir));
		}
		return result;
	}

	/**
	 * 取一个发布目录
	 */
	public DomainDir getDomain(int docLibID, long docID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document dir = docManager.get(docLibID, docID);
		return new DomainDir(dir);
	}

	/**
	 * 目录删除：目录和子目录做删除标记
	 */
	public void delete(int docLibID, long dirID) throws E5Exception {
		deleteDir(docLibID, dirID);
	}

	private void deleteDir(int docLibID, long dirID) throws E5Exception {

		DocLib docLib = LibHelper.getLibByID(docLibID);

		String sql = "delete from " + docLib.getDocLibTable()
				+ " where SYS_DOCUMENTID=? or dir_parentID=?";
		InfoHelper.executeUpdate(docLibID, sql, new Object[] { dirID, dirID });

	}

	/**
	 * 判断域名目录是否被发布规则引用
	 */
	public boolean dirUsedforRule(long dirID) throws E5Exception {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] dirdocs = null;
			Document[] subdirdocs = null;
			String dirsql = "rule_column_dir_ID = ? "
					+ "or rule_article_dir_ID = ? "
					+ "or rule_photo_dir_ID = ? " + "or rule_attach_dir_ID = ?";
			String subdirsql = "rule_column_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?)"
					+ "or rule_article_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?) "
					+ "or rule_photo_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?) "
					+ "or rule_attach_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?)";
			int siteRuleLibID = LibHelper.getSiteRuleLibID();
			dirdocs = docManager.find(siteRuleLibID, dirsql, new Object[] {
					dirID, dirID, dirID, dirID });
			subdirdocs = docManager.find(siteRuleLibID, subdirsql,
					new Object[] { dirID, dirID, dirID, dirID });

			if ((dirdocs == null || dirdocs.length == 0)
					&& (subdirdocs == null || subdirdocs.length == 0)) {
				return false;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 判断域名目录是否被页面区块发布目录引用
	 */
	public boolean dirUsedforBlock(long dirID) throws E5Exception {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] dirdocs = null;
			Document[] subdirdocs = null;
			String dirsql = "b_dir_ID = ? ";
			String subdirsql = "b_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?)";
			int blockLibID = LibHelper.getBlockLibID();
			dirdocs = docManager.find(blockLibID, dirsql,
					new Object[] { dirID });
			subdirdocs = docManager.find(blockLibID, subdirsql,
					new Object[] { dirID });
			if ((dirdocs == null || dirdocs.length == 0)
					&& (subdirdocs == null || subdirdocs.length == 0)) {
				return false;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 判断域名目录是否被公共资源发布目录引用
	 */
	public boolean dirUsedforRes(long dirID) throws E5Exception {
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] dirdocs = null;
			Document[] subdirdocs = null;
			String dirsql = "res_dir_ID = ? ";
			String subdirsql = "res_dir_ID in (select SYS_DOCUMENTID from xy_domaindir where dir_parentID=?)";

			int resLibID = LibHelper.getResourceLibID();
			dirdocs = docManager.find(resLibID, dirsql, new Object[] { dirID });
			subdirdocs = docManager.find(resLibID, subdirsql,
					new Object[] { dirID });

			if ((dirdocs == null || dirdocs.length == 0)
					&& (subdirdocs == null || subdirdocs.length == 0)) {
				return false;
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 作为资源目录
	 * 
	 * @param dirId
	 * @param siteId
	 * @return
	 */
	public JSONObject resDir(long dirId, int siteId) {
		JSONObject json = new JSONObject();
		try {
			String[] dirs = getDirs(dirId);
			String dirPath = dirs[1];
			String dirUrl = dirs[2];
			
			int libId = LibHelper.getSiteLibID();
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document siteDoc = docManager.get(libId, siteId);
			if (siteDoc != null) {
				String webRoot = dirs[0];
				if (dirPath != null && dirUrl != null && webRoot != null) {
					// 更新以下两个字段
					siteDoc.set("site_resPath", webRoot + dirPath);
					siteDoc.set("site_resUrl", dirUrl);
					docManager.save(siteDoc);
					
					json.put("status", "success");
					json.put("dirpath", webRoot + dirPath);
					json.put("dirurl", dirUrl);
					return json;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		json.accumulate("status", "failure");
		return null;
	}

	public JSONObject getResourceDir(int siteId) {
		JSONObject json = new JSONObject();
		try {
			Document siteDoc;
			String dirPath = null, dirUrl = null;
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			// 获得 site
			int libId = LibHelper.getSiteLibID();
			siteDoc = docManager.get(libId, siteId);
			if (siteDoc != null) {
				dirPath = siteDoc.getString("site_resPath");
				dirUrl = siteDoc.getString("site_resUrl");
				// 更新以下两个字段
				json.accumulate("status", "success");
				json.accumulate("dirpath", dirPath);
				json.accumulate("dirurl", dirUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 根据发布目录ID，得到站点的目录、发布目录的目录。 用于指定发布目录的场合，如公共资源、移动平台设置。
	 * 
	 * 返回格式如：[z:\webroot, /xy/resource]
	 */
	public String[] getDirs(long dirID) {
		String[] result = new String[3];
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int dirLibID = LibHelper.getLib(DocTypes.DOMAINDIR.typeID()).getDocLibID();
		int siteID = 0;
		try {
			Document dir = docManager.get(dirLibID, dirID);
			result[1] = dir.getString("dir_path");
			result[2] = dir.getString("dir_url");
			
			siteID = dir.getInt("dir_siteID");
		} catch (E5Exception e) {
			e.printStackTrace();
		}

		result[0] = InfoHelper.getWebRoot(siteID);
		if (StringUtils.isBlank(result[0])) {
			// 获取站点,然后获取站点发布根目录
			int siteLibID = LibHelper.getLib(DocTypes.SITE.typeID()).getDocLibID();
			try {
				Document site = docManager.get(siteLibID, siteID);
				result[0] = site.getString("site_webRoot");
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
