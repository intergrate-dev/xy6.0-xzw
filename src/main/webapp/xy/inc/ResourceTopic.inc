<script language="javascript" type="text/javascript" src="../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
<style>
	#rMenu  a {
		color: #646464;
		display: block;
		width:100%;
		padding-left:8px;
	}
	#rMenu > ul {
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
	#btnUpdate{
		cursor: pointer;
	}
	#rs_tree .edit, #rs_tree .remove, #rs_tree .add{
		display: none;
	}
	#rs_tree li span.add {
		background-position: -143px 0;
	}
	#rs_tree li span.button.ico_docu{
		background: url(../xy/img/home.png) 0 0 no-repeat !important;
	}
	/*自制弹窗样式*/
	.mark {
		display: none;
		z-index: 10000;
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}
	.mark2 {
		z-index: 9990;
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: #fff;
		opacity: 0;
	}
	.mark1 {
		z-index: 9995;
		position: absolute;
		top: 0;
		left: 387px;
		margin: auto;
		height: 129px;
		width: 450px;
		text-align: center;
		font-size: 16px;
		color: #000;
		background: #fefefe;
		border: 1px solid #A09FA3;
		border-radius: 3px;
	}
	.mark1 p{
		margin-top: 48px;
		font-size: 16px;
	}
	.mark1 button{
		position: absolute;
		right: 10px;
		bottom: 10px;
		width: 66px;
	    height: 26px;
	    font-size: 13px;
	    color: #fff;
	    letter-spacing: 1px;
	    background: #3385ff;
	    border: none;
	    border-bottom: 1px solid #2d78f4;
	    outline: medium;
	    -webkit-appearance: none;
	    -webkit-border-radius: 0;
	    cursor: pointer;
	}
</style>
<div id="main_resourcetree" class="sidebar">
	<div class="div">
		<span id="btnAdd" title="增加" class="icon-plus"></span>
		<span id="btnUpdate" title="修改"  class="icon-pencil"></span>
		<span  id="btnDelete" title="删除" class="icon-trash"></span>
	</div>
	<hr class="hr" />
	<div class="div">
		<div id="divColSearch">
			<input id="colSearch" type="text" value="" title="请输入栏目名进行查询"/>
		</div>
	</div>
	<div id="rs_tree" class="ztree"></div>
</div>


<div id="rMenu" style="left:0;">
	<ul id="menuRefresh"><a>刷新</a></ul>
	<!-- <ul id="menuView"><a>预览</a></ul> -->
	<!-- <ul id="menuDetails"><a>栏目详情</a></ul> -->
</div>
<script language="javascript" type="text/javascript" src="column/script/topicTree.js"></script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script language="javascript" type="text/javascript" src="column/script/topicManage.js"></script>
<script>
//分类类型ID：查询栏目稿件关联表时需要
//main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";

e5.mod("workspace.resourcetree", function() {
	var api;
	var isEnterPress = false;
	var treeClick = function(treeNode) {
		if (treeNode.nocheck)
			return false;

		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		param.groupID = treeNode.id;
		param.ruleFormula = "a_groupID_EQ_" + treeNode.id + "_AND_" + main_param.ruleFormula;

		main_param.colID = param.groupID; // 给columnColor.js使用

		api.broadcast("resourceTopic", param);
	};
	var btnUpdate = function() {
		$('#rs_tree .curSelectedNode').find('.edit').click();
	}
	var btnDelete = function() {
		$('#rs_tree .curSelectedNode').find('.remove').click();
	}
	var btnAdd = function() {
		// $('#rs_tree .curSelectedNode').find('.add').click();
		var zTree = topic_tree.tree;
		zTree.addNodes(null, -1, {name:"", id: ''});
		var nodes = zTree.getNodes();
		zTree.editName(nodes[nodes.length -1]);
	}
	var init = function(sandbox) {
		api = sandbox;

		$("#btnUpdate").click(btnUpdate);
		$("#btnDelete").click(btnDelete);
		$("#btnAdd").click(btnAdd);
	}
	return {
		init : init,
		treeClick : treeClick
	}
});
//$(function() {
	//设置栏目树的参数
	topic_tree.siteID = main_param.siteID;
	topic_tree.rootUrl = "column/TopicTree.do?admin=1";
	topic_tree.rootPath = "column/";

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
	topic_tree.colClick0 = colClick0;
	//拖拽
//----栏目管理：拖拽--------
	var topic_drag = {
		edit_setting : {
			enable : true,
			showRemoveBtn : false,
			showRenameBtn : false,
			drag : {
				isCopy : false
			}
		},
		colDrop0 : function(event, treeId, treeNodes, targetNode, moveType) {
			if (targetNode && targetNode.nocheck) {
			// 无权限，不允许操作
				return false;
			}
			var destID = 0;
			if(targetNode!=null){
				destID = targetNode.id;
			}
		
			var theURL = "./column/TopicDrag.do?srcID=" + treeNodes[0].id
					+ "&destID=" + destID
					+ "&moveType=" + moveType;
			$.ajax({url:theURL, async:false, success: function(data) {
				if (data == "ok") {
				} else {
					// alert("异常：" + data);
				}
			}});
		}
	}
    topic_tree.edit = topic_drag.edit_setting;
    topic_tree.colDrop0 = topic_drag.colDrop0;

	topic_tree.init();
//});
</script>
