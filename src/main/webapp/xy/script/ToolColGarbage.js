//已删除栏目操作栏
e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, 
		//订阅响应
		listening = function(msgName, callerId, param) {
			for (var name in param){
				toolbarparam[name] = param[name];
			}
			
			if (toolbarparam.docLibIDs){
				toolbarparam.docLibID = toolbarparam.docLibIDs;
			}
			toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
			toolbarparam.docIDs = dealDocIDs(toolbarparam.docIDs);
			
			toolbar(toolbarparam);
		},
		toolbar = function(param){
			//只选了一条记录时显示操作
			if (param.docIDs && param.docIDs.split(",").length == 1) {
				$("#toolTR").show();
			} else {
				$("#toolTR").hide();
			}
		},
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){srcButton.click(event.data, process)}, 500);
			
			var opid = srcButton.attr("opid");
			var opurl = null;
			if (opid == "0") {
				if (!confirm("确定要恢复该栏目吗？子栏目将被一起恢复。")) return;
				opurl = "../xy/column/Restore.do?colID=" + toolbarparam.docIDs;
			} else if (opid == "1") {
				if (!confirm("确定要标为僵尸栏目吗？子栏目将被一起被标记。栏目树中将不再显示。")) return;
				opurl = "../xy/column/Delete.do?type=1&colID=" + toolbarparam.docIDs;
			} else if (opid == "2") {
				opurl = "../e5workspace/manoeuvre/FlowRecordList.do?DocLibID=" + toolbarparam.docLibID
						+ "&DocIDs=" + toolbarparam.docIDs;
				var iTop = (window.screen.availHeight-30-600)/2; //获得窗口的垂直位置;
				var iLeft = (window.screen.availWidth-10-800)/2; //获得窗口的水平位置;
				
				var feature = "height=600, width=800,left="+iLeft+", top="+iTop
					+ ",toolbar=no,location=no,z-look=yes,alwaysRaised=yes";
				window.open(opurl, "_blank", feature);
				return;
			}
			
			//无窗口
			$.get(opurl, function(data){
				if (data == "ok") {
					var frame = parent.frames["frmColLeft"];
					if (opid != "0") {
						frame.col_tree.remove(toolbarparam.docIDs);
					} else {
						alert("栏目已恢复，刷新栏目树后可查看");
					}
					api.broadcast("refreshTopic", "true");
				} else {
					alert(data);
				}
			});
		},
		//若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if (!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
				docLibID = libArr[0];

			for (var i = 1; i < libArr.length; i++){
				if (libArr[i] && libArr[i] != docLibID) {
					return dealDocIDs(docLibIDs);
				}
			}
			
			return docLibID;
		},
		//处理文档ID串，若是以逗号结尾，则去掉逗号
		dealDocIDs = function(docIDs) {
			if (docIDs && docIDs.charAt(docIDs.length - 1) == ',')
				return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		};
	//-----init & onload--------
	var init = function(sandbox){
		api = sandbox;
		$(".toolButton").click(process);
		
		toolbarparam = new ToolkitParam();
		api.listen("workspace.doclist:doclistTopic", listening);
	};
	return {
		init: init
	}
});