<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<html>
<head>
	<title><i18n:message key="fvpagelist.title"/></title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	
	<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
</head>
<body>
	<script type="text/javascript">
		function nodeClick(src){
			var fvID = src.getAttribute("fvID");
			if (!fvID) return;
						
			var docLibID = src.getAttribute("docLibID");
			var docTypeID = src.getAttribute("docTypeID");
						
			var theURL = "./FVListPage.do?FVID=" + fvID + "&DocTypeID=" + docTypeID;
			window.parent.frames["mainframe"].location.href = theURL;
		}
			
		webFXTreeConfig.rootPath = "../e5script/";
		webFXTreeConfig.defaultClickAction = "nodeClick(this);";
		webFXTreeConfig.defaultContextAction = "popmenu(this)";
		webFXTreeConfig.getFileIcon = webFXTreeConfig.getFolderIcon;
					
		var tree = new WebFXLoadTree("<i18n:message key="fvpagelist.tree.head"/>", "../e5dom/FolderTree.do");
		tree.show();
	</script>
</body>
</html>