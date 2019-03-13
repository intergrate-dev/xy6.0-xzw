<%@include file="../../e5include/IncludeTag.jsp"%>
<script>
var treeid = "<c:out value="${treeobj.treeid}"/>";
 parent.leftFrame.reLoadChildTree(treeid);
</script>