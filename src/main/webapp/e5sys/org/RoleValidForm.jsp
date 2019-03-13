<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="org.user.role.valid"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5script/calendar/calendar.css"/>
		<style type="text/css">
			.noborderinput{
				border:0px;
			}
		</style>
		<script src="../../e5script/calendar/usecalendar.js"></script>
		<script src="../../e5script/calendar/calendar.js"></script>
		<script type="text/javascript" src="./js/xmlhttps.js"></script>
		<script type="text/javascript" src="./js/query.js"></script>
		<script type="text/javascript">
			function setOption(initVal,sel,nloop){
				for(i=initVal;i<nloop;i++){
					document.write("<option value='");
					document.write(i);
					document.write("' ");
					if(sel==i){
						document.write("selected");
					}
					document.write(">");
					document.write(i);
					document.write("</option>");
				}
			}
		</script>
	</head>
	<body oncontextmenu="if (!event.ctrlKey){return false;}">
		<div class="mainBdoyWrap">
		<form name="mainForm" id="mainForm" action="RoleValidAction.do" method="post">
			<table cellPadding="0" cellSpacing="0" class="table">
				<caption><i18n:message key="org.user.role.valid"/></caption>
				<tr>
					<td class="alignCenter"><i18n:message key="org.role.valid.startdate"/></td>
					<td><input type="text" style="cursor:hand;" name="startDateStr" ondblClick="showCalendar(startDateStr, 'y-mm-dd');" value="<c:out value="${userrolelist.userrole.startDate}"/>" readonly></td>
				</tr>
				<tr>
					<td class="alignCenter"><i18n:message key="org.role.valid.enddate"/></td>
					<td><input type="text" style="cursor:hand;" name="endDateStr" ondblClick="showCalendar(endDateStr, 'y-mm-dd');" value="<c:out value="${userrolelist.userrole.endDate}"/>" readonly></td>
				</tr>
				<tr>
					<td class="alignCenter" colspan="2">
						<input type="hidden" name="timeType" value='<c:out value="${userrolelist.userrole.timeType}"/>'>
						<input class="noborderinput" type="radio" value="0" name="timeType1" id="timeType_1" onclick="checkDay()" <c:if test="${userrolelist.userrole.timeType==0}">checked</c:if>>
						<label for="timeType_1"><i18n:message key="org.role.valid.timetype.everyday"/>&nbsp;</label>
						<input class="noborderinput" type="radio" value="1"  name="timeType1" id="timeType_2" onclick="checkWeek()" <c:if test="${userrolelist.userrole.timeType==1}">checked</c:if>>
						<label for="timeType_2"><i18n:message key="org.role.valid.timetype.everyweek"/>&nbsp;</label>
						<input class="noborderinput" type="radio" value="2"  name="timeType1" id="timeType_3" onclick="checkMonth()" <c:if test="${userrolelist.userrole.timeType==2}">checked</c:if>>
						<label for="timeType_3"><i18n:message key="org.role.valid.timetype.everymonth"/>&nbsp;</label>
					</td>
				</tr>
				<script>
					var weekPara="";
					var monthPara="";
				</script>
				<tr style="background-color:#f7f7f7">
					<td class="alignCenter" width="100%" colspan="2">
						<table id="week" style="display:none" cellpadding="3">
							<tr>
								<td>
									<c:forEach var="week" items="${userrolelist.weeklist}">
									<input class="noborderinput" type="checkbox" name="week<c:out value='${week.dayValue}'/>" <c:if test="${'1'==week.checkStatus}"> checked </c:if> onClick="selectWeek(this)"><c:out value='${week.dayName}'/>&nbsp;<c:if test="${week.dayValue%4==0}"><br></c:if>
									<script><c:if test="${'1'==week.checkStatus}"> weekPara+="&week<c:out value='${week.dayValue}'/>=on"; </c:if></script>
									</c:forEach>
								</td>
							</tr>
						</table>
						<table id="month" style="display:none" cellpadding="3">
							<tr>
								<td>
									<c:forEach var="month" items="${userrolelist.monthlist}">
									<input class="noborderinput" type="checkbox" name="month<c:out value='${month.dayValue}'/>"  <c:if test="${month.checkStatus=='1'}"> checked </c:if> onClick="selectMonth(this)"><c:out value='${month.dayValue}'/><i18n:message key="org.role.valid.timetype.day"/><c:if test="${month.dayValue<10}">&nbsp;&nbsp;</c:if><c:if test="${month.dayValue%5==0}"><br></c:if><script> <c:if test="${'1'==month.checkStatus}"> monthPara+="&month<c:out value='${month.dayValue}'/>=on"; </c:if></script>
									</c:forEach>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2"><input type=hidden name=timeValue value="<c:out value="${userrolelist.userrole.timeValue}"/>"></td>
			  	</tr>
			 	<tr>
					<td class="alignCenter"><i18n:message key="org.role.valid.wholeday"/><input class="noborderinput" type="checkbox" name="isWholeDay" <c:if test="${userrolelist.interval.wholeDay}"> checked</c:if> onClick="selectWholeDay(this)"></td>
				 	<td></td>
				</tr>
				<tr id="starttimetab" name="starttimetab" <c:if test="${userrolelist.interval.wholeDay}"> style="display:none" </c:if>>
					<td class="alignCenter"><i18n:message key="org.role.valid.starttime"/></td>
					<td>
						<select name=startTimeStr_hour>
							<script>
								setOption(0,<c:out value="${userrolelist.interval.startMomentHour}"/>,24);
							</script>
						</select>
						:
						<select name=startTimeStr_minu>
							<script>
								setOption(0,<c:out value="${userrolelist.interval.startMomentMinute}"/>,60);
							</script>
						</select>
					</td>
				</tr>
				<tr id="endtimetab" name="endtimetab" <c:if test="${userrolelist.interval.wholeDay}"> style="display:none" </c:if>>
					<td class="alignCenter"><i18n:message key="org.role.valid.endtime"/></td>
					<td>
						<select name=endTimeStr_hour>
						<script>
							setOption(0,<c:out value="${userrolelist.interval.endMomentHour}"/>,24);
						</script>
						</select>
						:
						<select name=endTimeStr_minu>
						<script>
							setOption(1,<c:out value="${userrolelist.interval.endMomentMinute}"/>,60);
						</script>
						</select>
					</td>
			  	</tr>
			  	<c:if test="${userrolelist.opType=='update'}">
			  	<tr>
			  		<td class="alignCenter">
			  			<input type="button" class="button" value="<i18n:message key="org.role.valid.submit"/>" onClick="UpdateRole();">
			  			<input type="button"  class="button" value="<i18n:message key="org.role.valid.close"/>" onClick="window.close();">
			  		</td>
			  	</tr>
			  	</c:if>
			</table>
			<input type=hidden name="methodType">
			<input type=hidden name="UserID" value="<c:out value="${userrolelist.userrole.userID}"/>">
			<input type=hidden name="RoleID" value="<c:out value="${userrolelist.userrole.roleID}"/>">
			<input type=hidden name="invoke" value="updateUserRole">
		</form>
		</div>
		<script type="text/javascript">
			if(document.getElementById("timeType").value=="1")document.getElementById("week").style.display="block"
			if(document.getElementById("timeType").value=="2")document.getElementById("month").style.display="block"
			function checkDay(){
			  document.getElementById("timeType").value="0"
			  document.getElementById("week").style.display="none"
			  document.getElementById("month").style.display="none"
			}
			function checkWeek(){
			  document.getElementById("timeType").value="1"
			  document.getElementById("week").style.display="block"
			  document.getElementById("month").style.display="none"
			}
			function checkMonth(){
			  document.getElementById("timeType").value="2"
			  document.getElementById("week").style.display="none"
			  document.getElementById("month").style.display="block"
			}
			function selectWholeDay(e)
			{
				if(e.checked)
				{
					document.getElementById("starttimetab").style.display="none"
					document.getElementById("endtimetab").style.display="none"
				}
				else
				{
					document.getElementById("starttimetab").style.display=""
					document.getElementById("endtimetab").style.display=""
				}
			}

			function selectWeek(e)
			{
				var sel="&"+e.name+"="+e.value;
				if(e.checked)
				{
					var index=weekPara.indexOf(sel);
					if(index=-1)
					{
						weekPara+=sel;
					}
				}
				else
				{
					var index=weekPara.indexOf(sel);
					if(index!=-1)
					{
						weekPara=weekPara.substring(0,index)+weekPara.substring(index+sel.length);
					}
				}
			}
			function selectMonth(e)
			{
				var sel="&"+e.name+"="+e.value;
				if(e.checked)
				{
					var index=monthPara.indexOf(sel);
					if(index=-1)
					{
						monthPara+=sel;
					}
				}
				else
				{
					var index=monthPara.indexOf(sel);
					if(index!=-1)
					{
						monthPara=monthPara.substring(0,index)+monthPara.substring(index+sel.length);
					}
				}
			}

			var treeid="<c:out value="${userrolelist.treeid}"/>";
			function UpdateRole()
			{
					urlsrc="RoleMgrUserAction.do?invoke=grantRole"
					+"&RoleID="+ mainForm.RoleID.value
					+"&UserID="+ mainForm.UserID.value
					+getDatePara();
					invokeGetXmlHttpDo(urlsrc);
					if(treeid=="-1")
					{
						showUser(mainForm.UserID.value);
					}
					else
					{
						refreshPage();
					}
					window.close();

			}

			function getDatePara()
			{
				var strStartTime="";
				var strEndTime="";

					if(mainForm.isWholeDay.checked)
					{
						strStartTime="&StartTime=00:00";
						strEndTime="&EndTime=00:00"
					}
					else
					{
						strStartTime="&StartTime="+ mainForm.startTimeStr_hour.value+":"+ mainForm.startTimeStr_minu.value;
						strEndTime="&EndTime="+ mainForm.endTimeStr_hour.value+":"+mainForm.endTimeStr_minu.value

					}

					urlsrc="&StartDate="+ mainForm.startDateStr.value
					+"&EndDate="+ mainForm.endDateStr.value
					+strStartTime
					+strEndTime
					+"&TimeType="+ mainForm.timeType.value
					+weekPara
					+monthPara;

					checkVaildDate();

					return urlsrc;
			}

			function checkVaildDate()
			{
				var startDate=getDateString(mainForm.startDateStr.value);
				var endDate = getDateString(mainForm.endDateStr.value);
				if(startDate=="")
				{
					alert("<i18n:message key="org.role.valid.startdate.alert"/>");
					return;
				}

				if(endDate=="")
				{
					alert("<i18n:message key="org.role.valid.enddate.alert"/>");
					return;
				}
				var  startDDDate = new  Date(startDate);
				var endDDDate= new Date(endDate);

				if  (isNaN(startDDDate))
				{
					alert("<i18n:message key="org.role.valid.startdate.format.alert"/>");
					return;
				}
				if  (isNaN(endDDDate))
				{
					alert("<i18n:message key="org.role.valid.enddate.format.alert"/>");
					return;
				}
				if(startDDDate>endDDDate)
				{
					alert("<i18n:message key="org.role.valid.date.compare"/>");
					return;
				}
				nowDate=new Date();
				var s = nowDate.getFullYear() + "/";                       // Get year.
				s += (nowDate.getMonth() + 1) + "/";          // Get month
				s += nowDate.getDate();                 // Get day

				nowBDate = new Date(s);

				if(endDDDate<nowBDate)
				{
					alert("<i18n:message key="org.role.valid.date.compare.today"/>");
					return;
				}

			}
			function getDateString(dateStr){
				var  tempArray = dateStr.split('-');
				if  (tempArray.length!=3)  return "";
				var  tempy = tempArray[0];
				var  tempm = tempArray[1];
				var  tempd = tempArray[2];

				var  tDateString = tempy + '/' + tempm + '/' + tempd;
				return tDateString;
			}
		</script>
	</body>
</html>
