package com.founder.amuc.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.config.ConfigReader;
import com.founder.amuc.config.SubTab;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.controller.DocViewController;

/**
 * 细览的统一入口，将分发到各自的细览页
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/amuc")
@SuppressWarnings({"rawtypes", "unchecked"})
public class AmucViewController extends DocViewController{
	
	@RequestMapping("/View.do")
	public ModelAndView entry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		int docLibID = getInt(request, "DocLibID");
		long docID = getInt(request, "DocIDs");
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		List<SubTab> tabs = ConfigReader.getViewTabs(docTypeID);
		int folderID = DomHelper.getFVIDByDocLibID(docLibID);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("tabs", tabs);	
		model.put("DocLibID", docLibID);
		model.put("DocIDs", docID);
		model.put("FVID", folderID);
		
		//缺省：显示操作记录
		/*
		if (tabs == null || tabs.size() == 0) {
			response.sendRedirect("../e5workspace/manoeuvre/FlowRecordList.do?" + request.getQueryString());
			return;
		}*/
		String viewName = "";
		
		if (tabs == null || tabs.size() == 0) {
			viewName = "redirect:../e5workspace/manoeuvre/FlowRecordList.do?" + request.getQueryString();
		}
		else {
			viewName = "/amuc/View";
		}
		
		return new ModelAndView(viewName, model);
	}
	
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		int docLibID = getInt(request, "DocLibID");
		long docID = getInt(request, "DocIDs");
		int docTypeID = DomHelper.getDocTypeIDByLibID(docLibID);
		
		//若是群会员或活动成员，则应该细览时显示的是会员本身的细览
		if (docTypeID == InfoHelper.getTypeIDByCode(Constant.DOCTYPE_MEMBERGROUP)
				|| docTypeID == InfoHelper.getTypeIDByCode(Constant.DOCTYPE_MEMBERACTION)) {
			DocumentManager docManager = DocumentManagerFactory.getInstance();
			Document doc = docManager.get(docLibID, docID);
			
			docID = doc.getLong("mgMemberID"); //群会员
			if (docID < 0)
				docID = doc.getLong("maMemberID");//活动成员
			
			docTypeID = InfoHelper.getTypeIDByCode(Constant.DOCTYPE_MEMBER);
			docLibID = InfoHelper.getLibID(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));
		}
		int folderID = DomHelper.getFVIDByDocLibID(docLibID);
		
		List<SubTab> tabs = ConfigReader.getViewTabs(docTypeID);
		
		//缺省：显示操作记录
		if (tabs == null || tabs.size() == 0) {
			response.sendRedirect("../e5workspace/manoeuvre/FlowRecordList.do?" + request.getQueryString());
			return;
		}
		
		model.put("tabs", tabs);	
		model.put("DocLibID", docLibID);
		model.put("DocIDs", docID);
		model.put("FVID", folderID);
	}
	
	protected void setTabsURL(HttpServletRequest request,List<SubTab> tabs){
		String webroot = WebUtil.getRoot(request);
		
		for (SubTab sub : tabs) {					
			String url = sub.getUrl();
			//设置过，则退出
			if (url.startsWith(webroot)) 
				break;
			
			if(!StringUtils.isBlank(url)){
				sub.setUrl(webroot+(url.startsWith("/") ? url.replaceFirst("/", "") : url));
			}
			setSubTabURL(webroot,sub);
		}
	}
	
	protected void setSubTabURL(String webroot,SubTab tab){
		List<SubTab> subs = tab.getChildren();
		if (subs != null){
			for (SubTab sub : subs) {
				String url = sub.getUrl();
				if (!StringUtils.isBlank(url)){
					sub.setUrl(webroot + (url.startsWith("/") ? url.replaceFirst("/", "") : url));
				}
				setSubTabURL(webroot,sub);
			}
		}
	}
}
