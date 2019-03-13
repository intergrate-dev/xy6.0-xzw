<%
	String url="./Manoeuvre.do?invoke=doClean&DocLibID=";
	url+=request.getParameter("DocLibID");
	url+="&DocIDs=";
	url+=request.getParameter("DocIDs");
	url+="&UUID=";
	url+=request.getParameter("UUID");
	response.sendRedirect(url);
%>
