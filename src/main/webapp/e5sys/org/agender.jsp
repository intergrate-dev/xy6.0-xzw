<%@include file="../../e5include/IncludeTag.jsp"%>
<script>

var addNodeMode="<c:out value="${userinfo.addNodeMode}"/>";//�Ƿ����û�������,�����ڻ��������ӣ��ڸ��½ڵ�ʱ
var treeid = "<c:out value="${userinfo.treeid}"/>";
		if(addNodeMode=="under")
		{
//����ѡ������ڵ�����
			parent.leftFrame.addUserNodeUnderOrg("<c:out value="${userinfo.userName}"/>","<c:out value="${userinfo.userID}"/>","<c:out value="${userinfo.orgID}"/>",treeid);
	
		}
		else if(addNodeMode=="on")
		{
//�����û����ɫͬ���ڵ���
			parent.leftFrame.addUserNodeUnderUser("<c:out value="${userinfo.userName}"/>","<c:out value="${userinfo.userID}"/>","<c:out value="${userinfo.orgID}"/>",treeid);
		}
//�ص����ӵ�form
		var urlsrc="UserMgrAction.do?invoke=UserForm&OpType=add&OrgID=<c:out value="${userinfo.orgID}"/>&treeid=<c:out value="${userinfo.treeid}"/>&UserID=<c:out value="${userinfo.userID}"/>&addNodeMode="+addNodeMode;
		document.location.href=urlsrc;
</script>