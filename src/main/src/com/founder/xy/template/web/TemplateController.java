package com.founder.xy.template.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.cat.Category;
import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.Pair;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.dom.DocLib;
import com.founder.e5.dom.DocLibReader;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.web.util.UploadHelper;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.FormSaver;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.commons.AntZipUtils;
import com.founder.xy.commons.CatTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.web.GroupManager;
import com.founder.xy.config.Channel;
import com.founder.xy.config.ConfigReader;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.system.site.SiteUserManager;
import com.founder.xy.template.GrantInfo;
import com.founder.xy.template.TemplateService;
import com.founder.xy.template.parser.TemplateParser;

/**
 * 主要是处理 栏目挂接操作
 * @author Isaac_Gu
 *
 */
@Controller
@RequestMapping("/xy/template")
public class TemplateController {
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private TemplateParser templateParser;
	
	private TemplateService templateService;
	
	@Autowired
	private SiteUserManager userManager;

	public TemplateService getTemplateService() {
		return templateService;
	}

	@Resource(name = "templateService")
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	/**
	 * 点击关联栏目时，弹出关联栏目。
	 * 调用弹出的树，并且在树上标记该模版已经关联上的栏目。
	 * @param parameters
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("grantColumn.do")
	public String grantColumn(GrantInfo parameters, Map<String, Object> model)
			throws Exception {

		parameters = templateService.findIds(parameters);
		//把下面的参数推给前台以备后面进一步操作使用
		model.put("siteID", parameters.getSiteID());
		model.put("ids", parameters.getRt_ids());
		model.put("type", "admin");
		model.put("docIDs", parameters.getDocIDs());
		model.put("docLibID", parameters.getDocLibID());
		model.put("templateColumnName", parameters.getDb_templateColumnName());
		model.put("templateRealName", parameters.getDb_templateRealName());
		model.put("UUID", parameters.getUUID());
		//		return "redirect:/xy/column/TemplateGrantColumn.jsp"+SomeUtils.assembleURLParameters(model);
		return "/xy/template/GrantColumn";
	}
	
	/**
	 * 查看引用栏目
	 * 由于rule太长，配置操作时有长度限制，无法配置，因此做个跳转
	 * @param parameters
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("viewQuoteColumn.do")
	public String viewQuoteColumn(GrantInfo parameters, Map<String, Object> model) throws Exception {
		parameters = templateService.findIds(parameters);
		String rule = "(col_template_ID_EQ_@DOCID@_OR_col_templateArticle_ID_EQ_@DOCID@_OR_col_templatePic_ID"
				+ "_EQ_@DOCID@_OR_col_templateVideo_ID_EQ_@DOCID@_OR_col_templatePad_ID_EQ_@DOCID@"
				+ "_OR_col_templateArticlePad_ID_EQ_@DOCID@_OR_col_templatePicPad_ID_EQ_@DOCID@"
				+ "_OR_col_templateVideoPad_ID_EQ_@DOCID@)";
		String url = "/e5workspace/DataMain.do?type=COLUMN"
				+"&rule="+rule+"&noOp=1&list=已删栏目列表&DocLibID="+parameters.getDocLibID()
				+"&DocIDs="+parameters.getDocIDs()+"&FVID="+parameters.getFVID()+"&UUID="
				+parameters.getUUID()+"&siteID="+parameters.getSiteID();
		return "redirect:"+url;
	}

	/**
	 * 给模版挂接新的栏
	 * 1. 操作成功 返回success
	 * 2. 没有操作 返回success ？ FIXME
	 * 3. 操作失败 返回failure 
	 * @param parameters
	 * @throws Exception
	 */
	@RequestMapping(value = "changeColumnTemplateAjax.do", method = RequestMethod.POST)
	public void changeColumnTemplate(HttpServletRequest request, HttpServletResponse response,
			GrantInfo parameters) throws Exception {
		
		int colLibID = LibHelper.getColumnLibID(request);
		parameters.setGroupID(colLibID+"");
		//进行挂接栏目的操作
		parameters = templateService.changeColumnTemplate(parameters);
		//如果操作成功，在栏目里写日志
		if (parameters.getRt_operationResult() != null
				&& parameters.getRt_operationResult().trim().substring(0, 7).equals("success")) {
			SysUser sysUser = ProcHelper.getUser(request);
			templateService.writeColumnLog(request, sysUser, parameters);
		}
		PublishTrigger.templateGrant(Integer.parseInt(parameters.getDocLibID()), Integer.parseInt(parameters.getDocIDs()), StringUtils.getIntArray(parameters.getIds()));
		//回传消息
		InfoHelper.outputText(parameters.getRt_operationResult(), response);

	}
	
	/**
	 * 根据type获取模板类型列表，根据分组，组成一级树
	 * @param siteID 站点
	 * @param type	模板类型
	 * @throws Exception 
	 */
	@RequestMapping(value = "Template.do")
	public void templateTree(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID,@RequestParam int type,@RequestParam int channel) throws Exception {
		
			Document[] tpldocs = templateService.getTempaltes(siteID, type,channel);
			int catType = CatTypes.CAT_TEMPLATE.typeID();
		
			Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
			groups = groupManager.getGroupsWithPower(request, siteID,groups);
			JSONArray templates = getJSONArr(siteID,channel,type,groups,tpldocs);

			InfoHelper.outputJson(templates.toString(), response);
	}
	
	/**
	 * 模板根据名称查找功能
	 * @param siteID 站点
	 * @param type	模板类型
	 * @param q	输入查询名字
	 * @throws Exception 
	 */
	@RequestMapping(value = "Find.do")
	public void find(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID,@RequestParam String q,
			@RequestParam int type,@RequestParam int channel) throws Exception {
		if (q != null) q = q.trim();
		boolean flag= q != null && q.matches("[0-9]+");
		Document[] tpldocs = templateService.find(siteID, type,channel,q,flag);
		String result = json(tpldocs);
		InfoHelper.outputJson(result, response);
	}
	
	/**保存完毕后，进行解析模板*/
	@RequestMapping(value = "FormSave.do")
	public String formSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormSaver formSaver = (FormSaver)Context.getBean(FormSaver.class);
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String siteId = request.getParameter("t_siteID");
		int siteID = Integer.parseInt(siteId);
		String[] siteDir = readSiteInfo(siteID);
		
		Document doc = null;
		File resZipFile = null;
		String tempPath = null;
		String resPath = null;
		Pair changed;
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");	
		Boolean format = checkFile(request);
		if(!format){
			return "redirect:/xy/template/error.do?error=-7";
		}
		try {
			changed = formSaver.handleChanged(request);
		} catch (Exception e) {
			url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
			return "redirect:" + url;
		}
		
		String id = request.getParameter("DocID");
		boolean isNew = (StringUtils.isBlank(id) || "0".equals(id));
		
		long docID = Long.parseLong(changed.getKey());
		String changeValue = String.valueOf(changed.getValue());
		int docLibID = LibHelper.getTemplateLibID();
		if (isNew){
			doc = docManager.get(docLibID, docID);
			url += "&DocIDs=" + docID;
		} else {
			doc = docManager.get(docLibID, Long.parseLong(id));
			url += "&DocIDs=" + docID;
			if (!StringUtils.isBlank(changeValue))
				url += "&Opinion=" + URLEncoder.encode(changeValue, "UTF-8");
		}
		if(changeValue.contains("模板文件:") || isNew){
			//文件的全路径
			String oldPath = getOldPath(doc, null);
			tempPath = oldPath;
			String tFile = doc.getString("T_FILE");
			Date date = formatFileDate(tFile);
			String resource = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(date, "yyyyMM/dd/") + docID;
			if (!isNew)
			cleanOldTemp(doc, resource);
			
			//获取文件路径，并判断是否为压缩文件
			String zipPath = oldPath.length()>4?oldPath.substring(oldPath.lastIndexOf(".")+1, oldPath.length()):null;
			if(zipPath != null && "zip".compareToIgnoreCase(zipPath)==0){
				rename(doc, null);
				oldPath = getOldPath(doc, null);
				resZipFile = new File(oldPath);
				if (!resZipFile.getParentFile().exists()) {
					resZipFile.getParentFile().mkdirs();
				}
				//解压ZIP文件，并返回解压后文件路径
				int m = resZipFile.getPath().lastIndexOf(".");
				File oldRes = new File(resZipFile.getPath().substring(0, m));
					if (oldRes.exists()) {
						deleteFile(oldRes);
				}
				String[] paths = AntZipUtils.unZipForPath(resZipFile, resZipFile.getPath().substring(0, m));
				String checkType = null;
				if(paths.length>0)	
					for(int i=0;i<paths.length;i++){
						if(paths[i].startsWith("1")){
							tempPath = paths[i].substring(2);
							checkType = tempPath;
						}
						else
							resPath = paths[i].substring(2);
					}
				if(paths.length>2 || checkType==null){
					if(isNew){
						templateService.deleteTpl(docLibID, docID, isNew);
					}
					return "redirect:/xy/template/error.do?error=-7";
				}
				/*if(resPath != null)
					resPath = renameRes(resPath, doc.getString("SYS_DOCUMENTID"));*/
			}
			
			String reliPath = "";
			//模板文件发生了变化，需要重新解析
			if(isNew || changeValue.contains("模板文件:") || resPath != null){
				//解析前先用UUID重新命名一下名字
				rename(doc, tempPath);
				if (resZipFile != null) {
					//压缩改名后的文件，并删除原压缩文件
					resZipFile.delete();
					String zipPathNew = oldPath.substring(0, oldPath.lastIndexOf("."));
					File resDir = new File(zipPathNew);
					if (resDir.exists()) {
						resZipFile = new File(zipPathNew + ".zip");
						// 压缩文件
						AntZipUtils.makeZip(resDir.listFiles(), zipPathNew + ".zip");
					}
				}
				reliPath = getOldPath(doc, tempPath);
				templateParser.parse(doc,siteDir,reliPath);
			}
			//通知发布服务
			PublishTrigger.otherData(doc.getDocLibID(), doc.getDocID(), DocIDMsg.TYPE_TEMPLATE);
			if(zipPath != null && "zip".compareToIgnoreCase(zipPath)==0 && resPath!= null){
				DwApiController.uploadResource(resPath, siteID, reliPath, siteDir, request, response);
			}
		}
		return "redirect:" + url;
	}
	
	/**
	 * 校验压缩文件是否格式正确
	 * 校验完毕需删除解压后的文件
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	private Boolean checkFile(HttpServletRequest request) throws IOException {
		Boolean format = true;
		String oldPath = request.getParameter("t_file");
		File resZipFile = null;
		String tempPath = null;
		if(StringUtils.isBlank(oldPath))
			return format;
		//获取文件路径，并判断是否为压缩文件    模板存储;201803/26/zfq_4044.zip
		String zipPath = oldPath.length()>4?oldPath.substring(oldPath.lastIndexOf(".")+1, oldPath.length()):null;
		if(zipPath != null && "zip".compareToIgnoreCase(zipPath)==0){
			int pos = oldPath.indexOf(";");
			String device = oldPath.substring(0, pos); //存储设备
			String rltPath = oldPath.substring(oldPath.indexOf(";") + 1); //相对路径和文件名
			String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
			oldPath = devicePath + File.separator + rltPath;
			resZipFile = new File(oldPath);
			if (!resZipFile.getParentFile().exists()) {
				resZipFile.getParentFile().mkdirs();
			}
			//解压ZIP文件，并返回解压后文件路径
			int m = resZipFile.getPath().lastIndexOf(".");
			File oldRes = new File(resZipFile.getPath().substring(0, m));
				if (oldRes.exists()) {
					deleteFile(oldRes);
			}
			String[] paths = AntZipUtils.unZipForPath(resZipFile, resZipFile.getPath().substring(0, m));
			String checkType = null;
			if(paths.length>0)	
				for(int i=0;i<paths.length;i++){
					if(paths[i].startsWith("1")){
						tempPath = paths[i].substring(2);
						checkType = tempPath;
					}
				}
			if(paths.length>2 || checkType==null){
				format = false;
			}
			if (oldRes.exists()) {
				deleteFile(oldRes);
		}
		}
		return format;
	}

	/**保存完毕后，进行解析模板*/
	@RequestMapping(value = "error.do")
	public ModelAndView returnError(String error) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/xy/template/Error");
		model.addObject("error", error);
		return model;
	}

	private String renameRes(String resPath, String docID) {
		File oldFile = new File(resPath);
//		resPath = resPath.substring(0, resPath.lastIndexOf("\\")); 
		resPath = resPath.substring(0, resPath.lastIndexOf(File.separator));
		resPath = resPath + File.separator + docID;
		File newFile = new File(resPath);
		if (oldFile.renameTo(newFile)) 
			return newFile.getPath();
		else
			return oldFile.getPath();
		
	}

	//上传模板文件，需判断并删掉原文件、原压缩文件、原资源文件
	private void cleanOldTemp(Document doc, String resource) throws Exception {
		//文件的全路径
		String oldZipPath = getOldPath(doc, "find");
		if(new File(oldZipPath).exists())
			deleteFile(new File(oldZipPath));
		if(new File(resource).exists())
			deleteFile(new File(resource));
		if(new File(oldZipPath + ".zip").exists())
			new File(oldZipPath + ".zip").delete();
		new File(oldZipPath).mkdirs();

		
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
	    	files[i].delete(); 
	    }  
	    file.delete();  
	}

	@RequestMapping(value = "loadZip.do")
	public void loadZip(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long DocID = WebUtil.getLong(request, "DocID", 0);
        int doclibID = WebUtil.getInt(request, "doclibID", 0);
		Boolean isZip = true;
		Boolean downLoad = true;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document tpldoc = docManager.get(doclibID, DocID);
		if(tpldoc == null)
			return;
		
		String tFile = tpldoc.getString("T_FILE");
		String suffix = tpldoc.getString("t_fileType");
		String siteID = tpldoc.getString("T_SITEID");
		String[] siteDir = readSiteInfo(Integer.parseInt(siteID));
		Date date = formatFileDate(tFile);  
		
		//获取写入文件的全路径
		String devicePath = getDevicePath();
		String oldDir = devicePath + File.separator + tFile.substring(tFile.indexOf(";")+1, tFile.length());
		String path = devicePath + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
		
		//获取资源文件存储位置
		String resPath = siteDir[1] + File.separator + "templateRes" + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
		
		String zipPath = null;
		if(!StringUtils.isBlank(path) && !StringUtils.isBlank(suffix)){
			zipPath = path + "." + suffix;
		}

		request.setCharacterEncoding("UTF-8");

		File zipDir = new File(zipPath);
		File resDir = new File(resPath);
		
		//兼容新旧版模板存储目录
		if(!zipDir.exists())
			zipDir = new File(path + File.separator + DocID + "." + suffix);
		if(!zipDir.exists()){
			zipDir = new File(oldDir);
			//如果存在，则为旧模板，此处进行重命名操作
			if(zipDir.exists()){
				rename(tpldoc, oldDir);
				zipDir = new File(path + File.separator + DocID + "." + suffix);
			}
		}
		//如果压缩文件不存在，则标识模板非压缩文件，并进行压缩
		if(!new File(path + ".zip").exists()){
			isZip = false;
			downLoad = false;
			//只有模板和资源文件同时存在才进行压缩
			if (zipDir.exists() && resDir.exists()){
				File fa[] = resDir.listFiles();
				File[] fileList = new File[fa.length+1];
				fileList[0]=zipDir;
				for (int i = 0; i < fa.length; i++) {
					File fs = fa[i];
					fileList[i+1]=fs;
			     
				}
				AntZipUtils.makeZip(fileList, path + ".zip");
				zipDir = new File(path + ".zip");
				if(zipDir.exists())
					downLoad = true;
			}
		}
		
		//如果资源文件不存在，则只下载模板文件，不下载压缩包
		if (zipDir.exists() && (downLoad || !resDir.exists())) {
			File resZipFile = new File(path + ".zip");
			if(!resDir.exists())
				resZipFile = zipDir;
			// 临时文件输出到response
			InputStream in = new FileInputStream(resZipFile);
			OutputStream out = null;
			try {
				//自动判断下载文件类型
				response.setContentType("multipart/form-data");
				if(!resDir.exists())
					response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(DocID + "."+ suffix, "UTF-8"));
				else
					response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(DocID + ".zip", "UTF-8"));
				out = response.getOutputStream();
				IOUtils.copy(in, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ResourceMgr.closeQuietly(in);
				ResourceMgr.closeQuietly(out);
				//如果不是压缩文件且目标压缩文件存在，则需每次传输完毕后删除目标压缩文件
				if(!isZip && new File(path + ".zip").exists())
					zipDir.delete();
				InfoHelper.outputText("-3", response);
			}
		} else {
			InfoHelper.outputText("-4", response);
			return;
		}
	
	}
	@RequestMapping(value = "uploadFile.do")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String savePath = null;
		InputStream in = null;
		try {
			FileItem file = UploadHelper.getFileItem(request);
			System.out.println(file.getName());
			boolean fileType = ((file.getName().toLowerCase()).endsWith("zip") || (file.getName().toLowerCase()).endsWith("html") || (file.getName().toLowerCase()).endsWith("htm")); 
			boolean type = ((file.getName().substring(0, file.getName().lastIndexOf("."))).contains("."));
			if(type || !fileType)
				throw new Exception("文件格式错误！");
			savePath = getSavePath(request, file);
			StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
			in = file.getInputStream();
			sdManager.write("附件存储", savePath, in);
		} catch (Exception e) {
			String result = "0;" + e.getLocalizedMessage();
			outputQuietly(result, response);
		} finally {
			ResourceMgr.closeQuietly(in);
		}
		
		String result = "1;" + "附件存储;" + savePath;
		outputQuietly(result, response);
	}
	
	private void outputQuietly(String result, HttpServletResponse response) {
		try {
			response.setContentType("text/plain; charset=UTF-8");
			
			PrintWriter out = response.getWriter();
			out.write(result);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getSavePath(HttpServletRequest request, FileItem file) {
		String fullName = file.getName();
		String fileName = fullName.substring(fullName.lastIndexOf(File.separator) + 1);
		
		String userCode = ProcHelper.getUserCode(request);
		String savePath = DateUtils.format("yyyyMM/dd/") + userCode + "_" + fileName; // "201504/23/gonglijie_�ҵ��ļ�.pdf"
		
		return savePath;
	}

	private Date formatFileDate(String tFile) throws ParseException {
		String tplDate = tFile.substring(tFile.indexOf(";")+1, tFile.indexOf(";")+11).replaceAll("\\\\", "/");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM/dd/");//格式化模板日期路径  
		Date date = sdf.parse(tplDate);  
		return date;
	}
	
	private String formatDateByCreate(Document doc) throws ParseException {
		Timestamp createdTime = doc.getTimestamp("SYS_CREATED");
		String date = DateUtils.format(createdTime, "yyyyMM/dd/");  
		return date;
	}

	@RequestMapping(value = "getDoc.do")
	public void getDoc(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long DocID = WebUtil.getLong(request, "DocID", 0);
        int doclibID = WebUtil.getInt(request, "doclibID", 0);
		String oldDir = null;
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		Document tpldoc = docManager.get(doclibID, DocID);
		if(tpldoc == null){
			InfoHelper.outputText("false", response);
			return;
		}
			
		String tFile = tpldoc.getString("T_FILE");
		String suffix = tpldoc.getString("t_fileType");
		Date date = formatFileDate(tFile);  
		
		//获取写入文件的全路径
		String devicePath = getDevicePath();
		oldDir = devicePath + File.separator + tFile.substring(tFile.indexOf(";")+1, tFile.length());
		String path = devicePath + File.separator + DateUtils.format(date, "yyyyMM/dd/") + DocID;
		
		String zipPath = null;
		if(!StringUtils.isBlank(path) && !StringUtils.isBlank(suffix)){
			zipPath = path + "." + suffix;
		}

		request.setCharacterEncoding("UTF-8");

		File zipDir = new File(zipPath);
		//兼容新旧版模板存储目录
		if(!zipDir.exists())
			zipDir = new File(path + File.separator + DocID + "." + suffix);
		if(!zipDir.exists())
			zipDir = new File(oldDir);
		//判断模板是否存在，若不存在返回错误信息
		if (!zipDir.exists()){
			InfoHelper.outputText("false", response);
			return;
		}
		InfoHelper.outputText("success", response);
		return;
	}

	private String getDevicePath() throws E5Exception {
		//获取存储设备名
		int docLibID = LibHelper.getTemplateLibID();
		DocLibReader libReader = (DocLibReader) Context.getBean(DocLibReader.class);
		DocLib docLib = libReader.get(docLibID);
		String deviceName = docLib.getStorageDevice();
		//获取写入文件的全路径
		return InfoHelper.getDevicePath(InfoHelper.getDeviceByName(deviceName));
	}

	private String[] readSiteInfo(int siteID) {
		return InfoHelper.readSiteInfo(siteID);
	}

	private String getOldPath(Document doc, String tempPath) throws Exception {
		
		String fileName = doc.getString("t_file");
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); //存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); //相对路径和文件名
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
		String oldPath = devicePath + File.separator + rltPath;
		String newrltPath = formatDateByCreate(doc) + doc.getDocID();
		if("find".equals(tempPath)){
			return devicePath + File.separator + newrltPath;
		}
		if("".equals(tempPath) || tempPath == null)
			return oldPath;
		else
			return newrltPath;
		
	}

	/**
	 * 删除选中的模板
	 */
	@RequestMapping(value = "Delete.do")
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			StringBuilder result = new StringBuilder();
			int docLibID = WebUtil.getInt(request, "DocLibID", 0);
			long docID = WebUtil.getLong(request, "DocIDs", 0);
			
			boolean bUsed = templateService.tplUsed(docLibID, docID);
			if(!bUsed){
				templateService.deleteTpl(docLibID, docID, false);
			}
			if(bUsed){
				result.append("模板被引用，删除失败");
			} else {
				PublishTrigger.otherData(docLibID, docID, DocIDMsg.TYPE_TEMPLATE);
			}
			// 记录日志
			writeLog(docLibID, docID, ProcHelper.getUser(request));
			InfoHelper.outputText("@refresh@"+result.toString(), response);	//操作成功		
		}catch(Exception e) {
		}
		
		return "";
	}
	
	private void writeLog(int docLibID, long docID, SysUser user) throws E5Exception {
		LogHelper.writeLog(docLibID, docID, user, "删除", "删除模板");
	}

	/**
	 * 用于模板表单，单渠道时做必要的发布渠道隐藏
	 */
	@RequestMapping(value = "Channels.do")
	public void getChannels(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Channel[] chs = ConfigReader.getChannels();
		String value = null;
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] != null)
				value = (value == null) ? String.valueOf(i) : value + "," + i;
		}
		InfoHelper.outputText(value, response);
	}

	/**
	 * 将获取到的模板列表转化为JSON
	 * @param siteID 站点ID
	 * @param type    模板类型
	 * @param groups 模板分组（作为模板树的第一级）
	 * @param tpldocs    符合type的模板
	 * @throws E5Exception
	 */
	private JSONArray getJSONArr(int siteID, int channel, int type, Category[] groups, Document[] tpldocs) throws E5Exception{
		JSONArray templates = new JSONArray();
		String catIDAppend = "_group";
		Set<Integer> powerGroupIDs = new HashSet<>();
		//将分组加入JSON
		for (Category group : groups) {
			Map<String, String> json = new HashMap<String, String>();
			//判断该分组下是否有该模板类型，若没有该模板类型，就不需要将其加入到显示的json数据中
			boolean bHas = templateService.hasChild(siteID,channel,group.getCatID(),type);
			if(bHas){
				//特殊处理组ID，因为组id与模板ID不在一个数据库，有可能重复所以组ID特殊处理一下，加一个"_group"以示区分
				json.put("id",String.valueOf(group.getCatID())+catIDAppend);
				json.put("name",InfoHelper.filter4Json(group.getCatName()));	
				json.put("pid", "0");
				templates.add(json);
				powerGroupIDs.add(group.getCatID());
			}
		}
		//将模板列表转化为JSON格式
		for (Document tpl : tpldocs) {

			if (powerGroupIDs.contains(tpl.getInt("t_groupID"))) {
				Map<String, String> json = new HashMap<String, String>();
				json.put("id", String.valueOf(tpl.getDocID()));
				json.put("name", InfoHelper.filter4Json(tpl.getString("t_name")));
				json.put("pid", InfoHelper.filter4Json(tpl.getString("t_groupID") + catIDAppend));
				templates.add(json);
			}
		}
		return templates;	
	}

	//查找结果的json，格式为[{key,value},{key,value},...]
	private String json(Document[] tpldocs) throws E5Exception {
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		for (Document tpl : tpldocs) {
			if (result.length() > 1) result.append(",");
			
			result.append("{\"value\":\"").append(InfoHelper.filter4Json(tpl.getString("t_name")))
				.append("\",\"key\":\"").append(String.valueOf(tpl.getDocID()))
				.append("\"}");
		}
		result.append("]");
	
		return result.toString();
	}
	
	//模板上传后，重新以新uuid命名，避免覆盖
	private void rename(Document doc, String tempPath) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String fileName = doc.getString("t_file");
		
		int pos = fileName.indexOf(";");
		String device = fileName.substring(0, pos); //存储设备
		String rltPath = fileName.substring(fileName.indexOf(";") + 1); //相对路径和文件名
		
		//文件的全路径
		String devicePath = InfoHelper.getDevicePath(InfoHelper.getDeviceByName(device));
		String oldPath = tempPath==null?devicePath + File.separator + rltPath:tempPath;
		String zipPath = oldPath.length()>4?oldPath.substring(oldPath.lastIndexOf(".")+1, oldPath.length()):null;
		String newrltPath = formatDateByCreate(doc) + doc.getDocID();
		if(tempPath != null && doc.getString("T_FILETYPE") == "zip")
			newrltPath +=  File.separator+doc.getDocID();
		if(zipPath != null && "zip".compareToIgnoreCase(zipPath)!=0)
			newrltPath +=  File.separator+doc.getDocID()+"."+zipPath;
		else
			newrltPath +=  "."+zipPath;
		
		String newPath = devicePath + File.separator + newrltPath;
		
		//改名并且写回数据库
		File oldFile = new File(oldPath);
		if (oldFile.exists()){
			File newFile = new File(newPath);
			newFile.getParentFile().mkdirs();
			if (newFile.exists() && newPath.endsWith("zip"))//
				newFile.delete();
			if (oldFile.renameTo(newFile)) {
				String newName = device + ";" + newrltPath;
				doc.set("t_file",newName);
				doc.set("T_FILETYPE",zipPath);
				docManager.save(doc);
			}
		}
	}
	
	/**
	 * 点击移动分组时，弹出全部分组列表。
	 * 调用弹出的树，并且在树上标记该模版当前所在分组。
	 * 注意系统是否涉及分组的权限控制。
	 * @param parameters
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("moveChannel.do")
	public void moveChannel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID) throws Exception {
		
			int catType = CatTypes.CAT_TEMPLATE.typeID();
		
			Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
			groups = groupManager.getGroupsWithPower(request, siteID,groups);
			JSONArray templates = getJSONArr(siteID, groups);

			InfoHelper.outputJson(templates.toString(), response);
	}

	private JSONArray getJSONArr(int siteID, Category[] groups) {
		JSONArray templates = new JSONArray();
		String catIDAppend = "_group";
		Set<Integer> powerGroupIDs = new HashSet<>();
		//将分组加入JSON
		for (Category group : groups) {
			Map<String, String> json = new HashMap<String, String>();
			//特殊处理组ID，因为组id与模板ID不在一个数据库，有可能重复所以组ID特殊处理一下，加一个"_group"以示区分
			json.put("id",String.valueOf(group.getCatID())+catIDAppend);
			json.put("name",InfoHelper.filter4Json(group.getCatName()));	
			json.put("pid", "0");
			templates.add(json);
			powerGroupIDs.add(group.getCatID());
		}
		return templates;	
	}
	
	/**
	 * 模板分组根据名称查找功能
	 * @param siteID 站点
	 * @param q	输入查询名字
	 * @throws Exception 
	 */
	@RequestMapping(value = "FindChannel.do")
	public void findChannel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int siteID,@RequestParam String q) throws Exception {
		int catType = CatTypes.CAT_TEMPLATE.typeID();
		if (q != null) q = q.trim();
		Category[] groups = InfoHelper.getCatGroups(request, catType, siteID);
		groups = groupManager.getGroupsWithPower(request, siteID,groups);
		String result = jsonForGroup(groups, q);
		InfoHelper.outputJson(result, response);
	}
	
	private String jsonForGroup(Category[] groups, String q) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		
		for (Category cat : groups) {
			String name = String.valueOf(q);
			if(name.equals(cat.getCatName())){
				if (result.length() > 1) result.append(",");
				
				result.append("{\"value\":\"").append(InfoHelper.filter4Json(cat.getCatName()))
					.append("\",\"key\":\"").append(String.valueOf(cat.getCatID()))
					.append("\"}");
			}
		}
		result.append("]");
	
		return result.toString();
	}

	/**保存调整分组后的模板*/
	@RequestMapping(value = "tmlSave.do")
	public String tmlSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentManager docManager = DocumentManagerFactory.getInstance();
		String groupid = request.getParameter("id");
		String channelName = request.getParameter("name");
		
		Document doc = null;
		String url = "/e5workspace/after.do?UUID=" + WebUtil.get(request, "UUID");	
		
		String id = request.getParameter("DocID");
		
		int docLibID = LibHelper.getTemplateLibID();
		doc = docManager.get(docLibID, Long.parseLong(id));
		url += "&DocIDs=" + id;
		if (!StringUtils.isBlank(channelName))
			url += "&Opinion=" + URLEncoder.encode(channelName, "UTF-8");
		
		doc.set("t_groupid", Integer.parseInt(groupid));
		docManager.save(doc);
		return "redirect:" + url;
	}
	
}
