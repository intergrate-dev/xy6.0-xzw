<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>模板树</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<body>
	<%@include file="TemplateTree.inc"%>
	<script>
		//设置模板树需要的站点参数
		tpl_tree.siteID = "<c:out value="${param.siteID}"/>";
		tpl_tree.ckeckedID = "<c:out value="${param.ckeckedID}"/>";
		tpl_tree.channel = "<c:out value="${param.channel}"/>";
		if (!tpl_tree.siteID) tpl_tree.siteID = 1;
		tpl_tree.type = "<c:out value="${param.type}"/>";
		
	
		$(function(){
			//点击保存按钮
			$('#doSave').click(function(){
				var id = "";
				var name = "";
				try {
					var checked = tpl_tree.getCheck();
					if(checked == "group"){
						alert("对不起，您选择的是模板组名，请选择模板名！");
					}else if(checked == "null"){
						alert("对不起，您未选中任何模板");
					}else{
						id = checked[0];
						name = checked[1];
						parent.column_form.closeTemplate(tpl_tree.channel,tpl_tree.type, name, id);
					}
				} catch (e) {
					var hint = "模板树应实现选择方法供栏目扩展属性选用。"
						+ "\n   每个参数的格式是: [id, name]"
					alert(hint);
				}

			});
			$("#rs_tree").dblclick(function(){
				var id = "";
				var name = "";
				try {
					var checked = tpl_tree.getCheck();
					if(checked == "group"){
						alert("对不起，您选择的是模板组名，请选择模板名！");
					}else if(checked == "null"){
						alert("对不起，您未选中任何模板");
					}else{
						id = checked[0];
						name = checked[1];
						parent.column_form.closeTemplate(tpl_tree.channel,tpl_tree.type, name, id);
					}
				} catch (e) {
					var hint = "模板树应实现选择方法供栏目扩展属性选用。"
						+ "\n   每个参数的格式是: [id, name]"
					alert(hint);
				}

			});
			//点击取消按钮
			$('#doCancel').click(function(){
				parent.column_form.cancelTemplate(tpl_tree.type);
			});
		});
	</script>
</body>
</html>