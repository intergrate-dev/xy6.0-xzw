//数字报的导航
var col_tree = {
	isArticle : 1,
	tree : null,
	curNode : null,

	check : {
		enable : false,
		chkboxType : {"Y":"s", "N":"s"},
		chkStyle : "checkbox",
		radioType : "all",
		ids : "" //预设的选中id
	}, 
	edit : {
		enable : false
	},
	init : function() {
		if (!$.fn.zTree) return;
		
		var theURL = "Tree.do?siteID=1" ;
		$.ajax({url:theURL, async:false, dataType:"json", success:col_tree._show});		
	},
	_show : function(data) {
		var setting = {
			callback: {
				onClick: col_tree._click,
				onRightClick: col_tree._menu
			},
			async: {
				enable : true,
				url : "Tree.do",
				autoParam: ["paper", "year", "month", "date"],
				otherParam: ["isArticle", col_tree.isArticle],
				dataType : "json"
			},
			check: col_tree.check,
			edit: col_tree.edit,
			view: {
				showLine: false,
				selectedMulti: false,
				fontCss: col_tree._fontCss
			},
			data : {key : {title : "title"}}
		};
		$.fn.zTree.init($("#rs_tree"), setting, data);
		var treeObj = $.fn.zTree.getZTreeObj("tree");
		col_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");
		col_tree._firstShow();
		
	},
	
	_click : function(event, treeId, treeNode, clickFlag) {
		if (treeNode.nocheck) return false;
		
		if (col_tree.check.enable) {
			//点击时也触发checkbox/radio
			col_tree.tree.checkNode(treeNode);
		}
		col_tree.colClick0(event, treeId, treeNode, clickFlag);
	},
	colClick0 : function(event, treeId, treeNode, clickFlag) {
		return false;
	},
	_firstShow : function() {
		var nodes = col_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			
			//展开所有第一次取回的节点
			for ( var i = 0; i < nodes.length; i++) {
				col_tree._firstExpand(nodes[i], true);
				col_tree._clickFirst();
			}
			
			
		
		}
	},
	_firstExpand : function(node, isRoot) {
		var children = node.children;
		if (children && children.length > 0) {
			for (var i = 0; i < children.length; i++) {
				col_tree._firstExpand(children[i], true);
			}
		}
		col_tree.tree.expandNode(node, true);
	},
	_clickFirst : function() {
		if (!col_tree.tree) return;
		
		var nodes = col_tree.tree.getNodes();
		if (!col_tree.check.enable) {
			//默认定位到第一个有权限的节点
			while (nodes && nodes.length > 0 && nodes[0].nocheck) {
				nodes = nodes[0].children;
			}
			if (nodes && nodes.length > 0) {
				col_tree._nodeClick(nodes[0]);
			}
		}
	},
	//使指定的node响应click
	_nodeClick : function(node) {
		var id = node.tId + "_span";
		$("#" + id).click();
	},
	
	
	
	_menu : function(event, treeId, treeNode) {
		col_tree.colMenu0(event, treeId, treeNode);
	},
	//chckbox选中响应函数，供外界修改使用
	_check : function(event, treeId, treeNode){
		return false;
	},
	getChecks : function(){
		var nodes = col_tree.tree.getCheckedNodes();
		return nodes[0].layout;
	}
	
};

