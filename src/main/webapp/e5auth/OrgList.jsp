<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>

<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Style>
		input{border:0;}
	</Style>
	<Script type="text/javascript">
		/**
		 * 检查一个被选中的节点是否有祖宗节点已经被选中了
		 
		 * check if the ancestor node is checked.
		 */
		function parentChecked(srcNode)
		{
			var parentNode = srcNode.parentNode;
			while (parentNode != null)
			{
				var orgID = parentNode.getAttribute("orgID");
				if (permissionForm["manage-"+orgID] && permissionForm["manage-"+orgID].checked)
					return true;
				parentNode = parentNode.parentNode;
			}
			return false;
		}
		
		/**
		*清除所有能够看到的Checked为false
		*/
		function doClearChecked()
		{
			var mynode;
			var orgID;
			for (treenode in webFXTreeHandler.all)
			{
				mynode = webFXTreeHandler.all[treenode];
				if (mynode != null)
				{
					orgID = mynode.getAttribute("orgID");
					if (orgID != null && orgID != "")
						permissionForm["manage-"+orgID].checked = false;
				}
			}
		}
		/**
		 * 取所有被展开了的节点的ID串
		
		 * 过滤祖宗节点已被选中的情况，得到实际的选中节点ID串
		 
		 */
		function doSubmit(type)
		{
			if (type == 1)
			{
				var checkedIDs = "";
				var nIDs = "";
				var mynode;
				var orgID = "";
				for (treenode in webFXTreeHandler.all)
				{
					mynode = webFXTreeHandler.all[treenode];
					if (mynode != null)
					{
						orgID = mynode.getAttribute("orgID");
						if (orgID != null && orgID != "")
						{
							if (permissionForm["manage-" + orgID] 
								&& permissionForm["manage-"+ orgID].checked	&& !parentChecked(mynode))
								checkedIDs = checkedIDs + orgID + ",";
							else
								nIDs = nIDs + orgID + ",";
						}
					}
				}
				permissionForm.Clear.value = ""; 
				permissionForm.ManageIDs.value = checkedIDs;
				permissionForm.ManageNIDs.value = nIDs;
				permissionForm.submit();
			}
			else
			{
				doClearChecked();
				permissionForm.Clear.value = "Clear";
				permissionForm.submit();
			}
		}

		function nodeClick(id)
		{
			var mycheck = permissionForm["manage-" + id];
			if (mycheck != null) mycheck.checked = !(mycheck.checked);
		}
	</Script>
<Style>
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
</Style>
</HEAD>
<BODY>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<Form Name="permissionForm"  Target="iframe" Action="./submitPart.do" Method="Post">
		<Input Type="hidden" Name="Type" value="1">
		<Input Type="hidden" Name="ManageIDs" value="">
		<Input Type="hidden" Name="ManageNIDs" value="">
		<Input Type="hidden" Name="Clear" value="">
		<Script>
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function()
			{
				return "../images/org.gif";
			}

			webFXTreeConfig.cbRootable 	= false;
			webFXTreeConfig.cbCount 	= 1;
			webFXTreeConfig.cbLabels 	= ['<i18n:message key="perm.manage"/>'];
			webFXTreeConfig.cbPrefix 	= ["manage"];
			webFXTreeConfig.cbRefAttribute = "orgID";
			var tree = new WebFXLoadTree('<i18n:message key="org.root"/>',
					'./treeOrg.do?CurID=0');
			tree.show();
		</Script>
		<DIV>
		<input type="button" value="<i18n:message key="button.submit"/>" onclick="doSubmit(1)" class="button">
		<input type="button" value="<i18n:message key="button.clear"/>" onclick="doSubmit(2)" class="button">
		</DIV>
	</Form>
</BODY>
</HTML>
