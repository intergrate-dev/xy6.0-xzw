<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 

<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>
<div id="rs_tree" class="ztree">
</div>
<div id="divColBtn" style="position:fixed;bottom:0;right:20px;display:none;">
	<input class="btngroup" type='button' id="btnColOK" value='确定'/>
	<input class="btngroup" type='button' id="btnColCancel" value='取消'/>
</div>
<style>
	.ztree *{
		font-family: "微软雅黑";
		font-size:12px;
	}
	#rs_tree {
		min-height:250px;
	}
	.btngroup{
		margin:5px 5px;
		font-family: microsoft yahei; 
		color: #fff;
		border: none;
		background: #b1b1b1;
		border-radius: 3px;
		padding: 5px 20px;
		font-size: 12px;
	}
	#btnColOK{
	
		background: #00a0e6;
	}
</style>
<script language="javascript" type="text/javascript" src="script/tree.js"></script>
<script>
$(function() {
	col_tree.init();
});
</script>