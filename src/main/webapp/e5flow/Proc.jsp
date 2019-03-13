<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title>Proc Create-Update</title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			select {width:200px;}
			input{width:200px;}
		</style>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script type="text/javascript">
			var length = 0;
			<c:forEach items="${flows}" var="item" varStatus="var">
				length++;
			</c:forEach>

			var flowIDs = new Array(length);
			var flowNames = new Array(length);
			var nodeIDArr = new Array();
			var nodeNameArr = new Array();
			var flowID = 0; //当前流程ID。没有从控制器中获得，因此通过下面的方式取得。
			var flowNodeID = <c:out value="${flowNodeID}"/>

			<c:forEach items="${flows}" var="item" varStatus="var">
				flowIDs[<c:out value="${var.index}" />] = "<c:out value="${item.ID}" />";
				flowNames[<c:out value="${var.index}" />] = "<c:out value="${item.name}" />";
				var names<c:out value="${var.index}" /> = new Array();
				var ids<c:out value="${var.index}" /> = new Array();
				<c:forEach items="${nodes[var.index]}" var="node" varStatus="var2">
					names<c:out value="${var.index}"/>[<c:out value="${var2.index}"/>] = '<c:out value="${node.name}"/>';
					ids<c:out value="${var.index}"/>[<c:out value="${var2.index}"/>] = <c:out value="${node.ID}"/>;
					if (<c:out value="${node.ID}"/> == flowNodeID)
					{
						flowID = <c:out value="${node.flowID}"/>;
					}
				</c:forEach>
				nodeIDArr[<c:out value="${var.index}"/>] = ids<c:out value="${var.index}"/>;
				nodeNameArr[<c:out value="${var.index}"/>] = names<c:out value="${var.index}"/>;
			</c:forEach>
			var nextFlowID = "0";
			var nextFlowNodeID = "0";
			<c:if test="${proc.procType != 5}">
				nextFlowID = "<c:out value="${proc.nextFlowID}"/>";
				nextFlowNodeID = "<c:out value="${proc.nextFlowNodeID}"/>";
			</c:if>
			/////////////////////////////
			//页面加载后的初始化，跳转流程、跳转流程节点、图标显示、操作模块指定

			function doInit(){
				doInitSelection(document.form1.operationID, "<c:out value="${proc.opID}"/>");
				doInitIcon();
				check(); //操作类型变化
				doInitSelection(document.form1.nextFlowID, nextFlowID);
				change();//流程节点变化
				doInitSelection(document.form1.nextFlowNodeID, nextFlowNodeID);
			}
			//修改时，初始化图标显示
			function doInitIcon(){
				<c:if test="${proc.iconID == 0}">
				return;
				</c:if>
				<c:forEach items="${icons}" var="icon">
					<c:if test="${icon.ID == proc.iconID}">
					selIcon(<c:out value="${icon.ID}"/>,'<c:out value="${icon.url}"/>');
					</c:if>
				</c:forEach>
			}
			//修改时，初始化select下拉框显示

			function doInitSelection(e, strValue){
				if (!e || !strValue) return;

				for (var i = 0; i < e.options.length; i++)
				{
					if (e.options[i].value == strValue)
					{
						e.selectedIndex = i;
						break;
					}
				}
			}
			//操作类型变化时，对跳转操作，显示流程节点选择框
			function check(){
				var jp = document.getElementById("jumpNode");
				if (form1.procType.value == 4) 
				{
					jp.style.display='';
					<c:if test="${(flowNodeID > 0) && (proc.procID == 0)}">
						doInitSelection(form1.nextFlowID, flowID);
						change();
					</c:if>
				}
				else 
					jp.style.display='none';


			}
			//跳转的流程变化时，改变流程节点

			function change(){
				var sel = form1.nextFlowID;
				if (sel.selectedIndex == -1) return false;

				var value = sel.value;

				var src = form1.nextFlowNodeID;

				var ids = null;
				var names;
				for(i=0;i<flowIDs.length;i++)
				{
					if(flowIDs[i]==value)
					{
						ids = nodeIDArr[i];
						names = nodeNameArr[i];
					}
				}
				while (src.length>0) src.remove(0);

				if (!ids) return;

				var pos = 0;

				if (nextFlowNodeID == "0") nextFlowNodeID = form1.flowNodeID.value;
				for(i = 0; i < ids.length; i++)
				{
					var op = document.createElement('option');
					op.value = ids[i];
					op.text = names[i];

					if (op.value == nextFlowNodeID)
						pos = i;
					if(navigator.appName.indexOf("Microsoft")!=-1) src.add(op);
					else src.appendChild(op);
				}
				src.selectedIndex = pos;
			}
			//提交：检查合法性

			function subform(){
				if(form1.operationID.options.length==0)
				{
					alert("<i18n:message key="operation.proc.opnull"/>");
					return false;
				}
				if(form1.procName.value==null||form1.procName.value=='')
				{
					alert("<i18n:message key="operation.proc.unnull"/>");
					return false;
				}
				if(form1.procType.value==4&&form1.nextFlowID.value=='-1')
				{
					alert("<i18n:message key="operation.listOP.jumperr"/>");
					return false;
				}
				if(form1.procType.value==4&&form1.nextFlowID.value!='-1'&&form1.nextFlowNodeID.options.length<1)
				{
					alert("<i18n:message key="operation.listOP.jumperr2"/>");
					return false;
				}
				if(form1.iconID.value=="-99")
				{
					form1.iconID.value==0;
				}
				if(getLength(form1.procName.value)>13*3)
				{
					alert('<i18n:message key="operation.proc.procName"/><i18n:message key="operation.common.prelength"/>13<i18n:message key="operation.common.afterlength"/>');
					return false;
				}
				if(getLength(form1.description.value)>40*3)
				{
					alert('<i18n:message key="operation.proc.description"/><i18n:message key="operation.common.prelength"/>40<i18n:message key="operation.common.afterlength"/>');
					return false;
				}
			}
			//点击选择图标的按钮

			function selIcon2(){
				var iconDiv = document.getElementById("showPic");
				iconDiv.style.display='block';
			}
			//图标选择后，改变显示的图标

			function selIcon(id,url){
				document.image.src = "../"+url;
				form1.iconID.value = id;
				var iconDiv = document.getElementById("showPic");
				iconDiv.style.display='none';
			}
			function doDelete(){
				if (!window.confirm('<i18n:message key="operation.proc.confirm"/>'))
					return false;
				form1.action = "deleteProc.do";
				form1.submit();
			}
		</script>
	</head>
<body onload="doInit()">
	<div class="mainBodyWrap">
		<form name="form1" method="post" action="addProc.do">
			<input type="hidden" name="flowNodeID" value="<c:out value="${flowNodeID}"/>" />
			<input type="hidden" name="docTypeID" value="<c:out value="${docTypeID}"/>" />
			<input type="hidden" name="iconID" value="<c:out value="${proc.iconID}"/>" />
			<input type="hidden" name="procID" value="<c:out value="${proc.procID}"/>" />
			<table class="table" cellpadding="0" cellspacing="0">
				<caption><i18n:message key="operation.proc.title"/></caption>
				<tr>
					<th class="w90"><i18n:message key="operation.proc.procName"/></th>
					<td><input type="text" name="procName" value="<c:out value="${proc.procName}"/>" /></td>
				</tr>
				<tr>
					<th valign="top"><i18n:message key="operation.proc.procType"/></th>
					<td>
					<!-- 若是新增操作(proc.procID == 0)，并且是新加流程操作(flowNodeID > 0)，则显示操作类型，否则不能修改 -->
						<c:if test="${(flowNodeID > 0) && (proc.procID == 0)}">
							<select  class="select" name="procType" id="sele" onchange="check()">
								<option value="1"><i18n:message key="operation.proc.DO"/></option>
								<c:if test="${!isLast}">
									<option value="2"><i18n:message key="operation.proc.GO"/></option>
								</c:if>
								<c:if test="${!isFirst}">
									<option value="3"><i18n:message key="operation.proc.BACK"/></option>
								</c:if>
								<option value="4"><i18n:message key="operation.proc.JUMP"/></option>
							</select>
						</c:if>
						<c:if test="${(flowNodeID == 0) || (proc.procID > 0)}">
							<select name="procType" id="sele">
								<option value="<c:out value="${proc.procType}"/>">
								<c:choose>
									<c:when test="${(proc.procType == 2)}"><i18n:message key="operation.proc.GO"/></c:when>
									<c:when test="${(proc.procType == 3)}"><i18n:message key="operation.proc.BACK"/></c:when>
									<c:when test="${(proc.procType == 4)}"><i18n:message key="operation.proc.JUMP"/></c:when>
									<c:when test="${(proc.procType == 5)}"><i18n:message key="operation.proc.UNFLOW"/></c:when>
									<c:otherwise><i18n:message key="operation.proc.DO"/></c:otherwise>
								</c:choose>
								</option>
							</select>
						</c:if>
						<c:if test="${proc.procType != 5}">
							<br/><br/>
							<font color="gray"><i18n:message key="operation.proc.proctype.hint"/></font>
						</c:if>
					</td>
				</tr>
				<tr>
					<th><i18n:message key="operation.proc.opID"/></th>
					<td>
						<select class="select" name="operationID">
							<c:forEach items="${operations}" var="item3" varStatus="var">
								<option value="<c:out value="${item3.ID}"/>"><c:out value="${item3.name}"/></option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<th><i18n:message key="operation.proc.iconID"/></th>
					<td>
						<img name="image" id="image" src=""/>
						<input class="button" type="button" value="<i18n:message key="operation.icon.selicon"/>" 
							onclick="selIcon2()" title="<i18n:message key="operation.icon.selicon"/>" style="width:90px;"/>
					</td>
				</tr>
				<tr style="display:none" id="jumpNode">
						<th><i18n:message key="operation.proc.jumpflownode"/></th>
						<td>
							<select class="select" name="nextFlowID" onchange="change()">
								<c:forEach items="${flows}" var="item2" varStatus="var2">
									<option value="<c:out value="${item2.ID}"/>">
										<c:out value="${item2.name}"/>
									</option>
								</c:forEach>
							</select>&nbsp;--&nbsp;
							<select class="select" name="nextFlowNodeID"></select>
						</td>
				</tr>
				<tr>
					<th><i18n:message key="operation.proc.description"/></th>
					<td>
						<input type="text" name="description" style="width:300px;"
							value="<c:out value="${proc.description}"/>"/>
					</td>
				</tr>
				<tr>
					<td COLSPAN="2" align="center" >
						<button type="submit" class="button" onclick="return subform();"><i18n:message key="operation.proc.submit"/></button>&nbsp;&nbsp;&nbsp;&nbsp;
						<c:if test="${proc.procID != 0}">
						<button class="button" onclick="doDelete()"><i18n:message key="operation.proc.delete"/></button>
						</c:if>
					</td>
				</tr>
			</table>
		</form>
		<div id="showPic" style="display:none">
			<table cellspacing="0" cellpadding="0" class="onlyBorder">
			<tr>
			<c:forEach items="${icons}" var="item" varStatus="var">
				<td align="center" onclick="selIcon(<c:out value="${item.ID}"/>,'<c:out value="${item.url}"/>');">
					<img name="icons" src="../<c:out value="${item.url}"/>"/>
					<br />
					<c:out value="${item.fileName}"/>
				</td>
				<c:if test="${(var.index>=7)&&((var.index+1)%8==0)}"></tr><tr></c:if>
			</c:forEach>
			</tr>
			</table>
		</div>
	</div>
</body>
</html>
