<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../pic/script/edit.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
</head>
<body>
<input type="button" id="preViewCancel" value="关闭" style="width:50px;" />
<span>宽度：${param.width}</span><span>高度：${param.height}</span>
<div>
	<img style="max-height: 500px; max-width: 980px;" src="${param.src}" />
</div>
<script type="text/javascript">
	$("#preViewCancel").click(parent.preViewCancel);
</script>

</body>
</html>