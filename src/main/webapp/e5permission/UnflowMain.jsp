<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style type="text/css">
		input{border: 0;}
		#hint{color:gray;}
		th{cursor:hand;background-color:#CCCCCC;}
	</style>
	<Script type="text/javascript">
		function nodeClick()
		{
			var mycheck = document.getElementById("Unflow_0");
			if (mycheck == null) return;

			var setcheck = !mycheck.checked;
			var i = 0;
			while (mycheck != null)
			{
				mycheck.checked = setcheck;
				i++;
				mycheck = document.getElementById("Unflow_" + i);
			}
		}
	</Script>
</HEAD>
<BODY>
	<%@include file="../e5include/Error.jsp"%>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<BR>
	<Form Name="permissionForm"  Target="iframe" Action="./submitUnflow.do" Method="Post">
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyborder">
		<Input Type="hidden" Name="RoleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
		<Input Type="hidden" Name="DocTypeID" Value="<c:out value="${docTypeID}"/>">
		<Input Type="hidden" Name="UnflowCount" Value="<c:out value="${unflowCount}"/>">
		<TR>
			<TH onclick="nodeClick()"><i18n:message key="flow.unflow"/></TH>
			<c:forEach var="unflow" items="${unflows}" varStatus="status">
				<TD>
					<input type="checkbox" name="Unflow_<c:out value="${status.index}"/>"
						<c:if test="${unflow.procID == 1}">checked</c:if>
					/>
					<c:out value="${unflow.procName}"/>
				</TD>
			</c:forEach>
		</TR>
	</Table>
	<BR>
	<DIV><input Type="submit" AccessKey="S" class="button" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>
	<BR>
	<DIV id="hint"><i18n:message key="flow.hint"/></DIV>
</BODY>
</HTML>
