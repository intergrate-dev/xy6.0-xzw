<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title><i18n:message key="catType.edit.title"/></title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script language="javascript">
			function checkInput(){
				var errors = "";
				var f1 = document.forms[0];
				if(f1.name.value == ''){
					alert('<i18n:message key="catType.edit.typeName.required"/>');
					f1.name.focus();
					return false;
				}
				if(getLength(f1.name.value)>255){
					 errors = errors +  "<i18n:message key="error.catType.name.maxlength"/>\n";
				}
				if(errors!=''){
					alert("<i18n:message key="errors.header"/>\n"+errors);
					return false;
				}
				return true;
			}
  		</script>
  		<style type="text/css">
			.area{
				background: #F3F3F3;
				border: 1px solid #D1D1D1;
				padding: 5px;
			}
			.layout-table{
				width:100%;
				margin:20px 0;
			}
			.layout-table td{
				vertical-align: top;
			}
  		</style>
	</head>
<body onload="document.form1.name.focus();">
	<form name="form1" method="post" action="CatTypeEdit.do?action=save" onsubmit="return checkInput()">
		<input type="hidden" name="id" value="<c:out value="${item.catType}"/>">
		<div class="area">
			<i18n:message key="catType.edit.typeName"/>:&nbsp;<input name="name" type="text" value="<c:out value="${item.name}"/>" size="40">
		</div>
		<div style="display:none">
			<i18n:message key="catType.edit.tableName"/><input name="tableName" type="text" value="<c:out value="${item.tableName}"/>">
		</div>
		<table cellpadding="0" cellspacing="0" class="layout-table">
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" class="table">
						<caption><i18n:message key="catType.edit.propertySet"/></caption>
						<tr>
							<td>
								<input id="propertySet1" name="propertySet1" type="checkbox"  value="1" <c:if test="${requestScope.propertySet1=='1'}">checked</c:if>>
								<label for="propertySet1"><i18n:message key="catType.edit.ps1"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet2" name="propertySet2" type="checkbox" value="1" <c:if test="${requestScope.propertySet2=='1'}">checked</c:if>>
								<label for="propertySet2"><i18n:message key="catType.edit.ps2"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet3" name="propertySet3" type="checkbox"  value="1" <c:if test="${requestScope.propertySet3=='1'}">checked</c:if>>
								<label for="propertySet3"><i18n:message key="catType.edit.ps3"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet4" name="propertySet4" type="checkbox"  value="1" <c:if test="${requestScope.propertySet4=='1'}">checked</c:if>>
								<label for="propertySet4"><i18n:message key="catType.edit.ps4"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet5" name="propertySet5" type="checkbox"  value="1" <c:if test="${requestScope.propertySet5=='1'}">checked</c:if>>
								<label for="propertySet5"><i18n:message key="catType.edit.ps5"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet6" name="propertySet6" type="checkbox"  value="1" <c:if test="${requestScope.propertySet6=='1'}">checked</c:if>>
								<label for="propertySet6"><i18n:message key="catType.edit.ps6"/></label>
							</td>
						</tr>
						<tr>
							<td>
								<input id="propertySet7" name="propertySet7" type="checkbox"  value="1" <c:if test="${requestScope.propertySet7=='1'}">checked</c:if>>
								<label for="propertySet7"><i18n:message key="catType.edit.ps7"/></label>
							</td>
						</tr>
					</table>
				</td>
				<td width="20"></td>
				<td>
					<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="catType.edit.synchSet"/></caption>
						<tr>
							<td>
								<input id="synchSet1" name="synchSet1" type="checkbox"  value="1" <c:if test="${requestScope.synchSet1=='1'}">checked</c:if>>
								<label for="synchSet1"><i18n:message key="catType.edit.ss1"/></label>
							</td>
						</tr>
						<tr>
							<td>
						  		<input id="synchSet2" name="synchSet2" type="checkbox"  value="1" <c:if test="${requestScope.synchSet2=='1'}">checked</c:if>>
								<label for="synchSet2"><i18n:message key="catType.edit.ss2"/></label>
							</td>
						</tr>
						<tr>
							<td>
						  		<input id="synchSet3" name="synchSet3" type="checkbox"  value="1" <c:if test="${requestScope.synchSet3=='1'}">checked</c:if>>
								<label for="synchSet3"><i18n:message key="catType.edit.ss3"/></label>
							</td>
						</tr>
						<tr>
							<td>
						  		<input id="synchSet4" name="synchSet4" type="checkbox"  value="1" <c:if test="${requestScope.synchSet4=='1'}">checked</c:if>>
								<label for="synchSet4"><i18n:message key="catType.edit.ss4"/></label>
							</td>
						</tr>
						<tr>
							<td>
						  		<input id="synchSet5" name="synchSet5" type="checkbox"  value="1" <c:if test="${requestScope.synchSet5=='1'}">checked</c:if>>
								<label for="synchSet5"><i18n:message key="catType.edit.ss5"/></label>
							</td>
						</tr>
					</table>
				</td>
			<tr>
		</table>
		<div class="alignCenter area">
			<input type="submit" name="Submit" value="<i18n:message key="cat.button.submit"/>" class="button">&nbsp;&nbsp;<input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button">
		</div>
	</form>
</body>
</html>
