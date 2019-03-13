<!DOCTYPE html>
<html>
<head>
	<title>切换域名</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
</head>
<body>
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form id="form" method="post" target="iframe" action="SwitchSubmit.do">
		<input type="hidden" id="DocLibID" name="DocLibID" value="${param.DocLibID}"/>
		<input type="hidden" id="DocIDs" name="DocIDs" value="${param.DocIDs}"/>
		<input type="hidden" id="siteID" name="siteID" value="${param.siteID}"/>
		<table class="tablecontent" style="font-size:14px;">
			<tr><td colspan="2">
				注意：域名的改变会影响到相关子目录的Url。对旧稿件若需使用新域名，请重新发布。
			</td></tr>
			<tr><td colspan="2">当前域名：${url}</td></tr>
			<tr>
				<td colspan="2">新域名：
				<input type="text" id="dir_url" name="dir_url" value="" 
					class="custform-input validate[maxSize[255],required,funcCall[checkDuplicate],custom[url]]"
					style="width:300px;"/>  
				</td>
			</tr>
			<tr>
				<td><input class="button btn" id="btnFormSave" type="submit" value="保存"/></td>
				<td><input class="button btn" id="btnFormCancel" type="button" value="取消"/></td>
			</tr>
		</table>
	</form>
</body>
</html>
<script>
	//查是否重名时，增加站点条件
	e5_form._duplicateUrl = function(field) {
		var theURL = "xy/Duplicate.do"
			+ "?DocLibID=" + $("#DocLibID").val()
			+ "&DocIDs=" + $("#DocIDs").val()
			+ "&field=" + field.attr("id")
			+ "&value=" + e5_form.encode(field.val())
			+ "&siteID=" + $("#siteID").val()
			+ "&parentID=" + 0;
		return theURL;
	}
	var dir_form = {
		init : function() {
			$("#btnFormCancel").click(dir_form.btnCancel);
		},
		btnCancel : function() {
			parent.dir_menu.dialog.close();	
		}
	}
	$(function(){
		dir_form.init();
	});
</script>
