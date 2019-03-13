<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title>Icon List</title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript">
			function editIcon(id){
				form1.fileName.value = form1.fileNames[id].value;
				form1.description.value = form1.descriptions[id].value;
				form1.url.value = form1.urls[id].value;
				form1.format.value = form1.formats[id].value;
				form1.size.value = form1.sizes[id].value;
				form1.iconID.value = form1.iconIDs[id].value;
				var errMsg='';
				if(form1.fileName.value==null||form1.fileName.value=="")
					errMsg += errMsg+'[fileName]';
				if(form1.format.value==null||form1.format.value=="")
					errMsg += errMsg+'[format]';
				if(form1.size.value==null||form1.size.value=="")
					errMsg += errMsg+'[size]';
				if (!errMsg)
				{
					form1.action="IconSubmit.do";
					form1.submit();
				}
				else{
					alert(errMsg+'<i18n:message key="operation.common.script"/>');
					return false;
				}
			}
			function deleteIcon(id){
				if (!window.confirm('<i18n:message key="operation.common.delconfirm"/>'))
					return false;
				form1.iconID.value=id;
				form1.action="IconSubmit.do?del=1";
				form1.submit();
			}
			function addIcon(){
				window.location.href = 'Icon.jsp';
			}
		</script>
		<style>
			input.short{width:50px}
			input.long{width:250px}
			.description{
				width:270px;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap">
			<form name="form1" method="post" action="">
			<table cellpadding ="0" cellspacing="0" class="table">
				<caption><i18n:message key="operation.listIcon.contentTitle"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button class="button" onclick="addIcon();return false;"><i18n:message key="operation.addIcon.contentTitle"/></button></caption>
				<tr>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					<th><i18n:message key="operation.icon.iconID"/></th>
					<th><i18n:message key="operation.icon.fileName"/></th>
					<th style="display:none"><i18n:message key="operation.icon.url"/></th>
					<th class=message key="operation.icon.format"/></th>
					<th><i18n:message key="operation.icon.size"/></th>
					<th class="description"><i18n:message key="operation.icon.description"/></th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
				</tr>
				<tr style="display:none">
					<td><input  type="text" name="fileNames" value="none" />
						<input type="hidden" name="iconIDs" value="none" />
					</td>
					<td><input type="text" name="descriptions" value="none" /></td>
					<td style="display:none"><input type="text" name="urls" value="none" /></td>
					<td><input type="text" name="formats" value="none" /></td>
					<td><input type="text" name="sizes" value="none" /></td>
				</tr>
				<c:forEach items="${icons}" var="item" varStatus="var">
				<tr onmouseover="this.bgColor='#E4E8EB';" onmouseout="this.bgColor='#ffffff';">
					<td><c:out value="${var.index+1}"/></td>
					<td><img src="../<c:out value="${item.url}"/>"></td>
					<td><c:out value="${item.ID}"/></td>
					<td>
						<input type="hidden" name="fileNames" value="<c:out value="${item.fileName}"/>" />
						<input type="hidden" name="iconIDs" value="<c:out value="${item.ID}"/>" />
						<c:out value="${item.fileName}"/>
					</td>
					<td style="display:none">
						<input type="text" name="urls" value="<c:out value="${item.url}"/>" />
					</td>
					<td><input type="text" name="formats" class="short" value="<c:out value="${item.format}"/>" /></td>
					<td><input type="text" name="sizes" class="short" value="<c:out value="${item.size}"/>" /></td>
					<td><input type="text" name="descriptions" class="long" value="<c:out value="${item.description}"/>" /></td>
					<td>
					<input type="button" class="button" value="<i18n:message key="operation.icon.edit" />" onClick="editIcon(<c:out value="${var.index+1}"/>);" />
					</td>
					<td><input class="button" type="button" value='<i18n:message key="operation.icon.delete" />' onClick='deleteIcon(<c:out value="${item.ID}"/>);'/></td>
				</tr>
				</c:forEach>
			</table>
			<input name="fileName" value="2" type="hidden" />
			<input name="url" value="2" type="hidden" />
			<input name="description" value="2" type="hidden" />
			<input name="format" value="2" type="hidden" />
			<input name="size" value="2" type="hidden" />
			<input name="iconID" value="2" type="hidden" />
			</form>
		</div>
	</body>
	<c:if test="${needAlert||needRefresh}">
	<script>
	alert('<i18n:message key="flow.menu.alert"/>');
	</script>
	</c:if>
</html>
