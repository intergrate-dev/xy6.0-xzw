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
		<c:forEach var="one" items="${flow.manageFlows}" varStatus="status1">
		ele = document.getElementById('<c:out value="${one}"/>');
		if (ele != null) 
			ele.checked = true;
		</c:forEach>

		<c:forEach var="one" items="${flow.permFlows}" varStatus="status1">
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
	<Input Type="hidden" Name="Type"   Value="3">	
	<c:if test="${sessionScope.FlowInfos.docType}">
		<Input Type="hidden" Name="DocClass"   Value="DocType">	
		<TR>
			<TH class="bottomlinetd"><i18n:message key="perm.title"/></TD>
			<TH class="bottomlinetd"><i18n:message key="perm.manage"/></TD>
			<TH class="bottomlinetd"><i18n:message key="perm.perm"/></TD>
		</TR>
		<c:forEach var="type" items="${sessionScope.FlowInfos.types}">
		<TR>
			<TD class="bottomlinetd"><c:out value="${type.docTypeName}"/></TD>
			<TD class="bottomlinetd">
				<input type="checkbox" name='<c:out value="${type.docTypeID}_1"/>' id='<c:out value="${type.docTypeID}_1"/>'/>
			</TD>
			<TD class="bottomlinetd">
				<input type="checkbox" name='<c:out value="${type.docTypeID}_2"/>' id='<c:out value="${type.docTypeID}_2"/>'/>
			</TD>
		</TR>
		</c:forEach>
	</c:if>
	<c:if test="${not sessionScope.FlowInfos.docType}">
		<Input Type="hidden" Name="DocClass"   Value="Workflow">	
		<TR>
			<TH class="bottomlinetd"><i18n:message key="auth.doctype"/></TD>
			<TH class="bottomlinetd"><i18n:message key="auth.flowname"/></TD>
			<TH class="bottomlinetd"><i18n:message key="perm.manage"/></TD>
			<TH class="bottomlinetd"><i18n:message key="perm.perm"/></TD>
		</TR>
		<c:forEach var="type" items="${sessionScope.FlowInfos.types}" varStatus="status1">
			<TR><TD class="bottomlinetd"><c:out value="${type.docTypeName}"/></TD><TD></TD><TD></TD><TD></TD></TR>
			<c:forEach var="flow" items="${sessionScope.FlowInfos.flows[status1.index]}">
				<TR>			
				<TD class="bottomlinetd"></TD>
				<TD class="bottomlinetd"><c:out value="${flow.name}"/></TD>
				<TD class="bottomlinetd">
					<input type="checkbox" name='<c:out value="${flow.ID}_1"/>' id='<c:out value="${flow.ID}_1"/>'/>
				</TD>
				<TD class="bottomlinetd">
					<input type="checkbox" name='<c:out value="${flow.ID}_2"/>' id='<c:out value="${flow.ID}_2"/>'/>
				</TD>
				</TR>
			</c:forEach>
		</c:forEach>
	</c:if>
</Table>
<BR>
<DIV>
<input type="submit" value="<i18n:message key="button.submit"/>" class="button" AccessKey="S">
</DIV>
</FORM>
<BR>
</BODY>
</HTML>