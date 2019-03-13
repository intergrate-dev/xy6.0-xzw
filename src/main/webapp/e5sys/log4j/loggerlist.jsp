<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="com.founder.e5.web.WebUtil" %>
<%@page import="com.founder.e5.commons.Log4jUtil" %>
<%@page import="org.apache.log4j.*" %>
<html>
<%
	Logger root = LogManager.getRootLogger();
	Map loggers = Log4jUtil.getCurrentLoggers();
	
	String changed = request.getParameter( "changed" );
	boolean show_spring = WebUtil.getBoolParam(request, "show_spring");
	boolean show_hb = WebUtil.getBoolParam(request, "show_hb");
	boolean show_other = WebUtil.getBoolParam(request, "show_other");
	
	if ( changed != null ) {
		String[] ss = changed.split( "," );
		for ( int i = 0; i < ss.length; i++ ) {
			String name = ss[i];
			String value = request.getParameter( name );
			
			String[] ss2 = name.split( "_", 2 );
			if ( ss2.length == 2 ) {
				String loggerName = ss2[1];
				Logger logger = null;
				
				if ( "root".equals( loggerName ) )
					logger = root;
				else
					logger = Logger.getLogger( loggerName );
				
				if ( "level".equals( ss2[0] ) ) {
					Level oldLevel = logger.getEffectiveLevel();
					Level newLevel = Level.toLevel( value, oldLevel );
					System.out.println( logger.getName() + "'s level: " + oldLevel + " -> " + newLevel );
					logger.setLevel( newLevel );
				}
				
				else if ( "additivity".equals( ss2[0] ) ) {
					boolean newAdditivity = WebUtil.getBoolParam(request, name);
					System.out.println( logger.getName() + "'s additivity: " + logger.getAdditivity() + " -> " + newAdditivity );
					logger.setAdditivity( newAdditivity );
				}
			}
		}
	}
%>
<head>
	<title><i18n:message key="log4j.config.loggerlist"/></title>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript">
		function registerChanged( obj ) {
			var str = document.form1.changed.value;
			if ( str == "" ) str = obj.name;
			else	str = str + "," + obj.name;
			document.form1.changed.value = str;
		}
	</script>
	<style type="text/css">
		.table th{
			text-align: center;
		}
	</style>
</head>

<body>
	<div class="mainBodyWrap">
	<form name="form1" method="post" action="loggerlist.jsp">
		<input name="changed" type="hidden">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption>
				<i18n:message key="log4j.config"/> -&gt; <i18n:message key="log4j.config.loggerlist"/>
				&nbsp;&nbsp;			
				&nbsp;&nbsp;
				<input id="show_hb" name="show_hb" type="checkbox" 
					<%=show_hb? "checked" : "" %> onclick="document.form1.submit()">
				<label for="show_hb">&nbsp;<i18n:message key="log4j.config.with"/>Hibernate</label>
				
				&nbsp;&nbsp;
				<input id="show_spring" name="show_spring" type="checkbox" 
					<%=show_spring? "checked" : "" %> onclick="document.form1.submit()">
				<label for="show_spring">&nbsp;<i18n:message key="log4j.config.with"/>Spring</label>
				
				&nbsp;&nbsp;
				<input id="show_other" name="show_other" type="checkbox" 
					<%=show_other? "checked" : "" %> onclick="document.form1.submit()">
				<label for="show_other">&nbsp;<i18n:message key="log4j.config.with"/>Others</label>
			</caption>
			<tr>
				<th><i18n:message key="log4j.table.category"/></th>
				<th><i18n:message key="log4j.table.level"/></th>
				<th><i18n:message key="log4j.table.additivity"/></th>
				<th><i18n:message key="log4j.table.appenders"/></th>
				<th>&nbsp;</th>
			</tr>
			<tr>
				<td><a href="loggeredit.jsp?name=root">root</a></td>
				<td class="alignCenter">
					<select name="level_root" onchange="registerChanged(this);">
					  <option value="DEBUG">DEBUG</option>
					  <option value="INFO">INFO</option>
					  <option value="WARN">WARN</option>
					  <option value="ERROR">ERROR</option>
					  <option value="FATAL">FATAL</option>
					</select></td>
					<script language="JavaScript" type="text/javascript">
						document.form1.level_root.value="<%=root.getEffectiveLevel()%>";
					</script>

				<td class="alignCenter">&nbsp;-&nbsp;</td>
				<td class="alignCenter"><%=Log4jUtil.getEffectiveAppenderNames(root)%></td>
				<td class="alignCenter"><button class="button" onclick="document.form1.submit()"><i18n:message key="log4j.table.save"/></button></td>
			</tr>
	  
			<%
			for (Iterator i = loggers.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry me = (Map.Entry) i.next();
				String name = (String) me.getKey();
				Logger value = (Logger) me.getValue();
				
				if (!show_hb && name.startsWith("org.hibernate"))
					continue;
				
				if (!show_spring && name.startsWith("org.springframework"))
					continue;
				
				if (!show_other && name.indexOf("e5") == -1
						&& !name.startsWith("org.hibernate")
						&& !name.startsWith("org.springframework"))
					continue;
			%>
			
			<tr>
				<td><a href="loggeredit.jsp?name=<%=name%>"><%=name%></a></td>
				<td class="alignCenter">
					<select name="level_<%=name%>" onchange="registerChanged(this);">
					  <option value="DEBUG">DEBUG</option>
					  <option value="INFO">INFO</option>
					  <option value="WARN">WARN</option>
					  <option value="ERROR">ERROR</option>
					  <option value="FATAL">FATAL</option>
					</select></td>
					<script language="JavaScript" type="text/javascript">
						document.form1["level_<%=name%>"].value="<%=value.getEffectiveLevel()%>";
					</script>

				<td class="alignCenter">
				<input name="additivity_<%=name%>" type="checkbox" <%=value.getAdditivity() ? "checked" : ""%> 
					onchange="registerChanged(this);">
				</td>
				<td class="alignCenter"><%=Log4jUtil.getEffectiveAppenderNames(value)%></td>
				<td class="alignCenter">
					<button class="button" onclick="document.form1.submit()"><i18n:message key="log4j.table.save"/></button>
				</td>
			</tr>
			<%
			}
			%>
		</table>
	</form>
	</div>
</body>
</html>