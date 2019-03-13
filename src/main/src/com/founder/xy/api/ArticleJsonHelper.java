package com.founder.xy.api;

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
import com.founder.e5.dom.DocLib;
import com.founder.xy.api.column.ColumnApiManager;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 稿件api的辅助类。 从数据库读稿件，并组织成列表api可用的稿件json、详情api可用的稿件json
 * 
 * @author Gong Lijie
 */
public class ArticleJsonHelper {
	@Autowired
	ColumnApiManager columnApiManager;
	/**
	 * 组织一篇稿件的json
	 */
	public static JSONObject article(int docLibID, long docID)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);

		if (doc == null || doc.getInt("a_status") != Article.STATUS_PUB_DONE)
			return null;

		JSONObject redisJson = new JSONObject();

		// 设置基础属性
		setBasicField(redisJson, doc);

		// 设置栏目属性
		int mainColID = doc.getInt("a_columnID");
		setArticleColumn(redisJson, mainColID);

		// 如果是记者稿则添加记者名片
		if (1 == doc.getInt("a_sourceType")) {
			setAuthorInfo(redisJson, doc.getLong("SYS_AUTHORID"));
		}

		// 处理内容
		setContent(redisJson, doc);
		// 扩展字段
		setExtFields(redisJson, doc);
		// 挂件
		setWidgetJson(redisJson, docLibID, docID);
		// 相关稿件
		setRelationJson(redisJson, docLibID, docID);

		return redisJson;
	}

	public static JSONObject listArticleOne(int docLibID, long docID,
			int attLibID, int artExtLibID) {
		try {
			JSONObject inJson = listArticleBaseField(docLibID, docID);
			if (inJson == null)
				return null;

			listArticleAttExtField(inJson, docID, attLibID, artExtLibID,
					docLibID);
			return inJson;
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置基本属性
	 */
	private static void setBasicField(JSONObject redisJson, Document doc) throws E5Exception{
		redisJson.put("fileId",
				StringUtils.getNotNull(doc.getString("SYS_DOCUMENTID"))); // 稿件ID
		redisJson.put("siteID",doc.getInt("a_siteID")); //站点ID
		redisJson.put("version", doc.getLong("a_realPubTime")); // 稿件最后修改时间
		redisJson.put("title",
				StringUtils.getNotNull(doc.getString("SYS_TOPIC"))); // 稿件标题
		redisJson.put("attAbstract",
				StringUtils.getNotNull(doc.getString("a_abstract"))); // 稿件摘要
		redisJson.put("publishtime",
				InfoHelper.formatDate(doc.getTimestamp("a_pubTime"))); // 发布时间
		redisJson.put("source",
				StringUtils.getNotNull(doc.getString("a_source"))); // 来源
		redisJson.put("sourceUrl",
				StringUtils.getNotNull(doc.getString("a_sourceUrl"))); // 来源链接
		redisJson.put("author",
				StringUtils.getNotNull(doc.getString("SYS_AUTHORS"))); // 作者
		redisJson.put("collaborator",
				StringUtils.getNotNull(doc.getString("a_collaborator")));
		redisJson.put("editor",
				StringUtils.getNotNull(doc.getString("a_editor"))); // 编辑
		redisJson.put("liability",
				StringUtils.getNotNull(doc.getString("a_liability"))); // 责任编辑
		redisJson.put("subtitle",
				StringUtils.getNotNull(doc.getString("a_subTitle"))); // 副题
		redisJson.put("tag", 
				StringUtils.getNotNull(doc.getString("a_tag"))); //tag
		redisJson.put("columnID", doc.getInt("a_columnID")); // 主栏目ID
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		Column col = colReader.get(LibHelper.getColumnLibID(), doc.getInt("a_columnID"));
        if(col==null){
            redisJson.put("colIcon","");
        }else{
            redisJson.put("colIcon",col.getIconSmall());
        }
		redisJson.put("discussClosed", doc.getInt("a_discussClosed"));
		redisJson.put("articleType", doc.getInt("a_type"));
		redisJson.put("shareUrl",
				StringUtils.getNotNull(doc.getString("a_urlPad")));// url，供分享
		redisJson.put("url",
				StringUtils.getNotNull(doc.getString("a_url")));// url，评论读取
		redisJson.put("multimediaLink",
				StringUtils.getNotNull(doc.getString("a_multimediaLink")));//多媒体链接
		redisJson.put("trade", StringUtils.getNotNull(doc.getString("a_trade"))); //行业分类
		redisJson.put("tradeID", StringUtils.getNotNull(doc.getString("a_tradeID")));
		redisJson.put("linkID", doc.getInt("a_linkID")); //外链资源
		redisJson.put("linkName", StringUtils.getNotNull(doc.getString("a_linkName")));

		// 为后面读扩展字段准备的属性：扩展字段组ID、栏目ID
		redisJson.put("extGroupID", getInt(doc.getInt("a_extFieldGroupID")));
		redisJson.put("colID",
				StringUtils.getNotNull(doc.getString("a_columnID"))); // 主栏目ID

		redisJson.put("countDiscuss",
				StringUtils.getNotNull(doc.getString("a_countDiscuss"))); // 评论数
		redisJson.put("countPraise",
				StringUtils.getNotNull(doc.getString("a_countPraise"))); // 点赞数
		redisJson.put("countShare",
				StringUtils.getNotNull(doc.getString("a_countShare"))); // 分享数
		redisJson.put("countClick",
				StringUtils.getNotNull(doc.getString("a_countClick"))); // 点击数
		redisJson.put("countClickInitial",
				StringUtils.getNotNull(doc.getString("a_countClickInitial"))); // 初始点击数
		redisJson.put("rssType",doc.getInt("a_rssType")); // 内容源类型 0-编辑 1-微信 2微博

        if(doc.getInt("a_type") == Article.TYPE_PIC){
            redisJson.put("picContent",StringUtils.getNotNull(doc.getString("a_content")));
        }else{
            redisJson.put("picContent","");
        }
        //设置话题
		setTopics(redisJson,doc);
	}
	
	private static void setTopics(JSONObject inJson, Document doc) throws E5Exception {
		DBSession conn = null;
		IResultSet rs = null;
		Object[] params = new Object[] { doc.getDocID(), doc.getInt("a_channel")};
		try {
			String sql = "SELECT a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=? order by a_topicID desc limit 1 ";

			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			String topicID = "";
			String topicName = "";
			while(rs.next()){
				topicID=rs.getString("a_topicID");
				topicName = StringUtils.getNotNull(rs.getString("a_topicName"));
			}
			inJson.put("topicID", topicID);
			inJson.put("topicName", topicName);
			JSONArray jsonArray = new JSONArray();
            inJson.put("topics",jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	// 稿件详情json中，添加主栏目的描述、图标等信息
	private static void setArticleColumn(JSONObject redisJson, int mainColID)
			throws E5Exception {
		if (mainColID <= 0)
			return;

		ColumnReader colReader = (ColumnReader) Context.getBean("columnReader");
		Column col = colReader.get(LibHelper.getColumnLibID(), mainColID);

		if (null != col) {
			// 栏目图标
			String icon = StringUtils.getNotNull(col.getIconSmall());
			if (null == icon || "".equals(icon)) {
				icon = StringUtils.getNotNull(col.getIconBig());
			}
			redisJson.put("columnIcon", icon);
			
			// 栏目描述
			redisJson.put("columnDescription", StringUtils.getNotNull(col.getDescription()));
	        // 栏目类型
			redisJson.put("columnType", InfoHelper.getCatCode(CatTypes.CAT_COLUMN.typeID(), col.getAppTypeID()));
	        // 栏目样式
			redisJson.put("columnStyle", InfoHelper.getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), col.getAppStyleID()));
			redisJson.put("columnRssCount",col.getRssCount());
            redisJson.put("columnName",
                    StringUtils.getNotNull(col.getName())); // 主栏目名
			redisJson.put("forbidden",col.isForbidden());//是否禁用
		}
	}

	// 稿件详情中的记者名片
	private static void setAuthorInfo(JSONObject redisJson, long authorID)
			throws E5Exception {
		if (authorID <= 0)
			return;

		int userExtLibID = LibHelper.getUserExtLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document authorDoc = docManager.get(userExtLibID, authorID);
		if (authorDoc != null) {
			JSONObject author = new JSONObject();
			author.put("id", authorDoc.getInt("SYS_DOCUMENTID"));
			author.put("name", authorDoc.getString("u_name"));
			author.put("url", authorDoc.getString("u_iconUrl"));
			author.put("duty", authorDoc.getString("u_duty"));
			author.put("description", authorDoc.getString("u_comment"));

			redisJson.put("author", author);
		}
	}

	// 处理内容
	private static void setContent(JSONObject redisJson, Document doc)
			throws E5Exception {
		// 读出附件表的附件
		Document[] atts = readAtts(doc);

		String content = doc.getString("a_content");

		// 按稿件类型处理内容
		int type = doc.getInt("a_type");
		if (type == 0) { // 文章稿
			content = getArticleContent(redisJson, content, atts);
		} else if (type == 1) { // 组图稿
			content = getPicContent(redisJson, content, atts);
		} else if (type == 2) { // 视频稿
			content = getVideoContent(redisJson, content, atts);
		}

		redisJson.put("content", content); // 内容
	}

	private static void setExtFields(JSONObject redisJson, Document doc)
			throws E5Exception {
		int artExtDocLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.ARTICLEEXT.typeID(), doc.getDocLibID());
		Object[] params = new Object[] { doc.getDocID(), doc.getDocLibID() };
		listExtFields(redisJson, artExtDocLibID, doc.getDocLibID(), params);
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

	/**
	 * 文章稿属性
	 */
	private static String getArticleContent(JSONObject redisJson,
			String content, Document[] atts) throws E5Exception {
		// 取到所有附件
		org.jsoup.nodes.Document html = Jsoup.parse(content);

		// 替换稿件内容中的图片部分
		replaceAttPics(redisJson, atts, html);

		// 替换稿件内容中的视频部分
		replaceAttVideos(redisJson, atts, html);

		// 取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);

		return html.html();
	}

	/**
	 * 图片稿属性
	 */
	private static String getPicContent(JSONObject redisJson, String content,
			Document[] atts) throws E5Exception {
		JSONObject inJson = null;
		JSONArray jsonArr = new JSONArray();
		JSONArray inJsonArr = new JSONArray();

		// 图片
		Document[] docs = getAttsByType(atts, 0);
		String tag = null; // 标签内容

		int length = docs.length;
		for (int i = 0; i < length; i++) {
			tag = StringUtils.getNotNull(docs[i].getString("att_urlPad"));
			inJson = new JSONObject();
			inJson.put("ref", tag);
			inJson.put("picType", 1); // 图片类型
			inJson.put("summary",
					StringUtils.getNotNull(docs[i].getString("att_content"))); // 附件描述
			inJson.put("imageUrl", tag);

			inJsonArr.add(inJson);
		}
		if (length > 0) {
			inJson = new JSONObject();
			inJson.put("ref", "<!--IMAGES#1-->");
			inJson.put("imagearray", inJsonArr);
			jsonArr.add(inJson);
			redisJson.put("images", jsonArr);
			content = "<!--IMAGES#1-->" + content;
		}

		// 取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);

		return content;
	}

	/**
	 * 视频稿属性
	 * 
	 * @param redisJson
	 */
	private static String getVideoContent(JSONObject redisJson, String content,
			Document[] atts) throws E5Exception {
		// 视频
		Document[] docs = getAttsByType(atts, 1);
		int length = docs.length;

		JSONArray inJsonArr = new JSONArray();
		for (int i = 0; i < length; i++) {
			String tag = StringUtils
					.getNotNull(docs[i].getString("att_urlPad"));
			JSONObject inJson = new JSONObject();
			// inJson.put("ref", tag);
			inJson.put("ref", "<!--VIDEOARRAY#" + i + "-->");
			inJson.put("attAbstract",
					StringUtils.getNotNull(docs[i].getString("att_content"))); // 附件描述
			inJson.put("videoUrl", tag);
			inJson.put("imageUrl",
					StringUtils.getNotNull(docs[i].getString("att_picUrlPad"))); // 关键帧外网地址
			inJson.put("duration",docs[i].getInt("att_duration"));

			inJsonArr.add(inJson);
		}
		if (length > 0) {
			JSONObject inJson = new JSONObject();
			inJson.put("ref", "<!--VIDEOS#1-->");
			inJson.put("videoarray", inJsonArr);

			JSONArray jsonArr = new JSONArray();
			jsonArr.add(inJson);

			redisJson.put("videos", jsonArr); // (视频信息定义)

			content = "<!--VIDEOS#1-->" + content;
		}

		// 取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);

		return content;
	}

	// 替换稿件内容中的图片部分
	private static void replaceAttPics(JSONObject redisJson, Document[] atts,
			org.jsoup.nodes.Document html) {
		Document[] docs = getAttsByType(atts, 0);
		if (docs == null || docs.length == 0)
			return;

		Elements list = html.select("img");
		if (list.isEmpty())
			return;

		int i = 0;
		JSONArray inJsonArr = new JSONArray();
		for (Element img : list) {
			String url = img.attr("src");
			if (StringUtils.isBlank(url)) {
				img.remove();
				continue;
			}
			//文章稿件图说处理
			String summary = img.parent().select("figcaption").text();

			String imgStyle = covertImgStyle(img.toString());//新增img标签中的样式
//			String title = img.attr("title");

			String IMAGEARRAY = "<!--IMAGEARRAY#" + i + "-->";
			img.after(IMAGEARRAY);
			img.remove();

			JSONObject inJson = new JSONObject();
			inJson.put("ref", IMAGEARRAY);
			inJson.put("picType", 1); // 图片类型，附件图或者插图,1代表插图，0代表附件图
            inJson.put("summary", StringUtils.getNotNull(summary));
			inJson.put("imageStyle", imgStyle);
			inJson.put("imageUrl",
					StringUtils.getNotNull(docs[i].getString("att_urlPad"))); // 外网地址

			inJsonArr.add(inJson);
			i++;
		}
		if (i > 0)
			attPicArray(redisJson, inJsonArr);
	}

	private static String covertImgStyle(String img) {
		String regrex = "<\\s*img([\\s\\S]*?)src\\s*=\\s*\"[\\s\\S]*?\"([\\s\\S]*?)>";
		Pattern pat = Pattern.compile(regrex);
		Matcher match = pat.matcher(img);
		if(match.find()){
			return match.group(1) + " " + match.group(2);
		}
		return "";
	}

	private static void attPicArray(JSONObject redisJson, JSONArray inJsonArr) {
		JSONObject inJson = new JSONObject();
		inJson.put("ref", "<!--IMAGES#1-->"); // 第一组图片定义
		inJson.put("imagearray", inJsonArr);

		JSONArray jsonArr = new JSONArray();
		jsonArr.add(inJson);

		redisJson.put("images", jsonArr); // 图片信息定义
	}

	// 替换稿件内容中的视频部分
	private static void replaceAttVideos(JSONObject redisJson, Document[] atts,
			org.jsoup.nodes.Document html) {
		Document[] docs = getAttsByType(atts, 1);
		if (docs == null || docs.length == 0)
			return;

		Elements list = html.select("embed, audio, video");
		if (list.isEmpty())
			return;

		int i = 0;
		JSONArray videoArray = new JSONArray();
		for (Element embed : list) {
			String VIDEOARRAY = "<!--VIDEOARRAY#" + i + "-->";
			embed.after(VIDEOARRAY);
			embed.remove();

			JSONObject inJson = new JSONObject();
			inJson.put("ref", VIDEOARRAY);
			inJson.put("videoUrl",
					StringUtils.getNotNull(docs[i].getString("att_urlPad"))); // 外网地址
			inJson.put("imageUrl",
					StringUtils.getNotNull(docs[i].getString("att_picUrlPad"))); // 关键帧外网地址
			inJson.put("duration",docs[i].getInt("att_duration"));

			videoArray.add(inJson);
			i++;
		}
		if (i > 0)
			attVideoArray(redisJson, videoArray);
	}

	private static void attVideoArray(JSONObject redisJson, JSONArray videoArray) {
		JSONObject videoGroup = new JSONObject();
		videoGroup.put("ref", "<!--VIDEOS#1-->"); // 第一组视频信息定义":"<!--VIDEOS#0-->",
		videoGroup.put("videoarray", videoArray);

		JSONArray videos = new JSONArray();
		videos.add(videoGroup);

		redisJson.put("videos", videos); // (视频信息定义)
	}

	// 取出标题图片url，写入稿件json
	private static void setArticleImgUrl(Document[] atts, JSONObject redisJson) {
		String imgUrl = null;
		Document pic = getAttByType(atts, 4); // 标题图片-小
		if (pic != null) {
			imgUrl = pic.getString("att_urlPad");
		} else {
			pic = getAttByType(atts, 3); // 标题图片-中
			if (pic != null) {
				imgUrl = pic.getString("att_urlPad");
			} else {
				pic = getAttByType(atts, 2); // 标题图片-大
				if (pic != null) {
					imgUrl = pic.getString("att_urlPad") + ".0"; // 标题图片大图有抽图
				} else {
					imgUrl = "";
				}
			}
		}
		redisJson.put("imageUrl", imgUrl);
	}

	private static Document[] getAttsByType(Document[] atts, int type) {
		List<Document> list = new ArrayList<>();

		for (int i = 0; i < atts.length; i++) {
			if (atts[i].getInt("att_type") == type)
				list.add(atts[i]);
		}
		return list.toArray(new Document[0]);
	}

	private static Document getAttByType(Document[] atts, int type) {
		for (int i = 0; i < atts.length; i++) {
			if (atts[i].getInt("att_type") == type)
				return atts[i];
		}
		return null;
	}

	/**
	 * 设置挂件属性
	 * 
	 * @param docLibID
	 */
	private static void setWidgetJson(JSONObject redisJson, int docLibID,
			long docID) throws E5Exception {
		int widgetDocLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.WIDGET.typeID(), docLibID);
		int attLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.ATTACHMENT.typeID(), docLibID);

		// 读出所有挂件
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(widgetDocLibID,
				"w_articleID=? AND w_articleLibID=?", new Object[] { docID,
						docLibID });

		JSONArray picJsonArr = new JSONArray();
		JSONArray videoJsonArr = new JSONArray();
		JSONObject voteJsonArr = new JSONObject();
		JSONArray fileJsonArr = new JSONArray();

		for (int i = 0; i < docs.length; i++) {
			int type = docs[i].getInt("w_type");
			int objLibID = docs[i].getInt("w_objLibID");
			long objID = docs[i].getLong("w_objID");
			switch (type) {
			case 1:
				getWidgetAtts(picJsonArr, objLibID, objID, attLibID, type);
				break;
			case 2:
				getWidgetAtts(videoJsonArr, objLibID, objID, attLibID, type);
				break;
			case 3:
				getWidgetVote(voteJsonArr, objLibID, objID);
				break;
			case 0:
				JSONObject inJson = new JSONObject();
				inJson.put("ref",
						StringUtils.getNotNull(docs[i].getString("w_path")));
				inJson.put("attUrl",
						StringUtils.getNotNull(docs[i].getString("w_path")));
				inJson.put("attContent",
						StringUtils.getNotNull(docs[i].getString("w_content"))); // 附件描述

				fileJsonArr.add(inJson);
				break;
			default:
				break;
			}
		}

		JSONObject widgetsJson = new JSONObject();
		widgetsJson.put("pic", picJsonArr); // 组图
		widgetsJson.put("video", videoJsonArr); // 视频
		widgetsJson.put("vote", voteJsonArr); // 投票
		widgetsJson.put("file", fileJsonArr); // 附件

		redisJson.put("widgets", widgetsJson); // 挂件
	}

	// 读组图挂件的组图信息
	private static void getWidgetAtts(JSONArray inJsonArr, int docLibID,
			long picID, int attLibID, int type) throws E5Exception {
		int attType = (type == 1) ? 0 : 1;

		// 读出组图的每个图片
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docsAtt = docManager
				.find(attLibID,
						"att_articleID=? AND att_articleLibID=? AND att_type=? ORDER BY att_order",
						new Object[] { picID, docLibID, attType });

		int length_att = docsAtt.length;
		for (int j = 0; j < length_att; j++) {
			JSONObject inJson = new JSONObject();
			inJson.put("ref",
					StringUtils.getNotNull(docsAtt[j].getString("att_urlPad")));
			if (type == 1) {
				inJson.put("picType", 1); // 图片类型
				inJson.put("imageUrl", StringUtils.getNotNull(docsAtt[j]
						.getString("att_urlPad")));
				inJson.put("summary", StringUtils.getNotNull(docsAtt[j]
						.getString("att_content"))); // 附件描述
			} else {
				inJson.put("videoUrl", StringUtils.getNotNull(docsAtt[j]
						.getString("att_urlPad")));
				inJson.put("attAbstract", StringUtils.getNotNull(docsAtt[j]
						.getString("att_content"))); // 附件描述
			}

			inJsonArr.add(inJson);
		}
	}

	private static void getWidgetVote(JSONObject inJson, int voteLibID,
			long voteID) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document vote = docManager.get(voteLibID, voteID);

		// 投票属性
//		JSONObject inJson = new JSONObject();
		inJson.put("id", voteID);
//		inJson.put("type", vote.getInt("vote_type"));
		inJson.put("name", StringUtils.getNotNull(vote.getString("vote_topic")));
		inJson.put("endDate", vote.getString("vote_endDate"));
//		inJson.put("limited", vote.getInt("vote_selectLimited"));
		inJson.put("needLogin", vote.getInt("vote_needlogin"));

		JSONArray questions = new JSONArray();
		Document[] childVotes = docManager.find(voteLibID, "vote_rootid=?", new Object[] { voteID });
		for(Document childVote : childVotes){
			JSONObject question = new JSONObject();
			question.put("qid", childVote.getDocID());
			question.put("qname", StringUtils.getNotNull(childVote.getString("vote_topic")));
			question.put("qtype", childVote.getInt("vote_type"));

			// 投票选项
			int votOptLibID = LibHelper.getLibIDByOtherLib(
					DocTypes.VOTEOPTION.typeID(), voteLibID);
			Document[] options = docManager.find(votOptLibID, "vote_voteID=?",
					new Object[] { childVote.getDocID() });

			JSONArray optionsJson = new JSONArray();
			for (Document option : options) {
				JSONObject json = new JSONObject();
				json.put("oid", option.getDocID());
				json.put("oname", StringUtils.getNotNull(option.getString("vote_option")));
				String picUrl = StringUtils.getNotNull(option.getString("vote_picUrl"));
				if(picUrl.contains(";")){
					String[] dirs = InfoHelper.readSiteInfo(vote.getInt("vote_siteID"));
					picUrl = dirs[2] + File.separator + picUrl.substring(picUrl.lastIndexOf(";")+1);
				}
				json.put("pic",picUrl );

				optionsJson.add(json);
			}
			question.put("options", optionsJson);

			questions.add(question);
		}

		inJson.put("questions", questions);

//		inJsonArr.add(inJson);
	}

	/**
	 * 设置相关稿件属性
	 */
	private static void setRelationJson(JSONObject redisJson, int docLibID,
			long docID) throws E5Exception {
		String tenantCode = Tenant.DEFAULTCODE;
		int attLibId = LibHelper.getLibID(DocTypes.ATTACHMENT.typeID(),
				tenantCode);
		int videlLibId = LibHelper
				.getLibID(DocTypes.VIDEO.typeID(), tenantCode);
		// 读出相关稿件
		int artRelLibID = LibHelper.getLibIDByOtherLib(
				DocTypes.ARTICLEREL.typeID(), docLibID);
		
		String sql = "select distinct xaS.att_urlPad picS,xaM.att_urlPad picM,xaB.att_urlPad picB,xv.v_time,"
				+ " xaa.a_relID,xaa.SYS_TOPIC,xaa.a_url,xaa.a_source,xaa.a_sourceID,xaa.a_columnID,xaa.a_pubTime,xaa.a_type,"
				+ " xa.a_type,xa.a_content,xa.a_countDiscuss,xa.a_countPraise,xa.a_countShare,a_countClick,xa.a_countClickInitial"
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
			JSONArray dates = new JSONArray();
			while (rs.next()) {
				JSONObject date = new JSONObject();
				long fileId = rs.getLong("a_relID");
				date.put("relId", fileId); // 相关ID
				date.put("fileId", fileId); // 为与redis中的互动计数比较而添加的冗余字段
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

				ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
				Column col = colReader.get(LibHelper.getColumnLibID(), rs.getInt("a_columnID"));
				if(col==null){
                    date.put("columnIcon","");
                    date.put("columnName","");
                    date.put("columnRssCount","");
                }else{
                    date.put("columnIcon",col.getIconSmall());
                    date.put("columnName",col.getName());
                    date.put("columnRssCount",col.getRssCount());
                }

                //相关文章的话题字段,取一个话题
                String topicName = getTopicNameByID(fileId,1);
				date.put("topicName",topicName);

				int type = rs.getInt("a_type");
				if(type==Article.TYPE_PIC){
					date.put("picContent",StringUtils.getNotNull(rs.getString("a_content")));
				}else{
					date.put("picContent","");
				}

				dates.add(date);
			}
			redisJson.put("related", dates); // 相关稿件
		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

	}


    private static String getTopicNameByID(long id, int channel) {

        String sql = "select a_topicName from xy_topicrelart where a_articleID = ? and a_channel = ? limit 1";
        int channelValue;
        if(channel==1){//app
            channelValue = 2;
        }else{//web
            channelValue = 1;
        }

        Object[] param = new Object[]{id,channelValue};
        String result = getSqlResult(sql, param);

        return result;
    }

    private static String getSqlResult(String sql, Object[] param){
        String resultValue = "";
        IResultSet rs = null;
        DBSession conn = null;
        try {
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql,param);
            while (rs.next()) {
                resultValue = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }

        return resultValue;
    }

	/**
	 * 组装稿件列表中的一个稿件
	 */
	private static JSONObject listArticleBaseField(int docLibID, long docID)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		if (doc == null || doc.getInt("a_status") != Article.STATUS_PUB_DONE)
			return null;

		long version = doc.getLong("a_realPubTime");
		if (version < 0) version = doc.getLong("a_pubTime");
		
		JSONObject inJson = new JSONObject();
		inJson.put("fileId", docID);
		inJson.put("version", version);
		inJson.put("isRel", 0);

		inJson.put("title",StringUtils.getNotNull(doc.getString("SYS_TOPIC")));
		inJson.put("textTitle",StringUtils.xhtml2Text(doc.getString("SYS_TOPIC")));
		inJson.put("attAbstract",StringUtils.getNotNull(doc.getString("a_abstract")));
		inJson.put("textAbstract",StringUtils.xhtml2Text(doc.getString("a_abstract")));

		inJson.put("arthorID", doc.getInt("SYS_AUTHORID")); //作者名
		inJson.put("arthorName", StringUtils.getNotNull(doc.getString("SYS_AUTHORS"))); //作者ID
		inJson.put("publishtime", InfoHelper.formatDate(doc.getTimestamp("a_pubTime")));
		inJson.put("articleType", doc.getInt("a_type"));

		inJson.put("tag", StringUtils.getNotNull(doc.getString("a_tag")));
		inJson.put("trade", StringUtils.getNotNull(doc.getString("a_trade"))); //行业分类
		inJson.put("tradeID", StringUtils.getNotNull(doc.getString("a_tradeID")));
		inJson.put("colName", doc.getString("a_column"));
		inJson.put("colID", doc.getInt("a_columnID"));
		ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
		Column col = colReader.get(LibHelper.getColumnLibID(), doc.getInt("a_columnID"));
		if(col==null){
            inJson.put("columnIcon","");
            inJson.put("colColor","");
        }else{
            String icon = StringUtils.getNotNull(col.getIconSmall());
            if (null == icon || "".equals(icon)){
                icon = StringUtils.getNotNull(col.getIconBig());
            }
            inJson.put("columnIcon",icon);
            inJson.put("colColor",StringUtils.getNotNull(col.getColor()));
            String colName = col.getName();
            if(!StringUtils.isBlank(colName)){
				inJson.put("colName",colName);
			}
        }

		inJson.put("mark", StringUtils.getNotNull(doc.getString("a_mark"))); //列表角标

		inJson.put("source", StringUtils.getNotNull(doc.getString("a_source"))); // 来源
		inJson.put("multimediaLink", StringUtils.getNotNull(doc.getString("a_multimediaLink"))); //多媒体链接
		inJson.put("shareUrl", StringUtils.getNotNull(doc.getString("a_urlPad")));
		inJson.put("url", doc.getString("a_url"));
		inJson.put("urlPad", doc.getString("a_urlPad"));
		// 若是链接稿、广告稿，则直接填写url
		String url;
		int type = doc.getInt("a_type");
		if (type >= Article.TYPE_SPECIAL && type != Article.TYPE_ACTIVITY
				&& type != Article.TYPE_PANORAMA && type != Article.TYPE_FILE) {
			url = StringUtils.getNotNull(doc.getString("a_urlPad"));
			inJson.put("isBigPic", 0);// 原大图稿件标记。避免app旧版在直播稿、链接稿等时闪退
		} else {
			// 稿件内容url，带当前栏目ID，以便稿件详情页里按当前栏目显示广告信息（如：首页的广告与其它栏目不同）
			url = UrlHelper.getArticleContentUrl(docID);
			inJson.put("isBigPic", getInt(doc.getInt("a_isBigPic")));// 大图稿件
		}
		inJson.put("bigPic", getInt(doc.getInt("a_isBigPic")));// 大图稿件
		inJson.put("contentUrl", url);

		inJson.put("linkID", getlong(doc.getLong("a_linkID")));
		inJson.put("linkName", StringUtils.getNotNull(doc.getString("a_linkName")));

		// 初始阅读数，用于外网api从缓存redis读实际阅读数时相加
		// 显示阅读数：数据库中的初始阅读数+实际阅读数+分享页的阅读数。在外网api里会替换成实时的阅读数，取自redis缓存
		int countClickInitial = getInt(doc.getInt("a_countClickInitial"));
		int countClickReal = getInt(doc.getInt("a_countClick"));
		int countShareClick = getInt(doc.getInt("a_countShareClick"));
		int countClickAll = countClickInitial + countClickReal + countShareClick;

		inJson.put("countClick", countClickAll); // 所有点击数
		inJson.put("countClickInitial", countClickInitial); // 初始点击数
		inJson.put("countClickReal", countClickReal); // 实际点击数
		inJson.put("countShareClick", countShareClick);// 分享点击数

		inJson.put("countDiscuss", getlong(doc.getLong("a_countDiscuss")));
		inJson.put("countPraise", getlong(doc.getLong("a_countPraise")));
		inJson.put("countShare", getlong(doc.getLong("a_countShare")));

		//组图稿件，增加返回字段content;其他稿件不增加
        if(type==Article.TYPE_PIC){
            inJson.put("picContent",StringUtils.getNotNull(doc.getString("a_content")));
        }else{
            inJson.put("picContent","");
        }

		// 扩展字段组ID，用于后面读扩展字段
		inJson.put("extGroupID", getInt(doc.getInt("a_extFieldGroupID")));
        //直播状态
		inJson = setOtherInfo(inJson, doc);
		//inJson.put("liveStatus", doc.getInt("a_liveStatus"));
		
		//设置话题
		setTopic(inJson,doc);

        List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
                Tenant.DEFAULTCODE);
        int artLibID = articleLibs.get(0).getDocLibID();
        if(artLibID == docLibID){
            //新增需求字段
            addColumnUrl(inJson,col,colReader);
        }

		return inJson;
	}

    private static void addColumnUrl(JSONObject jsonObject, Column column, ColumnReader colReader) {
        if(column!=null){
            int columnLibID = column.getLibID();
            int templateLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), columnLibID);

            BaseDataCache baseDataCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));

            Template[] template = new Template[2];
            template[0] = baseDataCache.getTemplateByID(templateLibID, column.getTemplate());
            template[1] = baseDataCache.getTemplateByID(templateLibID, column.getTemplatePad());

            int siteRuleLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), columnLibID);

            SiteRule siteRule0 = baseDataCache.getSiteRuleByID(siteRuleLibID, column.getPubRule());
            SiteRule siteRule1 = baseDataCache.getSiteRuleByID(siteRuleLibID, column.getPubRulePad());

            //把发布地址也读出来
            String url = colReader.getColumnUrl(column.getId(), column.getFileName(), template[0], siteRule0);
            String urlPad = colReader.getColumnUrl(column.getId(), column.getFileNamePad(), template[1], siteRule1);
            jsonObject.put("columnUrl", StringUtils.isBlank(url)?"":url);
            jsonObject.put("columnUrlPad", StringUtils.isBlank(urlPad)?"":urlPad);
        }else{
            jsonObject.put("columnUrl", "");
            jsonObject.put("columnUrlPad", "");
        }
    }

	/**
	 * 设置附件表和扩展字段表中的字段
	 */
	private static void listArticleAttExtField(JSONObject inJson, long docID,
			int attDocLibID, int artExtDocLibID, int docLibID)
			throws E5Exception {
		Object[] params = new Object[] { docID, docLibID };

		// 附件
		listAtts(inJson, attDocLibID, params);

		// 扩展字段表
		listExtFields(inJson, artExtDocLibID, docLibID, params);
	}

	/**
	 * 认证记者：confirmAuthor
	 认证羊城号：confirmXY
	 角标：mark（现有字段修改）
	 推荐标识：加上字段isRecommed 是否是推荐（一期不做）
	 直播状态：liveStatus
	 列表加原创：copyright
	 * @param inJson
	 * @param doc
	 * @return
	 * @throws E5Exception
	 */
	private static JSONObject setOtherInfo(JSONObject inJson, Document doc) throws E5Exception {
		inJson.put("liveStatus", doc.getInt("a_liveStatus"));

		return inJson;
	}

	private static void setTopic(JSONObject inJson, Document doc) throws E5Exception {
		DBSession conn = null;
		IResultSet rs = null;
		//如果当前稿件没有话题，则返回空
        inJson.put("topicID", "");
        inJson.put("topicName", "");
        inJson.put("topicColor", "");
		Object[] params = new Object[] { doc.getDocID(), doc.getInt("a_channel")};
		try {
			String sql = "SELECT a.a_topicID, b.SYS_TOPIC, b.a_color from xy_topicrelart a left join " +
                    " xy_topics b on a.a_topicID = b.SYS_DOCUMENTID and b.SYS_DELETEFLAG = 0 where a_articleID=? and a_channel=? order by a.a_topicID desc limit 1";

			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, params);
			
			if(rs.next()){
				inJson.put("topicID", rs.getInt("a_topicID"));
				inJson.put("topicName", StringUtils.getNotNull(rs.getString("SYS_TOPIC")));
                inJson.put("topicColor", StringUtils.getNotNull(rs.getString("a_color")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}
	}

	private static void listAtts(JSONObject inJson, int attDocLibID,
			Object[] params) throws E5Exception {
		// 附件表
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(attDocLibID,
				"att_articleID=? and att_articleLibID=? ORDER BY att_order",
				params);

		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(),
				Tenant.DEFAULTCODE);
		int artLibID = articleLibs.get(0).getDocLibID();
		int docLibID = (int) params[1];
		boolean isWeb = false;
		if(artLibID == docLibID){//web
			isWeb = true;
		}

		int length = 0;
		for (Document doc : docs) {
			String url;
			if(isWeb){
				url = StringUtils.getNotNull(doc.getString("att_url"));
			}else{
				url = StringUtils.getNotNull(doc.getString("att_urlPad"));
			}

			int type = doc.getInt("att_type");
			if (0 == type) {
				if (length < 3)
					inJson.put("pic" + length, url);// 列出前三个图片的url，外网地址
				length++;
			} else if (1 == type) {
				inJson.put("videoUrl", url); // 稿件对应视频的url，视频稿件时使用，附件表中查，外网地址
				inJson.put("duration",doc.getInt("att_duration")); //视频时长。若多个视频附件时会重复替换到最后一个
			} else if (2 == type) {
				inJson.put("picBig", url); // big
			} else if (3 == type) {
				inJson.put("picMiddle", url); // middle
			} else if (4 == type) {
				inJson.put("picSmall", url); // small
			}
		}
		inJson.put("picCount", String.valueOf(length)); // 正文图片/组图图片数量
	}

	private static void listExtFields(JSONObject inJson, int aextLibID,
			int docLibID, Object[] params) throws E5Exception {
		// 稿件的扩展字段组ID（稿件可自设扩展字段组，也可以使用栏目中的设置）
		int extGroupID = getExtGroupID(inJson);
		if (extGroupID <= 0)
			return;

		// 取出扩展字段定义
		int extLibID = LibHelper.getLibIDByOtherLib(DocTypes.EXTFIELD.typeID(),
				docLibID);
		ExtFieldReader extReader = (ExtFieldReader) Context
				.getBean("extFieldReader");
		Set<ExtField> fields = extReader.getFields(extLibID, extGroupID);
		if (fields == null)
			return;

		// 取出稿件的扩展字段值，查到扩展字段名，放进json
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] exts = docManager.find(aextLibID,
				"ext_articleID=? and ext_articleLibID=?", params);
		for (int i = 0; i < exts.length; i++) {
			ExtField field = findName(fields, exts[i].getString("ext_code"));
			if (field != null) {
				inJson.put(field.getExt_name(),
						StringUtils.getNotNull(exts[i].getString("ext_value")));
			}
		}
	}

	// 读稿件的扩展字段组ID（稿件可自设扩展字段组，也可以使用栏目中的设置）
	private static int getExtGroupID(JSONObject inJson) {
		int extGroupID = inJson.getInt("extGroupID");
		if (extGroupID <= 0) {
			try {
				ColumnReader colReader = (ColumnReader) Context
						.getBean("columnReader");
				Column col = colReader.get(LibHelper.getColumnLibID(),
						inJson.getInt("colID"));
				extGroupID = col.getExtFieldGroupID();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return extGroupID;
	}

	// 按扩展字段code找到名称
	private static ExtField findName(Set<ExtField> fields, String code) {
		for (ExtField extField : fields) {
			if (extField.getExt_code().equals(code))
				return extField;
		}
		return null;
	}

	private static int getInt(int value) {
		if (value < 0)
			return 0;
		return value;
	}

	private static long getlong(long value) {
		if (value < 0)
			return 0;
		return value;
	}
}
