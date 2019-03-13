<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>源稿栏目管理树</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
		#rMenu  a{
			color: #646464;
			display: block;
			width:100%;
			padding-left:8px;
		}
		#rMenu > ul
		{
		  cursor: pointer;
		  margin: 0;
		  padding: 0;
		  width:105px;
		  height: 30px;
		  line-height: 30px;
		}
		
		#rMenu ul a:hover{
			color: #fff;
			text-decoration: none;
			display: block;
			width:93%;
			background: linear-gradient(#0087cb, #007dbd);
		}
		#tree .channels{
			padding: 0;
		}
		.ztree {
			margin: 0;
			padding: 5px;
			color: #333;
			overflow:auto;
		}
	</style>
	<script>
		function handlerRisize(){
			var winH = $(window).height();
			var chs = $(".channels");
			if (chs.css("display") == "none")
				$("#rs_tree").height(winH - 61);
			else {
				$("#rs_tree").height(winH - 99);
			}
		}	
	</script>
</head>
<body style="overflow: hidden; margin-left: 6px;" id="tree" onresize="handlerRisize()" onload="handlerRisize()">
	<%@include file="TreeOriginal.inc"%>
	
	<div id="rMenu" style="left:0;display:none;">
		<ul style="padding: 0;" id="menuAdd"><a>增加分类</a></ul>
		<ul style="padding: 0;" id="menuAddBat"><a>批量增加分类</a></ul>
		<ul style="padding: 0;" id="menuDelete"><a>删除分类</a></ul>
		<!-- <ul style="padding: 0;" id="menuCopy"><a>复制</a></ul> -->
		
		<!-- <ul style="padding: 0;" id="menuUserOp"><a>设置操作权限</a></ul> -->
		<!-- <ul style="padding: 0;" id="menuUserAdmin"><a>设置管理权限</a></ul> -->
		
		<!-- <ul style="padding: 0;" id="menuLog"><a>日志</a></ul> -->
		
		<ul style="padding: 0;" id="menuRoot"><a>增加根分类</a></ul>
		
		<ul style="padding: 0;" id="menuRefresh"><a>刷新</a></ul>
		<!-- <ul style="padding: 0;" id="menuGarbage"><a>回收站</a></ul> -->
	</div>
	<script language="javascript" type="text/javascript" src="script/originalManage.js"></script>
	
	<script>
		//设置siteID、并定义点击时在右窗口显示栏目
		ori_tree.siteID = parent.parent.headerinfo.siteID;
		ori_tree.rootUrl = "OrgTree.do?admin=1";
		
		ori_tree.colClick0 = ori_menu.colClick0;
		
		//拖拽
		ori_tree.edit = ori_drag.edit_setting;
		ori_tree.colDrop0 = ori_drag.colDrop0;
	</script>
</body>
</html>