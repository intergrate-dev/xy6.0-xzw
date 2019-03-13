<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<!doctype html>
<html lang="en">
<head>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title><%=com.founder.e5.context.Context.getSystemName()%></title>
	<link rel="stylesheet" type="text/css" href="../e5style/appLogin.css"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"  />
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="./script/login.js"></script>
	<script type="text/javascript">
		var headerinfo = {
			nouser : "<i18n:message key="workspace.login.nouser"/>",
			norole : "<i18n:message key="workspace.login.norole"/>",
			expired:"<i18n:message key="workspace.login.expired"/>",
			otheragain : "<i18n:message key="workspace.login.otheragain"/>",
			haserror : "<i18n:message key="workspace.login.haserror"/>",
			frozen : "<i18n:message key="workspace.login.hasfrozen"/>",
			sessionlose : "<i18n:message key="workspace.login.nossioncaptcha"/>",
			errorcaptcha : "<i18n:message key="workspace.login.errorcaptcha"/>",
			errorpubkey : "<i18n:message key="workspace.login.pwdencrypterror"/>",
			passwordwrong : "<i18n:message key="workspace.login.nouser"/>",

			captchaEnabled : true,
			app:true
		}
	</script>
	<!--[if IE]>
		<style type="text/css">
		.mark {
		background:transparent;
		filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#c80e92cb,endColorstr=#c80e92cb);
		zoom: 1;
		}
		.submit{
		background:transparent;
		filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#E540a3cd,endColorstr=#E540a3cd);
		zoom: 1;
		}
		.login .user{
		background:transparent;
		filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#000e92cb,endColorstr=#000e92cb);
		zoom: 1;
		}
		</style>
	<![endif]-->
	<style type="text/css">
		text::-moz-focus-inner
		input[type="password"]::-moz-focus-inner{
			border: none;
		}
	</style>
</head>
<body>
	
	<div class="mark"></div>
	<div class="login">
		<img class="logo" src="../images/app02.png" alt="" />
		<form action="" method="post">
			<ul>
				<li>
					<img class="icon app03" src="../images/app03.png" alt="" />
					<input class="user usrName" type="text" name="UserCode" id="UserCode" value="" required="required" placeholder="<i18n:message key="workspace.login.user"/>" />
				</li>
				<li>
					<img class="icon app04" src="../images/app04.png" alt="" />
					<input class="user passWord" type="password" name="UserPassword" id="UserPassword"  
						value="" autocomplete="off"
						required="required" placeholder="<i18n:message key="workspace.login.password"/>" />
				</li>
				<li>
					<img class="icon app05" src="../images/app05.png" alt="" />
					<!--<label for="inputcode" accesskey="c"><i18n:message key="workspace.login.captcha"/></label>-->
					<input type="text" id="inputcode" class="user code">
					
					<img id="randImg" src="../e5workspace/security/captcha.do"/>
					<a href="javascript:randImgFresh()" class="change"><img class="" src="../images/app06.png" alt="" /><!--<i18n:message key="workspace.login.captchaobscure"/>--></a>
				</li>
			</ul>
			<div class="keep">
			</div>
			<input class="submit" id="login-btn" type="button" value="确定"/>
		</form>
	</div>
</body>
</html>