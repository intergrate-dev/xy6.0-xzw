//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
e5.mod("workspace.resourcetree",function() {
	var api;
	var defaultClick = function() {
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
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
	},
	onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload:onload
	}
});
