//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
e5.mod("workspace.resourcetree",function() {
	var api;
	var defaultClick = function(evt) {
		var src = $(evt.target);
		$(".channelTab").removeClass("select");
		src.addClass("select");
		
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		param.opFlow = true; //源稿带分类了所以不能在“我的”里创建。直接不显示操作。
		param.ruleFormula = src.attr("rule");
		
		var channel = src.attr("channel");
		if (channel) {
			var theParam = self_param[channel];
			for (var name in theParam) 
				param[name] = theParam[name];
			//其它
			//param.opFlow = true;
		}
		//可能在发布库和草稿箱中切换，需重新读操作
		var procUrl = main_procs.getUrl(param.docLibID, param.fvID, param.ruleFormula, param.opFlow);
		main_procs.readData(procUrl);
		
		api.broadcast("resourceTopic", param);
	}
	//检查statusbar是否加载完毕，加载完毕才发送点击消息
	var checkLoad = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady&&!!searchReady&&statusReady()&&searchReady();
		if (!ready) {
			setTimeout(checkLoad, 100);
			return;
		}
		$(".channelTab").first().click();
	}
	var init = function(sandbox) {
		api = sandbox;
		
		var tabs = $(".channelTab");
		tabs.click(defaultClick);
		
		if (tabs.length <= 1) {
			$(".channels").hide();
		}
	},
	onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload:onload
	}
},{requires:["../e5script/jquery/jquery.min.js"]});
