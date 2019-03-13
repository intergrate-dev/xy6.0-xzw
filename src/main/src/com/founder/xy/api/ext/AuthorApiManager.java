package com.founder.xy.api.ext;

import static com.founder.xy.api.ApiManager.ALIST_COUNT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;

/**
 * 与外网api通讯的互动记者名片Api
 */
@Service
public class AuthorApiManager {
	/**
	 * 记者文章数/粉丝数
	 */
	public boolean authorCount(int id)throws E5Exception{

		DBSession conn = null;
		IResultSet rs = null;
		try{
			conn = Context.getDBSession();
			JSONObject jsonObj = new JSONObject();

			StringBuilder countSql = new StringBuilder();
			countSql.append("select count(SYS_DOCUMENTID) articlecounts from ");
			countSql.append(LibHelper.getLibTable(LibHelper.getArticleAppLibID()));
			countSql.append(" where  a_status = 1 AND SYS_DELETEFLAG = 0 ");
			countSql.append("AND SYS_AUTHORID = ?");

			rs = conn.executeQuery(countSql.toString(), new Object[]{id});
			if(rs.next())
				jsonObj.put("countArticle", rs.getInt("articlecounts"));
			rs.close();

			StringBuilder fanSql = new StringBuilder();
			fanSql.append("select count(SYS_DOCUMENTID) fan from ");
			fanSql.append(LibHelper.getLibTable(DocTypes.FAN.typeID(),Tenant.DEFAULTCODE));
			fanSql.append(" where SYS_DELETEFLAG = 0 AND fan_authorID = ?");
			rs = conn.executeQuery(fanSql.toString(), new Object[]{id});
			if(rs.next())
				jsonObj.put("countFan", rs.getInt("fan"));

			RedisManager.set(RedisKey.APP_AUTHOR_COUNT_KEY + id, jsonObj .toString());

			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 用户关注
	 */
	public boolean myAuthor(int userID,String userName,int authorID,
			String authorName)throws E5Exception{
		int fanDocLibID = DomHelper.getDocLibID(DocTypes.FAN.typeID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = null ;
		docs = docManager.find(fanDocLibID, "fan_id=? and fan_authorID=? and SYS_DELETEFLAG = 0", new Object[]{userID,authorID});
		if(null != docs && docs.length > 0) return true ;
		Document doc = docManager.newDocument(fanDocLibID);
		doc.set("fan_id", userID);
		doc.set("fan_name", userName);
		doc.set("fan_authorID", authorID);
		doc.set("fan_author", authorName);
		docManager.save(doc);
		
		//用户关注一个记者时，清空redis中记者的粉丝数缓存
		RedisManager.clear(RedisKey.APP_AUTHOR_COUNT_KEY + authorID);
		RedisManager.clearKeyPages(RedisKey.MY_AUTHOR_KEY + userID);
		if(!RedisManager.exists(RedisKey.APP_AUTHOR_FANS_KEY+authorID)){
			setAuthorFans(authorID);
		}else{
			RedisManager.sadd(RedisKey.APP_AUTHOR_FANS_KEY+authorID, userID);
		}
		return true;
	}
	
	/**
	 * 取消关注
	 */
	public boolean myAuthorCancel(int userID,int authorID)throws E5Exception{
		DocLib fanLib = LibHelper.getLib(DocTypes.FAN.typeID(), Tenant.DEFAULTCODE);
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(fanLib.getDocLibTable());
		sql.append(" where fan_id=? and fan_authorID=?");

		InfoHelper.executeUpdate(fanLib.getDocLibID(), sql.toString(), new Object[]{userID,authorID});
		
		//取消关注 清空该记者数粉丝数redis缓存
		RedisManager.clear(RedisKey.APP_AUTHOR_COUNT_KEY + authorID);
		RedisManager.clearKeyPages(RedisKey.MY_AUTHOR_KEY + userID);
		if(!RedisManager.exists(RedisKey.APP_AUTHOR_FANS_KEY+authorID)){
			setAuthorFans(authorID);
		}else{
			RedisManager.srem(RedisKey.APP_AUTHOR_FANS_KEY+authorID, String.valueOf(userID));
		}
		return true;
	}
	
	
	
	/**
	 * 我的关注
	 */
	public boolean myAuthorView(int userID,int page)throws E5Exception{
		int count = ALIST_COUNT; //固定个数
		int start = page * count;
	
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		DBSession conn = null;
		IResultSet rs = null;
		
		JSONArray jsonArr = new JSONArray();
		try{
			conn = Context.getDBSession();
	
			StringBuilder sql = new StringBuilder();
			sql.append("select fan_authorID from ");
			sql.append(LibHelper.getLibTable(DocTypes.FAN.typeID(), Tenant.DEFAULTCODE));
			sql.append(" where fan_id = ? and SYS_DELETEFLAG = 0 order by SYS_DOCUMENTID desc");
	
			String sqlStr = conn.getDialect().getLimitString(sql.toString(), start, count);
			rs = conn.executeQuery(sqlStr,new Object[]{userID});
			int userExtLibID = LibHelper.getUserExtLibID();
			while(rs.next()){
				doc = docManager.get(userExtLibID, rs.getLong("fan_authorID"));
				if (doc != null) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("id", doc.getLong("SYS_DOCUMENTID"));
					jsonObj.put("name", doc.getString("u_name"));
					jsonObj.put("url", doc.getString("u_iconUrl"));
					jsonObj.put("duty", doc.getString("u_duty"));
					jsonObj.put("description", doc.getString("u_comment"));
					
					jsonArr.add(jsonObj);
				}
			}
			RedisManager.setLonger(RedisKey.MY_AUTHOR_KEY  + userID + "." + page, jsonArr.toString());
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	/**
	 * 判断是否已关注
	 */
	public boolean setAuthorFans(int authorID){
		String key = RedisKey.APP_AUTHOR_FANS_KEY+authorID ;
		if(!RedisManager.exists(key)){
			DBSession conn = null;
			IResultSet rs = null;
			try {
				conn = Context.getDBSession();
				StringBuilder sql = new StringBuilder();
				sql.append("select fan_id from ");
				sql.append(LibHelper.getLibTable(DocTypes.FAN.typeID(), Tenant.DEFAULTCODE));
				sql.append(" where fan_authorID = ? and SYS_DELETEFLAG = 0 order by SYS_DOCUMENTID desc");
				rs = conn.executeQuery(sql.toString(),new Object[]{authorID});
				while(rs.next()){
					RedisManager.sadd(key, rs.getInt("fan_id"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(rs);
				ResourceMgr.closeQuietly(conn);
			}
		}
		if(!RedisManager.exists(key)||RedisManager.smembers(key).size()<1){
			RedisManager.sadd(key, -1);
		}
		RedisManager.setTime(key, RedisManager.week1);
		return true ;
	}
}
