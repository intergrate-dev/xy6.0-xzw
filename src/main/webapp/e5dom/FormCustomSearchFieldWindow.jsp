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
                switch ($.query.get("fieldtype")) {
                    case "-1":
                        break;
                    case "1":
                    case "5":
                    case "16":
                    case "17":
                        defaultWidth = "100";
                        break;
                    case "7":
                    case "13":
                        defaultHeight = "50";
                        defaultWidth = "100";
                        break;
                    case "21":
                        defaultHeight = "50";
                        defaultWidth = "133";
                        break;
                    case "27":
                        defaultWidth = "266";
                        break;
                }
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
						
					
							var showAll = "false";
							if($("#cbShowAll").attr("checked")){
								showAll = "true";
							}
							var multiple = "false";
							if(!$("#cbAllowMultiple").prop("disabled")&&$("#cbAllowMultiple").prop("checked")){
								multiple = "true";
							}
							var showModel = $("#showModel").val();
							var range = "false";
							if($("#cbAllowRange").attr("checked")){
								range = "true";
							}
							var fieldData = {
								fieldtype: $.query.get("fieldtype"),
								doctypeid:$.query.get("doctypeid"),
								code:$.query.get("code"),
								name: $("#txtName").val(),
								datatype:$.query.get("datatype"),
								range:range,
								showall:showAll,
								multiple:multiple,
								showmodel:showModel
							};
							window.parent.listfieldWindowClose(fieldData,isDel);
						}
					}
				});
				//赋值
				$("#txtName").val($.query.get("name"));
			
                $("#fShowAll").css("display","none");
                $("#fAllowMultiple").css("display","none");
                $("#fShowModel").css("display","none");
				$("#fAllowRange").css("display","none");
				if($.query.get("fieldtype")=="1" || $.query.get("fieldtype")=="5" || $.query.get("fieldtype")=="7" ||
                        $.query.get("fieldtype")=="13" || $.query.get("fieldtype")=="11" || $.query.get("fieldtype")=="12" || 
                        $.query.get("fieldtype")=="14" || $.query.get("fieldtype")=="15" || $.query.get("fieldtype")=="16" || $.query.get("fieldtype")=="17"){
                    if($.query.get("showall")=="true"){
                        $("#cbShowAll").attr("checked","checked");
                    }
                    if($.query.get("multiple")=="true"){
                        $("#cbAllowMultiple").attr("checked","checked");
                    }
                    $("#showModel").val($.query.get("showmodel"));
					$("#fShowAll").css("display","");
					$("#fAllowMultiple").css("display","");
					$("#fShowModel").css("display","");
                    allowMultipleChanged();
				}else if($.query.get("fieldtype")=="6" || $.query.get("fieldtype")=="33" || $.query.get("fieldtype")=="29" ||
                        $.query.get("fieldtype")=="34" || $.query.get("fieldtype")=="30" || $.query.get("fieldtype")=="35" || 
                        $.query.get("fieldtype")=="36" || $.query.get("fieldtype")=="37"){
                    $("#fAllowMultiple").css("display","");
                    if($.query.get("multiple")=="true"){
                        $("#cbAllowMultiple").attr("checked","checked");

                    }
                }
				
				if($.query.get("fieldtype")=="0" && 
					($.query.get("datatype")=="integer" || $.query.get("datatype")=="long" 
						|| $.query.get("datatype")=="float" || $.query.get("datatype")=="double")){
					if($.query.get("range")=="true"){
                        $("#cbAllowRange").attr("checked","checked");
                    }
					$("#fAllowRange").css("display","");
				}
				
				$("#cbAllowMultiple").click(function(){
                    if($("#fShowModel").css("display")!="none"){
                        allowMultipleChanged();
                    }
                    
                    $("#cbShowAll").prop("disabled",$(this).prop("checked"));
				});
                $("#showModel").change(function(){
                    showModelChanged();
                });
                 $("#cbShowAll").prop("disabled",$("#cbAllowMultiple").prop("checked"));

                
			});
			function allowMultipleChanged(){
				var v = $("#showModel").val();
				if($("#cbAllowMultiple").attr("checked")){
					$("#showModel").html("<option value=\"select\">下拉框</option><option value=\"checkbox\">复选框</option>");
					if(v=="radio") {
						v="select";
					}
				}else{
					$("#showModel").html("<option value=\"select\">下拉框</option><option value=\"radio\">单选框</option>");
					if(v=="checkbox"){
						v="select";
					} 
				}
				 $("#showModel").val(v)
                 showModelChanged();
			}
            function showModelChanged(){
               
            }
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
						<td><input class="validate[required,custom[onlyCharNumberChiness],maxSize[40]]" type="text" id="txtName" /></td>
					</tr>
				
					<tr id="fShowAll">
						<td colspan="2"><input type="checkbox" id="cbShowAll" /><label for="cbShowAll">显示"全部"</label></td>
					</tr>
					<tr id="fAllowMultiple">
						<td colspan="2"><input type="checkbox" id="cbAllowMultiple" /><label for="cbAllowMultiple">允许多选</label></td>
					</tr>
					<tr id="fAllowRange">
						<td colspan="2"><input type="checkbox" id="cbAllowRange" /><label for="cbAllowRange">范围查询</label></td>
					</tr>
					<tr id="fShowModel">
						<td>展现形式:</td>
						<td>
							<select id="showModel">
								<option value="select">下拉框</option>
								<option value="radio">单选框</option>
								<option value="checkbox">复选框</option>
							</select>
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