<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>

<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
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
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Script type="text/javascript">
		function showPermission(catTypeID, catName)
		{
			var theURL = "./CatMain.jsp?CatTypeID=" + catTypeID
					+ "&CatTypeName=" + encodeSpecialCode(catName);
			GetFrameByName(window.parent, "CatPermission").location.href = theURL;
		}
	</Script>
</HEAD>
<BODY>
	<%@include file="../e5include/Error.jsp"%>
	<table class="table">
		<caption><i18n:message key="cat.hint"/></caption>
		<c:forEach var="type" items="${types}">
			<tr title="<c:out value="${type.name}"/>"
				onclick="showPermission('<c:out value="${type.catType}"/>', '<c:out value="${type.name}"/>');"
				>
				<td><c:out value="${type.name}"/></td>
			</tr>
		</c:forEach>
</BODY>
</HTML>
