<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<HEAD>
<TITLE>Process List</TITLE>
<link   type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
<style  type="text/css">
	#titleDiv{
		height: 40px;
		background-color: #DDDDDD;
		padding-top: 10px;
		padding-left: 200px;
	}
	form{margin:0px;}
</style>
<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>

<script type="text/javascript">
var flowID = "<c:out value="${flowID}"/>";
var flowNodeID = "<c:out value="${flowNodeID}"/>";
var docTypeID = "<c:out value="${docTypeID}"/>";

function addProc(){
	var theURL = "Proc.do?docTypeID=" + docTypeID;
	if (flowNodeID) theURL += "&flowNodeID=" + flowNodeID;

	window.location.href = theURL;
}
function editProc(procID, procType)
{
	var theURL = "Proc.do?procID=" + procID
			+ "&procType=" + procType;
	if (docTypeID) theURL += "&docTypeID=" + docTypeID;
	if (flowNodeID) theURL += "&flowNodeID=" + flowNodeID;
	window.location.href = theURL;
}

function deleteProc(procType, procID)
{
	if (!window.confirm('<i18n:message key="operation.proc.confirm"/>'))
		return false;
	var theURL = "deleteProc.do?procType=" + procType + "&procID=" + procID;
	if (flowNodeID) theURL += "&flowNodeID=" + flowNodeID;
	if (docTypeID) theURL += "&docTypeID=" + docTypeID;

	window.location.href = theURL;
}
</script>
</HEAD>
  <body>

  <table border="0" align="center" cellpadding ="4" cellspacing="0" width="100%" class="table">
  <caption> <i18n:message key="operation.listproc.contentTitle"/></caption>
	<tr>
		<td>ID</td>
		<td><i18n:message key="operation.proc.procType"/></td>
		<td><i18n:message key="operation.proc.iconID"/></td>
		<td><i18n:message key="operation.proc.procName"/></td>
		<td><i18n:message key="operation.proc.opID"/></td>
		<td><i18n:message key="operation.proc.description"/></td>
		<td></td>
		<td colspan=2></td>
	</tr>
	<c:forEach items="${procs}" var="item" varStatus="var">
		<tr>
			<td ><c:out value="${item.proc.procID}"/></td>
			<td>
			<c:choose>
				<c:when test="${item.proc.procType == 1}">
					<i18n:message key="operation.proc.DO"/>
				</c:when>
				<c:when test="${item.proc.procType == 2}">
					<i18n:message key="operation.proc.GO"/>
				</c:when>
				<c:when test="${item.proc.procType == 3}">
					<i18n:message key="operation.proc.BACK"/>
				</c:when>
				<c:when test="${item.proc.procType == 4}">
					<i18n:message key="operation.proc.JUMP"/>
				</c:when>
				<c:when test="${item.proc.procType == 5}">
					<i18n:message key="operation.proc.UNFLOW"/>
				</c:when>
			</c:choose>
			</td>
			<td ><img src="../<c:out value="${item.iconURL}"/>" title="<c:out value="${item.iconURL}"/>"></td>
			<td ><font color="blue"><c:out value="${item.proc.procName}"/></font></td>
			<td ><c:out value="${item.opName}"/></td>
			<td ><c:out value="${item.proc.description}"/>&nbsp;</td>
			<td ><c:out value="${item.nextFlowName}"/>&nbsp; </td>
			<td ><c:out value="${item.nextFlowNodeName}"/>&nbsp;</td>
			<td >
				<input class="button" type="button"
				value="<i18n:message key="operation.proc.edit"/>"
				onclick="editProc(<c:out value="${item.proc.procID}"/>, <c:out value="${item.proc.procType}"/>)"  alt="">
				<input class="button" type="button"
				value="<i18n:message key="operation.proc.delete"/>"
				onclick="deleteProc(<c:out value="${item.proc.procType}"/>,<c:out value="${item.proc.procID}"/>)"/>
			</td>
		</tr>
	</c:forEach>
	<tr><td colspan="10" >&nbsp;</td></tr>
  </table>
  <div class="btn-area" style="text-align:center">
  <button class="button" onclick="addProc();return false;"><i18n:message key="operation.addproc.contentTitle"/></button>
  </div>
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
