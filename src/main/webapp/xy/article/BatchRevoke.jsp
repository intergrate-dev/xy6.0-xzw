<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>批量撤稿</title>
	<link rel="stylesheet" type="text/css" href="../script/bootstrap/css/bootstrap.css">
	<style type="text/css">
		form{
			margin-left:20px;
			margin-top:25px;
		}
		p div:first-child,p{
			font-size:16px; 
		}
		textarea{
			width:400px;height:80px;
		}
		.btn{
			font-size: 16px;
		}
	</style>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
		function batSubmit(){
			var docIDs = document.getElementById("DocIDs").value;
			var reason = document.getElementById("Detail").value;
            if (!docIDs){
                alert("撤稿ID不能为空");
                return false;
            }
			$.ajax({
				url : "../../xy/article/Revoke.do",
				type : 'POST',
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : document.getElementById("DocLibID").value,
					"Detail":reason
				},
				success:function(msg){	
					if (msg.rs == "success") {
						var tool = opener.e5.mods["workspace.toolkit"];
						tool.closeOpDialog("OK", 1);
						window.close();
					} else {
						alert(msg.rs);
						// window.close();
					}
				}
			});		
		}
		function closeWin(){
			window.close();
		}
	</script>
</head>
<body>
	<form action="">
		<input type="hidden" id="DocLibID" name="DocLibID" value="${param.DocLibID}"/>
		
		<p>请输入稿件ID,用英文逗号隔开：</p>
		<textarea id="DocIDs" placeholder="必填" ></textarea>
		<br>
		<p>
			撤稿原因：
		</p>
		<textarea id="Detail" placeholder="请在这里填写撤稿原因"></textarea>
		<br>
		<div class="text-center">
			<input  type="button" class="button btn btn-primary " onclick="batSubmit()" value="确定">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" class="button btn" onclick="closeWin()" value="取消">
		</div>

	</form>
</body>
</html>