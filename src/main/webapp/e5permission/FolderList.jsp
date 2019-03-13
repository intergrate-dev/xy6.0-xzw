<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>

<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<Script type="text/javascript" src="../e5script/Function.js"></Script>
	<Script type="text/javascript">
		function showPermission()
		{
			var sel = document.getElementById("selDocLib");
			var theURL = "./mainFolder.do?DocLibID=" + sel.value;
			getIframe("FolderPermission").src = theURL;
		}
	</Script>
	<Style>
		select{width:300px;}
		iframe{width:100%; height:95%;}
		#docLibDiv{
			background:	url(../images/menu_bg1.gif);
			height: 30px;
			border:	0px;
			padding-top : 3px;
			padding-left: 10px;
			margin: 0px;
			color: white;
		}
	</Style>
</HEAD>
<BODY onload="showPermission()">
	<%@include file="../e5include/Error.jsp"%>
	<div id="docLibDiv">
	<i18n:message key="folder.select"/>
	<Select id="selDocLib" onchange="showPermission()">
		<OPTION VALUE="0"><i18n:message key="folder.allFolder"/></OPTION>
		<c:forEach var="docLib" items="${docLibList}">
			<OPTION value="<c:out value="${docLib.docLibID}"/>"><c:out value="${docLib.docLibName}"/></OPTION>
		</c:forEach>
	</SELECT>
	</div>
	<IFRAME Name="FolderPermission" id="FolderPermission" frameborder=0
		style="height:100%;min-height:600px;width:100%;"></Iframe>
</BODY>
</HTML>
