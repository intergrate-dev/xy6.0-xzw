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
	<div id="wrapMain">
		<%@include file="inc/SearchTipoff.inc"%>
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
<script type="text/javascript" src="../xy/script/StatusbarNis.js"></script>
<script>
    $(".channelTab").css("display","");

</script>
<%@include file="inc/MainFooter.inc"%>
