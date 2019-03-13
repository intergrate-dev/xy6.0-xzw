var ori_tree = {
	//外界修改的参数
	siteID : 0, //当前站点ID
	rootUrl : "OrgTree.do?parentID=0", //读第一层节点的url
	rootPath : "", //相对路径，用来改变栏目树使用的url路径
	check : {
		enable : false,
		chkboxType : {"Y":"s", "N":"s"},
		chkStyle : "checkbox",
		radioType : "all",
		ids : "", //预设的选中id
        parent : "" //有子节点被选中的父节点
	},
	edit : {
		enable : false
	},

	tree : null,
	init : function() {
		if ($.fn.zTree) {
			ori_tree.showTree();
			ori_tree.autoCompleter.init();
		}
	},
	//显示源稿分类树
	showTree : function() {
		var theURL = ori_tree.rootUrl + "&siteID=" + ori_tree.siteID;
		$.ajax({url:theURL, async:false, dataType:"json", success:ori_tree._show});
	},
	_show : function(data) {
		var setting = {
			callback: {
				onClick: ori_tree._click,
				onRightClick: ori_tree._menu,
				onAsyncSuccess: ori_tree._afterExpand,
				onDrop: ori_tree._drop,
				beforeDrop:ori_tree._beforeDrop,
				beforeDrag:ori_tree._beforeDrag,
				onCheck:ori_tree._check
			},
			async: {
				enable : true,
				url : ori_tree.rootPath + "OrgTree.do?siteID=" + ori_tree.siteID,
				autoParam: ["id=parentID"],
				dataType : "json"
			},
			check: ori_tree.check,
			edit: ori_tree.edit,
			view: {
				showLine: false,
				selectedMulti: false,
				fontCss: ori_tree._fontCss
			},
			data : {
				key : {
					title : "title"
				}
			}
		};
		$.fn.zTree.init($("#rs_tree"), setting, data);

		ori_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");//用于获取zTree树对象. 参数DOM 容器的 id

		ori_tree._firstShow();
	},
	_firstShow : function() {
		var nodes = ori_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			/*
			 //展开所有第一次取回的节点
			 for ( var i = 0; i < nodes.length; i++) {
			 ori_tree._firstExpand(nodes[i], true);
			 }
			 */
			ori_tree._firstExpand(nodes[0], true);
			console.log(nodes[0]);
			ori_tree._clickFirst();
			ori_tree._afterExpand();
		}
	},
	_firstExpand : function(node, isRoot) {
		var children = node.children;
		console.log(children);
		if (children && children.length > 0) {
			for (var i = 0; i < children.length; i++) {
				ori_tree._firstExpand(children[i], false);
			}
		}
			ori_tree.tree.expandNodeOnly(node, true);


	},
	_clickFirst : function() {
		if (!ori_tree.tree) return;

		var nodes = ori_tree.tree.getNodes();
		if (!ori_tree.check.enable) {
			//默认定位到第一个有权限的节点
			while (nodes && nodes.length > 0 && nodes[0].nocheck) {
				nodes = nodes[0].children;
			}
			if (nodes && nodes.length > 0) {
				ori_tree._nodeClick(nodes[0]);
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

		if (ori_tree.check.enable) {
			//点击时也触发checkbox/radio
			ori_tree.tree.checkNode(treeNode);
		}
		ori_tree.colClick0(event, treeId, treeNode, clickFlag);
	},
	_menu : function(event, treeId, treeNode) {
		ori_tree.colMenu0(event, treeId, treeNode);
	},
	_beforeDrag : function(treeId, treeNodes) {
		if (treeNodes[0].nocheck) {
			//无权限，不允许操作
			return false;
		}
	},
	_beforeDrop : function(treeId, treeNodes, targetNode, moveType) {

		if (targetNode && targetNode.nocheck || !confirm("确定要移动栏目 '"+treeNodes[0].name+"' ?")) {
			//无权限，不允许操作
			return false;
		}
	},
	_drop : function(event, treeId, treeNodes, targetNode, moveType) {
		ori_tree.colDrop0(event, treeId, treeNodes, targetNode, moveType);
	},

	//展开后，找到预选节点，设为checked状态
	_afterExpand : function(event, treeId, treeNode) {
		ori_tree._finding();

        var _node = ori_tree.tree.getNodeByParam("id", ori_tree._ids_to_find, treeNode);
        // 滚动ztree的滚动条
        if(treeNode && _node){
            var $treeContainer = $("#rs_tree");
            var _iframe = $($(window.parent.document).find("iframe")[0]).attr("name");
            var tid = _node.tId;
            if(_iframe === "frmColumn"){
                $treeContainer.closest("body").animate({scrollTop:$("#"+tid).offset().top}, 500);
                $treeContainer.closest("html").animate({scrollTop:$("#"+tid).offset().top}, 500);
            } else {
                $treeContainer.animate({scrollTop:$("#"+tid).offset().top}, 500);
			}
		}

        if (!ori_tree.check.enable) return;
		if (!ori_tree.check.ids) return;

		//不能只读当前子节点，对于无权限的节点，一次性会显示多层树
		var ids = ori_tree.check.ids.split(",");
		for (var i = 0; i < ids.length; i++) {
			var node = ori_tree.tree.getNodeByParam("id", ids[i], false);
			if (node && !node.checked) {
				node.checked = true;
				ori_tree.tree.updateNode(node);
			}
		}
        /*var parents = ori_tree.check.parent.split(",");
        for (var i = 0; i < parents.length; i++) {
            var node = ori_tree.tree.getNodeByParam("id", parents[i], false);
            if (node && !node.halfCheck) {
                node.halfCheck = true;
                ori_tree.tree.updateNode(node);
            }
        }*/
		if(ori_tree.check.chkboxType.N =="s" &&ori_tree.check.chkboxType.Y =="s" ){
			if(!!treeNode && treeNode.checked){
			    for(var i = 0;i<treeNode.children.length;i++)
                {
                     node = treeNode.children[i];
                    if (node && !node.checked) {
                        node.checked = true;
                        ori_tree.tree.updateNode(node);
                    }
                }
			}
		}
	},
	//点击栏目的实际响应函数，外界可以通过修改这个函数而增加点击栏目的响应
	colClick0 : function(event, treeId, treeNode, clickFlag) {
		return false;
	},


	//右键菜单的实际响应函数，外界可以通过修改这个函数而增加右键菜单
	colMenu0 : function(event, treeId, treeNode) {
		return false;
	},
	//拖放的实际响应函数，外界可以通过修改这个函数而增加拖放功能
	colDrop0 : function(event, treeId, treeNodes, targetNode, moveType) {
		return false;
	},
	//chckbox选中响应函数，供外界修改使用
	_check : function(event, treeId, treeNode){
		var nodeID = treeNode.id.toString();
		var ids = ori_tree.check.ids.split(",");
		var nodePos=ids.indexOf(nodeID);
		if (nodePos==-1){
			ids.push(nodeID);
		}
		else{
			ids.splice(nodePos,1);
		}

		ori_tree.check.ids=ids.toString();

		return false;
	},
	//----栏目管理时，给外界调用的树方法：增/删/改节点
	treeAdd : function(colID, colName, parentID) {
		var parent = ori_tree.tree.getNodeByParam("id", parentID, null);
		ori_tree.tree.addNodes(parent, [{ name:colName, id:colID}]);
	},

	treeUpdate : function(colID, colName) {
		var col = ori_tree.tree.getNodeByParam("id", colID, null);
		col.name = colName;
		ori_tree.tree.updateNode(col);
	},
	treeDelete : function(colID) {
		var col = ori_tree.tree.getNodeByParam("id", colID, null);
		ori_tree.tree.removeNode(col);
	},

	//----获取复选框选中的节点
	//判断父辈节点是否选中
	_parentChecked : function(node) {
		while (node.getParentNode() != null) {
			var parent = node.getParentNode();
			if (parent.checked && !$('#'+parent.tId+'_check').hasClass('checkbox_true_part'))
				return true;
			node = parent;
		}
		return false;
	},
	//获取复选框选中的节点
	getChecks : function() {
		var nodes = ori_tree.tree.getCheckedNodes();
		var ids = "", names = "", casIDs = "";
		for (var i = 0; i < nodes.length; i++) {
			if (ids) {
				ids += ",";
				names += ",";
				casIDs += ",";
			}
			ids += nodes[i].id;
			names += nodes[i].casName;
			casIDs += nodes[i].casID;
		}
		//取出所有未展开的节点
		var notExpandedIDs = ori_tree._getNotExpanded(true);

		return [ids, names, casIDs, notExpandedIDs];
	},
	//获取复选框选中的节点，父节点选中的话，则忽略子节点
	getFilterChecks : function() {
		var notExpanded = ori_tree._getNotExpanded();

		var nodes = ori_tree.tree.getCheckedNodes();
		var ids = "", names = "", casIDs = "";
		for (var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			//不是半选 父节点没有全选
            var halfSelected=$('#'+node.tId+'_check').hasClass('checkbox_true_part');
            var parentFullSelected=ori_tree._parentChecked(node) && node.getParentNode() && !$('#'+node.getParentNode().tId+'_check').hasClass('checkbox_true_part');
			if (!halfSelected && !parentFullSelected){
            //if (!ori_tree._parentChecked(node)){
				if (ids) {
					ids += ",";
					names += ",";
					casIDs += ",";
				}
				ids += nodes[i].id;
				names += nodes[i].casName;
				casIDs += nodes[i].casID;
			}
		}
		//取出所有未展开的节点，过滤掉已选中了父节点的
		var notExpandedIDs = ori_tree._getNotExpanded(true);

		return [ids, names, casIDs, notExpandedIDs];
	},
	_getNotExpanded : function(needFilter) {
		//取出所有未展开的节点
		var notExpanded = [];
		var all = ori_tree.tree.getNodes();
		for (var i = 0; i < all.length; i++) {
			var node = all[i];
			if (node.isParent) {
				ori_tree._getNotExpandedByParent(node, notExpanded);
			}
		}

		var notExpandedIDs = "";
		if (!needFilter) {
			for (var i = 0; i < notExpanded.length; i++) {
				if (notExpandedIDs)
					notExpandedIDs += ",";
				notExpandedIDs += notExpanded[i].id;
			}
		} else {
			//过滤掉已选中了父节点的
			for (var i = 0; i < notExpanded.length; i++) {
				var node = notExpanded[i];
                var halfSelected=$('#'+node.tId+'_check').hasClass('checkbox_true_part');
                var parentFullSelected=ori_tree._parentChecked(node) && node.getParentNode() && !$('#'+node.getParentNode().tId+'_check').hasClass('checkbox_true_part');
                //if (!halfSelected && !parentFullSelected) {
				if(!ori_tree._parentChecked(node)){
					if (notExpandedIDs) {
						notExpandedIDs += ",";
					}
					notExpandedIDs += node.id;
				}
			}
		}
		return notExpandedIDs;
	},
	_getNotExpandedByParent : function(node, result) {
		if (node.children == null || node.children.length == 0) {
			result.push(node);
		} else {
			for (var i = 0; i < node.children.length; i++) {
				var son = node.children[i];
				if (son.isParent)
					ori_tree._getNotExpandedByParent(son, result);
			}
		}
	},

	//---查找定位功能。若找到的是深层的栏目，则按路径层层展开---
	_ids_to_find : null,
	find : function(casID) {
		//casID:要定位的栏目的级联ID
		ori_tree._ids_to_find = casID.split("~");
		ori_tree._finding();
	},
	_finding : function() {
		var ids = ori_tree._ids_to_find;
		if (!ids || ids.length == 0) return;

		var id = ids[ids.length - 1];
		var col = ori_tree.tree.getNodeByParam("id", id, null);
		if (col) {
            // 节点展开后但是之后被合上 再次搜索该节点时需要将节点的父节点依次展开
            if(ids.length > 1) {
                var parent = null;
                for (var i = 0; i < ids.length; i++) {
                    var c = ori_tree.tree.getNodeByParam("id", ids[0], parent);
                    ori_tree.tree.expandNode(c, true, false, false, true);
                    parent = c;
                }
                // 动画定位: 滚动滚动条
                var $treeContainer = $("#rs_tree");
                var _iframe = $($(window.parent.document).find("iframe")[0]).attr("name");
                var tid = col.tId;
                // 有两种情况 : ztree在iframe中,滚动条是iframe的body或者html的,视浏览器而定; ztree在div中, 滚动条是该div的.
                if (_iframe === "frmColumn") {
                    $treeContainer.closest("body").animate({scrollTop: $("#" + tid).offset().top-110}, 500);
                    $treeContainer.closest("html").animate({scrollTop: $("#" + tid).offset().top-110}, 500);
                } else {
                    $treeContainer.animate({scrollTop: $("#" + tid).offset().top-110}, 500);
                }
            }

			//若当前的树中可以找到了，则终止后续动作，定位，结束。
			//ori_tree._ids_to_find = null;
			ori_tree.tree.selectNode(col,false,false);
			//ori_tree._nodeClick(col);在发布库里会出现选中但不点击的情况，因此改成下面一句
			ori_tree._click(null, 'rs_tree', col, 1);

			return;
		}

		//否则，按路径层层展开。这种展开是异步的
		var parent = null;
		while (ids.length > 0) {
			var col = ori_tree.tree.getNodeByParam("id", ids[0], parent);
			if (!col) break;

			parent = col;
			ids.splice(0,1); //去掉一层父路径，[1,2,3]===>[2,3]
		}
		if (parent == null) {
			ori_tree._ids_to_find = null;
			alert("找不到栏目，可能是没有权限");
			return;
		}
		ori_tree._ids_to_find = ids; //异步展开后，可以按_ids_to_find继续
		ori_tree.tree.expandNodeOnly(parent, true);



	},

	//----------------
	remove : function(id) {
		var col = ori_tree.tree.getNodeByParam("id", id, null);
		if (col) {
			ori_tree.tree.removeNode(col);
		}
	}
};
//---------查找框auto-complete-------------
ori_tree.autoCompleter = {
	url : null,
	init : function() {
		if ($("#colSearch").length == 0)
			return;
		ori_tree.autoCompleter.url = ori_tree.rootPath
			+ "OrgFind.do?siteID=" + ori_tree.siteID;
		//可能会切换PC版和移动版，因此需要重新绑定url
		ori_tree.autoCompleter._newSearch(ori_tree.autoCompleter.url);
	},
	_newSearch : function(url) {
		$("#colSearch").remove();
		var s = $("<input type='text' id='colSearch'/>")
			.attr("title", "输入栏目名或者栏目ID进行查找")
			.attr("size", 20)
			.attr("placeholder", "查找")
			.addClass("colSearch")
			;
		s.autocomplete(url, ori_tree.autoCompleter.options);
		s.result(ori_tree.autoCompleter.search);
		s.appendTo("#divColSearch");
	},

	search : function(event, row, formatted) {
		ori_tree.find(row.key);
	},

	options : {
		max : 20,
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