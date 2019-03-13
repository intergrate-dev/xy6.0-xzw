<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>会员转正</title>
</head>
<%=request.getAttribute("content")%>
</html>
<script>
	var potential_form = {
		init : function() {
			$("#mPotential").val(0);//设置标记为会员
		}
	}
	$(function(){
		potential_form.init();
	});
</script>
