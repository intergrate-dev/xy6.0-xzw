	
	function addOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="OrgMgrAction.do?invoke=openOrgForm&OrgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
	function exportOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="OrgExchangeAction.do?invoke=exportNode&orgID="+p.orgID;
		window.open(urlsrc);
	}
	function importOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="OrgExchangeAction.do?invoke=importNodeForm&orgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}

	function addOrgNode(orgName,newOrgID,curTreeID)
	{
		var node = webFXTreeHandler.getNode(curTreeID);;
		var a = new WebFXTreeItem(orgName);
		a.click = "showOrg('"+newOrgID+"');";
		a.contextAction = "popmenu(1, this);return false;";
		a.setAttribute("orgID", newOrgID);
		a.icon="../../images/org.gif";
		a.openIcon="../../images/org.gif";
		a.fileIcon="../../images/org.gif"
		a.setAttribute("nodetype", "1");
		node.add(a);
	
	}
	function updateOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="OrgMgrAction.do?invoke=updateOrgForm&OrgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
	function updateOrgNode(orgName,curTreeID)
	{
		var treeXmlNode = document.getElementById(curTreeID);
		treeXmlNode.innerHTML=orgName;
}	

	function moveOrg(src)
	{
		alert("moveOrg");
	}
	
	function sortOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="SortAction.do?invoke=sortOrgForm&OrgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
function sortRole(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="SortAction.do?invoke=sortRoleForm&OrgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
function sortUser(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="SortAction.do?invoke=sortUserForm&OrgID="+p.orgID+"&treeid="+p.treeid;
		parent.mainBody.location.href=urlsrc;
	}
	
	function showOrg(orgid)
	{
		//点击部门，则右侧显示该部门下的用户。
		//本功能可能在系统管理中也可能在前端，是前端时还需要加siteID限制。
		//把部门ID用groupID参数传入，则新建用户时可按这个参数设置部门。
		var url = "../../e5sys/DataMain.do?type=USEREXT&groupID=" + orgid;
		var rule = "u_orgID_EQ_" +orgid;
		if (siteID) {
			url += "&siteID=" + siteID;
			rule += "_AND_u_siteID_EQ_" + siteID;
		}
		url += "&rule=" + rule;
		
		parent.mainBody.location.href = url;
	}
	function reLoadChildTree(curTreeID)
	{
		var node = webFXTreeHandler.getNode(curTreeID);
		var orgID = node.getAttribute("orgID");
		if(!orgID)
		{
			reloadRoot();
			return;
		}
		var cs = node.childNodes;
		var l = cs.length;
		if(l==0)
		{
			return;
		}
		if(node.reload)
		{
			node.reload();
		}
	}
	function reloadOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		reLoadChildTree(p.treeid);
	}
	function reloadRoot()
	{
		document.location.href="OrgTreeGenerate.do?invoke=rootNode";
	}
	
	function delOrg(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="OrgMgrAction.do?invoke=delOrg&OrgID="+p.orgID;
		delSelObj(urlsrc,p,1);
	}
	/**==========
		设置机构的缺省目录
	============*/
	function setDefaultFolder(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
		var urlsrc="DefaultFolder.do?OrgID=" + p.orgID;
		parent.mainBody.location.href=urlsrc;
	}

	function folderMapping(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);
	    var urlsrc = "./FolderMapping.jsp?OrgID=" + p.orgID;
	    parent.mainBody.location.href = urlsrc;
	}
