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
	<table width="100%" cellPadding="0" cellSpacing="0" class="table">
		<tr>
			<th class="titleTD" ><i18n:message key="org.sort.user.id"/></th>
			<th><i18n:message key="org.sort.user.name"/></th>
			<th><i18n:message key="org.sort.user.code"/></th>
			<th><i18n:message key="org.user.orgpath"/></th>
			<th> </th>
		</tr>
		<c:forEach items="${usermap.userlist}" var="userinfo">
		<tr>
			<td class="titleTD"><c:out value="${userinfo.userID}"/></td>
			<td><c:out value="${userinfo.userName}"/></td>
			<td><c:out value="${userinfo.userCode}"/></td>
			<td><c:out value="${userinfo.orgNamePath}"/></td>
			<td>
				<input type="button" class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateUser('<c:out value="${userinfo.userID}"/>')">
				<input type="button" class="button" value="<i18n:message key="org.query.result.show"/>" onclick="showUser('<c:out value="${userinfo.userID}"/>')">
			</td>
		</tr>
		</c:forEach>
	</table>
	</div> 
</body> 
</html>