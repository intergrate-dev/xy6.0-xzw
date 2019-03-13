//源稿栏目管理：右键菜单
var ori_menu = {
	rMenu : document.getElementById("rMenu"),
	colLibID : 0, //源稿栏目库ID
	init : function() {
		ori_tree.colMenu0 = ori_menu.showMenu;
		$("body").bind("mousedown", function(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				ori_menu.rMenu.style.visibility = "hidden";
				ori_menu.rMenu.style.visibility = "";
			}
		});
		ori_menu._readData();
		
		$("#menuRoot").click(ori_menu.addRoot);
		//$("#menuUserOp").click(ori_menu.setUserOp);
		//$("#menuUserAdmin").click(ori_menu.setUserAdmin);
		$("#menuAddBat").click(ori_menu.addColumnBat);
		$("#menuAdd").click(ori_menu.addColumn);
		$("#menuDelete").click(ori_menu.delColumn);
		//$("#menuCopy").click(ori_menu.copyColumn);
		$("#menuRefresh").click(ori_menu.refresh);
		$("#menuGarbage").click(ori_menu.garbage);
		//$("#menuOutOfDate").click(ori_menu.outOfDate);
		//$("#menuUseView").click(ori_menu.useView);
		$("#menuLog").click(ori_menu.showLog);
		
		ori_tree._clickFirst();
	},
	//读源稿栏目库ID
	_readData : function() {
		var theURL = "DatasInit.do";
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
			ori_menu.colLibID = data.docLibID;
		}});
	},
	showMenu : function(event, treeId, treeNode) {
		var y = event.clientY;
		var menuHeight = 250;
		if (y + menuHeight > $(window).height())
			y = $(window).height() - menuHeight;

		if (!treeNode && event.target.tagName.toLowerCase() != "button" 
			&& $(event.target).parents("a").length == 0) {
			ori_tree.tree.cancelSelectedNode();
			ori_menu.showRMenu("root", 80, y);
		} else if (treeNode && !treeNode.noR) {
			if (treeNode.nocheck) {
				//无权限，不允许操作
				return false;
			}
			ori_tree.tree.selectNode(treeNode);
			ori_menu.showRMenu("node", 80, y);
		}
	},
	showRMenu : function(type, x, y) {
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
		if (ori_menu.rMenu) ori_menu.rMenu.style.visibility = "hidden";
	},
	//点击栏目时，在右窗口显示栏目设置
	colClick0 : function(event, treeId, treeNode, clickFlag) {
		if (!ori_menu.colLibID) return;
		
		var colID = treeNode.id;
		var url = "OriginalForms.jsp?DocLibID=" + ori_menu.colLibID
				+ "&DocIDs=" + colID
				+ "&siteID=" + ori_tree.siteID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击增加栏目
	addColumn : function() {
		ori_menu.hideRMenu();
		
		var sel = ori_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		var url = "../../e5workspace/manoeuvre/Form.do?code=formOriginalColumn&new=1"
				+ "&DocLibID=" + ori_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + ori_tree.siteID
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击批量增加栏目
	addColumnBat : function() {
		ori_menu.hideRMenu();

		var sel = ori_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;

		var url = "../../e5workspace/manoeuvre/Form.do?code=formOriginalColumn&new=1"
				+ "&DocLibID=" + ori_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + ori_tree.siteID
				+ "&isBat=true"
			;
		window.parent.frames["frmColRight"].location.href = url;
	},

	//点击增加根栏目
	addRoot : function() {
		ori_menu.hideRMenu();
		var url = "../../e5workspace/manoeuvre/Form.do?code=formOriginalColumn&new=1"
				+ "&DocLibID=" + ori_menu.colLibID
				+ "&DocIDs=0"
				+ "&siteID=" + ori_tree.siteID
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	
	//点击删除栏目
	delColumn : function() {
		ori_menu.hideRMenu();
		
		var node = ori_tree.tree.getSelectedNodes();
		if (node.length > 0) 
			node = node[0];
		if (node.nodes && node.nodes.length > 0) {
			var msg = "栏目(" + node.name + ")还有子节点，确定要连同子节点一起删掉吗？";
			if (!confirm(msg))
				return;
		} else {
			var msg = "确定要删除栏目(" + node.name + ")吗？";
			if (!confirm(msg))
				return;
		}
		var theURL = "OrgDelete.do?colID=" + node.id;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				ori_tree.treeDelete(node.id);
				ori_tree._clickFirst();
			} else {
				alert("删除时异常：" + data);
			}
		}});
	},
	//显示日志
	showLog : function() {
		ori_menu.hideRMenu();
		
		var sel = ori_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		sel = sel[0];
		var colID = sel.id;
		
		var url = "../../e5workspace/manoeuvre/FlowRecordList.do?code=formOriginalColumn&new=1"
				+ "&DocLibID=" + ori_menu.colLibID
				+ "&DocIDs=" + colID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//栏目使用情况一览
	useView : function() {
		ori_menu.hideRMenu();
		
		var url = "../MainOrgGarbage.do?type=0&siteID=" + ori_tree.siteID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//回收站
	garbage : function() {
		ori_menu.hideRMenu();
		
		var url = "../MainOrgGarbage.do?type=1&siteID=" + ori_tree.siteID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//刷新
	refresh : function() {
		ori_menu.hideRMenu();
		
		var node = ori_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			ori_tree.tree.reAsyncChildNodes(node, "refresh");
		} else {
			window.location.reload();
		}
	}
	
}

//----栏目管理：拖拽--------
var ori_drag = {
	edit_setting : {
		enable : true,
		showRemoveBtn : false,
		showRenameBtn : false,
		drag : {
			isCopy : false
		}
	},
	colDrop0 : function(event, treeId, treeNodes, targetNode, moveType) {
		if (targetNode && targetNode.nocheck) {
			//无权限，不允许操作
			return false;
		}
		var theURL = "Drag.do?srcID=" + treeNodes[0].id
				+ "&destID=" + targetNode.id
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
	ori_menu.init();
});
