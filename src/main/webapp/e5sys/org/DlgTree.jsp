<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title>Tree</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
		<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/rest.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
	</head>
	<body>
	<script type="text/javascript">
		//tree
		webFXTreeConfig.rootPath = "../../e5script/";
		webFXTreeConfig.defaultTarget = "showItem";
		
		var meld="<c:out value="${treeobj.method}"/>";
		if(meld=="folderNode")
		{

			webFXTreeConfig.defaultAction = "#";
		}

		webFXTreeConfig.multiple = false;

		webFXTreeConfig.defaultContextAction = "return false;";
		var paraSrc="<c:out value="${treeobj.appParaMeter}"/>";
		var i = paraSrc.indexOf("&amp;");
		while(i!=-1) 
		{
			var head=paraSrc.substring(0,i);
			var tail=paraSrc.substring(i+5);
			paraSrc = head+"&"+tail;
			i=paraSrc.indexOf("&amp;");
		}

		//扩展docLibID参数
		paraSrc = paraSrc+"&docLibID=<c:out value="${param.docLibID}"/>";

		var subTreeUrl="<c:out value="${treeobj.rootUrl}"/>?invoke=<c:out value="${treeobj.method}"/>"+paraSrc;
		
		var tree = new WebFXLoadTree("<c:out value="${treeobj.rootName}"/>",subTreeUrl);

		tree.contextAction = "return false;";
		tree.setBehavior('classic');

		function showTree()
		{
			if (document.getElementById)
			{
				document.write(tree);
			}
		}
		

		function getFrame(name)
		{
			return document.getElementById(name);
		}
		var selID = null;
			//下面两种方式都可以

			
		showTree();
		var entryPara="<c:out value="${treeobj.method}"/>";
		var selid=-1;
		var attr_id=-1;
		function selectRole()
		{
				var node = tree.getSelected();
				if(selid!=-1)
				{
					window.returnValue=selid;
					window.close();
				}

		}
			function showRole(roleid)
			{
				selid=roleid;
				parent.getRoleVaildForm(roleid);
			}

			function showUser(userid)
			{
				selid=userid;
				parent.getRoleVaildForm(userid);
			}
			function showOrg(orgid)
			{
				if("orgRoleNode"==entryPara)
				{
					alert("<i18n:message key="org.user.select.role.alert"/>");
				}
				else if(("orgUserNode"==entryPara))
				{
					alert("<i18n:message key="org.role.select.user.alert"/>");
				}
				else
				{
					alert("<i18n:message key="org.folder.tree.select.alert"/>");
				}
			}
			function showFolder(folderid,doclibid)
			{
				selid=folderid;
				attr_id=doclibid;
				if(attr_id==-1)
				{
					alert("<i18n:message key="org.folder.tree.select.alert"/>");
					return;
				}
				return;
			}
	</script>
	</body>
</html>
