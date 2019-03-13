package com.founder.xy.template;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;

@Repository("templateDao")
public class TemplateDao {

	/**
	 *  1. 先根据模版的id查出来他是什么类型的模版，例如手机模版或者栏目模版
	 * 	2. 根据模版的类型确定查询栏目表中的哪个字段，例如0-col_template 1-col_templatearticle
	 * @param parameters
	 * @return
	 * @throws E5Exception
	 */
	public GrantInfo findTemplateCode(GrantInfo parameters)
			throws E5Exception {
		//获得数据库id
		int docLibID = Integer.valueOf(parameters.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(docLibID, "SYS_DOCUMENTID=? and SYS_DELETEFLAG=0",
				new Object[] { parameters.getDocIDs() });
		// TODO 如果查询不到模版，该如何处理
		if (cols != null && cols.length > 0) {
			//2. 根据模版的类型确定查询栏目表中的哪个字段，例如0-col_template 1-col_templatearticle
			parameters.setRt_templateType((Integer)cols[0].get("t_type"),
					(Integer)cols[0].get("t_channel"));
			parameters.setDb_templateRealName((String) cols[0].get("T_NAME"));
		}
		return parameters;
	}

	/**
	 * 3. 在xy_column表中查询 相应模版字段中等于模版id的栏目列表
	 * @param parameters
	 * @return
	 * @throws E5Exception
	 */
	public GrantInfo findIds(GrantInfo parameters) throws E5Exception {
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), 
				Integer.parseInt(parameters.getDocLibID()));
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] cols = docManager.find(docLibID, "col_siteID=? and SYS_DELETEFLAG=0 and "
				+ parameters.getDb_templateColumnName() + "=?",
				new Object[] { parameters.getSiteID(), parameters.getDocIDs() });
		parameters.setRt_ids(cols);
		return parameters;
	}

	/**
	 * 把模版加入到添加列表中的数据项中
	 * @param parameters
	 * @param list
	 * @throws E5Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addNewColumns(GrantInfo parameters, List list) throws E5Exception {
		StringBuilder qm = new StringBuilder();
		int i = 0;
		while (i < list.size()) {
			qm.append("?,");
			i++;
		}
		LinkedList<Object> ll = new LinkedList<Object>(list);
		ll.addFirst(parameters.getDocIDs());
		String sql = "UPDATE xy_column c SET c."
				+ parameters.getDb_templateColumnName()
				+ "=? , "
				+ parameters.getDb_templateColumnName().substring(0,
						parameters.getDb_templateColumnName().length() - 3) + "='"
				+ parameters.getDb_templateRealName() + "' WHERE c.SYS_DOCUMENTID in ("
				+ qm.toString().substring(0, qm.length() - 1) + ");";
		InfoHelper.executeUpdate(sql, ll.toArray());

	}

	/**
	 * 把模版从列表中的数据项中删除
	 * @param parameters
	 * @param list
	 * @throws E5Exception
	 */
	@SuppressWarnings("rawtypes")
	public void delColumns(GrantInfo parameters, List list) throws E5Exception {
		StringBuilder qm = new StringBuilder();
		int i = 0;
		while (i < list.size()) {
			qm.append("?,");
			i++;
		}

		String sql = "UPDATE xy_column c SET c."
				+ parameters.getDb_templateColumnName()
				+ "=null , "
				+ parameters.getDb_templateColumnName().substring(0,
						parameters.getDb_templateColumnName().length() - 3)
				+ "=null WHERE c.SYS_DOCUMENTID in (" + qm.toString().substring(0, qm.length() - 1)
				+ ");";
		InfoHelper.executeUpdate(sql, list.toArray());

	}

	/**
	 * 查询给定列表所对应栏目的名字
	 * @param list
	 * @return
	 * @throws E5Exception
	 */
	public String findNewColumnNames(int colLibID, List<String> list) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		StringBuilder qm = new StringBuilder();
		int i = 0;
		while (i < list.size()) {
			qm.append("?,");
			i++;
		}
		String sql = " SYS_DOCUMENTID in (" + qm.toString().substring(0, qm.length() - 1)
				+ ") and SYS_DELETEFLAG=0 ";

		Document[] cols = docManager.find(colLibID, sql, list.toArray());
		qm = new StringBuilder();
		if (cols != null && cols.length > 0) {
			for (Document doc : cols) {
				qm.append("《" + (String) doc.get("col_name") + "》");
			}

		}
		if (qm.toString().trim().equals("")) {
			qm.append("无");
		}
		return qm.toString();
	}

}
