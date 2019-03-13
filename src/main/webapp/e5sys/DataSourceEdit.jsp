<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="dataSource.title"/></title>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript">
	function checkInput(){
		if(form1.name.value==''){
			alert("<i18n:message key="dataSource.edit.name.required"/>");
			form1.name.focus();
			return false;
		}
		if(form1.dbType.value==''){
			alert("<i18n:message key="dataSource.edit.dbType.required"/>");
			form1.dbType.focus();
			return false;
		}
		/*
		if(form1.dbServer.value==''){
			alert("<i18n:message key="dataSource.edit.dbServer.required"/>");
			form1.dbServer.focus();
			return false;
		}
		if(form1.db.value==''){
			alert("<i18n:message key="dataSource.edit.db.required"/>");
			form1.db.focus();
			return false;
		}
		*/
		if (form1.dbServer.value==''){
			form1.dbServer.value=='DBServer';
		}
		if (form1.db.value==''){
			form1.db.value=='DBName';
		}
		if(form1.dataSource.value==''){
			alert("<i18n:message key="dataSource.edit.dataSource.required"/>");
			form1.dataSource.focus();
			return false;
		}
		closeDialog();
		return true;
	}
	function testDataSource(){
		var theURL = "./TestDataSource.jsp?jndi="+form1.dataSource.value+"&dbType="+form1.dbType.value;
		var spantext = document.getElementById("test_ret");
		spantext.innerHTML = "<i18n:message key="dataSource.test.wait"/>";
		spantext.style.color="black";
		getDataProvider(theURL, doCheckResult, true, true);
	}
	function doCheckResult(type, xmlDoc, evt){
		var spantext = document.getElementById("test_ret");
		if ("true" == getText(xmlDoc.documentElement.childNodes[0])) {
			spantext.innerHTML   = "<i18n:message key="dataSource.test.succ"/>";
			spantext.style.color = "green";
		} else {
			spantext.innerHTML = "<i18n:message key="dataSource.test.fail"/>";
			spantext.style.color = "red";
		}
	}
	function getText(oNode){
		return (oNode.text) ? oNode.text : oNode.textContent;
	}
	function closeDialog(){
		window.parent.dialogClose();
	}
</script>
<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<style>
	.input{
		width:300px;
	}
	.labelTD {
		width:90px;
	}
	.pw{
		background:#FFF;
		border: 1px solid #A7A6AA;
		height: 13px;
		margin: 0 5px;
		padding: 3px;
	}
</style>
</head>
<body>
	<form name="form1" method="post" action="DataSourceEdit.do?action=save" onsubmit="return checkInput();">
		<input type="hidden" name="id" value="<c:out value="${dsID}"/>">
		<table cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="dataSource.title"/></caption>
			<tr>
				<td class="labelTD"><span class="field-required">*</span><i18n:message key="dataSource.edit.name"/>:</td>
				<td><input class="input" name="name" type="text" id="name" value="<c:out value="${item.name}"/>"></td>
			</tr>
			<tr>
				<td><span class="field-required">*</span><i18n:message key="dataSource.edit.dbType"/>: </td>
				<td><select class="input" name="dbType" id="dbType">
				<c:forEach var="dbt" items="${datTypes}">
				<option value="<c:out value="${dbt}"/>" <c:if test="${dbt==item.dbType}">selected</c:if>><c:out value="${dbt}"/></option>
				</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td><span class="field-required">*</span><i18n:message key="dataSource.edit.dataSource"/>:</td>
				<td><input class="input" name="dataSource" type="text" id="dataSource" value="<c:out value="${item.dataSource}"/>"></td>
			</tr>
			<tr style="display:none;">
				<td><span class="field-required">*</span><i18n:message key="dataSource.edit.dbServer"/>:</td>
				<td><input class="input" name="dbServer" type="text" id="dbServer" value="<c:out value="${item.dbServer}"/>"></td>
			</tr>
			<tr style="display:none;">
				<td><span class="field-required">*</span><i18n:message key="dataSource.edit.db"/>:</td>
				<td><input class="input" name="db" type="text" id="db" value="<c:out value="${item.db}"/>"></td>
			</tr>
			<tr style="display:none;">
				<td><i18n:message key="dataSource.edit.user"/></td>
				<td><input class="input" name="user" type="text" id="user" value="<c:out value="${item.user}"/>"></td>
			</tr>
			<tr style="display:none;">
				<td><i18n:message key="dataSource.edit.password"/></td>
				<td><input class="input pw" name="password" type="password" id="password" value="<c:out value="${item.password}"/>"></td>
			</tr>
		</table>
		<div class="alignCenter mt">
			<input class="button" type="submit" value="<i18n:message key="dataSource.button.submit"/>">
			<input class="button" type="button" value="<i18n:message key="dataSource.button.cancel"/>" onclick="closeDialog()">
			<input class="button" type="button" value="<i18n:message key="dataSource.test.title"/>" onclick="testDataSource()">
		</div>
	</form>
<!--
	<div class="mt">
		<i18n:message key="dataSource.note.password"/>
	</div>
-->
	<div id="test_ret" style="font-size:15;text-align:left"></div>
</body>
</html>
<%@include file="../e5include/Error.jsp"%>
