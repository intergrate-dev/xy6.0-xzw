<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="workspace.docmove.title"/></title>
<script type="text/javascript" src="../../e5script/PageLoading.js"></script>
<script language="javascript">

	var isselect = false;
	function folderWindowSelect(folderIDs,folderNames)
	{
		isselect = true;
		document.getElementById("redirect_frame").src="DocMove.do?action=Move&DocIDs=<c:out value="${DocIDs}"/>&UUID=<c:out value="${UUID}"/>&toFolder="+folderIDs+"&DocLibID=<c:out value="${DocLibID}"/>";	
		PageLoading.show();
	}
	function folderWindowHidden()
	{
		if(!isselect)
			document.getElementById("redirect_frame").src="../after.do?UUID=<c:out value="${UUID}"/>";
	}
</script>
<style type="text/css">
<!--
body {
	margin: 0px;
}
-->
</style>
</head>
<body>
<iframe src="" style="display:none" id="redirect_frame"></iframe>
<iframe src="FolderSelect.do?docLibIDs=<c:out value="${DocLibID}"/>&multiple=false" width="100%" height="100%" border="0">
</iframe>
</body>
</html>
<SCRIPT LANGUAGE="JavaScript">
<!--
    //��ΪҪ��ʾ�û�ʧ�ܵ��ĵ�ID,����Ҫ��jsp����ҳ�����ת
	<c:if test="${success!=null}">
	   var success = "<c:out value="${success}"/>";
	   var failed  = "<c:out value="${failed}"/>";
	   if(failed!='')
	   {
		   alert("�����ĵ�����ʧ��:\n"+failed+"\nʧ��ԭ����鿴ϵͳ��־!");
	   }
	   document.getElementById("redirect_frame").src="../e5workspace/after.do?UUID=<c:out value="${UUID}"/>&DocIDs="+success;
	</c:if>
//-->
</SCRIPT>
<%@include file="../../e5include/Error.jsp"%>