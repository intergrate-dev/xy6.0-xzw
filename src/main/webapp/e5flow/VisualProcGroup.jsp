<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title></title>
		<meta content="text/html;charset=utf-8" http-equiv="content-type">
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<script type="text/javascript" src="../e5script/e5.utils.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
		<script type="text/javascript">
			var i18n = {
				opt_success:"<i18n:message key="flow.group.opt.success"/>",
				opt_failed:"<i18n:message key="flow.group.opt.failed"/>",
				opt_confirm:"<i18n:message key="flow.group.tab.opt.groupconfirm"/>",
				opt_procconfirm:"<i18n:message key="flow.group.tab.opt.procconfirm"/>",
				opt_hassameproc:"<i18n:message key="flow.group.tab.opt.hassame"/>",
				opt_del:"<i18n:message key="flow.group.tab.opt.del"/>",
				opt_tab_name:"<i18n:message key="flow.group.tab.name"/>",
				id:""
			};
		</script>
		<script type="text/javascript" src="VisualProcGroup.js"></script>
		<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
		<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
		<link href="../e5style/e5-flow.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<form name="visualprocgroup" id="visualprocgroup" method="post" action="">
			<div class="mainBodyWrap">
				<div class="visual-proc-group-sidebar">
					<table class="table">
						<caption><i18n:message key="flow.group.proc.title"/></caption>
						<tr>
							<td>
								<i18n:message key="flow.group.proc.title.tooltip"/>
								<ul id="icon_sortlist" class="icon_sortlist"></ul>
							</td>
						</tr>
					</table>
				</div>
				<div class="visual-proc-group-main">
					<table class="table">
						<caption><i18n:message key="flow.group.tab.title"/></caption>
						<tr>
							<td>
								<i18n:message key="flow.group.tab.title.tooltip"/>
								<div class="tab-container">
									<ul id="tab_list" class="tabs clearfix">
										<li id="tab_add" class="add tab"><a><img src="../images/tab_add.gif" alt="<i18n:message key="flow.group.tab.opt.add"/>" title="<i18n:message key="flow.group.tab.opt.add"/>" /></a></li>
									</ul>
									<div class="panes clearfix" id="tabcontent_container">
										<ul id="tabconten_list" class="pane icon_sortlist"></ul>
									</div>
								</div>
							</td>
						</tr>
					</table>
				</div>
				<div class="group-btn-container">
					<input type="button" id="btnReset" class="button" value="<i18n:message key="operation.proc.reset"/>" />
					<input type="button" id="btnSave" class="button" value="<i18n:message key="operation.proc.submit"/>" />
				</div>
			</div>
		</form>
	</body>
</html>
