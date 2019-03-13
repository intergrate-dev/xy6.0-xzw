e5.mod("workspace.search",function() {
	var api;
	var searchClick = function(colId, title, typeselect) {
		var searchparam = new SearchParam();
		for (var name in main_param) 
			searchparam[name] = main_param[name];
		
		ch = channel_tab.getChannelType();
		var libParam = main_channelLib[ch];
		for (var name in libParam) 
			searchparam[name] = libParam[name];
		
		var rule = "";
		//选择栏目
		if (colId) rule += "_AND_a_columnID_EQ_" + colId;
		//页面区块内容选稿
		if(search_art.type == 1 && typeselect){
			rule += "_AND_a_type_EQ_" + typeselect;
		}
		//基本查询条件,已发布、站点
		rule += "_AND_a_status_EQ_1_AND_a_siteID_EQ_" + main_param.siteID;

		//最后去掉_AND_
		searchparam.ruleFormula = rule.substring(5);
		//标题
		if (title != ""){
			searchparam.query = "SYS_TOPIC=" + title;
		}
		api.broadcast("searchTopic", searchparam);
	};
	
	var defaultClick = function() {
		var param = new SearchParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		
		//按渠道设置docLibID和fvID
		ch = channel_tab.getChannelType();
		var libParam = main_channelLib[ch];
		for (var name in libParam) 
			param[name] = libParam[name];
		
		$.ajax({
			url: "../xy/wx/getInitCol.do",
			type : 'POST',
			data : {
				"ch" : ch ,
				"siteID" : main_param.siteID 
			},
			dataType: "json",
			async: false,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				colID = data.colID;
				colName = data.colName;
			}
		});
		
		$("#colFrmId").val(colID);
		$("#colFrm").val(colName);
		
		var rule = "";
		rule += "_AND_a_columnID_EQ_" + colID;
		//基本查询条件,已发布、站点
		rule += "_AND_a_status_EQ_1_AND_a_siteID_EQ_" + main_param.siteID;

		//最后去掉_AND_
		param.ruleFormula = rule.substring(5);
		
		api.broadcast("searchTopic", param);
	}
	
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
		channel_tab.tabClick = defaultClick;
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