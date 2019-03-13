
<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslogin" changeResponseLocale="false"/>

<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	
	<script type="text/javascript" src="../../e5script/calendar_ts/calendar.js"></script>
	
	<title><i18n:message key="loginarchive.query.title"/></title>
</HEAD>

<BODY onLoad="document.getElementById('submitBtn').focus()" style="margin:10px">

<form name="form1" method="post" action="loginArchive.do">

<table border="0" cellpadding="0" cellspacing="0" class="table">
	<caption><i18n:message key="loginarchive.query.title"/></caption>
	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.userCode"/>:
	  </th>
	  <td width="300px" align="left">
	  	<input name="userCode" type="text" class="field" size="30" maxlength="50">
	  </td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.userName"/>:
	  </th>
	  <td width="300px" align="left">
	  	<input name="userName" type="text" class="field" size="30" maxlength="50">
	  </td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.roleName"/>:
	  </th>
	  <td width="300px" align="left">
	 	<input name="roleName" type="text" class="field" size="30" maxlength="50">
	  </td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.hostName"/>:
	  </th>
	  <td width="300px" align="left">
	  <input name="hostName" type="text" class="field" size="30" maxlength="50"></td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.serverName"/>:
	  </th>
	  <td width="300px" align="left">
	  	<input name="serverName" type="text" class="field" size="30" maxlength="50">
	  </td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.beginTime"/>:
	  </th>
	  <td width="300px" align="left">
	 	 <input name="beginTime" type="text" class="field" size="30" maxlength="50" readonly="true">
	  	<a href="javascript:cal_begin.popup();">
		<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16" border="0" alt="Click Here to Pick up the date" />
	  	</a>
	  </td>
	</tr>

	<tr align="center">
	  <th class="w90" align="right">
	  	<i18n:message key="loginarchive.query.endTime"/>:
	  </th>
	  <td width="300px" align="left">
	  	<input name="endTime" type="text" class="field" size="30" maxlength="50" readonly="true">
	  	<a href="javascript:cal_end.popup();">
		<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16" border="0" alt="Click Here to Pick up the date" />
		</a>
	  </td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<button class="button" id="submitBtn" onclick="document.form1.submit()">
			<i18n:message key="loginarchive.query.submit"/></button>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    <button class="button" onclick="window.location.href='loginArchive.do'">
			<i18n:message key="loginarchive.query.cancel"/></button>
		</td>
	</tr>
</table>
<br/>
</form>

<SCRIPT LANGUAGE="JavaScript">

	var cal_begin = new calendar3( "../../e5script/calendar_ts", document.form1.beginTime );
	cal_begin.year_scroll = true;
	cal_begin.month_scroll = true;
	cal_begin.time_comp = true;
	
	var cal_end = new calendar3( "../../e5script/calendar_ts", document.form1.endTime );
	cal_end.year_scroll = true;
	cal_end.month_scroll = true;
	cal_end.time_comp = true;

</SCRIPT>
	
</BODY>
</HTML>