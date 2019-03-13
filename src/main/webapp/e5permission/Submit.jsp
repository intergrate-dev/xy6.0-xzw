<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

<Script type="text/javascript">
	function show()
	{
		alert("<i18n:message key="operation.success"/>");

		//cache refresh message
		var url = window.location.href;
		var pos = url.indexOf("e5permission");
		if (pos > 0)
			url = url.substring(0, pos) + "xy/system/Refresh.do";
		else {
			pos = url.lastIndexOf("/xy/");
			url = url.substring(0, pos) + "/xy/system/Refresh.do";
		}
		window.location.href = url;
	}
</Script>
<%@include file="../e5include/Error.jsp"%>
</HTML>
