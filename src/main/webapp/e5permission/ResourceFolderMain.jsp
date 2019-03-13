<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE><i18n:message key="resource.folder.title"/></TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
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
		body{margin: 0px;}
		form{margin:5px;}
		#nameDiv{
			background:url(../images/t-bar.gif);
			text-align:center;
			color:white;
		}
	</style>
	<Script type="text/javascript">
		/**
		 * 取所有被展开了的节点的ID串
		 */
		function doSubmit()
		{
			var ids = "";

			var mynode;
			var roleID;
			for (treenode in webFXTreeHandler.all)
			{
				mynode = webFXTreeHandler.all[treenode];
				if (mynode != null)
				{
					roleID = mynode.getAttribute("roleID");
					if (roleID) ids += roleID + ",";
				}
			}
			permissionForm.IDExpanded.value = ids;

			permissionForm.submit();
		}
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
	</Script>
</HEAD>

<BODY scroll="yes">
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<div class="caption"><i18n:message key="resource.folder.title"/> -- <i18n:message key="resource.folder.select"/>
	<c:out value="${FVName}"/>
	</div>
	<Form Name="permissionForm"  Target="iframe" Action="./submitFolderResource.do" Method="Post">
	<!--<font color="gray"><i18n:message key="resource.folder.hint"/></font>-->
		<Input Type="hidden" Name="FVID" Value="<c:out value="${param.FVID}"/>"/>
		<Input Type="hidden" Name="IDExpanded" Value=""/>

		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
		<BR/>
		<Script>
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function(){
				return "../images/org.gif";
			}

			webFXTreeConfig.cbRootable 	= false;
			webFXTreeConfig.cbCount 	= 3;
			webFXTreeConfig.cbLabels 	= [
					"<i18n:message key="folder.permission.read"/>",
					"<i18n:message key="folder.permission.transfer"/>",
					"<i18n:message key="folder.permission.deal"/>"];
			webFXTreeConfig.cbPrefix 	= ["read", "transfer", "deal"];
			webFXTreeConfig.cbRefAttribute = "roleID";

			var tree = new WebFXLoadTree("<i18n:message key="resource.tree.role"/>",
						"./treeFolderResource.do?OrgID=0&FVID=<c:out value="${param.FVID}"/>");
			tree.show();
		</Script>

		<BR>
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>

</BODY>
</HTML>
