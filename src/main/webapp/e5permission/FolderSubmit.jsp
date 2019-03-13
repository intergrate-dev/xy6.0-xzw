<%@include file="../e5include/IncludeTag.jsp"%>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

<Script type="text/javascript">
	window.parent.show("<c:out value="${error}"/>", "<c:out value="${errormessage}"/>");
</Script>
</HTML>
