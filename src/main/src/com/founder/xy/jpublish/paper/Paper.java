package com.founder.xy.jpublish.paper;

import com.founder.e5.doc.Document;

/**
 * 报纸
 * @author Gong Lijie
 */
public class Paper {
	private int siteID;
	private long id;
	private int docLibID;
	private String name;
	
	private long pubRule;
	private long template;//版面模板
	private long templateArticle;//文章模板
	
	private long pubRulePad;
	private long templatePad;//触屏版版面模板
	private long templateArticlePad;//触屏版文章模板
	
	private long templateHome;
	private long templateHomePad; //首页模板
	
	private String piles; //报纸的叠定义
	
	public Paper(Document doc) {
		id = doc.getDocID();
		docLibID = doc.getDocLibID();
		siteID = doc.getInt("pa_siteID");
		name = doc.getString("pa_name");
		
		pubRule = getLong(doc.getLong("pa_pubRule_ID"));
		template = getLong(doc.getLong("pa_template_ID"));
		templateArticle = getLong(doc.getLong("pa_templateArticle_ID"));
		
		pubRulePad = getLong(doc.getLong("pa_pubRulePad_ID"));
		templatePad = getLong(doc.getLong("pa_templatePad_ID"));
		templateArticlePad = getLong(doc.getLong("pa_templateArticlePad_ID"));
		
		templateHome = getLong(doc.getLong("pa_templateHome_ID"));
		templateHomePad = getLong(doc.getLong("pa_templateHomePad_ID"));
		
		piles = doc.getString("pa_piles");
	}

	public int getSiteID() {
		return siteID;
	}
	public long getId() {
		return id;
	}
	public int getDocLibID() {
		return docLibID;
	}

	public long getPubRule() {
		return pubRule;
	}
	public long getTemplate() {
		return template;
	}
	public long getTemplateArticle() {
		return templateArticle;
	}
	public long getPubRulePad() {
		return pubRulePad;
	}
	public long getTemplatePad() {
		return templatePad;
	}
	public long getTemplateArticlePad() {
		return templateArticlePad;
	}
	public String getName() {
		return name;
	}

	public long getTemplateHome() {
		return templateHome;
	}

	public long getTemplateHomePad() {
		return templateHomePad;
	}

	private long getLong(long value) {
		if (value < 0) return 0;
		return value;
	}

	public String getPiles() {
		return piles;
	}
}
