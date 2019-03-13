<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Tree</TITLE>
	<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
</HEAD>
<BODY>
<Script type="text/javascript">
	//tree
	webFXTreeConfig.rootPath = "../../e5script/";
	webFXTreeConfig.defaultTarget = "showItem";
	webFXTreeConfig.defaultAction = "#";
	webFXTreeConfig.multiple = false;

	webFXTreeConfig.defaultContextAction = "return false;";

	<c:forEach items="${liblist}" var="treeobj">

		var paraSrc="<c:out value="${treeobj.appParaMeter}"/>";
		var i = paraSrc.indexOf("&amp;");
		while(i!=-1)
		{
			var head=paraSrc.substring(0,i);
			var tail=paraSrc.substring(i+5);
			paraSrc = head+"&"+tail;
			i=paraSrc.indexOf("&amp;");
		}

		var subTreeUrl="<c:out value="${treeobj.rootUrl}"/>?invoke=<c:out value="${treeobj.method}"/>"+paraSrc;

		var tree = new WebFXLoadTree("<c:out value="${treeobj.rootName}"/>",subTreeUrl);

		tree.contextAction = "return false;";
		tree.setBehavior('classic');
		tree.click="showFolder('<c:out value="${treeobj.folderID}"/>','<c:out value="${treeobj.docLibID}"/>');";

		if (document.getElementById)
		{
			document.write(tree);
		}
	</c:forEach>
	function getFrame(name)
	{
		return document.getElementById(name);
	}
</Script>
<script type="text/javascript">

var entryPara="<c:out value="${treetype}"/>";
var selid="";
var attr_id="";
function showFolder(folderid,doclibid)
{
	selid=folderid;
	attr_id=doclibid;

	if(attr_id=="")
	{
		alert("<i18n:message key="org.folder.tree.select.alert"/>");
		return;
	}
	if(entryPara=="pending")
	{
		//递交时调用
		parent.dealerFrame.location.href="ListUser.do?invoke=getFolderUser&FolderID="+selid;
	}
	return;
}
</script>
</BODY>
</HTML>
