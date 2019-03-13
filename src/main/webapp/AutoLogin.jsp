<%
	//String url = com.founder.newsedit.edit.v2.SimpleLoadBalance.getRedirectUrl().replace("e5workspace/Login.jsp", "Auto.jsp")+"?token="+request.getParameter("token");
	//System.out.print(url);
	//response.sendRedirect(url);
	response.sendRedirect("Auto.jsp"+"?token="+request.getParameter("token"));
%>

