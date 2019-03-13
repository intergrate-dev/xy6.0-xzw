<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title>FlowNode View</title>
		<script type="text/javascript">
			function editNode(){
				if (!form1.flowNodeName.value){
					alert("<i18n:message key="operation.flownode.nullalert"/>");
					return false;
				}
			}
			/*--删除--*/
			function deleteNode(){
				if (form1.flowID.value == "0") return;
				/*--新建的，不做删除处理--*/
				if (window.confirm("<i18n:message key="operation.flownode.confirm"/>")){
					form1.action = "FlowNodeSubmit.do?del=1";
					form1.submit();
				}
			}
		</script>
		<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
		<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>
	</head>
<body>
	<div class="mainBodyWrap">
		<form name="form1" method="post" action="FlowNodeSubmit.do">
			<input type="hidden" name="flowID" value="<c:out value="${flowID}"/>" />
			<input type="hidden" name="flowNodeID" value="<c:out value="${flowNode.ID}"/>" />
			<c:if test="${second==1}">
			<input type="hidden" name="second" value="1" />
			</c:if>
			<table border="0" cellpadding="0" class="table">
				<caption><i18n:message key="operation.flownode.title"/></caption>
				<tr>
					<th class="w90"><i18n:message key="operation.flownode.name"/></th>
					<td><input type="text" name="flowNodeName" value='<c:out value="${flowNode.name}"/>'/></td>
				</tr>
				<tr style="display:none;">
					<th><i18n:message key="operation.flownode.description"/></th>
					<td><input type="text" name="description" value='<c:out value="${flowNode.description}"/>' /></td>
				</tr>
				<tr>
					<th><i18n:message key="operation.flownode.doingStatus"/></th>
					<td><input type="text" name="doingStatus" value='<c:out value="${flowNode.doingStatus}"/>' /></td>
				</tr>
				<tr>
					<th><i18n:message key="operation.flownode.waitingStatus"/></th>
					<td><input type="text" name="waitingStatus" value='<c:out value="${flowNode.waitingStatus}"/>' /></td>
				</tr>
				<tr>
					<th><i18n:message key="operation.flownode.doneStatus"/></th>
					<td><input type="text" name="doneStatus" value='<c:out value="${flowNode.doneStatus}"/>' /></td>
				</tr>
				<!--======当新加流程节点时，显示插入位置=======-->
				<c:if test="${flowNode.ID == 0}">
				<tr>
					<th><i18n:message key="operation.flownode.intonode"/></th>
					<td>
						<select class="select" name="loc">
							<c:forEach items="${flowNodes}" var="item" varStatus="var">
								<c:if test="${var.index==0}">
									<option value="<c:out value="${item.ID}"/>"><i18n:message key="operation.flownode.first"/></option>
								</c:if>
								<option value="<c:out value="${item.ID}"/>">
									<c:out value="${item.name}" />
									<i18n:message key="operation.flownode.insertBefore"/>
								</option>
							</c:forEach>
							<option value="0" selected><i18n:message key="operation.flownode.insertAfter"/></option>
						</select>
					</td>
				</tr>
				</c:if>
			</table>
			<div class="alignCenter mt">
				<button type="submit" class="button" onclick="return editNode();"><i18n:message key="operation.flownode.submit"/></button>
				<c:if test="${flowNode.ID != 0}">
				<button class="button" onclick="deleteNode()"><i18n:message key="operation.flownode.delete"/></button>
				</c:if>
			</div>
		</form>
	</div>
</body>
</html>
