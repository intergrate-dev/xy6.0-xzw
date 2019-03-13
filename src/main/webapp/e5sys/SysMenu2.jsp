<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<link type="text/css" rel="stylesheet" href="../e5style/sys-style-sysMenu2.css"/>
<div id="SysMenu" class="SysMenu2">
	<div class="menuShadow"></div>
	<div class="logo" title="e5基础工作平台">e5基础工作平台</div>
	<c:forEach var="menu" items="${menus}" varStatus="index">
		<div id="div<c:out value="${index.index}"/>" class="menuTop<c:if test="${index.index == 0}"> cur</c:if>">
			<a class="submenu" href="#">
				<span style="background-image:url('<c:out value="${menu.icon}"/>')" alt="<c:out value="${menu.name}"/>"><c:out value="${menu.name}"/></span>
			</a>
			<div class="accordionTabContentBox">
				<c:forEach var="item" items="${menu.items}">
					<c:if test="${(!item.separator) and (user.admin or !item.onlyAdmin) and (!item.disabled or sessionScope.sysmenuenabled)}">
						<a onclick="show('<c:out value="${item.url}"/>')" class="divmenuitem" href="#">
							<c:out value="${item.name}"/>
						</a>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</c:forEach>
	<!--应用系统设置-->
	<c:forEach var="appPage" items="${appPages}">
		<div id="divApp" class="menuTop">
			<a class="submenu" href="#"><span><c:out value="${appPage.appName}"/></span></a>
			<div class="accordionTabContentBox">
				<c:forEach var="page" items="${appPage.pages}">
					<a onclick="show('<c:out value="${page.webURL}"/>')" class="divmenuitem" href="#">
						<c:out value="${page.webName}"/>
					</a>
				</c:forEach>
			</div>
		</div>
	</c:forEach>
	<div class="menuTop" id="divWindow"><!--窗口：刷新、重新登录、退出-->
		<a class="submenu" href="#">
			<span style="background-image:url('../images/menu-window.gif')" alt="<i18n:message key="sysmenu.window.main"/>"><i18n:message key="sysmenu.window.main"/></span>
		</a>
  		<div class="accordionTabContentBox">
			<a onclick="refresh()" class="divmenuitem" href="#">
				<i18n:message key="sysmenu.window.refresh"/>
			</a>
			<a onclick="viewChange()" class="divmenuitem" href="#"/>
				<i18n:message key="sysmenu.window.menuType"/>
			</a>
			<!--
			<div onclick="styleChange()" class="divmenuitem">
				<i18n:message key="sysmenu.window.styleType"/>
			</div>
			-->
			<a onclick="reLogin()" class="divmenuitem" href="#"/>
				<i18n:message key="sysmenu.window.relogin"/>
			</a>
			<a onclick="window.close()" class="divmenuitem" href="#"/>
				<i18n:message key="sysmenu.sys.logout"/>
			</a>
		</div>
	</div>
</div>
<div class="mainWrap2">
	<iframe frameborder=0 id="SysFrame" class="SysFrame" name="SysFrame" scrolling="auto" src="" style="display:none;"></iframe>
</div>
<script type="text/javascript">
	var menuTops = $(".menuTop"),
		divmenuitems = $(".divmenuitem").click(changeClass2);
	$(".submenu").click(changeClass1);
	function changeClass1(){
		var elm;
		elm = $(this);
		menuTops.removeClass("cur");
		elm.parent().addClass("cur");
	}
	function changeClass2(){
		var elm;
		elm = $(this);
		divmenuitems.removeClass("cur");
		elm.addClass("cur");
	}
</script>