<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="e5dom.DocLib.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5dom.Author' />" />
		<meta name="description" content="" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link type="text/css" rel="stylesheet" href="css/dom.css"/>
		<link type="text/css" rel="stylesheet" href="css/doclib.css"/>
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css" />
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script language="javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script language="javascript">
			var i18nInfo = {
				deleteDocLib :"<i18n:message key="e5dom.DocLib.DeleteDocLib"/>",
				updateDocLib : "<i18n:message key="e5dom.DocLib.UpdateDocLib"/>",
				confirmDeleteDocLib : "<i18n:message key="e5dom.DocLib.confirmDeleteDocLib"/>",
				confirmDeleteDocLibEnd :"<i18n:message key="e5dom.DocLib.confirmDeleteDocLibEnd"/>",

				deleteOK : "<i18n:message key="e5dom.DocLib.DeleteOK"/>",
				deleteNO : "<i18n:message key="e5dom.DocLib.DeleteNO"/>",
				RightClick : "<i18n:message key="e5dom.DocLib.RightClick"/>"
			}
		</script>
		<script language="javascript" src="script/domv3.js"></script>
		<script language="javascript" src="script/doclib.js"></script>
		<script type="text/javascript">
			$(document).ready(function(){
				//创建文档库
				$("#DocLibProps").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#CreateDocLibBtn").attr("disabled","true");
							page.handlers.CreateDocLib();
						}
					}
				});
				//修改文档库
				$("#UpdateDocLibForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#UpdateDocLibBtnSave").attr("disabled","true");
							$("#UpdateDocLibBtnCel").attr("disabled","true");
							page.handlers.submitUpdateDocLib();
						}
					}
				});
			});
		</script>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<th><i18n:message key="e5dom.DocLib.title"/></th>
					<td><i18n:message key="e5dom.DocType.PleaseChooseDocType"/></td>
					<td><select id="DocTypeList"></select></td>
					<td><span>|</span></td>
					<td><i18n:message key="e5dom.DocLib.PleaseChooseDataSource"/></td>
					<td><select id="DSConfigList"></select></td>
					<td><span>|</span></td>
					<td><a href="#" id="createDocLib"><i18n:message key="e5dom.DocLib.createDocLib"/></a></td>
					<td><span>|</span></td>
					<td><span id="prompt"></span></td>
				</tr>
			</table>
		</div>
		<div class="mainBodyWrap">
			<div id="LeftDiv">
				<table id="testTbl" cellpadding="5" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocLib.DocLibList"/></caption>
				</table>
			</div>
			<div id="RightDiv">
				<table id="testTbl2" cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocLib.DocLibPropts"/></caption>
					<tr>
						<th width="100">ID</th>
						<td id="docLibID_td"></td>
					</tr>
					<tr>
						<th width="100"><i18n:message key="e5dom.DocLib.DocLibName"/></th>
						<td id="docLibName_td"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.DocLibDescription"/></th>
						<td id="description_td"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.libTable"/></th>
						<td id="docLibTable_td"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.attachDevName"/></th>
						<td id="attachDevName_td"></td>
					</tr>
					<tr style="display:none;">
						<th><i18n:message key="e5dom.DocLib.libDB"/></th>
						<td id="libDB_td"></td>

					</tr>
					<tr style="display:none;">
						<th><i18n:message key="e5dom.DocLib.libServer"/></th>
						<td id="libServer_td"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.FlowTable"/></th>
						<td id="libTable_td"></td>
					</tr>
				</table>
			</div>
			<div id="UpdateDocLibDIV">
				<div id="errorDiv1" style="color:red;font-weight:bold"></div>
				<form id="UpdateDocLibForm" name="UpdateDocLibForm" method="post" action="">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocLib.UpdateDocLib"/></caption>
					<tr>
						<th width="100"><i18n:message key="e5dom.DocLib.DocLibName"/></th>
						<td><input type="text" id="docLibName" name="docLibName" class="validate[required]"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.DocLibDescription"/></th>
						<td><input type="text" id="docLibDesc" name="docLibDesc"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5dom.DocLib.attachDevName"/></th>
						<td>
						<select id="attachDevName1" name="attachDevName">
						<option value="">------</option>
						</select>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center"><input class="button" type="submit" id="UpdateDocLibBtnSave" value="<i18n:message key="e5dom.DocLib.updateDocLib"/>">&nbsp;<input type="reset" class="button" id="UpdateDocLibBtnCel" value="<i18n:message key="e5dom.DocLib.Reset"/>"></td>
					</tr>
				</table>
				</form>
			</div>
			<div id="CreateDocLibDiv">
				<form id="DocLibProps" method="post" action="">
					<div id="newDocLibProps">
						<table cellpadding="0" cellspacing="0" class="table">
							<caption><i18n:message key="e5dom.DocLib.CreateDocLibStep1"/></caption>
							<tr>
								<th width="100" align="right"><i18n:message key="e5dom.DocLib.DocLibName"/></th>
								<td colspan="2"><input  id="newDocLibName" name="newDocLibName" class="validate[required]" TYPE="text" maxlength="80"></td>

							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.DocLib.DocLibTable"/></th>
								<td width="100"><input  id="docLibTable" name="docLibTable" TYPE="text" maxlength="80" onblur="page.handlers.ReplaceDocLibTable();"></td>
								<td>
								<span style="color:gray;font-style:italic"><i18n:message key="e5dom.DocLib.DocLibTableDesc"/></span>
								</td>
							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.DocLib.ShareRecordTable"/></th>
								<td width="100">
									<input type="checkbox" name="shareRecordTable" id="shareRecordTable"/><i18n:message key="e5dom.DocLib.ShareRecordTable"/>
								</td>
								<td>
								<span style="color:gray;font-style:italic"><i18n:message key="e5dom.DocLib.ShareRecordTableDesc"/></span>
								</td>
							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.DocLib.DocLibDescription"/></th>
								<td colspan="2"><input  id="newDocLibDesc" name="newDocLibDesc" TYPE="text" maxlength="80"></td>
							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.DocLib.DocLibKeepDay"/></th>
								<td colspan="2"><input  id="newDocLibKeepDays" name="newDocLibKeepDays" class="validate[custom[positiveNumber]]" TYPE="text" maxlength="6"></td>
							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.DocLib.attachDevName"/></th>
								<td colspan="2">
								<select id="attachDevName2" name="attachDevName">
								<option value="">------</option>
								</select>
								</td>
							</tr>
						</table>
					</div>
					<div id="DDLDiv">
						<input type="hidden" id="newDocTypeID" value=""/>
						<input type="hidden" id="newDSID" value=""/>
						<input type="hidden" id="newDocLibID" value="" />
						<table cellpadding="0" cellspacing="0" class="table">
							<caption><i18n:message key="e5dom.DocLib.CreateDocLibStep2"/></caption>
							<tr>
								<td class="wrap"><div id="FieldsDDL"></div></td>
							</tr>
						</table>
						<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.DocLib.CreateDocLibStep3"/></caption>
							<tr>
								<td class="wrap">
									<div id="AppendDDLDiv">
										<textarea id="appendDDL" ROWS="2" style="width:100%"></textarea>
										<div id="DDLEnd">)</div>
										<textarea id="extendDDL" ROWS="6" style="width:100%"></textarea>
									</div>
								</td>
							</tr>
							<!--<tr>
								<td>
									<div><input class="button" id="ConfirmDDLBtn" type="button" value="<i18n:message key="e5dom.DocLib.Confirm"/>"></div>
								</td>
							</tr>
							-->
							<tr>
								<td align="center" colspan="2"><input class="button" id="CreateDocLibBtn" type="submit" value="<i18n:message key="e5dom.DocLib.CreateDocLib"/>"></td>
							</tr>
						</table>
					</div>
					<div id="errorDiv" style="color:red;font-weight:bold"></div>
				</form>
			</div>
		</div>
	</body>
</html>