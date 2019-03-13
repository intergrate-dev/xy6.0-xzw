e5.mod("workspace.search",function() {
	var api, _superdoclist;
	
	//切换栏目，提交查询
	var searchClick = function() {
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
		searchparam.ruleFormula = "CLASS_1_EQ_" + $("#column").val();
		
		//发出search栏消息，statusbar会接收并显示列表
		api.broadcast("searchTopic", searchparam);
	}
	
	//列表加载完成时
	var listListening = function() {
		//添加点击打开细览的事件
		var list = $("#listing table tr");
		list.click(view);
		
		//隐藏翻页
		$("#status li.status-pages").hide();
		$("#btnChangepage").hide();
		$("#btnFirstpage").hide();
		$("#pagesArea").hide();
		$("#btnFinalpage").hide();
	}
	
	//点击稿件时，打开预览窗口
	var view = function(evt) {
		//找到tr
		var src = $(evt.target);
		var count = 0;
		while (src[0].tagName.toLowerCase() != "tr") {
			src = src.parent();
			if (++count > 20) break;
		}
		
		var docID = src.attr("id");
		if (!docID) return;
		
		var url = "../xy/View.jsp?DocLibID=" + main_param.docLibID
			+ "&DocIDs=" + docID
			+ "&FVID=" + main_param.fvID;
		window.open(url);
	}
	
	//判断是否加载完毕
	var checkLoad = function() {
		try {
			_superdoclist = e5.mods["workspace.doclist"].self;
			if (!_superdoclist) {
				setTimeout(checkLoad, 100);
				return;
			}
			searchClick();
		} catch (e) {
			alert(e.message);
		}
	}
	var init = function(sandbox) {
		$("#column").change(searchClick);
		
		api = sandbox;
		api.listen("workspace.doclist:setDataFinish", listListening);
	}
	var onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload: onload
	}
});