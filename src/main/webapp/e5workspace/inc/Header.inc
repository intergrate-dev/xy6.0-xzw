<a class="logo" href="#">E5基础平台V3.0</a>
<div class="hdOperateArea">
	<a id="hdConfig" class="hdspan"><i18n:message key="workspace.personconfig"/></a>
	<a id="hdMsg" class="hdspan"><i18n:message key="workspace.message"/></a>
	<a id="hdRefresh" class="hdspan"><i18n:message key="workspace.refresh"/></a>
	<a id="hdRelogin" class="hdspan"><i18n:message key="workspace.relogin"/></a>
	<a id="hdExit" class="hdspan"><i18n:message key="workspace.exit"/></a>
	<a><i18n:message key="workspace.login.role"/></a>
	<select id="hdRoles" name="hdRoles"></select>
</div>
<script type="text/javascript">
	var headerinfo = {
		confirm : "<i18n:message key="workspace.confirm.exit"/>",
		
		novaliduser : "<i18n:message key="workspace.change.nouser"/>",
		nouser : "<i18n:message key="workspace.change.nouser"/>",
		novalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		invalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		adminaskquit : "<i18n:message key="workspace.change.adminaskquit"/>",
		haserror : "<i18n:message key="workspace.login.haserror"/>",
		otheragain : "<i18n:message key="workspace.login.otheragain"/>",
		i18n_lastIp : "<i18n:message key="workspace.message.lastIp"/>",
		i18n_lastTime : "<i18n:message key="workspace.message.lastTime"/>",
		i18n_nowIp : "<i18n:message key="workspace.message.nowIp"/>"
	}
</script>
<script type="text/javascript" src="../e5workspace/script/header.js"></script>