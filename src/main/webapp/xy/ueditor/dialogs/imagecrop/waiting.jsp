<%@include file="../../../../e5include/IncludeTag.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <script type="text/javascript" src="<%=path %>/xy/script/jquery/jquery.min.js"></script>
    <script type="text/javascript">
    	var isUploaded = '<c:out value="${isUploaded}"/>';
    	var imagePath = '<c:out value="${imagePath}"/>';
    	var itype = '<c:out value="${itype}"/>';
    	$(function(){
    		if(isUploaded=="true"){
				window.parent.channel_frame.setTitleImage();
				return;
			}
    	});
    </script>
  </head>
  
  <body>
  	<div style="margin: auto;text-align: center; vertical-align: middle;">
  		<img src="xy/ueditor/dialogs/imagecrop/loading_16.gif" />
  	</div>
    
  </body>
</html>
