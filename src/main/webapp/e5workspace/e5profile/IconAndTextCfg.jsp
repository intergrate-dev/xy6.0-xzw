<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><i18n:message key="workspace.ps.iconAndText.title"/></title>
	<link type="text/css" rel="stylesheet" href="../../e5style/work.css"/>
	<style type="text/css">
		.butt {
			background-image: url(../../images/but_blank.gif);
		}
	</style>
	<script type="text/JavaScript">
		function doInit(){
			document.form1.cfgValue.value="<c:out value="${cfgValue}"/>";
		}
	</script>
</head>
<body onload="doInit()">
<br />
<br />
<br />
	<form name="form1" action="iconAndTextCfg.do" method="post">
		<input name="action" type="hidden" value="setIconAndText"/>

		<i18n:message key="workspace.ps.iconAndText.title"/>
		<p/>
		<select name="cfgValue" style="width:200px;">
			<option value="1"><i18n:message key="workspace.ps.iconAndText.onlyText"/></option>
			<option value="2"><i18n:message key="workspace.ps.iconAndText.onlyIcon"/></option>
			<option value="3"><i18n:message key="workspace.ps.iconAndText.leftIconRightText"/></option>
			<option value="4"><i18n:message key="workspace.ps.iconAndText.topIconDownText"/></option>
		</select>

		&nbsp;&nbsp;
		<input type="submit" class="butt" value="<i18n:message key="workspace.ps.resTree.submit"/>"/>
	</form>

</body>
</html>
