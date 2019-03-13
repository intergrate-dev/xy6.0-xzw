<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title><i18n:message key="org.user.form.list.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-UserForm.css"/>
		<style type="text/css">.hidden{display:none;}</style>
		
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<script type="text/javascript">
			function submitFrm(){
				if(userform.userPassword.value!=userform.verifyPassword.value){
					alert("<i18n:message key="org.user.password.alert"/>");
					return false;
				}
				if(userform.userName.value==""){
					alert("<i18n:message key="org.user.username.alert"/>");
					return false;
				}
				if(userform.userCode.value==""){
					alert("<i18n:message key="org.user.usercode.alert"/>");
					return false;
				}

				if(getLength(userform.userName.value)>20){
					alert("<i18n:message key="org.user.form.name.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.userCode.value)>254){
					alert("<i18n:message key="org.user.form.code.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.userPassword.value)>254){
					alert("<i18n:message key="org.user.form.password.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.emailAddress.value)>30){
					alert("<i18n:message key="org.user.form.emailAddress.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.address.value)>254){
					alert("<i18n:message key="org.user.form.address.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.postCode.value)>20){
					alert("<i18n:message key="org.user.form.postCode.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.callNumber.value)>20){
					alert("<i18n:message key="org.user.form.callNumber.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.telHomeNumber.value)>20){
					alert("<i18n:message key="org.user.form.telHomeNumber.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.telOffNumber.value)>20){
					alert("<i18n:message key="org.user.form.telOffNumber.input.length.alert"/>");
					return false;
				}
				if(getLength(userform.handSetNumber.value)>20){
					alert("<i18n:message key="org.user.form.handSetNumber.input.length.alert"/>");
					return false;
				}
				return true;
			}
		</script>
	</head> 
	<body>
		<div class="mainBodyWrap" style="margin:0">
		<form name="userform" action="UserMgrFormAction.do"  method="post" onsubmit="return submitFrm();">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><i18n:message key="org.user.form.orgname"/><c:out value="${usermap.userinfo.orgName}"/></caption>
			<tr>
				<td class="titleTD"><span class="field-required">*</span><i18n:message key="org.user.form.name"/></td>
				<td><input class="textField" type="text" name="userName" value="<c:out value="${usermap.userinfo.userName}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD" ><span class="field-required">*</span><i18n:message key="org.user.form.code"/></td>
				<td><input class="textField" type="text" name="userCode" value="<c:out value="${usermap.userinfo.userCode}"/>" <c:choose><c:when test="${usermap.userinfo.userID=='0'}"></c:when><c:otherwise>readonly</c:otherwise></c:choose>></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.password"/></td>
				<td><input type=password class="textField" name="userPassword" value="<c:out value="${usermap.userinfo.userPassword}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.verify.password"/></td>
				<td><input type=password class="textField" name="verifyPassword" value="<c:out value="${usermap.userinfo.userPassword}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.email"/></td>
				<td><input type="text" class="textField" size="20" name=emailAddress value="<c:out value="${usermap.userinfo.emailAddress}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.address"/></td>
				<td><input type="text" class="textField" size="20" name=address value="<c:out value="${usermap.userinfo.address}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.postcode"/></td>
				<td><input type="text" class="textField" size="20" name=postCode value="<c:out value="${usermap.userinfo.postCode}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.bpnumber"/></td>
				<td><input type="text" class="textField" size="20" name=callNumber value="<c:out value="${usermap.userinfo.callNumber}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.homenumber"/></td>
				<td><input type="text" class="textField" size="20"  name=telHomeNumber value="<c:out value="${usermap.userinfo.telHomeNumber}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.officenumber"/></td>
				<td><input type="text" class="textField" size="20" name=telOffNumber value="<c:out value="${usermap.userinfo.telOffNumber}"/>"></td>
			</tr>
			<tr>
				<td class="titleTD"><i18n:message key="org.user.form.mobilenumber"/></td>
				<td><input type="text"  class="textField" size="20" name=handSetNumber value="<c:out value="${usermap.userinfo.handSetNumber}"/>"></td>
			</tr>

			<!-- 扩展属性 -->
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property1"/></td>
				<td><input type="text"  class="textField" size="20" name="property1" value="<c:out value="${usermap.userinfo.property1}"/>"></td>
			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property2"/></td>
				<td><input type="text"  class="textField" size="20" name="property2" value="<c:out value="${usermap.userinfo.property2}"/>"></td>
			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property3"/></td>
				<td><input type="text"  class="textField" size="20" name="property3" value="<c:out value="${usermap.userinfo.property3}"/>"></td>
			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property4"/></td>
				<td><input type="text"  class="textField" size="20" name="property4" value="<c:out value="${usermap.userinfo.property4}"/>"></td>
			</tr class="hidden">
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property5"/></td>
				<td><input type="text"  class="textField" size="20" name="property5" value="<c:out value="${usermap.userinfo.property5}"/>"></td>
			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property6"/></td>
				<td><input type="text"  class="textField" size="20" name="property6" value="<c:out value="${usermap.userinfo.property6}"/>"></td>
			</tr>
			<tr class="hidden">
				<td class="titleTD"><i18n:message key="org.user.form.property7"/></td>
				<td><input type="text"  class="textField" size="20" name="property7" value="<c:out value="${usermap.userinfo.property7}"/>"></td>
			</tr>
			<!-- end.扩展属性 -->
			
			<tr>
				<td colspan="2" class="alignCenter">
					<input type="submit" class="button" value="<i18n:message key="org.submit"/>" />  
					<c:if test="${usermap.userinfo.treeid=='-1'}"><input type="button" class="button" value="<i18n:message key="org.cancel"/>" onClick="window.close();"/></c:if>
				</td>
			</tr>
		</table>
		<c:if test="${usermap.roleidstring!=''}">
		<table cellPadding="0" cellSpacing="0" class="table mt">
			<tr>
				<th width="14%"><i18n:message key="role.name"/></th>
				<th width="19%"><i18n:message key="org.user.orgpath"/></th>
				<th><i18n:message key="org.user.role.valid.time"/></th>
				<th><i18n:message key="org.role.status"/></th>
			</tr>
			<c:forEach items="${usermap.rolelist}" var="roleinfo">
			<tr>
				<td class="alignCenter"><c:out value="${roleinfo.roleName}"/></td>
				<td><c:out value="${roleinfo.orgNamePath}"/></td>
				<td><c:out value="${roleinfo.endTime}"/></td>
				<td><i18n:message key="org.role.status.valid"/></td>
			</tr>
			</c:forEach>
		</table>
		</c:if>
		<c:if test="${usermap.folderstring!=''}"> 
		<table cellPadding="0" cellSpacing="0" class="table mt">
			<tr class="bluetd">
				<th width="14%"><i18n:message key="org.user.default.doctype"/></th>
				<th width="19%"><i18n:message key="org.user.default.doclib"/></th>
				<th><i18n:message key="org.user.default.folder"/></th>
			</tr>
			<c:forEach items="${usermap.folderlst}" var="folderinfo">
			<c:if test="${folderinfo.folderNamePath!=''}">
			<tr>
				<td class="alignCenter"><c:out value="${folderinfo.docTypeName}"/></td>
				<td><c:out value="${folderinfo.docLibName}"/></td>
				<td><c:out value="${folderinfo.folderNamePath}"/></td>
			</tr>
			</c:if>
			</c:forEach>
		</table>
		</c:if>
		<input type="hidden" name="roleString" value="<c:out value="${usermap.roleidstring}"/>">
		<input type="hidden" name="folderString" value="<c:out value="${usermap.folderstring}"/>">
		<input type="hidden" name="refUserID" value="<c:out value="${usermap.userinfo.refUserID}"/>">
		<input type="hidden" name="roleID" value="<c:out value="${usermap.userinfo.roleID}"/>">
		<input type="hidden" name="orgID" value="<c:out value="${usermap.userinfo.orgID}"/>">
		<input type="hidden" name="treeid" value="<c:out value="${usermap.userinfo.treeid}"/>">
		<input type="hidden" name="userID" value="<c:out value="${usermap.userinfo.userID}"/>">
		<input type="hidden" name="addNodeMode" value="<c:out value="${usermap.userinfo.addNodeMode}"/>">
		</form>
		</div>
	</body> 
</html>