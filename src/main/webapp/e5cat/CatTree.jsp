<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Tree</TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>

	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
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
	webFXTreeConfig.defaultTarget = "mainBody";
	webFXTreeConfig.multiple = false;

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	webFXTreeConfig.getFolderIcon = function(){
		return "../images/catrule_folder.gif";
	}
	webFXTreeConfig.getOpenFolderIcon = function(){
		return "../images/catrule_folderopen.gif";
	}
	webFXTreeConfig.getFileIcon = function(){
		return "../images/catrule_document.gif";
	}
	//拖拽
	webFXTreeConfig.draggable  = true;
	webFXTreeConfig.defaultDragStartAction = "doDrag(this)";
	webFXTreeConfig.defaultDragOverAction = "doDragOver(this)";
	webFXTreeConfig.defaultDropAction = "doDrop(this)";

	//menu
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 50;
	webFXMenuConfig.showTime	= 0;


	var tree = new WebFXLoadTree("<i18n:message key="catTree.root"/>", "../e5cat/CatTypeTreeView.do?catTypes=<c:out value="${param.catTypes}"/>");
	tree.contextAction = "popmenu(1, this)";
	tree.setBehavior('classic');

	function showTree()
	{
		if (document.getElementById)
		{
			document.write(tree);
		}
	}
	var rootMenu 	= null;
	var catTypeMenu = null;
	var catMenu 	= null;
	var refCatMenu	= null;

	prepareMenu();
	function prepareMenu()
	{
		if (rootMenu == null)
		{
			rootMenu = new WebFXMenu;
			rootMenu.width = 160;
			rootMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuRoot.addType"/>", "addCatType();", "<i18n:message key="catTree.menuCatType.addType.title"/>"));
			document.write(rootMenu);
		}
		if (catTypeMenu == null)
		{
			catTypeMenu = new WebFXMenu;
			catTypeMenu.width = 160;
			//catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.addType"/>", "addCatType();", "<i18n:message key="catTree.menuCatType.addType.title"/>"));
      //catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.deleteType"/>", "deleteCatType(this);", "<i18n:message key="catTree.menuCatType.deleteType.title"/>"));
      //catTypeMenu.add(new WebFXMenuSeparator());
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.addCat"/>", "addCat(this);", "<i18n:message key="catTree.menuCatType.addCat.title"/>"));
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.addRefCat"/>", "addRefCat(this);", "<i18n:message key="catTree.menuCatType.addRefCat.title"/>"));
			catTypeMenu.add(new WebFXMenuSeparator());
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.catRestore"/>", "catRestore(this);", "<i18n:message key="catTree.menuCatType.catRestore"/>"));
			catTypeMenu.add(new WebFXMenuSeparator());
			//---
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.export"/>", "catExport(this);", "<i18n:message key="catTree.menuCat.export.title"/>"));
			catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.import"/>", "catImport(this);", "<i18n:message key="catTree.menuCat.import.title"/>"));
			catTypeMenu.add(new WebFXMenuSeparator());
catTypeMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(catTypeMenu);

		}
		if (catMenu == null)
		{
			catMenu = new WebFXMenu;
			catMenu.width = 160;
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.addCat"/>", "addSubCat(this);", "<i18n:message key="catTree.menuCat.addCat.title"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.updateCat"/>", "modifyCat(this);", "<i18n:message key="catTree.menuCat.updateCat.title"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			catMenu.add(new WebFXMenuSeparator());
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCatType.addRefCat"/>", "addRefCat(this);", "<i18n:message key="catTree.menuCatType.addRefCat.title"/>"));
      catMenu.add(new WebFXMenuSeparator());
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.catPub"/>", "catPubEdit(this);", "<i18n:message key="catTree.menuCat.catPub.title"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.catLink"/>", "catLinkEdit(this);", "<i18n:message key="catTree.menuCat.catLink.title"/>"));
			catMenu.add(new WebFXMenuSeparator());
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.sort"/>", "catSort(this);", "<i18n:message key="catTree.menuCat.sort"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.export"/>", "catExport(this);", "<i18n:message key="catTree.menuCat.export.title"/>"));
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.import"/>", "catImport(this);", "<i18n:message key="catTree.menuCat.import.title"/>"));
      catMenu.add(new WebFXMenuSeparator());
			catMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(catMenu);
		}
    if(refCatMenu == null)
   {
			refCatMenu = new WebFXMenu;
			refCatMenu.width = 160;
			refCatMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.updateCat"/>", "modifyCat(this);", "<i18n:message key="catTree.menuCat.updateCat.title"/>"));
			refCatMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.deleteCat"/>", "deleteCat(this);", "<i18n:message key="catTree.menuCat.deleteCat.title"/>"));
			refCatMenu.add(new WebFXMenuSeparator());
			refCatMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menuCat.export"/>", "catExport(this);", "<i18n:message key="catTree.menuCat.export.title"/>"));
      refCatMenu.add(new WebFXMenuSeparator());
			refCatMenu.add(new WebFXMenuItem("<i18n:message key="catTree.menu.reload.title"/>", "reloadNode(this);", "<i18n:message key="catTree.menu.reload.title"/>"));
			document.write(refCatMenu);
   }
	}
	
	function hideMenus() {
		try {
			webFXMenuHandler.all[rootMenu.id].hide();
			webFXMenuHandler.all[catTypeMenu.id].hide();
			webFXMenuHandler.all[catMenu.id].hide();
			webFXMenuHandler.all[refCatMenu.id].hide();
		} catch (e){}
	}
	function popmenu(flag, src)
	{
		hideMenus();
		
		if (flag == 1)
		{
			//webFXMenuHandler.showMenu(rootMenu, src);
		}
		else if (flag == 2)
		{

			catTypeMenu.catType  = src.getAttribute("catType");
 			catTypeMenu.treeID   = src.getAttribute("id");
			catTypeMenu.catID    = 0;
			<c:if test="${isAdmin == 'true'}">
			   webFXMenuHandler.showMenu(catTypeMenu, src);
      </c:if>
		}
		else if (flag == 3)
		{
			//catMenu.FlowID = src.FlowID;
			//catMenu.FlowNodeID = src.FlowNodeID;
			catMenu.catType  = src.getAttribute("catType");
 			catMenu.treeID   = src.getAttribute("id");
			catMenu.catID    = src.getAttribute("catID");

			webFXMenuHandler.showMenu(catMenu, src);

		}
		else if (flag == 4)
		{
//			refCatMenu.FlowID = src.FlowID;
//			refCatMenu.FlowNodeID = src.FlowNodeID;
//			refCatMenu.Tag = "Do";
			refCatMenu.catType  = src.getAttribute("catType");
 			refCatMenu.treeID   = src.getAttribute("id");
			refCatMenu.catID    = src.getAttribute("catID");
			refCatMenu.refCat   = src.getAttribute("refCat");
			webFXMenuHandler.showMenu(refCatMenu, src);
		}
	}

	function getFrame(name)
	{
		return document.getElementById(name);
	}
	var selID = null;

	showTree();
	function doHide()
	{
		//dojo.widget.getWidgetById('contentPane').hide();
		document.getElementById("contentPane").style.display = "none";
	}
	
	//-----------------------树的拖拽响应函数------------------------------------
	var drag_src;
	var drag_dest;
	function doDrag(src){	drag_src = webFXTreeHandler.drag_src;return true;}
	function doDragOver(src){
		drag_dest = src;

		return true;
	}
	
	function doDrop(src) {
		var src = webFXTreeHandler.drag_src;
		var dest =  webFXTreeHandler.drag_dest;
		
		if (!canDrop(src, dest)) return;
		
		var srcNode     = webFXTreeHandler.getNode(src.id);
		var srcCatID    = src.getAttribute("catID");
		var srcCatType  = src.getAttribute("catType");
		var srcRefCat   = src.getAttribute("refCat");

		var destNode    = webFXTreeHandler.getNode(dest.id);
		var destCatType = dest.getAttribute("catType");
		var destCatID   = dest.getAttribute("catID");
		var destRefCat  = dest.getAttribute("refCat");

		if (destCatID == undefined) destCatID = 0;
		
		//引用的分类不能移动
		var ownerRefCat = src.getAttribute("ownerRefCat");
		if ((srcRefCat=='true' && !ownerRefCat) || destRefCat =='true') {
			return false;
        }
		//只能移动分类
		if (srcCatID == '' || destCatType =='') {
			return false;
		}
		//只能移动同类型下的分类
		if (srcCatType!=destCatType) {
			return false;
		}
		//部门管理员不能移动分类到根
		if(destCatID == 0) {
		   <c:if test="${isAdmin == 'false'}">
			return false;
		   </c:if>
		}
		 var ok = confirm("<i18n:message key="catTree.confirm.moveCat"/>");
		 if (ok) {
			var dragUrl = "../e5cat/CatDrag.do?catType="+destCatType+"&srcCatID="+srcCatID+"&destCatID="+destCatID;

			var xmlHttp = XmlHttp.create();
			xmlHttp.open("GET", dragUrl, false);	// async
			xmlHttp.send();
			var result  = xmlHttp.responseText;
			if (result == "ok") {
				return true;
			} else {
				alert(result);
				return false;
			}
		} else {
			return false;
		}
	}
	function canDrop(src, dest) {
		return true;
	}

	//-------------------------分类类型操作函数---------------------------

    //增加分类类型
    function addCatType()
    {
	 	parent.mainBody.location.href='../e5cat/CatTypeEdit.do?action=new';
    }
   //编辑分类类型函数
	function catTypeClick(src)
	{
		/*
		var url = "../e5cat/CatTypeEdit.do?action=edit&id=" + src.catType + "&treeID=" + src.id;
		parent.mainBody.location.href = url;
		dojo.widget.getWidgetById('contentPane').restoreWindow();
		document.getElementById("contentPane").style.display = "block";
		*/
	}
	//添加新的分类类型到树
	function addCatTypeToTree(catType, name)
	{
		var node = tree;
		var a = new WebFXLoadTreeItem(name);
		a.click = "catTypeClick(this);";
		a.contextAction = "popmenu(2, this);return false;";
		a.setAttribute("catType", catType);
		a.src  = "../e5cat/CatTreeView.do?catType="+catType;
		a.icon = webFXTreeConfig.getFolderIcon();
		a.openIcon = webFXTreeConfig.getOpenFolderIcon();
		node.add(a);

	}
	//删除分类类型函数
	function deleteCatType(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
	 	var ok = confirm("<i18n:message key="catTree.confirm.deleteCatType"/>");
		if(ok)
		{
			 var xmlHttp = XmlHttp.create();
			 var deleteUrl = "../e5cat/CatType.do?action=delete&id="+p.catType;
			 xmlHttp.open("GET", deleteUrl, false);	// async
			 xmlHttp.send();
			 var result = xmlHttp.responseXML;

			//删除js上的节点
			var node = webFXTreeHandler.getNode(p.treeID);
			//alert(node);
			node.remove();
		}
	}
	function modifyCatTypeToTree(catType,name)
	{
		//var node = webFXTreeHandler.getNode(selID);
		var node = tree.getSelected();
		if (node)
		{
		}
	}

	//------------------------分类操作函数---------------------------====
	//增加分类
	function addCat(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../e5cat/CatEdit.do?action=new&catType='+p.catType+"&treeID="+p.treeID;

	}

	//增加引用分类
  function addRefCat(src)
  {
		var p = webFXMenuHandler.getMainMenu(src);
		//refCatAdd 参数=true 表示添加引用分类
		parent.mainBody.location.href='../e5cat/CatEdit.do?action=new&catType='+p.catType+"&treeID="+p.treeID+"&refCatAdd=true"+"&parentID="+p.catID;
  }

	//添加子分类

	function addSubCat(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../e5cat/CatEdit.do?action=new&catType='+p.catType+"&parentID="+p.catID+"&treeID="+p.treeID;
		//addCatToTree('2','1','ceshi',p.treeID);

	}
   //编辑分类函数
	function modifyCat(src)
	{
    var p = webFXMenuHandler.getMainMenu(src);
		var refCat = false;
		if(p.refCat) refCat = true;

		var url = "../e5cat/CatEdit.do?action=edit&id=" + p.catID+ "&catType=" + p.catType+"&treeID="+p.treeID+"&refCatAdd="+refCat;
		parent.mainBody.location.href = url;
	}

	//查看
	function catViewClick(src)
	{
		var url = "../e5cat/CatView.do?action=edit&id=" + src.getAttribute("catID")+ "&catType=" + src.getAttribute("catType")+"&treeID="
								+src.getAttribute("id")+"&refCatAdd="+src.getAttribute("refCat");
		parent.mainBody.location.href = url;
	}

	//添加分类到树
	function addCatToTree(catType,catID,catName,catCode,nodeID,refCat,refType,refID)
	{
		//alert("save ok");
		var node = webFXTreeHandler.getNode(nodeID);
		if(catCode == 'null') catCode = '';

		//alert(nodeID);
		//alert(node);
		//树还未被装在不必添加到树
		//1。增加分类，如果父分类有load但未打开，不必加入节点

		//2。增加分类，如果父分类有load打开了，加入节点
		//	如果分类是引用分类，该节点是loadtree,否则就是treeitem
		//3。如果父分类节点无load，加入节点

		//if(refCat !='true' || (refCat == 'true' && node.loaded))
		if((node.reload && node.loaded) || !node.reload)
		{
			var tmpName = catName;
			if (catCode) tmpName += "(" + catCode + ")";
			
			var a;
			if (refCat == 'true')
				a = new WebFXLoadTreeItem(tmpName);
			else
				a = new WebFXTreeItem(tmpName);

			a.click = "catViewClick(this);";
			a.contextAction = "popmenu(3, this);return false;";

			//引用分类
			 if(refCat == 'true')
			{
				a.src = "../e5cat/CatTreeView.do?catType="+refType+"&catID="+refID + "&refCat=true";
				a.icon = "../images/cat_ref_node.gif";
				a.openIcon = "../images/cat_ref_node.gif";
				//a.fileIcon = "../images/cat_ref_node.gif";
				a.setAttribute("fileIcon","../images/cat_ref_node.gif");
				a.setAttribute("catID", catID);
				a.setAttribute("catType",catType);
				a.contextAction= "popmenu(4, this);return false;";
				a.setAttribute("refCat","true");
				a.src="../e5cat/CatTreeView.do?catType="+refType+"&catID="+refID+"&refCat=true";
			 }
			else
			 {
				a.setAttribute("catID", catID);
				a.setAttribute("catType",catType);
				a.icon     = webFXTreeConfig.getFileIcon();
			 }
			node.add(a);
		}
	}

	//删除分类
	function deleteCat(src) {
		var p = webFXMenuHandler.getMainMenu(src);
		var ok = confirm("<i18n:message key="catTree.confirm.deleteCat"/>");
		if (ok && canDeleteCat(p)) {
			var xmlHttp = XmlHttp.create();
			var deleteUrl = "../e5cat/Cat.do?action=delete&id="+p.catID+"&catType="+p.catType;
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
		//var node = webFXTreeHandler.getNode(treeId);
		//alert(node);
		//node.setAttribute("name",name);
	}

	//分类排序
	function catSort(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../e5cat/CatList.do?action=list&catType='+p.catType+"&catID="+p.catID+"&treeID="+p.treeID;
	}
	//重新装载
	function catReload(treeID)
	{
		var node = webFXTreeHandler.getNode(treeID);

		if(node.reload)
		{
				node.reload();
		}
	}
	//分类导出
	function catExport(src)
	{
		var p        = webFXMenuHandler.getMainMenu(src);
		var url      = "CatExport.jsp?catType="+p.catType+"&catID="+p.catID;
		var exportW = window.open(url,'export','scrollbars=0,width=200,height=100');
		var x = screen.width/2-100;
		var y = screen.height/2-50;

		exportW.moveTo(x,y);
	}

	var importTreeID = '';
	//分类导入
	function catImport(src)
	{
		var p        = webFXMenuHandler.getMainMenu(src);
		var url      = "../e5cat/CatImport.do?catType="+p.catType+"&catID="+p.catID;
		var exportW  = window.open(url,'import','scrollbars=0,width=350,height=150');
		var x = screen.width/2-175;
		var y = screen.height/2-75;
		importTreeID =  p.treeID;

		exportW.moveTo(x,y);
	}
	function importReload()
  	{

		var node = webFXTreeHandler.getNode(importTreeID);
		{
			//递归调用刷新父(避免他的父没不能reload,最顶层是有reload的的)
			while (node && !node.reload)
				node = node.parentNode;
			//alert(node.id);
  			if(node.reload) node.reload();
		}
  	}

	//分类删除管理
	function catRestore(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		parent.mainBody.location.href='../e5cat/CatRestore.do?catType='+p.catType;
	}

	function reloadNode(src)
  {
		var p = webFXMenuHandler.getMainMenu(src);
    catReload(p.treeID);
  }

   //分类发布设置
	function catPubEdit(src)
	{
    	var p = webFXMenuHandler.getMainMenu(src);
		var url = "../e5cat/CatPubEdit.do?action=edit&id=" + p.catID+ "&catType=" + p.catType+"&treeID="+p.treeID;
		//window.open(url,'pubedit','scrollbars=0,width=450,height=200');
		parent.mainBody.location.href = url;
	}
	//分类相关分类设置
	function catLinkEdit(src)
	{
  		var p = webFXMenuHandler.getMainMenu(src);
		var url = "../e5cat/CatLinkEdit.do?action=edit&id=" + p.catID+ "&catType=" + p.catType+"&treeID="+p.treeID;
		//window.open(url,'linkedit','scrollbars=0,width=450,height=200');
		parent.mainBody.location.href = url;
	}
</Script>
&nbsp;
</BODY>
</HTML>
