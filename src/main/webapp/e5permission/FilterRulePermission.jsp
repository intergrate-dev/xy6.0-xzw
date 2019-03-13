<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		input{border: 0;}
		th{cursor:hand;background-color:#CCCCCC;}
		#nameDiv{
			height: 32px;
			padding-top:5px;
		}
		#hint{color:gray;}
		.bottomlinetd{white-space:nowrap;}
	</style>
</HEAD>
<BODY>
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="pForm" Target="iframe" Action="./filterRuleSubmit.do" Method="Post">
	<Input Type="hidden" Name="RoleID" 
		Value="<c:out value="${sessionScope.permissionRoleID}"/>">

	<div id="nameDiv"><i18n:message key="filter.title"/></div>
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
		<c:forEach var="docType" items="${filterList}">
			<TR>
				<TH class="bottomlinetd">
					<c:out value="${docType.name}"/></label>
				</TH>
				<c:forEach var="filter" items="${docType.list}" varStatus="status">
					<c:if test="${status.index > 0 and (status.index % 5 == 0)}">
						</tr>
						<tr>
							<th class="bottomlinetd">&nbsp;</th>
					</c:if>
					<input type="hidden" name="FilterID" value="<c:out value="${filter.id}"/>"/>
					<TD class="bottomlinetd">
						<input type="checkbox" id="Filter_<c:out value="${filter.id}"/>"
							name="Filter_<c:out value="${filter.id}"/>"
							<c:if test="${filter.enabled}">checked</c:if>
						/>
						<label for="Filter_<c:out value="${filter.id}"/>"><c:out value="${filter.name}"/></label>
					</TD>
				</c:forEach>
			</TR>
		</c:forEach>
	</Table>
	<BR>
	<div id="nameDiv"><i18n:message key="rule.title"/></div>
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
		<c:forEach var="docType" items="${ruleList}">
			<TR>
				<TH class="bottomlinetd">
					<c:out value="${docType.name}"/></label>
				</TH>
				<c:forEach var="rule" items="${docType.list}" varStatus="status">
					<c:if test="${status.index > 0 and (status.index % 5 == 0)}">
						</tr>
						<tr>
							<th class="bottomlinetd">&nbsp;</th>
					</c:if>
					<input type="hidden" name="RuleID" value="<c:out value="${rule.id}"/>"/>
					<TD class="bottomlinetd">
						<input type="checkbox" id="Rule_<c:out value="${rule.id}"/>"
							name="Rule_<c:out value="${rule.id}"/>"
							<c:if test="${rule.enabled}">checked</c:if>
						/>
						<label for="Rule_<c:out value="${rule.id}"/>"><c:out value="${rule.name}"/></label>
					</TD>
				</c:forEach>
			</TR>
		</c:forEach>
	</Table>
	<BR/>
	<DIV><input Type="submit" AccessKey="S" class="button" value="<i18n:message key="button.submit"/>"/></DIV>
</Form>
</BODY>
</HTML>
