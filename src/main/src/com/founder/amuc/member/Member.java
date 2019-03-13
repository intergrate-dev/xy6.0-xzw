package com.founder.amuc.member;

public class Member {
	private int id;
	private String name;
	private int score;
	private int level;
	private int activity;

	public int getActivity() {
		return activity;
	}
	public void setActivity(int activity) {
		this.activity = activity;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getLevel(){
		return level;
	}
	public void setLevel(int level){
		this.level = level;
	}
}
