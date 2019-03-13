<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="e5dom.Folder.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5dom.Author' />" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link rel="stylesheet" rev="stylesheet" href="css/dom.css" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
		<link rel="stylesheet" rev="stylesheet" href="css/folder.css" type="text/css"/>
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var i18nInfo = {
				createFolder : "<i18n:message key="e5dom.Folder.CreateFolder"/>",
				modifyFolder : "<i18n:message key="e5dom.Folder.ModifyFolder"/>",
				deleteFolder : "<i18n:message key="e5dom.Folder.DeleteFolder"/>",

				createView : "<i18n:message key="e5dom.View.CreateView"/>",
				modifyView : "<i18n:message key="e5dom.View.ModifyView"/>",
				deleteView : "<i18n:message key="e5dom.View.DeleteView"/>",

				CreateOK : "<i18n:message key="e5dom.Folder.CreateOK"/>",
				CreateNo : "<i18n:message key="e5dom.Folder.CreateNo"/>",
				ModifyOK : "<i18n:message key="e5dom.Folder.ModifyOK"/>",
				ModifyNo : "<i18n:message key="e5dom.Folder.ModifyNo"/>",
				DeleteOK : "<i18n:message key="e5dom.Folder.DeleteOK"/>",
				DeleteNo : "<i18n:message key="e5dom.Folder.DeleteNo"/>",
				confirm1:"<i18n:message key="e5dom.Filter.Confirm1"/>",
				confirm2:"<i18n:message key="e5dom.Filter.Confirm2"/>",

				DeleteConfirm1 : "<i18n:message key="e5dom.Folder.DeleteConfirm1"/>",
				DeleteConfirm2 : "<i18n:message key="e5dom.Folder.DeleteConfirm2"/>",

				DeleteViewConfirm1 : "<i18n:message key="e5dom.View.DeleteViewConfirm1"/>",
				DeleteViewConfirm2 : "<i18n:message key="e5dom.View.DeleteViewConfirm2"/>",

				NotAllowDeleteRoot : "<i18n:message key="e5dom.Folder.NotAllowDeleteRoot"/>",

				FVFilters : "<i18n:message key="e5dom.Folder.FVFilters"/>",
				AssignFilters : "<i18n:message key="e5dom.Folder.AssignFilters"/>",
				DelFilters : "<i18n:message key="e5dom.Folder.DelFilters"/>",
				AssignRules : "<i18n:message key="e5dom.Folder.AssignRules"/>",
				AssignListPages : "<i18n:message key="e5dom.Folder.AssignListPages"/>",
				SortNow : "<i18n:message key="e5dom.Folder.SortNow"/>",
				Cancel : "<i18n:message key="e5dom.Folder.Cancel"/>"
			}
		</script>
		<script type="text/javascript" src="script/domv3.js"></script>
		<script type="text/javascript" src="script/folder.js"></script>
			<script type="text/javascript">
			$(document).ready(function(){
				//创建文档类型字段验证
				$("#CreateFolderForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#submitBtn").attr("disabled","true");
							$("#cancelBtn").attr("disabled","true");
							var folderID = $("#folderID").val();
							if(folderID==null||folderID==""){
								page.operation.SubmitCreateFolder();
							}else{
								page.operation.SubmitMotifyFolder();
							}
						}
					}
				});
				//创建文档类型验证
				$("#ViewForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#submitViewBtn").attr("disabled","true");
							$("#cancelViewBtn").attr("disabled","true");
							var viewID = $("#viewID").val();
							if(viewID==null||viewID==""){
								page.operation.SubmitCreateView();
							}else{
								page.operation.SubmitMotifyView();
							}
						}
					}
				});
			});
		</script>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th><i18n:message key="e5dom.Folder.title"/></th>
					<td><i18n:message key="e5dom.Folder.PleaseChooseDocLib"/></td>
					<td><select id="DocLibList"></select></td>
					<td id="prompt"></td>
				</tr>
			</table>
		</div>
		<div class="mainBodyWrap">
			<div id="LeftDiv">
				<iframe Name="FolderTreeIFrame" id="FolderTreeIFrame" frameborder="0" width="100%" height="100%"></iframe>
			</div>
			<div id="RightDiv">
				<!--======================  ShowFolderDiv  =============================================-->
				<div id="ShowFolderDiv" style="display:none">
					<table id="Tb" cellpadding="5" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.Folder.FolderProps"/></caption>
						<tr>
							<th align="right" width="20%">ID</th>
							<td id="folder_id" width="80%"></td>
						</tr>
						<tr id="FVFiltersList1">
							<th align="right"><i18n:message key="e5dom.Folder.FolderName"/></th>
							<td id="folderName"></td>

						</tr>
						<tr id="FVFiltersList2">
							<th align="right"><i18n:message key="e5dom.Folder.FVRules"/></th>
							<td style="white-space:normal">
								<div id="FVRules" ></div>
							</td>
						</tr>
						<tr id="FVFiltersList3">
							<th align="right"><i18n:message key="e5dom.Folder.FVListPages"/></th>
							<td>
								<div id="FVListPages" ></div>
							</td>
						</tr>
						<tr <%if (!com.founder.e5.dom.FolderView.showKeepDay) out.println("style='display:none'");%>>
							<th align="right"><i18n:message key="e5dom.DocLib.DocLibKeepDay"/></th>
							<td id="keepDay"></td>

						</tr>
					</table>
				</div>
				<!--======================  ShowViewDiv  =============================================-->
				<div id="ShowViewDiv" style="display:none">
					<table cellpadding="5" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.View.ViewProps"/></caption>
						<tr>
							<th align="right" width="20%">ID</th>
							<td id="view_id" width="80%"></td>
						</tr>
						<tr>
							<th align="right"><i18n:message key="e5dom.View.ViewName"/></th>
							<td id="viewName"></td>
						</tr>
						<tr id="viewList1">
							<th align="right"><i18n:message key="e5dom.View.ViewFormula"/></th>
							<td style="white-space:normal">
								<div id="ViewFormula" ></div>
							</td>
						</tr>
						<tr id="viewList2">
							<th align="right"><i18n:message key="e5dom.Folder.FVRules"/></th>
							<td style="white-space:normal">
								<div id="ViewFVRules" ></div>
							</td>
						</tr>
						<tr id="viewList3">
							<th align="right"><i18n:message key="e5dom.Folder.FVListPages"/></th>
							<td style="white-space:normal">
								<div id="ViewFVListPages" ></div>
							</td>
						</tr>
					</table>
				</div>
				<!--=================  Folder Form Div  =================================================-->
				<div id="errorDiv" style="color:red;font-weight:bold"></div>
				<div id="CreateFolderDiv" style="display:none">
					<form id="CreateFolderForm" method="post" action="">
					<input type="hidden" id="parentID" name="parentID">
					<input type="hidden" id="folderID" name="folderID">
					<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="e5dom.Folder.CreateFolder"/></caption>
						<tr>
							<th align="right" width="20%"><i18n:message key="e5dom.Folder.FolderName"/></th>
							<td width="80%"><input type="text" id="newFolderName" NAME="folderName" class="validate[required,maxSize[70]] field"></td>
						</tr>
						<tr id="Filter1">
							<th></th><th>
							<a href="#" onclick="setFVFilters(null, 'newFilters' ,'fvFilter', 'folder', 'Filter2');">
							<i18n:message key="e5dom.Filter.AddFilters"/><img id="img" src="../images/plus.gif"/>
							</a>
							</th>
						</tr>
						<tr id="Filter2">
							<th align="center"><i18n:message key="e5dom.Folder.FVRules"/></th>
							<td><select class="field" id="newRules" name="fvRules" multiple="multiple" size="5"></select>&nbsp;<input class="button" TYPE="button" onclick="page.handlers.AssignRules('folder');" value="<i18n:message key="e5dom.Folder.AssignRules"/>"></td>
						</tr>
						<tr id="Filter3">
							<th align="center"><i18n:message key="e5dom.Folder.FVListPages"/></th>
							<td><select class="field" id="newListPages" name="fvListPages" multiple="multiple" size="5"></select>&nbsp;<input class="button" TYPE="button" onclick="page.handlers.AssignListPages('folder');" value="<i18n:message key="e5dom.Folder.AssignListPages"/>"></td>
						</tr>
						<tr <%if (!com.founder.e5.dom.FolderView.showKeepDay) out.println("style='display:none'");%>>
							<th align="center"><i18n:message key="e5dom.DocLib.DocLibKeepDay"/></th>
							<td><input type="text" id="newKeepDay" name="keepDay" class="field" maxlength="6" onkeyup="this.value=value.replace(/[^\d]/g,'')"></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align:left;">
								<input type="hidden" id="fvFilters" name="fvFilters" value=""/>
								<input class="button" id="submitBtn" type="submit">&nbsp;
								<input class="button" type="button" id="cancelBtn" onclick="page.handlers.resetFolderForm();" value="<i18n:message key="e5dom.Folder.Reset"/>">
							</td>
						</tr>
					</table>
					</form>
				</div>
				<!--===================ViewFormDiv===========================================-->
				<div id="errorDiv1" style="color:red;font-weight:bold"></div>
				<div id="ViewFormDiv" style="display:none">
					<form method=post id="ViewForm" action="">
						<input type="hidden" id="parentFolderID" name="parentFolderID">
						<input type="hidden" id="viewID" name="viewID">
						<table cellpadding="0" cellspacing="0" class="table">
							<caption><i18n:message key="e5dom.View.CreateView"/></caption>
							<tr>
								<th align="right" width="20%"><i18n:message key="e5dom.View.ViewName"/></th>
								<td width="80%"><input type="text" id="newViewName" NAME="viewName" class="validate[required,maxSize[70]] field"></td>
							</tr>
							<tr>
								<th align="right"><i18n:message key="e5dom.View.ViewFormula"/></th>
								<td><input type="text" id="newViewFormula" NAME="viewFormula" class="largefield"></td>
							</tr>
							<tr id="view1">
								<th></th><th>
								<a href="#" onclick="setFVFilters(null, 'newViewFilters', 'viewFVFilter', 'view', 'view2');">
								<i18n:message key="e5dom.Filter.AddFilters"/><img id="img" src="../images/plus.gif"/>
								</a>
								</th>
							</tr>
							<tr id="view2">
								<th align="center"><i18n:message key="e5dom.Folder.FVRules"/></th>
								<td><select class="field" id="newViewRules" name="viewFVRules" multiple="multiple" size="5"></select>&nbsp;
									<input class="button" TYPE="button" onclick="page.handlers.AssignRules('view');" value="<i18n:message key="e5dom.Folder.AssignRules"/>"></td>
							</tr>
							<tr id="view3">
								<th align="center"><i18n:message key="e5dom.Folder.FVListPages"/></th>
								<td><select class="field" id="newViewListPages" name="viewFVListPages" multiple="multiple" size="5"></select>&nbsp;
									<input class="button" TYPE="button" onclick="page.handlers.AssignListPages('view');" value="<i18n:message key="e5dom.Folder.AssignListPages"/>"></td>
							</tr>
							<tr>
								<td></td>
								<td style="text-align:left;">
									<input type="hidden" id="viewFVFilters" name="viewFVFilters" value=""/>
									<input class="button" id="submitViewBtn" type="submit">&nbsp;
									<input class="button" id="cancelViewBtn" type="button" onclick="page.handlers.resetViewForm();" value="<i18n:message key="e5dom.Folder.Reset"/>">
								</td>
							</tr>
						</table>
					</form>
				</div>
				<!--================= SortFolder Div ============================-->
				<div id="SortFolderDiv">
					<table id="testTbl" cellpadding="0" cellspacing="0" class="table">
						<tr>
							<th align="center"><i18n:message key="e5dom.Folder.FVID"/></th>
							<th><i18n:message key="e5dom.Folder.FVName"/></th>
						</tr>
					</table>
					<input type="hidden" id="curFilterNum" value="0"/>
				</div>
			</div>
		</div>
	</body>
</html>