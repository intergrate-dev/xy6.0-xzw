package com.founder.amuc.jms.data;

import com.founder.amuc.member.Event;
import com.founder.amuc.score.Rule;

/**
 * <未使用>
 * 
 * 积分计算消息的数据
 * @author Gong Lijie
 */
public class RuleEvent {
	private Rule rule;
	private Event event;
	
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
}