<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<html>
<head>
	<title><i18n:message key="workspace.docview.title"/></title>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<style type="text/css">
body{
	margin:10px;
}
.tablecontent{
	width:100%;
	font-size:12px;
}
.tablecontent td {
	border-bottom: 1px solid #B9CFE4;
	padding:5px;
}
.custview-span{
	vertical-align:top;
}
.custview-label{
	float:left;
	margin-top:5px;
	margin-bottom:5px;
	line-height: 24px;
}
.custview-field{
	float:left;
	margin-top:5px;
	margin-bottom:5px;
	line-height: 24px;
}
.custview-textarea{
	float:left;
	margin-top:5px;
	margin-bottom:5px;
	line-height: 24px;
	border:0px;
}
	</style>
	<c:forEach var="cssFile" items="${pathCSS}">
		<link type="text/css" rel="StyleSheet" href="<c:out value="${cssFile}"/>"/>
	</c:forEach>
	<c:forEach var="jsFile" items="${pathJS}">
		<script type="text/javascript" src="<c:out value="${jsFile}"/>"></script>
	</c:forEach>
	<script type="text/javascript">
		function doInit() {
			var preview = "<c:out value="${preview}"/>";
			if (preview) return;
			
			var tbody = document.getElementsByTagName("tbody");
			if (!tbody) return;
			
			tbody = tbody[0];
			
			var width = parseInt(tbody.getAttribute("customwidth")) + 20;
			var height = parseInt(tbody.getAttribute("customheight")) + 40;
			
			if (height > screen.availHeight) height = screen.availHeight;
			if (width > screen.availWidth) width = screen.availWidth;
			
			window.resizeTo(width, height);
		}
	</script>
</head>
<body onload="doInit()">
	<table class="tablecontent"><%=request.getAttribute("content")%></table>
</body>
</html>
