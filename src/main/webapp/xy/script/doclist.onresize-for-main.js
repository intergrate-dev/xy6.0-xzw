e5.mod("doclist.onresize",function(){
	var api,resizing = false,
		handlerRisize = function(){
			var winH = $(window).height();
			var doclistframe = $("#doclistframe");
			var doclistframeTop = doclistframe.offset().top;
			
			var h = winH-doclistframeTop>0 ? winH-doclistframeTop : 0;
			doclistframe.height(h - 7); //多去掉5，否则IE下看不到下边
			
			$("#listing").height(doclistframe.height()-$("#tablePinHeader").height());

			if((!$("#column_coll_id").is(":visible"))){
				$("#rs_tree").height(winH - 57);
			} else{
				$("#rs_tree").height(winH - 116);
			}
		},
		init = function(sandbox){
			api = sandbox;
			api.listen("workspace.resize:windowResize",handlerRisize);
			api.listen("workspace.search:searchResize",handlerRisize);
			api.listen("workspace.doclist:setDataFinish",handlerRisize);
			api.listen("workspace.search:showSearchArea",handlerRisize);
			
			handlerRisize();
		}
	return {
		init:init
	}
},{requires:["../e5script/e5.resize.js"]});