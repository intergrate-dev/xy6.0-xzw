<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<style>
	#wrapMain{
		padding-left:0;
	}
</style>
<script>
setTimeout(function(){
	$("#Search").hide();},100);
</script>
<%@include file="inc/MainHeader.inc"%>
<body>
	<div id="wrapMain">
		<%@include file="inc/ChannelTab2.inc"%>
		<%@include file="inc/ResourceChannel2.inc"%>
		<%-- <span id="ResourceGroup"><%@include file="inc/ResourceGroup.inc"%></span> --%>
		
		<span id="Search"><%@include file="inc/Search.inc"%></span>
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

