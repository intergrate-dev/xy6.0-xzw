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


<div id="rMenu" style="left:0;">
	<ul id="menuRefresh"><a>刷新</a></ul>
	<ul id="menuView"><a>预览</a></ul>
	<ul id="menuDetails"><a>栏目详情</a></ul>
</div>
	<style>
		#rMenu  a {
			color: #646464;
			display: block;
			width:100%;
			padding-left:8px;
		}
		#rMenu > ul
		{
		  cursor: pointer;
		  margin: 0;
		  padding: 0;
		  width: 80px;
		  height: 30px;
		  line-height: 30px;
		}
		
		 #rMenu  a:hover{
			color: #fff;
			text-decoration: none;
			display: block;
			width:90%;
			background: linear-gradient(#0087cb, #007dbd);
		} 
       .btn-nav,#ztreeBtn button{
          font-size: 12px;
           letter-spacing: 2px;
           padding: 2px 12px;
       }
	</style>

<script language="javascript" type="text/javascript" src="column/script/originalTree.js"></script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script>
e5.mod("workspace.resourcetree", function() {
	var api;
	var treeClick = function(treeNode) {
		if (treeNode.nocheck)
			return false;

		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		param.groupID = treeNode.id;
		param.ruleFormula = "a_catID_EQ_" + treeNode.id + "_AND_" + main_param.ruleFormula;

		api.broadcast("resourceTopic", param);
	};

	var init = function(sandbox) {
		api = sandbox;
	}
	return {
		init : init,
		treeClick : treeClick
	}
});
//$(function() {
	//设置栏目树的参数
	ori_tree.siteID = main_param.siteID;
	ori_tree.rootUrl = "column/OrgTree.do?admin=1";
	ori_tree.rootPath = "column/";

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
	ori_tree.colClick0 = colClick0;
	
	ori_tree.init();
//});
</script>
