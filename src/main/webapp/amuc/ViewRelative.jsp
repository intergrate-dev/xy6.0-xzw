<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<HEAD>
	<TITLE>相关的列表</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/e5.min.js"></script>
	<script type="text/javascript">
		var pathPrefix = "";
	</script>
	<link rel="stylesheet" type="text/css" href="../amuc/script/bootstrap/css/bootstrap.css"/>
	<c:if test="${showSearchBar}">
		<link rel="stylesheet" type="text/css" href="../amuc/css/query-custom.css"/>
	</c:if>
	<link rel="stylesheet" type="text/css" href="../amuc/css/main.css">
</HEAD>
<c:if test="${showToolkit }">
	<div class="tabHr toolkitArea">
		<%@include file="inc/Toolkit.inc"%>
	</div>
</c:if>
<c:if test="${showSearchBar}">
	<%@include file="inc/ViewRelativeSearch.inc"%>
</c:if>
<c:if test="${!showSearchBar}">
	<%@include file="inc/ViewRelativeNoSearch.inc"%>
</c:if>
<script type="text/javascript" src="../amuc/script/doclist.onresize-for-main.js"></script>
</html>