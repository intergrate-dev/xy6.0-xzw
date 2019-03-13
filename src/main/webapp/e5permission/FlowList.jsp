<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Script type="text/javascript" src="../e5script/xmenu/xmenu.js"></Script>
	<link type="text/css" rel="stylesheet" href="../e5script/xmenu/xmenu.css">
<style type="text/css">
.webfx-menu-bar {
	background:	url(../images/menu_bg1.gif);
	height: 30px;
	border:	0px;
	padding : 0px;
	margin: 0px;
}
.webfx-menu-bar a,
.webfx-menu-bar a:visited{
	color:	white;
	border:	0px;
	height: 30px;
	padding : 4px;
	width : 110px;
}
.webfx-menu-bar a:hover {
	color: white;
	background:	url(../images/menu_bg2.gif);
	height: 30px;
	border:	0px;
}
</style><Script type="text/javascript">
	function prepare()
	{
		webFXMenuConfig.imagePath	= "../e5script/xmenu/images/";
		webFXMenuConfig.defaultWidth = 150;

		var myBar = new WebFXMenuBar;
		<c:forEach var="docTypeFlow" items="${flowList}" varStatus="docTypeIndex">
			var myMenu = new WebFXMenu;
			<c:forEach var="flow" items="${docTypeFlow.flows}">
				myMenu.add(new WebFXMenuItem("<c:out value="${flow.name}"/>"
					,"showPermission(<c:out value="${flow.ID}"/>)"));
			</c:forEach>
			myMenu.add(new WebFXMenuItem("<i18n:message key="flow.unflow"/>"
					,"showUnflow(<c:out value="${docTypeFlow.docTypeID}"/>)"));
			myBar.add(new WebFXMenuButton("<c:out value="${docTypeFlow.docTypeName}"/>", null, null, myMenu));
		</c:forEach>
		myBar.generate();
	}
	prepare();

	function showPermission(flowID)
	{
		getIframe("FlowPermission").src = "./mainFlow.do?FlowID=" + flowID;
	}
	function showUnflow(docTypeID)
	{
		getIframe("FlowPermission").src = "./mainUnflow.do?DocTypeID=" + docTypeID;
	}

</Script>
</HEAD>
<BODY>
	<%@include file="../e5include/Error.jsp"%>
	<IFrame frameborder=0 id="FlowPermission" name="FlowPermission" scrolling="auto"
		style="height:100%;min-height:600px;width:100%;" src="">
	</IFrame>
</BODY>
</HTML>
