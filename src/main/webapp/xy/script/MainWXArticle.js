//微信菜单稿件：导航
e5.mod("workspace.resourcetree", function() {
	var api;
	
	var init = function(sandbox) {
		api = sandbox;
		$(".menu-sub").click(treeClick);
	},
	onload = function() {
		defaultClick();
	}

	var treeClick = function(evt) {
		$(".menu-sub").removeClass("select");
		$(this).addClass("select");
		
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		var groupID = $(evt.target).attr("groupID");
		if (!param.ruleFormula)
			param.ruleFormula = "wx_menuID_EQ_" + groupID;
		else
			param.ruleFormula = "wx_menuID_EQ_" + groupID + "_AND_" + param.ruleFormula;
		param.groupID = groupID;
		
		api.broadcast("resourceTopic", param);
	}
	var defaultClick = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
		if (!ready) {
			setTimeout(defaultClick, 100);
			return;
		}

		$(".menu-sub").first().click();
	}
	return {
		init : init,
		onload : onload
	};
});