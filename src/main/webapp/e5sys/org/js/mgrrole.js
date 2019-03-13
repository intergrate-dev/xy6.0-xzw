	function addRoleUnderOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="RoleMgrAction.do?invoke=addRoleForm&OrgID="+p.orgID+"&treeid="+p.treeid+"&AddType=underorg";
		parent.mainBody.location.href=urlsrc;
		
	}
//在资源树中增加新建角色
	function addRoleNode(roleName,newRoleID,curTreeID,orgID)
	{
		var node = webFXTreeHandler.getNode(curTreeID);;
		var a = new WebFXTreeItem(roleName);
		a.click = "showRole('"+newRoleID+"');";
		a.contextAction = "popmenu(2, this);return false;";
		a.setAttribute("roleID", newRoleID);
		a.setAttribute("orgID", orgID);
		a.setAttribute("icon", "../../images/role.gif");
		a.setAttribute("openIcon", "../../images/role.gif");
		a.setAttribute("nodetype", "2");
		a.icon = "../../images/role.gif";
		node.add(a);
	
		}

//更资源树角色节点	
	function updateRoleNode(roleName,curTreeID)
	{
			var treeXmlNode = document.getElementById(curTreeID);
			treeXmlNode.innerHTML=roleName;
	}	
		
	function addRoleUnderRole(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="RoleMgrAction.do?invoke=addRoleForm&OrgID="+p.orgID+"&treeid="+p.treeid+"&AddType=underrole";
		parent.mainBody.location.href=urlsrc;
	
	}
	function addRoleNodeByRole(roleName,newRoleID,curTreeID,orgID)
	{
		var node = webFXTreeHandler.getNode(curTreeID);
		var parNode=node.parentNode;
		var a = new WebFXTreeItem(roleName);
		a.click = "showRole('"+newRoleID+"');";
		a.contextAction = "popmenu(2, this);return false;";
		a.setAttribute("roleID", newRoleID);
		a.setAttribute("orgID", orgID);
		a.setAttribute("nodetype", "2");
		a.setAttribute("icon", "../../images/role.gif");
		a.setAttribute("openIcon", "../../images/role.gif");
		a.icon = "../../images/role.gif";
		a.openIcon="../../images/role.gif";
		parNode.add(a);

	}
//更新角色内容	
	function updateRole(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="RoleMgrAction.do?invoke=updateRoleForm&RoleID="+p.roleID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
//显示角色内容	
	function showRole(rolid)
	{
		//var urlsrc="RoleMgrAction.do?invoke=roleFormList&RoleID="+rolid;
		var urlsrc="../../e5permission/listPage.do?RoleID=" + rolid;
		parent.mainBody.location.href=urlsrc;
	}

//权限管理
	function authorityMgr(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="../../e5permission/listPage.do?RoleID="+p.roleID;
		parent.mainBody.location.href=urlsrc;
		
	}
//设置部门管理员
	function setInsAdministrotor(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="../../e5auth/authInit.do?RoleID="+p.roleID;
		parent.mainBody.location.href=urlsrc;
	}
	
	function delRole(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="RoleMgrAction.do?invoke=delRole&RoleID="+p.roleID;
		delSelObj(urlsrc,p,2);
	}
//作为参考角色-----
	var refRoleID;
	var refRoleName;
	function roleReferrence(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		refRoleID = p.roleID;
		refRoleName = p.roleName;
		alert(rolecopyhint.okreferrence);
	}
//设置参考权限
	function roleCopy(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		if (!refRoleID)
		{
			alert(rolecopyhint.noreferrence);
			return;
		}
		if (p.roleID == refRoleID)
		{
			alert(rolecopyhint.same);
			return;
		}
		
		if (window.confirm(rolecopyhint.sure + refRoleName))
		{		
			var urlsrc="../../e5permission/copy.do?SrcRoleID=" + refRoleID + "&DestRoleID=" + p.roleID;
			parent.mainBody.location.href = urlsrc;
		}
	}
