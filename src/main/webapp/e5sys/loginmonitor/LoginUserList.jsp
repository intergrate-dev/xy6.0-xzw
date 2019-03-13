<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head>
	<title>Add Role</title>
	<link type="text/css" rel="stylesheet" href="../../e5script/calendar/calendar.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
	<script src="../../e5script/calendar/usecalendar.js"></script>
	<script src="../../e5script/calendar/calendar.js"></script>
	<style type="text/css">
		.table td,.table th{
			text-align: center;
		}
	</style>
</head> 
<body onload="checkArchive()">
	<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table">
			<caption>
				<form name="rmloginfrm" action="LoginMonitorAction.do">
					<input type="hidden" name="invoke" value="kickOutObsoleteUsers">
					<input type="hidden" name="SortField" value="">
					<input type="hidden" name="SortBy" value="">
					<i18n:message key="loginuser.batch.kickout.title"/><input type="text" name="LastDay" value=""  style="cursor:hand;" readonly onclick="showCalendar(LastDay, 'y-mm-dd');">
					<input type="button" class="button" value="<i18n:message key="loginuser.batch.kickout.button"/>" onClick="batchKickOut();">
				</form>
			</caption>
			<tr>
				<th></th>
				<th onClick="sortBy('userCode')"><i18n:message key="loginuser.list.username"/></th>
				<th onClick="sortBy('userCode')"><i18n:message key="loginuser.list.usercode"/></th>
				<th onClick="sortBy('roleID')"><i18n:message key="loginuser.list.role"/></th>
				<th onClick="sortBy('loginTime')"><i18n:message key="loginuser.list.logintime"/></th>
				<th onClick="sortBy('lastAccessTime')"><i18n:message key="loginuser.list.last.accesstime"/></th>
				<th onClick="sortBy('hostName')"><i18n:message key="loginuser.list.hostname"/></th>
				<th onClick="sortBy('serverName')"><i18n:message key="loginuser.list.serverName"/></th>
			</tr>
			<c:forEach items="${loginmap.loginlist}" var="logininfo">
			<tr id="<c:out value="${logininfo.userID}"/>">
				<td><input type="button" class="button" value="<i18n:message key="loginuser.list.kickout"/>" onClick="kickOut('<c:out value="${logininfo.loginID}"/>')" /></td>
				<td>
				<c:if test="${logininfo.userName=='not exist'}">
					<i18n:message key="loginuser.list.user.delete"/>
				</c:if>
				<c:if test="${logininfo.userName!='not exist'}">
					<c:out value="${logininfo.userName}"/>
				</c:if>
				</td>
				<td>
				<c:out value="${logininfo.userCode}"/>
				</td>
				<td>
				<c:if test="${logininfo.roleName=='not exist'}">
					<!--<i18n:message key="loginuser.list.role.delete"/>-->
					-
				</c:if>
				<c:if test="${logininfo.roleName!='not exist'}">
					<c:out value="${logininfo.roleName}"/>
				</c:if>
				</td>
				<td>
				<c:out value="${logininfo.loginTime}"/>
				</td>
				<td>
				<c:out value="${logininfo.lastAccessTime}"/>
				</td>
				<td>
				<c:out value="${logininfo.hostName}"/>
				</td>
				<td>
				<c:out value="${logininfo.serverName}"/>
				</td>
			</tr>
			</c:forEach>
		</table>
	</div>
<script type="text/javascript">
	var setStyle="<c:out value="${loginmap.sortby}"/>";
	var sortField="<c:out value="${loginmap.sortfield}"/>";
	var userID="0";
	var olde=null;
	var SortStyle = new Array();
		SortStyle["userCode"]="ASC";
		SortStyle["loginTime"]="ASC";
		SortStyle["lastAccessTime"]="ASC";
		SortStyle["hostName"]="ASC";
		SortStyle["serverName"]="ASC";
		SortStyle["roleID"]="ASC";

		if(setStyle!=null && setStyle!="")
		{
			 if(setStyle=="ASC")
			 {
				SortStyle[sortField]="DESC";
			 }
			 else
			 {
				SortStyle[sortField]="ASC";
			 }

		}

	var nowDate= new Date();
		var s = nowDate.getFullYear() + "-";     // Get year.
		var sMon=nowDate.getMonth() + 1;
		if(sMon>9)
		{
			s += sMon + "-";     // Get month
		}
		else
		{
			s += "0"+sMon + "-";     // Get month
		}
		var sDay = nowDate.getDate();
		if(sDay>9)
		{
			s += sDay                 // Get day
		}
		else
		{
			s += "0"+sDay                 // Get day
		}
		rmloginfrm.LastDay.value=s;

	function SelectID(e){
	if(olde!=null)olde.style.backgroundColor=document.bgColor
	 e.style.backgroundColor="yellow"
	 olde=e
	 userID=e.id;
	}

	function kickOut(loginID){
		 var url="LoginMonitorAction.do?invoke=kickOutCurrentUser"
		 +"&SortField="+sortField+"&SortBy="+setStyle
		 +"&LoginID="+loginID;
		 document.location.href=url;
	}

	function sortBy(fieldName){
		 var url="LoginMonitorAction.do?invoke=sortUser"
		 +"&SortField="+fieldName+"&SortBy="+SortStyle[fieldName];

		 document.location.href=url;
	}

	function batchKickOut(notAsk){
		if (!notAsk) {
			if(!confirm("<i18n:message key="loginuser.batch.kickout.alert.head"/>"+rmloginfrm.LastDay.value+"<i18n:message key="loginuser.batch.kickout.alert.tail"/>"))
				 return;
		}
		if (rmloginfrm.LastDay.value > s)
		{
			rmloginfrm.LastDay.value = s;
		}
		 rmloginfrm.SortField.value=sortField;
		 rmloginfrm.SortBy.value=setStyle;
		 rmloginfrm.submit();
	 }
	function doGetCookie(strName){
		var theValue = null;
		var aCookie = document.cookie.split("; ");

		for (var i=0; i < aCookie.length; i++)
		{
			var aCrumb = aCookie[i].split("=");
			if (strName == aCrumb[0]) theValue = unescape(aCrumb[1]);
		}
		if (theValue == "0") theValue = "";
		return theValue;
	}

	function checkArchive(){
		
		var hasArchived = doGetCookie("LoginUser_Archived");
		if (hasArchived) return;
		
		document.cookie = "LoginUser_Archived=1";
		
		batchKickOut(1);
	}
</script>
</body> 
</html>