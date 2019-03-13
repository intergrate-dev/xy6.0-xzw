<%@ include file="../e5include/IncludeTag.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script language="javascript">
/*
 * 该文件只用于动态更新js树，不能添加流转到等功能
 * wanghc
 */
  //分类类型添加刷新功能
  <c:if test="${functionName=='catType'}">
	  <c:if test="${isNew == 'true'}">
			parent.leftFrame.addCatTypeToTree('<c:out value="${type.catType}"/>','<c:out value="${type.name}"/>');
	  </c:if>
	  <c:if test="${isNew == 'false'}">			
	        parent.leftFrame.location.reload();
	  </c:if>
  </c:if>
  //分类添加刷新功能
  <c:if test="${functionName=='cat'}">
	<c:if test="${isNew == 'true'}">
			parent.leftFrame.addCatToTree("<c:out value="${cat.catType}"/>","<c:out value="${cat.catID}"/>","<c:out value="${cat.catName}"/>","<c:out value="${cat.catCode}"/>",'<c:out value="${cat.treeID}"/>','<c:out value="${cat.refCatAdd}"/>','<c:out value="${cat.refType}"/>','<c:out value="${cat.refID}"/>');
			location.href="CatEdit.do?action=new&catType=<c:out value="${cat.catType}"/>&treeID=<c:out value="${cat.treeID}"/>"+"&catName="+encodeURI("<c:out value="${cat.catName}"/>")+"&parentID=<c:out value="${cat.parentID}"/>";
	</c:if> 
    <c:if test="${isNew == 'false'}">
			parent.leftFrame.modifyCatToTree('<c:out value="${cat.treeID}"/>',"<c:out value="${cat.catName}"/>","<c:out value="${cat.catCode}"/>");
	</c:if> 
  </c:if>
  <c:if test="${functionName=='catSort'}">
			parent.leftFrame.catReload('<c:out value="${treeID}"/>');
  </c:if>
  //分类恢复
  <c:if test="${functionName=='catRestore'}">
			parent.leftFrame.location.reload();
  </c:if>
	//分类导入
	<c:if test="${functionName=='catImport'}">
			window.opener.importReload();
			window.close();
	</c:if>
  <c:if test="${functionName==null}">
		if (parent.doReload) {
			parent.doReload();
		}
  </c:if>
  </script>
</head>
<body>
</body>
</html>
