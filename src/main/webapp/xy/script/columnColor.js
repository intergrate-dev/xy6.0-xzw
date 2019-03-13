/**
 * 发布库，若是关联栏目的稿件，则字体颜色变化
 */
e5.mod("article.columnColor", function() {
	var api;
	var oldOrder ="";
	var listening = function() {
		if($("#listing table tr").length){
			$("#listing table tr").each(function(e){
				var hiddenColSpan = $(this).find("td .hiddenColID");
				var colID = hiddenColSpan.attr("colID");
				if (!colID) return;
				
				if (main_param.colID != colID) {
					$(this).addClass("relatedColumn");
				}
			});
		}else{
			var spans = $("#frmRight").contents().find('table tr td .hiddenColID');
			var currentID = window.frames["frmColumn"].col_param.colID;
			spans.each(function(e){
				var colID = $(this).attr("colid");
				if (!colID) return;
				
				if (currentID != colID) {
					$(this).parent().parent().parent().addClass("relatedColumn td");
				}
			});
		}
	};
	var init = function(sandbox) {
		api = sandbox;
		api.listen("workspace.doclist:setDataFinish", listening);
	};
	return {
		init : init
	};
});