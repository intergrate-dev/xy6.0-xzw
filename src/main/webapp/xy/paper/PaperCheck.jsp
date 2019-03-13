<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>栏目树checkbox选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
</head>
<body style="margin-bottom:0">
	<%@include file="Tree.inc"%>
	<script>
		
		//设置栏目树需要的参数
		col_tree.check.ids = "<c:out value="${param.ids}"/>";
		col_tree.siteID = "<c:out value="${param.siteID}"/>";
		if (!col_tree.siteID) col_tree.siteID = 1;
		col_tree.check.enable = true;

		var type = "<c:out value="${param.type}"/>";
		 if (type == "radio") {
			col_tree.check.chkStyle = "radio";
		} 
		
		function getChecks() {
				parent.columnClose(col_tree.getChecks());
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				//var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				//alert(hint);
			}
		}
		
		//提供一个接口供父级窗口调用
		function  getAllFilterChecked(){
			return col_tree.getChecks();
		}
		
		//按钮
		$("#divColBtn").show();
		$("#btnColCancel").click(doCancel);
		$("#btnColOK").click(getChecks);
		
		
	</script>
	<script type="text/javascript">
		$(function(){
			//$(parent.window).height() + "px"
			//document.getElementById("rs_tree").style.height = 500;
			$("#rs_tree", window.parent.document).css("height",500);
		});
	</script>
</body>
</html>