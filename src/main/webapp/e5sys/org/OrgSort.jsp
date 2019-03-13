<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="org.sort.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="StyleSheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/sys-main-body-style.css"/>
		<link type="text/css" rel="StyleSheet" href="../../e5style/e5sys-org-Query.css"/>
		<style>
			.table td{
				cursor: pointer;
			}
			.table{
				-moz-user-select:none;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap">
		<table cellPadding="0" cellSpacing="0" class="table" id="list">
			<tr class="ui-state-disabled">
				<c:choose>
				<c:when test="${sortmap.treeobj.opType=='sortOrg'}" >
				<th class="titleTD"><i18n:message key="org.sort.org.id"/></th>
				<th><i18n:message key="org.sort.org.code"/></th>
				<th><i18n:message key="org.sort.org.name"/></th>
				</c:when>
				<c:when test="${sortmap.treeobj.opType=='sortRole'}" > 
				<th class="titleTD"><i18n:message key="org.sort.role.id"/></th>
				<th><i18n:message key="org.sort.role.name"/></th>
				</c:when>
				<c:when test="${sortmap.treeobj.opType=='sortUser'}" > 
				<th class="titleTD"><i18n:message key="org.sort.user.id"/></th>
				<th><i18n:message key="org.sort.user.code"/></th>
				<th><i18n:message key="org.sort.user.name"/></th>
				</c:when>
				</c:choose>
			</tr>
			<c:forEach var="item" items="${sortmap.list}">
			<tr id="tr<c:out value="${item.id}"/>" >
				<td class="titleTD"><c:out value="${item.id}"/></td>
				<c:choose>
				<c:when test="${sortmap.treeobj.opType=='sortOrg'}" > 
				<td><c:out value="${item.code}"/>&nbsp;</td>
				</c:when>    
				<c:when test="${sortmap.treeobj.opType=='sortRole'}" > 
				</c:when>
				<c:when test="${sortmap.treeobj.opType=='sortUser'}" > 
				<td><c:out value="${item.code}"/>&nbsp;</td>
				</c:when>
				</c:choose>
				<td><c:out value="${item.name}"/></td>
			</tr>
			</c:forEach>
			<tr  class="ui-state-disabled">
				<td colspan="
				<c:choose>
				<c:when test="${sortmap.treeobj.opType=='sortOrg'}" >3</c:when>    
				<c:when test="${sortmap.treeobj.opType=='sortRole'}" >2</c:when>
				<c:when test="${sortmap.treeobj.opType=='sortUser'}" >3</c:when>
				</c:choose>
				" class="alignCenter">
					<form name="form1" id="form1" action="SortAction.do">
						<input type="hidden" name="SortID" id="SortID">
						<input type="hidden" name="invoke" value="<c:out value="${sortmap.treeobj.opType}"/>"/>
						<input type="hidden" name="treeid" value="<c:out value="${sortmap.treeobj.treeid}"/>"/>
						<input type="button" class="button" name="Submit1" value="<i18n:message key="org.sort.submit"/>" onclick="doSubmit();">
						<input type="button"  class="button" name="Submit" value="<i18n:message key="org.sort.cancel"/>" onclick="location.href='blank.htm'">
					</form>
				</td>
			</tr>
		</table>
		</div>
	</body>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
	<script type="text/javascript">
			$(function(){
				$("#list tbody").sortable({axis: 'y',containment: 'parent', items:'tr:not(.ui-state-disabled)'  });
			});
			
			function doSubmit(){	
				var cats = new Array();
				$("tr:not(.ui-state-disabled)").each(function(){
					cats.push($(this).attr("id").replace("tr",""));
				});
				document.getElementById("SortID").value=cats.join(",");
				document.getElementById("form1").submit();
			}
			//禁止选择文本
			document.onselectstart=function(){return false;};
		</script>
</html>
