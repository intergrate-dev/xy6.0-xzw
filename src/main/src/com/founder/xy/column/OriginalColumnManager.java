package com.founder.xy.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.Permission;
import com.founder.e5.permission.PermissionManager;
import com.founder.xy.config.TabHelper;
import com.founder.xy.system.site.SiteUserManager;

/**
 * 源稿分类栏目管理器
 * 
 * @author JiangYu
 */
@Component
public class OriginalColumnManager {

	static final char SEPARATOR = '~';

	@Autowired
	private SiteUserManager siteUserManager;

	@Autowired
	private PermissionManager permissionManager;
	
	/**
	 * 按站点取根源稿栏目
	 * 
	 * @param siteID
	 *            站点ID
	 */
	public Document[] getRoot(int colLibID, int siteID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] cols = docManager
				.find(colLibID,
						"col_siteID=? and col_parentID=0 and SYS_DELETEFLAG=0 order by col_displayOrder",
						new Object[] { siteID });
		return cols;
	}

	/**
	 * 按源稿栏目名称查找。用于栏目树上的查找动作
	 */
	public Document[] find(int colLibID, int siteID, String name, boolean flag) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		System.out.println("%" + name + "%");
		if(flag==false){
			Document[] cols = docManager.find(colLibID,
					"(col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0", 
					new Object[] {"%" + name + "%", "%" + name + "%", siteID});
			return cols;
		}else{
			int sys_documentId=Integer.parseInt(name);
			Document[] cols = docManager.find(colLibID,"SYS_DOCUMENTID like ? or ((col_name like ? or col_pinyin like ?) and col_siteID=? and SYS_DELETEFLAG=0 ) ", 
				new Object[] {sys_documentId+"%", "%" + name + "%", "%" + name + "%",  siteID });
			return cols;
		}
	}
	
	/**
	 * 取角色的源稿栏目 不读缓存
	 */
	public Document[] getPermissionColumns(int colLibID, int siteID, int roleID)
			throws E5Exception {
		Permission[] permission = permissionManager.getPermissions(roleID,
				"OriginalColumn"+ siteID);
		if (permission == null) return null;
		long[] roleColIDs = StringUtils.getLongArray(permission[0].getResource(),",");
		return getColumns(colLibID, roleColIDs);
	}
	
	/**
	 * 取角色的源稿栏目 读缓存
	 */
	public Document[] getRoleColumns(int colLibID, int siteID, int roleID)
			throws E5Exception {

		long[] roleColIDs = TabHelper.getColIDsByRole(roleID, "OriginalColumn"+siteID);
		return getColumns(colLibID, roleColIDs);
	}

	private Document[] getColumns(int colLibID, long[] ids) throws E5Exception {
		if (ids == null)
			return null;

		DocumentManager docManager = DocumentManagerFactory.getInstance();

		List<Document> docs = new ArrayList<Document>();

		for (int i = 0; i < ids.length; i++) {
			Document doc = docManager.get(colLibID, ids[i]);
			if (doc != null && doc.getDeleteFlag() == 0)
				docs.add(doc);
		}

		Collections.sort(docs, new Comparator<Document>() {
			public int compare(Document me1, Document me2) {
				return new Integer(me1.getInt("col_displayOrder"))
						.compareTo(me2.getInt("col_displayOrder"));
			}
		});

		return docs.toArray(new Document[0]);
	}

	/**
	 * 根据源稿栏目ID 查询源稿栏目名称
	 */
	public String getCatName(long docID, int docLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		return doc.getString("col_name");
	}
	
	/**
	 * 获取站点下 所有带有审核流程的栏目
	 */
	public Document[] getAuditCols(int colLibID, int siteID, long parentID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		Document[] cols = docManager
				.find(colLibID,
						"col_siteID = ? and col_flow_ID > 1 and col_parentID = ? and SYS_DELETEFLAG = 0 order by col_displayOrder",
						new Object[] { siteID, parentID });
		return cols;
	}
	
}
