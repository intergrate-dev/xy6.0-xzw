<%@page pageEncoding="UTF-8"%>
<c:if test="${domInfo.docTypeID == 1 or domInfo.docTypeID == 2}">
	<!--<li id="btnReadMode" title="显示/隐藏阅读窗口" class="btn btnReadMode">阅读窗</li>-->
	<img  id="btnReadMode" title="显示/隐藏阅读窗口" src="img/18.png"  class="btnReadMode" />
	<div class="switch" data-on="info" data-off="success">
	    <input type="checkbox" checked />
	</div>
	<div id="divRead" style="display:none; position:absolute; right:11%; top:166px; bottom: 5px;">
		<img class="closeBtn" style="cursor:pointer;  position: ABSOLUTE;width: 15px;top: 10px;right: 10px;" src="img/close.png">
		<iframe id="readFrame"  srcdoc="" src="" frameborder="1"></iframe>
	</div>
</c:if>
<style>

	.btnReadMode {
		position:absolute;
		top:105px;
		right:10px;
	}
	
	#readFrame {
		width:803px; 
		/*min-height:380px;*/
		border:1px solid #ddd;
		height: 100%;
		background: #fff;
		cursor: pointer;
	}
	

</style>
<script type="text/javascript">
	//订阅窗口的列表选择事件
	e5.mod("workspace.leftListener",function() {
		var listening = function(msgName, callerId, param) {
			var ids = param.docIDs;
			var url = '<!doctype html> '+
						'<html lang="en">' +
						'<head>' +
							'<meta charset="UTF-8" />' +
							'<title>Document</title>' +
						'</head>' +
						'<body>' +
							'<div style="font-size:14px; text-align:center; margin-top:100px; color:#999; font-weight:bold;">请先在列表中选择稿件</div>' +
						'</body>' +
						'</html>'; 
			$("#readFrame").attr("srcdoc", url);			
			if (ids) {
				var id = (ids.split(","))[0];
				url = "article/ReadMode.do?DocLibID=" + param.docLibID + "&DocIDs=" + id;
				$("#readFrame").removeAttr("srcdoc");	
				$("#readFrame").attr("src", url);
			}
			
		}
		//读稿模式响应
		var readmode = function(evt) {
			if ($("#divRead").is(":visible")) {
				$("#divRead").hide();
			} else {
				$("#divRead").show();
			}
		}
		//改变readFrame的宽度
		var chageW = function(){
			var w = $(window).width();
			if(w <= 1200 ){
				$("#readFrame").css("width","444px");
				$(".btnReadMode").css("top","132px");
				$("#divRead").css('top','196px')
			}else if(1200 < w & w < 1260){
				$("#readFrame").css("width","522px");
			}else{
				$("#readFrame").css("width","803px");
			}
		}
		var init = function(sandbox) {
			api = sandbox;
			api.listen("workspace.doclist:doclistTopic", listening);
			
			$("#btnReadMode").click(readmode);
			$('.closeBtn').click(readmode);
			chageW();
		}
		
		return {
			init: init
		}
	});
</script>