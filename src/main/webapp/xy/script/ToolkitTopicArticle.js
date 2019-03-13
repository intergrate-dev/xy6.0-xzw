//话题稿件操作栏
e5.mod("workspace.toolkit",function() {
	var api, toolbarparam, 
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
            $(".toolButton").hide();
            $(".toolButton[opid=2]").show();
            if (param.docIDs) {
                $(".toolButton[opid=0]").show();
                $(".toolButton[opid=1]").show();
                $(".toolButton[opid=3]").show();
                if(param.docIDs.indexOf(',')==-1){
                    $(".toolButton[opid=4]").show();
                }
            }
        },

		//工具栏上点击一个按钮后的响应
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){srcButton.click(event.data, process)}, 500);

            var opurl = "";
 			var opid = srcButton.attr("opid");

            var ids = dealDocIDs();
            var topicID = $("#topicID").val();
            var channel = $("#channel").val();

            if((ids==""||ids==null)&&opid!="2"){
                alert("至少需要选择一篇稿件！");
                return;
            }

            var deleteUrl = "";
			if(opid=="2"){//更新排序
                updateOrderOfTable();
                return;
            }else if(opid=="0"){//置顶
                opurl = "../xy/articleorder/topicArticleMoveTop.do?docIDs=";
            }else if(opid=="1"){//取消置顶
                if (!confirm("是否确认取消置顶?")) return;
                opurl = "../xy/articleorder/topicArticleCancelTop.do?docIDs=";
            }else if(opid=="3"){//从话题删除
                var channelValue = "App";
                if(channel=="2"){
                    channelValue = "Web";//app话题稿件关联操作时，询问是否删除web稿件话题关联
                }
                var deleteAll = "";
                if (confirm(channelValue+"发布库中的同一稿件将同时删除？")){
                    deleteAll = "0";//同步删除
                    opurl = "../xy/topic/topicArticleDeleteRelation.do?docIDs=";
                    deleteUrl="&deleteAll="+deleteAll;
                }else{//不删除
                    return;
                }
            }else if(opid=="4"){//修改
                if(!ids){
                    alert("至少需要选择一篇稿件");
                    return;
                }else if(ids.indexOf(',')!=-1){
                    alert("只能选择一篇稿件进行修改");
                    return;
                }
                updateTopicArticle(ids,channel);
                return
            }else{
                return;
            }


            $.ajax({
                url: opurl+ids+"&topicID="+topicID+"&channel="+channel+deleteUrl,
                type: "GET",
                async: false,
                contentType: "application/json;charset=utf-8",
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown + ':' + textStatus); // 错误处理
                },
                success: function(data) {
                    if(data.status=="1"){
                        $("#btnRefresh").click();
                    }
                    alert(data.info);
                }
            });
		},

        updateTopicArticle = function(ids,channel){
            $.ajax({
                url: "../xy/topic/topicArticleUpdate.do?docID="+ids+"&channel="+channel,
                type: "GET",
                async: false,
                contentType: "application/json;charset=utf-8",
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown + ':' + textStatus); // 错误处理
                },
                success: function(data) {
                    // window.open(data.url,'_top');
                    var winObj = window.open(data.url, 'newwindow', 'height=100, width=400, top=0,left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
                    var loop = setInterval(function() {
                        if(winObj.closed) {
                            clearInterval(loop);
                            $("#btnRefresh").click();
                        }
                    }, 1000);
                }
            });
        }

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
			if (docIDs&&docIDs.charAt(docIDs.length - 1) == ',') return docIDs.substring(0, docIDs.length - 1);
			return docIDs;
		};

	//-----init & onload--------
	var init = function(sandbox){
        $("#toolTR").append("<li class=\"toolButton tIconBText\" alt=\"修改\" opid=\"4\">\n" +
            "                <button class=\"btn btn-small\" type=\"button\">修改</button>\n" +
            "            </li>\n" +
            "            <li class=\"toolButton tIconBText\" alt=\"置顶\" opid=\"0\">\n" +
            "                <button class=\"btn btn-small\" type=\"button\">置顶</button>\n" +
            "            </li>\n" +
            "            <li class=\"toolButton tIconBText\" alt=\"取消置顶\" opid=\"1\">\n" +
            "                <button class=\"btn btn-small\" type=\"button\">取消置顶</button>\n" +
            "            </li>\n" +
            "            <li class=\"toolButton tIconBText\" alt=\"更新排序\" opid=\"2\">\n" +
            "                <button class=\"btn btn-small\" type=\"button\">更新排序</button>\n" +
            "            </li>\n" +
            "            <li class=\"toolButton tIconBText\" alt=\"从话题删除\" opid=\"3\">\n" +
            "                <button class=\"btn btn-small\" type=\"button\">从话题删除</button>\n" +
            "            </li>")

		api = sandbox;
		$(".toolButton").click(process);

		toolbarparam = new ToolkitParam();
		api.listen("workspace.doclist:doclistTopic", listening);
	};
	return {
		init: init
	}
});