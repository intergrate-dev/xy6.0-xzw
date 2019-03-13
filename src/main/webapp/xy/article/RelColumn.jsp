<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>关联栏目</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
		.frm {
			border: 0;
			width: 100%;
			height: 430px;
		}
	</style>
</head>
<body>
	<iframe name="frmRefColumn" id="frmRefColumn" src="" class="frm"></iframe>
	<script>
		//获取参数
		var siteID = "<c:out value="${param.siteID}"/>";
		var docIDs = "<c:out value="${param.DocIDs}"/>";
		var UUID = "<c:out value="${param.UUID}"/>";
		var DocLibID = "<c:out value="${param.DocLibID}"/>";
		var oldColID = "<c:out value="${param.colID}"/>";
		var ch = "<c:out value="${param.ch}"/>";
		var colType = "op";

		//初始化-直接在弹出窗口中设置frame的链接地址
		$(function() {
			
			var url = "../../xy/column/ColumnFavorite.jsp?cache=1&type=" + colType + "&siteID="
			+ siteID + "&ch=" + ch;
			$("#frmRefColumn").attr("src", url);
			window.onbeforeunload = operationClsoe;
		});
		//当用户提交选择的栏目,实现columnClose方法
		function columnClose(filterChecked, allFilterChecked) {
			var colIDs = allFilterChecked[0];
			if(colIDs == '' || colIDs == null){
				alert("您未选中任何栏目！");
			}
			$.ajax({				
				url : "../../xy/article/relColumn.do",
				type : 'POST',
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : DocLibID,
					"colIDs" : colIDs,
					"oldColID" : oldColID
				},
				dataType : 'html', 
				success:function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//推送成功
							operationClsoe();
						} 
						else {
							alert("操作失败");
							operationClsoe();
						}
					} else {
						operationClsoe();
					}
			}});
		}
		
		//操作成功了调用
		function operationClsoe(){
			window.onbeforeunload = null;
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmRefColumn").attr("src", url);
		}
		
		//点击取消按钮时
		function columnCancel() {
			operationClsoe();
		}
		
	</script>
</body>
</html>