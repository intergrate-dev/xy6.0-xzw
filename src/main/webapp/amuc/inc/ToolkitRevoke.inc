<style>
	.btn-small{
		padding-left: 20px;
	}
	#toolTR2{
		 margin: 5px 0 0 5px;
	}
	li{
		list-style: none;
	}
	#toolTR{
		margin:3px 0 0 0 !important;
	}
</style>
<div id="main_toolbar">
	<div id="toolTable" class="overflow">
		<ul id="toolTR2" class="pull-left">
			<li class="tIconBText" title="批量撤稿" opid="3">
				<button id = "revokeBat" class="btn btn-small" type="button">批量撤稿</button>
			</li>
		</ul>
		<ul id="toolTR" class="pull-left">
			<li class="toolButton tIconBText" title="撤稿" opid="0">
				<button class="btn btn-small" type="button">撤稿</button>
			</li>
			<li class="toolButton tIconBText" title="彻底删除" opid="1">
				<button class="btn btn-small" type="button">彻底删除</button>
			</li>
		</ul>
	</div>
</div>
<ul id="toolMenu" class="dropdown-menu"></ul>
<script type="text/javascript">
	var toolhint = {
		sure		:	"<i18n:message key="workspace.toolkit.hint.sure"/>",
		stateChange : 	"<i18n:message key="workspace.toolkit.error.stateChange"/>",
		allLocked : 	"<i18n:message key="workspace.toolkit.error.allLocked"/>",
		lockContinue : 	"<i18n:message key="workspace.toolkit.error.lockContinue"/>",
		exception : 	"<i18n:message key="workspace.toolkit.error.exception"/>"
	};
</script>
<script language="javascript" type="text/javascript" src="script/ToolkitRevoke.js"></script>