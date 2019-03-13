//发布库的导航区
e5.mod("workspace.resourcetree", function() {
	var api;
	var treeClick = function(treeNode, isFirstClick) {
		if (treeNode.nocheck)
			return false;

		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		param.colID = treeNode.id; // 栏目ID
		param.ch = col_tree.ch; // 渠道
		param.ruleFormula = "CLASS_1_EQ_" + param.colID;

        //发布库显示当日所有栏目的所有稿件 并且隐藏部分相关操作的按钮 该处隐藏稿件列表的部分操作（除查看和预览）
        var tabName = $(window.parent.document).find("#tabContentDiv .tab-pane.active iframe").attr("showname");
        if($("#rs_tree").is(":visible") && tabName=="Web发布库" && isFirstClick && col_tree && col_tree.tree && col_tree.tree.getSelectedNodes().length==0){
            //发布库打开以后默认显示当日的所有栏目所有稿件
            param.ruleFormula = "a_siteID_EQ_"+ main_param["siteID"];
            param.catTypeID = 0;
            var date = new Date();
            var day = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+(date.getDate()<10?'0'+date.getDate():date.getDate());
            var d1 = "&a_pubTime_0=" + day + " 00:00:00";
            var d2 = "&a_pubTime_1=" + day + " 23:59:59";
            // 添加了标记isFirstClick
            param.isFirstClickQuery = "isFirstClick@QUERYCODE@=qArticle"+d1+d2;
        } else {
            param.isFirstClickQuery = "";
		}
		main_param.colID = param.colID; // 给columnColor.js使用
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

// ----右键菜单----
var col_menu = {
	rMenu : document.getElementById("rMenu"),
	permision : {},

    // 操作权限
	init : function() {
		col_tree.colMenu0 = col_menu.showMenu;
		$("body").bind(
				"mousedown",
				function(event) {
					if (!(event.target.id == "rMenu" || $(event.target)
							.parents("#rMenu").length > 0)) {
						col_menu.rMenu.style.visibility = "hidden";
						col_menu.rMenu.style.visibility = "";
					}
				});
		col_menu._readData();

		$("#menuPub").click(col_menu.publish);
		$("#menuPubCol").click(col_menu.publishCol);
		$("#menuRefresh").click(col_menu.refresh);
		$("#menuView").click(col_menu.view);
		$("#menuDetails").click(col_menu.details);
		$("#menuAddToColl").click(col_menu.addToColl);
		col_tree._clickFirst();
	},
	// 读操作权限等
	_readData : function() {
		var theURL = "column/MenuInit.do";
		$.ajax({
			url : theURL,
			async : false,
			dataType : "json",
			success : function(data) {
				col_menu.permision = data;
			}
		});
	},
	showMenu : function(event, treeId, treeNode) {
		if (!treeNode && event.target.tagName.toLowerCase() != "button"
				&& $(event.target).parents("a").length == 0) {
			col_tree.tree.cancelSelectedNode();
			col_menu.showRMenu("root", event.clientX, event.clientY);
		} else if (treeNode && !treeNode.noR) {
			if (treeNode.nocheck) {
				// 无权限，不允许操作
				// return false;
				// 改成无权限只要预览功能和栏目详情
                col_tree.tree.selectNode(treeNode);
                col_menu.showRMenu("node-nocheck", event.clientX, event.clientY);
			} else {
                col_tree.tree.selectNode(treeNode);
                col_menu.showRMenu("node", event.clientX, event.clientY);
			}
		}
	},
	showRMenu : function(type, x, y) {
		var p = col_menu.permision;
        var menuHeight = 122;
		// 不选中栏目时的右键菜单
		if (type == "root") {
            menuHeight=30;
			$("#rMenu ul").hide();

			$("#menuRefresh").show();
		} else if (type == "node-nocheck"){
            // 改成无权限只要预览功能和栏目详情
            // 选中栏目时的右键菜单
            $("#rMenu ul").hide();
            $("#menuView").show();
            $("#menuDetails").show();
		} else {
			// 选中栏目时的右键菜单
			$("#rMenu ul").hide();

			// $("#menuRefresh").show();
			// if (col_tree.ch != 1)
			$("#menuView").show();
			$("#menuDetails").show();
			$("#menuPubCol").show();
			$("#menuAddToColl").show();
			if (p.p3 == "true")
				$("#menuPub").show(); // 按栏目发布
			if(col_tree.ch==1){
				$("#menuAddToColl").hide();
			}
		}
		var menuHeight = 100;
		if (y + menuHeight > $(window).height())
			y = $(window).height() - menuHeight;
		$("#rMenu").css({
			"top" : y + "px",
			"left" : x + "px",
			"visibility" : "visible"
		});
	},
	hideRMenu : function() {
		if (col_menu.rMenu)
			col_menu.rMenu.style.visibility = "hidden";
	},

	// 按栏目发布
	curOpDialog : null,
	publish : function() {
		//用了一个取巧的方法，根据显示的是那个右键菜单，来区分是收藏夹还是栏目树
		var colID = favoriteDiv.id;
		if(col_menu.rMenu.style.visibility=="visible") {
			col_menu.hideRMenu();
			var node = col_tree.tree.getSelectedNodes();
			if (!node || node.length == 0)
				return;

			 colID = node[0].id;
		}
		var opurl = "article/pubByColumn.do?colID=" + colID + "&ch="
				+ col_tree.ch;

		var aWidth = 600;
		var aHeight = 500;
		var sWidth = document.body.clientWidth; // 窗口的宽和高
		var sHeight = document.body.clientHeight;

		if (aWidth + 10 > sWidth)
			aWidth = sWidth - 10; // 用e5.dialog时会额外加宽和高
		if (aHeight + 70 > sHeight)
			aHeight = sHeight - 70;

		// chrome下点击窗口的关闭时无法正确执行after.do，因此隐藏窗口关闭按钮，并不允许esc关闭
		var showClose = !e5.utils.isChrome();
		col_menu.curOpDialog = e5.dialog({
			type : "iframe",
			value : opurl
		}, {
			title : "按栏目发布",
			width : aWidth,
			height : aHeight,
			resizable : true,
			showClose : showClose,
			esc : true
		});
		col_menu.curOpDialog.show();
	},

	// 关闭“挂接栏目”操作窗口，供外部调用
	close : function() {
		if (col_menu.curOpDialog) {
			col_menu.curOpDialog.close();
			col_menu.curOpDialog = null;
		}
	},
	// 栏目预览，调用外网网页
	view : function() {
		var colID = favoriteDiv.id;
		if(col_menu.rMenu.style.visibility=="visible") {
			col_menu.hideRMenu();


			var node = col_tree.tree.getSelectedNodes();
			if (!node || node.length == 0)
				return;

			colID = node[0].id;
		}
		var opurl = "article/viewColumn.do?colID=" + colID + "&ch="
				+ col_tree.ch;

		var sWidth = screen.width - 50; // --去掉边--
		var sHeight = screen.height - 100;

		var feature = 'width=' + sWidth + ',height=' + sHeight
		+ ',left=0,top=0';
		var hWindow = window.open(opurl, "wndCol" + colID, feature, true);
		hWindow.focus();
	},
	// 栏目详情
	details : function() {
		var colID = favoriteDiv.id;
		if(col_menu.rMenu.style.visibility=="visible") {
			col_menu.hideRMenu();

			var node = col_tree.tree.getSelectedNodes();
			if (!node || node.length == 0)
				return;

		colID = node[0].id;
		}
		var opurl = "article/columnDetails.do?colID=" + colID + "&ch="
				+ col_tree.ch;

		var sWidth = 500; // --去掉边--
		var sHeight = 720;
		var sTop = (window.screen.availHeight - 30 - sHeight) / 2; // 获得窗口的垂直位置;

		var sLeft = (window.screen.availWidth - 10 - sWidth) / 2; // 获得窗口的水平位置;

		var feature = 'width=' + sWidth + ',height=' + sHeight + ',left='
				+ sLeft + ',top=' + sTop;
		var hWindow = window.open(opurl, "wndCol" + colID, feature, true);
		hWindow.focus();
	},

	// 刷新
	refresh : function() {
		col_menu.hideRMenu();
		favoriteDiv.loadColl();
		var node = col_tree.tree.getSelectedNodes();
		if (node && node.length > 0) {
			node = node[0];
			col_tree.tree.reAsyncChildNodes(node, "refresh");
		} else {
			window.location.reload();
		}
	},

	//添加一个栏目到收藏
	addToColl : function(){
		col_menu.hideRMenu();
		var siteID = col_tree.siteID;
		var type = col_tree.ch+6;     //ch为和表中的type相差为6  ch 0表示web,1表示app
		var node = col_tree.tree.getSelectedNodes();
		var id = node[0].id;
		if (!node || node.length == 0){
			return;
		}
		//添加前先判断有没有重复的还有长度不超过10个
		var all_a=$("#MyFavorite_ul li");
		if(all_a.length >= 10){
			alert("您最多只能添加10个栏目到收藏夹！")
			return false;
		}
		for(var i = 0;i<all_a.length;i++){
			if(id==$(all_a[i]).attr("colID")){
				alert("该栏目在收藏夹中已存在，无法重复添加！");
				return false;
			}
		}
		$.ajax({
			type:"post",
			url:"../xy/user/RelAdd.do?siteID="+siteID+ "&type="+type+ "&id="+id,
			success:function(data){
				var data =data;
				if(data=="ok"){
					alert("添加成功！");
				}else{
					alert("添加失败，请联系管理员！")
				}
				favoriteDiv.loadColl();
			}
		});
	},
	//重发栏目页
    publishCol: function () {
        //用了一个取巧的方法，根据显示的是那个右键菜单，来区分是收藏夹还是栏目树
        var colID = favoriteDiv.id;
        if(col_menu.rMenu.style.visibility=="visible") {
            col_menu.hideRMenu();
            var node = col_tree.tree.getSelectedNodes();
            if (!node || node.length == 0)
                return;

            colID = node[0].id;
        }
        var url = "../xy/article/pubColOperation.do?ch=" +  col_tree.ch
            + "&colID=" + colID+ "&type=" + 0 + "&pubCount=" + 0+ "&pubContent="+1;
		$.ajax({
		url: url,
		dataType: "text",
		async: false,
		error: function() {//请求失败处理函数
			alert("error");
		},
		success: function (data) {
			if (data.substr(0, 2) == "ok") {//发布成功
            }else{
				alert("error");
			}
		}
	});
    }
}


$(function() {
	col_menu.init();
	favoriteDiv.init();
	$(document).on({"contextmenu":function(){
		return false;
	}},"#rMenu-col")
});
