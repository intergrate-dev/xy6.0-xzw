<%@ page language="java" contentType="text/html; charset=gb2312" %>
<%@ page import="com.jspsmart.upload.SmartUpload"%>
<%@ page import="java.io.File"%>

<%
  String path="watermarkimage";
String servletPath =request.getServletPath();
		String realPath = request.getSession().getServletContext().getRealPath(servletPath);
        path=new File(realPath).getParent() +"//" +path;
	//新建一个SmartUpload对象
	SmartUpload su = new SmartUpload();
	//上传初始化
	su.initialize(pageContext);
	// 设定上传限制
	//1.限制每个上传文件的最大长度。
	su.setMaxFileSize(10000000);

	//2.限制总上传数据的长度。
	su.setTotalMaxFileSize(20000000);

	//3.设定允许上传的文件（通过扩展名限制）,仅允许doc,txt文件。
	su.setAllowedFilesList("jpg,png,gif,jpeg,bmp");
	boolean sign = true;
	String filename=null;
	//4.设定禁止上传的文件（通过扩展名限制）,禁止上传带有exe,bat,jsp,htm,html扩展名的文件和没有扩展名的文件。
	try {
		su.setDeniedFilesList("exe,bat,jsp,htm,html");
		//上传文件
		su.upload();
		//将上传文件保存到指定目录
		su.save(path);
		
		com.jspsmart.upload.File file = su.getFiles().getFile(0);     
		filename=file.getFileName();
          
	} catch (Exception e) {
		e.printStackTrace();
		sign = false;
	}
	if(sign==true)
	{
		out.println("<script>parent.callback('水印图章上传成功','"+filename+"')</script>");
	}else
	{
		out.println("<script>parent.callback('水印图章上传失败','')</script>");
	}
%>
