<%@ page language="java" contentType="text/html; charset=gb2312" %>
<%@ page import="com.jspsmart.upload.SmartUpload"%>
<%@ page import="java.io.File"%>

<%
  String path="watermarkimage";
String servletPath =request.getServletPath();
		String realPath = request.getSession().getServletContext().getRealPath(servletPath);
        path=new File(realPath).getParent() +"//" +path;
	//�½�һ��SmartUpload����
	SmartUpload su = new SmartUpload();
	//�ϴ���ʼ��
	su.initialize(pageContext);
	// �趨�ϴ�����
	//1.����ÿ���ϴ��ļ�����󳤶ȡ�
	su.setMaxFileSize(10000000);

	//2.�������ϴ����ݵĳ��ȡ�
	su.setTotalMaxFileSize(20000000);

	//3.�趨�����ϴ����ļ���ͨ����չ�����ƣ�,������doc,txt�ļ���
	su.setAllowedFilesList("jpg,png,gif,jpeg,bmp");
	boolean sign = true;
	String filename=null;
	//4.�趨��ֹ�ϴ����ļ���ͨ����չ�����ƣ�,��ֹ�ϴ�����exe,bat,jsp,htm,html��չ�����ļ���û����չ�����ļ���
	try {
		su.setDeniedFilesList("exe,bat,jsp,htm,html");
		//�ϴ��ļ�
		su.upload();
		//���ϴ��ļ����浽ָ��Ŀ¼
		su.save(path);
		
		com.jspsmart.upload.File file = su.getFiles().getFile(0);     
		filename=file.getFileName();
          
	} catch (Exception e) {
		e.printStackTrace();
		sign = false;
	}
	if(sign==true)
	{
		out.println("<script>parent.callback('ˮӡͼ���ϴ��ɹ�','"+filename+"')</script>");
	}else
	{
		out.println("<script>parent.callback('ˮӡͼ���ϴ�ʧ��','')</script>");
	}
%>
