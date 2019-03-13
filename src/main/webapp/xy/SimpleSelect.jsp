<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    #panContent {
        height: 350px;
		border-top: 1px solid #ddd;
    }
	#wrapMain {
		margin-left: 5px !important;
		padding-right: 18px !important;
}
</style>
<!-- 无组的数据的选择窗口，如选择直播话题 -->
<body>
<div id="wrapMain">
    <div>
        <%@include file="inc/SearchGroupSelect.inc"%>
        <div id="main">
            <div id="panContent" class="panContent">
                <%@include file="inc/Statusbar.inc" %>
            </div>
        </div>
    </div>
</div>
</body>
<%@include file="inc/MainFooter.inc" %>
<script>
//无分组，页面加载后按默认参数传递消息，以触发整个列表的显示
e5.mod("workspace.resourcetree", function() {
	var api;
	var init = function(sandbox) {
		api = sandbox;
	},
	onload = function() {
		defaultClick();
	}

	var defaultClick = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
		if (!ready) {
			setTimeout(defaultClick, 100);
			return;
		}
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];
		api.broadcast("resourceTopic", param);
	}
	return {
		init : init,
		onload : onload
	};
});

//操作栏：点击确定、取消按钮
    var toolbarparam = new ToolkitParam();
    e5.mod("workspace.toolkit", function(){
        var api;
		var type = "${type}";
		
        //订阅响应
		var listening = function(msgName, callerId, param){
			for(var name in param){
				toolbarparam[name] = param[name];
			}

			if(toolbarparam.docLibIDs){
				toolbarparam.docLibID = toolbarparam.docLibIDs;
			}
			toolbarparam.docLibID = dealDocLibIDs(toolbarparam.docLibID);
		},
		save = function(event){
			//防连击
			var srcButton = $(this);
			srcButton.unbind("click");
			setTimeout(function(){
				srcButton.click(event.data, save)
			}, 500);
			var docLibID = toolbarparam.docLibID;
			var docIDs = toolbarparam.docIDs;
			
			if (!docIDs) {
				alert("请先做选择");
				return;
			}
			try {
                parent.groupSelectOK(docLibID, docIDs);
			} catch(e){
				alert("父窗口应实现groupSelectOK(docLibID,docID)方法");
			}
		},
		cancel = function(event){
			try {
                parent.groupSelectCancel();
			} catch(e){
				alert("父窗口应实现groupSelectCancel()方法"); 
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
		}
        //-----init & onload--------
        var init = function(sandbox){
            api = sandbox;
            $("#doSave").click(save);
            $("#doCancel").click(cancel);

            api.listen("workspace.doclist:doclistTopic", listening);
        };
        return {
            init: init
        }
	});
</script>
