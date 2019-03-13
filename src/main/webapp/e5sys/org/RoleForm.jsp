<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head>
	<title><i18n:message key="org.query.result.role"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/Function.js"></script>
	<script type="text/javascript">
		var operate="<c:out value="${roleinfo.operate}"/>";
		var isfresh="<c:out value="${roleinfo.fresh}"/>";
		if(operate=="add" && isfresh=="true"){
			document.write("<i18n:message key="org.role.form.add.info"/><c:out value="${roleinfo.roleName}"/>");
		}
	</script>
	<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
</head>
<body>
	<div class="mainBodyWrap" style="margin:0">
		<form name="loginform" action="RoleMgrAction.do" method="post"  onsubmit="return submitFrm();">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption>
				<c:if test="${roleinfo.operate=='add'}"><i18n:message key="org.menu.addrole"/></c:if>
				<c:if test="${roleinfo.operate=='update'}"><i18n:message key="org.menu.updaterole"/></c:if>
			</caption>
			<tr>
				<td class="alignCenter">
					<span><i18n:message key="role.name"/></span>:&nbsp;&nbsp;<input type="text" name="RoleName" value="<c:out value="${roleinfo.roleName}"/>"/>
					<input type="submit" class="button" value="<i18n:message key="org.submit"/>"/>
					<c:if test="${roleinfo.treeid=='-1'}">
					<input type="button" class="button" value="<i18n:message key="org.cancel"/>" onClick="window.close();"/> 
					</c:if>
					<input type="hidden" name="OrgID" value="<c:out value="${roleinfo.orgID}"/>"> 
					<input type="hidden" name="RoleID" value="<c:out value="${roleinfo.roleID}"/>"> 
					<input type="hidden" name="invoke" value="addRoleUnderOrg"> 
					<!--here user property1 to set treeid,only by wangchaoyang user-->        
					<input type="hidden" name="treeid" value="<c:out value="${roleinfo.treeid}"/>">
					<input type="hidden" name="AddType" value="<c:out value="${roleinfo.addType}"/>">
				</td>
			</tr>
		</table>
		</form>
	</div>
</body> 
</html>
<script type="text/javascript">
function submitFrm(){
	var val = loginform.RoleName.value.trim();
	loginform.RoleName.value = val;
	
	if(loginform.RoleName.value=="") {
		alert("<i18n:message key="org.role.form.alert"/>");
		return false;
	}
	if(getLength(loginform.RoleName.value)>254) {
		alert("<i18n:message key="org.role.form.name.input.length.alert"/>");
		return false;
	}
	//查重
	var url = "../../xy/user/CheckOrgRole.do"
		+ "?parentID=" + loginform.OrgID.value
		+ "&name=" + loginform.RoleName.value
		+ "&type=1";
	if (operate=="update")
		url += "&id=" + loginform.RoleID.value
	
	var result;
	$.ajax({type: "POST", url: url, async:false, 
		success: function(data) {
			result = data;
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown + ':' + textStatus);  // 错误处理
		}
	});
	if (result == "1") {
		alert("已存在同名角色");
		return false;
	} else
		return true;
}

if (operate=="add") {
	document.loginform.invoke.value="addRoleUnderOrg";
	if (isfresh == "true") {
		var addType="<c:out value="${roleinfo.addType}"/>"
		if(addType=="underorg") {
			parent.leftFrame.addRoleNode("<c:out value="${roleinfo.roleName}"/>","<c:out value="${roleinfo.roleID}"/>","<c:out value="${roleinfo.treeid}"/>","<c:out value="${roleinfo.orgID}"/>");
		} else if(addType=="underrole") {
			parent.leftFrame.addRoleNodeByRole("<c:out value="${roleinfo.roleName}"/>","<c:out value="${roleinfo.roleID}"/>","<c:out value="${roleinfo.treeid}"/>","<c:out value="${roleinfo.orgID}"/>");
		}
	}
} else if (operate=="update") {
	document.loginform.invoke.value="updateRole";
}
</script>
