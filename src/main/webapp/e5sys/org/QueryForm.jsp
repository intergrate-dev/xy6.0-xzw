<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
	<head>
		<title>Add Role</title>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-Query.css"/>
		<script type="text/javascript">
			function submitFrm(){
				var queryName=mainForm.queryName.value
				if((queryName==null)||(queryName=="")){
					alert("<i18n:message key="org.query.form.alert"/>");
					return false;
				}else{
					if(mainForm.queryType.value=="org"){
						mainForm.invoke.value="queryOrg";
					}else if(mainForm.queryType.value=="user"){
						mainForm.invoke.value="queryUser";
					}else if(mainForm.queryType.value=="role"){
						mainForm.invoke.value="queryRole";
					}else{
						mainForm.invoke.value="queryAll";
					}
					return true;
				}
			}
		</script>
	</head>
	<body oncontextmenu="if (!event.ctrlKey){return false;}">
		<div class="mainBodyWrap">
		<form name="mainForm" action="QueryOrgAction.do" method="post"  onsubmit="return submitFrm();">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption><i18n:message key="org.query.form.title"/></caption>
			<tr>
				<td class="titleObj"><i18n:message key="org.query.form.queryType"/></td>
				<td>
					<select class="textField" name="queryType">
						<option value="org" <c:if test="${queryinfo.object=='org'}"> selected </c:if>><i18n:message key="org.query.form.queryType.org"/></option>
						<option value="role" <c:if test="${queryinfo.object=='role'}"> selected </c:if>><i18n:message key="org.query.form.queryType.role"/></option>
						<option value="user"<c:if test="${queryinfo.object=='user'}"> selected </c:if>><i18n:message key="org.query.form.queryType.user"/></option>
						<option value="all"<c:if test="${queryinfo.object=='all'}"> selected </c:if>><i18n:message key="org.query.form.queryType.all"/></option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="titleObj"><i18n:message key="org.query.form.queryModal"/></td>
				<td>
					<select class="textField" name="queryModal">
						<option value="precision" <c:if test="${queryinfo.queryMode=='precision'}"> selected</c:if>><i18n:message key="org.query.form.queryModa.precision"/></option>
						<option value="fuzzy" <c:if test="${queryinfo.queryMode=='fuzzy'}"> selected</c:if>><i18n:message key="org.query.form.queryModa.fuzzy"/></option>
					</select>
				</td>
			</tr>
			<tr>
				<td class="titleObj"><i18n:message key="org.query.form.queryName"/></td>
				<td><input type="text" class="textField"  name="queryName" value="<c:out value="${queryinfo.name}"/>"></td>
			</tr>
			<tr>
				<td colspan="2" class="alignCenter">
					<input type="hidden" name="invoke" value="">
					<input type="submit" class="button" name="doSubmit" value="<i18n:message key="org.query.form.submit"/>">
				</td>
			</tr>
		</table>
		</form>
		</div>
	</body>
</html>