/**
 * 期刊的导航
 */
var col_tree = {
	isArticle : "",
	tree : null,
	curNode : null,
	
	init : function() {
		if (!$.fn.zTree) return;
		
		var theURL = "magazine/Tree.do?siteID=" + main_param.siteID;
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
				url : "magazine/Tree.do",
				autoParam: ["magazine", "year", "month", "date"],
				otherParam: ["isArticle", col_tree.isArticle],
				dataType : "json"
			},
			view: {
				showLine: false,
				selectedMulti: false
			},
			data : {key : {title : "title"}}
		};
		$.fn.zTree.init($("#rs_tree"), setting, data);
		
		col_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");
		col_tree._firstShow();
	},
	_firstShow : function() {
		var nodes = col_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			col_tree._firstExpand(nodes[0]);
			
			setTimeout(col_tree._clickFirst, 1000);
		}
	},
	_firstExpand : function(node) {
		col_tree.tree.expandNode(node, true);
	},
	_clickFirst : function() {
		if (!col_tree.tree) return;
		
		var nodes = col_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			var children = nodes[0].children;
			if (children && children.length > 0) {
				col_tree._nodeClick(children[0]);
			}
		}
	},
	//使指定的node响应click
	_nodeClick : function(node) {
		var id = node.tId + "_a";
		$("#" + id).click();
	},
	//----节点事件：点击、右键
	_click : function(event, treeId, treeNode, clickFlag) {
		if (!treeNode) return;
		if (!col_tree.isArticle && !treeNode.date || col_tree.isArticle && !treeNode.layout) return;
		
		col_tree.curNode = treeNode;
		
		var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
		var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
		if (!statusReady || !searchReady) {
			setTimeout(col_tree.colClick0, 100);
			return;
		}
		
		var treeClick = e5.mods["workspace.resourcetree"].treeClick;
		treeClick(col_tree.curNode);
	},
	_menu : function(event, treeId, treeNode) {
		col_tree.colMenu0(event, treeId, treeNode);
	}
};
e5.mod("workspace.resourcetree",function() {
	var api;
	var treeClick = function(treeNode) {
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		if (col_tree.isArticle) {
			param.ruleFormula = "a_columnID_EQ_" + treeNode.layout;
			param.groupID = treeNode.layout; //按统一的groupID参数名传入参数，用于更新排序操作
		} else {
			param.ruleFormula = "pl_date_EQ_'" + treeNode.date + "'_AND_pl_magID_EQ_" + treeNode.paper;
		}
		
		api.broadcast("resourceTopic", param);
	};
	
	var init = function(sandbox) {
		api = sandbox;
	}
	return {
		init: init,
		treeClick : treeClick
	}
});

//----右键菜单----
var col_menu = {
	rMenu : document.getElementById("rMenu"),
	init : function() {
		col_tree.colMenu0 = col_menu.showMenu;
		$("body").bind("mousedown", function(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				col_menu.rMenu.style.visibility = "hidden";
				col_menu.rMenu.style.visibility = "";
			}
		});
		
		$("#menuDelete").click(col_menu.delete);
		$("#menuPublish").click(col_menu.publish);
		$("#menuView").click(col_menu.view);
	},
	showMenu : function(event, treeId, treeNode) {
		if (!treeNode || !treeNode.date) return;
		
		if (!treeNode.noR) {
			col_tree.tree.selectNode(treeNode);
			col_menu.showRMenu("node", event.clientX, event.clientY);
		}
	},
	showRMenu : function(type, x, y) {
		$("#rMenu").css({"top":y+"px", "left":x+"px", "visibility":"visible"});
	},
	hideRMenu : function() {
		if (col_menu.rMenu) col_menu.rMenu.style.visibility = "hidden";
	},
	//删除某刊期
	delete : function() {
		col_menu.hideRMenu();
		
		var node = col_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			if (!confirm("您确定要删除整个刊期吗？")) {
				return;
			}
			var params = {
				magazineLibID : main_param.docLibID,
				magazine : node.magazine,
				date : node.date
			}
			$.post("magazine/DeleteDate.do", params, function(data) {
				if (!data)
					window.location.reload();
				else
					alert(data);
			});
		}
	},
	//重发某天所有版面
	publish : function() {
		col_menu.hideRMenu();
		
		var node = col_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			if (!confirm("确定要发布吗？")) {
				return;
			}
			var params = {
				magazineLibID : main_param.docLibID,
				magazine : node.magazine,
				date : node.date
			}
			$.post("magazine/PublishDate.do", params, function(data) {
				if (!data) {
					alert("已经提交给发布服务，请稍待");
				} else {
					alert(data);
				}
			});
		}
	},
	view : function() {
		col_menu.hideRMenu();
		
		var node = col_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			
			var url = "magazine/View.do?magazineLibID=" + main_param.docLibID
				+ "&magazine=" + node.magazine
				+ "&date=" + node.date;
			window.open(url);
		}
	}
}

$(function() {
	col_menu.init();
});