<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Resource Flow Tree</TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>

	<style>
		.webfx-tree-checkbox {position: absolute;}
		.webfx-tree-label    {position: absolute;}
		<%
			for (int i = 0; i <= ((Integer)request.getAttribute("ProcCount")).intValue(); i++)
			{
				out.println(".webfx-tree-checkbox" + i + "{left: " + (230 + i * 100) + "px;}");
				out.println(".webfx-tree-label" + i + "{left: " + (250 + i * 100) + "px;}");
			}
		%>
		input{border: 0;}
		body{margin: 0px;}
		form{margin: 5px;}
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
			var mycheck = permissionForm["p0-" + id];
			if (mycheck == null) return;
			var setcheck = !mycheck.checked;

			for (var i = 0; i < procCount; i++)
			{
				var mycheck = permissionForm["p" + i + "-" + id];
				if (mycheck != null) mycheck.checked = setcheck;
			}
		}
		var procCount = <c:out value="${ProcCount}"/> ;
	</Script>
</HEAD>

<BODY>
	<%@include file="../e5include/Error.jsp"%>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<div id="nameDiv"><i18n:message key="resource.unflow.title"/></div>
	<Form Name="permissionForm"  Target="iframe" Action="./submitUnflowResource.do" Method="Post">
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
		<BR>
		<Input Type="hidden" Name="DocTypeID" Value="<c:out value="${param.DocTypeID}"/>"/>
		<Input Type="hidden" Name="ProcCount" Value="<c:out value="${ProcCount}"/>"/>
		<Input Type="hidden" Name="IDExpanded" Value=""/>

		<Script>
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function(){
				return "../images/org.gif";
			}

			webFXTreeConfig.cbRootable 	= false;
			//以每个非流程操作的名称作为checkbox的label
			webFXTreeConfig.cbLabels 	= [];
			<c:forEach var="proc" items="${procs}" varStatus="status">
				webFXTreeConfig.cbLabels[<c:out value="${status.index}"/>]
					= "<c:out value="${proc.procName}"/>";
			</c:forEach>
			//checkbox的个数

			webFXTreeConfig.cbCount 	= webFXTreeConfig.cbLabels.length;

			webFXTreeConfig.cbPrefix 	= [];
			//checkbox的命名前缀，是从0开始的数字. '2-3',后面是roleID
			for (var i = 0; i < procCount; i++)
				webFXTreeConfig.cbPrefix[i] = "p" + i;

			webFXTreeConfig.cbRefAttribute = "roleID";

			var tree = new WebFXLoadTree("<i18n:message key="resource.tree.role"/>",
						"./treeUnflowResource.do?OrgID=0&DocTypeID=<c:out value="${param.DocTypeID}"/>"
						);
			tree.show();
		</Script>

		<BR>
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>

</BODY>
</HTML>
