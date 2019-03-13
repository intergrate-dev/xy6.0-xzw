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
				window.parent.document.getElementById('btnFormSave').disabled = false;
				window.parent.document.getElementById('btnFormCancel').disabled = false;
			}
		}
	</script>
</HEAD>

<BODY onload="doClose()">
</BODY>
</HTML>
