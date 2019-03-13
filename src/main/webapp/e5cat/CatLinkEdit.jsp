<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="catLink.title"/></title>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
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
	if(f1.linkType.value=='')
	{
		errors = errors + "<i18n:message key="error.cat.linkType.required"/>\n";
	}
	if(f1.linkID.value!=null)
	{
		if(!isInt(f1.linkID.value))
		{
			errors = errors + "<i18n:message key="error.cat.linkID.integer"/>\n";			
		}
		else if(f1.linkID.value><%=Integer.MAX_VALUE%>)
		{
			errors = errors + "<i18n:message key="error.cat.linkID.maxlength"/>\n";
		}
		else if(f1.linkID.value>0 && f1.linkType.value!='')
		{
			var checkUrl = "CatCheck.do?catID="+f1.linkID.value+"&catType="+f1.linkType.value;
			
			var xmlHttp = XmlHttp.create();
			//检查ID是否存在
			xmlHttp.open("GET", checkUrl, false);	// async
			xmlHttp.send(null);
			var result = xmlHttp.responseText;
			if(result=="false")
			{
				errors = errors + "<i18n:message key="error.cat.linkID.existed"/>\n";
			}			
		}
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
function show()
{
}
</script>
</head>

<body>
<%@include file="ErrorSelf.jsp"%>
<form name="form1" method="post" action="CatLinkEdit.do?action=save" onSubmit="return checkInput();">
<input type="hidden" name="catType" value="<c:out value="${item.catType}"/>">
<input type="hidden" name="catID" value="<c:out value="${catID}"/>">
<input type="hidden" name="parentID" value="<c:out value="${item.parentID}"/>">
<input type="hidden" name="treeID" value="<c:out value="${item.treeID}"/>">
<input type="hidden" name="refCatAdd" value="<c:out value="${item.refCatAdd}"/>">
<input type="hidden" name="psRefCat" value="<c:out value="${typeForm.psRefCat}"/>"/>
<table border="0" cellpadding="2" cellspacing="0" class="table">
	<caption><i18n:message key="catLink.title"/></caption>
	<tr>
		<th width="80"><i18n:message key="cat.view.catID"/></th>
		<td width="320">
			<c:out value="${catID}"/>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.catName"/></th>
		<td>
			<c:out value="${item.catName}"/>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.linkType"/></th>
		<td>						
			<select name="linkType" <c:if test="${!typeForm.psCorrCat}">disabled</c:if> style="width:150px">
				<option value=""><i18n:message key="cat.edit.selected.defaultValue"/></option>
				<c:forEach var="catType" items="${catTypes}">
				<option value="<c:out value="${catType.catType}"/>" <c:if test="${catType.catType==item.linkType}"> selected</c:if>><c:out value="${catType.name}"/></option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.linkID"/></th>
		<td><input name="linkID" type="text" size="5" id="linkID" value="<c:out value="${item.linkID}"/>" style="width:150px"></td>
	</tr>
	<tr>
		<th><i18n:message key="cat.edit.subExtends"/></th>
		<td><input style="border:none"  id="linkTypeExtends" type="checkbox" checked name="linkTypeExtends" value="true" <c:if test="${!typeForm.ssCorrCat}">disabled</c:if>></td>
	</tr>            
	<tr align="center">
		<td colspan="2">
			<input type="button" name="sure" value="<i18n:message key="cat.button.submit"/>" onclick="checkInput();" class="button">&nbsp;
			<input type="button" name="cancel" value="<i18n:message key="cat.button.cancel"/>"  onclick="location.href='blank.htm'" class="button">
		</td>
	</tr>
</table>
</form>
</body> 
</html>