<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Tree</TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		.webfx-tree-checkbox0 {
			position: absolute; left: 230px;
		}
		.webfx-tree-label0 {
			position: absolute; left: 250px;
		}
		.webfx-tree-checkbox1 {
			position: absolute; left: 300px;
		}
		.webfx-tree-label1 {
			position: absolute; left: 320px;
		}

		.webfx-tree-checkbox2 {
			position: absolute; left: 370px;
		}
		.webfx-tree-label2 {
			position: absolute; left: 390px;
		}
		input{border: 0;}
		body{margin: 5px;}
	</style>
	<Script>
		function nodeClick(id)
		{
			var mycheck = permissionForm["read-" + id];
			if (mycheck == null) return;
			var setcheck = !mycheck.checked;

			mycheck.checked = setcheck;

			mycheck = permissionForm["transfer-" + id];
			if (mycheck != null) mycheck.checked = setcheck;

			mycheck = permissionForm["deal-" + id];
			if (mycheck != null) mycheck.checked = setcheck;
		}
		function doSubmit()
		{
			if (tree.childNodes.length == 0) return ;

			var ids = "";

			var mynode;
			var fvID;
			for (treenode in webFXTreeHandler.all)
			{
				mynode = webFXTreeHandler.all[treenode];
				if (mynode != null)
				{
					fvID = mynode.getAttribute("fvID");
					if (fvID)
						ids += fvID + ",";
				}
			}
			permissionForm.IDExpanded.value = ids;
			permissionForm.submit();
		}
	</Script>
</HEAD>

<BODY>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<Form Name="permissionForm"  Target="iframe" Action="./submitFolder.do" Method="Post">
		<Input Type="hidden" Name="RoleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
		<Input Type="hidden" Name="IDExpanded" Value=""/>
		<Script>
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFileIcon = webFXTreeConfig.getFolderIcon;

			webFXTreeConfig.cbRootable 	= false;
			webFXTreeConfig.cbCount 	= 3;
			webFXTreeConfig.cbLabels 	= [
					"<i18n:message key="folder.permission.read"/>",
					"<i18n:message key="folder.permission.transfer"/>",
					"<i18n:message key="folder.permission.deal"/>"];
			webFXTreeConfig.cbPrefix 	= ["read", "transfer", "deal"];
			webFXTreeConfig.cbRefAttribute = "fvID";

			var tree = new WebFXLoadTree("<i18n:message key="folder.permission.title"/>",
						"./treeFolder.do?DocLibID=<c:out value="${docLibID}"/>");

			tree.show();
		</Script>

		<BR>
		<DIV><input type="button" onclick="doSubmit()" AccessKey="S" class="button" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>

</BODY>
</HTML>
