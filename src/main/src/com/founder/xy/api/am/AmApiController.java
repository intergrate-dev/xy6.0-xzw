package com.founder.xy.api.am;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.founder.e5.context.E5Exception;
import com.founder.e5.web.org.StringValueUtils;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteManager;

/**
 * 与全媒体生产接口的Api
 */
@Controller
@RequestMapping("/api/am")
public class AmApiController {
	@Autowired
	private SiteManager siteManager;
	
	@Autowired
	private ColumnReader colReader;
	
	@Autowired
	private AmApiManager amApiManager;
	
	
	//掌媒登陆ftp返回的信息
	@RequestMapping(method = RequestMethod.POST,value = "Login.do")
	public void mobileLogin(HttpServletRequest request,HttpServletResponse response, int channel){
		String result = amApiManager.getLoginResult();
		InfoHelper.outputText(result, response);
	}
	
	/**
	 * 获取所有站点栏目树结构
	 * type：0 web库栏目树， 1 app库栏目树
	 * @param response
	 * @param type
	 * @throws E5Exception 
	 */
	@RequestMapping(value = "getAllSiteNodeTreeXml.do")
	public void getAllSiteNodeTreeXml(HttpServletResponse response, int channel) throws E5Exception{
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("nodeTree");
		//获取所有站点数据
		int siteLibID = LibHelper.getLibID(DocTypes.SITE.typeID(), Tenant.DEFAULTCODE);
		List<Site> siteList = siteManager.getSites(siteLibID);
		for(int i=0; i<siteList.size(); i++) {
			//创建站点节点元素
			Site site = (Site) siteList.get(i);
			Element siteEle = root.addElement("site");
			siteEle.addAttribute("id", site.getId()+"");
			siteEle.addAttribute("name", site.getName());
			siteEle.addAttribute("type", "site");		
			//获取站点下的所有栏目数据
			int colLibID = LibHelper.getColumnLibID();
			List<Column> subList = colReader.getRoot(site.getId(), colLibID, channel);
			amApiManager.addChildrenNode(siteEle, colLibID, subList, 0);
		}
		InfoHelper.outputText(document.asXML(), response);
	}
	
	/**
	 * WEB投稿
	 * @param request
	 * @param response
	 * @param docxml
	 */
	//传过来的稿件publishStatus=0的时候为未发布，其他值的时候为签发
	@RequestMapping(method = RequestMethod.POST,value = "publish.do")
	public void publish(HttpServletRequest request, HttpServletResponse response, String docxml){
		int status = StringValueUtils.getInt(request.getParameter("publishStatus"), 1);//稿件的发布状态
		String tenantCode = InfoHelper.getTenantCode(request);	
		String result = amApiManager.publish(docxml, tenantCode, status);
		InfoHelper.outputText(result, response);
	}
	
	/**
	 * APP投稿
	 * @param request
	 * @param response
	 * @param articleXmlUrl
	 */
	//传过来的稿件publishStatus=0的时候为未发布，其他值的时候为签发
	@RequestMapping(method = RequestMethod.POST,value = "publishApp.do")
	public void publishApp(HttpServletRequest request,HttpServletResponse response,String articleXmlUrl){
		int status = StringValueUtils.getInt(request.getParameter("publishStatus"), 1);//稿件的发布状态
		String tenantCode = InfoHelper.getTenantCode(request);	
		String result = amApiManager.publishApp(articleXmlUrl, tenantCode, status);
		InfoHelper.outputText(result, response);
	}
	
	//撤稿
	@RequestMapping(method = RequestMethod.POST,value = "revoke.do")
	public void revoke(HttpServletResponse response, String articleId,String docId, int channel){
		String result = "";
		if(channel == 0){
			result = amApiManager.revoke(docId,channel);
		}else {
			result = amApiManager.revoke(articleId,channel);
		}
		
		InfoHelper.outputText(result, response);
	}
	
	@RequestMapping(method = RequestMethod.POST,value = "refresh.do")
	public void reflash(HttpServletResponse response,String articleId,String docId, int channel){
		String result = "";
		if(channel == 0){
			result = amApiManager.refresh(docId,channel);
		}else {
			result = amApiManager.refresh(articleId,channel);
		}
	
		InfoHelper.outputText(result, response);
	}
	//通讯员同步接口
	@RequestMapping(method = RequestMethod.GET,value = "batmanSyn.do")
	public void reflashBatman(HttpServletResponse response) throws ParseException, DocumentException, E5Exception, URISyntaxException, MalformedURLException{
		String result=amApiManager.batman();
		
		InfoHelper.outputText(result, response);
	}
	
	
}
