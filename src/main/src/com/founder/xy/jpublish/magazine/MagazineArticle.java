package com.founder.xy.jpublish.magazine;

import java.util.List;

import com.founder.e5.doc.Document;
import com.founder.xy.jpublish.data.PubArticle;

/**
 * 报纸稿件
 * @author Gong Lijie
 */
public class MagazineArticle extends PubArticle{
	private static final long serialVersionUID = -3512350283737323342L;
	
	private MagazineArticle previous; //上一篇
	private MagazineArticle next; //下一篇
	
	private String dir; //发布路径
	private String dirPad;
	
	private List<MagazineColumn> columns;
	private String magazine; //所属期刊
	private int magazineID;
	
	public MagazineArticle(Document doc) {
		super(doc);
		
		magazine = doc.getString("a_magazine");
		magazineID = doc.getInt("a_magazineID");
	}
	
	public MagazineArticle getPrevious() {
		return previous;
	}
	public MagazineArticle getNext() {
		return next;
	}
	
	public void setPrevious(MagazineArticle previous) {
		this.previous = previous;
	}

	public void setNext(MagazineArticle next) {
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

	public List<MagazineColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<MagazineColumn> columns) {
		this.columns = columns;
	}

	public String getMagazine() {
		return magazine;
	}

	public void setMagazine(String magazine) {
		this.magazine = magazine;
	}

	public int getMagazineID() {
		return magazineID;
	}

	public void setMagazineID(int magazineID) {
		this.magazineID = magazineID;
	}
}
