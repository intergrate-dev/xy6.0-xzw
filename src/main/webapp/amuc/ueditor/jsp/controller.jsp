<%@page import="com.founder.e5.web.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="com.founder.amuc.ueditor.ActionEnter"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%

    request.setCharacterEncoding("utf-8");
	response.setHeader("Content-Type" , "text/html");
	
	String rootPath = application.getRealPath( "/" );
	//String rootPath = WebUtil.getRoot(request);
	String result = new ActionEnter(request, rootPath).exec();
	out.write(result);
%>