package com.founder.xy.jpublish.magazine;

import java.util.ArrayList;
import java.util.List;

/**
 * 报纸栏目
 * @author Gong Lijie
 */
public class MagazineColumn {
	private String name;
	private List<MagazineArticle> articles; //本栏目下的稿件列表
	
	public MagazineColumn(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<MagazineArticle> getArticles() {
		return articles;
	}
	public void add(MagazineArticle article) {
		if (articles == null)
			articles = new ArrayList<>();
		
		int size = articles.size();
		if (size > 0) {
			articles.get(size - 1).setNext(article); //前一个稿件的“后一篇”设置
			article.setPrevious(articles.get(size - 1)); //当前稿件的“前一篇”设置
		}
		
		articles.add(article);
	}
}
