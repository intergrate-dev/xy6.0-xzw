<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>域名目录选择</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<body>
	<%@include file="Tree.inc"%>
	<script>	
		//设置域名目录需要的参数
		dir_tree.siteID = "<c:out value="${param.siteID}"/>";
		if (!dir_tree.siteID) dir_tree.siteID = 1;	
		var inputid = "<c:out value="${param.inputid}"/>";
		
		dir_tree.dirClick0 = function(event, treeId, treeNode, clickFlag) {
			var id = "";
			var url = "";
			try {
				var checked = dir_tree.getCheck();
				id = checked[0];
				url = checked[1];
			} catch (e) {
			}
			
			parent.ruleSelect_form.inputid.value= url;
			parent.ruleSelect_form.dirID.value= id;
			
			parent.ruleSelect_form.dialog.close();
		};	
	</script>
</body>
</html>