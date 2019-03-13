<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<%
	int articleTradeTypeID = com.founder.xy.commons.CatTypes.CAT_ARTICLETRADE.typeID();
%>
<HTML>
<HEAD>
	<TITLE>分类树</TITLE>
	<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
	<script type="text/javascript" src="../../e5script/xmenu/xmenu.js"></script>

	<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5script/xmenu/xmenu.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/style.css"/>
	<style type="text/css">
	body{
		background-color:#EEEEEE;
		margin-left : 3px;
		margin-top: 3px;
	}
	</style>
</HEAD>

<BODY oncontextmenu="if (!event.ctrlKey){return false;}">
<Script>
	var articleTradeTypeID = "<%=articleTradeTypeID%>";
	
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
	webFXTreeConfig.rootPath = "../../e5script/";
	webFXTreeConfig.defaultTarget = "mainBody";
	webFXTreeConfig.multiple = false;

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	webFXTreeConfig.getFolderIcon = function(){
		return "../../images/catrule_folder.gif";
	}
	webFXTreeConfig.getOpenFolderIcon = function(){
		return "../../images/catrule_folderopen.gif";
	}
	webFXTreeConfig.getFileIcon = function(){
		return "../../images/catrule_document.gif";
	}
	//menu
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 50;
	webFXMenuConfig.showTime	= 0;

	var tree = new WebFXLoadTree("<i18n:message key="catTree.root"/>", "CatTree.do?siteID=${param.siteID}");
	tree.setBehavior('classic');

	function showTree() {
		if (document.getElementById)
			document.write(tree);
	}
	
	var catTypeMenu = null;
	var catMenu = null;
	var catMenu2 = null;
	
	var selID = null;
	
	function prepareMenu() {
		if (catTypeMenu == null) {
			catTypeMenu = new WebFXMenu;
			catTypeMenu.width = 160;
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.addCat"/>", "addCat(this);", "<i18n:message key="catTree.menuCatType.addCat.title"/>"));
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(catTypeMenu);
		}
		if (catMenu == null) {
			catMenu = new WebFXMenu;
			catMenu.width = 160;
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.updateCat"/>", "modifyCat(this);", "<i18n:message key="catTree.menuCat.updateCat.title"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			document.write(catMenu);
		}
		if (catMenu2 == null) {
			catMenu2 = new WebFXMenu;
			catMenu2.width = 160;
			catMenu2.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.addCat"/>", "addSubCat(this);", "<i18n:message key="catTree.menuCat.addCat.title"/>"));
			catMenu2.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.updateCat"/>", "modifyCat(this);", "<i18n:message key="catTree.menuCat.updateCat.title"/>"));
			catMenu2.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			catMenu2.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			document.write(catMenu2);
		}
	}

	function popmenu(flag, src) {
		webFXMenuHandler.all[catTypeMenu.id].hide();
		webFXMenuHandler.all[catMenu.id].hide();
		
		if (flag == 2) {
			catTypeMenu.catType = src.getAttribute("catType");
 			catTypeMenu.treeID = src.getAttribute("id");
			catTypeMenu.catID = src.getAttribute("catID");
			webFXMenuHandler.showMenu(catTypeMenu, src);
		} else if (flag == 3) {
			var catType = src.getAttribute("catType");
			var menu = null;
			
			if (catType == articleTradeTypeID) {
				menu = catMenu2;
			} else {
				menu = catMenu;
			}
			menu.catType  = src.getAttribute("catType");
			menu.treeID   = src.getAttribute("id");
			menu.catID    = src.getAttribute("catID");
			webFXMenuHandler.showMenu(menu, src);
		}
	}

	function getFrame(name) {
		return document.getElementById(name);
	}
	showTree();
	prepareMenu();
	
	function doHide() {
		document.getElementById("contentPane").style.display = "none";
	}
	
	//------------------------分类操作函数---------------------------====
	//增加分类
	function addCat(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../../e5cat/CatEdit.do?action=new&catType='+p.catType+"&parentID="+p.catID+"&treeID="+p.treeID;
	}
 	//添加子分类
	function addSubCat(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../../e5cat/CatEdit.do?action=new&catType='+p.catType+"&parentID="+p.catID+"&treeID="+p.treeID;
	}
  //编辑分类函数
	function modifyCat(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var refCat = false;
		if(p.refCat) refCat = true;

		var url = "../../e5cat/CatEdit.do?action=edit&id=" + p.catID+ "&catType=" + p.catType+"&treeID="+p.treeID+"&refCatAdd="+refCat;
		parent.mainBody.location.href = url;
	}

	//查看
	function catViewClick(src)
	{
		var url = "../../e5cat/CatView.do?action=edit&id="
			+ src.getAttribute("catID")+ "&catType=" + src.getAttribute("catType")+"&treeID="
			+ src.getAttribute("id")+"&refCatAdd="+src.getAttribute("refCat");
		parent.mainBody.location.href = url;
	}

	//添加分类到树
	function addCatToTree(catType,catID,catName,catCode,nodeID,refCat,refType,refID)
	{
		var node = webFXTreeHandler.getNode(nodeID);
		if(catCode == 'null') catCode = '';

		if((node.reload && node.loaded) || !node.reload)
		{
			var tmpName = catName;
			if (catCode) tmpName += "(" + catCode + ")";
			
			var a = new WebFXTreeItem(tmpName);

			a.click = "catViewClick(this);";
			a.contextAction = "popmenu(3, this);return false;";

			a.setAttribute("catID", catID);
			a.setAttribute("catType",catType);
			a.icon = webFXTreeConfig.getFileIcon();
			node.add(a);
		}
	}

	//删除分类
	function deleteCat(src) {
		var p = webFXMenuHandler.getMainMenu(src);
		var ok = confirm("<i18n:message key="catTree.confirm.deleteCat"/>");
		if (ok && canDeleteCat(p)) {
			var xmlHttp = XmlHttp.create();
			var deleteUrl = "../../e5cat/Cat.do?action=delete&id="+p.catID+"&catType="+p.catType;
			xmlHttp.open("GET", deleteUrl, false);	// async
			xmlHttp.send(null);
			
			var result = xmlHttp.responseText;
			if (result == "ok") {
				//删除js上的节点
				var node = webFXTreeHandler.getNode(p.treeID);
				node.remove();
				
				//清除frame上的内容
				parent.mainBody.location.href = 'about:blank';
			}
			else
				alert(result);
		}
	}
	function canDeleteCat(p) {
		return true;
	}
	
	//修改分类名称
	function modifyCatToTree(treeId,name,catCode)
	{
		if(catCode == 'null')catCode = '';
		var tmpName = name;
		if (catCode) tmpName += "(" + catCode + ")";

		webFXTreeHandler.changeText(treeId, tmpName);
	}

	//分类排序
	function catSort(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../../e5cat/CatList.do?action=list&catType='+p.catType+"&catID="+p.catID+"&treeID="+p.treeID;
	}
	//重新装载
	function catReload(treeID)
	{
		var node = webFXTreeHandler.getNode(treeID);
		if(node.reload)
			node.reload();
	}
	function reloadNode(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		catReload(p.treeID);
	}
</Script>
&nbsp;
</BODY>
</HTML>
