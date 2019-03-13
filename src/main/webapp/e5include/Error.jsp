<%@page import="com.founder.e5.web.ErrorMessage"%>
<c:choose>
	<c:when test="${errors != null}">
		<Script>
			var errorWindow = window.open("_blank", "ErrorReport", "width=400,height=300");
			var doc = errorWindow.document;
			doc.open();
			doc.write("<HTML><title><i18n:message key="sys.error.title"/></title><BODY><H2><i18n:message key="sys.error.caption"/></H2>");
			<%
				java.util.List errorList = (java.util.List)request.getAttribute("errors");
				if (errorList != null)
				for (int errorIndex = 0; errorIndex < errorList.size(); errorIndex++)
				{%>
					doc.write("<i18n:message key="<%=((ErrorMessage)errorList.get(errorIndex)).getKey()%>"/>");
					<c:set var="errmessage" scope="request">
						<%=((ErrorMessage)errorList.get(errorIndex)).getMessage()%>
					</c:set>
					doc.write("<br/>(<c:out value="${errmessage}"/>)<br/>");
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
