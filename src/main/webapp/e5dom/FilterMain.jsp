<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5dom.Author' />" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link rel="stylesheet" rev="stylesheet" href="css/dom.css" type="text/css" media="all" />
		<link rel="stylesheet" rev="stylesheet" href="css/filter.css" type="text/css" media="all" />
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var i18nInfo = {
				newFilter:"<i18n:message key="e5dom.Filter.CreateFilter"/>",
				modifyFilter:"<i18n:message key="e5dom.Filter.ModifyFilter"/>",
				deleteFilter:"<i18n:message key="e5dom.Filter.DeleteFilter"/>",
				confirm1:"<i18n:message key="e5dom.Filter.Confirm1"/>",
				confirm2:"<i18n:message key="e5dom.Filter.Confirm2"/>",

				createOK:"<i18n:message key="e5dom.Filter.CreateOK"/>",
				createNo:"<i18n:message key="e5dom.Filter.CreateNo"/>",
				modifyOK:"<i18n:message key="e5dom.Filter.ModifyOK"/>",
				modifyNo:"<i18n:message key="e5dom.Filter.ModifyNo"/>",
				deleteOK:"<i18n:message key="e5dom.Filter.DeleteOK"/>",
				deleteNo:"<i18n:message key="e5dom.Filter.DeleteNo"/>",
				RightClick:"<i18n:message key="e5dom.Filter.RightClick"/>"
			}
		</script>
		<script type="text/javascript" src="script/domv3.js"></script>
		<script type="text/javascript" src="script/filter.js"></script>
	</head>
<body>
	<div id="TopDiv" class="titleDiv">
		<table cellpadding="0" cellspacing="0">
			<tr>
				<th><i18n:message key="e5dom.Filter.title"/></th>
				<td><i18n:message key="e5dom.DocType.PleaseChooseDocType"/></td>
				<td><select id="DocTypeList"></select></td>
				<td><span>|</span></td>
				<td><a href="#" id="CreateFilter"><i18n:message key="e5dom.Filter.CreateFilter"/></a></td>
			</tr>
		</table>
	</div>
	<div class="mainBodyWrap">
		<div id="LeftDiv">
			<table id="tab1" cellpadding="5" cellspacing="0" class="table">
				<caption><i18n:message key="e5dom.Filter.FilterList"/></caption>
			</table>
		</div>
		<div id="RightDiv">
			<div id="FilterProps">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.Filter.FilterProperties"/></caption>
					<tr>
						<th width="20%"><i18n:message key="e5dom.Filter.FilterName"/></th>
						<td width="80%"><span id="FilterNameSpan"></span></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.Filter.FilterDesc"/></th>
						<td><span id="FilterDescSpan"></span></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.Filter.FilterFomula"/></th>
						<td><span id="FilterFormulaSpan"></span></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.Filter.FilterFolders"/></th>
						<td><span id="FilterFoldersSpan"></span></td>
					</tr>
				</table>
			</div>

			<div id="errorDiv" style="color:red;font-weight:bold;"></div>
			<div id="FormDiv">
				<form id="FilterForm" name="FilterForm" method="post" action="">
					<input type="hidden" id="filterID" name="filterID"/>
					<input type="hidden" id="docTypeID" name="docTypeID"/>
					<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.Filter.CreateFilter"/></caption>
						<tr>
							<th width="20%"><i18n:message key="e5dom.Filter.FilterName"/></th>
							<td><input size="50" TYPE="text" id="filterName" name="filterName" class="validate[required,maxSize[60]]"></td>
						</tr>
						<tr>
							<th><i18n:message key="e5dom.Filter.FilterDesc"/></th>
							<td><input size="100" TYPE="text" id="filterDesc" name="filterDesc"></td>
						</tr>

						<tr>
							<th><i18n:message key="e5dom.Filter.FilterFomula"/></td>
							<td><input size="100" TYPE="text" id="filterFormula" name="filterFormula"></th>
						</tr>
						<tr>
							<td colspan="2" align="center">
							<input id="createBtn" class="button" type="submit" value="<i18n:message key="e5dom.Filter.CreateFilter"/>"/>
							<input id="modifyBtn" class="button" type="submit" value="<i18n:message key="e5dom.Filter.ModifyFilter"/>"/>
							<input id="cancelBtn" type="button" class="button" onclick="page.handlers.resetFilterForm();" value="<i18n:message key="e5dom.Filter.Reset"/>"/>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(document).ready(function(){
		//创建文档类型字段验证
		$("#FilterForm").validationEngine({
			autoPositionUpdate:true,
			onValidationComplete:function(from,r){
				if(r){
					window.onbeforeunload=null;
					$("#cancelBtn").attr("disabled","true");
					var filterID = $("#filterID").val();
					if(filterID==null||filterID==""){
						$("#createBtn").attr("disabled","true");
						page.handlers.SubmitCreateFilter();
					}else{
						$("#modifyBtn").attr("disabled","true");
						page.handlers.SubmitModifyFilter();
					}
				}
			}
		});
	});
</script>
</html>