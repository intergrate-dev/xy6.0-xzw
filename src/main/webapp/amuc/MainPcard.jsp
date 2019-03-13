<%@page pageEncoding="UTF-8"%>
<%@include file="../e5include/IncludeTag.jsp"%>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<style type="text/css">
body{
	overflow-y: hidden;
}
</style>
<body style="">
	<input type="hidden" id="siteID" name="siteID" value="<%=request.getParameter("siteID")%>"/>
	<script>
	$(function(){
		//$("#subtab_a_memcardlog").style.display="none";
		_tabClick(0);
	});
	function _tabClick(t) {
		var siteID = $("#siteID").val();
		$(".channelTab").removeClass("select");
		if(t==0){
			var src0 = "MainSimple.do?t=memcard&siteID="+siteID;
			$("#channelTab0").addClass("select");
			$("#contentFrame").attr("src",src0);
		}else if(t==1){
			var src1 = "MainSimple.do?t=memcardlog&siteID="+siteID;
			$("#channelTab1").addClass("select");
			$("#contentFrame").attr("src",src1);
		}		
	}
</script>
	<ul class="channels">
		<li class="channelTab" id="channelTab0" onclick="_tabClick(0)">数字报卡</li>		
		<li class="channelTab" id="channelTab1" onclick="_tabClick(1)">报卡日志</li>
	</ul>
	<iframe id="contentFrame" name="contentFrame" src="" frameborder="0" scrolling="auto" width="100%" height="94%"></iframe>
</body>
<style type="text/css">
html{
	overflow: hidden;
}
</style>
