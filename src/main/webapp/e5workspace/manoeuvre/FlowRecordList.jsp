<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<HTML>
<HEAD><TITLE><i18n:message key="workspace.flowRecordList.title"/></TITLE>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="StyleSheet" href="../../e5style/work.css"/>
</HEAD>
<BODY style="overflow:auto; margin:10px;" SCROLL="auto">
<table width="100%" border="0" cellspacing="0" cellpadding="5" class="work">
	<tr class="bluetd">
		<td nowrap><i18n:message key="workspace.flowRecordList.operation"/></td>
		<td nowrap><i18n:message key="workspace.flowRecordList.operator"/></td>
		<td nowrap><i18n:message key="workspace.flowRecordList.endTime"/></td>
		<td nowrap><i18n:message key="workspace.flowRecordList.demo"/></td>
	</tr>
	<%
		com.founder.e5.doc.FlowRecord[] list = (com.founder.e5.doc.FlowRecord[])request.getAttribute("list");
		if (list != null) {
		for (int i = 0; i < list.length; i++) {%>
			<tr>
				<td class="bottomlinetd" nowrap><%=list[i].getOperation()%></td>
				<td class="bottomlinetd" nowrap><%=list[i].getOperator()%></td>
				<td class="bottomlinetd" nowrap><%=(list[i].getStartTime()==null||list[i].getStartTime().toString().length()<2) ? "" : list[i].getStartTime().toString().substring(0, list[i].getStartTime().toString().length()-2)%></td>
				<td class="bottomlinetd"><%=list[i].getDetail() != null ? list[i].getDetail() : ""%></td>
			</tr>
		<%}
		}
	%>
</Table>
</BODY>
</HTML>
