package com.founder.xy.app.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/xy/mobileos")
public class MobileOSController {
	
	@RequestMapping(value = "pushConfig.do")
	public ModelAndView pushConfig(HttpServletRequest request, HttpServletResponse response) throws E5Exception{
		String UUID = WebUtil.get(request, "UUID");
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		Map<String, Object> model = new HashMap<String, Object>();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document os = docManager.get(docLibID, docID);
		String config = os.getString("os_pushConfig");
		if(config != null && !"".equals(config)){
			JSONObject obj = JSONObject.fromObject(config);
			model.put("androidAppID", obj.get("androidAppID"));
			model.put("androidAppKey", obj.get("androidAppKey"));
			model.put("androidMasterSecret", obj.get("androidMasterSecret"));
			model.put("iosAppID", obj.get("iosAppID"));
			model.put("iosApKey", obj.get("iosApKey"));
			model.put("iosMasterSecret", obj.get("iosMasterSecret"));
			model.put("autoBadge", obj.get("autoBadge"));
		}
		model.put("UUID", UUID);
		model.put("DocLibID", docLibID);
		model.put("DocIDs", docID);
		return new ModelAndView("xy/app/PushConfig", model);
	}
	
	@RequestMapping(value = "addPushConfig.do")
	public String addPushConfig(HttpServletRequest request, HttpServletResponse response) throws E5Exception{
		String UUID = WebUtil.get(request, "UUID");
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocIDs", 0);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document os = docManager.get(docLibID, docID);
		JSONObject obj = new JSONObject();
		obj.put("androidAppID", WebUtil.get(request, "androidAppID"));
		obj.put("androidAppKey", WebUtil.get(request, "androidAppKey"));
		obj.put("androidMasterSecret", WebUtil.get(request, "androidMasterSecret"));
		obj.put("iosAppID", WebUtil.get(request, "iosAppID"));
		obj.put("iosApKey", WebUtil.get(request, "iosApKey"));
		obj.put("iosMasterSecret", WebUtil.get(request, "iosMasterSecret"));
		obj.put("autoBadge", WebUtil.get(request, "autoBadge"));
		os.set("os_pushConfig", obj.toString());
		docManager.save(os);
		return "redirect:"+"/e5workspace/after.do?UUID=" + UUID;
	}
}
