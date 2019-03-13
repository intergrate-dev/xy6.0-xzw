<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		input{border: 0;}
	</style>
	<script language="JavaScript">
	function init()
	{
		var ele = null;
		<c:forEach var="one" items="${module.haveModules}" varStatus="status1">
		ele = document.getElementById('<c:out value="${one}"/>');
		if (ele != null) 
			ele.checked = true;
		</c:forEach>
	}
	</script>
</HEAD>
<BODY onload="init()">
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="otherForm"  Target="iframe" Action="./submitPart.do" Method="Post">
<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
	<Input Type="hidden" Name="RoleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
	<Input Type="hidden" Name="Type"   Value="5">
	<c:forEach var="app" items="${sessionScope.adminModule.apps}" varStatus="status1">
		<TR><TD class="bottomlinetd"><c:out value="${app.name}"/></TD><TD class="bottomlinetd"></TD></TR>
		<c:forEach var="web" items="${sessionScope.adminModule.appWebs[status1.index]}" varStatus="status2">
		<TR><TD class="bottomlinetd"></TD><TD class="bottomlinetd">
		<input type="checkbox" name='<c:out value="${app.appID}_${web.webName}"/>' id='<c:out value="${app.appID}_${web.webName}"/>'/><c:out value="${web.webName}"/>
		</TD></TR>
		</c:forEach>
	</c:forEach>
</Table>
<BR>
<DIV>
<input type="submit" value="<i18n:message key="button.submit"/>" class="button">
</DIV>
</FORM>
<BR>
</BODY>
</HTML>