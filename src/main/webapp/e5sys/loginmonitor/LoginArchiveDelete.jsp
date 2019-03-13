
<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslogin" changeResponseLocale="false"/>

<%
	long threshold = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;
	String maxEndTime = new java.sql.Timestamp( threshold ).toString();
%>

<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	
	<script type="text/javascript" src="../../e5script/calendar_ts/calendar.js"></script>
		
	<script language="JavaScript" type="text/javascript">
	
		var date_pattern = /\d{4}-\d{2}-\d{2}.*/;
		
		function validate(){
			obj = document.logquery;
			
			if ( obj.beginTime.value == ""){
				alert("<i18n:message key="loginarchive.delete.range.begin"/>" + 
				"<i18n:message key="loginarchive.delete.errorInfo.notNull"/>");
				return false;
			}
			else if ( !obj.beginTime.value.match( date_pattern ) ) {
				alert("<i18n:message key="loginarchive.delete.range.begin"/>" + 
				"<i18n:message key="loginarchive.delete.errorInfo.illegalDate"/>");
				return false;
			}
			
			if ( obj.endTime.value == ""){
				alert("<i18n:message key="loginarchive.delete.range.end"/>" + 
				"<i18n:message key="loginarchive.delete.errorInfo.notNull"/>");
				return false;
			}
			else if ( !obj.endTime.value.match( date_pattern ) ) {
				alert("<i18n:message key="loginarchive.delete.range.end"/>" + 
				"<i18n:message key="loginarchive.delete.errorInfo.illegalDate"/>");
				return false;
			}
			
			if ( obj.beginTime.value > obj.endTime.value ){
				alert("<i18n:message key="loginarchive.delete.range.begin"/>" + 
				"<i18n:message key="loginarchive.delete.errorInfo.noLarge"/>" + 
				"<i18n:message key="loginarchive.delete.range.end"/>" );
				return false;
			}
			
			if ( obj.endTime.value > "<%=maxEndTime%>" ) {
				alert( "<i18n:message key="loginarchive.delete.range.errorInfo"/>" );
				return false;
			}
			
			if ( confirm( "<i18n:message key="loginarchive.delete.makeSure"/>" ) )		
				document.logquery.submit();
		}
	</script>
	
	<title><i18n:message key="loginarchive.delete.title"/></title>
</HEAD>

<BODY onload="document.getElementById('submitBtn').focus()">
<form name="logquery" method="post" action="loginArchive.do">
	<input name="action" type="hidden" value="delete">
		<table border="0" cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="loginarchive.delete.title"/></caption>
			<tr>
				<td colspan="2" width="440px">
					<i18n:message key="loginarchive.delete.note"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<i18n:message key="loginarchive.delete.choose"/><i18n:message key="loginarchive.delete.range"/>(yyyy-MM-dd HH:mm:ss:SSS)
				</td>
			</tr>
			<tr align="left">
			  <th class="w90" align="right">
			  	<i18n:message key="loginarchive.delete.range.begin"/>:
			  </th>
			  <td width="350px" align="left">
				<input name="beginTime" type="text" class="field" size="30" maxlength="50" readonly="true">
			  	<a href="javascript:cal_begin.popup();">
				<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16"
				 border="0" alt="Click Here to Pick up the date" />
			 	</a>
			  </td>
			</tr>
			<tr align="left">
			  <th class="w90" align="right">
			  	<i18n:message key="loginarchive.delete.range.end"/>:
			  </th>
			  <td width="350px" align="left">
				<input name="endTime" type="text" class="field" size="30" maxlength="50" readonly="true">
			 	 <a href="javascript:cal_end.popup();">
				<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16"
				 border="0" alt="Click Here to Pick up the date" />
				</a>
			  </td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<button id="submitBtn" class="button" onclick="return validate();">
					<i18n:message key="loginarchive.delete.submit"/></button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<button class="button" onclick="window.location.href='loginArchive.do'">
					<i18n:message key="loginarchive.delete.cancel"/></button>
				</td>
			</tr>
		</table>
		<p/>
</form>

<SCRIPT LANGUAGE="JavaScript">
<!--
	var cal_begin = new calendar3( "../../e5script/calendar_ts", document.logquery.beginTime );
	cal_begin.year_scroll = true;
	cal_begin.month_scroll = true;
	cal_begin.time_comp = true;
	
	var cal_end = new calendar3( "../../e5script/calendar_ts", document.logquery.endTime );
	cal_end.year_scroll = true;
	cal_end.month_scroll = true;
	cal_end.time_comp = true;
	
//-->
</SCRIPT>
	
</BODY>
</HTML>