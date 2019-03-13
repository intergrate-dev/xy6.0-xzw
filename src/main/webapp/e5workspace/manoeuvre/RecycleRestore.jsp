<%
	String url="./Manoeuvre.do?invoke=doReconvert&DocLibID=";
	url+=request.getParameter("DocLibID");
	url+="&DocIDs=";
	url+=request.getParameter("DocIDs");
	url+="&UUID=";
	url+=request.getParameter("UUID");
	url+="&UserID=";
	url+=request.getParameter("UserID");
	response.sendRedirect(url);
%>
