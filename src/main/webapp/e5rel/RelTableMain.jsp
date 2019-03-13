<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5rel" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta name="author" content="<i18n:message key='e5rel.Author' />"/>
		<link type="text/css" rel="StyleSheet" href="../e5style/reset.css">
		<link type="text/css" rel="stylesheet" href="../e5dom/css/dom.css"/>
		<link type="text/css" rel="stylesheet" href="css/reltable.css"/>
		<link type="text/css" rel="StyleSheet" href="../e5script/xmenu/xmenu.css">
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/xmenu/xmenu.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript">
			var i18nInfo = {
				deleteRelTable :"<i18n:message key="e5rel.RelTable.DeleteRelTable"/>",
				confirmDeleteRelTable : "<i18n:message key="e5rel.RelTable.confirmDeleteRelTable"/>",
				confirmDeleteRelTableEnd :"<i18n:message key="e5rel.RelTable.confirmDeleteRelTableEnd"/>",
				createTableOk :"<i18n:message key="e5rel.RelTable.CreateTableOK"/>",
				createTableNo :"<i18n:message key="e5rel.RelTable.CreateTableNo"/>",
				ModifyTableOK :"<i18n:message key="e5rel.RelTable.ModifyTableOK"/>",
				ModifyTableNo :"<i18n:message key="e5rel.RelTable.ModifyTableNo"/>",
				DeleteTableOK :"<i18n:message key="e5rel.RelTable.DeleteTableOK"/>",
				DeleteTableFailed : "<i18n:message key="e5rel.RelTable.DeleteTableFailed"/>",
				addNewField : "<i18n:message key="e5rel.RelTable.AddNewField"/>",

				modifyRelTable : "<i18n:message key="e5rel.RelTable.modifyRelTable"/>",

				SameNameTableExisted : "<i18n:message key="e5rel.RelTable.SameNameTableExisted"/>",

				relationExisted : "<i18n:message key="e5rel.RelTable.relationExisted"/>",

				sameNameFieldExisted : "<i18n:message key="e5rel.RelTable.sameNameFieldExisted"/>",
				deleteRelationFirst : "<i18n:message key="e5rel.RelTable.deleteRelationFirst"/>",
				RightClick : "<i18n:message key="e5rel.RelTable.RightClick"/>"
			}
		</script>
		<script language="javascript" src="../e5dom/script/domv3.js"></script>
		<script language="javascript" src="script/reltable.js"></script>
	</head>
	<body>
		<div id="TopDiv" class="titleDiv">
			<table cellpadding="0" cellspacing="0">
				<tr>
					<th><i18n:message key="e5rel.RelTable.title"/></th>
					<td><a href="#" id="createRelTable"><i18n:message key="e5rel.RelTable.createRelTable"/></a></td>
				</tr>
			</table>
		</div>
		<div class="mainBodyWrap">
			<div id="LeftDiv">
				<table id="testTbl" cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5rel.RelTable.RelTableList"/></caption>
				</table>
			</div>
			<div id="RightDiv">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5rel.RelTable.RelTableProps"/></caption>
					<tr>
						<th width="20%">ID</th>
						<td id="tb_id" width="80%"></td></tr>
					<tr>
						<th><i18n:message key="e5rel.RelTable.RelTableName"/></th>
						<td id="tb_name" ></td>
					</tr>
					<tr>
						<th><i18n:message key="e5rel.RelTable.RelTableTableName"/></th>
						<td id="tb_tableName"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5rel.RelTable.DataSource"/></th>
						<td id="tb_dsID"></td>
					</tr>
					<tr>
						<th><i18n:message key="e5rel.RelTable.RelTableDocType"/></th>
						<td id="tb_DocType"></td>
					</tr>
				</table>
			</div>
			<!--=========Modify RelTable DIV==============-->
			<div id="ModifyRelTableDiv">
				<div id="errorDiv2" style="color:red;font-weight:bold"></div>
				<form onsubmit="return false;" id="ModifyRelTableForm">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5rel.RelTable.modifyRelTable"/></caption>
					<tr>
						<th width="20%"><i18n:message key="e5rel.RelTable.RelTableName"/></th>
						<td width="80%"><input type="text" id="newRelTableName" name="newRelTableName" class="validate[required]"></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input class="button" id="modifySub" type="submit" value="<i18n:message key="e5rel.RelTable.Modify"/>">
							<input class="button" id="modifyCel" type="button" onclick="page.handlers.resetRelTableForm();" value="<i18n:message key="e5rel.RelTable.Reset"/>">
						</td>
					</tr>
				</table>
				</form>
			</div>
			<div id="AddFieldDiv">
				<div id="errorDiv1" style="color:red;font-weight:bold"></div>
				<form id="AddFieldForm" onsubmit="return false;">
					<input type="hidden" name="tableId" id="tableId"/>
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5rel.RelTable.AddNewField"/></caption>
						<tr>
							<th width="20%"><i18n:message key="e5rel.RelTable.FieldNameName"/></th>
							<td width="80%"><input id="fieldName" name="fieldName" type="text" class="validate[required,custom[commonchar]]"/></td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.FieldType"/></th>
							<td>
								<SELECT id="fieldType" name="fieldType" onchange="page.operation.fieldTypeChange();">
								<option value="CHAR" ><i18n:message key="e5rel.RelTable.CreateField.DataType.CHAR"/>
								<option value="VARCHAR" ><i18n:message key="e5rel.RelTable.CreateField.DataType.VARCHAR"/>
								<option value="INTEGER" selected="selected"><i18n:message key="e5rel.RelTable.CreateField.DataType.INTEGER"/>
								<option value="LONG" ><i18n:message key="e5rel.RelTable.CreateField.DataType.BIGINT"/>
								<option value="FLOAT" ><i18n:message key="e5rel.RelTable.CreateField.DataType.REAL"/>
								<option value="DOUBLE" ><i18n:message key="e5rel.RelTable.CreateField.DataType.NUMERIC"/>
								<option value="BLOB" ><i18n:message key="e5rel.RelTable.CreateField.DataType.BLOB"/>
								<option value="CLOB"><i18n:message key="e5rel.RelTable.CreateField.DataType.CLOB"/>
								<option value="DATE" ><i18n:message key="e5rel.RelTable.CreateField.DataType.DATE"/>
								<option value="TIME" ><i18n:message key="e5rel.RelTable.CreateField.DataType.TIME"/>
								<option value="TIMESTAMP" ><i18n:message key="e5rel.RelTable.CreateField.DataType.TIMESTAMP"/>
							</SELECT>
							</td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.CreateField.FieldLength"/></th>
							<td><input type="text" name="fieldLength" id="fieldLength" class="validate[custom[integer]]"></td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.CreateField.Nullable"/></th>
							<td><input checked="checked" value="1" type="checkbox" name="nullable" id="nullable"></td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input class="button" id="CreateFieldBtn" type="submit" value="<i18n:message key="e5rel.RelTable.CreateField"/>" /></td>
						</tr>

			   </table>
			   </form>
			</div>

			<!--========创建分类关联表的DIV================================================-->
			<div id="CreateRelTableDiv">
				<!--========输入关联表名字的DIV=========-->
				<div id="newRelTableNameDIV" style="display:none">
					<div id="errorDiv" style="color:red;font-weight:bold"></div>
					<form onsubmit="return false;" id="RelTableNameForm">
					<table cellpadding="0" cellspacing="0" class="table">
						<caption><i18n:message key="e5rel.RelTable.CreateStep1"/></caption>
						<tr>
							<th width="20%"><i18n:message key="e5rel.RelTable.RelTableName"/></th>
							<td width="80%"><input id="Name" name="Name" type="text" class="validate[required]"/></td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.RelTableTableName"/></th>
							<td>DOM_REL_<input id="NewTableName" name="NewTableName" type="text" class="validate[required,custom[commonchar]]"/></td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.PleaseChooseDocType"/></th>
							<td><select id="DocTypeList" style="width:160px"></select></td>
						</tr>
						<tr>
							<th><i18n:message key="e5rel.RelTable.PleaseChooseDataSource"/></th>
							<td><select id="DSConfigList" style="width:160px"></select></td>
						</tr>
						<tr>
							<td align="center" colspan="2"><input class="button" id="TableNameBtn" type="submit" value="<i18n:message key="e5rel.RelTable.BeginCreate"/>" /></td>
						</tr>
					</table>
					</form>
				</div>
				<br/>
				<!--========选择文档类型字段的DIV=========-->
				<div id="ChooseFieldsDIV" style="display:none">
					<form id="fieldsForm">
						<table id="tab1" cellpadding="0" cellspacing="0" class="table">
							<caption><i18n:message key="e5rel.RelTable.CreateStep2"/></caption>
							<tr>
								<th class="bluetd"><input name='chkAll' type='checkbox' id='chkAll' onclick='CheckAll(this.form)' value='checkbox'><i18n:message key="e5rel.RelTable.Checked"/></th>
								<th class="bluetd">ID</th>
								<th class="bluetd"><i18n:message key="e5rel.RelTable.ColumnName"/></th>
								<th class="bluetd"><i18n:message key="e5rel.RelTable.ColumnCode"/></th>
								<th class="bluetd"><i18n:message key="e5rel.RelTable.DataType"/></th>
							</tr>
						</table>
					</form>
					<!--===用于提交生成DDL的Form====-->
					<form onsubmit="return false;" id="RelTableForm">
						<div style="display:none">
							<input type="hidden" name="newName" id="newName"/>
							<input type="hidden" name="tableName" id="tableName"/>
							<input type="hidden" name="docTypeId" id="docTypeId"/>
							<input type="hidden" name="dsId" id="dsId"/>
						</div>
					</form>
					<!--=======================-->
					<div><br><input class="button" id="FieldsBtn" type="button" value="<i18n:message key="e5rel.RelTable.ChooseFields"/>"></div>
				</div>
				<br>
				<!--========显示修改DDL的DIV=========-->
				<div id="DDLDIV" style="display:none">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="e5rel.RelTable.CreateStep3"/></caption>
					<tr>
						<td class="wrap">
							<div id="DDLBegin"></div>
						</td>
					</tr>
					<tr>
						<td>
							<div id="AppendDDLDiv">
							<textarea id="AppendDDL" rows="6" style="width:90%"></textarea>
							<div id="DDLEnd">)</div>
							</div>
						</td>
					</tr>
				</table>
				<div><br><input class="button" id="DDLBtn" type="button" value="<i18n:message key="e5rel.RelTable.Create"/>"></div>
				</div>
			</div>
		</div>
	</body>
	<!--===========添加字段的DIV===========-->
	<script type="text/javascript">
		$(document).ready(function(){
			//创建关联表验证
			$("#RelTableNameForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$("#TableNameBtn").attr("disabled","true");
						page.handlers.ConfirmRelTableName();
					}
				}
			});
			//修改关联表名称验证
			$("#ModifyRelTableForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$("#modifySub").attr("disabled","true");
						$("#modifyCel").attr("disabled","true");
						page.handlers.submitModifyRelTable();
					}
				}
			});
			//添加表字段
			$("#AddFieldForm").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r){
						window.onbeforeunload=null;
						$("#CreateFieldBtn").attr("disabled","true");
						page.handlers.CreateField();
					}
				}
			});
		});
	</script>
</html>
