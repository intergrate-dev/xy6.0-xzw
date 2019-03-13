<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<HTML>
<HEAD>
	<title><i18n:message key="fvproc.title"/></title>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>

	<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
	<link 	type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css"/>

	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
</HEAD>
<BODY class="treeBody">
	<script type="text/javascript">
		var fvID = "<c:out value="${param.FVID}"/>";
		
		function nodeClick(roleID){
			if (!roleID || roleID == "0") {
				window.parent.frames["mainframe"].location.href = "about:blank";
				return;
			}
			
			var theURL = "./FVProcPermission.do?RoleID=" + roleID
				+ "&FVID=" + fvID;
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
		
		var tree = new WebFXLoadTree("<i18n:message key="fvproc.title"/>", 
				"./treeFolderResource.do?OrgID=0&FVID=" + fvID);
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
		treeMenu.add(new WebFXMenuItem("<i18n:message key="fvproc.copy"/>", "roleReferrence(this);", "<i18n:message key="resource.fvproc.copy.title"/>"));
		treeMenu.add(new WebFXMenuSeparator());
		treeMenu.add(new WebFXMenuItem("<i18n:message key="fvproc.paste"/>", "roleCopy(this);", "<i18n:message key="resource.fvproc.paste.title"/>"));
		document.write(treeMenu);
	}
	
	function popmenu(src)
	{
		var roleID = src.getAttribute("roleID");
		if (!roleID || roleID == "0") return;
		
		treeMenu.roleID = roleID;
		treeMenu.roleName = getTreeNode(src).text;
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
		refID = p.roleID;
		refName = p.roleName;
		alert("<i18n:message key="resource.fvproc.copy.hint"/>" + refName);
	}
	//设置参考权限
	function roleCopy(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		if (!refID)
		{
			alert("<i18n:message key="resource.fvproc.copy.hint.noref"/>");
			return;
		}
		if (p.roleID == refID)
		{
			alert("<i18n:message key="resource.fvproc.copy.hint.same"/>");
			return;
		}
		
		if (window.confirm("<i18n:message key="resource.fvproc.copy.hint.confirm"/>" + refName))
		{		
			var urlsrc="./FVProcPermissionSubmit.do?Copy=2&FVID=" + fvID 
				+ "&SrcRoleID=" + refID 
				+ "&DestRoleID=" + p.roleID;
			parent.mainframe.location.href = urlsrc;
		}
	}
	</Script>
</BODY>
</HTML>
