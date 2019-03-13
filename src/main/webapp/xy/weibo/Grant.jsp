<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-COMPATIBLE" content="IE=edge">
	<title>微博账号授权</title>
	<style>
		body, button, input{
			font-family:"microsoft yahei";
			font-size : 14px;
		}
		.siteUrl{color:blue;}
		.code, .error{color:red;}
		.error{margin-bottom:50px;}
		
		.codeinput{width:400px; height:30px; inline-height:25px;}
		
		.goto, .nextStep{
			background: #00a0e6;
			color: #fff;
			border: none;
			display: inline-block;
			padding: 4px 12px;
		}
	</style>
	<script>
		var weibo_grant = {
			appKey : "${appKey}"
		}
		function goto() {
			//var redirect = "http://app.weibo.com/detail/5Mu4Cs"; //2wAB7a
			var redirect = "https://api.weibo.com/oauth2/default.html"; //2wAB7a
			var url = "https://api.weibo.com/oauth2/authorize"
				+ "?client_id=" + weibo_grant.appKey
				+ "&redirect_uri=" + redirect
				+ "&response_type=code";
			window.open(url);
		}
		
		function nextStep() {
			if (!document.getElementById("code").value) {
				alert("请先输入code参数值");
				return false;
			}
			var form = document.getElementById("form");
			form.submit();
		}
	</script>
</head>
<body>
	<c:if test="${error != null}">
		<p class="error">${error}</p>
	</c:if>
	
	<p>点击下面的按钮跳转到新浪微博的授权登录界面。</p>
	<p>
	授权界面登录后，页面会自动跳转到一个新页面，其地址类似于：<br/>
	<span class="siteUrl">http://app.weibo.com/detail/5Mu4Cs?code=</span><span class="code">9e82c2db1d2007174872d84472706b1c</span>
	</p>
	<p>
	请复制实际地址中的code（对应上面的<span class="code">红色</span>部分），填入下面的输入框，然后点击“继续授权”。
	</p>
	
	<p>
	<button onclick="goto()" class="goto">请先点这里开始授权</button>
	</p>
	
	<hr/>
	<form method="post" name="form" id="form">
		<p>
		请把code参数填入下面的页面，然后继续：
		</p>
		<p>
		<input type="text" name="code" id="code" class="codeinput" placeholder="请把code参数填在这里"/>
		<button onclick="return nextStep()" class="nextStep">继续授权</button>
		</p>
	</form>
	<p>
	授权成功，则页面关闭，否则会有红字声明错误。
	</p>
</body>
</html>