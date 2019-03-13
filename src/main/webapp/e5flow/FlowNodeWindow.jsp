<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<title>Flow View</title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript">
		$(function () {
			//$("#txtNodeName").focus();
			 //验证
			$("#flowform").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						var nodeData = {
							id: $("#J_id").val(),
							name: $("#txtNodeName").val(),
							note: "",
							doing: $("#txtNodeing").val(),
							dopre: $("#txtNodePre").val(),
							done: $("#txtNodeDonw").val()
						};
						//验证是否有重复节点
						if(!window.parent.hasSameNode(nodeData)){
							window.parent.e5NodeWindowClose(nodeData);
						}
						else{
							alert("<i18n:message key="flow.visual.node.samenode"/>");
							return;
						}
					}
				},
				//// OPENNING BOX POSITION, IMPLEMENTED: topLeft, topRight, bottomLeft, centerRight, bottomRight
				promptPosition:"centerRight"
			});
			if ($.query.get("id") != null ) {
				$("#J_id").val($.query.get("id"));
				$("#txtNodeName").val($.query.get("name"));
				$("#txtNodeing").val($.query.get("doing"));
				$("#txtNodePre").val($.query.get("dopre"));
				$("#txtNodeDonw").val($.query.get("done"));
			}
			$("#btnNodeSave").click(function () {
				 $("#flowform").submit();
			});
			$("#btnNodeCancle").click(function () {
				 window.parent.e5NodeWindowClose(null);
			});
			document.onkeydown = function (e) { 
				var theEvent = window.event || e; 
				var code = theEvent.keyCode || theEvent.which; 
				if (code == 13) { 
					$("#btnNodeSave").click(); 
				} 
			}
			$("#txtNodeName").focus();
		});
	</script>
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-ui/jquery-ui.custom.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/e5-flow.css"/>
</head>
<body>
	<form name="flowform" id="flowform" method="post" action="">
		<div id="nodewindows">
			<table class="table" cellpadding="0" cellspacing="0">
				<tr>
					<td class="w90"><span class="field-required">*</span><i18n:message key="flow.visual.node.window.nodename"/>:</td>
					<td><input id="txtNodeName" class="validate[required,custom[onlyCharNumberChiness],maxSize[40]]" type="text" value="" />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span>
					</td>
				</tr>
				<tr>
					<td><i18n:message key="flow.visual.node.window.predo"/>:</td>
					<td><input id="txtNodePre" class="validate[custom[onlyCharNumberChiness],maxSize[40]]" type="text" value="" />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span>
					</td>
				</tr>
				<tr>
					<td><i18n:message key="flow.visual.node.window.doing"/>:</td>
					<td><input id="txtNodeing" class="validate[custom[onlyCharNumberChiness],maxSize[40]]"  type="text" value="" />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span></td>
				</tr>
				<tr>
					<td><i18n:message key="flow.visual.node.window.done"/>:</td>
					<td><input id="txtNodeDonw" class="validate[custom[onlyCharNumberChiness],maxSize[40]]"  type="text" value=""  />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span></td>
				</tr>
			</table>
			<div class="alignCenter m">
				<input id="btnNodeSave" class="button" type="button" value="<i18n:message key="flow.visual.node.window.save"/>" />
				<input id="btnNodeCancle"  class="button"  type="button" value="<i18n:message key="flow.visual.node.window.cancle"/>" />
			</div>
			<input type="hidden" id="J_id" value="" />
		</div>
	</form>
</body>
</html>
