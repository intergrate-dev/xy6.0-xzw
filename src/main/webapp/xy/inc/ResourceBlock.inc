<%@ page pageEncoding="UTF-8"%>
<script language="javascript" type="text/javascript" src="../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<div id="main_resourcetree" class="sidebar">
	<div class="div">
		<input class="input ac_input" id="blockSearch" placeholder="查找" type="text" value="" title="请输入页面区块名进行查询" size="16"/>
	</div>
	<div id="rs_tree" class="ztree"></div>
</div>

<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script language="javascript" type="text/javascript" src="script/ResourceBlock.js"></script>
<script language="javascript" type="text/javascript" src="../xy/block/script/tree.js"></script>
<script>
	main_param["blockIDField"] = "<c:out value="${blockIDField}"/>";
	//设置页面区块树的参数
	b_tree.siteID = main_param.siteID;
	b_tree.rootUrl = "../xy/block/Block.do?Op=1";
	b_tree.rootFindUrl = "../xy/block/Find.do?siteID=";
	b_tree.check.enable = false;
	
	//页面区块树的点击事件对应到resource的消息发布
	var curNode = null;
	var blockClick0 = function(event, treeId, treeNode, clickFlag) {
		if (treeNode)
			curNode = treeNode;

		var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
		var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
		if (!statusReady || !searchReady) {
			setTimeout(blockClick0, 100);
			return;
		}
		
		var treeClick = e5.mods["workspace.resourcetree"].treeClick;
		treeClick(curNode);
	}
	b_tree.blockClick0 = blockClick0;
	
	function searchClick(curNode){
		var click = e5.mods["workspace.resourcetree"].treeClick;
		click(curNode);
	}

	//第一次找不到ztree，再做一次。待查原因
	b_tree.init();
</script>
<style type="text/css">
	.div{
		height: 45px; 
		line-height: 53px;
	}
	.input{
		width: 150px;
	}
	
</style>