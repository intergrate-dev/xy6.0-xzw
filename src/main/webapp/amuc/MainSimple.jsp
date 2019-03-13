<style>
	#searchList,#queryReset{
		background: none;
	}
	#rule_name{
		  height: 23px;
	}
	
</style>
<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
	<%@include file="inc/ResourceSimple.inc"%>
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
<c:if test="${subTab.id == 'nistopic'}">
<script type="text/javascript" src="script/tabledrag.js"></script>
</c:if>
<c:if test="${subTab.id == 'suser'}">
	<script type="text/javascript" src="script/tabledrag.js"></script>

<script type="text/javascript" src="script/userColor.js"></script>
</c:if>