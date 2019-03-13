<%@ include file="../e5include/IncludeTag.jsp"%>
<%@page import="com.founder.e5.context.Context"%>
<%@page import="com.founder.e5.cat.CatReader"%>
<%@page import="com.founder.e5.cat.CatManager"%>
<%@page import="com.founder.e5.dom.DocTypeField"%>
<%
CatManager catManager = (CatManager)Context.getBean(CatManager.class);
request.setAttribute("catTypes", catManager.getTypes());//分类树
//地址（省市区县）、日期（年月日）、部门（部门树）、用户（用户树）、分类（分类树）、分类（分类树，可多选）、部门（部门树，可多选）、用户（用户树，可多选）(不可修改的选项)
int[] readonlyArr = {
		DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE, //10 任意填写（单行，带填写提示，键值对）
		DocTypeField.EDITTYPE_TREE, //6	分类（分类树）
		DocTypeField.EDITTYPE_TREE_MULTI, //33	分类（分类树，可多选）
		DocTypeField.EDITTYPE_TREE_SELECT, //16	分类（下拉框select，只可用于单层分类）
		DocTypeField.EDITTYPE_OTHER_DATA, //17	其它数据（下拉框，动态取值，名值对）
		DocTypeField.EDITTYPE_DEPT, //29	部门（部门树）
		DocTypeField.EDITTYPE_DEPT_MULTI, //34	部门（部门树，可多选）
		DocTypeField.EDITTYPE_USER, //30	用户（用户树）
		DocTypeField.EDITTYPE_USER_MULTI, //35	用户（用户树，可多选）
		DocTypeField.EDITTYPE_ROLE, //36	角色（角色树）
		DocTypeField.EDITTYPE_ROLE_MULTI, //37	角色（角色树，可多选）
		DocTypeField.EDITTYPE_ADDRESS, //27	地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）
		DocTypeField.EDITTYPE_DATE_SPLIT //28	日期拆分（分开填写方式，分为：年,月,日）
};

%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="e5dom.DocType.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="author" content="<i18n:message key='e5dom.Author' />" />
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">
		<link rel="stylesheet" rev="stylesheet" href="css/dom.css" type="text/css"/>
		<link rel="stylesheet" rev="stylesheet" href="css/doctype.css" type="text/css"/>
		<link type="text/css" rel="stylesheet" href="../e5script/calendar/calendar.css"/>
		<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script src="../e5script/calendar/usecalendar.js"></script>
		<script src="../e5script/calendar/calendar.js"></script>
		<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var DocTypeField = {
					/**字段填写方式：任意填写（单行）*/
					EDITTYPE_FREE : "<%=DocTypeField.EDITTYPE_FREE %>",
					/**字段填写方式：任意填写（单行，带填写提示）*/
					EDITTYPE_FREE_AUTOCOMPLETE : "<%=DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE %>",
					/**字段填写方式：任意填写（单行，带填写提示，键值对）*/
					EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE : "<%=DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE %>",
					/**字段填写方式：单选（下拉框方式）*/
					EDITTYPE_ENUM : "<%=DocTypeField.EDITTYPE_ENUM %>",
					/**字段填写方式：是/否，勾选方式*/
					EDITTYPE_BOOLEAN : "<%=DocTypeField.EDITTYPE_BOOLEAN %>",
					/**字段填写方式：多选（下拉框方式）*/
					EDITTYPE_MULTI : "<%=DocTypeField.EDITTYPE_MULTI %>",
					/**字段填写方式：任意填写（多行）*/
					EDITTYPE_FREE_LINES : "<%=DocTypeField.EDITTYPE_FREE_LINES %>",
					/**字段填写方式：电子邮件（专用文本，自动进行格式验证）*/
					EDITTYPE_EMAIL : "<%=DocTypeField.EDITTYPE_EMAIL %>",
					/**字段填写方式：固定电话（专用文本，自动进行格式验证）*/
					EDITTYPE_PHONE : "<%=DocTypeField.EDITTYPE_PHONE %>",
					/**字段填写方式：手机（专用文本，自动进行格式验证）*/
					EDITTYPE_MOBILE : "<%=DocTypeField.EDITTYPE_MOBILE %>",
					/**字段填写方式：地址拆分（分开填写方式，分为：省,市,区/县,街道,楼号）*/
					EDITTYPE_ADDRESS : "<%=DocTypeField.EDITTYPE_ADDRESS %>",
					/**字段填写方式：日期拆分（分开填写方式，分为：年,月,日）*/
					EDITTYPE_DATE_SPLIT : "<%=DocTypeField.EDITTYPE_DATE_SPLIT %>",
					/**字段填写方式：部门（部门树）*/
					EDITTYPE_DEPT : "<%=DocTypeField.EDITTYPE_DEPT %>",
					/**字段填写方式：用户（用户树）*/
					EDITTYPE_USER : "<%=DocTypeField.EDITTYPE_USER %>",
					/**字段填写方式：分类（分类树）*/
					EDITTYPE_TREE : "<%=DocTypeField.EDITTYPE_TREE %>",
					/**字段填写方式：分类（分类树，可多选）*/
					EDITTYPE_TREE_MULTI : "<%=DocTypeField.EDITTYPE_TREE_MULTI %>",
					/**字段填写方式：部门（部门树，可多选）*/
					EDITTYPE_DEPT_MULTI : "<%=DocTypeField.EDITTYPE_DEPT_MULTI %>",
					/**字段填写方式：用户（用户树，可多选）*/
					EDITTYPE_USER_MULTI : "<%=DocTypeField.EDITTYPE_USER_MULTI %>",

					/**字段填写方式：角色（角色树）*/
					EDITTYPE_ROLE : "<%=DocTypeField.EDITTYPE_ROLE %>",
					/**字段填写方式：角色（角色树，可多选）*/
					EDITTYPE_ROLE_MULTI : "<%=DocTypeField.EDITTYPE_ROLE_MULTI %>",
					
					//---旧版本定义，不使用了的---
					/**字段填写方式：复杂控件方式*/
					EDITTYPE_COMPLEX : "<%=DocTypeField.EDITTYPE_COMPLEX %>",
					/**字段填写方式：APPLET.保留类型，没使用*/
					EDITTYPE_APPLET : "<%=DocTypeField.EDITTYPE_APPLET %>",
					/**字段填写方式：日期值，不使用了*/
					EDITTYPE_DATE : "<%=DocTypeField.EDITTYPE_DATE %>",
					//---end.
					
					//---2013-5-13 增加填写方式---
					/**字段填写方式：单选（下拉框select，动态取值）*/
					EDITTYPE_SELECT : "<%=DocTypeField.EDITTYPE_SELECT %>",
					/**字段填写方式：单选（单选框radio）*/
					EDITTYPE_SELECT_RADIO : "<%=DocTypeField.EDITTYPE_SELECT_RADIO %>",
					/**字段填写方式：单选（单选框radio，动态取值）*/
					EDITTYPE_SELECT_RADIO_DYNAMIC : "<%=DocTypeField.EDITTYPE_SELECT_RADIO_DYNAMIC %>",
					
					/**字段填写方式：多选（下拉框select，动态取值）*/
					EDITTYPE_MULTI_DYNAMIC : "<%=DocTypeField.EDITTYPE_MULTI_DYNAMIC %>",
					/**字段填写方式：多选（复选框checkbox）*/
					EDITTYPE_MULTI_CHECKBOX : "<%=DocTypeField.EDITTYPE_MULTI_CHECKBOX %>",
					/**字段填写方式：多选（复选框checkbox，动态取值）*/
					EDITTYPE_MULTI_CHECKBOX_DYNAMIC : "<%=DocTypeField.EDITTYPE_MULTI_CHECKBOX_DYNAMIC %>",
					
					/**字段填写方式：分类（下拉框select，只可用于单层分类）*/
					EDITTYPE_TREE_SELECT : "<%=DocTypeField.EDITTYPE_TREE_SELECT %>",
					/**字段填写方式：其它数据（下拉框，动态取值，名值对）*/
					EDITTYPE_OTHER_DATA : "<%=DocTypeField.EDITTYPE_OTHER_DATA %>"
			}
			var i18nInfo = {

				docTypeLoaded : "<i18n:message key="e5dom.DocType.promptInfo.DocTypeLoaded"/>",
				createTypeRelation : "<i18n:message key="e5dom.DocType.CreateTypeRelation"/>",
				DocTypeRelation : "<i18n:message key="e5dom.DocType.DocTypeRelation"/>",
				NewDocType : "<i18n:message key="e5dom.DocType.NewDocType"/>",
				createType : "<i18n:message key="e5dom.DocType.createDocType"/>",
				createTypeCustom : "<i18n:message key="e5dom.DocType.createDocType.Custom"/>",
				createTypeSimple : "<i18n:message key="e5dom.DocType.createDocType.Simple"/>",
				docTypeSelected : "<i18n:message key="e5dom.DocType.promptInfo.DocTypeSelected"/>",
				docTypeRelationCreated :"<i18n:message key="e5dom.DocType.promptInfo.DocTypeRelationCreated"/>"	,
				FieldCount :"<i18n:message key="e5dom.DocType.FieldCount"/>",
				DocTypeName	:"<i18n:message key="e5dom.DocType.TypeName"/>",

				deleteField : "<i18n:message key="e5dom.DocType.FieldMenu.Delete"/>",
				modifyField : "<i18n:message key="e5dom.DocType.FieldMenu.Modify"/>",

				orderByID : "<i18n:message key="e5dom.DocType.FieldMenu.orderByID"/>",
				orderByIDDesc : "<i18n:message key="e5dom.DocType.FieldMenu.orderByIDDesc"/>",
				orderByColumnCode : "<i18n:message key="e5dom.DocType.FieldMenu.orderByColumnCode"/>",
				orderByColumnName : "<i18n:message key="e5dom.DocType.FieldMenu.orderByColumnName"/>",
				orderByDataType : "<i18n:message key="e5dom.DocType.FieldMenu.orderByDataType"/>",
				orderByAttribute : "<i18n:message key="e5dom.DocType.FieldMenu.orderByAttribute"/>",

				confirmDeleteField : "<i18n:message key="e5dom.DocType.FieldMenu.confirmDeleteField"/>",
				confirmDeleteFieldEnd : "<i18n:message key="e5dom.DocType.FieldMenu.confirmDeleteFieldEnd"/>",

				updateButton : "<i18n:message key="e5dom.DocType.CreateField.Update"/>",
				createButton : "<i18n:message key="e5dom.DocType.CreateField.Create"/>",

				createDocTypeOK : "<i18n:message key="e5dom.DocType.CreateDocTypeOK"/>",
				createDocTypeNo : "<i18n:message key="e5dom.DocType.CreateDocTypeNo"/>",

				FieldExisted : "<i18n:message key="e5dom.DocType.FieldExisted"/>",

				deleteFieldOK : "<i18n:message key="e5dom.DocType.DeleteField.DeleteOK"/>",
				deleteFieldNo : "<i18n:message key="e5dom.DocType.DeleteField.DeleteNo"/>",
				deleteSysField : "<i18n:message key="e5dom.DocType.DeleteField.DeleteSysField"/>",
				deleteAppField : "<i18n:message key="e5dom.DocType.DeleteField.DeleteAppField"/>",

				FieldLengthLimit : "<i18n:message key="e5dom.DocType.FieldLengthLimit"/>",

				CreateFieldTitle : "<i18n:message key="e5dom.DocType.CreateField.Title"/>",
				RestoreTitle : "<i18n:message key="e5dom.docTypeFieldRestore.title"/>",
				AlterDocLib : "<i18n:message key="e5dom.DocType.alterDocLib"/>",
				createDocTypeField : "<i18n:message key="e5dom.DocType.createDocTypeField"/>",
				createDocTypeRelation : "<i18n:message key="e5dom.DocType.createDocTypeRelation"/>",
				cancel : "<i18n:message key="e5dom.DocType.CreateField.Cancel"/>",
				RightClick : "<i18n:message key="e5dom.DocType.RightClick"/>",
				System : "<i18n:message key="e5dom.DocType.CreateField.Attribute.System"/>",
				Application : "<i18n:message key="e5dom.DocType.CreateField.Attribute.Application"/>",
				User : "<i18n:message key="e5dom.DocType.CreateField.Attribute.User"/>",
				EnumTitle:"<i18n:message key="e5dom.DocType.CreateField.EditType.Enum.Title"/>",
				apptitle:"<i18n:message key="e5dom.DocType.apptitle"/>"

			}
			var docTypeChosen = "<c:out value="${docTypeID}"/>";
			var readonlyArray = new Array();
			<%for(int readonly:readonlyArr){%>
				readonlyArray.push("<%=readonly%>");
			<%}%>
		</script>
		<script language="javascript" src="script/domv3.js"></script>
		<script language="javascript" src="script/doctype.js"></script>
		<script type="text/javascript">
			$(document).ready(function(){
				//创建文档类型字段验证
				$("#CreateFieldForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#CreateFieldBtn").attr("disabled","true");
							$("#CancelCreateFieldBtn").attr("disabled","true");
							$("#options option").each(function(){
								$(this).attr("selected","selected");
							});
							CreateFieldForm.submit();
						}
					}
				});
				//创建文档类型验证
				$("#DocTypeForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#DocTypeBtn").attr("disabled","true");
							$("#CancelDocTypeBtn").attr("disabled","true");
							DocTypeForm.submit();
						}
					}
				});
				//创建文档类型_平台字段可定制验证
				$("#DocTypeCustomForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#DocTypeCustomBtn").attr("disabled","true");
							$("#CancelDocTypeCustomBtn").attr("disabled","true");
							DocTypeCustomForm.submit();
						}
					}
				});
				//创建简单文档类型
				$("#DocTypeSimpleForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							$("#DocTypeSimpleBtn").attr("disabled","true");
							$("#CancelDocTypeSimpleBtn").attr("disabled","true");
							DocTypeSimpleForm.submit();
						}
					}
				});
				//验证枚举值
				$("#optionsForm").validationEngine({
					autoPositionUpdate:true,
					onValidationComplete:function(from,r){
						if(r){
							window.onbeforeunload=null;
							page.handlers.clickAddOptions();
						}
					}
				});
			});
		</script>
		<style>
			.area{
				margin-bottom:10px;
				background:#F3F3F3;
				border: 1px solid #D1D1D1;
				padding: 5px;
			}
		</style>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th><i18n:message key="e5dom.DocType.title"/></th>
					<td><i18n:message key="e5dom.DocType.PleaseChooseDocType"/></td>
					<td><select id="DocTypeList"></select></td>
					<td><span>|</span></td>
					<td><a href="#" id="createDocTypeField"><i18n:message key="e5dom.DocType.createDocTypeField"/></a></td>
					<td><span>|</span></td>
					<td><a href="#" id="alterDocLib"><i18n:message key="e5dom.DocType.alterDocLib"/></a></td>
					<td><span>|</span></td>
					<td><a href="#" id="DeleteFieldsMgr"><i18n:message key="e5dom.DocLib.DeleteFieldsMgr"/></a></td>
					<td><span>|</span></td>
					<td><a href="#" id="exportDocType"><i18n:message key="e5dom.DocType.exportDocType"/></a></td>
					<td><span>|</span></td>
					<td><a href="#" id="createDocType"><i18n:message key="e5dom.DocType.createDocType"/></a></td>
					<td><span>|</span></td>
					<td><a href="#" id="createDocTypeSimple"><i18n:message key="e5dom.DocType.createDocType.Simple"/></a></td>
					<!--<td><a href="#" id="createDocTypeCustom"><i18n:message key="e5dom.DocType.createDocType.Custom"/></a></td>-->
					<td><span>|</span></td>
					<td><a href="#" id="createDocTypeRelation"><i18n:message key="e5dom.DocType.createDocTypeRelation"/></a></td>
					<td><span>|</span></td>
					<td><span id="prompt"></span></td>
				</tr>
			</table>
			<iframe frameborder="0" id="exportFrame"></iframe>
		</div>
		<div class="mainBodyWrap">
			<div id="LeftDiv">
				<table cellspacing="0" cellpadding="0" class="table">
					<caption><i18n:message key="e5dom.DocType.DocTypeProperty"/></caption>
					<tr>
						<td align="center" width="10"><img src="../images/arrow2.png" border="0"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeID"/><span id="TypeID"></span></td>
					</tr>
					<tr>
						<td align="center"><img src="../images/arrow2.png"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeName"/><span id="TypeName"></span></td>
					</tr>
					<tr>
						<td align="center"><img src="../images/arrow2.png"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeDesc"/><span id="TypeDesc"></span></td>
					</tr>
					<tr>
						<td align="center"><img src="../images/arrow2.png"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeApp"/><span id="TypeApp"></span></td>
					</tr>
					<tr>
						<td align="center"><img src="../images/arrow2.png"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeDefaultFlow"/><span id="TypeFlow"></span></td>
					</tr>
					<tr>
						<td align="center"><img src="../images/arrow2.png"></img></td>
						<td><i18n:message key="e5dom.DocType.TypeRelated"/><span id="TypeRelated"/></span></td>
					</tr>
				</table>
			</div>
			<div id="RightDiv">
				<div id="FieldCount" class="area"></div>
				<table cellpadding="3" cellspacing="0" class="table">
					<tr>
						<th width="10%"><i18n:message key="e5dom.DocType.FieldID"/></th>
						<th width="15%"><i18n:message key="e5dom.DocType.FieldColumnName"/></th>
						<th width="20%"><i18n:message key="e5dom.DocType.FieldColumnCode"/></th>
						<th width="10%"><i18n:message key="e5dom.DocType.FieldDataType"/></th>
						<th width="15%"><i18n:message key="e5dom.DocType.EditType"/></th>
						<th width="10%"><i18n:message key="e5dom.DocType.FieldDefaultValue"/></th>
						<th width="10%"><i18n:message key="e5dom.DocType.FieldNullable"/></th>
						<th width="10%"><i18n:message key="e5dom.DocType.FieldAttr"/></th>
					</tr>
					<tbody id="testTbl2">
					</tbody>
				</table>
			</div>
		</div>

		<div id="DocTypeRelationDiv">
		</div>
		<div id="CreateDocTypeDiv">
			<form onsubmit="page.operation.CreateDocType();return false;" name="DocTypeForm" id="DocTypeForm" method="post">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocType.NewDocType"/></caption>
					<tr>
						<td width="30%"><span class="red">*</span><i18n:message key="e5dom.DocType.TypeName"/></td>
						<td width="70%"><input type="text" id="newDocTypeName" name="newDocTypeName" class="validate[required,maxSize[40]]" value=""></td>
					</tr>
					<tr>
						<td><i18n:message key="e5dom.DocType.BelongApp"/></td>
						<td>
						<select name="appID" id="appID" style="width:150px"></select>&nbsp;<img id="img" src="../images/plus.gif" onclick="page.handlers.addApp('appID');"/>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input class="button" type="submit" id="DocTypeBtn" value="<i18n:message key="e5dom.DocType.createDocType"/> " style="width:90px"/>&nbsp;
							<input class="button" id="CancelDocTypeBtn" type="reset" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div id="CreateDocTypeCustomDiv">
			<form name="DocTypeCustomForm" id="DocTypeCustomForm"  method="post" action="DocTypeController.do?invoke=createDocTypeCustom">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocType.NewDocType"/></caption>
					
					<tr>
						<td colspan="2"><span style="color:gray;font-style:italic"><i18n:message key="e5dom.DocType.createDocType.CustomDesc"/></span></td>
					</tr>
					<tr>
						<td width="30%"><span class="red">*</span><i18n:message key="e5dom.DocType.TypeName"/></td>
						<td width="70%"><input type="text" id="newDocTypeNameCustom" name="newDocTypeNameCustom" class="validate[required,maxSize[40]]" value=""></td>
					</tr>
					<tr>
						<td><i18n:message key="e5dom.DocType.BelongApp"/></td>
						<td>
						<select id="appIDCustom" name="appIDCustom" style="width:150px"></select>&nbsp;<img id="img" src="../images/plus.gif" onclick="page.handlers.addApp('appIDCustom');"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table cellpadding="3" cellspacing="0" class="table">
								<tr>
									<th width="10px"></th>
									<!--<th width="10%"><i18n:message key="e5dom.DocType.FieldID"/></th>
									-->
									<th width="25%"><i18n:message key="e5dom.DocType.FieldColumnName"/></th>
									<th width="25%"><i18n:message key="e5dom.DocType.FieldColumnCode"/></th>
									<th width="25%"><i18n:message key="e5dom.DocType.FieldDataType"/></th>
									<th width="15%"><i18n:message key="e5dom.DocType.FieldDefaultValue"/></th>
									<th width="10%"><i18n:message key="e5dom.DocType.FieldNullable"/></th>
								</tr>
								<tbody id="sysFieldsTb">
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input class="button" type="submit" id="DocTypeCustomBtn" value="<i18n:message key="e5dom.DocType.createDocType"/> " style="width:90px"/>&nbsp;
							<input class="button" id="CancelDocTypeCustomBtn" type="reset" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
		
		<div id="CreateDocTypeSimpleDiv">
			<form name="DocTypeSimpleForm" id="DocTypeSimpleForm"  method="post" action="DocTypeController.do?invoke=createDocTypeSimple">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocType.NewDocType"/></caption>
					<tr>
						<td width="30%"><span class="red">*</span><i18n:message key="e5dom.DocType.TypeName"/></td>
						<td width="70%"><input type="text" id="newDocTypeNameSimple" name="newDocTypeNameSimple" class="validate[required,maxSize[40]]" value=""></td>
					</tr>
					<tr>
						<td><i18n:message key="e5dom.DocType.BelongApp"/></td>
						<td>
						<select id="appIDSimple" name="appIDSimple" style="width:150px"></select>&nbsp;<img id="img" src="../images/plus.gif" onclick="page.handlers.addApp('appIDSimple');"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table cellpadding="3" cellspacing="0" class="table">
								<tr>
									<th width="25%"><i18n:message key="e5dom.DocType.FieldColumnName"/></th>
									<th width="25%"><i18n:message key="e5dom.DocType.FieldColumnCode"/></th>
									<th width="25%"><i18n:message key="e5dom.DocType.FieldDataType"/></th>
									<th width="15%"><i18n:message key="e5dom.DocType.FieldDefaultValue"/></th>
									<th width="10%"><i18n:message key="e5dom.DocType.FieldNullable"/></th>
								</tr>
								<tbody id="sysSimpleFieldsTb">
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input class="button" type="submit" id="DocTypeSimpleBtn" value="<i18n:message key="e5dom.DocType.createDocType"/> " style="width:90px"/>&nbsp;
							<input class="button" id="CancelDocTypeSimpleBtn" type="reset" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>"/>
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div id="CreateFieldFormDIV">
			<div id="errorDiv" style="color:red;font-weight:bold"></div>
			<div id="SubCreateFieldFormDIV">
			<form id="CreateFieldForm" name="CreateFieldForm" method="post" action="DocTypeFieldController.do?invoke=createField">

				<input type="hidden" name="docTypeID" id="docTypeID" value="">
				<input type="hidden" name="fieldID" id="fieldID" value="">
				<input type="hidden" name="isNew" id="isNew" value="">

				<input type="hidden" name="status" id="status" value="P">
				<table id="CreateFieldTB" cellpadding="5" cellspacing="0" class="table">
					<caption><i18n:message key="e5dom.DocType.CreateField.Title"/></caption>
					<tr>
						<td align="right" width="20%"><i18n:message key="e5dom.DocType.CreateField.ColumnName"/></td>
						<td width="80%"><input type="text" id="columnName" name="columnName" class="validate[required,maxSize[40]] small"/></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.ColumnCode"/></td>
						<td><input type="text" id="columnCode" name="columnCode" class="validate[required,maxSize[40]] small"/></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.DataType"/></td>
						<td>
							<select id="dataType" name="dataType" onchange="dataTypeChange(this);" class="small">
								<option value="CHAR" ><i18n:message key="e5dom.DocType.CreateField.DataType.CHAR"/>
								<option value="VARCHAR" selected="selected"><i18n:message key="e5dom.DocType.CreateField.DataType.VARCHAR"/>
								<option value="INTEGER" ><i18n:message key="e5dom.DocType.CreateField.DataType.INTEGER"/>
								<option value="LONG" ><i18n:message key="e5dom.DocType.CreateField.DataType.BIGINT"/>
								<option value="FLOAT" ><i18n:message key="e5dom.DocType.CreateField.DataType.REAL"/>
								<option value="DOUBLE" ><i18n:message key="e5dom.DocType.CreateField.DataType.NUMERIC"/>
								<option value="BLOB" ><i18n:message key="e5dom.DocType.CreateField.DataType.BLOB"/>
								<option value="CLOB"><i18n:message key="e5dom.DocType.CreateField.DataType.CLOB"/>
								<option value="DATE" ><i18n:message key="e5dom.DocType.CreateField.DataType.DATE"/>
								<option value="TIME" ><i18n:message key="e5dom.DocType.CreateField.DataType.TIME"/>
								<option value="TIMESTAMP" ><i18n:message key="e5dom.DocType.CreateField.DataType.TIMESTAMP"/>
								<option value="EXTFILE" ><i18n:message key="e5dom.DocType.CreateField.DataType.EXTFILE"/>
							</select>
						</td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.DataLength"/></td>
						<td><input type="text" name="dataLength" id="dataLength" class="validate[custom[integer]] small"></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.Scale"/></td>
						<td><input type="text" name="scale" id="scale" class="validate[custom[integer]] small"></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.DefaultValue"/></td>
						<td><input type="text" name="defaultValue" id="defaultValue" class="small"></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.Nullable"/></td>
						<td><input type="checkbox" checked="checked" name="nullable" id="nullable" class="checkbox"/></td>
					</tr>
					<tr>
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.Readonly"/></td>
						<td><input type="checkbox" name="readonly" id="readonly" class="checkbox"/></td>
					</tr>
					<tr style="display:none;">
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.Attribute"/></td>
						<td>
							<select name="attribute" id="attribute" class="small">
								<option value="3"><i18n:message key="e5dom.DocType.CreateField.Attribute.User"/></option>
							</select>
						</td>
					</tr>
					<tr id="trEditType">
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.EditType"/></td>
						<td>
							<select name="editType" id="editType" onchange="changeEditType();removeOptions();" class="small"></select>
						</td>
					</tr>
					<tr id="trEnum" style="display:none;">
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.EditType.Enum"/></td>
						<td>
							<table style="width:100%">
								<tr>
									<td width="80%">
									<select name="options" id="options" size="10" multiple="multiple" style="width:100%"></select>
									</td>
									<td width="20%" align="left">
									<img alt="" src="../images/plus.gif" onclick="page.handlers.addOptions();">
									<br/>
									<img alt="" src="../images/minus.gif" onclick="page.handlers.delOptions();">
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr id="trCat" style="display:none;">
						<td align="right"><i18n:message key="e5dom.DocType.CreateField.CatType"/></td>
						<td>
							<select id="cats" name="cats" class="small">
								<c:forEach var="cat" items="${catTypes}">
									<option value="<c:out value="${cat.catType}"/>"><c:out value="${cat.name}"/></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr id="trUrl" style="display:none;">
						<td align="right">URL</td>
						<td><input type="text" name="URL" id="URL" class1="small" style="width:300px;"></td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input class="button" id="CreateFieldBtn" type="submit" value="<i18n:message key="e5dom.DocType.CreateField.Create"/>">
							<input class="button" id="CancelCreateFieldBtn" type="reset" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>">
						</td>
					</tr>
				</table>
			</form>
			</div>
		</div>
		<div id="optionsDiv" style="display:none;">
			<form id="optionsForm" name="optionsForm" method="post" action="">
				<table id="optionsTB" cellpadding="0" cellspacing="0" class="table">
					<tr>
						<th align="right"><span class="red">*</span><i18n:message key="e5dom.DocType.CreateField.EditType.Enum"/></th>
						<td id="optionTD"><input type="text" id="optionValue" name="optionValue" class="validate[required,maxSize[100]] small"/></td>
					</tr>
					<tr>
						<th align="right"><i18n:message key="e5dom.DocType.CreateField.EditType.EnumName"/></th>
						<td id="optionNameTD"><input type="text" id="optionName" name="optionName" class="validate[maxSize[100]] small"/></td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<br/>
							<input class="button" id="addOptionsBtn" type="submit" value="<i18n:message key="e5dom.custom.btn.confirm"/>">
							<input class="button" id="cancelOptionsBtn" type="reset" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>">
						</td>
					</tr>
				</table>
			</form>
		</div>
	</body>
</html>
