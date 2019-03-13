<%@include file="../e5include/IncludeTag.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":"
			+ request.getServerPort() + path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title></title>
<script type="text/javascript" src="e5script/jquery/jquery.min.js"></script>
<script type="text/javascript">
var url = "<c:out value="${params}"/>";
var colID = "<c:out value="${ colID }"/>";
var UUID = "<c:out value="${ UUID }"/>";
var DocLibID = "<c:out value="${ DocLibID }"/>";
$(function(){
	var result = window.parent.window.updateOrderOfTable(url,colID, DocLibID,function(){});
	if((result + "") != "1"){
		$("#infoDiv").html(result);
	}

});

</script>
<style type="text/css">
	body input{
		font-family:"microsoft yahei";
	}
	body{
		font-family:"microsoft yahei";
	}
	.div
	{
		
		color: #333;
		 text-align: CENTER; 
		 margin-top: 20%; 
		 font-size: 20px;
		 margin-bottom: 15px;
	}
	.input{
		  border-radius: 3px;
		  color: #fff;
		  background:#00a0e6;
		  padding: 0 20px;
		  height: 25px;
		  border: none;
		  margin-left:36%;
		  font-size: 12px;
		  cursor: pointer;

	}
</style>
</head>
<body >
<div id="infoDiv" class="div">
更新成功！
</div>
<!--<img src="Icons/del.gif" alt="" />-->
<input class="input" type="button" value="关闭" onclick='window.parent.window.e5.mods["workspace.toolkit"].self.closeOpDialog("OK", 2)' />

</body>
</html>
