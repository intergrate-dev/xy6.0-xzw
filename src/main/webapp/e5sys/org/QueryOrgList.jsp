<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title></title>
		<script type="text/javascript" src="./js/query.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-Query.css"/>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<tr>
				<th class="titleTD"><i18n:message key="org.sort.org.id"/></th>
				<th><i18n:message key="org.sort.org.name"/></th>
				<th><i18n:message key="org.sort.org.code"/></th>
				<th> </th>
			</tr>
			<c:forEach items="${orgmap.orglist}" var="orginfo">
			<tr>
				<td class="titleTD"><c:out value="${orginfo.orgID}"/></td>
				<td><c:out value="${orginfo.orgNamePath}"/></td>
				<td><c:out value="${orginfo.orgCode}"/></td>
				<td>
					<input type="button" class="button" value="<i18n:message key="org.query.result.update"/>" onclick="updateOrg('<c:out value="${orginfo.orgID}"/>')">
				</td>
			</tr>
			</c:forEach>
		</table>
		</div>
	</body> 
</html>