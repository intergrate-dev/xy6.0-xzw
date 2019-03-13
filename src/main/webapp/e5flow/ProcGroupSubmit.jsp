<%@include file="../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<script type="text/javascript">
	var errors = "<c:out value="${errors}"/>";
	if (errors && (errors != "null"))
		window.alert("操作失败：[ " + errors + " ]请联系管理员");
	else
	{
		window.alert("操作成功");
	}
</script>