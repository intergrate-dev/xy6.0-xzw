<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
<head><title>Add Role</title></head>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5sys/org/js/xmlhttps.js"></script>
<script type="text/javascript" src="../e5sys/org/js/other.js"></script>
<script language="javascript">
	function checkInput(){
		if(form1.file.value == ''){
			alert("Please select a xml file.");
			return false;
		}
		return true;
	}
	function returnback(){
		document.location.href="ApplicationSystemAction.do";
	}
	function uploadApp(templatefiel){
		var urlstr="ApplicationSystemAction.do?invoke=upload&Template="+templatefiel+"&AppID=0";
		document.location.href=urlstr;
	}
	function deleteApp(appID){
		if (!confirm('<i18n:message key="app.delete.alert"/>')) return;
		if (!confirm('<i18n:message key="app.delete.alert.2"/>')) return;
		if (!confirm('<i18n:message key="app.delete.alert.3"/>')) return;
		{
			
			var urlstr="ApplicationSystemAction.do?invoke=download&AppID="+appID;
			document.location.href=urlstr;
		}
	}
	function exportXml(appID){
		var urlstr="ApplicationSystemAction.do?invoke=export&AppID="+appID;
		window.open(urlstr,"_aaa");
	}
	function uploadTemplate(){
		document.location.href="ApplicationSystemAction.do?invoke=uploadtemplateform";
	}
	function deleteFile(templatefile){
		if(!confirm('<i18n:message key="app.download.delete.templatefile"/>'))
		{
			return;
		}
		var urlsrc="ApplicationSystemAction.do?invoke=deletetemplate"
			+"&Template="+templatefile;
		invokeGetXmlHttpUpdate(urlsrc,"ApplicationSystemAction.do");
	}
	function doUpgrade() {
		document.getElementById("btnUpgrade").disabled = true;
		var urlstr = "ApplicationSystemAction.do?invoke=upgrade";
		document.location.href = urlstr;
	}
</script>
<body>
	<c:if test="${appmap.latestVersion != null}">
		<div style="margin:100px;font-size:20px;">
			<span style="color:red;"><i18n:message key="app.upgrade.title"/>--></span>
			<input type="button" id="btnUpgrade" style="font-size:20px;" onclick="doUpgrade()"
				value="<i18n:message key="app.upgrade.operation"/>${appmap.latestVersion}"/>
		</div>
	</c:if>
	
	<div class="mainBodyWrap">
		<table cellpadding="0" cellspacing="0" class="table">
		<tr>
			<th><i18n:message key="app.list.title.appname"/></th>
			<th><i18n:message key="app.list.title.abbreviation"/></th>
			<th><i18n:message key="app.list.title.version"/></th>
			<th><i18n:message key="app.list.title.provider"/></th>
			<th></th>
		</tr>
		<c:forEach items="${appmap.applist}" var="app">
		<tr onmouseover="overOneTR(this);" onmouseout="outOneTR(this);">
			<td class="alignCenter"><c:out value="${app.name}"/></td>
			<td class="alignCenter"><c:out value="${app.absVersion}"/></td>
			<td class="alignCenter"><c:out value="${app.version}"/></td>
			<td class="alignCenter"><c:out value="${app.provider}"/></td>
			<td class="alignCenter">
				<input class="button" type="button" value="<i18n:message key="app.list.export"/>" 
					onclick="exportXml('<c:out value="${app.appID}"/>')">
				<input class="button" type="button" value="<i18n:message key="app.list.download"/>"
					onclick="deleteApp('<c:out value="${app.appID}"/>')" style="background-color:#aa9999;">
			</td>
		</tr>
		</c:forEach>
		</table>
		<form name="form1" action="ApplicationSystemAction.do?invoke=uploadtemplate"
			method="post" enctype="multipart/form-data" onsubmit="return checkInput();">
			<div class="mt">
				<input type="file" name="file" style="width:600px;">
				<input class="button" type="submit" value="<i18n:message key="app.list.upload"/>" >
			<div>
		</form>
	</div>
</body>
</html>
