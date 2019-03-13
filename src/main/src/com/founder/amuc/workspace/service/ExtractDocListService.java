package com.founder.amuc.workspace.service;

import java.util.ArrayList;
import java.util.List;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;

/**
 * 会员抽取：抽取成活动客户或其它关联关系，继承ListService，读出所有行的需要的列
 * @author Gong Lijie
 * 2014-7-9
 */
public class ExtractDocListService extends DocListService{
	public List<String[]> getDatas() {
		String sql = getDocListSQL();
		
		DBSession db = null;
		IResultSet rs = null;
		try {
			db = Context.getDBSession(docLib.getDsID());
			rs = db.executeQuery(sql, null);
			return assembleData(rs);
		} catch (Exception e) {
			log.error("[ExtractDocListService.getDocList(sql)]", e);
			log.error("[ExtractDocListService.getDocList(sql)]--" + sql);
			return null;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}	
	}
	private List<String[]> assembleData(IResultSet rs) {
		String[] fields = param.getFields();
		int len = fields.length;
		List<String[]> result = new ArrayList<String[]>();
		try {
			while (rs.next()) {
				String[] values = new String[len];
				for (int i = 0; i < len; i++) {
					String value = rs.getString(fields[i]);
					values[i] = value;
				}
				result.add(values);
			}
		} catch (Exception e) {
			log.error("[ExtractDocListService.assemble]", e);
		}
		return result;
	}
}
