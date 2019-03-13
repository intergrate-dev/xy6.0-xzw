package com.founder.xy.api.nis;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.founder.e5.context.E5Exception;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.nis.EventCountHelper;

/**
 * 事件处理器：
 * 点击事件（稿件、直播）、
 * 点赞事件（稿件、直播、评论）、
 * 分享事件（稿件、直播）、
 * 分享点击事件（稿件、直播）、
 * 评论事件（稿件）
 * 举报事件（直播、评论）
 * 订阅事件（选题）
 * @author Gong Lijie
 */
@Service
public class EventApiManager {
	/**
	 * 提交稿件点击事件、点赞事件、分享事件、分享页点击事件
	 * 渠道channel 0-网站 1-触屏 2-app
	 * 对象类型type：0——稿件；1——评论；3——直播；4--活动；5--栏目；7数字报稿件；8话题问答；9问答
	 * 事件类型eventType：0——点击；1——点赞；2——分享；3——分享页点击
	 */
	public boolean event(String jsonStr) throws E5Exception {
		JSONObject obj = JSONObject.fromObject(jsonStr);
		
		long id = obj.getLong("id");
		int eventType = JsonHelper.getInt(obj, "eventType"); // 事件类型
		int channel = JsonHelper.getInt(obj, "channel", 2); // 来自渠道，默认是app
		
		int type = obj.getInt("type");
		switch (type){
			case 0:
				EventCountHelper.addArticleCount(id, eventType, channel);
				break;
			case 1:
				EventCountHelper.addDiscussCount(id, eventType);
				break;
			case 3:
				EventCountHelper.addLiveCount(id, eventType, channel);
				break;
			case 4: //活动
				EventCountHelper.addActivityCount(id, eventType);
				break;
			case 5:
				EventCountHelper.addColumnCount(id, eventType);
				break;
			case 6://行业分类预留
				return EventCountHelper.eventTrade(obj);
			case 7: //数字报稿件
				EventCountHelper.addPaperArticleCount(id, eventType);
				break;
			case 8: //互动话题问答
				EventCountHelper.addSubjectQACount(id, eventType);
				break;
			case 9: //互动问答
				EventCountHelper.addQACount(id, eventType);
				break;
		}
		return true;
	}

}
