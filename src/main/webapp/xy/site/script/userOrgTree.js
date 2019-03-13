var col_tree = {
	//外界修改的参数
	siteID : 0, //当前站点ID
	rootUrl : "../user/OrgTree.do",
	check : {enable : false}, 
	edit : {enable : false},
	
	DocLibID : $(window.parent.document.getElementById("DocLibID")).val(),
	
	tree : null,
	init : function() {
		if ($.fn.zTree) {
			col_tree.showTree();
		}
	},
	showTree : function() {
		var theURL = col_tree.rootUrl + "?parentID=0&siteID=" + col_tree.siteID + "&auth=" + col_tree.auth+"&DocLibID="+col_tree.DocLibID;
		$.ajax({url:theURL, async:false, dataType:"json", success:col_tree._show});
	},
	_show : function(data) {
		var setting = {
			callback: {
				onClick: col_tree._click
			},
			//异步加载
			async: {
				enable : true,
				url : col_tree.rootUrl + "?siteID=" + col_tree.siteID,
				autoParam: ["id=parentID"],
				dataType : "json"
			},
			check: col_tree.check,
			edit: col_tree.edit,
			view: {
				showLine: false,
				selectedMulti: false
			},
			data : {
				key : {
					title : "title"
				}
			}
		};
		$.fn.zTree.init($("#rs_tree"), setting, data);
		
		col_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");//用于获取zTree树对象. 参数DOM 容器的 id
		
		col_tree._firstShow();
	},
	_firstShow : function() {
		var nodes = col_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			/*
			//展开所有第一次取回的节点
			for ( var i = 0; i < nodes.length; i++) {
				col_tree._firstExpand(nodes[i], true);
			}
			*/
			col_tree._firstExpand(nodes[0], true);
			
			col_tree._clickFirst();
		}
	},
	_firstExpand : function(node, isRoot) {
		var children = node.children;
		if (children && children.length > 0) {
			for (var i = 0; i < children.length; i++) {
				col_tree._firstExpand(children[i], false);
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
		var id = node.tId + "_a";
		$("#" + id).click();
	},
	//----节点事件：点击、右键、展开后预选中、拖放
	_click : function(event, treeId, treeNode, clickFlag) {
		//点击部门，则右侧显示该部门下的用户。
		//本功能可能在系统管理中也可能在前端，是前端时还需要加siteID限制。
		//把部门ID用groupID参数传入，则新建用户时可按这个参数设置部门。
		var rule = "u_orgID_EQ_" + treeNode.id + "_AND_u_siteID_EQ_" + col_tree.siteID;
		var url = "../../e5workspace/DataMain.do?type=USEREXT&groupID=" + treeNode.id
				+ "&siteID=" + col_tree.siteID
				+ "&rule=" + rule;
		parent.frmRight.location.href = url;
	}
};
