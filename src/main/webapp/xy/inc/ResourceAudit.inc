<div id="main_resourcetree" class="sidebar">
	<div id="check" class="div">审核栏目</div>
	<ul class="columnlist ztree" id="rs_tree">
		<c:forEach var="nodes" items="${nodes}">
			<li class="column"
				nodeID="<c:out value="${nodes.ID}"/>"
			><c:out value="[${nodes.description}]${nodes.name}"/></li>
		</c:forEach>
	<c:forEach var="cols" items="${cols}">
		<li class="column"
			colID="<c:out value="${cols.id}"/>"
			channel="<c:out value="${cols.channel}"/>"
			><c:out value="${cols.casNames}"/></li>
	</c:forEach>

	</ul>
</div>

<script type="text/javascript">
	var main_channelLib = [
		{docLibID:"<c:out value="${channelLib0.docLibID}"/>", fvID:"<c:out value="${channelLib0.folderID}"/>"},
		{docLibID:"<c:out value="${channelLib1.docLibID}"/>", fvID:"<c:out value="${channelLib1.folderID}"/>"}
	]
</script>
<script type="text/javascript" src="./script/ResourceAudit.js"></script>


<style type="text/css">
	.columnlist{
		margin:0;
		list-style: none;
		text-align: left;
		text-indent:2em;
	}
	.columnlist li{
		cursor:pointer;
		padding:3px;
		margin-bottom:5px;
		font-size: 12px;
		color: #000;
	}
	.columnlist li.select{
		background-color:#00a0e6;
		color:#fff;
		font-family: "microsoft yahei";
		font-size:12px;
	}
	#check{
		text-align: left;
	    padding-left: 7px;
	    font-size: 15px;
	    font-weight: bold;
	    color: #999;
	    width: 153px;
	}
	

</style>