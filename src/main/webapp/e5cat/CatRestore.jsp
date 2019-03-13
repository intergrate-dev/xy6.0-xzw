<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="catRestore.title"/></title>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
</head>
<SCRIPT LANGUAGE="JavaScript">
<!--
<c:if test="${restore=='ok'}">
   parent.leftFrame.location.reload();
</c:if>
var selectedTr = '';
var selectedParent = '';
function selected(src)
{	
	if(selectedTr!='')
    {
		//alert(selectedTr);
		//alert(document.getElementsById(selectedTr));
    	document.getElementById(selectedTr).style.backgroundColor='';
	}
	src.style.backgroundColor="#E4E8EB";
	selectedTr = src.getAttribute("id");
	selectedParent = src.getAttribute("parentID");
}
function doSubmit()
{
	var ids = selectedTr;
	var selected = false;
	//alert(selectedTr);
	if(selectedTr == '')
	{
		alert("<i18n:message key="catRestore.alert.selected"/>");
		return ;
	}
	else if(selectedParent>0)
	{
	    var checkUrl = "CatCheck.do?catID="+selectedParent+"&catType="+form1.catType.value;
		
		var xmlHttp = XmlHttp.create();
		//检查ID是否存在
		xmlHttp.open("GET", checkUrl, false);	// async
		xmlHttp.send(null);
		var result = xmlHttp.responseText;
		if(result=="false")
		{
			alert("<i18n:message key="errors.cat.restore.parentstate"/>");
			return;
		}	
	}
	document.getElementById("id").value = selectedTr;
	//alert(document.getElementById("id").value);
	var ok = confirm("<i18n:message key="catRestore.confirm.restore"/>");
	if(ok)
	{
		form1.submit();
	}
}
//-->
</SCRIPT>
<body>
<%@include file="ErrorSelf.jsp"%>
<form name="form1" method="post" action="CatRestore.do?action=delete">
<input type="hidden" id="id" name="id" value="">
<input type="hidden" name="catType" value="<c:out value="${catType}"/>">
  <table border="0" cellspacing="0" cellpadding="2" class="table">
  	<caption><i18n:message key="catRestore.title"/></caption>
  <tr>
    <td align="center" colspan="4">
    	<i18n:message key="cat.note"/>
    	<i18n:message key="catRestore.message"/>
    </td>   
  </tr>
  	<tr>
      <td align="center" colspan="4">
      	<input type="button" name="Submit" value="<i18n:message key="cat.button.restore"/>" onclick="doSubmit()" class="button">
   		<input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button">
      </td>      
    </tr>    
    <tr class="blacktd">
      <th width="102"><i18n:message key="cat.view.catID"/></th>
      <th width="119"><i18n:message key="cat.edit.catName"/></th>
      <th width="104"><i18n:message key="cat.edit.catCode"/></th>
      <th width="168"><i18n:message key="catRestore.cascadeName"/></th>
    </tr>
	<c:forEach var="item" items="${list}">
    <tr  id="<c:out value="${item.catID}"/>" parentID="<c:out value="${item.parentID}"/>" onclick="selected(this)" style="cursor:hand">      
      <td><c:out value="${item.catID}"/></td>
      <td><c:out value="${item.catName}"/></td>
      <td><c:out value="${item.catCode}"/>&nbsp;</td>
      <td><c:out value="${item.cascadeName}"/>&nbsp;</td>
    </tr>
	</c:forEach>
  	<tr>
      <td align="center" colspan="4">
      	<input type="button" name="Submit" value="<i18n:message key="cat.button.restore"/>" onclick="doSubmit()" class="button">
   		<input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button">
      </td>      
    </tr> 
  </table>
</form>
<p>&nbsp;</p>
</body>
</html>
