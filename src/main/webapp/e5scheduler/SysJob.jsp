<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><i18n:message key="scheduler.sysJob.title"/></title>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script language="javascript">
			var selectedTr = '';
			//选中一个记录的函数
			function selected(id){
				if(selectedTr!=''){
					document.getElementById(selectedTr).style.backgroundColor='';
				}
				selectedTr = id;
				doModify(id);
			}
			function doModify(id){
				document.getElementById(id).style.backgroundColor="#E4E8EB";
				getIframe("bodyFrame").src = "SysJobEdit.do?action=edit&id="+id;
			}
			function doDelete(id){
				var ok = confirm("<i18n:message key="sysJob.confirm.delete"/>");
				if(ok)
					location.href="SysJob.do?action=delete&id="+id;
			}
			function doAdd(){
				getIframe("bodyFrame").src = "SysJobEdit.do?action=new";
			}
			function doTrigger(id){
				getIframe("bodyFrame").src = "SysTrigger.do?jobID=" + id;
			}
			function doReload() {
				location.href="SysJob.do";
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			.bluetext{
				color:blue;
				cursor: pointer;
			}
			.sidebar{
				margin-right:-500px;
				width:500px;
				float:left;
			}
			.main-area{
				margin-left:520px;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap">
			<div class="sidebar">
				<table cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="scheduler.sysJob.title"/></caption>
					<tr>
						<th width="10%">ID</th>
						<th width="40%"><i18n:message key="scheduler.sysJob.name"/></th>
						<th width="15%">&nbsp;</th>
						<th width="15%">&nbsp;</th>
						<th width="20%">&nbsp;</th>
					</tr>
					<c:forEach var="item" items="${list}">
						<tr id="<c:out value="${item.jobID}"/>" style="cursor:hand">
							<td onclick="selected('<c:out value="${item.jobID}"/>')"><c:out value="${item.jobID}"/></td>
							<td onclick="selected('<c:out value="${item.jobID}"/>')">
								<c:out value="${item.name}"/>
								<c:if test="${item.active=='Y'}">
									(<i18n:message key="scheduler.active.Y"/>)
								</c:if>
								<c:if test="${item.active=='N'}">
									(<i18n:message key="scheduler.active.N"/>)
								</c:if>
								<c:if test="${item.active=='A'}">
									(<i18n:message key="scheduler.active.A"/>)
								</c:if>
							</td>
							<td>
								<span class="bluetext" onclick="doTrigger(<c:out value="${item.jobID}"/>)"><i18n:message key="scheduler.button.setTrigger"/></span>
							</td>
							<td>
				  				<span class="bluetext" onclick="doModify(<c:out value="${item.jobID}"/>)"><i18n:message key="scheduler.button.modify"/></span>
								<span class="bluetext" onclick="doDelete(<c:out value="${item.jobID}"/>)"><i18n:message key="scheduler.button.delete"/></span>
							</td>
							<td>
								<c:choose>
									<c:when test="${item.configUrl == ''}">
										<span disabled><i18n:message key="scheduler.sysJob.config"/></span>
									</c:when>
									<c:otherwise>
										<a href="<c:out value="${item.configUrl}"/>"><i18n:message key="scheduler.sysJob.config"/></a>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${item.logUrl == ''}">
										<span disabled><i18n:message key="scheduler.sysJob.log"/></span>
									</c:when>
									<c:otherwise>
										<a href="<c:out value="${item.logUrl}"/>"><i18n:message key="scheduler.sysJob.log"/></a>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="10" align="center">
				  			<input type="button" name="Submit" value="<i18n:message key="scheduler.button.add"/>" onclick="doAdd()" class="button">
						</td>
					</tr>
				</table>
			</div>
			<div class="main-area">
				<div id="editFrame">
					<iframe id="bodyFrame" src="" width="100%" height="420" frameborder="0"></iframe>
				</div>
			</div>
		</div>
	</body>
</html>
