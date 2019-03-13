<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
        <script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.contextmenu.r2.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-tablehandle/jquery.tablehandle.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
		<script type="text/javascript" src="script/detail.js"></script>
		<script type="text/javascript">
			var i18n = {
				tablehandle_addrow_up:"<i18n:message key="tablehandle_addrow_up"/>",
				tablehandle_addrow_down:"<i18n:message key="tablehandle_addrow_down"/>",
				tablehandle_addcol_left:"<i18n:message key="tablehandle_addcol_left"/>",
				tablehandle_addcol_right:"<i18n:message key="tablehandle_addcol_right"/>",
				tablehandle_delrow:"<i18n:message key="tablehandle_delrow"/>",
				tablehandle_delcol:"<i18n:message key="tablehandle_delcol"/>",
				tablehandle_mergcell:"<i18n:message key="tablehandle_mergcell"/>",
				tablehandle_splitrow:"<i18n:message key="tablehandle_splitrow"/>",
				tablehandle_splitcol:"<i18n:message key="tablehandle_splitcol"/>",
				tablehandle_delcell: "<i18n:message key="tablehandle_delcell"/>",
				tablehandle_mergdown:"<i18n:message key="tablehandle_mergdown"/>",
				tablehandle_mergright:"<i18n:message key="tablehandle_mergright"/>",

				tablehandle_merg_tiptext:"<i18n:message key="tablehandle_merg_tiptext"/>",
				tablehandle_celldelall_tiptext:"<i18n:message key="tablehandle_celldelall_tiptext"/>",
				tablehandle_selectcellone_tiptext: "<i18n:message key="tablehandle_selectcellone_tiptext"/>",
				tablehandle_dontdelonlycol: "<i18n:message key="tablehandle_dontdelonlycol"/>",
				tablehandle_dontdelonlyrow: "<i18n:message key="tablehandle_dontdelonlyrow"/>",
					
				tablehandle_removefield:"<i18n:message key="tablehandle_removefield"/>",
				operation_success:"<i18n:message key="oper.success"/>",
				operation_fail:"<i18n:message key="oper.faield"/>",
				modifyfield:"<i18n:message key="e5dom.form.modifyfield"/>",
				inserttext:"<i18n:message key="e5dom.form.inserttext"/>",
				btnselect:"<i18n:message key="e5dom.form.custom.btnselect"/>",
				province:"<i18n:message key="e5dom.form.custom.province"/>",
				city:"<i18n:message key="e5dom.form.custom.city"/>",
				area:"<i18n:message key="e5dom.form.custom.area"/>",
				street:"<i18n:message key="e5dom.form.custom.street"/>",
				building:"<i18n:message key="e5dom.form.custom.building"/>",
				demodate:"<i18n:message key="e5dom.form.custom.demodate"/>",
				selectdept:"<i18n:message key="e5dom.form.custom.selectdept"/>",
				selectusers:"<i18n:message key="e5dom.form.custom.selectusers"/>",
				selectcatgory:"<i18n:message key="e5dom.form.custom.selectcatgory"/>",
				pleaseinserttext:"<i18n:message key="e5dom.form.custom.pleaseinserttext"/>",
				btnsave:"<i18n:message key="e5dom.form.custom.btnsave"/>",
				btnsavesubmit:"<i18n:message key="e5dom.form.custom.btnsavesubmit"/>",
				xmlerror:"<i18n:message key="e5dom.form.custom.xmlerror"/>",
				option1:"<i18n:message key="e5dom.custom.option1"/>",
				option2:"<i18n:message key="e5dom.custom.option2"/>",
				regoption_success:"<i18n:message key="e5dom.custom.btn.regoption.success"/>",
				regoption_failed:"<i18n:message key="e5dom.custom.btn.regoption.failed"/>",
				regoption_title:"<i18n:message key="e5dom.custom.btn.regoption.title"/>",
				tablehandle_removefield:"<i18n:message key="tablehandle_removefield"/>",
				btncancel:"<i18n:message key="e5dom.form.custom.btncancel"/>",
				selectrole:"<i18n:message key="e5dom.form.custom.selectrole"/>",
				showlabel:"<i18n:message key="e5dom.detailView.filedlableshow"/>",
				preview:"<i18n:message key="e5dom.form.custom.predetail"/>"
			};
		</script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/dialog.style.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
		<link type="text/css" rel="stylesheet" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
		<link href="../e5script/jquery/jquery-tablehandle/jquery.tablehandle.css" rel="stylesheet" type="text/css" />
		<link href="css/form.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div class="field-container">
			<div class="menu">
				<h2><i18n:message key="e5dom.custom.form.viewdetail.title"/></h2>
				<div class="content">
					<i18n:message key="e5dom.form.docTypeName"/>:<span id="txtDocTypeName"></span>
					<i18n:message key="e5dom.custom.formname"/>:<span id="txtFormName"></span>
					<i18n:message key="e5dom.custom.formcode"/>:<span id="txtFormCode"></span>
				</div>
			</div>
			<div class="mainContent">
				<div class="sidebar">
					<h2><i18n:message key="e5dom.custom.form.editor.fields"/></h2>
					<div class="search-field"><input id="txt-field-search" type="text" /></div>
					<ul id="listfield" class="field-list clearfix">
					</ul>
					<ul id="listCust"  class="field-list-cust clearfix">
						<li class="drop-class text-icon" fieldtype="-4" fieldcode="inserttxt"><i18n:message key="e5dom.form.inserttext"/></li>
						<li class="drop-class hr-icon" fieldtype="-5" fieldcode="inserthr"><i18n:message key="e5dom.form.custom.inserthr"/></li>
					</ul>
				</div>
				<div class="workspace">
					<h2><i18n:message key="e5dom.custom.form.editor.fieldedit"/><span class="formfieldtooltip"><i18n:message key="e5dom.custom.form.fieldtooltip"/></span></h2>
					<div class="content" id="tablecontent">
						<div id="tableContainer" >
							<table id="tbcontent" class="table tablecontent" cellspacing="0" cellpadding="0" style="width:100%;height:100%">
								<tr><td>&nbsp;</td></tr>
								<tr><td>&nbsp;</td></tr>
								<tr><td>&nbsp;</td></tr>
								<tr><td>&nbsp;</td></tr>
								<tr><td>&nbsp;</td></tr>
								<tr><td>&nbsp;</td></tr>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="header">
			
			    <input id="btnpre" class="button" type="button" value="<i18n:message key="e5dom.custom.btnpre"/>" />
				<input id="btnSave" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btnsave"/>" />
				<input id="btnCancel" class="button" type="button" value="<i18n:message key="e5dom.form.custom.btncancel"/>" />
				<input id="btnPreview" class="button" type="button" value="<i18n:message key="e5dom.custom.btn.preview"/>" />
				
			</div>
		</div>
		<div class="size-tooltip-container">
			<div class="size-tooltip-text">100px</div>
			<div class="size-tooltip-line "></div>
		</div>

	</body>
</html>