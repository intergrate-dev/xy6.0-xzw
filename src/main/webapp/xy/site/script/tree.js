var dir_tree={
	//外界修改的参数
	siteID : 0, //当前站点ID
	rootUrl : "DomainDirByPId.do?parentID=0",
	tree:null,
	init : function() {
		var theURL = dir_tree.rootUrl + "&siteID=" + dir_tree.siteID;
		$.ajax({type:'POST',url:theURL, async:false, dataType:"json", success:dir_tree._show});	
	},
	_show : function(data) {
			var setting = {
					view:{
						dblClickExpand: false,
						showLine: true,
						fontCss:{'color':'black','font-weight':'bold'},
						selectedMulti: false 
					},
					//异步加载
					async: {
						enable : true,
						url : "DomainDirByPId.do?siteID=" + dir_tree.siteID,
						autoParam: ["id=parentID"],
						dataType : "json"
					},
					data:{
					       simpleData: {//简单数据模式            
					    	   	enable:true,            
					    	   	idKey: "id",            
					    	   	pIdKey: "pid",            
					    	   	rootPId: "0"        
					    		}
					},
					callback:{
						onClick: dir_tree._click,
						onRightClick: dir_tree._menu
					}
				
			};
		$.fn.zTree.init($("#rs_tree"), setting, data);
		dir_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");
		//默认展开一级目录
		//dir_tree._firstExpand();
	},
	_firstExpand : function() {
		var nodes = dir_tree.tree.getNodesByParam("pid", "0", null);
		if (nodes && nodes.length > 0) {
			for(var i = 0; i< nodes.length;i++)
			{
				dir_tree.tree.expandNode(nodes[i], true, false, true);
			}
		}
	},
	getCheck : function() {
		var nodes = dir_tree.tree.getSelectedNodes();
		var node = null;
		if ( nodes && nodes.length > 0 ){
			node = nodes[0];
		}
		
		var id = "", url = "";
		var urlArr = new Array();	
		if ( node != null ){
			id = node.id;
			if(node.pid == 0){
				url = node.dirurl;
				return [id, url];
			}else{
				urlArr.push(node.dirname);
			}		
			while (node.getParentNode() != null) {
				var parent = node.getParentNode();
				if( parent.pid == 0){
					urlArr.push(parent.dirurl);
					break;
				}else{
					urlArr.push(parent.dirname);
				}
				node = parent;
			}
		}
		
		var arr = urlArr.reverse();
		url = arr.join("/");
		
		return [id, url];
	},
	_menu : function(event, treeId, treeNode) {
		dir_tree.dirMenu0(event, treeId, treeNode);
	},
	
	dirMenu0 : function(event, treeId, treeNode) {
		return false;
	},
	
	treeAdd : function(dirID, dirName, parentID) {
		if(typeof(dirName) != "undefined"){
			var parent = dir_tree.tree.getNodeByParam("id", parentID, null);
			dir_tree.tree.addNodes(parent, [{ name:dirName, id:dirID}]);
		}
	},
	treeUpdate : function(dirID, dirName) {
		var dir = dir_tree.tree.getNodeByParam("id", dirID, null);
		dir.name = dirName;
		dir_tree.tree.updateNode(dir);
	},
	treeDelete : function(dirID) {
		var dir = dir_tree.tree.getNodeByParam("id", dirID, null);
		dir_tree.tree.removeNode(dir);
	},
	//点击目录的实际响应函数，外部重写这个函数可以实现单击事件
	dirClick0 : function(event, treeId, treeNode, clickFlag) {
		return false;
	},
	//域名栏目树的单击事件
	_click : function(event, treeId, treeNode, clickFlag) {
		if (treeNode.nocheck) return false;
		dir_tree.dirClick0(event, treeId, treeNode, clickFlag);
	}
};

$(function() {
	dir_tree.init();
});









