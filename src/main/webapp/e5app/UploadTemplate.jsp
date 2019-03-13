<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>import</title>
<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
</head>
<script language="javascript">
function checkInput()
{
	if(form1.file.value == '')
	{
		alert("Please select a xml file.");
		return false;
	}

	return true;
}
function returnback()
{
	document.location.href="ApplicationSystemAction.do";
}
</script>
<body>
<form name="form1" method="post" action="ApplicationSystemAction.do?invoke=uploadtemplate" enctype="multipart/form-data" onsubmit="return checkInput();">
<br>
<center>
<input type="file" name="file">
<br><br>
<input type="submit" value="<i18n:message key="app.submit"/>" >
<input type="button" value="<i18n:message key="app.back"/>" onclick="returnback();">
</form>
</center>
</body>
</html>

