<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    #wrapMain {
        margin-left: 0px;
        padding-left: 0px;
        width: 98%;
    	margin: 0 auto;
    }
    #panContent {
        height: 350px;
    }
</style>
<script>
    //删除选中规则
    var reljsonData ;
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
			toolbarparam.docIDs = toolbarparam.docIDs;

			if(search_art.type == 2 || search_art.type == 3 || search_art.type == 5){
				try{//供组图稿、视频稿调用、选题选取关联稿件
					if(toolbarparam.docIDs){
						parent.articleClose(toolbarparam.docLibID, toolbarparam.docIDs);
					}
				}catch(e){
					var hint = "父窗口应实现articleClose()方法供已发布稿件对话框关闭时调用。"
							+ "\n   参数是:  [docLibID,docIDs]"
					alert(hint);
				}
			}
		},
		process = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){
				srcButton.click(event.data, process)
			}, 500);

			relateArticle = {
				relateArticleCon: $('#colFrm').val(),
				relateArticleId: $('#colFrmId').val()
			};
			localStorage.setItem('relateArticle',JSON.stringify(relateArticle));

			try{
				if(search_art.type == 4){//相关稿件
					parent.article_rel.articleClose(toolbarparam.docLibID, toolbarparam.docIDs);
				}else{
					//其它选稿调用
					parent.articleClose(toolbarparam.docLibID, toolbarparam.docIDs);
				}
			}catch(e){
				var hint = "父窗口应实现articleClose()方法供已发布稿件对话框关闭时调用。"
						+ "\n   参数是:  [docLibID,docIDs]"
				alert(hint);
			}
		},
		cancel = function(event){
			if(search_art.type == 4){
				parent.article_rel.articleCancel();
			}else{
				parent.articleCancel();
			}

		},
		//若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if(!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
					docLibID = libArr[0];

			for(var i = 1; i < libArr.length; i++){
				if(libArr[i] && libArr[i] != docLibID){
					return docLibIDs;
				}
			}
			return docLibID;
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
    <div>
        <%@include file="inc/SearchArticle.inc" %>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
            </div>
        </div>
    </div>
</div>
<%@include file="inc/RelReadMode.inc"%>
</body>
<%@include file="inc/MainFooter.inc" %>
