<style type="text/css">
	.nav-tabs>li>a{
		  background-color: #f9f9f9;
  		 

	}
	#setting li a{
		 border: 1px solid #ddd;
		 margin-top: 10px;
		 color: #666;
	}
	body{
		padding: 0 0 0 10px;
		overflow:hidden;
	}
	.tab-content{
		border: 1px solid #ddd;
  		border-top: none;
	}
	iframe{
		width:100%;
	 }
	a{
		color:#646464;
	 }
	 .nav-tabs>li.active>a{
	 	border-bottom: #fff !important;
    	height: 42px;
	 }
</style>
<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>test</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
	<script src="../script/jquery/jquery.min.js"></script>
	<script src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
</head>
<body>
 <!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist" id="setting">
 		<li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab">基本属性</a></li>
		<c:if test="${param.ext and param.ch == 1}">
		<li role="presentation"><a href="#ext2" aria-controls="ext2" role="tab" data-toggle="tab">APP属性</a></li>
		</c:if>
		<li role="presentation"><a href="#ext0" aria-controls="ext0" role="tab" data-toggle="tab">发布设置</a></li>
		<li role="presentation"><a href="#ext1" aria-controls="ext1" role="tab" data-toggle="tab">触屏发布设置</a></li>
		<li role="presentation"><a href="#messages" aria-controls="messages" role="tab" data-toggle="tab">栏目聚合</a></li>
		<c:if test="${param.ch == 0}">
		<li role="presentation"><a href="#ext3" aria-controls="ext3" role="tab" data-toggle="tab">自动推送</a></li>
		</c:if>
	</ul>

	<!-- Tab panes -->
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane active" id="home">
			<iframe id="frmBase" name="frmBase" frameborder="0" 
				src="../../e5workspace/manoeuvre/Form.do?code=formColumn&DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="${param.ch}"/>"></iframe>
		</div>
		<c:if test="${param.ext}">
		<div role="tabpanel" class="tab-pane" id="ext0">
			<iframe id="frmExt" name="frmExt0" frameborder="0"
				src="../../e5workspace/manoeuvre/Form.do?code=formColumnExt0&DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="${param.ch}"/>""></iframe>
	</div>
		<div role="tabpanel" class="tab-pane" id="ext1">
			<iframe id="frmExt" name="frmExt1" frameborder="0"
				src="../../e5workspace/manoeuvre/Form.do?code=formColumnExt1&DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="${param.ch}"/>""></iframe>
		</div>
		<div role="tabpanel" class="tab-pane" id="ext2">
			<iframe id="frmExt" name="frmExt2" frameborder="0"
				src="../../e5workspace/manoeuvre/Form.do?code=formColumnExt2&DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="${param.ch}"/>""></iframe>
		</div>
		</c:if>
		<div role="tabpanel" class="tab-pane" id="messages">
			<iframe id="frmExt" name="frmExt2" frameborder="0"
				src="../../xy/column/initColumnAggregate.do?DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="${param.ch}"/>"></iframe>
		</div>
		<c:if test="${param.ch == 0}">
		<div role="tabpanel" class="tab-pane" id="ext3">
			<iframe id="frmExt" name="frmExt2" frameborder="0"
				src="../../xy/column/initColumnPush.do?DocLibID=<c:out value="${param.DocLibID}"/>&DocIDs=<c:out value="${param.DocIDs}"/>&siteID=<c:out value="${param.siteID}"/>&ch=<c:out value="1"/>"></iframe>
		</div>
		</c:if>
	</div>
</body>
<script>
	$(function() {
		//调整iframe高度
		var winH = $(document).height();
		var top = $("#home").offset().top;
		$("iframe").height(winH - top - 7);
	});
</script>
</html>