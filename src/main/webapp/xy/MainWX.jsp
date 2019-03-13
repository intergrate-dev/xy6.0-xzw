<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
<style type="text/css">
	label{
		font-weight: 100;
		padding: 0;
		margin: 0;
	}
	.account{padding-left:10px;}
	.menu-main{padding-left:30px;color:navy;}
	.menu-sub{padding-left:50px;}
	
</style>
<div id="main_resourcetree" class="sidebar" >
	<div class="div-left">账号</div>
	<hr class="hr" />
	<div id="rs_tree" class="ztree">
		<ul id="groupUl">
			<c:forEach var="account" items="${accounts}">
				<c:if test="${!isArticle}">
				<li class="group" groupID="<c:out value="${account.id}"/>">
					<div class="account" groupID="<c:out value="${account.id}"/>"><c:out value="${account.name}" /></div>
				</li>
				</c:if>
				<c:if test="${isArticle}">
				<li class="group">
					<!-- 账号 -->
					<div class="account"><c:out value="${account.name}" /></div>
					
					<c:forEach var="menu" items="${account.menus}">
						<!-- 1级菜单 -->
						<div class="menu-main" level="0"><c:out value="${menu.name}"/></div>
						
						<!-- 2级菜单 -->
						<c:forEach var="subMenu" items="${menu.children}">
							<div class="menu-sub" groupID="<c:out value="${subMenu.id}"/>" level="1"><c:out value="${subMenu.name}"/></div>
						</c:forEach>
					</c:forEach>
				</li>
				</c:if>
			</c:forEach>
		</ul>
	</div>
</div>
	<div id="wrapMain">
		<%@include file="inc/Search.inc"%>
		<div id="main">
			<div id="panContent" class="panContent">
				<div class="tabHr toolkitArea">
					<%@include file="inc/Toolkit.inc"%>
				</div>
				<%@include file="inc/Statusbar0.inc"%>
			</div>
		</div>
	</div>
</body>
<c:choose>
	<c:when test="${isArticle}">
	<script type="text/javascript" src="script/MainWXArticle.js"></script>
	<script type="text/javascript" src="script/StatusbarMain.js"></script>
	</c:when>
	<c:otherwise>
	<script type="text/javascript" src="script/MainWX.js"></script>
	</c:otherwise>
</c:choose>

<%@include file="inc/MainFooter.inc"%>
<c:if test="${subTab.id == 'wxarticle'}">
<script type="text/javascript" src="script/tabledrag.js"></script>
</c:if>