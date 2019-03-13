<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title>Add Role</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="./js/query.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-Query.css"/>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<tr>
				<th class="titleTD"><i18n:message key="org.sort.role.id"/></th>
				<th><i18n:message key="org.sort.role.name"/></th>
				<th><i18n:message key="org.role.orgpath"/></th>
				<th> </th>
			</tr>
			<c:forEach items="${rolemap.rolelist}" var="roleinfo">
			<tr>
				<td class="titleTD"><c:out value="${roleinfo.roleID}"/></td>
				<td><c:out value="${roleinfo.roleName}"/></td>
				<td><c:out value="${roleinfo.orgNamePath}"/></td>
				<td>
					<input type="button" class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateRole('<c:out value="${roleinfo.roleID}"/>')">
				</td>
			</tr>
			</c:forEach>
		</table>
		</div>
	</body>
</html>