
<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>

<body>
	<%@include file="inc/ResourcePaper.inc"%>
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
</body>
<%@include file="inc/MainFooter.inc"%>
<c:if test="${subTab.id == 'eparticle'}">
	<script type="text/javascript" src="script/tabledrag.js"></script>
</c:if>
<c:if test="${subTab.id == 'eplayout'}">
	<script type="text/javascript" src="script/griddrag.js"></script>
</c:if>