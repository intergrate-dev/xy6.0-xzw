<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
	<head>
		<title><i18n:message key="fvproc.title"/></title>
	</head>
	<frameset cols="200,*" frameborder="no">
  		<frame src="MultiCodeProcList.jsp?code=<c:out value="${param.code}"/>&docTypeID=<c:out value="${param.docTypeID}"/>" name="listframe">
		<frame src="" name="mainframe">
	</frameset>
</html>
