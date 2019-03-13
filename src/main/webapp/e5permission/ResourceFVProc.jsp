<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="fvproc.title"/></title>
	</head>
	<frameset cols="200,*" frameborder="no">
  		<frame src="ResourceFVProcList.jsp?FVID=<c:out value="${param.FVID}"/>" name="listframe">
		<frame src="" name="mainframe">
	</frameset>
</html>
