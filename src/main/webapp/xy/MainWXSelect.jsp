<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<style>
	#wrapMain{
		padding-left:0;
	}
</style>
<script>
    //删除选中规则
    e5.mod("workspace.toolkit", function(){
        var api, toolbarparam,
		        //订阅响应
		        listening = function(msgName, callerId, param){
		            for(var name in param){
		                toolbarparam[name] = param[name];
		            }
		
		            if(toolbarparam.docLibIDs){
		                toolbarparam.docLibID = toolbarparam.docLibIDs;
		            }
		            toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
		            toolbarparam.docIDs = dealDocIDs(toolbarparam.docIDs);
		        },
                process = function(event){
                    //防连击
                    var srcButton = $(this);
                    srcButton.unbind("click");
                    setTimeout(function(){
                        srcButton.click(event.data, process)
                    }, 500);

                    try{
                        parent.articleClose(toolbarparam.docLibID, toolbarparam.docIDs);
                    }catch(e){
                        var hint = "父窗口应实现articleClose()方法供已发布稿件对话框关闭时调用。"
                                + "\n   参数是:  [docLibID,docIDs]"
                        alert(hint);
                    }
                },
                cancel = function(event){
                     parent.articleCancel();
                },
        //若是同库，则只返回一个ID
                dealDocLibIDs = function(docLibIDs){
                    if(!docLibIDs) return "";

                    var libArr = (docLibIDs + "").split(","),
                            docLibID = libArr[0];

                    for(var i = 1; i < libArr.length; i++){
                        if(libArr[i] && libArr[i] != docLibID){
                            return dealDocIDs(docLibIDs);
                        }
                    }

                    return docLibID;
                },
        //处理文档ID串，若是以逗号结尾，则去掉逗号
                dealDocIDs = function(docIDs){
                    if (docIDs && docIDs.charAt(docIDs.length - 1) == ',')
                        docIDs = docIDs.substring(0, docIDs.length - 1);
                    return docIDs;
                };
        //-----init & onload--------
        var init = function(sandbox){
            api = sandbox;
            $("#doSave").click(process);
            $("#doCancel").click(cancel);

            toolbarparam = new ToolkitParam();
            api.listen("workspace.doclist:doclistTopic", listening);
        };
        return {
            init: init
        }
    }, {
        requires: [
            "../e5script/e5.utils.js",
            "../e5workspace/script/Param.js"
        ]
    });
</script>
<body>
	<div id="wrapMain">
		<%@include file="inc/ChannelTab.inc"%>
		<%@include file="inc/WXChannel.inc"%>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
            </div>
        </div>
	</div>
</body>
<%@include file="inc/MainFooter.inc"%>