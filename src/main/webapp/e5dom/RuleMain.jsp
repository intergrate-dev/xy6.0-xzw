<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5dom.Author' />" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link rel="stylesheet" rev="stylesheet" href="css/dom.css" type="text/css"/>
		<link rel="stylesheet" rev="stylesheet" href="css/rule.css" type="text/css"/>
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var i18nInfo = {
				newRule:"<i18n:message key="e5dom.Rule.CreateRule"/>",
				modifyRule:"<i18n:message key="e5dom.Rule.ModifyRule"/>",
				deleteRule:"<i18n:message key="e5dom.Rule.DeleteRule"/>",
				confirm1:"<i18n:message key="e5dom.Rule.Confirm1"/>",
				confirm2:"<i18n:message key="e5dom.Rule.Confirm2"/>",

				createOK:"<i18n:message key="e5dom.Rule.CreateOK"/>",
				createNo:"<i18n:message key="e5dom.Rule.CreateNo"/>",
				modifyOK:"<i18n:message key="e5dom.Rule.ModifyOK"/>",
				modifyNo:"<i18n:message key="e5dom.Rule.ModifyNo"/>",
				deleteOK:"<i18n:message key="e5dom.Rule.DeleteOK"/>",
				deleteNo:"<i18n:message key="e5dom.Rule.DeleteNo"/>",
				RightClick:"<i18n:message key="e5dom.Rule.RightClick"/>"

			}
		</script>
		<script type="text/javascript" src="script/domv3.js"></script>
		<script type="text/javascript" src="script/rule.js"></script>
		<script type="text/javascript">
			$(document).ready(function(){
				//创建文档类型字段验证
				$("#RuleForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#cancelBtn").attr("disabled","true");
							var ruleID = $("#ruleID").val();
							if(ruleID==null||ruleID==""){
								$("#createBtn").attr("disabled","true");
								page.handlers.SubmitCreateRule();
							}else{
								$("#modifyBtn").attr("disabled","true");
								page.handlers.SubmitModifyRule()
							}
						}
					}
				})
			})
		</script>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th><i18n:message key="e5dom.Rule.title"/></th>
					<td><i18n:message key="e5dom.DocType.PleaseChooseDocType" /></td>
					<td><select id="DocTypeList"></select></td>
					<td><span>|</span></td>
					<td><a href="#" id="CreateRule"><i18n:message key="e5dom.Rule.CreateRule" /></a></td>
				</tr>
			</table>
		</div>
		<div class="mainBodyWrap">
			<div id="LeftDiv">
				<table id="tab1" cellpadding="5" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.Rule.RuleList" /></caption>
				</table>
			</div>
			<div id="RightDiv">
				<div id="RuleProps">
					<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.Rule.RuleProps" /></caption>
						<tr>
							<th width="20%"><i18n:message key="e5dom.Rule.RuleName" /></th>
							<td width="80%"><span id="RuleNameSpan"></span></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Rule.RuleDesc" /></th>
							<td><span id="RuleDescSpan"></span></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Rule.RuleClass" /></th>
							<td><span id="RuleClassNameSpan"></span></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Rule.RuleMethod" /></th>
							<td><span id="RuleMethodSpan"></span></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Rule.RuleArguments" /></th>
							<td><span id="RuleArgumentsSpan"></span></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Rule.RuleFolders" /></th>
							<td><span id="RuleFoldersSpan"></span></td>
						</tr>
					</table>
				</div>

				<div id="errorDiv" style="color:red;font-weight:bold"></div>
				<div id="FormDiv">
					<form id="RuleForm" name="RuleForm" method="post" action="">
						<input type="hidden" id="ruleID" name="ruleID">
						<input type="hidden" id="docTypeID" name="docTypeID">
						<table cellpadding="0" cellspacing="0" class="table">
							<caption><i18n:message key="e5dom.Rule.CreateRule" /></caption>
							<tr>
								<th width="20%"><i18n:message key="e5dom.Rule.RuleName" /></th>
								<td width="80%"><input size="60" type="text" id="ruleName" name="ruleName" class="validate[required,maxSize[30]]"></td>
							</tr>
							<tr>
								<th><i18n:message key="e5dom.Rule.RuleDesc" /></th>
								<td><input size="100" type="text" id="ruleDesc" name="ruleDesc"></td>
							</tr>
							<tr>
								<th><i18n:message key="e5dom.Rule.RuleClass" /></th>
								<td><input size="60" type="text" id="ruleClassName" name="ruleClassName" ></td>
							</tr>
							<tr>
								<th><i18n:message key="e5dom.Rule.RuleMethod" /></th>
								<td><input size="60" type="text" id="ruleMethod" name="ruleMethod" class="validate[custom[commonchar]]"></td>
							</tr>
							<tr>
								<th><i18n:message key="e5dom.Rule.RuleArguments" /></th>
								<td><input size="100" TYPE="text" id="ruleArguments" NAME="ruleArguments"></td>
							</tr>
							<tr>
								<td colspan="2" align="center">
								<input id="createBtn" type="submit" class="button" value="<i18n:message key="e5dom.Rule.CreateRule"/>">
								<input id="modifyBtn" type="submit" class="button" value="<i18n:message key="e5dom.Rule.ModifyRule"/>">
								<input id="cancelBtn" type="button" class="button" onclick="page.handlers.resetRuleForm();" value="<i18n:message key="e5dom.Rule.Reset"/>"></td>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
