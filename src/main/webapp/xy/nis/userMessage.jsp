<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html lang="en">
<head>
	<meta charset="UTF-8" />
	<title>Document</title>
	<link rel="stylesheet" type="text/css" href="css/userMessage.css"/>
    <style type="text/css">
        #message li span:first-child{
            text-align: left;
        }
    </style>
	<script src="../special/third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
</head>
<body>
	<ul id="message" style="margin:10px 0 0 10px;">
		<li><span>用户头像：</span><span id="head">
			<img src=${head} />
		</span></li>
		<!--<li><span>地点：</span><span id="position">华盛顿</span></li>-->
		<li><span>地区：</span><span id="regionId">
			${region}
		</span></li>
		<li><span>生日：</span><span id="birthdayId">
			${birthday}
		</span></li>
		<li><span>性别：</span><span id="sexId">
			${sex}
		</span></li>
		<li><span>地址：</span><span id="addressId">
			${address}
		</span></li>
		<li><span>邮箱：</span><span id="emailId">
			${email}
		</span></li>
		<li><span>昵称：</span><span id="nicknameId">
			${nickname}
		</span></li>
		<li><span>用户名：</span><span id="nameId">
			${name}
		</span></li>
		<li><span>手机号码：</span><span id="mobileId">
			${mobile}
		</span></li>
		<li><span>公司：</span><span id="orgId">
			${org}<%--<input id="org" type="text" name="Org" value="${org}" >--%>
		</span></li>
		<li>
			<input type="button" id="btn-cancle" value="关闭" onClick="doCancel()" style="width: 122px;height: 22px;margin: 10px 0 0 151px;"/>
		</li>
		<input id="rename-uuid" type="hidden" name="UUID" value="${UUID}">
		
		
	</ul>
</body>
<script type="text/javascript">
	function doCancel() {
		window.onbeforeunload = null;

		$("#btn-confirm").disabled = true;
		$("#btn-cancle").disabled = true;

		beforeExit();
	};

	function beforeExit() {
		var uuid = $("#rename-uuid").val();
		var dataUrl = "../../e5workspace/after.do?UUID=" + uuid;

		window.location.href = dataUrl;
	}
</script>
</html>