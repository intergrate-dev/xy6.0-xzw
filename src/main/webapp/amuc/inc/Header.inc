<div id="header">
<h1>
	<a href="#"><%=com.founder.e5.context.Context.getSystemName()%></a>   
</h1>
<!--测试阶段版本号，产品化后需要去掉__start-->
<div id="version">V1.0.1</div>
<!--测试阶段版本号，产品化后需要去掉__stop-->
<div id="user-nav" class="navbar navbar-inverse">
	<ul class="nav btn-group">
		<li class="btn btn-inverse dropdown" id="profile">
			<a href="#" class="dropdown-toggle" data-target="#" data-toggle="dropdown">
				<i class="icon-user"></i>
				<span class="text"><c:out value="${sessionScope.sysUser.userName}"/></span>
				<!--
				<span class="label label-important">5</span>
				<b class="caret"></b>
				-->
			</a>
			<!--
			<ul class="dropdown-menu">
				<li>
					<a href="#" title="" class="sAdd">消息提醒 <span class="label label-important">5</span>
					</a>
				</li>
			</ul>
			-->
		</li>
		<li class="btn btn-inverse dropdown" id="roles" style="display:none;">
			<a href="#" class="dropdown-toggle" data-target="#" data-toggle="dropdown">
				<span class="text">--助理用户切换--</span>
				<b class="caret"></b>
			</a>
			<ul class="dropdown-menu" id="hdRoles">
				<!-- <li><select id="hdRoles" name="hdRoles"></select></li> -->
			</ul>
		</li>
		<!-- 
		<li class="btn btn-inverse">
			<a id="hdExit" href="#">
				<i class="icon icon-share-alt"></i>
				<span class="text"><i18n:message key="workspace.exit"/></span>
			</a>
		</li>
		 -->
	</ul>
</div>
	<ul id="nav">
	<c:forEach var="tab" items="${tabs}">
		<li class="btn dropdown <c:out value='${tab.icon}'/>">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown">
				<span class="text"><c:out value="${tab.name}"/></span>
				<!--<b class="caret"></b> -->
			</a>
			<ul class="dropdown-menu">
				<c:forEach var="subtab" items="${tab.children}">
					<c:choose>
						<c:when test="${subtab.id=='separator'}">
							<li class="divider"></li>
						</c:when>
						<c:otherwise>
							<li>
								<a href="<c:out value="${subtab.url}"/>" seperate="<c:out value="${subtab.seperate}" />" subID="<c:out value="${subtab.id}" />"><c:out value="${subtab.name}"/></a>
							</li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</li>
	</c:forEach>
	</ul>
<script type="text/javascript">
	var headerinfo = {
		confirm : "<i18n:message key="workspace.confirm.exit"/>",		
		novaliduser : "<i18n:message key="workspace.change.nouser"/>",
		nouser : "<i18n:message key="workspace.change.nouser"/>",
		novalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		invalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		adminaskquit : "<i18n:message key="workspace.change.adminaskquit"/>",
		haserror : "<i18n:message key="workspace.login.haserror"/>",
		otheragain : "<i18n:message key="workspace.login.otheragain"/>",
		i18n_lastIp : "<i18n:message key="workspace.message.lastIp"/>",
		i18n_lastTime : "<i18n:message key="workspace.message.lastTime"/>",
		i18n_nowIp : "<i18n:message key="workspace.message.nowIp"/>",
		curTab : "<c:out value="${subTab.id}"/>"
	}
	function go2Home() {
		window.onbeforeunload = "javascript:void(0);";
		var theURL = "../amuc/MyConfig.do?t=default";
		window.location.href = theURL;
	}
	function go2Self() {
		window.onbeforeunload = "javascript:void(0);";
		var theURL = "<c:out value="${subTab.url}"/>";
		window.location.href = theURL;
	}
</script>
<script type="text/javascript" src="../amuc/script/header.js"></script>
</div>

<div id="breadcrumb">
	<a class="tip-bottom" title="" href="#" onclick="go2Home();" data-original-title="Go to Home"><i class="icon-home"></i> </a>
	<a href="#"><c:out value="${subTab.pName}"/></a>
	<a class="current" href="#" onclick="go2Self();" ><c:out value="${subTab.name}"/></a>
</div>
