<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5cat" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="catSort.title"/></title>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
</head>
<script language="javascript">
function Cat(catID,catCode,catName)
{
	this.catID    = catID;
	this.catCode  = catCode;
	this.catName  = catName;
}

  <%int i = 0;%>
  var cats = new Array();
  
  <c:forEach var="item" items="${list}">
	cats[<%=i%>] = new Cat('<c:out value="${item.catID}"/>','<c:out value="${item.catCode}"/>','<c:out value="${item.catName}"/>');
   <%i++;%>
  </c:forEach>

	var downnum = 0;
	var upnum = 0;

	function doMouseOver(idx)
	{		
		//test1.value = test1.value + "\r\n路过"+id;
		var obj  = window.document.getElementsByTagName("tr");
		for (var j = 0; j < obj.length; j++)
		{
			var tmp = obj[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				//其他的tr都是white，现在停留的是#a6caf0
				tmp.style.backgroundColor = "white";				
				if(tmp.id == "tr"+idx)
					tmp.style.backgroundColor = "#E4E8EB";
			}
		}
	}

	function doMouseDown(idx)
	{
		//test1.value = test1.value + "\r\n选择:"+id;
		var obj  = window.document.getElementsByTagName("tr");
		var num = 0;
		for (var j = 0; j < obj.length; j++)
		{
			var tmp = obj[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				if(tmp.id.substring(2,tmp.id.length) == idx)
				//记录下当前节点id
					downnum = num;
				num++;
			}
		}		
	}	
	
	function doMouseUp(idx)
	{

		var obj  = window.document.getElementsByTagName("tr");
		var num = 0;
		for (var j = 0; j < obj.length; j++)
		{
			var tmp = obj[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				if(tmp.id.substring(2,tmp.id.length) == idx)
					upnum = num;
				num++;
			}
		}
		if(downnum > upnum)
		{
			var tmpCat= cats[downnum];
			for(var i = downnum;i > upnum;i--)
			{
				cats[i] = cats[i-1];
				
			}
			cats[upnum] = tmpCat;
		}
		else if(downnum < upnum)
		{
			var tmpCat= cats[downnum];
			for(var i = downnum; i <upnum;i++)
			{
				cats[i] = cats[i+1];
			}
			cats[upnum] = tmpCat;
		}
		var obj  = window.document.getElementsByTagName("td");		
		for (var j = 0; j < obj.length; j++)
		{		
		
			var tmp = obj[j];						
			if(tmp.getAttribute("catID") != null)
				tmp.innerHTML = cats[tmp.getAttribute("catID")].catID;
			if(tmp.getAttribute("catCode") != null)
				tmp.innerHTML = cats[tmp.getAttribute("catCode")].catCode+"&nbsp;";
			if(tmp.getAttribute("catName") != null)
				tmp.innerHTML = unescape(cats[tmp.getAttribute("catName")].catName);
		}
	}
	function doSubmit()
	{	
		var temp = "";
		for(i=0;i<cats.length;i++)
		{
			temp = temp + cats[i].catID + ":" + i ;
			if(i<cats.length-1)
				temp = temp + ",";
		}
		form1.catSort.value=temp;
		form1.submit();
	}
	function test1()
	{
		alert("xx");
		return false;
	}

//禁止选择文本
//document.oncontextmenu=new Function("event.returnValue=false;");
//document.onselectstart=new Function("event.returnValue=false;");

</script>
<body onselectstart="return false">

<table id="table_body" border="0" cellpadding="2" cellspacing="0" class="table">
  <caption><i18n:message key="catSort.title"/></caption>
  <tr align="center" id="head" class="blacktd">
    <th width="85"><i18n:message key="catSort.catID"/></th>
    <th width="121"><i18n:message key="catSort.catCode"/></th>
    <th width="236"><i18n:message key="catSort.catName"/></th>
  </tr>
  <%int j=0;%>
  <c:forEach var="item" items="${list}">
	  <tr align="center" id="tr<c:out value="${item.catID}"/>" onMouseOver="doMouseOver('<c:out value="${item.catID}"/>')" onMouseDown="doMouseDown('<c:out value="${item.catID}"/>')" onMouseUp="doMouseUp('<c:out value="${item.catID}"/>')" style="cursor:hand">
		<td catID="<%=j%>"><c:out value="${item.catID}"/></td>
		<td catCode="<%=j%>"><c:out value="${item.catCode}"/>&nbsp;</td>
		<td catName="<%=j++%>"><c:out value="${item.catName}"/></td>
	  </tr>
  </c:forEach>
  <tr>
  	<td align="center" colspan="3">
  		<p>
		<form name="form1" action="CatSort.do?action=save" method="POST">
		  <input type="hidden" name="catSort">
		  <input type="hidden" name="catID" value="<c:out value="${catID}"/>"/>
		  <input type="hidden" name="catType" value="<c:out value="${catType}"/>"/>
		  <input type="hidden" name="treeID" value="<c:out value="${treeID}"/>"/>
		  <input type="button" name="Submit1" value="<i18n:message key="cat.button.submit"/>" onclick="doSubmit();" class="button">
		  <input type="button" name="Submit" value="<i18n:message key="cat.button.cancel"/>" onclick="location.href='blank.htm'" class="button">
		</form>
		</p>

  	</td>
  </tr>
</table>

</body>
</html>
