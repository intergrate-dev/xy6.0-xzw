//区块内容的导航区
e5.mod("workspace.resourcetree",function() {
	var api;
	var treeClick = function(treeNode) {
		if (treeNode.nocheck) return false;
		
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		var blockID = treeNode.id;
		//判断节点ID中是否包含"_group"若包含，则不需要处理，若是不包含，则需要处理。"_group"是对分组树组名ID的特别标示。
		if(blockID.indexOf("_group")<0){
			param.ruleFormula = param.blockIDField + "_EQ_" + blockID;
			param.groupID = blockID; //统一使用groupID作为参数传递
			api.broadcast("resourceTopic", param);
		}
	};
	
	var init = function(sandbox) {
			api = sandbox;
		}
	return {
		init: init,
		treeClick : treeClick
	}
});