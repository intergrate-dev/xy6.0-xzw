<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>页面区块树</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<style type="text/css">
		.li
		{
			list-style:none;
			font-family:'微软雅黑'; 
			color: #666;  
			margin-top: 20px;
		}
		.span
		{
			margin-left: 8px;
		}
		.input
		{
			border-radius: 5px;
			border: 1px solid #ccc;
			font-size: 12px;
  			height: 26px;
  			width: 157px;
  			padding-left: 10px;	
		}
		.frm
		{
			display:none;
		}
	</style>
</head>
<body>	
	<%@include file="Tree.inc"%>
	<iframe name="frmBlock" id="frmBlock" src="" class="frm"></iframe>
	<script>
		//设置页面区块树需要的站点参数
		b_tree.siteID = "<c:out value="${siteID}"/>";
		if (!b_tree.siteID) b_tree.siteID = 1;
        b_tree.powerCheck=true;
		var articleIDs = "<c:out value="${docIDs}"/>";
		var docLibID = "<c:out value="${docLibID}"/>";
		var UUID = "<c:out value="${param.UUID}"/>";
	
		$(function(){
			$("#divBlockBtn").show();
			//点击保存按钮
			$('#btnBlockOK').click(function(){
				var filterChecked = b_tree.getChecks();
				var ids = filterChecked[0];
				
				if (!ids) {
					alert("没有选择区块，不能提交");
					return;
				}
				$.ajax({				
					url : "../../xy/block/PushArticle.do",
					type : 'POST',
					data : {
						"articleIDs" : articleIDs,
						"docLibID" : docLibID,
						"blockids" : ids
					},
					dataType : 'html',
					success:function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//推送成功
							operationSuccess();
						} else {
							alert(msg);
							operationFailure();
						}
					} else {
						operationFailure();
					}
					
				}});
			});
			//点击取消按钮
			$('#btnBlockCancel').click(function(){
				var url = "../../e5workspace/after.do?UUID=" + UUID ;
				$("#frmBlock").attr("src", url);
			});
		});
		
		//操作成功了调用
		function operationSuccess(){
			alert("推送完成");
			operationFailure();
		}
		//操作失败了调用
		function operationFailure() {
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmBlock").attr("src", url);
		}
		/** 对特殊字符和中文编码 */
		function encodeU(param1) {
			if (!param1)
				return "";
			var res = "";
			for ( var i = 0; i < param1.length; i++) {
				switch (param1.charCodeAt(i)) {
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
					break;
				}
			}
			return res;
		}
	</script>
</body>
</html>