<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="resource.flow.title"/></title>
	</head>
	<frameset cols="200,*" frameborder="no">
  		<frame src="ResourceFlowFVUserList.jsp?flag=<c:out value="${param.flag}"/>&FlowNodeID=<c:out value="${param.FlowNodeID}"/>&DocTypeID=<c:out value="${param.DocTypeID}"/>" name="listframe">
		<frame src="" name="mainframe">
	</frameset>
</html>
