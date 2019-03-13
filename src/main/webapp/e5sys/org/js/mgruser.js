
function addUserUnderOrg(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=add&OrgID="+p.orgID+"&treeid="+p.treeid+"&addNodeMode=under";
	parent.mainBody.location.href=urlsrc;

}
function addUserNodeUnderOrg(userName,newUserID,orgID,curTreeID)
{
	var node = webFXTreeHandler.getNode(curTreeID);;
	var a = new WebFXTreeItem(userName);
	a.click = "showUser('"+newUserID+"');";
	a.contextAction = "popmenu(3, this);return false;";
	a.setAttribute("userID", newUserID);
	a.setAttribute("orgID", orgID);
	a.setAttribute("nodetype", "3");
	a.icon = "../../images/user.gif";
	node.add(a);

}

function updateUser(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=update&UserID="+p.userID+"&treeid="+p.treeid;
	parent.mainBody.location.href=urlsrc;
	
}
function updateUserNode(userName,curTreeID)
{
	var treeXmlNode = document.getElementById(curTreeID);
	treeXmlNode.innerHTML=userName;
}	
function	addUserUnderRole(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=add&OrgID="+p.orgID+"&treeid="+p.treeid+"&RoleID="+p.roleID+"&addNodeMode=on";
	parent.mainBody.location.href=urlsrc;
}
function addUserByUser(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=add&OrgID="+p.orgID+"&treeid="+p.treeid+"&UserID="+p.userID+"&addNodeMode=on";
	parent.mainBody.location.href=urlsrc;
}
function addUserNodeUnderUser(userName,newUserID,orgID,curTreeID)
{
	var node = webFXTreeHandler.getNode(curTreeID);
	var parNode=node.parentNode;
	var a = new WebFXTreeItem(userName);
	a.click = "showUser('"+newUserID+"')";
	a.contextAction = "popmenu(3, this);return false;";
	a.setAttribute("userID", newUserID);
	a.setAttribute("orgID", orgID);
	a.setAttribute("nodetype", "3");
	a.icon = "../../images/user.gif";
	a.openIcon="../../images/user.gif";
	parNode.add(a);
}

function showUser(userid)
{
	parent.mainBody.location.href="UserMgrAction.do?invoke=UserFormList&UserID="+userid;
}

function showSysAdmin(userid)
{
	parent.mainBody.location.href="blank.htm";

}

function updateSysAdmin(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	parent.mainBody.location.href="UserMgrAction.do?invoke=SysAdminForm&UserID="+p.userID+"&treeid="+p.treeid;
}

function delUser(src)
{
	var p = webFXMenuHandler.getMainMenu(src);
	var urlsrc="UserMgrAction.do?invoke=delUser&UserID="+p.userID;
	delSelObj(urlsrc,p,3);
}
