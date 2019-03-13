package com.founder.xy.api.imp;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.listpage.cache.ListMode;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.param.DocListParam;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.commons.*;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.e5.context.E5Exception;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;

import javax.net.ssl.HttpsURLConnection;


@Service
public class ImpApiManager extends AbstractArticleParser {

	@Autowired
	private ColumnReader colReader;
	@Autowired
	private ColumnManager colManager;
	@Autowired
	private ArticleManager articleManager;

//	private static int expireTime = 1;
	/**
	 * 获取子栏目信息  拼接XML
	 * @throws E5Exception 
	 */
	public String addChildrenNode(int eid, long time, String sign, String data) throws E5Exception {
		//打印参数
		System.out.println("method---->getSiteNodeTreeXml");
		System.out.println("eid---->"+eid);
		System.out.println("time---->"+time);
		System.out.println("sign---->"+sign);
		System.out.println("data---->"+data);
		//进行身份和安全认证
		String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
		if(authResult == null){
			//身份和安全认证成功，进行取栏目树业务
			JSONObject jsonObject = JSONObject.fromObject(data);
			int userID = jsonObject.optInt("userID",0);
			int siteID = jsonObject.getInt("siteID");
			int channel = jsonObject.getInt("Channel");
			int parentID = jsonObject.getInt("parentID");

			JSONObject ret = new JSONObject();

			List<Column> colList = null;
			int colLibID = LibHelper.getColumnLibID();
			if(parentID==0){
				colList = colReader.getRoot(siteID, colLibID, channel-1);
			}else{
				colList = colReader.getSub(colLibID, parentID);
			}

			//根据用户id判断，有权限的栏目才加入到JSON中
			JSONArray arr = new JSONArray();
			if(userID == 0){
				if (colList != null) {
					for(Column column:colList){
						JSONObject json = new JSONObject();
						json.put("id", String.valueOf(column.getId()));
						json.put("title", column.getName());
						json.put("casID", column.getCasIDs());
						int childcount = 0;
						List<Long> children = colManager.getChildrenIDs(column);
						if(children != null){
							childcount = children.size();
						}
						String isParent = childcount==0?"false":"true";
						json.put("isParent", isParent);
						json.put("children", childcount);

						arr.add(json);
					}
				}
			}else{
				Long[] ids = null;
				if(channel == 1){
					ids = colReader.getOpColumnIds(colLibID,userID,siteID,0);
				}else if(channel == 2){
					ids = colReader.getOpColumnIds(colLibID,userID,siteID,4);
				}
				List idLists = Arrays.asList(ids);

				if (colList != null) {
					for(Column column:colList){
						Long id = column.getId();
						if(idLists.contains(id)) {
							JSONObject json = new JSONObject();
							json.put("id", String.valueOf(column.getId()));
							json.put("title", column.getName());
							json.put("casID", column.getCasIDs());
							int childcount = 0;
							List<Long> children = colManager.getChildrenIDs(column);
							if(children != null){
								childcount = children.size();
							}
							String isParent = childcount==0?"false":"true";
							json.put("isParent", isParent);
							json.put("children", childcount);

							arr.add(json);
						}
					}
				}
			}

			ret.put("Column",arr);
			return ret.toString();
		} else {
			return authResult;
		}

	}

	public String addArticle(int eid, long time, String sign, String data) throws E5Exception {

		//打印参数
		System.out.println("method---->addArticle");
		System.out.println("eid---->"+eid);
		System.out.println("time---->"+time);
		System.out.println("sign---->"+sign);
		System.out.println("data---->"+data);
		//进行身份和安全认证
		String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
		if(authResult == null){
			//身份和安全认证成功，进行新增稿件业务
			System.out.println("operation:new");
//			System.out.println("articleInfo------------>"+data);
			String result = null;

			try {
				StorageDevice device = InfoHelper.getPicDevice();
				setStoreBasePath(InfoHelper.getDevicePath(device));

				String startTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
				setArticleTime(startTime);

				JSONObject jsonObject = JSONObject.fromObject(data);
				JSONArray array=jsonObject.getJSONArray("Article");
				if(array.size() <= 0 ){
					System.out.println("the record is null");
				}
				List<ImpResult> results = parseArticle(array, 1);

				result = convertReturn(results, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		} else {
			return authResult;
		}

	}

	public String updateArticle(int eid, long time, String sign, String data) throws E5Exception {

		//打印参数
		System.out.println("method---->updateArticle");
		System.out.println("eid---->"+eid);
		System.out.println("time---->"+time);
		System.out.println("sign---->"+sign);
		System.out.println("data---->"+data);
		//进行身份和安全认证
		String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
		if(authResult == null){
			//身份和安全认证成功，进行修改稿件业务
			System.out.println("operation:update");
//			System.out.println("articleInfo------------>"+data);
			String result = null;

			try {
				StorageDevice device = InfoHelper.getPicDevice();
				setStoreBasePath(InfoHelper.getDevicePath(device));

				String startTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
				setArticleTime(startTime);

				JSONObject jsonObject = JSONObject.fromObject(data);
				JSONArray array=jsonObject.getJSONArray("Article");
				if(array.size() <= 0 ){
					System.out.println("the record is null");
				}
				List<ImpResult> results = parseArticle(array, 2);

				result = convertReturn(results, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		} else {
			return authResult;
		}

	}

	public String delArticle(int eid, long time, String sign, String data) throws E5Exception {

		//打印参数
		System.out.println("method---->delArticle");
		System.out.println("eid---->"+eid);
		System.out.println("time---->"+time);
		System.out.println("sign---->"+sign);
		System.out.println("data---->"+data);
		//进行身份和安全认证
		String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
		if(authResult == null){
			//身份和安全认证成功，进行删除稿件业务
			System.out.println("operation:delete");
//			System.out.println("articleInfo------------>"+data);
			String result = null;

			try {
				StorageDevice device = InfoHelper.getPicDevice();
				setStoreBasePath(InfoHelper.getDevicePath(device));

				String startTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
				setArticleTime(startTime);

				JSONObject jsonObject = JSONObject.fromObject(data);
				JSONArray array=jsonObject.getJSONArray("Article");
				if(array.size() <= 0 ){
					System.out.println("the record is null");
				}
				List<ImpResult> results = parseArticle(array, 3);

				result = convertReturn(results, 3);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		} else {
			return authResult;
		}

	}

	public String getPubArticles(int eid, long time, String sign, String data) throws E5Exception {
		//打印参数
		System.out.println("method---->getPubArticles");
		System.out.println("eid---->"+eid);
		System.out.println("time---->"+time);
		System.out.println("sign---->"+sign);
		System.out.println("data---->"+data);
		//进行身份和安全认证
		String authResult = ExternalSystemAuth.extSystemAuth(eid, time, sign, data);
		if(authResult == null){
			//身份和安全认证成功，进行取稿件业务
			JSONObject jsonObject = JSONObject.fromObject(data);
			int type = jsonObject.optInt("type",100);
			int status = jsonObject.optInt("status",100);
			int columnID = jsonObject.optInt("columnID",0);
			int siteID = jsonObject.getInt("siteID");
			int channel = jsonObject.getInt("channel");
			int page = jsonObject.getInt("page");
			int pageSize = jsonObject.optInt("pageSize",20);
			String startTime = jsonObject.optString("startTime");
			String endTime = jsonObject.optString("endTime");

			JSONObject ret = new JSONObject();

			if(columnID == 0){
				StringBuffer tableName = new StringBuffer();
				if(channel == 1){
					tableName.append("xy_article");
				} else {
					tableName.append("xy_articleApp");
				}

				StringBuffer where = new StringBuffer();
				where.append(" where a_siteID=").append(siteID);
				if(status != 100){
					where.append(" AND a_status=").append(status);
				}
				if(type != 100){
					where.append(" AND a_type=").append(type);
				}
				if(!startTime.isEmpty() && !endTime.isEmpty()){
					where.append(" and a_pubTime between '").append(startTime).append("' and '").append(endTime).append("'");
				}
				where.append(" AND SYS_DELETEFLAG=0");

				StringBuffer countSql = new StringBuffer();
				countSql.append("select count(SYS_DOCUMENTID) from ");
				countSql.append(tableName);
				countSql.append(where);

				int count = getCount(countSql.toString());
				if(count == -1){
					ret.put("result","error");
					return ret.toString();
				}else if (count == 0) {
					ret.put("result","success");
					ret.put("page",0);
					ret.put("pagesize",pageSize);
					ret.put("pagecount",0);
					return ret.toString();
				}

				StringBuffer listSql = new StringBuffer();
				listSql.append("select SYS_DOCUMENTID,a_type,a_channel,a_attr,SYS_TOPIC,a_shortTitle,a_subTitle,a_abstract,a_wordCount,SYS_AUTHORS,SYS_AUTHORID,a_editor,a_lastPublish,a_lastPublishID,a_pubTime,a_url,a_urlPad,a_columnID,a_column,a_countClick,a_countDiscuss,a_countShare,a_countPraise,a_copyright,a_isExclusive,a_isSensitive from ");

				listSql.append(tableName);
				listSql.append(where);
				listSql.append(" order by a_pubTime desc ");

				int begin = (page-1)*pageSize;
				JSONArray arr = getDocList(begin, pageSize, listSql.toString());
				if(arr == null){
					ret.put("result","error");
					return ret.toString();
				}

				ret.put("result","success");
				ret.put("page",page);
				ret.put("pagesize",pageSize);
				ret.put("pagecount",count/pageSize+1);
				ret.put("articles",arr);
				return ret.toString();
			} else {
				StringBuffer tableName = new StringBuffer();
				StringBuffer relTableName = new StringBuffer();
				if(channel == 1){
					tableName.append("xy_article");
					relTableName.append("DOM_REL_Web");
				} else {
					tableName.append("xy_articleApp");
					relTableName.append("DOM_REL_App");
				}

				StringBuffer where = new StringBuffer();
				where.append(" where CLASS_1=").append(columnID);
				where.append(" AND a_siteID=").append(siteID);
				if(status != 100){
					where.append(" AND a_status=").append(status);
				}
				if(type != 100){
					where.append(" AND a_type=").append(type);
				}
				if(!startTime.isEmpty() && !endTime.isEmpty()){
					where.append(" and a_pubTime between '").append(startTime).append("' and '").append(endTime).append("'");
				}
				where.append(" AND SYS_DELETEFLAG=0");

				StringBuffer countSql = new StringBuffer();
				countSql.append("select count(SYS_DOCUMENTID) from ");
				countSql.append(relTableName);
				countSql.append(where);

				int count = getCount(countSql.toString());
				if(count == -1){
					ret.put("result","error");
					return ret.toString();
				}else if (count == 0) {
					ret.put("result","success");
					ret.put("page",0);
					ret.put("pagesize",pageSize);
					ret.put("pagecount",0);
					return ret.toString();
				}

				StringBuffer relListSql = new StringBuffer();
				relListSql.append("select SYS_DOCUMENTID from ");
				relListSql.append(relTableName);
				relListSql.append(where);
				relListSql.append(" order by a_order asc ");

				int begin = (page-1)*pageSize;
				String docIDs = getDocIDs(begin, pageSize, relListSql.toString());
				if(docIDs == null){
					ret.put("result","error");
					return ret.toString();
				}

				StringBuffer docWhere = new StringBuffer();
				docWhere.append(" where SYS_DOCUMENTID in (");
				docWhere.append(docIDs);
				docWhere.append(")");

				StringBuffer docListSql = new StringBuffer();
				docListSql.append("select SYS_DOCUMENTID,a_type,a_channel,a_attr,SYS_TOPIC,a_shortTitle,a_subTitle,a_abstract,a_wordCount,SYS_AUTHORS,SYS_AUTHORID,a_editor,a_lastPublish,a_lastPublishID,a_pubTime,a_url,a_urlPad,a_columnID,a_column,a_countClick,a_countDiscuss,a_countShare,a_countPraise,a_copyright,a_isExclusive,a_isSensitive from ");
				docListSql.append(tableName);
				docListSql.append(docWhere);

				JSONArray arr = getDocList(docListSql.toString(),columnID);
				if(arr == null){
					ret.put("result","error");
					return ret.toString();
				}

				ret.put("result","success");
				ret.put("page",page);
				ret.put("pagesize",pageSize);
				ret.put("pagecount",count/pageSize+1);
				ret.put("articles",arr);
				return ret.toString();
			}
		} else {
			return authResult;
		}
	}

	private int getCount(String sql) {
		DBSession db = null;
		IResultSet rs = null;
		int count = 0;
		try
		{
			db = Context.getDBSession();

			rs = db.executeQuery(sql, null);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			count = -1;
		}
		finally
		{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return count;
	}

	private JSONArray getDocList(int begin,int limit, String sql){
		DBSession db = null;
		IResultSet rs = null;
		JSONArray arr = new JSONArray();
		try
		{
			db = Context.getDBSession();
			sql = db.getDialect().getLimitString(sql, 0, begin+limit);

			rs = db.executeQuery(sql, null);

			int tmpBegin = 0;
			int colLibID = LibHelper.getColumnLibID();
			while (rs.next()){
				if (tmpBegin < begin) {
					tmpBegin++;
					continue;
				}
				tmpBegin++;
				if (tmpBegin > (begin+limit)) {
					break;
				}

				JSONObject jsonObj = new JSONObject();
				jsonObj.put("articleID", StringUtils.getNotNull(rs.getString("SYS_DOCUMENTID")));
				jsonObj.put("articleType", StringUtils.getNotNull(rs.getString("a_type")));
				jsonObj.put("channel", StringUtils.getNotNull(rs.getString("a_channel")));
				jsonObj.put("attr", StringUtils.getNotNull(rs.getString("a_attr")));
				jsonObj.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
				jsonObj.put("shortTitle", StringUtils.getNotNull(rs.getString("a_shortTitle")));
				jsonObj.put("subtitle", StringUtils.getNotNull(rs.getString("a_subTitle")));
				jsonObj.put("abstract", StringUtils.getNotNull(rs.getString("a_abstract")));
				jsonObj.put("wordCount", StringUtils.getNotNull(rs.getString("a_wordCount")));
				jsonObj.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
				jsonObj.put("authorID", StringUtils.getNotNull(rs.getString("SYS_AUTHORID")));
				jsonObj.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
				jsonObj.put("lastPublish", StringUtils.getNotNull(rs.getString("a_lastPublish")));
				jsonObj.put("lastPublishID", StringUtils.getNotNull(rs.getString("a_lastPublishID")));
				jsonObj.put("pubTime", StringUtils.getNotNull(rs.getString("a_pubTime")));
				jsonObj.put("url", StringUtils.getNotNull(rs.getString("a_url")));
				jsonObj.put("urlPad", StringUtils.getNotNull(rs.getString("a_urlPad")));

				Column column = colReader.get(colLibID,rs.getInt("a_columnID"));
				jsonObj.put("casIDs", column.getCasIDs());
				jsonObj.put("casNames", StringUtils.getNotNull(rs.getString("a_column")));
				jsonObj.put("colID", StringUtils.getNotNull(rs.getString("a_columnID")));
				jsonObj.put("colName", column.getName());

				jsonObj.put("countClick", StringUtils.getNotNull(rs.getString("a_countClick")));
				jsonObj.put("countDiscuss", StringUtils.getNotNull(rs.getString("a_countDiscuss")));
				jsonObj.put("countShare", StringUtils.getNotNull(rs.getString("a_countShare")));
				jsonObj.put("countPraise", StringUtils.getNotNull(rs.getString("a_countPraise")));
				jsonObj.put("copyright", StringUtils.getNotNull(rs.getString("a_copyright")));
				jsonObj.put("isExclusive", StringUtils.getNotNull(rs.getString("a_isExclusive")));
				jsonObj.put("isSensitive", StringUtils.getNotNull(rs.getString("a_isSensitive")));

				arr.add(jsonObj);
			}
			return arr;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
	}

	private String getDocIDs(int begin,int limit, String sql){
		DBSession db = null;
		IResultSet rs = null;
		JSONArray arr = new JSONArray();
		try
		{
			db = Context.getDBSession();
			sql = db.getDialect().getLimitString(sql, 0, begin+limit);

			rs = db.executeQuery(sql, null);

			int tmpBegin = 0;
			StringBuffer docIDs = new StringBuffer();
			while (rs.next()){
				if (tmpBegin < begin) {
					tmpBegin++;
					continue;
				}
				if(tmpBegin > begin){
					docIDs.append(",");
				}
				tmpBegin++;
				if (tmpBegin > (begin+limit)) {
					break;
				}
				docIDs.append(rs.getString("SYS_DOCUMENTID"));
			}
			return docIDs.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
	}

	private JSONArray getDocList(String sql,int columnID){
		DBSession db = null;
		IResultSet rs = null;
		JSONArray arr = new JSONArray();
		try
		{
			db = Context.getDBSession();

			rs = db.executeQuery(sql, null);

			int colLibID = LibHelper.getColumnLibID();
			Column column = colReader.get(colLibID,columnID);
			while (rs.next()){
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("articleID", StringUtils.getNotNull(rs.getString("SYS_DOCUMENTID")));
				jsonObj.put("articleType", StringUtils.getNotNull(rs.getString("a_type")));
				jsonObj.put("channel", StringUtils.getNotNull(rs.getString("a_channel")));
				jsonObj.put("attr", StringUtils.getNotNull(rs.getString("a_attr")));
				jsonObj.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
				jsonObj.put("shortTitle", StringUtils.getNotNull(rs.getString("a_shortTitle")));
				jsonObj.put("subtitle", StringUtils.getNotNull(rs.getString("a_subTitle")));
				jsonObj.put("abstract", StringUtils.getNotNull(rs.getString("a_abstract")));
				jsonObj.put("wordCount", StringUtils.getNotNull(rs.getString("a_wordCount")));
				jsonObj.put("author", StringUtils.getNotNull(rs.getString("SYS_AUTHORS")));
				jsonObj.put("authorID", StringUtils.getNotNull(rs.getString("SYS_AUTHORID")));
				jsonObj.put("editor", StringUtils.getNotNull(rs.getString("a_editor")));
				jsonObj.put("lastPublish", StringUtils.getNotNull(rs.getString("a_lastPublish")));
				jsonObj.put("lastPublishID", StringUtils.getNotNull(rs.getString("a_lastPublishID")));
				jsonObj.put("pubTime", StringUtils.getNotNull(rs.getString("a_pubTime")));
				jsonObj.put("url", StringUtils.getNotNull(rs.getString("a_url")));
				jsonObj.put("urlPad", StringUtils.getNotNull(rs.getString("a_urlPad")));

				jsonObj.put("casIDs", column.getCasIDs());
				jsonObj.put("casNames", StringUtils.getNotNull(rs.getString("a_column")));
				jsonObj.put("colID", StringUtils.getNotNull(rs.getString("a_columnID")));
				jsonObj.put("colName", column.getName());

				jsonObj.put("countClick", StringUtils.getNotNull(rs.getString("a_countClick")));
				jsonObj.put("countDiscuss", StringUtils.getNotNull(rs.getString("a_countDiscuss")));
				jsonObj.put("countShare", StringUtils.getNotNull(rs.getString("a_countShare")));
				jsonObj.put("countPraise", StringUtils.getNotNull(rs.getString("a_countPraise")));
				jsonObj.put("copyright", StringUtils.getNotNull(rs.getString("a_copyright")));
				jsonObj.put("isExclusive", StringUtils.getNotNull(rs.getString("a_isExclusive")));
				jsonObj.put("isSensitive", StringUtils.getNotNull(rs.getString("a_isSensitive")));

				arr.add(jsonObj);
			}
			return arr;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
	}

//	/**
//	 * 解析JSON 并进行稿件的增、删、改操作
//	 */
//	public String parseImpXml(int op, int userID, String articleInfo) {
//
//		System.out.println("articleInfo------------>"+articleInfo);
//		String result = null;
//		try {
//			//需要 读取翔宇图片存储  并设置setStoreBasePath("");
//			StorageDevice device = InfoHelper.getPicDevice();
//			setStoreBasePath(InfoHelper.getDevicePath(device));
//
//			String startTime = InfoHelper.getConfig("写稿", "稿件顺序起点日期");
//			setArticleTime(startTime);
//
//			JSONObject jsonObj = JSONObject.fromObject(articleInfo);
//			JSONArray array=jsonObj.getJSONArray("Article");
//			if(array.size() <= 0 ){
//				System.out.println("the record is null");
//			}
//			List<ImpResult> results = parseArticle(array, userID, op);
//
//			result = convertReturn(results, op);
//		}  catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	
	/**
	 * 循环解析Article 每一个对应一个ImpResult对象
	 * @throws Exception 
	 */
	private List<ImpResult> parseArticle(JSONArray array, int op) throws Exception {
		List<ImpResult> results = new ArrayList<>();

		int colLibID = LibHelper.getColumnLibID();
		for(int t=0; t<array.size(); ++t){
			JSONObject jsonObject = array.getJSONObject(t);
			//根据userID判断权限  如果没有当前栏目权限  解析下一个Article
			long columnID = jsonObject.getLong("ColumnID");
			int channel = jsonObject.getInt("Channel");
			int siteID = jsonObject.getInt("SiteId");
			int userID = jsonObject.getInt("userID");

			Long[] ids = null;
			if(channel == 1){
				ids = colReader.getOpColumnIds(colLibID,userID,siteID,0);
			}else if(channel == 2){
				ids = colReader.getOpColumnIds(colLibID,userID,siteID,4);
			}
			List idLists = Arrays.asList(ids);

			if(idLists.contains(columnID)){
				boolean hasAuth = true;
				String columnRelID = jsonObject.getString("ColumnRelID");
				if(columnRelID != null && !columnRelID.isEmpty()){
					String[] relIDs = columnRelID.split(";");
					for(String relID : relIDs){
						long relIDLong = Long.parseLong(relID);
						if(!idLists.contains(relIDLong)){
							hasAuth = false;
							break;
						}
					}
				}
				if(hasAuth){
					ImpResult impResult =  convertArticle(jsonObject, op);
					results.add(impResult);
				} else {
					ImpResult impResult = new ImpResult();

					impResult.setOriginalId(jsonObject.getInt("OriginalId"));
					impResult.setPublishId(jsonObject.getInt("ArticleId"));
					impResult.setType(jsonObject.getInt("ArticleType"));
					impResult.setChannel(channel);
					impResult.setPublish(jsonObject.getInt("Publish"));

					impResult.setArticle(null);
					impResult.setAttachList(null);
					impResult.setArticleRelIDs(null);

					impResult.setSuccess("false");
					impResult.setErrorCode("109");
					impResult.setErrorCause("关联栏目不存在或关联栏目无操作权限");

					results.add(impResult);
				}
			}else {
				ImpResult impResult = new ImpResult();

				impResult.setOriginalId(jsonObject.getInt("OriginalId"));
				impResult.setPublishId(jsonObject.getInt("ArticleId"));
				impResult.setType(jsonObject.getInt("ArticleType"));
				impResult.setChannel(channel);
				impResult.setPublish(jsonObject.getInt("Publish"));

				impResult.setArticle(null);
				impResult.setAttachList(null);
				impResult.setArticleRelIDs(null);

				impResult.setSuccess("false");
				impResult.setErrorCode("101");
				impResult.setErrorCause("主栏目不存在或主栏目无操作权限");

				results.add(impResult);

			}

		}
		return results;
	}

	private ImpResult convertArticle(JSONObject jsonObject, int op) throws Exception {
		
		int channel = jsonObject.getInt("Channel");
		int docLibID = 0;
		if(channel==1){
			docLibID = LibHelper.getArticleLibID();
		}else{
			docLibID = LibHelper.getArticleAppLibID();
		}
		
		ImpResult impResult = new ImpResult();
		impResult.setOriginalId(jsonObject.getInt("OriginalId"));
		impResult.setPublishId(jsonObject.getInt("ArticleId"));
		impResult.setType(jsonObject.getInt("ArticleType"));
		impResult.setChannel(channel);
		impResult.setPublish(jsonObject.getInt("Publish"));

		//取E5 稿件对象
		com.founder.e5.doc.Document article = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		long docID = jsonObject.getInt("ArticleId");

		if(docID == 0){
			// 取稿件ID
			docID = InfoHelper.getNextDocID(DocTypes.ARTICLE.typeID()); // 提前取稿件ID
			article = docManager.newDocument(docLibID,docID);
			ProcHelper.initDoc(article);
			impResult.setPublishId((int)docID);
		}else{
			article = docManager.get(docLibID, docID);
			if(article == null){
				impResult.setArticle(null);
				impResult.setAttachList(null);
				impResult.setArticleRelIDs(null);

				impResult.setSuccess("false");
				impResult.setErrorCode("102");
				impResult.setErrorCause("稿件ID错误");
				return impResult;

			}
		}

//		article.set("sys_documentid",jsonObject.getInt("ArticleId"));
		article.set("a_siteID", jsonObject.getInt("SiteId"));
		article.set("a_channel", channel);

		if(op==1 || op == 2){
			article.set("a_type", jsonObject.getInt("ArticleType"));
			article.set("sys_topic", jsonObject.getString("Title").replaceAll("[\n\r]", ""));
			article.set("a_linkTitle", jsonObject.getString("Title").replaceAll("[\n\r]", ""));
			article.set("a_subTitle", jsonObject.getString("Subtitle"));
			article.set("a_abstract", jsonObject.getString("Abstract"));
			article.set("a_keyword", jsonObject.getString("Keyword"));
			article.set("a_tag", jsonObject.getString("Tag"));
			if(op == 1){
				article.set("sys_created", jsonObject.getString("Nsdate"));
			}
			article.set("a_source", jsonObject.getString("Source"));
			article.set("sys_authors", jsonObject.getString("Author"));
			article.set("a_editor", jsonObject.getString("Editor"));
			article.set("a_liability", jsonObject.getString("Liability"));

			if(op == 2){
				if(jsonObject.getInt("ColumnID") != article.getInt("a_columnID")){
					impResult.setArticle(null);
					impResult.setAttachList(null);
					impResult.setArticleRelIDs(null);

					impResult.setSuccess("false");
					impResult.setErrorCode("107");
					impResult.setErrorCause("主栏目不可修改");
					return impResult;
				}
			}

			article.set("a_columnID", jsonObject.getInt("ColumnID"));

			int colLibID = LibHelper.getColumnLibID();
			String colName = getColumnName(colLibID, jsonObject.getInt("ColumnID"));
			article.set("a_column",colName);

			if(jsonObject.getString("ColumnRelID")!=null && !jsonObject.getString("ColumnRelID").equals("")){
				String [] relIDs = jsonObject.getString("ColumnRelID").split(";");
				StringBuffer relID = new StringBuffer();
				StringBuffer relName = new StringBuffer();
				for(int i = 0; i < relIDs.length; i++){
					if(i > 0){
						relID.append(",");
						relName.append(",");
					}
					String relNames = getColumnName(colLibID, Integer.parseInt(relIDs[i]));

					relID.append(relIDs[i]);
					relName.append(relNames);
				}
				article.set("a_columnRelID", relID.toString());
				article.set("a_columnRel", relName.toString());

				article.set("a_columnAll", jsonObject.getString("ColumnID")+";"+jsonObject.getString("ColumnRelID"));
			}else{
				article.set("a_columnAll", jsonObject.getString("ColumnID"));
			}
			article.set("a_content", jsonObject.getString("Content"));

			article.set("a_status", Article.STATUS_PUB_NOT);
			if(op==1){
				article.set("a_pubTime", DateUtils.getTimestamp());
			}

//			article.set("a_docLibID", docLibID);
//			article.set("a_isSensitive", 0);
//			article.set("a_templatePadID", WebUtil.getInt(request, "templatePadID", 0));
//			article.set("a_templateID", WebUtil.getInt(request, "templateID", 0));

//			String content = WebUtil.getStringParam(request, "Content");
//
//			if (content != null) {
//				//增加翔宇分页符
//				content = content.replaceAll("<hr>", "_ueditor_page_break_tag_");
//				content = content.replaceAll("<hr/>", "_ueditor_page_break_tag_");
//				article.set("a_content", content);
//
//			}

			try {
				convertTitleImage(SmallTitlePic, jsonObject.getString("SmallTitlePic"), article);
				convertTitleImage(MiddleTitlePic, jsonObject.getString("MiddleTitlePic"), article);
				convertTitleImage(BigTitlePic, jsonObject.getString("BigTitlePic"), article);
			} catch (Exception e) {
				e.printStackTrace();

				impResult.setArticle(null);
				impResult.setAttachList(null);
				impResult.setArticleRelIDs(null);

				impResult.setSuccess("false");
				impResult.setErrorCode("103");
				impResult.setErrorCause("获取标题图失败");
				return impResult;
			}

			JSONObject attachment = jsonObject.getJSONObject("Attachement");
			List<Document> attachList = null;
			try {
				attachList = convertAttachemnt(attachment, article);
			} catch (Exception e) {
				e.printStackTrace();

				impResult.setArticle(null);
				impResult.setAttachList(null);
				impResult.setArticleRelIDs(null);

				impResult.setSuccess("false");
				impResult.setErrorCode("104");
				impResult.setErrorCause("获取附件失败");
				return impResult;
			}
			impResult.setAttachList(attachList);

			String articleRel = jsonObject.getString("ArticleRel");
			if(articleRel != null && !articleRel.isEmpty()){
				String[] articleRelIDs = articleRel.split(",");
				impResult.setArticleRelIDs(articleRelIDs);
			}
		}else if(op == 3){
			//删除稿件 设置稿件DeleteFlag=1
			article.setDeleteFlag(1);
		}
		
		impResult.setArticle(article);
		
		return impResult;
	}

	private String getColumnName(int colLibID, long colID) throws E5Exception {
		Column col = colReader.get(colLibID, colID);
		return (col == null) ? "" : col.getName();
	}

	private List<com.founder.e5.doc.Document> convertAttachemnt(JSONObject attachment,
			com.founder.e5.doc.Document article) throws Exception {
		JSONArray files = attachment.getJSONArray("Attachfile");
		if(files.size()==0 && article.get("a_picSmall")==null
				&& article.get("a_picMiddle")==null
				&& article.get("a_picBig")==null){
			if(article.getString("a_type").equals("0")){
				article.set("SYS_HAVEATTACH",0);
			}
			return null;
		}
		List<com.founder.e5.doc.Document> newList = new ArrayList<>();
		//需要拿到xy_attachment的文档库
//		com.founder.e5.doc.Document attach = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());

		if(files != null){
			if(article.getString("a_type").equals("0")){
				if(files.size() == 0){
					article.set("SYS_HAVEATTACH",0);
				}else{
					article.set("SYS_HAVEATTACH",1);
				}

			}
			int index = 0;
			for(int t=0; t<files.size(); ++t){
				//每次循环 传建一个新的对象
//				attach = null;
				long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
				com.founder.e5.doc.Document attach = docManager.newDocument(attLibID, attDocID);
				
				JSONObject file = files.getJSONObject(t);
				boolean result = false;
				String type = attachment.getString("Type");
				if("0".equals(type)){
					result = executeArticleType(attach, file, article);
				}else if("1".equals(type)){
					result = executeMultiImageType(attach, file, index);
				}else if("2".equals(type)){
					result = executeVideoType(attach, file);
				}
				if(result){
					attach.set("att_articleID", article.getDocID()); // 所属稿件
					attach.set("att_articleLibID", article.getDocLibID());
					attach.set("att_order", index++);
					newList.add(attach);
				}
			}
			
		} else {
			if(article.getString("a_type").equals("0")){
				article.set("SYS_HAVEATTACH",0);
			}
		}

		insertTitlePicAtt(newList, article, 2);
		insertTitlePicAtt(newList, article, 3);
		insertTitlePicAtt(newList, article, 4);
		return newList;
	}

	/**
	 * 插标题图入附件表
	 * @param article
	 * @param type
	 */
	private void insertTitlePicAtt(List<com.founder.e5.doc.Document> newList, 
			com.founder.e5.doc.Document article, int type) throws E5Exception {
		Object titlePic = null;
		if(2 == type) titlePic = article.get("a_picBig");
		else if(3 == type) titlePic = article.get("a_picMiddle");
		else if(4 == type) titlePic = article.get("a_picSmall");
		if(titlePic != null){
			//需要拿到xy_attachment的文档库
//			com.founder.e5.doc.Document attach = null;
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), article.getDocLibID());
			long attDocID = InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID());
			com.founder.e5.doc.Document attach = docManager.newDocument(attLibID, attDocID);

			attach.set("att_articleID", article.getDocID()); // 所属稿件
			attach.set("att_articleLibID", article.getDocLibID());
			attach.set("att_type",type);
			attach.set("att_path",titlePic);
			newList.add(attach);
		}
	}
	
	private void convertTitleImage(String name,
			String picStr, com.founder.e5.doc.Document article) throws Exception {
		if(picStr.length()<=0){
			return;
		}

		//这里需要做一下兼容  1，base64格式  2，http链接
		//http连接需要先下载图片到翔宇
		if(picStr.startsWith("http")){

			String type=getImageType(picStr);
			FileNamePair fileNamePair = generateFilePath(type);

			downImageFromUrl(picStr,fileNamePair);

			if(SmallTitlePic.equals(name)){
				article.set("a_picSmall", fileNamePair.recordPath);
			}else if(MiddleTitlePic.equals(name)){
				article.set("a_picMiddle", fileNamePair.recordPath);
			}else if(BigTitlePic.equals(name)){
				article.set("a_picBig", fileNamePair.recordPath);
			}
		}
		else{
			byte[] bytes = Base64Decoder.decode(picStr);
			FileNamePair fileNamePair = generateFilePath(getImageType(bytes));
			FilePathUtil.write(bytes, fileNamePair.abstractPath);
			if(SmallTitlePic.equals(name)){
				article.set("a_picSmall", fileNamePair.recordPath);
			}else if(MiddleTitlePic.equals(name)){
				article.set("a_picMiddle", fileNamePair.recordPath);
			}else if(BigTitlePic.equals(name)){
				article.set("a_picBig", fileNamePair.recordPath);
			}
		}

	}

	
	/**
	 * 视频稿的附件处理
	 */
	protected boolean executeVideoType(com.founder.e5.doc.Document attachement,	JSONObject file){
		
		attachement.set("att_type", 1);
		//视频连接
		attachement.set("att_path",	file.getString("Multiattach"));
        attachement.set("att_url",	file.getString("Multiattach"));
		attachement.set("att_urlPad",	file.getString("Multiattach"));
		return true;
		
	}
	
	/**
	 * 组图稿的附件处理
	 */
	protected boolean executeMultiImageType(com.founder.e5.doc.Document attachement, 
			JSONObject file, int index) throws Exception{

		attachement.set("att_type",0);
		String fileCode = file.getString("Filecode");

		//这里需要做一下兼容  1，base64格式  2，http链接
		//http连接需要先下载图片到翔宇
		if(fileCode.startsWith("http")){

			String type=getImageType(fileCode);
			FileNamePair fileNamePair = generateFilePath(type);

			downImageFromUrl(fileCode,fileNamePair);

			attachement.set("att_path",fileNamePair.recordPath);
		}else{
			byte[] bytes = Base64Decoder.decode(fileCode);
			FileNamePair fileNamePair = generateFilePath(getImageType(bytes));
			FilePathUtil.write(bytes, fileNamePair.abstractPath);
			attachement.set("att_path",fileNamePair.recordPath);
		}
		attachement.set("att_content",file.getString("Attdesc"));
		return true;
		
	}
	
	/**
	 * 文章稿的附件处理
	 */
	protected boolean executeArticleType(com.founder.e5.doc.Document attachement, JSONObject file, 
			com.founder.e5.doc.Document article) throws Exception{
		
		attachement.set("att_type",0);
		
		FileNamePair fileNamePair = null;
		String fileName = file.getString("Filename");
		fileNamePair = generateFilePath(getImageType(fileName), true);
		attachement.set("att_path",fileNamePair.recordPath.replace("../../xy/image.do?path=", ""));
		String replCont = replaceAttPath(article.get("a_content").toString(), AttType.AttArticle, fileName, fileNamePair.recordPath);//将正文中的图片地址替换成E5中的地址
		article.set("a_content", replCont);
		String fileCode = file.getString("Filecode");//处理附件，保存，转换文件名

		if(fileCode.startsWith("http")){

			downImageFromUrl(fileCode,fileNamePair);

		}else{
			byte[] bytes = Base64Decoder.decode(fileCode);
			FilePathUtil.write(bytes, fileNamePair.abstractPath);
		}
		
		attachement.set("att_content",file.getString("Attdesc"));
		return true;
		
	}
	
	/**
	 * 操作数据库   推送发布   组装请求返回值
	 */
	private String convertReturn(List<ImpResult> results, int op) {
		ImpResult impResult = null;
		JSONObject ret = new JSONObject();
		JSONArray arr = new JSONArray();
		String result = null;
		String relResult = null;
		for (int i = 0; i < results.size(); i++) {
			impResult = results.get(i);
			//操作数据库  判断op值  对稿件和附件增删改
			//保存稿件之前，需要调用getNewOrder方法获取排序字段值
			//保存附件之后，需要调用extractingImg方法生成抽图文件信息
			Document article = impResult.getArticle();

			if(article!=null){
				if(op == 1||op == 2){
					if(op == 1){
						double order = getNewOrder(article);
						article.set("a_order", order);
					}

					List<com.founder.e5.doc.Document> attachList = impResult.getAttachList();

					result = save(article, attachList, op);

					if(result == null){
						//保存稿件成功以后才能保存相关稿件
						String[] articleRelIDs = impResult.getArticleRelIDs();

						relResult = saveRels(article, articleRelIDs, op);

						if(relResult == null){
							impResult.setSuccess("true");

							if(attachList != null){
								for(int j = 0; j < attachList.size(); j++){
									Document attach = attachList.get(j);
									extractingImg(attach.getString("att_path"));
								}
							}

							//推送发布
							if(impResult.getPublish() == 1){
								System.out.println("publish");
								article.set("a_status",Article.STATUS_PUB_ING);
//							if("1".equals(impResult.getPublish())){
								PublishTrigger.article(article);
							}
						} else {
							impResult.setSuccess("false");
							impResult.setErrorCode("108");
							impResult.setErrorCause("保存稿件成功但保存相关稿件失败");
						}
					}else{
						impResult.setSuccess("false");
						impResult.setErrorCode("105");
						impResult.setErrorCause("保存稿件失败");
					}

				} else if (op == 3){
					//删除稿件 设置稿件DeleteFlag=1
//					article.setDeleteFlag(1);
					result = save(article, null, op);
					if(result == null){
						impResult.setSuccess("true");
					}else{
						impResult.setSuccess("false");
						impResult.setErrorCode("106");
						impResult.setErrorCause("删除稿件失败");
					}

				}

			}
			 
			JSONObject json = new JSONObject();//组装返回值
			json.put("success", impResult.getSuccess());
			json.put("originalId", impResult.getOriginalId());
			json.put("type", impResult.getType());
			json.put("channel", impResult.getChannel());
			json.put("publishId", impResult.getPublishId());
			json.put("errorCode", impResult.getErrorCode());
			json.put("errorCause", impResult.getErrorCause());
			arr.add(json);
			
		}
		ret.put("ImpResult",arr);
		return ret.toString();
	}

	private String save(Document article, List<Document> attachList, int op) {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			if(op == 2){
				int docLibID = article.getDocLibID();
				long docID = article.getDocID();
				Document[] oldAttachments = articleManager.getAttachments(docLibID, docID);
				// 去掉不用了的附件
				deleteOldPic(oldAttachments, conn);
			}

			docManager.save(article, conn);
			if(attachList!=null){
				for (Document attach : attachList) {
					docManager.save(attach, conn);
				}
			}

			// 提交transaction
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private void deleteOldPic(Document[] old, DBSession conn)
			throws E5Exception {
		if (old == null)
			return;

		DocumentManager docManager = DocumentManagerFactory.getInstance();

		for (int i = 0; i < old.length; i++) {
			if (old[i] != null) {
				docManager.delete(old[i].getDocLibID(), old[i].getDocID(), conn);

				// 若有对应的图片库数据，也删除（2015.7.6 实际上已经没有图片库对应了）
				if (old[i].getInt("att_type") == 0
						&& old[i].getInt("att_objID") > 0) {
					docManager.delete(old[i].getInt("att_objLibID"),
							old[i].getLong("att_objID"), conn);
				}
			}
		}
	}

	/**
	 * 保存相关稿件
	 */
	private String saveRels(Document article, String[] articleRelIDs, int op) {

		DBSession conn = null;
		try {
			conn = Context.getDBSession();
			conn.beginTransaction();

			if(op == 2){
				int docLibID = article.getDocLibID();
				long docID = article.getDocID();
				// 取出已有。注意这里使用了另一个session
				Document[] oldRels = articleManager.getRels(docLibID, docID);
				deleteOldRel(oldRels, conn);
			}

			if(articleRelIDs != null){
				for (String articleRelID : articleRelIDs) {
					addNewRel(article, articleRelID, conn);
				}
			}

			// 提交transaction
			conn.commitTransaction();
			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return "操作中出现错误：" + e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}

	}

	private void deleteOldRel(Document[] old, DBSession conn)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		for (int i = 0; i < old.length; i++) {
			docManager.delete(old[i].getDocLibID(), old[i].getDocID(), conn);
		}
	}

	private void addNewRel(Document article, String rel, DBSession conn) throws E5Exception {
		// 取出相关稿件的Document
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document relDoc = docManager.get(article.getDocLibID(), Long.parseLong(rel));

		// 组装新的相关稿件记录
		int docTypeID = DocTypes.ARTICLEREL.typeID();
		int docLibID = LibHelper.getLibIDByOtherLib(docTypeID, article.getDocLibID());
		//不传入conn，尽快释放表锁。
		long id = EUID.getID("DocID" + docTypeID);

		Document doc = docManager.newDocument(docLibID, id);

		int type = relDoc.getInt("a_type");

		doc.set("a_articleID", article.getDocID());
		doc.set("a_articleLibID", article.getDocLibID());
		doc.set("a_relID", relDoc.getDocID());
		doc.set("a_relLibID", article.getDocLibID());
		doc.setTopic(relDoc.getTopic());
		doc.set("a_pubTime", relDoc.getTimestamp("a_pubTime"));

		if (article.getInt("a_channel") == 1) {
			doc.set("a_url", relDoc.getString("a_url"));
			doc.set("a_urlPad",relDoc.getString("a_urlPad"));
		}
		else
			doc.set("a_url", relDoc.getString("a_urlPad"));

		// 若是专题或直播稿，则把linkID记录到相关稿件的url里
		doc.set("a_type", type);
//		if ((type == Article.TYPE_SPECIAL || type == Article.TYPE_LIVE) && relDoc.getInt("a_channel") == 2) {
//			doc.set("a_url", relDoc.getString("a_linkID"));
//		}
		doc.set("a_picBig", relDoc.getString("a_picBig"));
		doc.set("a_picMiddle", relDoc.getString("a_picMiddle"));
		doc.set("a_picSmall", relDoc.getString("a_picSmall"));
		doc.set("a_column", relDoc.getString("a_column"));
		doc.set("a_columnID", relDoc.getInt("a_columnID"));
		doc.set("a_source", relDoc.getString("a_source"));
		doc.set("a_sourceID", relDoc.getInt("a_sourceID"));
		doc.set("a_shortTitle",relDoc.getString("a_shortTitle"));

		docManager.save(doc, conn);
	}

	private void downImageFromUrl(String imageUrl, FileNamePair fileNamePair) throws Exception{
//		//设置网络代理
//		UeditorControl.setProxy();

//		// 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
//		String suffix = getSuffix(imagePath);
//		String picPath = InfoHelper.getPicSavePath(request) + suffix;
//
		// 开始存储到存储设备上
		StorageDevice device = InfoHelper.getPicDevice();
		InputStream is = null;
		StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
		try {
			URL url = new URL(imageUrl);
			if(imageUrl.startsWith("https")){
				//先忽略证书认证
				SslUtils.ignoreSsl();
				HttpsURLConnection conn= (HttpsURLConnection)url.openConnection();
				conn.setConnectTimeout(1000);
				conn.connect();
				is = conn.getInputStream();// 通过输入流获取图片数据
			}else{

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(1000);
				conn.connect();
				is = conn.getInputStream();// 通过输入流获取图片数据
			}

			sdManager.write(device, fileNamePair.storePath, is);
		} catch (Exception e) {
			System.out.println("转为本地图片时异常：" + e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} finally {
			ResourceMgr.closeQuietly(is);
		}

//		if (!StringUtils.isBlank(picPath)) {
//			//加抽图任务
//			InfoHelper.prepare4Extract(device, picPath);
//
//			picPath = "../../xy/image.do?path=" + device.getDeviceName() + ";" + picPath;
//			picPath = picPath.replaceAll("\\\\", "/");
//		}
//
//		JSONObject json = new JSONObject();
//		json.accumulate("picPath", picPath);
//		System.out.println("imagePath ==> " + imagePath);
//		System.out.println("picPath  ==> " + picPath);
//		InfoHelper.outputJson(json.toString(), response);
//
	}

}
