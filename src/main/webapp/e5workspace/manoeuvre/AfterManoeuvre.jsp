<%@include file="../../e5include/IncludeTag.jsp"%>
<html>
<body>
<form name="afterfrm" action="../after.do" method="post">
<input type="Hidden" name="UUID" value="<c:out value="${offerkey.UUID}"/>">
<input type="Hidden" name="DocLibID" value="<c:out value="${offerkey.docLibID}"/>">
<input type="Hidden" name="DocIDs" value="<c:out value="${offerkey.docIDs}"/>">
<input type="Hidden" name="Opinion" value="<c:out value="${offerkey.opinion}"/>">
</form>
</body>
</html>
<script type="text/javascript">
	afterfrm.submit();
</script>
