package com.founder.xy.jpublish.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.founder.e5.doc.Document;

/**
 * 发布服务中使用的Article对象，包括稿件本身的属性和附件、挂件、相关稿件等信息
 * @author Gong Lijie
 */
public class PubArticle extends BareArticle{
	private static final long serialVersionUID = 1770017202969849547L;
	
	public static final int SUCCESS = 0;
	public static final int ERROR_WEB_NO_TEMPLATE = 1; //"网站稿件需要先配置模板才可发布"
	public static final int ERROR_NO_DATA = 2; //"没有读到稿件，无法发布"
	public static final int ERROR_NO_PUBRULE = 3; //"没有设置发布规则，无法发布"
	public static final int ERROR_NO_PUBDIR = 4; //"发布路径为空"
	public static final int ERROR_PUBLISH_WIDGETS = 5; //"发布挂件时出错"
	public static final int ERROR_PUBLISH = 6; //"发布失败"
	public static final int ERROR_EXTRACTING = 7; //"抽图尚未完成"
	public static final int ERROR_BLOCK_PIC = 8; //"区块标题图片发布异常"
	
	public static final String[] ERROR_MSG = new String[]{
		null,
		"网站稿件需要先配置模板才可发布",
		"没有读到稿件，无法发布",
		"没有设置发布规则，无法发布",
		"发布路径为空",
		"发布挂件时出错",
		"发布失败",
		"抽图尚未完成",
		"区块标题图片发布异常",
	};
	
	private int[] templateIDs;
	private int extFieldGroupID;
	
	private List<Attachment> attachments; //正文附件（图片、视频、标题图片）
	private List<Attachment> attPics; //正文附件之图片，与attachments里的元素引用的是同一个对象
	private List<Attachment> attVideos; //正文附件之视频，与attachments里的元素引用的是同一个对象
	private List<Attachment> attFiles; //正文附件之附件，与attachments里的元素引用的是同一个对象
	
	private Widgets widgets; //挂件
	private List<BareArticle> rels; //相关稿件
	private HashMap<String, String> extFields; //扩展字段。<fieldName, value>
	
	private boolean lastPage = true; //分页显示时是否最后一页的标记
	
	private AuthorInfo authorInfo;
	
	public PubArticle() {
		super();
	}
	
	public PubArticle(Document doc) {
		super(doc);
		columnRel = doc.getString("a_columnRel");
		columnRelID = doc.getString("a_columnRelID");
		
		templateIDs = new int[2];
		templateIDs[0] = doc.getInt("a_templateID");
		templateIDs[1] = doc.getInt("a_templatePadID"); //稿件单独指定的模板ID

		extFieldGroupID = doc.getInt("a_extFieldGroupID");
	}
	
	public String getColumnRel() {
		return columnRel;
	}

	public String getColumnRelID() {
		return columnRelID;
	}

	public int[] getTemplateIDs() {
		return templateIDs;
	}

	public int getExtFieldGroupID() {
		return extFieldGroupID;
	}

	public Widgets getWidgets() {
		return widgets;
	}

	public void setWidgets(Widgets widgets) {
		this.widgets = widgets;
	}

	public List<BareArticle> getRels() {
		return rels;
	}

	public void setRels(List<BareArticle> rels) {
		this.rels = rels;
	}

	public HashMap<String, String> getExtFields() {
		return extFields;
	}

	public void setExtFields(HashMap<String, String> extFields) {
		this.extFields = extFields;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
		
		//从附件列表中拆出正文图片列表、正文视频列表
		attPics = new ArrayList<>();
		attVideos = new ArrayList<>();
		attFiles = new ArrayList<>();
		if (attachments != null) {
			for (Attachment att : attachments) {
				if (att.getType() == com.founder.xy.article.Article.ATTACH_PIC)
					attPics.add(att);
				else if (att.getType() == com.founder.xy.article.Article.ATTACH_VIDEO)
					attVideos.add(att);
				else if (att.getType() == com.founder.xy.article.Article.ATTACH_FILE)
					attFiles.add(att);
			}
		}
	}

	public List<Attachment> getAttPics() {
		return attPics;
	}

	public List<Attachment> getAttVideos() {
		return attVideos;
	}
	
	public List<Attachment> getAttFiles() {
		return attFiles;
	}

	public boolean isLastPage() {
		return lastPage;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

	public AuthorInfo getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(AuthorInfo authorInfo) {
		this.authorInfo = authorInfo;
	}
}
