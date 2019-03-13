<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>

<%
	String strDocLibID = request.getParameter("DocLibID");
%>
<HTML>
<HEAD>
	<TITLE>Tree</TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">

	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style type="text/css">
	body{
		background-color:#EEEEEE;
		margin-left : 5px;
		margin-top: 5px;
	}
	</style>
<script language="javascript">

var i18nInfo = {

//	deleteDocLib :"<i18n:message key="e5dom.DocLib.DeleteDocLib"/>",
//	confirmDeleteDocLib : "<i18n:message key="e5dom.DocLib.confirmDeleteDocLib"/>",
//	confirmDeleteDocLibEnd :"<i18n:message key="e5dom.DocLib.confirmDeleteDocLibEnd"/>"

	createFolder : "<i18n:message key="e5dom.Folder.CreateFolder"/>",
	modifyFolder : "<i18n:message key="e5dom.Folder.ModifyFolder"/>",
	deleteFolder : "<i18n:message key="e5dom.Folder.DeleteFolder"/>",

	createView : "<i18n:message key="e5dom.View.CreateView"/>",
	modifyView : "<i18n:message key="e5dom.View.ModifyView"/>",
	deleteView : "<i18n:message key="e5dom.View.DeleteView"/>",

	CreateOK : "<i18n:message key="e5dom.Folder.CreateOK"/>",
	CreateNo : "<i18n:message key="e5dom.Folder.CreateNo"/>",
	ModifyOK : "<i18n:message key="e5dom.Folder.ModifyOK"/>",
	ModifyNo : "<i18n:message key="e5dom.Folder.ModifyNo"/>",
	DeleteOK : "<i18n:message key="e5dom.Folder.DeleteOK"/>",
	DeleteNo : "<i18n:message key="e5dom.Folder.DeleteNo"/>",

	DeleteConfirm1 : "<i18n:message key="e5dom.Folder.DeleteConfirm1"/>",
	DeleteConfirm2 : "<i18n:message key="e5dom.Folder.DeleteConfirm2"/>",

	MoveConfirm1 :"<i18n:message key="e5dom.Folder.MoveConfirm1"/>",
	MoveConfirm2 :"<i18n:message key="e5dom.Folder.MoveConfirm2"/>",

	setPermission: "<i18n:message key="e5dom.Folder.SetPermission"/>",

	refresh : "<i18n:message key="e5dom.Folder.RefreshAction"/>",
	sort : "<i18n:message key="e5dom.Folder.SortAction"/>"

}
</script>

	<script language="javascript" src="script/prototype.js"></script>
	<SCRIPT LANGUAGE="JavaScript">
		webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function(){
			return "../images/blue_folder.gif";
		}

		webFXTreeConfig.draggable  = true;
		webFXTreeConfig.defaultDragStartAction = "doDrag(this)";
		webFXTreeConfig.defaultDragOverAction = "doDragOver(this)";
		webFXTreeConfig.defaultDropAction = "doDrop(this)";

		var folderMenu = new WebFXMenu();
		folderMenu.width = 100;
		folderMenu.add(new WebFXMenuItem(i18nInfo.createFolder, "addNewFolder();", i18nInfo.createFolder));
		folderMenu.add(new WebFXMenuItem(i18nInfo.modifyFolder, "ModifyFolder();", i18nInfo.modifyFolder));
		folderMenu.add(new WebFXMenuItem(i18nInfo.deleteFolder, "DeleteFolder();", i18nInfo.deleteFolder));
		folderMenu.add(new WebFXMenuSeparator());
		folderMenu.add(new WebFXMenuItem(i18nInfo.createView, "addNewView();", i18nInfo.createView));
		folderMenu.add(new WebFXMenuSeparator());
		//文件夹权限
		//folderMenu.add(new WebFXMenuItem(i18nInfo.setPermission, "setFolderPermission();", i18nInfo.setPermission));
		folderMenu.add(new WebFXMenuItem(i18nInfo.setPermission, "setFVProcPermission(0);", i18nInfo.setPermission));
		folderMenu.add(new WebFXMenuSeparator());
		folderMenu.add(new WebFXMenuItem(i18nInfo.sort, "sort();", i18nInfo.sort));
		folderMenu.add(new WebFXMenuSeparator());
		folderMenu.add(new WebFXMenuItem(i18nInfo.refresh, "refresh();", i18nInfo.refresh));
		folderMenu.generate();
		////////////////////////////////////////
		var viewMenu = new WebFXMenu();
		viewMenu.width = 100;
		viewMenu.add(new WebFXMenuItem(i18nInfo.modifyView, "ModifyView();", i18nInfo.modifyView));
		viewMenu.add(new WebFXMenuItem(i18nInfo.deleteView, "DeleteView();", i18nInfo.deleteView));
		viewMenu.add(new WebFXMenuSeparator());
		//viewMenu.add(new WebFXMenuItem(i18nInfo.setPermission, "setViewPermission();", i18nInfo.setPermission));
		viewMenu.add(new WebFXMenuItem(i18nInfo.setPermission, "setFVProcPermission(1);", i18nInfo.setPermission));
		viewMenu.generate();
		///////////////////////////////////////
		function nodeClick(fvid,flag){
			parent.page.operation.nodeClick(fvid,flag);
		}

		function contextClick(fvid,flag,el) {
			var fvName = el.innerHTML;

			if(flag == 1){
				folderMenu.folderid = fvid;
				folderMenu.itemid = el.id;
				folderMenu.fvName = fvName;
				folderMenu.parentID = el.getAttribute("parentID");
				webFXMenuHandler.showMenu(folderMenu, el);
			}
			else {
				viewMenu.viewid = fvid;
				viewMenu.itemid = el.id;
				viewMenu.fvName = fvName;
			    webFXMenuHandler.showMenu(viewMenu, el);
			}

//			parent.page.operation.contextClick(fvid);
		}

		function addNewFolder() {
			var parentID = folderMenu.folderid;
			parent.page.operation.addNewFolder(parentID);

		}

		function sort() {
			var parentID = folderMenu.folderid;
			parent.page.operation.sortFolders(parentID);
		}

		function addNewView() {
  			var parentID = folderMenu.folderid;
			parent.page.operation.addNewView(parentID);
		}

		function ModifyFolder() {
			var folderid = folderMenu.folderid;
			parent.page.operation.ModifyFolder(folderid);

		}

		function ModifyView() {
			var viewid = viewMenu.viewid;
			parent.page.operation.ModifyView(viewid);

		}

		//------原：对一个文件夹/视图，设置所有角色的文件夹权限
		function setFolderPermission() {
			var folderid = folderMenu.folderid;
			openPermission(folderid);
		}
		function setViewPermission() {
			var viewid = viewMenu.viewid;
			openPermission(viewid);
		}
		function openPermission(fvid)
		{
			var theURL = "../e5permission/permissionFolderResource.do?FVID=" + fvid;
			var target = "folder_permit";
			var feature = "width=450px, height=500px, left=200, top=100,resizable=1,"
				+ 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no';
			var folderPermissionWnd = window.open(theURL, target, feature, true);
			folderPermissionWnd.focus();
		}

		//------新：对一个文件夹/视图，设置所有角色的文件夹操作权限
		function setFVProcPermission(flag) {
			var id = (flag == 0) ? folderMenu.folderid : viewMenu.viewid;
			openFVProcPermission(id);
		}
		function openFVProcPermission(fvid) {
			var theURL = "../e5permission/ResourceFVProc.jsp?FVID=" + fvid;
			var target = "folder_permit";
			var feature = "width=950px, height=650px, resizable=1,"
				+ 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no';
			var folderPermissionWnd = window.open(theURL, target, feature, true);
			folderPermissionWnd.focus();
		}
		
		function DeleteFolder() {
			var folderid = folderMenu.folderid;
			var fvName = folderMenu.fvName;
			var parentID = folderMenu.parentID;
			parent.page.operation.DeleteFolder(folderid,fvName,parentID);

		}

		function DeleteView() {
			var viewid = viewMenu.viewid;
			parent.page.operation.DeleteView(viewid,viewMenu.fvName);

		}

		function addFolderToTree(folderObj) {

			var parentID = folderObj.parentID;
			var folderID = folderObj.FVID;
			var folderName = folderObj.FVName;

			var parentFolder = webFXTreeHandler.getNode(folderMenu.itemid);
			if(typeof(parentFolder.loaded) == "undefined" || parentFolder.loaded){
				var a = new WebFXTreeItem(folderName);
				a.click = "nodeClick("+folderID+",1)";
				a.contextAction = "contextClick("+folderID+",1,this)";
				a.setAttribute("fvID", folderID);
				a.src = "./showSubFolders.do?folderID="+folderID;
				parentFolder.add(a);
			}

		}

		function addViewToTree(viewObj) {

			var parentID = viewObj.parentFolderID;
			var viewID = viewObj.FVID;
			var viewName = viewObj.FVName;

			var parentFolder = webFXTreeHandler.getNode(folderMenu.itemid);
			if(typeof(parentFolder.loaded) == "undefined" || parentFolder.loaded){
				var a = new WebFXTreeItem(viewName);
				a.click = "nodeClick("+viewID+",2)";
				a.contextAction = "contextClick("+viewID+",2,this)";
				a.setAttribute("fvID", viewID);
				a.icon = "../images/pink_folder.gif";
				parentFolder.add(a);
			}

		}

		function deleteFolderFromTree() {
			var folder = webFXTreeHandler.getNode(folderMenu.itemid);
			folder.remove();

		}

		function deleteViewFromTree() {
			var view = webFXTreeHandler.getNode(viewMenu.itemid);
			view.remove();

		}

		function updateFolderToTree(folderObj) {
			var itemid = folderMenu.itemid;
			$(itemid).innerHTML = folderObj.FVName;

		}

		function updateViewToTree(viewObj) {
			var itemid = viewMenu.itemid;
			$(itemid).innerHTML = viewObj.FVName;
		}

		function doDrag(el){return true;}
		function doDragOver(el){return true;}
		function doDrop(src){
			//if(!drag_dest.catID)
			//	return false;
			var folderID    = webFXTreeHandler.drag_src.getAttribute("fvID");
			var folderName    = webFXTreeHandler.drag_src.text;

			var newParentID = webFXTreeHandler.drag_dest.getAttribute("fvID");
			var newParentTreeLevel = webFXTreeHandler.drag_dest.getAttribute("treeLevel");
			var newTreeLevel = parseInt(newParentTreeLevel)+1;

			if(confirm(i18nInfo.MoveConfirm1+" "+folderName+" "+i18nInfo.MoveConfirm2))
			{
				var END_POINT="<%=request.getContextPath()%>/buffalo";
				var buffalo = new Buffalo(END_POINT);
				buffalo.remoteCall("folderService.dragFolder", [parseInt(folderID),parseInt(newParentID)], function(reply){
					});
				return true;
			}
			else
			  return false;
		}

		function refresh() {

			var folder = webFXTreeHandler.getNode(folderMenu.itemid);

			if(folder.reload)
				folder.reload();
			else if (folder.parentNode.reload){
			    folder.parentNode.reload();
			}
		}
	</SCRIPT>

</HEAD>

<BODY class="treeBody">
	<Script>
		//回调函数,打开第一层子树后,自动点击第一个子树节点.
		webFXTreeConfig.loadCallBack = function(){
			var x = treeInit();
			webFXTreeConfig.loadCallBack = function(){};
		}
		//tree
		webFXTreeConfig.rootPath = "../e5script/";

		var tree = new WebFXLoadTree("<i18n:message key="e5dom.Folder.FolderTree"/>",
					"./showFolderTree.do?DocLibID="+<%=strDocLibID%>);

		tree.show();
		
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
	</Script>

</BODY>
</HTML>
