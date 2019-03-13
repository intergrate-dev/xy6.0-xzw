<div id="main_toolbar">
	<span id="toolkitConfig" style="width:20px;display:none;">
		<img src="../images/toolkitConfig.gif" onclick="toolkitConfig()"
			alt="<i18n:message key="workspace.toolkit.config.hint"/>"></img>
	</span>
	<ul id="toolTabTR"></ul>
	<div id="toolTable"><ul id="toolTR"></ul></div>
</div>
<div id="toolMenu"><ul></ul></div>
<script type="text/javascript">
	var toolhint = {
		sure		:	"<i18n:message key="workspace.toolkit.hint.sure"/>",
		stateChange : 	"<i18n:message key="workspace.toolkit.error.stateChange"/>",
		allLocked : 	"<i18n:message key="workspace.toolkit.error.allLocked"/>",
		lockContinue : 	"<i18n:message key="workspace.toolkit.error.lockContinue"/>",
		exception : 	"<i18n:message key="workspace.toolkit.error.exception"/>",
		garbageRestore :"<i18n:message key="workspace.toolkit.garbagebin.restore"/>",
		garbageDelete : "<i18n:message key="workspace.toolkit.garbagebin.delete"/>",
		garbageLog : "<i18n:message key="workspace.toolkit.garbagebin.log"/>"
	};
</script>
<script language="javascript" type="text/javascript" src="../e5workspace/script/Toolkit.js"></script>