package com.founder.xy.jpublish.magazine;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.article.Article;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

/**
 * 期刊发布的上下文环境，准备刊期和栏目的稿件列表、附件的数据。
 * 
 * @author Gong Lijie
 */
public class MagazineContext {
	private DocIDMsg message;
	private Log log;
	
	private Magazine magazine;
	private List<MagazineColumn> columns;
	
	private Template templateLayout;
	private Template templateArticle;
	private Template templateLayoutPad;
	private Template templateArticlePad;
	
	Map<String,String> pageUrlMap = new HashMap<String,String>();
	Map<String,String> pageDirMap = new HashMap<String,String>();
	
	/**
	 * 初始化
	 * @param message 期刊库ID、期刊ID，relIDs用来表示刊期yyyyMMdd
	 * @param log
	 */
	public int init(DocIDMsg message, Log log) {
		this.message = message;
		this.log = log;
		
		//读期刊，得到发布规则、模板
		readMagazine(message);
		if (templateLack()) return PubArticle.ERROR_WEB_NO_TEMPLATE;
		
		Timestamp pubDate = new Timestamp(DateUtils.parse(message.getRelIDs(), "yyyyMMdd").getTime());
		magazine.setDate(pubDate);
		readMagazineAtts(magazine); //读刊期的封面图
		
		//读稿件和附件。
		columns = readColumnArticles(message);
		if (columns == null || columns.isEmpty()) return PubArticle.ERROR_NO_DATA;
		
		//检查发布规则是否定义，并设置发布路径
		String rootPath = getRootPath(magazine.getSiteID(), magazine.getDocLibID());//站点根目录
		return checkAndSetPath(rootPath, message.getRelIDs()); //发布url、发布物理路径
	}
	
	public DocIDMsg getMessage() {
		return message;
	}

	public Magazine getMagazine() {
		return magazine;
	}

	public List<MagazineColumn> getColumns() {
		return columns;
	}

	public Template getTemplateLayout() {
		return templateLayout;
	}

	public Template getTemplateArticle() {
		return templateArticle;
	}

	public Template getTemplateLayoutPad() {
		return templateLayoutPad;
	}

	public Template getTemplateArticlePad() {
		return templateArticlePad;
	}

	public String getPageUrl(String key) {
		return pageUrlMap.get(key);
	}

	public String getPageDir(String key) {
		return pageDirMap.get(key);
	}

	//判断是否模板设置不完整
	public boolean templateLack() {
		return (templateArticle == null && templateArticlePad == null);
	}

	//读稿件模板（网站版模板、触屏版模板）
	private void readMagazine(DocIDMsg message) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document paperDoc = null;
		try {
			paperDoc = docManager.get(message.getDocLibID(), message.getDocID());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		
		if (paperDoc != null) {
			magazine = new Magazine(paperDoc);
			
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), message.getDocLibID());
			
			BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
			templateLayout = templateCache.getTemplateByID(libID, magazine.getTemplate());
			templateArticle = templateCache.getTemplateByID(libID, magazine.getTemplateArticle());
			templateLayoutPad = templateCache.getTemplateByID(libID, magazine.getTemplatePad());
			templateArticlePad = templateCache.getTemplateByID(libID, magazine.getTemplateArticlePad());
		}
	}
	
	/**
	 * 读某刊期的所有稿件，按栏目进行分组
	 */
	private List<MagazineColumn> readColumnArticles(DocIDMsg message) {
		//读期刊稿件，按栏目排序
		int articleLibID = LibHelper.getLibIDByOtherLib(DocTypes.MAGAZINEARTICLE.typeID(), message.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] layoutDocs = null;
		try {
			//标记为撤稿的稿件不做发布
			layoutDocs = docManager.find(articleLibID, 
					"a_pubTime=? and a_magazineID=? and a_status<7 order by a_columnID", 
					new Object[]{magazine.getDate(), magazine.getId()});
		} catch (Exception e) {
			log.error("读期刊稿件异常：" + e.getLocalizedMessage(), e);
		}
		
		MagazineColumn column = new MagazineColumn("默认");
		long columnID = 0;
		
		List<MagazineColumn> columns = new ArrayList<>();
		for (Document doc : layoutDocs) {
			long newColumnID = doc.getLong("a_columnID");
			if (newColumnID != columnID) {
				columnID = newColumnID;
				
				column = new MagazineColumn(doc.getString("a_column"));
				columns.add(column);
			}
			MagazineArticle article = new MagazineArticle(doc);
			article.setAttachments(readAttachments(doc.getDocLibID(), doc.getDocID()));
			article.setColumns(columns); //稿件里引用全部的栏目稿件列表，以便在期刊稿件页上做导航
			
			column.add(article);
		}
		return columns;
	}

	//读附件
	private List<Attachment> readAttachments(int docLibID, long docID) {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), docLibID);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attDocs = null;
		try {
			attDocs = docManager.find(attLibID, 
					"att_articleID=? and att_articleLibID=? order by att_order",
					new Object[]{docID, docLibID});
		} catch (E5Exception e) {
			log.error("读附件异常：" + e.getLocalizedMessage(), e);
		}
		
		List<Attachment> attList = new ArrayList<Attachment>();
		for(Document attDoc : attDocs){
			attList.add(new Attachment(attDoc));
		}
		return attList;
	}

	//读封面图和Pdf
	private void readMagazineAtts(Magazine magazine) {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.ATTACHMENT.typeID(), magazine.getDocLibID());
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] attDocs = null;
		try {
			attDocs = docManager.find(attLibID, 
					"att_articleID=? and att_articleLibID=? and att_objID=?",
					new Object[]{message.getDocID(), message.getDocLibID(), Integer.parseInt(message.getRelIDs())});
		} catch (E5Exception e) {
			log.error("读封面图附件时异常：" + e.getLocalizedMessage(), e);
		}
		
		List<Attachment> attList = new ArrayList<Attachment>();
		for(Document attDoc : attDocs){
			Attachment att = new Attachment(attDoc);
			attList.add(att);
			
			//设置封面图
			if (att.getType() == Article.ATTACH_LAYOUT_PIC)
				magazine.setPic(att);
		}
		magazine.setAttachments(attList);
	}

	//按发布规则，得到稿件、图片、附件的发布URL和发布地址
	private int checkAndSetPath(String rootPath, String paperDate) {
		//取发布规则
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), magazine.getDocLibID());
		BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		SiteRule pubRule = siteRuleCache.getSiteRuleByID(libID, magazine.getPubRule());
		SiteRule pubRulePad = siteRuleCache.getSiteRuleByID(libID, magazine.getPubRulePad());
		
		if (pubRule == null && pubRulePad == null) {
			log.error("没有设置发布规则，无法发布");
			return PubArticle.ERROR_NO_PUBRULE;
		}
		
		pageDirMap.put("root", rootPath);
		
		//从20160101改为201601/01
		String datePath = paperDate.substring(0, 6) + "/" + paperDate.substring(6);
		
		if (pubRule != null) { //Web版url和dir
			pageUrlMap.put("article", getArticleUrl(pubRule, datePath));
			pageUrlMap.put("pic", getUrlWithDate(pubRule.getPhotoDir(), datePath));
			pageUrlMap.put("att", getUrlWithDate(pubRule.getAttachDir(), datePath));
			pageUrlMap.put("column", getColumnUrl(pubRule, datePath));
			
			pageDirMap.put("column", getColumnDir(rootPath, pubRule));
			pageDirMap.put("article", getArticleDir(rootPath, pubRule, datePath));
			pageDirMap.put("pic", getUrlWithDate(rootPath, pubRule.getPhotoPath(), datePath));
			pageDirMap.put("att", getUrlWithDate(rootPath, pubRule.getAttachPath(), datePath));
			
			pageDirMap.put("picRoot", rootPath + pubRule.getPhotoPath()); //图片根路径，为抽图服务
		}
		if (pubRulePad != null) {//触屏版url和dir
			pageUrlMap.put("articlePad", getArticleUrl(pubRulePad, datePath));
			pageUrlMap.put("picPad", getUrlWithDate(pubRulePad.getPhotoDir(), datePath));
			pageUrlMap.put("attPad", getUrlWithDate(pubRulePad.getAttachDir(), datePath));
			pageUrlMap.put("columnPad", getColumnUrl(pubRulePad, datePath));

			pageDirMap.put("columnPad", getColumnDir(rootPath, pubRulePad));
			pageDirMap.put("articlePad", getArticleDir(rootPath, pubRulePad, datePath));
			pageDirMap.put("picPad", getUrlWithDate(rootPath, pubRulePad.getPhotoPath(), datePath));
			pageDirMap.put("attPad", getUrlWithDate(rootPath, pubRulePad.getAttachPath(), datePath));
			
			pageDirMap.put("picRootPad", rootPath + pubRulePad.getPhotoPath()); //图片根路径，为抽图服务
		}
		
		return PubArticle.SUCCESS;
	}

	//根据发布规则拼出稿件发布URL
	private String getArticleUrl(SiteRule siteRule, String datePath){
		StringBuffer url = new StringBuffer();
		if(siteRule != null){
			url.append(siteRule.getArticleDir());
			if(siteRule.isArticleByDate()){
				url.append("/" + datePath);
			}
		}
		return url.toString();
	}
	private String getArticleDir(String siteWebRoot, SiteRule siteRule, String datePath){
		StringBuffer dir = new StringBuffer();
		
		dir.append(siteWebRoot);
		dir.append(siteRule.getArticlePath());
		
		if (siteRule.isArticleByDate()) {
			dir.append("/" + datePath);
		}
		return dir.toString();
	}

	private String getUrlWithDate(String siteWebRoot, String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return siteWebRoot + path + datePath;
	}
	
	private String getUrlWithDate(String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return path + datePath;
	}
	private String getColumnDir(String siteWebRoot, SiteRule siteRule){
		StringBuffer dir = new StringBuffer();
		if (siteRule != null) {
			dir.append(siteWebRoot + siteRule.getColumnPath());
			
			if (siteRule.isColumnByDate()) {
				String datePath = DateUtils.format("yyyyMM/dd");
				dir.append("/" + datePath);
			}
		}
		return dir.toString();
	}
	//根据发布规则拼出稿件发布URL
	private String getColumnUrl(SiteRule siteRule, String datePath){
		StringBuffer url = new StringBuffer();
		
		if (siteRule != null){
			url.append(siteRule.getColumnDir());
			if(siteRule.isColumnByDate()){
				url.append("/" + datePath);
			}
		}
		return url.toString();
	}
	
	private String getRootPath(int siteID, int refLibID) {
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), refLibID);
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		return siteCache.getSiteWebRootByID(siteLibID, siteID);
	}
}
