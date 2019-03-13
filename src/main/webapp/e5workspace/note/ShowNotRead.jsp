<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<html>
<head>
	<title>Notice</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="noteWorkspace.js"></script>
</head>
<body>
  <table width="100%">
	<tr>
		<td style="display:none"><input class="button" type="button" value="<i18n:message key="workspace.note.createnote"/>" onclick="window.showModalDialog('main.htm','','dialogWidth=600px;dialogHeight=500px');window.location.reload();"></td>
		<td align="right"><input class="button" type="button" value="<i18n:message key="workspace.note.historynote"/>" onclick="getHistory();"></td>
	</tr>
  </table>
  <table align="center" cellpadding="5" cellspacing="0" class="table">
	<tr class="bluetd">	    		
		<th><i18n:message key="workspace.note.topic"/></th>
		<th><i18n:message key="workspace.note.sender"/></th>
		<th><i18n:message key="workspace.note.sendtime"/></th>
	</tr>   
	<c:forEach items="${notelist}" var="note" varStatus="count">
		<tr onmouseover="this.bgColor='#E4E8EB';" onmouseout="this.bgColor='#ffffff';" ondblclick="noteopen(<c:out value='${count.index}'/>);changefont(<c:out value='${count.index}'/>);">
			<td class="wrap">
				<input type="hidden" value="<c:out value='${notelist[count.index].noteID}'/>" id="noteid<c:out value='${count.index}'/>"/>
				<div id="topictd<c:out value='${count.index}'/>" style="font-weight: 800"><c:out value="${note.topic}" /></div>
			</td>
			<td><div id="persontd<c:out value='${count.index}'/>" style="font-weight: 800"><c:out value="${note.sender}" /></div></td>
			<td><div id="timetd<c:out value='${count.index}'/>" style="font-weight: 800"><c:out value="${note.sendTime}" /></div></td>
		</tr>		
	</c:forEach>
</table>
</body>
</html>