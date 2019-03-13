package com.founder.xy.statistics.web;

import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnManager;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.statistics.service.StatisticsService;
import com.founder.xy.statistics.util.FileUtil;
import com.founder.xy.system.site.SiteUserReader;


import com.google.gson.JsonObject;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan on 2016/12/14.
 */

@Controller
@Scope("prototype")
@RequestMapping("/xy/statistics")
public class StatisticsController {
    @Autowired
    StatisticsService statisticsService;
    @Autowired
	private SiteUserReader userReader;
    @Autowired
	private ColumnReader colReader;
    private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
    
    @Autowired
	private ColumnManager colManager;

    @RequestMapping(value = "Statistics.do", method = RequestMethod.POST)
    public
    @ResponseBody
    /*Map<String, Object>*/ModelAndView statistics(HttpServletRequest request, @RequestBody Map<String, Object> inParam) throws Exception {
        String tenantCode = InfoHelper.getTenantCode(request);
        log.error("---开始统计---");
        String channelCode = MapUtils.getString(inParam, "channelCode");
        int userID = ProcHelper.getUserID(request);
        int ch = WebUtil.getInt(request, "ch", 0);
		int colLibID = LibHelper.getColumnLibID(request);
		String columnID = MapUtils.getString(inParam, "columnID");
		int roleID = ProcHelper.getRoleID(request);
        if (tenantCode == null || tenantCode.equals("") || channelCode == null || channelCode.equals("")) {
            throw new E5Exception("Wrong Parameter!");
        }


        String siteID = MapUtils.getString(inParam, "siteID");
        if (siteID == null || siteID.equals("")) {
            throw new E5Exception("Site_ID is null!");
        }
        if("channelApp".equals(channelCode))
        	ch =1;
        String columnIds = "";
        log.error("---查询可操作的栏目---");
        Date start = new Date();
        //栏目权限限制仅针对栏目总览-栏目明细模块，其他模块不考虑权限
        if (columnID == null||"".equals(columnID.trim())){
	        String statisticsType = MapUtils.getString(MapUtils.getMap(inParam, "dataParam"), "statisticsType");
	        if("ColumnDetail".equals(statisticsType.trim())){
	        //处理检索范围，管理员查全部栏目根节点，非管理员查权限下栏目根节点
		  		if (!isAdmin(userID)) {
		  			Document[] cols = colManager.getAllRoot(colLibID, Integer.parseInt(siteID), ch);
		  			columnIds = jsonTreeGetParent(cols);
		  			//columnIds = colManager.getAllRootStr(colLibID, Integer.parseInt(siteID), ch);
		  		}
		  		else {
		        //获取当前登录用户可操作所有栏目的根节点
		        Column[] cols = colReader.getOpColumns(colLibID, userID, Integer.parseInt(siteID), ch, roleID);
				columnIds = jsonTreeWithParent(cols,colLibID, Integer.parseInt(siteID), ch);
		  		}
	        }
        }
  		Date end = new Date();
  		log.error("---可统计的栏目---"+columnIds);
  		System.err.println("统计栏目耗时："+(end.getTime()-start.getTime()));
        Map dataParam = MapUtils.getMap(inParam, "dataParam");
        dataParam.put("columnIds", columnIds);
        dataParam.put("channelCode", channelCode);
        dataParam.put("tenantCode", tenantCode);
        dataParam.put("siteID", siteID);
        Map<String, Object> model = statisticsService.getStatisticsData(dataParam);
        Date endStatistics = new Date();
        System.err.println("统计耗时："+(endStatistics.getTime()-end.getTime()));
        String viewName = MapUtils.getString(model, "viewName");
        return new ModelAndView(viewName, model);
    }

    private String jsonTreeGetParent(Document[] cols) {
		String columnIds = "";
		int i = 0;
		for (Document col : cols) {
			if(i <= 998){
				if(i==0)
					columnIds = columnIds + col.getDocID();
				else
					columnIds = columnIds + "," + col.getDocID();
				i++;
			}
		}
		return columnIds;
	}

	/**
	 * 判断当前用户是否管理员。
	 * @param userID
	 * @return
	 */
	private boolean isAdmin(int userID) {
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		try {
			User user = userReader.getUserByID(userID);
			return "1".equals(user.getProperty2());
		} catch (E5Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//返回权限下栏目的节点及其子节点
	private String jsonTreeWithParent(Column[] cols, int colLibID, int siteID, int ch) throws E5Exception {
		String columnIds = "";
		int i = 0;
		if (cols != null) {
			for (Column col : cols) {
				if(i <= 998 && col.getParentID() == 0){
					if(i == 0)
						columnIds = columnIds + col.getId();
					else
						columnIds = columnIds + "," + col.getId();
					i++;
					/*if (col.isExpandable()){
						columnIds = getChildColumnIds(colLibID, siteID, ch, col.getId(), columnIds);
					}*/
					}
			}
		}
		return columnIds;
	}
	//循环获取子节点
	@SuppressWarnings("unused")
	private String getChildColumnIds(int colLibID, int siteID, int ch,
			long id, String columnIds) throws E5Exception {
		Document[] docs = colManager.getRoot(colLibID, siteID, ch, id);
		for (Document doc : docs) {
			columnIds = columnIds + "," + doc.getDocID();
			if(doc.getInt("col_childCount") > 0)
				columnIds = getChildColumnIds(colLibID, siteID, ch, doc.getDocID(), columnIds);
		}
		return columnIds;
	}


	/**
     * csv导出
     */

    @RequestMapping(value = "ExportCSV.do")
    public void exportCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取参数
        String inParamJson = request.getParameter("jsonData");
        ObjectMapper mapper = new ObjectMapper(); //转换器
        Map inParam = mapper.readValue(inParamJson, Map.class); //json转换成map
        String fileName = MapUtils.getString(inParam, "fileName") + System.currentTimeMillis();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if ((agent.indexOf("msie") > -1) || (agent.indexOf("rident") > -1)) {
            fileName = URLEncoder.encode(fileName, "ISO8859-1");
        }
        inParam.remove("fileName");
        String tenantCode = InfoHelper.getTenantCode(request);
        String channelCode = MapUtils.getString(inParam, "channelCode");
        if (tenantCode == null || tenantCode.equals("") || channelCode == null || channelCode.equals("")) {
            throw new E5Exception("Wrong Parameter!");
        }
        String siteID = MapUtils.getString(inParam, "siteID");
        if (siteID == null || siteID.equals("")) {
            throw new E5Exception("Site_ID is null!");
        }
        Map dataParam = MapUtils.getMap(inParam, "dataParam");
        dataParam.put("channelCode", channelCode);
        dataParam.put("tenantCode", tenantCode);
        dataParam.put("siteID", siteID);
        //获取准备导出的数据
        Map<String, Object> exportData = statisticsService.getExportData(dataParam);
        inParam.putAll(exportData);
        //生成CSV文件
        String newFileName = FileUtil.generateCSVFile(fileName, filePath, inParam);
        if (newFileName != null && !"".equals(newFileName)) {
            fileName = newFileName;
        }
        //下载CSV文件
        FileUtil.downLoadCSVFile(response, fileName, filePath);
        //删除生成的CSV文件
        FileUtil.deleteFile(fileName, filePath);
        System.out.println("执行");

    }

    @RequestMapping(value = "ExportCSVnoHead.do")
    public void exportCSVnoHead(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取参数
        String inParamJson = request.getParameter("jsonData");
        ObjectMapper mapper = new ObjectMapper(); //转换器
        Map inParam = mapper.readValue(inParamJson, Map.class); //json转换成map
        String fileName = MapUtils.getString(inParam, "fileName") + System.currentTimeMillis();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if ((agent.indexOf("msie") > -1) || (agent.indexOf("rident") > -1)) {
            fileName = URLEncoder.encode(fileName, "ISO8859-1");
        }
        inParam.remove("fileName");
        String tenantCode = InfoHelper.getTenantCode(request);
        String channelCode = MapUtils.getString(inParam, "channelCode");
        if (tenantCode == null || tenantCode.equals("") || channelCode == null || channelCode.equals("")) {
            throw new E5Exception("Wrong Parameter!");
        }
        String siteID = MapUtils.getString(inParam, "siteID");
        if (siteID == null || siteID.equals("")) {
            throw new E5Exception("Site_ID is null!");
        }
        Map dataParam = MapUtils.getMap(inParam, "dataParam");
        dataParam.put("channelCode", channelCode);
        dataParam.put("tenantCode", tenantCode);
        dataParam.put("siteID", siteID);
        //获取准备导出的数据
        Map<String, Object> exportData = statisticsService.getExportData(dataParam);
        inParam.putAll(exportData);
        //生成CSV文件
        String newFileName = FileUtil.generateCSVFileWithoutHead(fileName, filePath, inParam);
        if (newFileName != null && !"".equals(newFileName)) {
            fileName = newFileName;
        }
        //下载CSV文件
        FileUtil.downLoadCSVFile(response, fileName, filePath);
        //删除生成的CSV文件
        FileUtil.deleteFile(fileName, filePath);
        System.out.println("执行");

    }

    @RequestMapping(value = "ExportCSVWithStaticData.do")
    public void exportCSVWithStaticData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取参数
        String inParamJson = request.getParameter("jsonData");
        ObjectMapper mapper = new ObjectMapper(); //转换器
        Map inParam = mapper.readValue(inParamJson, Map.class); //json转换成map
        String fileName = MapUtils.getString(inParam, "fileName") + System.currentTimeMillis();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if ((agent.indexOf("msie") > -1) || (agent.indexOf("rident") > -1)) {
            fileName = URLEncoder.encode(fileName, "ISO8859-1");
        }
        inParam.remove("fileName");
        //生成CSV文件
        String newFileName = FileUtil.generateCSVFileWithoutHead(fileName, filePath, inParam);
        if (newFileName != null && !"".equals(newFileName)) {
            fileName = newFileName;
        }
        //下载CSV文件
        FileUtil.downLoadCSVFile(response, fileName, filePath);
        //删除生成的CSV文件
        FileUtil.deleteFile(fileName, filePath);

    }


    /**
     * 稿件总览-按话题统计-查看话题组
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getColumnTopic.do")
    public void getTopicsGroup(HttpServletRequest request, HttpServletResponse response, int siteID) throws Exception {
        List<Map<String, Object>> model = statisticsService.getColumnTopicData(siteID);
        JSONArray jsonArray = JSONArray.fromObject(model);

        InfoHelper.outputJson(jsonArray.toString(), response);
    }
}
