<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5note" changeResponseLocale="false" />
<html>
	<head>
		<title><i18n:message key="e5note.createnote" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css" />
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css" />
		<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<script type="text/javascript" src="note.js"></script>
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<style type="text/css">
			.table{
				width:auto;
				margin: 0 auto;
			}
		</style>	
	</head>
	<body onload="setnoteValue();setFocus();">
		<div class="mainBodyWrap">
			<form action="CreateNote.do" name="form1" id="form1" target="procFrame" method="POST">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5note.createnote"/></caption>
					<tr>
						<td><span class="field-required">*</span><i18n:message key="e5note.notetype" /></td>
						<td>
							<input type="radio" class="radio" name="notetype1" onclick="setnoteValue();" id="notetype1" value="0">
							<label for="notetype1"><i18n:message key="e5note.notetype1" /></label>
							<input type="radio" class="radio" name="notetype1" onclick="setnoteValue2();" id="notetype2" value="1">
							<label for="notetype2"><i18n:message key="e5note.notetype2" /></label>
						</td>
					</tr>
					<tr>
						<td><span class="field-required">*</span><i18n:message key="e5note.topic" />	</td>
						<td>
							<input type="text" id="topic" name="topic" size="60" class="validate[required,maxSize[255]]">
						</td>
					</tr>
					<tr>
						<td colspan="2"><textarea cols="62" rows="20" style="overflow:auto" name="content" id="content"></textarea></td>
					</tr>
					<tr>
						<td colspan="2" class="alignCenter">
							<input type="hidden" value="0" name="noteType" id="noteType">
							<input type="hidden" value="" name="receivers" onclick="">
							<input id="saveBtn" class="button" type="submit" value="<i18n:message key="e5note.noteRelease"/>">
							<input id="resetBtn" class="button" type="button" value="<i18n:message key="e5note.setnull"/>" onclick="setnull();">
							<input id="cancelBtn" class="button" type="button" value="<i18n:message key="e5note.cancel"/>" onclick="complete1();">
						</td>
					</tr>
				</table>
			</form>
		</div>
		<iframe name="procFrame" width="0" height="0" style="display:none;"></iframe>
		<script type="text/javascript">
			$(document).ready(function(){
				//字段验证
				$("#form1").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#saveBtn").attr("disabled","true");
							$("#resetBtn").attr("disabled","true");
							$("#cancelBtn").attr("disabled","true");
							CreateNote();
						}
					}
				});
			});
		</script>
	</body>
</html>

