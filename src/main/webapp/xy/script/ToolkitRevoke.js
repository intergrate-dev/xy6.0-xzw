//撤稿中心操作栏
e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, searchInited = false,
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
			
			addSearchInit();
		},
		toolbar = function(param){
			$(".toolButton").hide();
			$(".toolButton[opid=2]").show();
			
			if (param.docIDs) {
				$(".toolButton[opid=3]").show();
				
				var status = $("#a_status").val();
				if (status == 1) {
					//已发布，显示撤稿
					$(".toolButton[opid=0]").show();
				} else {
					// 已发布的稿件不显示 "彻底删除"按钮
                    $(".toolButton[opid=1]").show();
				}
			}
		},
	//打开窗口的操作
	dialogOpen = function(url, iWidth, iHeight){
		url += "DocLibID=" + toolbarparam.docLibID
				+ "&DocIDs=" + dealDocIDs();

        var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		
		var feature = "height="+iHeight+", width="+iWidth+",left="+iLeft+", top="+iTop
			+ ",toolbar=no,location=no,z-look=yes,alwaysRaised=yes";
		
		window.open(url, "revokeCenter", feature);
	},
	callUrl = function(opurl, opName) {
		if (!confirm(toolhint.sure + opName + "?")) return;
		
		opurl += "DocLibID=" + toolbarparam.docLibID
				+ "&DocIDs=" + dealDocIDs();
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
		closeOpDialog = function(ret, callMode) {
			if (ret == "OK") {
				api.broadcast("refreshTopic", "true");
			}
			if (callMode == 2) {
				if (curOpDialog) curOpDialog.closeEvt();
				curOpDialog = null;
			}
		},
		//工具栏上点击一个按钮后的响应
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){srcButton.click(event.data, process)}, 500);

			var opid = srcButton.attr("opid");
			if (opid == 0){
				//撤稿
				dialogOpen('../xy/article/Reject.jsp?type=1&', 450, 350);
				return;
			} else if (opid == 2){
				//批量撤稿
				dialogOpen('../xy/article/BatchRevoke.jsp?', 450, 350);
				return;
			} else if (opid == 3) {
				//流程记录
				dialogOpen('../e5workspace/manoeuvre/FlowRecordList.do?', 800, 450);
				return;
			} else if (opid == 1) {
				callUrl("../xy/article/RevokeDelete.do?", "彻底删除");
			}
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
		//在调用操作前处理一下文档ID串，若是以逗号结尾，则去掉逗号
		dealDocIDs = function() {
			var docIDs = toolbarparam.docIDs;
			if (docIDs && docIDs.charAt(docIDs.length - 1) == ',') 
				return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		};

	//增加撤稿的查询条件，只需要做一次。
	//由于Search区域引用的是公用模块，因此改在本模块中才对特殊条件做初始化
	var addSearchInit = function() {
		if (searchInited) return;
		
		searchInited = true;
		
		//部门列表初始化
		var sel = document.getElementById("a_orgID");
		while (sel.options.length > 0)
			sel.remove(0);
		var op = document.createElement("OPTION");
		op.value = "";
		op.text = "";
		sel.options.add(op);
					
		var dataUrl = "../xy/user/Org.do";
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
			if (datas != null){
				//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
				for (var i = 0; i < datas.length; i++) {
					var op = document.createElement("OPTION");
					op.value = datas[i].key;
					op.text = datas[i].value;
					sel.options.add(op);
				}
			}
		}});
		
		//状态列表初始化，只保留“已发布”和“已撤稿”两个选项
		var sel = document.getElementById("a_status");
		while (sel.options.length > 0)
			sel.remove(0);

		var op = document.createElement("OPTION");
		op.value = 1;
		op.text = "已发布";
		sel.options.add(op);
		
		var op = document.createElement("OPTION");
		op.value = 7;
		op.text = "已撤稿";
		sel.options.add(op);
	}
	//-----init & onload--------
	var init = function(sandbox){
		api = sandbox;
		$(".toolButton").click(process);

		toolbarparam = new ToolkitParam();
		api.listen("workspace.doclist:doclistTopic", listening);
	};
	return {
		init: init,
		closeOpDialog : closeOpDialog
	}
});