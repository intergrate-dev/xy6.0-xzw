<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page import="com.founder.e5.flow.FlowNode" %>
<HTML>
<HEAD>
	<TITLE><i18n:message key="resource.flow.title"/></TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>

	<style type="text/css">
		<%
			for (int i = 0; i <= ((Integer)request.getAttribute("ProcCount")).intValue(); i++) {
				out.println(".webfx-tree-checkbox" + i + "{margin-left:20px;}");
				out.println(".webfx-tree-label" + i + "{margin-left:2px;}");
			}
			//若是第一个节点，则要显示“新建文档”权限，把这个名称用蓝色，以示区别
			//“读”权限也是蓝色表示，在下面的.webfx-tree-label0中有专门定义
			if (((FlowNode)(request.getAttribute("FlowNode"))).getPreNodeID() == 0)
			{
				out.println(".webfx-tree-label1{color:blue;}");
			}
		%>
		input{border: 0;}
		form{margin: 5px;}
		.webfx-tree-label0{color:blue;}
		#nameDiv{
			padding-top:5px;
			height:32px;
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
		var procCount = <c:out value="${ProcCount}"/> + 1; // + 1是读权限
	</Script>
</HEAD>

<BODY>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<div id="nameDiv">
	<i18n:message key="flow.caption"/> -- <i18n:message key="resource.flow.select"/>
	<c:out value="${FlowNode.name}"/>
	</div>
	<Form Name="permissionForm"  Target="iframe" Action="./submitFlowResource.do" Method="Post">
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
		<BR>

		<Input Type="hidden" Name="FlowNodeID" Value="<c:out value="${param.FlowNodeID}"/>"/>
		<Input Type="hidden" Name="ProcCount" Value="<c:out value="${ProcCount}"/>"/>
		<Input Type="hidden" Name="IDExpanded" Value=""/>

		<Script>
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function(){
				return "../images/org.gif";
			}

			webFXTreeConfig.cbRootable 	= false;
			//以每个流程操作的名称作为checkbox的label
			webFXTreeConfig.cbLabels 	= [
					//read
					"<i18n:message key="flow.read"/>"
					//new or back
					<c:choose>
						<c:when test="${FlowNode.preNodeID == 0}">
							,"<i18n:message key="flow.newDoc"/>"
						</c:when>
						<c:when test="${BackName != null}">
							,"<c:out value="${BackName}"/>"
						</c:when>
					</c:choose>
					//do
					<c:if test="${DoName != null}">
						,"<c:out value="${DoName}"/>"
					</c:if>
					//go
					<c:if test="${GoName != null}">
						,"<c:out value="${GoName}"/>"
					</c:if>
					//jumps
					<c:forEach var="jump" items="${Jumps}" varStatus="status">
						,"<c:out value="${jump.procName}"/>"
					</c:forEach>
					];
			//checkbox的个数

			webFXTreeConfig.cbCount 	= webFXTreeConfig.cbLabels.length;

			webFXTreeConfig.cbPrefix 	= [];
			//checkbox的命名前缀，是从0开始的数字. '2-3',后面是roleID
			for (var i = 0; i < procCount; i++)
				webFXTreeConfig.cbPrefix[i] = "p" + i;

			webFXTreeConfig.cbRefAttribute = "roleID";

			var tree = new WebFXLoadTree("<i18n:message key="resource.tree.role"/>",
						"./treeFlowResource.do?OrgID=0&FlowID=<c:out value="${param.FlowID}"/>&FlowNodeID=<c:out value="${param.FlowNodeID}"/>"
						);
			tree.show();
		</Script>

		<BR>
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>

</BODY>
</HTML>
