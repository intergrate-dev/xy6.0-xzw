package com.founder.xy.api.nis;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动Json。 从数据库读出数据，组织成列表api可用的json、详情api可用的json
 * 
 * @author Gong Lijie
 */
public class ActivityJsonHelper {
	/**
	 * 组织一篇稿件的json
	 */
	public static JSONObject article(int docLibID, long docID)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);

		return article(doc);
	}

	public static JSONObject article(Document doc) throws E5Exception {
		if (doc == null) return new JSONObject();

		JSONObject redisJson = new JSONObject();

		// 设置基础属性
		setBasicField(redisJson, doc);

		// 处理内容
		setContent(redisJson, doc);

		return redisJson;
	}

	/**
	 * 设置基本属性
	 */
	private static void setBasicField(JSONObject redisJson, Document doc) {
		redisJson.put("fileId",
				StringUtils.getNotNull(doc.getString("SYS_DOCUMENTID"))); // 稿件ID
		redisJson.put("version", doc.getTimestamp("SYS_LASTMODIFIED").getTime()); // 稿件最后修改时间
		redisJson.put("title",
				StringUtils.getNotNull(doc.getString("SYS_TOPIC"))); // 稿件标题
		redisJson.put("attAbstract", // 旧版本是用content
				StringUtils.getNotNull(doc.getString("a_abstract")));
		redisJson.put("startTime",InfoHelper.formatDate(doc.getTimestamp("a_startTime")));
		redisJson.put("endTime",InfoHelper.formatDate(doc.getTimestamp("a_endTime"))) ;
		redisJson.put("publishtime", InfoHelper.formatDate(doc.getCreated())); // 发布时间
		redisJson.put("author",
				StringUtils.getNotNull(doc.getString("SYS_AUTHORS"))); // 作者
		redisJson.put("discussClosed", doc.getInt("a_discussClosed"));
		redisJson.put("statusEntry", doc.getInt("a_statusEntry")); //是否已公布名单
		
		redisJson.put("position",doc.getString("a_location"));
		redisJson.put("organizer",doc.getString("a_organizer"));  //主办方
		redisJson.put("participatorNum",doc.getInt("a_count"));
		redisJson.put("targetNum",doc.getString("a_countLimited"));
		redisJson.put("picBig", "");
		redisJson.put("picMiddle", "");
		redisJson.put("picSmall", "");
		redisJson.put("expenses",doc.getString("a_expenses"));
		
		redisJson.put("attachments", ""); //旧版使用的附件
	}

	// 处理内容
	private static void setContent(JSONObject redisJson, Document doc)
			throws E5Exception {
		// 读出附件表的附件
		Document[] atts = readAtts(doc);

		String content = doc.getString("a_content");

		content = getArticleContent(redisJson, content, atts);

		redisJson.put("content", content); // 旧版本里是otherInfo
	}

	private static Document[] readAtts(Document doc) throws E5Exception {
		int attTypeID = DocTypes.NISATTACHMENT.typeID();
		int attDocLibID = LibHelper.getLibIDByOtherLib(attTypeID,
				doc.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] atts = docManager.find(attDocLibID,
				"att_articleID=? and att_articleLibID=? ORDER BY SYS_DOCUMENTID",
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

	// 替换稿件内容中的图片部分
	private static void replaceAttPics(JSONObject redisJson, Document[] atts,
			org.jsoup.nodes.Document html) {
		Document[] docs = getAttsByType(atts, 1);
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

			String title = img.attr("title");

			String IMAGEARRAY = "<!--IMAGEARRAY#" + i + "-->";
			img.after(IMAGEARRAY);
			img.remove();

			JSONObject inJson = new JSONObject();
			inJson.put("ref", IMAGEARRAY);
			inJson.put("picType", 1); // 图片类型，附件图或者插图,1代表插图，0代表附件图
			inJson.put("summary", StringUtils.getNotNull(title));
			inJson.put("imageUrl",
					StringUtils.getNotNull(docs[i].getString("att_url"))); // 外网地址

			inJsonArr.add(inJson);
			i++;
		}
		if (i > 0)
			attPicArray(redisJson, inJsonArr);
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
		Document[] docs = getAttsByType(atts, 2);
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
					StringUtils.getNotNull(docs[i].getString("att_url"))); // 外网地址
			inJson.put("imageUrl",
					StringUtils.getNotNull(docs[i].getString("att_picUrlPad"))); // 关键帧外网地址
			inJson.put("duration", docs[i].getInt("att_duration")); //时长

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
		Document pic = getAttByType(atts, 3); // 标题图片-大
		if (pic != null) {
			imgUrl = pic.getString("att_url");
			redisJson.put("picBig", imgUrl);
			
			imgUrl +=  ".0";
		} else {
			pic = getAttByType(atts, 4); // 标题图片-中
			if (pic != null) {
				imgUrl = pic.getString("att_url");
				redisJson.put("picMiddle", imgUrl); 
			} else {
				pic = getAttByType(atts, 5); // 标题图片-小
				if (pic != null) {
					imgUrl = pic.getString("att_url");
					redisJson.put("picSmall", imgUrl);
				} else {
					if(atts.length>0){
						imgUrl = atts[0].getString("att_url");
					}else{
						imgUrl ="";
					}
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
}
