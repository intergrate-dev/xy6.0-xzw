<%@page import="java.util.*" %>
<%@page import="org.apache.log4j.*" %>
<%@page import="com.founder.e5.commons.Log4jUtil" %>
<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>

<%
	Logger root = LogManager.getRootLogger();
	String name = request.getParameter( "name" );
	if ( name == null || "".equals( name.trim() ) ) {
		out.println( "<p>None logger name found!</p>" );
		return;
	}
	
	Logger logger = null;
	if ( "root".equals( name ) ) logger = root;
	else logger = LogManager.exists( name );
	
	if ( logger == null ) {
		out.println( "No logger with name \"" + name + "\" exists!" );
		return;
	}
	
	List appenders = Log4jUtil.getEffectiveAppenders( logger );
%>
<html>
	<head>
		<title><i18n:message key="log4j.loggeredit"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script src="../../e5script/jquery/jquery.min.js" type="text/javascript"></script>
		<script src="../../e5script/jquery/jquery.tab.js" type="text/javascript"></script>
		<script language="JavaScript" type="text/javascript">
			// function add() {
			// 	var pane = document.getElementById( "appendersPane" );
			// 	var newDiv = document.createElement( "div" );
			// 	newDiv.className = "tab-page";
			// 	newDiv.id = "new";
			// 	var newDivTab = document.createElement( "h2" );
			// 	newDivTab.className = "tab";
			// 	newDivTab.innerHTML = "new";
			// 	newDiv.appendChild( newDivTab );
			// 	pane.appendChild( newDiv );
			// 	tabPane1.addTabPage( newDiv ).select();
			// }
			// function del() {
			// 	var selectedTab = tabPane1.pages[ tabPane1.selectedIndex ];
			// 	alert( selectedTab.tab );
			// }
			function doInit() {
				var f = document.form1;
				f.level.value = "<%=logger.getEffectiveLevel()%>";
				f.additivity.checked = <%=logger.getAdditivity()%>;
				// if ( tabPane1.pages.length > 0 )
					// tabPane1.setSelectedIndex( 0 );
			}
			var tab1;
			$(function(){
				doInit();
				tab1 = $("#tabs").tabs("#panes");
			}); 
		</script>
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
		<style>
			.loggeredit{
				line-height: 50px;
			}
			.pane{
				height:auto;
			}
			#tab-pane{
				margin-top:20px;
			}
			.m10{
				margin:10px;
			}
		</style>
	</head>
	<body>
		<form name="form1" action="loggeredit.jsp" method="post">
			<div id="titleDiv" class="loggeredit">
				<a href="loggerlist.jsp"><i18n:message key="log4j.config"/></a> -&gt;
				<i18n:message key="log4j.loggeredit"/>
			</div>
			<div class="mainBodyWrap">
				<table class="table">
					<tr>
						<td width="30"><i18n:message key="log4j.table.category"/>:</td>
						<td><%=name%></td>
					</tr>
					<tr>
						<td><i18n:message key="log4j.table.level"/>:</td>
						<td><select name="level">
								<option value="DEBUG">DEBUG</option>
								<option value="INFO">INFO</option>
								<option value="WARN">WARN</option>
								<option value="ERROR">ERROR</option>
								<option value="FATAL">FATAL</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input style="float:left;margin-right:5px;" name="additivity" type="checkbox">
							<label style="float:left;"><i18n:message key="log4j.table.additivity"/></label>
						</td>
					</tr>
				</table>
				<!-- 
				<i18n:message key="log4j.loggeredit.appenders_cfg"/>: &nbsp;
				<a href="javascript:add()"><i18n:message key="log4j.loggeredit.add_appender"/></a> &nbsp;
				<a href="javascript:delete()"><i18n:message key="log4j.loggeredit.del_appender"/></a>
				-->
				<div id="tab-pane">
					<ul id="tabs" class="tabs clearfix">
						<%
							for( Iterator i = appenders.iterator(); i.hasNext(); ) {
								Appender appender = (Appender) i.next();
								String appdName = appender.getName();
						%>
						<li class="tab">
							<a href="#<%=appdName%>"><%=appdName%></a>
						</li>
						<%
							}
						%>
					</ul>
					<div class="panes" id="panes">
						<%
							for( Iterator i = appenders.iterator(); i.hasNext(); ) {
								Appender appender = (Appender) i.next();
								String appdName = appender.getName();
						%>
						<div class="pane">
							<a id="<%=appdName%>" name="<%=appdName%>"></a>
							<div class="adminform">	
								<div class="m10">Type:<%=appender.getClass().getName()%>
									<%
										if ( appender instanceof FileAppender ) {
											FileAppender fa = (FileAppender) appender;
											String pathname = fa.getFile();
											if ( pathname != null )
												pathname = pathname.replace( '\\', '/' );
											%>
											<input name="filename" type="text" 
														size="50"
														value="<%=pathname%>" readonly/>
											&nbsp;&nbsp;<a href="logview.jsp?logfile=<%=pathname%>">
											<i18n:message key="og4j.loggeredit.viewLog"/></a>
											<%
										}
									%>
								</div>			
								<div class="m10">Layout:<%=appender.getLayout().getClass().getName()%>
									<%
										Layout layout = appender.getLayout();
										if ( layout instanceof PatternLayout ) {
											PatternLayout pLayout = (PatternLayout) layout;
											%>
											<input name="conversionPattern" type="text" 
													size="50"
													value="<%=pLayout.getConversionPattern()%>" readonly/>
											<%
										}
									%>
								</div>
							</div>
						</div>
						<%
							}
						%>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
