<%@ include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
	<title>导入（结果）</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
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
			//if (callMode == 1) opWnd.close();
		}
		function doClose() {
			var callMode = parseInt("<c:out value="${callMode}"/>");
			var needRefresh = "<c:out value="${needRefresh}"/>";
			/**
			 * 若操作需要刷新，则调用刷新方法:
			 * 在DocList.js和Statusbar.js中同样定义refreshPage方法
			 * 则操作从工具栏启动和从文档列表右键菜单启动都可以正常返回刷新
			 */
			if (needRefresh)
				closeWindow("OK", callMode);
			else
				closeWindow(null, callMode);
		}
	</script>
</head>
<body onload="doClose();">
	<div>成功导入的个数：<c:out value="${result.success}"/></div>
	<div>无法导入的个数：<c:out value="${result.error}"/></div>
	<c:if test="${result.error > 0}">
	<div>无法导入的原因（请参照原因修改，再单独导入）：<br/>
	<%=((com.founder.amuc.member.input.ImportMsg)request.getAttribute("result")).getMessage()%>
	</div>
	</c:if>
</body>
</html>