<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslogin" changeResponseLocale="false"/>

<html>
	<head>
		<title><i18n:message key="loginarchive.view.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
			function initForm(){
				var f = document.form1;
				f.startIndex.value = "<c:out value="${pageData.startIndex}"/>";
				f.pageSize.value = "<c:out value="${pageData.pageSize}"/>";
				f.userCode.value = "<c:out value="${pageData.formBean.userCode}"/>";
				f.userName.value = "<c:out value="${pageData.formBean.userName}"/>";
				f.roleName.value = "<c:out value="${pageData.formBean.roleName}"/>";
				f.hostName.value = "<c:out value="${pageData.formBean.hostName}"/>";
				f.serverName.value = "<c:out value="${pageData.formBean.serverName}"/>";
				f.beginTime.value = "<c:out value="${pageData.formBean.beginTime}"/>";
				f.endTime.value = "<c:out value="${pageData.formBean.endTime}"/>";
			}
			function refresh(){
				document.form1.submit();
			}
			function gotoPage(index){
				document.form1.startIndex.value = index;
				document.form1.submit();
			}
			function prevPage(){
				gotoPage(<c:out value="${pageData.prevIndex}"/>);
			}
			function nextPage(){
				gotoPage(<c:out value="${pageData.nextIndex}"/>);
			}
			function clear() {
				var f = document.form1;		
				f.userCode.value = "";
				f.userName.value = "";
				f.roleName.value = "";
				f.hostName.value = "";
				f.serverName.value = "";
				f.beginTime.value = "";
				f.endTime.value = "";
			}
			function queryAll() {
				clear();
				refresh();
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
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
<body onload="initForm()">
	<div class="mainBodyWrap">
		<form name="form1" method="post" action="loginArchive.do">
			<input name="startIndex" type="hidden" />
			<input name="userCode" type="hidden" />
			<input name="userName" type="hidden" />
			<input name="roleName" type="hidden" />
			<input name="hostName" type="hidden" />
			<input name="serverName" type="hidden" />
			<input name="beginTime" type="hidden" />
			<input name="endTime" type="hidden" />
			<input name="" type="hidden" />
			<div class="area clearfix">
				<table cellspacing="0" cellpadding="0" class="fl">
					<tr>
						<td>
							<button class="button" onclick="window.location.href='LoginArchiveQuery.jsp';return false;"><i18n:message key="loginarchive.view.query"/></button>
						</td>
						<td>
							<button class="button" onclick="queryAll()"><i18n:message key="loginarchive.view.expressQuery.all"/></button>
						</td>
						<%--<td>--%>
							<%--<button class="button" onclick="window.location.href='LoginArchiveDelete.jsp';return false;"><i18n:message key="loginarchive.view.clear"/></button>--%>
						<%--</td>--%>
					</tr>
				</table>
				<table cellspacing="0" cellpadding="0" class="fr">
					<tr>
					<td>
						<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'/>
					</td>
					<td>
						<select class="work" size="1" name="pageSize" onchange="refresh()">
							<option value="25">25</option>
							<option value="50">50</option>
							<option value="100">100</option>
							<option value="250">250</option>
							<option value="500">500</option>
						</select>
					</td>
					<td>
						<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'/>
					</td>
					<td>
						<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'/>
					</td>
					<td><c:out value="${pageData.pageNo}"/>/<c:out value="${pageData.totalPages}"/>[<c:out value="${pageData.totalCount}"/>]</td>
				  </tr>
				</table>
			</div>
			<table cellPadding="0" cellSpacing="0" class="table">
				<caption><i18n:message key="loginarchive.view.title"/></caption>
				<tr>
					<th><i18n:message key="loginarchive.view.table.loginTime"/></th>
					<th><i18n:message key="loginarchive.view.table.userCode"/></th>
					<th><i18n:message key="loginarchive.view.table.userName"/></th>
					<th><i18n:message key="loginarchive.view.table.roleName"/></th>
					<th><i18n:message key="loginarchive.view.table.hostName"/></th>
					<th><i18n:message key="loginarchive.view.table.serverName"/></th>
					<th><i18n:message key="loginarchive.view.table.lastAccessTime"/></th>
					<th><i18n:message key="loginarchive.view.table.normalExit"/></th>
				</tr>
				<% int pageCnt = 0; //local variable indicating records on page %>
				<c:forEach items="${pageData.items}" var="bean" varStatus="status">
					<tr height="26" bgColor="#f7f7f7" onmouseover="this.bgColor='#E4E8EB';" 
						  onmouseout="this.bgColor='#f7f7f7';">
						<td><c:out value="${bean.loginTime}"/></td>
						<td><c:out value="${bean.userCode}"/></td>
						<td><c:out value="${bean.userName}"/></td>
						<td><c:out value="${bean.roleName}"/></td>
						<td><c:out value="${bean.hostName}"/></td>
						<td><c:out value="${bean.serverName}"/></td>
						<td><c:out value="${bean.lastAccessTime}"/></td>
						<td><c:out value="${bean.normalExitDisplay}"/></td>
					</tr>
					<% pageCnt++; %>
				</c:forEach>
			</table>
			<% if( pageCnt>20 ) {%>
			<div class="area clearfix">
				<table cellspacing="0" cellpadding="0" class="fr">
					<tr align="center">
						<td>
							<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'>
						</td>
							<td>
							<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'>
						</td>
							<td>
							<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'>
						</td>
						<td><c:out value="${pageData.pageNo}"/>/<c:out value="${pageData.totalPages}"/>[<c:out value="${pageData.totalCount}"/>]</td>
					</tr>
				</table>
			</div>
			<%}%>
		</form>
	</div>
</body>
</html>