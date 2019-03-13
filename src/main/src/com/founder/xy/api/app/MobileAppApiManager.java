package com.founder.xy.api.app;

import java.sql.SQLException;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

@Service
public class MobileAppApiManager {

	/**
	 * 获得app更新
	 * 
	 * @param channel
	 */
	public boolean getMobileApp(String appKey, String channel)
			throws E5Exception {

		String tenantCode = Tenant.DEFAULTCODE;
		int appLibId = LibHelper.getLibID(DocTypes.MOBILEAPP.typeID(),
				tenantCode);
		int packageLibId = LibHelper.getLibID(DocTypes.MOBILEPACKAGE.typeID(),
				tenantCode);

		StringBuffer sqlSb = new StringBuffer(
				" select xmp.mp_versionCode,xmp.mp_url,xmp.mp_log,xmp.mp_size from "
						+ LibHelper.getLibTable(packageLibId)
						+ " xmp left join "
						+ LibHelper.getLibTable(appLibId)
						+ " xma on xma.SYS_DOCUMENTID = xmp.mp_maId where xma.ma_appKey = ?");

		Object[] params = new Object[] { appKey };
		
		if (null != channel && !"null".equals(channel)) {
			CatManager catManager = (CatManager) Context
					.getBean(CatManager.class);
			Category cateG = catManager.getCatByCode(CatTypes.CAT_APP.typeID(),
					channel);
			if(cateG==null){
				return false;
			}
			int channelId = cateG.getCatID();

			params = new Object[] { appKey, channelId };
			sqlSb.append(" and xmp.mp_channel_ID=?");
		}
		sqlSb.append(" order by xmp.SYS_DOCUMENTID desc limit 1");

		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sqlSb.toString(), params);
			JSONObject date = new JSONObject();
			if (rs.next()) {
				date.put("update", "Yes");
				date.put("new_version",
						StringUtils.getNotNull(rs.getString("mp_versionCode")));
				date.put("apk_url",
						StringUtils.getNotNull(rs.getString("mp_url")));
				date.put("update_log",
						StringUtils.getNotNull(rs.getString("mp_log")));
				date.put("target_size",
						StringUtils.getNotNull(rs.getString("mp_size")));
			}
			StringBuffer redisK = new StringBuffer(RedisKey.APP_MOBILEAPP
					+ appKey);
			if (null != channel && !"null".equals(channel)) {
				redisK.append("." + channel);
			}
			RedisManager.setWeekly(redisK.toString(), date.toString());
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}

}
