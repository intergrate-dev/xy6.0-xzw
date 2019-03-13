<style type="text/css">
	
button, input, select, textarea {
    font-family: inherit;
    font-size: inherit;
    line-height: inherit;
}
</style>
<div id="main_toolbar">
	<div id="toolTable" class="overflow">
		<button type="button" id="toolTRLeft" class="btn left"><i class="icon-chevron-left"></i></button>
		<ul id="toolTR"></ul>
		<button type="button" id="toolTRRight" class="btn right"><i class="icon-chevron-right"></i></button>
	</div>
</div>
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
<c:choose>
	<%--站点管理需要区分前后台--%>
	<c:when test="${extParams == 'xyFront'}">
		<script language="javascript" type="text/javascript" src="../xy/script/ToolkitSite.js"></script>
		<script type="text/javascript">
			const isFront = true;
		</script>
	</c:when>
	<c:when test="${extParams == 'xyBack'}">
		<script language="javascript" type="text/javascript" src="../xy/script/ToolkitSite.js"></script>
		<script type="text/javascript">
            const isFront = false;
		</script>
	</c:when>

	<c:otherwise>
		<script language="javascript" type="text/javascript" src="../xy/script/Toolkit.js"></script>
	</c:otherwise>
</c:choose>
<script language="javascript" type="text/javascript" src="../xy/script/Toolkit.js"></script>
<script language="javascript" type="text/javascript" src="../xy/script/Toolkit.js"></script>