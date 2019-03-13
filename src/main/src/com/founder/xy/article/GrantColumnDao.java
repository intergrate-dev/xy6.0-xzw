package com.founder.xy.article;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

/**
 * 扩展字段的挂接栏目
 * @author Isaac_Gu
 */
@Repository
public class GrantColumnDao {
	/**
	 * 3. 在xy_column表中查询 扩展字段
	 * @param parameters
	 * @param channel 
	 * @return
	 * @throws E5Exception
	 */
	public GrantInfo findGrantedColumns(GrantInfo parameters, int channel)
			throws E5Exception {
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), 
				Integer.parseInt(parameters.getDocLibID()));
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(colLibID, 
					"col_siteID=? and SYS_DELETEFLAG=0 and " + "col_extField_ID=? and col_channel=?",
					new Object[] { parameters.getSiteID(), parameters.getGroupID() ,  channel});
		parameters.setRt_ids(cols);
		return parameters;
	}

	/**
	 * 把模版加入到添加列表中的数据项中
	 * @param parameters
	 * @param list
	 * @throws E5Exception
	 */
	public void addNewColumns(GrantInfo parameters, List<String> list)
			throws E5Exception {
		StringBuilder qm = new StringBuilder();
		int i = 0;
		while (i < list.size()) {
			qm.append("?,");
			i++;
		}
		LinkedList<Object> ll = new LinkedList<Object>(list);
		ll.addFirst(parameters.getGroupID());

		//取组的名称
		int catID = Integer.parseInt(parameters.getGroupID());
		int catTypeID = CatTypes.CAT_EXTFIELD.typeID();
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
		Category cat = catManager.getCat(catTypeID, catID);
		String catName = cat==null?"":cat.getCatName();
		ll.addFirst(catName);
		
		String sql = "UPDATE xy_column c SET c.col_extField=?, c.col_extField_ID=? WHERE c.SYS_DOCUMENTID in ("
				+ qm.toString().substring(0, qm.length() - 1) + ");";
		InfoHelper.executeUpdate(sql, ll.toArray());
	}

	/**
	 * 把模版从列表中的数据项中删除
	 * @param parameters
	 * @param list
	 * @throws E5Exception
	 */
	public void delColumns(GrantInfo parameters, List<String> list) throws E5Exception {
		StringBuilder qm = new StringBuilder();
		int i = 0;
		while (i < list.size()) {
			qm.append("?,");
			i++;
		}

		String sql = "UPDATE xy_column c SET c.col_extField_ID=null, c.col_extField=''"
				+ " WHERE c.SYS_DOCUMENTID in (" + qm.toString().substring(0, qm.length() - 1)
				+ ");";
		InfoHelper.executeUpdate(sql, list.toArray());

	}

}
