//带分组的导航
e5.mod("workspace.resourcetree", function() {
	var api;
	
	onload = function() {
		defaultClick();
	}

	var treeClick = function(evt) {
		$("#groupUl li div").removeClass("select");
		$(this).addClass("select");
		
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		var groupID = $(evt.target).attr("groupID");
		param.groupID = groupID;
		if (!param.ruleFormula)
			param.ruleFormula = param.groupField + "_EQ_" + groupID;
		else
			param.ruleFormula = param.groupField + "_EQ_" + groupID + "_AND_" + param.ruleFormula;

		$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).show();
		$("#groupIdInput_" + $("#chosenGroupIDInput").val()).hide();
		
		// 当用户点击列表的时候，给隐藏域赋值，以便于修改时知道当前选择的是哪个组
		$("#chosenGroupIDInput").val(groupID);

	}
	var defaultClick = function() {
		
		$(".group").find("Div").click(treeClick);
		
		// 绑定点击事件
		/*$("input[id^=groupIdInput]").keypress(
		function(event) {
			if (event.keyCode == 13) {
				submitModifiedGroup();
			}
		});*/
		
		$(".group").find("Div").first().click();
	}
	return {
		onload : onload,
	};
});
