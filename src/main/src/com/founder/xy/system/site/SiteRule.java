package com.founder.xy.system.site;

public class SiteRule {
	private long ruleID;
	
	private int siteID;
	//规则名称
	private String  ruleName;
	//栏目URL
	private String columnDir;
	private long columnDirID;
	private String columnPath;
	
	//稿件URL
	private String articleDir;
	private long articleDirID;
	private String articlePath;
	
	//稿件图片URL
	private String photoDir;
	private long photoDirID;
	private String photoPath;
	
	//稿件附件URL
	private String attachDir;
	private long attachDirID;
	private String attachPath;
	
	//稿件页按日期
	private boolean articleByDate;
	//栏目页按日期
	private boolean columnByDate;

	public long getRuleID() {
		return ruleID;
	}

	public void setRuleID(long rule_ID) {
		this.ruleID = rule_ID;
	}

	public int getSiteID() {
		return siteID;
	}

	public void setSiteID(int rule_siteID) {
		this.siteID = rule_siteID;
	}

	public String getColumnDir() {
		return columnDir;
	}

	public void setColumnDir(String rule_column_dir) {
		this.columnDir = rule_column_dir;
	}

	public String getArticleDir() {
		return articleDir;
	}

	public void setArticleDir(String rule_article_dir) {
		this.articleDir = rule_article_dir;
	}

	public String getPhotoDir() {
		return photoDir;
	}

	public void setPhotoDir(String rule_photo_dir) {
		this.photoDir = rule_photo_dir;
	}

	public String getAttachDir() {
		return attachDir;
	}

	public void setAttachDir(String rule_attach_dir) {
		this.attachDir = rule_attach_dir;
	}

	public boolean isArticleByDate() {
		return articleByDate;
	}

	public void setArticleByDate(boolean rule_article_date) {
		this.articleByDate = rule_article_date;
	}

	public String getColumnPath() {
		return columnPath;
	}

	public void setColumnPath(String rule_column_path) {
		this.columnPath = rule_column_path;
	}

	public String getArticlePath() {
		return articlePath;
	}

	public void setArticlePath(String rule_article_path) {
		this.articlePath = rule_article_path;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String rule_photo_path) {
		this.photoPath = rule_photo_path;
	}

	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String rule_attach_path) {
		this.attachPath = rule_attach_path;
	}

	public boolean isColumnByDate() {
		return columnByDate;
	}

	public void setColumnByDate(boolean columnByDate) {
		this.columnByDate = columnByDate;
	}

	public long getColumnDirID() {
		return columnDirID;
	}

	public void setColumnDirID(long columnDirID) {
		this.columnDirID = columnDirID;
	}

	public long getArticleDirID() {
		return articleDirID;
	}

	public void setArticleDirID(long articleDirID) {
		this.articleDirID = articleDirID;
	}

	public long getPhotoDirID() {
		return photoDirID;
	}

	public void setPhotoDirID(long photoDirID) {
		this.photoDirID = photoDirID;
	}

	public long getAttachDirID() {
		return attachDirID;
	}

	public void setAttachDirID(long attachDirID) {
		this.attachDirID = attachDirID;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
