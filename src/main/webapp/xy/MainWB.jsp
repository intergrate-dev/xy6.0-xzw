<%@include file="../e5include/IncludeTag.jsp"%>

<%@include file="inc/MainHeader.inc"%>
<body>
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
				</li>
				</c:if>
			</c:forEach>
		</ul>
	</div>
</div>
	<div id="wrapMain">
		<div id='tab-wb-component' class='wwa-component-body'></div>
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
<%@include file="inc/MainFooter.inc"%>
<script type="text/javascript" src="script/MainWB.js"></script>