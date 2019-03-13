<%@include file="../../e5include/IncludeTag.jsp"%>
<script>

var addNodeMode="<c:out value="${userinfo.addNodeMode}"/>";//是否在用户下增加,还是在机构下增加，在更新节点时
var treeid = "<c:out value="${userinfo.treeid}"/>";
		if(addNodeMode=="under")
		{
//加在选择机构节点下面
			parent.leftFrame.addUserNodeUnderOrg("<c:out value="${userinfo.userName}"/>","<c:out value="${userinfo.userID}"/>","<c:out value="${userinfo.orgID}"/>",treeid);
	
		}
		else if(addNodeMode=="on")
		{
//加在用户或角色同级节点上
			parent.leftFrame.addUserNodeUnderUser("<c:out value="${userinfo.userName}"/>","<c:out value="${userinfo.userID}"/>","<c:out value="${userinfo.orgID}"/>",treeid);
		}
//回到增加的form
		var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=add&OrgID=<c:out value="${userinfo.orgID}"/>&treeid=<c:out value="${userinfo.treeid}"/>&UserID=<c:out value="${userinfo.userID}"/>&addNodeMode="+addNodeMode;
		document.location.href=urlsrc;
</script>