<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<title>Flow View</title>
	<script type="text/javascript">
		function editFlow(){
			if (form1.flowName.value == null||form1.flowName.value=="")
			{
				alert("<i18n:message key="operation.flow.nullalert"/>");
				return false;
			}
		}
		//删除流程
		function deleteFlow(){
			if (form1.flowID.value == "0") return;//新建流程，不做删除处理
			if (window.confirm('<i18n:message key="operation.listflow.deleteconfirm"/>')){
				form1.action = "FlowSubmit.do?del=1";
				form1.submit();
			}
		}
		//设为缺省流程
		function setDefault(){
			if (form1.flowID.value == "0") return;//新建流程，不做删除处理
			if (window.confirm('<i18n:message key="operation.flow.confirmset"/>')){
				form1.action = "FlowSubmit.do?default=1";
				form1.submit();
			}
		}
	</script>
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>
</head>
<body>
	<div class="mainBodyWrap">
		<form name="form1" method="post" action="FlowSubmit.do">
	  		<input name="docTypeID" value='<c:out value="${docTypeID}"/>' type="hidden"/>
	  		<input name="flowID" value='<c:out value="${flow.ID}"/>' type="hidden"/>
			<c:if test="${second==1}">
				<input type="hidden" name="second" value="1" />
			</c:if>
			<table class="table" cellpadding="0" cellspacing="0">
				<caption><i18n:message key="operation.flow.title"/></caption>
				<tr>
					<th class="w90">
						<i18n:message key="operation.flow.name"/>
					</th>
					<td>
						<input type="text" name="flowName" value="<c:out value="${flow.name}"/>" />
					</td>
				</tr>
				<tr>
					<th>
						<i18n:message key="operation.flow.description"/>
					</th>
					<td>
						<input type="text" name="description" style="width:400px;" value="<c:out value="${flow.description}"/>" />
					</td>
				</tr>
			</table>
			<div class="alignCenter mt">
				<button type="submit" class="button" onclick="return editFlow();"><i18n:message key="operation.flow.submit"/></button>
				<c:if test="${flow.ID != 0}">
				<button class="button" onclick="deleteFlow()"><i18n:message key="operation.flow.delete"/></button>
				<button class="button" onclick="setDefault()"><i18n:message key="operation.flow.setdefault"/></button>
				</c:if>
			</div>
		</form>
	</div>
</body>
</html>
