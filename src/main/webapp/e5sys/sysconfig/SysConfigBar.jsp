<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
	<head>
		<title>Add Role</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../org/js/xmlhttps.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-sysconfig-SysConfigBar.css"/>
	</head>
<body onload="initShow();">
<div id="titleDiv">
	<select name="selectedApp" id="selectedApp" onchange="doChange(this)" >
	<option value="<c:out value="0"/>"><i18n:message key="sysconfig.bar.mainapp"/></option>
	<c:forEach items="${applist}" var="app">
	<option value="<c:out value="${app.appID}"/>"><c:out value="${app.name}"/></option>
	</c:forEach>
	</select>
	<input type="button" class="button" onClick="addSysConfig();" value="<i18n:message key="sysconfig.bar.add"/>"/>
	<input type="button" class="button" onClick="modifySysConfig()" value="<i18n:message key="sysconfig.bar.update"/>"/>
	<input type="button" class="button" onClick="deleteSysConfig()" value="<i18n:message key="sysconfig.bar.delete"/>"/>
</div>
<script type="text/javascript">
var appID;
function initShow(){
	var selectedApp = document.getElementById("selectedApp");
	selectedApp.value = 1;
	doChange(selectedApp);
}
function addSysConfig(){
	var url="SysConfigureMgrAction.do?invoke=configItemForm&appID="+appID;
	parent.frames[1].configItemForm(url);
}

function modifySysConfig(){
	var selid=parent.mainBody.sysConfigID;
	if(selid=='0')alert("<i18n:message key="sysconfig.bar.update.select.alert"/>");
	else{
	  	var url="SysConfigureMgrAction.do?invoke=configItemForm&appID="+appID+"&sysConfigID="+selid
	  	parent.frames[1].configItemForm(url);
	}
	
}
function deleteSysConfig(){
	var selid=parent.mainBody.sysConfigID;
	if(selid=='0')alert("<i18n:message key="sysconfig.bar.delete.select.alert"/>");
 	 else
 	 {
  	  if(confirm("<i18n:message key="sysconfig.bar.delete.alert"/>"))
  	  {

		document.body.style.cursor = 'wait';
		urlsrc="SysConfigureMgrAction.do?invoke=delConfigItem&sysConfigID="+selid;
		var sInf=invokeGetXmlHttpDoForResponse(urlsrc);
		if(sInf=="1")
		{
			parent.frames[1].document.location.href="SysConfigureMgrAction.do?invoke=sysConfigList&appID="+appID;
		}
		else
		{
			alert("<i18n:message key="sysconfig.delete.failed"/>");
		}
		parent.mainBody.document.getElementById("iframe").style.display="none";
	  }
	}
}
function doChange(src){
	appID = src.options[src.selectedIndex].value
	parent.frames[1].document.location.href="SysConfigureMgrAction.do?invoke=sysConfigList&appID="+appID
}
</script>
</body>
</html>
