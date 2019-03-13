<%@include file="../../e5include/IncludeTag.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
</head>
<frameset cols="400,*" frameborder="NO" border="0" framespacing="0">
	<frame src="./FieldGroupTree.jsp?catTypes=<c:out value="${catType}"/>" name="leftFrame" scrolling="AUTO">
	<frame src="FieldGroupStart.jsp" name="mainBody">
</frameset>
<noframes><body></body></noframes>
</html>
