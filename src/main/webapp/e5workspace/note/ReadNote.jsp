<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<html>
<head>
	<title><i18n:message key="workspace.note.title" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css" />
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="noteWorkspace.js"></script>
</head>
<style>
.divStyle{
	height:300px;
	overflow:auto;
	border:0px solid #0078B6;
	background:#fff;
	font-size: 14px;
	line-height: 24px;
}
.divStyle p{
	margin:10px;
}
</style>
<body onload="setType(<c:out value="${note.noteType}"/>);">
<table align="center" class="table">
	<caption>
	<i18n:message key="workspace.note.notetitle" />
	</caption>
	<tr>
		<td width="10%"><i18n:message key="workspace.note.notetype" /></td>
		<td>
			<div id="notetype"></div>
		</td>
	</tr>
	<tr align="left">
		<td><i18n:message key="workspace.note.topic" /></td>
		<td class="wrap">
			<c:out value="${note.topic}"/>
		</td>
	</tr>
	<tr align="left">
		<td align="left" class="wrap" colspan="2">
			<div class="divStyle">
				<p><c:out value="${note.content}" /></p>
			</div>
		</td>
	</tr>
	<tr align="center">
		<td colspan="2"><input class="button" type="button"
			value="<i18n:message key="workspace.note.close"/>"
			onclick='window.parent.e5.dialog.close("noteDialog");'></td>
	</tr>
</table>
</body>
</html>