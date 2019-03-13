package com.founder.amuc.jms;

import org.springframework.jms.core.JmsTemplate;

/**
 * 消息发布器。
 * 不同消息发送时可以都使用这个Publisher，用send(Object)发送。
 * 需要在Spring xml里定义不同的template。
 * 
 * @author Gong Lijie
 */
public class Publisher {
	protected JmsTemplate jmsTemplate;
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final Object obj){
		jmsTemplate.convertAndSend(obj);
	}
}