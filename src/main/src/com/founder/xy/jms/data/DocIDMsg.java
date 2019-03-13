package com.founder.xy.jms.data;

/**
 * 消息传递对象 之 ID（包括docLibID + docID + 关联ID）
 * @author Gong Lijie
 */
public class DocIDMsg {
	public static final int TYPE_COLUMN = 0;
	public static final int TYPE_TEMPLATE = 1;
	public static final int TYPE_TEMPLATE_GRANT = 2;
	public static final int TYPE_BLOCK = 3;
	
	public static final int TYPE_SITE = 4;
	public static final int TYPE_DOMAINDIR = 5;
	public static final int TYPE_SITERULE = 6;
	
	public static final int TYPE_EXTFIELD = 7;
	public static final int TYPE_SOURCE = 8;
	public static final int TYPE_COLUMN_REFRESHONLY = 9; //只刷新栏目缓存，不做其它栏目判断
	
	public static final int TYPE_ORGUSER = 10; //刷新E5机构用户以及SiteUser
	public static final int TYPE_USERREL = 11; //只刷新SiteUser
	
	public static final int TYPE_PERMISSION = 12; //角色权限。多租户时可能需要前台设置权限
	
	public static final int TYPE_COLUMN_SYNC = 13; //（栏目）同步到子栏目
	
	public static final int TYPE_PAPERDATE = 14; //（报纸）刊期发布
	
	public static final int TYPE_WX = 15; //微信菜单稿件发布
	
	public static final int TYPE_MAGAZINEDATE = 16; //期刊一期发布

	public static final int TYPE_REVOKE_PAPER = 17; //撤一期报纸
	public static final int TYPE_REVOKE_PAPER_LAYOUT = 18; //撤报纸版面
	public static final int TYPE_REVOKE_PAPER_ARTICLE = 19; //撤报纸稿件
	public static final int TYPE_CREATE_SITE = 20; //新建站点消息


	private int type; //消息类型
	
	private String relIDs; //稿件关联栏目中作为关联ID，报纸刊期发布中作为刊期
	private int docLibID;
	private long docID;
	
	public DocIDMsg() {
		super();
	}

	public DocIDMsg(int docLibID, long docID, String relIDs) {
		this.docLibID = docLibID;
		this.docID = docID;
		this.relIDs = relIDs;
	}

	public int getDocLibID() {
		return docLibID;
	}

	public long getDocID() {
		return docID;
	}

	public String getRelIDs() {
		return relIDs;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "docLibID=" + docLibID + ",docID=" + docID + ",relIDs=" + relIDs + ",type=" + type;
	}
}
