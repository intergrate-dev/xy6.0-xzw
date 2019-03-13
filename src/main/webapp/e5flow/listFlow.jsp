<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>

<html>
<HEAD>
<TITLE>Flow List</TITLE>
<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript">
var docTypeID = "<c:out value="${docTypeID}"/>";
//增加流程
function addFlow()
{
	this.location.href = 'Flow.do?docTypeID=' + docTypeID;
}
//修改流程
function editFlow(id)
{
	//window.location.href = "Flow.do?docTypeID=" + docTypeID + "&flowID=" + id ;
	window.location.href = "FlowVisualDefine.jsp?flowid=" + id + "&docTypeID=" + docTypeID;
}
//删除流程
function deleteFlow(id)
{
	if (window.confirm('<i18n:message key="operation.listflow.deleteconfirm"/>'))
		window.location.href = "FlowSubmit.do?del=1&docTypeID=" + docTypeID + "&flowID=" + id ;
}
//设为缺省流程
function setDefault(id)
{
	if (window.confirm('<i18n:message key="operation.flow.confirmset"/>'))
		window.location.href = "FlowSubmit.do?default=1&docTypeID=" + docTypeID + "&flowID=" + id ;
}
</script>
</HEAD>
<body>
	<div class="mainBodyWrap">
		<table border="0" cellpadding ="4" cellspacing="0" class="table">
			<caption><i18n:message key="operation.listflow.contentTitle"/></caption>
			<tr>
				<th>&nbsp;</th>
				<th>ID</th>
				<th><i18n:message key="operation.flow.name"/></th>
				<th><i18n:message key="operation.flow.description"/></th>
				<th colspan="3">&nbsp;</th>
			</tr>
			<c:forEach items="${flows}" var="item" varStatus="var">
			<tr>
				<td><c:if test="${item.ID==defaultFlowID}">
					<font color="blue"><i18n:message key="operation.flow.isconfirms"/></font>
				</c:if>&nbsp;</td>
				<td><c:out value="${item.ID}"/></td>
				<td><c:out value="${item.name}"/></td>
				<td><c:out value="${item.description}"/>&nbsp;</td>
				<td class="alignCenter">
					<input class="button" onclick="editFlow(<c:out value="${item.ID}"/>)" type="button" name="edit" value="<i18n:message key="operation.icon.edit"/>"/>
					<c:if test="${isAdmin}">
					<input class="button" onclick="deleteFlow(<c:out value="${item.ID}"/>)" type="button" name="delete" value="<i18n:message key="operation.icon.delete"/>"/>
					</c:if>
					<c:if test="${item.ID!=defaultFlowID}">
					<input class="button"  type="button" onclick="setDefault(<c:out value="${item.ID}"/>)" value="<i18n:message key="operation.flow.setdefault"/>"/>
					</c:if>
				</td>
			</tr>
			</c:forEach>
		</table>
	</div>
</body>
<script type="text/javascript">
	<c:if test="${needAlert||needRefresh}">
		<c:if test="${not second}">
			window.parent.refreshNode();
		</c:if>
		<c:if test="${second}">
			window.parent.refreshUpNode();
		</c:if>
	</c:if>
</script>
</html>
