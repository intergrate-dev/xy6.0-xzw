<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<%
String rootPath = request.getScheme() + "://" + request.getServerName() + 
	":" + request.getServerPort() + request.getContextPath() + "/";
%>
<head>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<title>详细信息</title>
    <meta content="IE=edge" http-equiv="X-UA-Compatible" />
    <script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
    <script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	
	<script type="text/javascript" src="script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		var frame;
		function doInit(){
			//$(document.body).bind("resize",function(){adjustIframeHOnce()});
			$("#tabs li a").on({
				"click":function(evt) {
					evt.preventDefault();
					var elm = $(evt.target);
					var type = elm.data("src");
					frame = $("#contentFrame");
					if (!!type) {
						frame.attr("src", type);
						elm.parent().addClass("active").siblings().removeClass("active");
					}
				}
			}).first().click();
			$(window).trigger("resize");
		}
		$(window).on({
			resize:function(){
				frame.css("height",document.documentElement.clientHeight - 110 +"px");
			},
			load:doInit
		});
		var currentDocID = "<c:out value="${DocIDs}"/>";
		var currentDocLibID = "<c:out value="${DocLibID}"/>";
		var currentFVID = "<c:out value="${FVID}"/>";
		var toolhint = {
			sure		:	"<i18n:message key="workspace.toolkit.hint.sure"/>",
			stateChange : 	"<i18n:message key="workspace.toolkit.error.stateChange"/>",
			allLocked : 	"<i18n:message key="workspace.toolkit.error.allLocked"/>",
			lockContinue : 	"<i18n:message key="workspace.toolkit.error.lockContinue"/>",
			exception : 	"<i18n:message key="workspace.toolkit.error.exception"/>",
			garbageRestore :"<i18n:message key="workspace.toolkit.garbagebin.restore"/>",
			garbageDelete : "<i18n:message key="workspace.toolkit.garbagebin.delete"/>",
			garbageLog : "<i18n:message key="workspace.toolkit.garbagebin.log"/>"
		};
		var approveType = "<c:out value="${param.approveType}"/>";
		//获取url地址中的参数
		function getUrlVars(name){
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
		    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
		    if (r != null) return unescape(r[2]); return null; //返回参数值
		}
		function goback(){   //返回到第一个历史页面
			var len = -(history.length-1);
			window.history.go(len);
		}
		$(function(){
			var url_doclibid = getUrlVars('DocLibID');  //获取url中的docLibID这个参数的值
			if(url_doclibid == 9){  //活动管理界面中的详情页面里面增加返回按钮。（推广、报名、签到、反馈管理）
				$("#goBack").append('<a href="javascript:;" onclick="goback()">返回</a>');
			}
		});
	</script>
	<script language="javascript" type="text/javascript" src="../amuc/script/ToolkitView.js"></script>

	<link rel="stylesheet" type="text/css" href="script/bootstrap/css/bootstrap.css"/>
	<link type="text/css" rel="stylesheet" href="css/main.css"/>
	<link type="text/css" rel="stylesheet" href="css/sub-page.css"/>
	<link type="text/css" rel="stylesheet" href="../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="css/DocView.css"/>
</head>
<body>
	<div class="wrap">
		<div id="goBack" class="goBack"></div>
		<!--操作-->
		<!-- <div id="main_toolbar">
			<div id="toolTable"><ul id="toolTR"></ul></div>
		</div> -->
		<!--tab-->
		<ul id="tabs" class="nav nav-tabs">
		<c:forEach var="tab" items="${tabs}" varStatus="index">
			<li><a href="#" data-src="<%=rootPath %><c:out value="${tab.url}"/>DocLibID=<c:out value="${DocLibID}"/>&DocIDs=<c:out value="${DocIDs}"/>"><c:out value="${tab.name}"/></a></li>
		</c:forEach>
		</ul>
		<!--iframe-->
		<iframe id="contentFrame" name="contentFrame" src="" frameborder="0" scrolling="auto" width="100%"></iframe>
	</div>
</body>
</html>
