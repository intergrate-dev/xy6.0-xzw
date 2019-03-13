<%@include file="../../e5include/IncludeTag.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>栏目模块管理</title>
</head>

<frameset cols="510,*" frameborder="1" border="2" framespacing="0" bordercolor="white">
  <frame src="../../e5workspace/DataMain.do?type=COLMODULE&colID=${param.colID}&rule=cm_columnID_EQ_${param.colID}&siteID=${param.siteID}" name="frameLeft" style="border: 1px solid white;">
  <frame src="about:blank" id="frmModuleRight" name="frmModuleRight" style="border: 1px solid white;">
</frameset>
<noframes><body>frame unsupported!</body></noframes>
</html>
