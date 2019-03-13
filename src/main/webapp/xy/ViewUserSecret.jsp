<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>查看用户秘钥</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="./script/bootstrap-3.3.4/css/bootstrap.min.css">
	<script type="text/javascript" src="./script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="./script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<style type="text/css">.container-fluid{margin-bottom: 10px}</style>
</head>
<body>
	<div class="pull-right container-fluid">
		<button id="btnCancel" type="button" class="btn btn-primary">关闭</button>
	</div>
	<div class="container-fluid">
		<table class="table table-hover table-bordered ">
			<thead>
				<tr style="background:#e5e4ea">
					<td>用户名</td>
					<td class="text-left">秘钥</td>
				</tr>
			</thead>
			<tbody id="userlist"></tbody>
		</table>	
	</div>
	<script type="text/javascript">
		function doCancel(){
			var url = "../e5workspace/after.do?UUID=" + <%=request.getParameter("UUID")%>;
			window.location.href= url;
		}
		$(function(){
			var theURL = './security/viewDynamicCode.do?DocLibID=<%=request.getParameter("DocLibID")%>&DocIDs=<%=request.getParameter("DocIDs")%>';
			$('#btnCancel').click(doCancel);
			$.ajax({
				url:theURL, 
				async:false, 
				dataType:'json', 
				success:function(data){
					$.each(data, function(i,val){      
						$("#userlist").append("<tr><td>"+val.name+"</td><td>"+val.secret+"</td></tr>");
					});
				}
			});
		})
	</script>
</body>
</html>