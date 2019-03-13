<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false"/>
<html>
<head>
<title><i18n:message key="e5dom.docTypeFieldRestore.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
<script type="text/javascript">
<!--
<c:if test="${restore=='ok'}">
	window.parent.page.operation.showDocTypeFields();
</c:if>
var selectedTr = '';
function selected(src)
{
	if(selectedTr!='')
	{
		document.getElementById(selectedTr).style.backgroundColor='';
	}
	document.getElementById(src).style.backgroundColor="#E4E8EB";
	selectedTr = src;
}
function doSubmit(dealflag)
{
	var ids = selectedTr;
	var selected = false;
	if(selectedTr == ''||selectedTr == undefined ||selectedTr == null)
	{
		if("0"==dealflag){//恢复字段
			alert("<i18n:message key="e5dom.docTypeFieldRestore.alert.selected"/>");
		}
		if("1"==dealflag){//彻底删除字段
			alert("<i18n:message key="e5dom.docTypeFieldRestore.alert.quiteDelete"/>");
		}
		return ;
	}
	document.getElementById("fieldID").value = selectedTr;
	document.getElementById("dealflag").value = dealflag;
	var ok;
	if("0"==dealflag){//恢复字段
		ok = confirm("<i18n:message key="e5dom.docTypeFieldRestore.confirm.restore"/>");
	}
	if("1"==dealflag){//彻底删除字段
		ok = confirm("<i18n:message key="e5dom.docTypeFieldRestore.confirm.quiteDelete"/>");
	}
	if(ok)
	{
		form1.submit();
	}
}
//-->
</script>
</head>
<body>
<form name="form1" method="post" action="DocTypeFieldRestore.do?action=delete">
<input type="hidden" id="fieldID" name="fieldID" value="">
<input type="hidden" name="docTypeID" value="<c:out value="${docTypeID}"/>">
<input type="hidden" id="dealflag" name="dealflag" value="">
	<table cellspacing="0" cellpadding="0" class="table">
		<caption><i18n:message key="e5dom.docTypeFieldRestore.title"/></caption>
	<tr>
		<th width="50"><i18n:message key="e5dom.DocType.FieldID"/></th>
		<th width="100"><i18n:message key="e5dom.DocType.FieldColumnName"/></th>
		<th width="100"><i18n:message key="e5dom.DocType.FieldColumnCode"/></th>
		<th width="100"><i18n:message key="e5dom.DocType.FieldDataType"/></th>
		<th width="200"><i18n:message key="e5dom.DocType.EditType"/></th>
		<th width="50"><i18n:message key="e5dom.DocType.FieldDefaultValue"/></th>
		<th width="50"><i18n:message key="e5dom.DocType.FieldNullable"/></th>
		<th width="60"><i18n:message key="e5dom.DocType.FieldAttr"/></th>
	</tr>
	<c:forEach var="field" items="${list}">
	<tr id="<c:out value="${field.fieldID}"/>" onclick="selected(<c:out value="${field.fieldID}"/>)" style="cursor:hand;text-align:center">     
		<td>
		<c:out value="${field.fieldID}"/>
		</td>
		<td><c:out value="${field.columnName}"/></td>
		<td><c:out value="${field.columnCode}"/></td>
		<td>
		<c:choose>
		   <c:when test="${field.dataLength>0&&field.scale>0}">
		   <c:out value="${field.dataType}"/>(<c:out value="${field.dataLength}"/>,<c:out value="${field.scale}"/>)
		   </c:when>
		   <c:when test="${field.dataLength>0}">
		   <c:out value="${field.dataType}"/>(<c:out value="${field.dataLength}"/>)
		   </c:when>
		   <c:otherwise><c:out value="${field.dataType}"/>
		   </c:otherwise>
		</c:choose>
		</td>
		<!-- 文档类型填写方式，借用DocTypeField中的废弃字段beanName为填写方式的名称。 -->
		<td><c:out value="${field.beanName}"/>&nbsp;</td>
		<td><c:out value="${field.defaultValue}"/>&nbsp;</td>
		<td>
		<c:if test="${field.isNull == 0}">N</c:if>&nbsp;
		</td>
		<td>
			<span style="display:<c:if test="${field.attribute == 2 }">block</c:if><c:if test="${field.attribute != 2 }">none</c:if>"><i18n:message key="e5dom.DocType.CreateField.Attribute.Application"/></span>
			<span style="display:<c:if test="${field.attribute == 3 }">block</c:if><c:if test="${field.attribute != 3 }">none</c:if>"><i18n:message key="e5dom.DocType.CreateField.Attribute.User"/></span>
		</td>
	</tr>
	</c:forEach>
	<tr>
	  <td align="center" colspan="8">
		<input type="button" name="Submit" value="<i18n:message key="e5dom.docTypeFieldRestore.button.restore"/>" onclick="doSubmit(0)" class="button">
		<input type="button" name="Submit" value="<i18n:message key="e5dom.docTypeFieldRestore.button.quiteDelete"/>" onclick="doSubmit(1)" class="button"> 
		<input type="button" name="Submit" value="<i18n:message key="e5dom.docTypeFieldRestore.button.cancel"/>" onclick="window.parent.e5.dialog.close('DeleteFieldsMgrPage');" class="button">
	  </td>      
	</tr> 
  </table>
</form>
<p>&nbsp;</p>
<script>
<c:if test="${delete=='no'}">
	selected(<c:out value="${fieldID}"/>);
	alert("<i18n:message key="e5dom.docTypeFieldRestore.button.noQuiteDelete"/>");
	document.getElementById("fieldID").value = <c:out value="${fieldID}"/>;
	document.getElementById("dealflag").value = <c:out value="${dealflag}"/>;
	document.getElementById("docTypeID").value = <c:out value="${sDocTypeID}"/>;
</c:if>
</script>
</body>
</html>