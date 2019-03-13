<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>二维码</title>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript">
		var UUID = "<c:out value="${param.UUID}"/>";
		$(function(){
			$('#btnCancel').click(function(){
				var url = "../../e5workspace/after.do?UUID=" + UUID;
				window.location.href= url;
			});
		})
	</script>
	<style type="text/css">
		.col-center-block
			{
				 text-align: CENTER; 
			}
		.col-margin-block
			{
				 margin-top: 20%; 
				 margin-bottom: 15px;
			}
		
	</style>
</head>
<body>
<div class="container-fluid">
<form class="form-horizontal" action="<%=basePath%>xy/article/GeneratorEmptyArticleAction.do" method="post">
   	<div class="form-group">
	    <div class="col-center-block col-margin-block">
			<input id="num" name="num" class="input" type="number"  placeholder="请输入稿件数量">
		</div>
	</div>
	<div class="form-group">
		<div class="col-center-block">
			<input id="btnSubmit" type="submit" class="btn btn-primary" value="确定">
			<input id="btnCancel" type="button" class="btn btn-default" value="取消">
		</div>			
	</div>
	<input type="hidden" id="UUID" name="UUID" value="${param.UUID}">
	<input type="hidden" id="siteID" name="siteID" value="${param.siteID}">
	<input type="hidden" id="ch" name="ch" value="${param.ch}">
	<input type="hidden" id="colID" name="colID" value="${param.colID}">			
</form>
</div>
</body>
</html>