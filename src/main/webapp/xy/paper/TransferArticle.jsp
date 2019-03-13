<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>转版稿件</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="./script/transferArticle.js"></script>
	<style type="text/css">.container-fluid{margin-bottom: 10px}</style>
</head>
<body>
	<div class="pull-right container-fluid">
		<button id="btnCancel" type="button" class="btn btn-primary">关闭</button>
	</div>
	
	<div class="container-fluid">
		<input type="hidden" name="targetID" id="targetID" value="<c:out value="${id}"/>">
		<table class="table table-hover table-bordered ">
			<thead>
				<tr style="background:#e5e4ea">
					<td class="text-left">当前稿件：<c:out value="${title}"/></td>
					<td>版次：<c:out value="${layoutName}"/></td>
					<td id="transStatus">处理状态：<c:choose>
	  						<c:when test="${transStatus == '1'}">已合成</c:when>
	  						<c:otherwise>未合成</c:otherwise></c:choose>
	  					</td>
				</tr>
			</thead>
			<tbody id="rellist"></tbody>
		</table>	
	</div>
	
	<div class="form-inline pull-right container-fluid">
		转版版次 <select id="paperLayout" name="layoutID" class="form-control">
			<c:forEach var="layout" items="${layouts}">
				<option value="<c:out value="${layout.id}"/>" <c:if test="${layout.id == layoutId}">selected="selected"</c:if> ><c:out value="${layout.layout}"/></option>
			</c:forEach>
		</select>
		<div class="input-group">
			<input id="keyword" name="keyword" type="text" class="form-control" placeholder="搜索词">
			<a id="btnSearch" type="button" class="input-group-addon"><i class="glyphicon glyphicon-search"></i></a>
		</div>
	</div>
	
	<div class="container-fluid">
		<table class="table table-hover">
			<thead>
				<tr style="background:#f5f9fc">
					<th width="5%">#</th>
					<th width="5%">类型</th>
					<th>标题</th>
					<th width="15%">作者</th>
					<th width="15%">操作</th>
				</tr>
			</thead>
			<tbody id="alist"></tbody>
		</table>
	</div>
	
	<script type="text/javascript">
		transferArticle.UUID = "<c:out value="${UUID}"/>";
		transferArticle.layoutID = "<c:out value="${layoutId}"/>";		
		transferArticle.targetID = "<c:out value="${id}"/>";		
		$(function(){
			$('#btnCancel').click(transferArticle.doCancel);
			$('#btnSearch').click(transferArticle.doSearch);
			$('#paperLayout').change(transferArticle.doSearch);
			$('#keyword').keydown(function(event){ 
				if(event.keyCode==13) transferArticle.doSearch();
			});
			transferArticle.init();
		})
	</script>
</body>
</html>