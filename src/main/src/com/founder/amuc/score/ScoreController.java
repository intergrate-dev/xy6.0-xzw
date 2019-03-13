package com.founder.amuc.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.founder.amuc.commons.Constant;
import com.founder.amuc.commons.InfoHelper;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.web.BaseController;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormParam;
import com.founder.e5.workspace.app.form.FormViewer;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.commons.JsonHelper;

/**
 * 积分相关处理：规则启用、规则停用、规则复制、取全部规则、异常积分置为有效、异常积分置为无效等
 * @author Gong Lijie
 * 2014-6-3
 */
@Controller
@RequestMapping("/amuc")
@SuppressWarnings("rawtypes")
public class ScoreController extends BaseController{
	
	@Override
	protected void handle(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
	}

	// 取出所有的规则，用于手工添加积分时
	@RequestMapping(value = { "score/Rules.do" })
	private void rules(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		String siteId = request.getParameter("siteID");
		int siteID = 1;
		if(!StringUtils.isBlank(siteId)){
			siteID = Integer.parseInt(siteId);
		}
		//取得租户代号
		String tenantCode = InfoHelper.getTenantCode(request);
		//取出规则
		int docLibID = InfoHelper.getLibID(Constant.DOCTYPE_SCORERULE, tenantCode);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(docLibID, "SYS_DELETEFLAG=0 and srStatus=1 and m_siteID = " + siteID, null);
		
		List<Pair> ps = new ArrayList<Pair>();
		Pair blank =  new Pair("", "");
		ps.add(0, blank);
		
		for (Document doc : docs) {
			//规则ID,积分,经验值,规则类型,数据来源,数据来源ID,行为类型,行为类型ID
			StringBuffer value = new StringBuffer();
			value.append(doc.getDocID())
				.append(",").append(doc.getInt("srScore"))
				.append(",").append(doc.getInt("srExperience"))
				.append(",").append(doc.getInt("srType"))
				.append(",").append(doc.getString("srSource"))
				.append(",").append(doc.getInt("srSource_ID"))
				.append(",").append(doc.getString("srEventType"))
				.append(",").append(doc.getInt("srEventType_ID"))
				;
			Pair data =  new Pair(value.toString(), doc.getString("srName"));
			ps.add(data);
		}
		String result = JSONArray.fromObject(ps).toString();
		output(result, response);
	}
	
	@RequestMapping(value = { "score/Enable.do" })
	private void enable(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		int docLibID = getInt(request, "DocLibID");
		long[] docIDs = StringUtils.getLongArray(get(request, "DocIDs"));
		
		//按租户代号取出积分记录表
		String tenantCode = InfoHelper.getTenantCode(request);
		ScoreManager scoreManager = new ScoreManager();
		boolean ok = scoreManager.enableUnusual(tenantCode, docLibID, docIDs);
		
		String url = "../../e5workspace/after.do?DocIDs=" + get(request, "DocIDs") + "&UUID=" + get(request, "UUID");
		if (!ok)
			url = "../../e5workspace/after.do?UUID=" + get(request, "UUID");
		
		//调用AfterProcess，使异常积分记录的状态变化
		response.sendRedirect(url);
	}
	
	@RequestMapping(value = { "score/Disable.do" })
	private void disable(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		int docLibID = getInt(request, "DocLibID");
		long[] docIDs = StringUtils.getLongArray(get(request, "DocIDs"));
		
		ScoreManager scoreManager = new ScoreManager();
		boolean ok = scoreManager.disableUnusual(docLibID, docIDs);
			
		//调用AfterProcess，使异常积分记录的状态变化
		String url = "../../e5workspace/after.do?DocIDs=" + get(request, "DocIDs") + "&UUID=" + get(request, "UUID");
		if (!ok)
			url = "../../e5workspace/after.do?UUID=" + get(request, "UUID");
		response.sendRedirect(url);
	}

	@RequestMapping(value = { "score/RuleStart.do" })
	private void start(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		_set(request, response, 1);
	}
	
	@RequestMapping(value = { "score/RuleStop.do" })
	private void stop(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		_set(request, response, 0);
	}
	private void _set(HttpServletRequest request,
			HttpServletResponse response, int result) throws Exception {
		int docLibID = getInt(request, "DocLibID");
		int docID = getInt(request, "DocIDs");
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		doc.set("srStatus", result);
		docManager.save(doc);
		
		String url = "../../e5workspace/after.do?DocIDs=" + docID + "&UUID=" + get(request, "UUID");
		response.sendRedirect(url);
	}

	@RequestMapping(value = { "score/RuleCopy.do" })
	private void copy(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		int docLibID = getInt(request, "DocLibID");
		int docID = getInt(request, "DocIDs");
		
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = docManager.get(docLibID, docID);
		
		Document copy = docManager.newDocument(doc, docLibID, InfoHelper.getID(doc.getDocTypeID()));
		ProcHelper.initDoc(copy);
		copy.set("srStatus", 0);
		docManager.save(copy);
		
		LogHelper.writeLog(docLibID, copy.getDocID(), ProcHelper.getUser(request), "复制规则", "复制源ID=" + docID);
		
		output("@refresh@", response);
	}
	
	// 积分扣减：使用与积分添加一样的表单，在前端设置隐藏域（积分标记）为积分扣减（2）
	@RequestMapping(value = { "score/Reduce.do" })
	@SuppressWarnings("unchecked")
	private ModelAndView reduce(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		FormParam param = new FormParam();
		param.setDocID(0);
		param.setDocLibID(Integer.parseInt(request.getParameter("DocLibID")));
		param.setFvID(Integer.parseInt(request.getParameter("FVID")));
		param.setFormCode("FormScore");
		param.setUuid(String.valueOf(Long.parseLong(request.getParameter("UUID"))));

		String content = FormViewer.getFormJsp(param);
		model.put("content", content);
		
		return new ModelAndView("amuc/score/Reduce", model);
	}

	/**
	 * 检查积分是否为负
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(value = { "score/CheckSE.do" })
	private void checkSE(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {
		
		String mbn = get(request, "mbn"); //固定参数名
		String result = "false";
		DocLib relLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(relLib.getDocLibID(), "mName = ? and SYS_DELETEFLAG=0", new Object[]{mbn});
		if (docs != null && docs.length > 0) {
			if(docs[0].getInt("mScore") <= 0){
				result = "true";
			}else{
				result = "false";
			}
		}
		output(result.toString(), response);
	}
	
	@RequestMapping(value = { "score/ScoreLine.do" })
	private void ScoreLine(HttpServletRequest request,
			HttpServletResponse response, Map model) throws Exception {		
		String msScore = get(request, "msScore"); //固定参数名
		if(StringUtils.isBlank(msScore.toString())){
			return;
		}

		String refererUrl = request.getHeader("referer");
		String siteID = refererUrl.substring(refererUrl.lastIndexOf("=") + 1);
		DocLib relLib = InfoHelper.getLib(Constant.DOCTYPE_SITE);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] docs = docManager.find(relLib.getDocLibID(), "SYS_DOCUMENTID = ? and SYS_DELETEFLAG=0", new Object[]{siteID});
		String result ="false";
		String siteConfig = "";
		int scoreline = 0;
		if (docs != null && docs.length > 0) {
			siteConfig = docs[0].getString("site_config");
			if (!StringUtils.isBlank(siteConfig)) {
				JSONObject jsonConfig = JsonHelper.getJson(siteConfig);
				if (jsonConfig.has("member") 
						&& jsonConfig.getJSONObject("member").has("guardLine")) {
					scoreline = jsonConfig.getJSONObject("member").getInt("guardLine");
					System.out.println(scoreline);
				}
			}
		}
		
		if(Integer.parseInt(msScore) > scoreline){
			result = "true";
		}
		
		if(scoreline == 0){
			result = "false";
		}
		
		output(result.toString(), response);
	}
}
