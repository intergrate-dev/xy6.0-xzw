<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title>Tree</title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript">
			var form1,docTypeID;
			function init(){
				form1 = document.getElementById("form1");
				docTypeID = document.getElementById("docTypeID");
			}
			function editOp(id){
				form1.action="Operation.do?operationID=" + id;
				form1.submit();
			}

			function deleteOp(id){
				if (!window.confirm('<i18n:message key="operation.listOP.confirm"/>'))
					return false;

				form1.action="OpSubmit.do?del=1&operationID=" + id;
				form1.submit();
			}

			function addOp(){
				var theURL = 'Operation.do?docTypeID=' + docTypeID.value;
				window.location.href = theURL;
			}
			window.onload = init;
		</script>
	</head>
	<body>
		<div class="mainBodyWrap">
		<form name="form1" id="form1" method="post" action="listOp.do">
			<table cellpadding ="0" cellspacing="0" class="table">
				<caption><i18n:message key="operation.listOP.contentTitle"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button class="button" onclick="addOp();return false;"><i18n:message key="operation.addOP.contentTitle"/></button>
				</caption>
				<tr>
					<th>ID</th>
					<th><i18n:message key="operation.listOP.name"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.codeUrl"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.description"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.needLock"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.needRefresh"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.needLog"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.needPrompt"/></th>
					<th style="display:none" ><i18n:message key="operation.listOP.resizable"/></th>
					<th><i18n:message key="operation.listOP.callMode"/></th>
					<th><i18n:message key="operation.listOP.showType"/></th>
					<th><i18n:message key="operation.listOP.dealCount"/></th>
					<th><i18n:message key="operation.listOP.width"/>/<i18n:message key="operation.listOP.height"/></th>
					<th><i18n:message key="operation.listOP.edit"/></th>
					<th><i18n:message key="operation.listOP.delete"/></th>
				</tr>
			<c:forEach items="${operations}" var="item" varStatus="var">
				<tr>
					<td class="alignCenter"><c:out value="${item.ID}"/></td>
					<td class="alignCenter"><c:out value="${item.name}"/></td>
					<td class="alignCenter">
						<c:if test="${item.callMode==2}">
							<i18n:message key="operation.listOP.dialog"/>
						</c:if>
						<c:if test="${item.callMode==1}">
							<i18n:message key="operation.listOP.window"/>
						</c:if>
						<c:if test="${item.callMode==3}">
							<i18n:message key="operation.listOP.nowindow"/>
						</c:if>
					</td>
					<td class="alignCenter">
						<c:if test="${item.showType==0}">
							<label class="blueLabel"><i18n:message key="operation.listOP.shownone"/></label>
						</c:if>
						<c:if test="${item.showType==1}">
							<label class="blueLabel"><i18n:message key="operation.listOP.showtop"/></label>
						</c:if>
						<c:if test="${item.showType==2}">
							<label class="blueLabel"><i18n:message key="operation.listOP.showside"/></label>
						</c:if>
						<c:if test="${item.showType==3}">
							<label class="blueLabel"><i18n:message key="operation.listOP.showtop"/></label>
							<label class="blueLabel"><i18n:message key="operation.listOP.showside"/></label>
						</c:if>
					</td>
					<td><label>
						<c:if test="${item.dealCount==0}">
							<i18n:message key="operation.listOP.none" />
						</c:if>
						<c:if test="${item.dealCount==1}">
							<i18n:message key="operation.listOP.onlyone" />
						</c:if>
						<c:if test="${item.dealCount==2}">
							<i18n:message key="operation.listOP.many" />
						</c:if><label>
					</td>
					<td class="alignCenter">
						<c:out value="${item.width}"/><i18n:message key="operation.listOP.multi"/><c:out value="${item.height}"/>
					</td>
					<td class="alignCenter">
						<input class="button" onclick="editOp(<c:out value="${item.ID}"/>)" type="button" name="edit" value="<i18n:message key="operation.listOP.edit"/>"/>
					</td>
					<td class="alignCenter">
						<input class="button" onclick="deleteOp(<c:out value="${item.ID}"/>)"
							type="button" name="delete" value="<i18n:message key="operation.listOP.delete"/>"
						/>
					</td>
				</tr>
			</c:forEach>
  			</table>
			<input name="docTypeID" id="docTypeID" value="<c:out value="${docTypeID}"/>" type="hidden"/>
		</form>
		</div>
	</body>
	<c:if test="${needAlert||needRefresh}">
	<script>
	window.parent.refreshNode();
	</script>
	</c:if>
</html>
