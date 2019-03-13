<%@include file="../../e5include/IncludeXmlTag.jsp"%><i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/><?xml version="1.0"?>
<tree>
<c:forEach items="${orglist.orgs}" var="org"><tree text="<c:out value="${org.name}" />" src="OrgTreeGenerate.do?invoke=orgNode&amp;orgID=<c:out value="${org.orgID}"/>" nodetype="1" contextAction="popmenu(1, this)"/></c:forEach>
<c:forEach items="${orglist.roles}" var="role"><tree text="<c:out value="${role.roleName}" />"  nodetype="2"  contextAction="popmenu(2, this)" /></c:forEach>
<c:forEach items="${orglist.users}" var="user"><tree text="<c:out value="${user.userName}" />"  nodetype="3"  contextAction="popmenu(3, this)"/></c:forEach>
</tree>