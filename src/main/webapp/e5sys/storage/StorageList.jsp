<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5storage" changeResponseLocale="false"/>
<html>
	<head>
		<title>Add Role</title>
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<script type="text/javascript" src="../org/js/xmlhttps.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
		<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
	</head>
	<body>
		<iframe name="iframe" id="iframe" src="" style="display:none" height="280" width="650" frameborder="0"></iframe>
		<div class="mainBodyWrap">
		<div id="test_ret"></div>
		<div id="test_ret_form"></div>
		<table cellpadding="0" cellspacing="0" class="table">
			<tr>
				<th><i18n:message key="storage.list.name"/></th>
				<th><i18n:message key="storage.list.device.type"/></th>
				<th><i18n:message key="storage.list.content"/></th>
				<th><i18n:message key="storage.list.device.note"/></th>
				<th><i18n:message key="storage.list.device.operate"/></th>
			</tr>
			<c:forEach items="${storagelist}" var="storageinfo">
				<tr id="<c:out value="${storageinfo.deviceName}"/>">
					<td><c:out value="${storageinfo.deviceName}"/></td>
					<td>
						<c:choose>
							<c:when test="${storageinfo.deviceType=='1'}">NFS</c:when>
							<c:when test="${storageinfo.deviceType=='2'}">NTFS</c:when>
							<c:when test="${storageinfo.deviceType=='3'}">FTP</c:when>
							<c:when test="${storageinfo.deviceType=='4'}">HTTP</c:when>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${storageinfo.deviceType=='1'}"><c:out value="${storageinfo.nfsDevicePath}"/></c:when>
							<c:when test="${storageinfo.deviceType=='2'}"><c:out value="${storageinfo.ntfsDevicePath}"/></c:when>
							<c:when test="${storageinfo.deviceType=='3'}">
								<c:out value="${storageinfo.ftpDeviceURL}"/>
								<br>
								<i18n:message key="storage.list.device.accessuser"/>
								<c:out value="${storageinfo.userName}"/>
							</c:when>
							<c:when test="${storageinfo.deviceType=='4'}"><c:out value="${storageinfo.httpDeviceURL}"/></c:when>
						</c:choose>
					</td>
					<td><c:out value="${storageinfo.notes}"/></td>
					<td>
						<input class="button" type="button" value="<i18n:message key="storage.list.device.button.update"/>" onclick="updateDevice('<c:out value="${storageinfo.deviceName}"/>')"/>
						<input class="button" type="button" value="<i18n:message key="storage.list.device.button.delete"/>" onclick="delDevice('<c:out value="${storageinfo.deviceName}"/>')"/>
						<input class="button" type="button" value="<i18n:message key="storage.test.title"/>" onclick="testftp('<c:out value="${storageinfo.ftpDeviceURL}"/>','<c:out value="${storageinfo.userName}"/>','<c:out value="${storageinfo.userPassword}"/>','test_ret')" <c:if test="${storageinfo.deviceType!='3'}">style="display:none"</c:if>/>
					</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="5"><input class="button" type="button" value="<i18n:message key="storage.list.device.button.add"/>" onclick="addDevice()"></td>
			</tr>
		</table>
		</div>
		<form name="devform" id="devform" action="StorgeDeviceFormAction.do" method="post" target="iframe">
			<input name="invoke" id="invoke" value="" type="hidden">
			<input name="deviceName" id="deviceName" value="" type="hidden">
		</form>
<script type="text/javascript">
var addDeviceDialog;
$(document).ready(function(){
	addDeviceDialog = e5.dialog("", {
		title : "<i18n:message key="storage.form.title"/>",
		id : "addDeviceDialog",
		width : 700,
		height : 320,
		resizable : true,
		showClose : true,
		ishide : true
	});
	addDeviceDialog.DOM.content.append($("#test_ret_form"));
	addDeviceDialog.DOM.content.append($("#iframe"));
});
var deviceName = document.getElementById('deviceName'),
	iframe = document.getElementById('iframe'),
	invoke = document.getElementById('invoke'),
	devform = document.getElementById('devform');

function addDevice(){
	addDeviceDialog.show();
	deviceName.value="";
	iframe.style.display="";
	invoke.value="storageForm";
	devform.submit();
	clearText();
}
function updateDevice(storageName){
	addDeviceDialog.show();
	iframe.style.display="";
	invoke.value="storageForm";
	deviceName.value=storageName;
	devform.submit();
	clearText();
}
function delDevice(storageName){
	if (!confirm("<i18n:message key="storage.del.confirm"/>")){
		return;
	}
	invoke.value="delStorage";
	deviceName.value=storageName;
	devform.target="_self";
	devform.submit();
	clearText();
}
function refreshPage(){
	document.location.href="StorageDeviceMgrAction.do?invoke=storageList&pp=1";
}
function hideform(){
	addDeviceDialog.hide();
	deviceName.value="";
	clearText();
}
function clearText(){
	var listspantext = document.getElementById("test_ret");
	var formspantext = document.getElementById("test_ret_form");
	listspantext.innerHTML = '';
	formspantext.innerHTML = '';
}
var spantext = "";
function testftp(url,usr,pwd,textId){
	spantext = document.getElementById(textId);
	var theURL = "./TestStorage.jsp?url="+url+"&user="+usr+"&pwd="+pwd;
	getDataProvider(theURL, doCheckResult, true, true);
}
function doCheckResult(type, xmlDoc, evt){
	if("true" == getText(xmlDoc.documentElement.childNodes[0])){
		alert("<i18n:message key="storage.test.succ"/>");
	} else {
		alert("<i18n:message key="storage.test.fail"/>");
	}
}
function getText(oNode){
	return (oNode.text) ? oNode.text : oNode.textContent;
}
</script>
</body>
</html>
