//----目录管理：右键菜单----
var dir_menu = {
	rMenu : document.getElementById("rMenu"),
	dirLibID : 0, //域名目录ID
	dialog : null,
	init : function() {
		dir_tree.dirMenu0 = dir_menu.showMenu;
		$("body").bind("mousedown", function(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				dir_menu.rMenu.style.visibility = "hidden";
				dir_menu.rMenu.style.visibility = "";
			}
		});
		dir_menu._readData();
		
		$("#menuAddRoot").click(dir_menu.addRoot);
		$("#menuAdd").click(dir_menu.addDir);
		$("#menuDelete").click(dir_menu.delDir);
		$("#menuSwitch").click(dir_menu.domainSwitch);
		
		$("#buttonAddRoot").click(dir_menu.addRoot);
		$("#buttonAdd").click(dir_menu.bAddDir);
		$("#buttonDelete").click(dir_menu.bDelDir);
		$("#buttonResPath").click(dir_menu.bResDir);
		
	},
	
	_readData : function() {
		var theURL = "SiteInit.do?siteId=" + dir_tree.siteID;
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
			
			dir_menu.dirLibID = data.docLibID;
			$("#pathLi").html("资源文件路径："+ data.dirpath);
			$("#urlLi").html("资源文件发布地址：" + data.dirurl);
		}});
	},
	showMenu : function(event, treeId, treeNode) {
		if (!treeNode && event.target.tagName.toLowerCase() != "button" 
			&& $(event.target).parents("a").length == 0) {
			dir_tree.tree.cancelSelectedNode();
			dir_menu.showRMenu("root", event.clientX, event.pageY);//clientY
		} else if (treeNode && !treeNode.noR) {
			dir_tree.tree.selectNode(treeNode);
			
			if (treeNode.getParentNode() != null) {
				dir_menu.showRMenu("node", event.clientX, event.pageY);
			} else {
				dir_menu.showRMenu("rootNode", event.clientX, event.pageY);
			}
		}
	},
	showRMenu : function(type, x, y) {
		$("#rMenu ul").show();
		if (type=="root") {
			$("#menuAdd").hide();
			$("#menuUpdate").hide();
			$("#menuDelete").hide();
			$("#menuSwitch").hide();
			$("#menuAddRoot").show();
		} else if (type=="node"){
			$("#menuAdd").show();
			$("#menuUpdate").show();
			$("#menuDelete").show();
			$("#menuSwitch").hide();
			$("#menuAddRoot").hide();
		} else {
			$("#menuAdd").show();
			$("#menuUpdate").show();
			$("#menuDelete").show();
			$("#menuSwitch").show();
			$("#menuAddRoot").hide();
		}
		$("#rMenu").css({"top":y+"px", "left":x+"px", "visibility":"visible"});

		$("body").bind("mousedown", dir_menu.onBodyMouseDown);
	},
	
	onBodyMouseDown:function(event){
		if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
			$("#rMenu").css({"visibility" : "hidden"});
		}
	},
	
	hideRMenu : function() {
		if ($("#rMenu")) $("#rMenu").css({"visibility": "hidden"});
		$("body").unbind("mousedown", dir_menu.onBodyMouseDown);
	},

	//点击增加目录
	addDir : function() {
		dir_menu.hideRMenu();
		
		var sel = dir_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		var url = "../../e5workspace/manoeuvre/Form.do?code=formDomainDir&new=1"
			+ "&DocLibID=" + dir_menu.dirLibID
			+ "&parentID=" + sel[0].id
			+ "&siteID=" + dir_tree.siteID;
		
		dir_menu.dialog = e5.dialog({type:"iframe", value:url}, 
				{title:"增加子目录", width:"500px", height:"200px",resizable:false});
		dir_menu.dialog.show();
	},
	//点击增加根目录
	addRoot : function() {
		dir_menu.hideRMenu();
		var url = "../../e5workspace/manoeuvre/Form.do?code=formDomain&new=1"
			+ "&DocLibID=" + dir_menu.dirLibID
			+ "&siteID=" + dir_tree.siteID
			+ "&parentID=0";
		
		dir_menu.dialog = e5.dialog({type:"iframe", value:url}, 
				{title:"增加域名目录", width:"500px", height:"250px",resizable:false});
		dir_menu.dialog.show();
	},
	//域名切换
	domainSwitch : function() {
		dir_menu.hideRMenu();
		
		var sel = dir_tree.tree.getSelectedNodes();
		if (sel.length == 0) return;
		var url = "Switch.do?DocLibID=" + dir_menu.dirLibID
			+ "&DocIDs=" + sel[0].id
			+ "&siteID=" + dir_tree.siteID;
		
		dir_menu.dialog = e5.dialog({type:"iframe", value:url}, 
				{title:"域名切换", width:"500px", height:"200px",resizable:false});
		dir_menu.dialog.show();
	},
	//删除目录
	delDir : function() {
		dir_menu.hideRMenu();
		var node = dir_tree.tree.getSelectedNodes();
		if (node.length > 0) 
			node = node[0];
		if (node.nodes && node.nodes.length > 0) {
			var msg = "目录(" + node.name + ")还有子目录，确定要连同子目录一起删掉吗？";
			if (!confirm(msg))
				return;
		} else {
			var msg = "确定要删除目录(" + node.name + ")吗？";
			if (!confirm(msg))
				return;
		}
		var theURL = "DomainDirIsUsed.do?dirID=" + node.id;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				dir_menu.delOperate(node.id);
			} else {
				alert("目录或子目录被引用，不能删除");
				return;
			}
		}});
	},
	
	delOperate: function(dirID){
		var theURL = "Deletedir.do?dirID=" + dirID;
		$.ajax({url:theURL, async:false, success: function(data) {
			if (data == "ok") {
				dir_tree.treeDelete(dirID);
			} else {
				alert("删除时异常：" + data);
			}
		}});
	},
	bAddDir: function(){
		var nodes = dir_tree.tree.getSelectedNodes();
		if (nodes && nodes.length > 0 ){
			dir_menu.addDir();
		}else{
			alert("没有选中的节点");
		}

	},
	bDelDir: function(){
		var nodes = dir_tree.tree.getSelectedNodes();
		if ( nodes && nodes.length > 0 ){
			dir_menu.delDir();
		}else{
			alert("没有选中的节点");
		}

	},
	bResDir: function(){
		var nodes = dir_tree.tree.getSelectedNodes();
		if ( nodes && nodes.length > 0 ){
			dir_menu.resDir(nodes);
		}else{
			alert("没有选中的节点");
		}

	},
	resDir : function(dirID) {
		var theURL = "resdir.do?dirID=" + dirID[0].id + "&siteID=" + dir_tree.siteID;
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
			if(data.status == "success"){
				$("#pathLi").html("资源文件路径："+ data.dirpath);
				$("#urlLi").html("资源文件发布地址：" + data.dirurl);
			}else{
				alert("添加失败！请找管理员查看系统日志！");
			}
		}});
	}
	
}

$(function() {
	dir_menu.init();
});