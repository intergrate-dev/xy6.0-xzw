<div id="main_doclist">
	<ul id="status" class="status">
		<li><i18n:message key="workspace.status.count"/>&nbsp;[&nbsp;<span id="PageCount"></span>&nbsp;]&nbsp;&nbsp;</li>
		<li style="display:none"><i18n:message key="workspace.status.pages"/>
			[&nbsp;<span id="CurrentPage"></span><span id="pageSepr">&nbsp;/&nbsp;</span><span id="Pages">1</span>&nbsp;]</span>&nbsp;&nbsp;
		</li>
		<li class="separator-status">&nbsp;|&nbsp;</li>
		
		<li id="btnFirstpage" class="page-unselected" title="<i18n:message key="workspace.status.firstpage"/>"><<</li>
		<li id="btnLastpage" class="page-unselected" title="<i18n:message key="workspace.status.lastpage"/>"><</li>
		<li id="pagesArea"></li>
		<li id="btnNextpage" class="page-unselected" title="<i18n:message key="workspace.status.nextpage"/>">></li>
		<li id="btnFinalpage" class="page-unselected" title="<i18n:message key="workspace.status.finalpage"/>">>></li>
		<li id="btnChangepage"><!--<i18n:message key="workspace.status.turntopage"/>-->
			<input type="text" size="2" maxlength="10" id="Turn2Page" value="" title="<i18n:message key="workspace.status.turntopage.title"/>"/>
		</li>
		<li class="separator-status">&nbsp;|&nbsp;</li>

		<li><!--<i18n:message key="workspace.status.countperpage"/>-->
			<select id="CountPerPage" name="CountPerPage" title="<i18n:message key="workspace.status.countperpage.title"/>"></select>&nbsp;&nbsp;
		</li>
		<li class="separator-status">&nbsp;|&nbsp;</li>
		<li id="btnRefresh">
			<table cellspacing="0" cellpadding="0" class="btnRefresh fillet btn" title="<i18n:message key="workspace.status.refresh"/>">
				<tr>
					<td class="L"> </td>
					<td class="C"><div><i18n:message key="workspace.status.refresh"/></div></td>
					<td class="R"> </td>
				</tr>
			</table>
		</li>
		<li id="ListCustSpan">
			<table cellspacing="0" cellpadding="0" class="listConfig fillet btn" title="<i18n:message key="workspace.status.listpage.config.title"/>">
				<tr>
					<td class="L"> </td>
					<td class="C"><div><i18n:message key="workspace.status.listpage.config.title"/></td>
					<td class="R"> </td>
				</tr>
			</table>
		</li>
		<li id="ListExportSpan">
			<table cellspacing="0" cellpadding="0" class="export fillet btn" title="<i18n:message key="workspace.status.listpage.export.title"/>">
				<tr>
					<td class="L"> </td>
					<td class="C"><div><i18n:message key="workspace.status.listpage.export.title"/></td>
					<td class="R"> </td>
				</tr>
			</table>
		</li>
		<li id="ListSeparator" class="separator-status">&nbsp;|&nbsp;</li>
		<li id="ListRemember">
			<table cellspacing="0" cellpadding="0" class="remember fillet btn" title="<i18n:message key="workspace.status.listpage.remember.title"/>">
				<tr>
					<td class="L"> </td>
					<td class="C"><div><i18n:message key="workspace.status.listpage.remember.title"/></td>
					<td class="R"> </td>
				</tr>
			</table>
		</li>
		<li id="ListRemClear">
			<table cellspacing="0" cellpadding="0" class="remclear fillet btn" title="<i18n:message key="workspace.status.listpage.remember.clear.title"/>">
				<tr>
					<td class="L"> </td>
					<td class="C"><div><i18n:message key="workspace.status.listpage.remember.clear.title"/></td>
					<td class="R"> </td>
				</tr>
			</table>
		</li>
		<input name="ListPages" id="ListPages" type="hidden" value=""/>
		<li id="listPageArea" title="<i18n:message key="workspace.status.listpage.title"/>"></li>
	</ul>
	<div id="doclistframe" class="doclistframe"></div>
</div>
<script type="text/javascript">
	var statuserror = {
		"invalidPage": "<i18n:message key="workspace.status.error.invalidPage"/>",
		"noListPage" : "<i18n:message key="workspace.status.error.noListPage"/>"
	}
	var statusinfo = {
		"listcustom" : "<i18n:message key="workspace.status.listpage.config.title"/>",
		"export" : "<i18n:message key="workspace.status.listpage.export.title"/>",
		"remember_ok": "<i18n:message key="workspace.status.listpage.remember.ok"/>"
	}
</script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Statusbar.js"></script>
