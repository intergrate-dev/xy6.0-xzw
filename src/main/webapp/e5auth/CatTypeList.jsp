<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5auth" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Script type="text/javascript">
		function showPermission(catTypeID, catName)
		{
			var theURL = "./CatMain.jsp?CatTypeID=" + catTypeID
					+ "&CatTypeName=" + encodeSpecialCode(catName);
			GetFrameByName(window.parent, "CatPermission").location.href = theURL;
		}
	</Script>
	<Style>
		.typelist{
			padding: 5px;
		}
		.cattype{
			cursor:hand;
			text-align:center;
			border-bottom: 1px solid #868686;
		}
		#cathint{
			background:url(../images/t-bar.gif);
			text-align:center;
			color:white;
		}
	</Style>
</HEAD>
<BODY>
	<DIV id="cathint"><i18n:message key="cat.hint"/></DIV>
	<BR>
	<DIV class="typelist">
		<c:forEach var="type" items="${sessionScope.CatInfos.types}">
			<DIV class="cattype" title="<c:out value="${type.name}"/>"
				onclick="showPermission('<c:out value="${type.catType}"/>', '<c:out value="${type.name}"/>');">
				<c:out value="${type.name}"/>
			</DIV>
		</c:forEach>
	</DIV>
</BODY>
</HTML>
