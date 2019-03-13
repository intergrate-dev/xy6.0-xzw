<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="utf-8"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<title>ProcOrder Config</title>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
	<script type="text/javascript">
	var i18n = {
			resetsuccess:"<i18n:message key="flow.proc.btn.resetsuccess"/>",
			resetfailed:"<i18n:message key="flow.proc.btn.resetfailed"/>",
			savesucess:"<i18n:message key="flow.proc.btn.savesucess"/>",
			savefailed:"<i18n:message key="flow.proc.btn.savefailed"/>"
	
	};
	</script>
	<script type="text/javascript" src="ProcOrder.js"></script>
	<link href="../e5style/reset.css" rel="stylesheet" type="text/css" />
	<link href="../e5style/sys-main-body-style.css" rel="stylesheet" type="text/css" />
	<link href="../e5script/jquery/jquery-ui/jquery-ui.custom.css" rel="stylesheet" type="text/css" />
	<link href="../e5style/e5-flow.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<form name="postForm" method="post" action="">
		<div class="mainBodyWrap">
			<table class="table">
				<caption><i18n:message key="operation.csproc.contentTitle"/></caption>
				<tr>
					<td>
						<i18n:message key="flow.proc.btn.tooltip"/>
						<ul id="icon_sortlist" class="icon_sortlist"></ul>
					</td>
				</tr>
				<tr>
					<td class="alignCenter">
						<input class="button" type="button" id="btnSubmit" value="<i18n:message key="operation.proc.submit"/>" />
						<input class="button" type="button" id="btnReset"  value="<i18n:message key="operation.proc.reset"/>" />
						<input class="button" type="button" id="btnGroup"  value="<i18n:message key="operation.proc.procGroup"/>" />
					</td>
				</tr>
			</table>		
		</div>
	</form>
</body>
</html>
