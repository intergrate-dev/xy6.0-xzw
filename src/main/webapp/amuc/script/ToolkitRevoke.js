//撤稿中心操作栏
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
			
			toolbar(toolbarparam);
		},
		toolbar = function(param){
			if (param.docIDs) {
				$("#toolTR").show();
			} else {
				$("#toolTR").hide();
			}
		},

		revokeOpen = function(event){
		var srcButton = $(this);
		srcButton.unbind("click");
		setTimeout(function(){srcButton.click(event.data, process)}, 500);
		var iWidth=450; //弹出窗口的宽度;
		var iHeight=350; //弹出窗口的高度;
        var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		window.open('./BatchRevokeArt.jsp', "BatchRevokeArt", "height="+iHeight+", width="+iWidth+",left="+iLeft+", top="+iTop+",toolbar=no,location=no,z-look=yes,alwaysRaised=yes");
	}

	revokeCallback = function (param) {
        if (param.DocIDs !== "" && param.DocIDs !== undefined){
            opurl = "../xy/article/Revoke.do"
                + "?DocLibID=" + toolbarparam.docLibID;
            $.post(opurl, {
                DocIDs : param.DocIDs,
                Detail : param.Detail
            },function(data){
                if (data.indexOf("@refresh@") >= 0) {
                    api.broadcast("refreshTopic", "true");
                    data = data.substring(9);
                }
                if (data) {
                    alert(data);
                }
            });
        }

    }

		//工具栏上点击一个按钮后的响应
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){srcButton.click(event.data, process)}, 500);

			var opid = srcButton.attr("opid");
			if (!confirm(toolhint.sure + (opid == 0 ? "撤稿" : "彻底删除" ) + "?")) return;
			
			var opurl = "../xy/article/Revoke.do"
				+ "?DocLibID=" + toolbarparam.docLibID
				+ "&DocIDs=" + dealDocIDs();
			//无窗口
			if(opid == 0){
				opurl = "../xy/article/Revoke.do"
					+ "?DocLibID=" + toolbarparam.docLibID
					+ "&DocIDs=" + dealDocIDs();
			} else if(opid == 1){
				opurl = "../xy/article/RevokeDelete.do"
					+ "?DocLibID=" + toolbarparam.docLibID
					+ "&DocIDs=" + dealDocIDs();
			}
			
			$.get(opurl, function(data){
				if (data.indexOf("@refresh@") >= 0) {
					api.broadcast("refreshTopic", "true");
					
					data = data.substring(9);
				}
				if (data) {
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
					return docLibIDs.substring(0, docLibIDs.length - 1);
				}
			}
			
			return docLibID;
		},
		//在调用操作前处理一下文档ID串，若是以逗号结尾，则去掉逗号
		dealDocIDs = function() {
			var docIDs = toolbarparam.docIDs;
			if (docIDs) return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		};
	//-----init & onload--------
	var init = function(sandbox){
		api = sandbox;
		$(".toolButton").click(process);
		$("#revokeBat").click(revokeOpen);

		toolbarparam = new ToolkitParam();
		api.listen("workspace.doclist:doclistTopic", listening);
	};
	return {
		init: init
	}
});