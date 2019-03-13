<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>会员积分参数设置</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../xy/article/script/json2.js"></script>
	<style>
		.tablecontent{
			font-size:15px;
		}
	</style>
	<script type="text/javascript">
		function beforeSubmit() {
			var config = {			
				"guardLine" : $("#guardLine").val()
			}
			config = JSON.stringify(config);
			
			$("#scoreConfig").val(config);
		}
		function doInit(){
			var config = $("#scoreConfig").val();
			if (!config) return;
			
			config = eval("(" + config + ")");

			$("#guardLine").val(config.guardLine);
		}
	</script>
</head>
<body onload="doInit()">
	<form id="form" method="post" action="SaveScoreConfig.do" onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}"/>
		<input type="hidden" id="siteID" name="siteID" value="${siteID}"/>
		<input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}"/>
		<input type="hidden" id="scoreConfig" name="scoreConfig" value="${scoreConfig}"/>
		<table class="tablecontent">
			<tr><td colspan="2">
				<label class="custform-label">积分警戒线</label>  
				<div class="custform-from-wrap">
					<input type="text" id="guardLine" name="guardLine" placeholder="积分警戒线"/>
				</div> 
			</td></tr>			
			<tr><td>
				  <span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
					<input class="button btn" id="btnFormSave" type="submit" value="保存"/>
				  </span>
				  <span class="custform-aftertxt ui-draggable"/> 
				</td>
				<td>
				  <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
					<input class="button btn" id="btnFormCancel" type="button" onclick="window.close()" value="取消"/>
				  </span>
				</td>
			</tr>  
		</table>
	</form>
</body>
</html>
