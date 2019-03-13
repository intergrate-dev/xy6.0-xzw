<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="syslog.view.title"/></title>
		<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
		<script type="text/javascript">
			function initForm() {
				var form = document.logquery;
				form.expressQuery.value = "<c:out value="${formBean.expressQuery}"/>";		
				form.logType.value = "<c:out value="${formBean.logType}"/>"; 
				form.pageSize.value = "<c:out value="${formBean.pageSize}"/>"; 
			}
			function prevPage(){
				document.logquery.pageNo.value--;
				document.logquery.submit();
			}
			function nextPage(){
				document.logquery.pageNo.value++;
				document.logquery.submit();
			}
			function firstPage(){
				document.logquery.pageNo.value = 1;
				document.logquery.submit();
			}
			function lastPage(){
				document.logquery.pageNo.value = 1000;
				document.logquery.submit();
			}
			function refresh(){
				document.logquery.submit();
			}
			function onExpressQuery( select_obj ) {		
				clearOthers();
				document.logquery.submit();
			}
			function clearOthers(){
				var obj = document.logquery;		
				obj.beginTime.value = "";
				obj.endTime.value = "";
				obj.msgOperator.value = "1";
				obj.msgValue.value = "";
				obj.operator.value = "";
				obj.operation.value = "";
				obj.userHost.value = "";
				obj.appDescription.value = "";
			}
		</script>
		<style type="text/css">
			.area{
				background: #F3F3F3;
				border: 1px solid #D1D1D1;
				padding: 5px;
			}
			.area td{
				padding:0 10px;
			}
			.area img{
				cursor: pointer;
			}
			.area td.clear-padding-left{
				padding-left:0;
			}
			.area td.clear-padding-right{
				padding-right:0;
			}
			.table{
				margin:20px auto;
			}
			.fr{
				float: right;
			}
			.fl{
				float: left;
			}
		</style>
	</head>
	<body onLoad="initForm()">
		<div class="mainBodyWrap">
		<form name="logquery" method="post" action="./logQuery.do">
			<input name="msgOperator" type="hidden" value="<c:out value="${formBean.msgOperator}"/>">
			<input name="msgValue" type="hidden" value="<c:out value="${formBean.msgValue}"/>">
			<input name="beginTime" type="hidden" value="<c:out value="${formBean.beginTime}"/>">
			<input name="endTime" type="hidden" value="<c:out value="${formBean.endTime}"/>">
			<input name="operator" type="hidden" value="<c:out value="${formBean.operator}"/>">
			<input name="operation" type="hidden" value="<c:out value="${formBean.operation}"/>">
			<input name="userHost" type="hidden" value="<c:out value="${formBean.userHost}"/>">
			<input name="appDescription" type="hidden" value="<c:out value="${formBean.appDescription}"/>">
			<input name="pageNo" type="hidden" value="<c:out value="${formBean.pageNo}"/>">
			<div class="area clearfix">
				<table cellspacing="0" cellpadding="0" class="fl">
					<tr>
						<td class="clear-padding-right"><i18n:message key="syslog.view.expressQuery"/></td>
						<td class="clear-padding-left">
							<select class="work" name="expressQuery" onChange="onExpressQuery(this)">
								<option value="0"><i18n:message key="syslog.view.expressQuery.any"/></option>
								<option value="1"><i18n:message key="syslog.view.expressQuery.today"/></option>
								<option value="2"><i18n:message key="syslog.view.expressQuery.thisweek"/></option>
								<option value="3"><i18n:message key="syslog.view.expressQuery.thismonth"/></option>
							</select>
						</td>
						<td class="clear-padding-right"><i18n:message key="syslog.view.logType"/></td>
						<td class="clear-padding-left">
							<select class="work" name="logType" onChange="onExpressQuery(this)">
								<option value="0"><i18n:message key="syslog.view.expressQuery.any"/></option>
								<option value="1"><i18n:message key="syslog.query.type.info"/></option>
								<option value="2"><i18n:message key="syslog.query.type.error"/></option>
								<option value="3"><i18n:message key="syslog.query.type.record"/></option>
								<option value="4"><i18n:message key="syslog.query.type.operate"/></option>
							</select>
						</td>
						<td>
							<button class="button" onclick="window.location.href='logquery.jsp';return false;"><i18n:message key="syslog.view.userquery"/></button>
						</td>
	            		<%--<td>--%>
	            			<%--<button class="button" onclick="window.location.href='logdelete.jsp';return false;"><i18n:message key="syslog.view.delete"/></button>--%>
						<%--</td>--%>
					</tr>
				</table>
				<table cellspacing="0" cellpadding="0" class="fr">
					<tr>
						<td>
							<img src="../../images/startpage.gif" width="20" height="20" onclick='firstPage()'>
						</td>
						<td>
							<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'>
						</td>
						<td>
							<select class="work" size="1" name="pageSize" onChange="refresh()">
								<option value="25">25</option>
								<option value="50">50</option>
								<option value="100">100</option>
								<option value="250">250</option>
								<option value="500">500</option>
							</select>
						</td>
						<td>
							<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'>
						</td>
						<td>
							<img src="../../images/endpage.gif" width="20" height="20" onclick='lastPage()'>
						</td>
						<td>
							<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'>
						</td>
						<td>
							<c:out value="${formBean.pageNo}"/>/<c:out value="${formBean.totalPageCnt}"/>[<c:out value="${formBean.totalCnt}"/>]
						</td>
					</tr>
				</table>
			</div>
			<table cellPadding="0" cellSpacing="0" class="table">
				<caption><i18n:message key="syslog.view.title"/></caption>
				<tr>
					<th><i18n:message key="syslog.view.finishTime"/></th>
					<th><i18n:message key="syslog.view.logType"/></th>
					<th><i18n:message key="syslog.view.operator"/></th>
					<th><i18n:message key="syslog.view.operation"/></th>
					<th><i18n:message key="syslog.view.userHost"/></th>
					<th><i18n:message key="syslog.view.subapp"/></th>
					<th><i18n:message key="syslog.view.message"/></th>
				</tr>
				<% int pageCnt = 0; //local variable indicating records on page %>
				<c:forEach items="${result}" var="bean" varStatus="status">
					<tr>
						<td><c:out value="${bean.finishTime}"/></td>
						<td><c:out value="${bean.logType}"/></td>
						<td><c:out value="${bean.operator}"/></td>
						<td><c:out value="${bean.operation}"/></td>
						<td><c:out value="${bean.userHost}"/></td>
						<td><c:out value="${bean.appDescription}"/></td>
						<td><c:out value="${bean.description}"/></td>
					</tr>
					<% pageCnt++; %>
				</c:forEach>
			</table>
			<% if( pageCnt>20 ) {%>
			<div class="area clearfix">
				<table cellspacing="0" cellpadding="0" class="fr">
					<tr>
						<td>
							<img src="../../images/startpage.gif" width="20" height="20" onclick='firstPage()'>
						</td>
						<td>
							<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'>
						</td>
						<td>
							<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'>
						</td>
						<td>
							<img src="../../images/endpage.gif" width="20" height="20" onclick='lastPage()'>
						</td>
						<td>
							<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'>
						</td>
						<td>
							<c:out value="${formBean.pageNo}"/>/<c:out value="${formBean.totalPageCnt}"/>[<c:out value="${formBean.totalCnt}"/>]
						</td>
					</tr>
				</table>
			</div>
			<%}%>
		</form>
		</div>
	</body>
</html>