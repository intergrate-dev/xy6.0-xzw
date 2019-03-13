<style type="text/css">
	body{
		padding: 0 0 0 10px;
		overflow:hidden;
	}

	iframe{
		width:100%;
		hight:500%;
	}
	
</style>
<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>源稿树基本属性表单</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<script src="../script/jquery/jquery.min.js"></script>
	<script src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
</head>
<body>
	
	<iframe id="frmBase" name="frmBase" frameborder="0" 
		src="../../e5workspace/manoeuvre/Form.do?code=formOriginalColumn&DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>"></iframe>
	<script type="text/javascript">
		$(function(){
			var windowHeight = $(window).height();
			$("#frmBase").css('height',windowHeight);
		})
	</script>
</body>
</html>