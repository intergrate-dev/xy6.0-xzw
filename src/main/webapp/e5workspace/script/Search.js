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
			
			searchparam.query = _getQuery();
			if (searchparam.query === false) { //验证不通过
				return;
			}
			
			if (searchparam.extType != "1") {
				searchparam.extType = searchparam.query ? 3 : searchparam.extType;
			}
			
			api.broadcast("searchTopic", searchparam);
		},
		changeFilter = function(){
			search();
			_save2Cookie(searchparam);
		}
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
			var conditions = $("#queryWord").val();
			if (conditions)
				return "SYS_TOPIC like '%" + conditions + "%'";
			
			//调用查询条件读取函数
			conditions = e5_queryform.getQuery();
			return conditions;
		},

		_reset = function(param){
			//重置查询条件
			_resetQuery(param);
			
			//取过滤器
			$("#tableFilters").hide();
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
					
					var filter = getArrayData(filterArray, index);
					if (filter) {
						$("#tableFilters").show();
						resetSelect(sel, filter, getArrayData(last, index));
					}
				}
			}});
		},
		_resetQuery = function(param) {
			var word = $("#queryWord");
			word.val("");
			
			//取定制的查询条件
			var url = (param.queryID)
				? "../e5workspace/statusbar.do?Flag=2&QueryID=" + param.queryID
				: "../e5workspace/statusbar.do?Flag=2&DocTypeID=" + param.docTypeID;
			$.ajax({url:url, async:false, success:function(data) {
				var div = $("#divQueryCust").empty().removeClass("hasQueryCust");
				$("#btnMoreArea").hide();
				if (data) {
					div.html(data).addClass("hasQueryCust");
					$("#btnMoreArea").show();
					word.parent().hide();
					$("#searchBtnArea").removeClass("hasInpt");
					
					e5_queryform.init();
					e5_queryform.reset.defKeydown(search);
					
					//$("#main_search").removeClass("noQueryCust");
					if (!$("#list_more").children().length){
						$("#btnMore").prop("disabled", true);
					} else {
						$("#btnMore").prop("disabled", false);
					}
				} else {
					word.parent().show();
					$("#searchBtnArea").addClass("hasInpt");
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
		
		advsearch = function(){
			var theURL = "../search/MainFrame.jsp";
			var target = "AdvancedSearch";
			var feature = "width=780px, height=550px, left=10, top=5,resizable=1,"
				+ 'directories=no,location=no,titlebar=no,toolbar=no,menubar=no,status=no';
			var searchWnd = window.open(theURL, target, feature, true);
			searchWnd.focus();
		},
		keysearch = function(){
			if (event.keyCode == 13) search();
		},
		showAdvSearch = function(){
			var more = $("#list_more");
			var self = $(this);
			if(more.is(":visible")){
				more.hide();
				self.removeClass("up");
			}else{
				more.show();
				self.addClass("up");
			}
			api.broadcast("resize");
		},
		init = function(sandbox) {
			api = sandbox;
			searchparam = new SearchParam();
			
			for (var i = 0; ; i++) {
				var sel = document.getElementById("Filters_" + i);
				if (!sel) break;

				sel.onchange = changeFilter;
			}
			$("#btnSearch").click(search);
			$("#queryWord").keydown(keysearch);
			$("#btnAdv").click(advsearch);
			$("#btnMore").click(showAdvSearch);
			api.listen("workspace.resourcetree:resourceTopic", listening);
			ready = true;
		},
		isReady = function() {
			return ready;
		};
	return {
		init: init,
		isReady: isReady
	}
},{requires:[
"../e5script/e5.utils.js", 
"../e5workspace/script/Param.js",

"../e5script/calendar/calendar.css",
"../e5script/calendar/usecalendar.js",
"../e5script/calendar/calendar.js",

"../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css",
"../e5script/jquery/jquery-autocomplete/jquery.autocomplete.js",

"../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css",
"../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js",
"../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js",

"../e5style/e5query-custom.css",
"../e5workspace/script/query-custom.js"

]});
