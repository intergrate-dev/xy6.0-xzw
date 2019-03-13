//ResourceTree for 审核稿件
e5.mod("workspace.resourcetree",function() {
	var api;
	var defaultClick = function(evt) {
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		//按渠道设置docLibID和fvID
		var src = $(evt.target);
		var ch = src.attr("channel");
		var libParam = main_channelLib[ch];
		for (var name in libParam) 
			param[name] = libParam[name];
		param.opFlow = true;
		if(!!ch) {
            param.ruleFormula = "a_columnID_EQ_" + src.attr("colID") + "_AND_" + param.ruleFormula;
        }else {
            param.ruleFormula = "SYS_CURRENTNODE_EQ_" + src.attr("nodeID") +  "_AND_" + param.ruleFormula  + "_AND_" + "A_SITEID_EQ_" +  param.siteID ;
        }
		api.broadcast("resourceTopic", param);
		
		$('.column').removeClass("select");
		src.addClass("select");
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
		$(".column").first().click();
	}
	var init = function(sandbox) {
		api = sandbox;
		
		$(".column").click(defaultClick);
	},
	onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload:onload
	}
});
