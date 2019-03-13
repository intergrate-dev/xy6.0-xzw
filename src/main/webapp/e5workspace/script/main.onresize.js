e5.mod("main.onresize",function(){
	var api,
		handlerRisize = function(msg,callId,callData){
			var body = $(window),
				bodyW = body.width(),
				bodyH = body.height(),
				warpMain = $("#warpMain"),
				main = $("#main"),
				main_header = $("#main_header"),
				fall = $("#main_header").height(),
				scrollX = false,
				scrollY = false,
				x = main.width();
			if(bodyH > 570){
				main.height(bodyH-fall);
				scrollY = false;
			}else{
				main.height(570-fall);
				scrollY = true;
			}
			if(bodyW > 760){
				main.css('width','auto');
				main_header.css('width','auto');
				scrollX = false;
			}else{
				main.css('width','760px');
				main_header.css('width','760px');
				scrollX = true;
			}
			if(scrollX||scrollY){
				if(warpMain.css('overflow')!== 'auto'){
					warpMain.css('overflow','auto');
				}
			}else{
				if(warpMain.css('overflow')!== 'hidden'){
					warpMain.css('overflow','hidden');
				}	
			}
			api.broadcast("resize");
		},
		init = function(sandbox){
			api = sandbox;
			api.listen("workspace.resize:windowResize",handlerRisize);
			api.listen("workspace.header:moveTo",handlerRisize);
		}
	return {
		init:init
	}
},{requires:["../e5script/e5.resize.js"]});