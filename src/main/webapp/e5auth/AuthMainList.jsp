<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><i18n:message key="page.title"/></title>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.tab.js"></script>
		<script type="text/javascript">
			var tab1;
			$(function(){
				tab1 = $("#tabs").tabs("#panes",{api:true}).click("click",0);
			});
			function doDeal(type){
				if (type == 1)
					allform.submit();
				else
				{
					confirm('<i18n:message key="auth.hint"/>');
				}
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			.btnArea{
				padding:20px 5px;
			}
			.pane{
				height:400px;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap" id="tab-pane">
			<ul id="tabs" class="tabs clearfix">
				<c:if test="${sessionScope.adminOption.organ}">
					<li class="tab" id="pageOrgan"><a href="#p1"><i18n:message key="page.org"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.document}">
					<li class="tab" id="pageDoc"><a href="#p2"><i18n:message key="page.doc"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.workflow}">
					<li class="tab" id="pageFlow"><a href="#p3"><i18n:message key="page.workflow"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.category}">
					<li class="tab" id="pageCat"><a href="#p4"><i18n:message key="page.category"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.module}">
					<li class="tab" id="pageModule"><a href="#p5"><i18n:message key="page.module"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.modulePerm}">
					<li class="tab" id="pagePerm"><a href="#p6"><i18n:message key="page.moduleperm"/></a></li>
				</c:if>
				<c:if test="${sessionScope.adminOption.online or sessionScope.adminOption.cache or sessionScope.adminOption.service or sessionScope.adminOption.logger or sessionScope.adminOption.config}">
					<li class="tab" id="pageOther"><a href="#p7"><i18n:message key="page.other"/></a></li>
				</c:if>
			</ul>
			<div id="panes" class="panes">
				<c:if test="${sessionScope.adminOption.organ}">
					<div class="pane">
						<a name="p1" id="p1"></a>
						<iframe src="partInit.do?Type=1" frameborder=0 scrolling="auto"></iframe>
					</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.document}">
					<div class="pane">
						<a name="p2" id="p2"></a>
						<iframe src="partInit.do?Type=2" frameborder=0 scrolling="no"></iframe>
					</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.workflow}">
					<div class="pane">
						<a name="p3" id="p3"></a>
						<iframe src="partInit.do?Type=3" frameborder=0 scrolling="auto"></iframe>
					</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.category}">
					<div class="pane">
						<a name="p4" id="p4"></a>
						<iframe src="partInit.do?Type=4" frameborder=0 scrolling="auto"></iframe>
					</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.module}">
					<div class="pane">
						<a name="p5" id="p5"></a>
						<iframe src="partInit.do?Type=5" frameborder=0 scrolling="auto"></iframe>
					</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.modulePerm}">
						<div class="pane">
							<a name="p6" id="p6"></a>
							<iframe src="partInit.do?Type=6" frameborder=0 scrolling="auto"></iframe>
						</div>
				</c:if>
				<c:if test="${sessionScope.adminOption.online or sessionScope.adminOption.cache or sessionScope.adminOption.service or sessionScope.adminOption.logger or sessionScope.adminOption.config}">
						<div class="pane">
							<a name="p7" id="p7"></a>
							<iframe src="partInit.do?Type=7" frameborder=0 scrolling="auto"></iframe>
						</div>
				</c:if>
			</div>
			<div class="btnArea">
				<input type="button" value="<i18n:message key="form.submit"/>" class="button" onclick="doDeal(1)">
				<input type="button" value="<i18n:message key="form.cancel"/>" class="button" onclick="doDeal(2)">
				&nbsp;<span style="color:red"><i18n:message key="auth.memo"/><span>
			</div>
		</div>
<iframe name="iframe" src="" frameborder=0 scrolling="auto"></iframe>
<form id="allform" name="allform" target="iframe" action="./submitPart.do">
	<Input Type="hidden" Name="Type" value="8">
</form>
</body>
</html>
