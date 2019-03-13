<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<style>
		fieldset{border:1px solid gray;margin:20px;}
		div{padding:20px;}
		select{width:300px;}
	</style>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
		function doInit() {
			var theURL = "FieldGroup.do?action=types";
			$.ajax({url: theURL, async:false, dataType:'json', success: function(datas) {
				var list = datas.unusedTypes;
				_setOptions(list);
			}});
		}
		function _setOptions(datas) {
			var sel = document.getElementById("docTypes");
			while (sel.options.length > 0)
				sel.options.remove(0);

			for (var i = 0; i < datas.length; i++) {
				var op = document.createElement("OPTION");
				op.value = datas[i].key;
				op.text = datas[i].value;
				
				sel.options.add(op);
			}
		}
		function startDocType() {
			var docTypeID = $("#docTypes").val();
			if (!docTypeID) return;
			
			var theURL = "FieldGroup.do?action=start&docType=" + docTypeID;
			$.ajax({url: theURL, async:false, success: function(datas) {
				window.parent.location.reload();
			}});
		}
	</script>
</HEAD>
<BODY onload="doInit()">
	<fieldset>
		<legend><i18n:message key="fieldgroup.description"/></legend>
		<div>
			<i18n:message key="fieldgroup.description.memo"/>
		</div>
	</fieldset>
	<fieldset>
		<legend><i18n:message key="fieldgroup.start"/></legend>
		<div>
			<select id="docTypes"></select>
			<input type="button" onclick="startDocType()" value="<i18n:message key="fieldgroup.button.ok"/>"/>
		</div>
</BODY>
</HTML>
