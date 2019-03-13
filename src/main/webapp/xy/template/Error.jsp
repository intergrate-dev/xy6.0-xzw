<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<HTML>
<HEAD>
	<TITLE>AfterProcess</TITLE>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
		function doClose() {
			var callMode = 1;
			var docCount = 1;
			var needRefresh = false;
			debugger;
			var error = "<c:out value="${error}"/>";
			if(error && error==-7){
				alert("您上传的模板格式错误，请重新上传！");
				//parent.location.reload();
				//window.parent.document.getElementById("DIV_t_file")
				$(window.parent.document.getElementById("btnFormSave")).removeAttr("disabled");
				$(window.parent.document.getElementById("btnFormCancel")).removeAttr("disabled");
				$(window.parent.document.getElementsByTagName("input")).remove("#t_file");
				var html = "<input type='file' id='t_file' name='t_file' oldvalue='-'>";
				html = $(window.parent.document.getElementById("DIV_t_file")).html()+html;
				$(window.parent.document.getElementById("DIV_t_file")).html(html);
			}
		}
	</script>
</HEAD>

<BODY onload="doClose()">
</BODY>
</HTML>
