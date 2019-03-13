package com.founder.amuc.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;



import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.founder.e5.cat.Category;
//import com.founder.amuc.commons.InfoHelper;
//import com.founder.amuc.config.SubTab;
//import com.founder.amuc.config.Tab;
import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.queryForm.QueryForm;
import com.founder.e5.web.DomInfo;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.GroupManager;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.config.SubTab;
import com.founder.xy.workspace.MainHelper;
import com.founder.xy.workspace.service.ListProcService;

import net.sf.json.JSONArray;
import org.json.JSONObject;

/**
 * 数据列表界面入口：取文档类型的列表方式、查询条件
 * 
 * url：amuc/Main.do
 * 
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/amuc")
public class MainController {
	@Autowired
	ListProcService listProcService;
	@Autowired
	private GroupManager groupManager;

	/**
	 * 主界面：不带左边导航树的
	 */
	//@RequestMapping(value = { "MainSimple.do" })
	@RequestMapping("/MainSimple.do")
	public ModelAndView mainSimple(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/amuc/MainSimple", model);
		
	}
	
	@RequestMapping("/MainOrdersStatic.do")
	public ModelAndView MainStatic(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/amuc/dataMining/OrderStatic", model);
		
	}
	@RequestMapping("/MainPcardStatic.do")
	public ModelAndView MainPcardStatic(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);
		return new ModelAndView("/amuc/dataMining/PaperCardDocStatic", model);
		
	}
	
	/**
	 * 主界面：邀请码
	 */
	@RequestMapping(value = { "MainInvite.do" })
	public ModelAndView mainRevoke(HttpServletRequest request) throws Exception {

		Map<String, Object> model = fillChannelModel(request);

		return new ModelAndView("/amuc/MainInvite", model);
	}
	/**
	 * 详情界面：邀请码记录
	 */
	@RequestMapping(value = { "MainInviteLog.do" })
	public ModelAndView mainInviteLog(HttpServletRequest request) throws Exception {

		Map<String, Object> model = fillChannelModel2(request);

		return new ModelAndView("/amuc/MainInviteLog", model);
	}
	/**
	 * 详情界面：报卡
	 */
	@RequestMapping(value = { "MainPcard.do" })
	public ModelAndView MainPcard(HttpServletRequest request) throws Exception {

		return new ModelAndView("/amuc/MainPcard");
	}
	/**
	 * 详情界面：支付配置管理
	 */
	@RequestMapping(value = { "MainPayConfig.do" })
	public ModelAndView MainPayConfig(HttpServletRequest request) throws Exception {

		return new ModelAndView("/amuc/ChargeConfig");
	}
	//
	private Map<String, Object> fillChannelModel(HttpServletRequest request)
			throws Exception {
		Map<String, Object> model = MainHelper.fillMainModel(request);
		//规则树开始
		SubTab curTab = (SubTab) model.get("subTab");
		String code = curTab.getDocTypeCode(); // 分类类型的常量定义成与对应的文档类型编码一样

		int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
		DocTypes docType = Enum.valueOf(DocTypes.class, code);

		int siteID = MainHelper.getSiteEnable(request);
		// 若是扩展字段，需要显示“挂件栏目”操作
		boolean isExtField = code.equals("EXTFIELD");

		model.put("catTypeID", catTypeID);
		model.put("isExtField", isExtField);
		model.put("groupField", docType.groupField());
		model.put("siteField", docType.siteField());
		//规则树结束

		Channel[] chs = ConfigReader.getChannels();
		Map<String, Object> model0 = new HashMap<String, Object>();
		Map<String, Object> model1 = new HashMap<String, Object>();
		List docLibs = new ArrayList();
		model0.put("code","Web");
		model0.put("id","0"); 
		model0.put("name","企业邀请码"); 
		model1.put("code","App"); 
		model1.put("id","1"); 
		model1.put("name","个人邀请码"); 
		docLibs.add(model0);
		docLibs.add(model1);

		DocLib[] channelLibs2 = channelArticleLibs(request);

		if (channelLibs2 != null) {
			model.put("channels", docLibs);
			
			model.put("channelLib0", channelLibs2[0]);
			model.put("channelLib1", channelLibs2[0]);
			model.put("ruleFormula0", "_SPC_icInviterID_EQ_-1_SPC__AND__SPC_m_siteID_EQ_"+siteID+"_SPC_");
			model.put("ruleFormula1", "_SPC_icInviterID_GT_-1_SPC__AND__SPC_m_siteID_EQ_"+siteID+"_SPC_");
		}
		return model;
	}
	//
	private Map<String, Object> fillChannelModel2(HttpServletRequest request)
			throws Exception {
		String docID = request.getParameter("DocIDs");
		Map<String, Object> model = MainHelper.fillMainModel(request);
		Channel[] chs = ConfigReader.getChannels();
		Map<String, Object> model0 = new HashMap<String, Object>();
		Map<String, Object> model1 = new HashMap<String, Object>();
		List docLibs = new ArrayList();
		model0.put("code","Web");
		model0.put("id","0"); 
		model0.put("name","企业邀请码"); 
		model1.put("code","App"); 
		model1.put("id","1"); 
		model1.put("name","个人邀请码"); 
		docLibs.add(model0);
		docLibs.add(model1);

		DocLib[] channelLibs = channelArticleLibs2(request);
		String str = "";
		if(StringUtils.isBlank(docID)){
			str="SYS_DELETEFLAG_EQ_0";
		}else{
			str = "SYS_DELETEFLAG_EQ_0_SPC__AND__SPC_icCodeID_EQ_" + docID;
		}
		if (channelLibs != null) {
			model.put("channels", docLibs);			
			model.put("channelLib0", channelLibs[0]);
			model.put("channelLib1", channelLibs[0]);
			model.put("ruleFormula0", str);
			model.put("ruleFormula1", str);
		}
		return model;
	}
	/**
	 * 
	 * 
	 * @return
	 */
	private DocLib[] channelArticleLibs(HttpServletRequest request) {
		String tenantCode = InfoHelper.getTenantCode(request);

		int docTypeID = DocTypes.MEMBERINVITECODE.typeID();
		try {
			List<DocLib> docLibs = LibHelper.getLibs(docTypeID, tenantCode);
			return docLibs.toArray(new DocLib[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private DocLib[] channelArticleLibs2(HttpServletRequest request) {
		String tenantCode = InfoHelper.getTenantCode(request);

		int docTypeID = DocTypes.MEMBERINVITECODELOG.typeID();
		try {
			List<DocLib> docLibs = LibHelper.getLibs(docTypeID, "xy");
			return docLibs.toArray(new DocLib[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 取列表列的操作
	 */
	@RequestMapping(value = "ListProcs.do")
	public void listProcs(HttpServletRequest request,
			HttpServletResponse response, @RequestParam int docLibID,
			@RequestParam int fvID, @RequestParam int opFlow) throws Exception {
		int roleID = ProcHelper.getRoleID(request);
		String procs = listProcService.getProcs(roleID, docLibID, fvID, opFlow);

		InfoHelper.outputJson(procs, response);
	}
	
	/**
	 * 主界面：带分组的
	 */
	@RequestMapping(value = { "MainGroup.do" })
	public ModelAndView mainGroup(HttpServletRequest request) throws Exception {

		Map<String, Object> model = MainHelper.fillMainModel(request);

		SubTab curTab = (SubTab) model.get("subTab");
		String code = curTab.getDocTypeCode(); // 分类类型的常量定义成与对应的文档类型编码一样

		int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code)).typeID();
		DocTypes docType = Enum.valueOf(DocTypes.class, code);

		int siteID = MainHelper.getSiteEnable(request);
		// 若是扩展字段，需要显示“挂件栏目”操作
		boolean isExtField = code.equals("EXTFIELD");
		Category[] AllGroups = InfoHelper.getCatGroups(request, catTypeID, siteID);
		//@wenkx 增加分组权限
		if("TEMPLATE".equals(code) || "SPECIAL".equals(code) || "PHOTO".equals(code)) {
			AllGroups = groupManager.getGroupsWithPower(request, siteID,AllGroups);
		}
		model.put("groups",AllGroups );

		model.put("catTypeID", catTypeID);
		model.put("isExtField", isExtField);
		model.put("groupField", docType.groupField());
		model.put("siteField", docType.siteField());

		return new ModelAndView("/amuc/MainInvite", model);
	}
}
