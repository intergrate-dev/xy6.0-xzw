package com.founder.xy.system.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocType;
import com.founder.e5.dom.DocTypeReader;
import com.founder.e5.dom.queryForm.QueryForm;
import com.founder.e5.web.DataMain;
import com.founder.e5.web.DomInfo;
import com.founder.e5.web.SysUser;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.TenantManager;

/**
 * 租户的“站点管理”操作主界面，继承自DataMain，按租户确定不同的文档库
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy")
public class TenantDataMain extends DataMain{
	@Autowired
	private TenantManager tenantManager;
	
	@Resource(name="DocTypeReader")
	private DocTypeReader docTypeReader;
	
	@RequestMapping(value = {"TenantData.do"})
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int tLibID = getDocLibID(request);
		long tID = getDocID(request);
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document tenant = docManager.get(tLibID, tID);
		
		//判断是否已经部署
		if (tenant.getInt("te_deployed") != 1) {
			InfoHelper.outputText("租户尚未部署！", response);
			return null;
		}
		
		if (getUserInfo(request) == null) {
			SysUser user = getAdminUser(request);
			request.getSession().setAttribute(SysUser.sessionName, user);
		}
		
		String tenantCode = tenant.getString("te_code").toLowerCase();
		String type = get(request, "type");
		
		DomInfo domInfo = getDomInfo(type, tenantCode);
		
		String rule = get(request, "rule");
		if (rule != null) {
			if (rule.indexOf("@DOCID@") >= 0) {
				rule = rule.replaceAll("@DOCID@", get(request, "DocIDs"));
			}
			domInfo.setRule(rule);
		}
		changeListpage(domInfo, request);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("noOp", get(request, "noOp"));
		model.put("domInfo", domInfo);
		model.put("extParams", get(request, "extParams"));
		
		return new ModelAndView("/e5sys/DataMain", model);
	}
	/**
	 * 根据一个文档类型，读取其文档类型ID、文档库ID、文件夹ID信息
	 * @param docType
	 * @return
	 * @throws E5Exception
	 */
	protected DomInfo getDomInfo(String type, String tenantCode) throws E5Exception {
		DomInfo domInfo = new DomInfo();
		
		DocType docType = docTypeReader.getByCode(type);
		
		domInfo.setDocTypeID(docType.getDocTypeID());
		domInfo.setRule("");
		domInfo.setName(docType.getDocTypeName());//文档类型名，可能在界面上作为title
		
		//读文档库和文件夹
		DocLib docLib = LibHelper.getLib(docType.getDocTypeID(), tenantCode);
		domInfo.setDocLibID(docLib.getDocLibID());
		domInfo.setFolderID(docLib.getFolderID());
		
		//读缺省列表方式（文档类型的第一个）
		int listID = DomHelper.getListID(docType.getDocTypeID());
		domInfo.setListID(listID);
		domInfo.setListIDs(String.valueOf(listID));
		
		//读缺省查询条件（文档类型的第一个）
		QueryForm query = DomHelper.getQuery(docType.getDocTypeID());
		if (query != null) {
			domInfo.setQueryID(query.getId());
			domInfo.setQueryScripts(StringUtils.split(query.getPathJS(), ","));
		}
		
		return domInfo;
	}
}
