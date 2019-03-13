var _css_postfix; //避免别处引用时失败
try {_css_postfix = css_postfix;} catch (e) {_css_postfix = "";}

e5.mod("workspace.header",function() {
	var api, msgbox, msgMsgNotbox
	//点击角色切换
	changeRole = function(){
		var theURL = "../e5workspace/change.do?RoleID=" + $("#hdRoles").val();
		access(theURL, true);
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
	},
	//初始化加载角色列表
	_getRoles = function() {
		var theURL = "../e5workspace/login.do?xml=true";
		$.ajax({url:theURL, async:false, success:function(data) {
			var select = $("#hdRoles");
			var cs = data.documentElement.childNodes;
			var currole;
			for (var i = 0; i < cs.length; i++) {
				if (cs[i].tagName == "currole") {
					currole = cs[i].getAttribute("id");
				}
				else if (cs[i].tagName == "role") {
					node = $("<option value='" + cs[i].getAttribute("id") +"'>"+ cs[i].getAttribute("name") +"</option>");
					select.append(node);
				}
			}
			select.val(currole);
		}});
	},
	//点击退出按钮
	exit = function() {
		if (!confirm(headerinfo.confirm)) return;

		if (e5.utils.isIE()) {
			window.close();
		}
		else {
			window.location.replace("../login.jsp")
		}
	},
	//window.beforeunload事件：注销当前用户
	_logout = function(){
		window.onbeforeunload = "javascript:void(0);";
		
		theURL = "../e5workspace/logout.do";
		$.ajax({url:theURL, async:false});
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
	}
	//点击个性化配置
	personConfig = function(){
		var theURL = "../e5workspace/e5profile/PersonalSetting.jsp",
			target = "个性化设置";
			curCustDialog = e5.dialog({type:"iframe", value:theURL},{title:target, width:600, height:470, resizable:true,afterClose:refresh});
		curCustDialog.show();
	},
	//点击系统消息
	messageBoard = function(){
		var theURL = "../e5workspace/note/SysMessage.do";
		e5.dialog({type:"iframe", value:theURL},{title:"系统通知", width:650, height:550, resizable:true}).show();
	},
	//系统公告
	messageMsgNot = function(sandbox){
		api = sandbox;
		$.ajax({
			url: "../e5workspace/note/SysMessage.do?MsgNot=MsgNot",
			success: function (data) {
				if (data != null) {
					var docs = $.xml2json(data);
					if (docs && docs.noteID != ""){
						var	topic = "<table class='table' width='100%'><tr><td align='left' colspan='2'><a href='#' id='topic' style='color:#444444;' onclick='noteopen(" + docs.noteID + ");'>" + (docs.topic == undefined ? "" : docs.topic) + "</a></td></tr>",
						sender = "<tr><td width='50%'></td><td width='50%' align='left'>" + (docs.sender == undefined ? "" : docs.sender) + "</td></tr>",
						sendtime = "<tr><td width='50%'></td><td width='50%' align='left'>" + (docs.sendtime == undefined ? "" : docs.sendtime) + "</td></tr>",
						msgMsgNot = topic + sender + sendtime + 
									"<tr><td align='left' colspan='2'><br/><a href='#' id='hdMsg' onclick='messageBoard();'>更多…（有" + docs.length + "个未读通知）</a></td></tr>";
						msgMsgNotbox = initMsgBox({
							id:100,
							msg:msgMsgNot,
							width:300,
							height:120,
							title:"系统通知",
							autoClose:60000
						});
						if(api != null && api != undefined){
							api.listen("workspace.resize:windowResize",handlerRisizeMsgNot);
						}
					}
				}
			}
		});
	},
	remove = function(){
		msgMsgNotbox.remove();
	},
	//上次登录消息提示
	messageLastLogin = function(sandbox){
		api = sandbox;
		$.ajax({
			url: "../e5workspace/login.do?lastinfo=1",
			success: function (data) {
				if (data != null) {
					var docs = $.xml2json(data),
						lastIp = "<table><tr><td align='right'>" + headerinfo.i18n_lastIp + "</td><td>" + (docs.lastIp == undefined ? "" : docs.lastIp) + "</td></tr>",
					 	lastTime = "<tr><td align='right'>" + headerinfo.i18n_lastTime + "</td><td>" + (docs.lastTime == undefined ? "" : docs.lastTime) + "</td></tr>",
					 	nowIp = "<tr><td align='right'>" + headerinfo.i18n_nowIp + "</td><td>" + (docs.nowIp == undefined ? "" : docs.nowIp) + "</td></tr>",
					 	msg = lastIp + lastTime + nowIp;
					msgbox = initMsgBox({
						id:101,
						msg:msg,
						width:300,
						height:100,
						title:"上次登录信息",
						autoClose:5000
					});
					if(api != null && api != undefined){
						api.listen("workspace.resize:windowResize",handlerRisize);
					}
				}
			}
		});
	},
	noteopen = function(noteID){
		var theURL = "../e5workspace/note/ReadNote.do?noteID=" + noteID;
		e5.dialog({type:"iframe", value:theURL},{id : "noteDialog",title:"查看消息", width:600, height:470, resizable:true}).show();
	},
	handlerRisize = function(msg,callId,callData){
		msgbox.resizeBind();
	},
	handlerRisizeMsgNot = function(msgMsgNot,callId,callData){
		messageMsgNot.resizeBind();
	},
	handlerChangeStyle = function(msgName, callerId, callerData){
		var theURL = "ws-",
			head = $("head"),
			css = $("head link");
		css.each(function(){
			if(~this.getAttribute("href").indexOf(theURL)){
				// var self = $(this),
				// 	oldUrl = self.attr("href");
				// self.remove();
				// $("<link/>").appendTo(head).attr({"rel":"stylesheet","type":"text/css","href":oldUrl.substring(0,oldUrl.indexOf("-style")+6)+ callerData +".css"});
				$(this).attr("href",function(i,v){
					return v.substring(0,v.indexOf("-style")+6) + callerData + v.substring(v.indexOf(".css"));
				});
			};
		});
	},
	init = function(sandbox){
		api = sandbox;
		
		$("#hdConfig").click(personConfig);
		$("#hdMsg").click(messageBoard);
		$("#hdRefresh").click(refresh);
		$("#hdRelogin").click(reLogin);
		$("#hdExit").click(exit);
		$("#hdRoles").change(changeRole);

		$(document).keydown(refreshF5);
		window.onbeforeunload = _logout;
		
		//皮肤切换
		api.listen("workspace.skin:changeStyle",handlerChangeStyle)
	},
	onload = function() {
		_getRoles();
		
		window.setInterval(keepSession, 300000);		
		
		window.moveTo(0,0);
		window.resizeTo(screen.availWidth,screen.availHeight);
		api.broadcast("moveTo");

		//消息提醒
		messageMsgNot(api);
		messageLastLogin(api);
		setInterval(function(){messageMsgNot(api)},180000); //3分钟一次
	};
	return {
		init: init,
		onload: onload
	}
//});
},{requires:["../e5script/jquery/jquery.min.js", "../e5script/e5.utils.js",
             "../e5script/e5.resize.js", "../e5script/jquery/jquery.bgiframe.js", 
             "../e5script/jquery/jquery.xml2json.js",
			 "../e5workspace/script/Tips.js",
             "../e5style/ws-tips-style" + _css_postfix + ".css"]});