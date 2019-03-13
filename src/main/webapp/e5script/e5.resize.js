e5.mod("workspace.resize",function(){
	var api,
		handlerRisize = function(){
			api.broadcast("windowResize");
		},
		init = function(sandbox){
			api = sandbox;
			$(window).resize(handlerRisize);
		}
	return {
		init:init
	}
},{requires:["../e5script/jquery/jquery.min.js"]});