<script language="javascript" type="text/javascript" src="../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<div id="main_resourcetree" class="sidebar">
	<div class="div">
		<div id="divColSearch">
			<input id="colSearch" type="text" value="" title="请输入栏目名进行查询"/>
		</div>
	</div>
	<div id="rs_tree" class="ztree"></div>
</div>
<script language="javascript" type="text/javascript" src="column/script/tree.js"></script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script language="javascript" type="text/javascript" src="script/ResourceAd.js"></script>
<script>
//$(function() {
	//设置栏目树的参数
	col_tree.siteID = main_param.siteID;
	//col_tree.rootUrl = "colcache/Tree.do?op=1&ch=1";
	col_tree.rootUrl = "column/Tree.do?parentID=0&ch=1";
	col_tree.rootPath = "column/";

	//栏目树的点击事件对应到resource的消息发布
	var curNode = null;
	var colClick0 = function(event, treeId, treeNode, clickFlag) {
		if (treeNode)
			curNode = treeNode;
		
		var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
		var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
		if (!statusReady || !searchReady) {
			setTimeout(colClick0, 100);
			return;
		}
		var treeClick = e5.mods["workspace.resourcetree"].treeClick;
		treeClick(curNode);
	}
	col_tree.colClick0 = colClick0;
	
	col_tree.init();
//});

//广告库的导航区
e5.mod("workspace.resourcetree",function() {
	var api;
	var treeClick = function(treeNode) {
		if (treeNode.nocheck) return false;
		
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		param.colID = treeNode.id; //栏目ID
		param.ch = col_tree.ch; 	//渠道
		param.ruleFormula = "ad_columnID_EQ_" + param.colID;
		
		api.broadcast("resourceTopic", param);
	};
	
	var init = function(sandbox) {
			api = sandbox;
		}
	return {
		init: init,
		treeClick : treeClick
	}
});
</script>
