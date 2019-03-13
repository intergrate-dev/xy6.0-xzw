var topic_tree = {
	//外界修改的参数
	siteID : 0, //当前站点ID
	rootUrl : "TopicTree.do?parentID=0", //读第一层节点的url
	rootPath : "", //相对路径，用来改变栏目树使用的url路径
	docLibID: '',
	check : {
		enable : false,
		chkboxType : {"Y":"s", "N":"s"},
		chkStyle : "checkbox",
		radioType : "all",
		ids : "", //预设的选中id
        parent : "" //有子节点被选中的父节点
	},
	edit : {
		enable : true,
		removeTitle: "删除",
		// showRemoveBtn: topic_tree._setRemoveBtn,
		renameTitle: "编辑",
		// showRenameBtn: topic_tree._setRenameBtn
	},
	timer: null,
	tree : null,
	init : function() {
		if ($.fn.zTree) {
			topic_tree.showTree();
			topic_tree.autoCompleter.init();
		}
	},
	//显示源稿分类树
	showTree : function() {
		var theURL = topic_tree.rootUrl + "&siteID=" + topic_tree.siteID;
		$.ajax({url:theURL, async:false, dataType:"json", success:topic_tree._show});
	},
	_show : function(data) {
		var setting = {
			callback: {
				onClick: topic_tree._click,
				onRightClick: topic_tree._menu,
				onAsyncSuccess: topic_tree._afterExpand,
				onDrop: topic_tree._drop,
				beforeDrop:topic_tree._beforeDrop,
				beforeDrag:topic_tree._beforeDrag,
				onCheck:topic_tree._check,
				beforeRemove: topic_tree._beforeRemove,
				onRemove: topic_tree._remove,
				beforeRename: topic_tree._beforeRename,
				onRename: topic_tree._rename
			},
			async: {
				enable : true,
				url : topic_tree.rootPath + "TopicTree.do?siteID=" + topic_tree.siteID,
				autoParam: ["id=parentID"],
				dataType : "json"
			},
			// check: topic_tree.check,
			/*check: {
				enable: true
			},*/
			edit: {
				enable : true,
				removeTitle: "删除",
				showRemoveBtn: topic_tree._setRemoveBtn,
				renameTitle: "修改",
				showRenameBtn: topic_tree._setRenameBtn,
				editNameSelectAll: true,
				drag: {
					inner: false,
					minMoveSize: 25
				}
			},
			view: {
				showLine: false,
				selectedMulti: false,
				fontCss: topic_tree._fontCss,
				addHoverDom: topic_tree.addHoverDom,
				removeHoverDom: topic_tree.removeHoverDom
			},
			data : {
				key : {
					title : "title"
				}
			}
		};
		if ($('.aui_title', parent.parent.document).html() == '复制') {
			setting.check = {
				enable: true
			}
		}
		topic_tree.docLibID = data.docLibID;
		//移动和复制话题时不显示当前话题所在的话题分组
		var _title = $("#rs_tree .curSelectedNode", parent.parent.document).prop('title');
		if (_title) {
			$.each(data.list, function(index, val) {
				if (val.title == _title) {
					data.list.splice(index, 1);
					return false;
				}
			});
		}
		$.fn.zTree.init($("#rs_tree"), setting, data.list);
        topic_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");//用于获取zTree树对象. 参数DOM 容器的 id

        topic_tree._firstShow();
	},
	addHoverDom: function(treeId, treeNode) {
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
		var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
			+ "' title='增加' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#addBtn_"+treeNode.tId);
		if (btn) btn.bind("click", function(){
			var zTree = topic_tree.tree;
			zTree.addNodes(null, -1, {name:"", id: ''});
			var nodes = zTree.getNodes();
			zTree.editName(nodes[nodes.length -1]);
			return false;
		});
	},
	removeHoverDom: function(treeId, treeNode) {
		$("#addBtn_"+treeNode.tId).unbind().remove();
	},
	_setRemoveBtn: function(treeId, treeNode) {
		return true;
	},
	_setRenameBtn: function(treeId, treeNode) {
		return true;
	},
	_beforeRemove: function(treeId, treeNode) {
		var stateMachine = false;
		var node = topic_tree.tree.getSelectedNodes();
		/*if ($('#doclistframe #listing table tr').length > 0) {
			alert('当前话题组下存在话题，不能删除');
			return stateMachine;
		}*/
		if (node.length > 0) 
			node = node[0];
		if (node.nodes && node.nodes.length > 0) {
			var msg = "栏目(" + node.name + ")还有子节点，确定要连同子节点一起删掉吗？";
			if (!confirm(msg))
				return stateMachine;
		} else {
			var msg = "确定要删除话题组(" + node.name + ")吗？";
			if (!confirm(msg))
				return stateMachine;
		}
		var theURL = "./column/TopicDelete.do?colID=" + node.id;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				topic_tree.treeDelete(node.id);
				stateMachine = true;
			} else {
				alert("删除时异常：" + data);
			}
		}});
		return stateMachine;
	},
	_remove: function(event, treeId, treeNode) {
		return true;
	},
	_beforeRename: function(treeId, treeNode, newName, isCancel) {
		var stateMachine = topic_tree.stateMachine(treeNode, newName);
		return stateMachine;
	},
	_rename: function(event, treeId, treeNode, isCancel) {
		return true;
	},
	stateMachine: function(treeNode, newName) {
		var stateMachine = false;
		var zTree = topic_tree.tree;
		var nodes = zTree.getNodes();
		$.ajaxSettings.async = false;
		if ($.trim(newName) == '') {
			if (treeNode.id == '') {
				zTree.removeNode(nodes[nodes.length - 1]);
			} else {
				alert('名字不能为空');
				zTree.cancelEditName();
			}
			return stateMachine
		}
		if ($.trim(newName).length > 50) {
			alert('话题组名字最大长度不能超过50');
			zTree.cancelEditName();
			return stateMachine; 
		}
		var params1 = {
			DocLibID: topic_tree.docLibID,
			DocIDs: treeNode.id,
			parentID: 0,
			siteID: main_param.siteID,
			value: $.trim(newName)
		};
		$.get('./column/TopicDuplicate.do', params1, function(data) {
			if (parseInt(data) == 0) {
				var params2 = {
					DocLibID: topic_tree.docLibID,
					DocID: treeNode.id,
					col_parentID: 0,
					col_siteID: main_param.siteID,
					col_name: $.trim(newName),
					col_description: ''
				};
				$.post('./column/TopicFormSubmit.do', params2, function(data, textStatus, xhr) {
					if (data) {
						$.each(nodes, function(index, val) {
							if (nodes[index].id == treeNode.id) {
								nodes[index].title = data.colName + ' [' + data.colID + ']';
								nodes[index].id = data.colID;
								nodes[index].name = data.colName
								zTree.updateNode(nodes[index]);
							}
						});
						stateMachine = true;
					} else {
						alert('修改失败');
					}
				}, 'json');
			} else if (parseInt(data) == 1) {
				// topic_tree.myalert('当前话题组已存在，请不要重复创建。', zTree, treeNode, newName);
				alert('当前话题组已存在，请不要重复创建。');
				zTree.cancelEditName();
				return false;
			}
		});
		return stateMachine;
	},
	myalert: function (str, zTree, treeNode, newName) {
		var div = '<div class="mark"><div class="mark1"><p>' + str + '</p><button>确认</button></div><div class="mark2"></div></div>';
		$('body').append(div);
		$('.mark').on('click', 'button', function() {
			$('.mark').remove();
			$('#rs_tree .curSelectedNode .node_name input.rename').prop('readonly', false);
			zTree.editName(treeNode);
		});
		$('.mark').show();
		$('#rs_tree .curSelectedNode .node_name input.rename').prop('readonly', true);
		/*clearTimeout(topic_tree.timer);
		topic_tree.timer = setTimeout(function() {
			$('.mark').hide();
			$('.mark').remove();
		}, 2000)*/
	},
	_firstShow : function() {
		var nodes = topic_tree.tree.getNodes();
		if (nodes && nodes.length > 0) {
			/*
			 //展开所有第一次取回的节点
			 for ( var i = 0; i < nodes.length; i++) {
			 act_tree._firstExpand(nodes[i], true);
			 }
			 */
            topic_tree._firstExpand(nodes[0], true);
			// console.log(nodes[0]);
            topic_tree._clickFirst();
            topic_tree._afterExpand();
		}
	},
	_firstExpand : function(node, isRoot) {
		var children = node.children;
		// console.log(children);
		if (children && children.length > 0) {
			for (var i = 0; i < children.length; i++) {
                topic_tree._firstExpand(children[i], false);
			}
		}
        topic_tree.tree.expandNodeOnly(node, true);


	},
	_clickFirst : function() {
		if (!topic_tree.tree) return;

		var nodes = topic_tree.tree.getNodes();
		if (!topic_tree.check.enable) {
			//默认定位到第一个有权限的节点
			while (nodes && nodes.length > 0 && nodes[0].nocheck) {
				nodes = nodes[0].children;
			}
			if (nodes && nodes.length > 0) {
                topic_tree._nodeClick(nodes[0]);
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

		if (topic_tree.check.enable) {
			//点击时也触发checkbox/radio
            topic_tree.tree.checkNode(treeNode);
		}
        topic_tree.colClick0(event, treeId, treeNode, clickFlag);
	},
	_menu : function(event, treeId, treeNode) {
        topic_tree.colMenu0(event, treeId, treeNode);
	},
	_beforeDrag : function(treeId, treeNodes) {
		if (treeNodes[0].nocheck || !treeNodes[0].id) {
			//无权限，不允许操作
			return false;
		}
	},
	_beforeDrop : function(treeId, treeNodes, targetNode, moveType) {
		//拖拽到目标节点时，设置是不允许成为目标节点的子节点
		// if (moveType == 'inner') return false;
		if (targetNode && targetNode.nocheck || !confirm("确定要移动话题组 '"+treeNodes[0].name+"' ?")) {
			//无权限，不允许操作
			return false;
		}
	},
	_drop : function(event, treeId, treeNodes, targetNode, moveType) {
        topic_tree.colDrop0(event, treeId, treeNodes, targetNode, moveType);
	},

	//展开后，找到预选节点，设为checked状态
	_afterExpand : function(event, treeId, treeNode) {
        topic_tree._finding();

        var _node = topic_tree.tree.getNodeByParam("id", topic_tree._ids_to_find, treeNode);
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

        if (!topic_tree.check.enable) return;
		if (!topic_tree.check.ids) return;

		//不能只读当前子节点，对于无权限的节点，一次性会显示多层树
		var ids = topic_tree.check.ids.split(",");
		for (var i = 0; i < ids.length; i++) {
			var node = topic_tree.tree.getNodeByParam("id", ids[i], false);
			if (node && !node.checked) {
				node.checked = true;
                topic_tree.tree.updateNode(node);
			}
		}
        /*var parents = act_tree.check.parent.split(",");
        for (var i = 0; i < parents.length; i++) {
            var node = act_tree.tree.getNodeByParam("id", parents[i], false);
            if (node && !node.halfCheck) {
                node.halfCheck = true;
                act_tree.tree.updateNode(node);
            }
        }*/
		if(topic_tree.check.chkboxType.N =="s" &&topic_tree.check.chkboxType.Y =="s" ){
			if(!!treeNode && treeNode.checked){
			    for(var i = 0;i<treeNode.children.length;i++)
                {
                     node = treeNode.children[i];
                    if (node && !node.checked) {
                        node.checked = true;
                        topic_tree.tree.updateNode(node);
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
		var ids = topic_tree.check.ids.split(",");
		var nodePos=ids.indexOf(nodeID);
		if (nodePos==-1){
			ids.push(nodeID);
		}
		else{
			ids.splice(nodePos,1);
		}

        topic_tree.check.ids=ids.toString();

		return false;
	},
	//----栏目管理时，给外界调用的树方法：增/删/改节点
	treeAdd : function(colID, colName, parentID) {
		var parent = topic_tree.tree.getNodeByParam("id", parentID, null);
        topic_tree.tree.addNodes(parent, [{ name:colName, id:colID}]);
	},

	treeUpdate : function(colID, colName) {
		var col = topic_tree.tree.getNodeByParam("id", colID, null);
		col.name = colName;
        topic_tree.tree.updateNode(col);
	},
	treeDelete : function(colID) {
		var col = topic_tree.tree.getNodeByParam("id", colID, null);
        topic_tree.tree.removeNode(col);
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
		var nodes = topic_tree.tree.getCheckedNodes();
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
		var notExpandedIDs = topic_tree._getNotExpanded(true);

		return [ids, names, casIDs, notExpandedIDs];
	},
	//获取复选框选中的节点，父节点选中的话，则忽略子节点
	getFilterChecks : function() {
		var notExpanded = topic_tree._getNotExpanded();

		var nodes = topic_tree.tree.getCheckedNodes();
		var ids = "", names = "", casIDs = "";
		for (var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			//不是半选 父节点没有全选
            var halfSelected=$('#'+node.tId+'_check').hasClass('checkbox_true_part');
            var parentFullSelected=topic_tree._parentChecked(node) && node.getParentNode() && !$('#'+node.getParentNode().tId+'_check').hasClass('checkbox_true_part');
			if (!halfSelected && !parentFullSelected){
            //if (!act_tree._parentChecked(node)){
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
		var notExpandedIDs = topic_tree._getNotExpanded(true);

		return [ids, names, casIDs, notExpandedIDs];
	},
	_getNotExpanded : function(needFilter) {
		//取出所有未展开的节点
		var notExpanded = [];
		var all = topic_tree.tree.getNodes();
		for (var i = 0; i < all.length; i++) {
			var node = all[i];
			if (node.isParent) {
                topic_tree._getNotExpandedByParent(node, notExpanded);
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
                var parentFullSelected=topic_tree._parentChecked(node) && node.getParentNode() && !$('#'+node.getParentNode().tId+'_check').hasClass('checkbox_true_part');
                //if (!halfSelected && !parentFullSelected) {
				if(!topic_tree._parentChecked(node)){
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
                    topic_tree._getNotExpandedByParent(son, result);
			}
		}
	},

	//---查找定位功能。若找到的是深层的栏目，则按路径层层展开---
	_ids_to_find : null,
	find : function(casID) {
		//casID:要定位的栏目的级联ID
        topic_tree._ids_to_find = casID.split("~");
        topic_tree._finding();
	},
	_finding : function() {
		var ids = topic_tree._ids_to_find;
		if (!ids || ids.length == 0) return;

		var id = ids[ids.length - 1];
		var col = topic_tree.tree.getNodeByParam("id", id, null);
		if (col) {
            // 节点展开后但是之后被合上 再次搜索该节点时需要将节点的父节点依次展开
            if(ids.length > 1) {
                var parent = null;
                for (var i = 0; i < ids.length; i++) {
                    var c = topic_tree.tree.getNodeByParam("id", ids[0], parent);
                    topic_tree.tree.expandNode(c, true, false, false, true);
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
			//act_tree._ids_to_find = null;
            topic_tree.tree.selectNode(col,false,false);
			//act_tree._nodeClick(col);在发布库里会出现选中但不点击的情况，因此改成下面一句
            topic_tree._click(null, 'rs_tree', col, 1);

			return;
		}

		//否则，按路径层层展开。这种展开是异步的
		var parent = null;
		while (ids.length > 0) {
			var col = topic_tree.tree.getNodeByParam("id", ids[0], parent);
			if (!col) break;

			parent = col;
			ids.splice(0,1); //去掉一层父路径，[1,2,3]===>[2,3]
		}
		if (parent == null) {
            topic_tree._ids_to_find = null;
			alert("找不到栏目，可能是没有权限");
			return;
		}
        topic_tree._ids_to_find = ids; //异步展开后，可以按_ids_to_find继续
        topic_tree.tree.expandNodeOnly(parent, true);



	},

	//----------------
	remove : function(id) {
		var col = topic_tree.tree.getNodeByParam("id", id, null);
		if (col) {
            topic_tree.tree.removeNode(col);
		}
	}
};
//---------查找框auto-complete-------------
topic_tree.autoCompleter = {
	url : null,
	init : function() {
		if ($("#colSearch").length == 0)
			return;
        topic_tree.autoCompleter.url = topic_tree.rootPath
			+ "TopicFind.do?siteID=" + topic_tree.siteID;
		//可能会切换PC版和移动版，因此需要重新绑定url
        topic_tree.autoCompleter._newSearch(topic_tree.autoCompleter.url);
	},
	_newSearch : function(url) {
		$("#colSearch").remove();
		var s = $("<input type='text' id='colSearch'/>")
			.attr("title", "输入栏目名或者栏目ID进行查找")
			.attr("size", 20)
			.attr("placeholder", "查找")
			.addClass("colSearch")
			;
		s.autocomplete(url, topic_tree.autoCompleter.options);
		s.result(topic_tree.autoCompleter.search);
		s.appendTo("#divColSearch");
	},

	search : function(event, row, formatted) {
        topic_tree.find(row.key);
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