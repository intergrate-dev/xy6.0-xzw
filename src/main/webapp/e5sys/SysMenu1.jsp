<script type="text/javascript" src="../e5script/xmenu/xmenu-modify-by-zxc.js"></script>
<link type="text/css" rel="stylesheet" href="../e5script/xmenu/xmenu-modify-by-zxc.css"/>
<script type="text/javascript">
webFXMenuHandler.getArrowPath = function(){
	return "../images/arrow.gif";
}
</script>
<div id="SysMenu" class="SysMenu1">
	<div class="logo">e5基础工作平台</div>
	<script type="text/javascript">
		function prepare(){
			webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
			webFXMenuConfig.defaultWidth = 150;

			var myBar = new WebFXMenuBar;
			var myMenu;
			
			myMenu = new WebFXMenu;
			myMenu.add(new WebFXMenuItem("机构管理" ,"show('../e5sys/org/OrgTreeGenerate.do?invoke=index')"));
			myMenu.add(new WebFXMenuItem("站点管理" ,"show('../e5sys/DataMain.do?type=SITE')"));
			myMenu.add(new WebFXMenuSeparator());
			myMenu.add(new WebFXMenuItem("网站目录" ,"show('../xy/site/DomainDir.jsp')"));
			myMenu.add(new WebFXMenuItem("网站规则" ,"show('../xy/site/Rule.jsp')"));
			myMenu.add(new WebFXMenuSeparator());
			
			<c:forEach var="menu" items="${menus}">
				if (myMenu == null) myMenu = new WebFXMenu;
				
				<c:forEach var="item" items="${menu.items}">
					<c:if test="${(user.admin or (!item.onlyAdmin)) and (!item.disabled or sessionScope.sysmenuenabled)}">
						<c:choose>
							<c:when test="${item.separator}">
								myMenu.add(new WebFXMenuSeparator());
							</c:when>
							<c:otherwise>
								myMenu.add(new WebFXMenuItem("<c:out value="${item.name}"/>"
									,"show('<c:out value="${item.url}"/>')"));
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
				if (myMenu._menuItems.length > 0)
					myBar.add(new WebFXMenuButton("<c:out value="${menu.name}"/>", null, null, myMenu));
				myMenu = null;
			</c:forEach>

			myBar.generate();
		}
		prepare();
	</script>
</div>
<div class="mainWrap1">
<iframe frameborder="0" id="SysFrame" class="SysFrame" name="SysFrame" scrolling="auto" src="" style="display:none;"></iframe>
</div>