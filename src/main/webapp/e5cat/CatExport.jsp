<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="catExport.title"/></title>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
</head>
<body>
<form name="form1" method="post" action="CatExport.do" target="_blank">
<input type="hidden" name="catID" value="<c:out value="${param.catID}"/>">
<input type="hidden" name="catType" value="<c:out value="${param.catType}"/>">	
	<table border="0" cellpadding="3" cellspacing="0" class="table">
		<caption><i18n:message key="catExport.title"/></caption>
		<tr align="center">
			<td class="bottomlinetd"><label for="children"><input style="border:none" type="checkbox" name="children" value="true" id="children"><i18n:message key="catExport.children"/></label></td>
		</tr>
		<tr align="center">
			<td class="bottomlinetd">
			    <input type="button" value="<i18n:message key="cat.button.submit"/>" onclick="form1.submit();window.close();" class="button">
				<input type="button" value="<i18n:message key="cat.button.cancel"/>" onclick="window.close();" class="button">
			</td>
		</tr>
	</table>
</form>
</body>
</html>
<%@include file="../e5include/Error.jsp"%>