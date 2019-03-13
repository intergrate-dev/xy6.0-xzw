<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>选题选关联稿件</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
		.frm {
			border: 0;
			width: 100%;
			height: 440px;
		}
	</style>
</head>
<body>
	<iframe name="frmRefArticle" id="frmRefArticle" src="" class="frm"></iframe>
	<script>
		//获取参数
		var siteID = "<c:out value="${param.siteID}"/>";
		var docIDs = "<c:out value="${param.DocIDs}"/>";
		var UUID = "<c:out value="${param.UUID}"/>";
		var DocLibID = "<c:out value="${param.DocLibID}"/>";
		var ch = "<c:out value="${param.ch}"/>";

		//初始化-直接在弹出窗口中设置frame的链接地址
		$(function() {
			//type为5代表选题选关联稿件
			var type = 5;
			var dataUrl = "../../xy/MainArticle.do?siteID=" + siteID + "&type="
			+ type+"&ch="+ch;
			$("#frmRefArticle").attr("src", dataUrl);
			
			window.onbeforeunload = articleCancel;
		});
		
		function articleClose(artLibID,artIDs){
			if( artIDs == null ){
				alert("未选中稿件");
			}
			
			$.ajax({			
				url : "../../xy/topic/DealSelect.do",
				type : 'POST',
				data : {
					"DocLibID" : DocLibID,
					"docIDs" : docIDs,
					"RelLibID" : artLibID,
					"RelDocIDs" : artIDs
				},
				dataType : 'html',
				success  : function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//保存成功
							operationSuccess();
						} else {
							alert(msg);
						}
					} 
			}});
		}
		function operationSuccess(){
			window.onbeforeunload = null;
			
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
			+ "&DocLibID=" + DocLibID;
			$("#frmRefArticle").attr("src", url);
		}
		function articleCancel(){
			window.onbeforeunload = null;
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmRefArticle").attr("src", url);
		}
	</script>
</body>
</html>