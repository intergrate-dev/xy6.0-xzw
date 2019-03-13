var tpl_tree={
	//外界修改的参数
	siteID : 1, //当前站点ID
	rootUrl : "../../xy/template/moveChannel.do?",
	tree:null,
	init : function() {
		var theURL = tpl_tree.rootUrl + "&siteID=" + tpl_tree.siteID
		$.ajax({type:'POST',url:theURL, async:false, dataType:"json", success:tpl_tree._show});
		
		tpl_tree.autoCompleter.init();
		if(tpl_tree.ckeckedID != null&& tpl_tree.ckeckedID != ""){
			tpl_tree.find(tpl_tree.ckeckedID);
		}
	},
	_show : function(data) {
			var setting = {
					view:{
						dblClickExpand: false,
						showLine: true,
						fontCss:{'color':'black','font-weight':'bold'},
						selectedMulti: false 
					},
					data:{
					       simpleData: {//简单数据模式            
					    	   	enable:true,            
					    	   	idKey: "id",            
					    	   	pIdKey: "pid",            
					    	   	rootPId: "0"        
					    		}
					}
			};
		$.fn.zTree.init($("#rs_tree"), setting, data);
		tpl_tree.tree = $.fn.zTree.getZTreeObj("rs_tree");
		   
		
		
		//tpl_tree._firstExpand();
	},
	_firstExpand : function() {
		var nodes = tpl_tree.tree.getNodesByParam("pid", "0", null);
		if (nodes && nodes.length > 0) {
			for(var i = 0; i< nodes.length;i++)
			{
				tpl_tree.tree.expandNode(nodes[i], true, false, true);
			}
		}
	},
	getCheck : function() {
		
		var nodes = tpl_tree.tree.getSelectedNodes();
		var node = null;
		var id = "", name = "";
		if ( nodes && nodes.length > 0 ){
			node = nodes[0];
			id = node.id;
			name = node.name;
			return [id.replace("_group",""), name];
		}
		return "null";
	},
	find : function(bID) {
		bID = bID + "_group";
		//bID:要定位的模板的ID
		var tpl = tpl_tree.tree.getNodeByParam("id", bID, null);
		if (tpl) {		
			tpl_tree.tree.selectNode(tpl);			
			return;
		}
	}
};

//---------模板名称查找框-------------
tpl_tree.autoCompleter = {
	url : null,
	init : function() {
		tpl_tree.autoCompleter.url = "../../xy/template/FindChannel.do?siteID=" + tpl_tree.siteID;
		var s = $("#templateSearch");
		s.autocomplete(tpl_tree.autoCompleter.url, tpl_tree.autoCompleter.options);
		//s.keydown(tpl_tree.autoCompleter.search);
        s.result(tpl_tree.autoCompleter.search);
		s.focus();
	},
	search : function(event) {
		
		//if (event.keyCode == 13) {
			var text = $("#templateSearch").val();
			var theURL = tpl_tree.autoCompleter.url + "&q=" + tpl_tree.autoCompleter.encode(text);
			
			$.ajax({url:theURL, async:false, dataType:"json", success:function(data){
				if (data) {
					var data = data[0].key;
					tpl_tree.find(data);
				}
			}});
			return false;
		//}
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
		formatItem: function(row, i,max) { return '['+row.key+']'+row.value; },
		formatMatch: function(row, i,max) { return '['+row.key+']'+row.value; },
		formatResult: function(row, i,max) { return '['+row.key+']'+row.value; }
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
	tpl_tree.init();
});