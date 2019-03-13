e5.mod("workspace.message",function() {
	var api, searchparam, msgbox
		init = function(sandbox) {
			api = sandbox;
			$(function(){
				$.ajax({
					url: "../e5workspace/login.do?lastinfo=1",
					success: function (data) {
						if (data != null) {
							var docs = $.xml2json(data),
								lastIp = "<table><tr><td align='right'>" + i18n_lastIp + "</td><td>" + (docs.lastIp == undefined ? "" : docs.lastIp) + "</td></tr>",
							 	lastTime = "<tr><td align='right'>" + i18n_lastTime + "</td><td>" + (docs.lastTime == undefined ? "" : docs.lastTime) + "</td></tr>",
							 	nowIp = "<tr><td align='right'>" + i18n_nowIp + "</td><td>" + (docs.nowIp == undefined ? "" : docs.nowIp) + "</td></tr>",
							 	msg = lastIp + lastTime + nowIp;
							msgbox = initMsgBox({
								id:100,
								msg:msg,
								width:300,
								height:100,
								title:"上次登录信息",
								autoClose:5000
							});
							api.listen("workspace.resize:windowResize",handlerRisize);
						}
					}
				});
			});
		},
		handlerRisize = function(msg,callId,callData){
			msgbox.resizeBind();
		};
	return {
		init: init
	}
},{requires:["../e5script/jquery/jquery.min.js", 
	"../e5script/e5.resize.js", 
	"../e5script/jquery/jquery.bgiframe.js", 
	"../e5script/jquery/jquery.xml2json.js", 
	"../e5workspace/script/Tips.js", 
	"../e5script/Function.js", 
	"../e5style/tips.css"]});
