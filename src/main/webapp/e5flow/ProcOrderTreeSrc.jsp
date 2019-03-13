<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
<link 	type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
	<style type="text/css">
	body{
		background-color:#EEEEEE;
		margin-left : 3px;
		margin-top: 3px;
	}
	</style>
<Script type="text/javascript">
	function treeInit(){
		try {
			if (tree.childNodes.length == 0) return;

			var node1 = webFXTreeHandler.getPlus(tree.childNodes[0].id);
			if (node1) 	node1["onclick"]();
		}catch (e){
			alert(e.message);
		}
		return true;
	}
	//回调函数,打开第一层子树后,自动点击第一个子树节点.
	webFXTreeConfig.loadCallBack = function(){
		var x = treeInit();
		webFXTreeConfig.loadCallBack = function(){};
	}
	//tree
	webFXTreeConfig.rootPath = "../e5script/";
	webFXTreeConfig.defaultTarget = "showItem";
	webFXTreeConfig.multiple = false;

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	var tree = new WebFXLoadTree("<i18n:message key="operation.procorder.contentTitle"/>",
		"listDocType.do?subType=order");
	tree.setBehavior('classic');
	function showTree()
	{
		if (document.getElementById)
			document.write(tree);
	}
	function getFrame(name)
	{
		return window.parent.frames[1];
	}
	function changeProc(src)
	{
		var cn = getFrame('showItem');
		cn.location = "ProcOrder.do?docTypeID=" + src.getAttribute("docTypeID")
			+ "&flowID=" + src.getAttribute("flowID")
			+ "&flowNodeID=" + src.getAttribute("flowNodeID");
	}
	function unflowClick(src)
	{
		var cn = getFrame('showItem');
		cn.location = "ProcOrder.do?docTypeID=" + src.getAttribute("docTypeID");
	}
	function noDocClick(src)
	{
		var cn = getFrame('showItem');
		cn.location = "ProcOrder.do?docTypeID=-1";
	}
	function docClick(src){}
	function flowClick(src){}
	showTree();

</script>
