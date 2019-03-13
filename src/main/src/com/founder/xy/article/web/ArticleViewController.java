package com.founder.xy.article.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
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
import com.founder.e5.doc.FlowRecord;
import com.founder.e5.doc.FlowRecordManager;
import com.founder.e5.doc.FlowRecordManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.WebUtil;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.article.ArticleRel;
import com.founder.xy.article.Original;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.column.OriginalColumnManager;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.page.ArticleGenerator;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/xy/article")
public class ArticleViewController {

	@Autowired
	ArticleManager articleManager;
	@Autowired
	private ExtFieldReader extFieldReader;
	@Autowired
	private ColumnReader columnReader;
	@Autowired
	private OriginalColumnManager orgManager;

	/**
	 * 文章查看 -- 需要初始化
	 */
	@RequestMapping("View.do")
	public String initArticleView(Model model, int DocLibID, Long DocIDs, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		long groupID = WebUtil.getLong(request, "groupID", 0); // 源稿分类
		int siteID = WebUtil.getInt(request, "siteID", 1);
		// 从文章库当中查找文章相关的内容
		String tenantCode = LibHelper.getTenantCodeByLib(DocLibID);
		// 获取文档库
		List<DocLib> articleLibs = LibHelper.getLibs(DocTypes.ARTICLE.typeID(), tenantCode);

		int pcLibId = articleLibs.get(0).getDocLibID();// typeID为0→pc
		int appLibId = articleLibs.get(1).getDocLibID();// typeID为1→app
		int orignalLibId = LibHelper.getLibID(DocTypes.ORIGINAL.typeID(), tenantCode);

		Article orignal = null, pcArticle = null, appArticle = null;

		String libName = "";

		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			String bt_name = "";
			String bt_phone = "";
			String bt_qq = "";
			Document batManInfo = null;
			// 获取pc版的文章
			Document pcDoc = docManager.get(pcLibId, DocIDs);
			if (pcDoc != null) {
				pcArticle = new Article(pcDoc);
				batManInfo = getBatManDoc(docManager, pcDoc, pcLibId, DocIDs);
				// 获取稿件的status
				int _status = pcDoc.getInt("a_status");
				// 判断若status==2，将Url以及PadUrl都set进Article中
				if (_status == Article.STATUS_PUB_TIMED) {
				pcArticle=setArticleUrl(pcDoc, pcArticle, pcLibId);
				}
				}
			// 获取移动版的文章
			Document appDoc = docManager.get(appLibId, DocIDs);
			if (appDoc != null) {
				appArticle = new Article(appDoc);
				// 如果根据pc没找到，则再app稿件库中查找
				if (batManInfo == null) {
					batManInfo = getBatManDoc(docManager, appDoc, appLibId, DocIDs);
					// 获取稿件的status
					int _status = appDoc.getInt("a_status");
					// 判断若status==2，将Url以及PadUrl都set进Article中
					if (_status == Article.STATUS_PUB_TIMED) {
					appArticle=setArticleUrl(appDoc, appArticle, appLibId);
				}
				}
			}
			// 如果是原稿的话，就用这个id查三个库
			if (DocLibID == orignalLibId) {
				Document orignalDoc = docManager.get(orignalLibId, DocIDs);
				if (orignalDoc != null) {
					orignal = new Article(orignalDoc);
					libName = "orignal";
				}
				// 如果在上面web和app中还未找到，则在原稿库中查找
				if (batManInfo == null) {
					try {
						batManInfo = getBatManDoc(docManager, orignalDoc, pcLibId, DocIDs);
					} catch (Exception e) {
					}
				}

				if (orignalDoc.getInt("a_type") == Original.TYPE_WEIXIN) {// 如果是源稿库的微信稿
					Original weixin = new Original(orignalDoc);
					weixin.setContent(weixin.getContent().replaceAll("_ueditor_page_break_tag_", "<hr/>"));
					int colLibID = LibHelper.getLibID(DocTypes.COLUMNORI.typeID(), tenantCode);
					weixin.setCatName(orgManager.getCatName(weixin.getCatID(), colLibID));
					model.addAttribute("orignal", weixin);
					model.addAttribute("hasSensitive", InfoHelper.sensitiveInArticle());
					model.addAttribute("hasIllegal", InfoHelper.illegalInArticle());
					model.addAttribute("groupID", groupID);
					model.addAttribute("siteID", siteID);
					return "/xy/article/OriginalWXview";
				}

			}
			// 如果是 pc 或者 app就根据他们里面的originalid来取原稿
			else if (DocLibID == pcLibId) {
				if (pcArticle != null && pcArticle.getOriginalID() != 0) {
					Document orignalDoc = docManager.get(orignalLibId, pcArticle.getOriginalID());
					if (orignalDoc != null)
						orignal = new Article(orignalDoc);
				}
				libName = "pc";
			} else if (appArticle != null && DocLibID == appLibId) {
				if (appArticle.getOriginalID() != 0) {
					Document orignalDoc = docManager.get(orignalLibId, appArticle.getOriginalID());
					if (orignalDoc != null)
						orignal = new Article(orignalDoc);
				}
				libName = "app";
			}

			if (batManInfo != null) {
				bt_name = batManInfo.getString("BM_NAME");
				bt_phone = batManInfo.getString("BM_PHONE");
				bt_qq = batManInfo.getString("BM_QQ");
			}

			model.addAttribute("libName", libName);
			model.addAttribute("batManInfo", batManInfo);
			model.addAttribute("orignal", orignal);
			model.addAttribute("pcArticle", pcArticle);
			model.addAttribute("appArticle", appArticle);
			model.addAttribute("bt_name", bt_name);
			model.addAttribute("bt_phone", bt_phone);
			model.addAttribute("bt_qq", bt_qq);
			model.addAttribute("groupID", groupID);
			model.addAttribute("siteID", siteID);

			// 获取话题
			setTopics(pcArticle,1);
			setTopics(appArticle,2);
			// 获取扩展字段
			initExtField(model, DocIDs, pcLibId, appLibId, orignalLibId, orignal, pcArticle, appArticle);
			// 获取日志
			initOperationRecord(model, DocIDs, pcLibId, appLibId, orignalLibId);
			// 获得相关稿件
			initArticleRel(model, DocIDs, orignalLibId, "orignalRelSet");
			initArticleRel(model, DocIDs, pcLibId, "pcRelSet");
			initArticleRel(model, DocIDs, appLibId, "appRelSet");

			// 获得挂件 - 原图不需要挂件
			// initArticleWidget(model, orignalLibId, DocIDs,
			// "orignalWidgetSet");
			initArticleWidget(model, pcLibId, DocIDs, "pcWidgetSet");
			initArticleWidget(model, appLibId, DocIDs, "appWidgetSet");
			initPicSet(model, request, response, DocIDs, pcLibId, 1, "pcPicset");
			initPicSet(model, request, response, DocIDs, pcLibId, 2, "pcVideoset");
			initPicSet(model, request, response, DocIDs, appLibId, 1, "appPicset");
			initPicSet(model, request, response, DocIDs, appLibId, 2, "appVideoset");
			// 获得标题图
			initTitlePic(model, orignal, pcArticle, appArticle);

			// 判断是否是组图稿
			initImageArrays(model, DocIDs, orignalLibId, orignal, "orignalImageArray");
			initImageArrays(model, DocIDs, pcLibId, pcArticle, "pcImageArray");
			initImageArrays(model, DocIDs, appLibId, appArticle, "appImageArray");

			// 模版
			initTemplate(model, DocLibID, pcArticle, "pc", "pTempName", "pGroupName");
			initTemplate(model, DocLibID, pcArticle, "pad", "pTempPadName", "pGroupPadName");

			// 获得视频列表
			if (orignal != null && orignal.getType() == 2) {
				initVideoList(model, DocLibID, orignal, "orignalVideoList");
			}
			if (pcArticle != null && pcArticle.getType() == 2) {
				initVideoList(model, DocLibID, pcArticle, "pcVideoList");
			}
			if (appArticle != null && appArticle.getType() == 2) {
				initVideoList(model, DocLibID, appArticle, "appVideoList");
			}
			String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
			model.addAttribute("videoplugin", _videoPluginUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/xy/article/ArticleView";
	}
    
	/**
	 * 拼接定时发布稿件的发布地址
	 *
	 * @param Doc
	 *            稿件的文件对象
	 * @param DocLibID
	 *            稿件库ID
	 * @return article
	 *            稿件的实例对象
	 */
	private Article setArticleUrl(Document Doc,Article article,int DocLibID){
			// 获取稿件的column
			Column column = getArticleColumn(Doc, DocLibID);
			// 获取参数siteRule
			SiteRule[] pubRule = getSiteRule(DocLibID, column);
			// 获取参数datepath
			String datepath = getDatePath(Doc);
			// 获取参数template
			Template[] template = getTemplate(column, Doc, DocLibID);
			// 拼装稿件发布地址，并set进pcArticle
			article.setUrl(getArticleUrl(Doc, pubRule[0], datepath, template[0]));
			article.setUrlPad(getArticleUrl(Doc, pubRule[1], datepath, template[1]));
			return article;
	}
	
	
	/**
	 * 获取稿件的栏目
	 *
	 * @param pcDoc
	 *            稿件的文件对象
	 * @param DocLibID
	 *            稿件库ID
	 * @return column
	 */
	private Column getArticleColumn(Document pcDoc, int DocLibID) {
		int ColID = pcDoc.getInt("a_columnID");
		int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), DocLibID);
		ColumnReader columnReader = (ColumnReader) Context.getBean("columnReader");
		try {
			Column column = columnReader.get(colLibID, ColID);
			return column;
		} catch (E5Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取稿件的发布规则
	 *
	 * @param column
	 *            稿件栏目
	 * @param DocLibID
	 *            稿件库ID
	 * @return pubRules
	 */
	private SiteRule[] getSiteRule(int DocLibID, Column column) {
		SiteRule[] pubRules = new SiteRule[2];
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), DocLibID);
			BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
			pubRules[0] = siteRuleCache.getSiteRuleByID(libID, column.getPubRule());
			pubRules[1] = siteRuleCache.getSiteRuleByID(libID, column.getPubRulePad());
			if (pubRules[0] == null && pubRules[1] == null){
				return null;
			}else{
			return pubRules;
			}
	}
	/**
	 * 获取稿件的发布时间
	 *
	 * @param pcDoc
	 *            稿件的文件对象
	 * @return datePath
	 */
	private String getDatePath(Document pcDoc) {
		Timestamp time = pcDoc.getTimestamp("a_pubTime");
		if (time != null) {
			Date pubTime = (time != null) ? new Date(time.getTime()) : new Date();
			String format = InfoHelper.getConfig("发布服务", "稿件日期目录格式") == null ? "yyyyMM/dd"
					: InfoHelper.getConfig("发布服务", "稿件日期目录格式");
			String datePath = DateUtils.format(pubTime, format);
			return datePath;
		} else {
			return null;
		}
	}

	/**
	 * 获取稿件的模板
	 *
	 * @param column
	 *            稿件的栏目
	 * @param pcDoc
	 *            稿件的文件对象
	 * @param DocLibID
	 *            稿件库ID
	 * @return template
	 */
	private Template[] getTemplate(Column column, Document pcDoc, int DocLibID) {
		// 稿件的栏目中指定的模板。没有组图视频模板时都使用文章模板
		long tID0 = column.getTemplateArticle();
		long tID1 = column.getTemplateArticlePad();

		switch (pcDoc.getInt("a_type")) {
		case 1:// 组图
			if (column.getTemplatePic() > 0)
				tID0 = column.getTemplatePic();
			if (column.getTemplatePicPad() > 0)
				tID1 = column.getTemplatePicPad();
			break;
		case 2:// 视频
			if (column.getTemplateVideo() > 0)
				tID0 = column.getTemplateVideo();
			if (column.getTemplateVideoPad() > 0)
				tID1 = column.getTemplateVideoPad();
			break;
		default:
			break;
		}
		// 若稿件指定了模板，优先要用指定的模板
		if (pcDoc.getInt("a_templateID") > 0)
			tID0 = pcDoc.getInt("a_templateID");
		if (pcDoc.getInt("a_templatePadID") > 0)
			tID1 = pcDoc.getInt("a_templatePadID");
       if (tID0 == Long.MIN_VALUE || tID1 == Long.MIN_VALUE){
        	 return null;
         }else{
		// 缓存中取出模板对象
		Template[] template = new Template[2];
			int LibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), DocLibID);
			BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
			template[0] = templateCache.getTemplateByID(LibID, tID0);
			template[1] = templateCache.getTemplateByID(LibID, tID1);
			return template;
         }
}
	/**
	 * 拼接稿件详情页面中的稿件发布地址
	 *
	 * @param pcDoc
	 *            稿件的文件对象
	 * @param siteRule
	 *            稿件的发布规则，根据column取出
	 * @param datePath
	 *            稿件的发布日期路径
	 * @param template
	 *            稿件的模板，根据column取出
	 * @return url.toString()
	 */
	private String getArticleUrl(Document pcDoc, SiteRule siteRule, String datePath, Template template) {
		if (siteRule == null || template == null|| datePath == null)
			return null;
		String prefix = InfoHelper.getConfig("发布服务", "稿件生成页前缀");
		StringBuffer url = new StringBuffer();
			url.append(siteRule.getArticleDir());
			if (siteRule.isArticleByDate()) {
				url.append("/" + datePath);
			}
			if (prefix == null || "".equals(prefix)) {
				url.append("/c" + pcDoc.getDocID() + "." + getSuffix(template));
			} else {
				if (prefix.contains("$"))
					prefix = parserPrefix(pcDoc, prefix);
				url.append("/" + prefix + pcDoc.getDocID() + "." + getSuffix(template));
			}
		return url.toString();
	}

	/**
	 * 由模板确定的发布文件的后缀，html/json/xml
	 */
	protected String getSuffix(Template t) {
		if (t == null || StringUtils.isBlank(t.getFileType()))
			return "html";
		else
			return t.getFileType();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String parserPrefix(Document pcDoc, String prefix) {
		Map map = new HashMap();
		// 先把栏目ID和栏目名称放进去，以后看需求添加
		map.put("article.columnID", pcDoc.getInt("a_columnID"));
		map.put("article.column", pcDoc.getString("a_column"));

		String regex = "\\$\\{([^\\}]+)\\}";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(prefix);

		String g;
		while (m.find()) {
			g = m.group(1);
			if (!StringUtils.isBlank(g) && map.containsKey(g))
				prefix = m.replaceAll(map.get(g) + "");
			else
				prefix = m.replaceAll("");
			m = p.matcher(prefix);
		}
		return prefix;
	}

	/**
	 * 稿件阅读窗
	 */
	@RequestMapping("ReadMode.do")
	public String initArticleReadMode(Model model, int DocLibID, Long DocIDs, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Article article = null;
		try {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(DocLibID, DocIDs);
			if (doc != null) {
				article = new Article(doc);
			}
			model.addAttribute("article", article);

			// 判断是否是组图稿
			initImageArrays(model, DocIDs, DocLibID, article, "imageArray");

			// 获得视频列表
			if (article != null && article.getType() == 2) {
				initVideoList(model, DocLibID, article, "videoList");
			}

			String _videoPluginUrl = InfoHelper.getConfig("视频系统", "视频播放控件地址");
			model.addAttribute("videoplugin", _videoPluginUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/xy/article/ReadMode";
	}

	private void initVideoList(Model model, int DocLibID, Article article, String listName) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int libId = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), DocLibID);
		// int libId =LibHelper.getAttaLibID();
		Document[] docs = docManager.find(libId, "att_articleID=? and att_articleLibID=?",
				new Object[] { article.getDocID(), article.getDocLibID() });
		List<String> videoList = new ArrayList<>();
		for (Document doc : docs) {
			if (doc.getInt("att_type") == 1) {
				videoList.add(doc.getString("att_path"));
			}
		}
		model.addAttribute(listName, videoList);
	}

	/**
	 * 1） 根据a_columnID，用ColumnReader的get方法得到栏目对象Column 2）
	 * 读栏目对象的属性templateArticle和templateArticlePad得到网站版文章模板ID和触屏版文章模板ID 3）
	 * 优先使用稿件自设的模板a_templateID
	 * （网站版模板）和a_templatePadID（触屏版模板），为0时使用栏目对象的上述属性得到模板ID。 4）
	 * 根据模板ID，用DocumentManager读出模板对象
	 * ，得到模板名和所属分组ID（t_groupID），使用CatReader的getCat(CatTypes.
	 * CAT_TEMPLATE.typeID(), tGroupID)得到模板组，取其getCatName得到模板组名称。
	 */
	private void initTemplate(Model model, int DocLibID, Article article, String type, String templateTab,
			String groupTab) throws E5Exception {
		if (article != null) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			int templateLibID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), DocLibID);
			// 判断取用pc或者pad的模版id
			Long templateArticleId = "pc".equals(type) ? Long.valueOf(article.getTemplateID())
					: Long.valueOf(article.getTemplatePadID());

			// 1） 根据a_columnID，用ColumnReader的get方法得到栏目对象Column
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), DocLibID);
			Column column = columnReader.get(colLibID, article.getColumnID());
			if (column != null && templateArticleId <= 0) {
				// 2）
				// 读栏目对象的属性templateArticle和templateArticlePad得到网站版文章模板ID和触屏版文章模板ID
				templateArticleId = "pc".equals(type) ? column.getTemplateArticle() : column.getTemplateArticlePad();
			}
			if (templateArticleId > 0) {
				// 4）
				// 根据模板ID，用DocumentManager读出模板对象，得到模板名和所属分组ID（t_groupID），使用CatReader的getCat(CatTypes.
				// CAT_TEMPLATE.typeID(), tGroupID)得到模板组，取其getCatName得到模板组名称。
				Document tempdoc = docManager.get(templateLibID, templateArticleId);
				if (tempdoc != null) {
					int groupID = tempdoc.getInt("t_groupID");
					String templateName = tempdoc.getString("t_name");
					CatReader reader = (CatReader) Context.getBean("CatManager");
					Category category = reader.getCat(CatTypes.CAT_TEMPLATE.typeID(), groupID);
					String groupName = category.getCatName();
					model.addAttribute(templateTab, templateName + "  (" + templateArticleId + ")");
					model.addAttribute(groupTab, groupName);
				}
			}
		}
	}

	/**
	 * 初始化 组图模块
	 *
	 * @param model
	 * @param DocIDs
	 * @param docLibId
	 * @param article
	 * @param arrayName
	 * @throws E5Exception
	 */
	private void initImageArrays(Model model, Long DocIDs, int docLibId, Article article, String arrayName)
			throws E5Exception {
		if (article != null && article.getType() == 1) {
			Document[] docs = articleManager.getAttachments(docLibId, DocIDs);
			JSONObject element = null;
			JSONArray imageArray = null;

			if (docs != null && docs.length > 0) {
				imageArray = new JSONArray();
				String _path = "../image.do?path=";
				String type = "";
				for (Document doc : docs) {
					type = doc.getString("att_type");
					if ("0".equals(type)) {
						element = new JSONObject();
						element.put("href", _path + doc.getString("att_path"));
						element.put("alt", "");
						element.put("src", _path + doc.getString("att_path"));
						element.put("smallSrc", _path + doc.getString("att_path"));
						element.put("title", doc.getString("att_content") == null ? "" : doc.getString("att_content"));
						imageArray.add(element);
					}
				}
				model.addAttribute(arrayName, imageArray);
			}
		}
	}

	/**
	 * 初始化标题图模块
	 *
	 * @param model
	 * @param orignal
	 * @param pcArticle
	 * @param appArticle
	 */
	private void initTitlePic(Model model, Article orignal, Article pcArticle, Article appArticle) {
		// 获得标题图片
		if (orignal != null && orignal.getPicSmall() != null && !orignal.getPicSmall().isEmpty()) {
			model.addAttribute("smallTitlePic", "../../xy/image.do?path=" + orignal.getPicSmall());
		}
		if (orignal != null && orignal.getPicMiddle() != null && !orignal.getPicMiddle().isEmpty()) {
			model.addAttribute("midTitlePic", "../../xy/image.do?path=" + orignal.getPicMiddle());
		}
		if (orignal != null && orignal.getPicBig() != null && !orignal.getPicBig().isEmpty()) {
			model.addAttribute("bigTitlePic", "../../xy/image.do?path=" + orignal.getPicBig());
		}

		// 获得PC标题图片
		if (pcArticle != null && pcArticle.getPicSmall() != null && !pcArticle.getPicSmall().isEmpty()) {
			model.addAttribute("pcSmallTitlePic", "../../xy/image.do?path=" + pcArticle.getPicSmall());
		}
		if (pcArticle != null && pcArticle.getPicMiddle() != null && !pcArticle.getPicMiddle().isEmpty()) {
			model.addAttribute("pcMidTitlePic", "../../xy/image.do?path=" + pcArticle.getPicMiddle());
		}
		if (pcArticle != null && pcArticle.getPicBig() != null && !pcArticle.getPicBig().isEmpty()) {
			model.addAttribute("pcBigTitlePic", "../../xy/image.do?path=" + pcArticle.getPicBig());
		}

		// 获得app标题图片
		if (appArticle != null && appArticle.getPicSmall() != null && !appArticle.getPicSmall().isEmpty()) {
			model.addAttribute("appSmallTitlePic", "../../xy/image.do?path=" + appArticle.getPicSmall());
		}
		if (appArticle != null && appArticle.getPicMiddle() != null && !appArticle.getPicMiddle().isEmpty()) {
			model.addAttribute("appMidTitlePic", "../../xy/image.do?path=" + appArticle.getPicMiddle());
		}
		if (appArticle != null && appArticle.getPicBig() != null && !appArticle.getPicBig().isEmpty()) {
			model.addAttribute("appBigTitlePic", "../../xy/image.do?path=" + appArticle.getPicBig());
		}
	}

	/**
	 * 获得挂件
	 */
	private void initArticleWidget(Model model, Integer DocLibID, Long DocIDs, String setName) throws E5Exception {

		String sql = "SELECT w.w_type, w.w_objID, w.w_objLibID, w.w_path, w.w_content, "
				+ " a.att_content, a.att_path, a.att_indexed " + " FROM xy_articlewidget w "
				+ " LEFT JOIN xy_attachment a ON (w.w_objID=a.att_articleID AND w.w_objLibID=a.att_articleLibID  AND a.att_type NOT IN(2,3,4)) "
				+ " WHERE w.w_articleID=? AND w.w_articleLibID=? " + " ORDER BY w_type";
		DBSession conn = null;
		IResultSet rs = null;
		Set<JSONObject> widgetSet = new HashSet<>();
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, new Object[] { DocIDs, DocLibID });

			JSONObject json;
			Integer type;
			String index;
			/**
			 * 如果是组图（w_type=1）、视频（w_type=2），根据w_objID和w_objLibID，
			 * 到xy_attachment库中拿数据
			 */
			while (rs.next()) {
				json = new JSONObject();
				type = rs.getInt("w_type");
				index = rs.getString("att_indexed");
				json.accumulate("type", type);
				json.accumulate("index", index);
				if (type != 0) {
					String _path = StringUtils.getNotNull(rs.getString("att_path"));
					if (!"".equals(_path) && _path.indexOf(";") != -1) {
						_path = URLEncoder.encode(_path.substring(0, _path.indexOf(";")), "UTF-8")
								+ _path.substring(_path.indexOf(";"));
					}

					json.accumulate("path", _path);
					json.accumulate("content", rs.getString("att_content"));
				} else {
					String _path = StringUtils.getNotNull(rs.getString("w_path"));
					if (!"".equals(_path)) {
						_path = URLEncoder.encode(_path.substring(0, _path.indexOf(";")), "UTF-8")
								+ _path.substring(_path.indexOf(";"));
					}

					json.accumulate("path", _path);
					json.accumulate("content", rs.getString("w_content") == null ? "" : rs.getString("w_content"));
					widgetSet.add(json);
				}

			}
		} catch (SQLException e) {
			throw new E5Exception(e);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

		model.addAttribute(setName, widgetSet);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initPicSet(Model model, HttpServletRequest request, HttpServletResponse response, long docID,
			int docLibID, int type, String setName) throws Exception {
		Map json = new HashMap();
		// 获得基本信息
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String sql = "SELECT w.w_type, w.w_objID, w.w_objLibID, w.w_path, w.w_content, "
				+ " a.att_content, a.att_path, a.att_indexed, a.att_objID, a.att_objLibID, a.att_type "
				+ " FROM xy_articlewidget w "
				+ " LEFT JOIN xy_attachment a ON (w.w_objID=a.att_articleID AND w.w_objLibID=a.att_articleLibID  AND a.att_type NOT IN(2,3,4)) "
				+ " WHERE w.w_articleID=? AND w.w_articleLibID=? " + " ORDER BY w_type";
		DBSession conn = null;
		IResultSet rs = null;
		try {
			conn = Context.getDBSession();
			rs = conn.executeQuery(sql, new Object[] { docID, docLibID });

			Integer _type;
			/**
			 * 如果是组图（w_type=1）、视频（w_type=2），根据w_objID和w_objLibID，
			 * 到xy_attachment库中拿数据
			 */
			while (rs.next()) {
				_type = rs.getInt("w_type");
				json.put("type", _type);
				json.put("index", rs.getString("att_indexed"));
				if (_type == 1 && type == _type) {
					json.put("imgPath", rs.getString("att_path"));
					json.put("libId", rs.getString("w_objLibID"));
					json.put("docId", rs.getString("w_objID"));
					break;
				} else if (_type == 2 && type == _type) {
					json.put("libId", rs.getString("w_objLibID"));
					json.put("docId", rs.getString("w_objID"));
					Document videoDoc = docManager.get(rs.getInt("att_objLibID"), rs.getLong("att_objID"));
					json.put("imgPath", videoDoc.getString("v_picPath"));
					break;
				}
			}
			if (json.get("libId") != null && json.get("docId") != null) {
				Document doc = docManager.get(Integer.parseInt((String) json.get("libId")),
						Long.parseLong((String) json.get("docId")));
				json.put("topic", doc.getTopic());
				json.put("author", doc.getAuthors());
				json.put("createDate", DateUtils.format(doc.getCreated()));
			} else {
				json = null;
			}

		} catch (SQLException e) {
			throw new E5Exception(e);
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(conn);
		}

		model.addAttribute(setName, json);
	}

	/**
	 * 获得相关稿件
	 */
	private void initArticleRel(Model model, Long DocIDs, int libId, String setName) throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 相关联的稿件
		int articleRelLibId = (LibHelper.getArticleRelLibID());
		String sql = "a_articleID=? and a_articleLibID=?";
		Document[] orignalrelDocs = docManager.find(articleRelLibId, sql, new Object[] { DocIDs, libId });
		ArticleRel articleRel;
		Set<ArticleRel> relSet = new HashSet<>();
		for (Document doc : orignalrelDocs) {
			articleRel = new ArticleRel(doc);
			relSet.add(articleRel);
		}
		model.addAttribute(setName, relSet);
	}

	/**
	 * 获得日志
	 */
	private void initOperationRecord(Model model, Long DocIDs, int pcLibId, int appLibId, int orignalLibId)
			throws E5Exception {
		FlowRecordManager manager = FlowRecordManagerFactory.getInstance();
		FlowRecord[] orignalRecord = manager.getAssociatedFRs(orignalLibId, DocIDs, true);
		FlowRecord[] pcRecord = manager.getAssociatedFRs(pcLibId, DocIDs, true);
		FlowRecord[] appRecord = manager.getAssociatedFRs(appLibId, DocIDs, true);
		model.addAttribute("orignalRecord", orignalRecord == null || orignalRecord.length == 0 ? null : orignalRecord);
		model.addAttribute("pcRecord", pcRecord == null || pcRecord.length == 0 ? null : pcRecord);
		model.addAttribute("appRecord", appRecord == null || appRecord.length == 0 ? null : appRecord);
	}

	/**
	 * 初始化扩展字段
	 */
	private void initExtField(Model model, Long DocIDs, int pcLibId, int appLibId, int orignalLibId, Article orignal,
			Article pcArticle, Article appArticle) throws E5Exception, CloneNotSupportedException {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		// 查找扩展字段
		if (orignal != null && orignal.getExtFieldGroupID() != 0) {
			findExtFieldSet(DocIDs, orignalLibId, orignal, docManager, model, "orignalExSet");
		}

		if (pcArticle != null && pcArticle.getExtFieldGroupID() != 0) {
			findExtFieldSet(DocIDs, pcLibId, pcArticle, docManager, model, "pcExSet");
		}

		if (appArticle != null && appArticle.getExtFieldGroupID() != 0) {
			findExtFieldSet(DocIDs, appLibId, appArticle, docManager, model, "appExSet");
		}
	}

	/**
	 * 扩展字段
	 */
	private Set<ExtField> findExtFieldSet(Long DocIDs, int libId, Article article, DocumentManager docManager,
			Model model, String setName) throws E5Exception, CloneNotSupportedException {
		Set<ExtField> exSet;
		// 查看扩展字段关联表里面的值
		int extLibID = LibHelper.getArticleExtLibID();
		Document[] valueCols = docManager.find(extLibID, "ext_articleID=? and ext_articleLibID=?",
				new Object[] { DocIDs, libId });
		// 值的map
		Map<String, String> valueMap = new HashMap<>();
		if (valueCols != null && valueCols.length > 0) {
			valueMap = new HashMap<>();
			// 给值map赋值，以便于取
			for (Document col : valueCols) {
				valueMap.put(((String) col.get("EXT_CODE")), ((String) col.get("EXT_VALUE")));
			}
		}
		// 组织成
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.EXTFIELD.typeID(), libId);
		Set<ExtField> extFieldSet = extFieldReader.getFields(docLibID, article.getExtFieldGroupID());
		exSet = new HashSet<>();
		if (extFieldSet != null) {
			for (ExtField ee : extFieldSet) {
				ExtField e = (ExtField) ee.clone();
				e.setExt_value(e.getExt_code() == null ? "" : valueMap.get(e.getExt_code()));
				exSet.add(e);
				String extName = e.getExt_name();
				if (extName != null && extName.contains("目标爱心数")) {
					model.addAttribute("isCharity", true);
					model.addAttribute("charityDocId", DocIDs);
					model.addAttribute("charityLibId", libId);
				}
			}
		}
		model.addAttribute(setName, exSet);
		return exSet;
	}

	/**
	 * 稿件“查看”操作。 稿件已发布，则访问发布的网页；否则套用模板做预览（调用发布服务的预览） 可能同时有网站版和触屏版
	 *
	 * url:http://.../xy/article/Preview.do?DocLibID=1&DocIDs=1299&ch=1
	 * 原稿预览时加refCol参数
	 */
	@RequestMapping("Preview.do")
	public String articlePreview(HttpServletRequest request, Model model, int DocLibID, long DocIDs)
			throws E5Exception {
		// 审核稿件时预览没有ch参数 默认设置成0
		int ch = WebUtil.getInt(request, "ch", 0);
		getPreviewParam(request, model, DocLibID, DocIDs, ch);

		if (request.getParameter("app") != null) {
			JSONObject result = (JSONObject) model.asMap().get("result");
			return "redirect:" + result.getString("urlPad"); // 移动端预览
		} else {
			return "/xy/article/ArticlePreview";
		}
	}

	// "查看"操作的参数组织
	private void getPreviewParam(HttpServletRequest request, Model model, int DocLibID, long DocIDs, int ch)
			throws E5Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(DocLibID, DocIDs);

		String status = "success";
		JSONObject result = new JSONObject();

		if (doc != null) {
			getPreviewParamUrl(request, DocLibID, DocIDs, ch, doc, result);
		}

		model.addAttribute("result", result);
		model.addAttribute("status", status);
	}

	// 不同稿件类型的预览链接不同
	private void getPreviewParamUrl(HttpServletRequest request, int DocLibID, long DocIDs, long ch, Document doc,
			JSONObject result) {
		int _status = doc.getInt("a_status");
		int _type = doc.getInt("a_type");

		result.put("status", _status);
		result.put("atype", _type);

		switch (_type) {
		case Article.TYPE_SPECIAL: {// 专题稿：显示专题对应的栏目页
			if (doc.getInt("a_channel") == 1) {
				if (_status == 1) {
					// 网站专题
					result.put("url", doc.getString("a_url"));
					result.put("urlPad", doc.getString("a_urlPad"));
				} else {
					// 还不能预览
					String url = "../../xy/special/specialPreview.do?a_templateID=" + doc.getLong("a_templateID");
					result.put("url", url);
					result.put("urlPad", url);
				}
			} else {
				// App专题
				String url = "../../xy/article/viewColumn.do?colID=" + doc.getLong("a_linkID") + "&ch=" + ch;
				result.put("url", url);
				result.put("urlPad", url);
			}
			break;
		}

		case Article.TYPE_LIVE:
		case Article.TYPE_LINK:
		case Article.TYPE_H5:
		case Article.TYPE_AD: {// 直播稿/ H5/链接稿/广告稿：显示链接
			String url = (!StringUtils.isBlank(doc.getString("a_url"))) ? doc.getString("a_url")
					: doc.getString("a_urlPad");
			result.put("url", url);
			result.put("urlPad", url);
			break;
		}
		case Article.TYPE_MULTITITLE: {// 合成多标题稿，没有详情
			// String url = "about:blank";
			String url = "/xy/article/ArticleMultiTitleNotView.html";
			result.put("url", url);
			result.put("urlPad", url);
			break;
		}
		default:
			if (_status == 1) {// 已发布的，显示发布页
				String _url = doc.getString("a_url");
				String _urlPad = doc.getString("a_urlPad");

				if (!StringUtils.isBlank(_url))
					result.put("url", _url);
				if (!StringUtils.isBlank(_urlPad))
					result.put("urlPad", _urlPad);

			} else { // 未发布，套模板预览

				/*
				 * xy/article/View.do?DocLibID=1&DocIDs=186492
				 * &FVID=1&UUID=1532941008674&siteID=1&colID=8&ch=0
				 */

				String param = "&DocLibID=" + DocLibID + "&DocIDs=" + DocIDs;
				int _colID = WebUtil.getInt(request, "refCol", 0); // 原稿预览时会指定参考栏目
				if (_colID > 0) {
					param += "&refCol=" + _colID;
				}
				result.put("url", "../../xy/article/previewHtml.do?type=0" + param);
				result.put("urlPad", "../../xy/article/previewHtml.do?type=1" + param);
				// 生成触屏分享地址，前台生成对应的二维码
				String webUrl = InfoHelper.getConfig("互动", "外网资源地址");
				result.put("urlTouch",
						webUrl + "pad/index.html#/detail/" + doc.getDocID() + "?site" + doc.getString("a_siteID"));
			}
		}
	}

	/**
	 * 未发布稿件预览（包括原稿指定栏目后的预览）
	 * <p/>
	 * 检查a_status字段如下： 1. a_status=1（已发布）： 1.1 若a_url非空，则显示网页效果；1.2
	 * 若a_urlPad非空，显示触屏效果。 2. a_status<>1：调用ArticleGenerator的preview方法得到网页内容数组。
	 * 该数组包含两个元素，1是网站html，2是触屏html，选择使用。 3.
	 * 预览时可能因为配置不到位等原因无法预览，这时会发出E5Exception异常，需从异常中取出错误描述，展示给用户。
	 */
	@RequestMapping("previewHtml.do")
	public String previewHtml(HttpServletRequest request, Model model, Integer DocLibID, Long DocIDs, int type)
			throws E5Exception {
		// 获得文章的实例
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(DocLibID, DocIDs);

		JSONObject result = new JSONObject();
		String status = "success";
		if (doc != null) {
			ArticleMsg articleMsg = getArticleMsg(doc, request, type);

			// 调用ArticleGenerator的preview方法得到网页内容数组。该数组包含两个元素
			try {
				ArticleGenerator articleGenerator = new ArticleGenerator();
				String[] articlePreviews = articleGenerator.preview(articleMsg);

				if (articlePreviews != null && articlePreviews.length > 1) {
					// 1是网站html，2是触屏html，选择使用。
					result.put("html", articlePreviews[type]);

					boolean hasHtml = articlePreviews[0] != null && !"".equals(articlePreviews[0]);
					result.put("hasHtml", hasHtml);
					hasHtml = articlePreviews[1] != null && !"".equals(articlePreviews[1]);
					result.put("hasHtmlPad", hasHtml);
				}
				result.put("htmlStatus", doc.getInt("a_status"));
			} catch (E5Exception e) {
				// 获得异常信息
				status = "failure";
				model.addAttribute("html", e.getMessage());
			}
		}
		model.addAttribute("result", result);
		model.addAttribute("status", status);

		return "/xy/article/previewhtml";
	}

	private ArticleMsg getArticleMsg(Document doc, HttpServletRequest request, int channelType) {
		int articleType = doc.getInt("a_type"); // 稿件类型

		// 若是原稿预览（在签发操作选择了栏目后），则传入待签发的栏目ID，供预览
		int _colID = WebUtil.getInt(request, "refCol", 0);
		if (_colID <= 0) {
			// 否则是发布库稿件预览
			_colID = doc.getInt("a_columnID");
			return new ArticleMsg(doc.getDocLibID(), doc.getDocID(), _colID, doc.getString("a_columnAll"), articleType,
					doc.getInt("a_channel"));
		} else {
			int channel = (int) Math.pow(2, channelType);
			return new ArticleMsg(doc.getDocLibID(), doc.getDocID(), _colID, String.valueOf(_colID), articleType,
					channel);
		}
	}

	// 查找通讯员Document
	private Document getBatManDoc(DocumentManager documentManager, Document doc, int libId, long Docid)
			throws E5Exception {

		long sys_authorId = Long.valueOf(doc.getString("SYS_AUTHORID"));
		try {
			int colLibID = LibHelper.getLibID(DocTypes.BATMAN.typeID(), Tenant.DEFAULTCODE);
			Document batManinfo = documentManager.get(colLibID, sys_authorId);
			if (batManinfo != null)
				return batManinfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//设置稿件的话题
	private void setTopics(Article article,int channel) {
		if(article == null){
			return;
		}

		String SQL =  "select a_topicID, a_topicName from xy_topicrelart where a_articleID=? and a_channel=?";
		Object[] params = new Object[]{article.getDocID(),channel};
		DBSession db = null;
		IResultSet rs = null;
		String topicName = "" ;
		try {
			db = InfoHelper.getDBSession(article.getDocLibID());
			rs = db.executeQuery(SQL, params);
			while (rs.next()) {
				topicName += rs.getString("a_topicName");
				topicName += " ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(rs);
			ResourceMgr.closeQuietly(db);
		}
		article.setTopics(topicName.trim());
	}
}
