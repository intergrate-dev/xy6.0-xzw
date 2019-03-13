﻿<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 

	
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="script/article.js"></script>
	<script type="text/javascript">
		var ueditor_size = {width : 828,height : 360}
	</script>
	<style>
		html{background-color: #f6f6f6; overflow-x: hidden;}
		
		.substance{
			font-size: 13px;
			margin-left: 80px;
		}
		.editContainer{margin-left: 73px !important;margin-top: 20px;}
		#edui1{
			margin-top: -36px;
			margin-left: 42px;
		}
		
		.underTop{position:absolute;top:735px;left: 127px; margin-bottom:20px;}
		.underTop input {
		    border: none;
		    color: #fff;
		    padding: 5px 20px;
		    border-radius: 3px;
		    font-family: "microsoft yahei";
		    font-size: 12px;
		}
		.underTop .dosave {
		    background-color: #1bb8fa;
		    margin-right: 26px;
		}
		.underTop .docancle {
		    background-color: #b1b1b1;
		}
		.underTop span {
			display:inline-block;
			margin-left:20px;
			font-size: 14px;
			color:red;
		}
	</style>
</head>
<body>
<iframe id="iframe" name="iframe" style="display:none;"></iframe>
<form id="form" method="post" action="../../e5workspace/manoeuvre/FormSubmit.do">
	<%=request.getAttribute("formContent")%>
		<span class="left substance">内容</span>
		<%@include file="../article/simpleEditor.html"%>
	
	<div class="underTop">
		<input class="dosave" type="button" id="btnSave" value="保存"/>
		<input class="docancle" type="button" id="btnCancel" value="关闭"/>
		<span>调整完成后请重发版面</span>
	</div>
</form>
</body>
</html>
