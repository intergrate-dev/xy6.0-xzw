<BODY class="tabs">
	<%@include file="StatusbarMain.inc"%>
	<div class="listFillet" id="listFillet"><b class="L"></b><b class="R"></b></div>
</BODY>

<script type="text/javascript">
	//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
	e5.mod("workspace.search",function() {
		var api;
		var defaultClick = function() {
			var param = new SearchParam();
			param.docTypeID = "<c:out value="${docTypeID}"/>";
			param.docLibID = "<c:out value="${docLibID}"/>";
			param.fvID = "<c:out value="${folderID}"/>";
			param.ruleFormula = "<c:out value="${formula}"/>";
			param.listID = "<c:out value="${listID}"/>";
			
			api.broadcast("searchTopic", param);
		}
		//检查statusbar是否加载完毕，加载完毕才发送点击消息
		var checkLoad = function() {
			var statusReady = e5.mods["workspace.doclistMain"].isReady;
			var ready = !!statusReady && statusReady();
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