<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title><i18n:message key="workspace.pwd.title"/></title>
	<link type="text/css" rel="stylesheet" href="../../e5style/work.css" />
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../script/security.js"></script>
	<script type="text/javascript" src="./ChangePwd.js"></script>
	<script type="text/javascript">
		var pwdInfo = {
			"ok" : "<i18n:message key="workspace.pwd.alert.ok"/>",
			"different" : "<i18n:message key="workspace.pwd.alert.different"/>",
			"nouser" : "<i18n:message key="workspace.pwd.alert.nouser"/>",
			"invalid" : "<i18n:message key="workspace.pwd.alert.invalid"/>",
			"newpwd" : "<i18n:message key="workspace.pwd.new.title"/>"
		}
	</script>
	<style>
		body, td, input{
			font-family: 'microsoft yahei';
		}
		#submitbtn{
			border-radius: 3px;
			color: #fff;
			background: #00a0e6;
			width: 352px;
			height: 50px;
			border: none;

			margin-top: 20px;
			font-size: 16px;
			font-weight: bold;
			letter-spacing: 2px;	
		}
		.oldPass,.newPass,.repeat{
			text-align: right;
			font-weight: bold;
			letter-spacing: 2px;
			padding-top: 7px;
		}
		#oldPwd,#newPwd,#newPwd2{
			width: 350px;
			height: 40px;
			border: 1px solid #ddd;
			margin-top: 10px;
			font-size: 14px;
		}
		.formError{
			top: -12px !importy;
		}
		#qrCodeBtn{
			border-radius: 3px;
			color: #fff;
			background: #00a0e6;
			width: 177px;
			height: 50px;
			border: none;

			font-size: 16px;
			font-weight: bold;
			letter-spacing: 2px;
		}

	</style>
</head>
<body>
<form id="form_id">
	<table>
	<tr>
		<td class="oldPass" width="100" height="35"><i18n:message key="workspace.pwd.old"/></td>
		<td><input type="password" name="oldPwd" id="oldPwd" 
			title="<i18n:message key="workspace.pwd.old.title"/>"
			placeholder="<i18n:message key="workspace.pwd.old.title"/>"
			maxlength="20" size="20" class="validate[required]"/></td>
	</tr>
	<tr>
		<td class="newPass"><i18n:message key="workspace.pwd.new"/></td>
		<td><input type="password" name="newPwd" id="newPwd" value=""
			title="<i18n:message key="workspace.pwd.new.title"/>"
			placeholder="<i18n:message key="workspace.pwd.new.title"/>"
			maxlength="20" size="20" class="validate[required]"/></td>
		<td style="color:gray;padding-left:5px;padding-top:8px;">密码中必须包含大写字母和小写字母，以及数字或者符号</td>
	</tr>
	<tr>
		<td class="repeat"><i18n:message key="workspace.pwd.new.again"/></td>
		<td><input type="password" name="newPwd2" id="newPwd2" value=""
			title="<i18n:message key="workspace.pwd.new.again.title"/>"
			placeholder="<i18n:message key="workspace.pwd.new.again.title"/>"
			maxlength="20" size="20" class="validate[required,equals[newPwd]]]"/></td>
	</tr>
	<tr>
		<td></td>
		<td><input type="button" id="submitbtn" class="button_three_1" value="<i18n:message key="workspace.ps.resTree.submit"/>" /></td>
	</tr>
	</table>

	<div id="qrCode" style="position:fixed;top: 20px; left: 550px;">
		<input type="button" id="qrCodeBtn" class="button_three_1" value="移动采编扫码登录" />
		<div id="qrCodeImgDiv" style="display:none;position:relative;top: 10px;">
			<img id="qrCodeImg" src="" />
		</div>
	</div>

</form>
</body>
</html>
