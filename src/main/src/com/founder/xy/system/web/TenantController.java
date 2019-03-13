package com.founder.xy.system.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.context.Context;
import com.founder.e5.context.DSManager;
import com.founder.e5.context.E5DataSource;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.org.UserManager;
import com.founder.e5.web.WebUtil;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;

@Controller
@RequestMapping("/xy/tenant")
public class TenantController {
	@Autowired
	private TenantManager tenantManager;
	
	// 部署
	@RequestMapping(value = "Deploy.do")
	public void deploy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int docID = WebUtil.getInt(request, "DocIDs", 0);
		int datasource = WebUtil.getInt(request, "datasource", 1);
		
		try {
			if (!tenantManager.deploy(docLibID, docID, datasource))
				InfoHelper.outputText("租户已经部署，请不要重复操作！", response);
			else
				InfoHelper.outputText("部署完毕", response);
		} catch (Exception e) {
			e.printStackTrace();
			InfoHelper.outputText("部署中异常：" + e.getLocalizedMessage(), response);
		}
	}
	
	// 归档部署
	@RequestMapping(value = "DeployArchive.do")
	public void deployArchive(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int docID = WebUtil.getInt(request, "DocIDs", 0);
		int datasource = WebUtil.getInt(request, "datasource", 2);
		
		if (docLibID == 0) {
			String tenantCode = Tenant.DEFAULTCODE;
			Tenant t = tenantManager.get(tenantCode);
			docLibID = LibHelper.getLibID(DocTypes.TENANT.typeID(), tenantCode);
			docID = (int)t.getId();
		}
		try {
			if (!tenantManager.deployArchive(docLibID, docID, datasource))
				InfoHelper.outputText("已经完成归档部署，请不要重复操作！", response);
			else
				InfoHelper.outputText("归档部署完毕", response);
		} catch (Exception e) {
			e.printStackTrace();
			InfoHelper.outputText("部署中异常：" + e.getLocalizedMessage(), response);
		}
	}
	
	//部门树的根机构:	若是在系统管理端，且系统内有多个租户，则显示完整的机构树。否则只显示一个租户的机构树。
	//这里处理的是系统管理端，因此若单租户，则只显示默认根机构
	@RequestMapping(value = "Org.do")
	public void org(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int orgID = 1;
		try {
			List<Tenant> ts = tenantManager.getAll();
			if (ts != null && ts.size() > 1)
				orgID = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		InfoHelper.outputText(String.valueOf(orgID), response);
	}
	
	
	/**选择数据源*/
	@RequestMapping("SelectDS.do")
	public ModelAndView selectDataSource(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		String strDocIDs = WebUtil.get(request, "DocIDs");
		DSManager dsManager = (DSManager)Context.getBean(DSManager.class);
		
		E5DataSource[] datasoureces = dsManager.getAll();
		model.put("datasources", datasoureces);
		model.put("datasourceCount", datasoureces.length);
		model.put("DocLibID", docLibID);
		model.put("DocIDs", strDocIDs);
		model.put("UUID", WebUtil.get(request, "UUID"));
		model.put("type", WebUtil.get(request, "type"));
		model.put("siteID", WebUtil.get(request, "siteID"));

		return new ModelAndView("/xy/tenant/SelectDS", model);
	}
	
	/**
	 * 检查用户账号是否已存在
	 */
	@RequestMapping(value = "Exist.do")
	public void exist(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
		UserManager userManager = (UserManager)Context.getBean(UserManager.class);

		String flag;
		if (code.endsWith("admin")) //不允许以admin为后缀
			flag = "1";
		else if(userManager.getUserByCode(code) != null)
			flag = "1";
		else
			flag = "0";
		
		InfoHelper.outputText(flag, response);
	}
	
	/**
	 * 删除选中的租户
	 */
	@RequestMapping(value = "Delete.do")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
		int docID = WebUtil.getInt(request, "DocIDs", 0);
		
		try{
			Document doc = docManager.get(docLibID,docID);
			int deploy = doc.getInt("te_deployed");
			if( deploy == 1 ){
				InfoHelper.outputText("租户已经部署，请不要删除！", response);
			} else {
				docManager.delete(doc);
				InfoHelper.outputText("@refresh@删除成功！", response);
			}
		}catch(Exception e) {
			e.printStackTrace();
			InfoHelper.outputText("删除出现异常：" + e.getLocalizedMessage(), response);
		}
	}
}
