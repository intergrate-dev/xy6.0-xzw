package com.founder.amuc.score;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.JsonHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;

@Controller
@RequestMapping("/amuc/score")
public class ScoreConfigController {

	@RequestMapping(value = "GetScoreConfig.do")
	public ModelAndView getScoreConfig(HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
	
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteLibID = LibHelper.getLibIDByOtherLib(DocTypes.SITE.typeID(), docLibID);
		int siteID = WebUtil.getInt(request, "DocIDs", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		String siteConfig = site.getString("site_config");
		if (!StringUtils.isBlank(siteConfig)) {
			JSONObject jsonConfig = JsonHelper.getJson(siteConfig);
			if (null != jsonConfig.get("member")){
				siteConfig = jsonConfig.get("member").toString();
			}
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("siteID", siteID);
		model.put("siteLibID", siteLibID);
		model.put("scoreConfig", siteConfig.replace("\"", "'"));
		model.put("UUID", WebUtil.get(request, "UUID"));
	
		return new ModelAndView("/amuc/score/ScoreConfig", model);
	}
	
	//保存会员积分的参数配置
	@RequestMapping(value = {"SaveScoreConfig.do"})
	public String saveScoreConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int siteLibID = WebUtil.getInt(request, "siteLibID", 0);
		int siteID = WebUtil.getInt(request, "siteID", 0);
		String disConfig = WebUtil.get(request, "scoreConfig");
		
		//取出站点的配置json，替换其中的积分设置json。
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		String siteConfig = site.getString("site_config");
		
		JSONObject jsonConfig = null;
		if (StringUtils.isBlank(siteConfig)) {
			jsonConfig = new JSONObject();
		} else {
			jsonConfig = JsonHelper.getJson(siteConfig);
		}
		jsonConfig.put("member", JsonHelper.getJson(disConfig));
		site.set("site_config", jsonConfig.toString());
		
		docManager.save(site);
		
		//发出站点变化的消息
		PublishTrigger.otherData(siteLibID, siteID, DocIDMsg.TYPE_SITE);
		
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");
			url += "&DocIDs=" + siteID + "&DocLibID=" + siteLibID;
		return "redirect:" + url;
	}
}
