package com.founder.xy.article.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.e5.db.IResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.allmedia.difftrace.bean.TraceVersion;
import com.founder.allmedia.difftrace.version.ReviseManifest;
import com.founder.allmedia.difftrace.version.ReviseManifestImpl;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.context.EUID;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.DocID;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.dom.DocLib;
import com.founder.e5.flow.ProcManager;
import com.founder.e5.flow.ProcUnflow;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.permission.merge.PermissionCache;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.e5.workspace.param.ProcParam;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.trace.DocTraceUtils;
import com.founder.xy.article.trace.HTMLUtil;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.UrlHelper;
import com.founder.xy.commons.web.FilePublishHelper;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.system.site.SiteUserReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 写稿保存
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/article")
public class ArticleSubmitController {
	@Autowired
	private ArticleSaver saver;
	@Autowired
	private ArticleManager articleManager;
	@Autowired
	private ColumnReader colReader;
	@Autowired
	private ExtFieldReader extFieldReader;
	@Autowired
	private SiteUserManager userManager;

	private Log log;

	@RequestMapping(value = "ArticleSubmit.do")
	public void submit(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log = Context.getLog("xy");

		// 提交的json数据的格式：{form:{...}, widget:{},pics:{},videos:{}}
		JSONObject params = getParams(request);
		JSONObject form = JsonHelper.getJsonObject(params, "form");
		boolean toPublish = ("1".equals(JsonHelper.getString(params,"toPublish")));
		boolean jabbarArticle = ("true".equals(JsonHelper.getString(params,"jabbarArticle")));
		boolean needRevoke=false;
		List<ChannelArticle> articles = assembleArticles(form, params, request);
		if(articles.size()>0)
			 needRevoke = articles.get(0).doc.getInt("a_status")==Article.STATUS_PUB_DONE;
		//判断是否同步链接标题至关联稿件
		String sync = null;
		try{
			sync = form.getString("syncLinkTitle");
		}catch(Exception e){
			sync = "false";
		}
		boolean syncLinkTitle = "on".equals(sync);

		//beforeSave方法可能会改变稿件当前流程节点，如果流程节点改变了，在判断是否有权限时，则判断的是下一个流程节点下的权限而不是当前节点的，这样就会出错
        if(articles != null && articles.size()>0){
        	for(int i = 0 ; i < articles.size(); i++) {
        		Document doc0 = articles.get(i).doc;
        		doc0.set("historyCurrentNode", doc0.getString("SYS_CURRENTNODE"));
        		doc0.set("published",articles.get(0).doc.getInt("a_status")==Article.STATUS_PUB_DONE);//用来区分是已发布还是未发布，已发布的则操作是重改操作
        	}
        }

		// 补充其它信息。在组装稿件完毕后才调用，以免日志中记录这些修改
		beforeSave(articles, form, request, toPublish, syncLinkTitle);


		// 保存文章及相应的扩展字段等信息
		boolean isNew = "true".equals(form.getString("isNew"));
		for(ChannelArticle article:articles){
			//如果修改了发布时间，需要先撤掉现有的稿件
			if(article.logChanged.contains("发布时间")&&needRevoke){
				PublishHelper.revoke(PublishTrigger.getArticleMsg(article.doc));
			}
			//如果是源稿库新写稿  按源稿分类栏目设置流程 并保存剩余校对次数
			int catID = article.doc.getInt("a_catID");
			if(catID > 0){
				String tenantCode = InfoHelper.getTenantCode(request);
				if(isNew) articleManager.setFlowByCat(article.doc, tenantCode);
				//记录历史版本
		        articleManager.recordHistoryVersion(null, article.doc, 
		        		tenantCode, "保存", ProcHelper.getUser(request).getUserName());
			}
				
		}
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		//判断是否是直播稿件
		if(articles.get(0).doc.getInt("a_type") == 6 && !StringUtils.isBlank(articles.get(0).doc.getString("a_linkID"))){
			int liveId = articles.get(0).doc.getInt("a_linkID");
			int liveLibID = LibHelper.getLive();

			Document liveDoc = docManager.get(liveLibID, liveId);
			if (liveDoc != null) {
				articles.get(0).doc.set("a_liveStatus", liveDoc.getInt("a_status"));
			}
		}
		String result = save(articles, isNew, jabbarArticle);

		if (result == null) {
			// 写稿中点的是“发布”按钮 或者稿件之前是已发布状态，触发更新
			if (toPublish || needRevoke) {
				//稿件之前已发布，且流程为审核流程时，（重改后）直接点发布 是提交审核，此时需要撤稿
				triggerPublish(articles,needRevoke);
			}
			// 记录日志
			writeLog(articles, ProcHelper.getUser(request), isNew);

			// 返回
			InfoHelper.outputText("ok", response);
		} else {
			InfoHelper.outputText(result, response);
		}
	}



	/**
	 * 组装各渠道的稿件列表。 现在写稿时只有一个渠道了（或原稿），用此方法也是可以的。
	 */
	private List<ChannelArticle> assembleArticles(JSONObject form, JSONObject params, HttpServletRequest request) throws Exception {
		List<ChannelArticle> articles = new ArrayList<ChannelArticle>();

		ChannelArticle article = oneChannel(params, request);
		articles.add(article);
		
		long columnID = form.optLong("a_columnID", 0);
		if(columnID > 0) {
			int colLibID = LibHelper.getColumnLibID(request);
			Column column = colReader.get(colLibID, form.getLong("a_columnID"));

			long colID = column.getPushColumn();
            if(colID > 0 && form.getInt("a_channel") == 1 && form.getInt("a_type") != Article.TYPE_SPECIAL){

                //如果自动同步的app栏目已经删除，则不需要自动同步数据到这个app栏目下的对应稿件
                Column pushColumn = colReader.get(colLibID,colID);
                if(pushColumn!=null){
                    form.put("DocLibID", LibHelper.getArticleAppLibID());
                    form.put("a_columnID", colID);
                    form.put("a_column", column.getCasNames());
                    form.put("a_columnRel", "");
                    form.put("a_columnRelID", "");
                    form.put("a_channel", 2);
                    JSONObject appChannel = JSONObject.fromObject(params);
                    appChannel.put("form", form);
                    articles.add(oneChannel(appChannel, request));
                }
            }
		}
		
		return articles;
	}

	// 组装一个频道下的信息，包括稿件、扩展字段、附件、挂件
	private ChannelArticle oneChannel(JSONObject params, HttpServletRequest request) throws Exception {
		// 获得表单对象
		JSONObject form = JsonHelper.getJsonObject(params, "form");

		ChannelArticle article = new ChannelArticle();
		// 组装一个稿件
		int docLibID = JsonHelper.getInt(form, "DocLibID");
		long docID = JsonHelper.getLong(form, "DocID");

		boolean isNew = "true".equals(form.getString("isNew"));

		article.doc = oneArticle(docLibID, docID, isNew, form, request);
		if (!isNew) {
			// 记录稿件的修改详情
			String detail1 = LogHelper.whatChanged(article.doc);
			if (!StringUtils.isBlank(detail1)) {
				article.logChanged = detail1;
			}
		}
		article.procName = getProcName(request, JsonHelper.getString(form, "UUID"));

		// 获得这个稿件的扩展字段列表
		int colLibID = LibHelper.getColumnLibID(request);
		int colID = JsonHelper.getInt(form, "a_columnID");
		int groupID = (colID > 0 ? colReader.get(colLibID, colID).getExtFieldGroupID() : 0);

		article.extFields = extFields(docLibID, docID, isNew, form, groupID);
		// 挂件
		article.widgets = widgets(params);
		// 附件
		attachments(params, article);
		// 相关稿件
		article.rels = relArticles(params);

		return article;
	}

	/** 组装一个渠道的稿件 */
	private Document oneArticle(int docLibID, long docID, boolean isNew,
			JSONObject formObj, HttpServletRequest request) throws Exception {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document article = null;
		if (isNew) {
			article = docManager.newDocument(docLibID, docID);
			ProcHelper.initDoc(article, request);
		} else {
			article = docManager.get(docLibID, docID);
			if (article == null) {
				article = docManager.newDocument(docLibID, docID);
				ProcHelper.initDoc(article, request);
			}
		}
		int roleID = ProcHelper.getRoleID(request);
		saver.fillValues(article, formObj, roleID);

		// 设置发布时间
		setPubTime(article, formObj, isNew);

		// 链接/广告稿，界面上填的都是a_url。补充App Url
		int type = article.getInt("a_type");
		if (type == Article.TYPE_LINK || type == Article.TYPE_AD) {
			article.set("a_urlPad", article.getString("a_url"));
		}

		return article;
	}

	// 设置发布时间
	private void setPubTime(Document article, JSONObject form, boolean isNew) {
		// 若是新稿，或修改稿但无发布时间（原稿无发布时间，直接发布时需复制给发布库稿件）
		if (isNew || (article.getTimestamp("a_pubTime") == null)) {
			article.set("a_pubTime", DateUtils.getTimestamp());
		}
		// 定时发布
		if ("on".equals(JsonHelper.getString(form, "pubTimer"))) {
			String formatDate = form.getString("pubTime");
			if (!StringUtils.isBlank(formatDate)) {
				Timestamp pubTime = new Timestamp(DateUtils.parse(formatDate,
						"yyyy-MM-dd HH:mm").getTime());
				article.set("a_pubTime", pubTime);
				article.set("a_realPubTime", pubTime);
			}
		}
	}

	/**
	 * 组装 文章的 扩展字段列表
	 * 
	 * @author guzm
	 */
	private List<Document> extFields(int ArticleDocLibID, Long docID,
			boolean isNew, JSONObject form, int groupID) throws Exception {
		// 如果是自定义扩展栏目，使用传过来的groupid
		int _groupID = JsonHelper.getInt(form, "a_extFieldGroupID");
		if (_groupID > 0)
			groupID = _groupID;

		List<Document> extDocs = new ArrayList<>();
		if (groupID <= 0)
			return extDocs;

		// 如果有扩展字段就进行填充，否则就直接pass
		if (form.containsKey("hasExtfield")) {
			// 从缓存当中获得groupid相对应的set
			int docLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.EXTFIELD.typeID(), ArticleDocLibID);
			Set<ExtField> extFieldSet = extFieldReader.getFields(docLibID,
					groupID);

			// 如果有扩展字段，组装成document以便于保存save操作
			if (extFieldSet != null && !extFieldSet.isEmpty()) {
				Document extDoc = null;
				DocumentManager docManager = DocumentManagerFactory
						.getInstance();
				for (ExtField ef : extFieldSet) {
					// 为节省表空间，若扩展字段没填值，则不保存。
					String value = JsonHelper.getString(form, ef.getExt_code());
					if (!StringUtils.isBlank(value)) {
						long extID = InfoHelper
								.getNextDocID(DocTypes.ARTICLEEXT.typeID());
						int extLibID = LibHelper.getLibIDByOtherLib(
								DocTypes.ARTICLEEXT.typeID(), ArticleDocLibID);

						extDoc = docManager.newDocument(extLibID, extID);
						extDoc.set("ext_articleLibID", ArticleDocLibID);
						extDoc.set("ext_articleID", docID);
						extDoc.set("ext_code", ef.getExt_code());
						extDoc.set("ext_value", value);

						extDocs.add(extDoc);
					}
				}
			}
		}
		return extDocs;
	}

	// 组装挂件数据。格式为：widgets:{pic:<id>,picLib:<id>,video:<id>,videoLib:<id>,attachment:[{path:,
	// content:}…]}
	private Widget widgets(JSONObject jsonChannel) {
		JSONObject ws = JsonHelper.getJsonObject(jsonChannel, "widgets");
		if (ws == null || ws.isEmpty()) {
			return null;
		} else {
			Widget w = new Widget();
			// 组图挂件
			long id = JsonHelper.getLong(ws, "pic");
			if (id > 0)
				w.picID = new DocID(JsonHelper.getInt(ws, "picLib"), id);

			// 视频挂件
			id = JsonHelper.getLong(ws, "video");
			if (id > 0)
				w.videoID = new DocID(JsonHelper.getInt(ws, "videoLib"), id);

			// 投票挂件
			id = JsonHelper.getLong(ws, "vote");
			if (id > 0)
				w.voteID = new DocID(JsonHelper.getInt(ws, "voteLib"), id);
			// 附件挂件
			JSONArray atts = JsonHelper.getJsonArray(ws, "attachments");
			if (atts != null && !atts.isEmpty()) {
				w.attachments = new ArrayList<Pair>();
				for (Object att : atts) {
					JSONObject json = ((JSONObject) att);
					Pair oneAttach = new Pair(
							JsonHelper.getString(json, "path"),
							JsonHelper.getString(json, "content"));
					w.attachments.add(oneAttach);
				}
			}
			return w;
		}
	}

	// 组装正文的附件
	private void attachments(JSONObject jsonChannel, ChannelArticle article) throws E5Exception  {
		// 正文图片
		JSONArray pics = JsonHelper.getJsonArray(jsonChannel, "pics");
		if (pics != null && !pics.isEmpty()) {

			long groupID = getPicGroupID(article);

			article.pics = new ArrayList<Pic>();
			for (Object att : pics) {
				JSONObject jsonPic = (JSONObject) att;
				Pic pic = new Pic();
				pic.content = JsonHelper.getString(jsonPic, "content");
				pic.path = JsonHelper.getString(jsonPic, "path");
				pic.isIndexed = (JsonHelper.getInt(jsonPic, "isIndexed") == 0); // 0索引图，2其它单图
				pic.groupID = groupID;

				String picID = JsonHelper.getString(jsonPic, "pic");// 来自图片库时，图片ID
				if (!StringUtils.isBlank(picID)) {
					long[] ids = StringUtils.getLongArray(picID);
					pic.picID = new DocID((int) ids[0], ids[1]);
				}

				article.pics.add(pic);
			}
			if (article.doc.getInt("a_type") == Article.TYPE_ARTICLE)
				article.doc.set("SYS_HAVEATTACH", 1); //表示正文中带图
		} else {
			if (article.doc.getInt("a_type") == Article.TYPE_ARTICLE)
				article.doc.set("SYS_HAVEATTACH", 0); //表示正文中无图
		}
		// 正文视频
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		JSONArray atts = JsonHelper.getJsonArray(jsonChannel, "videos");
		if (atts != null && !atts.isEmpty()) {
			article.videos = new ArrayList<Video>();
			for (Object att : atts) {
				JSONObject jsonPic = (JSONObject) att;
				Video video = new Video();
				video.url = JsonHelper.getString(jsonPic, "url");
				video.urlApp = JsonHelper.getString(jsonPic, "urlApp");
				
				String picID = JsonHelper.getString(jsonPic, "videoID");// 来自视频库时，视频ID
				if (!StringUtils.isBlank(picID)) {
					long[] ids = StringUtils.getLongArray(picID);
					video.videoID = new DocID((int) ids[0], ids[1]);
					// 把视频的视频时长,关键帧图片地址放到附件里
					Document v = docManager.get(video.videoID.docLibID,video.videoID.docID);
					video.picPath = v != null ? v.getString("v_picPath") : "";
					video.duration = InfoHelper.parseTime(v != null ? v.getString("v_time") : null);
				}
				
				article.videos.add(video);
			}
		}else{
            JSONObject form = JsonHelper.getJsonObject(jsonChannel, "form");
            //如果是视频稿件，获取a_bigID字段;
            String type = form.getString("a_type");
            String bigID = "";
            if("2".equals(type)){
                try {
                    bigID = form.getString("a_bigID");
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!StringUtils.isBlank(bigID)){
                    article.videos = new ArrayList<Video>();
                    Video video = new Video();
                    video.url = bigID;
                    video.urlApp = bigID;
                    article.videos.add(video);
                }
            }
        }
		
		// 正文附件
		JSONArray files = JsonHelper.getJsonArray(jsonChannel, "files");
		if (files != null && !files.isEmpty()) {
			article.files = new ArrayList<Files>();
			for (Object att : files) {
				JSONObject jsonPic = (JSONObject) att;
				Files file = new Files();
				file.path = JsonHelper.getString(jsonPic, "path").replace("../../xy/file.do?path=", "");
				String picID = JsonHelper.getString(jsonPic, "file");// 来自附件库时，附件ID
				if (!StringUtils.isBlank(picID)) {
					long[] ids = StringUtils.getLongArray(picID);
					file.fileID = new DocID((int) ids[0], ids[1]);
				}
				article.files.add(file);
			}
		}
	}

	// 组装相关稿件数据。格式为：rels:[{id:, lib:}…]}
	private List<DocID> relArticles(JSONObject jsonChannel) {
		JSONArray atts = JsonHelper.getJsonArray(jsonChannel, "rels");
		if (atts == null)
			return null;

		List<DocID> rels = new ArrayList<DocID>();
		if (!atts.isEmpty()) {
			for (Object att : atts) {
				JSONObject json = ((JSONObject) att);
				DocID one = new DocID(JsonHelper.getInt(json, "lib"),
						JsonHelper.getLong(json, "id"));
				rels.add(one);
			}
		}
		return rels;
	}

	private long getPicGroupID(ChannelArticle article) {
		if (!article.doc.isNew()) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			try {
				int picLibID = LibHelper.getLibIDByOtherLib(
						DocTypes.PHOTO.typeID(), article.doc.getDocLibID());

				Document[] _pics = docManager.find(
						picLibID,
						"p_articleID=? and p_articleLibID=?",
						new Object[] { article.doc.getDocID(),
								article.doc.getDocLibID() });
				if (_pics != null && _pics.length > 0)
					return _pics[0].getLong("p_groupID");
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return InfoHelper.getNextDocID(DocTypes.PHOTO.typeID());
	}

	/**
	 * 新写稿保存之前，设置流程、状态、发布时间等
	 * @param syncLinkTitle 
	 */
	private void beforeSave(List<ChannelArticle> docs, JSONObject form,
			HttpServletRequest request, boolean toPublish, boolean syncLinkTitle) throws Exception {
		
		for(int i = 0 ; i < docs.size() ; i++) {
			Document doc0 = docs.get(i).doc;
			
			SysUser user = ProcHelper.getUser(request);
			doc0.setLastmodified(DateUtils.getTimestamp());
			doc0.setLocked(false);
			doc0.setCurrentUserName(user.getUserName());
			doc0.setCurrentUserID(user.getUserID());
			
			//是否文章中带图，若是则加一个标记，用SYS_HAVEATTACH字段保存
			if (doc0.getInt("a_type") == Article.TYPE_ARTICLE) {
				List<Pic> pics = docs.get(0).pics;
				if (pics != null && pics.size() > 0) {
					doc0.setHaveAttach(1);
				} else {
					doc0.setHaveAttach(0);
				}
			}
			//APP专题稿件设置栏目Icon和描述
			if(doc0.getInt("a_type") == Article.TYPE_SPECIAL && doc0.getInt("a_channel") == 2 ){
				setSpecialColumn(form,request);
			}
			if (toPublish)
				docs.get(0).procName += "并发布";
			
			boolean isNew = "true".equals(JsonHelper.getString(form, "isNew")); // 是否新写稿
			boolean isOriginal = (doc0.getDocTypeID() == DocTypes.ORIGINAL.typeID()); // 是否原稿
			int currentColID = JsonHelper.getInt(form, "currentColID");
			// 如来源为通讯员 则不作此处理
			if (!"3".equals(doc0.getString("a_sourceType"))) {
				setAuthor(doc0, user, isNew);// 保存前检查作者，设置写稿人ID、是否记者稿、部门、来源类型
			}
			setSourceID(request,doc0);// 根据来源名查找来源ID
			setSummary(doc0); // 若系统参数配置为自动提取摘要，则保存前对摘要为空的文档自动提取摘要
			setTitle(doc0); //处理短标题
			
			// 修改相关稿件的链接标题和短标题
			if(!isNew) modifyRelArticles(doc0);
			
			setTrace(doc0, isNew, "这是啥", user);
			
			if (isNew) {
				doc0.set("a_originalID", 0);
			}
			// 补充发布库的专有属性：所有栏目、稿件顺序、流程等
			if (!isOriginal) {
				fillChannelDoc(doc0, isNew, toPublish, user.getRoleID(), currentColID);
				if (!isNew && currentColID != 0) {
					articleManager.saveLinkTitle(doc0, currentColID, syncLinkTitle);
				}
			}
			// 若是原稿，并且点击了“发布”按钮，则复制渠道稿件
			if (isOriginal) {
				String tenantCode = InfoHelper.getTenantCode(request);
				copyChannelArticles(docs, form, tenantCode, isNew, user.getRoleID(), toPublish);
			}
			//直播稿件设置直播地址
			if (doc0.getInt("a_type") == Article.TYPE_LIVE ) {
				doc0.set("a_url", UrlHelper.getWebLiveShareUrl() + "/" + doc0.getDocID() + "/" + doc0.getLong("a_linkID"));
				doc0.set("a_urlPad", UrlHelper.getLiveShareUrl() + "/" + doc0.getDocID() + "/" + doc0.getLong("a_linkID"));
			}

			if(!isNew){
				//清空话题稿件列表缓存
				String querySql = "SELECT a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
				Object[] param = new Object[]{doc0.getDocID(),doc0.getInt("a_channel")};
				JSONArray topicsList = queryTopics(querySql, param);

				if(topicsList != null && topicsList.size() > 0){
					for(int j = 0;j < topicsList.size();j++){
						JSONObject topic = topicsList.getJSONObject(j);
						long topicID = topic.getLong("topicID");

						String key1 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(doc0.getInt("a_siteID")))
								+ (doc0.getInt("a_channel")-1) + "." + topicID + "."+0;
						String key2 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(doc0.getInt("a_siteID")))
								+ (doc0.getInt("a_channel")-1) + "." + topicID + "."+1;
						String key3 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(doc0.getInt("a_siteID")))
								+ (doc0.getInt("a_channel")-1) + "." + topicID + "."+2;
						String key4 = RedisManager.getKeyBySite(RedisKey.APP_ARTICLELIST_TOPIC_KEY,Integer.valueOf(doc0.getInt("a_siteID")))
								+ (doc0.getInt("a_channel")-1) + "." + topicID + "."+100;

						RedisManager.clearLongKeys(key1);
						RedisManager.clearLongKeys(key2);
						RedisManager.clearLongKeys(key3);
						RedisManager.clearLongKeys(key4);
					}
				}

				//删除话题
				String deleteSql = "delete from xy_topicrelart where a_articleID=? and a_channel=?";
				InfoHelper.executeUpdate(deleteSql, new Object[]{doc0.getDocID(),doc0.getInt("a_channel")});
			}
			//插入话题
			JSONArray topics = form.optJSONArray("a_topics");
			if(topics != null && topics.size()>0){
				String insertSql = "insert into xy_topicrelart(a_topicID,a_articleID,a_topicName,a_order,a_channel,a_siteID,a_type,a_status) values(?,?,?,?,?,?,?,?)";
				for(int j = 0;j<topics.size();j++) {
					JSONObject topic = topics.getJSONObject(j);
					InfoHelper.executeUpdate(insertSql, new Object[]{topic.getInt("id"),doc0.getDocID(),topic.getString("name"),
							doc0.getDouble("a_order"),doc0.getInt("a_channel"),doc0.getInt("a_siteID"),
							doc0.getInt("a_type"),doc0.getInt("a_status")});
				}
			}
		}
	}

	private JSONArray queryTopics(String sql, Object[] param) {
		JSONArray result = new JSONArray();

		DBSession db = null;
		IResultSet rs = null;
		try {
			db = Context.getDBSession();
			rs = db.executeQuery(sql, param);
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("topicID", rs.getLong("a_topicID"));
				json.put("topicName", rs.getString("a_topicName"));

				result.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		return result;
	}

	//APP专题稿件设置栏目Icon和描述
	private void setSpecialColumn(JSONObject form, HttpServletRequest request) throws E5Exception {
		String picPath = form.getString("columnIcon");
		String picURL = FilePublishHelper.pubAndGetUrl(form.getInt("a_siteID"),picPath);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		if(!StringUtils.isBlank(form.getString("a_linkID"))) {
			Document column = docManager.get(LibHelper.getColumnLibID(), form.getLong("a_linkID"));
			column.set("col_iconBig", picURL);
			column.set("col_iconSmall", picURL);
			column.set("col_description", form.getString("a_abstract"));
			docManager.save(column);
			PublishTrigger.column(column.getDocLibID(), column.getDocID());
			RedisManager.clear(RedisKey.APP_COL_KEY + column.getDocID());
		}
	}

	/**
	 * 保存前检查作者，设置写稿人ID、是否记者稿、部门、来源类型
	 */
	private void setAuthor(Document doc0, SysUser user, boolean isNew) {
		int userLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(),
				doc0.getDocLibID());

		String author = doc0.getAuthors();
		Document siteUser = null;

		// 写稿人ID
		if (user.getUserName().equals(author)) {
			// 作者是当前用户
			doc0.set("SYS_AUTHORID", user.getUserID());
		} else {
			// 作者名是单独填写的，按名字查用户表，加入能找到就设置当前的作者id，不默认设置当前用户，否则通讯员稿件无法筛选
			siteUser = userManager.getUserByName(userLibID, author,
					doc0.getInt("a_siteID"));
			if (siteUser != null) {
				doc0.set("SYS_AUTHORID", siteUser.getDocID());
			} else if(isNew){//只有新写稿 没设置作者时 才把作者设置为当前用户
				doc0.set("SYS_AUTHORID", user.getUserID());
			}
		}
		boolean isAutoAuthorID = "是".equals(InfoHelper.getConfig("写稿", "是否自动填充作者ID"));
		if(isAutoAuthorID && doc0.getLong("SYS_AUTHORID")==0){
			doc0.set("SYS_AUTHORID", user.getUserID());
		}
		// 设部门 、来源类型
		if (siteUser == null)
			siteUser = userManager.getUser(userLibID, user.getUserID());
		int uType = siteUser.getInt("u_type");
		if (uType < 0)
			uType = 0;
		doc0.set("a_orgID", siteUser.getInt("u_orgID"));// 部门ID
		doc0.set("a_sourceType", uType);// 来源类型，0：编辑，1：记者

	}

	/**
	 * 根据来源名查找来源ID
	 */
	private void setSourceID(HttpServletRequest request,Document doc0) throws E5Exception {
		String srcName = doc0.getString("a_source").trim();
		long srcID = 0 ;
		if (!StringUtils.isBlank(srcName)) {
			int siteID = doc0.getInt("a_siteID") ;
			int srcLibID = LibHelper.getLibIDByOtherLib(DocTypes.SOURCE.typeID(), doc0.getDocLibID());
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document[] docs = docManager.find(srcLibID, "src_name=? and src_siteID=? and SYS_DELETEFLAG=0", new Object[] { srcName, siteID });
			if(docs == null){
				srcID = InfoHelper.getNextDocID(DocTypes.SOURCE.typeID());
				Document source = docManager.newDocument(srcLibID, srcID);
				ProcHelper.initDoc(source);
				source.set("src_siteID", siteID);
				Category[] groups = InfoHelper.getCatGroups(request, CatTypes.CAT_SOURCE.typeID(), siteID);
				source.set("src_groupID", groups[0].getCatID());
				source.set("src_name", srcName);
				docManager.save(source);
			}
		}
		doc0.set("a_sourceID", srcID);
	}

	/**
	 * 若系统参数配置为自动提取摘要，则保存前对摘要为空的文档自动提取摘要，关键字为空时也自动提取
	 */
	private void setSummary(Document doc0) {
		boolean autoExtract = "是".equals(InfoHelper.getConfig("写稿", "自动提取摘要"));

		// 自动提取摘要
		String summary = doc0.getString("a_abstract");
		if (StringUtils.isBlank(summary) && autoExtract) {
			String content = doc0.getString("a_content");
			if (!StringUtils.isBlank(content)) {
				content = content.replaceAll("\\&[a-zA-Z]{1,10};", "")
						.replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "").replaceAll("_ueditor_page_break_tag_","");
				summary = ArticleServiceHelper.extractSummary(content);
				doc0.set("a_abstract", summary);
			}
		}

		// 自动提取关键字
		summary = doc0.getString("a_keyword");
		if (StringUtils.isBlank(summary) && autoExtract) {
			String content = doc0.getString("a_content");
			if (!StringUtils.isBlank(content)) {
				content = content.replaceAll("\\&[a-zA-Z]{1,10};", "")
						.replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");
				summary = ArticleServiceHelper.extractKeywords(content).replaceAll("\\s",",").replaceAll("_ueditor_page_break_tag_","");
				doc0.set("a_keyword", summary);
			}
		}
	}

	/**
	 * 短标题处理
	 */
	private void setTitle(Document doc0) {
		String title = doc0.getString("a_shortTitle");
		if (StringUtils.isBlank(title)) {
			doc0.set("a_shortTitle", doc0.getTopic());
		}
	}
	
	/**
	 * 为文章稿加修改痕迹
	 */
	private void setTrace(Document doc, boolean isNew, String note, SysUser user) {
		if (doc.getInt("a_type") != Article.TYPE_ARTICLE) return;
		
		String content = doc.getString("a_content");
		String topic = doc.getTopic();
		
		ReviseManifest rm = new ReviseManifestImpl();
		TraceVersion ver = new TraceVersion();
		ver.setContent(HTMLUtil.xhtml2Text(content));
		ver.setCreateTime(new Date());
		ver.setNote(note);
		ver.setUserId(user.getUserID());
		ver.setUserName(user.getUserName());
		ver.setTopic(topic);
		ver.setIntroTopic("");
		ver.setSubTopic("");
		ver.setVersion(1);
		ver.setXhtml(content);

		try {
			String oldVerXml = null;
			if (!isNew) {
				oldVerXml = DocTraceUtils.getVerXml(doc);
				if (StringUtils.isBlank(oldVerXml)) {
					oldVerXml = null;
				}
				int nextVer = rm.getNextVersion(oldVerXml);
				ver.setVersion(nextVer);
			}
			
			String newXml = rm.updateVersion(oldVerXml, ver);
			doc.setBlob("a_trace", newXml.getBytes("UTF-16"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 从原稿复制渠道稿件 若勾选了渠道，并且设置了发布主栏目，则复制一份发布库稿件
	 * @param toPublish 
	 */
	private void copyChannelArticles(List<ChannelArticle> docs,
			JSONObject form, String tenantCode, boolean isNew, int roleID, boolean toPublish)
			throws E5Exception {
		Document doc0 = docs.get(0).doc;

		// 取出租户下的发布库。渠道代号是从0开始，正好与发布库一一对应
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				tenantCode);

		Channel[] chs = ConfigReader.getChannels();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == null)
				continue;

			int id = chs[i].getId();

			// 勾选渠道
			boolean checked = "on".equals(JsonHelper.getString(form, "channel"
					+ id));
			if (checked) {
				// 选择的主栏目ID
				int colID = JsonHelper.getInt(form, id + "_columnID");
				if (colID > 0) {
					ChannelArticle chArticle = copyArticle(docs.get(0),
							articleLibs.get(i).getDocLibID());
					chArticle.doc.set("a_columnID", colID);
					chArticle.doc.set("a_column",
							JsonHelper.getString(form, id + "_column"));
					chArticle.doc.set("a_originalID", doc0.getDocID());// 原稿ID
					chArticle.doc.set("a_channel", (int) Math.pow(2, id));// 稿件的渠道

					fillChannelDoc(chArticle.doc, true, toPublish, roleID, colID);
					chArticle.procName += "(原稿)";

					// 加入到稿件列表
					docs.add(chArticle);

					// 原稿的发布渠道设置
					int channel = doc0.getInt("a_channel");
					if (channel < 0)
						channel = 0;
					channel = channel | (int) Math.pow(2, id);
					doc0.set("a_channel", channel);
				}
			}
		}
	}

	// 针对栏目稿件，保存前的设置：所有栏目、顺序、流程
	private void fillChannelDoc(Document doc, boolean isNew, boolean toPublish,
			int roleID, long colID) throws E5Exception {
		// 设置所有栏目：主栏目ID、关联栏目ID、聚合栏目ID，格式为“1;2;3;4”
		// 稿件修改时不修改主栏目和关联栏目（以免解除栏目的聚合栏目又被关联），不计算columnAll
		if (StringUtils.isBlank(doc.getString("a_linkTitle")))
			doc.set("a_linkTitle", doc.getTopic()); // 链接标题
		if (isNew) {
			articleManager.setColumnAll(doc);
			if (StringUtils.isBlank(doc.getString("a_linkTitle")))
				doc.set("a_linkTitle", doc.getTopic()); // 链接标题
			doc.set("a_status", Article.STATUS_PUB_NOT); // 未发布
			// 设置顺序
			double order = articleManager.getNewOrder(doc);
			doc.set("a_order", order);
		}
		long oricolID = doc.getLong("a_columnID");

		// 推送至APP发布库的稿件以WEB发布库栏目为准设置审核流程
		if(colID != oricolID) doc.set("a_columnID", colID);
		
		if (toPublish) {// 发布
			articleManager.tryPublish(doc, doc.getLong("a_columnID"), roleID);
			// 处理定时发布,若稿件已经设置为发布，并且发布时间置后，则改成定时发布状态
			articleManager.changeTimedPublish(doc);
		} else if(doc.getInt("a_status") != Article.STATUS_AUDITING) { //审核状态的稿件，流程节点不变
			articleManager.setFlowByColumn(doc);
		}
		doc.set("a_columnID", oricolID);
	}

	/** 复制发布库稿件为原稿 */
	private ChannelArticle copyArticle(ChannelArticle channelArticle,
			int docLibID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		ChannelArticle copyArticle = new ChannelArticle();
		// 复制主稿件
		copyArticle.doc = docManager.newDocument(channelArticle.doc, docLibID,
				channelArticle.doc.getDocID());
		copyArticle.doc.setFolderID(0); // 清空文件夹ID，以便initDoc方法赋值
		ProcHelper.initDoc(copyArticle.doc);

		copyArticle.procName = channelArticle.procName;

		// 复制扩展字段
		if (channelArticle.extFields != null
				&& channelArticle.extFields.size() > 0) {
			copyArticle.extFields = new ArrayList<Document>();
			for (Document extField : channelArticle.extFields) {
				long extID = InfoHelper.getNextDocID(DocTypes.ARTICLEEXT
						.typeID());
				Document copyExtField = docManager.newDocument(extField,
						extField.getDocLibID(), extID);
				copyExtField.set("ext_articleLibID", docLibID);
				copyExtField.set("ext_articleID", copyArticle.doc.getDocID());

				copyArticle.extFields.add(copyExtField);
			}
		}
		/**
		 * 不需要复制挂件：从渠道复制到原稿时不需要有挂件，从原稿复制到渠道稿时也根本没挂件 同样也不需要复制相关稿件 if
		 * (channelArticle.widgets != null) { copyArticle.widgets =
		 * channelArticle.widgets.clone(); }
		 */

		// 复制正文图片
		if (channelArticle.pics != null) {
			copyArticle.pics = new ArrayList<Pic>();
			for (Pic pic : channelArticle.pics) {
				copyArticle.pics.add(pic.clone());
			}
		}
		// 复制正文视频
		if (channelArticle.videos != null) {
			copyArticle.videos = new ArrayList<Video>();
			for (Video v : channelArticle.videos) {
				copyArticle.videos.add(v.clone());
			}
		}

		return copyArticle;
	}
	
	/**
	 * 把所有渠道的稿件保存到数据库，作为一个事务
	 * @param jabbarArticle 
	 */
	private String save(List<ChannelArticle> articleList, boolean isNew, boolean jabbarArticle) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		DBSession conn = null;

		try {
			conn = E5docHelper.getDBSession(articleList.get(0).doc.getDocLibID());
			conn.beginTransaction();

			for (int i = 0, size = articleList.size(); i < size; i++) {
				ChannelArticle article = articleList.get(i);
				//保存标题图信息
				saveTitlePicInfo(article);
				if ((isNew && !hasWritePermission(article.doc)) || (!isNew && !hasModifyPermission(article.doc))) {
					int docLibID = article.doc.getDocLibID();
					if (docLibID == LibHelper.getArticleLibID()
							|| docLibID == LibHelper.getArticleAppLibID()) {
						continue;
					} else {
						article.doc.set("A_CHANNEL", 0);
					}
				}
				// 保存稿件
				docManager.save(article.doc, conn);

				// 保存扩展字段
				saveExtFields(article, isNew, conn);

				// 保存挂件
				saveWidgets(article, isNew, conn);

				// 保存附件
				saveAttachments(article, isNew, conn, jabbarArticle);

				// 保存相关稿件
				saveRels(article, isNew, conn);
			}
			// 提交transaction
			conn.commitTransaction();

			return null;
		} catch (Exception e) {
			ResourceMgr.rollbackQuietly(conn);
			e.printStackTrace();
			return e.getLocalizedMessage();
		} finally {
			ResourceMgr.closeQuietly(conn);
		}
	}

	private void saveTitlePicInfo(ChannelArticle article) {
		Document doc = article.doc;
		Integer pathBig = (StringUtils.isBlank(doc.getString("a_picBig")))?0:4;
		Integer pathMiddle = (StringUtils.isBlank(doc.getString("a_picMiddle")))?0:2;
		Integer pathSmall = (StringUtils.isBlank(doc.getString("a_picSmall")))?0:1;
		Integer havePic = pathBig + pathMiddle + pathSmall;
		log.error("---ArticleSubmitController.java:855---标题图情况：" + havePic );
		article.doc.set("a_hasTitlePic", havePic);
	}

	private boolean hasWritePermission(Document doc) throws E5Exception {
		SiteUserReader siteUserReader = (SiteUserReader) Context.getBean("siteUserReader");
		int siteID = doc.getInt("a_siteID");
		int userID = doc.getCurrentUserID();
		int userLibID = LibHelper.getUserExtLibID();
		int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0)
			return false;
		int roleID = roleIDs[0];
		try {
			if (roleIDs.length > 1)
				roleID = ((PermissionCache) CacheReader
						.find(PermissionCache.class)).merge(roleIDs);
		} catch (E5Exception e) {
		}
		FlowPermissionReader fpReader = (FlowPermissionReader) Context
				.getBean(FlowPermissionReader.class);
		ProcManager procManager = (ProcManager) Context
				.getBean(ProcManager.class);
		ProcUnflow proc = procManager
				.getUnflow(DocTypes.ARTICLE.typeID(), "写稿");
		return fpReader.hasUnflowPermission(roleID, proc.getProcID());
	}
	
	private boolean hasModifyPermission(Document doc) throws E5Exception {
		SiteUserReader siteUserReader = (SiteUserReader) Context.getBean("siteUserReader");
		int siteID = doc.getInt("a_siteID");
		int userID = doc.getCurrentUserID();
		int userLibID = LibHelper.getUserExtLibID();
		int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0)
			return false;
		int roleID = roleIDs[0];
		try {
			if (roleIDs.length > 1)
				roleID = ((PermissionCache) CacheReader
						.find(PermissionCache.class)).merge(roleIDs);
		} catch (E5Exception e) {
		}
		FlowPermissionReader fpReader = (FlowPermissionReader) Context.getBean(FlowPermissionReader.class);
		int flowID = doc.getCurrentFlow();

        //beforeSave方法可能会改变稿件当前流程节点，这里取修改前set的当前流程节点
        String historyCurrentNode = doc.getString("historyCurrentNode");
		int flowNodeID = StringUtils.isBlank(historyCurrentNode)?doc.getCurrentNode():Integer.valueOf(historyCurrentNode);

        boolean published = doc.getBoolean("published");
		String updateName = "修改";
		if(published){//稿件已发布，则状态为重改
            updateName = "重改";
        }

		return fpReader.hasPermission(roleID, flowID, flowNodeID, updateName);
	}

	// 保存扩展字段
	private void saveExtFields(ChannelArticle article, boolean isNew,
			DBSession conn) throws E5Exception {

		List<Document> extFields = article.extFields;
		// 如果是修改操作，那就先删除
		if (!isNew) {
			String sql = "delete from xy_articleExt where ext_articleID=? and ext_articleLibID=?";
			InfoHelper.executeUpdate(sql, new Object[] {
					article.doc.getDocID(), article.doc.getDocLibID() }, conn);
		}

		if (extFields != null && extFields.size() > 0) {

			// 保存扩展字段
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			for (Document extField : extFields) {
				docManager.save(extField, conn);
			}
		}
	}

	/**
	 * 保存挂件：全部删掉，重新加。 由于操作只在挂件表中进行，不会影响到图片库、视频库。因此采用了简单粗暴的做法。
	 */
	private void saveWidgets(ChannelArticle article, boolean isNew,
			DBSession conn) throws E5Exception {

		// 若挂件没被展开过，则不处理
		Widget ws = article.widgets;
		if (ws == null)
			return;

		int articleLibID = article.doc.getDocLibID();
		long articleDocID = article.doc.getDocID();

		// 已有挂件删除
		articleManager.deleteWidgets(articleLibID, articleDocID, conn);

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(),
				articleLibID);

		addWidget(article, ws.picID, docLibID, 1, conn);
		addWidget(article, ws.videoID, docLibID, 2, conn);
		addWidget(article, ws.voteID, docLibID, 3, conn);

		List<Pair> atts = ws.attachments;
		if (atts != null && atts.size() > 0) {
			//一次性读多个挂件ID，减少数据库交互次数。不传入conn，尽快释放表锁。
			long idStart = EUID.getID("DocID" + DocTypes.WIDGET.typeID(), atts.size());
			
			for (Pair att : atts) {
				Document widget = docManager.newDocument(docLibID, idStart++);

				widget.set("w_articleID", article.doc.getDocID()); // 所属稿件
				widget.set("w_articleLibID", article.doc.getDocLibID());
				widget.set("w_type", 0); // 0:附件
				widget.set("w_path", att.getKey());
				widget.set("w_content", att.getValue());

				dealFileTranscoding(article.doc, att.getKey());

				docManager.save(widget, conn);
			}
		}
	}

	/**
	 * 若稿件是文档，通知转码服务进行转码
	 */
	private void dealFileTranscoding(Document doc, String filePath) {
		if (doc.getInt("a_type") != Article.TYPE_FILE)
			return;
		if (StringUtils.isBlank(filePath))
			return;

		String suffix = filePath.substring(filePath.lastIndexOf(".") + 1)
				.toLowerCase();
		if (articleManager.isFile(suffix)
				&& "是".equals(InfoHelper.getConfig("写稿", "自动转码文档"))) {
			ArticleServiceHelper.fileTranscoding(filePath);
		}
	}

	private void addWidget(ChannelArticle article, DocID docID, int docLibID,
			int type, DBSession conn) throws E5Exception {
		if (docID != null) {
			//不传入conn，尽快释放表锁。
			long id = EUID.getID("DocID" + DocTypes.WIDGET.typeID());

			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document widget = docManager.newDocument(docLibID, id);

			widget.set("w_articleID", article.doc.getDocID()); // 所属稿件
			widget.set("w_articleLibID", article.doc.getDocLibID());

			widget.set("w_type", type); // 0:附件;1:组图;2:视频;3:投票

			widget.set("w_objID", docID.docID); // 挂件对应的图片、视频等
			widget.set("w_objLibID", docID.docLibID);

			docManager.save(widget, conn);
		}
	}

	/**
	 * 保存附件：把正文中的图片、视频保存到附件表 按path路径比较
	 * 是新加的（不管是上传的、选用的、原图修改的），创建附件表记录，以及图片/视频库记录（PC版才写库）；
	 * 是删掉的，去掉附件表记录，同时按附件表中的对应图片ID删除图片库/视频库的记录
	 * @param jabbarArticle 
	 */
	private void saveAttachments(ChannelArticle article, boolean isNew,
			DBSession conn, boolean jabbarArticle) throws E5Exception {
		int docLibID = article.doc.getDocLibID();
		long docID = article.doc.getDocID();

		// 取出已有附件。注意这里使用了另一个session
		Document[] old = isNew ? null : articleManager.getAttachments(docLibID,
				docID);

		// 图片
		saveAttachmentPic(old, article, conn, jabbarArticle);
		// 附件
		saveAttachmentFiles(old, article, conn);
		// 视频
		saveAttachmentVideo(old, article, conn);
		// 标题图片
		savePicTitle(old, article, conn);

		// 去掉不用了的附件
		deleteOldPic(old, conn);

	}

	private void saveAttachmentFiles(Document[] old, ChannelArticle article,
			DBSession conn) throws E5Exception {
		List<Files> files = article.files;

		if (files != null && files.size() > 0) {
			int docLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.ATTACHMENT.typeID(), article.doc.getDocLibID());
			int articleType = article.doc.getInt("a_type");

			//一次性读多个ID，减少数据库交互。不传入conn，尽快释放表锁。这里有些不严谨，在稿件修改时也会取一次
			long idStart = EUID.getID("DocID" + DocTypes.ATTACHMENT.typeID(), files.size());
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			for (int i = 0; i < files.size(); i++) {
				Files fileInfo = files.get(i);

				int index = find(old, fileInfo.path, Article.ATTACH_FILE);
				if (index < 0) {
					addNewFile(article, fileInfo, conn, docManager, docLibID,
							articleType, i, idStart++);
				} else {//附件如果没做改动就不做处理
					//changeOldFile(old[index], fileInfo, i, articleType, conn);
					old[index] = null;
				}
			}
		}
	}


	private void addNewFile(ChannelArticle article, Files fileInfo,
			DBSession conn, DocumentManager docManager, int attLibID, int articleType, int i,
			long attID) throws E5Exception {
		// 组装附件表的Document对象
		Document attach = docManager.newDocument(attLibID, attID);
		
		attach.set("att_articleID", article.doc.getDocID()); // 所属稿件
		attach.set("att_articleLibID", article.doc.getDocLibID());
		attach.set("att_path", fileInfo.path);
		attach.set("att_type", Article.ATTACH_FILE); // 5:正文附件

		// 若是外网附件，则把url字段也填好
		if (fileInfo.path != null
				&& fileInfo.path.toLowerCase().startsWith("http")) {
			attach.set("att_url", fileInfo.path);
			attach.set("att_urlPad", fileInfo.path);
		}
		DocID fileID = (DocID) fileInfo.fileID;
		if (fileID != null) {
			attach.set("att_objID", fileID.docID); // 对应的图片库ID
			attach.set("att_objLibID", fileID.docLibID);
		}

		docManager.save(attach, conn);
	}



	// 创建附件表记录，以及图片库记录
	private void saveAttachmentPic(Document[] old, ChannelArticle article,
			DBSession conn, boolean jabbarArticle) throws E5Exception {
		List<Pic> pics = article.pics;

		if (pics != null && pics.size() > 0) {
			int docLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.ATTACHMENT.typeID(), article.doc.getDocLibID());
			int articleType = article.doc.getInt("a_type");

			//一次性读多个ID，减少数据库交互。不传入conn，尽快释放表锁。这里有些不严谨，在稿件修改时也会取一次
			long idStart = EUID.getID("DocID" + DocTypes.ATTACHMENT.typeID(), pics.size());
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			for (int i = 0; i < pics.size(); i++) {
				Pic picInfo = pics.get(i);

				int index = find(old, picInfo.path, Article.ATTACH_PIC);
				if (index < 0) {
					//如果是天钩上传，且为组图，则需先删除数据库中附件表数据，然后再次保存
					if(jabbarArticle && articleType == Article.TYPE_PIC){
						deleteAtt(article);
					}
					addNewPic(article, picInfo, conn, docManager, docLibID,
							articleType, i, idStart++);
				} else {
					// 是组图稿，则还要检查content/order/isindexed是否有变化，有变化就更新附件表
					// if (articleType == Article.TYPE_PIC)
					changeOldPic(old[index], picInfo, i, articleType, conn);
					old[index] = null;
				}
			}
		}
	}

	private void deleteAtt(ChannelArticle article) throws E5Exception {
		long docID = article.doc.getDocID(); // 所属稿件
		int docLibID = article.doc.getDocLibID();
		articleManager.deleteAttachment(docLibID, docID);
	}



	// 创建附件表记录，以及视频库记录
	private void saveAttachmentVideo(Document[] old, ChannelArticle article,
			DBSession conn) throws E5Exception {

		int attLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.ATTACHMENT.typeID(), article.doc.getDocLibID());
		// boolean isApp = article.doc.getInt("a_channel") == 2;

		List<Video> videos = article.videos;
		if (videos != null) {
			for (int i = 0; i < videos.size(); i++) {
				Video videoInfo = videos.get(i);

				int index = find(old, videoInfo.url,Article.ATTACH_VIDEO);
				if (index < 0) {
					addNewVideo(article, videoInfo, conn, attLibID, i);
				} else {
					// 若顺序变了，做修改
					if (old[index].getInt("att_order") != i) {
						old[index].set("att_order", i);
						DocumentManager docManager = DocumentManagerFactory
								.getInstance();
						docManager.save(old[index], conn);
					}
					old[index] = null;
				}
			}
		}
	}

	// 新的正文图片保存
	private void addNewPic(ChannelArticle article, Pic picInfo, DBSession conn,
			DocumentManager docManager, int attLibID, int articleType, int i,
			long attID) throws E5Exception {
		// 组装附件表的Document对象
		Document attach = docManager.newDocument(attLibID, attID);
		
		attach.set("att_articleID", article.doc.getDocID()); // 所属稿件
		attach.set("att_articleLibID", article.doc.getDocLibID());
		attach.set("att_path", picInfo.path);
		attach.set("att_type", Article.ATTACH_PIC); // 0:正文图片;1:正文视频
		attach.set("att_order", i);

		// 若是外网图片，则把url字段也填好
		if (picInfo.path != null
				&& picInfo.path.toLowerCase().startsWith("http")) {
			attach.set("att_url", picInfo.path);
			attach.set("att_urlPad", picInfo.path);
		}

		// 若是组图稿，则图片附件有说明、顺序、是否索引的属性
		if (articleType == Article.TYPE_PIC
				|| articleType == Article.TYPE_PANORAMA) {
			attach.set("att_content", picInfo.content);
			attach.set("att_indexed", picInfo.isIndexed ? 1 : 0);
		}
		if (picInfo.picID != null) {
			attach.set("att_objID", picInfo.picID.docID); // 对应的图片库ID
			attach.set("att_objLibID", picInfo.picID.docLibID);
		}

		docManager.save(attach, conn);
	}

	// 新的正文视频保存
	private void addNewVideo(ChannelArticle article, Video videoInfo,
			DBSession conn, int attLibID, int i) throws E5Exception {
		//不传入conn，尽快释放表锁。
		long id = EUID.getID("DocID" + DocTypes.ATTACHMENT.typeID());

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document attach = docManager.newDocument(attLibID, id);

		attach.set("att_articleID", article.doc.getDocID()); // 所属稿件
		attach.set("att_articleLibID", article.doc.getDocLibID());

		attach.set("att_path", videoInfo.url);

		attach.set("att_url", videoInfo.url);
		attach.set("att_urlPad", videoInfo.urlApp);

		attach.set("att_type", Article.ATTACH_VIDEO); // 1:正文视频
		if (videoInfo.videoID != null) {
			attach.set("att_objID", videoInfo.videoID.docID); // 对应的视频ID
			attach.set("att_objLibID", videoInfo.videoID.docLibID);
			attach.set("att_picPath", videoInfo.picPath);
			attach.set("att_duration", videoInfo.duration); //视频时长
		}
		attach.set("att_order", i);

		docManager.save(attach, conn);
	}

	private void changeOldPic(Document old, Pic picInfo, int i,
			int articleType, DBSession conn) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 是组图稿，则还要检查content/order/isindexed是否有变化，有变化就更新附件表
		boolean changed = false;
		if ((articleType == Article.TYPE_PIC || articleType == Article.TYPE_PANORAMA)
				&& !picInfo.content.equals(old.getString("att_content"))) {
			changed = true;
			old.set("att_content", picInfo.content);
		}
		if ((articleType == Article.TYPE_PIC || articleType == Article.TYPE_PANORAMA)
				&& picInfo.isIndexed != (old.getDeleteFlag() == 0)) {
			changed = true;
			old.set("att_indexed", picInfo.isIndexed ? 1 : 0);
		}
		if (i != old.getInt("att_order")) {
			changed = true;
			old.set("att_order", i);
		}
		if (changed) {
			docManager.save(old, conn);
			/*
			 * //更新到对应的图片库（2015.7.6 实际上已经没有图片库对应了） long objID =
			 * old.getLong("att_objID"); if (objID > 0) { Document oldPic =
			 * docManager.get(old.getInt("att_objLibID"), objID); if (oldPic !=
			 * null) { oldPic.set("p_content", picInfo.content);
			 * oldPic.set("p_groupOrder", i);
			 * oldPic.setDeleteFlag(picInfo.isIndexed ? 0 : 2);
			 * 
			 * docManager.save(oldPic, conn); } }
			 */
		}
	}

	private void deleteOldPic(Document[] old, DBSession conn)
			throws E5Exception {
		if (old == null)
			return;

		DocumentManager docManager = DocumentManagerFactory.getInstance();

		// 前面没找到，是不需要的附件，删除
		for (int i = 0; i < old.length; i++) {
			if (old[i] != null) {
				docManager
						.delete(old[i].getDocLibID(), old[i].getDocID(), conn);

				// 若有对应的图片库数据，也删除（2015.7.6 实际上已经没有图片库对应了）
				if (old[i].getInt("att_type") == 0
						&& old[i].getInt("att_objID") > 0) {
					docManager.delete(old[i].getInt("att_objLibID"),
							old[i].getLong("att_objID"), conn);
				}
			}
		}
	}

	// 保存标题图片
	private void savePicTitle(Document[] old, ChannelArticle article,
			DBSession conn) throws E5Exception {
		int docLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.ATTACHMENT.typeID(), article.doc.getDocLibID());

		// 标题图片（大）
		savePicTitleOne(old, article, docLibID, conn, "a_picBig",
				Article.ATTACH_PICTITLE_BIG);
		// 标题图片（中）
		savePicTitleOne(old, article, docLibID, conn, "a_picMiddle",
				Article.ATTACH_PICTITLE_MIDDLE);
		// 标题图片（小）
		savePicTitleOne(old, article, docLibID, conn, "a_picSmall",
				Article.ATTACH_PICTITLE_SMALL);
	}

	private void savePicTitleOne(Document[] old, ChannelArticle article,
			int attLibID, DBSession conn, String field, int attType)
			throws E5Exception {
		Document doc = article.doc;

		String path = doc.getString(field);
		if (StringUtils.isBlank(path))
			return;

		int index = find(old, path,attType);
		if (index < 0) {
			//不传入conn，尽快释放表锁。
			long id = EUID.getID("DocID" + DocTypes.ATTACHMENT.typeID());

			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document attach = docManager.newDocument(attLibID, id);

			attach.set("att_articleID", doc.getDocID()); // 所属稿件
			attach.set("att_articleLibID", doc.getDocLibID());
			attach.set("att_path", path);
			attach.set("att_type", attType);

			docManager.save(attach, conn);
		} else {
			old[index] = null;
		}

	}

	/**
	 * 保存相关稿件
	 */
	private void saveRels(ChannelArticle article, boolean isNew, DBSession conn)
			throws E5Exception {
		// 若界面上相关稿件一栏没有展开过，则不必处理
		if (article.rels == null)
			return;

		int docLibID = article.doc.getDocLibID();
		long docID = article.doc.getDocID();

		// 取出已有。注意这里使用了另一个session
		Document[] old = articleManager.getRels(docLibID, docID);

		saveRels(old, article, conn);

		// 去掉不用了的附件
		deleteOldRel(old, conn);
	}

	// 比较并保存相关稿件
	private void saveRels(Document[] old, ChannelArticle article, DBSession conn)
			throws E5Exception {
		List<DocID> rels = article.rels;

		if (rels != null) {
			for (int i = 0; i < rels.size(); i++) {
				DocID rel = rels.get(i);

				int index = find(old, rel);
				if (index < 0) {
					addNewRel(article, rel, conn);
				} else {
					old[index] = null;
				}
			}
		}
	}

	// 创建相关稿件记录
	private void addNewRel(ChannelArticle article, DocID rel, DBSession conn)
			throws E5Exception {
		// 取出相关稿件的Document
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document relDoc = docManager.get(rel.docLibID, rel.docID);

		// 组装新的相关稿件记录
		int docTypeID = DocTypes.ARTICLEREL.typeID();
		int docLibID = LibHelper.getLibIDByOtherLib(docTypeID,
				article.doc.getDocLibID());
		//不传入conn，尽快释放表锁。
		long id = EUID.getID("DocID" + docTypeID);

		Document doc = docManager.newDocument(docLibID, id);

		int type = relDoc.getInt("a_type");

		doc.set("a_articleID", article.doc.getDocID());
		doc.set("a_articleLibID", article.doc.getDocLibID());
		doc.set("a_relID", rel.docID);
		doc.set("a_relLibID", rel.docLibID);
		doc.setTopic(relDoc.getTopic());
		doc.set("a_pubTime", relDoc.getTimestamp("a_pubTime"));

		if (article.doc.getInt("a_channel") == 1) {
			doc.set("a_url", relDoc.getString("a_url"));
			doc.set("a_urlPad",relDoc.getString("a_urlPad"));
		}
		else
			doc.set("a_url", relDoc.getString("a_urlPad"));

		// 若是专题或直播稿，则把linkID记录到相关稿件的url里
		doc.set("a_type", type);
		if ((type == Article.TYPE_SPECIAL || type == Article.TYPE_LIVE) && relDoc.getInt("a_channel") == 2) {
			doc.set("a_url", relDoc.getString("a_linkID"));
		}
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

	private void deleteOldRel(Document[] old, DBSession conn)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();

		// 前面没找到，是不需要的，删除
		for (int i = 0; i < old.length; i++) {
			if (old[i] != null) {
				docManager
						.delete(old[i].getDocLibID(), old[i].getDocID(), conn);
			}
		}
	}

	// 找挂件，若找到，则把附件数组中置为null
	private int find(Document[] old, String path, int type) {
		if (old == null)
			return -1;

		for (int i = 0; i < old.length; i++) {
			if (old[i] == null)
				continue;

			if (old[i].getInt("att_type") == type && old[i].getString("att_path").equals(path)) {
				return i;
			}
		}
		return -1;
	}

	// 找相关稿件
	private int find(Document[] old, DocID rel) {
		for (int i = 0; i < old.length; i++) {
			if (old[i] == null)
				continue;

			if (old[i].getDocID() == rel.docID
					&& old[i].getDocLibID() == rel.docLibID) {
				return i;
			}
		}
		return -1;
	}

	private void triggerPublish(List<ChannelArticle> articles, boolean needRevoke) {
		// 发出稿件发布消息，过滤掉原稿
		for (ChannelArticle article : articles) {
			if (article.doc.getDocTypeID() != DocTypes.ARTICLE.typeID())
				continue;
			if(needRevoke && article.doc.getInt("a_status")!=Article.STATUS_PUB_ING)
				PublishTrigger.articleRevoke(article.doc);
			else
				PublishTrigger.article(article.doc);
		}
	}

	// 保存后写日志
	private void writeLog(List<ChannelArticle> articles, SysUser user,
			boolean isNew) {
		for (ChannelArticle article : articles) {
			if (article != null) {
				LogHelper.writeLog(article.doc.getDocLibID(),
						article.doc.getDocID(), user, article.procName,
						article.logChanged);
			}
		}
	}

	// 从request中取出json格式的参数
	private JSONObject getParams(HttpServletRequest request) throws Exception {
		// {DocID:.., PC:{...}, Pad:{...}, Phone:{...}}
		String param = WebUtil.get(request, "param");
		if (log.isDebugEnabled()) {
			log.debug("稿件提交，参数：" + param);
		}
		return JSONObject.fromObject(param);
	}

	private String getProcName(HttpServletRequest request, String uuid) {
		ProcParam param = (ProcParam) request.getSession().getAttribute(uuid);
		return (param == null) ? "稿件" : param.getProcName();
	}
	
	/**
	 * 修改相关稿件的链接标题和短标题
	 * @param article
	 */
	private void modifyRelArticles(Document article){
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = LibHelper.getArticleRelLibID();
		String conditions = "a_relLibID=? and a_relID=?";
		Object[] params = new Object[]{article.getDocLibID(), article.getDocID()};
		try {
			Document[] docs = docManager.find(docLibID, conditions, params);
			for(Document doc : docs){
				doc.set("SYS_TOPIC", article.getString("a_linkTitle"));
				doc.set("a_shortTitle", article.getString("a_shortTitle"));
				docManager.save(doc);
			}
		} catch (E5Exception e) {
		}
	}
}

// 写稿提交的稿件对象，包括稿件本身的属性、扩展字段、挂件、正文中的图片和视频
class ChannelArticle {
	String code; // 渠道代码
	Document doc;
	List<Document> extFields;
	Widget widgets;
	List<Pic> pics;
	List<Files> files;
	List<Video> videos;
	List<DocID> rels;
	String logChanged = ""; // 修改情况
	String procName = "写稿"; // 操作名
}

// 挂件
class Widget implements Cloneable {
	DocID picID;
	DocID videoID;
	DocID voteID;
	List<Pair> attachments; // 用Pair类型保存附件的说明和存储地址

	@Override
	protected Widget clone() {
		Widget one = new Widget();
		if (picID != null)
			one.picID = new DocID(picID.docLibID, picID.docID);
		if (videoID != null)
			one.videoID = new DocID(videoID.docLibID, videoID.docID);
		if (voteID != null)
			one.voteID = new DocID(voteID.docLibID, voteID.docID);
		if (attachments != null) {
			one.attachments = new ArrayList<Pair>();
			for (Pair pair : attachments) {
				one.attachments.add(new Pair(pair.getKey(), pair.getValue()));
			}
		}
		return one;
	}
}

class Pic implements Cloneable {
	String path;
	String content;
	boolean isIndexed;
	long groupID;
	DocID picID; // 若是从图片库选的图，则记录图片库ID和图片ID

	@Override
	protected Pic clone() {
		try {
			Pic one = (Pic) super.clone();
			if (picID != null)
				one.picID = new DocID(picID.docLibID, picID.docID);

			return one;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}

class Video implements Cloneable {
	String url;
	String urlApp;
	String picPath; //视频关键帧地址
	int duration; //视频时长
	DocID videoID; // 视频库记录

	@Override
	protected Video clone() {
		try {
			Video one = (Video) super.clone();
			if (videoID != null)
				one.videoID = new DocID(videoID.docLibID, videoID.docID);

			return one;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}

class Files implements Cloneable {
	String path; //文档存储地址
	DocID fileID; // 文档库记录
	
	@Override
	protected Video clone() {
		try {
			Video one = (Video) super.clone();
			if (fileID != null)
				one.videoID = new DocID(fileID.docLibID, fileID.docID);
			
			return one;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
