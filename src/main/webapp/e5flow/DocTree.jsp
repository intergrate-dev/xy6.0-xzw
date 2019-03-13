<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<TITLE>Operation Management</TITLE>
	<link type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
</HEAD>
<script type="text/javascript">
	function refreshNode()
	{
		window.frames[0].refreshNode();
	}
	function refreshUpNode()
	{
		window.frames[0].refreshUpNode();
	}
	var isAdmin = false;
	<c:if test="${sessionScope.adminUser.admin}">
	isAdmin = true;
	</c:if>
</script>
<frameset id="test" cols="180,*" frameborder="0" framespacing="0">
  <frame src="DocTreeSrc.jsp" name="leftFrame" scrolling="AUTO">
  <frame src="" name="mainBody">
</frameset>
<noframes><body>
</body></noframes>
</html>
