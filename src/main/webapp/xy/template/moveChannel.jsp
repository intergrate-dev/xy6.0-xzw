<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>移动分组</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<body>
	<%@include file="channelTree.inc"%>
	<script>
		//设置模板树需要的站点参数
		tpl_tree.siteID = "<c:out value="${param.siteID}"/>";
		tpl_tree.ckeckedID = "<c:out value="${param.ckeckedID}"/>";
		tpl_tree.DocID = "<c:out value="${param.DocIDs}"/>";
		tpl_tree.UUID = "<c:out value="${param.UUID}"/>";
		
	
		$(function(){
			//点击保存按钮
			$('#doSave').click(function(){
				var id = "";
				var name = "";
				try {
					var checked = tpl_tree.getCheck();
					if(checked == "null"){
						alert("对不起，您未选中任何分组");
					}else{
						id = checked[0];
						name = checked[1];
						//~~~~~~~~~此处需调整为点击保存后获取模板ID和分组id，进行调整~~~~~~~~~~
						var url = "../../xy/template/tmlSave.do?id="+id+"&name="+name+"&DocID="+tpl_tree.DocID+"&UUID="+tpl_tree.UUID;
						window.location.href=(url);
					}
				} catch (e) {
					var hint = "模板树应实现选择方法供栏目扩展属性选用。"
						+ "\n   每个参数的格式是: [id, name]"
					alert(hint);
				}

			});
			//点击取消按钮
			$('#doCancel').click(function(){
				columnCancel();
			});
			
			//操作成功了调用
			function operationClsoe(){
				var url = "../../e5workspace/after.do?UUID=" + tpl_tree.UUID;
				window.location.href=(url);
			}
			
			//点击取消按钮时
			function columnCancel() {
				operationClsoe();
			}
		});
	</script>
</body>
</html>