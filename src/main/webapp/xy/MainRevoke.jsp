<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<style>
	#wrapMain{
		padding-left:0;
	}
</style>
<body>
	<div id="wrapMain">
		<%@include file="inc/ChannelTab.inc"%>
		<%@include file="inc/ResourceChannel.inc"%>
		
		<%@include file="inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/ToolkitRevoke.inc"%>
				</div>
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>
	</div>
</body>
<%@include file="inc/MainFooter.inc"%>

