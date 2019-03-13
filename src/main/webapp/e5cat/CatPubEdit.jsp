<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<title><i18n:message key="catPub.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script language="javascript">
var success = "<c:out value="${success}"/>";
if(success == 'ok')
{
	alert('<i18n:message key="cat.set.alert"/>');
	location.href='blank.htm';
}

function checkInput()
{
  	var f1 = document.forms[0];  
	var errors = "";
	
	if(f1.pubLevel.value!=null)
	{
		if(!isInt(f1.pubLevel.value))
		{
			errors = errors + "<i18n:message key="error.cat.pubLevel.integer"/>\n";			
		}
	 }
	if(getLength(f1.linkTable.value)>80)
	{
		 errors = errors +  "<i18n:message key="error.cat.linkTable.maxlength"/>\n";			
	}
	if(errors!='')
	{
		alert("<i18n:message key="errors.header"/>\n"+errors);
		return;
	}
	f1.submit();
	document.body.style.cursor = "wait";
	f1.sure.disabled=true;
	f1.cancel.disabled=true;
}
</script>
</head>

<body>
<%@include file="ErrorSelf.jsp"%>
<form name="form1" method="post" action="CatPubEdit.do?action=save">
<input type="hidden" name="catType" value="<c:out value="${item.catType}"/>">
<input type="hidden" name="catID" value="<c:out value="${catID}"/>">
<input type="hidden" name="parentID" value="<c:out value="${item.parentID}"/>">
<input type="hidden" name="treeID" value="<c:out value="${item.treeID}"/>">
<input type="hidden" name="refCatAdd" value="<c:out value="${item.refCatAdd}"/>">
<input type="hidden" name="psRefCat" value="<c:out value="${typeForm.psRefCat}"/>"/>
<div class="mainBodyWrap">
<table border="0" cellpadding="0" cellspacing="0" class="table">
	<caption><i18n:message key="catPub.title"/></caption>
	<tr>
		<th class="w90"><i18n:message key="cat.view.catID"/></th>
		<td colspan="2">
	 		<c:out value="${catID}"/>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.catName"/></th>
		<td colspan="2">
			<c:out value="${item.catName}"/>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.published"/></th>                  
		<td>
			<input style="border:none"  name="published" type="checkbox" id="published" value="true" 
			<c:if test="${item.published}"> checked</c:if> <c:if test="${!typeForm.psPublishState}">disabled</c:if>>
			<label for="published"><i18n:message key="cat.edit.published"/></label>
		</td>
		<td>
			<input style="border:none" id="publishedSubExtends" type="checkbox" checked name="publishedSubExtends" value="true" <c:if test="${!typeForm.ssPublished}">disabled</c:if>>
			<label for="publishedSubExtends"><i18n:message key="cat.edit.subExtends"/></label>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.pubLevel"/></th>
		<td>
			<input name="pubLevel" type="text" id="pubLevel"  value="<c:out value="${item.pubLevel}"/>" <c:if test="${!typeForm.psCatLevel}">disabled</c:if>>
		</td>
		<td>			
			<input style="border:none"  id="pubLevelSubExtends" type="checkbox" name="pubLevelSubExtends" checked value="true" <c:if test="${!typeForm.ssCatLevel}">disabled</c:if>>
			<label for="pubLevelSubExtends"><i18n:message key="cat.edit.subExtends"/></label>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.linkTable"/></th>
		<td><input name="linkTable" type="text" id="linkTable" value="<c:out value="${item.linkTable}"/>" <c:if test="${!typeForm.psLinkTable}">disabled</c:if>>
		</td>
		<td>
			<input style="border:none" id="linkTableSubExtends" type="checkbox" name="linkTableSubExtends" value="true" <c:if test="${!typeForm.ssRefTable}">disabled</c:if> checked>
			<label for="linkTableSubExtends"><i18n:message key="cat.edit.subExtends"/></label>			
		</td>
	</tr>
	<tr align="center">
		<td colspan="3">
			<input type="button" name="sure" value="<i18n:message key="cat.button.submit"/>" onclick="checkInput()" class="button">&nbsp;
			<input type="button" name="cancel" value="<i18n:message key="cat.button.cancel"/>"  onclick="location.href='blank.htm'" class="button">
		</td>
	</tr>
</table>
</div>
</form>
</body> 
</html>
