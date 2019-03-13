<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />

<%
String strDocTypeID = request.getParameter("docTypeID");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<link type="text/css" rel="StyleSheet" href="../e5style/style.css">
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<style>
#Content {

	font-size:12px;

	color:black;

	MARGIN: 10px 10px 10px 10px;
	PADDING:10px 10px 10px 10px;
	BORDER-TOP: #CCC 0px solid;
	BORDER-RIGHT: #CCC 0px solid;
	BORDER-BOTTOM: #CCC 0px solid;
	BORDER-LEFT: #CCC 0px solid;

	HEIGHT: 90%;


}
</style>
<meta name="author" content="<i18n:message key='e5dom.Author' />" />
<meta name="description" content="" />
<link rel="stylesheet" rev="stylesheet" href="css/dom.css" type="text/css" media="all" />
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<script language="javascript">

var i18nInfo = {

}

function ini() {

	var docTypeID = <%=strDocTypeID%>;
	$.ajax({url:"DocTypeController.do?invoke=getDocLibsToBeUpdated&docTypeID=0",dataType:"json", async:false, success:function(data) {
        if(data!="-1"){
        	$("#appID").html("");
    		var datas = new Array();
    		if(!$.isArray(data)){
    			datas.push(data);
    		}else{
    			datas = data;
    		}
    		if(datas!=null){
				$(".fields").remove();
        		$.each( datas, function(i, d){
        			var trHtml = "";
        			trHtml +="<tr class='fields' bgcolor=\"#FFFFF6\">";
        			trHtml +="<td align=\"center\">"+d.docLibID+"</td>";
        			trHtml +="<td>"+d.docLibName+"</td>";
        			trHtml +="<td><input class=\"button\" value=\"<i18n:message key="e5dom.DocLib.updateDocLib"/>\" onclick=\"alterDocLib('"+d.docLibID+"')\" type=\"button\"></td>";
        			trHtml +="</tr>";
        			$("#docLibTab").append(trHtml);
        		});
        	}
        }
      }});
}

function alterDocLib(docLibID) {
	$.ajax({url:"DocLibController.do?invoke=alterDocLib&docLibID="+docLibID,dataType:"json", async:false, success:function(data) {
        if(data!=null&&data==1){
        	alert("OK");
			ini();
        }else{
			alert("Failed");
		}
      }});
}

</script>

</head>

<body onload="ini();">

<div id="Content">
<table id="docLibTab" cellpadding="0" cellspacing="0" class="table">
<tr>
	<th><i18n:message key="e5dom.DocLib.DocLibID"/></th>
	<th><i18n:message key="e5dom.DocLib.DocLibName"/></th>
	<th><i18n:message key="e5dom.DocLib.Action"/></th>
</tr>
</table>
</div>
<center>
	<input class="button" type="button" name="docancel" value="<i18n:message key="e5dom.DocType.CreateField.Cancel"/>" onClick='window.parent.e5.dialog.close("ShowAlterDocLibPage");'>
</center>
</body>
</html>
