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
				
				$("#required_w").change(function(){
					
					if($(this).attr("checked")=="checked"){
						$(this).attr("checked",true);
						$("#txtWidth").attr("disabled", false);
					}
					else{
						$(this).attr("checked",false);
						$("#txtWidth").val("0");
						$("#txtWidth").attr("disabled", true);
						
					}
					
				});
				$("#required_h").change(function(){
					
					if($(this).attr("checked")=="checked"){
						$(this).attr("checked",true);
						$("#txtHeight").attr("disabled", false);
						
					}
					else{
						$(this).attr("checked",false);
						$("#txtHeight").val("0");
						$("#txtHeight").attr("disabled", true);
					}
					
				});
				
				$("#required_h").hide();
				$("#required_w").hide();
				
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
							if($.query.get("fieldtype")=="28"||$.query.get("fieldtype")=="2"||$.query.get("fieldtype")=="-1"){
								fwidth ="-1";
								fheight = "-1";
							}
							
							if($.query.get("height")=="-1"){
								fheight = "-1";
							}
							
							var showlabel = "false";
							
							if($.query.get("datatype").toUpperCase() == "BLOB" ||$.query.get("datatype").toUpperCase() == "EXTFILE"){
								
								if($("#required_w").attr("checked")=="checked"){
									fwidth = $("#txtWidth").val();
								}
								else{
									fwidth = "0";
								}
								if($("#required_h").attr("checked")=="checked"){
									fheight = $("#txtHeight").val();
								}
								else{
									fheight = "0";
								}
							}
							
							if(parseInt(fwidth,10)<0){
								fwidth = "-1";
							}
							if(parseInt(fheight,10)<0){
								fheight = "-1";
							}
							
							if($("#cbrequired").attr("checked")=="checked"){
								showlabel = "true";
							}
							var fieldData = {
								fieldtype: $.query.get("fieldtype"),
								datatype:$.query.get("datatype"),
								code:$.query.get("code"),
								name: $("#txtName").val(),
								showlabel:showlabel,
								width: fwidth,
								height:fheight
							};
							window.parent.listfieldWindowClose(fieldData,isDel);
						}
					}
				});
				//赋值
				$("#txtName").val($.query.get("name"));
				$("#txtWidth").val($.query.get("width"));
				$("#txtHeight").val($.query.get("height"));
				
				if($.query.get("showlabel") == "true"){
					$("#cbrequired").attr("checked","checked");
				}
				
				
				if($.query.get("width")=="-1"){
					$("#fwidth").empty().remove();
				}
				if($.query.get("height")=="-1"){
					$("#fheight").empty().remove();
				}
				
				if($.query.get("datatype").toUpperCase() == "BLOB" ||$.query.get("datatype").toUpperCase() == "EXTFILE"){
					
					$("#required_h").show();
					$("#required_w").show();
					
					if(parseInt($.query.get("width"),10)>0){
						$("#required_w").attr("checked","checked");
					}
					else{
						$("#txtWidth").attr("disabled", true);
					}
					if(parseInt($.query.get("height"),10)>0){
						$("#required_h").attr("checked","checked");
					}
					else{
						$("#txtHeight").attr("disabled", true);
					}
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
						<td style="width:50px;"><i18n:message key="e5dom.custom.txt.name"/>:</td>
						<td><input class="validate[required,custom[onlyCharNumberChiness],maxSize[40]]" type="text" id="txtName" /></td>
					</tr>
					<tr id="fwidth">
						<td ><input type="checkbox" class="fl" id="required_w"/><label for="required_w"><i18n:message key="e5dom.custom.txt.width"/>:<label></td>
						<td><input class="validate[custom[onlyNumberSp],maxSize[4]]" type="text" id="txtWidth" class="w30" />px</td>
					</tr>
					<tr id="fheight">
						<td><input type="checkbox" class="fl" id="required_h"/><label for="required_h"><i18n:message key="e5dom.custom.txt.height"/>:</label></td>
						<td><input class="validate[custom[onlyNumberSp],maxSize[4]]" type="text" id="txtHeight" class="w30" />px</td>
					</tr>
					<tr>
						<td colspan="2"><input type="checkbox" class="fl" id="cbrequired"/><label for="cbrequired" class="fl ml5"><i18n:message key="e5dom.custom.txt.isshowlabel"/></label></td>
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