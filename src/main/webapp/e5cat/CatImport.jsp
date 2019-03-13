<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="catImport.title"/></title>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
</head>
<script language="javascript">
function checkInput()
{
	if(form1.file.value == '')
	{
		alert("<i18n:message key="catImport.alert.file"/>");
		return false;
	}

	return true;
}
</script>
<body>
<c:if test="${errors!=null}">
	<font color="red"><i18n:message key="cat.error"/><c:out value="${errors}"/></font>
</c:if>
<form name="form1" method="post" action="CatImport.do?functionName=import" enctype="multipart/form-data" onsubmit="return checkInput();">
<input type="hidden" name="catID" value="<c:out value="${catID}"/>">
<input type="hidden" name="catType" value="<c:out value="${catType}"/>">	
	<table border="0" cellpadding="3" cellspacing="0" class="table">
		<caption><i18n:message key="catImport.title"/></caption>
		<tr align="center">
			<th class="w90"><i18n:message key="catImport.file"/></td>
			<td width="200px"><input type="file" name="file"></td>
		</tr>
		<tr align="center">
			<td colspan="2">
			    <input type="submit" value="<i18n:message key="cat.button.submit"/>" class="button">
				<input type="button" value="<i18n:message key="cat.button.cancel"/>" onclick="window.close();" class="button">
			</td>
		</tr>
	</table>
</form>
</center>
</body>
</html>
