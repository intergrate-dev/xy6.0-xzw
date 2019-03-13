<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<script type="text/javascript" src="../../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>
	<div class="div">
		<div id="divColSearch">
			<input  id="colSearch" type="text" value="" title="请输入栏目名进行查询" size="8"/>
		</div>
	</div>

<div id="rs_tree" class="ztree">
</div>
<div id="divColPuborApr" style="position:fixed;display:none; top: 20px; right: 35px; font-weight: bold; font-size: 14px;">
	<label id="labColPub" for="chkColPub"><input id="chkColPub" type="checkbox" value="" />直接发布</label>
	<label id="labColtoApr" for="chkColtoApr"> <input id="chkColtoApr" type="checkbox" value="" />直接送审</label>
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
	
	#colSearch {
		border-radius:3px; 
		border:1px solid #ccc;
		width: 122px;
  		height:24px;
  		padding-left: 5px;
  		margin-left: 5px;
  		margin-top: 5px;
	}
	#colSearchById{
		border-radius:3px; 
		border:1px solid #ccc;
		width: 122px;
  		height:24px;
  		padding-left: 5px;
  		margin-left: 5px;
  		margin-top: 5px;
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
<script language="javascript" type="text/javascript" src="script/originalTree.js"></script>
<script>
$(function() {
	ori_tree.init();
});
</script>