<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title>Add Role</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-SysAdminForm.css"/>
		<script type="text/javascript" src="../../e5script/Function.js"></script>
	</head>
	<body style="text-align:center;">
		<div class="mainBodyWrap" style="width:700px;">
		<form name="userform" action="UserMgrAction.do" method="post" onsubmit="return submitFrm();">
		<table cellpadding="0" cellspacing="0" class="table">
			<caption id="nameDiv"><i18n:message key="org.admin.title"/></caption>
			<tr>
				<th><i18n:message key="org.user.form.name"/></th>
				<td><input type="text" name="UserName" class="textField" value="<c:out value="${sysuser.userName}"/>"/></td></tr>
			<tr>
				<th><i18n:message key="org.user.form.code"/></th>
				<td><input type="text" name="UserCode" class="textField" value="<c:out value="${sysuser.userCode}"/>" readonly></td></tr>
			<tr>
				<th><i18n:message key="org.user.form.origin.password"/></th>
				<td><input type="password" class="textField" name="Password" value=""></td></tr>
			<tr>
				<th><i18n:message key="org.user.form.password"/></th>
				<td><input type="password" name="userPassword" class="textField" value=""></td></tr>
			<tr>
				<th><i18n:message key="org.user.form.verify.password"/></th>
				<td><input type="password" class="textField" name="verifyPassword" value=""></td>
			</tr>
			<tr>
				<td colspan="2" class="alignCenter">
					<input class="button" type="submit" value="<i18n:message key="org.submit"/>"/>
				</td>
			</tr>
		</table>
		<input type="hidden" name="invoke" value="UpdateSysAdmin">
		<input type="hidden" name="treeid" value="<c:out value="${sysuser.treeid}"/>">
		<input type="hidden" name="UserID" value="<c:out value="${sysuser.userID}"/>">
		</form>
		</div>
			<script type="text/javascript">
				var verPs="<c:out value="${sysuser.verifyPassword}"/>";
				if(verPs=="wrong"){
					alert("<i18n:message key="org.user.form.verify.password.alert"/>");
				}
				function submitFrm(){
					if(document.userform.userPassword.value!=document.userform.verifyPassword.value){
						alert("<i18n:message key="org.admin.password.alert"/>");
						return false;
					}
					if(document.userform.UserName.value==""){
						alert("<i18n:message key="org.admin.username.alert"/>");
						return false;
					}
					if(getLength(userform.UserName.value)>20){
						alert("<i18n:message key="org.admin.form.name.input.length.alert"/>");
						return false;
					}
					if(getLength(userform.userPassword.value)>254){
						alert("<i18n:message key="org.admin.form.password.input.length.alert"/>");
						return false;
					}
					return true;
				}
			</script>
	</body>
</html>
