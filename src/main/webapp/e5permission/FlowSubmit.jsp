<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
<%
	java.util.List list = (java.util.List)request.getAttribute("errors");
	for (int i = 0; i < list.size(); i++)
	{%>
		<i18n:message key="<%=((com.founder.e5.web.ErrorMessage)list.get(i)).getKey()%>"/>
		:
		<%=((com.founder.e5.web.ErrorMessage)list.get(i)).getMessage()%><BR>

	<%}
%>

<c:forEach var="err" items="${errors}">
	<!--i18n:message key=""/-->
	<!--i18n:message key="<c:out value="${err.key}"/>"/-->
	<c:out value="${err.message}"/>
	<BR>
</c:forEach>
</HTML>
