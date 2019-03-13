<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<%@include file="inc/MainHeader.inc"%>

<style type="text/css">
	#wrapMain{
		margin-left:0px;
		padding-left: 0;
		width: 98%;
		margin: 0 auto;
		}
	#panContent{
		height:350px;
		}
</style>
<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script>
//删除选中规则
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
			toolbarparam.docIDs = toolbarparam.docIDs;
					
			try {
				if(toolbarparam.docIDs){
						parent.articleClose(toolbarparam.docLibID,toolbarparam.docIDs);	
				}
			} catch (e) {
				var hint = "父窗口应实现articleClose()方法供已发布稿件对话框关闭时调用。"
					+ "\n   参数是:  [docLibID,docIDs]"
				alert(hint);
			}
		},
		cancel = function(event){
			//TODO	
			parent.articleCancel();
		},
		//若是同库，则只返回一个ID
		dealDocLibIDs = function(docLibIDs){
			if (!docLibIDs) return "";

			var libArr = (docLibIDs + "").split(","),
				docLibID = libArr[0];

			for (var i = 1; i < libArr.length; i++){
				if (libArr[i] && libArr[i] != docLibID) {
					return docLibIDs;
				}
			}
			
			return docLibID;
		};
		
	//-----init & onload--------
	var init = function(sandbox){
		api = sandbox;
		$("#doCancel").click(cancel);
		
		toolbarparam = new ToolkitParam();
		api.listen("workspace.doclist:doclistTopic", listening);
	};
	return {
		init: init
	}
},{requires:["../e5script/jquery/jquery.min.js", 
"../e5script/e5.utils.js", 
"../e5workspace/script/Param.js"
]});
</script>
<body>
<div id="wrapMain">
	<div>
		<%@include file="inc/SearchVote.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<%@include file="inc/Statusbar.inc"%>
			</div>
		</div>
		
	</div>
	
</div>
</body>

<%@include file="inc/MainFooter.inc"%>
