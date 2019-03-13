<div id="main_doclist" style="overflow:hidden;">
	<div id="doclistframe" class="doclistframe" unselectable="on" onselectstart="return false;"></div>	
	<ul id="status" class="status clearfix">
		<li class="status-pages"><i18n:message key="workspace.status.count"/>&nbsp;[&nbsp;<span id="PageCount"></span>&nbsp;]&nbsp;&nbsp;</li>
		<li style="display:none"><i18n:message key="workspace.status.pages"/>
			[&nbsp;<span id="CurrentPage"></span><span id="pageSepr">&nbsp;/&nbsp;</span><span id="Pages">1</span>&nbsp;]</span>&nbsp;&nbsp;
		</li>
		<li id="btnChangepage">&nbsp;<input type="text" maxlength="10" id="Turn2Page" title="<i18n:message key="workspace.status.turntopage.title"/>"/>&nbsp;</li>
		<li id="btnFirstpage" class="page-unselected" title="<i18n:message key="workspace.status.firstpage"/>"><<</li>
		<li id="btnLastpage" class="page-unselected"><i18n:message key="workspace.status.lastpage"/></li>
		<li id="pagesArea"></li>
		<li id="btnNextpage" class="page-unselected"><i18n:message key="workspace.status.nextpage"/></li>
		<li id="btnFinalpage" class="page-unselected" title="<i18n:message key="workspace.status.finalpage"/>">>></li>
		<li style="display:none">
			<select id="CountPerPage" name="CountPerPage" title="<i18n:message key="workspace.status.countperpage.title"/>"></select>&nbsp;&nbsp;
			<input name="ListPages" id="ListPages" type="hidden" value=""/>
		</li>
		<li id="listPageArea" title="<i18n:message key="workspace.status.listpage.title"/>"></li>
	</ul>
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
<script type="text/javascript" src="../xy/script/Statusbar.js"></script>