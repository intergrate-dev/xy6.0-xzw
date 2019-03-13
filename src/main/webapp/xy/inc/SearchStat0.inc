<style type="text/css">
	#wrapMain{margin-left:0px; padding: 0;}
	.selfTab{width:70px;cursor:pointer;padding:3px;}
	#searchList,#queryReset{
		background: none;
		margin-top:5px;
		border: none;
	}
</style>
<div id="main_search" class="noQueryCust">
	<ul class="channels">
		<c:forEach var="ch" items="${channels}">
			<c:if test="${ch != null}">
			<li class="channelTab" rule="a_status_EQ_1_AND_a_siteID_EQ_<c:out value="${siteID}"/>"
				channel="<c:out value="${ch.id}"/>"><c:out value="${ch.code}"/>稿件</li>
			</c:if>
		</c:forEach>
	</ul>
	<!-- query -->
	<div id="searchBtnArea">
		<div class="btn-group searchListGroup">
			<div id="searchList" class="btn">
				<i class="icon-search"></i>
				<span id="toggleSearchAdvList" class="more"><i class="caret"></i></span>
			</div>
			
			<div id="queryReset" class="btn" title="<i18n:message key="workspace.query.reset.hint"/>">
				<i class="icon-repeat"></i>
			</div>
		</div>
	</div>
	<div id="divQueryCust"></div>

	<!-- filters -->
	<ul class="filters">
		<li>
			<select class="slct" name="Filters_0" id="Filters_0" style="display:none"title="<i18n:message key="workspace.status.filter.title"/>"></select>
		</li>
	</ul>
</div>
<script type="text/javascript">
	var self_param = {
	<c:forEach var="domInfo" items="${docLibs}" varStatus="varStatus">
		"<c:out value="${varStatus.index}"/>": {
			docLibID: "<c:out value="${domInfo.docLibID}"/>",
			fvID : "<c:out value="${domInfo.folderID}"/>",
			ch : "<c:out value="${varStatus.index}"/>"
		},
	</c:forEach>
		tail:null
	}
</script>
<script type="text/javascript" src="script/ResourceSelf.js"></script>
<script type="text/javascript" src="script/Search.js"></script>
