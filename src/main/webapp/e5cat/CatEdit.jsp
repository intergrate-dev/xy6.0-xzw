<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<%
/**
  *
  * 页面逻辑：
  *  1.编辑分类:
  * 	1)不显示引用分类的设置
  * 	2)显示所有同步设置
  *  2.编辑引用分类
  * 	1)显示引用分类的设置
  * 	2）不显示同步设置
  */
%>
<html>
	<head>
		<title><i18n:message key="cat.edit.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
		<script type="text/javascript">
			var catKey = "<%=com.founder.e5.cat.Category.separator%>";
			function checkInput(){
			  var f1 = document.forms[0];
				var errors = "";

			  //catName
			  if(f1.catName.value == '')
			  {
					errors = errors + '<i18n:message key="error.cat.catName.required"/>\n';
			  }
			  else if(getLength(f1.catName.value)>120)
			  {
					errors = errors + "<i18n:message key="error.cat.catName.maxlength"/>\n";
			  }
			 // else if(f1.catName.value.indexOf(catKey)>-1)
			 /// {
					//errors = errors + "<i18n:message key="cat.edit.catName"/> <i18n:message key="error.cat.catKey.disabled"/> "+catKey+"\n";
			 // }

			//catCode
				if(getLength(f1.catCode.value)>1024)
			   {
						errors = errors + "<i18n:message key="error.cat.catCode.maxlength"/>\n";
			   }

			  if(f1.refCatAdd.value=='true')
			  {
					if(f1.refType.value=='')
					{
						errors = errors + '<i18n:message key="error.cat.refType.required"/>\n';
					}
					if(f1.refID.value=='')
					{
						errors = errors + '<i18n:message key="error.cat.refID.required"/>\n';
					}
					//refID
					if(f1.refID.value!=null)
				  {
						if(!isInt(f1.refID.value))
						{
							errors = errors + "<i18n:message key="error.cat.refID.integer"/>\n";
						}
						else if(f1.refID.value><%=Integer.MAX_VALUE%>)
						{
							errors = errors + "<i18n:message key="error.cat.refID.maxlength"/>\n";
						}
						else if(f1.refID.value>0 && f1.refType.value!='')
						{
							var checkUrl = "CatCheck.do?catID="+f1.refID.value+"&catType="+f1.refType.value;

							var xmlHttp = XmlHttp.create();
							//检查ID是否存在
							xmlHttp.open("GET", checkUrl, false);	// async
							xmlHttp.send(null);
							var result = xmlHttp.responseText;
							if(result=="false")
							{
								errors = errors + "<i18n:message key="error.cat.refID.existed"/>\n";
							}
						}
				  }
			  }
				//extend attrrbiute validate
			<c:forEach var="extType" items="${catExts}">
			  temp    = f1.<c:out value="ext_${extType.extType}"/>;
			  tempname="<c:out value="${extType.extTypeName}"/>";
			  if(temp.value!=null)
			  {
				 if(getLength(temp.value)>120)
				 {
					errors = errors + tempname + " <i18n:message key="error.cat.extendField.maxlength"/>\n";
				 }
				 // else if(temp.value.indexOf(catKey)>-1)
				 // {
				//		//errors = errors + tempname + "<i18n:message key="error.cat.catKey.disabled"/> "+catKey+"\n";
				 // }
			  }
			</c:forEach>


			//memeo
			if(getLength(f1.memo.value)>1024){
					 errors = errors +  "<i18n:message key="error.cat.memo.maxlength"/>\n";
			  }
				if(errors!="")
			  {
					alert("<i18n:message key="errors.header"/>\n"+errors);
					return false;
			   }
			  return true;
			}
			function checkRef()
			{
				var f1 = document.forms[0];
			//检查是否是新建引用分类，是先检查能否用引用分类
			  if(f1.refCatAdd.value=='true')
			  {
				if(f1.psRefCat.value == 'false')
				{
					alert('<i18n:message key="error.cat.refType.disabled"/>');
					location.href='blank.htm';
				}
			  }
			  else
			  {
				  document.getElementById("catName").focus();
				  document.getElementById("catName").select();
			  }
			}
			function show(){}
			function refreshChild(event){
				event = event || window.event;
				if(event.keyCode==116){
					event.keyCode=0;
					event.returnValue=false; 
					document.location.reload();
				}
			}
			document.onkeydown = refreshChild;
		</script>
	</head>
	<body onload="checkRef()">
<%@include file="ErrorSelf.jsp"%>
<form name="form1" method="post" action="CatEdit.do?action=save" onSubmit="return checkInput();">
	<input type="hidden" name="catType" value="<c:out value="${item.catType}"/>">
	<input type="hidden" name="catID" value="<c:out value="${catID}"/>">
	<input type="hidden" name="parentID" value="<c:out value="${item.parentID}"/>">
	<input type="hidden" name="treeID" value="<c:out value="${item.treeID}"/>">
	<input type="hidden" name="refCatAdd" value="<c:out value="${item.refCatAdd}"/>">
	<input type="hidden" name="psRefCat" value="<c:out value="${typeForm.psRefCat}"/>"/>
	<input type="hidden" name="published" value="<c:out value="${item.published}"/>"/>
	<input type="hidden" name="pubLevel" value="<c:out value="${item.pubLevel}"/>"/>
	<input type="hidden" name="linkTable" value="<c:out value="${item.linkTable}"/>"/>
	<input type="hidden" name="linkType" value="<c:out value="${item.linkType}"/>"/>
	<input type="hidden" name="linkID" value="<c:out value="${item.linkID}"/>"/>
	<input type="hidden" name="displayOrder" value="<c:out value="${item.displayOrder}"/>"/>
	<div class="mainBodyWrap">
		<table cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="cat.edit.title"/></caption>
			<c:if test="${item.catID > 0}">
			<tr>
				<th class="w90"><i18n:message key="cat.view.catID"/></th>
				<td><c:out value="${item.catID}"/></td>
			</tr>
			</c:if>
			<tr>
				<th class="w90"><span class="field-required">*</span><i18n:message key="cat.edit.catName"/></th>
				<td>
				<input name="catName" id="catName" type="text"  value="<c:out value="${item.catName}"/>" style="width:250">
				</td>
			</tr>
			<tr>
				<th><i18n:message key="cat.edit.catCode"/></th>
				<td><input name="catCode" type="text" value="<c:out value="${item.catCode}"/>" style="width:250"></td>
			</tr>
			<c:if test="${item.refCatAdd}">
			<tr>
				<th><i18n:message key="cat.edit.refCatType"/></th>
				<td>
					<select name="refType" <c:if test="${!typeForm.psRefCat}">disabled</c:if> style="width:250">
						<option value=""><i18n:message key="cat.edit.selected.defaultValue"/></option>
						<c:forEach var="catType" items="${catTypes}">
						<option value="<c:out value="${catType.catType}"/>" <c:if test="${catType.catType==item.refType}"> selected</c:if>><c:out value="${catType.name}"/></option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<th><i18n:message key="cat.edit.refID"/></th>
				<td><input name="refID" style="width:250" type="text" id="refID" size="5" value="<c:out value="${item.refID}"/>" <c:if test="${!typeForm.psRefCat}">disabled</c:if>></td>
			</tr>
			</c:if>
			<c:forEach var="extType" items="${catExts}">
				<tr>
					<th><c:out value="${extType.extTypeName}"/></th>
					<td><input type="text" id="ext" style="width:250" name="<c:out value="ext_${extType.extType}"/>" value="<c:out value="${extType.extName}"/>"></td>
				</tr>
			</c:forEach>
			<c:if test="${catID>0}">
			<tr>
				<th><i18n:message key="cat.edit.userName"/></th>
				<td><c:out value="${item.userName}"/></td>
			</tr>
			<tr>
				<th><i18n:message key="cat.edit.lastModified"/></th>
				<td><c:out value="${item.lastModified}"/><input type="hidden" name="lastModified" value="<c:out value="${item.lastModified}"/>"/></td>
			</tr>
			</c:if>
			<tr>
				<th><i18n:message key="cat.edit.memo"/></th>
				<td><textarea name="memo" rows="6" cols="50"><c:out value="${item.memo}"/></textarea></td>
			</tr>
			<tr align="center">
				<td colspan="2"><input type="submit" name="Submit" value="<i18n:message key="cat.button.submit"/>" class="button">&nbsp;
				<input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button"></td>
			</tr>
		</table>
	</div>
</form>
</body>
</html>
