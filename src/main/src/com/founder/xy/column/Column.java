package com.founder.xy.column;

import java.util.ArrayList;
import java.util.List;

import com.founder.e5.doc.Document;

/**
 * 栏目Bean。
 * 用于栏目缓存，以及栏目树。
 * 
 * 为栏目树准备时，记录树需要的信息，是否有权限、是否可展开等。
 * @author Gong Lijie
 */
public class Column implements Cloneable{
	private int libID;
	private long id;
	private String name;
	private String keyword;
	private String description; // 描述
	
	private int channel;
	
	private String casIDs; //栏目的级联ID
	private String fileName;
	private String fileNamePad;
	
	private String casNames;
	private String sourceGroupIDs;
	private String extFieldGroup;
	private int extFieldGroupID;
	private int flowID;
	
	private int siteID;
	private long parentID;
	
	private int order;	//同级的顺序号
	private boolean enable; //是否有权限
	private boolean expandable; //是否有子，可展开
	
	//栏目模板
	private long template;
	//文章模板
	private long templateArticle;
	//组图模板
	private long templatePic;
	//视频模板
	private long templateVideo;
	//触屏版栏目模板
	private long templatePad;
	//触屏版文章模板
	private long templateArticlePad;
	//触屏版组图模板
	private long templatePicPad;
	//触屏版视频模板
	private long templateVideoPad;
	
	private long pubRule;
	private long pubRulePad;
	
	private String appStyle; // 栏目样式
	private String linkUrl; // 外部链接
	
	//导航栏目是否显示
	private int isIncom;
	private int isIncomPad;
	
	//App栏目需要的属性
	private long lastmodified; //最后修改时间，以long型表示
	private String iconBig;
	private String iconSmall;
	private int appType;
	private int topCount;
	private int appTypeID; // App栏目类型_ID
	private int appStyleID; // App栏目样式_ID
	
	private List<Column> children; //权限路径上的子（未必是全部的子）
	private String url; //栏目url
	private String urlPad; //栏目urlPad
	private boolean forbidden; //栏目被禁用
	private int level;

	private int isdelete;
	
	private int rssCount;
	private int appShow;
	private int appFixed;


	private int synXCX;
	private long columnPush;
	
	private String searchName;//用于站内检索的搜索名

	private int colorID;
	private String color;

	private String code;

	public int getIsdelete() {
		return isdelete;
	}

	/**
	 * 栏目缓存使用的构造方法
	 */
	public Column(Document doc) {
		libID = doc.getDocLibID();
		id = (int)doc.getDocID();
		name = doc.getString("col_name");
		keyword = doc.getString("col_keyword");

		casIDs = doc.getString("col_cascadeID");
		casNames = doc.getString("col_cascadeName");
		fileName = doc.getString("col_fileName");
		fileNamePad = doc.getString("col_fileNamePad");
		sourceGroupIDs = doc.getString("col_source");
		extFieldGroupID = doc.getInt("col_extField_ID");
		extFieldGroup = doc.getString("col_extField");
		flowID = doc.getInt("col_flow_ID");
		channel = doc.getInt("col_channel");
		siteID = doc.getInt("col_siteID");
		
		order = doc.getInt("col_displayOrder");
		expandable = (doc.getInt("col_childCount") > 0);
		forbidden = (doc.getInt("col_status") > 0); //栏目禁用
		
		template = doc.getLong("col_template_ID");
		templateArticle = doc.getLong("col_templateArticle_ID");
		templatePic = doc.getLong("col_templatePic_ID");
		templateVideo = doc.getLong("col_templateVideo_ID");
		
		templatePad = doc.getLong("col_templatePad_ID");
		templateArticlePad = doc.getLong("col_templateArticlePad_ID");
		templatePicPad = doc.getLong("col_templatePicPad_ID");
		templateVideoPad = doc.getLong("col_templateVideoPad_ID");
		
		pubRule = doc.getLong("col_pubRule_ID");
		pubRulePad = doc.getLong("col_pubRulePad_ID");
		
		parentID = doc.getInt("col_parentID");
		
		if (doc.getLastmodified() != null)
			lastmodified = doc.getLastmodified().getTime();
		iconBig = doc.getString("col_iconBig");
		iconSmall = doc.getString("col_iconSmall");
		appType = doc.getInt("col_appType");
		topCount = doc.getInt("col_topCount");
		
		description = doc.getString("col_description"); // 描述
		appStyle = doc.getString("col_appStyle"); // 栏目样式
		linkUrl = doc.getString("col_linkUrl"); // 外部链接
		appTypeID = doc.getInt("col_appType_ID");
		appStyleID = doc.getInt("col_appStyle_ID");
		
		isIncom = doc.getInt("col_isIncom");
		isIncomPad = doc.getInt("col_isIncomPad");
		level = doc.getInt("col_level");

		isdelete = doc.getInt("SYS_DELETEFLAG");
		
		rssCount = doc.getInt("col_rssCount");
		appShow = doc.getInt("col_appShow");
		appFixed = doc.getInt("col_appFixed");
		synXCX = doc.getInt("col_synXCX");
		columnPush = doc.getLong("col_push_ID");
		
		searchName = doc.getString("col_searchName");

		colorID = doc.getInt("col_color_ID");
		color = doc.getString("col_color");

		code = doc.getString("col_code");
	}

	/**
	 * 栏目树使用的构造方法
	 */
	public Column(long id, String name, String casIDs, String casNames, boolean enable, 
			boolean expandable, int order, boolean forbidden) {
		this.id = id;
		this.name = name;
		this.casIDs = casIDs;
		this.casNames = casNames;
		this.enable = enable;
		this.expandable = expandable;
		this.order = order;
		this.forbidden = forbidden;
	}
	/**
	 * 栏目树使用的构造方法,专题导航用
	 */
	public Column(long id, String name, String casIDs, String casNames, boolean enable,
				  boolean expandable, int order, boolean forbidden,String linkUrl) {
		this.id = id;
		this.name = name;
		this.casIDs = casIDs;
		this.casNames = casNames;
		this.enable = enable;
		this.expandable = expandable;
		this.order = order;
		this.forbidden = forbidden;
		this.linkUrl = linkUrl;
	}
	/**
	 * 移动采编栏目树使用的构造方法
	 */
	public Column(long id, String name, String casIDs, String casNames, boolean enable,
				  boolean expandable, int order, boolean forbidden, long parentID) {
		this.id = id;
		this.name = name;
		this.casIDs = casIDs;
		this.casNames = casNames;
		this.enable = enable;
		this.expandable = expandable;
		this.order = order;
		this.forbidden = forbidden;
		this.parentID = parentID;
	}
	public long getTemplate() {
		return template;
	}

	public long getTemplateArticle() {
		return templateArticle;
	}

	public long getTemplatePic() {
		return templatePic;
	}

	public long getTemplateVideo() {
		return templateVideo;
	}

	public long getTemplatePad() {
		return templatePad;
	}

	public long getTemplateArticlePad() {
		return templateArticlePad;
	}

	public long getTemplatePicPad() {
		return templatePicPad;
	}

	public long getTemplateVideoPad() {
		return templateVideoPad;
	}

	public long getPubRule() {
		return pubRule;
	}

	public long getPubRulePad() {
		return pubRulePad;
	}

	public int getLibID() {
		return libID;
	}

	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getKeyword() {
		return keyword;
	}

	public String getCasIDs() {
		return casIDs;
	}
	public String getFileName() {
		return fileName;
	}

	public String getFileNamePad() {
		return fileNamePad;
	}

	public int getOrder() {
		return order;
	}
	public boolean isExpandable() {
		return expandable;
	}
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * 这是为栏目树准备的方法，需要在构造树时手工维护children。缓存中无children属性
	 */
	public List<Column> getChildren() {
		return children;
	}
	/**
	 * 这是为栏目树准备的方法，需要在构造树时手工维护children。缓存中无children属性
	 */
	public Column getChild(int id) {
		if (children == null) return null;
		for (Column col : children) {
			if (col.getId() == id)
				return col;
		}
		return null;
	}
	
	/**
	 * 这是为栏目树准备的方法，需要在构造树时手工维护children。缓存中无children属性
	 */
	public void removeChildren() {
		children = null;
	}
	
	/**
	 * 这是为栏目树准备的方法，需要在构造树时手工维护children。缓存中无children属性
	 */
	public void addChild(Column child) {
		if (children == null)
			children = new ArrayList<Column>();
		children.add(child);
	}

	public String getCasNames() {
		return casNames;
	}
	public String getSourceGroupIDs() {
		return sourceGroupIDs;
	}
	public int getExtFieldGroupID() {
		return extFieldGroupID;
	}
	public int getFlowID() {
		return flowID;
	}

	public int getChannel() {
		return channel;
	}

	public int getSiteID() {
		return siteID;
	}

	public String getExtFieldGroup() {
		return extFieldGroup;
	}

	public long getParentID() {
		return parentID;
	}

	public long getLastmodified() {
		return lastmodified;
	}

	public String getIconBig() {
		return iconBig;
	}

	public String getIconSmall() {
		return iconSmall;
	}

	public int getAppType() {
		return appType;
	}

	public int getTopCount() {
		return topCount;
	}

	public String getDescription() {
		return description;
	}
	public String getAppStyle() {
		return appStyle;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public int getAppTypeID() {
		return appTypeID;
	}
	public int getAppStyleID() {
		return appStyleID;
	}
	
	/** 只用于发布服务，需临时生成*/
	public String getUrl() {
		return url;
	}

	/** 只用于发布服务，需临时生成*/
	public void setUrl(String url) {
		this.url = url;
	}

	/** 只用于发布服务，需临时生成*/
	public String getUrlPad() {
		return urlPad;
	}

	/** 只用于发布服务，需临时生成*/
	public void setUrlPad(String urlPad) {
		this.urlPad = urlPad;
	}

	public boolean isForbidden() {
		return forbidden;
	}

	@Override
	public Column clone(){
		try {
			return (Column)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public int getIsIncom() {
		return isIncom;
	}

	public void setIsIncom(int isIncom) {
		this.isIncom = isIncom;
	}

	public int getIsIncomPad() {
		return isIncomPad;
	}

	public void setIsIncomPad(int isIncomPad) {
		this.isIncomPad = isIncomPad;
	}

	public int getLevel() {
		return level;
	}

	public int getRssCount() {
		return rssCount;
	}
	public int getAppShow() {
		return appShow;
	}
	public int getAppFixed() {
		return appFixed;
	}
	public int getSynXCX() {
		return synXCX;
	}
	
	public long getPushColumn() {
		return columnPush;
	}
	
	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}