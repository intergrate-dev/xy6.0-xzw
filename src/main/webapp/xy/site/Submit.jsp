<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<HTML>
<HEAD>
	<TITLE>AfterSubmit</TITLE>
	<script type="text/javascript">
		function doClose() {
			var error = "<c:out value="${error}"/>";
			if (error) {
				alert("保存时异常：" + error);
			} else if ("${refresh}" == "true") {
				//域名切换，整窗口刷新
				parent.parent.location.reload();
			} else {
				var dirID = "<c:out value="${dirID}"/>";
				var name = "<c:out value="${dirName}"/>";
				var isNew = "<c:out value="${isNew}"/>";
				
				var tool = parent.parent.dir_tree;
				if (isNew == "true") {
					var parentID = "<c:out value="${parentID}"/>";
					var isNewRoot = "<c:out value="${isNewRoot}"/>";
					var subIDs = "<c:out value="${subIDs}"/>";
					var subNames = "<c:out value="${subNames}"/>";
					
					parent.dir_form.refresh();
					tool.treeAdd(dirID, name, parentID);
					if (isNewRoot == "true"){
						var subIDArr = subIDs.split(",");
						var subNameArr = subNames.split(",");
						for (var x=0; x < subIDs.length; x++) {
							tool.treeAdd(subIDArr[x], subNameArr[x], dirID);
						}
					}
				} else {
					parent.dir_form.refresh();
					tool.treeUpdate(dirID, name);				
					//修改不太容易看出来变化，因此加提示
					alert("操作完成");
				}
			}
		}
	</script>
</HEAD>

<BODY onload="doClose()">
</BODY>
</HTML>
