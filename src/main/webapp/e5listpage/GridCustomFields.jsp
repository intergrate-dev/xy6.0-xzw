<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.contextmenu.r2.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-tablehandle/jquery.tablehandle.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
		<script type="text/javascript">
			var i18n = {
			sortWay:"<i18n:message key="ListPageCustomFields.field.fieldsortway"/>",
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

			tablehandle_merg_tiptext:"<i18n:message key="tablehandle_merg_tiptext"/>",
			tablehandle_celldelall_tiptext:"<i18n:message key="tablehandle_celldelall_tiptext"/>",
			tablehandle_selectcellone_tiptext: "<i18n:message key="tablehandle_selectcellone_tiptext"/>",
			tablehandle_dontdelonlycol: "<i18n:message key="tablehandle_dontdelonlycol"/>",
			tablehandle_dontdelonlyrow: "<i18n:message key="tablehandle_dontdelonlyrow"/>",
			tablehandle_formatimg: "<i18n:message key="tablehandle_formatimg"/>",
			tablehandle_removeimgformat: "<i18n:message key="tablehandle_removeimgformat"/>",
			tablehandle_recover:"<i18n:message key="tablehandle_recover"/>",
			tablehandle_removefield:"<i18n:message key="tablehandle_removefield"/>",
			tablehandle_mergdown:"<i18n:message key="tablehandle_mergdown"/>",
			tablehandle_mergright:"<i18n:message key="tablehandle_mergright"/>",
			operation_success:"<i18n:message key="pagelist.oper.success"/>",
			operation_fail:"<i18n:message key="pagelist.oper.faield"/>",
			field_types:"<i18n:message key="ListPageCustomFields.field.types"/>",
			field_showtype:"<i18n:message key="ListPageCustomFields.field.showtype"/>",
			field_tooltip:"<i18n:message key="ListPageCustomFields.field.tooltip"/>",
			field_adddecied:"<i18n:message key="ListPageCustomFields.field.adddecied"/>",
			field_add:"<i18n:message key="ListPageCustomFields.field.add"/>",
			field_add1 : "<i18n:message key="ListPageCustomFields.field.add1"/>",
			field_add2 : "<i18n:message key="ListPageCustomFields.field.add2"/>",
			field_customfield:"<i18n:message key="ListPageCustomFields.field.customfield"/>",
			field_show:"<i18n:message key="ListPageCustomFields.field.show"/>",
			field_selection:"<i18n:message key="ListPageCustomFields.field.selecticon"/>",
			template:"<i18n:message key="ListPageCustomFields.field.template"/>",
			selecticon : "<i18n:message key="ListPageCustomFields.field.selecticon"/>",
			confirmok : "<i18n:message key="ListPageCustomFields.field.confirmok"/>",
			valueof : "<i18n:message key="listpage.decied.valueof"/>",
			showimg : "<i18n:message key="listpage.decied.showimg"/>",
			showtext : "<i18n:message key="listpage.decied.showtext"/>",
			imgtexttooltip : "<i18n:message key="listpage.decied.imgtexttooltip"/>",
			onvalue:"<i18n:message key="listpage.decied.onvalue"/>",
			_delete : "<i18n:message key="ListPageCustomFields.field.delete"/>",
			widthnumber:"<i18n:message key="listpage.validate.widthnumber"/>",
			colnumname:"<i18n:message key="listpage.validate.colnumname"/>",
			charnumberchiness:"<i18n:message key="listpage.validate.charnumberchiness"/>",
			existfield:"<i18n:message key="ListPageCustomFields.field.existfield"/>", 
			hasfield:"<i18n:message key="ListPageCustomFields.field.hasfield"/>", 
			sdbsort:"<i18n:message key="listpage.sort.dbsort"/>",
			sfdesc:"<i18n:message key="listpage.sort.fdesc"/>",
			sfasc:"<i18n:message key="listpage.sort.fasc"/>",
			sfsortway:"<i18n:message key="listpage.sort.fsortway"/>",
			sfstitle:"<i18n:message key="listpage.sort.fstitle"/>",
			sftitle:"<i18n:message key="listpage.sort.ftitle"/>",
			sfall:"<i18n:message key="listpage.sort.fall"/>",
			sfsort:"<i18n:message key="listpage.sort.fsort"/>",
			sfbtnok:"<i18n:message key="listpage.sort.fbtnok"/>",
			sfbtncancle:"<i18n:message key="listpage.sort.fbtncancle"/>",
			field_delete:"<i18n:message key="ListPageCustomFields.field.delete"/>"

			};
		</script>
		<script type="text/javascript" src="js/CustomUtils.js"></script>
		<script type="text/javascript" src="js/GridTableTemplate.js"></script>
		<script type="text/javascript" src="js/GridCustomFields.js"></script>
		
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/dialog.style.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/e5-list.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-tablehandle/jquery.tablehandle.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div class="field-container">
			<!-- 列表属性区域 -->
			<div class="table-attr-area">
				<h2><i18n:message key="ListPageCustomFields.field.listiconattr"/></h2>
				<div class="list-attr interval clearfix">
					<label><i18n:message key="ListPageCustomFields.field.sort"/>:</label>
					<input id="txt-field-sort" type="text" value="" readonly="readonly" />
					<select id="sl-sortfield" style="display:none;"></select>
					<button id="btnFieldSort" class="button"><i18n:message key="listpage.sort.fsort"/></button>
					<input id="cb-lineindex" type="checkbox" />
					<label for="cb-lineindex"><i18n:message key="ListPageCustomFields.field.hasindex"/></label>
					<input id="cb-checkbox" type="checkbox" />
					<label for="cb-checkbox"><i18n:message key="ListPageCustomFields.field.hascheckbox"/></label>
					<label><i18n:message key="ListPageCustomFields.field.pagenum"/>:</label>
					<input id="txt-field-pagenum" type="text" value="20,50,100" />
					<input id="txt-template" class="button" type="button" value="<i18n:message key="ListPageCustomFields.field.template"/>" />
				</div>
			</div>
			<div class="field-content-container" id="field-content-container">
				<!-- 字段区域 -->
				<div class="field-area" id="field-area">
					<h2><i18n:message key="ListPageCustomFields.field.layout.fieldselect"/></h2>
					<div class="search-field"><input id="txt-field-search" type="text" /></div>
					<ul id="field-list" class="field-list clearfix"></ul>
				</div>
				<div class="field-workspace-container" id="field-workspace-container">
					<h2><i18n:message key="ListPageCustomFields.field.title"/></h2>
					<!-- 可视化操作区域 -->
					<div class="visual-grid">
						<table id="grid-custom" class="grid-custom">
							<tr>
							<td>&nbsp;</td>
							</tr>
							<tr>
							<td>&nbsp;</td>
							</tr>
							<tr>
							<td>&nbsp;</td>
							</tr>
						</table>
					</div>
					<!-- 字段属性区域 -->
					<div class="field-attr-area-grid">
						<div class="field-attr" id="field-attr">
							<!-- <div class="visual-draphelp" id="fielddraphelp"><i18n:message key="ListPageCustomFields.field.draphelp"/></div> -->
							
							<table cellpadding="0" cellspacing="0" class="table list-row">
								
								<tr style="display:none;">
									<th><i18n:message key="ListPageCustomFields.field.width"/>:</th>
									<td><input id="txt-field-width" type="text"  />px</td>
								</tr>
								<tr id="img_container">
									<th style="vertical-align:top;"><i18n:message key="grid_img_url"/>:</th>
									<td class="wrap"><textarea id="txt-img-url" type="text" class="grid-img-url"></textarea>
									<br/><i18n:message key="ListPageCustomFields.gird.example"/><i>../e5workspace/binary.do?TableName=MYTABLE&KeyName=KEYFIELD&FieldName=IMG_BLOB&KeyID=%PHOTO_REFID%</i>
									<br/><i>../e5workspace/bfile.do?TableName=MYTABLE&KeyName=KEYFIELD&FieldName=IMG_BFILE&KeyID=%PHOTO_REFID%</i>
									</td>
								</tr>
								<tr>
									<th><i18n:message key="ListPageCustomFields.field.class"/>:</th>
									<td><input id="txt-field-class" type="text" /></td>
								</tr>
								<tr>
									<th><i18n:message key="ListPageCustomFields.field.wrap"/></th>
									<td class="cb-field-nowrap-td">
										<input id="cb-field-nowrap" type="checkbox" class="checkbox"/>
										<label class="cb-field-nowrap-label" for="cb-field-nowrap"><i18n:message key="ListPageCustomFields.field.wrap"/></label>
									</td>
								</tr>
								<tr>
									<th><i18n:message key="ListPageCustomFields.field.showcontent"/>:</th>
									<td><textarea id="txt-showcontent"></textarea></td>
								</tr>
								<tr>
									<th valign="top"><i18n:message key="ListPageCustomFields.field.define"/>:</th>
									<td class="wrap">
										<span class="title">
											<i18n:message key="ListPageCustomFields.field.draphelp1"/>
										</span>
										<div class="list-row" id="fielddraphelp">
											<div class="field-attr-fields-tabs" id="field-attr-fields-tabs-wrap">
												<ul id="field-attr-fields-tabs" class="clearfix"></ul>
											</div>
											<div id="field-attr-fields" class="field-attr-fields"></div>
											<!-- <div class="list-row" id="field-attr-fields"></div> -->
										</div>
									</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 按钮区域 -->
		<div class="btn-area" id="btn-area">
			<input id="btn-cancle" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.cancle"/>" /> 
			<input id="btn-pre" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.pre"/>" />
			<input id="btn-complete" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.complete"/>" />
		</div>
		<div class="icon-list-container" id="icon-list">
			<ul style="overflow:auto;"></ul>
		</div>
		<div class="grid-tpl" id="template-list">
			<ul class="grid-tpl-list"></ul>
		</div>
		 <div class="sort_container">
  <div class="sort_header"><h2><i18n:message key="listpage.sort.fsort"/></h2></div>
  <div class="sort_mainContent">
    <div class="sort_right">
        <div class="sort_title"><i18n:message key="listpage.sort.fall"/></div>
		<div class="sort_list_container">
			<ul id="AllSortFieldsList" class="sort_ul">
			</ul>
		</div>
    </div>
    <div class="sort_left">
        <div class="sort_title"><i18n:message key="listpage.sort.fsort"/></div>
		<div class="sort_list_container">
			<ul id="SortFieldsList" class="sort_ul">
				<!-- <li><div class="sort_li_right">&nbsp;&nbsp;</div><div class="sort_li_left">姓名</div></li> -->
			</ul>
		</div>
    </div>
  </div>
  <div class="sort_footer">
      <button id="btnSortOK" class="button"><i18n:message key="listpage.sort.fbtnok"/></button>
      <button id="btnSortCancel" class="button"><i18n:message key="listpage.sort.fbtncancle"/></button>
  </div>
</div>
	</body>
</html>