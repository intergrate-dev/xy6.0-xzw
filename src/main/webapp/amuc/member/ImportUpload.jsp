<%@ include file="../../e5include/IncludeTag.jsp"%>
<html>
<head>
	<title>导入（第一步：上传文件）</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
	<c:if test="${hasError}">
		parent.alert("解析文件错误，请检查文件格式：<c:out value="${errorMsg}"/>");
	</c:if>
	<c:if test="${!hasError}">
		var sheets = [];
		var sheetHeaders = [];
		<c:forEach var="sheet" items="${headers}">
			sheets.push("<c:out value="${sheet.key}"/>");
			var header = [];
			<c:forEach var="v" items="${sheet.value}">
				header.push("<c:out value="${v}"/>");
			</c:forEach>
			sheetHeaders.push(header);
		</c:forEach>
		var file = "<c:out value="${file}"/>";
		
		parent.imp.loaded(file, sheets, sheetHeaders);
	</c:if>
	</script>
</head>
</html>