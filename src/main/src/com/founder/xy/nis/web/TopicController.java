package com.founder.xy.nis.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;

/**
 * 选题的操作
 */
@Controller
@RequestMapping("/xy/topic")
public class TopicController {
	/**
     * 推送客户端
     */
    @RequestMapping("Push.do")
    public ModelAndView pushApp(HttpServletRequest request, HttpServletResponse response,
    		Map<String, Object> model) throws Exception {

    	int topicLibID = WebUtil.getInt(request, "DocLibID", 0);
        long topicID = WebUtil.getLong(request, "DocIDs", 0);
        int siteID = WebUtil.getInt(request, "siteID",0);
        
        //选题
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document topic = docManager.get(topicLibID, topicID);
        
        //站点：为了得到站点名
        //int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), InfoHelper.getTenantCode(request));
        //Document site = docManager.get(siteLibID, siteID);

        //String description = "\"" + site.getString("site_name") + "\"" + "为我定制新闻：" + topic.getTopic();
        String description = "您的定制已送达：" + topic.getTopic();
        
        model.put("description", description);
        model.put("DocLibID", topic.getInt("t_articleLibID"));
        model.put("DocIDs", topic.getLong("t_articleID"));
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("siteID", siteID);
        model.put("topicID", topicID);
        model.put("type", 1);

        return new ModelAndView("/xy/article/PushApp", model);
    }
}