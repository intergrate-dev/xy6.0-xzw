e5.mod("workspace.header",function() {
	var api, curTabID, statURL,
	isFF = navigator.userAgent.indexOf('Firefox') > -1,
	//切换站点
	changeSite = function(e){
		e.stopPropagation();
		e.preventDefault();

		var theURL = "changeSite.do?s=" + $("#hdSites").val();
		$.get(theURL, function(result) {
			if (result == "ok") {
				//切换站点时
				window.location.href = "Entry.do?s=" + $("#hdSites").val();
			} else {
				if (headerinfo[result])
					alert(headerinfo[result]);
				else	//error里是IP，表示在其它机器有登录
					alert(headerinfo.otheragain + ":" + result);				
			}
		});
	},
	//维持session的时钟事件
	keepSession = function() {
		var theURL = "../xy/keeplive.do";
		$.get(theURL, function(result) {
			showError(result);
		});
	},
	showError = function(result) {
		if (result != "ok") {
			if (headerinfo[result])
				alert(headerinfo[result]);
			else	//error里是IP，表示在其它机器有登录
				alert(headerinfo.otheragain + ":" + result);
			window.location.href = "../login.jsp";
		}
	},

	//点击退出按钮
	exit = function() {
		if (!confirm(headerinfo.confirm)) return;
		_logout();
		if (navigator.appName.indexOf("Microsoft")!= -1) {
			window.close();
		}
		else {
			window.location.replace("../login.jsp");
		}
	},
	//window.beforeunload事件：注销当前用户
	_logout = function(){
		window.onbeforeunload = "javascript:void(0);";

		theURL = "../e5workspace/logout.do";
		$.ajax({url:theURL, async:false});
	},
	//按F5的响应
	refreshF5 = function(event) {
		if (event.keyCode == 116) { //F5
			window.onbeforeunload = "javascript:void(0);";
		}
	},

	tabShow = function(e){
		e.preventDefault();
		e.stopPropagation();

		//添加选中的样式
		$(this).parent().siblings().removeClass("selectTab");
		$(this).parent().addClass("selectTab");
		$("#TabATip").hide();

		var src = $(e.target);
		var id = src.attr('tabID');

		//$("#subtab-" + curTabID).hide();
		$("ul[id^=subtab]").hide();
		$("#subtab-" + id).show();
		curTabID = id;

		//默认打开第一个
		/*var lis = $("#subtab-" + id).find("li a");
		var li = lis.first();
		li.click();*/
		//加cookie记录
		var userLoginName= headerinfo.userLoginName;

		xy_cookie.setCookie("mainTab_" + userLoginName + "_" + headerinfo.siteID, id);

		//隐藏 tab menu
		$("div[id^=smartMenu_]").hide();

	},
	subTabShow = function(e){
		e.preventDefault();
		e.stopPropagation();

		var src = $(e.target);
		var theURL = src.attr('href');
		if (!theURL) {
			src = src.parent(); //找到<a>
			theURL = src.attr('href');
		}
		if (!theURL) return;

		$("#nav .dropdown-menu>li").removeClass("select");
		src.parent().addClass("select"); //在li上加class

		theURL = "../" + theURL + (theURL.indexOf('?') > 0 ? "&t=" : "?t=") + src.attr("tabID")
				+ "&siteID=" + headerinfo.siteID;
		// 解决火狐浏览器下切换tab时echarts不显示问题
		addStatUrl(src.attr("id") + "TabA");

		//添加tab
		addTab(src, theURL);

		//隐藏 tab menu
		$("div[id^=smartMenu_]").hide();
	},
	//在entry.jsp 中添加tab
	addTab = function(subTab, theURL){
		//取tabid+TabA作为新tab的id
		var tabName = subTab.text();
		var tabid = subTab.attr("tabid")+"TabA";
		var frameId = subTab.attr("tabid")+"Frm";
		//如果没有tab，就添加一个tab
		var needResize = false;
		if (!$("#" + tabid).length > 0){
			needResize = true;
			 _tabid = subTab.attr("tabid");
			addNewTabExt( _tabid, tabName, theURL);
		}else if($("#" + tabid).length > 0 && $("#" + frameId).children() && !$("#" + frameId).children().attr("src")){
			//解决top按钮存在 点击左侧按钮不刷新的问题
            $("#" + frameId).children().attr("src", $("#" + frameId).children().attr("_src"));
		}else{
			$("#TabATip").hide();
		}
		//tab高亮
		$("#"+tabid).tab('show');
		updateCurTabCookie();
		//设置iframe大小
		if (needResize) {
			changeFrameHeight(frameId);
		}
	},
	//更新cookie
	updateCurTabCookie = function(){
		//用curTab+用户登录id作为key
		var userLoginName= headerinfo.userLoginName;
		//如果当前没有tab页，清除相关cookie
		if( $("#tabContentDiv div:visible").attr("id")==null){
			xy_cookie.setCookie("curTab_" + userLoginName + "_" + headerinfo.siteID, "");
			xy_cookie.setCookie("curTabs_"  + userLoginName + "_" + headerinfo.siteID, "");
			return false;
		}
		//当前展示 tab
		var curTabId = $("#tabContentDiv div:visible").attr("id").replace("Frm","");
		var curTabs = new Array();
		//获取跟标签相关的信息
		for(var i = 0, size = $("#tabContentDiv div").length ; i < size; i++){
			_tabId = $("#tabContentDiv div").eq(i).attr("id").replace("Frm","");
			curTabs.push( _tabId);
		}
		var curTabsIds = curTabs.join(",");
		//放到cookies中
		xy_cookie.setCookie("curTab_" + userLoginName + "_" + headerinfo.siteID, curTabId);
		xy_cookie.setCookie("curTabs_"  + userLoginName + "_" + headerinfo.siteID, curTabsIds);
	},
	//初始化tab
	initTabs= function(){
		var userLoginName= headerinfo.userLoginName;
		//点击一下用户最后一次点击的主模块,以出现副模块
		//获得cookie
		var curTab=xy_cookie.getCookie("curTab_" + userLoginName + "_" + headerinfo.siteID);
		var curTabs=xy_cookie.getCookie("curTabs_" + userLoginName + "_" + headerinfo.siteID);
		//获得侧列表
		var mainTab=xy_cookie.getCookie("mainTab_" + userLoginName + "_" + headerinfo.siteID);
		$("#"+mainTab+"mainTab").parent().addClass("selectTab");

		//如果没有相关cookies，默认初始化主模块第一个副栏目
		if(curTab==null) return false;
		if(curTabs==null) return false;
		var tabsArr = curTabs.split(",");
		for (var i = 0 , size = tabsArr.length ; i < size ; i++){
			var _tabId = tabsArr[i];
			var tabName = $("#subtab_a_" + _tabId+" span").text();
			var theURL = $("#subtab_a_" + _tabId).attr("href");
			if (!theURL) continue;
			
			theURL = "../" + theURL + (theURL.indexOf('?') > 0 ? "&t=" : "?t=") + _tabId
			+ "&siteID=" + headerinfo.siteID;
			// 解决火狐浏览器下切换tab时echarts不显示问题
			if(isFF && _tabId == 'mystat'){
				statURL = theURL;
				theURL = '';
			}
			//添加标签与内容
			addNewTab( _tabId, tabName, theURL);

			if (i == 0)
				$("#" + _tabId + "TabA").tab('show');
			//修改frame高度
			changeFrameHeight(_tabId + "Frm");
		}
		//显示当前页
		$("#"+curTab+"TabA").click();

		//添加list浮动效果
		$("#tabTitleUL").sortable({axis: 'x',containment: 'parent', items:'li'  });

		return true;
	},
	//添加新的标签页
	addNewTab = function( _tabid, _tabName, theURL){
		var tabName = _tabName;
		var tabid = _tabid+"TabA";
		var frameId = _tabid+"Frm";

		//添加一个li
		var tabTemplate = '<li><a href="#{frameTab}" id="#{tabid}" ch="0">#{showname}<span class="ui-icon ui-icon-close" ></span></a></li> ';
		$("#tabTitleUL").append(tabTemplate.replace( /#\{frameTab\}/g , "#"+frameId ).replace( /#\{showname\}/g , tabName ).replace( /#\{tabid\}/g , tabid ));
		//添加一个div
		var tabContentHtml = '<div class="tab-pane" id="#{frameTab}"><iframe showname="#{showname}" class="tabFrame" _src="#{frmURL}"></iframe></div>';
		$("#tabContentDiv").append(tabContentHtml.replace( /#\{frameTab\}/g , frameId ).replace( /#\{frmURL\}/g , theURL ).replace( /#\{showname\}/g , $.trim(tabName) ));

		//给这个tab添加一个点击事件-直接调用相应subtab的点击事件
		$("#"+tabid).click(function(){
			//懒加载
			var _frmID = $(this).attr("href");
			var _$frame = $(_frmID).children('iframe');
			var _frmSrc = _$frame.attr("src");

			if(!_frmSrc || $.trim(_frmSrc) == ""){
				_$frame.attr("src", _$frame.attr("_src"));
			}

			//关闭提示
			$("#TabATip").hide();
			$(this).tab('show');
			//获得ul的id
			var _aid = $(this).attr("id").replace("TabA","");

			//从li中获得侧边ul的id
			var _pid = "#subtab-"+ $("#subtab_a_" + _aid).attr("pid");
			//隐藏所以的ul
			$("ul[id^=subtab-]").hide();
			//显示目标ul
			$(_pid).show();
			//去掉选择的class
			$(_pid).find("li").removeClass("select");
			//给目标侧栏目添加class
			$("#subtab_a_" + _aid).parent().addClass("select");

			//给maintab添加一个选中的样式
			var _maintabId = "#"+$("#subtab_a_" + _aid).attr("pid")+"mainTab";
			$(_maintabId).parent().siblings().removeClass("selectTab");
			$(_maintabId).parent().addClass("selectTab");

			updateCurTabCookie();
			// 解决火狐浏览器下切换tab时echarts不显示问题
			addStatUrl(tabid);
			
			$("div[id^=smartMenu_]").hide();
		});

		//当点击tab中的x时触发。
		$("#"+tabid).find("span").click(function(){
			//关闭提示
			$("#TabATip").hide();
			//如果只剩下一个标签，就不关闭
			if($("#tabTitleUL li").size()==1){
				return;
			}
			//判断所去掉的tab是否正在显示。如果是，显示这个tab前一个tab的内容
			if(!$("#"+frameId).is(":hidden")){
				var _ind = ($("#tabTitleUL a").index($("#"+tabid))-1) >=0 ? ($("#tabTitleUL a").index($("#"+tabid))-1) : 1;
				// 解决火狐浏览器下切换tab时echarts不显示问题
				addStatUrl($("#tabTitleUL a").eq( _ind ).attr("id"));

				$("#tabTitleUL a").eq( _ind ).click();
			}

			//去掉tab和相应的div
			$("#"+tabid).parent().remove();
			$("#"+frameId).remove();

			//如果当前没有显示的div 显示最后一个div
			if($("#tabContentDiv").find("div:visible").length==0){
				$("#tabTitleUL").find("a:last").click();
			}
			updateCurTabCookie();
		});

		//tab鼠标右键的菜单
		var tabMenuData = [
			[{
		        text: "重新加载",
		        func: function() {
		        	var _frmId = $(this).attr("href");
		        	$(_frmId).find("iframe").attr("src", $(_frmId).find("iframe").attr("src"));
		        }
		    }],
		    [
		    /**/{
		        text: "关闭标签",
		        func: function() {
		        	$(this).find("span").click();
		        	$("#TabATip").hide();
		        }
		    },
		    {
		        text: "关闭其他标签",
		        func: function() {
					$("#tabTitleUL li").find("a").not(this).find("span").each(function(){
						$(this).click();
					});
					$("#TabATip").hide();
		        }
		    }
		    /*,{
		        text: "关闭左侧标签",
		        func: function() {
					$(this).parent().prevAll().find("a span").each(function(){
						$(this).click();
					});
		        }
		    },
		    {
		        text: "关闭右侧标签",
		        func: function() {
					$(this).parent().nextAll().find("a span").each(function(){
						$(this).click();
					});
		        }
		    }*/
		    ]
		];
		$("#"+tabid).smartMenu(tabMenuData, {
	    	name: tabid
		});

       /* var _ulWidth = $("#tabTitleUL").width();
        var _liWidth = 0;
        var _liSize = $("#tabTitleUL").find("li").size();
        $("#tabTitleUL").find("li").each(function(){
            _liWidth += $(this).width();
        });

        if(_liWidth > _ulWidth){
            //var _averWidth = _ulWidth/_liSize;
            //$("#tabTitleUL").find("li").each(function(){
            //    $(this).width( _averWidth-5);
            //});
        }*/
	},
	addStatUrl = function(tabid) {
		if (!isFF) return;
		
		var statID = 'mystat';
		if (tabid == statID + 'TabA') {
			var frame = $("#" + statID + "Frm iframe");
			if (frame.attr('src') == ''){
				frame.attr("src", statURL);
			}
		}
	},
	//添加新的标签页,用于打开前判断标签页是否超出界面
	addNewTabExt = function( _tabid, _tabName, theURL){
		$("#TabATip").click(function() {
			$(this).hide();
		});
		var tabName = _tabName;
		var tabid = _tabid+"TabA";
		var frameId = _tabid+"Frm";

		//添加一个li
		var tabTemplate = '<li><a href="#{frameTab}" id="#{tabid}" ch="0">#{showname}<span class="ui-icon ui-icon-close" ></span></a></li> ';
		$("#tabTitleUL").append(tabTemplate.replace( /#\{frameTab\}/g , "#"+frameId ).replace( /#\{showname\}/g , tabName ).replace( /#\{tabid\}/g , tabid ));
		//判断标签页是否超出界面
		$("#TabATip").click(function() {
			$(this).hide();
		});
		var navTabWith = parseInt($(".nav_tab").find("ul:first li:first").css("width").replace("px",""));
		var tabTitleWiths = 0;
		$("#tabTitleUL").find("li").each(function(){
			tabTitleWiths += parseInt($(this).css("width").replace("px",""));
		})
		_newTabWidth = parseInt($("#"+_tabid+"TabA").parent().css("width").replace("px",""));
		if($(window).width()-navTabWith-tabTitleWiths < _newTabWidth + 20){
			$("#"+tabid).parent().remove();
			$("#TabATip").show();
			$("#TabATip").css("left",event.clientX+10);
			$("#TabATip").css("top",event.clientY+5); 
			$("#TabATip").css("position","absolute"); 
			return;
		}
		//添加一个div
		var tabContentHtml = '<div class="tab-pane" id="#{frameTab}"><iframe showname="#{showname}" class="tabFrame" src="#{frmURL}"></iframe></div>';
		$("#tabContentDiv").append(tabContentHtml.replace( /#\{frameTab\}/g , frameId ).replace( /#\{frmURL\}/g , theURL ).replace( /#\{showname\}/g , $.trim(tabName) ));
		
		//给这个tab添加一个点击事件-直接调用相应subtab的点击事件
		$("#"+tabid).click(function(){
			$(this).tab('show');
			//获得ul的id
			var _aid = $(this).attr("id").replace("TabA","");

			//从li中获得侧边ul的id
			var _pid = "#subtab-"+ $("#subtab_a_" + _aid).attr("pid");
			//隐藏所以的ul
			$("ul[id^=subtab-]").hide();
			//显示目标ul
			$(_pid).show();
			//去掉选择的class
			$(_pid).find("li").removeClass("select");
			//给目标侧栏目添加class
			$("#subtab_a_" + _aid).parent().addClass("select");

			//给maintab添加一个选中的样式
			var _maintabId = "#"+$("#subtab_a_" + _aid).attr("pid")+"mainTab";
			$(_maintabId).parent().siblings().removeClass("selectTab");
			$(_maintabId).parent().addClass("selectTab");

			updateCurTabCookie();
			// 解决火狐浏览器下切换tab时echarts不显示问题
			addStatUrl(tabid);
			
			$("div[id^=smartMenu_]").hide();
		});

		//当点击tab中的x时触发。
		$("#"+tabid).find("span").click(function(){
			//如果只剩下一个标签，就不关闭
			if($("#tabTitleUL li").size()==1){
				return;
			}
			//判断所去掉的tab是否正在显示。如果是，显示这个tab前一个tab的内容
			if(!$("#"+frameId).is(":hidden")){
				var _ind = ($("#tabTitleUL a").index($("#"+tabid))-1) >=0 ? ($("#tabTitleUL a").index($("#"+tabid))-1) : 1;
				// 解决火狐浏览器下切换tab时echarts不显示问题
				addStatUrl($("#tabTitleUL a").eq( _ind ).attr("id"));
				
				$("#tabTitleUL a").eq( _ind ).click();
			}

			//去掉tab和相应的div
			$("#"+tabid).parent().remove();
			$("#"+frameId).remove();

			//如果当前没有显示的div 显示最后一个div
			if($("#tabContentDiv").find("div:visible").length==0){
				$("#tabTitleUL").find("a:last").click();
			}
			updateCurTabCookie();
		});

		//tab鼠标右键的菜单
		var tabMenuData = [
			[{
		        text: "重新加载",
		        func: function() {
		        	var _frmId = $(this).attr("href");
		        	$(_frmId).find("iframe").attr("src", $(_frmId).find("iframe").attr("src"));
		        }
		    }],
		    [
		    /**/{
		        text: "关闭标签",
		        func: function() {
		        	$(this).find("span").click();
		        }
		    },
		    {
		        text: "关闭其他标签",
		        func: function() {
					$("#tabTitleUL li").find("a").not(this).find("span").each(function(){
						$(this).click();
					});
		        }
		    }
		    /*,{
		        text: "关闭左侧标签",
		        func: function() {
					$(this).parent().prevAll().find("a span").each(function(){
						$(this).click();
					});
		        }
		    },
		    {
		        text: "关闭右侧标签",
		        func: function() {
					$(this).parent().nextAll().find("a span").each(function(){
						$(this).click();
					});
		        }
		    }*/
		    ]
		];
		$("#"+tabid).smartMenu(tabMenuData, {
	    	name: tabid
		});
	},

	//找出当前frame的合适高度
	getFrameHeight = function() {
		var top = 0;
		var panes = $.find(".tab-pane");
		for (var i = 0; i < panes.length; i++) {
			var pane = $(panes[i]);
			if (pane.hasClass("active")) {
				top = pane.offset().top;
			}
		};
		if (top == 0) return 0;

		var winH = $(window).height();
		var height = winH - top;
		return height;
	},

	//改变iframe高度
	changeFrameHeight = function(frameId) {
		var height = getFrameHeight();
		var frm = $("#" + frameId).find("iframe");
		$(frm).height(height - 10);
	},
	//改变所有iframe高度，浏览器窗口大小改变时被触发
	changeAllHeight = function() {
		var height = getFrameHeight();
		if (height == 0) return;

		var panes = $.find(".tab-pane");
		for (var i = 0; i < panes.length; i++) {
			var pane = $(panes[i]);
			//pane.height(height);

			var frm = pane.find("iframe");
			$(frm).height(height - 10);
		}
	},
	init = function(sandbox){
		api = sandbox;
		$("#hdExit").click(exit);
		$("#hdSites").val(headerinfo.siteID);
		$("#hdSites").change(changeSite);

		$("#nav .main-tab a").click(tabShow);
		$("#nav .sub-tab a").click(subTabShow);

		api.listen("workspace.resize:windowResize",changeAllHeight);
		$(document).keydown(refreshF5);
		// 解决sub-tab显示不全的问题
		$('#nav .dropdown-menu').css('max-height',$(window).height()-60);

	},
	onload = function() {
		window.setInterval(keepSession, 300000);

		window.moveTo(0,0);
		window.resizeTo(screen.availWidth,screen.availHeight);
		//如果没有拿到cookies，触发主栏目第一个栏目的点击事件
		var b = initTabs();
		if(!b || $("#tabTitleUL").children().size()==0){
			//获得主菜单的对象
			var _mainTabA = $("#nav .main-tab a");
			//点击第一个
			_mainTabA.first().click();
			var id = _mainTabA.attr('tabID');
			$("#subtab-" + id).find("li a").first().click();
		}

		$("#hdSites").val(headerinfo.siteID);

		$("#closeTabBtn").click(function(){
			$("#tabTitleUL li").not(".active").find("a span").each(function(){
				$(this).click();
			});
		});
	};
	return {
		init: init,
		onload: onload
	};
});