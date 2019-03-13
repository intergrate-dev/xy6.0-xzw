<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
<title>部门树</title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>
<style>
	.ztree {
		margin: 0;
		padding: 5px;
		color: #333;
		overflow:auto;
	}
	.ztree *{
		font-family: "微软雅黑";
		font-size:12px;
	}
	#rs_tree {
		min-height:250px;
	}
	#btnFormSave {
		border-radius: 3px;
		color: #fff;
		background: #00a0e6;
		height: 30px;
		border: none;
		font-size: 12px;
		cursor: pointer;
		text-shadow: none;
		padding: 0 27px;
		font-family: "microsoft yahei";
	}
	#btnFormCancel, .btnAdded1 {
		height: 30px;
		background: #b1b1b1;
		border: none;
		color: #fff;
		border-radius: 3px;
		margin-top: 5px;
		padding: 0 27px;
		text-shadow: none;
	}
</style>
</head>
<body style="overflow: hidden; margin-left: 6px;" id="tree" onresize="handlerRisize()" onload="handlerRisize()">
<div id="rs_tree" class="ztree"></div>
<div id="divOps" style="display:none;">
	<input class="button btn" id="btnFormSave" type="submit" value="保存">
	<input class="button btn" id="btnFormCancel" type="button" value="取消">
</div>
<script language="javascript" type="text/javascript" src="script/userOrgTree.js"></script>
<script>
	var sel = "${param.sel}";
	function handlerRisize(){
		if (sel) {
			$("#rs_tree").height(340);
		} else {
			var winH = $(window).height();
			$("#rs_tree").height(winH - 27);
		}
	}
	$(function() {
		if (sel) {
			$("#divOps").show();

			$("#btnFormSave").click(function() {
				var nodes = col_tree.tree.getSelectedNodes();
				parent.orgOK(nodes[0].id);
			});
			$("#btnFormCancel").click(function() {
				parent.orgCancel();
			});
		}
		
		col_tree.siteID = "${param.siteID}";
		col_tree.auth = "${param.auth}";
		
		col_tree.init();
	});
	
</script>
</body>
</html>