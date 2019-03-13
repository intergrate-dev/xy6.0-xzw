<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
	<head>
		<title>Tree</title>
		<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
		<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	</head>
	<body>
		<div class="mainBodyWrap">
		<script type="text/javascript">
			webFXTreeConfig.rootPath = "../../e5script/";
			webFXTreeConfig.getFileIcon = webFXTreeConfig.getFolderIcon;
			webFXTreeConfig.defaultClickAction = "nodeClick(this);";

			var tree = new WebFXLoadTree("<i18n:message key="org.foldermapping.root"/>",
						"../../e5dom/FolderTree.do");
			tree.show();

			function nodeClick(src){
				var fvID = src.getAttribute("fvID");
				if (!fvID) return;

				postForm.fvID.value = fvID;
			}
			function doSubmit(){
				if (!postForm.fvID.value)
				{
					alert("<i18n:message key="org.foldermapping.hint"/>");
					return;
				}
			   postForm.submit();
			   
			   setTimeout(function(){window.location.reload();}, 500); 
			}
		</script>
		<iframe id="iframe" name="iframe" style="display:none"></iframe>
		<form name="postForm"  target="iframe" action="./FolderMapping.do" method="Post">
			<input type="hidden" name="OrgID" value="<c:out value="${param.OrgID}"/>">
			<input type="hidden" name="fvID" value=""/>
			
			<input type="checkbox" name="OrgInclude" id="OrgInclude"/>
			<label for="OrgInclude"><i18n:message key="org.foldermapping.orginclude"/></label>
			<br/><br/>
			<input type="button" onclick="doSubmit()" AccessKey="S" class="button" value="<i18n:message key="org.submit"/>"/>
		</form>
		</div>
	</body>
</html>
