<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="dataSourceBind.title"/></title>
<script type="text/javascript" src="../e5script/xtree/xtree.js"></script>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript" src="../e5script/xtree/xloadtree.js"></script>
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css" />
<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<style type="text/css">
	#nameDiv{
		width: 500px;
		color: #333333;
		background: #E0E0E0;
	}
	body{
		margin: 20px;
		margin-left: 10px;
	}
	select{width : 300px;}
</style>
<SCRIPT type="text/javascript">
<!--
var addDataSourceDialog;
$(document).ready(function(){
	addDataSourceDialog = e5.dialog("", {
		title : "<i18n:message key="dataSource.add.title"/>",
		id : "addDataSourceDialog",
		width : 510,
		height : 200,
		resizable : true,
		showClose : false
	});
	addDataSourceDialog.DOM.content.append($("#f1"));
});
function checkInput()
{
	if(f1.dsName.value=='')
	{
		alert("<i18n:message key="dataSourceBind.dsName.required"/>");
		f1.dsName.focus();
		return false;
	}

	return true;
}

var selectedTr        = '';  //选中的tr
var selectedDSID      = '';  //DSID

//选中一个记录的函数
function selected(src)
{
	if(selectedTr!='')
    {
		//alert(selectedTr);
		//alert(document.getElementsById(selectedTr));
    	document.getElementById(selectedTr).style.backgroundColor='';
	}
	src.style.backgroundColor="#e8e8e8";
	selectedTr = src.getAttribute("id");
	selectedDSID = src.getAttribute("dsID");

}

//保存扩展属性函数

function saveExtType()
{
	if(!checkInput()) return;
    var xmlHttp = XmlHttp.create();

	//form data
	var dsName = f1.dsName.value;
	var dsID   = f1.dsID.value;


    var saveUrl = "DataSourceBindEdit.do?action=save&dsName="+encodeSpecialCode(dsName)+"&dsID="+dsID;
	///alert(saveUrl);
	xmlHttp.open("GET", saveUrl, false);	// async
    xmlHttp.send();
    var result = xmlHttp.responseXML;

	//刷新当前页面
	window.location.reload();
}

function doChange()
{
	f1.dsName.value = selectedTr;
	f1.dsID.value = selectedDSID;
}

function doDelete()
{
		if(selectedTr=='')
		{
			alert("<i18n:message key="dataSourceBind.alert.delete"/>");
			return;
		}
		var sure = confirm("<i18n:message key="dataSourceBind.confirm.delete"/>");
    if(sure)
		{
		    var xmlHttp = XmlHttp.create();

		    var saveUrl = "DataSourceBind.do?id="+encodeSpecialCode(selectedTr)+"&action=delete";

				//alert(saveUrl);
			  xmlHttp.open("GET", saveUrl, false);	// async
		    xmlHttp.send();
		    var result = xmlHttp.responseText;
			if(result != "ok")
			{
				alert(result);
			}
			else
				//刷新当前页面
				window.location.reload();
		}
}

function doAdd()
{
	addDataSourceDialog.show();
	var tab = document.getElementById("addtable");
	tab.style.display = "";
	f1.dsName.value = '';
	f1.dsID.value = '';
}
//-->
</SCRIPT>
</head>

<body>
<table cellspacing="0" cellpadding="4" class="table">
	<caption><i18n:message key="dataSourceBind.header"/></caption>
    <tr>
      <th><i18n:message key="dataSourceBind.id"/></th>
	  <th><i18n:message key="dataSourceBind.dsName"/></th>
      <th><i18n:message key="dataSourceBind.dsID"/></th>
    </tr>
	 <%int j=1;%>
	 <c:forEach var="item" items="${list}">
	  <tr onclick="selected(this)" style="cursor:hand" id="<c:out value="${item.dsName}"/>" dsID="<c:out value="${item.dsID}"/>">
		<td align="center"><%=j++%></td>
	      <td><c:out value="${item.dsName}"/></td>
	      <td>
	      <!-- 循环取得名称-->
			<c:forEach var="type" items="${dsList}">
				<c:if test="${item.dsID==type.dsID}">
					<c:out value="${type.name}"/>
				</c:if>
				</c:forEach>
			</td>
	    </tr>
	 </c:forEach>
</table>
<br />
<input class="button" type="button" name="Submit" value="<i18n:message key="dataSource.button.add"/>" onclick="doAdd()">
<input class="button" type="button" name="Submit" value="<i18n:message key="dataSource.button.delete"/>" onclick="doDelete()">

<form id="f1" name="f1" action="DataSourceBindEdit.do?action=save" method="post">
  <table cellspacing="0" cellpadding="4" id="addtable" style="display:none" class="talbe">
	<tr>
	  <th><i18n:message key="dataSourceBind.dsName"/></th>
	  <th><input name="dsName" type="text" style="width: 300px;"></th>
	</tr>
	<tr>
	  <td><i18n:message key="dataSourceBind.dsID"/></td>
	  <td>
		<select name="dsID">
		  <c:forEach var="type" items="${dsList}">
		  <option value="<c:out value="${type.dsID}"/>"><c:out value="${type.name}"/></option> 
		  </c:forEach>
		</select>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<input class="button" type="button" name="sure" value="<i18n:message key="dataSource.button.submit"/>" onclick="saveExtType()">&nbsp;
			<input class="button" type="button" name="cancel" value="<i18n:message key="dataSource.button.cancel"/>" onclick="addDataSourceDialog.hide();">  
		</td>
	</tr>

  </table>
</form>
</body>
</html>
