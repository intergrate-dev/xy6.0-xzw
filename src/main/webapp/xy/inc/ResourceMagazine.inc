<script language="javascript" type="text/javascript" src="../e5script/jquery/ztree/jquery.ztree.all-3.3.min.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/ztree/zTreeStyle/zTreeStyle.css"/>

<div id="main_resourcetree" class="sidebar">
	<div class="div-left">期刊</div>
	<hr class="hr" />
	<div id="rs_tree" class="ztree"></div>
</div>

<div id="rMenu" style="left:0;">
	<ul id="menuView"><a>查看</a></ul>
	<ul id="menuPublish"><a>发布</a></ul>
	<ul id="menuDelete"><a>删除</a></ul>
</div>
	<style>
		#rMenu  a{
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
		
	</style>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script language="javascript" type="text/javascript" src="script/ResourceMagazine.js"></script>
<script>
//$(function() {
	col_tree.isArticle = "${isArticle}";
	col_tree.init();
//});
</script>
