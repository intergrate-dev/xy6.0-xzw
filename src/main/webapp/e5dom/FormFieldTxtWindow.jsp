<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript">
			var isDel = false;
			$(function(){
				$("#btnDel").click(function(){
					
					var data = {
							insertid:$.query.get("insertid"),
							content:$("#txtContent").val()
					};
					window.parent.listfieldtxtWindowClose(data,true);
				});
				$("#btnSave").click(function(){
					
					var xmlData = {
							xmlcontent:$("#txtContent").val()
					}
					//验证格式是否正确
					$.ajax({
						url: "../e5listpage/ListSubmit.do?method=isXml",
						type: "POST",
						data:xmlData,
						async: false,
						success: function(data) {
							var datas = {
									insertid:$.query.get("insertid"),
									content:$("#txtContent").val()
							};
							if(data.toString().toLowerCase() =="true"){
								window.parent.listfieldtxtWindowClose(datas,false);
							}
							else{
								alert("<i18n:message key="e5dom.form.custom.xmlerror"/>");
							}
							
						}
					});
				});
				$("#btnCancle").click(function(){
					
					window.parent.listfieldtxtWindowClose(null,false);
				});
				//赋值
				$("#txtContent").val($.query.get("content"));
			});
		</script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="css/form.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<form name="formwindow" id="formwindow" method="post" action="">
			<table class="table" cellpadding="0" cellspacing="0">
				<tr>
					<td><i18n:message key="e5dom.form.custom.inserttxtcontent"/>:<br><textarea id="txtContent" style="height:80px;width:280px;"></textarea></td>
				</tr>
				<tr>
					<td>
						<input id="btnDel" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btndel"/>" />
						<input id="btnSave" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btnsave"/>" />
						<input id="btnCancle" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btncancel"/>" />
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>