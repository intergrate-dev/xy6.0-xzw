package com.founder.xy.ueditor.web;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.commons.ResourceMgr;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.SysFactory;
import com.founder.e5.sys.org.User;
import com.founder.e5.sys.org.UserReader;
import com.founder.e5.web.SysUser;
import com.founder.e5.web.WebUtil;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.api.newmobile.NewMobileApiManager;
import com.founder.xy.article.web.ArticleServiceHelper;
import com.founder.xy.commons.*;
import com.founder.xy.ueditor.Params;
import com.founder.xy.ueditor.XYActionEnter;
import com.founder.xy.ueditor.proof.CheckExtendInfo;
import com.founder.xy.ueditor.proof.CheckType;
import com.founder.xy.ueditor.proof.ProofManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 百度编辑器的相关操作
 */
@Controller
@RequestMapping("/xy/ueditor")
public class UeditorControl {
	@Autowired
	private ProofManager proofManager;
	
    /**
     * 编辑器初始化及上传功能入口
     */
    @RequestMapping(value = "Controller.do")
    public void controller(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html");

        String rootPath = request.getSession().getServletContext().getRealPath("/");
        String result = new XYActionEnter(request, rootPath).exec();
        InfoHelper.outputText(result, response);
    }

    /**
     * 初始化 设置标题图
     *
     * @param request
     * @param response
     * @param imagePath
     * @param itype
     * @param model
     * @return
     * @throws Exception 
     */
    @RequestMapping("initTitleDialog.do")
    public String initTitleImageDialog(
            HttpServletRequest request, HttpServletResponse response,
            String radio,
            String imagePath, String itype, Model model) throws Exception {
    	//imagePath=new String(imagePath.getBytes("ISO-8859-1"),"UTF-8");
        // 初始化图片的路径
        model.addAttribute("imagePath", imagePath.replaceAll("\\\\", "/"));
        model.addAttribute("itype", itype);
        // 初始化图片的比例
        String smallRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-小");
        String midRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-中");
        String bigRadio = InfoHelper.getConfig("写稿", "标题图片尺寸-大");
        //标题图是否设置自定义的尺寸大小
        String smallResize = "";
        String midResize = "";
        String bigResize = "";
        //如果图片已经被自定义了尺寸大小，则按自定义的显示
        if(radio != null && radio != "" && !radio.equals("undefined")){
            if(itype.equals("small") && !radio.equals(smallRadio)){
                smallResize = radio;
            }
            if(itype.equals("mid") && !radio.equals(midRadio)) {
                midResize = radio;
            }
            if(itype.equals("big") && !radio.equals(bigRadio)){
                bigResize = radio;
            }
        }
        model.addAttribute("smallRadio", smallRadio);
        model.addAttribute("midRadio", midRadio);
        model.addAttribute("bigRadio", bigRadio);
        model.addAttribute("smallResize", smallResize);
        model.addAttribute("midResize", midResize);
        model.addAttribute("bigResize", bigResize);

        return "/xy/ueditor/dialogs/imagecrop/titleimage";
    }

    @RequestMapping("initCropDialog.do")
    public String initCropDialog(
            HttpServletRequest request, HttpServletResponse response,
            String imagePath, String itype, Model model) {
        return "/xy/ueditor/dialogs/imagecrop/imagecrop";

    }

    /**
     * 图片的切图服务
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("cropImage.do")
    public void cropImage(
            HttpServletRequest request, HttpServletResponse response,
            Params params) throws Exception {
    	if(!safeCheck(params)) {
    		InfoHelper.outputText("", response);
    		return;
    	}
        checkPic(params);
        String imagePath = params.getImagePath();
        // 图片的全名
        String pumpImgName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        // 文件的路径
        String relativePath = imagePath.substring(0, imagePath.lastIndexOf("/"));
        if(!StringUtils.isBlank(relativePath))
        	relativePath = relativePath.replace("../image.do", "image.do");

        // 获取服务的url
        String url = UrlHelper.imageServiceUrl();
        String filePath = "";
        String wholePath = "";
        String deviceName = "";

        if (imagePath.contains("image.do") && !imagePath.contains("../image.do")) {
            deviceName = imagePath.substring(imagePath.lastIndexOf("path=") + 5, imagePath.indexOf(";"));
            deviceName = URLDecoder.decode(deviceName, "UTF-8");
            // 获得文件的实际存储地址
            StorageDevice device = InfoHelper.getDeviceByName(deviceName);
            filePath = InfoHelper.getDevicePath(device);

            // 全路径 - 只需要有全路径，filename和filepath就无效了
            wholePath = filePath + File.separator
                    + imagePath.substring(imagePath.lastIndexOf(";") + 1);
        } else if (imagePath.contains("xytemplate")) {
            Integer siteID = params.getSiteID();
            String[] dirs = readSiteInfo(siteID);
            wholePath = dirs[1] + imagePath.substring(imagePath.indexOf("xytemplate") - 1);
        }

        // 文件名
        String fileName = pumpImgName;
        if (params.getIsSameSetting() != null && params.getIsSameSetting()) {
            JSONArray jsonArray = JSONArray.fromObject(params.getImgList());
            JSONArray targetArray = new JSONArray();
            String _p = "";
            for (Object p : jsonArray) {
                _p = (String) p;
                _p = filePath + File.separator + _p.substring(_p.lastIndexOf(";") + 1);
                targetArray.add(_p);
            }
            params.setImgList(targetArray.toString());
        }

        //不保持比例
        params.setKeepRadio(false);
        // 访问提取服务 - 公用方法
        String result = accessImageService(url, fileName, filePath, wholePath, relativePath, params);

        //生成抽图任务
        if (deviceName != null && !"".endsWith(deviceName)) {
            StorageDevice device = InfoHelper.getDeviceByName(deviceName);
            prepare4Extract(device, result);
        }
        InfoHelper.outputText(result, response);
    }
    
    private boolean safeCheck(Params params) {
		String command = params.getCommand();
		String imagePath = params.getImagePath();
		//1.命令为空 或图片路径为空
		if(StringUtils.isBlank(command) || StringUtils.isBlank(imagePath)) 
			return false;
		//2.命令不再预设命令中
		command = command.trim().toLowerCase();
		String[] allows = {"zoom","zsize","rotate","cut","watermark","batchwm","gray","drawtext"};
		List<String> allowList = Arrays.asList(allows);
		if(!allowList.contains(command))
			return false;
		//图片路径中包含非法路径的
		String[] illPaths = {"tomcat","webapps","root"};
		for(String illPath :illPaths ){
			if(imagePath.toLowerCase().indexOf(illPath)>-1)
				return false;
		}
		return true;
	}
    
    private void checkPic(Params params) {
        String imagePath = params.getImagePath();
        //如果是外网图片的话, 先下到本地 ----判断翔宇图片 需要改进
        if (imagePath != null && !"".equals(imagePath) && !imagePath.contains("xytemplate") && !imagePath.contains(
                "image.do")) {

            setProxy();
            URL url = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                url = new URL(imagePath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.connect();
                is = conn.getInputStream();
                String[] siteDir = readSiteInfo(params.getSiteID());
                imagePath = savePic(is, imagePath, siteDir);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ResourceMgr.closeQuietly(is);
                if (conn != null) {
                    conn.disconnect();
                }
            }

            //把图片的保存路径放到json当中，以便于以后做扩展
            params.setImagePath(imagePath);
        }

    }

    public String savePic(InputStream is, String fileName, String[] siteDir)
            throws E5Exception {
        // 附件存储设备的名称

        String dir = "/xytemplate/" + DateUtils.format("yyyyMM/dd/") + UUID.randomUUID().toString();
        String filePath = siteDir[1] + dir + fileName.substring(fileName.lastIndexOf("."));
        String url = siteDir[2] + dir + fileName.substring(fileName.lastIndexOf("."));

        File file = new File(filePath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            IOUtils.copy(is, os);
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
        return url;
    }

    protected String[] readSiteInfo(int siteID) {
        return InfoHelper.readSiteInfo(siteID);
    }

    //对裁剪、改尺寸、加水印等操作后的图片，添加抽图任务。对标题图片的大图，添加抽图任务
    private void prepare4Extract(StorageDevice device, String result) {
        //从result中得到多少个图片被修改了
        List<String> imgs = new ArrayList<>();

        //result格式：{"imgPath":"../../../../xy/image.do?path=图片存储;xy/201510/14/d7a9f14d-ed28-42d9-a222-1756cf2e72e3_cut.jpg",
        //"imgList":[]}
        JSONObject obj = JSONObject.fromObject(result);

        if (obj.containsKey("imgPath"))
            drawOutImgPath(imgs, obj.getString("imgPath"));

        JSONArray imgList = null;
        if (obj.containsKey("imgList")) {
            imgList = obj.getJSONArray("imgList");
            if (imgList != null && !imgList.isEmpty()) {
                for (int i = 0; i < imgList.size(); i++) {
                    drawOutImgPath(imgs, imgList.getString(i));
                }
            }
        }
        //标题图片，对大图做抽图
        if (obj.containsKey("imgBig"))
            drawOutImgPath(imgs, obj.getString("imgBig"));

        for (String picPath : imgs) {
            InfoHelper.prepare4Extract(device, picPath);
        }
    }

    //从url中截取图片的相对路径，取存储设备名后面的部分
    private void drawOutImgPath(List<String> imgs, String url) {
        if (url != null) {
            int pos = url.lastIndexOf(";");
            if (pos < 0) return;

            url = url.substring(pos + 1);
        }

        if (!StringUtils.isBlank(url))
            imgs.add(url);
    }

    /**
     * 访问提取服务 - 公用方法
     *
     * @return
     * @throws Exception
     */
    private String accessImageService(
            String url, String fileName, String filePath,
            String wholePath, String relativePath, Params params) throws Exception {
        // 2. 组织成 post 格式的数据
        HttpPost httpPost = assembleData(url, fileName, filePath, wholePath, params);
        // 3. a. 发送数据并获得response; b.处理获得的数据，并返回一个json对象
        JSONObject json = ArticleServiceHelper.executeHttpRequest(httpPost, true);
        // 4. 处理返回来的json数据
        String result = handleResult(json, relativePath, params);
        return result;
    }

    /**
     * 组织成 post 格式的数据
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    private HttpPost assembleData(
            String url, String fileName, String filePath, String wholePath,
            Params params) throws Exception {
        // 初始化一个post对象
        HttpPost httpPost = new HttpPost(url);
        // 封装参数列表
        List<NameValuePair> nvps = new ArrayList<>();

        String watermarkPath = params.getWatermark();
        if (watermarkPath != null && !"".equals(watermarkPath)) {
            StorageDevice device = InfoHelper.getWaterMarkDevice();
            String _wmPath = InfoHelper.getDevicePath(device);
            watermarkPath = _wmPath + "/" + watermarkPath.substring(watermarkPath.lastIndexOf(";") + 1);
        }
        params.setWatermark(watermarkPath);
        params.setName(fileName);
        params.setPath(filePath);
        params.setWholePath(wholePath);
        if (wholePath != null && !"".equals(wholePath)) {
            params.setImgType(wholePath.substring(wholePath.lastIndexOf(".") + 1));
        }

        JSONObject paramJson = JSONObject.fromObject(params);
        nvps.add(new BasicNameValuePair("json", paramJson.toString()));


        // 封装成form对象
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        return httpPost;
    }

    /**
     * 处理返回来的json数据
     *
     * @param json
     * @param params 
     * @return
     */
    private String handleResult(JSONObject json, String relativePath, Params params) throws Exception {
        if (json == null)
            return "";
        JSONObject resultJson = new JSONObject();
        // 获得状态
        String status = json.getString("status");
        JSONArray _result;
        JSONObject _obj;
        String imgPath = null;
        String destPath = null;
        String _resultArray = null;
        String _imgPath = null;
        JSONArray imgList = new JSONArray();
        // 如果成功， 组装result; 如果失败,返回""
        if ("success".equals(status)) {
            _result = json.getJSONArray("result");
            _obj = _result.getJSONObject(0);
            destPath = _obj.getString("destPath");
            imgPath = destPath;
            if (imgPath != null && !"".equals(imgPath)) {
                imgPath = relativePath
                        + imgPath.substring(imgPath.lastIndexOf("/") == -1 ? imgPath.lastIndexOf("\\")
                                                    : imgPath.lastIndexOf("/"));
            }
            StorageDevice device = InfoHelper.getPicDevice();
            String filePath = InfoHelper.getDevicePath(device);

            //如果是批量处理的，把批量处理的地址也进行处理
            _resultArray = _obj.getString("resultImgArray");
            if (_resultArray != null && !"".equals(_resultArray) && !"null".equals(_resultArray)) {
                JSONArray _jsonArray = JSONArray.fromObject(_resultArray);
                relativePath = relativePath.substring(0, relativePath.indexOf(";") + 1);
                for (Object o : _jsonArray) {
                    _imgPath = (String) o;
                    _imgPath = relativePath
                            + _imgPath.replace(filePath, "").substring(1);
                    imgList.add(_imgPath);
                }

            }


        } else {
            resultJson.put("status", "failure");
        }

        resultJson.accumulate("imgPath", imgPath);
        resultJson.accumulate("imgList", imgList);
        
        savePicInfo(destPath, params);
        
        return resultJson.toString();
    }

    private void savePicInfo(String imgPath, Params params) throws E5Exception {
    	long docID = params.getDocID();
        Integer docLibID = params.getDocLibID();
        //保存图片信息至附件信息表
        int attLibID = LibHelper.getAttaLibID();
        String attID = String.valueOf(InfoHelper.getNextDocID(DocTypes.ATTACHMENT.typeID()));
		String sql = "insert into xy_attachment(SYS_DOCUMENTID, SYS_DOCLIBID, att_articleID, att_articleLibID, att_path) values(?, ?, ?, ?, ?)";
		Object[] objs = new Object[]{attID, attLibID, docID, docLibID, imgPath};
		if(docID > 0 && docLibID != null && !StringUtils.isBlank(imgPath))
		InfoHelper.executeUpdate(sql, objs);
	}

	/**
     * 生成标题图
     *
     * @param request
     * @param response
     * @param imagePath
     * @param docLibID
     * @param docID
     * @param smallCoords
     * @param midCoords
     * @param bigCoords
     * @param widthRadio
     * @param heightRadio
     * @param isLocalFile
     * @param imageName
     * @throws Exception
     */
    @RequestMapping("createtitleImg.do")
    public void createtitleImg(
            HttpServletRequest request, HttpServletResponse response,
            String imagePath, Integer docLibID, Integer docID, @RequestParam(required=false)String smallCoords,
            @RequestParam(required=false)String midCoords, @RequestParam(required=false)String bigCoords, Float widthRadio, Float heightRadio,
            Boolean isLocalFile, String imageName,
            String smallCR,String midCR, String bigCR) throws Exception {
        JSONObject json = new JSONObject();

        if (isLocalFile) {
            imagePath = imageName;
        }

        // 图片的全名
        String pumpImgName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        // 文件的路径
        String relativePath = imagePath.substring(0, imagePath.lastIndexOf("/"));
        // 获取服务的url
        String url = UrlHelper.imageServiceUrl();

        // 获得文件的实际存储地址
        StorageDevice device = InfoHelper.getPicDevice();
        String filePath = InfoHelper.getDevicePath(device);

        // 文件名
        String fileName = pumpImgName;
        // 全路径 - 只需要有全路径，filename和filepath就无效了
        String wholePath = filePath + File.separator
                + imagePath.substring(imagePath.lastIndexOf(";") + 1);

        // 访问裁图服务
        if (smallCoords != null) {
            String radio = InfoHelper.getConfig("写稿", "标题图片尺寸-小");
            radio = smallCR;
            createImageFromServer(imagePath, widthRadio, heightRadio, json, relativePath, url,
                                  filePath, fileName, wholePath, smallCoords, "t0", radio, "imgSmall");
        }

        if (midCoords != null) {
            String radio = InfoHelper.getConfig("写稿", "标题图片尺寸-中");
            radio = midCR;
            createImageFromServer(imagePath, widthRadio, heightRadio, json, relativePath, url,
                                  filePath, fileName, wholePath, midCoords, "t1", radio, "imgMid");
        }

        if (bigCoords != null) {
            String radio = InfoHelper.getConfig("写稿", "标题图片尺寸-大");
            radio = bigCR;
            createImageFromServer(imagePath, widthRadio, heightRadio, json, relativePath, url,
                                  filePath, fileName, wholePath, bigCoords, "t2", radio, "imgBig");
        }

        //对标题图片的大图，做抽图任务
        String result = json.toString();
        prepare4Extract(device, result);

        InfoHelper.outputJson(result, response);
    }


    /**
     * 访问远程切图服务
     *
     * @param imagePath
     * @param widthRadio
     * @param heightRadio
     * @param json
     * @param relativePath
     * @param url
     * @param filePath
     * @param fileName
     * @param wholePath
     * @param coords
     * @param headName
     * @param radio
     * @param imgNameInJson
     * @throws Exception
     */
    private void createImageFromServer(
            String imagePath, Float widthRadio, Float heightRadio,
            JSONObject json, String relativePath, String url, String filePath, String fileName,
            String wholePath, String coords, String headName, String radio, String imgNameInJson)
            throws Exception {
        int resizedX;
        int resizedY;
        //获得四置坐标
        JSONObject sc = JSONObject.fromObject(coords);

        int sx = Math.round(Float.parseFloat(sc.getString("x")) * widthRadio);
        int sw = Math.round(Float.parseFloat(sc.getString("w")) * widthRadio);
        int sy = Math.round(Float.parseFloat(sc.getString("y")) * heightRadio);
        int sh = Math.round(Float.parseFloat(sc.getString("h")) * heightRadio);

        //把切图选择器的坐标加到图片名字上，以便于修改时，把选择器定位在图片上
        String _radio = Math.round(Float.parseFloat(sc.getString("x"))) + "X"
                + Math.round(Float.parseFloat(sc.getString("y"))) + "X"
                + Math.round(Float.parseFloat(sc.getString("x2"))) + "X"
                + Math.round(Float.parseFloat(sc.getString("y2")));

        //目标图片名
        String destPath = filePath
                + File.separator
                + imagePath.substring(imagePath.lastIndexOf(";") + 1,
                                      imagePath.lastIndexOf("/") + 1) + headName + "_(" + _radio + ")" + fileName;

        String[] _n = radio.split("\\*");
        resizedX = Integer.parseInt(_n[0]);
        resizedY = Integer.parseInt(_n[1]);

        Params params = new Params();
        params.setImagePath(imagePath);
        params.setDestName(destPath);
        params.setKeepRadio(true);
        params.setImagePumpW(resizedX);
        params.setImagePumpH(resizedY);
        params.setEnd_x(sw);
        params.setEnd_y(sh);
        params.setStart_x(sx);
        params.setStart_y(sy);
        params.setCommand("cut");

        String image = accessImageService(url, fileName, filePath, wholePath, relativePath, params);
        JSONObject _j = JSONObject.fromObject(image);
        image = _j.getString("imgPath");
        json.accumulate(imgNameInJson, image);
    }

    /**
     * 上传图片，用于单独上传标题图片时
     *
     * @param response
     * @param request
     * @param imageName
     * @throws Exception
     */
    @RequestMapping(value = "uploadPic.do", method = RequestMethod.POST)
    public String uploadPic(
            HttpServletResponse response, HttpServletRequest request,
            String imageName, String itype, Model model) throws Exception {
        // 初始化文件接收
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession()
                                                                          .getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        // 拿到前台传过来的数据
        MultipartFile file = multipartRequest.getFile("localFile");
        InputStream is = file.getInputStream();
        String fileName = file.getOriginalFilename();

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
        if (zipFlag && !".gif".equals(picSuffix) && file.getSize() >1024 * 1024 * userMaxSize ) {
            File tmpFile = PicHelper.picZip(is,800,picSuffix);
            is = new FileInputStream(tmpFile);
        }
        // 文件名
        String imagePath = savePic(request, is, fileName);

        itype = multipartRequest.getParameter("itype");
        model.addAttribute("imagePath", imagePath.replaceAll("\\\\", "/"));
        model.addAttribute("itype", itype);

        return "redirect:/xy/ueditor/initTitleDialog.do";
    }
    
    @RequestMapping(value = "prepare4Extract.do")
    public void prepare4Extract(HttpServletRequest request, HttpServletResponse response,String imagePath){
    	String[] str = imagePath.split(";");
    	StorageDevice device = InfoHelper.getDeviceByName(str[0]);
    	InfoHelper.prepare4Extract(device, str[1]);
    	InfoHelper.outputText("", response);
    }
    private String savePic(HttpServletRequest request, InputStream is, String fileName)
            throws E5Exception {
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getPicDevice();
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = InfoHelper.getPicSavePath(request)
                + fileName.substring(fileName.lastIndexOf("."));
        // 开始存储到存储设备上
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        try {
            sdManager.write(device, savePath, is);
        } finally {
            ResourceMgr.closeQuietly(is);
        }

//        InfoHelper.prepare4Extract(device, savePath);

        String imagePath = device.getDeviceName() + ";" + savePath;
        return imagePath;
    }

    @RequestMapping("TranslocalImg.do")
    public void translocalImg(
            HttpServletRequest request, HttpServletResponse response,
            String imagePath) throws Exception {

        //设置网络代理
        setProxy();

        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String suffix = getSuffix(imagePath);
        String picPath = InfoHelper.getPicSavePath(request) + suffix;

        // 开始存储到存储设备上
        StorageDevice device = InfoHelper.getPicDevice();
        InputStream is = null;
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        try {
            URL url = new URL(imagePath);
            if(imagePath.startsWith("https")){
                //先忽略证书认证
                SslUtils.ignoreSsl();
                HttpsURLConnection conn= (HttpsURLConnection)url
                        .openConnection();
                conn.setConnectTimeout(1000);
                conn.connect();
                is = conn.getInputStream();// 通过输入流获取图片数据
                sdManager.write(device, picPath, is);
            }else{
                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpGet httpget = new HttpGet(imagePath);
                    try (CloseableHttpResponse res = httpclient.execute(httpget)) {
                        // 获取响应实体
                        HttpEntity entity = res.getEntity();
                        // 打印响应状态
                        System.out.println(res.getStatusLine());
                        if (entity != null) {
                            is = entity.getContent();
                            sdManager.write(device, picPath, is);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("转为本地图片时异常：" + e.getLocalizedMessage());
            e.printStackTrace();
            picPath = "";
        } finally {
            ResourceMgr.closeQuietly(is);
        }
        if (!StringUtils.isBlank(picPath)) {
            //加抽图任务
            InfoHelper.prepare4Extract(device, picPath);

            picPath = "../../xy/image.do?path=" + device.getDeviceName() + ";" + picPath;
            picPath = picPath.replaceAll("\\\\", "/");
        }

        JSONObject json = new JSONObject();
        json.accumulate("picPath", picPath);
        System.out.println("imagePath ==> " + imagePath);
        System.out.println("picPath  ==> " + picPath);
        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 上传图片
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "uploadwatermark.do", method = RequestMethod.POST)
    public void uploadWaterMark(HttpServletResponse response, HttpServletRequest request) throws Exception {
        // 初始化文件接收
        MultipartResolver resolver = new CommonsMultipartResolver(request.getSession()
                                                                          .getServletContext());
        MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
        String siteID = request.getParameter("siteID");

        // 拿到前台传过来的数据
        MultipartFile file = multipartRequest.getFile("file");
        String fileName = file.getOriginalFilename();

        // 保存图片并获得图片名
        String imagePath = null;

        InputStream is = file.getInputStream();
        try {
            imagePath = saveWaterMark(request, is, fileName, siteID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ResourceMgr.closeQuietly(is);
        }

        //把图片的保存路径放到json当中，以便于以后做扩展
        JSONObject json = new JSONObject();
        json.put("imagePath", imagePath);

        InfoHelper.outputJson(json.toString(), response);
    }

    /**
     * 删除水印图片
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "deletewatermark.do")
    public void deleteWaterMark(HttpServletResponse response, HttpServletRequest request) throws Exception {
        String path = request.getParameter("path");

        String result = deleteWaterMark(path);

        //返回图片删除信息
        JSONObject json = new JSONObject();
        json.put("result", result);

        InfoHelper.outputJson(json.toString(), response);
    }
    
    
    private String deleteWaterMark(String path) {
    	String result = "";
    	if(StringUtils.isBlank(path))
    		result = "图片路径为空，请检查所选图片是否存在！";
    	else{
    		StorageDevice device = InfoHelper.getWaterMarkDevice();
            String filePath = InfoHelper.getDevicePath(device) + File.separator + path.substring(path.lastIndexOf(";")+1);
            File file = new File(filePath);
            if(file.exists()){
            	file.delete();
            	result = "图片删除成功！";
            }
            else
            	result = "图片未找到！";
    	}
		return result;
	}

	/**
     * 保存图片
     *
     * @param request
     * @param is
     * @param fileName
     * @param siteID 
     * @return
     * @throws E5Exception
     */
    public String saveWaterMark(HttpServletRequest request, InputStream is, String fileName, String siteID)
            throws E5Exception {
        // 附件存储设备的名称
        StorageDevice device = InfoHelper.getWaterMarkDevice();
        // 构造存储的路径和文件名，目录为201505/13/，文件名用uuid
        String savePath = "siteID_" + siteID + File.separator + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        //InfoHelper.getPicSavePath(request) + fileName.substring(fileName.lastIndexOf("."));
        System.out.println("水印图片存储目录：" + savePath);
        // 开始存储到存储设备上
        StorageDeviceManager sdManager = SysFactory.getStorageDeviceManager();
        sdManager.write(device, savePath, is);

        return device.getDeviceName() + ";" + savePath;
    }
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
    /**
     * 初始化 水印
     *
     * @param response
     * @param request
     */
    @RequestMapping("initwatermark.do")
    public void initWaterMark(HttpServletResponse response, HttpServletRequest request) {

        SysUser user = ProcHelper.getUser(request);

        //结果json
        JSONObject json = new JSONObject();
        //服务器处理的状态
        String status = "success";
        String siteID = request.getParameter("siteID");
        try {
            //获得存储器
            StorageDevice device = InfoHelper.getWaterMarkDevice();
            String filePath = InfoHelper.getDevicePath(device);
            JSONArray array = new JSONArray();
            String _ext = "";
            String _name = "";
            String _deviceName = device.getDeviceName() + ";";

            //放水印的文件夹（公共目录）
            File folder = new File(filePath);
            //列出来所有的文件（公共目录）
            File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                for (File _file : files) {
                    _name = _file.getName();
                    //TODO 是否应该判断文件类型，即只有图片类型才能放到array中
                    _ext = _name.substring(_name.lastIndexOf(".") + 1).toLowerCase();
                    if ("jpg".equals(_ext) || "png".equals(_ext) || "gif".equals(_ext)) {
                        array.add(_deviceName + _name);
                    }
                }
            }
            String filePathNew = filePath + "/"+ "siteID_" + siteID;
            //放水印的文件夹（站点目录）
            File folderNew = new File(filePathNew);
            //列出来所有的文件（站点目录）
            File[] filesNew = folderNew.listFiles();
            if (filesNew != null && filesNew.length > 0) {
            	for (File _file : filesNew) {
            		_name = _file.getName();
            		//TODO 是否应该判断文件类型，即只有图片类型才能放到array中
            		_ext = _name.substring(_name.lastIndexOf(".") + 1).toLowerCase();
            		if ("jpg".equals(_ext) || "png".equals(_ext) || "gif".equals(_ext)) {
            			array.add(_deviceName + "siteID_" + siteID + File.separator + _name);
            		}
            	}
            }
            if (isAdmin(user.getUserID())){
                json.put("isAdmin", true);
            }else{
                json.put("isAdmin", false);
            }
            json.put("deviceName", _deviceName);
            json.put("result", array);
            json.put("device", filePath + ";" + filePathNew);
            InetAddress addr = InetAddress.getLocalHost();
            String IP = addr.getHostAddress().toString();//获得本机IP
            json.put("IP", IP);

        } catch (Exception e) {
            e.printStackTrace();
            status = "failure";
        } finally {
            json.put("status", status);
            InfoHelper.outputJson(json.toString(), response);
        }
    }

    public static void setProxy() {
        if ("是".equals(InfoHelper.getConfig("写稿", "启用代理服务器"))) {
            String proxy = InfoHelper.getConfig("写稿", "代理服务器");
            if (!StringUtils.isBlank(proxy)) {
                String port = InfoHelper.getConfig("写稿", "代理服务器端口");
                if (StringUtils.isBlank(port)) port = "80";

                System.setProperty("http.proxyHost", proxy);
                System.setProperty("http.proxyPort", port);

                System.setProperty("https.proxyHost", proxy);
                System.setProperty("https.proxyPort", port);
            }
        }
    }

    public static String getSuffix(String imagePath) {
        int pos = imagePath.lastIndexOf("/");
        String suffix = imagePath.substring(pos + 1);
        pos = suffix.lastIndexOf(".");
        if (pos < 0)
            suffix = ".jpg";
        else {
            suffix = suffix.substring(pos);
            if (suffix.length() > 5)
                suffix = ".jpg";
        }
        return suffix;
    }

    @RequestMapping(value = "uploadWord.do", method = RequestMethod.POST)
    @ResponseBody
    public Map uploadWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GetFileContentUtil getFile = new GetFileContentUtil();
        return getFile.uploadWord(request, response);
    }
    @RequestMapping(value = "proof.do", method = RequestMethod.POST)
    public void proof(HttpServletResponse response, HttpServletRequest request) throws Exception {
    	String content = WebUtil.get(request, "content");
    	String temp = "";
    	String error = "" ;
    	JSONObject result = new JSONObject() ;
    	if(content != null && !"".equals(content)){
			String pid = proofManager.getCheckArticlePID();
			if(pid != null && !"".equals(pid)) {
				CheckExtendInfo checkExtenInfo = proofManager.getParams();
				CheckType checkType = proofManager.getOtherParams();
				String baseResult = proofManager.CheckArticle(pid,content,checkExtenInfo,checkType);
				if(proofManager.getParam(baseResult, "ErrCode").equals("0")){ //获取检查结果
					String checkWordsResult = baseResult;
					temp = proofManager.getJson(content, proofManager.getError(checkWordsResult));
				}else{ //提交检查内容出错
					error = "错误代码:" + proofManager.getParam(baseResult, "ErrCode") + ",错误信息：" + proofManager.getParam(baseResult, "ErrMsg");
					System.out.println(error);
				}
			} else {
				error = "服务器连接失败！";
			}
		}else{
			error = "内容不能为空";
		}
    	result.put("error", error);
    	result.put("result", temp);
    	InfoHelper.outputJson(result.toString(), response);
    }
}
