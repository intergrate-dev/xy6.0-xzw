<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>

<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
<link 	type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css"/>
<link 	type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
<style type="text/css">
	body{
		background-color:#EEEEEE;
		margin-left : 3px;
		margin-top: 3px;
	}
</style>
<Script type="text/javascript">
	function treeInit(){
		try {
			if (tree.childNodes.length == 0) return;

			var node1 = webFXTreeHandler.getPlus(tree.childNodes[0].id);
			if (node1) 	node1["onclick"]();
		}catch (e){
			alert(e.message);
		}
		return true;
	}
	//回调函数,打开第一层子树后,自动点击第一个子树节点.
	webFXTreeConfig.loadCallBack = function(){
		var x = treeInit();
		webFXTreeConfig.loadCallBack = function(){};
	}
	//tree
	webFXTreeConfig.rootPath = "../e5script/";
	webFXTreeConfig.defaultTarget = "";
	webFXTreeConfig.multiple = false;

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	//menu
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 50;
	webFXMenuConfig.showTime	= 0;

	var tree = new WebFXLoadTree("<i18n:message key="operation.OP.contentTitle"/>",
		"listDocTypeOp.do?type=tree&proc=1");
	tree.setBehavior('classic');

	var opMenu = null;
	var currDocType;
	var currOperation;
	var currID = 0;
	var treeID = 0;

	function getFrame(name)
	{
		var frm= window.parent.frames[1];
		var s="";
		return frm;
	}
	//右键菜单：增加操作
	function addOperation()
	{
		treeID = currID;
		var cn = getFrame('showItem');
		var docTypeID = currDocType;
		cn.location = 'Operation.do?docTypeID='+docTypeID;
	}
	//右键菜单：操作一览
	function listOp()
	{
		treeID = currID;
		var cn = getFrame('showItem');
		var docTypeID = currDocType;
		cn.location = 'listOp.do?docTypeID='+docTypeID;
	}
	//右键菜单的刷新
	function refresh()
	{
		var src = webFXTreeHandler.getNode(currID);
		src.reload();
	}
	//初次准备右键菜单
	function prepareMenu()
	{
		if (opMenu == null)
		{
			opMenu = new WebFXMenu;
			opMenu.width = 150;
			opMenu.add(new WebFXMenuItem("<i18n:message key="operation.listOP.contentTitle"/>", "listOp()", "<i18n:message key="operation.listOP.contentTitle"/>"));
			opMenu.add(new WebFXMenuItem("<i18n:message key="operation.addOP.contentTitle"/>", "addOperation()", "<i18n:message key="operation.addproc.contentTitle"/>"));
			opMenu.add(new WebFXMenuSeparator());
			opMenu.add(new WebFXMenuItem("<i18n:message key="operation.common.refresh"/>", "refresh()", "<i18n:message key="operation.common.refresh"/>"));
			document.write(opMenu);
		}
	}
	//右键文档类型，显示菜单
	function popmenu(flag, src)
	{
		currDocType = src.getAttribute("docTypeID");
		opMenu.flowID = src.getAttribute("docTypeID");
		currID = src.id;
		webFXMenuHandler.showMenu(opMenu, src);
	}
	//在文档类型上点击，则显示所有操作
	function docClick(src)
	{
		var url = "listOp.do?docTypeID=" + src.getAttribute("docTypeID");
		currDocType = src.getAttribute("docTypeID");
		currID =  src.id;
		treeID = currID;
		getFrame("showItem").location = url;

	}
	//在操作上点击，则进入操作编辑窗口
	function opClick(src)
	{
		currOperation = src.getAttribute("operationID");
		currID =  src.id;
		treeID = currID;
		var url = "Operation.do?docTypeID=" + src.getAttribute("docTypeID")
			+ "&operationID=" + src.getAttribute("operationID");
		getFrame("showItem").location = url;
	}
	//其他窗口的回调刷新
	function refreshNode()
	{
		var src = webFXTreeHandler.getNode(treeID);
		if(src.reload)
			src.reload();
		else{
			var newsrc = src.parentNode;
			if (newsrc && newsrc.reload)
			{
				treeID = newsrc.id;
				currID = treeID;
				newsrc.reload();
			}
		}
	}
	//
	function showTree()
	{
		if (document.getElementById)
			document.write(tree);
	}
	showTree();
	prepareMenu();
</script>
