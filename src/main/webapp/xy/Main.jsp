
<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>

<body>
<div id="divMainArea" class="mainArea">
	<%@include file="inc/Resource.inc"%>
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

<script type="text/javascript" src="script/tabledrag.js"></script>
<script type="text/javascript" src="script/columnColor.js"></script>