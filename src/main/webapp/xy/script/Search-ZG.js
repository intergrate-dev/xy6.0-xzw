e5.mod("workspace.search",function() {
	var api, searchparam,ready = false,
		listening = function(msgName, callerId, param) {
			for (var name in searchparam) searchparam[name] = "";
			for (var name in param) searchparam[name] = param[name];

			_reset(searchparam);

			search();
		},
		search = function(){
			searchparam.filterID = getFilterIDs();
			// 发布库打开以后默认显示当日的所有栏目所有稿件--添加了默认查询条件：当日时间区间
			if(searchparam.isFirstClickQuery && searchparam.isFirstClickQuery.indexOf("isFirstClick") != -1){
                searchparam.query = searchparam.isFirstClickQuery.replace('isFirstClick', '');
                searchparam.isFirstClickQuery = '';
                // 隐藏稿件的相关的操作按钮
                $("#toolTR").hide();
			} else {
                searchparam.query = _getQuery();
                // 显示之前隐藏的稿件的相关的操作按钮
                var tabName = $(window.parent.document).find("#tabContentDiv .tab-pane.active iframe").attr("showname");
                if(!($("#rs_tree").is(":visible") && tabName=="Web发布库" && col_tree && col_tree.tree && col_tree.tree.getSelectedNodes().length==0)) {
                    $("#toolTR").show();
				} else {
                	var _query = searchparam.query;
                	if (_query.indexOf("&a_pubTime_0=") == -1){
                        var date = new Date();
                        var day = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+(date.getDate()<10?'0'+date.getDate():date.getDate());
                        var d1 = "&a_pubTime_0=" + day + " 00:00:00";
                        var d2 = "&a_pubTime_1=" + day + " 23:59:59";
                        searchparam.query = _query.replace(/(@QUERYCODE@=qArticle)/, '$1'+d1+d2);
					}
                }
			}
			if (searchparam.query === false) {
				return;
			}

			if(searchparam.query == "@QUERYCODE@=qRevoke&a_status=1"){
                var _endDate = new Date();
                var _startDate = "";
                var _year1 = parseInt(_endDate.getFullYear());
                var _month0 = _endDate.getMonth();
                if(_month0 == 0){
                    var _year0 = _year1-1;
                    _month0 = 12;
                    var _month1 = 1;
                }else{
                    var _year0 = _year1;
                    var _month1 = _month0 + 1;
                }
                var _day = _endDate.getDate();

                _endDate = _year1 + "-" + add_zero(_month1) + "-" + add_zero(_day) + "%2000:00:00";
                _startDate = _year0 + "-" + add_zero(_month0) + "-" + add_zero(_day) + "%2000:00:00";

                searchparam.query = "@QUERYCODE@=qRevoke&a_status=1&a_pubTime_0="+_startDate+"&a_pubTime_1="+ _endDate;
                $("#a_pubTime_0").val(_year0 + "-" + add_zero(_month0) + "-" + add_zero(_day) + " 00:00:00");
                $("#a_pubTime_1").val(_year1 + "-" + add_zero(_month1) + "-" + add_zero(_day) + " 00:00:00");

			}

			if (searchparam.extType != "1") {
				searchparam.extType = searchparam.query ? 3 : searchparam.extType;
			}
			api.broadcast("searchTopic", searchparam);
		},

        add_zero = function(param){
        	if(param < 10){
            	return "0" + param;
        	}
        	return param;
		},

		changeFilter = function(){
			search();
			_save2Cookie(searchparam);
		},
		// 得到当前的过滤器ID串（处理多组的情况）
		getFilterIDs = function(){
			var theFilters = "";
			for (var index = 0; ; index++){
				var sel = document.getElementById("Filters_" + index);
				if (!sel) break;
				if (sel.value) theFilters += sel.value + ",";
			}
			return theFilters;
		},
		_getQuery = function() {
			//调用查询条件读取函数
			var conditions = e5_queryform.getQuery();
			return conditions;
		},

		_reset = function(param){
			//重置查询条件
			_resetQuery(param);

			//取过滤器
			var getFilterURL = "../e5workspace/statusbar.do?DocTypeID=" + param.docTypeID
				+ "&FVID=" + param.fvID;
			$.ajax({url:getFilterURL, async:false, success:function(data) {
				var last = _readCookie();
				var last = (last) ? last.split(",") : null;

				//把从服务器得到的过滤器拆分成组
				var filterArray = null;
				if (data) filterArray = data.split("@");

				for (var index = 0; ; index++){
					var sel = document.getElementById("Filters_" + index);
					if (!sel) break;

					resetSelect(sel, getArrayData(filterArray, index), getArrayData(last, index));
				}
			}});
		},
		_resetQuery = function(param) {
			var div = $("#divQueryCust").empty();

			// $("#btnSearch").hide();
			var curName = $('.select span', window.parent.document).html();
			//取定制的查询条件
			var url = (param.queryID && param.queryID != "0")
				? "../e5workspace/statusbar.do?Flag=2&QueryID=" + param.queryID
				: "../e5workspace/statusbar.do?Flag=2&DocTypeID=" + param.docTypeID;
			$.ajax({url:url, async:false, success:function(data) {
				if (data) {
                    data=data.replace(/validate\[custom\[dateFormat\]\]/g, 'validate[custom[dateTimeFormat1]]');
                    if(curName == '撤稿中心'){
                    	div.html(data);
                    	var sel = document.getElementById("a_status");
						while (sel.options.length > 0)
							sel.remove(0);

						var op = document.createElement("OPTION");
						op.value = 1;
						op.text = "已发布";
						sel.options.add(op);

						var op = document.createElement("OPTION");
						op.value = 7;
						op.text = "已撤稿";
						sel.options.add(op);
                    }else{
                    	div.html(data);
                    }
					e5_queryform.init();
					e5_queryform.reset.defKeydown(search);
					
					$("#main_search").removeClass("noQueryCust");
					if (!$("#list_more").children().length){
						$("#toggleSearchAdvList").hide();
					}
				}
			}});
		},
		_readCookie = function() {
			var cookieName = _cookieName(searchparam);
			return e5.utils.getCookie(cookieName);
		},
		/* cookie里记的过滤器名。
		 * 缺省是用fvID区分，文件夹太多时可能cookie不够用。
		 * 上层应用可酌情修改为docLibID或docTypeID。
		 * 注意若无过滤器，则不会记录cookie
		 */
		_cookieName = function(param) {
			return "ws_filter_" + param.fvID;
		},
		//记在Cookie中
		_save2Cookie = function(param) {
			if (!param.filterID) return;
			try {
				var cookieName = _cookieName(param);
				e5.utils.setCookie(cookieName, param.filterID);
			}catch (e){}
		},
		getArrayData = function(valueArray, index){
			if (valueArray && valueArray[index]) return valueArray[index];
			return null;
		},
		resetSelect = function(sel, data, valueInCookie){
			while (sel.options.length > 0) sel.remove(0);
			
			if (data) {
				//value1,text1;value2,text2
				var filterArr = data.split(";");
				for (var j = 0; j < filterArr.length; j++) {
					var f = filterArr[j].split(",");
					if (f.length < 2) continue;

					var op = document.createElement("OPTION");
					op.value = f[0];
					op.text = f[1];
					sel.options.add(op);
					if (op.value == valueInCookie)
						op.selected  = true;
				}
			}
			/* 若没有选项，则不可见 */
			if (sel.options.length == 0) {
				sel.style.display = "none";
			}
			else
				sel.style.display = "";
		},

		keysearch = function(){
			if (event.keyCode == 13) search();
		},
		showAdvSearch = function(e){
			e.preventDefault();
			e.stopPropagation();
			var elm = $(this);
			if(elm.is(":hidden")){
				return;
			}

			var more = $("#list_more");
			var self = $(this);
			// var main_search = $("#main_search");
			// if(main_search.is(":visible")){
				if(more.is(":visible")){
					more.hide();
					elm.removeClass("up");
				}else{
					more.show();
					elm.addClass("up");
				}
			// }else{
				// more.show();
				// api.broadcast("searchListClick");
				// return;
			// }

			api.broadcast("searchResize");
		},
		showSearchArea = function(e){

			var main_search = $("#main_search");
			if(main_search.hasClass("noQueryCust")){
				return;
			}
			if(main_search.is(":hidden")){
				main_search.show();
			}else{
				main_search.hide();
			}
			api.broadcast("showSearchArea");
		},
		init = function(sandbox) {
			api = sandbox;
			searchparam = new SearchParam();

			for (var i = 0; ; i++) {
				var sel = document.getElementById("Filters_" + i);
				if (!sel) break;

				sel.onchange = changeFilter;
			}
			$("#searchList").click(search);

			$("#toggleSearchAdvList").click(showAdvSearch);
			api.listen("workspace.resourcetree:resourceTopic", listening);
			api.listen("workspace.toolkit:showSearchArea", showSearchArea);
			ready = true;
		},
		isReady = function() {
			return ready;
		};
	return {
		init: init,
		isReady:isReady
	}
},{requires:[
"../xy/script/lhgcalendar/lhgcalendar.bootstrap.css",
"../xy/script/lhgcalendar/lhgcalendar.js",
"../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css",
"../e5script/jquery/jquery-autocomplete/jquery.autocomplete.js",
"../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css",
"../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js",
"../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js",
"../xy/css/query-custom.css",
"../e5workspace/script/query-custom.js"
]});
