package com.founder.xy.jpublish.paper;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.xy.jpublish.data.PubArticle;

/**
 * 报纸稿件
 * @author Gong Lijie
 */
public class PaperArticle extends PubArticle{
	private static final long serialVersionUID = -3512350283737323342L;
	
	private PaperLayout layout; //当前版面
	private PaperArticle previous; //上一篇
	private PaperArticle next; //下一篇
	
	private String dir; //发布路径
	private String dirPad;

	private int transStatus; //转版状态
	private long[] transArticleIDs; //转版的稿件ID
	
	private String urlAbsolute; //发布过程中使用的，绝对路径Url，用于填写数据库
	
	public PaperArticle(Document doc) {
		super(doc);
		this.transStatus = doc.getInt("a_transStatus");
		String transArticleIDsStr = doc.getString("a_transArticleIDs");
		if(transStatus==1 && !StringUtils.isBlank(transArticleIDsStr)){
			this.transArticleIDs = StringUtils.getLongArray(transArticleIDsStr);
		}
	}
	
	public PaperLayout getLayout() {
		return layout;
	}
	public PaperArticle getPrevious() {
		return previous;
	}
	public PaperArticle getNext() {
		return next;
	}
	public void setLayout(PaperLayout layout) {
		this.layout = layout;
	}

	public void setPrevious(PaperArticle previous) {
		this.previous = previous;
	}

	public void setNext(PaperArticle next) {
		this.next = next;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDirPad() {
		return dirPad;
	}

	public void setDirPad(String dirPad) {
		this.dirPad = dirPad;
	}

	public String getUrlAbsolute() {
		return urlAbsolute;
	}

	public void setUrlAbsolute(String urlAbsolute) {
		this.urlAbsolute = urlAbsolute;
	}

	public int getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(int transStatus) {
		this.transStatus = transStatus;
	}

	public long[] getTransArticleIDs() {
		return transArticleIDs;
	}

	public void setTransArticleIDs(long[] transArticleIDs) {
		this.transArticleIDs = transArticleIDs;
	}
}
