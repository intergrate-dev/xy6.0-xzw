<style type="text/css" >
button, input, select, textarea {
    font-family: inherit;
    font-size: inherit;
    line-height: inherit;
}
</style>
<div id="main_toolbar">
	<div id="toolTable" class="overflow">
        <ul id="toolTR" class="pull-left">
        </ul>
    </div>
</div>
<input id="topicID" type="hidden" value="${topicID}" />
<ul id="toolMenu" class="dropdown-menu"></ul>
<ul id="toolArticleBtns" class="dropdown-menu"></ul>
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
<script language="javascript" type="text/javascript" src="../xy/script/ToolkitTopicArticle.js"></script>