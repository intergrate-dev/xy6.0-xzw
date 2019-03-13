<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page import="com.founder.e5.config.*" %>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<%
	InfoCustomizeItem[] items = ConfigReader.getInstance().getCustomize().getItems();
	request.setAttribute("items", items);
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><i18n:message key="workspace.ps.title" /></title>
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
	<link type="text/css" rel="stylesheet" href="../../e5style/ws-personal-style.css" />
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.tab.js"></script>
	<script type="text/javascript">
		var needrefresh = false,
			tab1;
		function setRefresh(flag){
			needrefresh = flag;
		}
		function beforeExit(){
			try {
				if (needrefresh){
					window.top.refresh();
				}
			}catch (e){

			}
			window.onbeforeunload = "javascript:void(0);";
		}
		$(function(){
			tab1 = $("#tabs").tabs("#panes");
		})
	</script>
</head>
	<body><!--onbeforeunload="beforeExit()"-->
		<div id="tab-pane">
			<ul id="tabs" class="tabs clearfix">
				<c:forEach var="item" items="${items}" varStatus="var">
				<li class="tab<c:if test="${var.index == 0}"> fst current</c:if>">
					<table cellspacing="0" cellpadding="0" class="tabBg">
						<tr>
							<td class="L"></td>
							<td class="C">
								<div><a href="#p<c:out value="${var.index}"/>"><c:out value="${item.name}"/></a></div>
							</td>
							<td class="R"></td>
						</tr>
					</table>
				</li>
				</c:forEach>
			</ul>
			<div id="panes" class="panes">
				<c:forEach var="item" items="${items}" varStatus="var">
				<div class="pane">
					<a name="p<c:out value="${var.index}"/>" id="p<c:out value="${var.index}"/>"></a>
					<iframe class="iframe" src="<c:out value="${item.url}"/>" height="100%" width="100%" frameborder="0"></iframe>
				</div>
				</c:forEach>
			</div>
		</div>
	</body>
</html>