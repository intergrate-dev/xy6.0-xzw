<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>选用</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/bootstrap.min.css"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
</head>
<body>
	<iframe id="frmBase" name="frmBase" frameborder="0" 
		src="../../xy/column/OriginalCheck.jsp?type=radio&ids=0&siteID=<c:out value="${param.siteID}"/>
			&DocIDs=<c:out value="${param.DocIDs}"/>&DocLibID=<c:out value="${param.DocLibID}"/>">
	</iframe>
	

	<script>
		$(function(){
			var windowHeight = $(window).height();
			$("#frmBase").css('height',windowHeight);
		});
		
		function columnCancel() {
			var url = "../../e5workspace/after.do?UUID=" + "<c:out value="${param.UUID}"/>";
			$("#frmBase").attr("src", url);
		}
	</script>
	
</body>
</html>