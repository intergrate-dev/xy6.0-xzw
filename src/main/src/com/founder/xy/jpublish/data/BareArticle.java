package com.founder.xy.jpublish.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.founder.e5.doc.Document;

public class BareArticle implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected long id;
	protected String title;
	protected String subTitle; //副题
	protected String introTitle; //引题
	protected String shortTitle;
	protected String summary;
	protected String content;
	protected String author;
	protected String source;
	protected String sourceUrl;
	protected String editor;
	protected String liability;
	protected String keyword;
	protected String column; //主栏目名
	protected String currentColName; //当前栏目名字

	protected Date pubTime; //发布时间
	protected Date pubTimeReal; //实际发布时间
	protected Date created; //创建时间
	protected Date lastModified;//最后修改时间
	
	private int type; //稿件类型，0-文章，1-组图，2-视频，...

	protected int copyright; //是否原创，1=原创
	protected int discussClosed;
	
	protected String url;
	protected String urlPad;//触屏版链接
	protected String tag;
	
	protected int channel; //稿件所在渠道，1=Web，2=App
	
	protected String picBig; //标题图片 = "图片存储;201505/20/0b927b03-cb3e-43b0-8155-df3e813341ed.jpg";
	protected String picMiddle;
	protected String picSmall;
	
	protected int docLibID;
	protected int columnID;
	protected String columnRel; //关联栏目名，多个
	protected String columnRelID;
	protected int sourceID;
	protected int siteID;
	
	private int regionID; //地区
	private String region;
	
	protected int linkID;
	protected String linkName;
	protected int bigPic; //是大图稿件
	
	protected int countClick;
	protected int countClickInitial;
	protected int countDiscuss;
	protected int countPraise;
	protected int countShare;
	protected int countShareClick;
	
	protected int position;
	protected int sourceType;
	protected int authorID;

	protected String collaborator; //合作者
	
	//主栏目链接
	protected String masterColUrl;
	//主栏目图标
	protected String masterColIcon;

	//当前栏目链接
	protected String currentColUrl;
	//当前栏目图标
	protected String currentColIcon;

	//稿件属性
	protected int attr;
	//多媒体链接
	protected String multimediaLink;
	
	protected String mark;//列表角标
	private String trade; //行业分类
	private String tradeID;
	private int liveStatus;

    protected String videoID;//百格视频ID
	
	public BareArticle() {
		
	}
	
	public BareArticle(Document doc) {
		docLibID = doc.getDocLibID();
		id = doc.getDocID();
		author = doc.getAuthors();
		editor = doc.getString("a_editor");
		liability = doc.getString("a_liability");
		
		String columns = doc.getString("a_column");		
		column = columns.lastIndexOf("~")==-1?columns:columns.substring(columns.lastIndexOf("~")+1, columns.length());
		
		currentColName = doc.getString("a_column");
		columnID = getInt(doc.getInt("a_columnID"));
		columnRel = doc.getString("a_columnRel");
		columnRelID = doc.getString("a_columnRelID");
		copyright = getInt(doc.getInt("a_copyright"));
		discussClosed = getInt(doc.getInt("a_discussClosed"));
		
		source = doc.getString("a_source");
		sourceUrl = doc.getString("a_sourceUrl");
		sourceID = doc.getInt("a_sourceID");
		
		title = doc.getTopic();
		subTitle = doc.getString("a_subTitle");
		introTitle = doc.getString("a_leadTitle");
		shortTitle = doc.getString("a_shortTitle");
		keyword = doc.getString("a_keyword");
		summary = doc.getString("a_abstract");
		content = doc.getString("a_content");
		url = doc.getString("a_url");
		urlPad = doc.getString("a_urlPad");
		tag = doc.getString("a_tag");
		
		picBig = doc.getString("a_picBig");
		picMiddle = doc.getString("a_picMiddle");
		picSmall = doc.getString("a_picSmall");
		liveStatus = doc.getInt("a_liveStatus");

		Timestamp time = doc.getTimestamp("a_pubTime");
		pubTime = (time != null) ? new Date(time.getTime()) : new Date();
		
		time = doc.getTimestamp("a_realPubTime");
		pubTimeReal = (time != null) ? new Date(time.getTime()) : new Date();
		
		time = doc.getCreated();
		if (time != null) created = new Date(time.getTime());
		
		time = doc.getLastmodified();
		if (time != null) lastModified = new Date(time.getTime());

		channel = doc.getInt("a_channel");
		type = doc.getInt("a_type");

		region = doc.getString("a_region");
		regionID = doc.getInt("a_regionID");
		siteID = doc.getInt("a_siteID");
		linkID = getInt(doc.getInt("a_linkID"));
		linkName = doc.getString("a_linkName");
		bigPic = getInt(doc.getInt("a_isBigPic"));
		
		countClickInitial = getInt(doc.getInt("a_countClickInitial"));
		countShareClick = getInt(doc.getInt("a_countShareClick"));
		countClick = getInt(doc.getInt("a_countClick"));
		countDiscuss = getInt(doc.getInt("a_countDiscuss"));
		countPraise = getInt(doc.getInt("a_countPraise"));
		countShare = getInt(doc.getInt("a_countShare"));
		
		position = getInt(doc.getInt("a_position"));
		sourceType = getInt(doc.getInt("a_sourceType"));
		authorID = getInt(doc.getInt("SYS_AUTHORID"));
		
		collaborator = doc.getString("a_collaborator");
		
		attr = getInt(doc.getInt("a_attr"));
		multimediaLink = doc.getString("a_multimediaLink");
		mark = doc.getString("a_mark");
		trade = doc.getString("a_trade");
		tradeID = doc.getString("a_tradeID");
		liveStatus = doc.getInt("a_liveStatus");
	}
	private int getInt(int value) {
		if (value < 0) return 0;
		return value;
	}
	
	/**
	 * 区块内容的构造方法
	 */
	public BareArticle(Document doc, boolean blockArticle) {
		if (blockArticle) {
			docLibID = doc.getDocLibID();
			id = doc.getDocID();
			
			title = doc.getTopic();
			subTitle = doc.getString("ba_subTitle");
			summary = doc.getString("ba_abstract");
			url = doc.getString("ba_url");
			
			picBig = doc.getString("ba_pic");
			
			Timestamp time = doc.getTimestamp("ba_pubTime");
			if (time != null)
				pubTime = new Date(time.getTime());
		}
	}
	
	public long getId() {
		return id;
	}
	public int getType() {
		return type;
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

	public void setId(long docID) {
		this.id = docID;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public void setColumn(String column) {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String topic) {
		this.title = topic;
	}

	public String getSubTitle() {
		return subTitle;
	}
	
	public String getIntroTitle() {
		return introTitle;
	}

	public String getShortTitle() {
		return shortTitle;
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

	public Date getPubTime() {
		return pubTime;
	}

	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}
	
	public Date getPubTimeReal() {
		return pubTimeReal;
	}
	
	public void setPubTimeReal(Date pubTime) {
		this.pubTimeReal = pubTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String contentUrl) {
		this.url = contentUrl;
	}

	public String getUrlPad() {
		return urlPad;
	}

	public void setUrlPad(String urlPad) {
		this.urlPad = urlPad;
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

	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastModified() {
		return lastModified;
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

	public int getAttr() {
		return attr;
	}

	public void setAttr(int attr) {
		this.attr = attr;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getDiscussClosed() {
		return discussClosed;
	}
	
	public int getSiteID() {
		return siteID;
	}

	public int getLinkID() {
		return linkID;
	}

	public int getBigPic() {
		return bigPic;
	}

	public int getCountClick() {
		return countClick;
	}

	public int getCountClickInitial() {
		return countClickInitial;
	}

	public int getCountDiscuss() {
		return countDiscuss;
	}

	public int getCountPraise() {
		return countPraise;
	}

	public int getCountShare() {
		return countShare;
	}

	public int getCountShareClick() {
		return countShareClick;
	}

	public int getPosition() {
		return position;
	}

	public int getSourceType() {
		return sourceType;
	}

	public int getAuthorID() {
		return authorID;
	}

	public String getCollaborator() {
		return collaborator;
	}
	
	public String getMasterColUrl() {
		return masterColUrl;
	}

	public void setMasterColUrl(String masterColUrl) {
		this.masterColUrl = masterColUrl;
	}

	public String getMasterColIcon() {
		return masterColIcon;
	}

	public void setMasterColIcon(String masterColIcon) {
		this.masterColIcon = masterColIcon;
	}

	public String getCurrentColIcon() {
		return currentColIcon;
	}

	public void setCurrentColIcon(String currentColIcon) {
		this.currentColIcon = currentColIcon;
	}

	public String getCurrentColUrl() {
		return currentColUrl;
	}

	public void setCurrentColUrl(String currentColUrl) {
		this.currentColUrl = currentColUrl;
	}
	
	public String getCurrentColName() {
		return currentColName;
	}

	public void setCurrentColName(String currentColName) {
		this.currentColName = currentColName;
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

	public String getTrade() {
		return trade;
	}

	public String getTradeID() {
		return tradeID;
	}

	public String getLinkName() {
		return linkName;
	}

	public int getLiveStatus() {
		return liveStatus;
	}

	public void setLiveStatus(int liveStatus) {
		this.liveStatus = liveStatus;
	}

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
}
