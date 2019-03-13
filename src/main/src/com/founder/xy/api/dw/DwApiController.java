package com.founder.xy.api.dw;

import com.founder.e5.cat.CatManager;
import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.permission.PermissionHelper;
import com.founder.e5.sso.SSO;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.sys.org.Role;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.e5.workspace.mergerole.controller.SysUser;
import com.founder.xy.commons.AntZipUtils;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.GroupManager;
import com.founder.xy.config.TabHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.system.Tenant;
import com.founder.xy.system.TenantManager;
import com.founder.xy.system.site.Site;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.template.TemplateService;
import com.founder.xy.template.parser.TemplateParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 与DreamWaver插件通讯的Api
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/api/dw")
public class DwApiController {
	private static final Logger log = LoggerFactory.getLogger(DwApiController.class);
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private DwApiManager apiManager;
	private SSO sso;
	@Autowired
	private SiteUserReader siteUserReader;

	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private TemplateParser templateParser;
	
	private TemplateService templateService;
	
	private static Map<String, String> statisticsMap ;
	  static
	   {
		  statisticsMap = new HashMap<>();
		  statisticsMap.put("discussMain", "discuss/js/discussMain.js");
		  statisticsMap.put("revealJs", "discuss/js/jquery.reveal.js");
		  statisticsMap.put("revealCss", "discuss/css/reveal.css");
		  statisticsMap.put("jquery", "stat/jquery.min.js");
		  statisticsMap.put("WebClick", "stat/WebClick.js");
		  statisticsMap.put("WapClick", "stat/WapClick.js");
		  statisticsMap.put("NodeClick", "stat/NodeClick.js");
	   }

	public TemplateService getTemplateService() {
		return templateService;
	}

	@Resource(name = "templateService")
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}
	
	/**
	 * 读栏目树（第一层）
	 * url:http://WEBROOT/api/dw/Tree.do?parentID=&siteID=&ch=
	 */
	@RequestMapping(value = "Tree.do")
	public void tree(HttpServletRequest request, HttpServletResponse response, @RequestParam int parentID) throws Exception {
		/*
		int siteID = WebUtil.getInt(request, "siteID", 0);//站点ID。读第一层根栏目时才需要
		int ch = WebUtil.getInt(request, "ch", 0);//渠道：0表示Web版，1表示App版，默认为0
		int colLibID = LibHelper.getColumnLibID();//栏目库ID，暂时不考虑租户
		
		//TODO：使用ColumnReader
		*/
	}

	/**
	 * dw 登录
	 */
	@RequestMapping(method = RequestMethod.POST, value = "Login.do")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = login(request);
		InfoHelper.outputXML(result, response);
	}

	/**
	 * 获取站点下分组数据列表
	 */
	@RequestMapping(value = "datalist.do")
	public void datalist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date start = new Date();
		Document[] tpldocs = templateService.findAll(request);
		Date end = new Date();
        System.out.println("时间差值--查询："+(end.getTime()-start.getTime()));
		String datalist = getDataList(request, tpldocs.length);
		Date end2 = new Date();
        System.out.println("时间差值--拼接："+(end2.getTime()-end.getTime()));
		InfoHelper.outputXML(datalist, response);
	}
	/**
	 * 获取站点或站点及分组下模板列表--分页
	 */
	@RequestMapping(value = "TemplateList.do")
	public void templateList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String siteID = request.getParameter("siteID");
		String type = request.getParameter("type");
		String groupID = request.getParameter("groupID");
		Date start = new Date();
		Document[] tpldocs = templateService.findPage(request);
		Date end = new Date();
        System.out.println("时间差值--查询："+(end.getTime()-start.getTime()));
        Document[] tpldocsAll = templateService.findByParamsForPage(groupID, type, Integer.parseInt(siteID), request, false);
		String datalist = getDataList(request, tpldocs, tpldocsAll.length);
		/*String datalist = getDataListForPage(request, tpldocs);*/
		Date end2 = new Date();
        System.out.println("时间差值--拼接："+(end2.getTime()-end.getTime()));
		InfoHelper.outputXML(datalist, response);
	}
	/**
	 * 获取站点下分组数据列表
	 */
	@RequestMapping(value = "QueryGroupDetail.do")
	public void queryGroupDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date start = new Date();
		Document[] tpldocs = templateService.findBySite(request);
		Date end = new Date();
        System.out.println("时间差值--查询："+(end.getTime()-start.getTime()));
		String datalist = getDataPage(request, tpldocs.length);
		Date end2 = new Date();
        System.out.println("时间差值--拼接："+(end2.getTime()-end.getTime()));
		InfoHelper.outputXML(datalist, response);	
	}
	
	/**
	 * 根据模板ID获取站点ID
	 */
	@RequestMapping(value = "getSiteId.do")
	public void getSiteId(HttpServletRequest request, HttpServletResponse response, @RequestParam int DocID) throws Exception {
		Document[] tpldocs = templateService.findById(DocID);
		String datalist = getSiteInfoByDocId(request, tpldocs);
		InfoHelper.outputXML(datalist, response);
	}
	
	/**
	 * 根据模板ID获取站点ID
	 */
	@RequestMapping(value = "QueryTemplateInfo.do")
	public void QueryTemplateInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String siteID = request.getParameter("siteID");
		String DocIDstr = request.getParameter("DocID");
		String DocTitle = request.getParameter("DocTitle");
		int DocID = 0;
		if(!StringUtils.isBlank(DocIDstr))
			DocID = Integer.parseInt(DocIDstr);
		Document[] tpldocs = templateService.findByParams(DocID, DocTitle, Integer.parseInt(siteID), request, true);
		Document[] tpldocsAll = templateService.findByParams(DocID, DocTitle, Integer.parseInt(siteID), request, false);
		String datalist = getDataList(request, tpldocs, tpldocsAll.length);
		InfoHelper.outputXML(datalist, response);
	}
	
	/**
	 * 制作模板时，添加统计相关组件所需URL
	 */
	@RequestMapping(value = "pathlist.do")
	public void pathlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Date start = new Date();
		String datalist = getPathList();
		Date end = new Date();
        System.out.println("时间差值--拼接："+(end.getTime()-start.getTime()));
		InfoHelper.outputXML(datalist, response);
	}

	private String getPathList() {
		String staticPath = InfoHelper.getConfig("互动", "外网资源地址");
		String outerPath = InfoHelper.getConfig("互动", "外网Api地址");
		String outerIP = staticPath;
		if(staticPath.startsWith("http"))
			outerIP = staticPath.replace("http://", "").replace("https://", "");
		outerIP = outerIP.substring(0, outerIP.indexOf("/"));
		System.out.println("外网ip："+outerIP);
		
		StringBuffer result = new StringBuffer(1024);
		result.append("\t\n<data>\t\n");
		result.append("<statistics id=\"").append("rooturl");
		result.append("\" path=\"").append(outerIP).append("\">");
		result.append("</statistics>\t\n");
		result.append("<statistics id=\"").append("apiroot");
		result.append("\" path=\"").append(outerPath.replace("http://", "").replace("https://", "")).append("\">");
		result.append("</statistics>\t\n");
		for(Map.Entry<String, String> entry : statisticsMap.entrySet()){
			result.append("<statistics id=\"").append(entry.getKey());
			result.append("\" path=\"").append(staticPath + entry.getValue()).append("\">");
			result.append("</statistics>\t\n");
		}
		result.append("</data>");
		return result.toString();
	
	}

	private String getDataList(HttpServletRequest request, Document[] tpldocs, int len) throws Exception {
		String code = request.getParameter("code");
		String siteId = request.getParameter("siteID");
		String siteName = null;
		int catTypeID = (Enum.valueOf(CatTypes.class, "CAT_" + code.toUpperCase())).typeID();
		siteName = getSiteName(siteId, catTypeID);
		StringBuffer result = new StringBuffer(1024);
		StringBuffer resultNoTemGroup = new StringBuffer(1024);
		Set<String> checkNoTemGroup = new HashSet<String>();
		Set<String> checkTemGroup = new HashSet<String>();
		Set<String> checkGroup = new HashSet<String>();
		int num = 0;
		result.append("\t\n<data>\t\n");
		result.append("<site id=\"").append(siteId);
		result.append("\" name=\"").append(siteName).append("\" count=\"").append(len).append("\">");
		//如果模板不为空，拼接分组
		if(tpldocs != null && tpldocs.length>0){
			//记录全部有数据分组ID
			for(Document doc: tpldocs){
				if(doc != null)
				checkGroup.add(doc.getString("t_groupID"));
			}
			for(Document doc: tpldocs){
				if(doc != null){
				int groupBeginSize = checkTemGroup.size();
				String t_groupID = null;
				String[] siteDir = null;
				String groupName = null;
				Category[] roots = null;
				int i = 0;
				siteId = doc.getString("t_siteID");
				siteDir = readSiteInfo(Integer.parseInt(siteId));
				t_groupID = doc.getString("t_groupID");
				
				//根据类型和站点ID取分组
				roots = getCategory(siteId, catTypeID);
				num ++;
				if(roots != null && roots.length > 0){
					for(Category root : roots){
				        if(root != null)
				        	groupName = root.getCatName();
						if(!StringUtils.isBlank(groupName))
							groupName = groupName.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
						
						if (!StringUtils.isBlank(t_groupID) && Integer.parseInt(t_groupID) == root.getCatID()) {
							checkNoTemGroup.add(String.valueOf(root.getCatID()));
							int beginSize = checkTemGroup.size();
		                	checkTemGroup.add(String.valueOf(root.getCatID()));
		                	int endSize = checkTemGroup.size();
		                	//当有模板且该分组首次加载，则添加分组，添加分组前判断之前是否存在未结束分组
		                	if(i == 0 && endSize > beginSize){
		                		int groupEndSize = checkTemGroup.size();
		                		if(groupEndSize > groupBeginSize && num > 1)
		                			result.append("</group>\t\n");
		                		result.append("<group id=\"").append(root.getCatID());
		                		result.append("\" name=\"").append(groupName).append("\">\t\n");
		                	}
		                	result.append(apiManager.getTemplateXML(doc, siteDir[2]));
		                    i++;
		                }else{
		                	int beginSize = checkNoTemGroup.size();
		                	int beginCheck = checkGroup.size();
		                	checkNoTemGroup.add(String.valueOf(root.getCatID()));
		                	checkGroup.add(String.valueOf(root.getCatID()));
		                	int endSize = checkNoTemGroup.size();
		                	int endCheck = checkGroup.size();
		                	//拼接无数据分组信息
		                	if(endSize > beginSize && endCheck > beginCheck){
								resultNoTemGroup.append("<group id=\"").append(root.getCatID());
								resultNoTemGroup.append("\" name=\"").append(groupName).append("\">\t\n");
								resultNoTemGroup.append("</group>\t\n");
		                	}
		                }
					}
					//分组ID为0,代表模板为专题模板，单独进行拼接，此处应考虑排除专题模板
					if(i == 0 && "0".equals(t_groupID)){
						result.append("<group id=\"").append(t_groupID);
						result.append("\" name=\"null\">\t\n");
		                result.append(apiManager.getTemplateXML(doc, siteDir[2]));
						result.append("</group>\t\n");
					}
					
				}
				int groupEndSize = checkTemGroup.size();
				//处理分组下只有一条模板时，拼接分组结束符
				if(i > 0 && (groupEndSize == groupBeginSize || tpldocs.length == groupEndSize || (groupEndSize - groupBeginSize == 1)) && num == tpldocs.length)
					result.append("</group>\t\n");
			
			}
			}
		}else{
			//未检索到模板时，只拼接分组信息
			Category[] roots = null;
			roots = getCategory(siteId, catTypeID);
			String groupName = null;
			if(roots != null && roots.length > 0)
				for(Category root : roots){
					if(root != null)
			        	groupName = root.getCatName();
					if(!StringUtils.isBlank(groupName))
						groupName = groupName.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
					resultNoTemGroup.append("<group id=\"").append(root.getCatID());
					resultNoTemGroup.append("\" name=\"").append(groupName).append("\">\t\n");
					resultNoTemGroup.append("</group>\t\n");
				}
		}
		result.append(resultNoTemGroup);
		result.append("</site>\t\n");
		result.append("</data>");
		return result.toString();
	
	}

	private String getSiteName(String siteId, int catTypeID) throws Exception {

		Category root = null;
		String siteName = null;
		
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
        Category[] roots = catManager.getSubCats(catTypeID, 0);

        if (roots != null) {
            String cod = String.valueOf(siteId);
            for (Category cat : roots) {
                if (cod.equals(cat.getCatCode())) {
                    root = cat;
                    siteName = root.getCatName();
                    break;
                }
            }
        }
        
        return siteName;
	
	}

	private Category[] getCategory(String siteId, int catTypeID) throws Exception {
		Category root = null;
		
		CatManager catManager = (CatManager) Context.getBean(CatManager.class);
        Category[] roots = catManager.getSubCats(catTypeID, 0);

        if (roots != null) {
            String cod = String.valueOf(siteId);
            for (Category cat : roots) {
                if (cod.equals(cat.getCatCode())) {
                    root = cat;
                    break;
                }
            }
        }
        roots = catManager.getSubCats(catTypeID, root.getCatID());
        return roots;
	}

	/**
	 * 带有中文参数时,用来接受处理参数
	 */
	@RequestMapping(value = "params.do")
	public void dispatcher(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		request.setCharacterEncoding("utf-8");
		BufferedReader read = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuffer url = new StringBuffer(255);
		String str = null;
		while (null != (str = read.readLine())) {
			url.append(str);
		}
		log.error("url:" + url.toString());
		if (url != null && !"".equals(url)) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(url.toString());
			dispatcher.forward(request, response);
		}
	}

	/**
	 * 上传模板文件
	 */
	@RequestMapping(method = RequestMethod.POST, value = "uploadtpl.do")
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		String newrltPath = null;
		String deviceName = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			//验证用户
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String fileType = request.getParameter("FileType");
			int ret = sso.verifyUserPassword(username, password);
			log.error("sso连接状态:" + ret);
			if (ret != 0) {
				InfoHelper.outputText("-2", response);
				return;
			}
			//获取模板ID
			String id = request.getParameter("DocID");
			//获取模板后缀
			fileType = StringUtils.isBlank(fileType)? "html":fileType;
			newrltPath = DateUtils.format("yyyyMM/dd/") + id + "." + fileType;
			//获取存储设备名
			int docLibID = LibHelper.getTemplateLibID();
			DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
			DocLib docLib = libReader.get(docLibID);
			deviceName = docLib.getStorageDevice();

			log.error("diviceName:" + deviceName + ";newrltPath:" + newrltPath);
			//保存模板
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			sdManager.write(deviceName, newrltPath, request.getInputStream());
			doc = docManager.get(docLibID, Long.parseLong(id));
			log.error("模板保存路径:" + newrltPath);
			if (doc != null) {
				//更新数据库
				doc.set("t_file", deviceName + ";" + newrltPath);
				doc.setLastmodified(new Timestamp(System.currentTimeMillis()));
				docManager.save(doc);
				log.error("---模板保存成功----");
				//写日志
				writeLog(docLibID, Long.parseLong(id), username, "上传", "从Dreamweaver上传模板");
				//解析新模板
				templateParser.parse(doc);

				//通知发布服务
				PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_TEMPLATE);
			}
		} catch (Exception e) {
			String result = "0;" + e.getLocalizedMessage();
			log.error("模板保存异常:" + e);
			InfoHelper.outputText(result, response);
			e.printStackTrace();
		}
		String result = "1;" + newrltPath;
		InfoHelper.outputText(result, response);
	}

	/**
	 * 上传资源文件 @param request the request
	 * 将资源文件上传至 网站资源文件路径，再生成trs信息文件
	 *
	 * @param response the response
	 * @throws Exception the exception
	 */
	@RequestMapping(method = RequestMethod.POST, value = "uploadRes.do")
	public void uploadRes(HttpServletRequest request, HttpServletResponse response, @RequestParam int doclibID, @RequestParam int DocID, @RequestParam int siteID)
			throws Exception {


		String[] siteDir = readSiteInfo(siteID);
		request.setCharacterEncoding("utf-8");

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		OutputStream os = null;
		String newrltPath = null;
		File resZipFile = null;
		String oldDir = null;
		InputStream is = null;
		try {
			//验证用户
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			int ret = sso.verifyUserPassword(username, password);
			log.error("sso登录状态:" + ret);
			if (ret != 0) {
				InfoHelper.outputText("-2", response);
				return;
			}
			// 初始化文件接收
			MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

			// 拿到前台传过来的数据
			MultipartFile file = multipartRequest.getFile("file");
			if (file == null) {
				log.error("---模板为空上传失败----");
				InfoHelper.outputText("-4", response);
				return;
			}
			//根据模板创建时间生成模板资源存放文件
			Document tplDoc = docManager.get(doclibID, DocID);
			Timestamp createdTime = tplDoc.getTimestamp("SYS_CREATED");

			oldDir = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(createdTime, "yyyyMM/dd/") + DocID;
			newrltPath = oldDir + ".zip";
			log.error("模板保存路径:" + newrltPath);
			resZipFile = new File(newrltPath);
			if (!resZipFile.getParentFile().exists()) {
				resZipFile.getParentFile().mkdirs();
			}
			is = file.getInputStream();
			os = new FileOutputStream(resZipFile);
			IOUtils.copy(is, os);
			log.error(newrltPath);
		} catch (Exception e) {
			log.error("模板保存异常:" + e);
			InfoHelper.outputText("-3", response);
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(is);
			if (os != null) {
				os.flush();
				os.close();
			}
		}
		if (resZipFile != null) {
			// 删除原有文件
			if (oldDir != null) {
				File oldRes = new File(oldDir);
				if (oldRes.exists()) {
					clearDir(oldRes);
				}
			}
			AntZipUtils.unZip(resZipFile, resZipFile.getParent());
			// 删除zip文件
			resZipFile.delete();
			// 生成Trs 文件
			listfiles(oldDir, siteDir[0]);
		}
		String result = "1;" + newrltPath;
		InfoHelper.outputText(result, response);
	}


	/**
	 * dw下载模板
	 */
	@RequestMapping(value = "loadtpl.do")
	public void tplinfo(HttpServletRequest request, HttpServletResponse response,
						@RequestParam int doclibID, @RequestParam int tplID) throws Exception {
		//验证用户
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int ret = sso.verifyUserPassword(username, password);
		if (ret != 0) {
			InfoHelper.outputText("-2", response);
			return;
		}

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document tpldoc = docManager.get(doclibID, tplID);
		String path = tpldoc.getString("t_file");

		request.setCharacterEncoding("UTF-8");

		//调用e5的下载地址下载模板
		String url = "../../e5workspace/Data.do?action=download&path=" + path;
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, response);
	}

	/**
	 * dw下载模板资源文件
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "downloadRes.do")
	public void downloadRes(HttpServletRequest request, HttpServletResponse response,
							@RequestParam int doclibID, @RequestParam int DocID, @RequestParam int siteID) throws Exception {
		//验证用户
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		int ret = sso.verifyUserPassword(username, password);
		if (ret != 0) {
			InfoHelper.outputText("-2", response);
			return;
		}
		String[] siteDir = readSiteInfo(siteID);

		//获取资源文件存储位置
		//根据ID 获取（时间）-文件夹位置
		DocumentManager docManager = DocumentManagerFactory.getInstance();//根据模板创建时间生成模板资源存放文件
		Document tplDoc = docManager.get(doclibID, DocID);
		Timestamp createdTime = tplDoc.getTimestamp("SYS_CREATED");

		String newrltPath = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(createdTime, "yyyyMM/dd/") + DocID;
		File resDir = new File(newrltPath);
		if (resDir.exists()) {
			File resZipFile = new File(newrltPath + ".zip");
			// 压缩文件 生成临时文件
			AntZipUtils.makeZip(new String[]{newrltPath}, newrltPath + ".zip");
			// 临时文件输出到response

			InputStream in = new FileInputStream(resZipFile);
			OutputStream out = null;
			try {
				//自动判断下载文件类型
				response.setContentType("multipart/form-data");
				response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(DocID + ".zip", "UTF-8"));
				out = response.getOutputStream();
				IOUtils.copy(in, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(in);
				ResourceMgr.closeQuietly(out);
				resZipFile.delete();
				InfoHelper.outputText("-3", response);
				return;
			}
		} else {
			InfoHelper.outputText("-4", response);
			return;
		}
	}

	/**
	 * DW插件创建创建模板
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "createTpl.do", method = RequestMethod.POST)
	public void createTpl(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("utf-8");
		//获取输入流
		InputStream inputStream = request.getInputStream();
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document doc = null;
		try {
			//验证用户
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			int ret = sso.verifyUserPassword(username, password);
			if (ret != 0) {
				InfoHelper.outputText("-2", response);
				return;
			}
			log.error("sso连接用户:" + ret);
			byte[] b = new byte[1024];
			int len = inputStream.read(b);
			log.error("-----username:" + username );
			System.out.println("-----username:" + username );
			System.out.println("----文件大小available："+len);
			int docLibID = LibHelper.getTemplateLibID();
			long docID = InfoHelper.getNextDocID(DocTypes.TEMPLATE.typeID());
			doc = docManager.newDocument(docLibID, docID);
			if(doc != null)
				log.error("创建模板成功:" + docID);
			//首先读取头部参数的长度
			doc = apiManager.parse(request, doc);
			String fileType = request.getParameter("FileType");
			fileType = StringUtils.isBlank(fileType)? "html":fileType;
			String tplPath = DateUtils.format("yyyyMM/dd/") + doc.getDocID() + "."+ fileType;
			DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
			DocLib docLib = libReader.get(docLibID);
			String deviceName = docLib.getStorageDevice();
			doc.set("t_file", deviceName + ";" + tplPath);
			//保存模板文件内容
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			if (log.isInfoEnabled()) {
				log.info("模板文件的路径是: " + tplPath);
			}
			log.error("模板保存路径:" + tplPath);
			sdManager.write(deviceName, tplPath, request.getInputStream());
			doc.setLastmodified(new Timestamp(System.currentTimeMillis()));
			docManager.save(doc);

			//写日志
			writeLog(docLibID, docID, username, "新建", "在Dreamweaver新建模板");
			//关闭输入流
			ResourceMgr.closeQuietly(inputStream);
			//解析新模板
			templateParser.parse(doc);
			//通知发布服务
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_TEMPLATE);

			//输出结果给DW插件
			//返回状态码说明0:成功, -1 发生未知错误 
			InfoHelper.outputText("0" + doc.getDocID() +"."+ fileType, response);
		} catch (Exception exp) {
			log.error("创建模板出现异常", exp);
			InfoHelper.outputText("-1", response);
		} finally {
			ResourceMgr.closeQuietly(inputStream);
		}

	}


	/**
	 * 上传压缩文件 @param request the request
	 * 先取模板文档，获取模板id和创建时间，以生成模板资源文件保存位置
	 * 解压文件，取得模板资源，拼凑出模板资源外网地址，替换模板文件中的相对资源地址
	 * 保存模板文件和资源文件到对应位置，资源文件生成trans消息文件，模板文件调用模板解析
	 * 将上传的模板资源压缩包改名为模板ID.zip，保存到模板文件所在位置，方便下载
	 * 修改模板文档中模板文件地址，保存模板文档。
	 * @param request
	 * @param response
	 * @param doclibID
	 * @param DocID
	 * @param siteID
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadZip.do")
	public void uploadZip(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String DocID = request.getParameter("DocID");
		String siteID = request.getParameter("siteID");
		String[] siteDir = readSiteInfo(Integer.parseInt(siteID));
		request.setCharacterEncoding("utf-8");
		log.error("-----DocID:" + DocID );
		log.error("-----siteID:" + siteID);
		System.out.println("-----DocID:" + DocID );
		System.out.println("-----siteID:" + siteID);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String newrltPath = null;
		File resZipFile = null;
		String tempPath = null;
		String resPath = null;
		Document doc = null;
		String oldDir = null;
		Date date = null;
		try {
			//验证用户
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String fileType = "zip";
			int ret = sso.verifyUserPassword(username, password);
			if (ret != 0) {
				InfoHelper.outputText("-2", response);
				return;
			}
			log.error("sso连接用户:" + ret);
			log.error("-----username:" + username );
			System.out.println("-----username:" + username );
			
			//获取存储设备名
			int docLibID = LibHelper.getTemplateLibID();
			DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
			DocLib docLib = libReader.get(docLibID);
			String deviceName = docLib.getStorageDevice();
			Boolean isNew=true;
			
			//判断是否为新建，并生成模板存放目录
			if(DocID == null || StringUtils.isBlank(DocID)){
				isNew=false;
				DocID = String.valueOf(InfoHelper.getNextDocID(DocTypes.TEMPLATE.typeID()));
				doc = docManager.newDocument(docLibID, Long.parseLong(DocID));
				doc = apiManager.parse(request, doc);
				oldDir = DateUtils.format("yyyyMM/dd/") + DocID;
				newrltPath = oldDir + "." + fileType;
			}else{
				doc = docManager.get(docLibID, Long.parseLong(DocID));
				String tFile = doc.getString("T_FILE");
				date = formatFileTime(tFile);  
				oldDir = DateUtils.format(date, "yyyyMM/dd/") + DocID;
				newrltPath = oldDir + "." + fileType;
			}
			log.error("diviceName:" + deviceName + ";newrltPath:" + newrltPath);
			
			// 初始化文件接收
			MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
			
			// 拿到前台传过来的数据
			MultipartFile file = multipartRequest.getFile("file");
			
			if (file == null) {
				InfoHelper.outputText("-4", response);
				return;
			}else{
				System.out.println("----文件大小getSize："+file.getSize());
			}

			//获取写入文件的全路径
			String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(deviceName));
			String newPath = devicePath + File.separator + newrltPath;
			String resource = siteDir[1] + File.separator + "templateRes" + File.separator + formatDateByCreate(doc) + DocID;
			if(date != null)
				resource = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
			if (DocID != null && !StringUtils.isBlank(DocID) && isNew)
				cleanOldTemp(doc, newPath, resource);
			
			resZipFile = new File(newPath);
			//上传并解压ZIP文件，并返回解压后文件路径
			String[] paths = getPath(resZipFile, file);
			if(paths.length>0)	
				for(int i=0;i<paths.length;i++){
					if(paths[i].startsWith("1"))
						tempPath = paths[i].length()>devicePath.length()?paths[i].substring(devicePath.length()+3, paths[i].length()):paths[i].substring(2);
					else
						resPath = paths[i].substring(2);
				}
			/*if(resPath != null)
				resPath = renameRes(resPath, doc.getString("SYS_DOCUMENTID"));*/
			//保存模板并发布
			if(tempPath != null)
				uploadTemp(deviceName, tempPath, doc, docLibID, username, docManager, Integer.parseInt(DocID), resZipFile, newPath, siteDir);
			
		} catch (Exception e) {
			InfoHelper.outputText("-3", response);
			e.printStackTrace();
		} 
		if (resZipFile != null && resPath != null) {
			String reliPath = getOldPath(doc, tempPath);
			uploadResource(resPath, Integer.parseInt(siteID), reliPath, siteDir, request, response);
		}
		String result = "1;" + tempPath;
		InfoHelper.outputText(result, response);
	}
	private String renameRes(String resPath, String docID) {
		File oldFile = new File(resPath);
		resPath = resPath.substring(0, resPath.lastIndexOf(File.separator));
		resPath = resPath + File.separator + docID;
		File newFile = new File(resPath);
		if (oldFile.renameTo(newFile)) 
			return newFile.getPath();
		else
			return oldFile.getPath();
	}

	private void cleanOldTemp(Document doc, String newPath, String resource) throws Exception {
		//文件的全路径
		String oldZipPath = getOldPath(doc, null);
		if(new File(oldZipPath).exists())
			deleteFile(new File(oldZipPath));
		if(new File(resource).exists())
			deleteFile(new File(resource));
		if(new File(newPath).exists())
			new File(newPath).delete();
		//new File(oldZipPath).mkdirs();
	}

	//获取zip文件中模板和资源文件路径
	private String[] getPath(File resZipFile, MultipartFile file) throws IOException{
		OutputStream os = null;
		InputStream is = null;
		if (!resZipFile.getParentFile().exists()) {
			resZipFile.getParentFile().mkdirs();
		}
		if(resZipFile.exists())
			resZipFile.delete();
		try {
			is = file.getInputStream();
			os = new FileOutputStream(resZipFile);
			IOUtils.copy(is, os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ResourceMgr.closeQuietly(is);
			if (os != null) {
				os.flush();
				os.close();
			}
		}
		int m = resZipFile.getPath().lastIndexOf(".");
		//判断原目录是否存在，存在则清空原目录下全部文件
		File oldRes = new File(resZipFile.getPath().substring(0, m));
		if (oldRes.exists()) {
			deleteFile(oldRes);
		}
	
		//解压ZIP文件，并返回解压后文件路径
		String[] paths = AntZipUtils.unZipForPath(resZipFile, resZipFile.getPath().substring(0, m));
		return paths;
	}

	private void deleteFile(File file) {
		if (!file.exists())  
	        return;  
	    if (file.isFile()) {  
	    	file.delete();  
	        return;  
	    }  
	    File[] files = file.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	    	deleteFile(files[i]);  
	    }  
	    file.delete();   
		
	}

	@SuppressWarnings("finally")
	@RequestMapping( value = "loadZip.do")
	public void loadZip(HttpServletRequest request, HttpServletResponse response, @RequestParam int doclibID, @RequestParam int DocID)
			throws Exception {
		
		//验证用户
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String oldDir = null;
		int ret = sso.verifyUserPassword(username, password);
		if (ret != 0) {
			InfoHelper.outputText("-2", response);
			return;
		}
		log.error("sso连接用户:" + ret);
		log.error("-----username:" + username );
		System.out.println("-----username:" + username );
		Boolean isZip = true;
		Boolean downLoad = true;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document tpldoc = docManager.get(doclibID, DocID);
		if(tpldoc == null)
			return;
		String tFile = tpldoc.getString("T_FILE");
		String suffix = tpldoc.getString("t_fileType");
		String siteID = request.getParameter("siteID");
		String[] siteDir = readSiteInfo(Integer.parseInt(siteID));
		Date date = formatFileTime(tFile);  
		
		//获取写入文件的全路径
		String devicePath = getDevicePath();
		oldDir = devicePath + File.separator + tFile.substring(tFile.indexOf(";")+1, tFile.length());
		String path = devicePath + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
		String resPath = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
		
		String zipPath = null;
		if(!StringUtils.isBlank(path) && !StringUtils.isBlank(suffix)){
			zipPath = path + "." + suffix;
		}

		request.setCharacterEncoding("UTF-8");

		File zipDir = new File(zipPath);
		File resDir = new File(resPath);
		log.error("oldDir："+ oldDir);
		log.error("path："+ path);
		//兼容新旧版模板存储目录
		if(!zipDir.exists())
			zipDir = new File(path + File.separator + DocID + "." + suffix);
		log.error("处理后的模板地址1："+ zipDir.getPath());
		if(!zipDir.exists()){
			zipDir = new File(oldDir);
			//如果存在，则为旧模板，此处进行重命名操作
			if(zipDir.exists()){
				log.error("旧模板地址："+ oldDir);
				renameFile(tpldoc, oldDir);
				zipDir = new File(path + File.separator + DocID + "." + suffix);
			}
		}
		log.error("处理后的模板地址2："+ zipDir.getPath());
		if(!new File(path + ".zip").exists()){
			isZip = false;
			downLoad = false;
			//判断模板是否为压缩文件，如果不是，则创建新压缩文件
			if (zipDir.exists()){
				File[] fileList = new File[2];
				fileList[0]=zipDir;
				if(!resDir.exists()){//如果资源文件不存在，则创建一个空目录
					resDir.getParentFile().mkdirs();
					resDir.mkdirs();
					fileList[1]=resDir;
				}else{
					File fa[] = resDir.listFiles();
					fileList = new File[fa.length+1];
					fileList[0]=zipDir;
					for (int i = 0; i < fa.length; i++) {
						File fs = fa[i];
						fileList[i+1]=fs;
					}
				}
				// 压缩文件,只有当模板文件不为空时才进行压缩
				AntZipUtils.makeZip(fileList, path + ".zip");
				zipDir = new File(path + ".zip");
				log.error("压缩文件地址："+ zipDir.getPath());
				if(zipDir.exists())
					downLoad = true;
				log.error("是否可下载："+ downLoad);
			}
		}
		
		if (zipDir.exists() && downLoad) {
			File resZipFile = new File(path + ".zip");
			// 临时文件输出到response
			InputStream in = new FileInputStream(resZipFile);
			OutputStream out = null;
			try {
				//自动判断下载文件类型
				response.setContentType("multipart/form-data");
				response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(DocID + ".zip", "UTF-8"));
				out = response.getOutputStream();
				IOUtils.copy(in, out);
				//写日志
				writeLog(doclibID, (long)DocID, username, "下载", "在Dreamweaver下载模板");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(in);
				ResourceMgr.closeQuietly(out);
				if(!isZip)//如果不是压缩文件，则需每次传输完毕后删除当前压缩的压缩文件
					zipDir.delete();
				InfoHelper.outputText("-3", response);
				return;
			}
		}else {
			InfoHelper.outputText("-4", response);
			return;
		}
	
	}
	
	private Date formatFileTime(String tFile) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM/dd/");//格式化模板日期路径  
		String tplDate = tFile.substring(tFile.indexOf(";")+1, tFile.indexOf(";")+11).replaceAll("\\\\", "/");
		Date date = sdf.parse(tplDate);
		return date;
	}

	private String getDevicePath() throws E5Exception {
		int docLibID = LibHelper.getTemplateLibID();
		DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
		DocLib docLib = libReader.get(docLibID);
		String deviceName = docLib.getStorageDevice();
		//获取写入文件的全路径
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(deviceName));
		return devicePath;
	}

	private String getOldPath(Document doc, String tempPath) throws Exception {
		String fileName = doc.getString("t_file");
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); //存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); //相对路径和文件名
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
		String oldPath = devicePath + File.separator + rltPath;
		String newrltPath = formatDateByCreate(doc) + doc.getDocID();
		if("".equals(tempPath) || tempPath == null)
			return oldPath;
		else
			return newrltPath;
	}

	private void uploadTemp(String deviceName, String tempPath, Document doc,
			int docLibID, String username, DocumentManager docManager, int docID, File resZipFile, String newPath, String[] siteDir) throws Exception {
		try {
		if (doc != null) {
			doc.set("t_file", deviceName + ";" + tempPath);
			renameFile(doc,tempPath);//
			tempPath = getOldPath(doc, tempPath);
			doc.setLastmodified(new Timestamp(System.currentTimeMillis()));
			//更新数据库
			docManager.save(doc);
			if (resZipFile != null) {
				//压缩改名后的文件，并删除原压缩文件
				resZipFile.delete();
				String zipPath = newPath.substring(0, newPath.lastIndexOf("."));
				File resDir = new File(zipPath);
				if (resDir.exists()) {
					resZipFile = new File(zipPath + ".zip");
					// 压缩文件
					AntZipUtils.makeZip(resDir.listFiles(), zipPath + ".zip");
				}
			}
			//写日志
			writeLog(docLibID, docID, username, "上传", "从Dreamweaver上传模板");
			//解析新模板
			templateParser.parse(doc, siteDir, tempPath);
			//通知发布服务
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_TEMPLATE);
		}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		
	}

	private void renameFile(Document doc, String tempPath) throws E5Exception, ParseException {

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String fileName = doc.getString("t_file");
		
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); //存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); //相对路径和文件名
		
		//文件的全路径
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
		String oldPath = devicePath + File.separator + rltPath;
		String zipPath = oldPath.length()>4?oldPath.substring(oldPath.lastIndexOf(".")+1, oldPath.length()):null;
		Date date = formatFileTime(fileName);
		String newrltPath = formatDateByCreate(doc) + doc.getDocID() ;
		if(tempPath != null)
			newrltPath += File.separator+doc.getDocID();
		if(zipPath != null && ".zip".compareToIgnoreCase(zipPath)!=0){
			newrltPath += "."+zipPath;
			doc.set("t_fileType", zipPath);
		}else
			newrltPath += "."+doc.getString("t_fileType");
		String newPath = devicePath + File.separator + newrltPath;
		
		//改名并且写回数据库
		File oldFile = new File(oldPath);
		log.error("----开始改名-----："+ oldPath);
		if (oldFile.exists()){
			log.error("当前模板路径："+ oldPath);
			File newFile = new File(newPath);
			if(!newFile.getParentFile().exists())
				newFile.getParentFile().mkdir();
			if(newFile.exists() && newPath.endsWith("zip"))
				newFile.delete();
			if (oldFile.renameTo(newFile)) {
				log.error("改名成功，新路径："+ newPath);
				String newName = device + ";" + newrltPath;
				doc.set("t_file",newName);
				doc.set("t_fileType", zipPath);
				docManager.save(doc);
			}
		}
	}

	//资源文件发布
	public static void uploadResource(String resPath, int siteID, String tempPath, String[] siteDir,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		String newrltPath = null;
		File resZipFile = null;
		String fileName = resPath.substring(resPath.lastIndexOf(File.separator));
		//根据模板路径生成资源文件保存路径
		newrltPath = siteDir[1] + File.separator + "templateRes" + File.separator + tempPath + File.separator + fileName;
		resZipFile = new File(newrltPath);
		if (!resZipFile.getParentFile().exists()) {
			resZipFile.getParentFile().mkdirs();
		}
		log.error(newrltPath);
		if (resZipFile != null) {
			// 删除原有文件
			if (resZipFile.getParentFile().listFiles().length > 0) {
				clearDir(resZipFile.getParentFile());
				resZipFile.getParentFile().mkdirs();
				
			}
			//将解压后文件复制到资源文件保存目录
			copyDir(resPath, newrltPath);
			// 生成Trs 文件
			listfiles(newrltPath, siteDir[0]);
			//clearDir(new File(resPath));
			File resPathFile = new File(resPath);
			if(resPathFile.exists())
				clearDir(resPathFile);
		}
	}

	private static void copyFile(String oldPath, String newPath) throws IOException {
		File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = null;
        FileInputStream fin = null;
        FileOutputStream out = null; 
		try {
		in = new FileInputStream(oldFile);
		fin = new FileInputStream(oldFile);
        out = new FileOutputStream(file);;
        int len = fin.read(new byte[2097152]);
        if(len == -1)
        	len = 2097152;
        System.out.println("待复制资源文件size，DwAPIController.java：1129---"+len);
        byte[] buffer=new byte[len];
        
        while((in.read(buffer)) != -1){
            out.write(buffer);
        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			ResourceMgr.closeQuietly(in);
			ResourceMgr.closeQuietly(fin);
			ResourceMgr.closeQuietly(out);
		}
	}

	private static void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();
        
        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath  + File.separator  + filePath[i], newPath  + File.separator + filePath[i]);
            }
            if (new File(oldPath  + File.separator + filePath[i]).isFile()) {
                copyFile(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }
        }}

	private String login(HttpServletRequest request) throws Exception {
		ssoInit();

		String usercode = request.getParameter("UserCode");
		String sPass2 = request.getParameter("UserPassword");

		String result = null;
		List<Site> sites = null;
		List<Integer> siteRoles = new ArrayList<>(); //在不同站点下的角色
		try {
			int ret = sso.verifyUserPassword(usercode, sPass2);
			if (ret == -1) result = "nouser";
			if (ret == -2) result = "nouser"; //用户名密码错的报错一样，避免登录错误消息凭证枚举
			if (ret == -3) result = "frozen";

			if (ret == 0) {
				result = "ok";

				int userLibID = LibHelper.getUserExtLibID(request);
				UserReader userReader = (UserReader) Context.getBean(UserReader.class);
				User curUser = userReader.getUserByCode(usercode);

				//取用户可管理的站点列表
				int userID = curUser.getUserID();
				sites = siteUserReader.getSites(userLibID, userID);
				Role[] roles = null;
				for (int i = 0; i < sites.size(); i++) {
					int siteID = sites.get(i).getId();
					roles = getRolesBySite(userLibID, userID, siteID);

					int roleID = _newRoleID(roles);
					//判断是否有模板权限
					if (canTemplate(roleID))
						siteRoles.add(roleID);
					else
						siteRoles.add(0);
				}

				//登录，session中存放登录信息 供后续使用
				int roleID = siteRoles.get(0);
				int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);
				String[] rets = sso.login(usercode, roleID, request.getRemoteAddr(),
						request.getServerName(), true);
				int nID = Integer.parseInt(rets[0]);
				if (nID > 0) {
					// clearSession(request);
					putSession(curUser, sPass2, roleID, nID, newRoleID, request);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "error";
		}
		result = apiManager.getSiteInfoXML(result, sites, siteRoles);
		return result;
	}

	//判断角色是否有模板权限
	private boolean canTemplate(int roleID) {
		if (roleID <= 0) return false;

		//读取tabs
		String[] roleTabs = TabHelper.readRoleTabs(roleID, "MainPermission");
		return ArrayUtils.contains(roleTabs, "pagetpl");
	}

	private int _newRoleID(Role[] roles) throws E5Exception {
		if (roles == null || roles.length == 0) return -1;

		int roleID = roles[0].getRoleID();
		int newRoleID = (roles.length == 1) ? roleID : PermissionHelper.mergeRoles(roles);

		return newRoleID;
	}

	private void ssoInit() {
		if (sso == null) {
			sso = (SSO) Context.getBeanByID("ssoReader");
		}
	}

	private Role[] getRolesBySite(int userLibID, int userID, int siteID) {
		int[] roleIDs = siteUserReader.getRoles(userLibID, userID, siteID);
		if (roleIDs == null || roleIDs.length == 0) return null;

		Role[] roles = new Role[roleIDs.length];
		for (int i = 0; i < roles.length; i++) {
			roles[i] = new Role();
			roles[i].setRoleID(roleIDs[i]);
		}
		return roles;
	}

	/**
	 * 根据类型和站点获取xml列表字符串
	 * @param len 
	 */
	private String getDataList(HttpServletRequest request, int len) {
		StringBuffer result = new StringBuffer(1024);
		try {
			int siteID = request.getParameter("siteID") != null?Integer.parseInt(request.getParameter("siteID")):-1;
			String code = request.getParameter("code");

			result.append("\t\n<data>\t\n");
			//根据类型和站点ID取分组
			Category[] catGroups = InfoHelper.getCatGroups(request, code.toUpperCase(), siteID, len);
			if ("template".equals(code)) {
				catGroups = groupManager.getGroupsWithPower(request, siteID, catGroups);
			}
			if (catGroups != null && catGroups.length > 0) {

				for (int i = 0; i < catGroups.length; i++) {
					if(catGroups[i] != null && catGroups[i].getCatID()>=0){
					int groupID = catGroups[i].getCatID();
					String groupName = catGroups[i].getCatName();
					
					if(!StringUtils.isBlank(groupName))
						groupName = groupName.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
					
					result.append("<group id=\"").append(groupID);
					result.append("\" name=\"").append(groupName).append("\">\t\n");
					Document[] doclist = null;
					//根据code获取列表数据
					if ("template".equalsIgnoreCase(code)) {

						doclist = apiManager.getTemplates(siteID, groupID, request);
					} else if ("block".equalsIgnoreCase(code)) {
						doclist = apiManager.getBlocks(siteID, groupID);
					} else if ("resource".equalsIgnoreCase(code)) {
						doclist = apiManager.getResources(siteID, groupID);
					}
					if (doclist != null && doclist.length > 0) {
						//生成不同类型的列表字符串
						if ("template".equalsIgnoreCase(code)) {
							if(siteID != -1){
								String[] siteDir = readSiteInfo(siteID);
								for (int j = 0; j < doclist.length; j++) {
									result.append(apiManager.getTemplateXML(doclist[j], siteDir[2]));
								}
							}else{
								for (int j = 0; j < doclist.length; j++) {
									int siteId = Integer.parseInt(doclist[j].getString("t_siteID"));
									String[] siteDir = readSiteInfo(siteId);
									result.append(apiManager.getTemplateXML(doclist[j], siteDir[2]));
								}
							
							}
							} else if ("block".equalsIgnoreCase(code)) {
							for (int j = 0; j < doclist.length; j++) {
								result.append(apiManager.getBlockXML(doclist[j]));
							}
						} else if ("resource".equalsIgnoreCase(code)) {
							for (int j = 0; j < doclist.length; j++) {
								result.append(apiManager.getResourceXML(doclist[j]));
							}
						}

					}


					result.append("</group>\t\n");
				
				}}
			}

			result.append("</data>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}


	@SuppressWarnings("unused")
	private String[] getAllDir() throws E5Exception {

		int siteLibID = LibHelper.getSiteLibID();

		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document[] sites = docManager.find(siteLibID,	"SYS_DELETEFLAG=0", null);
		String [] allDir = new String[sites.length];
		int i = 0;
		
		for(Document site:sites){
			if(InfoHelper.readSiteInfo(site.getDocID()).length>2){
			allDir[i] = InfoHelper.readSiteInfo(site.getDocID())[2];
			i++;
			}
		}
	
		return allDir;
	
	}

	private void writeLog(int docLibID, long docID, String usercode, String operation, String detail) throws E5Exception {
		UserReader userReader = (UserReader) Context.getBean(UserReader.class);
		User curUser = userReader.getUserByCode(usercode);
		LogHelper.writeLog(docLibID, docID, curUser.getUserName(), curUser.getUserID(), operation, detail);
	}


	/**
	 * 根据站点得到站点下资源文件的存放目录和发布Url
	 * 返回格式如：["z:\webroot/xy/resource", "http://172.19.33.95/resource"]
	 */
	protected String[] readSiteInfo(int siteID) {
		return InfoHelper.readSiteInfo(siteID);
	}

	//递归生成消息文件
	public static void listfiles(String dir, String rootPath) {
		File file = new File(dir);
		File[] files = file.listFiles();
		if (null == files) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				listfiles(files[i].getAbsolutePath(), rootPath);
			} else {
				// 生成消息文件
				PublishHelper.writeTransPath(files[i].getAbsolutePath(), rootPath);
			}
		}
	}

	//删除文件夹及子文件
	public static void clearDir(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				clearDir(f);
				f.delete();
			}
		}
		file.delete();
	}




	// 验证通过后，把用户的信息存到session里
	void putSession(User curUser, String sPass2, int roleID, int loginID, int newRoleID, HttpServletRequest request) {
		SysUser user = new SysUser();

		user.setUserID(curUser.getUserID());
		user.setUserName(curUser.getUserName());
		user.setUserCode(curUser.getUserCode());

		user.setAdmin(true); // 设为系统管理员，使前台可以设置操作权限（此时不判断文档类型管理权限）
		// user.setUserPassword(sPass2);
		user.setRoleID(roleID);
		user.setLoginID(loginID);
		user.setIp(request.getRemoteAddr());

		user.setRealRoleID(roleID);
		user.setRoleID(newRoleID);

		request.getSession().setAttribute(SysUser.sessionName, user);

		if (needSysAdmin(newRoleID)) {
			// 为了能在前台设置分类、部门角色和权限，加管理端需要的session
			request.getSession().setAttribute(SysUser.sessionAdminName, user);
		}

		// 把租户放在session中（租户代号用于读对应文档库，租户机构ID用于管理部门和角色）
		// 扩展字段1中保存租户代号，扩展字段2保存的是否管理员
		Tenant tenant = tenantManager.get(curUser.getProperty1());
		request.getSession().setAttribute(Tenant.SESSIONNAME, tenant);
	}

	// 判断角色是否有部门角色的主界面权限
	private boolean needSysAdmin(int roleID) {
		if (roleID <= 0)
			return false;

		// 读取tabs
		String[] roleTabs = TabHelper.readRoleTabs(roleID, "MainPermission");
		return ArrayUtils.contains(roleTabs, "scat") || ArrayUtils.contains(roleTabs, "sorg");
	}

	private String getDataListForPage(HttpServletRequest request, Document[] tpldocs) throws Exception {
		String siteId = request.getParameter("siteID");
		StringBuffer result = new StringBuffer(1024);
		result.append("\t\n<data>\t\n");
		//如果模板不为空，拼接分组
		if(tpldocs != null && tpldocs.length>0){
			for(Document doc: tpldocs){
				if(doc != null){
				String[] siteDir = null;
				siteId = doc.getString("t_siteID");
				siteDir = readSiteInfo(Integer.parseInt(siteId));
		        result.append(apiManager.getTemplateXML(doc, siteDir[2]));
				}
			}
		}
		result.append("</data>");
		return result.toString();
	
	}

	private String getDataPage(HttpServletRequest request, int sumGroup) {
		StringBuffer result = new StringBuffer(1024);
		StringBuffer groupResult = new StringBuffer(1024);
		int siteID = request.getParameter("siteID") != null?Integer.parseInt(request.getParameter("siteID")):-1;
		try {
			String code = request.getParameter("code");
			String groupIDstr = request.getParameter("groupID");
			int groupId = StringUtils.isBlank(groupIDstr)?-1:Integer.parseInt(groupIDstr);

			result.append("\t\n<data>\t\n");
			result.append("<site id=\"").append(siteID);
			//根据类型和站点ID取分组
			Category[] catGroups = InfoHelper.getCatGroups(request, code.toUpperCase(), siteID, 0);
			if ("template".equals(code)) {
				catGroups = groupManager.getGroupsWithPower(request, siteID, catGroups);
			}
			if (catGroups != null && catGroups.length > 0) {

				for (int i = 0; i < catGroups.length; i++) {
					Date begin = new Date();
					StringBuffer templateResult = new StringBuffer(1024);
					if(catGroups[i] != null && catGroups[i].getCatID()>=0){
						int groupID = catGroups[i].getCatID();
						String groupName = catGroups[i].getCatName();
						int tempLength = 0;
						
						if(!StringUtils.isBlank(groupName))
							groupName = groupName.replaceAll("\"", "&#34;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
						
						groupResult.append("<group id=\"").append(groupID);
						Document[] doclist = null;
						//根据code获取列表数据
						if ("template".equalsIgnoreCase(code) && groupId == groupID) {
							doclist = apiManager.getTemplatesForPage(siteID, groupID, request);
							if(doclist != null && doclist.length > 0){
								tempLength = doclist.length;
								templateResult = apiManager.getCountType(siteID, groupID, request);
							}
						} 
						groupResult.append("\" name=\"").append(groupName);
						groupResult.append("\" count=\"").append(tempLength).append("\" >\t\n");
						groupResult.append(templateResult);
						groupResult.append("</group>\t\n");
					}
					Date end = new Date();
					System.out.println("分组下type数量详细耗时："+(end.getTime()-begin.getTime()));
				}
			}
			result.append("\" count=\"").append(sumGroup).append("\" >\t\n");
			result.append(groupResult);
			result.append("</site>\t\n");
			result.append("</data>");
		} catch (Exception e) {
			e.printStackTrace();
			result = new StringBuffer(1024);
			result.append("\t\n<data>\t\n");
			result.append("<site id=\"").append(siteID);
			result.append("\" count=\"").append(sumGroup).append("\" >\t\n");
			result.append(groupResult);
			result.append("</site>\t\n");
			result.append("</data>");
		}
		return result.toString();
	}
	
	private String getSiteInfoByDocId(HttpServletRequest request, Document[] tpldocs) {
		String siteId = null;
		if(tpldocs != null && tpldocs.length>0)
			siteId = tpldocs[0].getString("t_siteID");
		StringBuffer result = new StringBuffer(1024);
		result.append("\t\n<data>\t\n");
		result.append("<site id=\"").append(siteId);
		result.append("\"></site>\t\n");
		result.append("</data>");
		return result.toString();
	
	}
	
	private String formatDateByCreate(Document doc) throws ParseException {
		Timestamp createdTime = doc.getTimestamp("SYS_CREATED");
		String date = DateUtils.format(createdTime, "yyyyMM/dd/");  
		return date;
	}
}


