<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<%//不使用cache
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);%>
<html>
	<head>
		<title><i18n:message key="catExt.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
		<script type="text/javascript" src="../e5script/Function.js"></script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript">
			function checkInput(){
				if(f1.typeName.value==''){
					alert("<i18n:message key="catExt.edit.typeName.required"/>");
					f1.typeName.focus();
					return false;
				}
				else if(getLength(f1.typeName.value)>60){
					alert("<i18n:message key="error.catExt.typeName.maxlength"/>");
					f1.typeName.focus();
					return false;
				}
				if(!isInt(f1.order.value)){
					alert("<i18n:message key="error.catExt.order.integer"/>")
					f1.order.focus();
					return false;
				}
				else if(f1.order.value.length>3){
					alert("<i18n:message key="error.catExt.order.maxlength"/>")
					f1.order.focus();
					return false;
				}
				if(f1.catType.value==''){
					alert("<i18n:message key="catExt.edit.catType.required"/>");
					f1.catType.focus();
					return false;
				}
				return true;
			}
			var selectedTr        = '';  //选中的tr
			var selectedOrder     = '';  //选中记录的order属性
			var selectedTypeName  = '';  //选中记录的typeName属性
			var selectedCatType   = '';  //选中记录的catType属性
			//选中一个记录的函数
			function selected(src){
				if(selectedTr!='')
				{
					//alert(selectedTr);
					//alert(document.getElementsById(selectedTr));
					document.getElementById(selectedTr).style.backgroundColor='';
				}
				src.style.backgroundColor="#E4E8EB";
				selectedTr = src.getAttribute("id");
				selectedOrder = src.getAttribute("order");
				selectedTypeName=src.getAttribute("typeName");
				selectedCatType =src.getAttribute("catType");
			}
			//保存扩展属性函数
			function saveExtType(){
				if(!checkInput()) return;
				var xmlHttp = XmlHttp.create();
			
				//form data
				var type    = f1.type.value;
				var typeName= encodeSpecialCode(f1.typeName.value);
				var order   = f1.order.value;
				var catType = f1.catType.value;
				if(f1.catType.disabled)
				   catType  = selectedCatType;
			
				var saveUrl = "CatExtEdit.do?action=save&type="+type+"&order="+order+"&catType="+	catType+"&typeName="+typeName;
			
				//alert(saveUrl);
				xmlHttp.open("GET", saveUrl, false);	// async
				xmlHttp.send(null);
				var result = xmlHttp.responseText;
			
				if(result=='have'){
					var have = confirm('<i18n:message key="alert.catExt.name.already"/>');
					if(have)
					{
						saveUrl = saveUrl + "&sure=true";
						xmlHttp.open("GET", saveUrl, false);	// async
						xmlHttp.send(null);
						result = xmlHttp.responseText;
							//刷新当前页面
						window.location.reload();
					}
				}else{
					//刷新当前页面
					window.location.reload();
				}
			}
			
			function doChange(){
				if(selectedTr==''){
					alert("<i18n:message key="catExt.edit.alert.modify"/>");
					return;
				}
				addtable.style.display = "";
				f1.type.value = selectedTr;
				f1.typeName.value = selectedTypeName;
				f1.order.value    = selectedOrder;
				f1.catType.value  = selectedCatType;
				f1.catType.disabled=true;
			}
			
			function doDelete(){
				if(selectedTr==''){
					alert("<i18n:message key="catExt.edit.alert.delete"/>");
					return;
				}
				var ok = confirm("<i18n:message key="catExt.edit.confirm.delete"/>");
				if(!ok) return;
			
				var xmlHttp = XmlHttp.create();
			
				var saveUrl = "CatExt.do?action=delete&id="+selectedTr;
			
				xmlHttp.open("GET", saveUrl, false);	// async
				xmlHttp.send(null);
				var result = xmlHttp.responseXML;
			
				//刷新当前页面
				window.location.reload();
			}
			
			function doAdd(){
				var addtable = document.getElementById("addtable");
				addtable.style.display = "";
				f1.type.value = '';
				f1.typeName.value = '';
				f1.order.value    = '1';
				f1.catType.value  = '';
				f1.catType.disabled=false;
			
			}
			function doCancel(){
				var addtable = document.getElementById("addtable");
				addtable.style.display="none";
			}
		</script>
		<script type="text/JavaScript">
			function MM_reloadPage(init) {  //reloads the window if Nav4 resized
			  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
				document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
			  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
			}
			MM_reloadPage(true);
		</script>
		<style>
			.sidebar{
				margin-right:-380px;
				width:380px;
				float: left;
			}
			.main-area{
				margin-left:400px;
			}
			.input-width{
				width:200px;
			}
		</style>
	</head>
	<body>
		<div class="mainBodyWrap">
			<div class="sidebar">
				<table cellspacing="0" cellpadding="0" class="table">
					<caption><i18n:message key="catExt.header"/></caption>
					<tr>
						<th><i18n:message key="catExt.name"/></th>
						<th><i18n:message key="catExt.order"/></th>
						<th><i18n:message key="catExt.catType"/></th>
					</tr>
					<c:forEach var="item" items="${list}">
					<tr onclick="selected(this)" style="cursor:hand" id="<c:out value="${item.type}"/>" typeName="<c:out value="${item.typeName}"/>" order="<c:out value="${item.order}"/>" catType="<c:out value="${item.catType}"/>">
						<td><c:out value="${item.typeName}"/></td>
						<td><c:out value="${item.order}"/></td>
						<td>
						<c:forEach var="type" items="${types}">
							<c:if test="${type.catType==item.catType}">
								<c:out value="${type.name}"/>
							</c:if>
						</c:forEach>
						</td>
			  		</tr>
					</c:forEach>
					<tr align="center">
						<td colspan="3">
							<input type="button" name="Submit" value="<i18n:message key="cat.button.add"/>" onclick="doAdd()" class="button">
							<input type="button" name="Submit" value="<i18n:message key="cat.button.modify"/>" onclick="doChange()" class="button">
							<input type="button" name="Submit" value="<i18n:message key="cat.button.delete"/>" onclick="doDelete()" class="button">
						</td>
					</tr>
				</table>
			</div>
			<div id="Layer1" class="main-area">
				<form name="f1" action="CatExtEdit.do?action=save" method="post">
					<input name="type" type="hidden">
					<table cellspacing="0" cellpadding="0" id="addtable" class="table">
						<caption><i18n:message key="catExt.header"/></caption>
						<tr>
							<th class="w90">
								<i18n:message key="catExt.name"/>
							</th>
							<td>
								<input name="typeName" type="text" class="input-width">
							</td>
						</tr>
						<tr>
							<th>
								<i18n:message key="catExt.order"/>
							</th>
							<td>
								<input name="order" type="text" class="input-width">
							</td>
						</tr>
						<tr>
							<th>
								<i18n:message key="catExt.catType"/>
							</th>
							<td>
								<select name="catType" class="input-width">
								<c:forEach var="type" items="${types}">
									<option value="<c:out value="${type.catType}"/>"><c:out value="${type.name}"/></option>
								</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="2" class="alignCenter">
								<input type="button" name="sure" value="<i18n:message key="cat.button.submit"/>" onclick="saveExtType()" class="button">
								<input type="button" name="sure" value="<i18n:message key="cat.button.cancel"/>" onclick="doCancel()" class="button">
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</body>
</html>
