package com.founder.xy.api.paper;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.founder.xy.commons.InfoHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.system.Tenant;
/**
 * 与外网api通讯的基础Api，主要是数字报相关功能
 */
@Service
public class PaperApiManager {
	/**
	 * 根据站点查出报纸
	 */
	public boolean getPapers(int siteId) throws E5Exception{
		String tenantCode = Tenant.DEFAULTCODE;
		int paperLibId = LibHelper.getLibID(DocTypes.PAPER.typeID(), tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Object[] params = new Object[] {siteId};

		Document[] docs = docManager.find(paperLibId, 
				"pa_siteID=? and pa_status=0 and SYS_DELETEFLAG=0 order by pa_order", params);
		
		JSONObject res = new JSONObject();
		JSONArray papers = new JSONArray();
		Timestamp timestamp = new Timestamp(0l);
		for(int i=0;i<docs.length;i++){
			Document doc = docs[i];
			JSONObject paper = new JSONObject();
			paper.put("id", doc.getInt("SYS_DOCUMENTID"));
			paper.put("name", StringUtils.getNotNull(doc.getString("pa_name")));
			paper.put("code", StringUtils.getNotNull(doc.getString("pa_code")));
			paper.put("iconSmall", StringUtils.getNotNull(doc.getString("pa_iconSmall")));
			paper.put("iconBig",StringUtils.getNotNull(doc.getString("pa_iconBig")));
			Timestamp t = doc.getTimestamp("SYS_LASTMODIFIED");
			timestamp = t.after(timestamp)?t:timestamp;
			papers.add(paper);
		}
		res.put("version", timestamp.getTime());
		res.put("papers", papers);
		
		RedisManager.setTimeless(RedisKey.APP_PAPER_KEY+siteId, res.toString());
	
		return true;
	}
	/**
	 * 根据报纸id获取，报纸最新的N个期次
	 */
	public boolean getPaperDates(int siteId,int paperId,int start,int count) throws E5Exception{
		
		String tenantCode = Tenant.DEFAULTCODE;
		int paperDateLibId = LibHelper.getLibID(DocTypes.PAPERDATE.typeID(), tenantCode);
		
		String sql = "select pd_date from "+LibHelper.getLibTable(paperDateLibId)
				+" where pd_paperID=? and pd_status=1 and SYS_DELETEFLAG=0 order by pd_date desc";
		Object[] params = new Object[] {paperId};
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			String querySql = conn.getDialect().getLimitString(sql, start, count);
			
			rs = conn.executeQuery(querySql, params);
			
			JSONObject result = new JSONObject();
			JSONArray dates = new JSONArray();
			long version = 0;
			while(rs.next()){
				JSONObject date = new JSONObject();
				Date pdDate = rs.getDate("pd_date");
				date.put("date", DateUtils.format(pdDate, "yyyyMMdd"));
				long time = pdDate.getTime();
				version = time > version ? time:version; 
				dates.add(date);
			}
			result.put("version", version);
			result.put("dates", dates);
			
			RedisManager.setLonger(RedisKey.APP_PAPER_DATE_KEY+paperId, result.toString());
			
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return true;
	}
	/**
	 * 获取一期报纸下的所有版面
	 */
	public String getPaperLayouts(
			int siteId,int paperId,String date) throws E5Exception{
		if(date == null||"null".equals(date)||"".equals(date)){
			date = getLastPaperDate(paperId);
		}
        System.out.println(date+"#########"+paperId);
		String key = RedisKey.APP_PAPER_LAYOUT_KEY + paperId + "." + date;
		if(RedisManager.exists(key)) return date;
		String tenantCode = Tenant.DEFAULTCODE;
		int layoutLibId = LibHelper.getLibID(DocTypes.PAPERLAYOUT.typeID(), tenantCode);
		int attLibId = LibHelper.getLibID(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
        System.out.println(date+"!!!!!!!!"+paperId);

        Object[] params = new Object[] {DateUtils.parse(date, "yyyyMMdd"), paperId};
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		String conditions = "pl_date=? and pl_paperID=? and pl_status=1 order by ";
		String sortFlag = InfoHelper.getConfig("数字报", "版面排序");
		if(sortFlag.equals("是")){
			conditions += "pl_order";
		}else {
			conditions += "SYS_DOCUMENTID";
		}
		Document[] docs = docManager.find(layoutLibId, conditions, params);
		
		JSONObject result = new JSONObject();
		JSONArray layouts = new JSONArray();
		long version = 0;
		for(int i=0;i<docs.length;i++){
			JSONObject layout = new JSONObject();
			Document doc = docs[i];
			if (version == 0) {
				version = doc.getDate("pl_date").getTime();
			}
			int layoutId = doc.getInt("SYS_DOCUMENTID");
			layout.put("id", layoutId);
			layout.put("name", doc.getString("pl_layout")+" "+doc.getString("pl_layoutName"));
			layout.put("width", doc.getInt("pl_width"));
			layout.put("height", doc.getInt("pl_height"));
			layout.put("mapping", StringUtils.getNotNull(doc.getString("pl_mapping")));
			layout.put("url",StringUtils.getNotNull(doc.getString("pl_url")));
			layout.put("urlPad",StringUtils.getNotNull(doc.getString("pl_urlPad")));
			setLayoutAtts(layout, layoutId, layoutLibId, attLibId);
			
			layouts.add(layout);
		}
		result.put("version", version);
		result.put("date",date);
		result.put("layouts", layouts);
		RedisManager.setLonger(key, result.toString());
		
		return date;
	}
	/**
	 * 按版次获取稿件列表
	 */
	public boolean getPaperArticles(int siteId,int layoutId) throws E5Exception{
		String tenantCode = Tenant.DEFAULTCODE;
		int articleLibId = LibHelper.getLibID(DocTypes.PAPERARTICLE.typeID(), tenantCode);
		int attLibId = LibHelper.getLibID(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
		String sql = "select SYS_DOCUMENTID from " + LibHelper.getLibTable(articleLibId)
				+" where a_layoutID=? and a_status<7 and SYS_DELETEFLAG=0 order by a_order";
		
		Object[] params = new Object[] {layoutId};
		List<Long> docIDs = queryArticleIds(sql,params);
		JSONArray listArticles = listArticles(siteId, docIDs, articleLibId, attLibId);

		RedisManager.setLonger(RedisKey.APP_PAPER_ARTICLELIST_KEY+layoutId, listArticles.toString());
		
		return true;
	}
	/**
	 * 获取稿件详情
	 */
	public boolean getPaperArticle(int siteId ,int articleId) throws E5Exception{
		String tenantCode = Tenant.DEFAULTCODE;
		int articleLibId = LibHelper.getLibID(DocTypes.PAPERARTICLE.typeID(), tenantCode);
		int attLibId = LibHelper.getLibID(DocTypes.PAPERATTACHMENT.typeID(), tenantCode);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(articleLibId, articleId);
		
		JSONObject json = new JSONObject();
		
		//设置稿件的基础属性
		setBasicField(json,doc);
		
		//设置稿件的附件属性
		setAttachment(json,doc,attLibId);

		RedisManager.setLonger(RedisKey.APP_PAPER_ARTICLE_KEY+articleId, json.toString());
		return true;
	}
	/**
	 * 获取版面图、pdf的url
	 */
	private void setLayoutAtts(JSONObject layout, int layoutId, int layoutLibId, int attLibId) throws E5Exception{
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attLibId, "att_articleID=? and att_articleLibID=?",
				new Object[] {layoutId, layoutLibId});
		for (Document att : docs) {
			String url = att.getString("att_urlPad");
			if (att.getInt("att_type") == 5)
				layout.put("picUrl", StringUtils.getNotNull(url));
			else if (att.getInt("att_type") == 6)
				layout.put("pdfUrl", StringUtils.getNotNull(url));
		}
	}
	/**
	 * 查一版面下的稿件ids
	 */
	private List<Long> queryArticleIds(String sql , Object[] params) throws E5Exception{
		List<Long> docIDs = new ArrayList<Long>();
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			while (rs.next()) {
				docIDs.add(rs.getLong("SYS_DOCUMENTID"));
			}
		} catch (Exception e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		return docIDs;
	}
	/**
	 * 稿件列表的json串
	 */
	private JSONArray listArticles(int siteID, List<Long> docIDs,int articleLibId,int attLibId) throws E5Exception{
		JSONArray jsonArray = new JSONArray();
		for (long docID : docIDs) {
			JSONObject inJson = listArticleOne(siteID, docID, articleLibId, attLibId);
			
			if (inJson != null)
				jsonArray.add(inJson);
		}
		return jsonArray;
	}
	/**
	 * 稿件列表的单个稿件json串
	 */
	private JSONObject listArticleOne(int siteID, Long docID,int articleLibId,int attLibId) throws E5Exception{
		JSONObject inJson = listArticleBaseField(siteID, docID,articleLibId);
		listArticleAttExtField(inJson,docID, articleLibId, attLibId);
		return inJson;
	}
	/**
	 * 稿件列表中稿件的基础字段json串
	 */
	private JSONObject listArticleBaseField(int siteID, Long docID,int artLibId) throws E5Exception{
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(artLibId, docID);
		if(doc == null) return null;
		JSONObject inJson = new JSONObject();
		inJson.put("fileId", docID);
		inJson.put("title", StringUtils.getNotNull(doc.getString("SYS_TOPIC")));
		inJson.put("version", doc.getLong("a_realPubTime"));

		inJson.put("introTitle",StringUtils.getNotNull(doc.getString("a_leadTitle")));
		inJson.put("attAbstract", StringUtils.getNotNull(doc.getString("a_abstract")));
		inJson.put("publishtime", InfoHelper.formatDate(doc.getTimestamp("a_pubTime")));
		inJson.put("articleType", doc.getInt("a_type"));
		inJson.put("shareUrl", StringUtils.getNotNull(doc.getString("a_urlPad")));
		inJson.put("tag", StringUtils.getNotNull(doc.getString("a_tag")));
		inJson.put("layout", doc.getString("a_layout"));
		inJson.put("layoutId", doc.getInt("a_layoutID"));
	
		String url = UrlHelper.getPaperArticleContentUrl(siteID, docID);
		inJson.put("contentUrl", url);
		
		//初始阅读数，用于外网api从缓存redis读实际阅读数时相加
		//显示阅读数：数据库中的初始阅读数+实际阅读数+分享页的阅读数。在外网api里会替换成实时的阅读数，取自redis缓存
		int countClickInitial = getInt(doc.getInt("a_countClickInitial"));
		inJson.put("countClickInitial", countClickInitial);
		inJson.put("countClick", getInt(doc.getInt("a_countClick")) + countClickInitial + getInt(doc.getInt("a_countShareClick")));
		inJson.put("countDiscuss", getlong(doc.getLong("a_countDiscuss")));
		inJson.put("countPraise", getlong(doc.getLong("a_countPraise")));
		inJson.put("countShare", getlong(doc.getLong("a_countShare")));

		inJson.put("iskeep", getlong(doc.getLong("SYS_ISKEEP")));
		return inJson;
	}
	
	/**
	 * 添加稿件列表附件json串
	 */
	private void listArticleAttExtField(JSONObject inJson,long docID,int articleLibId, int attLibId) throws E5Exception{
		String conditions = "att_articleID=? and att_articleLibID=? order by att_order";
		Object[] params = new Object[]{docID, articleLibId};
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attLibId, conditions, params);
		int length = 0;
		for (Document doc : docs){
			if (doc.getInt("att_type") == 0){
				String url = StringUtils.getNotNull(doc.getString("att_urlPad"));
				inJson.put("pic" + length++, url);
			}
		}
	}
	/**
	 * 设置稿件详情的基本字段
	 */
	private void setBasicField(JSONObject redisJson,Document doc){
		redisJson.put("fileId", StringUtils.getNotNull(doc.getString("SYS_DOCUMENTID"))); // 稿件ID
		redisJson.put("version", doc.getLong("a_realPubTime")); // 稿件最后修改时间
		redisJson.put("title", StringUtils.getNotNull(doc.getString("SYS_TOPIC"))); // 稿件标题
		redisJson.put("introTitle",StringUtils.getNotNull(doc.getString("a_leadTitle")));
		redisJson.put("attAbstract", StringUtils.getNotNull(doc.getString("a_abstract"))); // 稿件摘要
		redisJson.put("publishtime", InfoHelper.formatDate(doc.getTimestamp("a_pubTime"))); // 发布时间
		redisJson.put("source", StringUtils.getNotNull(doc.getString("a_source"))); // 来源
		redisJson.put("author", StringUtils.getNotNull(doc.getString("SYS_AUTHORS"))); // 作者
		redisJson.put("collaborator", StringUtils.getNotNull(doc.getString("a_collaborator")));
		redisJson.put("editor", StringUtils.getNotNull(doc.getString("a_editor"))); // 编辑
		redisJson.put("subtitle", StringUtils.getNotNull(doc.getString("a_subTitle"))); // 副题
		redisJson.put("layoutId", StringUtils.getNotNull(doc.getString("a_layoutID"))); // 版次id
		redisJson.put("layout", StringUtils.getNotNull(doc.getString("a_layout"))); //版次
		redisJson.put("discussClosed", doc.getInt("a_discussClosed"));
		redisJson.put("articleType", doc.getInt("a_type"));
		redisJson.put("shareUrl", StringUtils.getNotNull(doc.getString("a_urlPad")));//url，供分享
		redisJson.put("url", StringUtils.getNotNull(doc.getString("a_url")));//url，评论提交用
		redisJson.put("content", doc.getString("a_content"));
		redisJson.put("countPraise", doc.getString("a_countPraise"));
	}
	
	private void setAttachment(JSONObject json,Document doc,int attLibId) throws E5Exception{
		Object[] params = new Object[] {doc.getDocID(), doc.getDocLibID()};
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] atts = docManager.find(attLibId, 
				"att_articleID=? and att_articleLibID=? order by att_order", params);
		
		JSONArray attArr = new JSONArray();
		JSONArray images = new JSONArray();
		JSONObject imgObj = new JSONObject();
		JSONArray imgarr = new JSONArray();
		imgObj.put("ref","<!--IMAGES#1-->");
		for(int i=0;i<atts.length;i++){
			Document att = atts[i];
			JSONObject attach = new JSONObject();
			JSONObject img = new JSONObject();
			int type = att.getInt("att_type");
			String summary = StringUtils.getNotNull(att.getString("att_content"));
			String url = StringUtils.getNotNull(att.getString("att_urlPad"));
			attach.put("type", type);
			attach.put("content",summary);
			attach.put("url",url);
			String ref = "<!--IMAGEARRAY#"+i+"-->";
			img.put("ref",ref);
			img.put("picType",type);
			img.put("summary",summary);
			img.put("imageUrl",url);
			imgarr.add(img);
			attArr.add(attach);
		}
		imgObj.put("imagearray",imgarr);
		images.add(imgObj);
		json.put("images",images);
		json.put("attachment", StringUtils.getNotNull(attArr.toString()));
	}
	/**
	 * 查询报纸的最新刊期
	 */
	private String getLastPaperDate(int paperID) throws E5Exception{
		String date="";
		DBSession conn = null;
		IResultSet rs = null;
		String tenantCode = Tenant.DEFAULTCODE;
		String tabName = LibHelper.getLibTable(DocTypes.PAPERDATE.typeID(), tenantCode);
		try {
			String sql = "select MAX(pd_date) from "+ tabName +" where pd_paperID=? and pd_status=1 and SYS_DELETEFLAG=0";
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql,new Object[] {paperID});
			if(rs.next()){
				date = new SimpleDateFormat("yyyyMMdd").format(rs.getTimestamp(1));
			}
			
		} catch (Exception e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
		
		return date;
	}
	private static int getInt(int value) {
		if (value < 0) return 0;
		return value;
	}
	private static long getlong(long value) {
		if (value < 0) return 0;
		return value;
	}
}
