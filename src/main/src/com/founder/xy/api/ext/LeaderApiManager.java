package com.founder.xy.api.ext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 与外网api通讯的人物Api
 */
@Service
public class LeaderApiManager {
	private static final int LIST_COUNT = 20; //其它列表一次获取的数量

	/**
	 * 人物列表
	 */
	public boolean leaderList(int siteID, int start,int count) throws E5Exception{
		count = LIST_COUNT; //固定个数
		
		String sql = getLeaderListSql(-1);
		String jsonArr = getLeaderListJsonArray(sql, null, start,count);
		
		String key = RedisManager.getKeyBySite(RedisKey.APP_LEADERLIST_KEY, siteID);
		RedisManager.set(key + start, jsonArr);
		return true;
	}

	/**
	 * 地区人物列表
	 */
	public boolean regionLeaderList(int siteID, int regionID) throws E5Exception{
		//读出地区分类，取级联ID进行查询
		CatReader catReader = (CatReader)Context.getBean(CatReader.class);
		Category region = catReader.getCat(CatTypes.CAT_REGION.typeID(), regionID);
		if (region == null) return false;
		
		String sql = getLeaderListSql(regionID);
		Object[] params = new Object[]{region.getCascadeID().replace('~', '_')};
		
		String jsonArr = getLeaderListJsonArray(sql, params, -1,-1);
		
		String key = RedisManager.getKeyBySite(RedisKey.APP_LEADERLIST_REGION_KEY, siteID) + regionID;
		RedisManager.set(key, jsonArr);
		return true;
	}

	/**
	 * 人物detail
	 */
	public boolean leaderDetail(int id) throws E5Exception{
		int leaderDocLibID = DomHelper.getDocLibID(DocTypes.LEADER.typeID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(leaderDocLibID, id);
	
		if (doc == null) return false;
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", doc.getLong("SYS_DOCUMENTID"));
		jsonObj.put("name", doc.getString("l_name"));
		jsonObj.put("url", doc.getString("l_iconUrl"));
		jsonObj.put("duty", doc.getString("l_duty"));
		jsonObj.put("description", doc.getString("l_description"));
		jsonObj.put("columnID", doc.getInt("l_columnID"));
		jsonObj.put("details", doc.getString("l_details"));
	
		RedisManager.set(RedisKey.APP_LEADER_KEY + id, jsonObj.toString());
	
		return true;
	}

	/**
	 * 领导人列表sql
	 * @throws E5Exception 
	 */
	private String getLeaderListSql(int regionID) throws E5Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select SYS_DOCUMENTID from ");
		sql.append(LibHelper.getLibTable(DocTypes.LEADER.typeID(), Tenant.DEFAULTCODE));
		sql.append(" WHERE l_status=0 AND SYS_DELETEFLAG=0");
		if (regionID <= 0)
			sql.append(" AND l_isMajor = 1");
		else
			sql.append(" AND l_regionID=?");
		sql.append(" ORDER BY l_order");
		return sql.toString();
	}
	
	/**
	 * 领导人列表
	 * @throws E5Exception 
	 */
	private String getLeaderListJsonArray(String sql, Object[] params, int start, int count) throws E5Exception{
		DBSession conn = null;
		IResultSet rs = null;
		List<Long> ids = new ArrayList<>();
		try{
			conn = Context.getDBSession();
			String sqlStr = null;
			if(start == -1 && count == -1)
				sqlStr = sql;
			else
				sqlStr = conn.getDialect().getLimitString(sql, start, count);
			rs = conn.executeQuery(sqlStr, params);
			while(rs.next()){
				ids.add(rs.getLong("SYS_DOCUMENTID"));
			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		JSONArray jsonArr = new JSONArray();
		
		JSONObject jsonObj = null;
		Document doc = null;
		int leaderDocLibID = DomHelper.getDocLibID(DocTypes.LEADER.typeID());

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		for (long id : ids) {
			doc = docManager.get(leaderDocLibID, id);
			if (doc == null) continue;
			
			jsonObj = new JSONObject();
			jsonObj.put("id", id);
			jsonObj.put("name", doc.getString("l_name"));
			jsonObj.put("url", doc.getString("l_iconUrl"));
			jsonObj.put("duty", doc.getString("l_duty"));
			jsonObj.put("description", doc.getString("l_description"));
			jsonObj.put("columnID", doc.getInt("l_columnID"));
			jsonObj.put("groupID", doc.getInt("l_groupID"));
			jsonArr.add(jsonObj);
		}
		return jsonArr.toString();
	}
}