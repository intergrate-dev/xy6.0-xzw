<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<style>
	.frm {
		border: 0;
		width: 100%;
		height: 475px;
	}
	.pub{
		list-style:none;
		font-family: '微软雅黑';
		color: #666;  
		margin: 20px 0;  
		margin-left:15px;
	}
	#pubTime{
		border-radius: 3px; 
		border: 1px solid #ccc;
		font-size: 12px;
		padding-left: 10px;
	}
	
	</style>
</head>
<body>
	<iframe name="frmArt" id="frmArt" src="" class="frm"></iframe>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${siteID}"/>";
		var docLibID = "<c:out value="${docLibID}"/>";
		var blockID = "<c:out value="${blockID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		var type = 1;//MainArticle.do共两个地方应用；合成多标题时候type为0，区块内容选稿时候type为1
		//初始化-直接在弹出窗口中设置iframe的src
		$(function() {
			var dataUrl = "../../xy/MainArticle.do?siteID=" + siteID + "&type="
			+ type;
			$("#frmArt").attr("src", dataUrl);
			
			$('#pubTime').datetimepicker({
				language : 'zh-CN',
				weekStart : 0,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : true,
				startView : 2,
				minView : 0,
				disabledDaysOfCurrentMonth : 0,
				forceParse : 0,
				pickerPosition: "bottom-left",
				format : 'yyyy-mm-dd hh:ii'
				
			});
			$('#pubTime').datetimepicker().on('changeDate', function(ev) {
//				priority_day = ev.date.formatToUTC("yyyy-MM-dd hh:mm:00");
//				$("#pubTime").val( priority_day );
			});
			
		});
		
		function articleClose(artLibID,docIDs){
			if( docIDs == null ){
				alert("未选中稿件");
			}
			
			$.ajax({			
				url : "../../xy/block/PushSelect.do",
				type : 'POST',
				data : {
					"artLibID" : artLibID,
					"artIDs" : docIDs,
					"blockID" : blockID
				},
				dataType : 'html',
				success  : function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//保存成功
							operationSuccess(msg.substr(7));
						} else {
							alert(msg);
						}
					} 
			}});
		}
		function operationSuccess(docIDs){
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
			+ "&DocLibID=" + docLibID;
			$("#frmArt").attr("src", url);
		}
		function articleCancel(){
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmArt").attr("src", url);
		}
	</script>
</body>
</html>