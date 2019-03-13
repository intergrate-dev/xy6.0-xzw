//话题组栏目管理：右键菜单
var topic_menu = {
	rMenu : document.getElementById("rMenu"),
	colLibID : 0, //源稿栏目库ID
	init : function() {
		topic_tree.colMenu0 = topic_menu.showMenu;
		$("body").bind("mousedown", function(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
                topic_menu.rMenu.style.visibility = "hidden";
                topic_menu.rMenu.style.visibility = "";
			}
		});
        topic_menu._readData();
		
		$("#menuRoot").click(topic_menu.addRoot);
		//$("#menuUserOp").click(act_menu.setUserOp);
		//$("#menuUserAdmin").click(act_menu.setUserAdmin);
		$("#menuAddBat").click(topic_menu.addColumnBat);
		$("#menuAdd").click(topic_menu.addColumn);
		$("#menuDelete").click(topic_menu.delColumn);
		//$("#menuCopy").click(act_menu.copyColumn);
		$("#menuRefresh").click(topic_menu.refresh);
		$("#menuGarbage").click(topic_menu.garbage);
		//$("#menuOutOfDate").click(act_menu.outOfDate);
		//$("#menuUseView").click(act_menu.useView);
		$("#menuLog").click(topic_menu.showLog);
		
		topic_tree._clickFirst();
	},
	//读活动栏目库ID
	_readData : function() {
		/*var theURL = "TopicInit.do";
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
            topic_menu.colLibID = data.docLibID;
		}});*/
	},
	showMenu : function(event, treeId, treeNode) {
		var y = event.clientY;
		var x = event.clientX;
		// var menuHeight = 250;
		var menuHeight = 32;
		var menuweight = 82;
		if (y + menuHeight > $(window).height()){
			y = $(window).height() - menuHeight;
		}
		if (x + menuweight > $('#rs_tree').width()){
			x = $('#rs_tree').width() - menuweight;
		}

		if (!treeNode && event.target.tagName.toLowerCase() != "button" 
			&& $(event.target).parents("a").length == 0) {
			// topic_tree.tree.cancelSelectedNode();
            topic_menu.showRMenu("root", treeNode, x, y);
		} else if (treeNode && !treeNode.noR) {
			if (treeNode.nocheck) {
				//无权限，不允许操作
				return false;
			}
			// topic_tree.tree.selectNode(treeNode);
			topic_menu.showRMenu("node", treeNode, x, y);
		}
	},
	showRMenu : function(type, treeNode, x, y) {
		//不选中栏目时的右键菜单
		if (type == "root") {
			$("#rMenu ul").hide();
			
			$("#menuRoot").show(); //创建根栏目
			$("#menuGarbage").show(); //回收站
			//$("#menuUseView").show(); //栏目使用情况一览
			
			$("#menuRefresh").show();
		} else {
			//选中栏目时的右键菜单
			$("#rMenu ul").show();
			
			$("#menuRoot").hide();
			$("#menuGarbage").hide();
			if(treeNode.casID.indexOf("~") != -1 ){//不是根栏目的时候 隐藏新建栏目选项
				$("#menuAdd").hide();
				$("#menuAddBat").hide();
			}
			//$("#menuOutOfDate").hide();
			//$("#menuUseView").hide();
			
			//$("#menuUserOp").hide();	//设置操作权限
			//$("#menuUserAdmin").hide();	//设置管理权限
			//$("#menuDelete").hide();	//删除栏目
			//$("#menuCopy").hide();	//复制栏目，与删除栏目一样的权限
		}
		
		$("#rMenu").css({"top":y+"px", "left":x+"px", "visibility":"visible", "display":"block"});
	},
	hideRMenu : function() {
		if (topic_menu.rMenu) topic_menu.rMenu.style.visibility = "hidden";
	},
	//点击栏目时，在右窗口显示栏目设置
	colClick0 : function(event, treeId, treeNode, clickFlag) {
		if (!topic_menu.colLibID) return;
		
		var colID = treeNode.id;
		var index = treeNode.casID.indexOf("~");
		var parentID = treeNode.casID.substring(0, index);
		var url = "TopicForms.jsp?DocLibID=" + topic_menu.colLibID
				+ "&DocIDs=" + colID
				+ "&siteID=" + topic_tree.siteID
				+ "&parentID=" + parentID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击增加栏目
	addColumn : function() {
        topic_menu.hideRMenu();
		
		var sel = topic_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		var url = "../../e5workspace/manoeuvre/Form.do?code=formTopicColumn&new=1"
				+ "&DocLibID=" + topic_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + topic_tree.siteID
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击批量增加栏目
	addColumnBat : function() {
		topic_menu.hideRMenu();

		var sel = topic_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;

		var url = "../../e5workspace/manoeuvre/Form.do?code=formTopicColumn&new=1"
				+ "&DocLibID=" + topic_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + topic_tree.siteID
				+ "&isBat=true"
			;
		window.parent.frames["frmColRight"].location.href = url;
	},

	//点击增加根栏目
	addRoot : function() {
		topic_menu.hideRMenu();
		var url = "../../e5workspace/manoeuvre/Form.do?code=formTopicColumn&new=1"
				+ "&DocLibID=" + topic_menu.colLibID
				+ "&DocIDs=0"
				+ "&siteID=" + topic_tree.siteID
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	
	//点击删除栏目
	delColumn : function() {
		topic_menu.hideRMenu();
		
		var node = topic_tree.tree.getSelectedNodes();
		if (node.length > 0) 
			node = node[0];
		if (node.nodes && node.nodes.length > 0) {
			var msg = "栏目(" + node.name + ")还有子节点，确定要连同子节点一起删掉吗？";
			if (!confirm(msg))
				return;
		} else {
			var msg = "确定要删除话题组(" + node.name + ")吗？";
			if (!confirm(msg))
				return;
		}
		var theURL = "TopicDelete.do?colID=" + node.id;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				topic_tree.treeDelete(node.id);
			} else {
				alert("删除时异常：" + data);
			}
		}});
	},
	//显示日志
	showLog : function() {
		topic_menu.hideRMenu();
		
		var sel = topic_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		sel = sel[0];
		var colID = sel.id;
		
		var url = "../../e5workspace/manoeuvre/FlowRecordList.do?code=formTopicColumn&new=1"
				+ "&DocLibID=" + topic_menu.colLibID
				+ "&DocIDs=" + colID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//栏目使用情况一览
	useView : function() {
		topic_menu.hideRMenu();
		
		var url = "../MainOrgGarbage.do?type=0&siteID=" + topic_tree.siteID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//回收站
	garbage : function() {
        topic_menu.hideRMenu();
		
		var url = "../MainOrgGarbage.do?type=1&siteID=" + topic_tree.siteID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//刷新
	refresh : function() {
		topic_menu.hideRMenu();
		
		/*var node = topic_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			topic_tree.tree.reAsyncChildNodes(node, "refresh");
		} else {
			window.location.reload();
		}*/
		window.location.reload();
	}
	
}

//----栏目管理：拖拽--------
var topic_drag = {
	edit_setting : {
		enable : true,
		showRemoveBtn : false,
		showRenameBtn : false,
		drag : {
			isCopy : false
		}
	},
	colDrop0 : function(event, treeId, treeNodes, targetNode, moveType) {
		//if (targetNode && targetNode.nocheck) {
		//无权限，不允许操作
		//	return false;
		//}
		var destID = 0;
		if(targetNode!=null){
			destID = targetNode.id;
		}
	
		var theURL = "TopicDrag.do?srcID=" + treeNodes[0].id
				+ "&destID=" + destID
				+ "&moveType=" + moveType;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
			} else {
				alert("异常：" + data);
			}
		}});
	}
}
$(function() {
	topic_menu.init();
});
