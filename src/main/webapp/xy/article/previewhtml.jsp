<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<html lang="zh-CN">
<head>
    <title>稿件详情</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>
<script type="text/javascript">
    var type = '<c:out value="${result.type}" />';
    var hasHtml = '<c:out value="${result.hasHtml}" />';
    var hasHtmlPad = '<c:out value="${result.hasHtmlPad}" />';
    window.onload= function(){
		if (parent.window.hideTab)
			parent.window.hideTab(type,hasHtml,hasHtmlPad);
    };

</script>

<body>
<c:out value="${result.html}" escapeXml="false"/>
</body>
</html>
