package com.founder.xy.jms.data;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * 稿件数据转换器
 * @author Gong Lijie
 */
public class ArticleConverter implements MessageConverter{

	@Override
	public Object fromMessage(Message obj) throws JMSException, MessageConversionException {
		try {
			MapMessage msg = (MapMessage) obj;
			
			return new ArticleMsg(msg.getInt("docLibID"), msg.getLong("docID"), 
					msg.getInt("columnID"), 
					msg.getString("columnAll"),
					msg.getInt("type"),
					msg.getInt("channel"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Message toMessage(Object obj, Session session) throws JMSException,
			MessageConversionException {
		ArticleMsg data = (ArticleMsg)obj;
		
		MapMessage msg = session.createMapMessage(); 
		msg.setInt("docLibID", data.getDocLibID());
		msg.setLong("docID", data.getId());
		msg.setInt("columnID", data.getColID());
		msg.setInt("type", data.getType());
		msg.setInt("channel", data.getChannel());
		msg.setString("columnAll", data.getColAll());
		
		return msg;
	}
}
