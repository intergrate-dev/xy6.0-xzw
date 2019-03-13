//Statusbar.js扩展部分
e5.mod("workspace.doclistMain",function() {
	var api,_super, ready = false;
	var _superRefreshPage;
	//双击细览
	var refreshDocView = function(id, libid) {
		//var url = _super.pathPrefix + "../amuc/View.do?DocIDs=" + id + "&DocLibID=" + libid;
		var url = "../amuc/View.do?DocIDs=" + id + "&DocLibID=" + libid;

		var winHeight = screen.availHeight - 70;
		var winWidth = screen.availWidth;
		if (winWidth > 1070) winWidth = 1070;
		var winLeft = (screen.availWidth - winWidth) / 2;
		var winTop = 0;

		var feature = "scrollbars=yes,status=no,toolbar=no,location=no,menubar=no,resizable=1"
			+ ",width=" + winWidth + ",height=" + winHeight
			+ ",left=" + winLeft + ",top=" + winTop;
		if(libid == 9){  //活动管理界面，详情页面在自身页面打开。（推广、报名、签到、反馈管理）
			var op = getUrlVars("op");
			var actID = getUrlVars("DocIDs");
			if(op=="apply"){
				//var docViewWnd = window.open(_super.pathPrefix + "../amuc/action/getAction.do?action=actionForm&DocIDs="+actID+"&acmID="+id,"_self", feature);
				var docViewWnd = window.open("../amuc/action/getAction.do?action=actionForm&DocIDs="+actID+"&acmID="+id,"_self", feature);
			}else{
				var docViewWnd = window.open(url,"_self", feature);
			}
			
		}else{
			var docViewWnd = window.open(url,"_blank", feature);
		}
		docViewWnd.focus();
	}
	//获取url地址中的参数
	var getUrlVars = function(name){
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	    if (r != null) return unescape(r[2]); return null; //返回参数值
	}
	//列头排序时，加上SYS_DOCUMENTID
	var getOrderBy = function(statusparam) {
		var orderBy = (typeof statusparam == "undefined") ? _super.statusparam["orderBy"] : statusparam["orderBy"];
		if (orderBy && orderBy != "SYS_DOCUMENTID") {
			orderBy += ",SYS_DOCUMENTID";
		}
		return orderBy;
	}
	var getOrderType = function(statusparam) {
		var orderType = (typeof statusparam == "undefined") ? _super.statusparam["orderType"] : statusparam["orderType"];
		
		var orderBy = (typeof statusparam == "undefined") ? _super.statusparam["orderBy"] : statusparam["orderBy"];
		if (orderBy && orderBy != "SYS_DOCUMENTID") {
			orderType += ",1";
		}
		return orderType;
	}
	
	var isReady = function() {
		return ready;
	}
	var refreshPage = function() {
		_superRefreshPage();
		
		//增加活动客户信息
		var divSum = $("#divSum");
		if (divSum.length == 0) return;
		
		var statusparam = _super.statusparam;
		if (statusparam.listPage == 0) return;
		
		if(statusparam.ruleFormula.indexOf("maActionID_EQ_") == 0){
			//var theURL = _super.pathPrefix + "../amuc/action/ActionMember.do?a=listInfo&DocLibID=" + statusparam.docLibID
			var theURL = "../amuc/action/ActionMember.do?a=listInfo&DocLibID=" + statusparam.docLibID
			+ "&FilterID=" + statusparam.filterID
			+ "&RuleFormula=" + e5.utils.encodeSpecialCode(statusparam.ruleFormula)
			+ "&Query=" + e5.utils.encodeSpecialCode(statusparam.query)
			;
			$.ajax({
				url:theURL,
				async:true,
				dataType:"json",
				success: function(data) {
					if(!data||typeof Object.prototype.toString.call(data) == "[object Array]"||!data.length)return;
					divSum.empty();
					var l = data.length;
					var j;
					var i = $("<i class='icon-arrow-right'></i>").appendTo(divSum).click(function(){
						if(list.is(":hidden")){
							i.removeClass().addClass("icon-arrow-right");
							list.show();
						}else{
							i.removeClass().addClass("icon-arrow-left");
							list.hide();
						}
					});
					for (var j = 0; j < l; j++) {
						divSum.append(data[j].key + ":" + data[j].value + "&nbsp;&nbsp;");
					}
				}
			});
		}
		
	}
	var replaceFunc = function() {
		_super = e5.mods["workspace.doclist"].self;
		if (!_super) {
			setTimeout(replaceFunc, 100);
			return;
		}
		//在页面里定义的路径深度
		//_super.pathPrefix = pathPrefix;
		//改变双击细览
		_super.refreshDocView = refreshDocView;
		
		//列头排序时，加上SYS_DOCUMENTID
		_super.getOrderBy = getOrderBy; 
		_super.getOrderType = getOrderType;
		//刷新列表：增加活动客户信息读取
		_superRefreshPage = _super.refreshPage;
		_super.refreshPage = refreshPage;
		ready = true;
	}
	var init = function(sandbox){
		api = sandbox;
		
		replaceFunc();
	}
	return {
		init : init,
		isReady : isReady
	}
});