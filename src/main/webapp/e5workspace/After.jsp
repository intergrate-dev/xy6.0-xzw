<%@include file="../e5include/IncludeTag.jsp"%>
<HTML>
<HEAD>
	<TITLE>AfterProcess</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript">
		function getOuterWindow(){
			var x = window;
			while (x.parent != null){
				if (x.name && x.name.indexOf("OpenartDialog") == 0) break;
				
				if (x == x.parent) break;
				x = x.parent;
			}
			return x;
		}
		function closeWindow(retValue, callMode){
			var opWnd = getOuterWindow();
			if (opWnd == null) opWnd = window;
			opWnd.callAP = true;
			
		    var parentWnd = (callMode == 1) ? opWnd.opener : opWnd.parent;
		    try {
				//对话框操作，若是iframe，则parent无法取到iframe这一级
				if (!parentWnd.e5 && parentWnd.frames.length > 0) {
					parentWnd = parentWnd.frames[0];
				}
				var tool = parentWnd.e5.mods["workspace.toolkit"];
				tool.self.closeOpDialog(retValue, callMode);
			}catch (e){
			}
			
			if (callMode == 1) opWnd.close();
		}
		function doClose() {
			var callMode = parseInt("<c:out value="${callMode}"/>");
			var docCount = parseInt("<c:out value="${docCount}"/>");
			var needRefresh = "<c:out value="${needRefresh}"/>";
			/**
			 * 若操作需要刷新，则调用刷新方法:
			 * 在DocList.js和Statusbar.js中同样定义refreshPage方法
			 * 则操作从工具栏启动和从文档列表右键菜单启动都可以正常返回刷新
			 */
			if (needRefresh && docCount > 0)
				closeWindow("OK", callMode);
			else
				closeWindow(null, callMode);
		}
	</script>
</HEAD>

<BODY onload="doClose()">
</BODY>
</HTML>
