<!--<link rel="stylesheet" href="zTree/css/demoZtree.css" type="text/css">-->
<link rel="stylesheet" href="zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<style type="text/css">
	body{
		font-family: "microsoft yahei";
		font-size: 13px;
	}
	label{
		font-weight: 100;
		padding: 0;
		margin: 0;
	}
	#rs_tree{
		height: 594px !important;
		overflow-x: hidden;
		padding-bottom: 20px !important;
	}
</style>
<div id="main_resourcetree" class="sidebar" style="background: #f0f6e4;">
	
	<!--<hr class="hr" />-->
	<div id="rs_tree" class="ztree" style="margin-top: 16px !important;">
		<ul id="groupUl">
			<div class="content_wrap">
				<div class="zTreeDemoBackground left">
					<ul id="treeDemo" class="ztree" style="margin-top: 16px !important;"></ul>
				</div>
			</div>
		</ul>
	</div>
</div>
<script>
	main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";
	main_param["groupField"] = "<c:out value="${groupField}"/>";
	main_param["siteField"] = "<c:out value="${siteField}"/>";
</script>
<script language="javascript" type="text/javascript" src="script/ResourceGroup.js"></script>
