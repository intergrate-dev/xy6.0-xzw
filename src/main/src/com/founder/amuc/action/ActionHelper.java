package com.founder.amuc.action;

import javax.servlet.http.HttpServletRequest;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.amuc.tenant.TenantManager;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.sys.SysConfigReader;

/**
 * @author leijj
 * @date 2014-9-10
 * Description:
 */
public class ActionHelper {
	public static int getDocID(HttpServletRequest request) throws Exception{
		int docID = 0;
		String ruleFormula = request.getParameter("RuleFormula");
		if(ruleFormula.indexOf("maActionID_EQ_") >= 0){
			ruleFormula = ruleFormula.substring(ruleFormula.indexOf("maActionID_EQ_"),ruleFormula.indexOf("_SPC__AND_@ACTIONMEMBERDEPTS@"));
		}
		if(ruleFormula != null && ruleFormula.startsWith("maActionID_EQ_")){
			String[] ruleArray = ruleFormula.split("_EQ_");
			if(ruleArray != null && ruleArray.length == 2)
				docID = Integer.valueOf(ruleArray[1]);//获取活动id
		}
		if(docID == 0)
			throw new Exception("获取活动数据错误");
		
		return docID;
	}
	
	/**
	 * 获取系统配置的参数值
	 * @param proname  项目名
	 * @param subname  条目名
	 * @return
	 * @throws E5Exception
	 */
	public static String getSysPara(String proname,String subname) throws E5Exception{
		int appID = 1;
		SysConfigReader configReader = (SysConfigReader)Context.getBean(SysConfigReader.class);
		String para_name = configReader.get(appID, proname, subname);
		return para_name;
	}
	
	/**
	 * 根据活动id获取需要的活动字段
	 * @param tenantCode
	 * @param aid
	 * @return
	 * @throws E5Exception
	 */
	public static Document getActByAid(String tenantCode,String aid) throws E5Exception{
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ACTION,tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		String[] column = {"aScoreLimit","aApplyTable","aName","aActionMode"};
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[]{aid},column);
		if(docs != null && docs.length > 0){
			return docs[0];
		}
		return null;
	}
	
	/**
	 * 根据活动表单id获取需要的活动表单需要的字段
	 * @param tenantCode
	 * @param aid
	 * @return
	 * @throws E5Exception
	 */
	public static Document getActTabByTid(String tenantCode,String tid,String[] column) throws E5Exception{
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_ENROLLTEMPLATE,tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[]{tid},column);
		if(docs != null && docs.length > 0){
			return docs[0];
		}
		return null;
	}
	
	/**
	 * 根据活动会员id获取需要的活动会员需要的字段
	 * @param tenantCode
	 * @param aid
	 * @return
	 * @throws E5Exception
	 */
	public static Document getActMemberByAMid(String tenantCode,String amid,String[] needcol) throws E5Exception{
		if (tenantCode == null) tenantCode = TenantManager.DEFAULTCODE;
		
		DocLib docLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERACTION,tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String condition = "SYS_DELETEFLAG=0 and SYS_DOCUMENTID=?";
		Document[] docs = docManager.find(docLib.getDocLibID(), condition, new Object[]{amid},needcol);
		if(docs != null && docs.length > 0){
			return docs[0];
		}
		return null;
	}
}