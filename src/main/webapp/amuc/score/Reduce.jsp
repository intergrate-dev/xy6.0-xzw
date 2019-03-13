<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script>
	var reduce_form = {
		init : function() {
			$("#msType").val(2);//设置标记为积分扣减
		}
	}
	$(function(){
		reduce_form.init();
	});
</script>
<head>
	<title>积分扣减</title>
</head>
<%=request.getAttribute("content")%>
</html>

