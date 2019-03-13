<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5storage" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="storage.form.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<script type="text/javascript" src="../org/js/xmlhttps.js"></script>
		<link type="text/css" rel="StyleSheet" href="../../e5style/style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-storage-StorageForm.css"/>
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	</head>
	<body onload="chnageType(<c:out value="${storagedevice.deviceType}"/>)">
	<form id="deviceform" name="deviceform" method="post" action="StorgeDeviceFormAction.do" target="_parent">
	<table cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="storage.add.title"/></caption>
		<tr>
			<td>
				<i18n:message key="storage.list.name"/>
			</td>
			<td>
				<c:if test="${storagedevice.deviceName==''}">
					<input type="hidden" id="invoke" name="invoke" value="add">
					<input type="text" id="deviceName" name="deviceName" value="<c:out value="${storagedevice.deviceName}"/>" class="validate[required,maxSize[60]] narrowinput"/>
					<br>
				</c:if>
				<c:if test="${storagedevice.deviceName!=''}">
						<input type="hidden" id="invoke" name="invoke" value="update">
						<input type="hidden" id="deviceName" name="deviceName" value="<c:out value="${storagedevice.deviceName}"/>"/>
						<c:out value="${storagedevice.deviceName}"/>
				</c:if>
			</td>
			<td>
				<i18n:message key="storage.list.device.type"/>
			</td>
			<td>
				<select class="narrowinput" id="deviceType" name="deviceType" onchange="chnageType(this.value);">
					<option value='3' <c:if test="${storagedevice.deviceType=='3'}">selected</c:if>>FTP</option>
					<option value='2' <c:if test="${storagedevice.deviceType=='2'}">selected</c:if> >NTFS</option>
					<option value='1' <c:if test="${storagedevice.deviceType=='1'}">selected</c:if>>NFS</option>
					<option value='4' <c:if test="${storagedevice.deviceType=='4'}">selected</c:if>>HTTP</option>
				</select>
			</td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.nfs"/></td>
			<td colspan="3"><input type="text" id="nfsDevicePath" name="nfsDevicePath" value="<c:out value="${storagedevice.nfsDevicePath}"/>" class="validate[maxSize[100]] broadinput"></td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.ntfs"/></td>
			<td colspan="3"><input type="text" id="ntfsDevicePath" name="ntfsDevicePath" value="<c:out value="${storagedevice.ntfsDevicePath}"/>" class="validate[maxSize[100]] broadinput"></td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.http"/></td>
			<td colspan="3"><input type="text" id="httpDeviceURL" name="httpDeviceURL" value="<c:out value="${storagedevice.httpDeviceURL}"/>" class="validate[maxSize[100]] broadinput"></td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.ftp"/></td>
			<td colspan="3"><input type="text" id="ftpDeviceURL" name="ftpDeviceURL" value="<c:out value="${storagedevice.ftpDeviceURL}"/>" class="validate[maxSize[100]] broadinput"></td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.ftp.user.name"/></td>
			<td><input type="text" id="userName" name="userName" value="<c:out value="${storagedevice.userName}"/>" class="validate[maxSize[20]] narrowinput"></td>
			<td><i18n:message key="storage.form.ftp.user.password"/></td>
			<td><input type="password" id="userPassword" name="userPassword" value="<c:out value="${storagedevice.userPassword}"/>" class="validate[maxSize[30]] narrowinput"></td>
		</tr>
		<tr>
			<td><i18n:message key="storage.form.note"/></td>
			<td colspan="3"><input type="text" id="notes" name="notes" value="<c:out value="${storagedevice.notes}"/>" class="validate[maxSize[250]] broadinput"></td>
		</tr>
		<tr>
			<td colspan="4" class="alignCenter">
			<input class="button" id="submitbutton" type="submit" value="<i18n:message key="storage.form.submit"/>"/>
			<input class="button" id="cancelbutton" type="button" value="<i18n:message key="storage.form.cancel"/>" onClick="parent.hideform();"/>
			<input class="button" id="testftpbutton" type="button" value="<i18n:message key="storage.test.title"/>" onClick="testftp()"/></td>
		</tr>
	</table>
	</form>
	<script type="text/javascript">
		$(document).ready(function(){
		   //创建文档类型字段验证
			$("#deviceform").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$("#submitbutton").attr("disabled",true);
						$("#cancelbutton").attr("disabled",true);
						$("#testftpbutton").attr("disabled",true);
						var invoke = $("#invoke").val();
						if(invoke == "add"){
							//验证设备名称是否重复
							$.ajax({
								url: "StorgeDeviceController.do?invoke=existStorage&deviceName=" + encodeURI($("#deviceName").val()),
								async: false,
								success: function(data) {
									if(data.toString().toLowerCase() =="true"){
										alert("<i18n:message key="storage.storageForm.sameName"/>");
										$("#submitbutton").removeAttr("disabled");
										$("#cancelbutton").removeAttr("disabled");
										$("#deviceName").focus();
										chnageType($("#deviceType").val());
									} else {
										deviceform.submit();
									}
								}	
							});
						} else {
							deviceform.submit();
						}
					}
				}
			});
		});
		function testftp(){
			 parent.testftp(deviceform.ftpDeviceURL.value,deviceform.userName.value,deviceform.userPassword.value,'test_ret_form');
		}
		function chnageType(type){
			parent.clearText();
			//type == 3为FTP测试连接显示，type=0是新增时默认为FTP，测试连接也显示
			if(type == 3 || type == 0){
				$("#testftpbutton").show();
			} else {
				$("#testftpbutton").hide();
			}
		}
	</script>
</body>
</html>
