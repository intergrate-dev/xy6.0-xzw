<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<html>
	<head>
		<title>Notice</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css" />
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css" />
		<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="noteWorkspace.js"></script>
	</head>
<body>
<table cellpadding="0" cellspacing="0" class="table">
	<tr align="left" class="bluetd" style="CURSOR: hand">
		<th><i18n:message key="workspace.note.topic" /></th>
		<th><i18n:message key="workspace.note.sender" /></th>
		<th><i18n:message key="workspace.note.sendtime" /></th>
	</tr>
	<c:forEach items="${notelist}" var="note" varStatus="count">
		<c:if test="${note.read == false}">
			<tr onmouseover="this.bgColor='#E4E8EB';"
				onmouseout="this.bgColor='#ffffff';"
				ondblclick="noteopen(<c:out value='${count.index}'/>);">
				<input type="hidden"
					value="<c:out value='${notelist[count.index].noteID}'/>"
					id="noteid<c:out value='${count.index}'/>" />
				<td><B><c:out value="${note.topic}" /></B></td>
				<td><B><c:out value="${note.sender}" /></B></td>
				<td><B><c:out value="${note.sendTime}" /></B></td>
			</tr>
		</c:if>
		<c:if test="${note.read == true}">
			<tr onmouseover="this.bgColor='#E4E8EB';"
				onmouseout="this.bgColor='#ffffff';"
				ondblclick="noteopen(<c:out value='${count.index}'/>);">
				<td class="wrap">
				<input type="hidden"
					value="<c:out value='${notelist[count.index].noteID}'/>"
					id="noteid<c:out value='${count.index}'/>" />
				<c:out value="${note.topic}" /></td>
				<td><c:out value="${note.sender}" /></td>
				<td><c:out value="${note.sendTime}" /></td>
			</tr>
		</c:if>
	</c:forEach>
</table>
</body>

</html>
