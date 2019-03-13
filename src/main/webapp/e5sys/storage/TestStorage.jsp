<%@page contentType="text/xml;charset=UTF-8"%><%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");
	response.setDateHeader("Expires",0);
	
	String url = request.getParameter("url");
	String usr = request.getParameter("user");
	String pwd = request.getParameter("pwd");
	boolean ret = com.founder.e5.commons.VerifyUtil.testFTPDevice(url,usr,pwd);
	out.print("<ret>"+ret+"</ret>");
%>