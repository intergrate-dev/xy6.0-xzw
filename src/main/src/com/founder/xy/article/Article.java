package com.founder.xy.article;

import java.util.Date;

import com.founder.e5.doc.Document;

/**
 * 稿件对象
 *
 * @author Gong Lijie
 */
public class Article {
    /**
     * 稿件类型：文章
     */
    public static final int TYPE_ARTICLE = 0;
    /**
     * 稿件类型：组图
     */
    public static final int TYPE_PIC = 1;
    /**
     * 稿件类型：视频
     */
    public static final int TYPE_VIDEO = 2;
    /**
     * 稿件类型：专题
     */
    public static final int TYPE_SPECIAL = 3;
    /**
     * 稿件类型：链接
     */
    public static final int TYPE_LINK = 4;
    /**
     * 稿件类型：合成多标题
     */
    public static final int TYPE_MULTITITLE = 5;
    /**
     * 稿件类型：直播
     */
    public static final int TYPE_LIVE = 6;
    /**
     * 稿件类型：活动
     */
    public static final int TYPE_ACTIVITY = 7;
    /**
     * 稿件类型：广告
     */
    public static final int TYPE_AD = 8;
    /**
     * 稿件类型：文档
     */
    public static final int TYPE_FILE = 9;
    /**
     * 稿件类型：话题
     */
    public static final int TYPE_SUBJECT = 10;
    /**
     * 稿件类型：全景图片
     */
    public static final int TYPE_PANORAMA = 11;
    /**
     * 稿件类型：H5
     */
    public static final int TYPE_H5 = 12;
    /**
     * 稿件类型：微信稿件
     */
    public static final int TYPE_WXARTICLE = 13;

    /**
     * 未发布
     */
    public static final int STATUS_PUB_NOT = 0;
    /**
     * 已发布
     */
    public static final int STATUS_PUB_DONE = 1;
    /**
     * 定时发布
     */
    public static final int STATUS_PUB_TIMED = 2;
    /**
     * 发布中
     */
    public static final int STATUS_PUB_ING = 3;
    /**
     * 审批中
     */
    public static final int STATUS_AUDITING = 4;
    /**
     * 已驳回
     */
    public static final int STATUS_REJECTED = 5;
    /**
     * 等待抽图
     */
    public static final int STATUS_EXTRACTING = 6;
    /**
     * 已撤稿
     */
    public static final int STATUS_REVOKE = 7;

    /**
     * 附件类型：正文图片
     */
    public static final int ATTACH_PIC = 0;
    /**
     * 附件类型：正文视频
     */
    public static final int ATTACH_VIDEO = 1;
    /**
     * 附件类型：标题图片（大）
     */
    public static final int ATTACH_PICTITLE_BIG = 2;
    /**
     * 附件类型：标题图片（中）
     */
    public static final int ATTACH_PICTITLE_MIDDLE = 3;
    /**
     * 附件类型：标题图片（小）
     */
    public static final int ATTACH_PICTITLE_SMALL = 4;
    /**
     * 附件类型：正文附件
     */
    public static final int ATTACH_FILE = 5;
    /**
     * 附件类型：报纸版面图
     */
    public static final int ATTACH_LAYOUT_PIC = 5;
    /**
     * 附件类型：报纸版面pdf
     */
    public static final int ATTACH_LAYOUT_PDF = 6;

    /**
     * 稿件属性：图片新闻
     */
    public static final int ARTICLE_ATTR_PIC = 61;
    /**
     * 稿件属性：头条新闻
     */
    public static final int ARTICLE_ATTR_HEADLINE = 62;
    /**
     * 稿件属性：一般新闻
     */
    public static final int ARTICLE_ATTR_COMMON = 63;

    private int docLibID;
    private long docID;
    private String author;
    private String editor;
    private String column;
    private int columnID;
    private String columnRel;
    private String columnRelID;
    private String source;
    private int sourceID;
    private String sourceUrl;
    private String liability;
    private int copyright;
    private String topic;
    private String subTitle;
    private String keyword;
    private String summary;
    private String content;
    private String contentUrl;
    private String tag;

    private String linkTitle;
    private int channel;

    private String picBig; // = "图片存储;201505/20/0b927b03-cb3e-43b0-8155-df3e813341ed.jpg";
    private String picMiddle;
    private String picSmall;

    private String template;
    private int templateID;
    private String templatePad;
    private int templatePadID;

    private String extFieldGroup;
    private int extFieldGroupID;

    private String currentColumn;
    private boolean isNew;
    private int type;
    //外链资源名字
    private String linkName;
    //外链资源ID
    private int linkID;

    // 稿件类型 名称
    private String typeName;
    // 稿件状态
    private int status;
    private String statusName;
    // 原稿id
    private int originalID;
    // 创建时间
    private Date createDate;
    //是否存在敏感词
    private int isSensitive;
    //是否存在非法词
    private int isIllegal;

    private String url;            //pc链接
    private String urlPad;        //pad链接

    private int regionID; //地区
    private String region;

    private boolean discussClosed;
    private boolean bigPic;
    private boolean exclusive;
    private int countClickInitial;

    private int wordCount;
    private String collaborator; //合作者

    private int attr; //稿件属性
    private String leadTitle; //引题
    private String shortTitle; //短标题

    private String multimediaLink; //多媒体链接
    private String mark; //列表角标
    private String trade; //行业分类
    private String tradeID;

    private String topics;//话题

    public Article() {

    }

    public Article(Document doc) {
        docLibID = doc.getDocLibID();
        docID = doc.getDocID();
        author = doc.getAuthors();
        editor = doc.getString("a_editor");
        column = doc.getString("a_column");
        columnID = getInt(doc.getInt("a_columnID"));
        columnRel = doc.getString("a_columnRel");
        columnRelID = doc.getString("a_columnRelID");
        liability = doc.getString("a_liability");
        copyright = getInt(doc.getInt("a_copyright"));
        source = doc.getString("a_source");
        sourceUrl = doc.getString("a_sourceUrl");
        sourceID = doc.getInt("a_sourceID");
        linkID = getInt(doc.getInt("a_linkID"));
        linkName = doc.getString("a_linkName");

        topic = doc.getTopic();
        subTitle = doc.getString("a_subTitle");
        keyword = doc.getString("a_keyword");
        summary = doc.getString("a_abstract");
        content = doc.getString("a_content");
        content = content.replaceAll("\r|\n", "");
        contentUrl = doc.getString("a_url");
        tag = doc.getString("a_tag");

        picBig = doc.getString("a_picBig");
        picMiddle = doc.getString("a_picMiddle");
        picSmall = doc.getString("a_picSmall");

        template = doc.getString("a_template");
        templateID = doc.getInt("a_templateID");
        templatePad = doc.getString("a_templatePad");
        templatePadID = doc.getInt("a_templatePadID");

        extFieldGroup = doc.getString("a_extFieldGroup");
        extFieldGroupID = doc.getInt("a_extFieldGroupID");

        type = doc.getInt("a_type");
        channel = doc.getInt("a_channel");
        status = doc.getInt("a_status");

        setTypeName(type);
        setStatusName(status);

        originalID = doc.getInt("a_originalID");
        createDate = doc.getDate("SYS_CREATED");
        if (column != null && !"".equals(column))
            currentColumn = column.lastIndexOf("~") == -1 ? column : column.substring(column.lastIndexOf("~") + 1,
                                                                                      column.length());
        isSensitive = doc.getInt("a_isSensitive");
        isIllegal = doc.getInt("a_isIllegal");

        this.url = doc.getString("a_url");
        this.urlPad = doc.getString("a_urlPad");

        region = doc.getString("a_region");
        regionID = doc.getInt("a_regionID");

        discussClosed = (doc.getInt("a_discussClosed") == 1);
        bigPic = (doc.getInt("a_isBigPic") == 1);
        exclusive = (doc.getInt("a_isExclusive") == 1);

        countClickInitial = getInt(doc.getInt("a_countClickInitial"));
        wordCount = getInt(doc.getInt("a_wordCount"));

        collaborator = doc.getString("a_collaborator");
        attr = getInt(doc.getInt("a_attr"));
        leadTitle = doc.getString("a_leadTitle");
        shortTitle = doc.getString("a_shortTitle");
        multimediaLink = doc.getString("a_multimediaLink");
        mark = doc.getString("a_mark");
        trade = doc.getString("a_trade");
        tradeID = doc.getString("a_tradeID");
    }

    public long getDocID() {
        return docID;
    }

    public String getAuthor() {
        return author;
    }

    public String getEditor() {
        return editor;
    }

    public String getColumn() {
        return column;
    }

    public int getColumnID() {
        return columnID;
    }

    public String getColumnRel() {
        return columnRel;
    }

    public String getColumnRelID() {
        return columnRelID;
    }

    public String getSource() {
        return source;
    }

    public String getLiability() {
        return liability;
    }

    public int getCopyright() {
        return copyright;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setColumn(String column) {
        //this.column = column.replaceAll("~", "&gt;");
        this.column = column;
    }

    public void setColumnID(int columnID) {
        this.columnID = columnID;
    }

    public void setColumnRel(String columnRel) {
        this.columnRel = columnRel;
    }

    public void setColumnRelID(String columnRelID) {
        this.columnRelID = columnRelID;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLiability(String liability) {
        this.liability = liability;
    }

    public void setCopyright(int copyright) {
        this.copyright = copyright;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDocLibID() {
        return docLibID;
    }

    public void setDocLibID(int docLibID) {
        this.docLibID = docLibID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPicMiddle() {
        return picMiddle;
    }

    public void setPicMiddle(String picMiddle) {
        this.picMiddle = picMiddle;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getExtFieldGroup() {
        return extFieldGroup;
    }

    public void setExtFieldGroup(String extFieldGroup) {
        this.extFieldGroup = extFieldGroup;
    }

    public int getExtFieldGroupID() {
        return extFieldGroupID;
    }

    public void setExtFieldGroupID(int extFieldGroupID) {
        this.extFieldGroupID = extFieldGroupID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceLink) {
		this.sourceUrl = sourceLink;
	}

	public int getLinkID() {
        return linkID;
    }

    public void setLinkID(int linkID) {
        this.linkID = linkID;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(int type) {
        switch (type) {
            case 0:
                this.typeName = "文章";
                break;
            case 1:
                this.typeName = "图片";
                break;
            case 2:
                this.typeName = "视频";
                break;
            case 3:
                this.typeName = "专题";
                break;
            case 4:
                this.typeName = "链接";
                break;
            case 5:
                this.typeName = "多标题";
                break;
            case 6:
                this.typeName = "直播";
                break;
            case 7:
                this.typeName = "广告";
                break;
            case 11:
                this.typeName = "全景图";
                break;
            case 15:
                this.typeName = "H5";
                break;
        }
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public void setStatusName(int status) {
        switch (status) {
            case 0:
                this.statusName = "未发布";
                break;
            case 1:
                this.statusName = "已发布";
                break;
            case 2:
                this.statusName = "定时发布";
                break;
            case 3:
                this.statusName = "正在发布";
                break;
            case 4:
                this.statusName = "审批中";
                break;
            case 5:
                this.statusName = "已驳回";
                break;
        }
    }

    public int getOriginalID() {
        return originalID;
    }

    public void setOriginalID(int originalID) {
        this.originalID = originalID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTemplatePad() {
        return templatePad;
    }

    public int getTemplatePadID() {
        return templatePadID;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentColumn(String currentColumn) {
        this.currentColumn = currentColumn;
    }

    public int getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(int isSensitive) {
        this.isSensitive = isSensitive;
    }

    public void setIsIllegal(int isIllegal) {
        this.isIllegal = isIllegal;
    }

    public int getIsIllegal() {
        return isIllegal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlPad() {
        return urlPad;
    }

    public void setUrlPad(String urlPad) {
        this.urlPad = urlPad;
    }

    public int getRegionID() {
        return regionID;
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setTemplatePad(String templatePad) {
        this.templatePad = templatePad;
    }

    public void setTemplatePadID(int templatePadID) {
        this.templatePadID = templatePadID;
    }

    public boolean isDiscussClosed() {
        return discussClosed;
    }

    public boolean isBigPic() {
        return bigPic;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public int getCountClickInitial() {
        return countClickInitial;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getCollaborator() {
        return collaborator;
    }

    private int getInt(int value) {
        if (value < 0) return 0;
        return value;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }


    public String getLeadTitle() {
        return leadTitle;
    }

    public void setLeadTitle(String leadTitle) {
        this.leadTitle = leadTitle;
    }

    public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getMultimediaLink() {
        return multimediaLink;
    }

    public void setMultimediaLink(String multimediaLink) {
        this.multimediaLink = multimediaLink;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }
}
