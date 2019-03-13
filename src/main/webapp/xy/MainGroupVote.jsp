<%@include file="../e5include/IncludeTag.jsp"%>
<%@include file="inc/MainHeader.inc"%>
<body>	
	<div id="wrapMain">
	    <input type="hidden" id="flag" name="flag" value="<%=request.getParameter("flag")%>" />
	    <input type="hidden" id="ch" name="ch" value="<%=request.getParameter("ch")%>" />
		<%@include file="inc/ChannelTabVote.inc"%>
		<%@include file="inc/Search.inc"%>
		<%@include file="inc/ResourceGroup.inc"%>
   		<%@include file="inc/ResourceChannelVote.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/Toolkit.inc"%>
				</div>
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>
	</div>
	<script type="text/javascript">
</script>
</body>
<%@include file="inc/MainFooter.inc"%>
<c:if test="${subTab.id == 'sext'}">
<script type="text/javascript" src="script/tabledrag.js"></script>
</c:if>
