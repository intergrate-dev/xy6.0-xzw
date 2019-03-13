<%@include file="../e5include/IncludeTag.jsp"%>
<%
String appID = request.getParameter("appID");
%>
<i18n:bundle baseName="i18n.e5sysconfig" changeResponseLocale="false"/>
<html>
<head><title><i18n:message key="app.form.title"/></title>
<link type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../org/js/xmlhttps.js"></script>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script> 
<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
<Style type="text/css">
	.broadinput{
		width:360;
	}
	form{margin:0px;}
</Style>
</head>
<script type="text/javascript">
$(document).ready(function(){
	$("#createAppform").validationEngine({
		autoPositionUpdate:true,
		onValidationComplete:function(from,r){
			if(r){
				window.onbeforeunload=null;
				$.ajax({
					  type: "POST",
					  url: "../e5dom/DocTypeController.do?invoke=createApp&name=" + encodeURI($("#name").val()) + 
					  		"&absversion=" + encodeURI($("#absversion").val()) + 
					  		"&version=" + encodeURI($("#version").val()) + 
					  		"&provider=" + encodeURI($("#provider").val()),
					  dataType : "text",
					  beforeSend:function(){
						  //设置按钮不可用，防止重复提交
						  $("#dosubmit").attr("disabled", "true");
						  $("#docancel").attr("disabled", "true");
					  },
					  success: function(data, textStatus){
						  var dataStr = new String(data);
							if(data != null && data != "" && !(dataStr.indexOf("isExist")>=0)){
								window.parent.page.handlers.getAllApp("<%=appID %>");
								window.parent.e5.dialog.close("AddApp");
							} else{
								var message = "<i18n:message key="app.form.appname"/>" + 
										$("#name").val() + "+" + 
										"<i18n:message key="app.form.version"/>" + 
										$("#version").val() + 
										"<i18n:message key="app.alert.isExist"/>";
								alert(message);
								 $("#dosubmit").removeAttr("disabled");
								 $("#docancel").removeAttr("disabled");
							}
					  },
					  error: function(data){
						  //请求出错处理
						  alert("<i18n:message key="app.alert.error"/>" + "," + data);
						   $("#dosubmit").removeAttr("disabled");
						   $("#docancel").removeAttr("disabled");
					  }
				});
			}
		}
	});
});

</script>
<body>
    <form name="createAppform" id="createAppform" action="" method="post">
		<table cellpadding="4" cellspacing="0" border="0" class="table">
		  	<caption><i18n:message key="app.form.title"/></caption>
			<tr>
				<td class="bottomlinetd"><span style="color:red">*</span><i18n:message key="app.form.appname"/></td>
				<td class="bottomlinetd" colspan='2'><input type="text" id="name" name="name" style="width:250px" class="validate[required,maxSize[30]]"></td>
			</tr>
			<tr>
				<td class="bottomlinetd"><span style="color:red">*</span><i18n:message key="app.form.abbreviation"/></td>
				<td class="bottomlinetd" colspan='2'><input type="text" id="absversion" name="absversion" style="width:250px" class="validate[required,maxSize[30]]"></td>
			</tr>
			<tr>
				<td class="bottomlinetd"><span style="color:red">*</span><i18n:message key="app.form.version"/></td>
				<td class="bottomlinetd" colspan='2'><input type="text" id="version" name="version" style="width:250px" class="validate[required,maxSize[30]]"></td>
			</tr>
			<tr>
				<td class="bottomlinetd"><span style="color:red">*</span><i18n:message key="app.form.provider"/></td>
				<td class="bottomlinetd" colspan='2'><input type="text" id="provider" name="provider" style="width:250px" class="validate[required,maxSize[30]]"></td>
			</tr>
		</table>
		<br/>
		<center>
			<input class="button" type="submit" id="dosubmit" name="dosubmit" value="<i18n:message key="sysconfig.form.submit"/>">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input class="button" type="button" id="docancel" name="docancel" value="<i18n:message key="sysconfig.form.cancel"/>" onClick='window.parent.e5.dialog.close("AddApp");'>
		</center>
	</form>
</body>
</html>