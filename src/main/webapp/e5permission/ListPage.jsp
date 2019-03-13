<%@include file="../e5include/IncludeTag.jsp"%>
<%
	java.util.List sysPages = (java.util.List)(request.getAttribute("sysPages"));
	int sysPageCount = (sysPages == null) ? 0 : sysPages.size();

	com.founder.e5.app.AppPermission[] appPages = (com.founder.e5.app.AppPermission[])(request.getAttribute("appPages"));
	int appPageCount = (appPages == null) ? 0 : appPages.length;

	request.setAttribute("sysPageCount", new Integer(sysPageCount));
	request.setAttribute("appPageCount", new Integer(appPageCount));
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Permission config of E5</title>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.tab.js"></script>
		<script type="text/javascript">
			var tab1;
			$(function(){
				tab1 = $("#tabs").tabs("#panes");
			});
			function doInit() {
				var _clientH = $(window).height();
				$(".pane").css("height", _clientH - 60);
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	</head>
	<body onload="doInit()">
		<div id="tab-pane" class="mainBodyWrap">
			<ul id="tabs" class="tabs clearfix">
				<c:forEach var="apppage" items="${appPages}" varStatus="status">
				<li class="tab<c:if test="${status.index == 0}"> fst current</c:if>">
					<a href="#p<c:out value="${sysPageCount + status.index}"/>"><c:out value="${apppage.resourceType}"/></a>
				</li>
				</c:forEach>
				<c:forEach var="syspage" items="${sysPages}" varStatus="status">
				<li class="tab<c:if test="${status.index == 0}"> fst current</c:if>">
					<a href="#p<c:out value="${status.index}"/>"><c:out value="${syspage.name}"/></a>
				</li>
				</c:forEach>
			</ul>
			<div id="panes" class="panes">
				<c:forEach var="apppage" items="${appPages}" varStatus="status">
				<div class="pane">
					<a name="p<c:out value="${sysPageCount + status.index}"/>" id="p<c:out value="${sysPageCount + status.index}"/>"></a>
					<iframe class="iframe" src="<c:out value="${apppage.commonURL}"/>" frameborder="0"></iframe>
				</div>
				</c:forEach>
				<c:forEach var="syspage" items="${sysPages}" varStatus="status">
				<div class="pane">
					<a name="p<c:out value="${status.index}"/>" id="p<c:out value="${status.index}"/>"></a>
					<iframe class="iframe" src="<c:out value="${syspage.url}"/>" frameborder="0"></iframe>
				</div>
				</c:forEach>
			</div>
		</div>
	</body>
</html>
