<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
<link type="text/css" rel="stylesheet" href="../e5script/xtree/xtree.css"/>
<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
<link 	type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css"/>
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

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";

	//menu
	webFXMenuConfig.useHover	= false;
	webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
	webFXMenuConfig.hideTime	= 200;
	webFXMenuConfig.showTime	= 0;

	var tree = new WebFXLoadTree("<i18n:message key="docTree.root"/>",
				"listDocType.do");
	tree.setBehavior('classic');

	var docTypeMenu = null;
	var flowMenu 	= null;
	var flowNodeMenu = null;
	var unflowMenu 	= null;
	var currDocType;
	var currFlow;
	var treeID;
	var currNode;
	var currProc;
	var currID;
	function showTree() {
		document.write(tree);
	}
	//右键菜单的“刷新”
	function reloadFlow(){
	
		//addFlow();
		listFlow();
		document.location.reload();
	}
	function refresh(src)
	{
		var p = webFXMenuHandler.getMainMenu(src);

		var node = webFXTreeHandler.getNode(p.treeID);
		if(node.reload)
			node.reload();
	}
	//右窗口的回调
	function refreshNode()
	{
		if (!treeID) treeID = currID;
		
		var src = webFXTreeHandler.getNode(treeID);
		if (!src) {
			treeID = currID;
			src = webFXTreeHandler.getNode(treeID);
		}
		if(src.reload)
			src.reload();
		else {
			getRef(src)
		}
	}
	//用右键菜单调用的窗口的回调

	function refreshUpNode()
	{
		if(!treeID) treeID = currID;
		var src = webFXTreeHandler.getNode(treeID);
		if (!src)
		{
			if(treeID == currID) return false;
			treeID = currID;
			src = webFXTreeHandler.getNode(treeID);
		}
		var newsrc = src.parentNode;
		if (newsrc && newsrc.reload)
		{
			treeID = newsrc.id;
			currID = treeID;
			newsrc.reload();
		}
	}
	function getRef(src)
	{
		src = src.parentNode;
		if (!src) return;
		
		if(src.reload) {
			if (!src.parentNode) return;//最上层节点，不刷新
			src.reload();
		}
		else
			getRef(src);
	}
	function getFrame(name)
	{
		return window.parent.frames[1];
	}
	//右键菜单的“增加流程”

	function addFlow()
	{
		treeID = currID;
		var cn = getFrame('showItem');
		var docTypeID = currDocType;
		//cn.location = 'Flow.do?docTypeID='+docTypeID;

		var url = 'FlowVisualDefine.jsp?docTypeID='+docTypeID;

		cn.location = url;
		
	}
	//右键菜单的“流程一览”

	function listFlow()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		var docTypeID = currDocType;
		cn.location = 'listFlow.do?docTypeID='+docTypeID;
		//cn.location.reload();
	}
	//右键菜单的“流程节点一览”

	function listFlowNode()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = 'listNodes.do?flowID='+currFlow;
	}
	//右键菜单的“增加流程节点”

	function addFlownode()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = 'FlowNode.do?flowID='+currFlow;
	}
	//右键菜单的“删除流程节点”

	function deleteNode()
	{
		treeID = currID;

		var ret = window.confirm("<i18n:message key="operation.flownode.confirm"/>");
		if(ret==false)
		{
			return false;
		}
		var cn = getFrame('showItem');
		cn.location = 'FlowNodeSubmit.do?del=1&flowID='+currFlow+'&flowNodeID='+currNode+"&second=1";

	}
	//右键菜单的“增加流程操作”

	function addProc()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = "Proc.do?flowNodeID=" + currNode+"&flowID="+currFlow+"&docTypeID="+currDocType;
	}
	//右键菜单的“流程操作一览”

	function listProc()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = "listProc.do?flowNodeID=" + currNode+"&flowID="+currFlow+"&docTypeID="+currDocType;
	}
	//右键菜单的“非流程操作一览”

	function listUnflowProc()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = "listProc.do?docTypeID="+currDocType;
	}
	//右键菜单的“增加非流程操作”

	function addUnflowProc()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location = "Proc.do?type=unflow&docTypeID="+currDocType+"&flowID=0&flowNodeID=0";
	}
	
	//原：右键菜单的“设置非流程权限”
	function setDocPermission()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		cn.location="../e5permission/permissionUnflowResource.do?DocTypeID="+currDocType;
	}
	
	//原：右键菜单的“设置流程权限”
	function setNodePermission()
	{
		treeID = currID;

		url="../e5permission/permissionFlowResource.do?FlowID="+currFlow+"&FlowNodeID="+currNode;
		var cn = getFrame('showItem');
		cn.location = url;
	}
	
	//新：右键菜单的“设置流程权限”，按文件夹和角色进行设置
	function setResourcePermission(flag) {
		treeID = currID;

		url = "../e5permission/ResourceFlowFVUser.jsp?flag=" + flag
			+ "&FlowNodeID=" + currNode
			+ "&DocTypeID=" + currDocType;
		var cn = getFrame('showItem');
		cn.location = url;
	}
	//右键菜单的“增加流程节点”，在流程节点菜单上
	function addNode()
	{
		treeID = currID;

		var url = "FlowNode.do?flowID="+currFlow+"&second=1";
		var cn = getFrame('showItem');
		cn.location = url;
	}
	//右键菜单的“修改流程节点”
	

	function editNode()
	{
		treeID = currID;

		var url = "FlowNode.do?flowID="+currFlow+"&flowNodeID="+currNode+"&second=1";
		var cn = getFrame('showItem');
		cn.location = url;
	}
	//右键菜单的“增加流程”，在流程菜单上
	function addFlowItem()
	{
		treeID = currID;

		var cn = getFrame('showItem');
		var docTypeID = currDocType;
		//cn.location = 'Flow.do?docTypeID=' + docTypeID + "&second=1";
		
		var url = 'FlowVisualDefine.jsp?docTypeID=' + docTypeID + "&second=1";
		cn.location = url;
	}
	//右键菜单的“修改流程”

	function editFlowItem()
	{
		treeID = currID;

		//var url = "Flow.do?flowID="+currFlow+"&docTypeID="+currDocType+"&second=1";
		var url = "FlowVisualDefine.jsp?flowid="+currFlow+"&docTypeID="+currDocType;
		var cn = getFrame('showItem');
		
		
		cn.location = url;
		
		
	}
	
	
	//右键菜单的“删除流程”

	function delFlowItem()
	{
		treeID = currID;

		var ret = window.confirm("<i18n:message key="operation.flownode.confirm"/>");
		if(ret==false)
		{
			return false;
		}
		var url = "FlowSubmit.do?del=1&docTypeID="+currDocType+"&flowID="+currFlow+"&second=1&needParent=1";
		var cn = getFrame('showItem');
		cn.location = url;
	}
	//准备右键菜单
	function prepareMenu()
	{
		if (docTypeMenu == null)
		{
			docTypeMenu = new WebFXMenu;
			docTypeMenu.width = 150;
			if(window.parent.isAdmin) {
				docTypeMenu.add(new WebFXMenuItem("<i18n:message key="flowTree.menuflow.addflow"/>", "addFlow();", "<i18n:message key="flowTree.menuflow.addflow"/>"));
			}
			docTypeMenu.add(new WebFXMenuItem("<i18n:message key="operation.listflow.contentTitle"/>", "listFlow();", "<i18n:message key="operation.listflow.contentTitle"/>"));
			docTypeMenu.add(new WebFXMenuSeparator());

			if(window.parent.isAdmin) {
				docTypeMenu.add(new WebFXMenuItem("<i18n:message key="operation.listunflowop.contentTitle"/>", "listUnflowProc();", "<i18n:message key="operation.listunflowop.contentTitle"/>"));
				docTypeMenu.add(new WebFXMenuItem("<i18n:message key="operation.addunflowop.contentTitle"/>", "addUnflowProc();", "<i18n:message key="operation.addunflowop.contentTitle"/>"));
				docTypeMenu.add(new WebFXMenuSeparator());
			}
			//docTypeMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.unflowpermission"/>", "setDocPermission();", "<i18n:message key="flow.menu.unflowpermission"/>"));
			docTypeMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.unflowpermission"/>", "setResourcePermission(1);", "<i18n:message key="flow.menu.unflowpermission"/>"));
			docTypeMenu.add(new WebFXMenuSeparator());
			docTypeMenu.add(new WebFXMenuItem("<i18n:message key="operation.common.refresh"/>", "refresh(this);", "<i18n:message key="operation.common.refresh"/>"));
			document.write(docTypeMenu);
		}
		if (flowMenu == null)
		{
			flowMenu = new WebFXMenu;
			flowMenu.width = 150;
			if(window.parent.isAdmin) {
				flowMenu.add(new WebFXMenuItem("<i18n:message key="operation.flow.editflow" />","editFlowItem();","<i18n:message key="operation.common.edit" />"));
				flowMenu.add(new WebFXMenuItem("<i18n:message key="operation.flow.deleteflow" />","delFlowItem();","<i18n:message key="operation.common.del" />"));
				flowMenu.add(new WebFXMenuSeparator());
				flowMenu.add(new WebFXMenuItem("<i18n:message key="operation.addflow.contentTitle" />","addFlowItem();","<i18n:message key="operation.common.add" />"));
				flowMenu.add(new WebFXMenuSeparator());
			}
			//flowMenu.add(new WebFXMenuItem("<i18n:message key="flowTree.menuflow.addflownode"/>", "addFlownode();", "<i18n:message key="flowTree.menuflow.addflownode"/>"));
			flowMenu.add(new WebFXMenuItem("<i18n:message key="flowTree.menuflow.listflownode"/>", "listFlowNode();", "<i18n:message key="flowTree.menuflow.listflownode"/>"));
			flowMenu.add(new WebFXMenuItem("<i18n:message key="operation.common.refresh"/>", "refresh(this);", "<i18n:message key="operation.common.refresh"/>"));
			document.write(flowMenu);
		}
		if(flowNodeMenu == null)
		{
			flowNodeMenu = new WebFXMenu;
			flowNodeMenu.width=150;
			flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.listproc.contentTitle" />","listProc();","<i18n:message key="operation.listproc.contentTitle" />"));
			flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.addproc.contentTitle" />","addProc();","<i18n:message key="operation.addproc.contentTitle" />"));
			flowNodeMenu.add(new WebFXMenuSeparator());
			//flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.flownode.editflownode" />","editNode();","<i18n:message key="operation.common.edit" />"));
			//flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.flownode.deleteflownode" />","deleteNode();","<i18n:message key="operation.common.del" />"));
			//flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.addnode.contentTitle" />","addNode();","<i18n:message key="operation.common.add" />"));
			//flowNodeMenu.add(new WebFXMenuSeparator());
			//flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.flowpermission" />","setNodePermission();","<i18n:message key="flow.menu.flowpermission" />"));
			flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.flowpermission" />","setResourcePermission(0);","<i18n:message key="flow.menu.flowpermission" />"));
			flowNodeMenu.add(new WebFXMenuSeparator());
			flowNodeMenu.add(new WebFXMenuItem("<i18n:message key="operation.common.refresh"/>", "refresh(this);", "<i18n:message key="operation.common.refresh"/>"));
			document.write(flowNodeMenu);
		}
		if(unflowMenu == null)
		{
			unflowMenu = new WebFXMenu;
			unflowMenu.width = 150;
			if(window.parent.isAdmin) {
				unflowMenu.add(new WebFXMenuItem("<i18n:message key="operation.listunflowop.contentTitle"/>", "listUnflowProc();", "<i18n:message key="operation.listunflowop.contentTitle"/>"));
				unflowMenu.add(new WebFXMenuItem("<i18n:message key="operation.addunflowop.contentTitle"/>", "addUnflowProc();", "<i18n:message key="operation.addunflowop.contentTitle"/>"));
				unflowMenu.add(new WebFXMenuSeparator());
			}
			//unflowMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.unflowpermission"/>", "setDocPermission();", "<i18n:message key="flow.menu.unflowpermission"/>"));
			unflowMenu.add(new WebFXMenuItem("<i18n:message key="flow.menu.unflowpermission"/>", "setResourcePermission(1);", "<i18n:message key="flow.menu.unflowpermission"/>"));
			unflowMenu.add(new WebFXMenuSeparator());
			unflowMenu.add(new WebFXMenuItem("<i18n:message key="operation.common.refresh"/>", "refresh(this);", "<i18n:message key="operation.common.refresh"/>"));
			document.write(unflowMenu);
		}
	}
	//弹出右键菜单
	function popmenu(flag, src)
	{
		if (flag == 2)//文档类型的右键菜单

		{
			currID = src.id;
			currDocType = src.getAttribute("docTypeID");
			docTypeMenu.treeID = src.getAttribute("id");
			webFXMenuHandler.showMenu(docTypeMenu, src);
		}
		else if (flag == 3)//流程的右键菜单

		{
			currID = src.id;
			currDocType = src.getAttribute("docTypeID");
			currFlow = src.getAttribute("flowID");
			flowMenu.treeID = src.getAttribute("id");
			webFXMenuHandler.showMenu(flowMenu, src);
		}
		else if (flag == 4)//流程节点的

		{
			currID = src.id;
			currDocType = src.getAttribute("docTypeID");
			currFlow = src.getAttribute("flowID");
			currNode = src.getAttribute("flowNodeID");
			flowNodeMenu.treeID = src.getAttribute("id");
			webFXMenuHandler.showMenu(flowNodeMenu, src);
		}
		else if ((flag == 9) || (flag == 11))//非流程操作的
		{
			currID = src.id;
			currDocType = src.getAttribute("docTypeID");
			unflowMenu.treeID = src.getAttribute("id");
			webFXMenuHandler.showMenu(unflowMenu, src);
		}
	}
	//点击“非流程操作”节点

	function listUnflowProcItem(id,src)
	{
		currID =  src.id;
		treeID = currID;

		currDocType = src.getAttribute("docTypeID");
		var cn = getFrame('showItem');
		cn.location = "listProc.do?docTypeID="+id;
	}
	//点击“独立功能条操作”节点

	function noDocClick(src)
	{
		currID =  src.id;
		treeID = currID;

		currDocType = -1;
		var cn = getFrame('showItem');
		cn.location = "listProc.do?docTypeID=-1";
	}
	//点击文档类型，列出所有流程

	function docClick(src)
	{
		var url = "listFlow.do?docTypeID=" + src.getAttribute("docTypeID");
		currDocType = src.getAttribute("docTypeID");
		currID =  src.id;
		treeID = currID;
		getFrame("showItem").location = url;

	}
	//点击流程，列出所有流程节点
	//2006.8.28 改为显示本身信息
	//2012.7.16 改为显示修改流程
	function flowClick(src)
	{
		currFlow = src.getAttribute("flowID");
		currID =  src.id;
		treeID = currID;
//		var url = "listNodes.do?flowID=" + src.getAttribute("flowID");
		currDocType = src.getAttribute("docTypeID");
		//var url = "Flow.do?flowID="+currFlow+"&docTypeID="+currDocType+"&second=1";
		var url = "FlowVisualDefine.jsp?flowid=" + currFlow + "&docTypeID=" + currDocType;

		getFrame("showItem").location = url;
	}
	//点击流程节点，列出所有流程操作
	//2006.8.28 改为显示本身信息
	function nodeClick(src)
	{
		currNode = src.getAttribute("flowNodeID");
		currFlow = src.getAttribute("flowID");
		currID =  src.id;
		treeID = currID;
		currDocType = src.getAttribute("docTypeID");

//		var url = "listProc.do?flowNodeID=" + currNode +"&flowID="+currFlow
//		+"&docTypeID="+src.getAttribute("docTypeID");
		var url = "FlowNode.do?flowID="+currFlow+"&flowNodeID="+currNode+"&second=1";

		getFrame("showItem").location = url;
	}
	//点击流程操作
	function procClick(src)
	{
		currProc = src.getAttribute("procID");
		currID =  src.parentNode.id;
		treeID = currID;
		var procType = src.getAttribute("procType");
		parent.frames["mainBody"].location.href = "Proc.do?procType=" + procType + "&procID=" + currProc;
	}
	showTree();
	prepareMenu();
</script>
