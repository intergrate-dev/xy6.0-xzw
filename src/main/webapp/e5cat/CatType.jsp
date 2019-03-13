<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
	<title><i18n:message key="catType.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../e5script/Function.js"></script>
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<script type="text/javascript">
		var selectedTr = '';
		var tableName  = '';
		//选一录暮
		function selected(src){
			if(selectedTr!='')
			{
				//alert(selectedTr);
				//alert(document.getElementsById(selectedTr));
				document.getElementById(selectedTr).style.backgroundColor='';
			}
			src.style.backgroundColor="#E4E8EB";
			selectedTr = src.getAttribute("id");
			tableName  = src.getAttribute("tableName");
			doModify();
		}
		function doDelete(){

			if(selectedTr == '')
			{
				alert("<i18n:message key="catType.alert.delete"/>");
				return ;
			}
			if(tableName!='')
			{
				alert("<i18n:message key="errors.catType.delete.system"/>");
				return ;
			}
			var ok = confirm("<i18n:message key="catType.confirm.delete"/>");
			if(ok)
			{
				location.href='CatType.do?action=delete&id='+selectedTr;
			}
		}
		function doAdd(){

			getIframe("bodyIframe").src='CatTypeEdit.do?action=new';

		}
		function doModify(){
			if(selectedTr == '')
			{
				alert("<i18n:message key="catType.alert.modify"/>");
				return ;
			}
			getIframe("bodyIframe").src='CatTypeEdit.do?action=edit&id='+selectedTr;
		}
		function doSort(){
			getIframe("bodyIframe").src='CatTypeList.do';
		}
		function doReload() {
			getIframe("bodyIframe").src = "about:blank";
			location.href='CatType.do';
		}
	</script>
	<style type="text/css">
		.sidebar{
			width:370px;
			margin-right:-370px;
			float: left;
		}
		.main-area{
			margin-left:390px;
		}
		#bodyIframe{
			width:100%;
			height:500px;
		}
	</style>
</head>
<body>
<%@include file="ErrorSelf.jsp"%>
		<div class="mainBodyWrap">
			<div class="sidebar">
				<table border="0" cellpadding="0" cellspacing="0" class="table">
					<caption><i18n:message key="catType.header"/></caption>	  
					<tr>
						<th class="alignCenter" width="53">ID</th>
						<th width="151"><i18n:message key="catType.edit.typeName"/></th>
						<th width="123"><i18n:message key="catType.edit.tableName"/></th>
					</tr>
					<%int j=1;%>
					<c:forEach var="item" items="${list}">
					<tr id="<c:out value="${item.catType}"/>" onclick="selected(this)" style="cursor:hand" tableName="<c:out value="${item.tableName}"/>">
						<td class="alignCenter"><c:out value="${item.catType}"/></td>
						<td>
							<c:if test="${item.tableName!=null && item.tableName!=''}">
								<font color="blue" title="<i18n:message key="note.catType.system"/>">
							</c:if>
							<c:out value="${item.name}"/>
							<c:if test="${item.tableName!=null && item.tableName!=''}">
							</font>
							</c:if>
						</td>
						<td><c:out value="${item.tableName}"/>&nbsp;</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="3" align="center">
						<input type="hidden" name="id" value=""/>
						<p>
						<input type="button" name="btnAdd" value="<i18n:message key="cat.button.add"/>" onclick="doAdd()" class="button">
						<input type="button" name="btnDelete" value="<i18n:message key="cat.button.delete"/>" onclick="doDelete()" class="button">
						<input type="button" name="sort" value="<i18n:message key="catType.button.sort"/>" onclick="doSort();" class="button">
						</p>
						</td>
					</tr>
				</table>
			</div>
			<div class="main-area">
				<iframe id="bodyIframe" name="bodyIframe" src="" frameborder="0"></iframe>
			</div>
		</div>
</body>
</html>
