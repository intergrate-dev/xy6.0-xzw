<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5note" changeResponseLocale="false" />
<html>
	<head>
		<title>Notice</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5script/calendar/calendar.css"/>
		<link rel="stylesheet" type="text/css" href="../../e5script/jquery/dialog.style.css" />
		<script type="text/javascript" src="../../e5script/calendar_ts/calendar.js"></script>
		<script type="text/javascript" src="../../e5script/Function.js"></script>
		<script src="../../e5script/calendar/usecalendar.js"></script>
		<script src="../../e5script/calendar/calendar.js"></script>
		<script type="text/javascript" src="note.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
	</head>
	<body onload="setselect(<c:out value='${type}'/>)" ID="bo">
		<div class="mainBodyWrap">
			<div class="caption">
				<table cellspacing="0" cellpadding="0" style="margin:0 auto;">
					<tr>
						<td><i18n:message key="e5note.createnote" /></td>
						<td style="display:none">
							<i18n:message key="e5note.query" />
							<select class="work" id="expressQuery" name="expressQuery" onChange="onExpressQuery(this)">
								<option value="0"><i18n:message key="e5note.nomatter" /></option>
								<option value="1"><i18n:message key="e5note.today" /></option>
								<option value="2"><i18n:message key="e5note.thisweek" /></option>
								<option value="3"><i18n:message key="e5note.thismonth" /></option>
							</select>
						</td>
						<td>
							<input class="button" type="button" value="<i18n:message key="e5note.createnote"/>" onclick="noteRelease();">
						</td>
						<td>
							<input class="button" type="button" value="<i18n:message key="e5note.deletenote"/>" onclick="deleteNote();">
						</td>
						<td align="right">
							<input name="EndTime" id="EndTime" type="text" class="field" size="30" maxlength="50" value="" readonly="true" title="<i18n:message key="e5note.tip" />" onClick="showCalendar('EndTime', 'y-mm-dd');"> 
							<input class="button" type="button" value="<i18n:message key="e5note.clearnote"/>" onclick="clearNote();">
						</td>
					</tr>
				</table>
			</div>
			<table cellpadding="0" cellspacing="0" class="table">
				<tr>
					<th ondblclick="allSelect();"></th>
					<th><i18n:message key="e5note.topic" /></th>
					<th><i18n:message key="e5note.sender" /></th>
					<th><i18n:message key="e5note.sendtime" /></th>
					<th><i18n:message key="e5note.notetype" /></th>
				</tr>
				<c:forEach items="${notelist}" var="note" varStatus="count">
					<tr ondblclick="noteopen(<c:out value='${count.index}'/>);">
						<c:set var="type" value="${notelist[count.index].noteType}" />
						<td><input type="checkbox" class="checkbox"
							id="checkbox<c:out value='${count.index}'/>"></td>
						<td><c:out value="${note.topic}" /></td>
						<input type="hidden"
							value="<c:out value='${notelist[count.index].noteID}'/>"
							id="noteid<c:out value='${count.index}'/>" />
						<input type="hidden"
							value="<c:out value='${notelist[count.index].noteType}'/>"
							id="notetype<c:out value='${count.index}'/>" />
						<td><c:out value="${note.sender}" /></td>
						<td><c:out value="${note.sendTime}" /></td>
						<c:choose>
							<c:when test="${type<=0}">
								<td><i18n:message key="e5note.notetype1" /></td>
							</c:when>
							<c:when test="${type=='1'}">
								<td><i18n:message key="e5note.notetype2" /></td>
							</c:when>
							<c:otherwise>
								<td><i18n:message key="e5note.notetype3" /></td>
							</c:otherwise>
						</c:choose>
					</tr>
					</c:forEach>
			</table>
		</div>
		<input type="hidden" value="" id="X" />
		<input type="hidden" value="" id="Y" />
	</body>
	<script type="text/javascript">
		var EndTime=document.getElementById("EndTime");
		var cal_begin = new calendar3( "../../e5script/calendar_ts", EndTime);
		cal_begin.year_scroll = true;
		cal_begin.month_scroll = true;
		cal_begin.time_comp = true;
	</script>
</html>
