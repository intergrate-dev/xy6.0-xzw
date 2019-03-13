<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
<div id="divMainArea" class="mainArea">
	<script language="javascript" type="text/javascript" src="../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

	<div id="main_resourcetree" class="sidebar">
		<div id="rs_tree" class="ztree"></div>
	</div>
	<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
	<script language="javascript" type="text/javascript" src="script/ResourceOriginal.js"></script>
	<div id="wrapMain">
		<%@include file="inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/Toolkit.inc"%>
				</div>
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>
	</div>
</div>
<%@include file="inc/ReadMode.inc"%>
</body>
<%@include file="inc/MainFooter.inc"%>
