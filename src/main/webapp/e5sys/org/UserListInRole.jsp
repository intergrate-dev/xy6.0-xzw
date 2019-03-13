<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title>Add Role</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="./js/xmlhttps.js"></script>
		<script type="text/javascript">
			function AppendUser(){
				var url="UserVaildIndex.jsp?RoleID=<c:out value="${usermap.treeinfo.treeNodeid}"/>";
				window.open(url,"_blank", "width=620,height=560");

			}

			function revokeRole(roleid,userid){
				var urlsrc="RoleMgrUserAction.do?invoke=revokeRole"
					+"&RoleID="+roleid
					+"&UserID="+userid;

				var localUrl="RoleMgrAction.do?invoke=roleFormList&RoleID="+roleid;
				invokeGetXmlHttpUpdate(urlsrc,localUrl);	
			}

			function updateValid(roleid,userid){
				var vaildUrl = "RoleValidAction.do?invoke=show&OpType=update&UserID="+userid+"&RoleID="+roleid;
				window.open(vaildUrl,"_blank", "width=400,height=400");
			}
			function refreshFrm(){
				document.location.href="RoleMgrAction.do?invoke=roleFormList&RoleID=<c:out value="${usermap.treeinfo.treeNodeid}"/>";
			}
		</script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><c:out value="${usermap.treeinfo.treeNodeName}"/></caption>
			<tr>
				<th><i18n:message key="org.user.name"/></th>
				<th><i18n:message key="org.user.orgpath"/></th>
				<th><i18n:message key="org.user.role.valid.time"/></th>
				<th><i18n:message key="org.role.status"/></th>
				<th> </th>
			</tr>
			<c:forEach items="${usermap.userlist}" var="userinfo">
			<tr>
				<td><c:out value="${userinfo.userName}"/></td>
				<td><c:out value="${userinfo.orgNamePath}"/></td>
				<td><c:out value="${userinfo.endTime}"/></td>
				<td>
					<c:choose>
						<c:when test="${userinfo.vaild==false}">
							<font color="red"><i18n:message key="org.role.status.invalid"/></font>
						</c:when>
						<c:when test="${userinfo.vaild==true}">
							<i18n:message key="org.role.status.valid"/>
						</c:when>
					</c:choose>
				</td>
				<td>
					<input type="button" class="button" value="<i18n:message key="org.user.role.valid.revoke"/>" onclick="revokeRole('<c:out value="${usermap.treeinfo.treeNodeid}"/>','<c:out value="${userinfo.userID}"/>')">
					<input type="button" class="button" value="<i18n:message key="org.user.role.valid"/>" onclick="updateValid('<c:out value="${usermap.treeinfo.treeNodeid}"/>','<c:out value="${userinfo.userID}"/>')">
				</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="5" class="alignCenter">
					<input type="button" class="button" value="<i18n:message key="org.role.grant.user"/>" onclick="AppendUser()">
				</td>
			</tr>
		</table>
		<div> 
	</body> 
</html>