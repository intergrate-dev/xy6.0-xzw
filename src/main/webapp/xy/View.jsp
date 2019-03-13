<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>

<head>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>查看</title>
    <meta content="IE=edge" http-equiv="X-UA-Compatible" />
    <script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
    <script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	
	<link rel="stylesheet" type="text/css" href="script/bootstrap/css/bootstrap.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="css/mainApp.css"/>
	
	<script type="text/javascript">
		var toolhint = {
			sure		:	"<i18n:message key="workspace.toolkit.hint.sure"/>",
			stateChange : 	"<i18n:message key="workspace.toolkit.error.stateChange"/>",
			allLocked : 	"<i18n:message key="workspace.toolkit.error.allLocked"/>",
			lockContinue : 	"<i18n:message key="workspace.toolkit.error.lockContinue"/>",
			exception : 	"<i18n:message key="workspace.toolkit.error.exception"/>"
		};
		var view_info = {
			docLibID : "${param.DocLibID}",
			docID : "${param.DocIDs}",
			fvID : "${param.FVID}"
		}
		function getFrameHeight(){
			var _winH = $(window).height();
			var _frameH = _winH - 70;
			
			$("#mobileFrame").height(_frameH);
        }
	</script>
</head>
<body onresize="getFrameHeight()" onload="getFrameHeight()">
	<iframe id="mobileFrame" frameborder="0" width="100%" height="90%" 
		src="article/Preview.do?ch=1&app=1&DocLibID=${param.DocLibID}&DocIDs=${param.DocIDs}"></iframe>
	<div id="main_toolbar">
		<div id="toolTable"><ul id="toolTR"></ul></div>
		<script language="javascript" type="text/javascript" src="script/ToolkitView.js"></script>
	</div>
</body>
</html>
