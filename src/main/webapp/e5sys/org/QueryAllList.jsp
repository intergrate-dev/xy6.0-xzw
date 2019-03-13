<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title>Add Role</title>
		<script type="text/javascript" src="./js/query.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<tr>
				<th class="titleTD"><i18n:message key="org.query.result.type"/></th><th><i18n:message key="org.query.result.name"/></th>
				<th><i18n:message key="org.user.orgpath"/></th>
				<th></th>
			</tr>
			<c:forEach items="${allmap.alllist}" var="allinfo">
			<tr>
				<c:choose>
				<c:when test="${allinfo.queryObject=='user'}">
				<td class="titleTD"><i18n:message key="org.query.result.user"/></td>
				<td><c:out value="${allinfo.userName}"/></td>
				<td><c:out value="${allinfo.orgNamePath}"/></td>
				<td>
					<c:if test="${allinfo.userID > 0}">
					<input type="button" class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateObj('<c:out value="${allinfo.queryObject}"/>','<c:out value="${allinfo.userID}"/>')">
					<input type="button" class="button" value="<i18n:message key="org.query.result.show"/>" onclick="showUser('<c:out value="${allinfo.userID}"/>')">
					</c:if>
				</td>
				</c:when>
				<c:when test="${allinfo.queryObject=='role'}">
				<td class="titleTD"><i18n:message key="org.query.result.role"/></td>
				<td><c:out value="${allinfo.roleName}"/></td>
				<td><c:out value="${allinfo.orgNamePath}"/></td>
				<td>
					<input type="button"  class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateObj('<c:out value="${allinfo.queryObject}"/>','<c:out value="${allinfo.roleID}"/>')">
				</td>
				</c:when>
				<c:when test="${allinfo.queryObject=='org'}">
				<td class="titleTD"><i18n:message key="org.query.result.org"/></td>
				<td><c:out value="${allinfo.orgName}"/></td>
				<td><c:out value="${allinfo.orgNamePath}"/></td>
				<td>
					<input type="button" class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateObj('<c:out value="${allinfo.queryObject}"/>','<c:out value="${allinfo.orgID}"/>')">
				</td>
				</c:when>
				</c:choose>
			</tr>
			</c:forEach>
		</table>
		</div>
	</body>
</html>