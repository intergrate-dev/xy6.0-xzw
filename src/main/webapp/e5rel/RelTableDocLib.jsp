<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5rel" changeResponseLocale="false"/>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5rel.Author' />" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link rel="stylesheet" type="text/css" href="../e5dom/css/dom.css"/>
		<link rel="stylesheet" type="text/css" href="css/reldoclib.css"/>
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">
		<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript">
			var i18nInfo = {
				deleteRelation :"<i18n:message key="e5rel.RelTable.deleteRelation"/>",
				confirmDeleteRelation : "<i18n:message key="e5rel.RelTable.confirmDeleteRelation"/>",
				createTableOk :"<i18n:message key="e5rel.RelTable.CreateTableOK"/>",
				createTableNo :"<i18n:message key="e5rel.RelTable.CreateTableNo"/>",
				DeleteTableOK :"<i18n:message key="e5rel.RelTable.DeleteTableOK"/>",
				DeleteTableFailed : "<i18n:message key="e5rel.RelTable.DeleteTableFailed"/>",
				addNewRelation : "<i18n:message key="e5rel.RelTable.CreateRelTableDocLib"/>",
				CreateRelationOK : "<i18n:message key="e5rel.RelTable.CreateRelationOK"/>",
				CreateRelationFailed : "<i18n:message key="e5rel.RelTable.CreateRelationFailed"/>",
				RelationExisted : "<i18n:message key="e5rel.RelTable.RelationExisted"/>",
				NoCategoryField : "<i18n:message key="e5rel.RelTable.NoCategoryField"/>",
				DataTypeMismatch : "<i18n:message key="e5rel.RelTable.DataTypeMismatch"/>",
				FieldsRelationAlreadyExisted : "<i18n:message key="e5rel.RelTable.FieldsRelationAlreadyExisted"/>",
				CatFieldMustVarchar : "<i18n:message key="e5rel.RelTable.CatFieldMustVarchar"/>",
				CreateDocLibRelTable : "<i18n:message key="e5rel.RelTable.CreateDocLibRelTable"/>",
				ondblclickTitle : "<i18n:message key="e5rel.RelTable.ondblclick.title"/>"
			}
		</script>
		<script type="text/javascript" src="../e5dom/script/domv3.js"></script>
		<script type="text/javascript" src="script/reldoclib.js"></script>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th><i18n:message key="e5rel.RelTableDocLib.title"/></th>
					<td>
						<a id="CreateRelTableDocLib" href="#"><i18n:message key="e5rel.RelTable.CreateRelTableDocLib"/></a>
					</td>
				</tr>
			</table>
		</div>
		<div class="mainBodyWrap">
			<div id="RelationList">
				<table id="testTbl" cellpadding="0" cellspacing="0" class="table">
					<tr>
						<th><i18n:message key="e5rel.RelTable.RelTableListName"/></th>
						<th><i18n:message key="e5rel.RelTable.DocLibName"/></th>
						<th><i18n:message key="e5rel.RelTable.CatType"/></th>
					</tr>
				</table>
			</div>
			<div id="CreateRelationDIV" style="width:95%">
				<div id="step1" style="width:100%;">
					<table class="table">
						<tr><th colspan="4"><i18n:message key="e5rel.RelTable.Step1"/></th></tr>
						<tr>
							<td width="10%"><i18n:message key="e5rel.RelTable.ToCatType"/></td>
							<td width="40%"><select id="CatTypeList" style="width:80%"></select></td>
							<td width="10%"></td>
							<td width="40%"></td>
						</tr>
					</table>
				</div>
				<div id="step2" style="width:100%;">
					<table class="table">
						<tr><th colspan="4"><i18n:message key="e5rel.RelTable.Step2"/></th></tr>
						<tr>
							<td width="10%"><i18n:message key="e5rel.RelTable.SelectDocLib"/></td>
							<td width="40%"><select id="DocLibList" style="width:80%"></select></td>
							<td width="10%"><i18n:message key="e5rel.RelTable.InsertCategoryField"/></td>
							<td width="40%"><select id="CategoryFieldDIV" style="width:80%"></select></td>
						</tr>
					</table>
				</div>
				<div id="step3" style="width:100%;">
					<table class="table">
						<tr><th colspan="4"><i18n:message key="e5rel.RelTable.Step3"/></th></tr>
						<tr>
							<td width="10%"><i18n:message key="e5rel.RelTable.SelectRelTable"/></td>
							<td width="40%"><select id="RelTableList" style="width:80%"></select></td>
							<td width="10%"></td>
							<td width="40%"></td>
						</tr>
					</table>
				</div>
				<div id="step4" style="width:100%;">
					<table class="table">
						<tr><th colspan="2"><i18n:message key="e5rel.RelTable.Step4"/></th></tr>
						<tr>
							<td style="vertical-align:top;width:50%;"><i18n:message key="e5rel.RelTable.DocLibFields"/>:<br/>
								<select size="8" id="DocLibFieldsList" style="width:100%"></select>
							</td>
							<td style="vertical-align:top;width:50%;"><i18n:message key="e5rel.RelTable.RelTableFields"/>:<br/>
								<select size="8" id="RelTableFieldsList" style="width:100%"></select>
							</td>
						</tr>
						<tr id="fieldsBtn" style="display:none;">
							<td>
								<span id="newFlag" style="display:none"></span>
								<!--
								<span style="padding:2px 0px" class="relationHeader">
									<label for="ignoreFlag">
									<input value="1" type="checkbox" name="ignoreFlag" id="ignoreFlag">
									<i18n:message key="e5rel.RelTable.IgnoreCategoryField"/>
									</label>
								</span>
								-->
							</td>
							<td>
								<input class="button" type="button"  id="ConfirmChozenFieldsBtn"
									value="<i18n:message key="e5rel.RelTable.InsertRelation"/>"
									title="<i18n:message key="e5rel.RelTable.InsertRelation.hint"/>"/>
							</td>
						</tr>
					</table>
				</div>
				<div id="chozenDIV">
					<table class="table">
						<tr>
						</tr>
						<tr><th><i18n:message key="e5rel.RelTable.BuiltRelation"/></th></tr>
						<tr>
							<td><div id="RelationFieldsDIV"></div></td>
						</tr>
						<tr>
							<td align="center">
								<div id="ConfirmRelationsDIV" style="width:100%;">
								<span id="ConfirmRelationsSpan">
									<input class="button" type="button" id="ConfirmRelationsBtn"
										value="<i18n:message key="e5rel.RelTable.ConfirmRelations"/>"
										title="<i18n:message key="e5rel.RelTable.ConfirmRelations.hint"/>"/>
								</span>
								&nbsp;
								<input class="button" type="button" id="RelationsCancel" value="<i18n:message key="e5rel.RelTable.Cancel"/>"/>
								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</body>
</html>