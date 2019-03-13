<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var isDel = false;
			$(function(){
				$("#btnDel").click(function(){
					isDel = true;
					$("#formwindow").submit();
				});
				$("#btnSave").click(function(){
					isDel = false;
					$("#formwindow").submit();
				});
				$("#btnCancle").click(function(){
					isDel = false;
					window.parent.listfieldWindowClose(null,isDel);
				});
				$("#formwindow").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							//start 
							//验证
							var fwidth ="-1";
							var fheight = "-1";
							if($("#txtWidth")!=null){
								fwidth = $("#txtWidth").val();
							}
							if($("#txtHeight")!=null){
								fheight = $("#txtHeight").val();
							}
							if($.query.get("fieldtype")=="28"||
								$.query.get("fieldtype")=="2"||
								$.query.get("fieldtype")=="11"||
								$.query.get("fieldtype")=="12"||
								$.query.get("fieldtype")=="14"||
								$.query.get("fieldtype")=="15"||
								$.query.get("fieldtype")=="-1"){
								fwidth ="-1";
								fheight = "-1";
							}
							if($.query.get("height")=="-1"){
								fheight = "-1";
							}
							var required = "false";
							var checkdup = "false";
							
							if($("#cbrequired").attr("checked")=="checked"){
								required = "true";
							}
							if($("#cbcheckdup").attr("checked")=="checked"){
								checkdup = "true";
							}
							var fieldData = {
								fieldtype: $.query.get("fieldtype"),
								code:$.query.get("code"),
								name: $("#txtName").val(),
								required:required,
								checkdup:checkdup,
								aftertxt:$("#txtAfter").val(),
								width: fwidth,
								height:fheight
							};
							//验证后置文本是否符合xml格式
							var xmlData = {
							xmlcontent:$("#txtAfter").val()
							}
							//验证格式是否正确
							$.ajax({
								url: "../e5listpage/ListSubmit.do?method=isXml",
								type: "POST",
								data:xmlData,
								async: false,
								success: function(data) {
									
									if(data.toString().toLowerCase() =="true"){
										window.parent.listfieldWindowClose(fieldData,isDel);
									}
									else{
										alert("<i18n:message key="e5dom.form.custom.xmlerror"/>");
									}
								}
							});
							
						}
					}
				});
				//赋值
				$("#txtName").val($.query.get("name"));
				$("#txtWidth").val($.query.get("width"));
				$("#txtHeight").val($.query.get("height"));
				
				if($.query.get("required") == "true"){
					$("#cbrequired").attr("checked","checked");
				}
				if($.query.get("checkdup") == "true"){
					$("#cbcheckdup").attr("checked","checked");
				}
				
				if($.query.get("aftertxt")!=null){
					$("#txtAfter").val($.query.get("aftertxt"));
					
				}
				if($.query.get("fieldtype")=="28"||
				$.query.get("fieldtype")=="2"||
				$.query.get("fieldtype")=="11"||
				$.query.get("fieldtype")=="12"||
				$.query.get("fieldtype")=="14"||
				$.query.get("fieldtype")=="15"||
				$.query.get("fieldtype")=="-1"){
					$("#fwidth").empty().remove();
					$("#fheight").empty().remove();
				}
				
				if($.query.get("height")=="-1"){
					$("#fheight").empty().remove();
				}
				if($.query.get("width")=="-1"){
					$("#fwidth").empty().remove();
				}
			});
		</script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="css/form.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<form name="formwindow" id="formwindow" method="post" action="">
			
			<div>
				<table class="table" cellpadding="0" cellspacing="0">
					<tr>
						<td class="w30"><i18n:message key="e5dom.custom.txt.name"/>:</td>
						<td><input type="text" id="txtName" /></td>
					</tr>
					<tr id="fwidth">
						<td><i18n:message key="e5dom.custom.txt.width"/>:</td>
						<td><input class="validate[required,custom[onlyNumberSp],maxSize[4]]" type="text" id="txtWidth" class="w30" />px</td>
					</tr>
					<tr id="fheight">
						<td><i18n:message key="e5dom.custom.txt.height"/>:</td>
						<td><input class="validate[required,custom[onlyNumberSp],maxSize[4]]" type="text" id="txtHeight" class="w30" />px</td>
					</tr>
					<tr>
						<td colspan="2"><input type="checkbox" class="fl" id="cbrequired"/><label for="cbrequired" class="fl ml5"><i18n:message key="e5dom.custom.txt.isrequired"/></label></td>
					</tr>
					<tr>
						<td colspan="2"><input type="checkbox" class="fl" id="cbcheckdup"/><label for="cbcheckdup" class="fl ml5"><i18n:message key="e5dom.custom.txt.ischeckdup"/></label></td>
					</tr>
					<tr>
						<td colspan="2">
							<div><i18n:message key="e5dom.custom.txt.postfixtext"/></div>
							<div><textarea id="txtAfter" style='width:98%;height:50px;' ></textarea></div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input id="btnDel" class="button" type="button" value="<i18n:message key="e5dom.custom.txt.delete"/>" />
							<input id="btnSave" class="button" type="button" value="<i18n:message key="e5dom.custom.txt.save"/>" />
							<input id="btnCancle" class="button" type="button" value="<i18n:message key="e5dom.custom.txt.cancle"/>" />
						</td>
					</tr>
				</table>
			</div>
		</form>
	</body>
</html>