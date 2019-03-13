<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>直播详情</title>
 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" type="text/css" href="css/jsp_share.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="js/jsp_livepage.js"></script>
	<script type="text/javascript">
		
		$(document).ready(function(){	
			$(".articleOpen").click(function(){
				$(this).toggle();
				$("#close").toggle();
				$("#showdiv").css("display","block")
			})
			$("#close").click(function(){
				$(this).toggle();
				$(".articleOpen").toggle();
				$("#showdiv").css("display","none")
				
			})
			$(".close").click(function(){
				$(".uploadtip").hide();
			})
			if($(".img001").is(":visible")){
// 				$(".header-img").css("width","32%")
				$(".header-img").css("width","95px")
				$(".header-img").css("height","95px")
				$(".header-img").css("border-radius","50%")
				$(".user-infor-name span").css("color","#fff")
				$("#user").css("margin-top","10%")	
				
			}
			else
			{
				$(".user-infor-name").css({
					"position":"static",
					"height":"50px",
					"margin-left":"3%",
					"margin-top":"1%",
					"margin-bottom":"1%"
					
				})
			}
			
			
			
		})
	</script>
</head>
<body class="sh-det-share">
		<div class="banner">
		<img class="img001"  alt="" />
			<div class="user-infor">
				<span class="user-infor-name">
					<img class="header-img" src="" alt="" />
					<span class="right">
						<span id="user"></span>
						<img class="timekeeper" src="img/timekeeper.png" alt="">
						<span id="publishtime"></span>
					</span>					
				</span>
			</div>
		</div>
		<h2 id="title"></h2>
		<div style="clear: both;"></div>
		<div class="right open">
			 <div class="showlist">
			 	<div class="showarticle">
					<span class="articleOpen" style="display:none;">
			 			<span>展开</span>
						<a href="#" class="icon top-icon"></a>
					</span>
					<span  id="close">
						
						<span>收起</span>	
			 			<a href="#" class="icon bottom-icon"></a>
					</span>
			 	</div>
			 	<div style="clear: both;"></div>
				 <div id="showdiv">
				 	<div class="article">
				 		<p id="class1content"></p>
						<!-- 主播图片列表 -->
						<ul id="mainimgdata">
						</ul>
						<div id="mainpic">
						</div>
					</div>
				 </div>
			 </div>
			 
		</div>
		<div style="clear:both"></div>
		<div class="container" id="context" pagesize="20">
			<!--头像   使用js拼写填充至此   create by wanghuaying 2015年9月12日 -->
			<!-- 跟帖列表    使用js拼写填充至此create by wanghuaying 2015年9月12日 -->
		</div>
		<div class="more"></div>
		
		
		<div id="modal-overlay" >
				<img id="maximg" src=""/>
    	</div>
    	
    	<div id="screen" onclick="cancelMaxPics()">
    	</div>
</div>
</body>
</html>