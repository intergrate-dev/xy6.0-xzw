<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
<head>
	<title>导航</title>
	<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
	<script type="text/javascript" src="../../e5script/xmenu/xmenu-modify-by-zxc.js"></script>
	<script type="text/javascript" src="./js/mgruser.js"></script>
	<script type="text/javascript" src="./js/mgrorg.js"></script>
	<script type="text/javascript" src="./js/mgrrole.js"></script>
	<script type="text/javascript" src="./js/xmlhttps.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>

	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5style/style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="StyleSheet" href="../../e5script/xmenu/xmenu-modify-by-zxc.css"/>

	<style type="text/css">
		html{
			width:100%;
			background-color:#eee;
			height:100%;
		}
		body{
			font-family: "微软雅黑";
			font-size: 12px;
		}
	</style>
</head>
<body>
<script type="text/javascript">
var siteID = "${param.siteID}";

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
	var rootOrgID;
	if (window.top.frames["SysFrame"] != null) {
		$.ajax({url:"../../xy/tenant/Org.do", async: false, success: function (data) {
			rootOrgID = data;
		}});
	} else {
		rootOrgID = "<c:out value="${sessionScope.tenant.orgID}"/>";
	}
	//回调函数,打开第一层子树后,自动点击第一个子树节点.
	webFXTreeConfig.loadCallBack = function(){
		var x = treeInit();
		webFXTreeConfig.loadCallBack = function(){};
	}
	//tree
	webFXTreeConfig.rootPath = "../../e5script/";
	webFXTreeConfig.defaultTarget = "showItem";
	webFXTreeConfig.multiple = false;
	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	//menu
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 200;
	webFXMenuConfig.showTime	= 0;

	//因为只允许单层机构，不需要拖放
	webFXTreeConfig.draggable  = false;
	webFXTreeConfig.defaultDragStartAction = "doDrag(this)";
	webFXTreeConfig.defaultDragOverAction = "doDragOver(this)";
	webFXTreeConfig.defaultDropAction = "doDrop(this)";

	webFXTreeConfig.getRootIcon = webFXTreeConfig.getOpenRootIcon = function(){
		return "../../images/orgtree.gif";
	}

	//构造树
	var treeUrl = "OrgTreeGenerate.do?invoke=orgNode&orgID=" + rootOrgID;
	var tree = new WebFXLoadTree("<i18n:message key="org.rootname"/>", treeUrl);
	tree.contextAction = "popmenu(0, this)";
	tree.setBehavior('classic');

	function showTree()
	{
		if (document.getElementById)
		{
			document.write(tree);
		}
	}
	var rootMenu 	= null;
	var orgMenu = null;
	var roleMenu 	= null;
	var userMenu = null;
	var sysAdminMenu = null;
	var rootMenuShowStatus="<c:out value="${rootmenu}"/>";

	function addRootOrg(src)
	{
		var mainMenu = webFXMenuHandler.getMainMenu(src);
		alert(mainMenu.CatTypeID);
	}
	prepareMenu();
	
	function prepareMenu()
	{
		if (siteID) return; //若有siteID参数证明是在前端，此时只显示部门树，不提供管理功能。
		
		if (rootMenu == null)
		{
			rootMenu = new WebFXMenu;
			rootMenu.width = 150;
			//若根机构>0，是前台的一个租户下的部门树
			if (rootOrgID > 0) {
				rootMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.addorg"/>", "addOrg(this);", "<i18n:message key="org.menu.addorg.title"/>"));
				rootMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.sortorg"/>", "sortOrg(this);", "<i18n:message key="org.menu.sortorg.title"/>"));
				rootMenu.add(new WebFXMenuSeparator());
				
				rootMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.addrole"/>", "addRoleUnderOrg(this);", "<i18n:message key="org.menu.addrole.title"/>"));
				rootMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.sortrole"/>", "sortRole(this);", "<i18n:message key="org.menu.sortrole.title"/>"));
				rootMenu.add(new WebFXMenuSeparator());
			}
			rootMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.refresh"/>", "reloadRoot();", "<i18n:message key="org.menu.refresh.title"/>"));
			rootMenu.add(new WebFXMenuItem("刷新缓存", "refreshCache();", "刷新部门角色缓存和权限缓存"));
			
			document.write(rootMenu);
		}
		if (orgMenu == null)
		{
			orgMenu = new WebFXMenu;
			orgMenu.width = 150;

			orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.updateorg"/>", "updateOrg(this);", "<i18n:message key="org.menu.updateorg.title"/>"));
			orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.delorg"/>", "delOrg(this);", "<i18n:message key="org.menu.delorg.title"/>"));
			//是在系统管理端，才允许在机构下建子机构（避免这么做，各自在前台做）
			//if (rootOrgID == 0) 
			{
				orgMenu.add(new WebFXMenuSeparator());
				orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.addorg"/>", "addOrg(this);", "<i18n:message key="org.menu.addorg.title"/>"));
				orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.sortorg"/>", "sortOrg(this);", "<i18n:message key="org.menu.sortorg.title"/>"));
				orgMenu.add(new WebFXMenuSeparator());
				
				orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.addrole"/>", "addRoleUnderOrg(this);", "<i18n:message key="org.menu.addrole.title"/>"));
				orgMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.sortrole"/>", "sortRole(this);", "<i18n:message key="org.menu.sortrole.title"/>"));
			}
			
			document.write(orgMenu);
		}
		if (roleMenu == null)
		{
			roleMenu = new WebFXMenu;
			roleMenu.width = 150;
			roleMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.role.authority"/>", "authorityMgr(this);", "<i18n:message key="org.menu.role.authority.title"/>"));
			roleMenu.add(new WebFXMenuSeparator());

			roleMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.updaterole"/>", "updateRole(this);", "<i18n:message key="org.menu.updaterole.title"/>"));
			roleMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.delrole"/>", "delRole(this);", "<i18n:message key="org.menu.delrole.title"/>"));
			roleMenu.add(new WebFXMenuSeparator());

			roleMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.role.referrence"/>", "roleReferrence(this);", "<i18n:message key="org.menu.role.referrence.title"/>"));
			roleMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.role.copy"/>", "roleCopy(this);", "<i18n:message key="org.menu.role.copy.title"/>"));

			document.write(roleMenu);
		}
		if (userMenu == null)
		{
			userMenu = new WebFXMenu;
			userMenu.width = 150;
			userMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.adduser"/>", "addUserByUser(this);", "<i18n:message key="org.menu.adduser.title"/>"));
			userMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.updateuser"/>", "updateUser(this);", "<i18n:message key="org.menu.updateuser.title"/>"));
			userMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.deluser"/>", "delUser(this);", "<i18n:message key="org.menu.deluser.title"/>"));
			userMenu.add(new WebFXMenuSeparator());
			userMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.retrieve"/>", "retrieve();", "<i18n:message key="org.menu.retrieve.title"/>"));
			document.write(userMenu);
		}
		if(sysAdminMenu==null)
		{
			sysAdminMenu = new WebFXMenu;
			sysAdminMenu.width = 150;
			sysAdminMenu.add(new WebFXMenuItem("<i18n:message key="org.menu.updateuser"/>", "updateSysAdmin(this);", "<i18n:message key="org.menu.updateuser.title"/>"));
			document.write(sysAdminMenu);
		}
	}
	function hideMenus() {
		webFXMenuHandler.all[roleMenu.id].hide();
		webFXMenuHandler.all[orgMenu.id].hide();
		webFXMenuHandler.all[rootMenu.id].hide();
		webFXMenuHandler.all[userMenu.id].hide();
		webFXMenuHandler.all[sysAdminMenu.id].hide();
	}

	function popmenu(flag, src)
	{
		hideMenus();
		
		if (flag == 0)
		{
			if(rootMenuShowStatus=="rootmenu")
			{
				rootMenu.orgID = rootOrgID;
				rootMenu.treeid = src.id;
				webFXMenuHandler.showMenu(rootMenu, src);
			}
		}
		else if (flag == 1)
		{
			orgMenu.orgID = src.getAttribute("orgID");
			orgMenu.treeid = src.id;
			webFXMenuHandler.showMenu(orgMenu, src);
		}
		else if (flag == 2)
		{
			roleMenu.orgID = src.getAttribute("orgID");
			roleMenu.treeid = src.id;
			roleMenu.roleID = src.getAttribute("roleID");

			var roleNode = webFXTreeHandler.getNode(src.id);
			if (roleNode)
				roleMenu.roleName = roleNode.text;

			webFXMenuHandler.showMenu(roleMenu, src);
		}
		else if (flag == 3)
		{
			userMenu.orgID = src.getAttribute("orgID");
			userMenu.treeid = src.id;
			userMenu.userID = src.getAttribute("userID");
			webFXMenuHandler.showMenu(userMenu, src);
		}
		else if (flag == 4)
		{
			sysAdminMenu.orgID = src.getAttribute("orgID");
			sysAdminMenu.treeid = src.id;
			sysAdminMenu.userID = src.getAttribute("userID");
			webFXMenuHandler.showMenu(sysAdminMenu, src);
		}
	}
	function getFrame(name)
	{
		return document.getElementById(name);
	}
	var selID = null;
	function delSelObj(urlsrc,p,objtype)
	{
		var alertStr="";
		if(objtype==1)
		{
			alertStr="<i18n:message key="org.tree.del.org.confirm"/>";
		}
		else if(objtype==2)
		{
			alertStr="<i18n:message key="org.tree.del.role.confirm"/>";
		}
		else if(objtype==3)
		{
			alertStr="<i18n:message key="org.tree.del.user.confirm"/>";
		}

		if(!confirm(alertStr))
		{
			return;
		}
		
		//document.body.style.cursor = 'wait';
		ret =invokeGetXmlHttp(urlsrc,p.treeid);
		if(-1==ret)
		{
			alert("<i18n:message key="org.tree.del.neterr"/>");
		}
		else if(0==ret)
		{
			alert("<i18n:message key="org.tree.del.operr"/>");
		}
		//document.body.style.cursor = 'arrow';
	}

	var drag_src;
	var drag_dest;
	function doDrag(src){	drag_src = webFXTreeHandler.drag_src;return true;}
	function doDragOver(src){
		drag_dest = src;

		return true;
	 }
	function doDrop(src)
  {
	  //if(!drag_dest.catID)
		//	return false;
		var tgtNodeType= webFXTreeHandler.drag_dest.getAttribute("nodetype");
		var srcNoteType    = webFXTreeHandler.drag_src.getAttribute("nodetype");
		if(tgtNodeType=="3")
		{
			alert("<i18n:message key="org.tree.move.user.alert"/>");
			return;
		}
		if(tgtNodeType=="2")
		{
			alert("<i18n:message key="org.tree.move.role.alert"/>");
			return;
		}

		var dragUrl="";
		var tgtOrgID= webFXTreeHandler.drag_dest.getAttribute("orgID");
		var confirmMsg="<i18n:message key="org.tree.move.confirm"/>";

		if(tgtNodeType=="1" && srcNoteType=="1")
		{
			//move org to org
			var srcID=webFXTreeHandler.drag_src.getAttribute("orgID");
			dragUrl = "OrgMgrAction.do?invoke=moveOrg&SrcID="+srcID+"&TgtOrgID="+tgtOrgID;
			confirmMsg="<i18n:message key="org.tree.move.org.confirm"/>";
		}
		else if(tgtNodeType=="1" && srcNoteType=="2")
		{
			//move role to org
			var srcID=webFXTreeHandler.drag_src.getAttribute("roleID");
			dragUrl = "RoleMgrAction.do?invoke=moveRole&SrcID="+srcID+"&TgtOrgID="+tgtOrgID;
			confirmMsg="<i18n:message key="org.tree.move.role.confirm"/>";
		}
	else if(tgtNodeType=="1" && srcNoteType=="3")
		{
//move user to org
			var srcID=webFXTreeHandler.drag_src.getAttribute("userID");
			dragUrl = "UserMgrAction.do?invoke=moveUser&SrcID="+srcID+"&TgtOrgID="+tgtOrgID;
			confirmMsg="<i18n:message key="org.tree.move.user.confirm"/>";
		}


	 var ok = confirm(confirmMsg);
	 if(ok)
    {
			document.body.style.cursor = 'wait';
			invokeGetXmlHttpDo(dragUrl);
			document.body.style.cursor = 'arrow';
			return true;
		}
	}

	function retrieve()
	{
		var urlsrc="QueryOrgAction.do?invoke=openQueryForm";
		parent.mainBody.location.href=urlsrc;
	}

	function exportorg()
	{
	}
	var rolecopyhint = {
		
		okreferrence :	"<i18n:message key="org.role.copy.hint.okreferrence"/>",
		noreferrence : 	"<i18n:message key="org.role.copy.hint.noreferrence"/>",
		sure		 :	"<i18n:message key="org.role.copy.hint.sure"/>",
		same 		 : 	"<i18n:message key="org.role.copy.hint.same"/>"
	};
	
	showTree();

	function refreshCache() {
		$.ajax({type: "POST", url: "../../xy/system/Refresh.do", async:false, success: function (data) {
			if (data == "ok") {
				alert("刷新完成");
			} else {
				alert("操作失败：" + data);
			}
		}});
	}
</script>
</body>
</html>
