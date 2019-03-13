<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>
<%@page import="java.util.*" %>
<%@page import="com.founder.e5.web.WebUtil" %>
<%@page import="com.founder.e5.commons.TraceManager" %>
<%
	String action = request.getParameter( "action" );
	String thread = request.getParameter( "thread" );
	String location = request.getParameter( "codeloc" );
	boolean needFlag = WebUtil.getBoolParam( request, "needStack" );

	System.out.println("action=" + action + ", thread=" + thread + ", location="
			+ location + ", needStack=" + needFlag );

	if ( "add".equals( action ) ) {
		TraceManager.addEnabled( thread, location, needFlag );
	}
	else if ( "delete".equals( action ) ) {
		TraceManager.removeEnabled( thread, location );
	}

	Map map = TraceManager.enabledProbes();
	request.setAttribute( "map", map );

%>

<html>
<head>
	<title><i18n:message key="trace.config"/></title>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript">
		function toggle() {
			if ( document.getElementById( "addprobe" ) ) {
				var sty = document.getElementById( "addprobe" ).style;
				if ( sty.display == "none" )
					sty.display = "";
				else
					sty.display = "none";
			}
		}

		function onAdd() {
			var obj = document.tracer;
			obj.action.value = "add";
			obj.submit();
		}

		function onDel( btnNode ) {
			var trObj = btnNode.parentNode.parentNode;
			var tdArray = trObj.getElementsByTagName( "td" );
			var threadTd = tdArray[0];
			var codeLocTd = tdArray[1];
		
			var obj = document.tracer;
			obj.action.value = "delete";
			obj.thread.value = threadTd.innerHTML;
			obj.codeloc.value = codeLocTd.innerHTML;
			obj.submit();
		}
	</script>
</head>
<body>
<h1 style="display:none;"><i18n:message key="trace.config"/></h1>
<div class="mainBodyWrap">
<form name="tracer" method="post" action="traceCfg.jsp">
	<input type="hidden" name="action" />
	<div style="color:gray;"><i18n:message key="trace.note"/></div>
	<table border="0" cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="trace.addprobe"/></caption>
		<tr align="left">
		  	<th width="100" align="right">
		  		<i18n:message key="trace.threadName"/>:
		  	</th>
		  	<td>
				<input name="thread" type="text" class="field" size="50">
			</td>
		</tr>
		<tr>
		  	<th align="right">
		  		<i18n:message key="trace.codeLocation"/>:
		  	</th>
		  	<td>
				<input name="codeloc" type="text" class="field" size="50">
			</td>
		</tr>
		<tr align="left">
		  	<th align="right">
		  		<i18n:message key="trace.needStack"/>:
		  	</th>
		  	<td>
				<input name="needStack" type="checkbox">
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<button class="button" onclick="onAdd()"><i18n:message key="trace.add"/></button>
			</td>
		</tr>
	</table>
	<br>
	<table id="probe_list" border=0 cellPadding=0 cellSpacing=0 class="table">
		<caption><i18n:message key="trace.probelist"/></caption>
		<tr>
			<th width="20%"><i18n:message key="trace.threadName"/></th>
			<th><i18n:message key="trace.codeLocation"/></th>
			<th width="10%"><i18n:message key="trace.needStack"/></th>
			<th width="10%">&nbsp;</th>
		</tr>
	  	<c:forEach items="${map}" var="entry">
	  	<tr align="center">
			<td><c:out value="${entry.key.threadName}"/></td>
			<td><c:out value="${entry.key.codeLocation}"/></td>
			<td><c:out value="${entry.value.needStackInfo}"/></td>
			<td><button class="button" onclick="onDel( this )"><i18n:message key="trace.delete"/></button></td>
	  	</tr>
		</c:forEach>
	</table>
	</form>
</div>
</body>
</html>