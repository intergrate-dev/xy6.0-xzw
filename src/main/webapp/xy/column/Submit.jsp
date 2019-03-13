<%@include file="../../e5include/IncludeTag.jsp"%>
<HTML>
<HEAD>
	<TITLE>AfterSubmit</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript">
		function doClose() {
			var error = "<c:out value="${error}"/>";
			if (error) {
				alert("保存时异常：" + error);
			} else {
				var colID = "<c:out value="${colID}"/>";
				var name = "<c:out value="${colName}"/>";
				var parentID = "<c:out value="${parentID}"/>";
				var isNew = "<c:out value="${isNew}"/>";
				var isBat = "<c:out value="${isBat}"/>";
				var needLocation = "<c:out value="${needLocation}"/>";
				if(isBat == "true"){
					var tool = parent.parent.frames["frmColLeft"].col_tree;
					var node = tool.tree.getNodeByParam("id", parentID, null);
					tool.treeAdd(colID, name, parentID);
					tool.tree.reAsyncChildNodes(node, "refresh");
					parent.column_form.newForm();
				}
				if (isNew == "true"&& isBat!="true") {
					var tool = parent.parent.frames["frmColLeft"].col_tree;
					tool.treeAdd(colID, name, parentID);
                    var node = tool.tree.getNodeByParam("id", colID, null);
                    if(needLocation == "true"){
                        tool.tree.selectNode(node);
                        tool._nodeClick(node);
                    }
                    else {
                        parent.column_form.newForm();
                    }

				} else {
					if (name) {
						var tool = parent.parent.parent.frames["frmColLeft"].col_tree;
						tool.treeUpdate(colID, name);
					}
					parent.column_form.refresh();
					//修改不太容易看出来变化，因此加提示
					alert("操作完成。");
				}
			}
		}
	</script>
</HEAD>

<BODY onload="doClose()">
</BODY>
</HTML>
