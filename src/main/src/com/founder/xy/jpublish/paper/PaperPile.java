package com.founder.xy.jpublish.paper;

import java.util.List;

/**
 * 报纸的叠
 * @author Gong Lijie
 */
public class PaperPile {
	private String name;
	private String code;
	private List<PaperLayout> layouts; //叠的版面列表
	
	public PaperPile(String name, String code, List<PaperLayout> layouts) {
		super();
		this.name = name;
		this.code = code;
		this.layouts = layouts;
	}
	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	public List<PaperLayout> getLayouts() {
		return layouts;
	}
}
