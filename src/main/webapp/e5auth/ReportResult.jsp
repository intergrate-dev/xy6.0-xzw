<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript">
		if ("success" == '<c:out value="${result}"/>')
			alert('<i18n:message key="auth.success"/>');
		else
			alert('<i18n:message key="auth.failed"/>');
	</script>
</HEAD>
<BODY>
</BODY>
</HTML>
