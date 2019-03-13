<%@include file="../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<TITLE></TITLE>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.jsPlumb-1.3.10-all-min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.contextmenu.r2.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript">
		var i18n = {
			exist: "<i18n:message key="flow.visual.node.exist"/>",
			define: "<i18n:message key="flow.visual.node.define"/>",
			success: "<i18n:message key="flow.visual.operation.success"/>",
			faield: "<i18n:message key="flow.visual.operation.faield"/>",
			dragtooltip: "<i18n:message key="flow.visual.node.dragtooltip"/>",
			actionsshowtooltip: "<i18n:message key="flow.visual.node.actionsshowtooltip"/>",
			tooltip: "<i18n:message key="flow.visual.delete.tooltip"/>",
			addselfaction: "<i18n:message key="flow.visual.node.context.addselfaction"/>",
			addflowsaction: "<i18n:message key="flow.visual.node.context.addflowsaction"/>",
			operationdefine:"<i18n:message key="flow.proc.layout.operationdefine"/>",
			existaction: "<i18n:message key="flow.visual.node.window.existaction"/>"
		};
	</script>
	<script type="text/javascript" src="js/e5flow-FlowVisualDefine.js"></script>
	<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />

	<link href="../e5script/jquery/dialog.style.css" rel="stylesheet" type="text/css" />
	<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
	<link href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
	<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
	<link href="../e5style/e5-flow.css" rel="stylesheet" type="text/css" />
	<style type="text/css">
		html,body{
			width:100%;
			height:100%;
			overflow: hidden;
		}
	</style>
</head>
<body>
	<form name="flowform1" id="flowform1" method="post" action="">
		<div class="container">
			<div class="toolbar">
				<h2 class="flowtitle"><i18n:message key="flow.visual.title"/></h2>
				<div class="clearfix">
					<label><span class="field-required">*</span><i18n:message key="flow.visual.flowname"/>:</label>
					<input class="validate[required,custom[onlyCharNumberChiness],maxSize[40]] text" type="text" id="txtFlowName"  />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span>
				</div>
			</div>
			<div class="flowcontent">
				<div class="sidebar">
					<h3 class="flowtitle"><i18n:message key="flow.proc.layout.nodeseleced"/></h3>
					<div class="sidebar-container">
						<ul>
							<li id="start" class="tools node-start">
								<i18n:message key="flow.visual.node.button.start"/>
							</li>
							<li id="end" class="tools node-end">
								<i18n:message key="flow.visual.node.button.node"/>
							</li>
						</ul>
					</div>
				</div>
				<div class="node-container-wrap">
					<h3 class="flowtitle"><i18n:message key="flow.proc.layout.operationarea"/></h3>
					<div class="node-container"></div>
				</div>
			</div>
			<div class="btn-area">
				<input class="button" type="button" value="<i18n:message key="flow.visual.node.window.submit"/>" id="btnFlowSave" />
			</div>
		</div>
		
		<!-- 右键菜单定义 -->
		<div class="contextMenu" id="nodeMenu">
			<ul>
				<li id="node_edit"><img alt="<i18n:message key="flow.visual.node.context.edit"/>" title="<i18n:message key="flow.visual.node.context.edit"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.edit"/></li>
				<li id="node_delete"><img alt="<i18n:message key="flow.visual.node.context.delete"/>" title="<i18n:message key="flow.visual.node.context.delete"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.delete"/></li> 
				<li id="action_add"><img alt="<i18n:message key="flow.visual.node.context.addselfaction"/>" title="<i18n:message key="flow.visual.node.context.addselfaction"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.addselfaction"/></li>
				<li id="action_flow"><img alt="<i18n:message key="flow.visual.node.context.addflowsaction"/>" title="<i18n:message key="flow.visual.node.context.addflowsaction"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.addflowsaction"/></li>
			</ul>
		</div>
		<div class="contextMenu" id="actionMenu">
			<ul>
				<li id="action_edit"><img alt="<i18n:message key="flow.visual.node.context.edit"/>" title="<i18n:message key="flow.visual.node.context.edit"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.edit"/></li>
				<li id="action_delete"><img alt="<i18n:message key="flow.visual.node.context.delete"/>" title="<i18n:message key="flow.visual.node.context.delete"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.delete"/></li> 
			</ul>
		</div>
		<div class="contextMenu" id="connActionMenu">
			<ul>
				<li id="connAction_edit"><img alt="<i18n:message key="flow.visual.node.context.edit"/>" title="<i18n:message key="flow.visual.node.context.edit"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.edit"/></li>
				<li id="connAction_delete"><img alt="<i18n:message key="flow.visual.node.context.delete"/>" title="<i18n:message key="flow.visual.node.context.delete"/>" src="../images/role.gif" /><i18n:message key="flow.visual.node.context.delete"/></li> 
			</ul>
		</div>
		<!-- 右键菜单定义 -->
		<input type="hidden" id="J_flowid" value="" />
	</form>
</body>
</html>
