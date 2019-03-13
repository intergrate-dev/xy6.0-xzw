<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

<div class="divBlockSearch">
	<input id="blockSearch" type="text" value="" title="请输入页面区块名进行查询" size="16"/>
</div>

<div id="rs_tree" class="ztree" style="min-height:300px;">
</div>

<div id="divBlockBtn" style="position:fixed;bottom:0;right:20px;display:none;">
	<input type='button' id="btnBlockOK" value='确定'/>
	<input type='button' id="btnBlockCancel" value='取消'/>
</div>
<style>
	.ztree *{
		font-family: "微软雅黑";
		font-size:12px;
		color: #000;
	}
	#blockSearch {
		border-radius:3px; 
		border:1px solid #ccc;
		width: 122px;
  		height:24px;
  		padding-left: 5px;
  		margin-left: 5px;
  		margin-top: 5px;
	}
	#divBlockSearch {
		padding-bottom:5px;
	}
	#rs_tree {
		min-height:200px;
	}
	#btnBlockCancel,#btnBlockOK{
		margin:5px 5px;
		font-family: microsoft yahei; 
		color: #fff;
		border: none;
		background: #b1b1b1;
		border-radius: 3px;
		width: 55px;
		height: 25px;
		font-size: 12px;
	}
	#btnBlockOK{
		margin:5px 5px 5px 10px;
		background: #00a0e6;
	}
	
</style>
<script language="javascript" type="text/javascript" src="../../xy/block/script/tree.js"></script>
