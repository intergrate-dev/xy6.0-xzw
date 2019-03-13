<!DOCTYPE html>
<html>
<head>
	<title>互动禁言IP</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
  <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
  <link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
  <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
  <script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
  <script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
  <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
  <script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script> 
  
  <link href="../script/jquery-textext/src/css/textext.core.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.autocomplete.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.clear.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.focus.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.prompt.css" rel="stylesheet">
  <link href="../script/jquery-textext/src/css/textext.plugin.tags.css" rel="stylesheet">

  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.core.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.ajax.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.autocomplete.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.clear.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.filter.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.focus.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.prompt.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.suggestions.js"></script>
  <script type="text/javascript" src="../script/jquery-textext/src/js/textext.plugin.tags.js"></script>
  
  
  <link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css">
  <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
  
  <script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
  <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="../script/picupload/upload_api.js"></script>
  
  <script type="text/javascript" src="./js/memberSelectDis.js"></script>
  <script type="text/javascript" src="./js/Shutup.js"></script>

	<script type="text/javascript">
		if (window.innerWidth)
        winWidth = window.innerWidth;
        else if ((document.body) && (document.body.clientWidth))
        winWidth = document.body.clientWidth;
        // 获取窗口高度
        if (window.innerHeight)
        winHeight = window.innerHeight;
        else if ((document.body) && (document.body.clientHeight))
        winHeight = document.body.clientHeight;
        // 通过深入 Document 内部对 body 进行检测，获取窗口大小
        if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth)
        {
        winHeight = document.documentElement.clientHeight;
        winWidth = document.documentElement.clientWidth;
	}
	var type =${param.type};
		function beforeSubmit() {
			var val = document.getElementById("userName").value;
			if (!val.trim()) {
				alert("请填写");
				return false;
			}
		}
	</script>
</head>
<body >
	<form id="form" method="post" action="Shutup.do" onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${param.UUID}"/>
		<input type="hidden" id="siteID" name="siteID" value="${param.siteID}"/>
		<input type="hidden" id="type" name="type" value="${param.type}"/>
		<input type="hidden" id="userID" name="userID" value=""/>
		<div class="mainBodyWrap">
			<table class="tablecontent">
			<tbody customwidth="617" customheight="109">
			<tr>
				<td colspan="2" class="ui-droppable">
					<span id="SPAN_SYS_AUTHORS" class="custform-span">
					<label id="LABEL_SYS_AUTHORS" class="custform-label custform-label-require">禁止的IP</label>
					<div class="custform-from-wrap" id="DIV_SYS_AUTHORS">
						<input type="text" id="userName" name="userName" value="" class="custform-input validate[maxSize[255],required]" style="width:300px;"/>  
						<input class="btn" style="margin-top:-3px;font-size:12px;color:#0c0c0c;" id="btnUser" type="button" value="选择"/>
						<span class="custform-postfix"></span>
					</div> 
					</span>
				</td>
			</tr>
			<tr>
			<td colspan="1" class="ui-droppable">
				<span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
					<input class="button btn" id="btnFormSave" type="submit" value="保存"/>
				</span>
				<span class="custform-aftertxt ui-draggable"/> 
			</td>
    		<td class="ui-droppable">
      			<span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
        			<input class="button btn" id="btnFormCancel" type="button" onclick="window.close()" value="取消"/>
      			</span>
    		</td>
  			</tr>  
			</tbody>
			</table>
		</div>
	</form>
</body>
</html>
