<%@page pageEncoding="UTF-8"%>
<style type="text/css">
	.btn-group{
		position: relative;
	}
	.book{
		position: absolute;
		top: 9px;
  		left: 5px;
	}
	.triangle{
		position: absolute;
		top: 13px;
  		left: 151px;
  		opacity: 0;
	}
	.nav-tabs{
		border: none;
	}
</style>
<link rel="stylesheet" type="text/css" href="script/jquery-smartMenu/css/smartMenu.css">
<div id="header">
<h1>
	<a href="#"></a>   
</h1>

<div id="user-nav" class="navbar navbar-inverse">
	<ul class="nav btn-group">
		<li <c:if test="${siteCount == 1}">style="display:none;"</c:if>>
			<img class="book" src="../Icons/book.png" alt="" />
			<img class="triangle" src="../Icons/triangle.png" alt="" />
		<select id="hdSites" name="hdSites">
			<c:forEach var="site" items="${sites}">
				<option value="<c:out value="${site.id}"/>"><c:out value="${site.name}"/></option>
			</c:forEach>
		</select>
		</li>
		<li class="btn btn-inverse dropdown" id="profile">
			<a href="#" class="dropdown-toggle">
				<i class="icon-user"></i>
				<span class="text"><c:out value="${sessionScope.sysUser.userName}"/></span>
			</a>
		</li>
		<li class="btn btn-inverse">
			<a id="hdExit" href="#">
				<i class="icon icon-share-alt"></i>
				<span class="text"><i18n:message key="workspace.exit"/></span>
			</a>
		</li>
	</ul>
</div>
<ul id="nav" class="nav-top">
	<c:forEach var="tab" items="${tabs}">
		<li class="main-tab btn dropdown">
			<a href="#" class="dropdown-toggle" tabID="<c:out value="${tab.id}"/>" id="<c:out value="${tab.id}"/>mainTab" >
				<c:out value="${tab.name}"/>
			</a>
		</li>
	</c:forEach>
</ul>
<ul id="nav" class="nav_tab">
	<c:forEach var="tab" items="${tabs}">
		
		<ul class="sub-tab dropdown-menu" id="subtab-<c:out value="${tab.id}"/>">
			<li style="height:27px; position: relative;margin-top: -1px;"></li>
			<img style="position: absolute; left:48px; top:0px; " src="../images/menu/topicon.png" alt="" />
			<c:forEach var="subtab" items="${tab.children}">
				<li>
					<a href="<c:out value="${subtab.url}"/>" tabID="<c:out value="${subtab.id}" />" id="subtab_a_<c:out value="${subtab.id}" />" pid='<c:out value="${tab.id}"/>' >
						<img src="../<c:out value="${subtab.icon}" />" />
						<span style="display: block;"><c:out value="${subtab.name}"/></span>
					</a>
				
				</li>
			</c:forEach>
		</ul>
	</c:forEach>
</ul>
<div id="TabATip" style="display:none;background: #e5ebf3;opacity:0.8; position: relative;z-index: 5001;color: #f71300;width: 100px; font-family:'宋体';font-size: 11px;border: 1px dashed #808690;padding: 4px 10px 4px 10px;white-space: nowrap;"><div>*请关闭多余的标签</div><i style="width:0;height: 0;border: 7px solid transparent; border-bottom-color: #e5ebf3; position: absolute; top:-14px;"></i></div>
<script type="text/javascript">
	var headerinfo = {
		confirm : "<i18n:message key="workspace.confirm.exit"/>",		
		novaliduser : "<i18n:message key="workspace.change.nouser"/>",
		nouser : "<i18n:message key="workspace.change.nouser"/>",
		norole : "<i18n:message key="workspace.login.norole"/>",
		expired:"<i18n:message key="workspace.login.expired"/>",
		novalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		invalidrole : "<i18n:message key="workspace.change.novalidrole"/>",
		adminaskquit : "<i18n:message key="workspace.change.adminaskquit"/>",
		haserror : "<i18n:message key="workspace.login.haserror"/>",
		otheragain : "<i18n:message key="workspace.login.otheragain"/>",
		i18n_lastIp : "<i18n:message key="workspace.message.lastIp"/>",
		i18n_lastTime : "<i18n:message key="workspace.message.lastTime"/>",
		i18n_nowIp : "<i18n:message key="workspace.message.nowIp"/>",
		siteID : "<c:out value="${siteID}"/>",
		userLoginName : "<%=com.founder.e5.workspace.ProcHelper.getUser(request).getUserCode() %>"
	};
	$(function(){
		$('#subtab_a_memcardlog').parent().css({'display':'none'});
                $('#subtab_a_paylog').parent().css({'display':'none'});
	});
</script>
<script type="text/javascript" src="script/cookie.js"></script>
<script type="text/javascript" src="script/jquery-smartMenu/js/mini/jquery-smartMenu-min.js"></script>
<script type="text/javascript" src="../e5script/jquery/jquery-ui/jquery-ui.min.js"></script><!---->
<script type="text/javascript" src="script/header.js"></script>

</div>
