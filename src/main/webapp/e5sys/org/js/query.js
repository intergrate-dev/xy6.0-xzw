function updateOrg(orgid)
{
		var urlsrc="OrgMgrAction.do?invoke=updateOrgForm&OrgID="+orgid+"&treeid=-1"
		window.open(urlsrc,"_blank", "width=300,height=200");
}

function updateObj(objname,objid)
{
	if(objname=="user")
	{
		updateUser(objid);
	}
	else if(objname=="role")
	{
		updateRole(objid);

	}
	else if(objname=="org")
	{
		updateOrg(objid);
	}
}

function updateUser(userid)
{
	var userUrl="UserMgrAction.do?invoke=UserForm&OpType=update&UserID="+userid+"&treeid=-1"
	window.open(userUrl,"_blank", "width=420,height=400");
}
function showUser(userid)
{
	var userUrl="UserMgrAction.do?invoke=UserFormList&UserID="+userid+"&treeid=-1";
	window.open(userUrl,"_blank", "width=620,height=560");
}
function updateRole(roleid)
{
	var urlsrc="RoleMgrAction.do?invoke=updateRoleForm&RoleID="+roleid+"&treeid=-1";
	window.open(urlsrc,"_blank", "width=320,height=100");

}



