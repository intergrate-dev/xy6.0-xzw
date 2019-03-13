e5.mod("doclist.onresize",function(){
	var api,
		handlerRisize = function(){
			var winH = $(window).height();
			var doclistframe = $("#doclistframe");
			var doclistframeTop = doclistframe.offset().top;
			var h = winH - doclistframeTop > 0 ? winH - doclistframeTop : 0;
			
			doclistframe.height(h);
			$("#listing").height(doclistframe.height()-$("#tablePinHeader").height());
		},
		init = function(sandbox){
			api = sandbox;
			// api.listen("workspace.toolkit:resize",handlerRisize);
			api.listen("workspace.resize:windowResize",handlerRisize);
			api.listen("workspace.search:searchResize",handlerRisize);
			api.listen("workspace.search:showSearchArea",handlerRisize)
			api.listen("workspace.doclist:setDataFinish",handlerRisize);
		}
	return {
		init:init
	}
},{requires:["../e5script/e5.resize.js"]});