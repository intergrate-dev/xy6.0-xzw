<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head>
	<title>Add Role</title>
	<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
</head> 
<body>
	<div class="mainBodyWrap">
	<table cellpadding="0" cellspacing="0" class="table">
		<caption><c:out value="${treeinfo.treeNodeName}"/></caption>
		<tr>
			<th><i18n:message key="org.user.name"/></th>
			<th><i18n:message key="org.user.orgpath"/></th>
			<th><i18n:message key="org.role.valid.startdate"/></th>
			<th><i18n:message key="org.role.valid.enddate"/></th>
		</tr>
		<c:forEach items="${roleinfo.userrole}" var="ruleuserinfo">
			<tr>
				<td><c:out value="${ruleuserinfo.userName}"/></td>
				<td><c:out value="${ruleuserinfo.orgNamePath}"/></td>
				<td><c:out value="${ruleuserinfo.startDate}"/></td>
				<td><c:out value="${ruleuserinfo.endDate}"/></td>
			</tr>
		</c:forEach>
	</table>
	</div>
    <script>
		<c:if test="${roleinfo.treeinfo.treeid!='-1'}">
					parent.leftFrame.updateRoleNode("<c:out value="${roleinfo.treeinfo.treeNodeName}"/>","<c:out value="${roleinfo.treeinfo.treeid}"/>");
		</c:if>
		<c:if test="${roleinfo.treeinfo.treeid=='-1'}">
			alert("<i18n:message key="org.query.role.refresh.alert"/>");
			window.close();
		</c:if>
    </script>
</body> 
</html>