<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<HEAD>
<TITLE>FlowNode List</TITLE>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript">
var flowID = "<c:out value="${flowID}"/>";
//增加流程节点
function addNode()
{
	window.location.href = "FlowNode.do?flowID=" + flowID;
}
//修改流程节点
function editNode(id)
{
	window.location.href = "FlowNode.do?flowID=" + flowID + "&flowNodeID=" + id ;
}
//删除流程节点
function deleteNode(id)
{
	if (window.confirm('<i18n:message key="operation.flownode.confirm"/>'))
		window.location.href = "FlowNodeSubmit.do?del=1&flowID=" + flowID + "&flowNodeID=" + id ;
}
</script>
<style>th{text-align:left;}</style>
</HEAD>
<body>
<table border="0" cellpadding ="4" cellspacing="0" class="table">
	<caption>
	<i18n:message key="operation.listnode.contentTitle"/>
	<!--<button class="button" onclick="addNode()"><i18n:message key="operation.addnode.contentTitle"/></button>-->
	</caption>
	<tr>
		<th>ID</th>
		<th><i18n:message key="operation.flownode.name"/></th>
		<th><i18n:message key="operation.flownode.waitingStatus"/></th>
		<th><i18n:message key="operation.flownode.doingStatus"/></th>
		<th><i18n:message key="operation.flownode.doneStatus"/></th>
		<!--<th><i18n:message key="operation.flownode.description"/></th>-->
		<!--<th colspan="2"></th>-->
	</tr>
	<c:forEach items="${flowNodes}" var="item" varStatus="var">
		<tr onmouseover="this.bgColor='#E4E8EB';" onmouseout="this.bgColor='#ffffff';">
			<td><c:out value="${item.ID}"/></td>
			<td><c:out value="${item.name}"/></td>
			<td><c:out value="${item.waitingStatus}"/></td>
			<td><c:out value="${item.doingStatus}"/></td>
			<td><c:out value="${item.doneStatus}"/></td>
			<!--
			<td>
				<input class="button" onclick="editNode(<c:out value="${item.ID}"/>)" type="button" value="<i18n:message key="operation.flownode.edit"/>"/>
			</td>
			<td>
				<input class="button"  onclick="deleteNode(<c:out value="${item.ID}"/>)"
					type="button" value="<i18n:message key="operation.flownode.delete"/>"/>
			</td>
			-->
		</tr>
	</c:forEach>
</table>
</body>
<script type="text/javascript">
	<c:if test="${needAlert||needRefresh}">
		<c:if test="${not second}">
		window.parent.refreshNode();
		</c:if>
	</c:if>
	<c:if test="${second}">
		window.parent.refreshUpNode();
	</c:if>
  </script>
</html>
