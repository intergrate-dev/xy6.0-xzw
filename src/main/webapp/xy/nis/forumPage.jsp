<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>论坛详情</title>
 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="css/jsp_share.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="js/jsp_forumpage.js"></script>
</head>
<body class="deta-share">
	<div class="wrap">
		<div class="header">
			<span><img id="headerimage" src="img/user.png" alt=""></span>
			<span class="user-name">
				<span class="username"></span>
				<span class="develop"></span><br>
				<span class="time"></span>
			</span>
		</div>
		<div class="news">
			<h2 class="newstitle"></h2>
			<p class="newscontent"></p>
			<!-- 主播图片列表 -->
			<div class="videoandpic">
				<ul id="mainimgdata"></ul>
				<div id="mainpic"></div>
			</div>
			
		</div>
		<div class="usr-say" pagesize="20">
		
		</div>
		<div class="showmore"></div>
</body>
</html>