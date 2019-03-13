
<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>

<html>
<head>
	<title><i18n:message key="syslog.query.title"/></title>
	<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script type="text/javascript" src="../../e5script/calendar_ts/calendar.js"></script>
</head>
<body onLoad="document.getElementById('submitBtn').focus()">
	<div class="mainBodyWrap">
	<form name="form1" method="post" action="logQuery.do">
		<input name="expressQuery" type="hidden" value="0">
		<table cellpadding="0" cellspacing="0" class="table" >
			<caption><i18n:message key="syslog.query.title"/></caption>
			<tr>
				<th class="w90"><i18n:message key="syslog.query.type"/>:</th>
				<td>
					<select name="logType" style="width:70px">
						<option value="0" selected><i18n:message key="syslog.view.expressQuery.any"/></option>
						<option value="1"><i18n:message key="syslog.query.type.info"/></option>
						<option value="2"><i18n:message key="syslog.query.type.error"/></option>
						<option value="3"><i18n:message key="syslog.query.type.record"/></option>
						<option value="4"><i18n:message key="syslog.query.type.operate"/></option>
					</select>
				</td>
			</tr>
			<tr>
				<th><i18n:message key="syslog.query.message"/>:</th>
				<td>
					<i18n:message key="syslog.query.show1"/>
					<select name="msgOperator">
						<option value="1" selected><i18n:message key="syslog.query.msgOperator1"/></option>
						<option value="2"><i18n:message key="syslog.query.msgOperator2"/></option>
						<option value="3"><i18n:message key="syslog.query.msgOperator3"/></option>
						<option value="4"><i18n:message key="syslog.query.msgOperator4"/></option>
					</select>
					<i18n:message key="syslog.query.show2"/>
					<input name="msgValue" type="text" class="field" size="30" maxlength="50">
					<i18n:message key="syslog.query.show3"/>
				</td>
			</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.operator"/>:</th>
				<td width="80%" align="left">
				<input name="operator" type="text" class="field" size="30" maxlength="50"></td>
			</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.operation"/>:</th>
				<td width="80%" align="left">
				<input name="operation" type="text" class="field" size="30" maxlength="50"></td>
			</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.userhost"/>:</th>
				<td width="80%" align="left">
				<input name="userhost" type="text" class="field" size="30" maxlength="50"></td>
					</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.subapp"/>:</th>
				<td width="80%" align="left">
				<input name="appDescription" type="text" class="field" size="30" maxlength="50"></td>
					</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.beginTime"/>:
				</th>
				<td width="80%" align="left">
				<input name="beginTime" type="text" class="field" size="30" maxlength="50" readonly="true">
				<a href="javascript:cal_begin.popup();">
					<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16" 
					border="0" alt="Click Here to Pick up the date" />
				</a>
				</td>
					</tr>
			<tr>
				<th>
				<i18n:message key="syslog.query.endTime"/>:</th>
				<td width="80%" align="left">
				<input name="endTime" type="text" class="field" size="30" maxlength="50" readonly="true">
				<a href="javascript:cal_end.popup();">
					<img src="../../e5script/calendar_ts/img/cal.gif" width="16" height="16"
					 border="0" alt="Click Here to Pick up the date" />
					</a>
					  </td>
					</tr>
			<tr>
				<td class="alignCenter" colspan="2">
					<button class="button" id="submitBtn" onClick="document.form1.submit()"><i18n:message key="syslog.query.submit"/></button><button class="button" onClick="window.location.href='logQuery.do'"><i18n:message key="syslog.query.cancel"/></button>
				</td>
			</tr>
		</table>
	</form>
	</div>
<script type="text/javascript">
	var cal_begin = new calendar3( "../../e5script/calendar_ts", document.form1.beginTime );
	cal_begin.year_scroll = true;
	cal_begin.month_scroll = true;
	cal_begin.time_comp = true;
	
	var cal_end = new calendar3( "../../e5script/calendar_ts", document.form1.endTime );
	cal_end.year_scroll = true;
	cal_end.month_scroll = true;
	cal_end.time_comp = true;
</script>
</body>
</html>