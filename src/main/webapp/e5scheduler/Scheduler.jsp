<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="scheduler.mgr.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
		<script type="text/javascript">
			function doAction(command,server,jobID){
				//Add random parameter,otherwise xmlhttp will request cache
				var url = "SchedulerCommand.do?command="+command+"&server="+encodeSpecialCode(server)+"&jobID="+jobID+"&random="+Math.random();
				
				var xmlHttp = XmlHttp.create();
				
				xmlHttp.open("GET", url, false);	// async
				xmlHttp.send(null);
				var result = xmlHttp.responseText;	
				location.reload();
			}
			function actionALL(command){
				var server = form1.servers.value;
				//alert(server);
				action(command,server,'');
			}
			function viewLog(theURL){
				if (theURL.indexOf("javascript:void") >= 0) return;
				window.open(theURL);
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			.style2{
				color: #008000;
				font-weight:bold;
			}
			.style3{
				color: #006600;
				font-weight:bold;
			}
			.current-state{
				margin-bottom:5px;
				border:1px solid #E8E8E8;
				background:#fff;
			}
			.current-state li{
				float:left;
				line-height: 24px;
				margin:0 10px;
				padding:10px 0;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap">
		<form name="form1" method="post" action="">
			<table cellpadding="0" cellspacing="0" class="table">
				<caption>
					<i18n:message key="scheduler.mgr.selectNode"/>
					<select name="servers" style="width:180">
						<option value="ALL" selected><i18n:message key="scheduler.allServer"/></option>
						<c:forEach var="item" items="${servers}">
						<option value="<c:out value="${item.name}"/>"><c:out value="${item.name}"/></option>
						</c:forEach>
					</select>
					<input type="button" name="startALL" value="<i18n:message key="scheduler.mgr.start"/>" onclick="actionALL('STARTALL')" class="button">
					<input type="button" name="stopALL" value="<i18n:message key="scheduler.mgr.stop"/>" onclick="actionALL('STOPALL')" class="button">
					<input type="button" name="Submit" value="<i18n:message key="scheduler.refresh"/>" class="button" onclick="location.reload();">
					<input type="button" name="Submit" value="<i18n:message key="scheduler.mgr.logView"/>" class="button" onclick="location.href='../e5sys/log4j/logview.jsp?logfile=<c:out value="${logfile}"/>'">
				</caption>
				<tr>
					<th width="200" class="alignCenter"><i18n:message key="scheduler.mgr.jobName"/></th>
					<th><i18n:message key="scheduler.mgr.currentlyState"/></th>
				</tr>
				<c:forEach var="job" items="${list}">
				<tr>
					<td class="alignCenter"><c:out value="${job.jobName}"/></td>
					<td>
					<c:forEach var="state" items="${job.jobStates}">
						<ul class="current-state clearfix">
							<li><c:out value="${state.serverName}"/></li>
							<c:if test="${state.jobState=='STARTED'}">
							<li class="style3"><i18n:message key="scheduler.mgr.state.STARTED"/></li>
							</c:if>
							<c:if test="${state.jobState=='NOSTART'}">
							<li><i18n:message key="scheduler.mgr.state.NOSTART"/></li>
							</c:if>
							<c:if test="${state.jobState=='EXECUTING'}">
							<li class="style2"><i18n:message key="scheduler.mgr.state.EXECUTING"/></li>
							</c:if>
							<li>
								<input class="button" type="button" name="Submit" value="<i18n:message key="scheduler.mgr.start"/>" onclick="doAction('START','<c:out value="${state.serverName}"/>','<c:out value="${job.jobID}"/>')" <c:if test="${!state.available}">disabled title="<i18n:message key="scheduler.mgr.button.notrigger"/>"</c:if><c:if test="${state.jobState!='NOSTART'}">disabled</c:if>/>
								<input class="button" type="button" name="Submit" value="<i18n:message key="scheduler.mgr.stop"/>" onclick="doAction('STOP','<c:out value="${state.serverName}"/>','<c:out value="${job.jobID}"/>')" <c:if test="${!state.available}">disabled</c:if><c:if test="${state.jobState=='NOSTART'}">disabled</c:if>/>
								<input class="button" type="button" value="<i18n:message key="scheduler.sysJob.log"/>" onclick="viewLog('<c:out value="${job.logUrl}"/>')"/>
							</li>
						</ul>
					</c:forEach>
					</td>
				</tr>  
				</c:forEach>
			</table>
		</form>
		</div>
	</body>
</html>
