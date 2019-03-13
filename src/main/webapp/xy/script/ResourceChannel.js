//带渠道的显示
e5.mod("workspace.resourcetree",function() {
	var api;
	var defaultClick = function() {
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		//按渠道设置docLibID和fvID
		var ch = channel_tab.getChannelType();
		var libParam = main_channelLib[ch];
		for (var name in libParam) 
			param[name] = libParam[name];
			
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
		defaultClick();
	}
	var init = function(sandbox) {
		api = sandbox;
		
		//渠道点击的响应
		channel_tab.tabClick = defaultClick;
	},
	onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload:onload
	}
});
