<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page import="java.net.InetAddress"%>
<html>
	<head>
		<meta content="text/html;charset=utf-8" http-equiv="Content-Type" />
		<meta content="IE=EmulateIE7" http-equiv="X-UA-Compatible" />
		<title><%=com.founder.e5.context.Context.getSystemName()%>-<i18n:message key="workspace.title"/></title>

		<script type="text/javascript" src="../e5script/e5.min.js"></script>
		<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
		<link rel="stylesheet" type="text/css" href="../e5style/ws-style<%=com.founder.e5.workspace.ProcHelper.getSkin(request)%>.css"/>
		<script type="text/javascript">
			var css_postfix = "<%=com.founder.e5.workspace.ProcHelper.getSkin(request)%>";
		</script>
	</head> 
	<body>
		<div id="warpMain">
			<div id="main_header">
				<%@include file="./inc/Header.inc"%>
			</div>
			<div id="main">
				<%@include file="./inc/ResourceTree.inc"%>
				<div id="main_right">
				<!--
					<div class="navtabbar">
						<table cellspacing="0" cellpadding="0" class="tab fillet">
							<td class="L"> </td>
							<td class="C"><span><i18n:message key="workspace.title"/></span></td>
							<td class="R"> </td>
						</table>
					</div>
				-->
					<%@include file="./inc/Search.inc"%>
					<div id="panContent" class="panContent">
						<div class="tabHr toolkitArea">
							<%@include file="./inc/Toolkit.inc"%>
						</div>
						<%@include file="./inc/Statusbar.inc"%>
					</div>
				</div>
			</div>
			<!-- <div id="main_footer">e5 v3.0.0 Copyright &copy; 2011</div> -->
		</div>
	</body>
	<script type="text/javascript" src="./script/main.onresize.js"></script>
	<script type="text/javascript" src="./script/doclist.onresize-for-main.js"></script>
</html>
