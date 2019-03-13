<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.founder.e5.workspace.app.param.*" %>
<%
	DefaultParam param = (DefaultParam)request.getAttribute("offerkey");
	String strUrl = ParaGenerate.getAfterCancelUrl(param);
	response.sendRedirect(strUrl);
%>
