<%@include file="../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false" />
<%//
	int docTypeID = 0;
	int flowID = 0;
	int flowNodeID = 0;
	if (request.getParameter("docTypeID") != null
			&& !request.getParameter("docTypeID").equals("null")) {
		docTypeID = Integer.parseInt(request.getParameter("docTypeID"));
	}
	if (request.getParameter("flowID") != null
			&& !request.getParameter("flowID").equals("null")) {
		flowID = Integer.parseInt(request.getParameter("flowID"));
	}
	if (request.getParameter("flowNodeID") != null
			&& !request.getParameter("flowNodeID").equals("null")) {
		flowNodeID = Integer.parseInt(request
				.getParameter("flowNodeID"));
	}
%>
<html>
	<head>
		<title>ProcGroup Config</title>
		<link type="text/css" rel="StyleSheet" href="../e5style/style.css" />
		<script type="text/javascript" src="./ProcGroup.js"></script>
		<style type="text/css">
	body{margin:0px;}
	#nameDiv{height:32px; padding-top:10px;}
	</style>
	<script type="text/javascript">
	function Icon(iconID,iconFileName){
		this.iconID = iconID;
		this.iconFileName = iconFileName;
	}
	<%int j = 0;%>
  	var icons = new Array();
 	<c:forEach var="item" items="${icons}">
		icons[<%=j%>] = new Icon('<c:out value="${item.ID}"/>','<c:out value="${item.fileName}"/>');
   		<%j++;%>
  	</c:forEach>
  	
	function Proc(procID,procName,iconUrl){
		this.procID = procID;
		this.procName = procName;
		this.procFileName = iconUrl;
	}
	
	<%int i = 0;%>
  	var procs = new Array();
 	<c:forEach var="item" items="${procs}">
		procs[<%=i%>] = new Proc('<c:out value="${item.procID}"/>','<c:out value="${item.procName}"/>','<c:out value="${item.iconURL}"/>');
   		<%i++;%>
  	</c:forEach>

	var mousedown = false;
	var beginnum = 0;
	var endnum = 0;
	var groupNum = 1;
	var procID = 0;
	var groupID = 0;
	var beginID = 0;
	var changeProcID = 0;
	var isSort = false;//用来区分是同一行的排序还是往组里拖放操作
	var groupArray = new Array();//保存最后组信息的数组
	</script>
	</head>
	<body scroll="yes" onselectstart="return false">
		<div id="nameDiv">
			<i18n:message key="operation.csproc.contentTitle" />
		</div>
		<form name="postForm" method="post" action="./procGroupSubmit.do">
			<input type="hidden" name="restore" value="false" />
			<input type="hidden" name="docTypeID" value="<c:out value="${docTypeID}" />" />
			<input type="hidden" name="flowID" value="<c:out value="${flowID}" />" />
			<input type="hidden" name="flowNodeID" value="<c:out value="${flowNodeID}" />" />
			<input type="hidden" name="ProcID" value="" />
			<script language="javascript">
  				var docTypeID = "<%=docTypeID%>";
  				var flowID = "<%=flowID%>";
  				var flowNodeID = "<%=flowNodeID%>";
  			</script>
			<p align="center">
				<input class="button" type="button" onclick="addGroup()" value="<i18n:message key="operation.proc.addGroup"/>" />
				<input class="button" type="button" onclick="doSubmit()" value="<i18n:message key="operation.proc.submit"/>" />
				<input class="button" type="button" onclick="onRestore()" value="<i18n:message key="operation.proc.reset"/>" />
			</p>
			<table border="0" id="operationTB" cellpadding="5">
				<tr id="operationTR">
					<c:forEach items="${procs}" var="item" varStatus="var">
						<td id="cell<c:out value="${var.index}"/>" 
							index="<c:out value="${var.index}"/>" 
							procID="<c:out value="${item.procID}"/>" 
							onMouseDown="doMouseDown1('<c:out value="${item.procID}"/>')" 
							onmouseup="doMouseUp2()" 
							align="center" style="cursor:hand" nowrap>
							<img id="img<c:out value="${var.index}"/>" src="../<c:out value="${item.iconURL}"/>" alt="" />
							<br />
							<span id="procName<c:out value="${var.index}"/>"> <c:out value="${item.procName}" /> </span>
						</td>
					</c:forEach>
				</tr>
			</table>
			<div id="divGroup">
				<c:forEach items="${groups}" var="item" varStatus="var">
					<table id="group<c:out value="${var.index+1}"/>" class="withWidth" width="100%" style="cursor:hand">
						<script type="text/javascript" language="javascript">
							groupArray[<c:out value="${var.index}"/>] = new Array();//创建一个操作组
							groupNum++;
						</script>
						<tr id="tr<c:out value="${var.index+1}"/>" 
							height="40px" 
							procNum="<c:out value="${item.procCount}"/>" 
							maxProcNum="<c:out value="${item.procCount}"/>" 
							onmouseover="doMouseOver1('group<c:out value="${var.index+1}"/>');" 
							onmouseup="doMouseUp1('tr<c:out value="${var.index+1}"/>', groupArray[<c:out value="${var.index}"/>]);">
							<td id="td0" width="50px" align="center">组<c:out value="${var.index+1}"/></td>
							<c:forEach items="${item.list}" var="list" varStatus="status">
								<td id="td<c:out value="${status.index+1}"/>" 
									procID="<c:out value="${list.procID}"/>" 
									align="center" width="50px" 
									onmouseover="doMouseOver()" 
									ondblclick="doDbClick('tr<c:out value="${var.index+1}"/>', 'td<c:out value="${status.index+1}"/>', groupArray[<c:out value="${var.index}"/>])" 
									onmousedown="doMouseDown('tr<c:out value="${var.index+1}"/>', 'td<c:out value="${status.index+1}"/>')" 
									onmouseup="doMouseUp('tr<c:out value="${var.index+1}"/>', 'td<c:out value="${status.index+1}"/>', groupArray[<c:out value="${var.index}"/>])">
									<script type="text/javascript" language="javascript">
										groupArray[<c:out value="${var.index}"/>].push('<c:out value="${list.procID}"/>');
									</script>
									<img id='img<c:out value="${status.index+1}"/>' src='../<c:forEach items="${procs}" var="proc"><c:if test="${proc.procID == list.procID}"><c:out value="${proc.iconURL}"/></c:if></c:forEach>' alt=''/><br/>
									<span id='procName<c:out value="${status.index+1}"/>'><c:forEach items="${procs}" var="proc"><c:if test="${proc.procID == list.procID}"><c:out value="${proc.procName}"/></c:if></c:forEach></span>
								</td>
							</c:forEach>
							<td id="tdx" align="left">&nbsp;</td>
						</tr>
					</table>
					<br/>
				</c:forEach>
			</div>
		</form>
	</body>
</html>
