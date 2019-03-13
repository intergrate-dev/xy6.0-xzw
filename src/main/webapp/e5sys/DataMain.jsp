<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title><c:out value="${domInfo.name}"/></title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>

	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/ws-style.css"/>
	<style>#btnAdv{display:none;}</style>
</head>
<body>
	<div id="warpMain">
		<%@include file="../e5workspace/inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="../e5workspace/inc/Toolkit.inc"%>
				</div>
				<%@include file="../e5workspace/inc/Statusbar.inc"%>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
	e5.mod("workspace.resourcetree",function() {
		var api;
		var defaultClick = function() {
			var param = new ResourceParam();
			param.docTypeID = "<c:out value="${domInfo.docTypeID}"/>";
			param.docLibID = "<c:out value="${domInfo.docLibID}"/>";
			param.fvID = "<c:out value="${domInfo.folderID}"/>";
			param.ruleFormula = "<c:out value="${domInfo.rule}"/>";
			param.listID = "<c:out value="${domInfo.listID}"/>";
			param.queryID = "<c:out value="${domInfo.queryID}"/>";
			param.opFree = true;
			param.extParams = "<c:out value="${extParams}"/>";
			
			//e5.mods["workspace.doclist"].self.setExtParams(param.extParams);
			api.broadcast("resourceTopic", param);
		}

		//检查statusbar是否加载完毕，加载完毕才发送点击消息
		var checkLoad = function() {
			var statusReady = e5.mods["workspace.doclist"].isReady;
			var searchReady = e5.mods["workspace.search"].isReady;
			var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
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
<script type="text/javascript" src="../e5workspace/script/doclist.onresize-for-main.js"></script>
</html>
