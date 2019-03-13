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
		var menuID = "<c:out value="${menuID}"/>";
		var accountID = "<c:out value="${accountID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		$(function() {
			var dataUrl = "../../xy/MainWXSelect.do?siteID=" + siteID;
			$("#frmArt").attr("src", dataUrl);	
		});
		
		function articleClose(artLibID,docIDs){
			if( docIDs == null ){
				alert("未选中稿件");
			}
			
			$.ajax({			
				url : "../../xy/wx/DealSelect.do",
				type : 'POST',
				data : {
					"artLibID" : artLibID,
					"artIDs" : docIDs,
					"menuID" : menuID,
					"accountID" : accountID
				},
				dataType : 'html',
				success  : function(msg, status){	
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {//保存成功
							var index = msg.indexOf("来自栏目");
							operationSuccess(msg.substring(7,index),msg.substr(index));
							
						} else {
							alert(msg);
						}
					} 
			}});
		}
		function operationSuccess(docIDs,opnions){
			var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
			+ "&DocLibID=" + docLibID + "&Opinion="
			+ encodeU(opnions);
			$("#frmArt").attr("src", url);
		}
		function articleCancel(){
			var url = "../../e5workspace/after.do?UUID=" + UUID;
			$("#frmArt").attr("src", url);
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