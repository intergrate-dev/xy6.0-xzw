<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
	<title>import</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript">
		function checkInput(){
			if(form1.file.value == ''){
				alert("Please select a xml file.");
				return false;
			}
			return true;
		}
	</script>
	</head>
	<body>
		<div class="mainBodyWrap">
		<form name="form1" method="post" action="OrgExchangeAction.do?invoke=importNode" enctype="multipart/form-data" onsubmit="return checkInput();">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><i18n:message key="org.menu.importorg"/></caption>
			<tr>
				<td>
					<input type="hidden" name="orgID" value="<c:out value="${orginfo.orgID}"/>">
					<input type="hidden" name="treeid" value="<c:out value="${orginfo.treeid}"/>">
					<input type="file" name="file">
				</td>
			</tr>
			<tr>
				<td>
					<input type="submit" class="button" value="<i18n:message key="org.submit"/>" >
					<input type="button"  class="button" value="<i18n:message key="org.cancel"/>" onclick="window.close();">
				</td>
			</tr>
		</table>
		</form>
		<div>
	</body>
</html>

