package com.founder.xy.jms.data;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * 按栏目发布：数据转换器
 * @author Gong Lijie
 */
public class ColumnArticleConverter implements MessageConverter{

	@Override
	public Object fromMessage(Message obj) throws JMSException, MessageConversionException {
		try {
			MapMessage msg = (MapMessage) obj;
			
			int colLibID = msg.getInt("colLibID");
			long colID = msg.getLong("colID");
			JSONArray values = new JSONArray(msg.getString("articles"));
			
			List<ArticleMsg> articles = new ArrayList<>();
			for (int i = 0; i < values.length(); i++) {
				articles.add(getOneArticle((JSONObject)values.get(i)));
			}
			
			return new ColumnArticleMsg(colLibID, colID, articles);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ArticleMsg getOneArticle(JSONObject value) {
		return new ArticleMsg(value.getInt("docLibID"), value.getLong("id"), 
				value.getInt("colID"), value.getString("colAll"), 
				value.getInt("type"), value.getInt("channel"));
	}

	@Override
	public Message toMessage(Object obj, Session session) throws JMSException,
			MessageConversionException {
		ColumnArticleMsg data = (ColumnArticleMsg)obj;
		
		MapMessage msg = session.createMapMessage(); 
		
		String value = (new JSONArray(data.getArticles())).toString();
		
		msg.setInt("colLibID", data.getColLibID());
		msg.setLong("colID", data.getColID());
		msg.setObject("articles", value);
		
		return msg;
	}
}
