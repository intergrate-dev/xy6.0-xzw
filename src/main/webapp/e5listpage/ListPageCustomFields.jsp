<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.xml2json.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
		<script type="text/javascript">
			var i18n = {
				sortWay : "<i18n:message key="ListPageCustomFields.field.fieldsortway"/>",
				success : "<i18n:message key="pagelist.oper.success"/>",
				faield : "<i18n:message key="pagelist.oper.faield"/>",
				types : "<i18n:message key="ListPageCustomFields.field.types"/>",
				showtype : "<i18n:message key="ListPageCustomFields.field.showtype"/>",
				tooltip : "<i18n:message key="ListPageCustomFields.field.tooltip"/>",
				adddecied : "<i18n:message key="ListPageCustomFields.field.adddecied"/>",
				add : "<i18n:message key="ListPageCustomFields.field.add"/>",
				add1 : "<i18n:message key="ListPageCustomFields.field.add1"/>",
				add2 : "<i18n:message key="ListPageCustomFields.field.add2"/>",
				customfield : "<i18n:message key="ListPageCustomFields.field.customfield"/>",
				nocolumnsort : "<i18n:message key="ListPageCustomFields.field.nocolumnsort" />",
				show : "<i18n:message key="ListPageCustomFields.field.show"/>",
				selecticon : "<i18n:message key="ListPageCustomFields.field.selecticon"/>",
				numerror : "<i18n:message key="ListPageCustomFields.field.pagenumerror"/>",
				confirmok : "<i18n:message key="ListPageCustomFields.field.confirmok"/>",
				closetip : "<i18n:message key="ListPageCustomFields.field.closetooltip"/>",
				valueof : "<i18n:message key="listpage.decied.valueof"/>",
				showimg : "<i18n:message key="listpage.decied.showimg"/>",
				showtext : "<i18n:message key="listpage.decied.showtext"/>",
				imgtexttooltip : "<i18n:message key="listpage.decied.imgtexttooltip"/>",
				onvalue:"<i18n:message key="listpage.decied.onvalue"/>",
				widthnumber:"<i18n:message key="listpage.validate.widthnumber"/>",
				colnumname:"<i18n:message key="listpage.validate.colnumname"/>",
				//charnumberchiness:"<i18n:message key="listpage.validate.charnumberchiness"/>",
				charnumberchiness:"<i18n:message key="inputvalidate.specialchar"/>",
				validatespecialchar:"<i18n:message key="inputvalidate.specialchar"/>",
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
				_delete : "<i18n:message key="ListPageCustomFields.field.delete"/>"
			};
		</script>
		<script type="text/javascript" src="js/CustomUtils.js"></script>
		<script type="text/javascript" src="js/ListPageCustomFields.js"></script>

		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/dialog.style.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/e5-list.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div class="field-container">
			<!-- 列表属性区域 -->
			<div class="table-attr-area">
				<h2><i18n:message key="ListPageCustomFields.field.listattr"/></h2>
				<div class="list-attr interval clearfix">
					<label><i18n:message key="ListPageCustomFields.field.sort"/>:</label>
					<input id="txt-field-sort" type="text" value="" readonly="readonly" />
					<select id="sl-sortfield" style="display:none;"></select>
					<!--
					
					<select id="sl-sortfield-way"></select>
					-->
					<button id="btnFieldSort" class="button"><i18n:message key="listpage.sort.fsort"/></button>
					<input id="cb-lineindex" type="checkbox" />
					<label for="cb-lineindex"><i18n:message key="ListPageCustomFields.field.hasindex"/></label>
					<input id="cb-checkbox" type="checkbox" />
					<label for="cb-checkbox"><i18n:message key="ListPageCustomFields.field.hascheckbox"/></label>
					<label><i18n:message key="ListPageCustomFields.field.pagenum"/>:</label>
					<input id="txt-field-pagenum" title="<i18n:message key="ListPageCustomFields.field.pagenumtitle"/>" type="text" value="20,50,100" />
					<span class="field-help-tootip" title="<i18n:message key="ListPageCustomFields.field.pagenumtitle"/>">[?]</span>
					
				</div>
				
			</div>
			<div class="field-content-container" id="field-content-container">
				<!-- 字段选择区域 -->
				<div class="field-area" id="field-area">
					<h2><i18n:message key="ListPageCustomFields.field.layout.fieldselect"/></h2>
					<div class="search-field"><input id="txt-field-search" type="text" /></div>
					<ul id="field-list" class="field-list clearfix"></ul>
				</div>
				<div class="field-workspace-container" id="field-workspace-container">
					<h2><i18n:message key="ListPageCustomFields.field.title"/></h2>
				    <!-- 可视化操作区域 -->
					<div class="visual-area interval">
						<div class="visual-draphelp" id="draphelp">
							<span class="title">
								<i18n:message key="ListPageCustomFields.field.draphelp"/>
							</span>
							<button id="up" type="button" class="draphelp-button-left"><--</button>
							<button id="down" type="button" class="draphelp-button-right">--></button>
							<div id="column-list-wrap">
								<ul id="column-list" class="clearfix"></ul>
							</div>
						</div>
				    </div>
					<!-- 字段属性区域 -->
					<div id="field-attr-area">
						<div class="field-attr-arrow" id="field-attr-arrow"></div>
						<div class="field-attr " id="field-attr">
							<!--div class="list-row">
								<i18n:message key="ListPageCustomFields.field.review"/>:
								<span id="txt-field-review"><i18n:message key="ListPageCustomFields.field.review"/></span>
							</div-->
							<table cellpadding="0" cellspacing="0" class="table list-row">
								<tr>
									<th><span class="field-required">*</span><i18n:message key="ListPageCustomFields.field.name"/>:</th>
									<td><input id="txt-field-name" type="text" />
									<span class="field-help-tootip" title="<i18n:message key="inputvalidate.specialchar"/>">[?]</span>
									</td>
									
									<th><i18n:message key="ListPageCustomFields.field.code"/>:</th>
									<td><span id="txt-field-code"><span/></td>
								</tr>								
								<tr>
									<th><span class="field-required">*</span><i18n:message key="ListPageCustomFields.field.width"/>:</th>
									<td><input id="txt-field-width" type="text"   />
									<span class="field-help-tootip" title="<i18n:message key="listpage.validate.widthnumber"/>">[?]</span>
									</td>
									<th><i18n:message key="ListPageCustomFields.field.columnsort"/>:</th>
									<td><select id="sl-column-sortfield"></select></td>
								</tr>
								<tr class="advanced-option unfold">
									<th><i18n:message key="ListPageCustomFields.field.wraptitle"/>:</th>
									<td class="cb-field-nowrap-td">
										<input id="cb-field-nowrap" type="checkbox" class="checkbox" />
										<label class="cb-field-nowrap-label" for="cb-field-nowrap"><i18n:message key="ListPageCustomFields.field.wrap"/></label>
									</td>
									<th><i18n:message key="ListPageCustomFields.field.class"/>:</th>
									<td><input id="txt-field-class" type="text"  /></td>
								</tr>
								<tr class="advanced-option unfold">
									<th ><span class="field-required">*</span><i18n:message key="ListPageCustomFields.field.showcontent"/>:</th>
									<td colspan="3"><textarea id="txt-showcontent"></textarea></td>
								</tr>
								<tr class="advanced-option unfold">
									<th valign="top"><i18n:message key="ListPageCustomFields.field.define"/>:</th>
									<td colspan="3" class="wrap">
										<span class="title">
											<i18n:message key="ListPageCustomFields.field.draphelp1"/>
										</span>
										<div class="list-row" id="fielddraphelp">
											<div class="field-attr-fields-tabs" id="field-attr-fields-tabs-wrap">
												<ul id="field-attr-fields-tabs" class="clearfix"></ul>
											</div>
											<div id="field-attr-fields" class="field-attr-fields"></div>
										</div>
									</td>
								</tr>
							</table>
							<div class="list-row clearfix">
								<a href="#" id="fold-advanced-option" class="unfold"><i18n:message key="ListPageCustomFields.field.layout.fieldadvtooltip"/></a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- 按钮区域 -->
			<div class="btn-area" id="btn-area">
				<!--  <input id="btn-save" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.save"/>" />--> 
				<input id="btn-cancle" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.cancle"/>" /> 
				<input id="btn-pre" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.pre"/>" />
				<input id="btn-complete" class="button" type="button" value="<i18n:message key="ListPageCustomFields.button.complete"/>" />
			</div>
			<div class="size-tooltip-container">
				<div class="size-tooltip-text">100px</div>
				<div class="size-tooltip-line "></div>
			</div>
			<div class="icon-list-container" id="icon-list">
				<ul class="icon_sortlist"></ul>
			</div>
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