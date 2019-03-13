//原稿库的导航区
var col_tree = {
	check : {
		enable : false,
		chkboxType : {"Y":"s", "N":"s"},
		chkStyle : "checkbox",
		radioType : "all",
		ids : "" //预设的选中id
	}, 
	
	tree : null,
	init : function() {
		if ($.fn.zTree) {
			col_tree.showTree();
			col_tree.autoCompleter.init();
		}
	},
	//按渠道显示栏目树
	showTree : function() {
		var theURL = "article/CatTree.do?parentID=0&siteID=" + main_param.siteID;
		$.ajax({url:theURL, async:false, dataType:"json", success:col_tree._show});
	},
	_show : function(data) {
		var setting = {
			callback: {
				onClick: col_tree._click,
				onAsyncSuccess: col_tree._afterExpand
			},
			//异步加载子栏目，使用固定Url:"Tree.do?siteID=&parentID=" + treeNode.id;
			async: {
				enable : true,
				url : "article/CatTree.do?siteID=" + main_param.siteID,
				autoParam: ["id=parentID"],
				dataType : "json"
			},
			check: col_tree.check,
			edit: {enable : false},
			view: {
				showLine: false,
				selectedMulti: false,
				fontCss: col_tree._fontCss
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
			col_tree._afterExpand();
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
	//对无权限的父节点设置特殊颜色
	_fontCss : function(treeId, treeNode) {
		return treeNode.nocheck ? {color:"#cccccc"} : {color:"#000000"};
	},
	//----节点事件：点击、右键、展开后预选中、拖放
	_click : function(event, treeId, treeNode, clickFlag) {
		if (treeNode.nocheck) return false;
		
		col_tree.colClick0(event, treeId, treeNode, clickFlag);
	},
	
	//展开后，找到预选节点，设为checked状态
	_afterExpand : function(event, treeId, treeNode) {
		col_tree._finding();
	},
	//---查找定位功能。若找到的是深层的栏目，则按路径层层展开---
	_ids_to_find : null,
	find : function(casID) {
		//casID:要定位的栏目的级联ID
		col_tree._ids_to_find = casID.split("~");
		col_tree._finding();
	},
	_finding : function() {
		var ids = col_tree._ids_to_find;
		if (!ids || ids.length == 0) return;
		
		var id = ids[ids.length - 1];
		var col = col_tree.tree.getNodeByParam("id", id, null);
		if (col) {
			//若当前的树中可以找到了，则终止后续动作，定位，结束。
			col_tree._ids_to_find = null;
			
			col_tree.tree.selectNode(col);
			//col_tree._nodeClick(col);在发布库里会出现选中但不点击的情况，因此改成下面一句
			col_tree._click(null, 'rs_tree', col, 1);

			return;
		}
		
		//否则，按路径层层展开。这种展开是异步的
		var parent = null;
		while (ids.length > 0) {
			var col = col_tree.tree.getNodeByParam("id", ids[0], parent);
			if (!col) break;
			
			parent = col;
			ids.splice(0,1); //去掉一层父路径，[1,2,3]===>[2,3]
		}
		if (parent == null) {
			col_tree._ids_to_find = null;
			alert("找不到栏目，可能是没有权限");
			return;
		}
		col_tree._ids_to_find = ids; //异步展开后，可以按_ids_to_find继续
		col_tree.tree.expandNode(parent, true);
	}
};
//---------查找框auto-complete-------------
col_tree.autoCompleter = {
	url : null,
	init : function() {
		if ($("#colSearch").length == 0)
			return;
		col_tree.autoCompleter.url = "Find.do?siteID=" + main_param.siteID;
		//可能会切换PC版和移动版，因此需要重新绑定url
		col_tree.autoCompleter._newSearch(col_tree.autoCompleter.url);
	},
	_newSearch : function(url) {
		$("#colSearch").remove();
		var s = $("<input type='text' id='colSearch'/>")
			.attr("title", "输入栏目名或者栏目ID进行查找")
			.attr("size", 20)
			.attr("placeholder", "查找")
			.addClass("colSearch")
			;
		s.autocomplete(url, col_tree.autoCompleter.options);
		s.result(col_tree.autoCompleter.search);
		s.appendTo("#divColSearch");
	},
	
	search : function(event, row, formatted) {
		col_tree.find(row.key);
	},
	
	options : {
		minChars : 1,
		delay : 1000,
		autoFill : true,
		selectFirst : true,
		matchContains: false,
		matchSubset: false,
		cacheLength : 1,
		dataType:'json',
		matchType: 'value',
		//把data转换成json数据格式
		parse: function(data) {
			if (!data)
				return [];
			
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.id+"-"+row.value,
					result: row.id+"-"+row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.id+"-"+row.value; },//下拉列表中显示的内容
		formatMatch: function(row, i,max) { return row.id+"-"+row.value; },
		formatResult: function(row, i,max) { return row.id+"-"+row.value; }
	},
	/** 对特殊字符和中文编码 */
	encode : function(param1){
		if (!param1) return "";

		var res = "";
		for(var i = 0;i < param1.length;i ++){
			switch (param1.charCodeAt(i)){
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
			}
		}
		return res;
	}
}
e5.mod("workspace.resourcetree", function() {
	var api;
	var treeClick = function(treeNode) {
		if (treeNode.nocheck)
			return false;

		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		param.groupID = treeNode.id;
		param.ruleFormula = "a_catID_EQ_" + treeNode.id + "_AND_" + main_param.ruleFormula;

		api.broadcast("resourceTopic", param);
	};

	var init = function(sandbox) {
		api = sandbox;
	}
	return {
		init : init,
		treeClick : treeClick
	}
});

//栏目树的点击事件对应到resource的消息发布
var curNode = null;
var colClick0 = function(event, treeId, treeNode, clickFlag) {
	if (treeNode)
		curNode = treeNode;
	
	var statusReady = e5.mods["workspace.doclist"] && e5.mods["workspace.doclist"].self;
	var searchReady = e5.mods["workspace.search"] && e5.mods["workspace.search"].init;
	if (!statusReady || !searchReady) {
		setTimeout(colClick0, 100);
		return;
	}
	
	var treeClick = e5.mods["workspace.resourcetree"].treeClick;
	treeClick(curNode);
}
col_tree.colClick0 = colClick0;
col_tree.init();
