var b_tree={
	//外界修改的参数
	siteID : 1, //当前站点ID

	rootUrl : "../../xy/block/Block.do?",
	rootFindUrl : "../../xy/block/Find.do?siteID=",
	tree:null,
	check : {
		enable : true,
		chkboxType : {"Y":"ps", "N":"ps"},
		chkStyle : "checkbox",
		radioType : "all",
		ids : "" //预设的选中id
	},
	init : function() {
		if(b_tree.dataType=="8") {
            b_tree.rootUrl = "../../xy/common/group/getGroup.do?";
            $(".divBlockSearch").hide();
        }

        if (typeof articleIDs != "undefined" && articleIDs!="null"){
            b_tree.rootUrl+="Op=1"
		}
		var theURL = b_tree.rootUrl + "&siteID=" + b_tree.siteID;
		$.ajax({type:'POST',url:theURL, async:false, dataType:"json", success:b_tree._show});
		
		b_tree.autoCompleter.init();
	},
	_show : function(data) {
			var setting = {
					callback: {
						onAsyncSuccess: b_tree._afterExpand,
						onClick: b_tree._click
					},
					view:{
						dblClickExpand: false,
						showLine: true,
						fontCss:{'color':'black','font-weight':'bold'},
						selectedMulti: false 
					},
					edit: true,
					data:{
					       simpleData: {//简单数据模式            
					    	   	enable:true,            
					    	   	idKey: "id",            
					    	   	pIdKey: "pid",            
					    	   	rootPId: "0"        
					    		}
					},
					check: b_tree.check
			};
		if ($.fn.zTree.init) $.fn.zTree.init($("#rs_tree"), setting, data);
		b_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");
		b_tree._initIds();
		b_tree._firstExpand();
		b_tree._firstClick();
	},
	//展开后，找到预选节点，设为checked状态
	_initIds : function() {
		if (!b_tree.check.enable) return;
		if (!b_tree.check.ids) return;
		
		//不能只读当前子节点，对于无权限的节点，一次性会显示多层树
		var ids = b_tree.check.ids.split(",");
		for (var i = 0; i < ids.length; i++) {
			var node = b_tree.tree.getNodeByParam("id", ids[i], false);
			if (node && !node.checked) {
				node.checked = true;
				b_tree.tree.updateNode(node);
			}
		}
	},
	//获取复选框选中的节点
	getChecks : function() {
		var nodes = b_tree.tree.getCheckedNodes();
		var ids = "";
		for (var i = 0; i < nodes.length; i++) {
			if (ids && nodes[i].pid !=0 ) {
				ids += ",";
			}
			if(nodes[i].pid !=0 ){
				ids += nodes[i].id;
			}
		}
		return [ids];
	},
	_firstExpand : function() {
		var nodes = b_tree.tree.getNodesByParam("pid", "0", null);
		if (nodes && nodes.length > 0) {
			for(var i = 0; i< nodes.length;i++)
			{
				b_tree.tree.expandNode(nodes[i], true, false, true);
			}
		}
	},
	_firstClick : function(){
		if (!b_tree.tree || b_tree.tree.getNodes().length == 0) return;
		
		var nodes = b_tree.tree.getNodes()[0].children;
		if (!b_tree.check.enable && nodes && nodes.length > 0) {
			b_tree._nodeClick(nodes[0]);
		}
	},
	_nodeClick : function(node) {
		var id = node.tId + "_a";
		$("#" + id).click();
	},
	_click : function(event, treeId, treeNode, clickFlag) {
		if (treeNode.nocheck) return false;
		
		if (b_tree.check.enable) {
			//点击时也触发checkbox
			b_tree.tree.checkNode(treeNode);
		}
		b_tree.blockClick0(event, treeId, treeNode, clickFlag);
	},
	//点击页面区块的实际响应函数，外界可以通过修改这个函数而增加点击区块的响应
	blockClick0 : function(event, treeId, treeNode, clickFlag) {
		return false;
	},
	find : function(bID) {
		//bID:要定位的区块页面的ID
		var block = b_tree.tree.getNodeByParam("id", bID, null);
		if (block) {		
			b_tree.tree.checkNode(block);
			b_tree.tree.selectNode(block);
//			b_tree._nodeClick(block);
			//用上述通过onClick方式响应触发不了，先自己写个searchClick方法来进行处理
			if(!b_tree.check.enable){//现在页面区块内容里面用吧
				searchClick(block);
			}

			return;
		}
	}
};

//---------区块名称查找框-------------
b_tree.autoCompleter = {
	url : null,
	init : function() {
		b_tree.autoCompleter.url = b_tree.rootFindUrl + b_tree.siteID;
		
		var s = $("#blockSearch");
		s.attr("placeholder", "查找");
		s.autocomplete(b_tree.autoCompleter.url, b_tree.autoCompleter.options);
		s.keydown(b_tree.autoCompleter.search);
		s.focus();
	},
	search : function(event) {
		if (event.keyCode == 13) {
			var text = $("#blockSearch").val();
			var theURL = b_tree.autoCompleter.url + "&q=" + b_tree.autoCompleter.encode(text);
			
			$.ajax({url:theURL, async:false, dataType:"json", success:function(data){
				if (data) {
					var data = data[0].key;
					b_tree.find(data);
				}
			}});
			return false;
		}
	},
	options : {
		minChars : 1,
		delay : 400,
		autoFill : true,
		selectFirst : true,
		matchContains: true,
		cacheLength : 1,
		dataType:'json',
		//把data转换成json数据格式
		parse: function(data) {
			if (!data)
				return [];
			
			return $.map(eval(data), function(row) {
				return {
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},
		//显示在下拉框中的值
		formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }
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

$(function() {
	b_tree.init();
});