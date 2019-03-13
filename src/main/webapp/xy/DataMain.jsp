<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<title><%=com.founder.e5.workspace.ProcHelper.getProcName(request)%></title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
	<link rel="stylesheet" type="text/css" href="../xy/script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../xy/css/main.css">
</head>
<body>
	<div id="warpMain">
		<%@include file="../xy/inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea" <c:if test="${noOp == '1'}">style="display:none;"</c:if>>
					<%@include file="../xy/inc/Toolkit.inc"%>
				</div>
				<%@include file="../xy/inc/Statusbar0.inc"%>
			</div>
		</div>
	</div>
<%@include file="inc/MainFooter.inc"%>
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
			param.listID = "<c:out value="${domInfo.listIDs}"/>";
			if (!param.listID) {
				param.listID = "<c:out value="${domInfo.listID}"/>";
			}
			param.queryID = "<c:out value="${domInfo.queryID}"/>";
			param.opFree = true;
			param.extParams = "<c:out value="${extParams}"/>";
			param.colID = "${param.colID}"; //自定义栏目推荐模块时用。主界面传入的参数
			param.ch = "${param.ch}";
			param.groupID = "${param.groupID}"; //主界面可能传入的参数，备用
			
			if ("${param.siteID}")
				param.siteID = "${param.siteID}";
			
			api.broadcast("resourceTopic", param);
		}

		//检查statusbar是否加载完毕，加载完毕才发送点击消息
		var checkLoad = function() {
			var statusReady = e5.mods["workspace.doclist"].self;
			var searchReady = e5.mods["workspace.search"].isReady;
			var ready = !!statusReady&&!!searchReady&&searchReady();
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
	//订阅左窗口的列表选择事件，刷新右侧列表（用于自定义模块）
	e5.mod("workspace.leftListener",function() {
		var listening = function(msgName, callerId, param) {
			var frmRight = parent.frames["frmModuleRight"];
			if (!frmRight) return;
			
			var isRight = e5.utils.getParam("isRight");
			if (isRight) return;
			
			var ids = param.docIDs;
			var url = "about:blank";
			if (ids) {
				var id = (ids.split(","))[0];
				var curDocType = e5.utils.getParam("type");
				if (curDocType == "COLMODULE") {
					url = "../e5workspace/DataMain.do?isRight=1&type=COLMODULEITEM&rule=cmi_moduleID_EQ_" + id
						+ "&groupID=" + id;
				}
			}
			frmRight.location.href = url;
		}
		var init = function(sandbox) {
			api = sandbox;
			api.listen("workspace.doclist:doclistTopic", listening);
		}
		return {
			init: init
		}
	});
</script>
<script type="text/javascript" src="../xy/script/doclist.onresize-for-main.js"></script>
<c:if test="${param.type == 'COLMODULEITEM'}">
<script type="text/javascript" src="../xy/script/tabledrag.js"></script>
</c:if>

</html>
