<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html> 
<head><title>Add Org</title></head> 
<body> 
<i18n:message key="org.name"/><font color="red"><c:out value="${orginfo.name}"/></font>
    <form name="loginform" action="OrgMgrAction.do" method="post"> 
        <i18n:message key="org.name"/><input type="text" name="OrgName" value="<c:out value="${orginfo.name}"/>"/><br> 
        <i18n:message key="org.type"/><input type="text" name="OrgTypeID" value="<c:out value="${orginfo.type}"/>"/><br>
        <input type="hidden" name="SelOrgID" value="<c:out value="${orginfo.orgID}"/>"> 
        <input type="hidden" name="invoke" value="updateOrg"> 
<!--here user property1 to set treeid,only by wangchaoyang user-->        
        <input type="hidden" name="treeid" value="<c:out value="${orginfo.treeid}"/>">
        <input type="submit" value="<i18n:message key="org.submit"/>"/> 
    </form> 
</body> 
</html>