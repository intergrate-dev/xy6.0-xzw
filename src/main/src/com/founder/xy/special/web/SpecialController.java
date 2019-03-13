package com.founder.xy.special.web;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.db.DBSession;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.doc.util.E5docHelper;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.e5.workspace.app.form.LogHelper;
import com.founder.xy.api.dw.DwApiController;
import com.founder.xy.article.Article;
import com.founder.xy.article.ArticleManager;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.commons.PicHelper;
import com.founder.xy.jms.PublishTrigger;
import com.founder.xy.jms.data.DocIDMsg;
import com.founder.xy.jpublish.JsoupHelper;
import com.founder.xy.jpublish.PublishHelper;
import com.founder.xy.jpublish.page.ColumnGenerator;
import com.founder.xy.set.web.AbstractResourcer;
import com.founder.xy.special.SpecialManager;
import com.founder.xy.template.parser.TemplateParser;
import com.founder.xy.ueditor.web.UeditorControl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 专题设计的相关功能
 *
 * @author Gong Lijie
 */
@Controller
@RequestMapping("/xy/special")
public class SpecialController extends AbstractResourcer {

    @Autowired
    SpecialManager specialManager;
    @Autowired
    private TemplateParser templateParser;
    
    @Autowired
    ArticleManager articleManager;
    
    private static final Logger log = LoggerFactory.getLogger(SpecialController.class);

    /**
     * 专题设计的新建、修改
     */
    @RequestMapping(value = "Special.do")
    public ModelAndView special(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        boolean isNew = (docID == 0); //是否新建
        log.error("---打开专题是否存在:" + isNew);
        if (isNew)
            docID = InfoHelper.getNextDocID(DocTypes.SPECIAL.typeID()); //取ID

        Map<String, Object> model = new HashMap<>();

        int templateID = 0;
        String specialName = "";
        if (!isNew) {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);
            templateID = doc.getInt("s_templateID");
            specialName = doc.getString("s_name");
        }

        int siteID = WebUtil.getInt(request, "siteID", 1);
        model.put("isNew", isNew);
        model.put("docLibID", docLibID);
        model.put("docID", docID);
        model.put("siteID", siteID);
        model.put("groupID", WebUtil.get(request, "groupID"));
        model.put("UUID", WebUtil.get(request, "UUID"));
        model.put("templateID", templateID);
        model.put("imgurl", "image.do?siteID=" +
                siteID +
                "&path=");
        model.put("serverurl", readSiteInfo(siteID)[2]);

        model.put("specialName", specialName);

        return new ModelAndView("/xy/special/Special", model);
    }

    /**
     * 专题设计保存
     * 3）	需要在两个表中添加记录：专题表、模板表。
     * 4）	模板文件上传后用StorageDeviceManager.write写入模板存储。
     * 5）	模板记录保存在模板表中，名称为“专题模板：<专题设计的名称>”，模板路径为上面一步的存储路径，groupID设为0。
     * 6）	与其它模板一样，保存后需调用模板解析得到组件，并触发模板消息发送。参考类TemplateController的formSave方法。
     * 7）	调用SpecialManager的convertDeviceFile自动生成可视图片（传入参数是模板文件的存储地址）
     * 8）	保存专题表，添加名称、可视图片地址、模板ID。
     * 组ID=0不会在模板管理界面中按组展示时出现。
     * 若希望能看到，则应保存在单独的“专题模板组”中（保存时检查和自动创建这个组）
     */
    @RequestMapping(value = "Save.do")
    public void save(
            HttpServletResponse response, String htmlData, String designHtml,String mobileHtmlData, 
            String canvasurl,
            Integer docLibID, Long docID, Integer siteID, Integer groupID, String s_name, HttpServletRequest request) {
        String expdate = request.getParameter("expDate");
        boolean status = true;
        //如果不是新的，那取出模版对象
        try {
            Document special = specialManager.getDocument(docLibID, docID);
            boolean isNew = (special == null);
            
            //图片发布外网
            publishALLPics(docLibID, docID, siteID);
            
            //替换图片地址为外网地址
            htmlData = replaceURL(htmlData, siteID, true);
            designHtml = replaceURL(designHtml, siteID, true);
            if(!StringUtils.isBlank(mobileHtmlData))
            	mobileHtmlData = replaceURL(mobileHtmlData, siteID, true);
            
            docID = saveSpecial(htmlData, designHtml, mobileHtmlData, docLibID, docID, siteID, groupID, s_name, special, isNew, null,
                                expdate, canvasurl);
            log.error("---保存专题ID---:" + docID);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        JSONObject json = new JSONObject();
        log.error("---专题保存状态:" + status);
        json.put("status", status);
        json.put("docID", docID);
        InfoHelper.outputJson(json.toString(), response);
        /*String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + _id;
        return "redirect:" + url;*/
    }

	private void publishALLPics(Integer docLibID, Long docID, Integer siteID) throws E5Exception {
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        int attLibID = LibHelper.getAttaLibID();
        String[] siteDir = readSiteInfo(siteID);
        Object[] params = new Object[]{docID, docLibID};
        String conditions = "SYS_DELETEFLAG=0 and att_articleID=? and att_articleLibID=?";
        Document[] docs = docManager.find(attLibID, conditions, params);
        for(Document doc:docs)
        	if(doc != null){
        		String filePath = doc.getString("att_path");
        		log.error("---发布图片至外网，本地路径:" + filePath);
        		if(!StringUtils.isBlank(filePath) && new File(filePath).exists())
        			PublishHelper.writeTransPath(filePath, siteDir[0]);
        			
        	}
	}

	private String replaceURL(String htmlData, Integer siteID, Boolean bo) {
    	String[] siteDir = readSiteInfo(siteID);
    	String newrltPath = siteDir[2];
    	String str = "image.do?siteID="+siteID+"&amp;path=";
    	log.error("---替换文本内容URL地址："+siteDir[2]);
    	log.error("---外网替换成内网："+bo);
    	if(bo)
    		htmlData = htmlData.replace(str, newrltPath);
    	else
    		htmlData = htmlData.replace(newrltPath, str);
    		
    	return htmlData;
	}

	private Long saveSpecial(
            String htmlData, String designHtml, String mobileHtmlData, Integer docLibID, Long docID, Integer siteID, Integer groupID,
            String s_name, Document special, boolean isNew, String copyTargetPath, String expdate, String canvasurl) {
        Document template = null;
        Document templateMobile = null;
        String specialPath = null;
        String templatePath = null;
        String templateMobilePath = null;
        //判断是否为新模版

        if (!isNew) {
    		//1--触屏；0--web
    		template = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templateID"));
			templateMobile = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templatePADID"));
			specialPath = special.getString("s_file");
			templatePath = template.getString("t_file");
			if(templateMobile != null)
				templateMobilePath = templateMobile.getString("t_file");
        }
        log.error("---是否新模板--:" + isNew);
        //1. 保存专题文件, 2.  保存设计稿
        //专题模版的路径
        templatePath = specialManager.saveAsShowFile(templatePath, htmlData, "web");
        //专题触屏模版的路径
        if(!StringUtils.isBlank(mobileHtmlData))
        	templateMobilePath = specialManager.saveAsShowFile(templateMobilePath, mobileHtmlData, "pad");
        //保存设计稿
        specialPath = specialManager.saveAsDesignFile(specialPath, designHtml);
        log.error("---专题模版的路径:" + templatePath);
        log.error("---专题触屏模版的路径:" + templateMobilePath);
        log.error("---设计稿的路径:" + specialPath);
        

        //把数据入库， 如果模版路径不为空，可以进行以下的操作
        //保存到模版表
        if (!StringUtils.isBlank(templatePath)) {
            template = specialManager.saveTemplate(template, templatePath, siteID, groupID, s_name, expdate);
        }
        //保存到模版表--触屏
        if (!StringUtils.isBlank(templateMobilePath)) {
        	templateMobile = specialManager.saveTemplate(templateMobile, templateMobilePath, siteID, groupID, s_name, expdate);
        }
        String picPath;
        if (template != null) {
            if (copyTargetPath != null) {
                picPath = specialManager.copyFile(copyTargetPath, template.getString("t_file"));
            } else {
                //7）	调用SpecialManager的convertDeviceFile自动生成可视图片（传入参数是模板文件的存储地址）
                //specialManager.convertDeviceFile( template.getString("t_file"));
                // "../xy/special/images/special.png";
                //picPath = specialManager.convertDeviceFile(template.getString("t_file"));

                UeditorControl.setProxy();
//                picPath = specialManager.htmlTohumbnail(template.getString("t_file"));
                //缩略图
                picPath = specialManager.htmlTohumbnail(template.getString("t_file"), canvasurl);
                log.error("---缩略图的路径:" + picPath);

            }
            Long templateMobileId = null;
            if (templateMobile != null)
            	templateMobileId = templateMobile.getDocID();
            //8）	保存专题表，添加名称、可视图片地址、模板ID。
            docID = specialManager.saveSpecialModel(special, specialPath, docLibID, docID, siteID, groupID,
                                                    template.getDocID(), templateMobileId, s_name, picPath, expdate);
            //6）	与其它模板一样，保存后需调用模板解析得到组件，并触发模板消息发送。参考类TemplateController的formSave方法。
            templateParser.parse(template);
            log.error("---模板发布---");
            //通知发布服务
            PublishTrigger.otherData(template.getDocLibID(), template.getDocID(), DocIDMsg.TYPE_TEMPLATE);
        }
        //如果触屏模板不为空，则同样解析模板并通知发布服务
        if (templateMobile != null){
        	templateParser.parse(templateMobile);
        	log.error("---模板发布---");
        	//通知发布服务
        	PublishTrigger.otherData(templateMobile.getDocLibID(), templateMobile.getDocID(), DocIDMsg.TYPE_TEMPLATE);
        	
        }
        return docID;
    }

	/**
     * 按专题设计得到模板ID，同时返回专题设计名称
     */
    @RequestMapping(value = "/findTemplate.do", method = RequestMethod.GET)
    public void findTemplate(
            HttpServletRequest request, HttpServletResponse response,
            int docLibID, long docID) throws Exception {

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);

        JSONObject json = new JSONObject();
        json.put("pcId", doc.getInt("s_templateID"));
        json.put("padId", doc.getInt("s_templatePADID"));
        json.put("name", doc.getString("s_name"));

        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 根据模板ID读出模板文件内容，用于专题设计
     */
    @RequestMapping(value = "/getFile.do", method = RequestMethod.POST)
    public void getFile(
            HttpServletRequest request, HttpServletResponse response,
            long id) throws Exception {

        String tCode = InfoHelper.getTenantCode(request);
        int templateLibID = LibHelper.getLibID(DocTypes.TEMPLATE.typeID(), tCode);

        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(templateLibID, id);
        String filePath = doc.getString("t_file");

        filePath = InfoHelper.getFilePathInDevice(filePath);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(new File(filePath));

            response.setContentType("text/plain; charset=UTF-8");
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            ResourceMgr.closeQuietly(in);
            ResourceMgr.closeQuietly(out);
        }
    }

    /**
     * 获得设计稿
     */
    @RequestMapping(value = "/getDesignFile.do", method = RequestMethod.GET)
    public void getDesignFile(
            HttpServletRequest request, HttpServletResponse response,
            long docID, Integer siteID) {
    	log.error("---开始获取设计稿---");
        JSONObject json = new JSONObject();
        json.put("status", false);
        //获得专题的libid
        try {
            String tenant = InfoHelper.getTenantCode(request);
            int specialLibId = LibHelper.getLibID(DocTypes.SPECIAL.typeID(), tenant);
            log.error("---设计稿libid：" + specialLibId+"--ID--"+docID);
            //获取专题对象
            Document special = specialManager.getDocument(specialLibId, docID);
            //如果对象为null，说明新的专题
            if (special != null) {
                String filePath = special.getString("s_file");
                log.error("---获取设计稿地址：" + filePath);
                //有这个值
                if (!StringUtils.isBlank(filePath)) {
                    filePath = InfoHelper.getFilePathInDevice(filePath);
                    log.error("---获取设计稿完整路径：" + filePath);
                    //获得html文本
                    String htmlData = specialManager.readFileData(filePath);
                    //log.error("---获取设计稿内容前一百字符：" + htmlData.substring(0, 100));
                    System.err.println(htmlData.substring(0, 100));
                    htmlData = replaceURL(htmlData, siteID, false);
                    //把html放到json当中
                    if (!StringUtils.isBlank(htmlData)) {
                    	log.error("---设计稿不为空---");
                        json.put("status", true);
                        json.put("htmlData", htmlData);
                        json.put("s_name", special.getString("s_name"));
                    }
                }
            }

            json.put("_location", InfoHelper.getConfig("互动", "外网资源地址")
                    + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        InfoHelper.outputJson(json.toString(), response);

    }

	@RequestMapping(value = "/uploadPic.do", method = RequestMethod.POST)
    private void uploadPic(HttpServletRequest request, HttpServletResponse response, Integer siteID, Model model)
            throws E5Exception, IOException {
        // 初始化文件接收
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);

        // 拿到前台传过来的数据
        MultipartFile file = multipartRequest.getFile("file");
        String fileName = file.getOriginalFilename();

        siteID = Integer.valueOf(multipartRequest.getParameter("siteID"));
        int docID = Integer.valueOf(multipartRequest.getParameter("docID"));
        long docLibID = Long.valueOf(multipartRequest.getParameter("docLibID"));
        // 保存图片并获得图片名
        String imagePath = null;
        InputStream is = file.getInputStream();
        String[] siteDir = readSiteInfo(siteID);
        int pos = fileName.lastIndexOf(".");
        String picSuffix = "";
        if (pos != -1) {
            picSuffix = fileName.substring(fileName.lastIndexOf("."));
        }
        int userMaxSize = 0;
        boolean zipFlag = false;
        String userMaxSizeStr = InfoHelper.getConfig("写稿", "上传图片大小限制");
        if (userMaxSizeStr != null && !"".equals(userMaxSizeStr)) {
            userMaxSize = Integer.parseInt(userMaxSizeStr);
            if (userMaxSize > 0)
                zipFlag = true;
        }
        if (zipFlag && !".gif".equals(picSuffix) && file.getSize() > 1024 * 1024 * userMaxSize) {
            File tmpFile = PicHelper.picZip(is, 800, picSuffix);
            is = new FileInputStream(tmpFile);
        }
        try {
            imagePath = savePic(is, fileName, siteDir, docID, docLibID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(is);
        }

        //把图片的保存路径放到json当中，以便于以后做扩展
        JSONObject json = new JSONObject();
        imagePath = "image.do?siteID="+siteID+"&path="+imagePath;
        json.put("imagePath", imagePath);

        InfoHelper.outputJson(json.toString(), response);

    }


    public String savePic(InputStream is, String fileName, String[] siteDir, int docID, long docLibID)
            throws E5Exception {
        // 附件存储设备的名称
        String dir = "/xytemplate/" + DateUtils.format("yyyyMM/dd/") + UUID.randomUUID().toString();
        String filePath = siteDir[1] + dir + fileName.substring(fileName.lastIndexOf("."));
        //String url = siteDir[2] + dir + fileName.substring(fileName.lastIndexOf("."));

        File file = new File(filePath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);
            //保存图片信息至附件信息表
            int attLibID = LibHelper.getAttaLibID();
            String attID = String.valueOf(InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
			String sql = "insert into xy_attachment(SYS_DOCUMENTID, SYS_DOCLIBID, att_articleID, att_articleLibID, att_path) values(?, ?, ?, ?, ?)";
			Object[] params = new Object[]{attID, attLibID, docID, docLibID, filePath};
			InfoHelper.executeUpdate(sql, params);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dir + fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 上传Excel文件
     * code:0 上传成功；1 扩展名错误；2 未知异常；
     *
     * @param request
     * @param response
     * @throws E5Exception
     * @throws IOException
     */
    @RequestMapping(value = "/uploadExcel.do", method = RequestMethod.POST)
    private void uploadExcel(HttpServletRequest request, HttpServletResponse response)
            throws E5Exception, IOException {
        // 初始化文件接收
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        // 拿到前台传过来的数据
        MultipartFile file = multipartRequest.getFile("file");
        // 获取文件名
        String fileName = file.getOriginalFilename();
        JSONObject json = new JSONObject();
        try {
            // 判断上传的文件是否为Excel文件
            if (fileName.matches("^.*\\.(?:xlsx|xls)$")) {
                List<List<String>> list = excelArray(file);
                JSONArray array = JSONArray.fromObject(list);
                // 把数据和code放到json当中
                json.put("code", "0");
                json.put("data", array.toString());
            } else {
                json.put("code", "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", "2");
        } finally {
            InfoHelper.outputJson(json.toString(), response);
        }
    }

    public List<List<String>> excelArray(MultipartFile file) throws E5Exception, IOException {
        List<List<String>> list = new ArrayList<>();
        InputStream is = file.getInputStream();
        // 要读取的excel文件
        Workbook wb = null;
        try {
            // Excel2003及2007版本不兼容性
            wb = WorkbookFactory.create(is);
            // 获得第一个工作表对象
            Sheet sheet = wb.getSheetAt(0);
            List<String> array;
            Row row;
            Cell cell;
            String cellValue = "";
            // 获取表格的每一行
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);
                // 忽略表格中的空行
                if (row == null) {
                    continue;
                }
                array = new ArrayList<String>();
                // 获取行的每一个单元格
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    cell = row.getCell(c);
                    // 判断单元格内容是否为空
                    if (cell != null) {
                        cellValue = cell.toString().trim();
                    }
                    array.add(cellValue);
                }
                list.add(array);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            // 关闭流和文件
            if (is != null) {
                is.close();
            }
            if (wb != null) {
                wb.close();
            }
        }
        return list;
    }

    @RequestMapping(value = "/uploadOnlinePic.do", method = RequestMethod.POST)
    public void uploadOnlinePic(
            HttpServletRequest request, HttpServletResponse response, String imagePath, Integer siteID, int docID, long docLibID) {

        UeditorControl.setProxy();

        URL url = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(imagePath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.connect();
            is = conn.getInputStream();
            String[] siteDir = readSiteInfo(siteID);
            imagePath = savePic(is, imagePath, siteDir, docID, docLibID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(is);
            if (conn != null) {
                conn.disconnect();
            }
        }

        //把图片的保存路径放到json当中，以便于以后做扩展
        JSONObject json = new JSONObject();
        imagePath = "image.do?siteID="+siteID+"&path="+imagePath;
        json.put("imagePath", imagePath);
        InfoHelper.outputJson(json.toString(), response);


    }


    @RequestMapping(value = "/getArticlesJson.do")
    public void getArticlesJson(HttpServletRequest request, HttpServletResponse response)
            throws E5Exception, IOException {
        int docLibID = WebUtil.getInt(request, "docLibID", 0);
        String docIDs = WebUtil.getStringParam(request, "docIDs");
        int ch = WebUtil.getInt(request, "ch", 0);
        int style = WebUtil.getInt(request, "style", 0);
        String result = specialManager.getArticlesJson(docLibID, docIDs, ch, style);
        InfoHelper.outputText(result, response);
    }

    @RequestMapping(value = "/preview.do")
    public String getPreviewHtml(
            HttpServletRequest request, HttpServletResponse response,
            String html, Model model) {
        model.addAttribute("_html", html);

        return "/xy/special/dialog/preview";
    }

    /**
     * 检测专题是否被引用并返回提示信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@RequestMapping(value = {"checkUsed.do"})
	public ModelAndView checkUsed(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        String FVID = WebUtil.get(request, "FVID");
        String UUID = WebUtil.get(request, "UUID");
        String siteID = WebUtil.get(request, "siteID");
        String groupID = WebUtil.get(request, "groupID");
        String siteField = WebUtil.get(request, "siteField");
        String groupField = WebUtil.get(request, "groupField");
        long[] docIDs = StringUtils.getLongArray(strDocIDs);
        //检查专题是否被引用了
        List<Article> docNames = checkSpecialIsUsed(docLibID, docIDs[0]);
		ModelAndView model = new ModelAndView();
		model.setViewName("/xy/special/ArticleAttr");
		model.addObject("DocLibID", docLibID);
		model.addObject("DocIDs", strDocIDs);
		model.addObject("FVID", FVID);
		model.addObject("UUID", UUID);
		model.addObject("siteID", siteID);
		model.addObject("groupID", groupID);
		model.addObject("siteField", siteField);
		model.addObject("groupField", groupField);
		model.addObject("error", docNames);
		return model;
	}
    
    @RequestMapping(value = {"Delete.do"})
    public String specialDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String strDocIDs = WebUtil.get(request, "DocIDs");
        String UUID = WebUtil.get(request, "UUID");
        long[] docIDs = StringUtils.getLongArray(strDocIDs);
        //检查专题是否被引用了
        //TODO   注意删测试数据
        String num = WebUtil.get(request, "error", "test");

        String url = null;
        /**
         * 1、如果不为null，说明被引用，需撤稿所有使用此专题的专题稿件
		   2、删除专题内容，删除对应模板
		   3、同时删除附件表中的对应图片，调用trans写空白文件至外网。
         */
        if (num != null) {
        	revokeAllDocs(docLibID, docIDs[0], request);
        }
      //彻底删除
        specialManager.specialDelete(docLibID, docIDs, request);
        specialManager.specialPicDelete(docLibID, docIDs);
        //由于彻底删除了稿件，所以需要手动存日志
        for (long docID : docIDs) {
            LogHelper.writeLog(docLibID, docID, request, "删除到垃圾箱，保留日志");
        }
        url = "/e5workspace/after.do?UUID=" + UUID
        		+ "&DocIDs=" + Long.parseLong(strDocIDs);
        return "redirect:"+url ;
    }
    /**
     * 查询全部引用稿件
     * 判断稿件是否已发布，发布则撤稿
     * @param docLibID
     * @param request 
     * @param l
     * @throws Exception 
     */
    private void revokeAllDocs(int docLibID, long docID, HttpServletRequest request) throws Exception {
        Document special = specialManager.getDocument(docLibID, docID);
        long templateId = special.getLong("s_templateID");
        if (templateId > 0) {
            try {
                Document[] docs = specialManager.findAllDocs(LibHelper.getArticleLibID(), templateId);
                for(Document doc:docs)
	                if (doc != null) {
	                	//如果稿件已发布则撤稿，否则不做处理
	                    if("1".equals(doc.getString("a_status")))
	                    	revoke(doc.getDocLibID(), doc.getDocID(), request);
	                }
            } catch (E5Exception e) {
                e.printStackTrace();
            }
        }
	}


	private void revoke(int docLibID, long docID, HttpServletRequest request) throws Exception {
        FlowReader flowReader = (FlowReader) Context.getBean(FlowReader.class);
        List<Document> articles = new ArrayList<Document>();
        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document article = docManager.get(docLibID, docID);
        if (article != null) {
            //获得当前稿件所处流程的ID，然后找到这个流程的第一个节点，作为稿件的当前节点，同时设置稿件的当前状态
            int curflowID = article.getCurrentFlow();
            FlowNode[] nodes = flowReader.getFlowNodes(curflowID);
            article.setCurrentNode(nodes[0].getID());
            article.setCurrentStatus(nodes[0].getWaitingStatus());
            //解锁操作
            article.setLocked(false);
            article.set("a_status", Article.STATUS_REVOKE);
            articles.add(article);
        }
        //同时修改多个稿件，使用事务
        String error = save(docLibID, articles);

        String uuid = WebUtil.get(request, "UUID");
        if (error == null) {
            //发布撤稿消息
            articleManager.revoke(articles);
	        //撤稿中心调用的操作，手工写操作日志、返回
	        SysUser sysUser = ProcHelper.getUser(request);
	        writeRevokeLog(sysUser, docLibID, docID);
            
        }
    }

	private String save(int docLibID, List<Document> articles) {
		if (articles == null || articles.size() == 0) return null;
		
	    DocumentManager docManager = DocumentManagerFactory.getInstance();
	    //同时修改多个稿件，使用事务
	    DBSession conn = null;
	    try {
	        conn = E5docHelper.getDBSession(docLibID);
	        conn.beginTransaction();
	
	        for (Document article : articles) {
	            docManager.save(article, conn);
	        }
	        conn.commitTransaction();
	        return null;
	    } catch (Exception e) {
	        ResourceMgr.rollbackQuietly(conn);
	        e.printStackTrace();
	        return "操作中出现错误：" + e.getLocalizedMessage();
	    } finally {
	        ResourceMgr.closeQuietly(conn);
	    }
	}

	private void writeRevokeLog(SysUser sysUser, int docLibID, long docID) {
	        LogHelper.writeLog(docLibID, docID, sysUser, "撤稿", "专题删除同时撤稿相关专题稿件");
		
	}

	private List<Article> checkSpecialIsUsed(int docLibID, long docID) {
        @SuppressWarnings("unused")
		String msg = null;
        Document special = specialManager.getDocument(docLibID, docID);
        long templateId = special.getLong("s_templateID");
        List<Article> docNames = new ArrayList<Article>();
        if (templateId > 0) {
            try {
            	Date beginTime = new Date();
            	Document[] docs = specialManager.findAllDocs(LibHelper.getArticleLibID(), templateId);
            	System.out.println("查询关联稿件耗时"+((new Date()).getTime()-beginTime.getTime()));
            	for(Document doc : docs){
            		if (doc != null)
                		docNames.add(new Article(doc));
            		/*if(docNames.size() >= 5)
            			break;*/
            	}
            } catch (E5Exception e) {
                e.printStackTrace();
                msg = "数据库操作失败";
            }
        }
        return docNames;
    }

    /**
     * 在special表中查找 是否存在跟 specialName一样的。
     * 如果是一样的回传true,否则回传false;
     * 回传数据使用 JsonObject
     *
     * @param docLibId
     * @param specialName
     * @throws E5Exception
     */
    @RequestMapping("/checkspecialname.do")
    public void checkSpecialName(
            HttpServletResponse response, Integer docLibId,
            String specialName) {
        boolean status;
        String msg = "";
        try {
            status = specialManager.isExist(docLibId, specialName);
            if (status) {
                msg = "对不起，专题重名！";
            }
        } catch (E5Exception e) {
            e.printStackTrace();
            status = true;
            msg = "数据库查询出错！";
        }

        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("msg", msg);
        InfoHelper.outputJson(json.toString(), response);
    }

    @RequestMapping("/Copy.do")
    public String Copy(
            HttpServletResponse response,
            Integer DocLibID, Long DocIDs, Integer siteID, Integer groupID, String UUID) {

        //1. 取出目标special对象
        Document special = specialManager.getDocument(DocLibID, DocIDs);
        //2. 取出 designhtml的路径
        String designPath = special.getString("s_file");
        designPath = InfoHelper.getFilePathInDevice(designPath);
        //获得html文本
        String designHtml = specialManager.readFileData(designPath);

        Document template = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templateID"));
        String showPath = template.getString("t_file");
        showPath = InfoHelper.getFilePathInDevice(showPath);
        //获得html文本
        String showHtml = specialManager.readFileData(showPath);
        
        //获得专题触屏版html文本
        String showPADHtml = null;
        Document templatePAD = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templatePADID"));
        if(templatePAD != null){
	        String showPADPath = templatePAD.getString("t_file");
	        showPADPath = InfoHelper.getFilePathInDevice(showPADPath);
	        showPADHtml = specialManager.readFileData(showPADPath);
        }
        DocIDs = InfoHelper.getNextDocID(DocTypes.SPECIAL.typeID()); //取ID

        String expdate = special.getString("s_expireDate");
        if (null != expdate && !expdate.equals("")) {
            String[] str = expdate.split("-");
            str[0] = String.valueOf(Integer.parseInt(str[0]) + 1);
            expdate = str[0] + "-" + str[1] + "-" + str[2];
        }

        long specialId = saveSpecial(showHtml, designHtml, showPADHtml, DocLibID, DocIDs, siteID, groupID, "（复制）"+special.getString("s_name"), null,
                                     true, special.getString("s_picPath"), expdate, null);

        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + DocIDs/* + "," + specialId*/;
        try {
            url += "&Info=" + URLEncoder.encode("已从" + specialId + "复制专题，新专题Id：" + specialId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:" + url;
    }


    /**
     * 按专题设计得到模板ID，同时返回专题设计名称
     */
    @RequestMapping(value = "/testSave.do", method = RequestMethod.GET)
    public void testSave(
            HttpServletRequest request, HttpServletResponse response,
            Integer DocLibID, Long DocIDs, Integer siteID, Integer groupID, String UUID,
            String expdate) throws Exception {

        System.out.printf("******************************** %d **********************************\r\n", DocIDs);
        //1. 取出目标special对象
        Document special = specialManager.getDocument(DocLibID, DocIDs);
        //2. 取出 designhtml的路径
        String designPath = special.getString("s_file");
        designPath = InfoHelper.getFilePathInDevice(designPath);
        //获得html文本
        String designHtml = specialManager.readFileData(designPath);

        Document template = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templateID"));
        String showPath = template.getString("t_file");
        showPath = InfoHelper.getFilePathInDevice(showPath);
        //获得html文本
        String showHtml = specialManager.readFileData(showPath);
        //获取专题触屏版html文本
        String showPADHtml = null;
        Document templatePAD = specialManager.getDocument(LibHelper.getTemplateLibID(), special.getLong("s_templatePADID"));
        if(templatePAD != null){
	        String showPADPath = templatePAD.getString("t_file");
	        showPADPath = InfoHelper.getFilePathInDevice(showPADPath);
	        showPADHtml = specialManager.readFileData(showPADPath);
        }
        DocIDs = InfoHelper.getNextDocID(DocTypes.SPECIAL.typeID()); //取ID
        saveSpecial(showHtml, designHtml, showPADHtml, DocLibID, DocIDs, siteID, groupID, "新专题", special,
                    false, null, expdate, null);

        InfoHelper.outputJson(DocIDs + "", response);
    }

    @RequestMapping("Rename.do")
    public ModelAndView rename(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1. 取出目标special对象
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);

        Map<String, Object> model = new HashMap<>();


        DocumentManager docManager = DocumentManagerFactory.getInstance();
        Document doc = docManager.get(docLibID, docID);

        String specialName = doc.getString("s_name");

        model.put("docLibID", docLibID);
        model.put("docID", docID);
        model.put("siteID", WebUtil.get(request, "siteID"));
        model.put("groupID", WebUtil.get(request, "groupID"));
        model.put("UUID", WebUtil.get(request, "UUID"));

        model.put("specialName", specialName);
        model.put("expdate", doc.getString("s_expireDate"));

        return new ModelAndView("/xy/special/dialog/articleReName", model);
    }

    @RequestMapping("renameSave.do")
    public String renameSave(
            HttpServletRequest request, HttpServletResponse response, Integer DocLibID, Long DocIDs, Integer siteID,
            Integer groupID, String UUID) throws Exception {
        long docID = WebUtil.getLong(request, "DocIDs", 0);
        int docLibID = WebUtil.getInt(request, "DocLibID", 0);
        String specialName = request.getParameter("specialname");
        String expdate = request.getParameter("expdate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = "/e5workspace/after.do?UUID=" + UUID + "&&DocIDs=" + DocIDs;

        try {
            DocumentManager docManager = DocumentManagerFactory.getInstance();
            Document doc = docManager.get(docLibID, docID);

            doc.set("s_name", specialName);
            doc.set("s_expireDate", sdf.parse(expdate));

            docManager.save(doc);
            //设置对应模板的过期时间,模板名称
            Document tpldoc = docManager.get(LibHelper.getLibIDByOtherLib(DocTypes.TEMPLATE.typeID(),docLibID), doc.getLong("s_templateID"));
            tpldoc.set("t_name",specialName);
            tpldoc.set("t_expireDate",sdf.parse(expdate));
            docManager.save(tpldoc);
            //通知发布服务
            PublishTrigger.otherData(tpldoc.getDocLibID(), tpldoc.getDocID(), DocIDMsg.TYPE_TEMPLATE);
        } catch (Exception e) {
            url += "&Info=" + URLEncoder.encode("操作失败", "UTF-8");
            return "redirect:" + url;
        }

        return "redirect:" + url;


    }

    @RequestMapping("checkName.do")
    public void checkName(
            HttpServletRequest request, HttpServletResponse response, Integer docLibId, int docId) throws Exception {
        String specialname = request.getParameter("specialName");
        String date = request.getParameter("date");
        JSONObject json = new JSONObject();
        if (specialManager.isExists(docLibId, specialname, date, docId)) {
            json.put("status", "1");
        } else {
            json.put("status", "2");
        }
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 显示用户的上传图片
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "image.do", method = RequestMethod.GET)
    protected void image(
            HttpServletRequest request, HttpServletResponse response, String path,
            @RequestParam(defaultValue = "1") int siteID) {
        String[] siteDir = readSiteInfo(siteID);
        String realPath = siteDir[1] + path;
        realPath = FilenameUtils.normalize(realPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            //显示图片的header
            String CONTENT_TYPE = "image/jpeg; charset=UTF-8";
            response.setContentType(CONTENT_TYPE);
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            in = new FileInputStream(realPath);
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (Exception e) {
            try {
                response.sendRedirect("../images/nopic.png");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            ResourceMgr.closeQuietly(in);
            ResourceMgr.closeQuietly(out);
        }
    }

    @RequestMapping(value = "republish.do")
    public String republish(
            HttpServletRequest request, HttpServletResponse response, Integer DocLibID, Long DocIDs, Integer groupID,
            String UUID) {
        String url = "redirect:/e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + DocIDs;
        DocumentManager documentManager = DocumentManagerFactory.getInstance();
        try {
            Document specialDoc = documentManager.get(DocLibID, DocIDs);
            if (specialDoc == null)
                throw new RuntimeException("没找到专题！");

                Integer templateId = specialDoc.getInt("s_templateID");
                if (templateId != null && templateId != 0) {
                    Document[] articleDocs = documentManager.find(LibHelper.getArticleLibID(), "a_templateID=?",
                                                                new Object[]{templateId});

                    if (articleDocs.length == 0) {
                        url += "&Info=" + URLEncoder.encode("专题未被引用！没有进行重发！", "UTF-8");//有错误，需返回前台做提示
                    }else{
                        for (Document doc : articleDocs) {
                            if(doc.getInt("a_status") == 1){
                                PublishTrigger.article(doc);
                            }

                        }
                        url += "&Info=" + URLEncoder.encode("重发成功", "UTF-8");//有错误，需返回前台做提示
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
            url += "&Info=" + "error!" + e.getLocalizedMessage();//有错误，需返回前台做提示
        }
        return url;
    }

    /**
     * 预览
     */
    @RequestMapping(value = "specialPreview.do")
    public ModelAndView specialPreview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //写稿地方传的模板ID
        long templateID = WebUtil.getLong(request, "a_templateID", -1);
        int specialLibID = LibHelper.getLibID(DocTypes.SPECIAL.typeID(),InfoHelper.getTenantCode(request));
        if(templateID<0){
            //专题列表传的专题ID
            long specialID = WebUtil.getLong(request, "DocIDs", 0);
            DocumentManager documentManager = DocumentManagerFactory.getInstance();
            Document special = documentManager.get(specialLibID,specialID);
            templateID = special.getInt("s_templateID");
        }
        int colLibID = LibHelper.getLibIDByOtherLib(DocTypes.COLUMN.typeID(), specialLibID);
        Map<String, Object> model = new HashMap<>();
        //调用栏目页预览
        DocIDMsg data = new DocIDMsg(colLibID, templateID, "specialPreview");
        ColumnGenerator generator = new ColumnGenerator();
        String[] pages = generator.preview(data);

        if (pages[0] != null) model.put("page0", JsoupHelper.replaceImgSuffix(pages[0]));
        if (pages[1] != null) model.put("page1", JsoupHelper.replaceImgSuffix(pages[1]));
        return new ModelAndView("xy/article/ColumnPreview", model);
    }

}
