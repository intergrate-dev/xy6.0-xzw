e5.mod("workspace.header",function() {
	var api, mouseY = 0,
	//点击角色切换
	changeRole = function(e){
		var elm = $(e.target).closest("li[role]"),theURL;
		if(elm.length&&!elm.hasClass("active")){
			theURL = "../e5workspace/change.do?RoleID=" + elm.attr("role");
			access(theURL, true);
		}
		e.stopPropagation();
		e.preventDefault();
	},
	//维持session的时钟事件
	keepSession = function() {
		var theURL = "../e5workspace/keeplive.do";
		access(theURL, false);
	},
	//后台url访问方法
	access = function(theURL, needRefresh){
		$.get(theURL, function(xmlDoc) {
			var result = parseXML(xmlDoc);
			if (!result.success) {
				if (headerinfo[result.error])
					alert(headerinfo[result.error]);
				else	//error里是IP，表示在其它机器有登录
					alert(headerinfo.otheragain + ":" + result.error);
				reLogin();
			} else if (needRefresh)
				refresh();
		}, "xml");
	},
	//后台返回数据解析
	parseXML = function(xmlDoc) {
		var result = {
			success : "",
			error : ""
		};
		if (!xmlDoc || !xmlDoc.documentElement)
			return result;
		var children = xmlDoc.documentElement.childNodes;
		//xml 要么是success=1
		//要么是error=novaliduser/nouser/novalidrole/invalidrole/haserror/adminaskquit/某一个IP
		result[children[0].tagName] = getText(children[0]);
		return result;
	},
	getText = function(oNode){
		return (oNode.text) ? oNode.text : oNode.textContent;
		return (oNode.text) ? oNode.text : oNode.textContent;
		return (oNode.text) ? oNode.text : oNode.textContent;
		return (oNode.text) ? oNode.text : oNode.textContent;
	},
	//初始化加载角色列表
	/*_getRoles = function() {
		var theURL = "../e5workspace/login.do?xml=true";
		$.ajax({url:theURL, async:false, success:function(data) {
			var select = $("#hdRoles");
			var cs = data.documentElement.childNodes;
			var curRoleId,curRoleName;
			for (var i = 0; i < cs.length; i++) {
				if (cs[i].tagName == "currole") {
					curRoleId = cs[i].getAttribute("id");
				}
				else if (cs[i].tagName == "role") {
					node = $("<li role='" + cs[i].getAttribute("id") +"'><a href='#'>"+ cs[i].getAttribute("name") +"</a></li>");
					select.append(node);
				}
			}
			curRoleName = select.find("[role="+curRoleId+"]").addClass("active").find("a").html();
			select.prev().find(".text").html(curRoleName);
		}});
	},*/
	//点击退出按钮
	exit = function() {
		if (!confirm(headerinfo.confirm)) return;
		if (e5.utils.isIE()) {
			window.close();
		}
		else {
			window.location.replace("../login.jsp");
		}
	},
	//window.beforeunload事件：注销当前用户
	_logout = function(){
		if (mouseY <= 0){
			window.onbeforeunload = "javascript:void(0);";
			
			theURL = "../e5workspace/logout.do";
			$.ajax({url:theURL, async:false});
		} else {
			window.onbeforeunload = "javascript:void(0);";
		}
	},
	//点击刷新按钮
	refresh = function() {
		window.onbeforeunload = "javascript:void(0);";
		window.location.reload();
	},
	//点击重新登录
	reLogin = function() {
		try{
			window.location.href="../login.jsp";
		}catch (e){}
	},
	//按F5的响应
	refreshF5 = function(event) {
		if (event.keyCode == 116) { //F5
			window.onbeforeunload = "javascript:void(0);";
		}
	},
	//鼠标移出（点叉关闭）时记录鼠标位置，从而可以在页面退出时判断是点了叉还是右键刷新
	mouseOut = function(e){
		mouseY = e.pageY;
	},
	skip = function(e){
		e.preventDefault();
		e.stopPropagation();
		var src = $(e.target);
		window.onbeforeunload = "javascript:void(0);";
		
		var theURL = src.attr("href");		
		var seperate = src.attr("seperate");
		if(seperate=="true"){
			window.open (theURL) ;
		}
		else{
			window.location.href = theURL;
		}
	},
	changeClass = function(){
		var w = window.document.body.offsetWidth;
		if(w<1100){
			$("body").addClass("w1024");
		}else{
			$("body").removeClass("w1024");
		}
	},
	init = function(sandbox){
		api = sandbox;
		$("#hdRefresh").click(refresh);
		$("#hdRelogin").click(reLogin);
		$("#hdExit").click(exit);
		$("#hdRoles").click(changeRole);
		$("#nav .dropdown-menu a").click(skip);
		api.listen("workspace.resize:windowResize",changeClass)
		$(document).keydown(refreshF5);
		//$(document).bind("mouseout", mouseOut);//频繁触发，效果不高
		//window.onbeforeunload = _logout;//允许打开多个主列表窗口，关闭任意一个都不退出，关闭所有窗口后15分钟自动退出
	},
	onload = function() {
		$("#breadcrumb").hide();
		$("#header").hide();
		//_getRoles();
		window.setInterval(keepSession, 300000);		
		
		window.moveTo(0,0);
		window.resizeTo(screen.availWidth,screen.availHeight);
		changeClass();
	};
	return {
		init: init,
		onload: onload
	};
},{requires:["../e5script/e5.utils.js"
]});
