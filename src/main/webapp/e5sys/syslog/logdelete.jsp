<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>
<%
	long threshold = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;
	String maxEndTime = new java.sql.Timestamp( threshold ).toString();
%>
<html>
<head>
	<title><i18n:message key="syslog.delete.title"/></title>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript" src="../../e5script/calendar_ts/calendar.js"></script>
	<script type="text/javascript">
		var date_pattern = /\d{4}-\d{2}-\d{2}.*/;
		function validate(){
			obj = document.logquery;
			
			if ( obj.beginTime.value == ""){
				alert("<i18n:message key="syslog.query.beginTime"/>" + 
				"<i18n:message key="syslog.delete.errorInfo.notNull"/>");
				return false;
			}
			else if ( !obj.beginTime.value.match( date_pattern ) ) {
				alert("<i18n:message key="syslog.query.range.begin"/>" + 
				"<i18n:message key="syslog.delete.errorInfo.illegalDate"/>");
				return false;
			}
			
			if ( obj.endTime.value == ""){
				alert("<i18n:message key="syslog.query.endTime"/>" + 
				"<i18n:message key="syslog.delete.errorInfo.notNull"/>");
				return false;
			}
			else if ( !obj.endTime.value.match( date_pattern ) ) {
				alert("<i18n:message key="syslog.query.endTime"/>" + 
				"<i18n:message key="syslog.delete.errorInfo.illegalDate"/>");
				return false;
			}
			
			if ( obj.beginTime.value > obj.endTime.value ){
				alert("<i18n:message key="syslog.query.beginTime"/>" + 
				"<i18n:message key="syslog.delete.errorInfo.noLarge"/>" + 
				"<i18n:message key="syslog.query.range.end"/>" );
				return false;
			}
			
			if ( obj.endTime.value > "<%=maxEndTime%>" ) {
				alert( "<i18n:message key="syslog.query.range.errorInfo"/>" );
				return false;
			}
			
			if ( confirm( "<i18n:message key="syslog.delete.makeSure"/>" ) )		
				document.logquery.submit();
		}
	</script>
</head>
<body onload="document.getElementById('submitBtn').focus()">
	<div class="mainBodyWrap">
	<form name="logquery" method="post" action="logQuery.do">
		<input name="action" type="hidden" value="delete">
		<table cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="syslog.view.delete"/></caption>
			<tr>
				<th width="250"><i18n:message key="syslog.query.range.prompt"/><i18n:message key="syslog.query.choose"/></th>
				<td><i18n:message key="syslog.query.range"/>(yyyy-MM-dd HH:mm:ss:SSS)</td>
			</tr>
			<tr>
				<th><i18n:message key="syslog.query.beginTime"/>:</th>
				<td><input name="beginTime" type="text" class="field" size="30" maxlength="50" readonly="true"><a href="javascript:cal_begin.popup();"><img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16" border="0" alt="Click Here to Pick up the date" /></a></td>
			</tr>
			<tr>
				<th><i18n:message key="syslog.query.endTime"/>:</th>
				<td><input name="endTime" type="text" class="field" size="30" maxlength="50" readonly="true"><a href="javascript:cal_end.popup();"><img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16" border="0" alt="Click Here to Pick up the date" /></a></td>
			</tr>
			<tr>
				<td colspan="2" class="alignCenter">
					<button id="submitBtn" class="button" onclick="return validate();"><i18n:message key="syslog.query.submit"/></button>
					<button class="button" onclick="window.location.href='logQuery.do'"><i18n:message key="syslog.query.cancel"/></button>
				</td>
			</tr>
		</table>
	</form>
	</div>
	<script LANGUAGE="JavaScript">
		var cal_begin = new calendar3( "../../e5script/calendar_ts", document.logquery.beginTime );
		cal_begin.year_scroll = true;
		cal_begin.month_scroll = true;
		cal_begin.time_comp = true;
		
		var cal_end = new calendar3( "../../e5script/calendar_ts", document.logquery.endTime );
		cal_end.year_scroll = true;
		cal_end.month_scroll = true;
		cal_end.time_comp = true;
	</script>
</body>
</html>