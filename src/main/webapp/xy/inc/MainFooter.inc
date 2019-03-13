<script type="text/javascript" src="./script/doclist.onresize-for-main.js"></script>
<c:forEach var="js" items="${domInfo.queryScripts}">
	<script type="text/javascript" src="../<c:out value="${js}"/>"></script>
</c:forEach>

</html>
