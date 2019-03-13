package com.founder.xy.stat.web;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.stat.StatManager;

/**
 * 我的工作量的相关操作
 */
@Controller
@RequestMapping("/xy/stat")
@SuppressWarnings("unchecked")
public class StatController {
	@Autowired
	private StatManager statManager;
	
	/**
	 * 我的工作量统计页面初始化
	 */
	@RequestMapping(value = "MyStat.do")
	public ModelAndView myStat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
 		//读统计数据
		int userID = ProcHelper.getUser(request).getUserID();
		int siteID = Integer.valueOf(request.getParameter("siteID"));
		String tCode = InfoHelper.getTenantCode(request);
		
		Map<String, Object> model = statManager.myloadInit(userID, siteID, tCode);
		
        return new ModelAndView("xy/stat/Stat", model);
	}
	
	/**
	 * 我的工作量统计页面检索
	 */
	@RequestMapping(value = "Search.do")
	public ModelAndView myStatSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> map = statManager.myloadSearch(request);
		
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("detailMapList", map.get("detailMapList")); // 工作量分条件细览
        model.put("count", map.get("count"));
        model.put("discuss", map.get("discuss"));
        model.put("click", map.get("click"));
        // 返回ajax json
        return new ModelAndView("", model);
	}
	
	/**
	 * 工作量统计页面初始化
	 */
	@RequestMapping(value = "Workload.do")
	public ModelAndView workload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String siteID = request.getParameter("siteID");
		Map<String, Object> model = statManager.workloadInit(Integer.valueOf(siteID), InfoHelper.getTenantCode(request));
		
        model.put("siteID", siteID);
        model.put("channels", ConfigReader.getChannels());
        // 返回ajax
        return new ModelAndView("xy/stat/Workload", model);
	}

	/**
	 * 工作量统计页面检索
	 */
	@RequestMapping(value = "WorkloadSearch.do")
	public ModelAndView workloadSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = statManager.workloadSearch(request);
		
        // 返回ajax
        return new ModelAndView("", model);
	}
	
	/**
	 * 稿件统计页面初始化
	 */
	@RequestMapping(value = "ArticleStat.do")
	public ModelAndView articleStat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String siteID = request.getParameter("siteID");
		Map<String, Object> model = statManager.articleInit(siteID, InfoHelper.getTenantCode(request));

        model.put("channels", ConfigReader.getChannels());
        model.put("siteID", siteID);
       
        // 返回ajax
        return new ModelAndView("xy/stat/ArticleStat", model);
	}
	/**
	 * 发稿量统计页面检索
	 */
	@RequestMapping(value = "ArticleBatmanSearch.do")
	public ModelAndView articleBatmanStatSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = statManager.articleBatmanSearch(request);
        
        // 返回ajax
        return new ModelAndView("", model);
	}
	/**
	 * 发稿量统计页面初始化
	 */
	@RequestMapping(value = "ArticleBatmanStat.do")
	public ModelAndView articleBatmanStat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String siteID = request.getParameter("siteID");
		Map<String, Object> model = statManager.articleBatmanInit(siteID, InfoHelper.getTenantCode(request));

        model.put("channels", ConfigReader.getChannels());
        model.put("siteID", siteID);
       
        // 返回ajax
        return new ModelAndView("xy/stat/ArticleBatmanStat", model);
	}
	/**
	 * 稿件统计页面检索
	 */
	@RequestMapping(value = "ArticleSearch.do")
	public ModelAndView articleStatSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map<String, Object> model = statManager.articleSearch(request);
        
        // 返回ajax
        return new ModelAndView("", model);
	}
	
	/**
	 * 工作量统计页面初始化
	 */
	@RequestMapping(value = "Column.do")
	public ModelAndView column(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = statManager.colStatInit(InfoHelper.getTenantCode(request));
        // 返回ajax
        return new ModelAndView("xy/stat/ColumnStat", model);
	}
	
	/**
	 * 来源autocomplete
	 */
	@RequestMapping(value = "Find.do")
	public void findSource(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID, @RequestParam String q) throws Exception {
		
		Document[] docs = statManager.find(siteID, q);
		String result = json(docs);
		InfoHelper.outputJson(result, response);
	}
	
	/**
	 *  csv导出
	 */
	@RequestMapping(value = "outputcsv.do")
	public ModelAndView outputcsv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取参数
		String data = request.getParameter("jsonData");
		if(null == data || "".equals(data)){
			data = "[]";
		}
		List<JSONObject> dataArray = (List<JSONObject>) JSONArray.toCollection(
				JSONArray.fromObject(data), JSONObject.class);

		response.setContentType("application/csv; charset=gbk"); // 必须是gbk，否则csv文件在excel里打开是乱码
		String fileName = new String(request.getParameter("csvName").getBytes("UTF-8"), "ISO8859-1");
		String agent = request.getHeader("USER-AGENT").toLowerCase();
		if ((agent.indexOf("msie") > -1) || (agent.indexOf("rident") > -1)) {
			fileName = URLEncoder.encode(fileName, "ISO8859-1");
		}
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		
		PrintWriter out = response.getWriter();
		int size = dataArray.size();
		for (int i = 0; i < size; i++) {
			StringBuilder sb = new StringBuilder();
			JSONObject json = dataArray.get(i);
			sb.append(",\"").append(((String)json.get("1")).replaceAll("\"", "\"\"")).append("\"");
			sb.append(",\"").append(((String)json.get("2")).replaceAll("\"", "\"\"")).append("\"");
			if(json.containsKey("3"))
				sb.append(",\"").append(((String)json.get("3")).replaceAll("\"", "\"\"")).append("\"");
			if(json.containsKey("4"))
				sb.append(",\"").append(((String)json.get("4")).replaceAll("\"", "\"\"")).append("\"");
			
			out.write(sb.toString().substring(1).replaceAll("\r|\n", "") + "\r\n");
		}
		response.flushBuffer();
		out.close();
		return new ModelAndView("xy/stat/Stat", new HashMap<String, Object>());
	}

	//查找结果的json，格式为[{key,value},{key,value},...]
	private String json(Document[] docs) throws E5Exception {
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		for (Document doc : docs) {
			if (result.length() > 1) result.append(",");
			
			result.append("{\"value\":\"").append(InfoHelper.filter4Json(doc.getString("src_name")))
				.append("\",\"key\":\"").append(String.valueOf(doc.getDocID()))
				.append("\"}");
		}
		result.append("]");
	
		return result.toString();
	}
}
