e5.mod("workspace.search",function() {
	var api;
	var searchClick = function(type) {
		
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
		
		var rule = searchparam.vSiteIDField + "_EQ_" + siteID +
					"_AND_" +  searchparam.vGroupIDField + "_EQ_" + type;

		searchparam.ruleFormula = rule;
		api.broadcast("searchTopic", searchparam);
	};
	var checkLoad = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var ready = !!statusReady&&statusReady();
		if (!ready) {
			setTimeout(checkLoad, 100);
			return;
		}
		
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
			
		api.broadcast("searchTopic", searchparam);
	}
	
	var init = function(sandbox) {
		api = sandbox;
	}
	var onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload: onload,
		searchClick : searchClick
	}
},{requires:[
"../e5workspace/script/Param.js"
]});
