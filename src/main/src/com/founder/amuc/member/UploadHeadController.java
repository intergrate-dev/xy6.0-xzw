package com.founder.amuc.member;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.founder.e5.commons.DateUtils;
import com.founder.e5.context.E5Exception;
import com.founder.e5.doc.Document;
import com.founder.e5.doc.DocumentManager;
import com.founder.e5.doc.DocumentManagerFactory;
import com.founder.e5.sys.StorageDevice;
import com.founder.e5.sys.StorageDeviceManager;
import com.founder.e5.sys.StorageDeviceReader;
import com.founder.e5.web.BaseController;
import com.founder.e5.web.util.UploadHelper;
import com.founder.e5.workspace.ProcHelper;
import com.founder.amuc.action.ReadPermissionManager;
import com.founder.amuc.api.MemberAdapter;
import com.founder.amuc.commons.BaseHelper;
import com.founder.amuc.commons.ImageHelper;
import com.founder.xy.jpublish.PublishHelper;

/**
 * @author Ren Yanfang
 * 2014-7-3
 */
@Controller
@RequestMapping("/amuc/member")
@SuppressWarnings({ "rawtypes" })
public class UploadHeadController extends BaseController{
  @Override
  protected void handle(HttpServletRequest request,
      HttpServletResponse response, Map model) throws Exception {
  }
  
  @RequestMapping("/InitHeadImg.do")
  public ModelAndView InitHeadImg(HttpServletRequest request,HttpServletResponse response) throws Exception {
    long memberID = getInt(request, "DocIDs");
    int docLibID = getInt(request, "DocLibID");
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.get(docLibID, memberID);
    String headSrc = doc.getString("mHead");
    
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("memberID", memberID);
    model.put("docLibID", docLibID);
    model.put("headSrc", headSrc);  
    
    return new ModelAndView("amuc/member/AddHeadImg", model);
  }
  
  //上传文件
  @RequestMapping("/UploadHeadImg.do")
  public void UploadHeadImg(HttpServletRequest request,HttpServletResponse response, Map model) throws Exception {
    
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",Locale.CHINESE);
    String newPrefix = ProcHelper.getUserCode(request) + formatter.format(new Date());
    
    String contextPath = request.getSession().getServletContext().getRealPath("/");
    String newPath = contextPath + "/amuc/member/headImg/";
    
    File img = UploadHelper.upload(request, newPath, newPrefix);
    
    String fileName=img.getName();
    String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
    String result =newPath+ fileName.substring(0,fileName.lastIndexOf("."))+".jpg";
    BufferedImage image;
    String fn;
    if(!(fileExt.equals("jpg")) && !(fileExt.equals("jpeg"))) {
      ImgConvert(img,fileExt,result);
          File f=new File(result);
          image = ImageIO.read(f);
          fn = f.getName();
          img.delete();
    } else {
      image = ImageIO.read(img);
      fn = img.getName();
    }
    int width = image.getWidth();
    int height = image.getHeight();
    
    fn = fn.substring(fn.lastIndexOf(File.separator) + 1);
    
    response.sendRedirect("uploadHeadOk.html?=headImg/" + fn + "," + width + "," + height);
  }
  
  @RequestMapping("/SaveHeadImg.do")
  public ModelAndView SaveHeadImg(HttpServletRequest request,HttpServletResponse response) throws Exception {
    
    long memberID = Long.parseLong(get(request, "MemberID"));
    int docLibID = Integer.parseInt(get(request, "DocLibID"));
    int width=120;
    //获取缩放和剪切参数
    String cutPos = request.getParameter("cut_pos");
    String[] pos = cutPos.split(",");
    //左上角坐标
    int x = Integer.parseInt(pos[0]);
    int y = Integer.parseInt(pos[1]);
    //缩放后的图片宽度
    int picWidth = Integer.parseInt(pos[2]);
    int picHeight = Integer.parseInt(pos[3]);
    String path= request.getSession().getServletContext().getRealPath("/amuc/member/");
    String fileName = request.getParameter("cut_url");
    String srcFileName = path + File.separator +fileName;
    
    StorageDeviceReader deviceReader = (StorageDeviceReader)com.founder.e5.context.Context.getBean(StorageDeviceReader.class);
    StorageDevice device = deviceReader.getByName("头像存储");
    StorageDeviceManager deviceManager = (StorageDeviceManager)com.founder.e5.context.Context.getBean(StorageDeviceManager.class);

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm",Locale.CHINESE);
    String newPrefix = formatter.format(new Date());
    String fileExt = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();   
    String resfilename = "portrait" + memberID + "at" + newPrefix + DateUtils.getTimestamp().getTime() + "." + fileExt;
    String destFile =path+"/headImg/" + resfilename;
    String imgHttppath = device.getHttpDeviceURL()+"/"+resfilename;
    
    String ntfsDevicePath = device.getNtfsDevicePath();
	String webroot = BaseHelper.getConfig("翔宇CMS", "发布服务", "发布根目录");
    //增加trans
	if(ntfsDevicePath != null){
		ntfsDevicePath = ntfsDevicePath.substring(webroot.length() + 1);
		ntfsDevicePath = org.apache.commons.lang.StringUtils
				.strip(ntfsDevicePath, "/")
				.replace("/", "~");
		PublishHelper.writePath(ntfsDevicePath + "~" + resfilename,PublishHelper.getTransPath());
	}

    try {
      ImageHelper.ZoomTheImage(srcFileName, destFile,picWidth,picHeight);
      HeadImage headImage = new HeadImage(x,y,width,width);
      headImage.setSrcPath(destFile);  
      headImage.setSubPath(destFile);
      ImageHelper.cut(headImage);
      InputStream in = new FileInputStream(destFile);
      deviceManager.write(device, resfilename , in);
      
      this.updateMemberInfo(memberID, docLibID,imgHttppath);
      WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
      MemberAdapter memberAdapter = (MemberAdapter)wac.getBean("memberAdapter");
      //MemberAdapter memberAdapter = (MemberAdapter)com.founder.e5.context.Context.getBean(MemberAdapter.class);
      memberAdapter.getPortraitCache().put(String.valueOf(memberID), imgHttppath);

    } catch (Exception e) {
      e.printStackTrace();
    }
    
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("headSrc", imgHttppath); 
    
    return new ModelAndView("amuc/member/AddHeadImg", model);
  }
  
  public void updateMemberInfo(long memberID, int docLibID, String fileName) throws E5Exception {
    DocumentManager docManager = DocumentManagerFactory.getInstance();
    Document doc = docManager.get(docLibID, memberID);
    doc.set("mHead", fileName);
    docManager.save(doc);
  }
  
  //bmp、gif、png转换为jpg
  private void ImgConvert(File file,String fileExt,String result) throws Exception {
    if(fileExt.equals("bmp")) {
        Image img = ImageIO.read(file);  
        BufferedImage tag = new BufferedImage(img.getWidth(null), img.getHeight(null),    BufferedImage.TYPE_INT_RGB);  
        tag.getGraphics().drawImage(img.getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);  
        String formatName = result.substring(result.lastIndexOf(".") + 1);
        ImageIO.write(tag, formatName, new File(result));
    } else {  
      file.canRead();  
          BufferedImage src = ImageIO.read(file);  
          ImageIO.write(src, fileExt, new File(result)); 
    }
  }

  public void SaveFileFromInputStream(InputStream stream,String filename) throws IOException
  {      
      FileOutputStream fs=new FileOutputStream(filename);
      byte[] buffer =new byte[1024*1024];
      int byteread = 0; 
      while ((byteread=stream.read(buffer))!=-1)
      {
         fs.write(buffer,0,byteread);
         fs.flush();
      } 
      fs.close();
      stream.close();      
  } 

}