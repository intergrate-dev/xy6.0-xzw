<%@page import="com.founder.e5.web.ErrorMessage"%>
<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>

<c:choose>
	<c:when test="${errors != null}">
		<Script>
			var errorWindow = window.open("", "ErrorReport", "width=400,height=300");
			var doc = errorWindow.document;
			doc.open();
			doc.write("<HTML><BODY><H2>Error</H2>");
			<%
				java.util.List errorList = (java.util.List)request.getAttribute("errors");
				if (errorList != null)
				for (int errorIndex = 0; errorIndex < errorList.size(); errorIndex++)
				{%> doc.write('<% request.setCharacterEncoding("UTF-8"); %>');
					doc.write('<i18n:message key="<%=((ErrorMessage)errorList.get(errorIndex)).getKey()%>" /><br/>');
					doc.write('<%=((ErrorMessage)errorList.get(errorIndex)).getMessage()%>');
					doc.write("<BR/>");
				<%}
			%>
			doc.write("</BODY></HTML>");
			doc.close();
			errorWindow.focus();
		</Script>
	</c:when>
	<c:otherwise>
		<Script>
			try{
				if (show) show();
			}catch (e){}
		</Script>
	</c:otherwise>
</c:choose>
