package com.founder.xy.jpublish;

import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.db.IResultSet;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.*;
import com.founder.xy.jpublish.data.*;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发布服务中为稿件api提供数据的辅助类。
 * 把稿件组织成列表api可用的稿件json、详情api可用的稿件json
 * @author Gong Lijie
 */
public class PubArticleJsonHelper {
	/**
	 * 组织一篇稿件的json
	 */
	public static void putRedisArticle(PubArticle article) {
		JSONObject redisJson = new JSONObject();
		
		//设置基础属性
		setBasicField(redisJson, article);
		
		//设置栏目属性
		int mainColID = article.getColumnID();
		setArticleColumn(redisJson, mainColID);

		//如果是记者稿则添加记者名片
		if (1 == article.getSourceType()) {
			setAuthorInfo(redisJson, article);
		}
		
		//处理内容
		setContent(redisJson, article);
		
		//扩展字段
		setExtFields(redisJson, article);
		
		// 挂件
		setWidgetJson(redisJson, article);

		// 相关稿件
		setRelationJson(redisJson, article);
		
		String json = redisJson.toString();
		//System.out.println("json size(for article api):" + json.getBytes().length);
		
		//app.article.<ArticleID>
		RedisManager.setLonger(RedisKey.APP_ARTICLE_KEY + article.getId(), json);
	}

	/**
	 * 组织稿件列表中的一篇稿件的json，写入redis
	 * @param article
	 */
	public static void putRedisListOne(PubArticle article) {
		
		JSONObject inJson = listArticleBaseField(article);
		
		listArticleAtts(inJson, article);
		setExtFields(inJson, article);

        //设置栏目属性
        int mainColID = article.getColumnID();
        setArticleColumn(inJson, mainColID);

		String json = inJson.toString();
		//System.out.println("json size(for list api):" + json.getBytes().length);
		//app.alist.one.<ArticleID>
		RedisManager.setLonger(RedisKey.APP_ARTICLELIST_ONE_KEY + article.getId(), json);
	}

	/**
	 * 设置基本属性
	 */
	private static void setBasicField(JSONObject redisJson, PubArticle doc) {
		redisJson.put("fileId", doc.getId());
		redisJson.put("siteID",doc.getSiteID());
		redisJson.put("version", doc.getPubTimeReal().getTime());
		redisJson.put("title", StringUtils.getNotNull(doc.getTitle()));
		redisJson.put("attAbstract", StringUtils.getNotNull(doc.getSummary()));
		redisJson.put("publishtime", InfoHelper.formatDate(doc.getPubTime()));
		redisJson.put("source", StringUtils.getNotNull(doc.getSource())); // 来源
		redisJson.put("sourceUrl", StringUtils.getNotNull(doc.getSourceUrl())); // 来源链接
		redisJson.put("author", StringUtils.getNotNull(doc.getAuthor())); // 作者
		redisJson.put("editor", StringUtils.getNotNull(doc.getEditor())); // 编辑
		redisJson.put("subtitle", StringUtils.getNotNull(doc.getSubTitle())); // 副题
		redisJson.put("tag", StringUtils.getNotNull(doc.getTag())); // 标签
		redisJson.put("columnID", doc.getColumnID());
        redisJson.put("colID", doc.getColumnID());
//		redisJson.put("columnName", doc.getColumn());
		redisJson.put("discussClosed", doc.getDiscussClosed());
		redisJson.put("articleType", doc.getType());
		redisJson.put("shareUrl", doc.getUrlPad());
		redisJson.put("url", doc.getUrl());//评论提交用
		redisJson.put("collaborator", doc.getCollaborator());
		redisJson.put("multimediaLink", StringUtils.getNotNull(doc.getMultimediaLink())); //多媒体链接
		redisJson.put("mark", StringUtils.getNotNull(doc.getMark())); //列表角标
		redisJson.put("trade", StringUtils.getNotNull(doc.getTrade())); //行业分类
		redisJson.put("tradeID", StringUtils.getNotNull(doc.getTradeID()));
		redisJson.put("linkName", StringUtils.getNotNull(doc.getLinkName()));//外链信息
		redisJson.put("linkID", doc.getLinkID());
		
		redisJson.put("content", doc.getContent());

		String key = RedisKey.NIS_EVENT_ARTICLE+ doc.getId();
		redisJson.put("countDiscuss", getEventCount(key, "d", doc.getCountDiscuss())); // 评论数
		redisJson.put("countPraise",doc.getCountPraise()); // 点赞数
		redisJson.put("countShare", getEventCount(key, "s", doc.getCountShare())); // 分享数
		redisJson.put("countClick", getEventCount(key, "c", doc.getCountClick())); // 点击数
		redisJson.put("countClickInitial",doc.getCountClickInitial()); // 初始点击数


        if(doc.getType() == Article.TYPE_PIC){
            redisJson.put("picContent",StringUtils.getNotNull(doc.getContent()));
        }else{
            redisJson.put("picContent","");
        }
        //设置稿件列表中的话题相关字段
//        JSONObject topic = getTopicNameByID(doc.getId(),1);
//        redisJson.put("topicName", topic.getString("topicName"));
//        redisJson.put("topicID", topic.getString("topicID"));

		//设置稿件详情中的话题
		setTopics(redisJson,doc);
	}

    private static void setTopics(JSONObject inJson, PubArticle doc) {
        DBSession conn = null;
        IResultSet rs = null;
        Object[] params = new Object[] { doc.getId(), doc.getChannel()};
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
			net.sf.json.JSONArray arr = new net.sf.json.JSONArray();
			inJson.put("topics", arr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(rs);
            ResourceMgr.closeQuietly(conn);
        }
    }

	private static long getEventCount(String key, String field,int count) {
		String rcs = RedisManager.hget(key, field);
		if (rcs != null) {
			long rc = Long.parseLong(rcs);
			return count > rc ? count : rc;
		}else{
			return count;
		}
	}

	//稿件详情json中，添加主栏目的描述、图标等信息
	private static void setArticleColumn(JSONObject redisJson, int mainColID) {
		Column col = null;
		try {
			ColumnReader colReader = (ColumnReader)Context.getBean("columnReader");
			col = colReader.get(LibHelper.getColumnLibID(), mainColID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		
		if (null != col){
			// 栏目图标
			String icon = StringUtils.getNotNull(col.getIconSmall());
			if (null == icon || "".equals(icon)){
				icon = StringUtils.getNotNull(col.getIconBig());
			}
			redisJson.put("columnIcon", icon);
			
			// 栏目描述
			redisJson.put("columnDescription", StringUtils.getNotNull(col.getDescription()));
	        // 栏目类型
			redisJson.put("columnType", InfoHelper.getCatCode(CatTypes.CAT_COLUMN.typeID(), col.getAppTypeID()));
	        // 栏目样式
			redisJson.put("columnStyle", InfoHelper.getCatCode(CatTypes.CAT_COLUMNSTYLE.typeID(), col.getAppStyleID()));

            redisJson.put("forbidden", col.isForbidden());//是否禁用

			redisJson.put("columnName",
					StringUtils.getNotNull(col.getName())); // 主栏目名
			redisJson.put("colName",
					StringUtils.getNotNull(col.getName())); // 主栏目名
            redisJson.put("colColor",StringUtils.getNotNull(col.getColor()));

		}
	}

	private static void setAuthorInfo(JSONObject redisJson, PubArticle article) {
		JSONObject author = new JSONObject();
		author.put("id", article.getAuthorInfo().getId());
		author.put("name", article.getAuthorInfo().getName());
		author.put("url", article.getAuthorInfo().getUrl());
		author.put("duty", article.getAuthorInfo().getDuty());
		author.put("description", article.getAuthorInfo().getDescription());
		
		redisJson.put("author", author);
	}

	//处理内容
	private static void setContent(JSONObject redisJson, PubArticle doc) {
		List<Attachment> atts = doc.getAttachments();
		if (atts == null || atts.isEmpty()) return;
		
		String content = doc.getContent();
		
		//按稿件类型处理内容
		int type = doc.getType();
		if (type == 0) { // 文章稿
			content = getArticleContent(redisJson, content, atts);
		} else if (type == 1) { // 组图稿
			content = getPicContent(redisJson, content, atts);
		} else if (type == 2) { // 视频稿
			content = getVideoContent(redisJson, content, atts);
		}
		
		redisJson.put("content", content); // 内容
	}

	private static void setExtFields(JSONObject inJson, PubArticle article) {
		//扩展字段
		HashMap<String, String> exts = article.getExtFields();
		if (exts != null) {
			for (String key : exts.keySet()) {
				inJson.put(key, StringUtils.getNotNull(exts.get(key)));
			}
		}
	}

	/**
	 * 文章稿属性
	 */
	private static String getArticleContent(JSONObject redisJson,String content, List<Attachment> atts) {
		//取到所有附件
		org.jsoup.nodes.Document html = Jsoup.parse(content);
		
		//替换稿件内容中的图片部分
		replaceAttPics(redisJson, atts, html);
		
		//替换稿件内容中的视频部分
		replaceAttVideos(redisJson, atts, html);
		
		//取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);
	
		return html.html();
	}

	/**
	 * 图片稿属性
	 */
	private static String getPicContent(JSONObject redisJson, String content, List<Attachment> atts) {
		JSONObject inJson = null;
		JSONArray jsonArr = new JSONArray();
		JSONArray inJsonArr = new JSONArray();
	
		// 图片
		List<Attachment> docs = getAttsByType(atts, 0);
		int length = docs.size();
		for (Attachment att : docs) {
			String url = StringUtils.getNotNull(att.getUrlPad());
			inJson = new JSONObject();
			inJson.put("ref", url);
			inJson.put("picType", 1); // 图片类型
			inJson.put("summary", StringUtils.getNotNull(att.getContent())); // 附件描述
			inJson.put("imageUrl", url);
	
			inJsonArr.put(inJson);
		}
		if (length > 0){
			inJson = new JSONObject();
			inJson.put("ref", "<!--IMAGES#1-->");
			inJson.put("imagearray", inJsonArr);
			jsonArr.put(inJson);
			redisJson.put("images", jsonArr);
			
			content = "<!--IMAGES#1-->" + content;
		}
	
		//取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);
	
		return content;
	}

	/**
	 * 视频稿属性
	 */
	private static String getVideoContent(JSONObject redisJson, String content, List<Attachment> atts) {
		// 视频
		List<Attachment> docs = getAttsByType(atts, 1);
		int length = docs.size();
		
		
		JSONArray inJsonArr = new JSONArray();
		for (int i = 0; i < docs.size(); i++) {
			Attachment att = docs.get(i);
			
			String tag = StringUtils.getNotNull(att.getUrlPad());
			
			JSONObject inJson = new JSONObject();
			inJson.put("ref", "<!--VIDEOARRAY#" + i + "-->");
			inJson.put("attAbstract", StringUtils.getNotNull(att.getContent())); // 附件描述
			inJson.put("videoUrl", tag);
			inJson.put("imageUrl", StringUtils.getNotNull(att.getPicUrlPad())); // 关键帧外网地址
			inJson.put("duration",att.getDuration()); //时长

			inJsonArr.put(inJson);
		}
		if(length > 0){
			JSONObject inJson = new JSONObject();
			inJson.put("ref", "<!--VIDEOS#1-->");
			inJson.put("videoarray", inJsonArr);
			
			JSONArray jsonArr = new JSONArray();
			jsonArr.put(inJson);
			
			redisJson.put("videos", jsonArr); // (视频信息定义)
			
			content = "<!--VIDEOS#1-->" + content;
		}
	
		//取出标题图片，写入json
		setArticleImgUrl(atts, redisJson);
	
		return content;
	}

	//替换稿件内容中的图片部分
	private static void replaceAttPics(JSONObject redisJson, List<Attachment> atts, org.jsoup.nodes.Document html) {
		List<Attachment> docs = getAttsByType(atts, 0);
		if (docs == null || docs.size() == 0) return;
		
		Elements list = html.select("img");
		if (list.isEmpty()) return;
		
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
			inJson.put("imageUrl", StringUtils.getNotNull(docs.get(i).getUrlPad())); // 外网地址
			inJsonArr.put(inJson);
			i++;
		}
		if (i > 0) attPicArray(redisJson, inJsonArr);
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
		jsonArr.put(inJson);
		
		redisJson.put("images", jsonArr); // 图片信息定义
	}

	//替换稿件内容中的视频部分
	private static void replaceAttVideos(JSONObject redisJson, List<Attachment> atts,
			org.jsoup.nodes.Document html) {
		List<Attachment> docs = getAttsByType(atts, 1);
		if (docs == null || docs.size() == 0) return;
		
		Elements list = html.select("embed, audio, video");
		if (list.isEmpty()) return;
		
		int i = 0;
		JSONArray videoArray = new JSONArray();
		for (Element embed : list) {
			String VIDEOARRAY = "<!--VIDEOARRAY#" + i +"-->";
			embed.after(VIDEOARRAY);
			embed.remove();
			
			JSONObject inJson = new JSONObject();
			inJson.put("ref", VIDEOARRAY);
			inJson.put("videoUrl", StringUtils.getNotNull(docs.get(i).getUrlPad())); // 外网地址
			inJson.put("imageUrl", StringUtils.getNotNull(docs.get(i).getPicUrlPad())); // 关键帧外网地址
			inJson.put("duration",docs.get(i).getDuration()); //时长
			
			videoArray.put(inJson);
			i++;
		}
		if(i > 0) attVideoArray(redisJson, videoArray);
	}

	private static void attVideoArray(JSONObject redisJson, JSONArray videoArray) {
		JSONObject videoGroup = new JSONObject();
		videoGroup.put("ref", "<!--VIDEOS#1-->"); // 第一组视频信息定义":"<!--VIDEOS#0-->",
		videoGroup.put("videoarray", videoArray);
		
		JSONArray videos = new JSONArray();
		videos.put(videoGroup);
		
		redisJson.put("videos", videos); // (视频信息定义)
	}

	//取出标题图片url，写入稿件json
	private static void setArticleImgUrl(List<Attachment> atts, JSONObject redisJson) {
		String imgUrl = null;
		Attachment pic = getAttByType(atts, 4); //标题图片-小
		if (pic != null) {
			imgUrl = pic.getUrlPad();
		} else {
			pic = getAttByType(atts, 3); //标题图片-中
			if (pic != null) {
				imgUrl = pic.getUrlPad();
			} else {
				pic = getAttByType(atts, 2); //标题图片-大
				if (pic != null) {
					imgUrl = pic.getUrlPad() + ".0"; //标题图片大图有抽图
				} else {
					imgUrl = "";
				}
			}
		}
		redisJson.put("imageUrl", imgUrl);
	}

	private static List<Attachment> getAttsByType(List<Attachment> atts, int type) {
		List<Attachment> list = new ArrayList<>();
	
		for (Attachment att : atts) {
			if (att.getType() == type)
				list.add(att);
		}
		return list;
	}
	private static Attachment getAttByType(List<Attachment> atts, int type) {
		for (Attachment att : atts) {
			if (att.getType() == type)
				return att;
		}
		return null;
	}

	/**
	 * 设置挂件属性
	 */
	private static void setWidgetJson(JSONObject redisJson, PubArticle doc) {
		Widgets docs = doc.getWidgets();
		if (docs == null) return;
		
		JSONArray picJsonArr = new JSONArray();
		JSONArray videoJsonArr = new JSONArray();
		JSONObject voteJsonArr = new JSONObject();
		JSONArray fileJsonArr = new JSONArray();
		
		getWidgetPics(picJsonArr, docs.getPic());
		getWidgetVideos(videoJsonArr, docs.getVideo());
		getWidgetVote(voteJsonArr, docs.getVote(), doc);
		getWidgetFiles(fileJsonArr, docs.getAttachments());
	
		JSONObject widgetsJson = new JSONObject();
		widgetsJson.put("pic", picJsonArr); 	// 组图
		widgetsJson.put("video", videoJsonArr); // 视频
		widgetsJson.put("vote", voteJsonArr); 	// 投票
		widgetsJson.put("file", fileJsonArr); 	// 附件
	
		redisJson.put("widgets", widgetsJson); // 挂件
	}

	//读组图挂件的组图信息
	private static void getWidgetPics(JSONArray inJsonArr, WidgetPic widgetPic) {
		if (widgetPic == null) return;
		
		List<Widget> pics = widgetPic.getMembers();
		if (pics != null) {
			for (Widget pic : pics) {
				JSONObject inJson = new JSONObject();
				inJson.put("ref", StringUtils.getNotNull(pic.getUrl()));
				inJson.put("picType", 1); // 图片类型
				inJson.put("imageUrl", StringUtils.getNotNull(pic.getUrl()));
				inJson.put("summary", StringUtils.getNotNull(pic.getContent())); // 附件描述
	
				inJsonArr.put(inJson);
			}
		}
	}

	//读视频挂件
	private static void getWidgetVideos(JSONArray inJsonArr, Widget w) {
		if (w == null || StringUtils.isBlank(w.getUrl())) return;
		
		JSONObject inJson = new JSONObject();
		inJson.put("ref", StringUtils.getNotNull(w.getUrl()));
		inJson.put("videoUrl", StringUtils.getNotNull(w.getUrl()));
		inJson.put("attAbstract", StringUtils.getNotNull(w.getContent())); // 附件描述

		inJsonArr.put(inJson);
	}

	private static void getWidgetVote(JSONObject inJson, Vote votes, PubArticle doc) {
		if (votes == null){
			// 读出所有挂件
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			try {
				Document[] docs = docManager.find(LibHelper.getWidgetLibID(),
						" w_articleID=? AND w_articleLibID=? and w_type=3 ", new Object[] { doc.getId(),
								doc.getDocLibID() });
				if(docs != null && docs.length > 0)
					votes = new Vote(docs[0].getInt("w_objID"), null, 0, 0, null, null);
				else return;
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}

		//投票属性
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document vote;
		try {
			vote = docManager.get(LibHelper.getVote(), votes.getId());
			System.out.println("vote" + vote==null);
			// 投票属性
			inJson.put("id", votes.getId());
			inJson.put("name", StringUtils.getNotNull(vote.getString("vote_topic")));
			inJson.put("endDate", vote.getString("vote_endDate"));
			inJson.put("needLogin", vote.getInt("vote_needlogin"));

			net.sf.json.JSONArray questions = new net.sf.json.JSONArray();
			Document[] childVotes = docManager.find(vote.getDocLibID(), "vote_rootid=?", new Object[] { votes.getId() });
			System.out.println("childVotes" + childVotes.length + "," +  votes.getId() +"," + vote.getDocLibID());
			for(Document childVote : childVotes){
				net.sf.json.JSONObject question = new net.sf.json.JSONObject();
				question.put("qid", childVote.getDocID());
				question.put("qname", StringUtils.getNotNull(childVote.getString("vote_topic")));
				question.put("qtype", childVote.getInt("vote_type"));

				// 投票选项
				int votOptLibID = LibHelper.getLibIDByOtherLib(
						DocTypes.VOTEOPTION.typeID(), vote.getDocLibID());
				Document[] options = docManager.find(votOptLibID, "vote_voteID=?",
						new Object[] { childVote.getDocID() });

				net.sf.json.JSONArray optionsJson = new net.sf.json.JSONArray();
				for (Document option : options) {
					net.sf.json.JSONObject json = new net.sf.json.JSONObject();
					json.put("oid", option.getDocID());
					json.put("oname", StringUtils.getNotNull(option.getString("vote_option")));
					String picUrl = StringUtils.getNotNull(option.getString("vote_picUrl"));
					if(picUrl.contains(";")){
						String[] dirs = InfoHelper.readSiteInfo(doc.getSiteID());
						picUrl = dirs[2] + File.separator + picUrl.substring(picUrl.lastIndexOf(";")+1);
					}
					json.put("pic",picUrl );

					optionsJson.add(json);
				}
				question.put("options", optionsJson);
				questions.add(question);
			}
			inJson.put("questions", questions);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
	}

	private static void getWidgetFiles(JSONArray fileJsonArr, List<Attachment> files) {
		if (files != null) {
			for (Attachment file : files) {
				JSONObject inJson = new JSONObject();
				inJson.put("ref", StringUtils.getNotNull(file.getUrlPad()));
				inJson.put("attUrl", StringUtils.getNotNull(file.getUrlPad()));
				inJson.put("attContent", StringUtils.getNotNull(file.getContent())); // 附件描述
				
				fileJsonArr.put(inJson);
			}
		}
	}

	/**
	 * 设置相关稿件属性
	 */
	private static void setRelationJson(JSONObject redisJson, PubArticle doc) {
		//读出相关稿件
		List<BareArticle> docs = doc.getRels();
		if (docs == null) return;

		//拼出json
		JSONArray jsonArr = new JSONArray();
		for (int i = 0; i < docs.size(); i++) {
			BareArticle rel = docs.get(i);
			
			JSONObject inJson = new JSONObject();
			inJson.put("relId", rel.getId()); // 相关ID
			inJson.put("fileId", rel.getId()); // 为与redis中的互动计数比较而添加的冗余字段
			inJson.put("articleType", rel.getType());
			inJson.put("title", StringUtils.getNotNull(rel.getTitle())); // 附件描述
			inJson.put("textTitle",StringUtils.xhtml2Text(rel.getTitle()));

			inJson.put("source", StringUtils.getNotNull(rel.getSource())); // 相关稿件来源
			inJson.put("sourceID", rel.getSourceID()); // 相关稿件来源ID
			inJson.put("picB", StringUtils.getNotNull(rel.getPicBig()));
			inJson.put("picM", StringUtils.getNotNull(rel.getPicMiddle()));
			inJson.put("picS", StringUtils.getNotNull(rel.getPicSmall()));

			inJson.put("countDiscuss", rel.getCountDiscuss());  //评论数
			inJson.put("countPraise", rel.getCountPraise());  //点赞数
			inJson.put("countShare", rel.getCountShare());  //分享数
			inJson.put("countClick", rel.getCountClick());  //点击数
			inJson.put("countClickInitial", rel.getCountClickInitial()); //初始化点击数

			inJson.put("publishtime", InfoHelper.formatDate(rel.getPubTime()));
			inJson.put("relUrl", StringUtils.getNotNull(rel.getUrl())); // URL
			inJson.put("contentUrl", UrlHelper.getArticleContentUrl(rel.getId()));
            inJson.put("columnName", StringUtils.getNotNull(rel.getColumn()));
            inJson.put("columnIcon", StringUtils.getNotNull(rel.getMasterColIcon()));

            //话题相关字段
            JSONObject topic = getTopicNameByID(rel.getId(),1);
            inJson.put("topicName", topic.getString("topicName"));
            inJson.put("topicID", topic.getString("topicID"));

			if(rel.getType()==Article.TYPE_PIC){
				inJson.put("picContent",rel.getContent());
			}else{
				inJson.put("picContent","");
			}

            jsonArr.put(inJson);
        }
        redisJson.put("related", jsonArr); // 相关稿件
    }

    //设置话题名称
    private static JSONObject getTopicNameByID(long id, int channel) {
        DBSession conn = null;
        IResultSet rs = null;
        JSONObject jsonObject = new JSONObject();
        String topicName = "";
        String topicID = "";
        String topicColor = "";
        int channelValue;
        if(channel==1){//app
            channelValue = 2;
        }else{//web
            channelValue = 1;
        }
        try {
            String sql = "SELECT a.a_topicID, b.SYS_TOPIC, b.a_color from xy_topicrelart a left join " +
                    " xy_topics b on a.a_topicID = b.SYS_DOCUMENTID and b.SYS_DELETEFLAG = 0 where a_articleID=? and a_channel=? order by a.a_topicID desc limit 1";
            conn = Context.getDBSession();
            rs = conn.executeQuery(sql, new Object[]{id,channelValue});
            while (rs.next()) {
                topicName = rs.getString("SYS_TOPIC");
                topicID = rs.getString("a_topicID");
                topicColor = rs.getString("a_color");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(conn);
            ResourceMgr.closeQuietly(rs);
        }
        jsonObject.put("topicID",topicID);
        jsonObject.put("topicName",topicName);
        jsonObject.put("topicColor",topicColor);

        return jsonObject;
    }
	/**
	 * 组装稿件列表中的一个稿件
	 */
	private static JSONObject listArticleBaseField(PubArticle article) {
		JSONObject inJson = new JSONObject();
		
		inJson.put("fileId", article.getId());
		inJson.put("title", StringUtils.getNotNull(article.getTitle()));
		inJson.put("textTitle",StringUtils.xhtml2Text(article.getTitle()));
		inJson.put("version", article.getPubTimeReal().getTime());

		inJson.put("attAbstract", StringUtils.getNotNull(article.getSummary()));
		inJson.put("textAbstract",StringUtils.xhtml2Text(article.getSummary()));
		inJson.put("publishtime", InfoHelper.formatDate(article.getPubTime()));
		inJson.put("articleType", article.getType());
		inJson.put("shareUrl", article.getUrlPad());
		inJson.put("tag", StringUtils.getNotNull(article.getTag()));
//		inJson.put("colName", article.getColumn());
		inJson.put("colID", article.getColumnID());
		inJson.put("source", StringUtils.getNotNull(article.getSource())); // 来源
		inJson.put("isRel", 0);





		inJson.put("multimediaLink", StringUtils.getNotNull(article.getMultimediaLink())); //多媒体链接
		inJson.put("mark", StringUtils.getNotNull(article.getMark())); //列表角标
		inJson.put("url", article.getUrl());
		inJson.put("urlPad", article.getUrlPad());
		inJson.put("liveStatus", article.getLiveStatus());

        //话题相关字段
        JSONObject topic = getTopicNameByID(article.getId(),1);
        inJson.put("topicName", topic.getString("topicName"));
        inJson.put("topicID", topic.getString("topicID"));
        inJson.put("topicColor", topic.getString("topicColor"));
		
		String url;
		int type = article.getType();
		if (type >= Article.TYPE_SPECIAL && type != Article.TYPE_ACTIVITY
				 && type != Article.TYPE_PANORAMA && type != Article.TYPE_FILE) {
			url = StringUtils.getNotNull(article.getUrlPad());
			inJson.put("isBigPic", 0);//原大图稿件标记。避免app旧版在直播稿、链接稿等时闪退
		} else {
			url = UrlHelper.getArticleContentUrl(article.getId());
			inJson.put("isBigPic", article.getBigPic());//大图稿件
		}
		inJson.put("bigPic", article.getBigPic());//大图稿件
		inJson.put("contentUrl", url);
		
		inJson.put("linkID", article.getLinkID());
		inJson.put("linkName", article.getLinkName()); 

		//初始阅读数，用于外网api从缓存redis读实际阅读数时相加
		//显示阅读数：数据库中的初始阅读数+实际阅读数+分享页的阅读数。在外网api里会替换成实时的阅读数，取自redis缓存
		int countClickInitial = article.getCountClickInitial();
		inJson.put("countClickInitial", countClickInitial);
		inJson.put("countClick", article.getCountClick() + countClickInitial + article.getCountShareClick());
		inJson.put("countDiscuss", article.getCountDiscuss());
		inJson.put("countPraise", article.getCountPraise());
		inJson.put("countShare", article.getCountShare());
		
		inJson.put("position", article.getPosition());
		inJson.put("trade", article.getTrade());
		inJson.put("tradeID", article.getTradeID());

		if(article.getType()==Article.TYPE_PIC){
			inJson.put("picContent",article.getContent());
		}else{
            inJson.put("picContent","");
        }

		if(article.getType() == 6)
			listArticleLiveField(inJson, article.getLinkID());
		else
			inJson.put("liveType", -1);

		return inJson;
	}

	/**
	 * 如果是直播稿件，需要添加直播类型
	 * @param inJson
	 * @param liveID
     * @throws E5Exception
	 */
	private static void listArticleLiveField(JSONObject inJson, int liveID){

		// 直播表
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			Document doc = docManager.get(LibHelper.getLive(), liveID);
			if (doc != null) {
				inJson.put("liveType", doc.getInt("a_type")); // 直播类型：0--图文；1--视频
			}else
				inJson.put("liveType", -1);
		} catch (E5Exception e) {
			e.printStackTrace();
		}

	}

	private static void listArticleAtts(JSONObject inJson, PubArticle article) {
		List<Attachment> pics = article.getAttPics();
		int length = pics != null ? pics.size() : 0;
		inJson.put("picCount", String.valueOf(length)); // 正文图片/组图图片数量，从附件表中查得
		
		if (length > 3) length = 3; // 列出前三个图片的url
		for (int i = 0; i < length; i++) {
			Attachment pic = pics.get(i);
			inJson.put("pic" + i, StringUtils.getNotNull(pic.getUrlPad())); // 组图稿时，列出前三个的url，外网地址
		}
		
		//其它附件：视频、标题图片
		List<Attachment> atts = article.getAttachments();
		if (atts != null) {
			for (Attachment att : atts) {
				int type = att.getType();
				String url = StringUtils.getNotNull(att.getUrlPad());
				if (1 == type){
					inJson.put("videoUrl", url); // 稿件对应视频的url，视频稿件时使用，附件表中查，外网地址
					inJson.put("duration", att.getDuration()); //视频附件时长，若多个视频附件会一直替换到最后一个
				}else if(2 == type){
					inJson.put("picBig", url); // big
				}else if(3 == type){
					inJson.put("picMiddle", url); // middle
				}else if(4 == type){
					inJson.put("picSmall", url); // small
				}
			}
		}
	}
}
