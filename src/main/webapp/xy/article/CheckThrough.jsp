<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>校对意见</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="script/originalWordcount.js"></script>
	<style>
		#main{width:300px;height:170px;} 
		#checkReason
		{
			width:373px;
			height: 150px; 
			border-radius:2px; 
			border: 1px solid #ddd;
			resize: none;
			font-family: "microsoft yahei";
			outline: none;
			padding-left: 5px;
			margin-top: 2px;
		}
		.frm {
			display:none;
		}
		#doSubmit {
		    border-radius: 3px;
		    color: #fff;
		    background: #1bb8fa;
		    height: 30px;
		    border: none;
		    font-size: 12px;
		    cursor: pointer;
		    text-shadow: none;
		    padding: 0 20px;
		    font-family: "microsoft yahei";
		    margin-right: 10px;
		    cursor: pointer;
		}
		#doCancel{
		    height: 30px;
		    background: #b1b1b1;
		    border: none;
		    color: #fff;
		    border-radius: 3px;
		    margin-top: 5px;
		    padding: 0 20px;
		    text-shadow: none;
		    cursor: pointer;
		}
	</style>
</head>
<body>
	<div id="main"><span id="checkReasonCount"></span>
		<textarea id="checkReason" class="text" placeholder="可输入校对意见"></textarea>
	</div>
	<iframe name="frmCheck" id="frmCheck" src="" class="frm"></iframe>
	<div style="text-align: center;">
		<input type='button' id="doSubmit" value='确定'/>
		<input type='button' id="doCancel" value='取消'/>
	</div>
	

	<script>
		var docIDs = "<c:out value="${param.DocIDs}"/>";
		var UUID = "<c:out value="${param.UUID}"/>";
		var DocLibID = "<c:out value="${param.DocLibID}"/>";
		var IsEditor = "<c:out value="${param.IsEditor}"/>";
		var reason = "";
		$(function(){
			//点击保存按钮
			$('#doSubmit').click(function(){
				reason = $("#checkReason").val();
				$.ajax({				
					url : "../../xy/article/CheckThrough.do",
					type : 'POST',
					data : {
						"DocIDs" : docIDs,
						"DocLibID" : DocLibID,
						"UUID" : UUID,
						"IsEditor" : IsEditor,
						"Reason" : reason
					},
					success:function(msg){
						//alert(msg);
						if(msg.needCheck < 0){
							alert("校对已经完成，无需再次校对！");
						}else{
							alert("校对成功，还需校对"+ msg.needCheck +"次！");
						}
						if(msg.checked > 0){
							reason = msg.checked + "校完成. " + reason;
						}
						operationSuccess(reason);
					}
				});
			});
			//点击取消按钮
			$('#doCancel').click(function(){
				operationFailure();
			});
		});
		
		//操作成功了调用
		function operationSuccess(reasonStr) {
			if (UUID) {
				var url = "../../e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
					+ "&DocLibID=" + DocLibID
					+ "&Opinion=" + encodeU(reasonStr);
				$("#frmCheck").attr("src", url);
			} else {
				window.close();
			}
		}
		
		//操作失败了调用
		function operationFailure() {
			if (UUID) {
				var url = "../../e5workspace/after.do?UUID=" + UUID;
				$("#frmCheck").attr("src", url);
			} else {
				window.close();
			}
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