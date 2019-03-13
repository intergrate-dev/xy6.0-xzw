package com.founder.xy.app;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;

@Component
public class MobileAppManager {
	/** 查找所有 */
	public Document[] findAll() {
		int docLibID = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				Tenant.DEFAULTCODE);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] bms = docManager.find(docLibID, "1=1", null);
			return bms;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 根据平台id查找 */
	public Document[] findByMoId(String moId) {
		int docLibID = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				Tenant.DEFAULTCODE);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document[] bms = docManager.find(docLibID, "ma_moID=?",
					new Object[] { moId });
			return bms;
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 根据id查找发布路径ID */
	public Map<String, String> getInfoById(String id) throws E5Exception {

		String tenantCode = Tenant.DEFAULTCODE;
		int appLibId = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				tenantCode);
		int osLibId = LibHelper
				.getLibID(DocTypes.MOBILEOS.typeID(), tenantCode);

		String sql = "select mo.os_dir_ID,xma.ma_appKey,xma.ma_md5,xma.ma_type from "
				+ LibHelper.getLibTable(osLibId) + " mo " + " left join "
				+ LibHelper.getLibTable(appLibId)
				+ " xma on xma.ma_moID = mo.SYS_DOCUMENTID"
				+ " where xma.SYS_DOCUMENTID = ? ";

		Object[] params = new Object[] { id };
		DBSession conn = null;
		IResultSet rs = null;
		Map<String, String> data = new HashMap<String, String>();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			if (rs.next()) {
				data.put("dirId", rs.getString("os_dir_ID"));
				data.put("md5", rs.getString("ma_md5"));
				data.put("appKey", rs.getString("ma_appKey"));
				data.put("maType", rs.getString("ma_type"));
			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return data;
	}

}
