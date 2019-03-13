package com.founder.xy.system.site.web;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SecurityHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.system.site.DomainDir;
import com.founder.xy.system.site.DomainDirManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;
import com.founder.xy.system.site.SiteRuleManager;



/**
 * 站点管理相关界面功能
 * @author Deng Chaochen
 */
@Controller
@RequestMapping("/xy/site")
public class SiteController {
	
	@Autowired
	private SiteManager siteManager;
	@Autowired
	private SiteRuleManager siteRuleManager;
	
	@Autowired
	private DomainDirManager domainDirManager;
	
	//1.为网站目录以及网站规则界面加载站点列表。
	//2.根据type来判断加载的是哪个界面：dir为网站目录，rule为网站规则
	@RequestMapping(value = "Site.do")
	public ModelAndView webStruct(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String type) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		
		String tenantCode = InfoHelper.getTenantCode(request);
		
		Document[] docs = siteManager.getSites(tenantCode);
		int docLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), tenantCode);
		
		List<Site> sites = new ArrayList<Site>();
		for (int j = 0; j < docs.length; j++) {
			Object idObj = docs[j].get("SYS_DOCUMENTID");
			Object nameObj = docs[j].get("site_name");
			
			Site s = new Site(nameObj.toString(), Integer.valueOf(idObj.toString()));
			sites.add(s);
		}
		
		model.put("siteLibID", docLibID);
		model.put("sites", sites);
		model.put("type", type);
			
		return new ModelAndView("/xy/site/Site", model);
	}
	
	//站点查找功能
	@RequestMapping(value = "Find.do")
	public void find(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String q) throws Exception {
		
		String tenantCode = InfoHelper.getTenantCode(request);
		Document[] sites = siteManager.find(tenantCode, q);
		
		String result = json(sites);
		InfoHelper.outputJson(result, response);
	}
	
	//站点删除：检查是否有栏目
	@RequestMapping(value = "DeleteSite.do")
	public String deleteSite(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuilder result = new StringBuilder();
		int siteLibID = WebUtil.getInt(request, "DocLibID", 0);
		int siteID = Integer.parseInt(request.getParameter("DocIDs"));
		boolean bContain =siteManager.containCols(siteLibID,siteID);
		if(bContain){
			result.append("本站点下已经创建栏目，不可删除");
		}else{
			String error = siteManager.deleteSite(siteLibID,siteID);
			if (error == null){
				result.append("删除成功");
			}else{
				result.append("删除失败");
			}	
		}
		PublishTrigger.otherData(siteLibID, siteID, DocIDMsg.TYPE_SITE);
		
		InfoHelper.outputText("@refresh@"+result.toString(), response);
		return null;
	}
	
	/**
	 * 删除选中的发布规则
	 */
	@RequestMapping(value = "DeleteRule.do")
	public void deleteRule(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuilder result = new StringBuilder();
		int ruleLibID = WebUtil.getInt(request, "DocLibID", 0);
		long[] ruleIDs = StringUtils.getLongArray(request.getParameter("DocIDs"));
		
		boolean bHasUsed = false;
		try{
			for (long ruleId : ruleIDs) {	
				if (siteRuleManager.ruleUsed(ruleLibID, ruleId)){
					result.append(siteRuleManager.getRuleNameByID(ruleId)).append(",");
					bHasUsed = true;
				} else {
					siteRuleManager.deleteSiteRule(ruleId);
				}
			}
			//发布规则被删除，不必刷新缓存。不会影响系统内任何操作
			//PublishTrigger.otherData(ruleLibID, ruleIDs[0], DocIDMsg.TYPE_SITERULE);
			
			if (bHasUsed){
				result.deleteCharAt(result.toString().lastIndexOf(","));
				result.append(" 被引用，不可删除");
			}
			InfoHelper.outputText("@refresh@"+result.toString(), response);	//操作成功		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清理未被使用的发布规则
	 */
	@RequestMapping(value = "DeleteRules.do")
	public void deleteRules(HttpServletRequest request, @RequestParam String docIDs, HttpServletResponse response) throws Exception {
		try {
			long[] ruleIDs = StringUtils.getLongArray(docIDs);
			for (long ruleId : ruleIDs) {	
				siteRuleManager.deleteNotUsedRule(ruleId);	
			}
		
			InfoHelper.outputText("ok", response);	
			
		} catch (Exception e) {
			InfoHelper.outputText(e.getLocalizedMessage(), response);
		}
	}

	/** 表单保存。检查站点个数 */
	@RequestMapping(value = "FormSubmit.do")
	public String formSubmit(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> model) throws Exception {

		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		long docID = WebUtil.getLong(request, "DocID", 0);
		
		//对于新建的情况，判断授权
		if (docID == 0) {
			int siteCount = siteManager.getSiteCount(docLibID);
			if (siteCount >= SecurityHelper.getSiteCount()) {
				model.put("error", "已超站点数");
				return "/xy/site/error";
			}
		}
		
		Pair changed = null;
		FormSaver formSaver = (FormSaver) Context.getBean(FormSaver.class);
		try {
			if (docID == 0) {
				docID = formSaver.handle(request);
				DocumentManager docManager = DocumentManagerFactory.getInstance();
				Document site = docManager.get(docLibID, docID);
				site.set("site_config", siteDefalutConfig());
				docManager.save(site);
			} else {
				changed = formSaver.handleChanged(request);
			}
		} catch (Exception e) {
			model.put("error", e.getLocalizedMessage());
			return "/xy/site/error";
		}
		
		//返回
		return returnUrl(request, docID, changed);
	}
	
	/** 站点地图配置 */
	@RequestMapping(value = "Seo.do")
	public ModelAndView seo(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int siteLibID = WebUtil.getInt(request, "DocLibID", 0);
		long siteID = WebUtil.getLong(request, "DocIDs", 0);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		
		//site_seo:  {dirWeb:"", dirApp:""}
		String config = site.getString("site_seo");
		if (!StringUtils.isBlank(config)) {
			JSONObject json = JSONObject.fromObject(config);
			String dirWeb = json.containsKey("dirWeb") ? json.getString("dirWeb") : "";
			String dirApp = json.containsKey("dirApp") ? json.getString("dirApp") : "";
			
			model.put("dirWeb", dirWeb);
			model.put("dirApp", dirApp);
		}
		
		int domainLibID = LibHelper.getLibIDByOtherLib(DocTypes.DOMAINDIR.typeID(), siteLibID);
		List<DomainDir> dirs = domainDirManager.getDomains(domainLibID, siteID);
		
		model.put("dirs", dirs);
		model.put("onlyApp", ConfigReader.onlyApp());
		model.put("onlyWeb", ConfigReader.onlyWeb());
		
		model.put("DocLibID", siteLibID);
		model.put("DocID", siteID);
		model.put("UUID", request.getParameter("UUID"));
		
		
		return new ModelAndView("/xy/site/Seo", model);
	}
	
	/** 站点地图配置提交 */
	@RequestMapping(value = "SeoSubmit.do")
	public String seoSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int siteLibID = WebUtil.getInt(request, "DocLibID", 0);
		long siteID = WebUtil.getLong(request, "DocID", 0);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document site = docManager.get(siteLibID, siteID);
		
		//site_seo:  {dirWeb:"", dirApp:""}
		JSONObject json = new JSONObject();
		String config = WebUtil.get(request, "dirWeb");
		if (!StringUtils.isBlank(config)) json.accumulate("dirWeb", config);
		
		config = WebUtil.get(request, "dirApp");
		if (!StringUtils.isBlank(config)) json.accumulate("dirApp", config);
		
		site.set("site_seo", json.toString());
		docManager.save(site);
		
		return returnUrl(request, siteID, null);
	}
	
	//查找结果的json，格式为[{key,value},{key,value},...]
	private String json(Document[] sites) throws E5Exception {
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		for (Document site : sites) {
			if (result.length() > 1) result.append(",");
			
			result.append("{\"value\":\"").append(InfoHelper.filter4Json(site.getString("site_name")))
				.append("\",\"key\":\"").append(String.valueOf(site.getDocID()))
				.append("\"}");
		}
		result.append("]");
	
		return result.toString();
	}
	
	private String siteDefalutConfig(){
		JSONObject config = new JSONObject() ;
		JSONObject discuss = new JSONObject() ;
		discuss.put("auditType", "0");
		discuss.put("showCount", true);
		discuss.put("showPic", false);
		discuss.put("showAnonymous", false);
		discuss.put("showDebase", false);
		discuss.put("defaultTitle", "评论");
		discuss.put("defaultHint", "来说两句吧");
		discuss.put("countHot", "3");
		discuss.put("countNew", "5");
		discuss.put("countReply", "3");
		discuss.put("defaultIcon", "");
		discuss.put("defaultName", "");
		discuss.put("styleColor", "blue");
		config.put("discuss", discuss);
		return config.toString();
	}
	
	/**
	 * 返回的url，调用after.do
	 */
	private String returnUrl(HttpServletRequest request, long docID, Pair changed) throws Exception {
		String url = "redirect:/e5workspace/after.do?UUID=" + request.getParameter("UUID")
				+ "&DocIDs=" + docID;
		if (changed != null)
			url += "&Opinion=" + URLEncoder.encode(changed.getStringValue(), "UTF-8");
		return url;
	}
}
