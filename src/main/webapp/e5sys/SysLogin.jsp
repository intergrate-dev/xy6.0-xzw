<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysui" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<%
if ("1".equals(request.getParameter("enabled")))
{
	session.setAttribute("sysmenuenabled", new Boolean(true));
}
%>
<html>
<head>
	<title><i18n:message key="sysui.login.title"/></title>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
	<script type="text/javascript" src="../e5script/Function.js"></script>
	<script type="text/javascript" src="login.js"></script>
	<script type="text/javascript">
		var WarningInfos =
			[
				"<i18n:message key="sysui.login.invaliduser"/>",
				"<i18n:message key="sysui.login.nouser"/>",
				"<i18n:message key="sysui.login.norole"/>",
				"<i18n:message key="sysui.login.noadminrole"/>",
				"<i18n:message key="sysui.login.invalidpassword"/>",
				"<i18n:message key="sysui.login.loginagain"/>",
				"<i18n:message key="sysui.login.otheragain"/>",
				"<i18n:message key="sysui.login.haserror"/>"
			];
		var headerinfo = {
			captchaEnabled : false,
			errorcaptcha : "验证码错误",
			sessionlose : "会话过期",
			errordynamiccode : "动态口令错误",
			nodynamiccode : "请输入数字动态口令"
		}
		$(function(){
			var _body = $("body"),
				loginBg = $("#login-bg");
			$("#login-btn").click(doAction);
			$("#UserCode").focus(focusHandle).blur(blurHandle).focus();
			$("#UserPassword").focus(focusHandle).blur(blurHandle);
			$(document).keydown(function(event){
				if(event.target.getAttribute("id") !== "login-btn" && event.keyCode == 13){
					doAction();
				}
			})
			_body.resize(resizeHandle);
			resizeHandle();
			function focusHandle(){
				$(this).parent().addClass("focus hasT");
			}
			function blurHandle(){
				var self = $(this),
					parent = self.parent();
				if(!self.attr("value")){
					parent.removeClass("focus hasT");
				}
			}
			function resizeHandle(){
				var h = _body.data("resize-special-event").h,
					w = _body.data("resize-special-event").w;
				if(loginBg.height()<h){
					loginBg.css({
							"width":"auto",
							"height":"100%"
						}).parent().css({
							"left":(w-loginBg.width())/2
						});
				}
				if(loginBg.width()<w){
					loginBg.css({
							"width":"100%",
							"height":"auto"
						}).parent().css({
							"top":(h-loginBg.height())/2
						});
				}
			}
			
			//for captcha
			if (!headerinfo["captchaEnabled"]) {
				$.ajax({url:"../e5workspace/security/securitypolicy.do", dataType:"json",async:false, 
					success:function(data) {
					if (data){
						$.each(data,function(i,n){
							if (n.captchaEnabled) {
								headerinfo.captchaEnabled = true;
								$(".security_class").css("display","block");
								$("#randImg").attr("src","../e5workspace/security/captcha.do");
							}
							if (n.transferEncrypt) {
								headerinfo.transferEncrypt = true;
							}
							//if (n.dynamicCodeEnabled) {
								headerinfo.dynamicCodeEnabled = true;
								$(".dynamic_class").css("display","block");
							//}
						});
					}
				}});
			}
			

		});
	</script>
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<!--<link type="text/css" rel="stylesheet" href="../e5style/e5sys-login.css"/>-->
	<link type="text/css" rel="stylesheet" href="../e5style/login1.css"/>
	<!--[if IE]>
		<style type="text/css">
		.basic {
		background:transparent;
		filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#c83da0d3,endColorstr=#c83da0d3);
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
	
</head>
<body>
		<img class="backimg" src="../images/05.png"/>
		<div class="basic login">
			<img class="logo" src="../images/06.png" alt="" />
			<form action="" method="post">
				<ul>
					<li>
						<img class="icon" src="../images/03.png" alt="" />
						<input class="user usrName" type="text"  name="UserCode" id="UserCode" value="" required="required" autofocus="autofocus" />
					</li>
					<li>
						<img class="icon" src="../images/04.png" alt="" />
						<input class="user passWord" type="password" name="UserPassword" id="UserPassword" value=""  />
					</li>
					<li class="security_class" style="display:none;">
					<img class="icon app05" src="../images/app08.png" alt="" />
					<!--<label for="inputcode" accesskey="c"><i18n:message key="workspace.login.captcha"/></label>-->
					<input type="text" id="inputcode" class="user code" style="width:43%;">
					
					<img id="randImg" src="../e5workspace/security/captcha.do"/>
					<a href="javascript:randImgFresh()" class="change"><img style="width: 30px; float: right; margin-top: 16px;" class="" src="../images/app07.png" alt="" /><!--<i18n:message key="workspace.login.captchaobscure"/>--></a>
					</li>
					<li  class="dynamic_class" style="display:none;">
						<img class="icon app05" src="../images/menu/004.png" alt="" />
						<input class="user code" type="text" id="DynamicCode" name="DynamicCode" value="" required="required" placeholder="动态口令"  style="width:43%;"/>
					</li>
				</ul>
				
				<!--<div class="keep">
					<input type="checkbox" name="" id="" value="" />
					<span>记住密码</span>
				</div>-->
				<input id="login-btn" style="margin-top: 80px;" class="submit" type="button" value="<i18n:message key="sysui.login.submit"/>"/>
			</form>
		</div>
		<div id="role" name="role" class="role" style="display:none">
			<label><i18n:message key="sysui.login.role"/></label>
			<select id="RoleID" name="RoleID">
			</select>
		</div>
		<input type="hidden" id="Super" name="Super" value="-1">
		<input type="hidden" id="UserID" name="UserID" value="">
		<input type="hidden" id="UserName" name="UserName" value="">
	
</html>
