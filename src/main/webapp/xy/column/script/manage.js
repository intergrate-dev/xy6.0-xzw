//----栏目管理：右键菜单----
var col_menu = {
	rMenu : document.getElementById("rMenu"),
	colLibID : 0, //栏目库ID
	permision : {}, //操作权限
	init : function() {
		col_tree.colMenu0 = col_menu.showMenu;
		$("body").bind("mousedown", function(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				col_menu.rMenu.style.visibility = "hidden";
				col_menu.rMenu.style.visibility = "";
			}
		});
		col_menu._readData();
		
		$("#menuRoot").click(col_menu.addRoot);
		$("#menuUserOp").click(col_menu.setUserOp);
		$("#menuUserAdmin").click(col_menu.setUserAdmin);
		$("#menuAddBat").click(col_menu.addColumnBat);
		$("#menuAdd").click(col_menu.addColumn);
		$("#menuDelete").click(col_menu.delColumn);
		$("#menuCopy").click(col_menu.copyColumn);
		$("#menuRefresh").click(col_menu.refresh);
		$("#menuGarbage").click(col_menu.garbage);
		$("#menuOutOfDate").click(col_menu.outOfDate);
		$("#menuUseView").click(col_menu.useView);
		$("#menuLog").click(col_menu.showLog);
		
		col_tree._clickFirst();
	},
	//读操作权限等
	_readData : function() {
		var theURL = "MenuInit.do";
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
			col_menu.permision = data;
			col_menu.colLibID = data.docLibID;
		}});
	},
	showMenu : function(event, treeId, treeNode) {
		var y = event.clientY;
		var menuHeight = 250;
		if (y + menuHeight > $(window).height())
			y = $(window).height() - menuHeight;

		if (!treeNode && event.target.tagName.toLowerCase() != "button" 
			&& $(event.target).parents("a").length == 0) {
			col_tree.tree.cancelSelectedNode();
			col_menu.showRMenu("root", 80, y);
		} else if (treeNode && !treeNode.noR) {
			if (treeNode.nocheck) {
				//无权限，不允许操作
				return false;
			}
			col_tree.tree.selectNode(treeNode);
			col_menu.showRMenu("node", 80, y);
		}
	},
	showRMenu : function(type, x, y) {
		var p = col_menu.permision;
		
		//不选中栏目时的右键菜单
		if (type == "root") {
			$("#rMenu ul").hide();
			
			if (p.p0 == "true") $("#menuRoot").show(); //创建根栏目
			if (p.p2 == "true") {
				$("#menuGarbage").show(); //回收站
				$("#menuOutOfDate").show(); //僵尸栏目
				$("#menuUseView").show(); //栏目使用情况一览
			}
			$("#menuRefresh").show();
		} else {
			//选中栏目时的右键菜单
			$("#rMenu ul").show();
			
			$("#menuRoot").hide();
			$("#menuGarbage").hide();
			$("#menuOutOfDate").hide();
			$("#menuUseView").hide();
			
			if (p.p4 != "true") $("#menuUserOp").hide();	//设置操作权限
			if (p.p5 != "true") $("#menuUserAdmin").hide();	//设置管理权限
			if (p.p6 != "true") $("#menuDelete").hide();	//删除栏目
			if (p.p6 != "true") $("#menuCopy").hide();	//复制栏目，与删除栏目一样的权限
		}
		
		$("#rMenu").css({"top":y+"px", "left":x+"px", "visibility":"visible", "display":"block"});
	},
	hideRMenu : function() {
		if (col_menu.rMenu) col_menu.rMenu.style.visibility = "hidden";
	},
	//点击栏目时，在右窗口显示栏目设置
	colClick0 : function(event, treeId, treeNode, clickFlag) {
		if (!col_menu.colLibID) return;
		
		var colID = treeNode.id;
		var p = col_menu.permision;
		var url = "Forms.jsp?DocLibID=" + col_menu.colLibID
				+ "&DocIDs=" + colID
				+ "&siteID=" + col_tree.siteID
				+ "&ext=" + (p.p1 == "true") //是否有设置扩展属性的权限
				+ "&ch=" + channel_tab.getChannelType()//渠道版本
				
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击增加栏目
	addColumn : function() {
		col_menu.hideRMenu();
		
		var sel = col_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		
		var ch = channel_tab.getChannelType();
		
		var url = "../../e5workspace/manoeuvre/Form.do?code=formColumn&new=1"
				+ "&DocLibID=" + col_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + col_tree.siteID
				+ "&ch=" + ch
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//点击批量增加栏目
	addColumnBat : function() {
		col_menu.hideRMenu();

		var sel = col_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;

		var ch = channel_tab.getChannelType();

		var url = "../../e5workspace/manoeuvre/Form.do?code=formColumn&new=1"
				+ "&DocLibID=" + col_menu.colLibID
				+ "&DocIDs=" + sel[0].id
				+ "&siteID=" + col_tree.siteID
				+ "&isBat=true"
				+ "&ch=" + ch
			;
		window.parent.frames["frmColRight"].location.href = url;
	},

	//点击增加根栏目
	addRoot : function() {
		col_menu.hideRMenu();
		
		var ch = channel_tab.getChannelType();
		
		var url = "../../e5workspace/manoeuvre/Form.do?code=formColumn&new=1"
				+ "&DocLibID=" + col_menu.colLibID
				+ "&DocIDs=0"
				+ "&siteID=" + col_tree.siteID
				+ "&ch=" + ch
				;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//设置栏目的可操作用户
	setUserOp : function() {
		if(channel_tab.getChannelType() == 0){
			initColumnUser(0);
		}else{
			initColumnUser(4);
		}
	},
	//设置栏目的可管理用户
	setUserAdmin : function() {
		if(channel_tab.getChannelType() == 0){
			initColumnUser(1);
		}else{
			initColumnUser(5);
		}
	},
	
	//点击删除栏目
	delColumn : function() {
		col_menu.hideRMenu();
		
		var node = col_tree.tree.getSelectedNodes();
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
		var theURL = "Delete.do?colID=" + node.id;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				col_tree.treeDelete(node.id);
			} else {
				alert("删除时异常：" + data);
			}
		}});
	},
	//复制栏目
	copyColumn : function() {
		col_menu.hideRMenu();
        var node = col_tree.tree.getSelectedNodes();
        if (node.length > 0)
            node = node[0];
        var dataUrl = "../../xy/column/CopyInfo.do?colID=" + node.id;
        var iWidth=400; //弹出窗口的宽度;
        var iHeight=270; //弹出窗口的高度;
        var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
        window.open(dataUrl, "ColumnCopy", "height="+iHeight+", width="+iWidth+",left="+iLeft+", top="+iTop+",toolbar=no,location=no,z-look=yes,alwaysRaised=yes");
	},
    colCopyCallback:function (param) {
        var node = col_tree.tree.getSelectedNodes();
        if (node.length > 0)
            node = node[0];
		 var theURL = "Copy.do?colID=" + node.id+"&colName="+node.name+"&copyTem="+param.copyTem
			 +"&newColName="+param.newColName
			 +"&newWebTemName="+param.newWebTemName
			 +"&newPadTemName="+param.newPadTemName;

		 $.ajax({url:theURL, async:false, success: function(data) {
		 if (data == "ok") {
		 var parent = node.getParentNode();
		 if (parent) {
		 col_tree.tree.reAsyncChildNodes(parent, "refresh");
		 } else {
		 window.location.reload();
		 }
		 } else {
		 alert("复制时异常：" + data);
		 }
		 }});
    },
	//栏目使用情况一览
	useView : function() {
		col_menu.hideRMenu();
		
		var ch = channel_tab.getChannelType();
		var url = "../MainColGarbage.do?type=0&siteID=" + col_tree.siteID + "&ch=" + ch;
		window.parent.frames["frmColRight"].location.href = url;
		//改为open窗口。每次设僵尸栏目后会刷新左右窗口导致一览列表不见了。不方便。
		//window.open(url, "_blank", true);
	},
	//回收站
	garbage : function() {
		col_menu.hideRMenu();
		
		var ch = channel_tab.getChannelType();
		var url = "../MainColGarbage.do?type=1&siteID=" + col_tree.siteID + "&ch=" + ch;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//僵尸栏目一览
	outOfDate : function() {
		col_menu.hideRMenu();
		
		var ch = channel_tab.getChannelType();
		var url = "../MainColGarbage.do?type=2&siteID=" + col_tree.siteID + "&ch=" + ch;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//显示日志
	showLog : function() {
		col_menu.hideRMenu();
		
		var sel = col_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		sel = sel[0];
		var colID = sel.id;
		
		var url = "../../e5workspace/manoeuvre/FlowRecordList.do?code=formColumn&new=1"
				+ "&DocLibID=" + col_menu.colLibID
				+ "&DocIDs=" + colID;
		window.parent.frames["frmColRight"].location.href = url;
	},
	//刷新
	refresh : function() {
		col_menu.hideRMenu();
		
		var node = col_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			col_tree.tree.reAsyncChildNodes(node, "refresh");
		} else {
			window.location.reload();
			//col_tree.tree.reAsyncChildNodes(null, "refresh");不正确
		}
	}
}

//----栏目管理：拖拽--------
var col_drag = {
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
	col_menu.init();
});

function initColumnUser(roleType){
	col_menu.hideRMenu();
	var sel = col_tree.tree.getSelectedNodes();
	if (sel.length == 0) return;
	
	var url = "../../xy/column/InitColumnUser.do?colID=" + sel[0].id
			+ "&siteID=" + col_tree.siteID + "&roleType=" + roleType;
	window.parent.frames["frmColRight"].location.href = url;
}
