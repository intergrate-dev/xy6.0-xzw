package com.founder.xy.jms.data;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * DocID数据转换器
 * @author Gong Lijie
 */
public class DocIDConverter implements MessageConverter{

	@Override
	public Object fromMessage(Message obj) throws JMSException, MessageConversionException {
		try {
			MapMessage msg = (MapMessage) obj;
			
			DocIDMsg data = new DocIDMsg(msg.getInt("docLibID"), msg.getLong("docID"), msg.getString("relIDs"));
			data.setType(msg.getInt("type"));
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Message toMessage(Object obj, Session session) throws JMSException,
			MessageConversionException {
		DocIDMsg data = (DocIDMsg)obj;
		
		MapMessage msg = session.createMapMessage(); 
		msg.setInt("docLibID", data.getDocLibID());
		msg.setLong("docID", data.getDocID());
		msg.setString("relIDs", data.getRelIDs());
		msg.setInt("type", data.getType());
		
		return msg;
	}

}
