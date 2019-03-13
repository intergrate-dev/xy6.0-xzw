<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'NewSetMeal.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->


<link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
<link type="text/css" rel="stylesheet"
	href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" />
<link type="text/css" rel="stylesheet"
	href="../../e5script/jquery/dialog.style.css" />
<link type="text/css" rel="stylesheet"
	href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css" />
<link type="text/css" rel="stylesheet"
	href="../../e5script/lhgcalendar/lhgcalendar.bootstrap.css">
<link type="text/css" rel="stylesheet"
	href="../../e5style/e5form-custom.css" />
<link type="text/css" rel="stylesheet"
	href="../../amuc/script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet"
	href="../../amuc/css/form-custom.css" />
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript"
	src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript"
	src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript"
	src="../../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript"
	src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript"
	src="../../e5script/calendar/usecalendar.js"></script>
<script type="text/javascript"
	src="../../e5script/lhgcalendar/lhgcalendar.js"></script>
<script type="text/javascript"
	src="../../e5workspace/script/form-custom.js"></script>
<script type="text/javascript" src="../../amuc/member/script/member.js"></script>



</head>

<body>

	<style type="text/css">
.defalttest {
	color: #CCCCCC
}

#tab tr {
	height: 50px
}

input {
	border: 1px solid #cccccc;
	border-radius: 4px;
}
</style>
	<input type="hidden" id="DocIDs" name="DocIDs"
			value="<%=request.getParameter("DocIDs")%>" /> <input type="hidden"
			id="UUID" name="UUID" value="<%=request.getParameter("UUID")%>" />
			<input type="hidden"
			id="flag" name="flag" value="" />
			<input type="hidden"
			id="FVID" name="FVID" value="<%=request.getParameter("FVID")%>" />
			<input type="hidden"
			id="HASCHANGE" name="HASCHANGE" value="<%=request.getParameter("HasChange")%>" />
			<input type="hidden" id="siteID" name="siteID"
			value="<%=request.getParameter("siteID")%>" />
			<input type="hidden" id="siteLibID" name="siteLibID"
			value="11" />
			<input type="hidden" id="httppath" name="httppath"
			value="" />
	<iframe id="iframe" name="iframe" style="display:none;"></iframe>
	<form action="<%=path%>/amuc/invitecode/upload.do"
		id="uploadForm" method="post" enctype="multipart/form-data">	
		<center>
			<table id="tab" style="margin-top:130px;">
				<tr>
					<td>上传App Logo :</td>
					<td><input name="emAffixFile"  id="emAffixFile" width="100px" class="custform-input" style="width:300px;border:0px;" type="file" value=""/>
						<!-- <input class="btn" onclick="doUpload()" type="button" id="btnSave" value="上传"/> --></td>
				</tr>
				<tr>
					<td>请输入App名称 :</td>
					<td><input value="" class="defalttest"
						style="height:40px;width:400px" name="AppName"
						id="AppName"></td>
				</tr>
				<tr>
					<td>请输入App下载链接 :</td>
					<td><input value="" class="defalttest"
						style="height:40px;width:400px" name="AppDownload"
						id="AppDownload"></td>
				</tr>
				<tr>
					<td></td>
					<td><br /> <input type="button" value="上传" onclick="doUpload()"
						style=" background-color: #169BD5;color:#FFFFFF;border-radius: 10px;width:100px;height:40px">
						<input type="button" value="取消" onclick="clos()"
						style=" background-color: #FFFFFF;border-radius: 10px;width:100px;height:40px">
				</tr>
			</table>
	</form>
	</center>

</body>
</html>
<script type="text/javascript" language="utf-8">

function doUpload() {  
     var formData = new FormData($( "#uploadForm" )[0]);  
     var AppName = $("#AppName").val();
     var AppDownload = $("#AppDownload").val();
     var siteID = $("#siteID").val();
     var url = '../createVote/upload.do?AppName='+AppName+"&siteID="+siteID+"&AppDownload="+AppDownload;
     $.ajax({  
          url: url,  
          type: 'POST',  
          data: formData, 
          dataType:"json",
          async: false,  
          cache: false,  
          contentType: false,  
          processData: false,  
          success: function (returndata) {  
        	  if(returndata.code == "1"){
        		alert(returndata.msg)
        	  }else if(returndata.code == "0"){
        		alert(returndata.msg)
        	  }
        	
          },  
          error: function (returndata) {  
              alert(returndata);  
          }  
     });  
};

function clos(){

	if($("#HASCHANGE").val()=="1"){
		document.getElementById("iframe").contentWindow.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val() + "&DocIDs=1";
	} else {
		document.getElementById("iframe").contentWindow.location.href = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
	}

}
</script>
