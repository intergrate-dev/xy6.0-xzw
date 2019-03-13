<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>

<style type="text/css">
	#wrapMain{margin-left:0px;}
	#ruleInfo{
	 width:auto;
  	 top:88px; 
  	 left:280px;
  	 position:absolute; 
  	 background-color:white; 
  	 border:1px solid #ddd;
  	 border-top-left-radius:3px;
  	 border-bottom-left-radius:3px;
  	 border-top-right-radius:3px;
  	 border-bottom-right-radius:3px;
  	 display:none;
  	 z-index:100;
	}
	.listP{
		color:#333;
		font-size: 12px;
		padding: 8px;
		padding-bottom: 0;
		margin-bottom: 0;
		/*width: 240px;
	    white-space: nowrap;
	    overflow: hidden;
	    text-overflow: ellipsis;*/
	}
	.listP:last-child{
		margin-bottom: 10px;
		
	}
</style>
<script type="text/javascript" src="../e5workspace/script/Param.js"></script>
<script type="text/javascript">
	main_param.type = "<c:out value="${type}"/>";
	
	e5.mod("workspace.resourcetree",function() {
		var api;
		var defaultClick = function() {
			var param = new ResourceParam();
			for (var name in main_param) 
				param[name] = main_param[name];
			
			api.broadcast("resourceTopic", param);
		}
		//检查statusbar是否加载完毕，加载完毕才发送点击消息
		var checkLoad = function() {
			var statusReady = e5.mods["workspace.doclistMain"].isReady;
			var searchReady = e5.mods["workspace.search"].isReady;
			var ready = !!statusReady&&!!searchReady&&statusReady()&&searchReady();
			if (!ready) {
				setTimeout(checkLoad, 100);
				return;
			}
			changeBtn4Cancel();
			defaultClick();
		}
		//规则选择列表是利用了主界面列表，把界面上的条件清空按钮改成关闭按钮
		var changeBtn4Cancel = function() {
			$("#queryReset").attr("title", "关闭");
			$("#queryReset").html("关闭");
			var reset = $("#queryReset").find("i");
			reset.removeClass("icon-repeat");
			reset.addClass("icon-remove");
			
			$("#queryReset").click(function(){
				parent.column_form.cancelRule(main_param.type);
			});
		}
		var init = function(sandbox) {
			api = sandbox;
			
		},
		onload = function(){
			checkLoad();
			$('tr').live('mouseenter',function(){
				if(!$(this).is(':has(td)')) {
				    return;
				}
				var ruleID = $(this).children().eq(4).html();
				/*e5.dialog({
					type : "iframe",
					value : "MainRuleInfo.do?ruleID="+ruleID
				}, {
					title : "发布规则详细信息",
					autoClose : true,
					ishide : false,
					showTitle : true,
					width : "420px",
					height : "125px",
					resizable : true,
					esc : true
				}).show();*/
				$.post("MainRuleInfo.do",{ruleID:ruleID},function(data){
					$("#ruleInfo").empty();
					$("#ruleInfo").html("<p class='listP'>栏目："+'<span title="'+data.column_dir+'">'+data.column_dir+'<span>'
						+"</p><p class='listP'>稿件："+'<span title="'+data.article_dir+'">'+data.article_dir+'<span>'
						+"</p><p class='listP'>内容图片："+'<span>'+data.photo_dir+'<span>'
						+"</p><p class='listP'>附件："+ '<span>'+data.attach_dir+'<span>'
					);
					$("#ruleInfo").show();
				});
			});
			
			$("#listing").live('mouseleave',function(){
				$("#ruleInfo").hide();
			});
			$("#ruleInfo").live('mouseover',function(){
				$(this).show();
			});
			$("#ruleInfo").live('mouseout',function(){
				$(this).hide();
			});
		}
		return {
			init: init,
			onload:onload
		}
	});
	
	e5.mod("workspace.toolkit",function() {
		var api, 
			//订阅响应
			listening = function(msgName, callerId, param) {
				var docID = param.docIDs;
				if (docID) {
					//以下取得发布规则名称的方式不太正规，若列表改了可能取不到名称
					var tds = $("tr[id=" + docID + "]").children();
					var name = $(tds[2]).text();
					parent.column_form.closeRule(main_param.type, name, docID);
				}
			}
		//-----init & onload--------
		var init = function(sandbox){
			api = sandbox;
			api.listen("workspace.doclist:doclistTopic", listening);
		};
		return {
			init: init
		}
	});
</script>
<body>
<div id="wrapMain">
	<%@include file="inc/Search.inc"%>
	<div id="main">
		<div id="panContent" class="panContent">
			<%@include file="inc/Statusbar.inc"%>
		</div>
	</div>
</div>
<div id="ruleInfo">
	
</div>
</body>

<%@include file="inc/MainFooter.inc"%>
