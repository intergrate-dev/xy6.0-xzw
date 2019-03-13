<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE><i18n:message key="resource.flow.title"/></TITLE>
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>

	<style type="text/css">
		<%for (int i = 0; i <= ((Integer)request.getAttribute("procCount")).intValue(); i++) {
			out.println(".webfx-tree-checkbox" + i + "{margin-left:20px;}");
			out.println(".webfx-tree-label" + i + "{margin-left:2px;}");
		}%>
		input{border: 0;}
		form{margin: 5px;}
	</style>
</HEAD>

<BODY>
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<Table class="table">
		<caption><c:choose>
			<c:when test="${flag == 0}">
				<i18n:message key="flow.caption"/> -- <i18n:message key="resource.flow.select"/>
				<c:out value="${showName}"/>
			</c:when>
			<c:when test="${flag == 1}">
				<i18n:message key="resource.unflow.title"/>
			</c:when>
		</c:choose></caption>
	</Table>
	<Form Name="permissionForm"  Target="iframe" Action="./submitFlowFVUserResource.do" Method="Post">
		<DIV><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
		<BR>

		<Input Type="hidden" Name="flag" Value="<c:out value="${flag}"/>"/>
		<Input Type="hidden" Name="targetID" Value="<c:out value="${targetID}"/>"/>
		<Input Type="hidden" Name="fvID" Value="<c:out value="${fvID}"/>"/>
		<Input Type="hidden" Name="procIDs" Value=""/>
		<Input Type="hidden" Name="IDExpanded" Value=""/>

		<script type="text/javascript">
			var procCount = <c:out value="${procCount}"/>;
			//tree
			webFXTreeConfig.rootPath = "../e5script/";
			webFXTreeConfig.getFolderIcon = webFXTreeConfig.getOpenFolderIcon = webFXTreeConfig.getFileIcon = function(){
				return "../images/org.gif";
			}
			//自动展开第一层所有节点
			function expandAll() {
				tree.expandAll();
			}
			function blankFunc(){}
			webFXTreeConfig.loadCallBack = function(){
				webFXTreeConfig.loadCallBack = blankFunc;
				var x = expandAll();
			}

			webFXTreeConfig.cbRootable 	= false;
			//以每个操作的名称作为checkbox的label
			webFXTreeConfig.cbLabels = [];
			var procIDs = [];
			<c:forEach var="proc" items="${procs}" varStatus="status">
				webFXTreeConfig.cbLabels.push("<c:out value="${proc.procName}"/>");
				procIDs.push("<c:out value="${proc.procID}"/>");
			</c:forEach>
			
			//checkbox的个数
			webFXTreeConfig.cbCount = webFXTreeConfig.cbLabels.length;

			webFXTreeConfig.cbPrefix = [];
			//checkbox的命名前缀，是从0开始的数字. '2-3',后面是roleID
			for (var i = 0; i < procCount; i++)
				webFXTreeConfig.cbPrefix[i] = "p" + i;

			webFXTreeConfig.cbRefAttribute = "roleID";

			var theTreeURL = "./treeFlowFVUserResource.do?flag=<c:out value="${flag}"/>"
					+ "&fvID=<c:out value="${fvID}"/>"
					+ "&procIDs=" + procIDs.join();
			var tree = new WebFXLoadTree("<i18n:message key="resource.tree.role"/>", theTreeURL);
			tree.show();

			function doSubmit() {
				var ids = "";

				var mynode;
				var roleID;
				for (treenode in webFXTreeHandler.all) {
					mynode = webFXTreeHandler.all[treenode];
					if (mynode != null) {
						roleID = mynode.getAttribute("roleID");
						if (roleID) ids += roleID + ",";
					}
				}
				permissionForm.IDExpanded.value = ids;
				permissionForm.procIDs.value = procIDs.join();

				permissionForm.submit();
			}
			function nodeClick(id) {
				var mycheck = permissionForm["p0-" + id];
				if (mycheck == null) return;
				var setcheck = !mycheck.checked;

				for (var i = 0; i < procCount; i++) {
					var mycheck = permissionForm["p" + i + "-" + id];
					if (mycheck != null) mycheck.checked = setcheck;
				}
			}
		</Script>
		<br>
		<div><input type="button" class="button" name="btnSubmit" onclick="doSubmit()" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>
</BODY>
</HTML>
