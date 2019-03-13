<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/e5.js"></script>
		<script type="text/javascript" src="SkinSwitch.js"></script>
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/work.css" />
	</head>
	<body>
		<div class="skin" id="skin">
			<a href="#" skin-data="blue">
				<img src="../../images/skin-blue.png" alt="<i18n:message key="workspace.skin.blue"/>"/>
				<span>blue</span>
			</a>
			<a href="#" skin-data="green">
				<img src="../../images/skin-green.png" alt="<i18n:message key="workspace.skin.green"/>"/>
				<span>green</span>
			</a>
			<a href="#" skin-data="simple">
				<img src="../../images/skin-simple.png" alt="<i18n:message key="workspace.skin.simple"/>"/>
				<span>simple</span>
			</a>
		</div>
		<div style="text-align:center;padding-top:10px;">
			<button type="button" id="save"><i18n:message key="workspace.ps.resTree.submit"/></button>
			<!--<button type="button" id="cancel">cancel</button>-->
		</div>
	</body>
</html>
