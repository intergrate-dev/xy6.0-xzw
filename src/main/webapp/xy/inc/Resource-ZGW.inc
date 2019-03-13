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
<c:if test="${ch==0}">
	 <div id="colFavorite">
		<ul><li id="column_coll_id" style="list-style:none;color:#000000" name="personalColl"  ><button type="button " class="btn  btn-sm btn-block btn-nav" onclick="favoriteDiv.showMyColl()">收藏夹</button></li></ul>
     </div>
    <div id="MyFavorite" style="display:none;">
       <ul id="MyFavorite_ul" style="margin:0px;padding:0px;">
       </ul>
    </div>
    <div class="ztreeBtn-div">
        <ul><li id="ztreeBtn" style="list-style:none;color:#000000" name="ztreeBtn"  ><button type="button " class="btn  btn-sm btn-block" onclick="favoriteDiv.showZtree()">栏目树</button></li></ul>
    </div>
	<div id="rs_tree" class="ztree" style="margin-top: 9px !important;"></div>
</c:if>
<c:if test="${ch==1}">
	<div id="rs_tree" class="ztree"></div>
</c:if>
</div>


<div id="rMenu" style="left:0;">
	<ul id="menuRefresh"><a>刷新</a></ul>
	<ul id="menuView"><a>预览</a></ul>
	<ul id="menuDetails"><a>栏目详情</a></ul>
	<ul id="menuPub"><a>按栏目发布</a></ul>
	<ul id="menuPubCol"><a>重发栏目页</a></ul>
	<ul id="menuAddToColl"><a>添加到收藏夹</a></ul>
</div>

<div id="rMenu-col">
	<ul id="menuView-col"><a>预览</a></ul>
	<ul id="menuDetails-col"><a>栏目详情</a></ul>
	<ul id="menuPub-col"><a>按栏目发布</a></ul>
	<ul id="menuPubCol-col"><a>重发栏目页</a></ul>
	<ul id="menuDelFromColl-col"><a>移出收藏夹</a></ul>
</div>
	<style>
		#rMenu  a ,#rMenu-col  a{
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
		#rMenu-col{
		left:0;
		top:0;
		display:none;
		position:absolute;
		z-index:9999;
		background:#fff;
		border:1px solid #eee;
		
		}
		#rMenu-col > ul
		{
		  cursor: pointer;
		  margin: 0;
		  padding: 0;
		  font-size: 12px;
		  height: 30px;
		  line-height: 30px;
		}
		#rMenu-col  a:hover{
			color: #fff;
			text-decoration: none;
			display: block;
			width:90%;
			background: linear-gradient(#0087cb, #007dbd);
		}
        #MyFavorite_ul li{
            list-style: none;
            text-align: left;
            margin: 6px 9px;
            cursor: pointer;
        }
        #MyFavorite_ul{
            border-bottom: 1px solid #ddd;
        }
       .btn-nav,#ztreeBtn button{
          font-size: 12px;
           letter-spacing: 2px;
           padding: 2px 12px;
       }
        #colFavorite ul,.ztreeBtn-div ul{
            margin: 5px 0 0 0;
        }


	</style>

<script language="javascript" type="text/javascript" src="column/script/tree.js"></script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script language="javascript" type="text/javascript" src="script/Resource.js"></script>
<script language="javascript" type="text/javascript" src="script/myFavorite.js"></script>
<script>
	//分类类型ID：查询栏目稿件关联表时需要
	main_param["catTypeID"] = "<c:out value="${catTypeID}"/>";
	
//$(function() {
	//设置栏目树的参数
	col_tree.ch = "<c:out value="${ch}"/>";
	col_tree.siteID = main_param.siteID;
	col_tree.rootUrl = "colcache/Tree.do?op=1&ch=" + col_tree.ch;
	col_tree.rootPath = "colcache/";

	//栏目树的点击事件对应到resource的消息发布
	var curNode = null;
	var _clicked;
	var colClick0 = function(event, treeId, treeNode, clickFlag, isFirstClick) {
		if (treeNode)
			curNode = treeNode;
		if (isFirstClick)
		    _clicked = isFirstClick;
		if (isFirstClick==false && _clicked==true)
		    _clicked = isFirstClick;
		var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
		var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
		if (!statusReady || !searchReady) {
			setTimeout(colClick0, 100);
			return;
		}
		
		var treeClick = e5.mods["workspace.resourcetree"].treeClick;
		treeClick(curNode, _clicked);
	}
	col_tree.colClick0 = colClick0;
	
	col_tree.init();
//});
</script>
