package com.founder.xy.api.imp;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.permission.PermissionCache;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.workspace.service.ToolkitServiceArticleApp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 稿件api的辅助类。 从数据库读稿件，并组织成列表api可用的稿件json、详情api可用的稿件json
 * 
 */
public class ArticleDetailHelper {
	/**
	 * 组织一篇稿件详情的json
	 */
	public static JSONObject articleDetail(int docLibID, long docID, int userID)
			throws E5Exception {

		JSONObject result = new JSONObject();
		//JSONArray articles = new JSONArray();
		JSONObject article = new JSONObject();
		JSONArray operations = new JSONArray();
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			if (doc == null) {
				result.put("success", false);
				result.put("errorInfo", "稿件不存在！");
				result.put("article", article);
				result.put("operation", operations);
			} else {
				JSONArray imageArray = new JSONArray();
				JSONArray videoArray = new JSONArray();
				JSONArray related = new JSONArray();

				// 设置基础属性
				setBasicField(article, doc);
				
				// 读出附件表的附件
				Document[] atts = readAtts(doc);
				// 处理内容
				setContent(article, doc); // ?
				// 图片
				setImageJsonArray(imageArray, atts); // ?
				// 视频
				setVideoJsonArray(videoArray, atts); // ?
				// 操作权限
				setOperationJsonArray(operations, doc, userID);
				//相关稿件
				setRelationJson(related, docLibID, docID);
				
				article.put("imageArray", imageArray);
				article.put("videoArray", videoArray);
				article.put("related", related);
				//articles.add(article);
				result.put("success", true);
				result.put("errorInfo", "");
				result.put("article", article);
				result.put("operation", operations);
			}
		} catch (E5Exception e) {
			result.put("success", false);
			result.put("errorInfo", e.getMessage());
			result.put("article", article);
			result.put("operation", operations);
		}
		return result;
	}

	/**
	 * 设置基本属性
	 */
	private static void setBasicField(JSONObject article, Document doc) {
		article.put("fileId", doc.getDocID());// 稿件ID
		article.put("docLibID", doc.getDocLibID());// 稿件库ID
		article.put("title", doc.getTopic());
		article.put("articleType", doc.getInt("A_TYPE"));
		article.put("status", doc.getInt("A_STATUS"));
		article.put("siteID", doc.getInt("A_SITEID"));
		article.put("channel", doc.getInt("A_CHANNEL"));
		article.put("publishtime", getDateString(doc.getDate("A_PUBTIME")));
		article.put("editor", doc.getString("A_EDITOR"));
		article.put("author", doc.getAuthors());
		article.put("countClick", doc.getInt("A_COUNTCLICK"));
		article.put("wordCount", doc.getInt("A_WORDCOUNT"));
		article.put("linkID", doc.getInt("A_LINKID"));
		article.put("linkName", doc.getString("A_LINKNAME"));
		article.put("linkUrl", doc.getString("A_URL"));
		article.put("subtitle", doc.getString("A_SUBTITLE"));
		article.put("attAbstract", doc.getString("A_ABSTRACT"));
		article.put("introtitle", doc.getString("A_LEADTITLE"));
		article.put("linktitle", doc.getString("A_LINKTITLE"));
		article.put("mark", doc.getString("A_MARK"));
		article.put("discussClosed", doc.getInt("A_DISCUSSCLOSED"));
		article.put("isBigPic", doc.getInt("A_ISBIGPIC"));
		article.put("longitude", doc.getDouble("A_LONGITUDE"));
		article.put("latitude", doc.getDouble("A_LATITUDE"));
		article.put("location", getNotNull(doc.getString("A_LOCATION")));
		article.put("colID", doc.getInt("A_COLUMNID"));
		article.put("colName", doc.getString("A_COLUMN"));
		article.put("picSmall", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICSMALL")));
		article.put("picMiddle", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICMIDDLE")));
		article.put("picBig", ArticleDetailHelper.getWanPicPath(doc.getString("A_PICBIG")));
	}

	private static Document[] readAtts(Document doc) throws E5Exception {
		int attTypeID = (doc.getDocTypeID() == DocTypes.ARTICLE.typeID()) ? DocTypes.ATTACHMENT
				.typeID() : DocTypes.PAPERATTACHMENT.typeID();
		int attDocLibID = LibHelper.getLibIDByOtherLib(attTypeID,
				doc.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] atts = docManager.find(attDocLibID,
				"att_articleID=? and att_articleLibID=? ORDER BY att_order",
				new Object[] { doc.getDocID(), doc.getDocLibID() });
		return atts;
	}
	
	// 处理内容
	private static void setContent(JSONObject article, Document doc)
			throws E5Exception {
		String content = doc.getString("A_CONTENT");
		// 按稿件类型处理内容
		int type = doc.getInt("A_TYPE");
		if (type == 0) { // 文章稿
			String url = getWebRoot() + "getImage";
			//String regex = "\\.\\.\\/\\.\\.\\/xy\\/image\\.do";
			String regex = "../../xy/image.do";
			content = content.replaceAll(regex, url);
		}
		article.put("content", content); // 内容
	}

	private static void setImageJsonArray(JSONArray imageArray, Document[] atts) {
		Document[] docs = getAttsByType(atts, 0); // 图片
		if(docs != null && docs.length > 0){
			for(Document doc : docs){
				JSONObject image = new JSONObject();
				image.put("path", getWanPicPath(doc.getString("ATT_PATH")));
				image.put("content", getNotNull(doc.getString("ATT_CONTENT")));
				imageArray.add(image);
			}
		}
	}
	
	private static void setVideoJsonArray(JSONArray videoArray, Document[] atts) {
		Document[] docs = getAttsByType(atts, 1); // 视频
		if(docs != null && docs.length > 0){
			for(Document doc : docs){
				JSONObject video = new JSONObject();
				video.put("picPath", getWanPicPath(doc.getString("ATT_PICPATH")));
				video.put("url", getNotNull(doc.getString("ATT_URL")));
				video.put("urlPad", getNotNull(doc.getString("ATT_URLPAD")));
				video.put("duration", doc.getInt("ATT_DURATION"));
				videoArray.add(video);
			}
		}
		
	}
	
	private static Document[] getAttsByType(Document[] atts, int type) {
		List<Document> list = new ArrayList<>();
		
		for (int i = 0; i < atts.length; i++) {
			if (atts[i].getInt("att_type") == type)
				list.add(atts[i]);
		}
		return list.toArray(new Document[0]);
	}

	private static void setOperationJsonArray(JSONArray operations, Document doc, int userID) throws E5Exception {
		int userLibID = LibHelper.getUserExtLibID();
		int siteID = doc.getInt("A_SITEID");
		if(siteID < 1){
			siteID = 1;
		}
		SiteUserReader siteRoleReader = (SiteUserReader) Context.getBean("siteUserReader");
		int[] roleIDs = siteRoleReader.getRoles(userLibID, userID, siteID);
		int newRoleID = (roleIDs.length==1) ? roleIDs[0] :
			((com.founder.e5.permission.merge.PermissionCache)CacheReader.find(PermissionCache.class)).merge(roleIDs);
		operations.addAll(new ToolkitServiceArticleApp()
			.getProcList(newRoleID, doc.getDocLibID(), doc.getCurrentFlow(), doc.getCurrentNode()));
		
		boolean isPubStatus = doc.getInt("a_status") == Article.STATUS_PUB_DONE;
		int docLibID = doc.getDocLibID();
		int web = LibHelper.getArticleLibID();
		for(int i = 0,len = operations.size(); i < len;){
			JSONObject oper = operations.getJSONObject(i);
			//修改流程记录的权限name
			if(oper.getString("code").equals("record")){
				operations.remove(i);
				oper.put("name", "流程记录");
				operations.add(oper);
				len--;
				continue;
			}
			//已发布稿件修改的name改为重改
			if(isPubStatus && oper.getString("code").equals("edit")){
				operations.remove(i);
				oper.put("name", "重改");
				operations.add(oper);
				len--;
				continue;
			}
			if(oper.getString("code").equals("pushapp")){
				if(docLibID == web){
					operations.remove(i);
					len--;
					continue;
				}
			}
			i++;
		}
	}

	/**
	 * 设置相关稿件属性
	 */
	public static void setRelationJson(JSONArray related, int docLibID, long docID) throws E5Exception {
		String tenantCode = Tenant.DEFAULTCODE;
		int attLibId = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(), tenantCode);
		int videlLibId = LibHelper.getLibID(DocTypes.VIDEO.typeID(), tenantCode);
		// 读出相关稿件
		int artRelLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEREL.typeID(), docLibID);
		
		String sql = "select distinct xaS.att_urlPad picS,xaM.att_urlPad picM,xaB.att_urlPad picB,xv.v_time,"
				+ "xaa.a_relID,xaa.a_relLibID,xaa.SYS_TOPIC,xaa.a_url,xaa.a_source,xaa.a_sourceID,xaa.a_pubTime,"
				+ "xaa.a_columnID,xaa.a_type,xa.a_countDiscuss,xa.a_countPraise,xa.a_countShare,a_countClick,"
				+ "xa.a_countClickInitial,xa.a_siteID"
				+ " from "
				+ LibHelper.getLibTable(artRelLibID)
				+ " xaa "
				+ " left join "
				+ LibHelper.getLibTable(attLibId)
				+ " xaS on xaS.att_articleID = xaa.a_relID and xaS.att_articleLibID = xaa.a_articleLibID and xaS.att_type = 4"
				+ " left join "
				+ LibHelper.getLibTable(attLibId)
				+ " xaM on xaM.att_articleID = xaa.a_relID and xaM.att_articleLibID = xaa.a_articleLibID and xaM.att_type = 3"
				+ " left join "
				+ LibHelper.getLibTable(attLibId)
				+ " xaB on xaB.att_articleID = xaa.a_relID and xaB.att_articleLibID = xaa.a_articleLibID and xaB.att_type = 2"
				+ " left join "
				+ LibHelper.getLibTable(attLibId)
				+ " xaVideo on xaVideo.att_articleID = xaa.a_relID and xaVideo.att_articleLibID = xaa.a_articleLibID and xaVideo.att_type = 1"
				+ " left join "
				+ LibHelper.getLibTable(videlLibId)
				+ " xv on xv.SYS_DOCUMENTID = xaVideo.att_objID and xv.SYS_DOCLIBID = xaVideo.att_objLibID"
				+ " left join "
				+ LibHelper.getLibTable(docLibID)
				+ " xa on xa.SYS_DOCUMENTID = xaa.a_relID "
				+ " where xaa.a_articleID = ? and xaa.a_articleLibID = ? and xa.a_status = 1";

		Object[] params = new Object[] { docID, docLibID};
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			while (rs.next()) {
				JSONObject date = new JSONObject();
				date.put("relId", rs.getLong("a_relID")); // 相关ID
				date.put("fileId", rs.getLong("a_relID")); // 为与redis中的互动计数比较而添加的冗余字段
				date.put("title", StringUtils.getNotNull(rs.getString("SYS_TOPIC"))); // 附件描述
				date.put("textTitle",StringUtils.xhtml2Text(rs.getString("SYS_TOPIC")));
				date.put("publishtime",InfoHelper.formatDate(rs,"a_pubTime"));

				date.put("source", StringUtils.getNotNull(rs.getString("a_source"))); // 相关稿件来源
				date.put("sourceID", rs.getInt("a_sourceID")); // 相关稿件来源ID
				date.put("picB", StringUtils.getNotNull(rs.getString("picB")));
				date.put("picM", StringUtils.getNotNull(rs.getString("picM")));
				date.put("picS", StringUtils.getNotNull(rs.getString("picS")));

				date.put("countDiscuss", StringUtils.getNotNull(rs.getString("a_countDiscuss"))); // 评论数
				date.put("countPraise", StringUtils.getNotNull(rs.getString("a_countPraise"))); // 点赞数
				date.put("countShare", StringUtils.getNotNull(rs.getString("a_countShare"))); // 分享数
				date.put("countClick", StringUtils.getNotNull(rs.getString("a_countClick"))); // 点击数
				date.put("countClickInitial", StringUtils.getNotNull(rs.getString("a_countClickInitial"))); // 初始点击数

				date.put("articleType", rs.getInt("a_type"));
				date.put("vTime", StringUtils.getNotNull(rs.getString("v_time")));
				date.put("relUrl", StringUtils.getNotNull(rs.getString("a_url"))); // URL
				date.put("contentUrl", UrlHelper.getArticleContentUrl(rs.getLong("a_relID")));

				date.put("siteID", rs.getInt("a_siteID"));
				date.put("docLibID", rs.getInt("a_relLibID"));
				date.put("colID", rs.getLong("a_columnID"));
				date.put("channel", rs.getInt("a_relLibID")==LibHelper.getArticleAppLibID()?2:1);
				
				related.add(date);// 相关稿件
			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

	}
	
	private static String getNotNull(String value) {
		if (value == null)
			return "";
		return value;
	}

	public static String getDateString(Date value) {
		if (value == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(value);
	}
	/** 改为外网路径 */
	public static String getWanPicPath(String picPath) {
		if(StringUtils.isBlank(picPath)) return "";
		return getWebRoot() + "/getImage?path=" + picPath;
	}
	
	private static String getWebRoot(){
		return InfoHelper.getConfig("互动", "外网Api地址");
	}
}
