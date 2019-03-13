<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Folder Tree</TITLE>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>

	<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
	<link 	type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css"/>

	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
</HEAD>
<BODY class="treeBody">
	<script type="text/javascript">
		var roleID = "<c:out value="${sessionScope.permissionRoleID}"/>";
		
		function nodeClick(src){
			var fvID = src.getAttribute("fvID");
			if (!fvID || fvID == "0") {
				window.parent.frames["mainframe"].location.href = "about:blank";
				return;
			}
			
			var docLibID = src.getAttribute("docLibID");
			var docTypeID = src.getAttribute("docTypeID");
			
			var theURL = "./FVProcPermission.do?RoleID=" + roleID
				+ "&FVID=" + fvID
				+ "&DocLibID=" + docLibID
				+ "&DocTypeID=" + docTypeID;
			window.parent.frames["mainframe"].location.href = theURL;
		}
		function expandAll() {
			tree.expandAll();
		}
		function blankFunc(){}

		webFXTreeConfig.rootPath = "../e5script/";
		webFXTreeConfig.defaultClickAction = "nodeClick(this);";
		webFXTreeConfig.defaultContextAction = "popmenu(this)";
		webFXTreeConfig.getFileIcon = webFXTreeConfig.getFolderIcon;
		webFXTreeConfig.loadCallBack = function(){
			var x = expandAll();
			webFXTreeConfig.loadCallBack = blankFunc;
		}
		
		var tree = new WebFXLoadTree("<i18n:message key="fvproc.title"/>", "./FVProcTree.do");
		tree.show();

	//--------------------右键菜单-----------
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 50;
	webFXMenuConfig.showTime	= 0;

	var treeMenu = null;
	prepareMenu();
	
	function prepareMenu()
	{
		treeMenu = new WebFXMenu;
		treeMenu.width = 160;
		treeMenu.add(new WebFXMenuItem("<i18n:message key="fvproc.copy"/>", "roleReferrence(this);", "<i18n:message key="fvproc.copy.title"/>"));
		treeMenu.add(new WebFXMenuSeparator());
		treeMenu.add(new WebFXMenuItem("<i18n:message key="fvproc.paste"/>", "roleCopy(this);", "<i18n:message key="fvproc.paste.title"/>"));
		document.write(treeMenu);
	}
	
	function popmenu(src)
	{
		var fvID = src.getAttribute("fvID");
		if (!fvID || fvID == "0") return;
		
		treeMenu.fvID = fvID;
		treeMenu.fvName = getTreeNode(src).text;
		webFXMenuHandler.showMenu(treeMenu, src);
	}
	function getTreeNode(oItem) {
		return webFXTreeHandler.all[oItem.id.replace('-icon','').replace('-anchor', '')];
	}

	//作为参考-----
	var refID;
	var refName;
	function roleReferrence(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		refID = p.fvID;
		refName = p.fvName;
		alert("<i18n:message key="fvproc.copy.hint"/>" + refName);
	}
	//设置参考权限
	function roleCopy(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		if (!refID)
		{
			alert("<i18n:message key="fvproc.copy.hint.noref"/>");
			return;
		}
		if (p.fvID == refID)
		{
			alert("<i18n:message key="fvproc.copy.hint.same"/>");
			return;
		}
		
		if (window.confirm("<i18n:message key="fvproc.copy.hint.confirm"/>" + refName))
		{		
			var urlsrc="./FVProcPermissionSubmit.do?Copy=1&RoleID=" + roleID 
				+ "&SrcFVID=" + refID 
				+ "&DestFVID=" + p.fvID;
			parent.mainframe.location.href = urlsrc;
		}
	}
	</Script>
</BODY>
</HTML>
