package com.founder.xy.jpublish.data;

import java.util.List;

public class WidgetPic {
	private String title;
	private String content;
	private List<Widget> members;
	
	public WidgetPic(String topic, String content, List<Widget> members) {
		super();
		this.title = topic;
		this.content = content;
		this.members = members;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public List<Widget> getMembers() {
		return members;
	}
}
