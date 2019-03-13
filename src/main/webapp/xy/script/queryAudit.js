//查询条件初始化
var nis_query = {
	init : function() {
		var sel = document.getElementById("a_status");
		if (sel) {
			var options = sel.options;
			if (options[0].text == "") {
				sel.selectedIndex = 1;
				$("#searchList").click();
			}
		}
		nis_query.initGroup();
	},
	//互动问答的分类初始化，取站点下的一层
	initGroup : function(){
		var sel = document.getElementById("a_group_ID");
		if (!sel) return;
		
		var catType = sel.getAttribute("catType");
		if (!catType) return;
		
		while (sel.options.length > 0)
			sel.remove(0);
		//增加全选
		var showAll = sel.getAttribute("show-all");
		if (showAll == "true") {
			var op = document.createElement("OPTION");
			op.value = "";
			op.text = "";
			sel.options.add(op);
		}
		var dataUrl = "../xy/Cats.do?catType=" + catType + "&siteID=" + nis_query.getParam("siteID");
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
			if(datas != null && datas.length > 0){
				for (var i = 0; i < datas.length; i++) {
					var op = document.createElement("OPTION");
					op.value = datas[i].catID;
					op.text = datas[i].catName;
					sel.options.add(op);
				}
			}
		}});
	},
	getParam : function(name) {
		var params = window.location.href;
		params = params.substring(params.indexOf("?") + 1, params.length);
		params = params.split("&");
		
		for (var i = 0; i < params.length; i++) {
			var arr = params[i].split("=");
			if (arr[0] == name) {
				return params[i].substring(name.length + 1, params[i].length);
			}
		}
		return null;
	}
}

$(function() {
	var init = function() {
		//等待查询界面加载完成
		var div = $("#divQueryCust").html();
		if (!div) {
			setTimeout(init, 100);
			return;
		}
		nis_query.init();
	}
	init();
});