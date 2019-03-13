package com.founder.xy.jpublish.context;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocTypeField;
import com.founder.xy.article.Article;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.ArticleMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.AuthorInfo;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PageDir;
import com.founder.xy.jpublish.data.PageUrl;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.jpublish.data.Widgets;
import com.founder.xy.set.ExtField;
import com.founder.xy.set.ExtFieldReader;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

/**
 * 稿件发布的上下文环境
 * @author Gong Lijie
 */
public class ArticleContext extends AbstractContext{
	private ArticleMsg message;
	
	private PubArticle article;
	private Log log;
	private List<String> extFileFields; //扩展字段的定义，用于对其中的附件做发布
	
	private PageDir[] pageDirs = new PageDir[2];
	private PageUrl[] pageUrls = new PageUrl[2];
	
	private boolean samePicDir; //主版和触屏版的图片发布目录是否相同
	private boolean sameAttDir; //主版和触屏版的附件发布目录是否相同
	
	
	public int init(ArticleMsg message, Log log) {
		this.message = message;
		this.log = log;
		
		column = readColumn();
		
		article = readArticle();
		
		//设置稿件生成模板
		template = readTemplate(column, article);
		
		if (article != null){
			int artType = article.getType();

			//web稿要有模板，app稿可能无模板,链接稿等稿件也不需要检查模板

			if ((artType <= Article.TYPE_VIDEO || artType == Article.TYPE_ACTIVITY || artType == Article.TYPE_FILE || artType == Article.TYPE_PANORAMA || artType == Article.TYPE_SPECIAL)
					&& message.getChannel() == 1 && isEmpty(template)) {
				log.error("先配置模板才可发布");
				return PubArticle.ERROR_WEB_NO_TEMPLATE;
			}
			String format = InfoHelper.getConfig( "发布服务", "稿件日期目录格式")==null?"yyyyMM/dd":InfoHelper.getConfig( "发布服务", "稿件日期目录格式");
			String datePath = DateUtils.format(article.getPubTime(), format);
			//站点根目录
			String rootPath = getRootPath(column.getSiteID(), message.getDocLibID());
			
			//检查发布规则是否定义，并设置发布路径
			return checkAndSetPath(column, rootPath, datePath); //发布url、发布物理路径
		} else {
			log.error("没有读到稿件，无法发布");
			return PubArticle.ERROR_NO_DATA;
		}
	}
	public ArticleMsg getMessage() {
		return message;
	}

	public PubArticle getArticle() {
		return article;
	}

	public List<String> getExtFileFields() {
		return extFileFields;
	}
	public PageUrl getPageUrl(int index) {
		return pageUrls[index];
	}
	public PageDir getPageDir(int index) {
		return pageDirs[index];
	}
	
	/** 主版和触屏版的图片发布目录是否相同 */
	public boolean isSamePicDir() {
		return samePicDir;
	}
	/** 主版和触屏版的附件发布目录是否相同 */
	public boolean isSameAttDir() {
		return sameAttDir;
	}
	
	//读出主栏目
	private Column readColumn() {
		try {
			int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), message.getDocLibID());
			
			ColumnReader columnReader = (ColumnReader)Context.getBean("columnReader");
			return columnReader.get(colLibID, message.getColID());
		} catch (E5Exception e) {
			log.error("读栏目信息时出错：" + e.getLocalizedMessage(), e);
		}
		return null;
	}
	
	//读稿件模板（主版模板、触屏版模板）
	private Template[] readTemplate(Column column, PubArticle article) {
		//稿件的栏目中指定的模板。没有组图视频模板时都使用文章模板
		long tID0 = column.getTemplateArticle();
		long tID1 = column.getTemplateArticlePad();
		
		switch (message.getType()) {
		case 1://组图
			if (column.getTemplatePic() > 0) tID0 = column.getTemplatePic();
			if (column.getTemplatePicPad() > 0) tID1 = column.getTemplatePicPad();
			break;
		case 2://视频
			if (column.getTemplateVideo() > 0) tID0 = column.getTemplateVideo();
			if (column.getTemplateVideoPad() > 0) tID1 = column.getTemplateVideoPad();
			break;
		default:
			break;
		}
		
		//若稿件指定了模板，优先要用指定的模板
		if (article.getTemplateIDs()[0] > 0) tID0 = article.getTemplateIDs()[0];
		if (article.getTemplateIDs()[1] > 0) tID1 = article.getTemplateIDs()[1];
		
		//缓存中取出模板对象
		Template[] template = new Template[2];
		
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), message.getDocLibID());
		
		BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		template[0] = templateCache.getTemplateByID(libID, tID0);
		template[1] = templateCache.getTemplateByID(libID, tID1);
		
		return template;
	}

	//读出稿件的数据：稿件本身、挂件、相关稿件、扩展字段、附件
	private PubArticle readArticle() {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document articleDoc = null;
		try {
			articleDoc = docManager.get(message.getDocLibID(), message.getId());
			//发布之前，先修改发布时间，以免后面引用发布时间做日期目录时错误
			if (articleDoc != null  && articleDoc.getDeleteFlag() ==0) setPubTime(articleDoc);
			
		} catch (E5Exception e) {
			log.error("读稿件和已发布状态参数时异常：" + e.getLocalizedMessage(), e);
		}
		
		if (articleDoc != null) {
			PubArticle article = new PubArticle(articleDoc);
			//附件
			article.setAttachments(readAttachments(message.getDocLibID(), message.getId()));
			//挂件
			article.setWidgets(readWidgets(message.getDocLibID(), message.getId()));
			//相关稿件
			article.setRels(readRels(message.getDocLibID(), message.getId()));
			//扩展字段
			int extGroupID = article.getExtFieldGroupID();
			if (extGroupID <= 0 && column != null)
				extGroupID = column.getExtFieldGroupID();
			article.setExtFields(readExts(message.getDocLibID(), message.getId(), extGroupID));
			//作者信息（记者名片）
			article.setAuthorInfo(readAuthor(article));
			
			return article;
		} else {
			return null;
		}
	}
	
	//设置稿件的发布时间（第一次发布时）、实际发布时间
	private void setPubTime(Document doc) {
		//设置实发时间
		Timestamp lastPubTime = doc.getTimestamp("a_realPubTime");
		Timestamp thisPubTime = DateUtils.getTimestamp();
		doc.set("a_realPubTime", thisPubTime);
		
		//若还没有实发时间，证明是第一次发布，设置发布时间。撤稿重发时不改变发布时间
		if (lastPubTime == null)
			doc.set("a_pubTime", thisPubTime);
	}

	//读稿件的挂件
	private Widgets readWidgets(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int wLibID = LibHelper.getLibIDByOtherLib(DocTypes.WIDGET.typeID(), docLibID);

		try {
			Document[] ws = docManager.find(wLibID, "w_articleID=? and w_articleLibID=?", 
					new Object[]{docID, docLibID});
			if (ws == null || ws.length == 0)
				return new Widgets();
			else
				return new Widgets(ws);
		} catch (E5Exception e) {
			log.error("读稿件的挂件信息时异常：" + e.getLocalizedMessage(), e);
			return new Widgets();
		}
	}
	//读稿件的相关稿件
	private List<BareArticle> readRels(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int relLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEREL.typeID(), docLibID);

		List<BareArticle> result = new ArrayList<>();
		try {
			Document[] rels = docManager.find(relLibID, "a_articleID=? and a_articleLibID=?", 
					new Object[]{docID, docLibID});
			
			if (rels != null) {
				for (Document rel : rels) {
					BareArticle article = new BareArticle(rel);
					article.setDocLibID(rel.getInt("a_relLibID"));
					article.setId(rel.getLong("a_relID"));

					if(article.getType() == Article.TYPE_PIC){
						Document relArticle = docManager.get(article.getDocLibID(),article.getId());
						article.setContent(relArticle.getString("a_content"));
					}
					//从相关稿件的附件中取出标题图片的发布url
					List<Attachment> atts = readAttachments(article.getDocLibID(), article.getId());
					changePicTitleUrl(article, atts);
					
					result.add(article);
				}
			}
		} catch (E5Exception e) {
			log.error("读稿件的相关稿件信息时异常：" + e.getLocalizedMessage(), e);
		}
		return result;
	}
	//从附件中取出标题图片的发布地址，修改稿件中的标题图片变量
	private void changePicTitleUrl(BareArticle article, List<Attachment> atts) {
		for (Attachment att : atts) {
			if (att.getType() == com.founder.xy.article.Article.ATTACH_PICTITLE_BIG) {
				String url = (StringUtils.isBlank(att.getUrl()) 
						|| !att.getUrl().startsWith("http")) ? att.getUrlPad() : att.getUrl();
				article.setPicBig(url);
			} else if (att.getType() == com.founder.xy.article.Article.ATTACH_PICTITLE_MIDDLE) {
				String url = (StringUtils.isBlank(att.getUrl())
						|| !att.getUrl().startsWith("http")) ? att.getUrlPad() : att.getUrl();
				article.setPicMiddle(url);
			} else if (att.getType() == com.founder.xy.article.Article.ATTACH_PICTITLE_SMALL) {
				String url = (StringUtils.isBlank(att.getUrl())
						|| !att.getUrl().startsWith("http")) ? att.getUrlPad() : att.getUrl();
				article.setPicSmall(url);
			}
		}
	}
	//读稿件的扩展字段
	private HashMap<String, String> readExts(int docLibID, long docID, int extGroupID) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		if (extGroupID <= 0) return result;
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int wLibID = LibHelper.getLibIDByOtherLib(DocTypes.ARTICLEEXT.typeID(), docLibID);
		int extLibID = LibHelper.getLibIDByOtherLib(DocTypes.EXTFIELD.typeID(), docLibID);
		try {
			ExtFieldReader extReader = (ExtFieldReader)Context.getBean("extFieldReader");
			Set<ExtField> fields = extReader.getFields(extLibID, extGroupID);
			if (fields == null) return result;
			
			Document[] rels = docManager.find(wLibID, "ext_articleID=? and ext_articleLibID=?", 
					new Object[]{docID, docLibID});
			
			if (rels != null && rels.length > 0) {
				extFileFields = new ArrayList<>();
				for (Document rel : rels) {
					ExtField field = findName(fields, rel.getString("ext_code"));
					if (field != null) {
						result.put(field.getExt_name(), rel.getString("ext_value"));
						
						//收集扩展字段中的附件类型，以便发布
						if (field.getExt_editType() == DocTypeField.EDITTYPE_FILE) {
							extFileFields.add(field.getExt_name());
						}
					}
				}
			}
		} catch (E5Exception e) {
			log.error("读稿件的扩展字段信息时异常：" + e.getLocalizedMessage(), e);
		}
		return result;
	}
	//读稿件的正文附件
	private List<Attachment> readAttachments(int docLibID, long docID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), message.getDocLibID());
		Document[] attDocs = null;
		try {
			attDocs = docManager.find(attLibID, 
					"att_articleID=? and att_articleLibID=? order by att_order",
					new Object[]{docID, docLibID});
		} catch (E5Exception e) {
			log.error("读稿件的正文附件信息时异常：" + e.getLocalizedMessage(), e);
		}
		
		List<Attachment> attList = new ArrayList<Attachment>();
		for(Document attDoc : attDocs){
			attList.add(new Attachment(attDoc));
		}
		return attList;
	}
	//稿件详情中的记者名片
	private AuthorInfo readAuthor(PubArticle article) {
		//只读记者
		if (article.getAuthorID() <= 0 || article.getSourceType() != 1) return null;
		
		Document authorDoc = null;
		try {
			int userExtLibID = LibHelper.getLibIDByOtherLib(DocTypes.USEREXT.typeID(), article.getDocLibID());
			
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			authorDoc = docManager.get(userExtLibID, article.getAuthorID());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		
		if (authorDoc != null) {
			return new AuthorInfo(authorDoc.getDocID(), authorDoc.getString("u_name"),
					authorDoc.getString("u_iconUrl"), authorDoc.getString("u_duty"),
					authorDoc.getString("u_comment"));
		} else {
			return null;
		}
	}

	//按扩展字段code找到名称
	private ExtField findName(Set<ExtField> fields, String code) {
		for (ExtField extField : fields) {
			if (extField.getExt_code().equals(code))
				return extField;
		}
		return null;
	}

	//按栏目设置的发布规则，得到稿件、图片、附件的发布URL和发布地址
	private int checkAndSetPath(Column column, String rootPath, String datePath) {
		//按栏目读发布规则
		SiteRule[] pubRules = getPubRules(column);
		if (pubRules[0] == null && pubRules[1] == null) {
			log.error("没有设置发布规则，无法发布");
			return PubArticle.ERROR_NO_PUBRULE;
		}
		
		//组装出主版的url和dir（稿件、图片、附件）
		setPageDirUrl(pubRules, template, rootPath, datePath, pageDirs, pageUrls, 0);
		
		//组装出触屏版的url和dir（稿件、图片、附件）
		setPageDirUrl(pubRules, template, rootPath, datePath, pageDirs, pageUrls, 1);
		
		if (pageUrls[0] != null && pageUrls[1] != null) {
			//补充另一个版的url
			pageUrls[0].setAnotherUrl(pageUrls[1].getArticle());
			pageUrls[1].setAnotherUrl(pageUrls[0].getArticle());
			
			//若两版发布的图片和附件的位置相同，则只发布一次就可以了
			samePicDir = (pageDirs[0].getPic() != null 
					&& pageDirs[0].getPic().equals(pageDirs[1].getPic()));
			sameAttDir = (pageDirs[0].getAttachment() != null 
					&& pageDirs[0].getAttachment().equals(pageDirs[1].getAttachment()));
		}
		
		return PubArticle.SUCCESS;
	}
	//按栏目读发布规则
	private SiteRule[] getPubRules(Column column) {
		SiteRule[] pubRules = new SiteRule[2];
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), message.getDocLibID());

		BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		pubRules[0] = siteRuleCache.getSiteRuleByID(libID, column.getPubRule());
		pubRules[1] = siteRuleCache.getSiteRuleByID(libID, column.getPubRulePad());
		
		return pubRules;
	}
	
	private void setPageDirUrl(SiteRule[] pubRules, Template[] templates, String rootPath, String datePath, 
			PageDir[] pageDirs, PageUrl[] pageUrls, int index) {
		SiteRule pubRule = pubRules[index];
		if (pubRule != null) {
			//Web稿件URL、图片URL、附件URL
			String article = getArticleUrl(pubRule, datePath, templates[index]);
			String pic = getUrlWithDate(pubRule.getPhotoDir(), datePath);
			String att = getUrlWithDate(pubRule.getAttachDir(), datePath);
			
			pageUrls[index] = new PageUrl(article, pic, att);
			
			//Web稿件dir、图片dir、附件dir
			article = getArticleDir(rootPath, pubRule, datePath, templates[index]);
			pic = getUrlWithDate(rootPath, pubRule.getPhotoPath(), datePath);
			att = getUrlWithDate(rootPath, pubRule.getAttachPath(), datePath);
			
			String picRoot = rootPath + pubRule.getPhotoPath();//图片根路径，为抽图服务
			pageDirs[index] = new PageDir(article, pic, att, picRoot, rootPath);
		}
	}

	//根据发布规则拼出稿件发布URL
	private String getArticleUrl(SiteRule siteRule, String datePath, Template template){
		if (siteRule == null || template == null) return null;
		String prefix = InfoHelper.getConfig( "发布服务", "稿件生成页前缀");
		
		StringBuffer url = new StringBuffer();
		if(siteRule != null){
			url.append(siteRule.getArticleDir());
			if(siteRule.isArticleByDate()){
				url.append("/" + datePath);
			}
			if (prefix == null || "".equals(prefix)) {
				url.append("/c" + article.getId() + "." + getSuffix(template));				
			} else {
				//大洋网希望稿件文件名称为 稿件栏目ID_稿件ID，借鉴EL表达式进行替换
				if(prefix.contains("$"))
					prefix = parserPrefix(prefix);
				url.append("/" + prefix + article.getId() + "." + getSuffix(template));
			}

		}
		return url.toString();
	}



	private String getArticleDir(String siteWebRoot, SiteRule siteRule, String datePath, Template template){
		StringBuffer dir = new StringBuffer();
		
		dir.append(siteWebRoot);
		dir.append(siteRule.getArticlePath());
		
		String prefix = InfoHelper.getConfig( "发布服务", "稿件生成页前缀");
		if (siteRule.isArticleByDate()) {
			dir.append("/" + datePath);
		}
		if (prefix == null || "".equals(prefix)) {
			dir.append("/c" + article.getId() + "." + getSuffix(template));
		} else {
			//大洋网希望稿件文件名称为 稿件栏目ID_稿件ID，借鉴EL表达式进行替换
			if(prefix.contains("$"))
				prefix = parserPrefix(prefix);
			dir.append("/" + prefix + article.getId() + "." + getSuffix(template));
		}
		
		return dir.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String parserPrefix(String prefix) {
		Map map = new HashMap();
		//先把栏目ID和栏目名称放进去，以后看需求添加
		map.put("article.columnID", article.getColumnID());
		map.put("article.column", article.getColumn());

		String regex = "\\$\\{([^\\}]+)\\}";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(prefix);

		String g;
		while (m.find()) {
			g = m.group(1);
			if(!StringUtils.isBlank(g) && map.containsKey(g))
				prefix = m.replaceAll(map.get(g) + "");
			else prefix = m.replaceAll("");
			m = p.matcher(prefix);
		}
		return prefix;
	}

	private String getUrlWithDate(String siteWebRoot, String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return siteWebRoot + path + datePath;
	}
	
	private String getUrlWithDate(String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return path + datePath;
	}
	//判断是否一个模板也没有
	private boolean isEmpty(Template[] ts) {
		if (ts == null) return true;
		
		for (Template t : ts) {
			if (t != null) return false;
		}
		return true;
	}
}
