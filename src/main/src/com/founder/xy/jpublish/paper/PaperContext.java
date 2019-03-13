package com.founder.xy.jpublish.paper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Log;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.CacheReader;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.BaseDataCache;
import com.founder.xy.jpublish.data.Attachment;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.system.site.SiteRule;
import com.founder.xy.template.Template;

/**
 * 数字报发布的上下文环境，准备版面和其下小样稿件、附件的数据。
 * 包括两种场景：
 * 1）入库服务，发布某一刊期的所有版面
 * 2）单版重发，发布某一个版面
 * 
 * @author Gong Lijie
 */
public class PaperContext {
	private DocIDMsg message;
	private Log log;
	
	private Paper paper;
	private Template templateLayout;
	private Template templateArticle;
	private Template templateLayoutPad;
	private Template templateArticlePad;
	
	private Template templateHome;
	private Template templateHomePad; //首页模板
	
	private List<PaperLayout> layouts = new ArrayList<>();
	private List<PaperPile> piles = new ArrayList<>(); //叠
	
	Map<String,String> pageUrlMap = new HashMap<String,String>();
	Map<String,String> pageDirMap = new HashMap<String,String>();
	
	/**
	 * 初始化
	 * @param message 
	 *          按刊期发布时，表示报纸库ID、报纸ID，relIDs用来表示刊期yyyyMMdd
	 *          单版发布时，表示版面库ID、版面ID，relIDs为null
	 * @param log
	 */
	public int init(DocIDMsg message, Log log) {
		this.message = message;
		this.log = log;
		
		String paperDate = null;
		if (message.getRelIDs() == null) {
			//若是单版发布，则读出当前版、设置它的前后版、读稿件和附件。
			PaperLayout layout = readLayout(message.getDocLibID(), message.getDocID());
			if (layout == null) {
				log.error("没有找到版面数据");
				return PubArticle.ERROR_NO_DATA;
			}
			
			//读报纸，得到发布规则、模板
			int paperID = layout.getPaperID();
			int paperLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPER.typeID(), message.getDocLibID());
			readPaper(paperLibID, paperID);
			if (templateLack()) {
				log.error("先配置模板才可发布");
				return PubArticle.ERROR_WEB_NO_TEMPLATE;
			}
			
			//单版发布时，layouts里只有一个layout
			layouts.add(layout);
			setLayoutSiblings(layout, layout.getDate());
			
			paperDate = DateUtils.format(layout.getDate(), "yyyyMMdd");
		} else {
			readPaper(message.getDocLibID(), message.getDocID());
			if (templateLack()) {
				log.error("先配置模板才可发布");
				return PubArticle.ERROR_WEB_NO_TEMPLATE;
			}
			
			paperDate = message.getRelIDs();
			readLayouts(layouts, DateUtils.parse(message.getRelIDs(), "yyyyMMdd"), false);
		}
		//初始化叠信息
		initPiles();
		
		//检查发布规则是否定义，并设置发布路径
		String rootPath = getRootPath(paper.getSiteID(), paper.getDocLibID());//站点根目录
		return checkAndSetPath(rootPath, paperDate); //发布url、发布物理路径
	}
	
	public DocIDMsg getMessage() {
		return message;
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

	public Template getTemplateHome() {
		return templateHome;
	}

	public Template getTemplateHomePad() {
		return templateHomePad;
	}

	public List<PaperLayout> getLayouts() {
		return layouts;
	}
	
	public List<PaperPile> getPiles() {
		return piles;
	}

	public String getPageUrl(String key) {
		return pageUrlMap.get(key);
	}

	public String getPageDir(String key) {
		return pageDirMap.get(key);
	}

	public Paper getPaper() {
		return paper;
	}

	//判断是否模板设置不完整
	public boolean templateLack() {
		return (templateArticle == null && templateArticlePad == null);
	}

	//读稿件模板（网站版模板、触屏版模板）
	private void readPaper(int paperLibID, long paperID) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document paperDoc = null;
		try {
			paperDoc = docManager.get(paperLibID, paperID);
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		
		if (paperDoc != null) {
			paper = new Paper(paperDoc);
			
			int libID = LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(), paperLibID);
			
			BaseDataCache templateCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
			templateLayout = templateCache.getTemplateByID(libID, paper.getTemplate());
			templateArticle = templateCache.getTemplateByID(libID, paper.getTemplateArticle());
			templateLayoutPad = templateCache.getTemplateByID(libID, paper.getTemplatePad());
			templateArticlePad = templateCache.getTemplateByID(libID, paper.getTemplateArticlePad());
			
			templateHome = templateCache.getTemplateByID(libID, paper.getTemplateHome());
			templateHomePad = templateCache.getTemplateByID(libID, paper.getTemplateHomePad());
		}
	}
	
	/**
	 * 读某刊期的所有版面
	 * @param layouts 数据存放列表
	 * @param paperDate 见报日期
	 * @param isOne  是否单版发布的场景（重发某版面时，不需要对其它版读稿件、附件等）
	 */
	 void readLayouts(List<PaperLayout> layouts, Date paperDate, boolean isOne) {
		//参数控制是否按pl_order字段做版面排序（个别客户要求）
		boolean byOrder = "是".equals(InfoHelper.getConfig("数字报", "版面排序"));
		String orderSQL = byOrder ? "order by pl_order" : "order by SYS_DOCUMENTID";
		
		int layoutLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERLAYOUT.typeID(), paper.getDocLibID());
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] layoutDocs = null;
		try {
			layoutDocs = docManager.find(layoutLibID, "pl_date=? and pl_paperID=? and pl_status<7 " + orderSQL,
					new Object[]{paperDate, paper.getId()});
		} catch (Exception e) {
			log.error("读版面异常：" + e.getLocalizedMessage(), e);
		}
		
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), paper.getDocLibID());
		for (Document doc : layoutDocs) {
			PaperLayout layout = new PaperLayout(doc);
			//每个版读其下的稿件、附件
			if (!isOne) {
				layout.setArticles(readArticles(docLibID, layout));
				layout.setAttachments(readAttachments(layout.getDocLibID(), layout.getId()));
			}
			
			layouts.add(layout);
		}
		if (!isOne) {
			//对每个版设置上一版、下一版
			setLayoutSiblings(layouts);
		}
	}

	//组装一个版，用于单版发布时
	private PaperLayout readLayout(int layoutLibID, long layoutID) {
		//从数据库中读当前版
		Document layoutDoc = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		try {
			layoutDoc = docManager.get(layoutLibID, layoutID);
		} catch (Exception e) {
			log.error("读版面异常：" + e.getLocalizedMessage(), e);
			return null;
		}
		
		//组装当前版
		int docLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERARTICLE.typeID(), layoutLibID);
		PaperLayout layout = new PaperLayout(layoutDoc);
		layout.setArticles(readArticles(docLibID, layout));
		layout.setAttachments(readAttachments(layout.getDocLibID(), layout.getId()));
		
		return layout;
	}
	
	//对每个版设置同刊期版面列表、上一版、下一版
	private void setLayoutSiblings(List<PaperLayout> layouts) {
		for (int i = 0; i < layouts.size(); i++) {
			PaperLayout layout = layouts.get(i);
			PaperLayout previous = (i > 0) ? layouts.get(i - 1) : null;
			PaperLayout next = (i < layouts.size() - 1) ? layouts.get(i + 1) : null;
			
			layout.setPrevious(previous);
			layout.setNext(next);
			layout.setSiblings(layouts);
		}
	}

	private void setLayoutSiblings(PaperLayout layout, Date paperDate) {
		List<PaperLayout> layouts = new ArrayList<>();
		readLayouts(layouts, paperDate, true);
		
		//对版设置同刊期版面列表、上一版、下一版
		for (int i = 0; i < layouts.size(); i++) {
			PaperLayout l = layouts.get(i);
			if (l.getId() == layout.getId()) {
				PaperLayout previous = (i > 0) ? layouts.get(i - 1) : null;
				PaperLayout next = (i < layouts.size() - 1) ? layouts.get(i + 1) : null;
				
				layout.setPrevious(previous);
				layout.setNext(next);
				layout.setSiblings(layouts);
				
				break;
			}
		}
	}

	//读出稿件的数据：稿件本身、附件
	private List<PaperArticle> readArticles(int docLibID, PaperLayout layout) {
		List<PaperArticle> result = new ArrayList<>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] articleDoc = null;
		try {
			//标记为撤稿的稿件不做发布
			articleDoc = docManager.find(docLibID, "a_layoutID=? and a_status<7 order by a_order", 
					new Object[]{layout.getId()});
		} catch (E5Exception e) {
			log.error("读稿件和已发布状态参数时异常：" + e.getLocalizedMessage(), e);
		}
		
		for (Document doc : articleDoc) {
			PaperArticle article = new PaperArticle(doc);
			String content = compilesArticleContent(doc);//获取转版稿件的组合内容
			article.setContent(content);//稿件正文内容改为合并后的正文内容
			//article.setAttachments(readAttachments(doc.getDocLibID(), doc.getDocID()));
			//附件列表改为合并后的附件列表
			article.setAttachments(readAllRefAttachments(doc));
			article.setLayout(layout);
			
			result.add(article);
		}
		
		//对每个稿件设置上一篇、下一篇
		for (int i = 0; i < result.size(); i++) {
			PaperArticle article = result.get(i);
			PaperArticle previous = (i > 0) ? result.get(i - 1) : null;
			PaperArticle next = (i < result.size() - 1) ? result.get(i + 1) : null;
			
			article.setPrevious(previous);
			article.setNext(next);
		}
		
		return result;
	}
	
	//读附件
	private List<Attachment> readAttachments(int docLibID, long docID) {
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERATTACHMENT.typeID(), docLibID);
		
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

	//初始化叠信息
	private void initPiles() {
		if (StringUtils.isBlank(paper.getPiles())) return;
		
		//从第一个layout（单版发布时只有一个layout）中取出所有的layout信息（siblings变量）
		List<PaperLayout> all = layouts.get(0).getSiblings();
		if (all == null) return;
		
		//根据报纸中的叠定义，查找每个layout属于哪个叠
		JSONArray jsonPiles = JsonHelper.getJsonArray(paper.getPiles());
		if (jsonPiles == null) return;
		
		for (Object objPile : jsonPiles) {
			JSONObject jsonPile = (JSONObject)objPile;
			String code = JsonHelper.getString(jsonPile, "code");
			
			List<PaperLayout> pileLayouts = findPileLayout(all, code);
			if (pileLayouts != null) {
				PaperPile one = new PaperPile(JsonHelper.getString(jsonPile, "name"), code, pileLayouts);
				piles.add(one);
			}
		}
	}

	private List<PaperLayout> findPileLayout(List<PaperLayout> all, String pileCode) {
		List<PaperLayout> result = new ArrayList<>();
		for (PaperLayout layout : all) {
			if (layout.getPile().equals(pileCode)) {
				result.add(layout);
			}
		}
		if (result.size() == 0) return null;
		
		return result;
	}

	//按发布规则，得到稿件、图片、附件的发布URL和发布地址
	private int checkAndSetPath(String rootPath, String paperDate) {
		//取发布规则
		int libID = LibHelper.getLibIDByOtherLib(DocTypes.SITERULE.typeID(), paper.getDocLibID());
		BaseDataCache siteRuleCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		SiteRule pubRule = siteRuleCache.getSiteRuleByID(libID, paper.getPubRule());
		SiteRule pubRulePad = siteRuleCache.getSiteRuleByID(libID, paper.getPubRulePad());
		
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
			
			pageDirMap.put("column", getColumnDir(rootPath, pubRule,datePath));
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

			pageDirMap.put("columnPad", getColumnDir(rootPath, pubRulePad,datePath));
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
			url.append(siteRule.getArticleDir()).append("/");
			if (siteRule.isArticleByDate()){
				url.append(datePath).append("/");
			}
		}
		return url.toString();
	}
	private String getArticleDir(String siteWebRoot, SiteRule siteRule, String datePath){
		StringBuffer dir = new StringBuffer();
		
		dir.append(siteWebRoot)
			.append(siteRule.getArticlePath())
			.append("/");
		
		if (siteRule.isArticleByDate()) {
			dir.append(datePath).append("/");
		}
		return dir.toString();
	}

	private String getUrlWithDate(String siteWebRoot, String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return siteWebRoot + path + datePath + "/";
	}
	
	private String getUrlWithDate(String path, String datePath) {
		if (!path.endsWith("/")) path += "/";
		
		return path + datePath + "/";
	}
	private String getColumnDir(String siteWebRoot, SiteRule siteRule,String datePath){
		StringBuffer dir = new StringBuffer();
		if (siteRule != null) {
			dir.append(siteWebRoot).append(siteRule.getColumnPath()).append("/");
			
			//if (siteRule.isColumnByDate())//（不按发布规则）固定对版面目录加上日期，方便period.xml和首页的读写
			dir.append(datePath).append("/"); //yyyyMM/dd
		}
		return dir.toString();
	}
	//根据发布规则拼出版面发布URL
	private String getColumnUrl(SiteRule siteRule, String datePath){
		StringBuffer url = new StringBuffer();
		
		if (siteRule != null){
			url.append(siteRule.getColumnDir()).append("/");
			
			//if (siteRule.isColumnByDate())//（不按发布规则）固定对版面目录加上日期，方便period.xml和首页的读写
			url.append(datePath).append("/"); //yyyyMM/dd
		}
		return url.toString();
	}
	
	private String getRootPath(int siteID, int refLibID) {
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), refLibID);
		
		BaseDataCache siteCache = (BaseDataCache) (CacheReader.find(BaseDataCache.class));
		return siteCache.getSiteWebRootByID(siteLibID, siteID);
	}
	
	private List<Long> getTransIDs(Document doc){
		List<Long> transIDs = new ArrayList<>();
		if(doc!=null){
			String transArticleIDs = doc.getString("a_transArticleIDs");
			if(StringUtils.isBlank(transArticleIDs)){
				transArticleIDs = "";
			}
			String[] transIDStrs =  StringUtils.split(transArticleIDs,",");
			for(String transIDStr : transIDStrs){
				transIDs.add(NumberUtils.toLong(transIDStr));
			}
		}
		return transIDs;
	}
	
	private String compilesArticleContent(Document doc) {
		String content = doc.getString("a_content");
		try {
			if(doc.getInt("a_transStatus")==1){
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				int docLibID = doc.getDocLibID();
				List<Long> transIDs = getTransIDs(doc);	
				String preLayout = "";
				for(int i=0; i<transIDs.size(); i++){
					Document tdoc = docManager.get(docLibID, transIDs.get(i));
					if(tdoc!=null){
						if(StringUtils.isBlank(preLayout)){
							content = tdoc.getString("a_content");
						}else{
							String afterContent = tdoc.getString("a_content");
							content = content.substring(0, content.lastIndexOf("</p>")) 
									+ "(转" + tdoc.getString("a_layout") + "版)"
									+ content.substring(content.lastIndexOf("</p>"))
									
									+ afterContent.substring(0, afterContent.indexOf("<p>")+3) 
									+ "(接" + preLayout + "版)"
									+ afterContent.substring(afterContent.indexOf("<p>")+3);
						}
						preLayout = tdoc.getString("a_layout");
					}
				}
			}
			return content;
		} catch (Exception e) {
			log.error("组合转版稿件正文内容出错：" + e.getLocalizedMessage(), e);
		}
		return content;
	}
	
	//读附件
	private List<Attachment> readAllRefAttachments(Document doc) {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = doc.getDocLibID();
		int attLibID = LibHelper.getLibIDByOtherLib(DocTypes.PAPERATTACHMENT.typeID(), docLibID);
		List<Long> transIDs = getTransIDs(doc);	
		if(transIDs.size()==0){
			transIDs.add(doc.getDocID());
		}
		List<Attachment> attList = new ArrayList<>();
		Document[] attDocs = null;
		try {
			for(Long docID : transIDs){
				attDocs = docManager.find(attLibID, 
						"att_articleID=? and att_articleLibID=? order by att_order desc",
						new Object[]{docID, docLibID});
				for(Document attDoc : attDocs){
					attList.add(new Attachment(attDoc));
				}
			}
		} catch (E5Exception e) {
			log.error("读附件异常：" + e.getLocalizedMessage(), e);
		}
		return attList;
	}
}
