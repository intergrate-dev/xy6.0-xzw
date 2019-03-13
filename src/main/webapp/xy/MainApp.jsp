<%@include file="../e5include/IncludeTag.jsp" %>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title><%=com.founder.e5.context.Context.getSystemName()%><c:out value="${subTab.name}"/></title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
	
	<link rel="stylesheet" type="text/css" href="script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="css/mainApp.css">
</head>
<script type="text/javascript">
	var main_param = {
		docTypeID : "<c:out value="${domInfo.docTypeID}"/>",
		docLibID : "<c:out value="${domInfo.docLibID}"/>",
		fvID : "<c:out value="${domInfo.folderID}"/>",
		ruleFormula : "<c:out value="${domInfo.rule}"/>",
		listID : "<c:out value="${domInfo.listID}"/>",
		queryID : "<c:out value="${domInfo.queryID}"/>",
		curTab : "<c:out value="${subTab.id}"/>",
		exportable : "<c:out value="${subTab.exportable}"/>",
		siteID : "<c:out value="${siteID}"/>",
		catTypeID : "<c:out value="${catTypeID}"/>"
	}
</script>
<body scroll="yes" style="overflow:auto;">
<div id="wrapMain">
	<div id="searchBtnArea">
		<select id="column">
		<c:forEach var="column" items="${columns}">
			<option value="<c:out value="${column.id}"/>"><c:out value="${column.casNames}"/></option>
		</c:forEach>
		</select>
		<a id="btnRefresh"><i18n:message key="workspace.status.refresh"/></a>
	</div>
	<div id="main">
		<%@include file="inc/StatusbarApp.inc" %>
	</div>
</div>
</body>
<script type="text/javascript" src="script/MainApp.js"></script>