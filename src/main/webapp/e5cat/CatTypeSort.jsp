<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="catType.sort.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<script type="text/javascript">
			<%int i = 0;%>
			var cats = [];
			<c:forEach var="item" items="${list}">
			cats[<%=i%>] = new Cat('<c:out value="${item.catType}"/>','<c:out value="${item.name}"/>','<c:out value="${item.tableName}"/>');
			<%i++;%>
			</c:forEach>
			var downnum = 0;
			var upnum = 0;
			function Cat(catType,name,tableName){
				this.catType = catType;
				this.name    = name;
				this.tableName=tableName;
			}
			function doMouseDown(idx){
				var obj  = window.document.getElementsByTagName("tr");
				var num = 0;
				for (var j = 0; j < obj.length; j++){
					var tmp = obj[j];
					if(tmp.id.substring(0,2) == "tr"){
						if(tmp.id.substring(2,tmp.id.length) == idx)
						//录碌前诘id
							downnum = num;
						num++;
					}
				}
			}
			function doMouseUp(idx){
				var obj  = window.document.getElementsByTagName("tr");
				var num = 0;
				for (var j = 0; j < obj.length; j++){
					var tmp = obj[j];
					if(tmp.id.substring(0,2) == "tr")
					{
						if(tmp.id.substring(2,tmp.id.length) == idx)
							upnum = num;
						num++;
					}
				}
				if(downnum > upnum){
					var tmpCat= cats[downnum];
					for(var i = downnum;i > upnum;i--)
					{
						cats[i] = cats[i-1];

					}
					cats[upnum] = tmpCat;
				}else if(downnum < upnum){
					var tmpCat= cats[downnum];
					for(var i = downnum; i <upnum;i++)
					{
						cats[i] = cats[i+1];
					}
					cats[upnum] = tmpCat;
				}
				var obj  = window.document.getElementsByTagName("td");
				for (var j = 0; j < obj.length; j++){
					var tmp = obj[j];
					if(tmp.getAttribute("catType")!= null)
						tmp.innerHTML = cats[tmp.getAttribute("catType")].catType;
					if(tmp.getAttribute("name") != null)
						tmp.innerHTML = cats[tmp.getAttribute("name")].name;
					if(tmp.getAttribute("tableName") != null)
						tmp.innerHTML = unescape(cats[tmp.getAttribute("tableName")].tableName)+"&nbsp;";
				}
			}
			function doSubmit(){
				var temp = "";
				for(i=0;i<cats.length;i++)
				{
					temp = temp + cats[i].catType + ":" + i ;
					if(i<cats.length-1)
						temp = temp + ",";
				}
				form1.catSort.value=temp;
				form1.submit();
			}
		</script>
	</head>
	<body onselectstart="return false">
		<table id="table_body" cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="catType.sort.title"/></caption>
			<tr id="head">
				<th width="95"><i18n:message key="catType.sort.catTypeID"/></th>
				<th width="167"><i18n:message key="catType.edit.typeName"/></th>
				<th width="152"><i18n:message key="catType.edit.tableName"/></th>
			</tr>
			<%int j=0;%>
			<c:forEach var="item" items="${list}">
			<tr id="tr<c:out value="${item.catType}"/>" onMouseDown="doMouseDown('<c:out value="${item.catType}"/>')" onMouseUp="doMouseUp('<c:out value="${item.catType}"/>')" style="cursor:hand">
				<td catType="<%=j%>"><c:out value="${item.catType}"/></td>
				<td name="<%=j%>"><c:out value="${item.name}"/></td>
				<td tableName="<%=j++%>"><c:out value="${item.tableName}"/>&nbsp;</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="3"  align="center">
					<form name="form1" action="CatTypeSort.do?action=save" method="POST">
						<input type="hidden" name="catSort">
						<input type="button" name="Submit1" value="<i18n:message key="cat.button.submit"/>" onclick="doSubmit();" class="button">
						<input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button">
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>
