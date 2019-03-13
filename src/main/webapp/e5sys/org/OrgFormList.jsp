<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head><title>Add Org</title></head> 
<link 	type="text/css" rel="StyleSheet" href="../../e5style/style.css"/>
<body>
        <i18n:message key="org.name"/><c:out value="${orginfo.name}"/><br> 
        <i18n:message key="org.code"/><c:out value="${orginfo.code}"/><br> 
        <i18n:message key="org.type"/><c:out value="${orginfo.typeName}"/>
        <br>

    <script>
	<c:if test="${orginfo.treeid!='-1'}">

		var operate="<c:out value="${orginfo.operate}"/>";
		if(operate=="update")
		{
			parent.leftFrame.updateOrgNode("<c:out value="${orginfo.name}"/>","<c:out value="${orginfo.treeid}"/>");
		}
	else if(operate=="add")
		{
			parent.leftFrame.addOrgNode("<c:out value="${orginfo.name}"/>","<c:out value="${orginfo.orgID}"/>","<c:out value="${orginfo.treeid}"/>");
  		}
	</c:if>
	<c:if test="${orginfo.treeid=='-1'}">
		alert("<i18n:message key="org.query.org.refresh.alert"/>");
		window.close();
	</c:if>
    </script>   
</body> 
</html>