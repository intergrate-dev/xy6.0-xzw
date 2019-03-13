<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>

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
				var catID = parentNode.getAttribute("catID");
				if (permissionForm["show-" + catID] && permissionForm["show-" + catID].checked)
					return true;
				parentNode = parentNode.parentNode;
			}
			return false;
		}

		/**
		 * 取所有被展开了的节点的ID串
		 * 过滤祖宗节点已被选中的情况，得到实际的选中节点ID串
		 */
		function doSubmit()
		{
			if (tree.childNodes.length == 0) return ;

			var ids = "";
			var checkedIDs = "";

			var mynode;
			var catID;
			for (treenode in webFXTreeHandler.all)
			{
				mynode = webFXTreeHandler.all[treenode];
				if (mynode != null)
				{
					catID = mynode.getAttribute("catID");
					if (catID && (catID != ""))
					{
						ids += catID + ",";
						if (permissionForm["show-" + catID] && permissionForm["show-" + catID].checked
							&& !parentChecked(mynode))
						{
							checkedIDs += catID + ",";
						}
					}
				}
			}
			permissionForm.IDExpanded.value = ids;
			permissionForm.IDChecked.value = checkedIDs;

			permissionForm.submit();
		}
		function nodeClick(id)
		{
			var mycheck = permissionForm["show-" + id];
			if (mycheck != null) mycheck.checked = !(mycheck.checked);
		}
	</Script>
<Style>
	select{width:300px;}
	#buttondiv{position:absolute; left: 300px; top:10px;}
</Style>
</HEAD>
<BODY>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<Form Name="permissionForm"  Target="iframe" Action="./submitCat.do" Method="Post">
		<Input Type="hidden" Name="CatTypeID" value="<c:out value="${param.CatTypeID}"/>">
		<Input Type="hidden" Name="RoleID" value="<c:out value="${sessionScope.permissionRoleID}"/>">
		<Input Type="hidden" Name="IDExpanded" value="">
		<Input Type="hidden" Name="IDChecked" value="">
		<DIV id="buttondiv">
			<input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/>
		</DIV>
		<Script>
			var catTypeName = "<c:out value="${param.CatTypeName}"/>";
			var catTypeID = "<c:out value="${param.CatTypeID}"/>";
			if (!catTypeName) catTypeName = "<c:out value="${CatTypeName}"/>";
			if (!catTypeID) catTypeID = "<c:out value="${CatTypeID}"/>";
			
			permissionForm["CatTypeID"].value = catTypeID;
			//tree
			webFXTreeConfig.rootPath = "../e5script/";

			webFXTreeConfig.cbRootable 	= false;
			webFXTreeConfig.cbCount 	= 1;
			webFXTreeConfig.cbLabels 	= [];
			webFXTreeConfig.cbPrefix 	= ["show"];
			webFXTreeConfig.cbRefAttribute = "catID";

			var tree = new WebFXLoadTree(catTypeName,
					"./treeCat.do?CatID=0&CatTypeID=" + catTypeID);
			tree.show();
		</Script>
	</Form>
</BODY>
</HTML>
