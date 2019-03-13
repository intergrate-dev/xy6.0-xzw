<HEAD>
	<TITLE>相关的列表</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript">
		var pathPrefix = "";
	</script>
	<link rel="stylesheet" type="text/css" href="../amuc/script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../amuc/css/query-custom.css"/>
	<link rel="stylesheet" type="text/css" href="../amuc/css/main.css">
</HEAD>
<BODY class="tabs">
	<div class="searchAndPage" id="searchAndPage">
	<!--===============搜索栏==================-->
	<%@include file="Search.inc"%>
	<!--===============状态条==================-->
	<%@include file="StatusbarMain.inc"%>
	</div>
	<div class="listFillet" id="listFillet"><b class="L"></b><b class="R"></b></div>
</BODY>

<script type="text/javascript">
	//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
	e5.mod("workspace.resourcetree",function() {
		var api;
		var defaultClick = function() {
			var param = new ResourceParam();
			param.docTypeID = "<c:out value="${docTypeID}"/>";
			param.docLibID = "<c:out value="${docLibID}"/>";
			param.fvID = "<c:out value="${folderID}"/>";
			param.ruleFormula = "<c:out value="${formula}"/>";
			param.listID = "<c:out value="${listID}"/>";
			param.hideFilters = true;
			
			api.broadcast("resourceTopic", param);
			
			$("#doclist").css("height","400px");
		}
		//检查statusbar是否加载完毕，加载完毕才发送点击消息
		var checkLoad = function() {
			var statusReady = e5.mods["workspace.doclistMain"].isReady;
			var searchReady = e5.mods["workspace.search"].isReady;
			var ready = !!statusReady&&!!searchReady&&statusReady()&&searchReady();
			if (!ready) {
				setTimeout(checkLoad, 100);
				return;
			}
			defaultClick();
		}
		var init = function(sandbox) {
			api = sandbox;
		},
		onload = function(){
			checkLoad();
		}
		return {
			init: init,
			onload:onload
		}
	});
</script>
<script type="text/javascript" src="../amuc/script/doclist.onresize-for-main.js"></script>
</HTML>
